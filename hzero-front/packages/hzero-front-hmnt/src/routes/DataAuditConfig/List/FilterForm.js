/**
 * FilterForm - 数据变更审计配置-查询条件表单
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { isNull } from 'lodash';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';
import cacheComponent from 'components/CacheComponent';
import { isTenantRoleLevel, getCurrentTenant } from 'utils/utils';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hmnt/data-audit-config/list' })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  state = {
    expandForm: false, // 是否展开
  };

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  /**
   * 多查询条件展示
   */
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  render() {
    const {
      form: { getFieldDecorator },
      currentTenant,
    } = this.props;
    const isTenant = isTenantRoleLevel();
    const { tenantName, tenantId } = getCurrentTenant();
    const tenant = {
      tenantName: currentTenant.tenantName || tenantName,
      tenantId: isNull(currentTenant.tenantId) ? tenantId : currentTenant.tenantId,
    };
    const layout = FORM_COL_4_LAYOUT;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...layout}>
            <FormItem
              label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.serviceName').d('服务名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serviceName', {
                rules: [
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...layout}>
            <FormItem
              label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.tableName').d('表名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('tableName', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input typeCase="lower" />)}
            </FormItem>
          </Col>
          {!isTenant && (
            <Col {...layout}>
              <FormItem
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tenant.tenantId,
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textField="tenantName"
                    textValue={tenant.tenantName}
                    allowClear={false}
                    onOk={this.handleSearch}
                  />
                )}
              </FormItem>
            </Col>
          )}
          <Col {...layout} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button data-code="reset" onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                data-code="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearch}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
