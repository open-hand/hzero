/**
 * model 组织信息-集团-编辑框
 * @date: 2018-7-3
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};

const FormItem = Form.Item;
@Form.create({ fieldNameProp: null })
export default class GroupModal extends React.PureComponent {
  /**
   * 编辑模态框确认
   */
  @Bind()
  handleOk() {
    const { onOk, form } = this.props;
    onOk(form.getFieldsValue());
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { form, initData, modalVisible, onCancel, updateLoading } = this.props;
    const { groupNum, groupName } = initData;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={intl.get('hpfm.group.view.message.title.modal.edit').d('编辑集团')}
        confirmLoading={updateLoading}
        visible={modalVisible}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.group.model.company.groupNum').d('集团编码')}
          >
            {groupNum}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hpfm.group.model.company.groupName').d('集团名称')}
          >
            {getFieldDecorator('groupName', {
              initialValue: groupName,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.group.model.company.groupName').d('集团名称'),
                  }),
                },
                {
                  max: 50,
                  message: intl.get('hzero.common.validation.max', { max: 50 }),
                },
              ],
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
