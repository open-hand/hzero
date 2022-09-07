import React, { PureComponent } from 'react';
import {
  Col,
  Form,
  Icon,
  Input,
  InputNumber,
  Modal,
  Row,
  Select,
  Spin,
  DatePicker,
  Tooltip,
  Tabs,
  Popconfirm,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import { forEach, isEmpty, isObject, isString, isUndefined, find, isNil } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';
import Lov from 'components/Lov';
import EditTable from 'components/EditTable';
import Switch from 'components/Switch';

import { encryptPwd, getDateFormat, getEditTableData } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';

import RoleModal from './RoleModal';
import styles from './index.less';

const { Option } = Select;
const FormItem = Form.Item;
const { TextArea } = Input;
const { TabPane } = Tabs;

function isJSON(str) {
  let result;
  try {
    result = JSON.parse(str);
  } catch (e) {
    return false;
  }
  return isObject(result) && !isString(result);
}

@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  state = {
    ownedRoleList: [],
    selectedRowKeys: [],
    selectedRows: [],
    visibleRole: false,
    // dataSource: [], // 客户端表格的信息
    currentClientId: '',
    roleType: 'permission',
    selectedVisitRowKeys: [],
    visible: 0,
  };

  componentDidMount() {
    this.queryOwnedRole();
  }

  // 查询当前已分配角色
  @Bind()
  queryOwnedRole(fields = {}) {
    const { dispatch, detailStatus, detailRecord, fetchDetailData } = this.props;
    switch (detailStatus) {
      case 'update':
        fetchDetailData(detailRecord).then((res) => {
          if (res) {
            dispatch({
              type: 'client/roleCurrent',
              payload: {
                clientId: res.id,
                memberType: 'client',
                page: isEmpty(fields) ? {} : fields,
              },
            }).then((detailRes) => {
              const { form } = this.props;
              if (detailRes) {
                form.resetFields();
                this.setState({
                  // 对获取到的用户列表进行处理，加入_status属性
                  ownedRoleList: detailRes.content.map((r) => ({
                    ...r,
                    _status: 'update',
                  })),
                  currentClientId: res.id,
                });
              }
            });
            dispatch({
              type: 'client/roleVisitCurrent',
              payload: {
                clientId: res.id,
                page: isEmpty(fields) ? {} : fields,
                organizationId: res.organizationId,
              },
            });
          }
        });
        break;
      default:
        break;
    }
  }

  @Bind()
  handleAfterAddPermissionRole(fields = {}) {
    const { dispatch } = this.props;
    const { currentClientId, roleType } = this.state;
    if (roleType === 'permission') {
      dispatch({
        type: 'client/roleCurrent',
        payload: {
          clientId: currentClientId,
          memberType: 'client',
          page: isEmpty(fields) ? {} : fields,
        },
      }).then((detailRes) => {
        const { form } = this.props;
        if (detailRes) {
          form.resetFields();
          this.setState({
            // 对获取到的用户列表进行处理，加入_status属性
            ownedRoleList: detailRes.content.map((r) => ({
              ...r,
              _status: 'update',
            })),
          });
        }
      });
    } else {
      dispatch({
        type: 'client/roleVisitCurrent',
        payload: {
          clientId: currentClientId,
          page: isEmpty(fields) ? {} : fields,
        },
      });
    }
  }

  /**
   * 模态框确定-保存客户端
   */
  @Bind()
  onOk() {
    const { onOk, form, publicKey, initData } = this.props;
    form.validateFields((error, fieldsValue) => {
      if (!error) {
        // ownedRoleList 获取到的用户列表
        const { ownedRoleList = [] } = this.state;
        // validatingDataSource 可编辑的数据
        const validatingDataSource = ownedRoleList.filter(
          (r) => r._status === 'create' || (r._status === 'update' && r.manageableFlag === 1)
        );
        // validateSource 编辑后的数据
        // TODO: 没有编辑的数据不传给后端，这里是把所有可编辑的数据全部都传了
        const validateDataSource = getEditTableData(validatingDataSource, ['_status']);
        const memberRoleList = validateDataSource
          .filter((i) => {
            const j = find(validatingDataSource, (or) => or.id === i.id);
            return (
              (j._status === 'update' &&
                (j.startDateActive !== i.startDateActive || j.endDateActive !== i.endDateActive)) ||
              j._status === 'create'
            );
          })
          .map((r) => ({
            ...r,
            roleId: r.roleId || r.id,
            // roleId: r.id,
            assignLevel: r.assignLevel,
            assignLevelValue: r.assignLevelValue,
            memberId: initData.id,
            memberType: 'client',
            startDateActive: r.startDateActive && r.startDateActive.format(DEFAULT_DATE_FORMAT),
            endDateActive: r.endDateActive && r.endDateActive.format(DEFAULT_DATE_FORMAT),
          }));

        const newValue = { ...fieldsValue };
        if (fieldsValue.secret) {
          newValue.secret = encryptPwd(fieldsValue.secret, publicKey);
        }
        const newClient = {
          ...newValue,
          memberRoleList: memberRoleList.map((item) => {
            if (item.createFlag) {
              const { id, createFlag, ...rest } = item;
              return rest;
            } else {
              const { id, ...res } = item;
              return res;
            }
          }),
          // : memberRoleList
          // .concat(ownedRoleList.filter(
          //   r => r._status === 'create'
          // )),
        };
        // if (validatingDataSource.length === validateDataSource.length) {
        onOk(newClient);
        // }
      }
    });
  }

  /**
   * 校验客户端名称
   * @param rule
   * @param value
   * @param callback
   */
  @Bind()
  checkName(rule, value, callback) {
    const { dispatch, tenantId } = this.props;
    if (value) {
      dispatch({
        type: 'client/checkClient',
        payload: {
          tenantId,
          name: value,
        },
      }).then((res) => {
        if (isJSON(res) && JSON.parse(res).failed) {
          callback(JSON.parse(res).message);
        } else {
          callback();
        }
      });
    } else {
      // 为空的话, 必输校验 已经 校验了
      callback();
    }
  }

  @Bind()
  isJson(string) {
    try {
      if (typeof JSON.parse(string) === 'object') {
        return true;
      }
    } catch (e) {
      return false;
    }
  }

  // 校验作用域和自动授权域
  @Bind()
  validateSelect(rule, value, callback, name) {
    const length = value && value.length;
    if (length) {
      const reg = new RegExp(/^[A-Za-z]+$/);
      if (!reg.test(value[length - 1]) && name === 'scope') {
        callback(intl.get('hiam.client.view.validate.scope').d(`作用域只能包含英文字母`));
        return;
      }
      if (!reg.test(value[length - 1]) && name === 'autoApprove') {
        callback(intl.get('hiam.client.view.validate.autoApprove').d(`自动授权域只能包含英文字母`));
        return;
      }

      if (length > 6) {
        callback(intl.get('hiam.client.view.validate.maxLength').d('最多只能输入6个域'));
        return;
      }
    }
    callback();
  }

  // 初始化 授权类型
  @Bind()
  getAuthorizedGrantTypes() {
    const { initData = {} } = this.props;
    const createFlag = isUndefined(initData.id);
    if (createFlag) {
      return ['password', 'implicit', 'client_credentials', 'authorization_code', 'refresh_token'];
    }
    return initData.authorizedGrantTypes ? initData.authorizedGrantTypes.split(',') : [];
  }

  // 删除选中的角色
  @Bind()
  handleRoleRemoveBtnClick() {
    const { initData = {}, dispatch, paginationRole } = this.props;
    const { selectedRows, selectedRowKeys, ownedRoleList } = this.state;
    const that = this;
    if (selectedRows.filter((i) => i._status !== 'create').length === 0) {
      if (selectedRowKeys.length === 0) {
        Modal.error({
          content: intl.get('hiam.client.view.message.chooseRoleFirst').d('请先选择要删除的角色'),
        });
        return;
      } else {
        that.setState({
          ownedRoleList: ownedRoleList.filter((item) => !selectedRowKeys.includes(item.id)),
        });
        return;
      }
    }
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.title').d('提示'),
      content: intl.get(`hiam.client.view.message.title.content`).d('确定删除吗？'),
      onOk() {
        const ids = [];
        selectedRows
          .filter((i) => i._status !== 'create')
          .forEach((item) => {
            ids.push({
              roleId: item.id,
              memberType: 'client',
              memberId: initData.id,
            });
          });
        dispatch({
          type: 'client/deleteRoles',
          payload: {
            memberRoleList: ids,
          },
        }).then((res) => {
          if (res) {
            that.queryOwnedRole(paginationRole);
            that.setState({ selectedRowKeys: [] });
            notification.success();
          }
        });
      },
    });
  }

  /**
   * 渲染 分配角色Table
   */
  @Bind()
  renderRoleTable() {
    const { ownedRoleList = [], selectedRowKeys = [] } = this.state;
    const { isSameUser, fetchOwnedLoading, paginationRole } = this.props;
    const rowSelection = isSameUser
      ? null
      : {
          selectedRowKeys,
          onChange: this.handleRoleSelectionChange,
        };
    const columns = [
      {
        title: intl.get('hiam.client.model.client.roleName').d('角色名称'),
        dataIndex: 'name',
        width: 100,
        render: (v, record) => <Tooltip title={record.tipMessage}>{v}</Tooltip>,
      },
      {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hiam.subAccount.model.role.startDateActive').d('起始时间'),
        key: 'startDateActive',
        width: 160,
        render: (_, record) => {
          const { $form } = record;
          const { getFieldDecorator } = $form;
          const dateFormat = getDateFormat();
          return (
            <Form.Item>
              {getFieldDecorator('startDateActive', {
                initialValue: record.startDateActive
                  ? moment(record.startDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabled={record.manageableFlag === 0}
                  disabledDate={(currentDate) => {
                    return (
                      $form.getFieldValue('endDateActive') &&
                      moment($form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                    );
                  }}
                />
              )}
            </Form.Item>
          );
        },
      },
      {
        title: intl.get('hiam.subAccount.model.role.endDateActive').d('失效时间'),
        key: 'endDateActive',
        width: 160,
        render: (_, record) => {
          const { $form } = record;
          const { getFieldDecorator } = $form;
          const dateFormat = getDateFormat();
          return (
            <Form.Item>
              {getFieldDecorator('endDateActive', {
                initialValue: record.endDateActive
                  ? moment(record.endDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabled={record.manageableFlag === 0}
                  disabledDate={(currentDate) =>
                    $form.getFieldValue('startDateActive') &&
                    moment($form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          );
        },
      },
    ];
    return (
      <EditTable
        rowKey="id"
        bordered
        onChange={this.handleAfterAddPermissionRole}
        loading={fetchOwnedLoading}
        rowSelection={rowSelection}
        dataSource={ownedRoleList}
        columns={columns}
        pagination={paginationRole}
      />
    );
  }

  /**
   * 渲染 分配角色Table
   */
  @Bind()
  renderVisitRoleTable() {
    const { selectedVisitRowKeys = [] } = this.state;
    const { isSameUser, fetchOwnedLoading, visitRoleList, visitRolePagination } = this.props;
    const rowSelection = isSameUser
      ? null
      : {
          selectedVisitRowKeys,
          onChange: this.handleVisitRoleSelectionChange,
        };
    const columns = [
      {
        title: intl.get('hiam.client.model.client.roleName').d('角色名称'),
        dataIndex: 'name',
        width: 300,
      },
      {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
      },
    ];
    return (
      <EditTable
        rowKey="id"
        bordered
        onChange={this.handleAfterAddPermissionRole}
        loading={fetchOwnedLoading}
        rowSelection={rowSelection}
        dataSource={visitRoleList}
        columns={columns}
        pagination={visitRolePagination}
      />
    );
  }

  /**
   * 打开新增角色 选择模态框
   */
  @Bind()
  handleRoleAddBtnClick() {
    const { ownedRoleList = [] } = this.state;
    const roleModalProps = {
      excludeRoleIds: [],
      excludeUserIds: [],
    };
    ownedRoleList.forEach((r) => {
      roleModalProps.excludeRoleIds.push(r.id);
    });
    this.setState({
      visibleRole: true,
      roleModalProps,
    });
  }

  /**
   * 打开新增角色 选择模态框
   */
  @Bind()
  handleVisitRoleAddBtnClick() {
    // const { ownedRoleList = [] } = this.state;
    const { visitRoleList } = this.props;
    const roleModalProps = {
      excludeVisitRoleIds: [],
      excludeUserIds: [],
    };
    visitRoleList.forEach((r) => {
      roleModalProps.excludeVisitRoleIds.push(r.id);
    });
    this.setState({
      visibleRole: true,
      roleModalProps,
    });
  }

  // 删除选中的角色
  @Bind()
  handleVisitRoleRemoveBtnClick() {
    const { initData = {}, dispatch, paginationRole } = this.props;
    const { roleType, selectedVisitRowKeys } = this.state;
    const that = this;
    if (selectedVisitRowKeys.length === 0) {
      Modal.error({
        content: intl.get('hiam.client.view.message.chooseRoleFirst').d('请先选择要删除的角色'),
      });
      return;
    }
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.title').d('提示'),
      content: intl.get(`hiam.client.view.message.title.content`).d('确定删除吗？'),
      onOk() {
        const ids = [];
        selectedVisitRowKeys.forEach((item) => {
          ids.push({
            roleId: item,
            memberId: initData.id,
          });
        });
        dispatch({
          type: roleType === 'permission' ? 'client/deleteRoles' : 'client/deleteVisitRoles',
          payload: {
            clientId: initData.id,
            memberRoleList: ids.map((item) => {
              const { id, ...rest } = item;
              return rest;
            }),
          },
        }).then((res) => {
          if (res) {
            that.queryOwnedRole(paginationRole);
            that.setState({ selectedRowKeys: [] });
            notification.success();
          }
        });
      },
    });
  }

  @Bind()
  fetchRoles(fields) {
    const { fetchAllRoles } = this.props;
    return fetchAllRoles(fields);
  }

  /**
   * 新增角色模态框确认按钮点击
   */
  @Bind()
  handleRoleAddSaveBtnClick(roles) {
    const { tenantId, dispatch, paginationRole, initData } = this.props;
    const { roleType } = this.state;
    const memberRoleList = [];
    forEach(roles, (record) => {
      const newRecord = {
        id: record.id,
        roleId: record.id,
        assignLevel: 'organization',
        memberType: 'client',
        memberId: initData.id,
        sourceId: tenantId,
        sourceType: record.level,
        assignLevelValue: record.tenantId,
        name: record.name,
        assignLevelValueMeaning: record.assignLevelValueMeaning,
        tenantName: record.tenantName,
        createFlag: true,
      };
      if (roleType === 'visit') {
        delete newRecord.memberType;
      }
      // newRecord.assignLevelValue = record.assignLevelValue || tenantId;
      if (!isEmpty(newRecord.assignLevel) && !isNil(newRecord.assignLevelValue)) {
        memberRoleList.push(newRecord);
      }
    });
    const payload =
      roleType === 'permission'
        ? [...memberRoleList]
        : {
            memberRoleList,
            clientId: initData.id,
            // organizationId: initData.organizationId,
          };
    return dispatch({
      type: roleType === 'permission' ? 'client/saveRoleSet' : 'client/saveVisitRoleSet',
      payload,
    }).then((res) => {
      if (res) {
        if (roleType === 'permission') {
          this.setState(
            {
              visibleRole: false,
            },
            () => {
              this.setState({
                ownedRoleList: this.state.ownedRoleList.concat(res),
              });
              // this.handleAfterAddPermissionRole(paginationRole);
            }
          );
        } else {
          this.setState(
            {
              visibleRole: false,
            },
            () => {
              notification.success();
              this.queryOwnedRole(paginationRole);
              // this.handleAfterAddPermissionRole(paginationRole);
            }
          );
        }
      }
    });
  }

  /**
   * 新增角色模态框取消按钮点击
   */
  @Bind()
  handleRoleAddCancelBtnClick() {
    this.setState({
      visibleRole: false,
    });
  }

  /**
   * @param {String[]} selectedRowKeys 选中的rowKey
   */
  @Bind()
  handleRoleSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  @Bind()
  handleVisitRoleSelectionChange(selectedVisitRowKeys) {
    this.setState({ selectedVisitRowKeys });
  }

  @Bind()
  handleChangeRoleType(type) {
    this.setState({
      roleType: type,
    });
  }

  render() {
    const {
      path,
      form,
      initData = {},
      typeList = [],
      title,
      visible,
      onCancel,
      loading,
      loadingDistributeUsers,
      saveRoleLoading,
      detailStatus,
      fetchLoading = false,
      isTenantRoleLevel,
    } = this.props;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;
    const {
      selectedRowKeys,
      visibleRole,
      roleModalProps = {},
      roleType,
      selectedVisitRowKeys,
      visible: confirmVisible,
    } = this.state;
    const updateFlag = detailStatus === 'update';
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };
    return (
      <Modal
        width={1000}
        title={title}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onOk={this.onOk}
        onCancel={onCancel}
        confirmLoading={loading}
        destroyOnClose
      >
        <Spin spinning={fetchLoading}>
          <Form>
            <Row gutter={24} type="flex">
              {!isTenantRoleLevel && (
                <Col span={12}>
                  <FormItem {...formLayout} label={intl.get('entity.tenant.tag').d('租户')}>
                    {getFieldDecorator('organizationId', {
                      initialValue: initData.organizationId,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('entity.tenant.tag').d('租户'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        code="HPFM.TENANT"
                        textField="tenantName"
                        disabled={updateFlag}
                        textValue={initData.tenantName}
                      />
                    )}
                  </FormItem>
                </Col>
              )}
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.name').d('名称')}
                >
                  {getFieldDecorator('name', {
                    initialValue: initData.name,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.client.model.client.name').d('名称'),
                        }),
                      },
                      {
                        pattern: /^[0-9a-zA-Z-]{0,32}$/,
                        message: intl
                          .get('hiam.client.model.client.namePattern')
                          .d('客户端名称只能由1-32位的数字或字母或中划线组成'),
                      },
                      {
                        validator: !updateFlag && this.checkName,
                      },
                    ],
                    validateTrigger: 'onBlur',
                  })(<Input disabled={updateFlag} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.secret').d('密钥')}
                >
                  {getFieldDecorator('secret', {
                    initialValue: initData.secret,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.client.model.client.secret').d('密钥'),
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
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.authorizedGrantTypes').d('授权类型')}
                >
                  {getFieldDecorator('authorizedGrantTypes', {
                    // initialValue: initData.authorizedGrantTypes,
                    initialValue: this.getAuthorizedGrantTypes(),
                    rules: [
                      {
                        type: 'array',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.client.model.client.authorizedGrantTypes')
                            .d('授权类型'),
                        }),
                      },
                    ],
                  })(
                    <Select mode="multiple" style={{ width: '100%' }}>
                      {typeList.map((item) => (
                        <Option label={item.meaning} value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl
                    .get('hiam.client.model.client.accessTokenValidity')
                    .d('访问授权超时(秒)')}
                >
                  {getFieldDecorator('accessTokenValidity', {
                    // eslint-disable-next-line no-nested-ternary
                    initialValue: !updateFlag
                      ? 3600
                      : initData.accessTokenValidity
                      ? parseInt(initData.accessTokenValidity, 10)
                      : undefined,
                    rules: [
                      {
                        validator: (rule, value, callback) => {
                          if (value < 60) {
                            callback(intl.get('hiam.client.view.validate.min').d(`最小不能小于60`));
                            return;
                          }
                          callback();
                        },
                      },
                    ],
                  })(<InputNumber style={{ width: '100%' }} min={60} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl
                    .get('hiam.client.model.client.refreshTokenValidity')
                    .d('授权超时(秒)')}
                >
                  {getFieldDecorator('refreshTokenValidity', {
                    // eslint-disable-next-line no-nested-ternary
                    initialValue: !updateFlag
                      ? 3600
                      : initData.refreshTokenValidity
                      ? parseInt(initData.refreshTokenValidity, 10)
                      : undefined,
                    rules: [
                      {
                        validator: (rule, value, callback) => {
                          if (value < 60) {
                            callback(intl.get('hiam.client.view.validate.min').d(`最小不能小于60`));
                            return;
                          }
                          callback();
                        },
                      },
                    ],
                  })(<InputNumber style={{ width: '100%' }} min={60} />)}
                </FormItem>
              </Col>

              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    // label={intl.get('hiam.client.model.client.scope').d('作用域')}
                    label={
                      <span>
                        {intl.get('hiam.client.model.client.scope').d('作用域')}&nbsp;
                        <Tooltip
                          title={intl
                            .get('hiam.client.view.message.scope.help.msg')
                            .d('作用域为申请的授权范围。您最多可输入6个域')}
                        >
                          <Icon type="question-circle-o" />
                        </Tooltip>
                      </span>
                    }
                  >
                    {getFieldDecorator('scope', {
                      initialValue: initData.scope ? initData.scope.split(',') : [],
                      rules: [
                        {
                          validator: (rule, value, callback) =>
                            this.validateSelect(rule, value, callback, 'scope'),
                        },
                      ],
                      validateTrigger: 'onChange',
                    })(<Select mode="tags" style={{ width: '100%' }} />)}
                  </FormItem>
                </Col>
              )}
              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    // label={intl.get('hiam.client.model.client.autoApprove').d('自动授权域')}
                    label={
                      <span>
                        {intl.get('hiam.client.model.client.autoApprove').d('自动授权域')}&nbsp;
                        <Tooltip
                          title={intl
                            .get('hiam.client.view.message.autoApprove.help.msg')
                            .d(
                              '自动授权域为oauth认证后，系统自动授权而非用户手动添加的作用域。您最多可输入6个域'
                            )}
                        >
                          <Icon type="question-circle-o" />
                        </Tooltip>
                      </span>
                    }
                  >
                    {getFieldDecorator('autoApprove', {
                      initialValue: initData.autoApprove ? initData.autoApprove.split(',') : [],
                      rules: [
                        {
                          validator: (rule, value, callback) =>
                            this.validateSelect(rule, value, callback, 'autoApprove'),
                        },
                      ],
                      validateTrigger: 'onChange',
                    })(<Select mode="tags" style={{ width: '100%' }} />)}
                  </FormItem>
                </Col>
              )}
              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    label={intl
                      .get('hiam.client.model.client.webServerRedirectUri')
                      .d('重定向地址')}
                  >
                    {getFieldDecorator('webServerRedirectUri', {
                      initialValue: initData.webServerRedirectUri,
                    })(<Input />)}
                  </FormItem>
                </Col>
              )}
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.timeZone').d('时区')}
                >
                  {getFieldDecorator('timeZone', {
                    initialValue: initData.timeZone,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.client.model.client.timeZone').d('时区'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HIAM.TIME_ZONE"
                      textValue={initData.timeZoneMeaning}
                      textField="timeZoneMeaning"
                      allowClear={false}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.pwdReplayFlag').d('密码防重放')}
                >
                  {getFieldDecorator('pwdReplayFlag', {
                    initialValue: updateFlag ? initData.pwdReplayFlag : 1,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.apiEncryptFlag').d('接口加密')}
                >
                  {getFieldDecorator('apiEncryptFlag', {
                    initialValue: updateFlag ? initData.apiEncryptFlag : 1,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hiam.client.model.client.apiReplayFlag').d('API防重放')}
                >
                  <Popconfirm
                    title={intl
                      .get('hiam.client.view.confirm.apiReplayFlag')
                      .d('是否确认开启API防重放')}
                    visible={
                      confirmVisible && getFieldValue('apiReplayFlag') && updateFlag
                        ? !initData.apiReplayFlag
                        : 0
                    }
                    onCancel={() => {
                      setFieldsValue({ apiReplayFlag: 0 });
                    }}
                    onConfirm={() => {
                      this.setState({ visible: 0 });
                    }}
                  >
                    {getFieldDecorator('apiReplayFlag', {
                      initialValue: updateFlag ? initData.apiReplayFlag : 1,
                    })(
                      <Switch
                        onChange={() => {
                          this.setState({ visible });
                        }}
                      />
                    )}
                  </Popconfirm>
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formLayout} label={intl.get('hzero.common.button.enable').d('启用')}>
                  {getFieldDecorator('enabledFlag', {
                    initialValue: updateFlag ? initData.enabledFlag : 1,
                  })(<Switch />)}
                </FormItem>
              </Col>
              {updateFlag && (
                <Col span={12}>
                  <FormItem
                    {...formLayout}
                    label={intl.get('hiam.client.model.client.additionalInformation').d('附加信息')}
                  >
                    {getFieldDecorator('additionalInformation', {
                      initialValue: initData.additionalInformation,
                      rules: [
                        {
                          validator: (rule, value, callback) => {
                            if (!value || this.isJson(value)) {
                              callback();
                            } else {
                              callback(
                                intl
                                  .get('hiam.client.view.validate.additionalInformation')
                                  .d('请输入正确的json字符串')
                              );
                            }
                          },
                        },
                      ],
                      validateTrigger: 'onBlur',
                    })(<TextArea rows={5} />)}
                  </FormItem>
                </Col>
              )}
            </Row>
            {updateFlag && (
              <Tabs
                tabBarGutter={10}
                defaultActiveKey="permission"
                animated
                onChange={this.handleChangeRoleType}
              >
                <TabPane
                  tab={
                    <>
                      {intl.get('hiam.client.view.title.tab.visitRole').d('可访问角色')}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.client.view.message.title.permissionRole')
                          .d('该客户端可使用以下角色的权限')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </>
                  }
                  key="permission"
                >
                  {updateFlag && (
                    <Row>
                      <Col>
                        <FormItem style={{ textAlign: 'right' }}>
                          <Col span={23}>
                            <ButtonPermission
                              permissionList={[
                                {
                                  code: `${path}.button.deleteRole`,
                                  type: 'button',
                                  meaning: '客户端-删除角色',
                                },
                              ]}
                              style={{ marginRight: 8 }}
                              onClick={this.handleRoleRemoveBtnClick}
                              disabled={selectedRowKeys.length === 0}
                            >
                              {intl.get('hzero.common.button.delete').d('删除')}
                            </ButtonPermission>
                            <ButtonPermission
                              permissionList={[
                                {
                                  code: `${path}.button.createRole`,
                                  type: 'button',
                                  meaning: '客户端-新建角色',
                                },
                              ]}
                              type="primary"
                              onClick={() => this.handleRoleAddBtnClick()}
                            >
                              {intl.get('hzero.common.button.create').d('新建')}
                            </ButtonPermission>
                          </Col>
                        </FormItem>
                      </Col>
                    </Row>
                  )}
                  {updateFlag && (
                    <Row type="flex">
                      <Col span={24} className={styles['rule-table']}>
                        {this.renderRoleTable()}
                      </Col>
                    </Row>
                  )}
                </TabPane>
                <TabPane
                  tab={
                    <>
                      {intl.get('hiam.client.view.title.tab.perRole').d('授权角色')}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hiam.client.view.message.title.visitRole')
                          .d('拥有以下任意角色的用户可访问该客户端')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </>
                  }
                  key="visit"
                >
                  {updateFlag && (
                    <Row>
                      <Col>
                        <FormItem style={{ textAlign: 'right' }}>
                          <Col span={23}>
                            <ButtonPermission
                              permissionList={[
                                {
                                  code: `${path}.button.deleteRole`,
                                  type: 'button',
                                  meaning: '客户端-删除角色',
                                },
                              ]}
                              style={{ marginRight: 8 }}
                              onClick={this.handleVisitRoleRemoveBtnClick}
                              disabled={selectedVisitRowKeys.length === 0}
                            >
                              {intl.get('hzero.common.button.delete').d('删除')}
                            </ButtonPermission>
                            <ButtonPermission
                              permissionList={[
                                {
                                  code: `${path}.button.createRole`,
                                  type: 'button',
                                  meaning: '客户端-新建角色',
                                },
                              ]}
                              type="primary"
                              onClick={() => this.handleVisitRoleAddBtnClick()}
                            >
                              {intl.get('hzero.common.button.create').d('新建')}
                            </ButtonPermission>
                          </Col>
                        </FormItem>
                      </Col>
                    </Row>
                  )}
                  {updateFlag && (
                    <Row type="flex">
                      <Col span={24} className={styles['rule-table']}>
                        {this.renderVisitRoleTable()}
                      </Col>
                    </Row>
                  )}
                </TabPane>
              </Tabs>
            )}
          </Form>
          {!!visibleRole && (
            <RoleModal
              {...roleModalProps}
              visible={visibleRole}
              fetchLoading={loadingDistributeUsers}
              saveLoading={saveRoleLoading}
              fetchRoles={this.fetchRoles}
              onSave={this.handleRoleAddSaveBtnClick}
              onCancel={this.handleRoleAddCancelBtnClick}
              roleType={roleType}
              id={initData.id}
            />
          )}
        </Spin>
      </Modal>
    );
  }
}
