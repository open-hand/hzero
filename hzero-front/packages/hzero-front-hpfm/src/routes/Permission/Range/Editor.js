/**
 * @version 0.9.x
 */
import React, { Fragment, PureComponent } from 'react';
import {
  Button,
  Form,
  Icon,
  Input,
  notification,
  Popconfirm,
  Switch,
  Table,
  Tooltip,
  Modal,
} from 'hzero-ui';
import { isEmpty, uniqBy } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';
import Lov from 'components/Lov';

import { Content } from 'components/Page';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE } from 'utils/regExp';
import { operatorRender, enableRender } from 'utils/renderer';

import Drawer from '../Drawer';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

@Form.create({ fieldNameProp: null })
export default class DrawerForm extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      currentRecord: {},
      sqlVisible: false,
    };
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
      rangeServiceNameExclList,
      rangeTenantExclList,
      rangeSqlidExclList,
    } = this.props;
    const { cancel } = this;
    validateFields(
      [
        'tableName',
        'tenantId',
        'sqlId',
        'serviceName',
        'enabledFlag',
        'customRuleFlag',
        'description',
      ],
      (err, values) => {
        if (isEmpty(err)) {
          handleUpdate(
            {
              ...dataSource,
              ...values,
              enabledFlag: values.enabledFlag ? 1 : 0,
              customRuleFlag: values.customRuleFlag ? 1 : 0,
              rangeServiceNameExclList,
              rangeTenantExclList,
              rangeSqlidExclList,
            },
            () => {
              notification.success({
                message: intl.get('hzero.common.notification.success.save').d('保存成功'),
              });
              cancel();
            }
          );
        }
      }
    );
  }

  create() {
    const {
      form: { validateFields },
      dataSource,
      handleCreate = (e) => e,
      handleSetEditorDataSource = (e) => e,
    } = this.props;
    // const { cancel } = this;
    validateFields(
      [
        'tableName',
        'tenantId',
        'sqlId',
        'serviceName',
        'enabledFlag',
        'customRuleFlag',
        'description',
      ],
      (err, values) => {
        if (isEmpty(err)) {
          handleCreate(
            {
              ...dataSource,
              ...values,
              enabledFlag: values.enabledFlag ? 1 : 0,
              customRuleFlag: values.customRuleFlag ? 1 : 0,
            },
            (res) => {
              handleSetEditorDataSource(res);
              notification.success({
                message: intl.get('hzero.common.notification.success.create').d('创建成功'),
              });
            }
          );
        }
      }
    );
  }

  cancel() {
    const {
      onCancel = (e) => e,
      form: { resetFields },
    } = this.props;
    resetFields();
    onCancel();
  }

  validator(rule, value, callback) {
    const {
      form: { getFieldValue = (e) => e },
    } = this.props;
    if (getFieldValue('tenantId') === undefined) {
      callback(
        intl.get('hzero.common.validation.notNull', {
          name: intl.get('hpfm.permission.model.permission.tenant').d('租户'),
        })
      );
    }
    callback();
  }

  onSelectRuleOk(selectedData = {}) {
    const { handleAddPermissionRel = (e) => e, dataSource = {} } = this.props;
    const { rangeId } = dataSource;
    handleAddPermissionRel({
      rangeId,
      ruleId: selectedData.ruleId,
    });
  }

  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  onDeletePermissionRel(record) {
    const { handleDeletePermissionRel = (e) => e } = this.props;
    handleDeletePermissionRel(record);
    this.setState({ currentRecord: record });
  }

  onSelectService(record) {
    const { dispatch, rangeServiceNameExclList } = this.props;
    const data = {
      serviceName: record.serviceCode,
    };
    const newData = uniqBy([...rangeServiceNameExclList, data], 'serviceName');
    dispatch({
      type: 'permission/updateState',
      payload: {
        rangeServiceNameExclList: newData,
      },
    });
  }

  onSelectTenant(record) {
    const { dispatch, rangeTenantExclList } = this.props;
    const newData = uniqBy([...rangeTenantExclList, record], 'tenantId');
    dispatch({
      type: 'permission/updateState',
      payload: {
        rangeTenantExclList: newData,
      },
    });
  }

  @Bind()
  handleDeleteService(record) {
    const { dispatch, rangeServiceNameExclList } = this.props;
    const newService = rangeServiceNameExclList.filter(
      (item) => record.serviceName !== item.serviceName
    );
    dispatch({
      type: 'permission/updateState',
      payload: {
        rangeServiceNameExclList: newService,
      },
    });
  }

  @Bind()
  handleDeleteTenant(record) {
    const { dispatch, rangeTenantExclList } = this.props;
    const newTenant = rangeTenantExclList.filter((item) => record.tenantId !== item.tenantId);
    dispatch({
      type: 'permission/updateState',
      payload: {
        rangeTenantExclList: newTenant,
      },
    });
  }

  @Bind()
  handleDeleteSqlId(record) {
    const { dispatch, rangeSqlidExclList } = this.props;
    const newSql = rangeSqlidExclList.filter((item) => record.sqlId !== item.sqlId);
    dispatch({
      type: 'permission/updateState',
      payload: {
        rangeSqlidExclList: newSql,
      },
    });
  }

  @Bind()
  handleOpenSql() {
    this.setState({
      sqlVisible: true,
    });
  }

  @Bind()
  handleSqlOk() {
    const { form, rangeSqlidExclList, dispatch } = this.props;
    form.validateFields(['SQLID', { force: true }], (err, value) => {
      if (!err) {
        const data = {
          sqlId: value.SQLID,
        };
        const newData = uniqBy([...rangeSqlidExclList, data], 'sqlId');
        dispatch({
          type: 'permission/updateState',
          payload: {
            rangeSqlidExclList: newData,
          },
        });
        this.setState({
          sqlVisible: false,
        });
      }
    });
  }

  @Bind()
  handleSqlCancel() {
    this.setState({
      sqlVisible: false,
    });
  }

  defaultTableRowKey = 'permissionRelId';

  render() {
    const {
      match,
      visible,
      processing = {},
      form: { getFieldDecorator = (e) => e },
      dataSource = {},
      permissionRelDataSource = [],
      rangeServiceNameExclList = [],
      rangeTenantExclList = [],
      rangeSqlidExclList = [],
    } = this.props;
    const { currentRecord, sqlVisible = false } = this.state;
    const {
      rangeId,
      tableName,
      sqlId,
      serviceName,
      ruleName,
      description,
      enabledFlag = 1,
      editableFlag = 1,
      customRuleFlag,
      tenantId,
      tenantName,
    } = dataSource;
    // 是否平台级
    const isSiteFlag = !isTenantRoleLevel();
    const drawerProps = {
      title:
        rangeId !== undefined
          ? intl.get('hpfm.permission.view.option.updateRangeTitle').d('修改屏蔽范围')
          : intl.get('hpfm.permission.view.option.createRangeTitle').d('添加屏蔽范围'),
      visible,
      anchor: 'right',
      onCancel: this.cancel.bind(this),
      footer: (
        <Fragment>
          <Button onClick={this.cancel.bind(this)} disabled={processing.save}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            loading={processing.save || false}
            onClick={() => (rangeId !== undefined ? this.update() : this.create())}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        </Fragment>
      ),
      width: 550,
    };

    const tableProps = {
      dataSource: permissionRelDataSource || [],
      pagination: false,
      columns: [
        {
          title: intl.get('hpfm.permission.model.permission.ruleCode').d('规则编码'),
          width: 160,
          dataIndex: 'ruleCode',
          onCell: this.onCell.bind(this),
        },
        {
          title: intl.get('hpfm.permission.model.permission.ruleName').d('规则名称'),
          dataIndex: 'ruleName',
        },
        {
          title: intl.get('hzero.common.status').d('状态'),
          dataIndex: 'enabledFlag',
          align: 'center',
          width: 80,
          render: enableRender,
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          align: 'center',
          width: 80,
          render: (text, record) => {
            const operators = [
              {
                key: 'delete',
                ele: (
                  <>
                    <Popconfirm
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.onDeletePermissionRel(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${match.path}.button.rangeRuleDelete`,
                            type: 'button',
                            meaning: '数据权限范围规则-删除',
                          },
                        ]}
                        disabled={record.editableFlag === 0}
                      >
                        {
                          <Icon
                            type={
                              currentRecord.permissionRelId === record.permissionRelId &&
                              processing.deletePermissionRel
                                ? 'loading'
                                : 'delete'
                            }
                          />
                        }
                      </ButtonPermission>
                      &nbsp;
                    </Popconfirm>
                    {record.editableFlag === 0 && (
                      <Tooltip
                        title={intl
                          .get('hpfm.permission.view.message.tip.disable')
                          .d('自动生成，禁止操作')}
                      >
                        <Icon type="question-circle" style={{ color: 'rgb(218, 213, 213)' }} />
                      </Tooltip>
                    )}
                  </>
                ),
                len: 2,
                title: intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？'),
              },
            ];
            return operatorRender(operators);
          },
        },
      ],
      rowKey: this.defaultTableRowKey,
      loading: processing.queryPermissionRel,
      bordered: true,
    };

    const serviceProps = {
      dataSource: rangeServiceNameExclList || [],
      pagination: false,
      columns: [
        {
          title: intl.get('hpfm.permission.model.permission.serviceName').d('服务名'),
          width: 160,
          dataIndex: 'serviceName',
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          align: 'center',
          width: 80,
          render: (text, record) => {
            const operators = [
              {
                key: 'delete',
                ele: (
                  <>
                    <Popconfirm
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.handleDeleteService(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${match.path}.button.deleteService`,
                            type: 'button',
                            meaning: '数据权限范围规则-删除服务',
                          },
                        ]}
                        disabled={record.editableFlag === 0}
                      >
                        {
                          <Icon
                            type={
                              currentRecord.permissionRelId === record.permissionRelId &&
                              processing.deletePermissionRel
                                ? 'loading'
                                : 'delete'
                            }
                          />
                        }
                      </ButtonPermission>
                      &nbsp;
                    </Popconfirm>
                    {/* {record.editableFlag === 0 && (
                      <Tooltip
                        title={intl
                          .get('hpfm.permission.view.message.tip.disable')
                          .d('自动生成，禁止操作')}
                      >
                        <Icon type="question-circle" style={{ color: 'rgb(218, 213, 213)' }} />
                      </Tooltip>
                    )} */}
                  </>
                ),
                len: 2,
                title: intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？'),
              },
            ];
            return operatorRender(operators);
          },
        },
      ],
      rowKey: this.defaultTableRowKey,
      loading: processing.queryPermissionRel,
      bordered: true,
    };

    const tenantProps = {
      dataSource: rangeTenantExclList || [],
      pagination: false,
      columns: [
        {
          title: intl.get('hpfm.permission.model.permission.tenantName').d('租户'),
          width: 160,
          dataIndex: 'tenantName',
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          align: 'center',
          width: 80,
          render: (text, record) => {
            const operators = [
              {
                key: 'delete',
                ele: (
                  <>
                    <Popconfirm
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.handleDeleteTenant(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${match.path}.button.deleteTenant`,
                            type: 'button',
                            meaning: '数据权限范围规则-删除租户',
                          },
                        ]}
                        disabled={record.editableFlag === 0}
                      >
                        {
                          <Icon
                            type={
                              currentRecord.permissionRelId === record.permissionRelId &&
                              processing.deletePermissionRel
                                ? 'loading'
                                : 'delete'
                            }
                          />
                        }
                      </ButtonPermission>
                      &nbsp;
                    </Popconfirm>
                    {/* {record.editableFlag === 0 && (
                      <Tooltip
                        title={intl
                          .get('hpfm.permission.view.message.tip.disable')
                          .d('自动生成，禁止操作')}
                      >
                        <Icon type="question-circle" style={{ color: 'rgb(218, 213, 213)' }} />
                      </Tooltip>
                    )} */}
                  </>
                ),
                len: 2,
                title: intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？'),
              },
            ];
            return operatorRender(operators);
          },
        },
      ],
      rowKey: this.defaultTableRowKey,
      loading: processing.queryPermissionRel,
      bordered: true,
    };

    const sqlIdProps = {
      dataSource: rangeSqlidExclList || [],
      pagination: false,
      columns: [
        {
          title: intl.get('hpfm.permission.model.permission.sqlId').d('SQLID'),
          width: 160,
          dataIndex: 'sqlId',
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          align: 'center',
          width: 80,
          render: (text, record) => {
            const operators = [
              {
                key: 'delete',
                ele: (
                  <>
                    <Popconfirm
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.handleDeleteSqlId(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${match.path}.button.deleteSqlId`,
                            type: 'button',
                            meaning: '数据权限范围规则-删除SQLID',
                          },
                        ]}
                        disabled={record.editableFlag === 0}
                      >
                        {
                          <Icon
                            type={
                              currentRecord.permissionRelId === record.permissionRelId &&
                              processing.deletePermissionRel
                                ? 'loading'
                                : 'delete'
                            }
                          />
                        }
                      </ButtonPermission>
                      &nbsp;
                    </Popconfirm>
                    {/* {record.editableFlag === 0 && (
                      <Tooltip
                        title={intl
                          .get('hpfm.permission.view.message.tip.disable')
                          .d('自动生成，禁止操作')}
                      >
                        <Icon type="question-circle" style={{ color: 'rgb(218, 213, 213)' }} />
                      </Tooltip>
                    )} */}
                  </>
                ),
                len: 2,
                title: intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？'),
              },
            ];
            return operatorRender(operators);
          },
        },
      ],
      rowKey: this.defaultTableRowKey,
      loading: processing.queryPermissionRel,
      bordered: true,
    };

    return (
      <Drawer {...drawerProps}>
        <div>
          <Content>
            <Form>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.tableName').d('屏蔽表名')}
                {...formLayout}
              >
                {getFieldDecorator('tableName', {
                  initialValue: tableName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.permission.model.permission.tableName').d('屏蔽表名'),
                      }),
                    },
                    {
                      pattern: CODE,
                      message: intl
                        .get('hzero.common.validation.codeLower')
                        .d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                })(<Input disabled={editableFlag === 0} trim inputChinese={false} />)}
              </FormItem>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.sqlId').d('SQLID')}
                {...formLayout}
              >
                {getFieldDecorator('sqlId', {
                  initialValue: sqlId,
                  rules: [
                    {
                      max: 120,
                      message: intl.get('hzero.common.validation.max', {
                        max: 120,
                      }),
                    },
                  ],
                })(<Input disabled={editableFlag === 0} />)}
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
                      textValue={tenantName}
                      disabled={rangeId !== undefined || editableFlag === 0}
                      code="HPFM.TENANT"
                    />
                  )}
                </FormItem>
              )}
              <FormItem
                label={intl.get('hpfm.permission.model.permission.serviceName').d('服务名')}
                {...formLayout}
              >
                {getFieldDecorator('serviceName', {
                  initialValue: serviceName,
                  rules: [
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                })(
                  <Lov
                    textValue={serviceName}
                    disabled={editableFlag === 0}
                    code={isSiteFlag ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG'}
                  />
                )}
              </FormItem>
              <FormItem label={intl.get('hzero.common.status.enable').d('启用')} {...formLayout}>
                {getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag === 1,
                  valuePropName: 'checked',
                })(<Switch disabled={editableFlag === 0} />)}
              </FormItem>
              <FormItem
                label={intl
                  .get('hpfm.permission.model.permission.customRuleFlag')
                  .d('自定义规则标识')}
                {...formLayout}
              >
                {getFieldDecorator('customRuleFlag', {
                  initialValue: customRuleFlag === 1,
                  valuePropName: 'checked',
                })(<Switch disabled={editableFlag === 0} />)}
              </FormItem>
              <FormItem
                label={intl.get('hpfm.permission.model.permission.description').d('描述')}
                {...formLayout}
              >
                {getFieldDecorator('description', {
                  initialValue: description,
                  rules: [
                    {
                      max: 240,
                      message: intl.get('hzero.common.validation.max', {
                        max: 240,
                      }),
                    },
                  ],
                })(<Input disabled={editableFlag === 0} />)}
              </FormItem>
            </Form>
            {rangeId !== undefined && (
              <Fragment>
                <div
                  className="action"
                  style={{
                    marginBottom: 16,
                    display: 'flex',
                    justifyContent: 'flex-end',
                  }}
                >
                  <Lov
                    textValue={ruleName}
                    isButton
                    permissionList={[
                      {
                        code: `${match.path}.button.rangeRuleAdd`,
                        type: 'button',
                        meaning: '数据权限范围规则-添加屏蔽规则',
                      },
                    ]}
                    type="primary"
                    disabled={tenantId === undefined}
                    onOk={this.onSelectRuleOk.bind(this)}
                    style={{ marginRight: 8, textAlign: 'right' }}
                    queryParams={isSiteFlag ? { tenantId, enabledFlag: 1 } : { enabledFlag: 1 }}
                    code={isSiteFlag ? 'HPFM.PERMISSION_RANGE.RULE' : 'HPFM.PERMISSION_RULE.ORG'}
                  >
                    {intl.get('hpfm.permission.view.option.add').d('添加屏蔽规则')}
                  </Lov>
                </div>
                <Table {...tableProps} />
              </Fragment>
            )}
            {rangeId !== undefined && (
              <Fragment>
                <div
                  className="action"
                  style={{
                    marginBottom: 16,
                    marginTop: 16,
                    display: 'flex',
                    justifyContent: 'flex-end',
                  }}
                >
                  <Lov
                    textValue={serviceName}
                    isButton
                    permissionList={[
                      {
                        code: `${match.path}.button.rangeServiceNameExclList`,
                        type: 'button',
                        meaning: '数据权限范围规则-添加排除服务名',
                      },
                    ]}
                    type="primary"
                    // disabled={tenantId === undefined}
                    onOk={this.onSelectService.bind(this)}
                    style={{ marginRight: 8, textAlign: 'right' }}
                    // queryParams={isSiteFlag ? { tenantId, enabledFlag: 1 } : { enabledFlag: 1 }}
                    code={isSiteFlag ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG'}
                  >
                    {intl
                      .get('hpfm.permission.view.option.rangeServiceNameExclList')
                      .d('添加排除服务名')}
                  </Lov>
                </div>
                <Table {...serviceProps} />
              </Fragment>
            )}
            {rangeId !== undefined && (
              <Fragment>
                <div
                  className="action"
                  style={{
                    marginBottom: 16,
                    marginTop: 16,
                    display: 'flex',
                    justifyContent: 'flex-end',
                  }}
                >
                  <Button type="primary" onClick={this.handleOpenSql}>
                    {intl.get('hpfm.permission.view.option.rangeSqlidExclList').d('添加排除SQLID')}
                  </Button>
                </div>
                <Table {...sqlIdProps} />
              </Fragment>
            )}
            {rangeId !== undefined && (
              <Fragment>
                <div
                  className="action"
                  style={{
                    marginBottom: 16,
                    marginTop: 16,
                    display: 'flex',
                    justifyContent: 'flex-end',
                  }}
                >
                  <Lov
                    textValue={serviceName}
                    isButton
                    permissionList={[
                      {
                        code: `${match.path}.button.rangeTenantNameExclList`,
                        type: 'button',
                        meaning: '数据权限范围规则-添加排除租户',
                      },
                    ]}
                    type="primary"
                    // disabled={tenantId === undefined}
                    onOk={this.onSelectTenant.bind(this)}
                    style={{ marginRight: 8, textAlign: 'right' }}
                    // queryParams={isSiteFlag ? { tenantId, enabledFlag: 1 } : { enabledFlag: 1 }}
                    code="HPFM.TENANT"
                  >
                    {intl
                      .get('hpfm.permission.view.option.rangeTenantNameExclList')
                      .d('添加排除租户')}
                  </Lov>
                </div>
                <Table {...tenantProps} />
              </Fragment>
            )}
            <Modal
              destroyOnClose
              title={intl
                .get('hpfm.permission.view.message.title.rangeSqlidExclList')
                .d('排除SQLID范围')}
              visible={sqlVisible}
              onCancel={() => this.handleSqlCancel()}
              onOk={() => this.handleSqlOk()}
            >
              <Form>
                <FormItem
                  label={intl.get('hpfm.permission.model.permission.SQLID').d('排除SQLID')}
                  {...formLayout}
                >
                  {getFieldDecorator('SQLID', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.permission.model.permission.SQLID').d('排除SQLID'),
                        }),
                      },
                      {
                        max: 120,
                        message: intl.get('hzero.common.validation.max', {
                          max: 120,
                        }),
                      },
                    ],
                  })(<Input trim />)}
                </FormItem>
              </Form>
            </Modal>
          </Content>
        </div>
      </Drawer>
    );
  }
}
