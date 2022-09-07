/**
 * List  - 应用管理 - 查询
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import {
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      // expandForm: false,
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

  render() {
    const { form = {} } = this.props;
    const { getFieldDecorator = (e) => e } = form;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
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
                  label={intl.get('hitf.application.model.application.interfaceCode').d('接口编码')}
                >
                  {getFieldDecorator('interfaceCode')(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT} className="search-btn-more">
                <FormItem>
                  <Button onClick={this.onReset}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button type="primary" htmlType="submit" onClick={this.onClick}>
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </FormItem>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
    );
  }
}
