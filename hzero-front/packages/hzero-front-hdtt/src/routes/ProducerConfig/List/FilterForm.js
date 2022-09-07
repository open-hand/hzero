/**
 * FilterForm - 数据消息生产消费配置列表页查询条件表单
 * @date: 2019-4-15
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, DatePicker, Select } from 'hzero-ui';
import moment from 'moment';
import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';
import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

const { Option } = Select;
const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @reactProps {array[Object]} initStatus - 初始化状态
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hdtt/producer-config/list' })
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
      form: { getFieldDecorator, getFieldValue },
      initStatus = [],
      isTenant,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get(`hdtt.producerConfig.model.producerConfig.producerService`)
                .d('生产服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serviceCode')(
                <Lov code="HDTT.SERVICE" textField="serviceName" />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`hdtt.producerConfig.model.producerConfig.tableName`).d('生产表名')}
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
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get(`hdtt.producerConfig.model.producerConfig.consServiceName`)
                .d('消费服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('consumerService')(
                <Lov code="HDTT.SERVICE" textField="serviceName" />
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
              label={intl
                .get(`hdtt.producerConfig.model.producerConfig.consTableName`)
                .d('消费表名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('consumerTable', {
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
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get(`hdtt.producerConfig.model.producerConfig.initStatus`)
                .d('初始化状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processStatus')(
                <Select allowClear>
                  {initStatus &&
                    initStatus.map(({ value, meaning }) => (
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
              label={intl
                .get(`hdtt.producerConfig.model.producerConfig.initTimeFrom`)
                .d('初始化时间从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processTimeFrom')(
                <DatePicker
                  showTime
                  format={getDateTimeFormat()}
                  disabledDate={currentDate =>
                    getFieldValue('processTimeTo') &&
                    moment(getFieldValue('processTimeTo')).isBefore(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get(`hdtt.producerConfig.model.producerConfig.initTimeTo`)
                .d('初始化时间至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processTimeTo')(
                <DatePicker
                  showTime
                  format={getDateTimeFormat()}
                  disabledDate={currentDate =>
                    getFieldValue('processTimeFrom') &&
                    moment(getFieldValue('processTimeFrom')).isAfter(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
          {!isTenant && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                label={intl
                  .get(`hdtt.producerConfig.model.producerConfig.tenantName`)
                  .d('消费租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" textField="tenantName" />)}
              </FormItem>
            </Col>
          )}
        </Row>
      </Form>
    );
  }
}
