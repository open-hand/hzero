/*
 * ListForm - 限流设置列表表单
 * @date: 2018/08/07 14:42:49
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isFunction } from 'lodash';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};
/**
 * 限流设置列表表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onHandleSelect // lov设置名称
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class ListForm extends PureComponent {
  constructor(props) {
    super(props);
    if (isFunction(props.onRef)) props.onRef(this);
    this.state = {};
  }

  // 保存
  @Bind()
  saveBtn() {
    const { form, onHandleAdd } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onHandleAdd(values);
      }
    });
  }

  render() {
    const { form, title, anchor, visible, onCancel, confirmLoading, limitTypes = [] } = this.props;
    return (
      <Modal
        destroyOnClose
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        confirmLoading={confirmLoading}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item
            {...formLayout}
            label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitKey`).d('代码')}
          >
            {form.getFieldDecorator('rateLimitKey', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitKey`).d('代码'),
                  }),
                },
                {
                  max: 80,
                  message: intl.get('hzero.common.validation.max', {
                    max: 80,
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
            })(<Input trim typeCase="upper" inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitType`).d('限流方式')}
          >
            {form.getFieldDecorator('rateLimitType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitType`)
                      .d('限流方式'),
                  }),
                },
              ],
            })(
              <Select>
                {limitTypes.map((item) => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hzero.common.explain').d('说明')}>
            {form.getFieldDecorator('remark', {})(<Input />)}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {form.getFieldDecorator('enabledFlag', {
              initialValue: 1,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
