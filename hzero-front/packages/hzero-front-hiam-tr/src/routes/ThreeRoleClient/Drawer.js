import React, { PureComponent } from 'react';
import {
  Col,
  Form,
  Icon,
  Input,
  InputNumber,
  Modal,
  Row,
  Select,
  Spin,
  Tooltip,
  Popconfirm,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isObject, isString, isUndefined } from 'lodash';

import Lov from 'components/Lov';
import Switch from 'components/Switch';

import { encryptPwd } from 'utils/utils';
import intl from 'utils/intl';

const { Option } = Select;
const FormItem = Form.Item;
const { TextArea } = Input;

function isJSON(str) {
  let result;
  try {
    result = JSON.parse(str);
  } catch (e) {
    return false;
  }
  return isObject(result) && !isString(result);
}

@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  state = {
    ownedRoleList: [],
    selectedRowKeys: [],
    selectedRows: [],
    visibleRole: false,
    // dataSource: [], // 客户端表格的信息
    currentClientId: '',
    roleType: 'permission',
    selectedVisitRowKeys: [],
    visible: 0,
  };

  componentDidMount() {
    this.queryOwnedRole();
  }

  // 查询当前已分配角色
  @Bind()
  queryOwnedRole() {
    const { detailStatus, detailRecord, fetchDetailData } = this.props;
    switch (detailStatus) {
      case 'update':
        fetchDetailData(detailRecord);
        break;
      default:
        break;
    }
  }

  /**
   * 模态框确定-保存客户端
   */
  @Bind()
  onOk() {
    const { onOk, form, publicKey } = this.props;
    form.validateFields((error, fieldsValue) => {
      if (!error) {
        const newValue = { ...fieldsValue };
        if (fieldsValue.secret) {
          newValue.secret = encryptPwd(fieldsValue.secret, publicKey);
        }
        const newClient = {
          ...newValue,
        };
        onOk(newClient);
      }
    });
  }

  /**
   * 校验客户端名称
   * @param rule
   * @param value
   * @param callback
   */
  @Bind()
  checkName(rule, value, callback) {
    const { dispatch, tenantId } = this.props;
    if (value) {
      dispatch({
        type: 'trClient/checkClient',
        payload: {
          tenantId,
          name: value,
        },
      }).then((res) => {
        if (isJSON(res) && JSON.parse(res).failed) {
          callback(JSON.parse(res).message);
        } else {
          callback();
        }
      });
    } else {
      // 为空的话, 必输校验 已经 校验了
      callback();
    }
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

  // 校验作用域和自动授权域
  @Bind()
  validateSelect(rule, value, callback, name) {
    const length = value && value.length;
    if (length) {
      const reg = new RegExp(/^[A-Za-z]+$/);
      if (!reg.test(value[length - 1]) && name === 'scope') {
        callback(intl.get('hiam.client.view.validate.scope').d(`作用域只能包含英文字母`));
        return;
      }
      if (!reg.test(value[length - 1]) && name === 'autoApprove') {
        callback(intl.get('hiam.client.view.validate.autoApprove').d(`自动授权域只能包含英文字母`));
        return;
      }

      if (length > 6) {
        callback(intl.get('hiam.client.view.validate.maxLength').d('最多只能输入6个域'));
        return;
      }
    }
    callback();
  }

  // 初始化 授权类型
  @Bind()
  getAuthorizedGrantTypes() {
    const { initData = {} } = this.props;
    const createFlag = isUndefined(initData.id);
    if (createFlag) {
      return ['password', 'implicit', 'client_credentials', 'authorization_code', 'refresh_token'];
    }
    return initData.authorizedGrantTypes ? initData.authorizedGrantTypes.split(',') : [];
  }

  render() {
    const {
      form,
      initData = {},
      typeList = [],
      title,
      visible,
      onCancel,
      loading,
      detailStatus,
      fetchLoading = false,
      isTenantRoleLevel,
    } = this.props;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;

    const updateFlag = detailStatus === 'update';
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };
    const { visible: confirmVisible } = this.state;
    return (
      <Modal
        width={1000}
        title={title}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onOk={this.onOk}
        onCancel={onCancel}
        confirmLoading={loading}
        destroyOnClose
      >
        <Spin spinning={fetchLoading}>
          <Form>
            <Row gutter={24} type="flex">
              {!isTenantRoleLevel && (
                <Col span={12}>
                  <FormItem {...formLayout} label={intl.get('entity.tenant.tag').d('租户')}>
                    {getFieldDecorator('organizationId', {
                      initialValue: initData.organizationId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('entity.tenant.tag').d('租户'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code="HPFM.TENANT"
                        textField="tenantName"
                        disabled={updateFlag}
                        textValue={initData.tenantName}
                      />
                    )}
                  </FormItem>
                </Col>
              )}
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.name').d('名称')}
                >
                  {getFieldDecorator('name', {
                    initialValue: initData.name,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.client.model.client.name').d('名称'),
                        }),
                      },
                      {
                        pattern: /^[0-9a-zA-Z-]{0,32}$/,
                        message: intl
                          .get('hiam.client.model.client.namePattern')
                          .d('客户端名称只能由1-32位的数字或字母或中划线组成'),
                      },
                      {
                        validator: !updateFlag && this.checkName,
                      },
                    ],
                    validateTrigger: 'onBlur',
                  })(<Input disabled={updateFlag} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.secret').d('密钥')}
                >
                  {getFieldDecorator('secret', {
                    initialValue: initData.secret,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.client.model.client.secret').d('密钥'),
                        }),
                      },
                      {
                        max: 110,
                        message: intl.get('hzero.common.validation.max', {
                          max: 110,
                        }),
                      },
                    ],
                  })(<Input type="password" />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.authorizedGrantTypes').d('授权类型')}
                >
                  {getFieldDecorator('authorizedGrantTypes', {
                    // initialValue: initData.authorizedGrantTypes,
                    initialValue: this.getAuthorizedGrantTypes(),
                    rules: [
                      {
                        type: 'array',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.client.model.client.authorizedGrantTypes')
                            .d('授权类型'),
                        }),
                      },
                    ],
                  })(
                    <Select mode="multiple" style={{ width: '100%' }}>
                      {typeList.map((item) => (
                        <Option label={item.meaning} value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl
                    .get('hiam.client.model.client.accessTokenValidity')
                    .d('访问授权超时(秒)')}
                >
                  {getFieldDecorator('accessTokenValidity', {
                    // eslint-disable-next-line no-nested-ternary
                    initialValue: !updateFlag
                      ? 3600
                      : initData.accessTokenValidity
                      ? parseInt(initData.accessTokenValidity, 10)
                      : undefined,
                  })(<InputNumber style={{ width: '100%' }} min={60} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl
                    .get('hiam.client.model.client.refreshTokenValidity')
                    .d('授权超时(秒)')}
                >
                  {getFieldDecorator('refreshTokenValidity', {
                    // eslint-disable-next-line no-nested-ternary
                    initialValue: !updateFlag
                      ? 3600
                      : initData.refreshTokenValidity
                      ? parseInt(initData.refreshTokenValidity, 10)
                      : undefined,
                  })(<InputNumber style={{ width: '100%' }} min={60} />)}
                </FormItem>
              </Col>

              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    // label={intl.get('hiam.client.model.client.scope').d('作用域')}
                    label={
                      <span>
                        {intl.get('hiam.client.model.client.scope').d('作用域')}&nbsp;
                        <Tooltip
                          title={intl
                            .get('hiam.client.view.message.scope.help.msg')
                            .d('作用域为申请的授权范围。您最多可输入6个域')}
                        >
                          <Icon type="question-circle-o" />
                        </Tooltip>
                      </span>
                    }
                  >
                    {getFieldDecorator('scope', {
                      initialValue: initData.scope ? initData.scope.split(',') : [],
                      rules: [
                        {
                          validator: (rule, value, callback) =>
                            this.validateSelect(rule, value, callback, 'scope'),
                        },
                      ],
                      validateTrigger: 'onChange',
                    })(<Select mode="tags" style={{ width: '100%' }} />)}
                  </FormItem>
                </Col>
              )}
              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    // label={intl.get('hiam.client.model.client.autoApprove').d('自动授权域')}
                    label={
                      <span>
                        {intl.get('hiam.client.model.client.autoApprove').d('自动授权域')}&nbsp;
                        <Tooltip
                          title={intl
                            .get('hiam.client.view.message.autoApprove.help.msg')
                            .d(
                              '自动授权域为oauth认证后，系统自动授权而非用户手动添加的作用域。您最多可输入6个域'
                            )}
                        >
                          <Icon type="question-circle-o" />
                        </Tooltip>
                      </span>
                    }
                  >
                    {getFieldDecorator('autoApprove', {
                      initialValue: initData.autoApprove ? initData.autoApprove.split(',') : [],
                      rules: [
                        {
                          validator: (rule, value, callback) =>
                            this.validateSelect(rule, value, callback, 'autoApprove'),
                        },
                      ],
                      validateTrigger: 'onChange',
                    })(<Select mode="tags" style={{ width: '100%' }} />)}
                  </FormItem>
                </Col>
              )}
              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    label={intl
                      .get('hiam.client.model.client.webServerRedirectUri')
                      .d('重定向地址')}
                  >
                    {getFieldDecorator('webServerRedirectUri', {
                      initialValue: initData.webServerRedirectUri,
                    })(<Input />)}
                  </FormItem>
                </Col>
              )}
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.timeZone').d('时区')}
                >
                  {getFieldDecorator('timeZone', {
                    initialValue: initData.timeZone,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.client.model.client.timeZone').d('时区'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HIAM.TIME_ZONE"
                      textValue={initData.timeZoneMeaning}
                      textField="timeZoneMeaning"
                      allowClear={false}
                    />
                  )}
                </FormItem>
              </Col>
              {/* <Col span={12}>
                <FormItem {...formLayout} label={intl.get('hzero.common.button.enable').d('启用')}>
                  {getFieldDecorator('enabledFlag', {
                    initialValue: updateFlag ? initData.enabledFlag : 1,
                  })(<Switch />)}
                </FormItem>
              </Col> */}
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.pwdReplayFlag').d('密码防重放')}
                >
                  {getFieldDecorator('pwdReplayFlag', {
                    initialValue: updateFlag ? initData.pwdReplayFlag : 1,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.apiEncryptFlag').d('接口加密')}
                >
                  {getFieldDecorator('apiEncryptFlag', {
                    initialValue: updateFlag ? initData.apiEncryptFlag : 1,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.apiReplayFlag').d('API防重放')}
                >
                  <Popconfirm
                    title={intl
                      .get('hiam.client.view.confirm.apiReplayFlag')
                      .d('是否确认开启API防重放')}
                    visible={
                      confirmVisible && getFieldValue('apiReplayFlag') && updateFlag
                        ? !initData.apiReplayFlag
                        : 0
                    }
                    onCancel={() => {
                      setFieldsValue({ apiReplayFlag: 0 });
                    }}
                    onConfirm={() => {
                      this.setState({ visible: 0 });
                    }}
                  >
                    {getFieldDecorator('apiReplayFlag', {
                      initialValue: updateFlag ? initData.apiReplayFlag : 1,
                    })(
                      <Switch
                        onChange={() => {
                          this.setState({ visible });
                        }}
                      />
                    )}
                  </Popconfirm>
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={
                    <span>
                      {intl.get('hiam.client.model.client.passwordEncryptFlag').d('密码加密传输')}
                      &nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.client.view.message.scope.help.passwordEncryptFlag')
                          .d('用于标识使用当前客户端登录时，登录密码是否加密传输')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {getFieldDecorator('passwordEncryptFlag', {
                    initialValue: updateFlag ? initData.passwordEncryptFlag : true,
                  })(<HzeroSwitch />)}
                </FormItem>
              </Col>
              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    label={intl.get('hiam.client.model.client.additionalInformation').d('附加信息')}
                  >
                    {getFieldDecorator('additionalInformation', {
                      initialValue: initData.additionalInformation,
                      rules: [
                        {
                          validator: (rule, value, callback) => {
                            if (!value || this.isJson(value)) {
                              callback();
                            } else {
                              callback(
                                intl
                                  .get('hiam.client.view.validate.additionalInformation')
                                  .d('请输入正确的json字符串')
                              );
                            }
                          },
                        },
                      ],
                      validateTrigger: 'onBlur',
                    })(<TextArea rows={5} />)}
                  </FormItem>
                </Col>
              )}
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
