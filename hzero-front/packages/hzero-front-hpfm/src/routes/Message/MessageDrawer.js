/**
 * Message API返回消息管理
 * @date: 2019-1-9
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal, Select, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { CODE } from 'utils/regExp';

const { Option } = Select;
const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class MessageForm extends React.PureComponent {
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
      initData,
      languageList,
      messageType,
      title,
      modalVisible,
      loading,
      onCancel,
      initLoading,
    } = this.props;
    const { messageId, code, type, lang, description } = initData;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
      >
        <Spin spinning={initLoading}>
          <Form>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.message.model.message.code').d('消息编码')}
            >
              {getFieldDecorator('code', {
                initialValue: code,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.message.model.message.code').d('消息编码'),
                    }),
                  },
                  {
                    max: 180,
                    message: intl.get('hzero.common.validation.max', {
                      max: 180,
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim inputChinese={false} disabled={messageId !== undefined} />)}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.message.model.message.type').d('消息类型')}
            >
              {getFieldDecorator('type', {
                initialValue: type,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.message.model.message.type').d('消息类型'),
                    }),
                  },
                ],
              })(
                <Select>
                  {messageType.map((item) => (
                    <Option key={item.meaning} value={item.meaning}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              className="lang"
              {...formLayout}
              label={intl.get('hpfm.message.model.message.lang').d('语言')}
            >
              {getFieldDecorator('lang', {
                initialValue: lang,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.message.model.message.lang').d('语言'),
                    }),
                  },
                ],
              })(
                <Select disabled={messageId !== undefined}>
                  {languageList.map((item) => (
                    <Option key={item.code} value={item.code}>
                      {item.description}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.message.model.message.description').d('消息描述')}
            >
              {getFieldDecorator('description', {
                initialValue: description,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.message.model.message.description').d('消息描述'),
                    }),
                  },
                  {
                    max: 1000,
                    message: intl.get('hzero.common.validation.max', {
                      max: 1000,
                    }),
                  },
                ],
              })(<Input dbc2sbc={false} />)}
            </FormItem>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
