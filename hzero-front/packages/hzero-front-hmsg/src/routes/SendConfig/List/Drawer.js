import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Icon, Input, Modal, Row, Select, Spin } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';
import { isTenantRoleLevel } from 'utils/utils';

import Lov from 'components/Lov';

import intl from 'utils/intl';

import styles from './index.less';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const FormItem = Form.Item;
const { Option } = Select;
const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 16 },
};
const formItemLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 16 },
};
const formParamsLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 12 },
};
/**
 * 发送配置-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  /**
   * state初始化
   */
  state = {
    recipientKey: [0], // 接收人
    recipientUuid: 1,
  };
  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { visible } = nextProps;
    if (!visible) {
      this.setState({
        recipientKey: [0],
        recipientUuid: 1,
      });
    }
  }

  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onOk: (e) => e,
    onCancel: (e) => e,
  };

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, onSendOk, paramsName } = this.props;
    const { recipientKey } = this.state;
    if (onOk) {
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          let messageSender = {};
          let weChatSender = {};
          let dingTalkSender = {};
          let receiverAddressList = [];
          const newReceiverAddressList = [];
          const messageTypeCodeList = [];
          const weChatTypeCodeList = [];
          const dingTalkTypeCodeList = [];
          const userList = []; // 公众号接收人
          const userIdList = []; // 企业微信接收人
          const dtUserIdList = []; // 钉钉接收人
          recipientKey.forEach((item) => {
            if (
              values[`recipientKey${item}`] !== 'WC_O' &&
              values[`recipientKey${item}`] !== 'WC_E' &&
              values[`recipientKey${item}`] !== 'DT'
            ) {
              newReceiverAddressList.push({
                [values[`recipientKey${item}`]]: values[`recipientValue${item}`],
              });
            }
          });
          receiverAddressList = newReceiverAddressList.map((item) => ({
            email: item.EMAIL ? item.EMAIL : undefined,
            phone:
              item.SMS || item.CALL || item.WEB_HOOK
                ? item.SMS || item.CALL || item.WEB_HOOK
                : undefined,
            userId: item.WEB ? item.WEB : undefined,
            // phone: item.CALL ? item.CALL : undefined,
            targetUserTenantId: item.WEB ? 0 : undefined,
          }));
          recipientKey.forEach((item) => {
            if (
              values[`recipientKey${item}`] === 'WC_O' ||
              values[`recipientKey${item}`] === 'WC_E'
            ) {
              weChatTypeCodeList.push(values[`recipientKey${item}`]);
            } else if (values[`recipientKey${item}`] === 'DT') {
              dingTalkTypeCodeList.push(values[`recipientKey${item}`]);
            } else {
              messageTypeCodeList.push(values[`recipientKey${item}`]);
            }
          });
          recipientKey.forEach((r) => {
            if (values[`recipientKey${r}`] === 'WC_O') {
              userList.push(values[`recipientValue${r}`]);
            }
            if (values[`recipientKey${r}`] === 'WC_E') {
              userIdList.push(values[`recipientValue${r}`]);
            }
            if (values[`recipientKey${r}`] === 'DT') {
              dtUserIdList.push(values[`recipientValue${r}`]);
            }
          });
          const args = {};
          const recipientParams = {};
          const { lang, messageCode, tenantId, agentId, dtAgentId, ...otherValues } = values;
          Object.keys(otherValues).forEach((item) => {
            if (item.startsWith('recipientKey') || item.startsWith('recipientValue')) {
              recipientParams[item] = otherValues[item];
            } else {
              args[item] = otherValues[item];
            }
          });
          const data = {};
          paramsName.forEach((item) => {
            data[item] = {
              value: values[item],
              color: '#000',
            };
          });
          messageSender = {
            lang,
            messageCode,
            tenantId,
            receiverAddressList,
            typeCodeList: messageTypeCodeList,
            args,
          };
          weChatSender = {
            agentId,
            lang,
            messageCode,
            args,
            tenantId,
            typeCodeList: weChatTypeCodeList,
            userIdList,
            userList,
            data,
          };
          dingTalkSender = {
            agentId: dtAgentId,
            tenantId,
            lang,
            messageCode,
            args,
            userIdList: dtUserIdList,
          };

          // FIXME: 没有选择相关类型的时候，不传给后端
          const dataSource = {
            messageSender:
              messageTypeCodeList && messageTypeCodeList.length > 0 ? messageSender : undefined,
            weChatSender:
              weChatTypeCodeList && weChatTypeCodeList.length > 0 ? weChatSender : undefined,
            dingTalkSender:
              dingTalkTypeCodeList && dingTalkTypeCodeList.length > 0 ? dingTalkSender : undefined,
          };
          onSendOk(dataSource);
        }
      });
    }
  }

  /**
   * 添加接收者参数
   *
   * @memberof Drawer
   */
  @Bind()
  addRecipient() {
    const { recipientKey, recipientUuid } = this.state;
    const nextKeys = recipientKey.concat(recipientUuid);
    this.setState({
      recipientKey: nextKeys,
      recipientUuid: recipientUuid + 1,
    });
  }

  /**
   * 移除接收者键，值
   * @param {*}
   */
  @Bind()
  removeRecipient(k) {
    const { recipientKey } = this.state;
    if (recipientKey.length === 1) {
      return;
    }
    this.setState({
      recipientKey: recipientKey.filter((key) => key !== k),
    });
  }

  /**
   * 改变语言，获取参数
   */
  @Bind()
  changeLang(value) {
    const { onGetParams, tableRecord } = this.props;
    const { tenantId, messageCode } = tableRecord;
    const langParams = {
      lang: value,
      tenantId,
      messageCode,
    };
    onGetParams(langParams);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      path,
      anchor,
      visible,
      form,
      tableRecord,
      langType,
      enableService,
      saving,
      onCancel,
      paramsName,
      tenantRoleLevel,
      getParamsLoading = false,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const { recipientKey } = this.state;
    const recipientFormItems = recipientKey.map((k) => (
      <React.Fragment key={k}>
        <Row>
          <Col span={10}>
            <FormItem
              required={false}
              label={intl.get('hmsg.common.view.type').d('类型')}
              {...formItemLayout}
            >
              {getFieldDecorator(`recipientKey${k}`, {
                rules: [
                  {
                    required: true,
                    whitespace: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.common.view.type').d('类型'),
                    }),
                  },
                ],
              })(
                <Select allowClear>
                  {enableService &&
                    enableService.map((item) => (
                      <Option value={item.typeCode} key={item.typeCode}>
                        {item.typeMeaning}
                      </Option>
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
                rules: [
                  {
                    // FIXME: 去除类型校验
                    // type: (getFieldValue(`recipientKey${k}`) === 'WEB' && typeof getFieldValue(`recipientValue${k}`) === 'number') ? 'number' : 'string',
                    required:
                      // eslint-disable-next-line no-nested-ternary
                      getFieldValue(`recipientKey${k}`) === 'WEB_HOOK'
                        ? false
                        : getFieldValue(`recipientKey${k}`) !== 'WEB'
                        ? true
                        : isEmpty(getFieldValue(`recipientValue${k}`)),
                    whitespace: getFieldValue(`recipientKey${k}`) !== 'WEB', // 当是站内消息时, 由于 Lov 时 id(不是数字) 会导致校验出错
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.sendConfig.model.sendConfig.recipient').d('接收人'),
                    }),
                    // FIXME: 添加transform，只会影响校验，不会影响原始值，都转化为string类型
                    transform: (value) => {
                      return String(value);
                    },
                  },
                ],
              })(
                getFieldValue(`recipientKey${k}`) === 'WEB' ? (
                  <Lov code={isTenantRoleLevel() ? 'HIAM.USER.ORG' : 'HIAM.SITE.USER'} />
                ) : (
                  <Input />
                )
              )}
            </FormItem>
          </Col>
          {form.getFieldValue(`recipientKey${k}`) === 'WC_E' && (
            <Col span={10}>
              <FormItem
                required={false}
                label={intl.get('hmsg.sendConfig.model.sendConfig.agentId').d('应用ID')}
                {...formItemLayout}
              >
                {getFieldDecorator('agentId', {
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
          {form.getFieldValue(`recipientKey${k}`) === 'DT' && (
            <Col span={10}>
              <FormItem
                required={false}
                label={intl.get('hmsg.sendConfig.model.sendConfig.agentId').d('应用ID')}
                {...formItemLayout}
              >
                {getFieldDecorator('dtAgentId', {
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
                onClick={() => this.removeRecipient(k)}
              />
            ) : null}
          </Col>
        </Row>
      </React.Fragment>
    ));
    return (
      <Modal
        destroyOnClose
        width={520}
        title={intl.get('hmsg.sendConfig.view.title.testSend').d('测试发送')}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        confirmLoading={saving}
        onCancel={onCancel}
      >
        <Spin spinning={getParamsLoading}>
          <Form>
            {!tenantRoleLevel && (
              <FormItem
                label={intl.get('hmsg.sendConfig.model.sendConfig.tenantId').d('租户')}
                {...formLayout}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tableRecord.tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.sendConfig.model.sendConfig.tenantId').d('租户'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    disabled={!isUndefined(tableRecord.tenantId)}
                    textValue={tableRecord.tenantName}
                  />
                )}
              </FormItem>
            )}
            <FormItem
              label={intl.get('hmsg.sendConfig.model.sendConfig.messageCode').d('消息代码')}
              {...formLayout}
            >
              {getFieldDecorator('messageCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.sendConfig.model.sendConfig.messageCode').d('消息代码'),
                    }),
                  },
                ],
                initialValue: tableRecord.messageCode,
              })(<Input disabled />)}
            </FormItem>
            <FormItem label={intl.get('entity.lang.tag').d('语言')} {...formLayout}>
              {getFieldDecorator('lang', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.lang.tag').d('语言'),
                    }),
                  },
                ],
              })(
                <Select allowClear onChange={this.changeLang}>
                  {langType &&
                    langType.map((item) => (
                      <Option value={item.lang} key={item.lang}>
                        {item.langMeaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
            {!isEmpty(paramsName) ? (
              <FormItem
                labelCol={{ span: 2 }}
                label={intl.get('hmsg.sendConfig.model.sendConfig.params').d('参数')}
              />
            ) : (
              ''
            )}
            {!isEmpty(paramsName)
              ? paramsName &&
                paramsName.map((item) => (
                  <FormItem label={`${item}`} key={`${item}`} {...formParamsLayout}>
                    {getFieldDecorator(`${item}`, {})(<Input />)}
                  </FormItem>
                ))
              : ''}
            <FormItem>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.addRecipient`,
                    type: 'button',
                    meaning: '消息发送配置-添加接收人',
                  },
                ]}
                icon="plus"
                type="primary"
                onClick={this.addRecipient}
              >
                {intl.get('hmsg.sendConfig.view.button.addRecipient').d('添加接收人')}
              </ButtonPermission>
            </FormItem>
            {recipientFormItems}
          </Form>
        </Spin>
      </Modal>
    );
  }
}
