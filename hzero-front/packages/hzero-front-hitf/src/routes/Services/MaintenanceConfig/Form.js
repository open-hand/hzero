/*
 * Form - 服务注册编辑弹窗
 * @date: 2018-10-25
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Card, Col, Form, InputNumber, Row, Select, Input, Icon } from 'hzero-ui';
import { Button as ButtonPermission } from 'components/Permission';
import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME, FORM_COL_2_LAYOUT } from 'utils/constants';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { Tooltip } from 'choerodon-ui/pro';

import styles from './index.less';

const organizationId = getCurrentOrganizationId();

const FormItem = Form.Item;

const formItemLayout = {
  labelCol: {
    span: 9,
  },
  wrapperCol: {
    span: 14,
  },
};

@Form.create({ fieldNameProp: null })
export default class EditorForm extends React.Component {
  constructor(props) {
    super(props);
    /**
     * state初始化
     */
    this.state = {
      enableService: this.props.enableService || [],
    };
  }

  componentDidMount() {}

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    this.state = {
      enableService: nextProps.enableService || [],
    };
  }

  /**
   * 添加接收者参数
   */
  @Bind()
  addItem() {
    const { addRecipient = () => {} } = this.props;
    addRecipient();
  }

  /**
   * 移除接收者键，值
   * @param {*}
   */
  @Bind()
  removeItem(k) {
    const { removeRecipient = () => {} } = this.props;
    removeRecipient(k);
  }

  /**
   * @function renderRecipient - 渲染接收人表单
   */
  @Bind()
  renderRecipient() {
    const { enableService } = this.state;
    const {
      form: { getFieldDecorator = () => {}, getFieldValue = (e) => e },
      recipientKey,
    } = this.props;
    return recipientKey.map(
      (data, k) =>
        data._status !== 'delete' && (
          <React.Fragment key={`Fragment${data.targetId || k}`}>
            <Row>
              <Col span={10}>
                <FormItem
                  required={false}
                  label={intl.get('hmsg.sendConfig.model.sendConfig.recipientType').d('类型')}
                  {...formItemLayout}
                >
                  {getFieldDecorator(`recipientKey${k}`, {
                    initialValue: data.typeCode,
                    rules: [
                      {
                        required: true,
                        whitespace: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hmsg.sendConfig.model.sendConfig.recipientType')
                            .d('类型'),
                        }),
                      },
                    ],
                  })(
                    <Select allowClear>
                      {enableService &&
                        enableService.map((item) => (
                          <Select.Option value={item.typeCode} key={item.typeCode}>
                            {item.typeMeaning}
                          </Select.Option>
                        ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={10} offset={1}>
                <FormItem
                  required={false}
                  label={intl.get('hmsg.sendConfig.model.sendConfig.recipient').d('接收人')}
                  {...formItemLayout}
                >
                  {getFieldDecorator(`recipientValue${k}`, {
                    initialValue: data.userId,
                    rules: [
                      {
                        type: 'string',
                        required:
                          getFieldValue(`recipientKey${k}`) !== 'WEB'
                            ? true
                            : isEmpty(getFieldValue(`recipientValue${k}`)),
                        // whitespace: getFieldValue(`recipientKey${k}`) !== 'WEB' , // 当是站内消息时, 由于 Lov 时 id(不是数字) 会导致校验出错
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hmsg.sendConfig.model.sendConfig.recipient').d('接收人'),
                        }),
                      },
                    ],
                  })(
                    getFieldValue(`recipientKey${k}`) === 'WEB' ||
                      getFieldValue(`recipientKey${k}`) === 'EMAIL' ||
                      getFieldValue(`recipientKey${k}`) === 'SMS' ? (
                      <Lov
                        code={isTenantRoleLevel() ? 'HIAM.USER.ORG' : 'HIAM.SITE.USER'}
                        textValue={data.userName}
                        lovOptions={{
                          displayField: 'realName',
                          valueField: 'id',
                        }}
                      />
                    ) : (
                      <Input />
                    )
                  )}
                </FormItem>
              </Col>
              {(getFieldValue(`recipientKey${k}`) === 'WC_E' ||
                getFieldValue(`recipientKey${k}`) === 'DT') && (
                <Col span={10}>
                  <FormItem
                    required={false}
                    label={intl.get('hmsg.sendConfig.model.sendConfig.agentId').d('应用ID')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator(`agentId${k}`, {
                      initialValue: data.agentId,
                      rules: [
                        {
                          required: true,
                          whitespace: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hmsg.sendConfig.model.sendConfig.agentId').d('应用ID'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </FormItem>
                </Col>
              )}
              <Col span={1}>
                {recipientKey.length > 1 ? (
                  <Icon
                    className={styles.iconButton}
                    type="minus-circle-o"
                    onClick={() => this.removeItem(k)}
                  />
                ) : null}
              </Col>
            </Row>
          </React.Fragment>
        )
    );
  }

  render() {
    const {
      form: { getFieldDecorator = () => {} },
      dataSource = {},
      interfaceId,
      activeinvokeStatisticsFlag,
      activeHealthCheckFlag,
      onHealthCheckFlagChange = () => {},
      handleCheckWarningMsgTplCodeChange = () => {},
      tenantId,
      logTypes,
    } = this.props;

    const {
      invokeDetailsFlag = '',
      healthCheckFlag = 0,
      checkUsecaseId,
      checkRoundRobin,
      checkPeriod,
      // checkWarningSmsFlag = 0,
      checkThreshold,
      // checkWarningEmailFlag = 0,
      // checkWarningUserId,
      checkWarningMsgTplCode,
      // checkWarningUserName,
      checkUsecaseName,
      alertCode,
    } = dataSource;
    const activeSwitchLayout =
      activeinvokeStatisticsFlag !== 1 && activeHealthCheckFlag !== 1
        ? {
            labelCol: {
              span: 14,
            },
            wrapperCol: {
              span: 10,
            },
          }
        : {};
    return (
      <Form>
        <Card
          key="invokeDimension"
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hitf.services.view.title.invokeDimension').d('记录维度')}</h3>}
        >
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.services.model.services.invokeDetailsFlag').d('记录调用详情')}
                {...formItemLayout}
                {...activeSwitchLayout}
              >
                {getFieldDecorator('invokeDetailsFlag', {
                  initialValue: `${invokeDetailsFlag}`,
                })(
                  <Select style={{ width: '200px' }}>
                    {logTypes.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
        </Card>
        <Card
          key="healthExamination"
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hitf.services.view.title.healthExamination').d('健康检查')}</h3>}
        >
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl
                  .get('hitf.services.model.services.healthCheckFlag')
                  .d('是否开启健康检查')}
                {...formItemLayout}
                {...activeSwitchLayout}
              >
                {getFieldDecorator('healthCheckFlag', {
                  initialValue: healthCheckFlag,
                  valuePropName: 'checked',
                })(<Switch onChange={onHealthCheckFlagChange} />)}
              </FormItem>
            </Col>
          </Row>
          {activeHealthCheckFlag === 1 && (
            <>
              <Row>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hitf.services.model.services.checkUsecaseId')
                      .d('所用测试用例')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkUsecaseId', {
                      initialValue: checkUsecaseId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hitf.services.model.services.checkUsecaseId')
                              .d('所用测试用例'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code={!isTenantRoleLevel() ? 'HITF.SITE.USE_CASE' : 'HITF.USE_CASE'}
                        textValue={checkUsecaseName}
                        queryParams={{ organizationId: tenantId, interfaceId }}
                        // onChange={value => this.handleLovChange(value, 'companyId')}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hitf.services.model.services.checkCycle').d('检查周期（秒）')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkRoundRobin', {
                      initialValue: checkRoundRobin,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hitf.services.model.services.checkCycle')
                              .d('检查周期（秒）'),
                          }),
                        },
                      ],
                    })(<InputNumber min={0} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hitf.services.model.services.checkPeriod').d('统计周期（秒）')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkPeriod', {
                      initialValue: checkPeriod,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hitf.services.model.services.checkPeriod')
                              .d('统计周期（秒）'),
                          }),
                        },
                      ],
                    })(<InputNumber min={0} />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hitf.services.model.services.checkThreshold').d('异常阈值')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkThreshold', {
                      initialValue: checkThreshold,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hitf.services.model.services.checkThreshold')
                              .d('异常阈值'),
                          }),
                        },
                      ],
                    })(<InputNumber min={0} />)}
                  </FormItem>
                </Col>
              </Row>
              {/* <Row>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hitf.services.model.services.checkWarningSmsFlag')
                      .d('短信预警')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkWarningSmsFlag', {
                      initialValue: checkWarningSmsFlag,
                      valuePropName: 'checked',
                    })(<Switch />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hitf.services.model.services.checkWarningEmailFlag')
                      .d('邮件预警')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkWarningEmailFlag', {
                      initialValue: checkWarningEmailFlag,
                      valuePropName: 'checked',
                    })(<Switch />)}
                  </FormItem>
                </Col>
              </Row> */}
              <Row>
                {/* <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hitf.services.model.services.checkWarningUserId')
                      .d('预警目标用户')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkWarningUserId', {
                      initialValue: checkWarningUserId,
                      rules: [
                        {
                          required:
                            getFieldValue('checkWarningSmsFlag') ||
                            getFieldValue('checkWarningEmailFlag'),
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hitf.services.model.services.checkWarningUserId`)
                              .d('预警目标用户'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code="HIAM.TENANT.USER"
                        queryParams={{ organizationId }}
                        textValue={checkWarningUserName}
                      />
                    )}
                  </FormItem>
                </Col> */}
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hitf.services.model.services.checkWarningMsgTplCode')
                      .d('预警消息模板代码')}
                    {...formItemLayout}
                  >
                    {getFieldDecorator('checkWarningMsgTplCode', {
                      initialValue: checkWarningMsgTplCode,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hitf.services.model.services.checkWarningMsgTplCode')
                              .d('预警消息模板代码'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code="HMSG.TEMPLATE_SERVER"
                        queryParams={{ tenantId: organizationId }}
                        onChange={handleCheckWarningMsgTplCodeChange}
                        textValue={checkWarningMsgTplCode}
                        lovOptions={{
                          displayField: 'messageCode',
                          valueField: 'messageCode',
                        }}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <FormItem>
                <Tooltip
                  title={intl
                    .get('hitf.services.view.message.tip.addRecipient')
                    .d('添加的接收人可以收到接口异常信息的通知')}
                  placement="top"
                >
                  <ButtonPermission
                    permissionList={[
                      {
                        code: 'button.addRecipient',
                        type: 'button',
                        meaning: '消息发送配置-添加接收人',
                      },
                    ]}
                    icon="add"
                    type="primary"
                    onClick={this.addItem}
                  >
                    {intl.get('hmsg.sendConfig.view.button.addRecipient').d('添加接收人')}
                  </ButtonPermission>
                </Tooltip>
              </FormItem>
              {this.renderRecipient()}
            </>
          )}
        </Card>
        <Card
          key="alertInfo"
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hitf.services.view.title.alertInfo').d('告警配置')}</h3>}
        >
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={
                  <span>{intl.get('hitf.services.model.services.alertCode').d('告警代码')}</span>
                }
                {...formItemLayout}
              >
                {getFieldDecorator('alertCode', { initialValue: alertCode })(
                  <Lov
                    code="HITF.ALERT_CODE"
                    textValue={alertCode}
                    lovOptions={{
                      displayField: 'alertCode',
                      valueField: 'alertCode',
                    }}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        </Card>
      </Form>
    );
  }
}
