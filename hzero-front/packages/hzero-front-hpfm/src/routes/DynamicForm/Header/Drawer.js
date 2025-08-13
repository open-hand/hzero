/**
 * 动态表单配置行
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import TLEditor from 'components/TLEditor';

const { Option } = Select;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  /**
   * 确定
   */
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      anchor,
      title,
      confirmLoading,
      onCancel,
      modalVisible,
      configGroupList,
      initData: {
        formHeaderId,
        formCode,
        formName,
        formGroupCode,
        formDescription,
        enabledFlag,
        _token,
      },
    } = this.props;
    const { getFieldDecorator } = form;
    const isEditing = !isUndefined(formHeaderId);
    return (
      <Modal
        destroyOnClose
        title={title}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={modalVisible}
        confirmLoading={confirmLoading}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.header.formCode').d('配置编码')}
          >
            {getFieldDecorator('formCode', {
              initialValue: formCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.header.formCode').d('配置编码'),
                  }),
                },
                {
                  pattern: CODE,
                  message: intl
                    .get('hzero.common.validation.code')
                    .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input disabled={isEditing} inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.header.formName').d('配置名称')}
          >
            {getFieldDecorator('formName', {
              initialValue: formName,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.header.formName').d('配置名称'),
                  }),
                },
                {
                  max: 255,
                  message: intl.get('hzero.common.validation.max', {
                    max: 255,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.dynamicForm.header.formName').d('配置名称')}
                field="formName"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.header.formGroupCode').d('配置归类')}
          >
            {getFieldDecorator('formGroupCode', {
              initialValue: formGroupCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.header.formGroupCode').d('配置归类'),
                  }),
                },
              ],
            })(
              <Select disabled={isEditing}>
                {configGroupList.map((config) => (
                  <Option key={config.value} value={config.value}>
                    {config.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.header.formDescription').d('配置描述')}
          >
            {getFieldDecorator('formDescription', {
              initialValue: formDescription,
              rules: [
                {
                  max: 480,
                  message: intl.get('hzero.common.validation.max', {
                    max: 480,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.dynamicForm.header.formDescription').d('配置描述')}
                field="formDescription"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: formHeaderId ? enabledFlag : 1,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
