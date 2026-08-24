import React from 'react';
import { Modal, Input, Form } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const FormItem = Form.Item;
const formItemLayout = {
  labelCol: {
    sm: { span: 6 },
  },
  wrapperCol: {
    sm: { span: 14 },
  },
};

@Form.create({ fieldNameProp: null })
export default class MultiInfoModal extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        onOk(values);
      }
    });
  }

  render() {
    const {
      visible,
      onCancel,
      form,
      modalData = [],
      label = intl.get('hzero.common.button.edit').d('编辑'),
      // dataIndex,
      width = '520px',
    } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={label}
        width={width}
        visible={visible}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          {modalData.map((item) => (
            <FormItem label={item.meaning || item.lang} key={item.lang} {...formItemLayout}>
              {getFieldDecorator(item.lang, {
                initialValue: item.value,
              })(<Input dbc2sbc={false} />)}
            </FormItem>
          ))}
        </Form>
        {modalData.length === 0 &&
          intl.get('himp.template.view.message.title.noMaintain').d('未维护多语言类型导入模版!!')}
      </Modal>
    );
  }
}
