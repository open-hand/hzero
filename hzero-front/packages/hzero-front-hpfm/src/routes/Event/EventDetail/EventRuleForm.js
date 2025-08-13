/**
 * Event - 事件规则维护界面
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, InputNumber, Select } from 'hzero-ui';

import Switch from 'components/Switch';
import SideBar from 'components/Modal/SideBar';

import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import { Bind } from 'lodash-decorators';

const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class EventRuleForm extends React.Component {
  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) onRef(this);
  }

  resetForm() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  onOk() {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      handleAdd(fieldsValue);
    });
  }

  render() {
    const { modalVisible, hideModal, confirmLoading = false, title, ...otherProps } = this.props;

    const { form, eventRule = {}, apiList = [], typeList = [] } = this.props;
    const {
      matchingRule,
      beanName,
      methodName,
      apiUrl,
      ruleDescription,
      apiMethod = 'GET',
      enabledFlag = 1,
      resultFlag = 0,
      syncFlag = 0,
      orderSeq = 1,
      callType,
      serverCode,
      messageCode,
    } = eventRule;
    return (
      <SideBar
        title={title}
        visible={modalVisible}
        onCancel={hideModal}
        onOk={this.onOk}
        confirmLoading={confirmLoading}
        {...otherProps}
      >
        <Form>
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.event.model.eventRule.rule').d('匹配规则')}
          >
            {form.getFieldDecorator('matchingRule', {
              initialValue: matchingRule,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.event.model.eventRule.rule').d('匹配规则'),
                  }),
                },
                {
                  max: 500,
                  message: intl.get('hzero.common.validation.max', { max: 500 }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.event.model.eventRule.callType').d('调用类型')}
          >
            {form.getFieldDecorator('callType', {
              initialValue: callType || 'M',
            })(
              <Select style={{ width: '100%' }}>
                {typeList.map((item) => (
                  <Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
          {/* eslint-disable-next-line no-nested-ternary */}
          {form.getFieldValue('callType') === 'M' ? (
            <React.Fragment>
              <Form.Item labelCol={{ span: 6 }} wrapperCol={{ span: 16 }} label="BeanName">
                {form.getFieldDecorator('beanName', {
                  initialValue: beanName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', { name: 'BeanName' }),
                    },
                    {
                      pattern: CODE,
                      message: intl
                        .get('hzero.common.validation.code')
                        .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                    {
                      max: 240,
                      message: intl.get('hzero.common.validation.max', { max: 240 }),
                    },
                  ],
                })(<Input trim inputChinese={false} />)}
              </Form.Item>
              <Form.Item labelCol={{ span: 6 }} wrapperCol={{ span: 16 }} label="MethodName">
                {form.getFieldDecorator('methodName', {
                  initialValue: methodName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', { name: 'MethodName' }),
                    },
                    {
                      pattern: CODE,
                      message: intl
                        .get('hzero.common.validation.code')
                        .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                    {
                      max: 240,
                      message: intl.get('hzero.common.validation.max', { max: 240 }),
                    },
                  ],
                })(<Input trim inputChinese={false} />)}
              </Form.Item>
            </React.Fragment>
          ) : form.getFieldValue('callType') === 'A' ? (
            <React.Fragment>
              <Form.Item labelCol={{ span: 6 }} wrapperCol={{ span: 16 }} label="API URL">
                {form.getFieldDecorator('apiUrl', {
                  initialValue: apiUrl,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', { name: 'API URL' }),
                    },
                    {
                      max: 480,
                      message: intl.get('hzero.common.validation.max', { max: 480 }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
              <Form.Item labelCol={{ span: 6 }} wrapperCol={{ span: 16 }} label="API Method">
                {form.getFieldDecorator('apiMethod', {
                  initialValue: apiMethod,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: 'API Method',
                      }),
                    },
                    {
                      max: 240,
                      message: intl.get('hzero.common.validation.max', { max: 240 }),
                    },
                  ],
                })(
                  <Select style={{ width: '100%' }}>
                    {apiList.map((item) => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </React.Fragment>
          ) : (
            <React.Fragment>
              <Form.Item
                labelCol={{ span: 6 }}
                wrapperCol={{ span: 16 }}
                label={intl.get('hpfm.event.model.eventRule.serverCode').d('WebHook接收方')}
              >
                {form.getFieldDecorator('serverCode', {
                  initialValue: serverCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.event.model.eventRule.serverCode').d('WebHook接收方'),
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
              <Form.Item
                labelCol={{ span: 6 }}
                wrapperCol={{ span: 16 }}
                label={intl.get('hpfm.event.model.eventRule.messageCode').d('消息编码')}
              >
                {form.getFieldDecorator('messageCode', {
                  initialValue: messageCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.event.model.eventRule.messageCode').d('消息编码'),
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </React.Fragment>
          )}
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.event.model.eventRule.orderSeq').d('顺序')}
          >
            {form.getFieldDecorator('orderSeq', {
              initialValue: orderSeq,
            })(<InputNumber min={1} max={100} />)}
          </Form.Item>
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.event.model.eventRule.ruleDescription').d('规则描述')}
          >
            {form.getFieldDecorator('ruleDescription', {
              initialValue: ruleDescription,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.event.model.eventRule.ruleDescription').d('规则描述'),
                  }),
                },
                {
                  max: 255,
                  message: intl.get('hzero.common.validation.max', { max: 255 }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.event.model.eventRule.syncFlag').d('是否同步')}
          >
            {form.getFieldDecorator('syncFlag', {
              initialValue: syncFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hpfm.event.model.eventRule.resultFlag').d('返回结果')}
          >
            {form.getFieldDecorator('resultFlag', {
              initialValue: resultFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
            label={intl.get('hzero.common.status.enable').d('启用')}
          >
            {form.getFieldDecorator('enabledFlag', {
              initialValue: enabledFlag,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </SideBar>
    );
  }
}
