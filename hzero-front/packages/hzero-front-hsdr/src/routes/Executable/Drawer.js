import React, { PureComponent } from 'react';
import { Col, Divider, Form, Input, InputNumber, Modal, Row, Select, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

const { Option } = Select;
const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  @Bind()
  onOk() {
    const { onOk, form, executorConfigList } = this.props;
    form.validateFields((error, fieldsValue) => {
      if (!error) {
        const params = fieldsValue;
        if (executorConfigList.length > 0) {
          const strategyParam = { jobWeight: {} };
          executorConfigList.forEach((item, index) => {
            Object.assign(strategyParam, {
              jobWeight: {
                ...strategyParam.jobWeight,
                [`${item.address}`]: fieldsValue[`${item.configId}/${index}`],
              },
            });
            delete params[`${item.configId}/${index}`];
          });
          if (fieldsValue.failStrategy === 'RETRY' && fieldsValue.retryNumber !== undefined) {
            Object.assign(strategyParam, { retryNumber: fieldsValue.retryNumber });
          }
          Object.assign(params, { strategyParam });
        } else {
          const strategyParam = {};
          if (fieldsValue.failStrategy === 'RETRY' && fieldsValue.retryNumber !== undefined) {
            Object.assign(strategyParam, { retryNumber: fieldsValue.retryNumber });
          }
          Object.assign(params, { strategyParam });
        }
        onOk(params);
      }
    });
  }

  /**
   * 监听执行器策略选择
   * @param {string} data - 执行器策略
   */
  @Bind()
  executorStrategyConfig(data) {
    const { form, initData = {}, onConfig = (e) => e } = this.props;
    const { executorId } = initData;
    const strategy = data;
    if (form.getFieldValue('executorId') !== undefined && strategy === 'JOB_WEIGHT') {
      if (
        initData.executorStrategy === 'JOB_WEIGHT' &&
        executorId === form.getFieldValue('executorId') &&
        strategy === 'JOB_WEIGHT'
      ) {
        onConfig(initData);
      } else {
        onConfig({ executorId: form.getFieldValue('executorId') });
      }
    } else {
      onConfig();
    }
  }

  @Bind()
  executorChange(_, data) {
    const { form, onConfig = (e) => e } = this.props;
    const executorStrategy = form.getFieldValue('executorStrategy');
    if (data !== undefined && executorStrategy === 'JOB_WEIGHT') {
      onConfig(data);
    } else {
      onConfig();
    }
  }

  @Bind()
  renderConfigList() {
    const { form, configLoading = false, executorConfigList = [] } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Spin spinning={configLoading}>
        <Divider orientation="left">
          {intl.get('hsdr.executable.model.executable.actuator').d('执行器配置列表')}
        </Divider>
        <Row>
          {executorConfigList.map((item, index) => {
            const { weight, address, configId } = item;
            return (
              <Col span={24} key={configId}>
                <FormItem labelCol={{ span: 6 }} wrapperCol={{ span: 16 }} label={`${address}`}>
                  {getFieldDecorator(`${configId}/${index}`, {
                    initialValue: weight,
                  })(<InputNumber />)}
                </FormItem>
              </Col>
            );
          })}
        </Row>
      </Spin>
    );
  }

  render() {
    const {
      tenantId,
      form,
      initData = {},
      executable,
      title,
      visible,
      onCancel,
      loading,
      detailLoading = false,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      exeTypeList = [],
      executorStrategyList = [],
      failStrategyList = [],
      executorConfigList = [],
    } = executable;
    const {
      executorName,
      executableId,
      executorId,
      executableCode,
      exeTypeCode,
      jobHandler,
      executableName,
      executableDesc,
      failStrategy,
      strategyParam,
      executorStrategy,
      enabledFlag = 1,
      _token,
    } = initData;
    return (
      <Modal
        width={620}
        title={title}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onOk={this.onOk}
        onCancel={onCancel}
        confirmLoading={loading}
        destroyOnClose
      >
        <Spin spinning={detailLoading}>
          <Form>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.executableCode').d('可执行编码')}
            >
              {getFieldDecorator('executableCode', {
                initialValue: executableCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hsdr.executable.model.executable.executableCode')
                        .d('可执行编码'),
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
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={!isUndefined(executableId)}
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.executableName').d('可执行名称')}
            >
              {getFieldDecorator('executableName', {
                initialValue: executableName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hsdr.executable.model.executable.executableName')
                        .d('可执行名称'),
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl
                    .get('hsdr.executable.model.executable.executableName')
                    .d('可执行名称')}
                  field="executableName"
                  token={_token}
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.exeType').d('可执行类型')}
            >
              {getFieldDecorator('exeTypeCode', {
                initialValue: exeTypeCode,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hsdr.executable.model.executable.exeType').d('可执行类型'),
                    }),
                  },
                ],
              })(
                <Select style={{ width: '100%' }}>
                  {exeTypeList.map((item) => (
                    <Option label={item.meaning} value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.exeHandler').d('JobHandler')}
            >
              {getFieldDecorator('jobHandler', {
                initialValue: jobHandler,
                rules: [
                  {
                    required: form.getFieldValue('exeTypeCode') === 'SIMPLE',
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hsdr.executable.model.executable.jobHandler').d('JobHandler'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.executableDesc').d('可执行描述')}
            >
              {getFieldDecorator('executableDesc', {
                initialValue: executableDesc,
              })(<Input />)}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.groupId').d('执行器')}
            >
              {getFieldDecorator('executorId', {
                initialValue: executorId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hsdr.executable.model.executable.groupId').d('执行器'),
                    }),
                  },
                ],
              })(
                <Lov
                  allowClear={false}
                  code="HSDR.AVAIL_EXECUTOR"
                  onChange={this.executorChange}
                  textValue={executorName}
                  queryParams={{ tenantId }}
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.executorStrategy').d('执行器策略')}
            >
              {getFieldDecorator('executorStrategy', {
                initialValue: executorStrategy,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hsdr.executable.model.executable.executorStrategy')
                        .d('执行器策略'),
                    }),
                  },
                ],
              })(
                <Select style={{ width: '100%' }} onChange={this.executorStrategyConfig}>
                  {executorStrategyList.map((item) => (
                    <Option label={item.meaning} value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hsdr.executable.model.executable.failStrategy').d('失败处理策略')}
            >
              {getFieldDecorator('failStrategy', {
                initialValue: failStrategy,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hsdr.executable.model.executable.failStrategy')
                        .d('失败处理策略'),
                    }),
                  },
                ],
              })(
                <Select style={{ width: '100%' }}>
                  {failStrategyList.map((item) => (
                    <Option label={item.meaning} value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            {form.getFieldValue('failStrategy') === 'RETRY' && (
              <FormItem
                label={intl.get('hsdr.executable.model.executable.retryNumber').d('重试次数')}
                {...formLayout}
              >
                {getFieldDecorator('retryNumber', {
                  initialValue: strategyParam && JSON.parse(strategyParam).retryNumber,
                })(<InputNumber min={0} step={1} style={{ width: '100%' }} />)}
              </FormItem>
            )}
            <FormItem label={intl.get('hzero.common.status').d('状态')} {...formLayout}>
              {getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag,
              })(<Switch />)}
            </FormItem>
            {executorConfigList.length > 0 ? this.renderConfigList() : null}
          </Form>
        </Spin>
      </Modal>
    );
  }
}
