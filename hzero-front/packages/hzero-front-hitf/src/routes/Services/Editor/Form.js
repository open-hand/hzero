/*
 * Form - 服务注册编辑弹窗
 * @date: 2018-10-25
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Input, Select, Button, Row, Col } from 'hzero-ui';
import { toSafeInteger } from 'lodash';

import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT, FORM_COL_2_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class EditorForm extends React.Component {
  state = {};

  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  parserSort(value) {
    return toSafeInteger(value);
  }

  handleResetClientKey() {
    const {
      resetClientKey = (e) => e,
      form: { getFieldsValue },
    } = this.props;
    resetClientKey(getFieldsValue(['clientId', 'clientKey']));
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e, getFieldValue },
      editable,
      dataSource = {},
      tenantRoleLevel,
      onTypeChange = (e) => e,
      changeListTenant,
      openAuthenticationServiceModal = (e) => e,
      serviceTypes,
      wssPasswordTypes,
    } = this.props;

    // const { dirModelVisible, currentParentDir, dirModelDataSource } = this.state;
    const {
      serverCode,
      tenantId,
      realName,
      serviceType,
      domainUrl,
      serverName,
      soapNamespace,
      soapElementPrefix,
      soapUsername,
      soapPassword,
      enabledFlag,
      soapWssPasswordType,
    } = dataSource;

    return (
      <Form>
        <Row>
          {!tenantRoleLevel && (
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.services.model.services.tenant').d('所属租户')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hitf.services.model.services.tenant').d('所属租户'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    textValue={realName || ''}
                    code="HPFM.TENANT"
                    onChange={changeListTenant}
                    disabled={editable}
                  />
                )}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.code').d('服务代码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serverCode', {
                initialValue: serverCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.code').d('服务代码'),
                    }),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} disabled={editable} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.name').d('服务名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serverName', {
                initialValue: serverName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.name').d('服务名称'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.type').d('服务类型')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serviceType', {
                initialValue: serviceType,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.type').d('服务类型'),
                    }),
                  },
                ],
              })(
                <Select onChange={onTypeChange}>
                  {serviceTypes.map((item) => (
                    <Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.address').d('服务地址')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('domainUrl', {
                initialValue: domainUrl,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.address').d('服务地址'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          {/* 服务类型为SOAP：显示命名空间、参数前缀、加密类型 */}
          {getFieldValue('serviceType') === 'SOAP' && (
            <>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hitf.services.model.services.nameSpace').d('命名空间')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('soapNamespace', {
                    initialValue: soapNamespace,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hitf.services.model.services.paramPrefix').d('参数前缀')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('soapElementPrefix', {
                    initialValue: soapElementPrefix,
                    rules: [
                      {
                        max: 30,
                        message: intl.get('hzero.common.validation.max', {
                          max: 30,
                        }),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hitf.services.model.services.encryptionType').d('加密类型')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('soapWssPasswordType', {
                    initialValue: soapWssPasswordType,
                  })(
                    <Select>
                      {wssPasswordTypes.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </>
          )}
          {/* 加密类型不为None：显示校验用户名、校验密码 */}
          {getFieldValue('soapWssPasswordType') !== 'None' &&
            getFieldValue('serviceType') === 'SOAP' && (
              <>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hitf.services.model.services.userName').d('校验用户名')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('soapUsername', {
                      initialValue: soapUsername,
                    })(<Input />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hitf.services.model.services.password').d('校验密码')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('soapPassword', {
                      initialValue: soapPassword,
                    })(<Input />)}
                  </FormItem>
                </Col>
              </>
            )}
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem>
              <Button onClick={openAuthenticationServiceModal}>
                {intl.get('hitf.services.view.button.authConfig').d('服务认证配置')}
              </Button>
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem label={intl.get('hzero.common.status').d('状态')} {...MODAL_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag === 0 ? 0 : 1,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
