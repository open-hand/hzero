import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const { TextArea } = Input;

@Form.create({ fieldNameProp: null })
export default class ApproveDrawer extends React.Component {
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
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
    const { visible, form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        title={intl.get('hwfp.task.view.title.approve').d('审批意见')}
        width={700}
        destroyOnClose
        visible={visible}
        closable
        onOk={this.handleOk}
        onCancel={this.handleCancel}
      >
        <Form>
          <Form.Item>
            {getFieldDecorator('comment', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.task.view.message.comment').d('审批意见'),
                  }),
                },
              ],
            })(<TextArea maxLength={200} style={{ height: '120px', marginBottom: 8 }} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
