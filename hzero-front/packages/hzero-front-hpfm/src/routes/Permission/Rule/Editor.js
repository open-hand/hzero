import React, { PureComponent, Fragment } from 'react';
import { Form, Button, Input, notification, Switch, Select } from 'hzero-ui';
import { isEmpty } from 'lodash';

import { Content } from 'components/Page';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

import Drawer from '../Drawer';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;

const formLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
};

@Form.create({ fieldNameProp: null })
export default class DrawerForm extends PureComponent {
  constructor(props) {
    super(props);
    this.update = this.update.bind(this);
    this.create = this.create.bind(this);
    this.cancel = this.cancel.bind(this);
  }

  state = {};

  componentDidMount() {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  }

  update() {
    const {
      form: { validateFields },
      dataSource,
      handleUpdate = (e) => e,
    } = this.props;
    const { cancel } = this;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        handleUpdate({ ...dataSource, ...values, enabledFlag: values.enabledFlag ? 1 : 0 }, () => {
          notification.success({
            message: intl.get('hzero.common.notification.success.save').d('保存成功'),
          });
          cancel();
        });
      }
    });
  }

  create() {
    const {
      form: { validateFields },
      dataSource,
      handleCreate = (e) => e,
    } = this.props;
    const { cancel } = this;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        handleCreate({ ...dataSource, ...values, enabledFlag: values.enabledFlag ? 1 : 0 }, () => {
          notification.success({
            message: intl.get('hzero.common.notification.success.create').d('创建成功'),
          });
          cancel();
        });
      }
    });
  }

  cancel() {
    const {
      onCancel = (e) => e,
      form: { resetFields },
    } = this.props;
    resetFields();
    onCancel();
  }

  render() {
    const {
      visible,
      processing,
      form: { getFieldDecorator = (e) => e, getFieldValue = (e) => e },
      dataSource = {},
      permissionRuleType,
    } = this.props;
    const {
      ruleId,
      ruleCode,
      ruleName,
      _token,
      sqlValue,
      enabledFlag = 1,
      description,
      tenantId,
      ruleTypeCode,
      tenantName,
    } = dataSource;
    const drawerProps = {
      title:
        ruleId !== undefined
          ? intl.get('hpfm.permission.view.option.update').d('修改屏蔽规则')
          : intl.get('hpfm.permission.view.option.add').d('添加屏蔽规则'),
      visible,
      anchor: 'right',
      onCancel: this.cancel,
      footer: (
        <Fragment>
          <Button onClick={this.cancel} disabled={processing}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            loading={processing}
            onClick={() => (ruleId !== undefined ? this.update() : this.create())}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        </Fragment>
      ),
      width: 700,
    };

    return (
      <Drawer {...drawerProps}>
        <div>
          <Content>
            <Form>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.ruleCode').d('规则编码')}
                {...formLayout}
              >
                {getFieldDecorator('ruleCode', {
                  initialValue: ruleCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.permission.model.permission.ruleCode').d('规则编码'),
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
                })(
                  <Input
                    disabled={ruleId !== undefined}
                    inputChinese={false}
                    typeCase="upper"
                    placeholder={intl
                      .get('hpfm.permission.model.permission.onlySupportUpper')
                      .d('仅支持大写')}
                  />
                )}
              </FormItem>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.ruleName').d('规则名称')}
                {...formLayout}
              >
                {getFieldDecorator('ruleName', {
                  initialValue: ruleName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.permission.model.permission.ruleName').d('规则名称'),
                      }),
                    },
                    {
                      max: 120,
                      message: intl.get('hzero.common.validation.max', {
                        max: 120,
                      }),
                    },
                  ],
                })(
                  <TLEditor
                    label={intl.get('hpfm.permission.model.permission.ruleName').d('规则名称')}
                    field="ruleName"
                    token={_token}
                  />
                )}
              </FormItem>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.ruleType').d('规则类型')}
                {...formLayout}
              >
                {getFieldDecorator('ruleTypeCode', {
                  initialValue: ruleTypeCode || 'SQL',
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.permission.model.permission.ruleType').d('规则类型'),
                      }),
                    },
                  ],
                })(
                  <Select disabled={ruleId !== undefined}>
                    {permissionRuleType.map((n) => (
                      <Option key={n.value} value={n.value}>
                        {n.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              {!isTenantRoleLevel() && (
                <FormItem
                  label={intl.get('hpfm.permission.model.permission.tenant').d('租户')}
                  {...formLayout}
                >
                  {getFieldDecorator('tenantId', {
                    initialValue: tenantId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.permission.model.permission.tenant').d('租户'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      disabled={ruleId !== undefined}
                      textValue={tenantName}
                      code="HPFM.TENANT"
                    />
                  )}
                </FormItem>
              )}
              <FormItem
                label={intl.get('hpfm.permission.model.permission.SQL').d('SQL')}
                {...formLayout}
              >
                {getFieldDecorator('sqlValue', {
                  initialValue: sqlValue,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.permission.model.permission.SQL').d('SQL'),
                      }),
                    },
                  ],
                })(<TextArea rows={8} />)}
                {getFieldValue('ruleTypeCode') === 'SQL' ? (
                  <ol style={{ margin: '12px 0 0 10px', padding: 0, color: '#898b96' }}>
                    <li style={{ lineHeight: '15px' }}>
                      {intl
                        .get('hpfm.permission.view.message.ruleType.sqlDescription')
                        .d('动态SQL为一个字段的条件查询,可嵌套SQL.')}
                    </li>
                    <li style={{ lineHeight: '15px' }}>
                      {intl
                        .get('hpfm.permission.view.message.ruleType.sqlParam')
                        .d('支持动态参数#{}（参数必须在CustomUserDetails中）.')}
                      <br />
                      {intl
                        .get('hpfm.permission.view.message.ruleType.sqlParamDemo')
                        .d(
                          '示例：columnA in (select AcolumnB from tableA where AcolumnC = #{varA}'
                        )}
                    </li>
                  </ol>
                ) : (
                  <ul
                    style={{
                      margin: '12px 0 0 0',
                      padding: 0,
                      color: '#898b96',
                      listStyleType: 'none',
                    }}
                  >
                    <li style={{ lineHeight: '15px' }}>
                      {intl
                        .get('hpfm.permission.view.message.ruleType.dataBaseName')
                        .d('请输入数据库名')}
                    </li>
                  </ul>
                )}
              </FormItem>
              <FormItem label={intl.get('hzero.common.status.enable').d('启用')} {...formLayout}>
                {getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag === 1,
                  valuePropName: 'checked',
                })(<Switch />)}
              </FormItem>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.description').d('描述')}
                {...formLayout}
              >
                {getFieldDecorator('description', {
                  initialValue: description,
                  rules: [
                    {
                      max: 480,
                      message: intl.get('hzero.common.validation.max', {
                        max: 480,
                      }),
                    },
                  ],
                })(<Input />)}
              </FormItem>
            </Form>
          </Content>
        </div>
      </Drawer>
    );
  }
}
