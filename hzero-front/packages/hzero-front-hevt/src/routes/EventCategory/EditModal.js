/**
 * EventCategory - 事件类型定义 - 编辑表单
 * @date: 2019-3-12
 * @author: Wu <qizheng.wu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import Switch from 'components/Switch';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
const otherProps = {
  wrapClassName: 'ant-modal-sidebar-right',
  transitionName: 'move-right',
};
@Form.create({ fieldNameProp: null })
export default class EditModal extends React.PureComponent {
  /**
   * 事件保存
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

  render() {
    const { form, initData, title, loading, onCancel, modalVisible } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
        footer={[
          <Button key="ok" onClick={this.handleOk} color="primary">
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
          <Button key="cancel" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
        ]}
        {...otherProps}
      >
        <Form>
          <FormItem
            {...formLayout}
            label={intl
              .get('hevt.eventCategory.model.eventCategory.categoryCode')
              .d('事件类型编码')}
          >
            {getFieldDecorator('categoryCode', {
              initialValue: initData.categoryCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hevt.eventCategory.model.eventCategory.categoryCode')
                      .d('事件类型编码'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
                {
                  pattern: /[A-Za-z0-9][A-Za-z0-9-_.]*$/,
                  message: intl
                    .get('hevt.common.validator.code')
                    .d('请输入字母及数字，只能以字母或数字开头，可包含“-”、“_”、“.”'),
                },
              ],
            })(<Input trim inputChinese={false} disabled={initData.categoryCode} />)}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl
              .get('hevt.eventCategory.model.eventCategory.categoryName')
              .d('事件类型描述')}
          >
            {getFieldDecorator('categoryName', {
              initialValue: initData.categoryName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hevt.eventCategory.model.eventCategory.categoryName')
                      .d('事件类型描述'),
                  }),
                },
                {
                  max: 360,
                  message: intl.get('hzero.common.validation.max', {
                    max: 360,
                  }),
                },
              ],
            })(<Input trim />)}
          </FormItem>
          <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: initData.enabledFlag === undefined ? 1 : initData.enabledFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
