import React from 'react';
import { Col, Form, Input, Modal, Row, Select, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE, CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Editor extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      channelCodes: '',
    };
  }

  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { agentAppId, miniAppId, ...others } = fieldsValue;
        onOk({ ...others, extParam: JSON.stringify({ agentAppId, miniAppId }) });
      }
    });
  }

  @Bind
  channelCodeChange(code) {
    const { form } = this.props;
    form.resetFields(['privateKey', 'publicKey']);
    this.setState({ channelCodes: code });
    if (code === 'alipay') {
      form.setFields({ mchId: { value: '', errors: null } });
    }
  }

  @Bind()
  channelRender(code) {
    const { form, initData } = this.props;
    const { getFieldDecorator } = form;
    let itemDom;
    switch (code) {
      case 'alipay': {
        const { appId, seller, mchId, publicKey, privateKey } = initData;
        itemDom = (
          <>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.appId').d('APPID')}
              >
                {getFieldDecorator('appId', {
                  initialValue: appId,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.appId').d('APPID'),
                      }),
                    },
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
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.mchId').d('合作商ID')}
              >
                {getFieldDecorator('mchId', {
                  initialValue: mchId,
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.seller').d('收款账号')}
              >
                {getFieldDecorator('seller', {
                  initialValue: seller,
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.alipublicKey').d('支付宝公钥')}
              >
                {getFieldDecorator('publicKey', {
                  initialValue: publicKey,
                })(
                  <Input.TextArea
                    autosize={{ minRows: 3, maxRows: 6 }}
                    placeholder={intl.get('hpay.payConfig.view.validation.notChange').d('未更改')}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.privateKey').d('私钥')}
              >
                {getFieldDecorator('privateKey', {
                  initialValue: privateKey,
                })(
                  <Input.TextArea
                    autosize={{ minRows: 3, maxRows: 6 }}
                    placeholder={intl.get('hpay.payConfig.view.validation.notChange').d('未更改')}
                  />
                )}
              </FormItem>
            </Col>
          </>
        );
        return itemDom;
      }
      case 'wxpay': {
        const {
          appId,
          extParam = '{}',
          mchId,
          publicKey,
          privateKey,
          certPwd,
          // privateCert,
          signType,
        } = initData;
        const { agentAppId = '', miniAppId = '' } = JSON.parse(extParam);
        const signTypeList = [
          { value: 'MD5', meaning: 'MD5' },
          { value: 'HMACSHA256', meaning: 'HMACSHA256' },
        ];
        itemDom = (
          <>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.appId').d('APPID')}
              >
                {getFieldDecorator('appId', {
                  initialValue: appId,
                  rules: [
                    {
                      type: 'string',
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.appId').d('APPID'),
                      }),
                    },
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
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.mchId').d('合作商ID')}
              >
                {getFieldDecorator('mchId', {
                  initialValue: mchId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.mchId').d('合作商ID'),
                      }),
                    },
                  ],
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.agentAppId').d('子应用ID')}
              >
                {getFieldDecorator('agentAppId', {
                  initialValue: agentAppId,
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.miniAppId').d('小程序ID')}
              >
                {getFieldDecorator('miniAppId', {
                  initialValue: miniAppId,
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.publicKey').d('转账公钥')}
              >
                {getFieldDecorator('publicKey', {
                  initialValue: publicKey,
                })(
                  <Input.TextArea
                    autosize={{ minRows: 3, maxRows: 6 }}
                    placeholder={intl.get('hpay.payConfig.view.validation.notChange').d('未更改')}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.privateKey').d('私钥')}
              >
                {getFieldDecorator('privateKey', {
                  initialValue: privateKey,
                })(
                  <Input.TextArea
                    autosize={{ minRows: 3, maxRows: 6 }}
                    placeholder={intl.get('hpay.payConfig.view.validation.notChange').d('未更改')}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.signType').d('签名方式')}
              >
                {getFieldDecorator('signType', {
                  initialValue: signType,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.signType').d('签名方式'),
                      }),
                    },
                  ],
                })(
                  <Select>
                    {signTypeList.map(item => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.certPwd').d('证书密码')}
              >
                {getFieldDecorator('certPwd', {
                  initialValue: certPwd,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.certPwd').d('证书密码'),
                      }),
                    },
                    {
                      max: 110,
                      message: intl.get('hzero.common.validation.max', {
                        max: 110,
                      }),
                    },
                  ],
                })(<Input type="password" trim />)}
              </FormItem>
            </Col>
          </>
        );
        return itemDom;
      }
      case 'unionpay': {
        const { mchId, certPwd } = initData;
        itemDom = (
          <>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.mchId').d('合作商ID')}
              >
                {getFieldDecorator('mchId', {
                  initialValue: mchId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.mchId').d('合作商ID'),
                      }),
                    },
                  ],
                })(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpay.payConfig.model.payConfig.certPwd').d('证书密码')}
              >
                {getFieldDecorator('certPwd', {
                  initialValue: certPwd,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpay.payConfig.model.payConfig.certPwd').d('证书密码'),
                      }),
                    },
                  ],
                })(<Input type="password" trim />)}
              </FormItem>
            </Col>
          </>
        );
        return itemDom;
      }
      default:
        break;
    }
  }

  render() {
    const {
      form,
      initData = {},
      title = '',
      visible = false,
      initLoading = false,
      loading = false,
      channelCodeList = [],
      onCancel = e => e,
    } = this.props;
    const { channelCodes } = this.state;
    const { getFieldDecorator } = form;
    const {
      channelCode,
      configCode,
      configName,
      returnUrl,
      payNotifyUrl,
      refundNotifyUrl,
      defaultFlag = 1,
      enabledFlag = 1,
      remark,
    } = initData;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width="1000px"
        visible={visible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={initLoading}>
          <Form>
            <Row>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.payConfig.model.payConfig.channelCode').d('支付渠道')}
                >
                  {getFieldDecorator('channelCode', {
                    initialValue: channelCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpay.payConfig.model.payConfig.channelCode')
                            .d('支付渠道'),
                        }),
                      },
                    ],
                  })(
                    <Select onChange={this.channelCodeChange} disabled={channelCode !== undefined}>
                      {channelCodeList.map(item => (
                        <Select.Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.payConfig.model.payConfig.configCode').d('支付配置编码')}
                >
                  {getFieldDecorator('configCode', {
                    initialValue: configCode,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpay.payConfig.model.payConfig.configCode')
                            .d('支付配置编码'),
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
                  })(
                    <Input
                      trim
                      typeCase="upper"
                      inputChinese={false}
                      disabled={configCode !== undefined}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.payConfig.model.payConfig.configName').d('支付配置名称')}
                >
                  {getFieldDecorator('configName', {
                    initialValue: configName,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpay.payConfig.model.payConfig.configName')
                            .d('支付配置名称'),
                        }),
                      },
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                  })(<Input trim />)}
                </FormItem>
              </Col>
              {this.channelRender(channelCode || channelCodes)}
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.payConfig.model.payConfig.payNotifyUrl').d('支付回调地址')}
                >
                  {getFieldDecorator('payNotifyUrl', {
                    initialValue: payNotifyUrl,
                    rules: [
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                  })(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.payConfig.model.payConfig.refundNotifyUrl')
                    .d('退款回调地址')}
                >
                  {getFieldDecorator('refundNotifyUrl', {
                    initialValue: refundNotifyUrl,
                    rules: [
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                  })(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.payConfig.model.payConfig.returnUrl').d('页面回调地址')}
                >
                  {getFieldDecorator('returnUrl', {
                    initialValue: returnUrl,
                    rules: [
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                  })(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hzero.common.remark').d('备注')}
                >
                  {getFieldDecorator('remark', {
                    initialValue: remark,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.payConfig.model.payConfig.defaultFlag').d('默认标识')}
                >
                  {getFieldDecorator('defaultFlag', {
                    initialValue: defaultFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hzero.common.status').d('状态')}
                >
                  {getFieldDecorator('enabledFlag', {
                    initialValue: enabledFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
