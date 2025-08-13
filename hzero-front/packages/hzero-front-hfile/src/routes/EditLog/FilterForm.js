/**
 * FilterForm - 二级域名单点登录配置-查询条件表单
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, Select, DatePicker } from 'hzero-ui';
import moment from 'moment';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';
import {
  DEFAULT_TIME_FORMAT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const { Option } = Select;
const dateTimeFormat = getDateTimeFormat();

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      hidden: true,
    };
  }

  /**
   *更多查询按钮
   */
  @Bind()
  handleToggle() {
    const { hidden } = this.state;
    this.setState({
      hidden: !hidden,
    });
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

  render() {
    const {
      form: { getFieldDecorator, getFieldValue },
      isSiteFlag,
      typeList = [],
    } = this.props;
    const { hidden } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" align="bottom">
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hfile.editLog.model.editLog.tenantName').d('租户')}
              >
                {getFieldDecorator('tenantId')(<Lov allowClear code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hfile.editLog.model.editLog.fileName').d('文件名')}
            >
              {getFieldDecorator('fileName')(<Input trim />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hfile.editLog.model.editLog.realName').d('提交人')}
            >
              {getFieldDecorator('realName')(<Input trim />)}
            </Form.Item>
          </Col>

          {!isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hfile.editLog.model.editLog.editType').d('编辑类型')}
              >
                {getFieldDecorator('editType')(
                  <Select allowClear>
                    {typeList.map(item => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.handleToggle}>
                {hidden
                  ? intl.get('hzero.common.button.viewMore').d('更多查询')
                  : intl.get('hzero.common.button.collected').d('收起查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" onClick={this.handleSearch} htmlType="submit">
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={hidden ? { display: 'none' } : { display: '' }}>
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hfile.editLog.model.editLog.editType').d('编辑类型')}
              >
                {getFieldDecorator('editType')(
                  <Select allowClear>
                    {typeList.map(item => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hfile.editLog.model.editLog.changeDateFrom').d('编辑时间从')}
            >
              {getFieldDecorator('changeDateFrom')(
                <DatePicker
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  placeholder=""
                  disabledDate={currentDate =>
                    getFieldValue('changeDateTo') &&
                    moment(getFieldValue('changeDateTo')).isBefore(currentDate, 'day')}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hfile.editLog.model.editLog.changeDateTo').d('编辑时间至')}
            >
              {getFieldDecorator('changeDateTo')(
                <DatePicker
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  placeholder=""
                  disabledDate={currentDate =>
                    getFieldValue('changeDateFrom') &&
                    moment(getFieldValue('changeDateFrom')).isAfter(currentDate, 'day')}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
