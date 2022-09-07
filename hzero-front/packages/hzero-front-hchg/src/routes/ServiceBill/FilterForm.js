/**
 * FilterForm - 服务账单-列表页查询条件表单
 * @date: 2019-8-30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import { Form, Button, Input, Row, Col, Select, DatePicker } from 'hzero-ui';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';
import { getDateFormat } from 'utils/utils';
import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hchg/service-bill' })
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
      form.validateFields(err => {
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
      form: { getFieldDecorator = e => e, getFieldValue = () => {} },
      statusList,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceBill.model.serviceBill.client').d('客户端')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('clientId', {
                rules: [
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.model.common.tenantId').d('租户')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('tenantId', {})(<Lov code="HPFM.TENANT" textField="tenantName" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceBill.model.serviceName').d('服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serviceName', {
                rules: [
                  {
                    max: 600,
                    message: intl.get('hzero.common.validation.max', {
                      max: 600,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
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
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceBill.model.serviceBill.from').d('发生日期从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeDateFrom')(
                <DatePicker
                  style={{ width: '100%' }}
                  format={getDateFormat()}
                  disabledDate={currentDate =>
                    getFieldValue('chargeDateTo') &&
                    moment(getFieldValue('chargeDateTo')).isBefore(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceBill.model.serviceBill.to').d('发生日期至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeDateTo')(
                <DatePicker
                  style={{ width: '100%' }}
                  format={getDateFormat()}
                  disabledDate={currentDate =>
                    getFieldValue('chargeDateFrom') &&
                    moment(getFieldValue('chargeDateFrom')).isAfter(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.status').d('状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('status')(
                <Select allowClear>
                  {statusList.map(item => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
