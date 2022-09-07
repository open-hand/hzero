import React from 'react';
import { Form, Select, Modal, InputNumber, DatePicker } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import Lov from 'components/Lov';
import { getDateTimeFormat } from 'utils/utils';
import intl from 'utils/intl';
import { dateTimeRender } from 'utils/renderer';
import styles from './index.less';

const formLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class ClearLogsDrawer extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showTimePicker: false,
    };
  }

  @Bind()
  handleCancel() {
    const { form, onCancel = (e) => e } = this.props;
    form.resetFields();
    onCancel();
  }

  @Bind()
  handleOk() {
    const { form, tenantRoleLevel, currentTenantId, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        let params = {
          ...fieldsValue,
          tenantId: tenantRoleLevel ? currentTenantId : fieldsValue.tenantId,
        };
        if (fieldsValue.requestTimeStart && fieldsValue.requestTimeEnd) {
          params = {
            ...params,
            requestTimeStart: dateTimeRender(fieldsValue.requestTimeStart),
            requestTimeEnd: dateTimeRender(fieldsValue.requestTimeEnd),
          };
        }
        onOk(params);
      }
    });
  }

  @Bind()
  handleClearTypeChange(value) {
    this.setState({ showTimePicker: value === 'SPECIFIED_TIME_RANGE' });
    this.props.form.setFieldsValue({ requestTimeStart: undefined, requestTimeEnd: undefined });
  }

  render() {
    const { form, title, modalVisible, loading, tenantRoleLevel, clearTypeList = [] } = this.props;
    const { showTimePicker } = this.state;
    const { getFieldDecorator, getFieldValue } = form;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        <Form>
          {!tenantRoleLevel && (
            <Form.Item
              {...formLayout}
              label={intl.get('hzero.common.model.common.tenantId').d('租户')}
            >
              {getFieldDecorator('tenantId', {})(<Lov code="HPFM.TENANT" />)}
            </Form.Item>
          )}
          <Form.Item
            label={intl.get('hitf.interfaceLogs.view.message.clearType').d('类型')}
            {...formLayout}
          >
            {getFieldDecorator('clearType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hitf.interfaceLogs.view.message.clearType').d('类型'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '100%' }} onChange={this.handleClearTypeChange}>
                {clearTypeList.map((item) => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          {showTimePicker && (
            <Form.Item
              label={intl
                .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeStart')
                .d('平台接口请求时间从')}
              {...formLayout}
            >
              {getFieldDecorator('requestTimeStart', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeStart')
                        .d('平台接口请求时间从'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  format={getDateTimeFormat()}
                  placeholder=""
                  disabledDate={(currentDate) =>
                    getFieldValue('requestTimeEnd') &&
                    moment(getFieldValue('requestTimeEnd')).isBefore(currentDate, 'time')
                  }
                  style={{ width: '100%' }}
                />
              )}
            </Form.Item>
          )}
          {showTimePicker && (
            <Form.Item
              label={intl
                .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeEnd')
                .d('平台接口请求时间至')}
              {...formLayout}
            >
              {getFieldDecorator('requestTimeEnd', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeEnd')
                        .d('平台接口请求时间至'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  format={getDateTimeFormat()}
                  placeholder=""
                  disabledDate={(currentDate) =>
                    getFieldValue('requestTimeStart') &&
                    moment(getFieldValue('requestTimeStart')).isAfter(currentDate, 'time')
                  }
                  style={{ width: '100%' }}
                />
              )}
            </Form.Item>
          )}
          <Form.Item
            {...formLayout}
            label={intl.get('hzero.common.model.hitf.interfaceLogIdStart').d('日志主键从')}
          >
            {getFieldDecorator(
              'interfaceLogIdStart',
              {}
            )(<InputNumber className={styles.inputNumberUnlimited} precision={0} min={1} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hzero.common.model.hitf.interfaceLogIdEnd').d('日志主键至')}
          >
            {getFieldDecorator(
              'interfaceLogIdEnd',
              {}
            )(<InputNumber className={styles.inputNumberUnlimited} precision={0} min={1} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hzero.common.model.hitf.executeBatchCount').d('执行批次数')}
          >
            {getFieldDecorator(
              'executeBatchCount',
              {}
            )(
              <InputNumber
                className={styles.inputNumberUnlimited}
                precision={0}
                min={1}
                max={1000}
              />
            )}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
