import React, { PureComponent } from 'react';
import { Modal, Form, Input } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';

import intl from 'utils/intl';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

/**
 * 新建模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  @Bind()
  onOk() {
    const { form, onAdd } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onAdd(values);
      }
    });
  }

  render() {
    const { visible, onCancel, anchor, form, initData = {} } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        width={520}
        title={
          initData.variableId
            ? intl.get('hwfp.processStart.view.message.edit').d('编辑流程启动')
            : intl.get('hwfp.processStart.view.message.create').d('新建流程启动')
        }
        visible={visible}
        onCancel={onCancel}
        onOk={this.onOk}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
      >
        <Form>
          <FormItem label={intl.get('hwfp.common.model.param.name').d('参数名称')} {...formLayout}>
            {getFieldDecorator('name', {
              initialValue: initData.name,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.param.name').d('参数名称'),
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          <FormItem label={intl.get('hwfp.common.model.param.value').d('参数值')} {...formLayout}>
            {getFieldDecorator('value', {
              initialValue: initData.value,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.param.value').d('参数值'),
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hwfp.common.model.param.requiredFlag').d('必输')}
            {...formLayout}
          >
            {getFieldDecorator('requiredFlag', {
              initialValue: initData.requiredFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
