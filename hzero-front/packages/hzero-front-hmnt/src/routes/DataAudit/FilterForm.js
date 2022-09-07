/**
 * FilterForm - 数据变更审计-查询条件表单
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, DatePicker } from 'hzero-ui';
import Lov from 'components/Lov';
import moment from 'moment';
import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  DEFAULT_TIME_FORMAT,
} from 'utils/constants';

const FormItem = Form.Item;
const dateTimeFormat = getDateTimeFormat();

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
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
      form.validateFields((err) => {
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
      isTenant = false,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAudit.model.dataAudit.serviceName').d('服务名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serviceName', {
                rules: [
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAudit.model.dataAudit.entityCode').d('审计实体')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('entityCode', {
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
              label={intl.get('hmnt.dataAudit.model.dataAudit.tableName').d('审计表')}
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
              label={intl.get('hmnt.dataAudit.model.dataAudit.processUserName').d('操作用户')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processUserName')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAudit.model.dataAudit.entityId').d('主键ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('entityId', {
                rules: [
                  {
                    max: 120,
                    message: intl.get('hzero.common.validation.max', {
                      max: 120,
                    }),
                  },
                ],
                validateFirst: true,
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAudit.model.dataAudit.startProcessTime').d('操作时间从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('startProcessTime')(
                <DatePicker
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={(currentDate) =>
                    getFieldValue('endProcessTime') &&
                    moment(getFieldValue('endProcessTime')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAudit.model.dataAudit.endProcessTime').d('操作时间至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('endProcessTime')(
                <DatePicker
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  disabledDate={(currentDate) =>
                    getFieldValue('startProcessTime') &&
                    moment(getFieldValue('startProcessTime')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
          {!isTenant && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('hzero.common.model.tenantName').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" textField="tenantName" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAudit.model.dataAudit.auditBatchNumber').d('操作审计批次')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('auditBatchNumber')(<Input />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
