/**
 * Drawer - 二级域名单点登录配置-抽屉
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Spin, Input, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { CODE } from 'utils/regExp';

const FormItem = Form.Item;
const { Option } = Select;
const PAAS = 'PAAS';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: true,
      ouathVisible: true,
    };
  }

  @Bind()
  handleOk() {
    const { form, onOk, initData, tenantId } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const fieldValues = {
          tenantId,
          ...initData,
          ...fieldsValue,
        };
        if (fieldValues.ssoTypeCode === 'NULL') {
          delete fieldValues.ssoServerUrl;
          delete fieldValues.ssoLoginUrl;
          delete fieldValues.clientHostUrl;
        } else if (fieldsValue.ssoTypeCode !== 'AUTH' && fieldsValue.ssoTypeCode !== PAAS) {
          delete fieldValues.ssoClientId;
          delete fieldValues.ssoClientPwd;
          delete fieldValues.ssoUserInfo;
        }
        if (!fieldsValue.loginNameField) {
          delete fieldValues.loginNameField;
        }
        onOk(fieldValues, initData);
        this.setState({ visible: true, ouathVisible: true });
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    this.setState({ visible: true, ouathVisible: true });

    onCancel();
  }

  render() {
    const {
      modalVisible,
      title,
      loading,
      initLoading,
      form,
      typeList,
      editflag,
      initData = {},
    } = this.props;
    const { visible, ouathVisible } = this.state;
    const { getFieldDecorator } = form;
    const {
      domainUrl,
      ssoTypeCode,
      samlMetaUrl,
      ssoServerUrl,
      ssoLoginUrl,
      ssoLogoutUrl,
      clientHostUrl,
      ssoClientId,
      ssoClientPwd,
      ssoUserInfo,
      loginNameField,
    } = initData;
    return (
      <Modal
        destroyOnClose
        title={title}
        width={800}
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={initLoading}>
          <Form>
            <Row>
              <Col>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.domainUrl').d('单点登录域名')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('domainUrl', {
                    initialValue: domainUrl,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.ssoConfig.model.ssoConfig.domainUrl')
                            .d('单点登录域名'),
                        }),
                      },
                      {
                        pattern: /(http|https):\/\/[\w\-_]/,
                        message: intl
                          .get('hzero.common.validation.httpUrl')
                          .d('请输入以“http/https”开头的正确网址'),
                      },
                    ],
                  })(<Input trim inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.ssoTypeCode').d('单点登录类型')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('ssoTypeCode', {
                    initialValue: ssoTypeCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.ssoConfig.model.ssoConfig.ssoTypeCode')
                            .d('单点登录类型'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      allowClear
                      onChange={(val) => {
                        if (val === 'NULL') {
                          this.setState({ visible: false, ouathVisible: !editflag });
                        } else if (val === 'AUTH' || val === PAAS) {
                          this.setState({ visible: true, ouathVisible: !!editflag });
                        } else if (val === 'SAML') {
                          this.setState({ visible: false, ouathVisible: !editflag });
                        } else {
                          this.setState({ visible: true, ouathVisible: !editflag });
                        }
                      }}
                    >
                      {typeList.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              {form.getFieldValue('ssoTypeCode') === 'SAML' && (
                <Col>
                  <FormItem
                    label={intl
                      .get('hiam.ssoConfig.model.ssoConfig.samlMetaUrl')
                      .d('SAML元数据地址')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('samlMetaUrl', {
                      initialValue: samlMetaUrl,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hiam.ssoConfig.model.ssoConfig.samlMetaUrl')
                              .d('SAML元数据地址'),
                          }),
                        },
                        {
                          max: 2550,
                          message: intl.get('hzero.common.validation.max', {
                            max: 2550,
                          }),
                        },
                        {
                          pattern: /(http|https):\/\/[\w\-_]/,
                          message: intl
                            .get('hzero.common.validation.httpUrl')
                            .d('请输入以“http/https”开头的正确网址'),
                        },
                      ],
                    })(<Input trim inputChinese={false} />)}
                  </FormItem>
                </Col>
              )}
              {form.getFieldValue('ssoTypeCode') !== 'NULL' &&
                form.getFieldValue('ssoTypeCode') !== 'SAML' &&
                visible && (
                  <>
                    <Col>
                      <FormItem
                        label={intl
                          .get('hiam.ssoConfig.model.ssoConfig.ssoServerUrl')
                          .d('单点登录服务器地址')}
                        {...MODAL_FORM_ITEM_LAYOUT}
                      >
                        {getFieldDecorator('ssoServerUrl', {
                          initialValue: ssoServerUrl,
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hiam.ssoConfig.model.ssoConfig.ssoServerUrl')
                                  .d('单点登录服务器地址'),
                              }),
                            },
                            {
                              max: 2550,
                              message: intl.get('hzero.common.validation.max', {
                                max: 2550,
                              }),
                            },
                            {
                              pattern: /(http|https):\/\/[\w\-_]/,
                              message: intl
                                .get('hzero.common.validation.httpUrl')
                                .d('请输入以“http/https”开头的正确网址'),
                            },
                          ],
                        })(<Input trim inputChinese={false} />)}
                      </FormItem>
                    </Col>
                    <Col>
                      <FormItem
                        label={intl
                          .get('hiam.ssoConfig.model.ssoConfig.ssoLoginUrl')
                          .d('单点登录地址')}
                        {...MODAL_FORM_ITEM_LAYOUT}
                      >
                        {getFieldDecorator('ssoLoginUrl', {
                          initialValue: ssoLoginUrl,
                          rules: [
                            form.getFieldValue('ssoTypeCode') !== 'IDM' && {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hiam.ssoConfig.model.ssoConfig.ssoLoginUrl')
                                  .d('单点登录地址'),
                              }),
                            },
                            {
                              max: 2550,
                              message: intl.get('hzero.common.validation.max', {
                                max: 2550,
                              }),
                            },
                            {
                              pattern: /(http|https):\/\/[\w\-_]/,
                              message: intl
                                .get('hzero.common.validation.httpUrl')
                                .d('请输入以“http/https”开头的正确网址'),
                            },
                          ].filter(Boolean),
                        })(<Input trim inputChinese={false} />)}
                      </FormItem>
                    </Col>
                    <Col>
                      <FormItem
                        label={intl
                          .get('hiam.ssoConfig.model.ssoConfig.ssoLogoutUrl')
                          .d('单点登出地址')}
                        {...MODAL_FORM_ITEM_LAYOUT}
                      >
                        {getFieldDecorator('ssoLogoutUrl', {
                          initialValue: ssoLogoutUrl,
                          rules: [
                            form.getFieldValue('ssoTypeCode') !== 'IDM' && {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hiam.ssoConfig.model.ssoConfig.ssoLogoutUrl')
                                  .d('单点登出地址'),
                              }),
                            },
                            {
                              pattern: /(http|https):\/\/[\w\-_]/,
                              message: intl
                                .get('hzero.common.validation.httpUrl')
                                .d('请输入以“http/https”开头的正确网址'),
                            },
                          ].filter(Boolean),
                        })(<Input trim inputChinese={false} />)}
                      </FormItem>
                    </Col>
                    <Col>
                      <FormItem
                        label={intl
                          .get('hiam.ssoConfig.model.ssoConfig.clientHostUrl')
                          .d('客户端地址')}
                        {...MODAL_FORM_ITEM_LAYOUT}
                      >
                        {getFieldDecorator('clientHostUrl', {
                          initialValue: clientHostUrl,
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hiam.ssoConfig.model.ssoConfig.clientHostUrl')
                                  .d('客户端地址'),
                              }),
                            },
                            {
                              max: 2550,
                              message: intl.get('hzero.common.validation.max', {
                                max: 2550,
                              }),
                            },
                            {
                              pattern: /(http|https):\/\/[\w\-_]/,
                              message: intl
                                .get('hzero.common.validation.httpUrl')
                                .d('请输入以“http/https”开头的正确网址'),
                            },
                          ],
                        })(<Input trim inputChinese={false} />)}
                      </FormItem>
                    </Col>
                  </>
                )}
              {(editflag
                ? ['AUTH', PAAS].includes(form.getFieldValue('ssoTypeCode')) && ouathVisible
                : !ouathVisible) && (
                <>
                  <Col>
                    <FormItem
                      label={intl
                        .get('hiam.ssoConfig.model.ssoConfig.ssoClientId')
                        .d('oAuth认证ClientId')}
                      {...MODAL_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('ssoClientId', {
                        initialValue: ssoClientId,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hiam.ssoConfig.model.ssoConfig.ssoClientId')
                                .d('oAuth认证ClientId'),
                            }),
                          },
                          {
                            max: 255,
                            message: intl.get('hzero.common.validation.max', {
                              max: 255,
                            }),
                          },
                        ],
                      })(<Input trim />)}
                    </FormItem>
                  </Col>
                  <Col>
                    <FormItem
                      label={intl
                        .get('hiam.ssoConfig.model.ssoConfig.ssoClientPwd')
                        .d('oAuth认证Client密码')}
                      {...MODAL_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('ssoClientPwd', {
                        initialValue: ssoClientPwd,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hiam.ssoConfig.model.ssoConfig.ssoClientPwd')
                                .d('oAuth认证Client密码'),
                            }),
                          },
                          {
                            max: 255,
                            message: intl.get('hzero.common.validation.max', {
                              max: 255,
                            }),
                          },
                        ],
                      })(<Input trim inputChinese={false} />)}
                    </FormItem>
                  </Col>
                  <Col>
                    <FormItem
                      label={intl
                        .get('hiam.ssoConfig.model.ssoConfig.ssoUserInfo')
                        .d('oAuth认证用户信息')}
                      {...MODAL_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('ssoUserInfo', {
                        initialValue: ssoUserInfo,
                        rules: [
                          {
                            max: 2550,
                            message: intl.get('hzero.common.validation.max', {
                              max: 2550,
                            }),
                          },
                          {
                            pattern: /(http|https):\/\/[\w\-_]/,
                            message: intl
                              .get('hzero.common.validation.httpUrl')
                              .d('请输入以“http/https”开头的正确网址'),
                          },
                        ],
                      })(<Input trim inputChinese={false} />)}
                    </FormItem>
                  </Col>
                </>
              )}
              <Col>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.loginNameField').d('登录名属性')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('loginNameField', {
                    initialValue: loginNameField,
                    rules: [
                      {
                        max: 60,
                        message: intl.get('hzero.common.validation.max', {
                          max: 60,
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
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
