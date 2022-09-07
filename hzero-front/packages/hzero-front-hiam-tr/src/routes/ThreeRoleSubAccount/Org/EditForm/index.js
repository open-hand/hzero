/**
 * EditForm - 用户管理 - 账号编辑表单
 * @date 2018/11/13
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import PropTypes from 'prop-types';
import { forEach, isUndefined, join, map, omit } from 'lodash';
import { Col, DatePicker, Form, Input, Row, Select, Tooltip, Icon } from 'hzero-ui';
import moment from 'moment';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import { EMAIL, NOT_CHINA_PHONE, PHONE, CODE } from 'utils/regExp';
import intl from 'utils/intl';
import { DEFAULT_DATE_FORMAT, FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { getDateFormat } from 'utils/utils';
import { validatePasswordRule } from '@/utils/validator';

/**
 * EditForm-编辑子账户信息
 * @reactProps {Object[]} LEVEL 资源层级的值集
 */
@Form.create({ fieldNameProp: null })
export default class EditForm extends React.Component {
  constructor(props) {
    super(props);
    const { onRef } = props;
    onRef(this);
    this.cancelDefaultParams = new Set();
  }

  static propTypes = {
    level: PropTypes.array,
  };

  static defaultProps = {
    level: [],
  };

  state = {
    level: [],
    // 选择组织的框是否显示
  };

