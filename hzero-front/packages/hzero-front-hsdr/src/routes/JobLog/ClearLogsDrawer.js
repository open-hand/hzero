import React from 'react';
import { Form, Select, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 15 },
};
@Form.create({ fieldNameProp: null })
export default class ClearLogsDrawer extends React.PureComponent {
  @Bind()
  handleCancel() {
    const { form, onCancel = e => e } = this.props;
    form.resetFields();
    onCancel();
  }

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
    const { form, title, modalVisible, loading, clearTypeList = [] } = this.props;
    const { getFieldDecorator } = form;
    const tenantRoleLevel = isTenantRoleLevel();
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
            <FormItem {...formLayout} label={intl.get('entity.tenant.tag').d('租户')}>
              {getFieldDecorator('tenantId', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.tenant.tag').d('租户'),
                    }),
                  },
                ],
              })(<Lov code="HPFM.TENANT" />)}
            </FormItem>
          )}
          <Form.Item
            label={intl.get('hsdr.jobLog.view.message.clearType').d('类型')}
            {...formLayout}
          >
            {getFieldDecorator('clearType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hsdr.jobLog.view.message.clearType').d('类型'),
                  }),
                },
              ],
            })(
              <Select style={{ width: '100%' }}>
                {clearTypeList.map(item => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item label={intl.get('hsdr.jobLog.view.message.jobId').d('任务')} {...formLayout}>
            {getFieldDecorator('jobId')(<Lov code="HSDR.LOG.JOB_INFO" />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
