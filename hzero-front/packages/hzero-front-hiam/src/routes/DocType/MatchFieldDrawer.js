import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

@Form.create({ fieldNameProp: null })
export default class MatchFieldDrawer extends React.Component {
  @Bind()
  onCancel() {
    const { form, onCancel } = this.props;
    form.resetFields();
    onCancel();
  }

  @Bind()
  onOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        onOk(values);
      }
    });
  }

  render() {
    const { form, modalVisible, value, validaterField } = this.props;
    return (
      <>
        <Modal
          destroyOnClose
          wrapClassName="ant-modal-sidebar-right"
          transitionName="move-right"
          width={520}
          title={intl.get('hiam.docType.model.docType.value').d('来源匹配字段')}
          visible={modalVisible}
          onCancel={this.onCancel}
          onOk={this.onOk}
        >
          <Form>
            <Form.Item
              label={intl.get('hiam.docType.model.docType.value').d('来源匹配字段')}
              {...formLayout}
            >
              {form.getFieldDecorator('sourceMatchField', {
                rules: [
                  {
                    max: 1200,
                    message: intl.get('hzero.common.validation.max', {
                      max: 1200,
                    }),
                  },
                  {
                    required: !validaterField,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.value').d('来源匹配字段'),
                    }),
                  },
                ],
                initialValue: value,
              })(<Input.TextArea autoSize={{ minRows: 6 }} />)}
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}
