import React, { PureComponent } from 'react';
import {
  Button,
  Col,
  Collapse,
  Form,
  Icon,
  Input,
  Radio,
  Row,
  Select,
  Tooltip,
  InputNumber,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import {
  DETAIL_DEFAULT_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const { Panel } = Collapse;
const RadioGroup = Radio.Group;

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleAdd - 添加确定的回调函数
 * @reactProps {Function} handleEdit - 编辑确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class LDAPForm extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      collapseKeys: ['service', 'userInfo'],
    };
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

  /* ssl修改状态默认端口号更改 */
  @Bind()
  handleSSLChange() {
    const { setFieldsValue, getFieldValue } = this.props.form;
    setFieldsValue({
      port: getFieldValue('useSSL') === 'Y' ? '389' : '636',
    });
  }

  // 保存并测试
  @Bind()
  handleSave() {
    const { form, onSaveAndTest } = this.props;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSaveAndTest(values);
      }
    });
  }

  // 取消
  @Bind()
  handleCancel() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { ldapData, updateLoading, directoryTypeList = [] } = this.props;
    const {
      path,
      form: { getFieldDecorator },
    } = this.props;
    const { collapseKeys } = this.state;

    return (
      <Form className={DETAIL_DEFAULT_CLASSNAME}>
        <Collapse
          className="form-collapse"
          defaultActiveKey={['service', 'userInfo']}
          onChange={this.onCollapseChange}
        >
          <Panel
            showArrow={false}
            header={
              <>
                <h3>{intl.get('hiam.ldap.view.message.serviceSet').d('服务器设置')}</h3>
                <a>
                  {collapseKeys.includes('service')
                    ? intl.get(`hzero.common.button.up`).d('收起')
                    : intl.get(`hzero.common.button.expand`).d('展开')}
                </a>
                <Icon type={collapseKeys.includes('service') ? 'up' : 'down'} />
              </>
            }
            key="service"
          >
            <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.directoryType').d('目录类型')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('directoryType', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.directoryType').d('目录类型'),
                        }),
                      },
                    ],
                    initialValue: ldapData.directoryType,
                  })(
                    <Select onChange={this.changeDataSourceType}>
                      {directoryTypeList.map((item) => (
                        <Select.Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  // label={intl.get('hiam.ldap.model.ldap.serverAddress').d('主机名')}
                  label={
                    <span>
                      {intl.get('hiam.ldap.model.ldap.serverAddress').d('主机名')}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.ldap.view.message.serverAddress.help.msg')
                          .d('运行 LDAP 的服务器主机名。例如：ldap://example.com')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('serverAddress', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.serverAddress').d('主机名'),
                        }),
                      },
                    ],
                    initialValue: ldapData.serverAddress,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...EDIT_FORM_ITEM_LAYOUT}
                  // label={intl.get('hiam.ldap.model.ldap.useSSL').d('是否使用SSL')}
                  label={
                    <span>
                      {intl.get('hiam.ldap.model.ldap.useSSL').d('是否使用SSL')}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.ldap.view.message.useSSL.help.msg')
                          .d('是否使用SSL会对端口号有影响')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {getFieldDecorator('useSSL', {
                    initialValue: ldapData.useSSL ? 'Y' : 'N', // isUndefined(ldapData.useSSL)?'false': ldapData.useSSL,
                  })(
                    <RadioGroup onChange={this.handleSSLChange}>
                      <Radio value="Y">{intl.get('hzero.common.status.yes').d('是')}</Radio>
                      <Radio value="N">{intl.get('hzero.common.status.no').d('否')}</Radio>
                    </RadioGroup>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.port').d('端口号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('port', {
                    initialValue: ldapData.port || (ldapData.useSSL ? '636' : '389'),
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.sagaBatchSize').d('分页同步用户数')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('sagaBatchSize', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.sagaBatchSize').d('分页同步用户数'),
                        }),
                      },
                    ],
                    initialValue: ldapData.sagaBatchSize,
                  })(<InputNumber min={1} step={1} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hiam.ldap.model.ldap.connectionTimeout')
                    .d('ldap服务器连接超时时间')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('connectionTimeout', {
                    initialValue: ldapData.connectionTimeout,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.ldap.model.ldap.connectionTimeout')
                            .d('ldap服务器连接超时时间'),
                        }),
                      },
                    ],
                  })(
                    <InputNumber
                      min={1}
                      step={1}
                      formatter={(value) => `${value}s`}
                      parser={(value) => value.replace('s', '')}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT} className="writable-row">
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  // label={intl.get('hiam.ldap.model.ldap.baseDn').d('基准DN')}
                  label={
                    <span>
                      {intl.get('hiam.ldap.model.ldap.baseDn').d('基准DN')}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.ldap.view.message.baseDn.help.msg')
                          .d(
                            'LDAP目录树的最顶部的根，从根节点搜索用户。例如：cn=users,dc=example,dc=com'
                          )}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('baseDn', {
                    initialValue: ldapData.baseDn,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.account').d('管理员登录名')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('account', {
                    initialValue: ldapData.account,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.account').d('管理员登录名'),
                        }),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.password').d('管理员密码')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('ldapPassword', {
                    initialValue: ldapData.ldapPassword,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.password').d('管理员密码'),
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
            </Row>
          </Panel>
          <Panel
            showArrow={false}
            header={
              <>
                <h3>{intl.get('hiam.ldap.view.message.userInfoSet').d('用户属性设置')}</h3>
                <a>
                  {collapseKeys.includes('userInfo')
                    ? intl.get(`hzero.common.button.up`).d('收起')
                    : intl.get(`hzero.common.button.expand`).d('展开')}
                </a>
                <Icon type={collapseKeys.includes('userInfo') ? 'up' : 'down'} />
              </>
            }
            key="userInfo"
          >
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.objectClass').d('用户对象类')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('objectClass', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.objectClass').d('用户对象类'),
                        }),
                      },
                    ],
                    initialValue: ldapData.objectClass,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.loginNameField').d('登录名属性')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('loginNameField', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.loginNameField').d('登录名属性'),
                        }),
                      },
                    ],
                    initialValue: ldapData.loginNameField,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.emailField').d('邮箱属性')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('emailField', {
                    initialValue: ldapData.emailField,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ldap.model.ldap.uuidField').d('uuid属性')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('uuidField', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ldap.model.ldap.uuidField').d('uuid属性'),
                        }),
                      },
                    ],
                    initialValue: ldapData.uuidField,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  style={{ marginBottom: '0' }}
                  label={
                    <span>
                      {intl.get('hiam.ldap.model.ldap.realNameField').d('用户名属性')}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.ldap.view.message.realNameField.help.msg')
                          .d('为空时系统将默认获取登录名的值')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('realNameField', {
                    initialValue: ldapData.realNameField,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  style={{ marginBottom: '0' }}
                  label={intl.get('hiam.ldap.model.ldap.phoneField').d('手机号属性')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('phoneField', {
                    initialValue: ldapData.phoneField,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...EDIT_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.ldap.model.ldap.customFilter').d('自定义筛选用户条件')}
                >
                  {getFieldDecorator('customFilter', {
                    initialValue: ldapData.customFilter,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
          </Panel>
        </Collapse>
        <div style={{ float: 'right' }}>
          <Button onClick={this.handleCancel} style={{ marginRight: 8 }}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.saveAndTest`,
                type: 'button',
                meaning: 'LDAP-保存并测试',
              },
            ]}
            type="primary"
            onClick={this.handleSave}
            loading={updateLoading}
          >
            {intl.get('hiam.ldap.button.saveAndTest').d('保存并测试')}
          </ButtonPermission>
        </div>
      </Form>
    );
  }
}
