import React from 'react';
import { Form, Icon, Input, InputNumber, Modal, Select, Tooltip } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import intl from 'utils/intl';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';
import { CODE_UPPER } from 'utils/regExp';

const FormItem = Form.Item;
const { TextArea } = Input;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
@Form.create({ fieldNameProp: null })
export default class JobGroupForm extends React.PureComponent {
  @Bind()
  onOk() {
    const { onOk, form } = this.props;
    form.validateFields((error, fieldsValue) => {
      if (!error) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const { form, initData, modalVisible, modalTitle, saving, creating, onCancel } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const {
      executorCode,
      orderSeq,
      addressList,
      executorId,
      executorName,
      executorType,
      serverName,
      _token,
    } = initData;
    return (
      <Modal
        destroyOnClose
        title={modalTitle}
        visible={modalVisible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={executorId ? saving : creating}
        onCancel={onCancel}
        onOk={this.onOk}
      >
        <FormItem {...formLayout} label={intl.get('hsdr.jobGroup.view.message.orderSeq').d('排序')}>
          {getFieldDecorator('orderSeq', {
            initialValue: orderSeq,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hsdr.jobGroup.view.message.orderSeq').d('排序'),
                }),
              },
              {
                pattern: /^(-(100|[1-9][0-9]?)|([1-9][0-9]{0,1}|100)|0)$/,
                message: intl.get('hsdr.jobGroup.view.message.seqRule').d('取值范围为-100~100'),
              },
            ],
          })(<InputNumber min={-100} max={100} step={1} style={{ width: '100%' }} />)}
        </FormItem>
        <FormItem
          {...formLayout}
          label={intl.get('hsdr.jobGroup.view.message.executorCode').d('执行器编码')}
        >
          {getFieldDecorator('executorCode', {
            initialValue: executorCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hsdr.jobGroup.view.message.executorCode').d('执行器编码'),
                }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
            ],
          })(
            <Input trim typeCase="upper" inputChinese={false} disabled={!isUndefined(executorId)} />
          )}
        </FormItem>
        <FormItem
          {...formLayout}
          label={intl.get('hsdr.jobGroup.view.message.executorName').d('执行器名称')}
        >
          {getFieldDecorator('executorName', {
            initialValue: executorName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hsdr.jobGroup.view.message.executorName').d('执行器名称'),
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('hsdr.jobGroup.view.message.executorName').d('执行器名称')}
              field="executorName"
              token={_token}
            />
          )}
        </FormItem>
        <FormItem
          {...formLayout}
          label={intl.get('hsdr.jobGroup.view.message.addressType').d('注册方式')}
        >
          {getFieldDecorator('executorType', {
            initialValue: executorType === 1 ? 1 : 0,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hsdr.jobGroup.view.message.addressType').d('注册方式'),
                }),
              },
            ],
          })(
            <Select style={{ width: '100%' }} disabled={!isUndefined(executorId)}>
              <Select.Option value={0}>
                {intl.get('hsdr.jobGroup.view.message.auto').d('自动注册')}
              </Select.Option>
              <Select.Option value={1}>
                {intl.get('hsdr.jobGroup.view.message.byHand').d('手动录入')}
              </Select.Option>
            </Select>
          )}
        </FormItem>
        {getFieldValue('executorType') === 1 ? (
          <FormItem
            // label={intl.get('hsdr.jobGroup.view.message.addressList').d('机器地址')}
            label={
              <span>
                {intl.get('hsdr.jobGroup.view.message.addressList').d('机器地址')}&nbsp;
                <Tooltip
                  title={intl
                    .get('hsdr.jobGroup.view.message.addressList.help.msg')
                    .d('多个机器地址用逗号隔开')}
                >
                  <Icon type="question-circle-o" />
                </Tooltip>
              </span>
            }
            {...formLayout}
          >
            {getFieldDecorator('addressList', {
              initialValue: addressList,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hsdr.jobGroup.view.message.addressList').d('机器地址'),
                  }),
                },
              ],
            })(<TextArea rows={3} disabled={executorType === 0} />)}
          </FormItem>
        ) : (
          <FormItem
            {...formLayout}
            label={intl.get('hsdr.jobGroup.view.message.serverName').d('服务')}
          >
            {getFieldDecorator('serverName', {
              initialValue: serverName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hsdr.jobGroup.view.message.serverName').d('服务'),
                  }),
                },
              ],
            })(<Lov allowClear={false} code="HADM.ROUTE.SERVICE_CODE" textValue={serverName} />)}
          </FormItem>
        )}
      </Modal>
    );
  }
}
