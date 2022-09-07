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
export default class TLModal extends React.Component {
  @Bind()
  saveAndClose() {
    const { onOK = (e) => e, form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOK(fieldsValue);
      }
    });
  }

  render() {
    const {
      modalVisible,
      onCancel,
      form,
      list,
      label,
      width = '520px',
      inputSize = {},
      dbc2sbc = false,
    } = this.props;
    const { getFieldDecorator } = form;
    const { zh = 60, en = 120 } = inputSize;
    return (
      <Modal
        destroyOnClose
        title={label}
        width={width}
        visible={modalVisible}
        onCancel={onCancel}
        onOk={this.saveAndClose}
      >
        <Form>
          {list.map((item) => (
            <FormItem label={item.name} key={item.code} {...formItemLayout}>
              {getFieldDecorator(item.code, {
                initialValue: item.value,
                rules: [
                  {
                    max: item.code === 'zh_CN' ? zh : en,
                    message: intl.get('hzero.common.validation.max', {
                      max: item.code === 'zh_CN' ? zh : en,
                    }),
                  },
                ],
              })(<Input dbc2sbc={dbc2sbc} />)}
            </FormItem>
          ))}
        </Form>
      </Modal>
    );
  }
}
