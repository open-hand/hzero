import React, { Component } from 'react';
import { Form, Col, Row, Input, Select, DatePicker } from 'hzero-ui';
import moment from 'moment';

import Switch from 'components/Switch';

import { EMAIL, PHONE } from 'utils/regExp';

import intl from 'utils/intl';
import { getDateFormat } from 'utils/utils';
import {
  FORM_COL_3_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  DEFAULT_DATE_FORMAT,
} from 'utils/constants';

import styles from './index.less';

Input.displayName = 'Input';

/**
 * 员工基本信息表单
 * @extends {Component} - React.Component
 * @reactProps {!Object} employeeInfo - 数据源
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
export default class DataForm extends Component {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { employeeStatus = [], customizeForm, form } = this.props;
    const { getFieldDecorator } = form;
    const employeeInfo = this.props.employeeInfo || {};
    return customizeForm(
      { code: 'HPFM.EMPLOYEE_DETAIL.HEADER', form, dataSource: employeeInfo },
      <Form style={{ maxWidth: 1000 }} className={styles['hpfm-employee-detail-item']}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('entity.employee.code').d('员工编码')}
            >
              {getFieldDecorator('employeeNum', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.employee.code').d('员工编码'),
                    }),
                  },
                ],
                initialValue: employeeInfo.employeeNum,
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('entity.employee.name').d('员工姓名')}
            >
              {getFieldDecorator('name', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.employee.name').d('员工姓名'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
                initialValue: employeeInfo.name,
              })(<Input disabled={employeeInfo.status === 'LEAVE'} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.employee.model.employee.phoneticize').d('拼音')}
            >
              {getFieldDecorator('phoneticize', {
                initialValue: employeeInfo.phoneticize,
                rules: [
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Input disabled={employeeInfo.status === 'LEAVE'} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={48}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.employee.model.employee.quickIndex').d('快速索引')}
            >
              {getFieldDecorator('quickIndex', {
                initialValue: employeeInfo.quickIndex,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={employeeInfo.status === 'LEAVE'}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.employee.model.employee.status').d('员工状态')}
            >
              {getFieldDecorator('status', {
                initialValue: employeeInfo.status || 'ON',
                dataSource: employeeStatus,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.employee.model.employee.status').d('员工状态'),
                    }),
                  },
                ],
              })(
                <Select className={styles['full-width']} allowClear>
                  {employeeStatus.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status.enable').d('启用')}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: employeeInfo.enabledFlag,
              })(<Switch disabled={employeeInfo.status === 'LEAVE'} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={48}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.cellphone').d('手机号')}
            >
              {getFieldDecorator('mobile', {
                initialValue: employeeInfo.mobile,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.cellphone').d('手机号'),
                    }),
                  },
                  {
                    pattern: PHONE,
                    message: intl.get('hzero.common.validation.phone').d('手机格式不正确'),
                  },
                ],
              })(<Input disabled={employeeInfo.status === 'LEAVE'} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item {...EDIT_FORM_ITEM_LAYOUT} label={intl.get('hzero.common.email').d('邮箱')}>
              {getFieldDecorator('email', {
                initialValue: employeeInfo.email,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.email').d('邮箱'),
                    }),
                  },
                  {
                    pattern: EMAIL,
                    message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Input disabled={employeeInfo.status === 'LEAVE'} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.employee.model.employee.entryDate').d('入职日期')}
            >
              {getFieldDecorator('entryDate', {
                initialValue: employeeInfo.entryDate
                  ? moment(employeeInfo.entryDate, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  placeholder=""
                  style={{ width: '100%' }}
                  format={getDateFormat()}
                  disabled={employeeInfo.status === 'LEAVE'}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
