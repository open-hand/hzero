import React from 'react';
import { DatePicker, Form, InputNumber, Modal, Select, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import moment from 'moment';
import { getDateFormat } from 'utils/utils';

const { Option } = Select;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
const dateFormat = getDateFormat();
@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const {
      form,
      initData = {},
      intervalTypeList = [],
      visible,
      createRequestLoading,
      onCancel,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    getFieldDecorator('cycleFlag', {
      initialValue: 1,
    });
    return (
      <Modal
        destroyOnClose
        title={intl.get('hrpt.reportQuery.option.createRequest').d('定时报表')}
        width={520}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        confirmLoading={createRequestLoading}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form.Item
          label={intl.get('hrpt.reportQuery.model.concRequest.startDate').d('周期开始时间')}
          {...formLayout}
        >
          {getFieldDecorator('startDate', {
            initialValue:
              initData.startDate && moment(initData.startDate, `${dateFormat} HH:mm:ss`),
          })(
            <DatePicker
              style={{ width: '100%' }}
              showTime
              placeholder=""
              format={`${dateFormat} HH:mm:ss`}
              disabledDate={(currentDate) =>
                getFieldValue('endDate') &&
                moment(getFieldValue('endDate')).isBefore(currentDate, 'day')
              }
            />
          )}
        </Form.Item>
        <Form.Item
          label={intl.get('hrpt.reportQuery.model.concRequest.endDate').d('周期结束时间')}
          {...formLayout}
        >
          {getFieldDecorator('endDate', {
            initialValue: initData.endDate && moment(initData.endDate, `${dateFormat} HH:mm:ss`),
          })(
            <DatePicker
              style={{ width: '100%' }}
              showTime
              placeholder=""
              format={`${dateFormat} HH:mm:ss`}
              disabledDate={(currentDate) =>
                getFieldValue('startDate') &&
                moment(getFieldValue('startDate')).isAfter(currentDate, 'day')
              }
            />
          )}
        </Form.Item>
        <Form.Item
          label={intl.get('hrpt.reportQuery.model.concRequest.intervalType').d('间隔类型')}
          {...formLayout}
        >
          {getFieldDecorator('intervalType', {
            initialValue: initData.intervalType,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hrpt.reportQuery.model.concRequest.intervalType').d('间隔类型'),
                }),
              },
            ],
          })(
            <Select style={{ width: '100%' }}>
              {intervalTypeList.map((item) => (
                <Option key={item.value} value={item.value}>
                  {item.meaning}
                </Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item
          {...formLayout}
          label={intl.get('hrpt.reportQuery.model.concRequest.intervalNumber').d('间隔大小')}
        >
          {getFieldDecorator('intervalNumber', {
            initialValue: initData.intervalHour,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hrpt.reportQuery.model.concRequest.intervalNumber').d('间隔大小'),
                }),
              },
            ],
          })(<InputNumber min={0} step={1} style={{ width: '100%' }} />)}
        </Form.Item>
        <Form.Item
          {...formLayout}
          label={intl.get('hrpt.reportQuery.model.concRequest.intervalHour').d('固定间隔-时')}
        >
          {getFieldDecorator('intervalHour', {
            initialValue: initData.intervalHour,
            rules: [
              {
                required: getFieldValue('intervalType') && getFieldValue('intervalType') === 'DAY',
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl
                    .get('hrpt.reportQuery.model.concRequest.intervalHour')
                    .d('固定间隔-时'),
                }),
              },
            ],
          })(
            <InputNumber
              min={0}
              step={1}
              style={{ width: '100%' }}
              disabled={getFieldValue('intervalType') && getFieldValue('intervalType') !== 'DAY'}
            />
          )}
        </Form.Item>
        <Form.Item
          {...formLayout}
          label={intl.get('hrpt.reportQuery.model.concRequest.intervalMinute').d('固定间隔-分')}
        >
          {getFieldDecorator('intervalMinute', {
            initialValue: initData.intervalMinute,
            rules: [
              {
                required:
                  getFieldValue('intervalType') === 'DAY' ||
                  getFieldValue('intervalType') === 'HOUR',
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl
                    .get('hrpt.reportQuery.model.concRequest.intervalMinute')
                    .d('固定间隔-分'),
                }),
              },
            ],
          })(
            <InputNumber
              min={0}
              step={1}
              max={60}
              style={{ width: '100%' }}
              disabled={
                getFieldValue('intervalType') === 'MINUTE' ||
                getFieldValue('intervalType') === 'SECOND'
              }
            />
          )}
        </Form.Item>
        <Form.Item
          {...formLayout}
          label={intl.get('hrpt.reportQuery.model.concRequest.intervalSecond').d('固定间隔-秒')}
        >
          {getFieldDecorator('intervalSecond', {
            initialValue: initData.intervalSecond,
            rules: [
              {
                required:
                  getFieldValue('intervalType') && getFieldValue('intervalType') !== 'SECOND',
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl
                    .get('hrpt.reportQuery.model.concRequest.intervalSecond')
                    .d('固定间隔-秒'),
                }),
              },
            ],
          })(
            <InputNumber
              min={0}
              step={1}
              max={60}
              style={{ width: '100%' }}
              disabled={getFieldValue('intervalType') === 'SECOND'}
            />
          )}
        </Form.Item>
        <Form.Item
          label={intl.get('hrpt.reportQuery.model.concRequest.endEmail').d('通知邮箱')}
          {...formLayout}
        >
          {getFieldDecorator('endEmail', {
            initialValue: initData.endEmail,
          })(<Input />)}
        </Form.Item>
      </Modal>
    );
  }
}
