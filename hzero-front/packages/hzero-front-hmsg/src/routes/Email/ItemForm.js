import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import intl from 'utils/intl';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
@Form.create({ fieldNameProp: null })
export default class ItemForm extends React.PureComponent {
  @Bind()
  handleOk() {
    const { form, onOk = e => e, initData = {} } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const data = initData.propertyId !== undefined ? {...initData, ...fieldsValue} : {
          ...fieldsValue,
          propertyId: uuid(),
          isCreate: true,
        };
        onOk(data);
      }
    });
  }

  render() {
    const { form, initData, title, modalVisible, onCancel } = this.props;
    const { getFieldDecorator } = form;
    const { propertyCode, propertyValue } = initData;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          <FormItem
            {...formLayout}
            label={intl.get('hmsg.email.model.email.propertyCode').d('属性编码')}
          >
            {getFieldDecorator('propertyCode', {
              initialValue: propertyCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.email.model.email.propertyCode').d('属性编码'),
                  }),
                },
              ],
            })(<Input trim inputChinese={false} />)}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hmsg.email.model.email.propertyValue').d('属性值')}
          >
            {getFieldDecorator('propertyValue', {
              initialValue: propertyValue,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hmsg.email.model.email.propertyValue').d('属性值'),
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
