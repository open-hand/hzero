/**
 * FilterForm 查询表单
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-07-18
 * @copyright 2019 © HAND
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, DatePicker, Select } from 'hzero-ui';
import moment from 'moment';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getDateTimeFormat, getCurrentOrganizationId } from 'utils/utils';
import {
  DEFAULT_DATETIME_FORMAT,
  DEFAULT_TIME_FORMAT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

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
    props.onRef(this);
    this.state = {
      hidden: true,
    };
  }

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch, history } = this.props;
    if (onSearch) {
      history.push('/hmnt/audit-query');
      form.validateFields((err) => {
        if (!err) {
          const fieldValues = form.getFieldsValue();
          fieldValues.auditDateStart =
            fieldValues.auditDateStart &&
            fieldValues.auditDateStart.format(DEFAULT_DATETIME_FORMAT);
          fieldValues.auditDateEnd =
            fieldValues.auditDateEnd && fieldValues.auditDateEnd.format(DEFAULT_DATETIME_FORMAT);
          onSearch(fieldValues);
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

  @Bind()
  handleToggle() {
    const { hidden } = this.state;
    this.setState({
      hidden: !hidden,
    });
  }

  render() {
    const {
      form: { getFieldDecorator, getFieldValue },
      statusList,
      methodList,
      isSiteFlag,
      tenantId,
    } = this.props;
    const { hidden } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hmnt.auditQuery.model.auditQuery.tenantName').d('租户')}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.serviceName').d('服务名称')}
            >
              {getFieldDecorator('serviceName')(
                <Lov
                  code={isSiteFlag ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG'}
                  queryParams={!isSiteFlag && { organizationId: tenantId }}
                  textField="serviceName"
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.userId').d('用户')}
            >
              {getFieldDecorator('userId')(
                <Lov
                  code={isSiteFlag ? 'HIAM.SITE.USER' : 'HIAM.TENANT.USER'}
                  queryParams={!isSiteFlag && { organizationId: tenantId }}
                />
              )}
            </Form.Item>
          </Col>
          {!isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hmnt.auditQuery.model.auditQuery.auditContent').d('操作内容')}
              >
                {getFieldDecorator('auditContent')(<Input />)}
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
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.auditResult').d('操作结果')}
            >
              {getFieldDecorator('auditResult')(
                <Select allowClear>
                  {statusList.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.auditDateStart').d('操作日期从')}
            >
              {getFieldDecorator('auditDateStart')(
                <DatePicker
                  placeholder=""
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={(currentDate) =>
                    getFieldValue('auditDateEnd') &&
                    moment(getFieldValue('auditDateEnd')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.auditDateEnd').d('操作日期至')}
            >
              {getFieldDecorator('auditDateEnd')(
                <DatePicker
                  placeholder=""
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={(currentDate) =>
                    getFieldValue('auditDateStart') &&
                    moment(getFieldValue('auditDateStart')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={hidden ? { display: 'none' } : { display: '' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.requestMethod').d('请求方式')}
            >
              {getFieldDecorator('requestMethod')(
                <Select allowClear>
                  {methodList.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.requestUrl').d('请求路径')}
            >
              {getFieldDecorator('requestUrl')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.requestIp').d('请求IP')}
            >
              {getFieldDecorator('requestIp')(<Input inputChinese={false} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={hidden ? { display: 'none' } : { display: '' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.clientName').d('客户端')}
            >
              {getFieldDecorator('clientName')(<Lov code="HIAM.OAUTH_CLIENT" />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmnt.auditQuery.model.auditQuery.roleId').d('角色名称')}
            >
              {getFieldDecorator('roleId')(
                <Lov
                  code="HMNT.AUDIT.ROLE"
                  queryParams={!isSiteFlag && { tenantId: getCurrentOrganizationId() }}
                />
              )}
            </Form.Item>
          </Col>
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hmnt.auditQuery.model.auditQuery.auditContent').d('操作内容')}
              >
                {getFieldDecorator('auditContent')(<Input />)}
              </Form.Item>
            </Col>
          )}
          {!isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hmnt.auditQuery.model.auditQuery.businessKey').d('业务主键')}
              >
                {getFieldDecorator('businessKey')(<Input />)}
              </Form.Item>
            </Col>
          )}
        </Row>
        {isSiteFlag && (
          <Row {...SEARCH_FORM_ROW_LAYOUT} style={hidden ? { display: 'none' } : { display: '' }}>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hmnt.auditQuery.model.auditQuery.businessKey').d('业务主键')}
              >
                {getFieldDecorator('businessKey')(<Input />)}
              </Form.Item>
            </Col>
          </Row>
        )}
      </Form>
    );
  }
}
