import React from 'react';
import {
  Button,
  Col,
  DatePicker,
  Divider,
  Form,
  Input,
  InputNumber,
  Modal,
  Row,
  Select,
  Spin,
} from 'hzero-ui';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import Lov from 'components/Lov';
import Switch from 'components/Switch';

import { getDateFormat } from 'utils/utils';
import intl from 'utils/intl';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { CODE_UPPER, EMAIL } from 'utils/regExp';

import CronModal from './CronModal';

const dateFormat = getDateFormat();
const { Option } = Select;
const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 15 },
};
@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      cronVisible: false,
      currentJobCorn: '',
    };
  }

  @Bind()
  handleOk() {
    const { form, executorConfigList = [], onOk = (e) => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { startDate, endDate, retryNumber, ...others } = fieldsValue;
        const params = {
          strategyParam: { retryNumber },
          startDate: startDate ? moment(startDate).format(DEFAULT_DATETIME_FORMAT) : null,
          endDate: endDate ? moment(endDate).format(DEFAULT_DATETIME_FORMAT) : null,
          ...others,
        };
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
        }
        onOk(params);
      }
    });
  }

  @Bind()
  handleCheck() {
    const { onCheck = (e) => e, form } = this.props;
    const executorId = form.getFieldValue('executorId');
    onCheck(executorId);
  }

  @Bind()
  isJson(string) {
    try {
      if (typeof JSON.parse(string) === 'object') {
        return true;
      }
    } catch (e) {
      return false;
    }
  }

  @Bind()
  renderConfigList() {
    const { form, configLoading = false, executorConfigList = [] } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Spin spinning={configLoading}>
        <Divider orientation="left">
          {intl.get('hsdr.jobInfo.model.jobInfo.actuator').d('执行器配置列表')}
        </Divider>
        <Row>
          {executorConfigList.map((item, index) => {
            const { weight, address, configId } = item;
            return (
              <Col span={12} key={configId}>
                <FormItem labelCol={{ span: 9 }} wrapperCol={{ span: 15 }} label={`${address}`}>
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

  /**
   * 监听执行器策略选择
   * @param {string} data - 执行器策略
   */
  @Bind()
  executorStrategyConfig(data) {
    const { form, initData = {}, onConfig = (e) => e } = this.props;
    const { executorId } = initData;
    const strategy = data;
    if (strategy === 'JOB_WEIGHT') {
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
  executorChange(record, data) {
    const { form, onConfig = (e) => e } = this.props;
    const executorStrategy = form.getFieldValue('executorStrategy');
    if (data !== undefined && executorStrategy === 'JOB_WEIGHT') {
      onConfig(data);
    } else {
      onConfig();
    }
  }

  @Bind()
  showCronModal() {
    const { form } = this.props;
    this.setState({
      cronVisible: true,
    });
    const { jobCron } = form.getFieldsValue();
    this.setState({
      currentJobCorn: jobCron,
    });
  }

  @Bind()
  handleCronCancel() {
    this.setState({
      cronVisible: false,
    });
  }

  @Bind()
  handleCronOk(value) {
    const { form } = this.props;
    form.setFieldsValue({ jobCron: value });
    this.setState({
      cronVisible: false,
    });
  }

  render() {
    const {
      form,
      initData = {},
      jobInfo,
      title,
      modalVisible,
      loading,
      onCancel,
      tenantId: currentOrganizationId, // 当前租户id
      tenantRoleLevel,
      checkLoading = false,
      detailLoading = false,
      executorConfigList = [],
    } = this.props;
    const { cronVisible, currentJobCorn } = this.state;
    const { getFieldDecorator } = form;
    const { executorRouteList = [], glueTypeList = [], executorFailList = [] } = jobInfo;
    const {
      executorName,
      executorId,
      executorStrategy,
      strategyParam,
      jobParam,
      jobHandler,
      glueType,
      description,
      jobCron,
      jobCode,
      failStrategy,
      alarmEmail,
      tenantId,
      tenantName,
      jobId,
      cycleFlag = 1,
      serial = 0,
      initFlag = 0,
    } = initData;
    const cronModalProps = {
      visible: cronVisible,
      onCancel: this.handleCronCancel,
      onOk: this.handleCronOk,
      initialValue: currentJobCorn,
    };
    return (
      <Modal
        destroyOnClose
        title={title}
        width="1000px"
        visible={modalVisible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        footer={[
          <Button key="cancel" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button
            disabled={isUndefined(form.getFieldValue('executorId'))}
            key="check"
            onClick={this.handleCheck}
            loading={checkLoading}
          >
            {intl.get('hsdr.jobInfo.view.button.check').d('校验执行器')}
          </Button>,
          <Button type="primary" key="save" onClick={this.handleOk} loading={loading}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <Spin spinning={detailLoading}>
          <Form>
            <Row>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.jobCode').d('任务编码')}
                >
                  {getFieldDecorator('jobCode', {
                    initialValue: jobCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hsdr.jobInfo.model.jobInfo.jobCode').d('任务编码'),
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
                      disabled={jobId !== undefined}
                    />
                  )}
                </FormItem>
                {!tenantRoleLevel && (
                  <Form.Item
                    {...formLayout}
                    label={intl.get('hsdr.jobInfo.model.jobInfo.tenantName').d('所属租户')}
                  >
                    {getFieldDecorator('tenantId', {
                      initialValue: tenantId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hsdr.jobInfo.model.jobInfo.tenantName').d('所属租户'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code="HPFM.TENANT"
                        textValue={tenantName}
                        textField="tenantName"
                        allowClear={false}
                        disabled={jobId !== undefined}
                      />
                    )}
                  </Form.Item>
                )}
                <FormItem
                  {...formLayout}
                  label={intl
                    .get('hsdr.jobInfo.model.jobInfo.executorFailStrategy')
                    .d('失败处理策略')}
                >
                  {getFieldDecorator('failStrategy', {
                    initialValue: failStrategy,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hsdr.jobInfo.model.jobInfo.executorFailStrategy')
                            .d('失败处理策略'),
                        }),
                      },
                    ],
                  })(
                    <Select style={{ width: '100%' }}>
                      {executorFailList.map((item) => (
                        <Option label={item.meaning} value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.groupId').d('执行器')}
                >
                  {getFieldDecorator('executorId', {
                    initialValue: executorId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hsdr.jobInfo.model.jobInfo.groupId').d('执行器'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      allowClear={false}
                      code="HSDR.AVAIL_EXECUTOR"
                      onChange={this.executorChange}
                      textValue={executorName}
                      queryParams={{
                        tenantId: currentOrganizationId,
                      }}
                    />
                  )}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.executorStrategy').d('执行器策略')}
                >
                  {getFieldDecorator('executorStrategy', {
                    initialValue: executorStrategy,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hsdr.jobInfo.model.jobInfo.executorStrategy')
                            .d('执行器策略'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      onChange={this.executorStrategyConfig}
                      disabled={form.getFieldValue('executorId') === undefined}
                    >
                      {executorRouteList.map((item) => (
                        <Option label={item.meaning} value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem {...formLayout} label="JobHandler">
                  {getFieldDecorator('jobHandler', {
                    rules: [
                      {
                        required: form.getFieldValue('glueType') === 'SIMPLE',
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hsdr.jobInfo.model.jobInfo.jobHandler').d('JobHandler'),
                        }),
                      },
                    ],
                    initialValue: jobHandler,
                  })(<Input />)}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.executorParam').d('任务参数')}
                >
                  {getFieldDecorator('jobParam', {
                    initialValue: jobParam,
                    rules: [
                      {
                        validator: (rule, value, callback) => {
                          if (!value || this.isJson(value)) {
                            callback();
                          } else {
                            callback(
                              intl
                                .get('hsdr.jobInfo.view.validate.additionalInformation')
                                .d('请输入正确的json字符串')
                            );
                          }
                        },
                      },
                    ],
                  })(<Input />)}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.serial').d('串行任务')}
                >
                  {getFieldDecorator('serial', {
                    initialValue: serial,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.jobDesc').d('任务描述')}
                >
                  {getFieldDecorator('description', {
                    initialValue: description,
                  })(<Input />)}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.glueType').d('任务类型')}
                >
                  {getFieldDecorator('glueType', {
                    initialValue: glueType,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hsdr.jobInfo.model.jobInfo.glueType').d('任务类型'),
                        }),
                      },
                    ],
                  })(
                    <Select style={{ width: '100%' }}>
                      {glueTypeList.map((item) => (
                        <Option label={item.meaning} value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                {form.getFieldValue('failStrategy') === 'RETRY' && (
                  <FormItem
                    label={intl.get('hsdr.jobInfo.model.jobInfo.retryNumber').d('重试次数')}
                    {...formLayout}
                  >
                    {getFieldDecorator('retryNumber', {
                      initialValue: strategyParam && JSON.parse(strategyParam).retryNumber,
                    })(<InputNumber min={0} step={1} style={{ width: '100%' }} />)}
                  </FormItem>
                )}
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.jobCron').d('Cron')}
                >
                  {getFieldDecorator('jobCron', {
                    initialValue: jobCron,
                  })(<Input inputChinese={false} onClick={this.showCronModal} />)}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.alarmEmail').d('报警邮件')}
                >
                  {getFieldDecorator('alarmEmail', {
                    initialValue: alarmEmail,
                    rules: [
                      {
                        pattern: EMAIL,
                        message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label={intl.get('hsdr.jobInfo.model.jobInfo.startDate').d('有效时间从')}
                  {...formLayout}
                >
                  {getFieldDecorator('startDate', {
                    initialValue:
                      initData.startDate && moment(initData.startDate, `${dateFormat} HH:mm:ss`),
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      showTime
                      format={`${dateFormat} HH:mm:ss`}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        form.getFieldValue('endDate') &&
                        moment(form.getFieldValue('endDate')).isBefore(currentDate, 'day')
                      }
                    />
                  )}
                </FormItem>
                <FormItem
                  label={intl.get('hsdr.jobInfo.model.jobInfo.endDate').d('有效时间至')}
                  {...formLayout}
                >
                  {getFieldDecorator('endDate', {
                    initialValue:
                      initData.endDate && moment(initData.endDate, `${dateFormat} HH:mm:ss`),
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      showTime
                      format={`${dateFormat} HH:mm:ss`}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        form.getFieldValue('startDate') &&
                        moment(form.getFieldValue('startDate')).isAfter(currentDate, 'day')
                      }
                    />
                  )}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.cycleFlag').d('周期性')}
                >
                  {getFieldDecorator('cycleFlag', {
                    initialValue: cycleFlag,
                  })(<Switch />)}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hsdr.jobInfo.model.jobInfo.initFlag').d('自动初始化')}
                >
                  {getFieldDecorator('initFlag', {
                    initialValue: initFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
            </Row>
            {executorConfigList.length > 0 ? this.renderConfigList() : null}
          </Form>
        </Spin>
        <CronModal {...cronModalProps} />
      </Modal>
    );
  }
}
