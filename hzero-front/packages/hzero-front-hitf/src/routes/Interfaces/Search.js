/**
 * List  - 应用管理 - 查询
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';
import Lov from 'components/Lov';
import { isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import {
  DEBOUNCE_TIME,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
  }

  @Bind()
  onClick() {
    const {
      fetchList = (e) => e,
      form: { getFieldsValue = (e) => e },
      pagination = { pageSize: 10, current: 1 },
    } = this.props;
    const data = getFieldsValue() || {};
    fetchList({
      ...data,
      size: pagination.pageSize,
      page: pagination.current - 1,
    });
  }

  @Bind()
  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  /**
   * 展开 收起 表单
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  render() {
    const { form = {}, serverTypeCode = [], yesOrNoCode = [] } = this.props;
    const { expandForm } = this.state;
    const { getFieldDecorator = (e) => e } = form;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              {!isTenantRoleLevel() && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.model.common.belongTenant').d('所属租户')}
                  >
                    {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
                  </FormItem>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.application.model.application.interfaceName').d('接口名称')}
                >
                  {getFieldDecorator('interfaceName')(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.application.model.application.interfaceCode').d('接口编码')}
                >
                  {getFieldDecorator('interfaceCode')(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
              {isTenantRoleLevel() && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.application.model.application.serverName').d('服务名称')}
                  >
                    {getFieldDecorator('serverName')(<Input />)}
                  </FormItem>
                </Col>
              )}
            </Row>
            <Row
              {...SEARCH_FORM_ROW_LAYOUT}
              style={expandForm ? expandFormStyle : noExpandFormStyle}
            >
              {!isTenantRoleLevel() && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.application.model.application.serverName').d('服务名称')}
                  >
                    {getFieldDecorator('serverName')(<Input />)}
                  </FormItem>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.application.model.application.serverCode').d('服务代码')}
                >
                  {getFieldDecorator('serverCode')(<Input typeCase="upper" inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.application.model.application.serverType').d('服务类型')}
                >
                  {getFieldDecorator('serverType')(
                    <Select allowClear>
                      {serverTypeCode.map((n) => (
                        <Option key={n.value} value={n.value}>
                          {n.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              {isTenantRoleLevel() && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaces.model.interfaces.authTenant').d('授权租户')}
                  >
                    {getFieldDecorator('tenantAuth')(
                      <Select allowClear>
                        {yesOrNoCode.map((n) => (
                          <Option key={n.value} value={n.value}>
                            {n.meaning}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              )}
            </Row>
            <Row
              {...SEARCH_FORM_ROW_LAYOUT}
              style={expandForm ? expandFormStyle : noExpandFormStyle}
            >
              {isTenantRoleLevel() && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaces.model.interfaces.authRole').d('授权角色')}
                  >
                    {getFieldDecorator('roleAuth')(
                      <Select allowClear>
                        {yesOrNoCode.map((n) => (
                          <Option key={n.value} value={n.value}>
                            {n.meaning}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.interfaces.model.interfaces.namespace').d('服务命名空间')}
                >
                  {getFieldDecorator('namespace')(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
            </Row>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <FormItem>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.onReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
