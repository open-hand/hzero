/**
 * passwordPolicy - 安全策略
 * @date: 2018-11-6
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import {
  Button,
  Col,
  Collapse,
  Form,
  Icon,
  Input,
  InputNumber,
  Row,
  Spin,
  Switch,
  Tooltip,
  Divider,
  Modal,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isString } from 'lodash';
import classnames from 'classnames';
import { PASSWORD } from 'utils/regExp';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import {
  DETAIL_DEFAULT_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';

import Drawer from './Drawer';

const FormItem = Form.Item;
const { Panel } = Collapse;

@connect(({ passwordPolicy, loading }) => ({
  passwordPolicy,
  fetchTableListLoading: loading.effects['passwordPolicy/fetchPasswordPolicyList'],
  saving: loading.effects['passwordPolicy/updatePasswordPolicy'],
  fetchUserCheckListLoading: loading.effects['passwordPolicy/fetchUserCheckList'],
  organizationId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hiam.passwordPolicy'] })
export default class PasswordPolicy extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      collapseKeys: ['passwordPolicy', 'loginPolicy', 'trPolicy', 'rolePolicy'],
      threeRoleFlag: false,
      visible: false,
    };
  }

  componentDidMount() {
    this.fetchPasswordPolicyList();
  }

  /**
   * onCollapseChange - 折叠面板onChange
   * @param {Array<string>} collapseKeys - Panels key
   */
  @Bind()
  onCollapseChange(collapseKeys) {
    this.setState({
      collapseKeys,
    });
  }

  /**
   * 获取表单数据
   */
  @Bind()
  fetchPasswordPolicyList() {
    const { dispatch, organizationId, form } = this.props;
    form.resetFields();
    dispatch({
      type: 'passwordPolicy/fetchPasswordPolicyList',
      payload: organizationId,
    }).then((res) => {
      if (res) {
        this.setState({
          threeRoleFlag: res.enableThreeRole,
        });
      }
    });
  }

  /**
   * 更新密码策略
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      form,
      passwordPolicy: { passwordPolicyList = {} },
    } = this.props;
    const { threeRoleFlag } = this.state;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        const {
          originalPassword,
          passwordUpdateRate,
          passwordReminderPeriod,
          minLength,
          maxLength,
          digitsCount,
          lowercaseCount,
          uppercaseCount,
          specialCharCount,
          notRecentCount,
          notUsername,
          forceModifyPassword,
          loginAgain,
        } = passwordPolicyList;
        dispatch({
          type: 'passwordPolicy/updatePasswordPolicy',
          payload: values.enablePassword
            ? {
                ...passwordPolicyList,
                ...values,
                maxCheckCaptcha: values.maxCheckCaptcha || 0,
                enableThreeRole: threeRoleFlag,
              }
            : {
                ...passwordPolicyList,
                ...values,
                maxCheckCaptcha: values.maxCheckCaptcha || 0,
                originalPassword,
                passwordUpdateRate,
                passwordReminderPeriod,
                minLength,
                maxLength,
                digitsCount,
                lowercaseCount,
                uppercaseCount,
                specialCharCount,
                notRecentCount,
                notUsername,
                forceModifyPassword,
                loginAgain,
                enableThreeRole: threeRoleFlag,
              },
        }).then((res) => {
          if (res) {
            this.fetchPasswordPolicyList();
            notification.success();
          }
        });
      }
    });
  }

  /**
   * 密码到期天数input框的自定义校验条件
   * @param {object} rules
   * @param {number} value - 设置密码到期提醒的天数
   * @param {function} callback
   * @returns
   * @memberof PasswordPolicy
   */
  @Bind()
  expireRemindCheck(rules, value, callback) {
    const {
      form: { getFieldValue },
    } = this.props;
    const passwordUpdateRate = getFieldValue('passwordUpdateRate') || 0;
    if (value > passwordUpdateRate) {
      return false;
    }
    callback();
  }

  /**
   * 密码安全策略表单
   */
  renderPasswordForm() {
    const {
      form: { getFieldDecorator, getFieldValue },
      passwordPolicy: { passwordPolicyList = {} },
    } = this.props;
    return (
      <Form>
        <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status.enable').d('启用')}
            >
              {getFieldDecorator('enablePassword', {
                initialValue: passwordPolicyList.enablePassword,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
        <Divider />
        <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.originalPassword')
                .d('新用户默认密码')}
            >
              {getFieldDecorator('originalPassword', {
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hiam.subAccount.model.user.password').d('密码'),
                          }),
                        },
                        {
                          pattern: PASSWORD,
                          message: intl
                            .get('hzero.common.validation.password')
                            .d('至少包含数字/字母/字符2种组合,长度至少为6个字符'),
                        },
                      ],
                initialValue: passwordPolicyList.originalPassword,
              })(<Input disabled={getFieldValue('enablePassword') === false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.passwordUpdateRate')
                .d('密码更新频率')}
            >
              {getFieldDecorator('passwordUpdateRate', {
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hiam.passwordPolicy.model.passwordPolicy.passwordUpdateRate')
                              .d('密码更新频率'),
                          }),
                        },
                      ],
                initialValue: passwordPolicyList.passwordUpdateRate,
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  formatter={(value) => {
                    const regex = /\D/g; // 匹配除数字外的所有字符
                    const v = isString(value) ? value.replace(regex, '') : value;
                    return `${v} ${intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.day')
                      .d('天')}`;
                  }}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.pwdReminderPeriod')
                .d('密码到期提醒')}
            >
              {getFieldDecorator('passwordReminderPeriod', {
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hiam.passwordPolicy.model.passwordPolicy.pwdReminderPeriod')
                              .d('密码到期提醒'),
                          }),
                        },
                        {
                          validator: this.expireRemindCheck,
                          message: intl
                            .get('hiam.passwordPolicy.view.validation.pwdExpiredRemindMsg')
                            .d('密码到期提醒天数不能大于密码更新频率天数'),
                        },
                      ],
                initialValue: passwordPolicyList.passwordReminderPeriod,
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  formatter={(value) => {
                    const regex = /\D/g; // 匹配除数字外的所有字符
                    const v = isString(value) ? value.replace(regex, '') : value;
                    return `${v} ${intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.dayAgo')
                      .d('天前')}`;
                  }}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.minLength')
                .d('最小密码长度')}
            >
              {getFieldDecorator('minLength', {
                initialValue: passwordPolicyList.minLength,
                rules: [
                  {
                    required: true,
                    message: intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.minLength')
                      .d('最小密码长度'),
                  },
                ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={6}
                  precision={0}
                  max={getFieldValue('maxLength')}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.maxLength')
                .d('最大密码长度')}
            >
              {getFieldDecorator('maxLength', {
                initialValue: passwordPolicyList.maxLength,
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl
                            .get('hiam.passwordPolicy.model.passwordPolicy.maxLength')
                            .d('最大密码长度'),
                        },
                      ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={getFieldValue('minLength')}
                  precision={0}
                  max={30}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.digitsCount')
                .d('最少数字数')}
            >
              {getFieldDecorator('digitsCount', {
                initialValue: passwordPolicyList.digitsCount,
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hiam.passwordPolicy.model.passwordPolicy.digitsCount')
                              .d('最少数字数'),
                          }),
                        },
                      ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.lowercaseCount')
                .d('最少小写字母数')}
            >
              {getFieldDecorator('lowercaseCount', {
                initialValue: passwordPolicyList.lowercaseCount,
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl
                            .get('hiam.passwordPolicy.model.passwordPolicy.lowercaseCount')
                            .d('最少小写字母数'),
                        },
                      ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.uppercaseCount')
                .d('最少大写字母数')}
            >
              {getFieldDecorator('uppercaseCount', {
                initialValue: passwordPolicyList.uppercaseCount,
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl
                            .get('hiam.passwordPolicy.model.passwordPolicy.uppercaseCount')
                            .d('最少大写字母数'),
                        },
                      ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.specialCharCount')
                .d('最少特殊字符数')}
            >
              {getFieldDecorator('specialCharCount', {
                initialValue: passwordPolicyList.specialCharCount,
                rules:
                  getFieldValue('enablePassword') === false
                    ? []
                    : [
                        {
                          required: true,
                          message: intl
                            .get('hiam.passwordPolicy.model.passwordPolicy.specialCharCount')
                            .d('最少特殊字符数'),
                        },
                      ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl
                    .get('hiam.passwordPolicy.model.passwordPolicy.notRecentCount')
                    .d('近期密码')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.subAccount.view.message.notRecentCount.tooltip')
                      .d('近期密码不能作为更新密码')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator('notRecentCount', {
                initialValue: passwordPolicyList.notRecentCount,
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  disabled={getFieldValue('enablePassword') === false}
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.notUsername')
                .d('允许与登录名相同')}
            >
              {getFieldDecorator('notUsername', { initialValue: passwordPolicyList.notUsername })(
                <Switch disabled={getFieldValue('enablePassword') === false} />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl
                    .get('hiam.passwordPolicy.model.passwordPolicy.forceCodeVerify')
                    .d('强制验证码校验')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.subAccount.view.message.forceCodeVerify.tooltip')
                      .d('在进行密码的相关操作时，需要强制使用验证码功能进行校验')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator('forceCodeVerify', {
                initialValue: passwordPolicyList.forceCodeVerify,
              })(<Switch disabled={getFieldValue('enablePassword') === false} />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  @Bind()
  handleChangeThreeRole(v) {
    const self = this;
    const { threeRoleFlag } = self.state;
    Modal.confirm({
      title: intl.get('hiam.passwordPolicy.model.passwordPolicy.three.role.enable').d('注意'),
      content: threeRoleFlag
        ? intl
            .get('hiam.passwordPolicy.model.passwordPolicy.three.role.disabled.content')
            .d('禁用三员后将禁用所有三员角色及子孙角色，建议先启用租户管理员，便于后续管理操作')
        : intl
            .get('hiam.passwordPolicy.model.passwordPolicy.three.role.enable.content')
            .d('启用三员后建议禁用租户管理员角色，便于将权限控制在三员内'),
      onOk() {
        self.setState({
          threeRoleFlag: !threeRoleFlag,
        });
      },
      onCancel() {
        self.setState({
          threeRoleFlag: !v,
        });
      },
    });
  }

  /**
   * 登录安全策略表单
   */
  renderLoginForm() {
    const {
      form: { getFieldDecorator, getFieldValue },
      passwordPolicy: { passwordPolicyList = {} },
      match: { path },
    } = this.props;
    return (
      <Form>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...EDIT_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.passwordPolicy.model.passwordPolicy.enableCaptcha')
                  .d('启用图形验证码')}
              >
                {getFieldDecorator('enableCaptcha', {
                  initialValue: passwordPolicyList.enableCaptcha,
                })(<Switch />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...EDIT_FORM_ITEM_LAYOUT}
                label={
                  <span>
                    {intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.maxErrorTime')
                      .d('最大密码错误次数')}
                    &nbsp;
                    <Tooltip
                      title={intl
                        .get('hiam.passwordPolicy.view.message.maxErrorTime.tooltip')
                        .d('登录时密码错误超过最大密码错误次数将锁定用户')}
                    >
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                }
              >
                {getFieldDecorator('maxErrorTime', {
                  initialValue: passwordPolicyList.maxErrorTime,
                })(
                  <InputNumber
                    style={{ width: '100%' }}
                    min={getFieldValue('maxCheckCaptcha')}
                    precision={0}
                  />
                )}
              </FormItem>
            </Col>

            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...EDIT_FORM_ITEM_LAYOUT}
                label={
                  <span>
                    {intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.maxCheckCaptcha')
                      .d('验证码错误次数')}
                    &nbsp;
                    <Tooltip
                      title={intl
                        .get('hiam.passwordPolicy.view.message.maxCheckCaptcha.tooltip')
                        .d('登录时密码错误超过开启验证码的密码错误次数将显示图像验证码')}
                    >
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                }
              >
                {getFieldDecorator('maxCheckCaptcha', {
                  initialValue: passwordPolicyList.maxCheckCaptcha,
                })(
                  <InputNumber
                    style={{ width: '100%' }}
                    min={0}
                    max={getFieldValue('maxErrorTime')}
                    precision={0}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.enableLock')
                .d('允许锁定用户')}
            >
              {getFieldDecorator('enableLock', { initialValue: passwordPolicyList.enableLock })(
                <Switch />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl
                    .get('hiam.passwordPolicy.model.passwordPolicy.lockedExpireTime')
                    .d('锁定时长')}
                  &nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.passwordPolicy.view.message.lockedExpireTime.tooltip')
                      .d('用户锁定时间超过锁定时长将自动解锁')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {getFieldDecorator('lockedExpireTime', {
                initialValue: passwordPolicyList.lockedExpireTime,
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={0}
                  formatter={(value) =>
                    `${value}${intl.get('hiam.passwordPolicy.model.passwordPolicy.second').d('秒')}`
                  }
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.enableWebLogin')
                .d('PC端允许多处登录')}
            >
              {getFieldDecorator('enableWebMultipleLogin', {
                initialValue: passwordPolicyList.enableWebMultipleLogin,
              })(<Switch />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.enableAppLogin')
                .d('移动端允许多处登录')}
            >
              {getFieldDecorator('enableAppMultipleLogin', {
                initialValue: passwordPolicyList.enableAppMultipleLogin,
              })(<Switch />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.forceModifyPassword')
                .d('强制修改初始密码')}
            >
              {getFieldDecorator('forceModifyPassword', {
                initialValue: passwordPolicyList.forceModifyPassword,
              })(<Switch />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.loginAgain')
                .d('修改密码后重新登录')}
            >
              {getFieldDecorator('loginAgain', {
                initialValue: passwordPolicyList.loginAgain,
              })(<Switch />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.verifyAgain')
                .d('用户登录二次校验')}
            >
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.verifyAgain`,
                    type: 'button',
                    meaning: '密码策略-用户登录二次校验',
                  },
                ]}
                onClick={this.handleOpenModal}
              >
                {intl.get('hzero.common.button.assignUser').d('指定用户')}
              </ButtonPermission>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  @Bind()
  renderThreeRole() {
    const { threeRoleFlag } = this.state;
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              {...EDIT_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.passwordPolicy.model.passwordPolicy.enableThreeRole')
                .d('启用三员')}
            >
              {getFieldDecorator('enableThreeRole', {
                initialValue: threeRoleFlag,
              })(<Switch onClick={this.handleChangeThreeRole} checked={threeRoleFlag} />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 角色配置策略表单
   */
  renderRoleForm() {
    const {
      form: { getFieldDecorator },
      passwordPolicy: { passwordPolicyList = {} },
    } = this.props;
    return (
      <Form>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...EDIT_FORM_ITEM_LAYOUT}
                label={
                  <span>
                    {intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.enableRoleInherit')
                      .d('是否启用角色继承功能')}
                    &nbsp;
                    <Tooltip
                      title={intl
                        .get('hiam.passwordPolicy.view.message.enableRoleInherit.tooltip')
                        .d('禁用后，角色管理将不能使用继承操作')}
                    >
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                }
              >
                {getFieldDecorator('enableRoleInherit', {
                  initialValue: passwordPolicyList.enableRoleInherit,
                })(<Switch />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...EDIT_FORM_ITEM_LAYOUT}
                label={
                  <span>
                    {intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.enableRoleAllocate')
                      .d('是否允许角色自分配')}
                    &nbsp;
                    <Tooltip
                      title={intl
                        .get('hiam.passwordPolicy.view.message.enableRoleAllocate.tooltip')
                        .d('禁用后，自己的顶级角色将不能分配给其它用户或客户端')}
                    >
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                }
              >
                {getFieldDecorator('enableRoleAllocate', {
                  initialValue: passwordPolicyList.enableRoleAllocate,
                })(<Switch />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...EDIT_FORM_ITEM_LAYOUT}
                label={
                  <span>
                    {intl
                      .get('hiam.passwordPolicy.model.passwordPolicy.enableRolePermission')
                      .d('是否允许角色操作权限')}
                    &nbsp;
                    <Tooltip
                      title={intl
                        .get('hiam.passwordPolicy.view.message.enableRolePermission.tooltip')
                        .d(
                          '禁用后，除租户管理员外，其它角色不能操作数据权限、字段权限、单据权限、工作台配置功能'
                        )}
                    >
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                }
              >
                {getFieldDecorator('enableRolePermission', {
                  initialValue: passwordPolicyList.enableRolePermission,
                })(<Switch />)}
              </FormItem>
            </Col>
          </Row>
        </Row>
      </Form>
    );
  }

  /**
   * 更新密码策略
   */
  @Bind()
  handleOpenModal() {
    this.setState({ visible: true });
    this.handleSearch();
  }

  /**
   * 更新密码策略
   */
  @Bind()
  handleCloseModal() {
    this.setState({ visible: false });
  }

  /**
   * 获取二次用户数据
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'passwordPolicy/fetchUserCheckList',
      payload: params,
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination = {}) {
    this.handleSearch({
      page: pagination,
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handleAdd(userIds, type, params = {}, cb = (e) => e) {
    const {
      dispatch,
      passwordPolicy: { pagination },
    } = this.props;
    dispatch({
      type:
        type === 'phone'
          ? 'passwordPolicy/addUserPhoneCheckList'
          : 'passwordPolicy/addUserEmailCheckList',
      payload: userIds,
    }).then(() => {
      cb();
      this.handleSearch({ page: pagination, ...params });
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handleDelete(userIds, type, params = {}, cb = (e) => e) {
    const {
      dispatch,
      passwordPolicy: { pagination },
    } = this.props;
    dispatch({
      type:
        type === 'phone'
          ? 'passwordPolicy/deleteUserPhoneCheckList'
          : 'passwordPolicy/deleteUserEmailCheckList',
      payload: userIds,
    }).then(() => {
      cb();
      this.handleSearch({ page: pagination, ...params });
    });
  }

  render() {
    const {
      saving,
      fetchTableListLoading,
      fetchUserCheckListLoading,
      match: { path },
      passwordPolicy: { pagination, checkList },
    } = this.props;
    const { collapseKeys = [], visible } = this.state;
    return (
      <>
        <Header title={intl.get('hiam.passwordPolicy.view.message.title').d('安全策略')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '密码策略-保存',
              },
            ]}
            type="primary"
            icon="save"
            onClick={this.handleSave}
            loading={saving || fetchTableListLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <Button icon="sync" onClick={this.fetchPasswordPolicyList}>
            {intl.get('hzero.common.button.reload').d('重新加载')}
          </Button>
        </Header>
        <Content>
          <Spin
            spinning={fetchTableListLoading}
            wrapperClassName={classnames(DETAIL_DEFAULT_CLASSNAME)}
          >
            <Collapse
              className="form-collapse"
              defaultActiveKey={['passwordPolicy', 'loginPolicy', 'trPolicy', 'rolePolicy']}
              onChange={this.onCollapseChange}
            >
              <Panel
                showArrow={false}
                header={
                  <>
                    <h3>
                      {intl
                        .get('hiam.passwordPolicy.view.message.subTitle.passwordPolicy')
                        .d('密码安全策略')}
                    </h3>
                    <a>
                      {collapseKeys.includes('passwordPolicy')
                        ? intl.get(`hzero.common.button.up`).d('收起')
                        : intl.get(`hzero.common.button.expand`).d('展开')}
                    </a>
                    <Icon type={collapseKeys.includes('passwordPolicy') ? 'up' : 'down'} />
                  </>
                }
                key="passwordPolicy"
              >
                {this.renderPasswordForm()}
              </Panel>
              <Panel
                showArrow={false}
                header={
                  <>
                    <h3>
                      {intl
                        .get('hiam.passwordPolicy.view.message.subTitle.loginPolicy')
                        .d('登录安全策略')}
                    </h3>
                    <a>
                      {collapseKeys.includes('loginPolicy')
                        ? intl.get(`hzero.common.button.up`).d('收起')
                        : intl.get(`hzero.common.button.expand`).d('展开')}
                      <Icon type={collapseKeys.includes('loginPolicy') ? 'up' : 'down'} />
                    </a>
                  </>
                }
                key="loginPolicy"
              >
                {this.renderLoginForm()}
              </Panel>
              <Panel
                showArrow={false}
                header={
                  <>
                    <h3>
                      {intl
                        .get('hiam.passwordPolicy.view.message.subTitle.trPolicy')
                        .d('三员安全策略')}
                    </h3>
                    <a>
                      {collapseKeys.includes('trPolicy')
                        ? intl.get(`hzero.common.button.up`).d('收起')
                        : intl.get(`hzero.common.button.expand`).d('展开')}
                      <Icon type={collapseKeys.includes('trPolicy') ? 'up' : 'down'} />
                    </a>
                  </>
                }
                key="trPolicy"
              >
                {this.renderThreeRole()}
              </Panel>
              <Panel
                showArrow={false}
                header={
                  <>
                    <h3>
                      {intl
                        .get('hiam.passwordPolicy.view.message.subTitle.rolePolicy')
                        .d('角色配置策略')}
                    </h3>
                    <a>
                      {collapseKeys.includes('rolePolicy')
                        ? intl.get(`hzero.common.button.up`).d('收起')
                        : intl.get(`hzero.common.button.expand`).d('展开')}
                      <Icon type={collapseKeys.includes('rolePolicy') ? 'up' : 'down'} />
                    </a>
                  </>
                }
                key="rolePolicy"
              >
                {this.renderRoleForm()}
              </Panel>
            </Collapse>
            <Drawer
              visible={visible}
              path={path}
              loading={fetchUserCheckListLoading}
              onSearch={this.handleSearch}
              onCancel={this.handleCloseModal}
              onAdd={this.handleAdd}
              onDelete={this.handleDelete}
              pagination={pagination}
              dataSource={checkList}
              onPagination={this.handlePagination}
            />
          </Spin>
        </Content>
      </>
    );
  }
}
