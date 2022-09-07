import React from 'react';
import { Form, Select, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 15 },
};

@Form.create({ fieldNameProp: null })
export default class ClearLogsDrawer extends React.Component {
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
          <Form.Item
            {...formLayout}
            label={intl.get('hzero.common.model.common.tenantId').d('租户')}
          >
            {getFieldDecorator('tenantId', {})(<Lov code="HPFM.TENANT" />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.login.view.message.clearType').d('类型')}
            {...formLayout}
          >
            {getFieldDecorator('clearType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.login.view.message.clearType').d('类型'),
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
        </Form>
      </Modal>
    );
  }
}
