/*
 * AuthenticationServiceModal - 认证弹窗
 * @date: 2018-10-25
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Modal, Form, Input, Row, Col, Select } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

const FormItem = Form.Item;
const { Option } = Select;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class AuthenticationServiceModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isGrantTypeActive: false,
    };
    this.cancel = this.cancel.bind(this);
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  @Bind()
  ok() {
    const {
      onOk = (e) => e,
      form: { validateFields },
    } = this.props;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        onOk(values);
      }
    });
    this.cancel();
  }

  @Bind()
  cancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel();
  }

  @Bind()
  handleChangeAuthType() {
    const {
      form: { setFieldsValue },
    } = this.props;
    setFieldsValue({ grantType: undefined });
  }

  defaultRowkey = 'id';

  @Bind()
  onGrantTypeChange() {
    const {
      form: { setFieldsValue },
    } = this.props;

    setFieldsValue({
      accessTokenUrl: undefined,
      authUsername: undefined,
      authPassword: undefined,
      clientId: undefined,
      clientSecret: undefined,
    });
    this.setState({
      isGrantTypeActive: true,
    });
  }

  render() {
    const {
      title,
      visible,
      onCancel,
      onOk,
      dataSource,
      loading,
      defaultSelectedRow,
      form: { getFieldDecorator = (e) => e, getFieldValue },
      authTypes,
      grantTypes,
      ...others
    } = this.props;
    const { isGrantTypeActive = false } = this.state;

    const {
      authType,
      grantType,
      authUsername,
      authPassword,
      clientSecret,
      clientId,
      accessTokenUrl,
    } = dataSource;
    // 因为不能清空, 所以如果表单有值 就是表单的值, 否则是 初始值
    const curAuthType = getFieldValue('authType');
    const curGrantType = getFieldValue('grantType');
    const onlyNamePwd =
      curAuthType === 'BASIC' || (curAuthType === 'OAUTH2' && curGrantType === 'PASSWORD'); // 只能输用户名和密码的条件
    const onlyAuthClient = curAuthType === 'OAUTH2' && curGrantType === 'CLIENT'; // 只能填除用户名和密码的情况
    return (
      <Modal
        title={intl.get('hitf.services.view.message.title.editor.authConfig').d('服务认证配置')}
        visible={visible}
        onOk={this.ok.bind(this)}
        onCancel={this.cancel.bind(this)}
        destroyOnClose
        width={680}
        {...others}
      >
        <Form>
          <Row>
            <Col span={12}>
              <FormItem
                label={intl.get('hitf.services.model.services.authType').d('认证模式')}
                {...formLayout}
              >
                {getFieldDecorator('authType', {
                  initialValue: authType || 'NONE',
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hitf.services.model.services.authType').d('认证模式'),
                      }),
                    },
                  ],
                })(
                  <Select onChange={this.handleChangeAuthType}>
                    {authTypes.map((item) => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            {getFieldValue('authType') === 'OAUTH2' && (
              <Col span={12}>
                <FormItem
                  label={intl.get('hitf.services.model.services.grantType').d('授权模式')}
                  {...formLayout}
                >
                  {getFieldDecorator('grantType', {
                    initialValue: authType === 'OAUTH2' ? grantType : undefined,
                  })(
                    <Select onChange={this.onGrantTypeChange}>
                      {grantTypes.map((n) => (
                        <Option key={n.value} value={n.value}>
                          {n.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            )}
          </Row>
          <Row>
            {onlyNamePwd && (
              <Col span={12}>
                <FormItem
                  label={intl.get('hitf.services.model.services.authUsername').d('认证用户名')}
                  {...formLayout}
                >
                  {getFieldDecorator('authUsername', {
                    initialValue: !isGrantTypeActive ? authUsername : null,
                  })(<Input />)}
                </FormItem>
              </Col>
            )}
            {onlyNamePwd && (
              <Col span={12}>
                <FormItem
                  label={intl.get('hitf.services.model.services.authPassword').d('认证密码')}
                  {...formLayout}
                >
                  {getFieldDecorator('authPassword', {
                    initialValue: !isGrantTypeActive ? authPassword : null,
                  })(<Input />)}
                </FormItem>
              </Col>
            )}
          </Row>
          <Row>
            {onlyAuthClient && (
              <Col span={12}>
                <FormItem
                  label={intl.get('hitf.services.model.services.clientId').d('客户端ID')}
                  {...formLayout}
                >
                  {getFieldDecorator('clientId', {
                    initialValue: !isGrantTypeActive ? clientId : null,
                  })(<Input />)}
                </FormItem>
              </Col>
            )}
            {onlyAuthClient && (
              <Col span={12}>
                <FormItem
                  label={intl.get('hitf.services.model.services.clientSecret').d('客户端密钥')}
                  {...formLayout}
                >
                  {getFieldDecorator('clientSecret', {
                    initialValue: !isGrantTypeActive ? clientSecret : null,
                  })(<Input />)}
                </FormItem>
              </Col>
            )}
          </Row>
          <Row>
            {onlyAuthClient && (
              <Col span={18}>
                <FormItem
                  label={intl
                    .get('hitf.services.model.services.accessTokenUrl')
                    .d('获取Token的URL')}
                  {...formLayout}
                >
                  {getFieldDecorator('accessTokenUrl', {
                    initialValue: !isGrantTypeActive ? accessTokenUrl : null,
                  })(<Input />)}
                </FormItem>
              </Col>
            )}
          </Row>
        </Form>
      </Modal>
    );
  }
}
