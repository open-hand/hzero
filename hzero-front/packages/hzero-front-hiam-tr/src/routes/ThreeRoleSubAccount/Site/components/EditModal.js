/**
 * EditModal.js
 * 当编辑自己的帐号时, 角色时不可以新增和删除的
 * @date 2018-12-16
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Col, DatePicker, Form, Input, Modal, Row, Select, Spin, Tooltip, Icon } from 'hzero-ui';
import { isUndefined, join, map } from 'lodash';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import PropTypes from 'prop-types';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getDateFormat } from 'utils/utils';
import { EMAIL, NOT_CHINA_PHONE, PHONE, CODE } from 'utils/regExp';
import { DEFAULT_DATE_FORMAT, FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { validatePasswordRule } from '@/utils/validator';

@Form.create({ fieldNameProp: null })
export default class EditModal extends React.Component {
  // todo 最后 页面的 propTypes 推荐 全部删掉
  static propTypes = {
    onOk: PropTypes.func.isRequired,
  };

  componentDidMount() {
    const { isCreate = true, fetchDetailData, detailRecord } = this.props;
    if (!isCreate) {
      fetchDetailData(detailRecord);
    }
  }

  @Bind()
  changeCountryId() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ regionId: undefined });
  }

  @Bind()
  handleEditModalOk() {
    const { form, isCreate } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { editRecord = {}, onOk } = this.props;
        const saveData = {
          ...editRecord,
          ...fieldsValue,
          startDateActive: fieldsValue.startDateActive.format(DEFAULT_DATE_FORMAT),
          endDateActive: fieldsValue.endDateActive
            ? fieldsValue.endDateActive.format(DEFAULT_DATE_FORMAT)
            : undefined,
          birthday: fieldsValue.birthday
            ? fieldsValue.birthday.format(DEFAULT_DATE_FORMAT)
            : undefined,
        };
        const newSaveData = isCreate
          ? {
              userType: 'P',
              ...saveData,
            }
          : saveData;
        onOk(newSaveData);
      }
    });
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

  @Bind()
  tenantChange(value, record) {
    const { getPasswordRule } = this.props;
    getPasswordRule(record.tenantId);
  }

  renderForm() {
    const {
      // path,
      form,
      editRecord = {},
      isCreate = true,
      // isAdmin = true,
      // roleRemoveLoading,
      idd = [],
      gender = [],
      // currentUser: { currentRoleCode = '' },
      passwordTipMsg = {},
    } = this.props;
    const emailError = form.getFieldError('email');
    const sameEmail = editRecord.email === form.getFieldValue('email');
    const phoneError = form.getFieldError('phone');
    const samePhone = editRecord.phone === form.getFieldValue('phone');
    const dateFormat = getDateFormat();

    return (
      <Form>
        <Row type="flex">
          <Col key="loginName" {...FORM_COL_2_LAYOUT}>
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
                initialValue: editRecord.loginName,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  isCreate && {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ].filter(Boolean),
              })(<Input disabled={!isCreate} />)}
            </Form.Item>
          </Col>
          <Col key="realName" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
            >
              {form.getFieldDecorator('realName', {
                initialValue: editRecord.realName,
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
              {form.getFieldDecorator('birthday', {
                initialValue: editRecord.birthday
                  ? moment(editRecord.birthday, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(<DatePicker format={dateFormat} style={{ width: '100%' }} placeholder="" />)}
            </Form.Item>
          </Col>
          <Col key="nickname" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.nickname').d('昵称')}
            >
              {form.getFieldDecorator('nickname', {
                initialValue: editRecord.nickname,
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
              {form.getFieldDecorator('gender', {
                initialValue: isUndefined(editRecord.gender) ? '' : `${editRecord.gender}`,
              })(
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
              {form.getFieldDecorator('countryId', {
                initialValue: editRecord.countryId,
              })(
                <Lov
                  code="HPFM.COUNTRY"
                  onChange={this.changeCountryId}
                  textValue={editRecord.countryName}
                  queryParams={{ enabledFlag: 1 }}
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
                initialValue: editRecord.regionId,
              })(
                <Lov
                  code="HPFM.REGION"
                  queryParams={{
                    countryId: form.getFieldValue('countryId'),
                  }}
                  textValue={editRecord.regionName}
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
                initialValue: editRecord.addressDetail,
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
          {isCreate && (
            <Col key="tenant" {...FORM_COL_2_LAYOUT}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.user.tenant').d('所属租户')}
              >
                {form.getFieldDecorator('organizationId', {
                  initialValue: editRecord.organizationId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.subAccount.model.user.tenant').d('所属租户'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={editRecord.tenantName}
                    textField="tenantName"
                    disabled={!isCreate}
                    onChange={this.tenantChange}
                  />
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_2_LAYOUT} key="email">
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.email').d('邮箱')}
              {...(isCreate
                ? {}
                : {
                    hasFeedback: true,
                    // eslint-disable-next-line no-nested-ternary
                    help: emailError
                      ? join(emailError)
                      : (sameEmail && editRecord.email && editRecord.emailCheckFlag) ||
                        !form.getFieldValue('email')
                      ? ''
                      : intl.get('hiam.subAccount.view.validation.emailNotCheck').d('邮箱未验证'),
                    // eslint-disable-next-line no-nested-ternary
                    validateStatus: emailError
                      ? 'error'
                      : // eslint-disable-next-line no-nested-ternary
                      sameEmail && editRecord.email && editRecord.emailCheckFlag
                      ? 'success'
                      : form.getFieldValue('email')
                      ? 'warning'
                      : undefined,
                  })}
            >
              {form.getFieldDecorator('email', {
                initialValue: editRecord.email,
                rules: [
                  {
                    pattern: EMAIL,
                    message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col key="phone" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
              {...(isCreate
                ? {}
                : {
                    hasFeedback: true,
                    // eslint-disable-next-line no-nested-ternary
                    help: phoneError
                      ? join(phoneError)
                      : editRecord.phoneCheckFlag
                      ? ''
                      : intl
                          .get('hiam.subAccount.view.validation.phoneNotCheck')
                          .d('手机号码未验证'),
                    // eslint-disable-next-line no-nested-ternary
                    validateStatus: phoneError
                      ? 'error'
                      : samePhone && editRecord.phoneCheckFlag
                      ? 'success'
                      : 'warning',
                  })}
            >
              {form.getFieldDecorator('phone', {
                initialValue: editRecord.phone,
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
                    initialValue:
                      editRecord.internationalTelCode || (idd[0] && idd[0].value) || '+86',
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
          {isCreate && (
            <Col key="password" {...FORM_COL_2_LAYOUT}>
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
                    {
                      max: 110,
                      message: intl.get('hzero.common.validation.max', {
                        max: 110,
                      }),
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
          )}
          <Col key="startDateActive" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.from').d('有效日期从')}
            >
              {form.getFieldDecorator('startDateActive', {
                initialValue: editRecord.startDateActive
                  ? moment(editRecord.startDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
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
          <Col key="endDateActive" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              key="endDateActive"
              label={intl.get('hzero.common.date.active.to').d('有效日期至')}
            >
              {form.getFieldDecorator('endDateActive', {
                initialValue: editRecord.endDateActive
                  ? moment(editRecord.endDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder=""
                  disabledDate={(currentDate) =>
                    form.getFieldValue('startDateActive') &&
                    moment(form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          {isCreate || (
            <Col key="tenant" {...FORM_COL_2_LAYOUT}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.user.tenant').d('所属租户')}
              >
                {form.getFieldDecorator('organizationId', {
                  initialValue: editRecord.organizationId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.subAccount.model.user.tenant').d('所属租户'),
                      }),
                    },
                  ],
                })(
                  <Lov code="HPFM.TENANT" textValue={editRecord.tenantName} disabled={!isCreate} />
                )}
              </Form.Item>
            </Col>
          )}
        </Row>
      </Form>
    );
  }

  render() {
    const { isCreate, queryDetailLoading = false, ...modalProps } = this.props;
    return (
      <Modal
        title={
          isCreate
            ? intl.get('hiam.subAccount.view.message.title.userCreate').d('账号新建')
            : intl.get('hiam.subAccount.view.message.title.userEdit').d('账号编辑')
        }
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={1000}
        {...modalProps}
        onOk={this.handleEditModalOk}
      >
        <Spin spinning={queryDetailLoading}>{this.renderForm()}</Spin>
      </Modal>
    );
  }
}
