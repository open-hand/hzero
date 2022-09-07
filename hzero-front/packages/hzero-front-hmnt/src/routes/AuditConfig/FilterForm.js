/**
 * audit-config 操作审计配置
 * @date: 2019-7-18
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

import styles from './styles.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 查询表单
   */
  @Bind()
  handleSearch() {
    const { form, search } = this.props;
    search(form);
  }

  /**
   * 重置表单
   */

  @Bind()
  handleReset() {
    const { form, reset } = this.props;
    reset(form);
  }

  render() {
    const { form, isTenantRoleLevel, organizationId } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form className={`${styles['message-search-form']} more-fields-search-form`}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          {!isTenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hmnt.auditConfig.model.auditConfig.tenantId').d('租户')}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" textField="tenantName" />)}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditConfig.model.auditConfig.serviceName').d('服务')}
            >
              {getFieldDecorator('serviceName')(
                <Lov
                  code={
                    !isTenantRoleLevel ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG'
                  }
                  textField="serviceName"
                  queryParams={isTenantRoleLevel && { organizationId }}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditConfig.model.auditConfig.auditContent').d('操作内容')}
            >
              {getFieldDecorator('auditContent', {})(<Input />)}
            </FormItem>
          </Col>

          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem>
              <Button onClick={this.handleReset} style={{ marginRight: 8 }}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        {/* {!isTenantRoleLevel && (
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        )} */}
      </Form>
    );
  }
}
