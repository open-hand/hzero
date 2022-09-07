/**
 * 流程定义 - 复制
 * @date: 2019-5-29
 * @author: jiacheng.wang <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Modal, Select, Spin, Input, InputNumber, Switch } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

import styles from './style/index.less';

@Form.create({ fieldNameProp: null })
export default class OverTimeSetting extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk = (e) => e, currentRecord = {} } = this.props;
    form.validateFields((error, { name, ...values }) => {
      if (!error) {
        onOk({ modelId: currentRecord.id, ...values });
      }
    });
  }

  render() {
    const {
      form,
      onCancel,
      visible,
      dataLoading = false,
      loading = false,
      currentRecord = {},
    } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        title={intl.get('hwfp.processDefine.view.title.overTimeSetting').d('流程超时设置')}
        visible={visible}
        width={500}
        confirmLoading={loading}
        onOk={this.handleOk}
        onCancel={onCancel}
      >
        <Spin spinning={dataLoading}>
          <Form>
            <Form.Item
              label={intl.get('hwfp.common.model.process.name').d('流程名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('name', {
                initialValue: currentRecord.name,
              })(<Input disabled />)}
            </Form.Item>
            <Form.Item
              className={styles['inline-form-item']}
              label={intl.get('hwfp.processDefine.model.processDefine.timeout').d('超时时间')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              <span>
                {getFieldDecorator('overtime', {
                  initialValue: currentRecord.overtime,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hwfp.processDefine.model.processDefine.timeout')
                          .d('超时时间'),
                      }),
                    },
                  ],
                })(<InputNumber min={0} />)}
              </span>
              <span>
                {getFieldDecorator('overtimeUnit', {
                  initialValue: currentRecord.overtimeUnit || 'hour',
                })(
                  <Select style={{ width: '30%' }}>
                    <Select.Option value="hour">
                      {intl.get('hzero.common.date.unit.hours').d('小时')}
                    </Select.Option>
                    <Select.Option value="day">
                      {intl.get('hzero.common.date.unit.day').d('小时')}
                    </Select.Option>
                  </Select>
                )}
              </span>
            </Form.Item>
            <Form.Item
              label={intl
                .get('hwfp.processDefine.model.processDefine.overtimeEnabled')
                .d('超时设置启用')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('overtimeEnabled', {
                initialValue: currentRecord.overtimeEnabled || 0,
              })(<Switch checkedValue={1} unCheckedValue={0} />)}
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
