/**
 * RefuseModal 拒绝对话框
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-17
 * @copyright 2018 © HAND
 */

import React from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Input, Modal } from 'hzero-ui';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const { TextArea } = Input;

@Form.create({ fieldNameProp: null })
export default class RefuseModal extends React.PureComponent {
  @Bind()
  handleOK() {
    const { form, onOk = e => e, initData } = this.props;
    const formDetail = form.getFieldsValue();
    const params = { ...formDetail, ...initData };
    onOk(params);
  }

  render() {
    const { form, title, modalVisible, onCancel, loading } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        onCancel={onCancel}
        onOk={this.handleOK}
        confirmLoading={loading}
      >
        <Form>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            required
            label={intl.get('hzero.common.button.refundReason').d('拒绝原因')}
          >
            {getFieldDecorator('remark', {})(<TextArea autosize={{ minRows: 6, maxRows: 6 }} />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
