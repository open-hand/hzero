/**
 * audit-config 操作审计配置
 * @date: 2019-7-18
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal, Spin, Table, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil, map } from 'lodash';

import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import styles from './styles.less';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  /**
   * 模态框确认
   */
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  /**
   * 服务Lov改变时，审计接口Lov清空
   */
  @Bind()
  handleChangeServiceName() {
    this.props.form.resetFields('permissionId');
  }

  @Bind()
  tenantChange() {
    const { form } = this.props;
    form.setFieldsValue({ roleId: undefined });
  }

  render() {
    const {
      form,
      initData,
      title,
      organizationId,
      modalVisible,
      loading,
      onCancel,
      initLoading,
      auditTypeList,
      isTenantRoleLevel,
      aboutDataAuditList = [],
      fetchAboutDataLoading = false,
      onAddDataAudit = (e) => e,
      onDeleteDataAudit = (e) => e,
    } = this.props;
    const { getFieldValue } = form;
    const {
      auditOpConfigId,
      tenantId,
      tenantName,
      serviceName,
      auditArgsFlag,
      auditResultFlag,
      auditDataFlag,
      permissionId,
      auditContent,
      businessKey,
      description,
      auditType,
      userId,
      username,
      roleId,
      roleName,
      clientName,
    } = initData;
    const { getFieldDecorator } = form;
    const columns = [
      !isTenantRoleLevel && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.tableName').d('表名'),
        dataIndex: 'tableName',
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.displayName').d('展示名称'),
        dataIndex: 'displayName',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'delete',
              ele: (
                <a onClick={() => onDeleteDataAudit(record)}>
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];

          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
        width={550}
      >
        <Spin spinning={initLoading}>
          <Form>
            {!isTenantRoleLevel && (
              <FormItem
                {...formLayout}
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={tenantName}
                    disabled={auditOpConfigId !== undefined}
                    onChange={this.tenantChange}
                  />
                )}
              </FormItem>
            )}
            <FormItem
              {...formLayout}
              label={intl.get('hmnt.auditConfig.model.auditConfig.auditType').d('审计类型')}
            >
              {getFieldDecorator('auditType', {
                initialValue: auditType,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmnt.auditConfig.model.auditConfig.auditType').d('审计类型'),
                    }),
                  },
                ],
              })(
                <Select allowClear disabled={auditOpConfigId !== undefined}>
                  {map(auditTypeList, (e) => (
                    <Select.Option value={e.value} key={e.value}>
                      {e.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
            {getFieldValue('auditType') === 'API' && (
              <>
                <FormItem
                  {...formLayout}
                  label={intl.get('hmnt.auditConfig.model.auditConfig.serviceName').d('服务')}
                >
                  {getFieldDecorator('serviceName', {
                    initialValue: serviceName,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hmnt.auditConfig.model.auditConfig.serviceName')
                            .d('服务'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code={
                        !isTenantRoleLevel
                          ? 'HADM.ROUTE.SERVICE_CODE'
                          : 'HADM.ROUTE.SERVICE_CODE.ORG'
                      }
                      textField="serviceName"
                      queryParams={!!isTenantRoleLevel && { organizationId }}
                      onChange={this.handleChangeServiceName}
                      disabled={auditOpConfigId !== undefined}
                    />
                  )}
                </FormItem>
                <FormItem
                  {...formLayout}
                  label={intl.get('hmnt.auditConfig.model.auditConfig.permissionId').d('审计接口')}
                >
                  {getFieldDecorator('permissionId', {
                    initialValue: permissionId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hmnt.auditConfig.model.auditConfig.permissionId')
                            .d('审计接口'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HIAM.PERMISSION.AUDIT"
                      textValue={description}
                      textField="description"
                      queryParams={{ serviceName: form.getFieldValue('serviceName') }}
                      disabled={
                        isNil(form.getFieldValue('serviceName')) || auditOpConfigId !== undefined
                      }
                    />
                  )}
                </FormItem>
              </>
            )}
            {getFieldValue('auditType') === 'USER' && (
              <FormItem
                {...formLayout}
                label={intl.get('hmnt.auditConfig.model.auditConfig.userName').d('用户名')}
              >
                {getFieldDecorator('userId', {
                  initialValue: userId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmnt.auditConfig.model.auditConfig.userName').d('用户名'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code={isTenantRoleLevel ? 'HIAM.TENANT.USER' : 'HIAM.SITE.USER'}
                    textValue={username}
                    textField="loginName"
                    disabled={auditOpConfigId !== undefined}
                    queryParams={
                      isTenantRoleLevel && { organizationId: getCurrentOrganizationId() }
                    }
                  />
                )}
              </FormItem>
            )}
            {getFieldValue('auditType') === 'ROLE' && (
              <FormItem
                {...formLayout}
                label={intl.get('hmnt.auditConfig.model.auditConfig.roleName').d('角色名')}
              >
                {getFieldDecorator('roleId', {
                  initialValue: roleId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmnt.auditConfig.model.auditConfig.roleName').d('角色名'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HMNT.AUDIT.ROLE"
                    textValue={roleName}
                    textField="roleName"
                    disabled={auditOpConfigId !== undefined}
                    queryParams={{
                      tenantId: isTenantRoleLevel
                        ? getCurrentOrganizationId()
                        : getFieldValue('tenantId'),
                      excludeConfigured: true,
                    }}
                  />
                )}
              </FormItem>
            )}
            {getFieldValue('auditType') === 'CLIENT' && (
              <FormItem
                {...formLayout}
                label={intl.get('hmnt.auditConfig.model.auditConfig.clientName').d('客户端')}
              >
                {getFieldDecorator('clientName', {
                  initialValue: clientName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmnt.auditConfig.model.auditConfig.clientName').d('客户端'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HIAM.OAUTH_CLIENT"
                    textValue={clientName}
                    textField="name"
                    disabled={auditOpConfigId !== undefined}
                    queryParams={{ tenantId: getCurrentOrganizationId() }}
                  />
                )}
              </FormItem>
            )}
            <Form.Item
              {...formLayout}
              label={intl.get(`hmnt.auditConfig.model.auditConfig.auditArgsFlag`).d('记录请求参数')}
            >
              {form.getFieldDecorator('auditArgsFlag', {
                initialValue: auditArgsFlag === 0 ? auditArgsFlag : 1,
              })(<Switch />)}
            </Form.Item>
            <Form.Item
              {...formLayout}
              label={intl
                .get(`hmnt.auditConfig.model.auditConfig.auditResultFlag`)
                .d('记录响应参数')}
            >
              {form.getFieldDecorator('auditResultFlag', {
                initialValue: auditResultFlag === 0 ? auditResultFlag : 1,
              })(<Switch />)}
            </Form.Item>
            <Form.Item
              {...formLayout}
              label={intl.get(`hmnt.auditConfig.model.auditConfig.auditDataFlag`).d('记录操作数据')}
            >
              {form.getFieldDecorator('auditDataFlag', {
                initialValue: auditDataFlag === 0 ? auditDataFlag : 1,
              })(<Switch />)}
            </Form.Item>
            <FormItem
              {...formLayout}
              label={intl.get('hmnt.auditConfig.model.auditConfig.businessKey').d('业务主键')}
            >
              {getFieldDecorator('businessKey', {
                rules: [
                  {
                    max: 480,
                    message: intl.get('hzero.common.validation.max', {
                      max: 480,
                    }),
                  },
                ],
                initialValue: businessKey,
              })(<Input />)}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hmnt.auditConfig.model.auditConfig.auditContent').d('操作内容')}
            >
              {getFieldDecorator('auditContent', {
                rules: [
                  {
                    max: 320,
                    message: intl.get('hzero.common.validation.max', {
                      max: 320,
                    }),
                  },
                ],
                initialValue: auditContent,
              })(<Input />)}
            </FormItem>
          </Form>
          {auditOpConfigId !== undefined && (
            <>
              <div className={styles['audit-config-btn-warp']}>
                <Lov
                  isButton
                  code="HMNT.AUDIT_OP.DATA"
                  onChange={(_, record) => onAddDataAudit(record)}
                  queryParams={{ tenantId, auditOpConfigId }}
                >
                  {intl.get('hmnt.auditConfig.view.button.aboutDataAudit').d('关联数据审计')}
                </Lov>
              </div>
              <Table
                bordered
                columns={columns}
                dataSource={aboutDataAuditList}
                loading={fetchAboutDataLoading}
              />
            </>
          )}
        </Spin>
      </Modal>
    );
  }
}
