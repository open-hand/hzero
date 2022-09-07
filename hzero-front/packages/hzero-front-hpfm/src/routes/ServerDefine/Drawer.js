/**
 * Drawer - 服务器定义-抽屉
 * @date: 2019-7-2
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Spin, Input, Row, Col, Select, InputNumber } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getCurrentOrganizationId, encryptPwd } from 'utils/utils';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';

const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk, initData, flag, publicKey } = this.props;
    form.validateFields((err, fieldsValue) => {
      let error = err;
      if (!flag && error !== null) {
        delete error.loginEncPwd;
        if (JSON.stringify(error) === '{}') {
          error = null;
        }
      }
      if (!error) {
        const { loginEncPwd } = fieldsValue;
        const fieldValues = {
          tenantId: getCurrentOrganizationId(),
          ...initData,
          ...fieldsValue,
          loginEncPwd: flag ? encryptPwd(loginEncPwd, publicKey) : undefined,
        };
        onOk(fieldValues, flag);
        form.resetFields('loginEncPwd');
      }
    });
  }

  render() {
    const {
      modalVisible,
      title = '',
      loading = false,
      initLoading = false,
      initData = {},
      onCancel,
      form,
      typeList,
      flag,
      isSiteFlag,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      serverCode,
      serverName,
      protocolCode,
      serverDescription,
      ip,
      port,
      loginUser,
      enabledFlag,
      loginEncPwd,
      tenantId,
      tenantName,
    } = initData;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={initLoading}>
          <Form>
            <Row>
              {isSiteFlag && (
                <Col>
                  <Form.Item
                    {...MODAL_FORM_ITEM_LAYOUT}
                    label={intl.get('entity.tenant.tag').d('租户')}
                  >
                    {getFieldDecorator('tenantId', {
                      initialValue: tenantId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hzero.common.model.tenantName').d('租户'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code="HPFM.TENANT"
                        textValue={tenantName}
                        disabled={!!tenantId || String(tenantId) === '0'}
                      />
                    )}
                  </Form.Item>
                </Col>
              )}
              <Col>
                <Form.Item
                  label={intl
                    .get('hpfm.serverDefine.model.serverDefine.serverCode')
                    .d('服务器代码')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('serverCode', {
                    initialValue: serverCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.ssoConfig.model.ssoConfig.serverCode')
                            .d('服务器代码'),
                        }),
                      },
                      {
                        pattern: CODE_UPPER,
                        message: intl
                          .get('hzero.common.validation.codeUpper')
                          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                      {
                        max: 30,
                        message: intl.get('hzero.common.validation.max', {
                          max: 30,
                        }),
                      },
                    ],
                  })(<Input trim typeCase="upper" inputChinese={false} disabled={!flag} />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl
                    .get('hpfm.serverDefine.model.serverDefine.serverName')
                    .d('服务器名称')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('serverName', {
                    initialValue: serverName,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.ssoConfig.model.ssoConfig.serverName')
                            .d('服务器名称'),
                        }),
                      },
                      {
                        max: 80,
                        message: intl.get('hzero.common.validation.max', {
                          max: 80,
                        }),
                      },
                    ],
                  })(<Input trim />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl
                    .get('hpfm.serverDefine.model.serverDefine.protocolCode')
                    .d('服务器协议')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('protocolCode', {
                    initialValue: protocolCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.ssoConfig.model.ssoConfig.protocol').d('服务器协议'),
                        }),
                      },
                    ],
                  })(
                    <Select>
                      {typeList.map((item) => {
                        return (
                          <Option label={item.meaning} value={item.value} key={item.value}>
                            {item.meaning}
                          </Option>
                        );
                      })}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl.get('hzero.common.status.ip').d('IP')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('ip', {
                    initialValue: ip,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.ssoConfig.model.ssoConfig.ip').d('IP'),
                        }),
                      },
                      {
                        pattern:
                          '^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.' +
                          '(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.' +
                          '(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.' +
                          '(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$',
                        message: intl.get('hzero.common.validation.ip').d('ip地址不正确'),
                      },
                    ],
                  })(<Input trim />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl.get('hzero.common.status.port').d('端口')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('port', {
                    initialValue: port,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.ssoConfig.model.ssoConfig.port').d('端口'),
                        }),
                      },
                    ],
                  })(<InputNumber trim precision={0} min={1} max={65535} />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl.get('hzero.common.status.loginUser').d('登录用户名')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('loginUser', {
                    initialValue: loginUser,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.ssoConfig.model.ssoConfig.loginUser')
                            .d('登录用户名'),
                        }),
                      },
                      {
                        max: 30,
                        message: intl.get('hzero.common.validation.max', {
                          max: 30,
                        }),
                      },
                    ],
                  })(<Input trim />)}
                </Form.Item>
              </Col>
              {flag && (
                <Col>
                  <Form.Item
                    label={intl.get('hzero.common.status.loginEncPwd').d('登录密码')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('loginEncPwd', {
                      initialValue: loginEncPwd,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.ssoConfig.model.ssoConfig.loginEncPwd')
                              .d('登录密码'),
                          }),
                        },
                        {
                          max: 60,
                          message: intl.get('hzero.common.validation.max', {
                            max: 60,
                          }),
                        },
                      ],
                    })(<Input type="password" trim />)}
                  </Form.Item>
                </Col>
              )}
              <Col>
                <Form.Item
                  label={intl
                    .get('hpfm.serverDefine.model.serverDefine.serverDescription')
                    .d('服务器说明')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('serverDescription', {
                    initialValue: serverDescription,
                    rules: [
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                  })(<Input trim />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl.get('hzero.common.status.enable').d('启用')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('enabledFlag', {
                    initialValue: enabledFlag || 0,
                  })(<Switch />)}
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
