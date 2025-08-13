/**
 * Message API返回消息管理
 * @date: 2019-1-9
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Select, Spin, DatePicker } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import Switch from 'components/Switch';

import intl from 'utils/intl';
import { getDateTimeFormat } from 'utils/utils';

const { Option } = Select;
const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class UserForm extends React.PureComponent {
  @Bind()
  handleOK() {
    const { form, onOk = (e) => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const {
      form,
      // loading,
      frequencyList = [],
      initData,
    } = this.props;
    const { frequency, startDate, endDate, enabledFlag, customFilter } = initData;
    const { getFieldDecorator, getFieldValue } = form;
    return (
      <Spin spinning={false}>
        <Form>
          <FormItem
            {...formLayout}
            label={intl.get('hiam.ldap.model.ldap.frequency').d('同步频率')}
          >
            {getFieldDecorator('frequency', {
              initialValue: frequency,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.ldap.model.ldap.frequency').d('同步频率'),
                  }),
                },
              ],
            })(
              <Select>
                {frequencyList.map((item) => (
                  <Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hiam.ldap.model.ldap.startDate').d('开始时间')}
          >
            {getFieldDecorator('startDate', {
              // initialValue: startDate,
              initialValue: startDate && moment(startDate, getDateTimeFormat()),
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.ldap.model.ldap.startDate').d('开始时间'),
                  }),
                },
              ],
            })(
              <DatePicker
                showTime
                placeholder=""
                format={getDateTimeFormat()}
                disabledDate={(currentDate) =>
                  getFieldValue('endDate') &&
                  moment(getFieldValue('endDate')).isBefore(currentDate, 'day')
                }
              />
            )}
          </FormItem>
          <FormItem {...formLayout} label={intl.get('hiam.ldap.model.ldap.endDate').d('结束时间')}>
            {getFieldDecorator('endDate', {
              // initialValue: endDate,
              initialValue: endDate && moment(endDate, getDateTimeFormat()),
            })(
              <DatePicker
                showTime
                placeholder=""
                format={getDateTimeFormat()}
                disabledDate={(currentDate) =>
                  getFieldValue('startDate') &&
                  moment(getFieldValue('startDate')).isAfter(currentDate, 'day')
                }
              />
            )}
          </FormItem>
          <Form.Item
            {...formLayout}
            label={intl.get('hzero.common.status.customFilter').d('自定义筛选条件')}
          >
            {form.getFieldDecorator('customFilter', {
              initialValue: customFilter,
            })(<Input />)}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {form.getFieldDecorator('enabledFlag', {
              initialValue: enabledFlag || 0,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Spin>
    );
  }
}
