/**
 * List  - 应用管理 - 查询
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
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

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const { form = {}, tenantRoleLevel } = this.props;
    const { getFieldDecorator = (e) => e } = form;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          {!tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hitf.application.model.application.tenant').d('所属租户')}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hitf.application.model.application.code').d('应用代码')}
            >
              {getFieldDecorator('applicationCode')(
                <Input typeCase="upper" inputChinese={false} />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hitf.application.model.application.name').d('应用名称')}
            >
              {getFieldDecorator('applicationName')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <FormItem>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick.bind(this)}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
