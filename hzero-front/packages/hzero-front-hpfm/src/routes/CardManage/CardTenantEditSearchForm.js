/**
 * 卡片管理 分配租户 查询表单
 * @date 2019-01-23
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Button, DatePicker, Col, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import {
  SEARCH_COL_CLASSNAME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';

/**
 * 卡片管理查询表单
 * @ReactProps {!Function} onRef - 拿到该组件的this
 * @ReactProps {!Function} onSearch - 触发查询方法
 */
@Form.create({ fieldNameProp: null })
export default class CardTenantEditSearchForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired, // 查询按钮点击触发
    onRef: PropTypes.func.isRequired, // 获取本省的this
  };

  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  /**
   * 注册时间从 的禁用 日期的方法
   * @param {moment} currentDate 选中的 时间
   */
  @Bind()
  beginDateDisabledDate(currentDate) {
    const { form } = this.props;
    const endDate = form.getFieldValue('endDate');
    return endDate && endDate.isBefore(currentDate, 'day');
  }

  /**
   * 注册时间至 的禁用 日期的方法
   * @param {moment} currentDate 选中的 时间
   */
  @Bind()
  endDateDisabledDate(currentDate) {
    const { form } = this.props;
    const beginDate = form.getFieldValue('beginDate');
    return beginDate && beginDate.isAfter(currentDate, 'day');
  }

  render() {
    const { form } = this.props;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.tenant.name').d('租户名称')}
            >
              {form.getFieldDecorator('tenantName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.register.from').d('注册时间从')}
            >
              {form.getFieldDecorator('beginDate')(
                <DatePicker
                  showTime
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={this.beginDateDisabledDate}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.register.to').d('注册时间至')}
            >
              {form.getFieldDecorator('endDate')(
                <DatePicker
                  showTime
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={this.endDateDisabledDate}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button key="reset" onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                key="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearchBtnClick}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  @Bind()
  handleSearchBtnClick() {
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleResetBtnClick() {
    const { form } = this.props;
    form.resetFields();
  }
}
