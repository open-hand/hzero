/**
 * FilterForm - 生产消费异常监控列表页查询条件表单
 * @date: 2019-5-6
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, DatePicker, Select } from 'hzero-ui';
import moment from 'moment';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  DEFAULT_TIME_FORMAT,
} from 'utils/constants';

const { Option } = Select;
const FormItem = Form.Item;
const dateTimeFormat = getDateTimeFormat();
const promptCode = 'hdtt.exception';

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

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
   * 表单展开收起
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
      form: { getFieldDecorator, getFieldValue },
      errorTypes,
      eventTypes,
      isTenant,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.errorType`).d('错误类型')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('errorType')(
                <Select allowClear>
                  {errorTypes.length &&
                    errorTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.errorTimeFrom`).d('出错时间从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('errorTimeFrom')(
                <DatePicker
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={currentDate =>
                    getFieldValue('errorTimeTo') &&
                    moment(getFieldValue('errorTimeTo')).isBefore(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.errorTimeTo`).d('出错时间至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('errorTimeTo')(
                <DatePicker
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={currentDate =>
                    getFieldValue('errorTimeFrom') &&
                    moment(getFieldValue('errorTimeFrom')).isAfter(currentDate, 'day')}
                />
              )}
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
              label={intl.get(`${promptCode}.model.exception.sourceKeyId`).d('数据ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceKeyId', {
                rules: [
                  {
                    pattern: /^[1-9]\d*$/,
                    message: intl
                      .get(`${promptCode}.model.exception.number.warning`)
                      .d('请输入正整数'),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.sourceService`).d('生产服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceService')(
                <Lov code="HDTT.SERVICE" textField="serviceName" />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.tableName`).d('生产表名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceTable', {
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
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.targetService`).d('消费服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('targetService')(
                <Lov code="HDTT.SERVICE" textField="serviceName" />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.eventType`).d('事件类型')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('eventType')(
                <Select allowClear>
                  {eventTypes.length &&
                    eventTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.model.exception.eventId`).d('事件ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('eventId', {
                rules: [
                  {
                    pattern: /^[1-9]\d*$/,
                    message: intl
                      .get(`${promptCode}.model.exception.number.warning`)
                      .d('请输入正整数'),
                  },
                  {
                    max: 20,
                    message: intl.get('hzero.common.validation.max', {
                      max: 20,
                    }),
                  },
                ],
                validateFirst: true,
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          {!isTenant && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                label={intl.get('entity.tenant.tag').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('sourceTenantId')(
                  <Lov code="HPFM.TENANT" textField="tenantName" />
                )}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get(`${promptCode}.model.exception.checkCompleteFlag`)
                .d('是否包含已完成')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('checkCompleteFlag', {
                initialValue: 0,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
