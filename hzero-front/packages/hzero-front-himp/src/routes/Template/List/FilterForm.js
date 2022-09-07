/**
 * FilterForm - 模板头数据表单
 * @since 2019-1-29
 * @author jiacheng.wang <jiacheng.wang@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

import Lov from 'components/Lov';
import { isTenantRoleLevel } from 'utils/utils';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

/**
 * 消息模板列表查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} search - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
export default class FilterForm extends PureComponent {
  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { search, form } = this.props;
    if (search) {
      form.validateFields(err => {
        if (!err) {
          // 如果验证成功,则执行search
          search();
        }
      });
    }
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          {!isTenantRoleLevel() && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`himp.template.model.template.tenantName`).d('租户')}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`himp.template.model.template.templateName`).d('模板名称')}
            >
              {getFieldDecorator('templateName', {})(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`himp.template.model.template.templateCode`).d('模板代码')}
            >
              {getFieldDecorator('templateCode', {})(
                <Input typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