  /**
   * @param {Object} nextProps 下一个属性
   * @param {Object} prevState 上一个状态
   */
  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {};
    const { level } = nextProps;
    if (level !== prevState.level) {
      nextState.level = level;
      nextState.levelMap = {};
      forEach(level, (l) => {
        nextState.levelMap[l.value] = l;
      });
    }
    return nextState;
  }

  /**
   * 将 hook 方法传递出去
   */
  componentDidMount() {
    this.init();
  }

  /**
   * 初始化数据
   * 编辑 + 加载用户角色
   * 重置form表单
   */
  init() {
    const { form } = this.props;
    form.resetFields();
    this.cancelDefaultParams.clear();
  }

  /**
   * 检查 确认密码是否与密码一致
   * @param {String} rule
   * @param {String} value
   * @param {Function} callback
   */
  @Bind()
  validatePasswordRepeat(rule, value, callback) {
    const { form } = this.props;
    if (value && value !== form.getFieldValue('password')) {
      callback(
        intl.get('hiam.subAccount.view.validation.passwordSame').d('确认密码必须与密码一致')
      );
    } else {
      callback();
    }
  }

  /**
   * 检查 有效日期至 大于 有效日期从
   * @param {String} rule
   * @param {String} value
   * @param {Function} callback
   */
  @Bind()
  validateEndDateActive(rule, value, callback) {
    const { form } = this.props;
    // startDateActive 已经保证会有值
    if (value && !value.isAfter(form.getFieldValue('startDateActive'), 'day')) {
      callback(
        intl.get('hiam.subAccount.view.validation.timeRange').d('有效日期至必须大于有效日期从')
      );
    } else {
      callback();
    }
  }

  /**
   * 获取编辑完成的数据
   */
  @Bind()
  getEditFormData() {
    const { form, initialValue, isCreate, organizationId } = this.props;
    let result = {};
    let saveData = {};
    const validateFields = ['realName', 'email', 'phone', 'startDateActive', 'endDateActive'];
    if (isCreate) {
      validateFields.push('password');
      validateFields.push('anotherPassword');
    } else {
      validateFields.push('enabled');
    }
    form.validateFields((err, values) => {
      if (!err) {
        const { birthday, startDateActive, endDateActive, ...data } = omit(values);
        result = {
          ...initialValue,
          ...data,
          startDateActive: startDateActive
            ? startDateActive.format(DEFAULT_DATE_FORMAT)
            : undefined,
          endDateActive: endDateActive ? endDateActive.format(DEFAULT_DATE_FORMAT) : undefined,
          birthday: birthday ? birthday.format(DEFAULT_DATE_FORMAT) : undefined,
          organizationId,
        };
        saveData = isCreate
          ? {
              userType: 'P',
              ...result,
            }
          : result;
      }
    });
    return saveData;
  }

  @Bind()
  changeCountryId() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ regionId: undefined });
  }

  /**
   * @date 2019-06-13
   * 区号改变 需要 重置手机号的校验状态
   */
  @Bind()
  reValidationPhone(value) {
    const { form } = this.props;
    const prevInternationalTelCode = form.getFieldValue('internationalTelCode');
    if (value === '+86' || prevInternationalTelCode === '+86') {
      // 只要 +86 出现在 中间态 就需要重新手动校验 phone
      const curPhone = form.getFieldValue('phone');
      let errors = null;
      if (curPhone) {
        const testReg = value === '+86' ? PHONE : NOT_CHINA_PHONE;
        if (!testReg.test(curPhone)) {
          errors = [new Error(intl.get('hzero.common.validation.phone').d('手机格式不正确'))];
        }
      } else {
        errors = [
          new Error(
            intl.get('hzero.common.validation.notNull', {
              name: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
            })
          ),
        ];
      }
      form.setFields({
        phone: {
          value: curPhone,
          errors,
        },
      });
    }
  }

  // #region 验证密码修改后 是否和 确认密码一致

  /**
   * 检查 确认密码是否与密码一致
   */
  @Bind()
  validatePasswordRepeatForPassword(e) {
    const { form } = this.props;

    const anotherPassword = form.getFieldValue('anotherPassword');
    const anotherPasswordField = {
      value: anotherPassword,
    };
    if (e.target.value) {
      if (e.target.value === anotherPassword) {
        anotherPasswordField.errors = null;
      } else {
        anotherPasswordField.errors = [
          new Error(
            intl.get('hiam.subAccount.view.validation.passwordSame').d('确认密码必须与密码一致')
          ),
        ];
      }
    } else {
      anotherPasswordField.errors = null;
    }
    form.setFields({
      anotherPassword: anotherPasswordField,
    });
  }

  // #regionend

  /**
   * 渲染新增表单
   */
  renderCreateForm() {
    const { form, passwordTipMsg = {}, customizeForm } = this.props;
    const dateFormat = getDateFormat();
    const { idd = [], gender = [] } = this.props;
    return customizeForm(
      { code: 'HIAM.SUB_ACCOUND.EDIT.FORM_CREATE', form },
      <Form>
        <Row type="flex">
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl.get('hiam.subAccount.model.user.loginName').d('账号')}&nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.subAccount.view.message.loginName.tooltip')
                      .d('不输入账户则自动生成')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {form.getFieldDecorator('loginName', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
            >
              {form.getFieldDecorator('realName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.subAccount.model.user.realName').d('名称'),
                    }),
                  },
                  {
                    max: 40,
                    message: intl.get('hzero.common.validation.max', {
                      max: 40,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col key="birthday" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              key="birthday"
              label={intl.get('hiam.subAccount.model.user.birthday').d('出生日期')}
            >
              {form.getFieldDecorator(
                'birthday',
                {}
              )(<DatePicker format={dateFormat} style={{ width: '100%' }} placeholder="" />)}
            </Form.Item>
          </Col>
          <Col key="nickname" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.nickname').d('昵称')}
            >
              {form.getFieldDecorator('nickname', {
                rules: [
                  {
                    max: 10,
                    message: intl.get('hzero.common.validation.max', {
                      max: 10,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col key="gender" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.gender').d('性别')}
            >
              {form.getFieldDecorator(
                'gender',
                {}
              )(
                <Select allowClear>
                  {map(gender, (item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col key="country" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.countryId').d('国家')}
            >
              {form.getFieldDecorator(
                'countryId',
                {}
              )(
                <Lov
                  code="HPFM.COUNTRY"
                  onChange={this.changeCountryId}
                  queryParams={{ enabledFlag: 1 }}
                  // textValue={initialValue.countryName}
                  // textField="tenantName"
                  // disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="regionId" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.regionId').d('地区')}
            >
              {form.getFieldDecorator(
                'regionId',
                {}
              )(
                <Lov
                  code="HPFM.REGION"
                  queryParams={{
                    countryId: form.getFieldValue('countryId'),
                  }}
                  // textValue={initialValue.regionName}
                  // textField="tenantName"
                  // disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="addressDetail" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.addressDetail').d('详细地址')}
            >
              {form.getFieldDecorator('addressDetail', {
                rules: [
                  {
                    max: 50,
                    message: intl.get('hzero.common.validation.max', {
                      max: 50,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.email').d('邮箱')}
            >
              {form.getFieldDecorator('email', {
                rules: [
                  {
                    pattern: EMAIL,
                    message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', { max: 128 }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
            >
              {form.getFieldDecorator('phone', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
                    }),
                  },
                  {
                    pattern:
                      form.getFieldValue('internationalTelCode') === '+86'
                        ? PHONE
                        : NOT_CHINA_PHONE,
                    message: intl.get('hzero.common.validation.phone').d('手机格式不正确'),
                  },
                ],
              })(
                <Input
                  addonBefore={form.getFieldDecorator('internationalTelCode', {
                    initialValue: (idd[0] && idd[0].value) || '+86',
                  })(
                    <Select onChange={this.reValidationPhone}>
                      {map(idd, (r) => (
                        <Select.Option key={r.value} value={r.value}>
                          {r.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl.get('hiam.subAccount.model.user.password').d('密码')}&nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.subAccount.view.message.password.tooltip')
                      .d('不输入密码则使用默认密码')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {form.getFieldDecorator('password', {
                rules: [
                  {
                    validator: (_, value, callback) => {
                      validatePasswordRule(value, callback, {
                        ...passwordTipMsg,
                        loginName: form.getFieldValue('loginName'),
                      });
                    },
                  },
                ],
              })(
                <Input
                  type="password"
                  autocomplete="new-password"
                  onChange={this.validatePasswordRepeatForPassword}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.from').d('有效日期从')}
            >
              {form.getFieldDecorator('startDateActive', {
                initialValue: undefined,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.date.active.from').d('有效日期从'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  allowClear={false}
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('endDateActive') &&
                    moment(form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.to').d('有效日期至')}
            >
              {form.getFieldDecorator('endDateActive', {
                rules: [
                  {
                    type: 'object',
                    validator: this.validateEndDateActive,
                  },
                ],
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('startDateActive') &&
                    moment(form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 渲染编辑表单
   */
  renderEditForm() {
    const { form, initialValue = {}, customizeForm, readOnly = false } = this.props;
    const { idd = [], gender = [] } = this.props;
    const emailError = form.getFieldError('email');
    const sameEmail = initialValue.email === form.getFieldValue('email');
    const phoneError = form.getFieldError('phone');
    const samePhone = initialValue.phone === form.getFieldValue('phone');
    const dateFormat = getDateFormat();
    return customizeForm(
      { code: 'HIAM.SUB_ACCOUND.EDIT.FORM_EDIT', dataSource: initialValue, form },
      <Form>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl.get('hiam.subAccount.model.user.loginName').d('账号')}&nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.subAccount.view.message.loginName.tooltip')
                      .d('不输入账户则自动生成')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {form.getFieldDecorator('loginName', {
                initialValue: initialValue.loginName,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  // {
                  //   pattern: CODE,
                  //   message: intl
                  //     .get('hzero.common.validation.code')
                  //     .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  // },
                ],
              })(<Input disabled={readOnly || initialValue.loginName !== undefined} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
            >
              {form.getFieldDecorator('realName', {
                initialValue: initialValue.realName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.subAccount.model.user.realName').d('名称'),
                    }),
                  },
                  {
                    max: 40,
                    message: intl.get('hzero.common.validation.max', {
                      max: 40,
                    }),
                  },
                ],
              })(<Input disabled={readOnly} />)}
            </Form.Item>
          </Col>
          <Col key="birthday" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              key="birthday"
              label={intl.get('hiam.subAccount.model.user.birthday').d('出生日期')}
            >
              {form.getFieldDecorator('birthday', {
                initialValue: initialValue.birthday
                  ? moment(initialValue.birthday, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder=""
                  disabled={readOnly}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="nickname" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.nickname').d('昵称')}
            >
              {form.getFieldDecorator('nickname', {
                initialValue: initialValue.nickname,
                rules: [
                  {
                    max: 10,
                    message: intl.get('hzero.common.validation.max', {
                      max: 10,
                    }),
                  },
                ],
              })(<Input disabled={readOnly} />)}
            </Form.Item>
          </Col>
          <Col key="gender" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.gender').d('性别')}
            >
              {form.getFieldDecorator('gender', {
                initialValue: isUndefined(initialValue.gender) ? '' : `${initialValue.gender}`,
              })(
                <Select allowClear disabled={readOnly}>
                  {map(gender, (item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col key="country" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.countryId').d('国家')}
            >
              {form.getFieldDecorator('countryId', {
                initialValue: initialValue.countryId,
              })(
                <Lov
                  code="HPFM.COUNTRY"
                  onChange={this.changeCountryId}
                  textValue={initialValue.countryName}
                  queryParams={{ enabledFlag: 1 }}
                  disabled={readOnly}
                  // textField="tenantName"
                  // disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="regionId" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.regionId').d('地区')}
            >
              {form.getFieldDecorator('regionId', {
                initialValue: initialValue.regionId,
              })(
                <Lov
                  code="HPFM.REGION"
                  queryParams={{
                    countryId: form.getFieldValue('countryId'),
                  }}
                  textValue={initialValue.regionName}
                  disabled={readOnly}
                  // textField="tenantName"
                  // disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="addressDetail" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.addressDetail').d('详细地址')}
            >
              {form.getFieldDecorator('addressDetail', {
                initialValue: initialValue.addressDetail,
                rules: [
                  {
                    max: 50,
                    message: intl.get('hzero.common.validation.max', {
                      max: 50,
                    }),
                  },
                ],
              })(<Input disabled={readOnly} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              hasFeedback
              help={
                // eslint-disable-next-line no-nested-ternary
                emailError
                  ? join(emailError)
                  : (sameEmail && initialValue.email && initialValue.emailCheckFlag) ||
                    !form.getFieldValue('email')
                  ? ''
                  : intl.get('hiam.subAccount.view.validation.emailNotCheck').d('邮箱未验证')
              }
              validateStatus={
                // eslint-disable-next-line no-nested-ternary
                emailError
                  ? 'error'
                  : // eslint-disable-next-line no-nested-ternary
                  sameEmail && initialValue.email && initialValue.emailCheckFlag
                  ? 'success'
                  : form.getFieldValue('email')
                  ? 'warning'
                  : undefined
              }
              label={intl.get('hiam.subAccount.model.user.email').d('邮箱')}
            >
              {form.getFieldDecorator('email', {
                initialValue: initialValue.email,
                rules: [
                  {
                    pattern: EMAIL,
                    message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                ],
              })(<Input disabled={readOnly} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
              hasFeedback
              help={
                // eslint-disable-next-line no-nested-ternary
                phoneError
                  ? join(phoneError)
                  : initialValue.phoneCheckFlag
                  ? ''
                  : intl.get('hiam.subAccount.view.validation.phoneNotCheck').d('手机号码未验证')
              }
              validateStatus={
                // eslint-disable-next-line no-nested-ternary
                phoneError
                  ? 'error'
                  : samePhone && initialValue.phoneCheckFlag
                  ? 'success'
                  : 'warning'
              }
            >
              {form.getFieldDecorator('phone', {
                initialValue: initialValue.phone,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
                    }),
                  },
                  {
                    pattern:
                      form.getFieldValue('internationalTelCode') === '+86'
                        ? PHONE
                        : NOT_CHINA_PHONE,
                    message: intl.get('hzero.common.validation.phone').d('手机格式不正确'),
                  },
                ],
              })(
                <Input
                  disabled={readOnly}
                  addonBefore={form.getFieldDecorator('internationalTelCode', {
                    initialValue:
                      initialValue.internationalTelCode || (idd[0] && idd[0].value) || '+86',
                  })(
                    <Select onChange={this.reValidationPhone} disabled={readOnly}>
                      {map(idd, (r) => (
                        <Select.Option key={r.value} value={r.value}>
                          {r.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.from').d('有效日期从')}
            >
              {form.getFieldDecorator('startDateActive', {
                initialValue: initialValue.startDateActive && moment(initialValue.startDateActive),
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hiam.subAccount.model.subAccount.startDateActive')
                        .d('有效日期从'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  allowClear={false}
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabled={readOnly}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('endDateActive') &&
                    moment(form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.to').d('有效日期至')}
            >
              {form.getFieldDecorator('endDateActive', {
                initialValue: initialValue.endDateActive && moment(initialValue.endDateActive),
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabled={readOnly}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('startDateActive') &&
                    moment(form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 渲染表单
   * 根据 isCreate 选择 渲染不同的表单
   * @return
   */
  @Bind()
  renderForm() {
    const { isCreate } = this.props;
    if (isCreate) {
      return this.renderCreateForm();
    }
    return this.renderEditForm();
  }

  render() {
    return (
      <>
        <Form>{this.renderForm()}</Form>
      </>
    );
  }
}
