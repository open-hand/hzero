/**
 * 用户管理-平台级
 * todo 角色，部分： 1。 别的公司管理员分配的角色怎么办
 * @date 2018-12-15
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Badge, Table, Tree, Modal } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, join, map } from 'lodash';
import classnames from 'classnames';

import { Content, Header } from 'components/Page';
import ExcelExport from 'components/ExcelExport';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_IAM } from 'utils/config';
import { dateRender, operatorRender } from 'utils/renderer';
import {
  getCurrentUser,
  getCurrentOrganizationId,
  tableScrollWidth,
  encryptPwd,
  setSession,
} from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import notification from 'utils/notification';

import FilterForm from './FilterForm';
import EditPasswordModal from './components/EditPasswordModal';
import EditPhoneModal from './components/EditPhoneModal';
import EditModal from './components/EditModal';
import AssignRoleModal from './components/AssignRoleModal';
import UserGroupModal from './components/UserGroupModal';
import EmployeeModal from './components/EmployeeModal';

const { TreeNode } = Tree;

@connect(({ loading, trSubAccount, user }) => ({
  user,
  fetching: loading.effects['trSubAccount/fetchSubAccountList'],
  saving:
    loading.effects['trSubAccount/createSubAccount'] ||
    loading.effects['trSubAccount/updateSubAccount'],
  phoneLoading: loading.effects['trSubAccount/resetPassword'],
  passwordLoading: loading.effects['trSubAccount/updatePassword'],
  queryDetailLoading: loading.effects['trSubAccount/querySubAccount'],
  roleRemoveLoading: loading.effects['trSubAccount/removeRoles'],
  fetchEmployeeLoading: loading.effects['trSubAccount/queryEmployee'],
  postCaptchaLoading: loading.effects['trSubAccount/postCaptcha'],
  currentUserId: (getCurrentUser() || {}).id,
  currentUser: getCurrentUser(),
  trSubAccount,
}))
@formatterCollections({ code: ['hiam.subAccount'] })
export default class ThreeRoleSubAccountSite extends React.Component {
  filterFormRef;

  state = {
    editPasswordModalProps: {},
    editPhoneModalProps: {},
    editModalProps: {},
    groupModalProps: {},
    createPasswordRuleData: {},
    // pagination: false,
    // 导出相关
    // expandedKeys: [],
    // checkedKeys: [],
    employeeVisible: false,
    currentUserId: '',
    assignRoleModalProps: {},
    forceCodeVerify: false,
  };

  componentDidMount() {
    this.handleFetchList();
    const { dispatch } = this.props;
    dispatch({
      type: 'trSubAccount/init',
    });
    dispatch({
      type: 'trSubAccount/queryDimension',
    });
    // 获取公钥
    dispatch({
      type: 'trSubAccount/getPublicKey',
    });
    dispatch({
      type: 'trSubAccount/getPasswordRule',
      payload: { organizationId: getCurrentOrganizationId() },
    }).then((res) => {
      if (res) {
        this.setState({ forceCodeVerify: res.forceCodeVerify });
      }
    });
  }

  render() {
    const {
      trSubAccount: {
        publicKey = '',
        passwordTipMsg = {},
        pagination = false,
        dataSource = [],
        lov: { level, levelMap, idd, gender, userType = [] } = {},
        employeeList = [],
        employeePagination = {},
      },
      user: {
        currentUser: { phone },
      },
      phoneLoading = false,
      passwordLoading = false,
      postCaptchaLoading = false,
      queryDetailLoading = false,
      roleRemoveLoading = false,
      fetching,
      saving,
      currentUser = {},
      match: { path },
      fetchEmployeeLoading,
    } = this.props;
    const {
      editPasswordModalProps = {},
      editModalProps = {},
      editPhoneModalProps = {},
      groupModalProps = {},
      editRecord = {},
      createPasswordRuleData = {},
      employeeVisible,
      assignRoleModalProps = {},
      forceCodeVerify = false,
    } = this.state;
    return (
      <>
        <Header title={intl.get('hiam.tr.subAccount.view.message.title').d('用户管理')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '用户管理-新建',
              },
            ]}
            type="primary"
            onClick={this.showCreateForm}
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ExcelExport
            exportAsync
            requestUrl={`${HZERO_IAM}/hzero/v1/users/export`}
            queryParams={this.getExportQueryParams()}
            queryFormItem={this.renderExportTree()}
          />
        </Header>
        <Content>
          <div className={classnames('table-list-form', 'table-list-search')}>
            <FilterForm
              onRef={this.handleFilterFormRef}
              onSearch={this.handleFetchList}
              userType={userType}
            />
          </div>
          <Table
            bordered
            pagination={pagination}
            dataSource={dataSource}
            loading={fetching || queryDetailLoading}
            onChange={this.handleTableChange}
            columns={this.getColumns()}
            scroll={{ x: tableScrollWidth(this.getColumns()) }}
            rowKey="id"
          />
        </Content>
        {editPasswordModalProps.visible && (
          <EditPasswordModal
            phone={phone}
            editRecord={editRecord}
            publicKey={publicKey}
            passwordTipMsg={{ ...passwordTipMsg, forceCodeVerify }}
            key="edit-modal-password"
            {...editPasswordModalProps}
            postCaptchaLoading={postCaptchaLoading}
            onEnd={this.handleEnd}
            onSend={this.handleSendCaptcha}
            onCancel={this.handlePasswordModalHidden}
            onOk={this.handlePasswordUpdate}
            confirmLoading={passwordLoading}
          />
        )}
        {editPhoneModalProps.visible && (
          <EditPhoneModal
            phone={phone}
            editRecord={editRecord}
            passwordTipMsg={passwordTipMsg}
            key="reset-password"
            {...editPhoneModalProps}
            postCaptchaLoading={postCaptchaLoading}
            onEnd={this.handleEnd}
            onSend={this.handleSendCaptcha}
            onCancel={this.handlePhoneModalHidden}
            onOk={this.handlePasswordReset}
            confirmLoading={phoneLoading}
          />
        )}
        {editModalProps.visible && (
          <EditModal
            key="edit-modal"
            passwordTipMsg={createPasswordRuleData}
            getPasswordRule={this.handlePasswordRule}
            {...editModalProps}
            currentUser={currentUser}
            fetchDetailData={this.fetchDetailData}
            onCancel={this.hiddenEditModal}
            onOk={this.handleEditModalOK}
            getCurrentUserRoles={this.getCurrentUserRoles}
            path={path}
            level={level}
            levelMap={levelMap}
            idd={idd}
            userType={userType}
            gender={gender}
            fetchRoles={this.fetchRoles}
            fetchCurrentUserRoles={this.fetchCurrentUserRoles}
            confirmLoading={saving}
            queryDetailLoading={queryDetailLoading}
            roleRemoveLoading={roleRemoveLoading}
          />
        )}
        {assignRoleModalProps.visible && (
          <AssignRoleModal
            key="role-modal"
            {...assignRoleModalProps}
            currentUser={currentUser}
            onCancel={this.hiddenRoleModal}
            onOk={this.handleRoleModalOK}
            getCurrentUserRoles={this.getCurrentUserRoles}
            path={path}
            level={level}
            levelMap={levelMap}
            userType={userType}
            fetchRoles={this.fetchRoles}
            fetchCurrentUserRoles={this.fetchCurrentUserRoles}
            onRoleRemove={this.handleRoleRemove}
            confirmLoading={saving}
            queryDetailLoading={queryDetailLoading}
            roleRemoveLoading={roleRemoveLoading}
          />
        )}
        {groupModalProps.visible && (
          <UserGroupModal
            key="group-modal"
            {...groupModalProps}
            path={path}
            onCancel={this.hiddenGroupModal}
            onOk={this.handleGroupModalOK}
            fetchGroups={this.fetchGroups}
            fetchCurrentUserGroups={this.fetchCurrentUserGroups}
            onGroupRemove={this.handleGroupRemove}
          />
        )}
        <EmployeeModal
          key="employee"
          path={path}
          dataSource={employeeList}
          pagination={employeePagination}
          visible={employeeVisible}
          onCancel={this.hiddenEmployeeModal}
          onSearch={this.handleQueryEmployee}
          onTableChange={this.handleEmployeeTableChange}
          fetchEmployeeLoading={fetchEmployeeLoading}
        />
      </>
    );
  }

  @Bind()
  hiddenEmployeeModal() {
    this.setState({
      employeeVisible: false,
    });
  }

  @Bind()
  handleQueryEmployee(params = {}) {
    const { dispatch } = this.props;
    const { currentUserId } = this.state;
    dispatch({
      type: 'trSubAccount/queryEmployee',
      payload: {
        userId: currentUserId,
        ...params,
      },
    });
  }

  @Bind()
  handleEmployeeTableChange(params = {}) {
    const { dispatch } = this.props;
    const { currentUserId } = this.state;
    dispatch({
      type: 'trSubAccount/queryEmployee',
      payload: {
        userId: currentUserId,
        ...params,
      },
    });
  }

  @Bind()
  handlePasswordRule(id) {
    const { dispatch } = this.props;
    const { forceCodeVerify } = this.state;
    dispatch({
      type: 'trSubAccount/getPasswordRule',
      payload: { organizationId: id, forceCodeVerify },
    }).then((res) => {
      if (res) {
        this.setState({ createPasswordRuleData: res });
      }
    });
  }

  /**
   * 拿到查询表单的引用
   * @param {object} filterFormRef
   */
  @Bind()
  handleFilterFormRef(filterFormRef) {
    this.filterFormRef = filterFormRef;
  }

  /**
   * 查询账号信息
   * @param {object} pagination
   */
  @Bind()
  handleFetchList(pagination) {
    this.setState({ pagination });
    const { dispatch } = this.props;
    let fieldsValue = {};
    if (this.filterFormRef) {
      const { form } = this.filterFormRef.props;
      fieldsValue = form.getFieldsValue();
      fieldsValue.startDateActive = fieldsValue.startDateActive
        ? fieldsValue.startDateActive.format(DEFAULT_DATE_FORMAT)
        : undefined;
      fieldsValue.endDateActive = fieldsValue.endDateActive
        ? fieldsValue.endDateActive.format(DEFAULT_DATE_FORMAT)
        : undefined;
    }
    dispatch({
      type: 'trSubAccount/fetchSubAccountList',
      payload: {
        ...pagination,
        ...fieldsValue,
      },
    });
  }

  /**
   * 从新加载页面，保留分页信息
   */
  @Bind()
  reloadList() {
    const { pagination } = this.state;
    this.handleFetchList(pagination);
  }

  /**
   * 页码改变
   * @param {object} page
   * @param {object} filter
   * @param {object} sort
   */
  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleFetchList({ page, sort });
  }

  @Bind()
  handleViewEmployee(record) {
    const { dispatch } = this.props;
    this.setState({
      employeeVisible: true,
      currentUserId: record.id,
    });
    dispatch({
      type: 'trSubAccount/queryEmployee',
      payload: {
        userId: record.id,
      },
    });
  }

  @Bind()
  handleChangeEnabled(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'trSubAccount/fetchChangeEnabled',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleFetchList();
      }
    });
  }

  @Bind()
  getColumns() {
    const {
      currentUserId,
      match: { path },
    } = this.props;
    return [
      {
        title: intl.get('hiam.subAccount.model.user.loginName').d('账号'),
        dataIndex: 'loginName',
        width: 120,
      },
      {
        title: intl.get('hiam.subAccount.model.user.realName').d('名称'),
        dataIndex: 'realName',
        // width: 220,
      },
      {
        title: intl.get('hiam.subAccount.model.user.email').d('邮箱'),
        dataIndex: 'email',
        width: 300,
      },
      {
        title: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
        dataIndex: 'phone',
        width: 200,
        render: (phone, record) => {
          if (record.internationalTelMeaning && record.phone) {
            // todo 需要改成好看的样子
            return `${record.internationalTelMeaning} | ${record.phone}`;
          }
          return phone;
        },
      },
      {
        title: intl.get('hzero.common.date.active.from').d('有效日期从'),
        dataIndex: 'startDateActive',
        width: 120,
        render: dateRender,
      },
      {
        title: intl.get('hzero.common.date.active.to').d('有效日期至'),
        dataIndex: 'endDateActive',
        width: 120,
        render: dateRender,
      },
      {
        title: intl.get('hiam.subAccount.model.user.enabled').d('冻结'),
        dataIndex: 'enabled',
        width: 80,
        render: (item) => (
          <Badge
            status={item === true ? 'success' : 'error'}
            text={
              item === false
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        title: intl.get('hiam.subAccount.model.user.locked').d('锁定'),
        dataIndex: 'locked',
        width: 80,
        render: (item) => (
          <Badge
            status={item === false ? 'success' : 'error'}
            text={
              item === true
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        title: intl.get('hiam.subAccount.model.user.userType').d('用户类型'),
        dataIndex: 'userTypeMeaning',
        width: 90,
      },
      {
        title: intl.get('hiam.subAccount.model.user.tenant').d('所属租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'edit',
        width: 200,
        fixed: 'right',
        render: (text, record) => {
          const { admin = false } = record;
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '用户管理-编辑',
                    },
                  ]}
                  key="edit"
                  onClick={() => {
                    this.showEditModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'employee',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '用户管理-查看员工',
                    },
                  ]}
                  key="employee"
                  onClick={() => {
                    this.handleViewEmployee(record);
                  }}
                >
                  {intl.get('hzero.common.button.viewEmployee').d('查看员工')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'enable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enable`,
                      type: 'button',
                      meaning: '用户管理-状态',
                    },
                  ]}
                  onClick={() => {
                    this.handleChangeEnabled(record);
                  }}
                >
                  {record.enabled === false
                    ? intl.get(`hiam.subAccount.view.option.status.enable`).d('解冻')
                    : intl.get(`hiam.subAccount.view.option.status.disable`).d('冻结')}
                </ButtonPermission>
              ),
              len: 2,
              title:
                record.enabled === false
                  ? intl.get(`hiam.subAccount.view.option.status.enable`).d('解冻')
                  : intl.get(`hiam.subAccount.view.option.status.disable`).d('冻结'),
            },
            {
              key: 'assign-role',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.role`,
                      type: 'button',
                      meaning: '用户管理-分配角色',
                    },
                  ]}
                  key="assign-role"
                  onClick={() => {
                    this.showAssignRoleModal(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.option.role').d('分配角色')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.role').d('用户组'),
            },
            {
              key: 'userGroup',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.userGroup`,
                      type: 'button',
                      meaning: '用户管理-用户组',
                    },
                  ]}
                  key="userGroup"
                  onClick={() => {
                    this.showGroupModal(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.option.userGroup').d('用户组')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hiam.subAccount.view.option.userGroup').d('用户组'),
            },
            {
              key: 'password',
              ele:
                admin || record.id === currentUserId ? null : (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.password`,
                        type: 'button',
                        meaning: '用户管理-修改密码',
                      },
                    ]}
                    key="password"
                    onClick={() => {
                      this.handleRecordUpdatePassword(record);
                    }}
                  >
                    {intl.get('hiam.subAccount.view.option.passwordUpdate').d('修改密码')}
                  </ButtonPermission>
                ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.passwordUpdate').d('修改密码'),
            },
            {
              key: 'reset',
              ele:
                admin || record.id === currentUserId ? null : (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.reset`,
                        type: 'button',
                        meaning: '子账户管理-重置密码',
                      },
                    ]}
                    key="password"
                    onClick={() => {
                      this.handleRecordResetPassword(record);
                    }}
                  >
                    {intl.get('hiam.subAccount.view.option.resetPassword').d('重置密码')}
                  </ButtonPermission>
                ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.resetPassword').d('重置密码'),
            },
            {
              key: 'unlock',
              ele: record.locked && (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.unlock`,
                      type: 'button',
                      meaning: '用户管理-解除锁定',
                    },
                  ]}
                  key="unlock"
                  onClick={() => {
                    this.handleUnlock(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.option.unlock').d('解除锁定')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.unlock').d('解除锁定'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
  }

  /**
   * 打开密码编辑模态框
   * @param {object} editRecord
   */
  @Bind()
  handleRecordUpdatePassword(editRecord) {
    const {
      dispatch,
      user: {
        currentUser: { phoneCheckFlag },
      },
    } = this.props;
    const { editPasswordModalProps, forceCodeVerify } = this.state;
    dispatch({
      type: 'trSubAccount/getPasswordRule',
      payload: { organizationId: editRecord.organizationId },
    }).then((res) => {
      if (res && forceCodeVerify) {
        if (res && phoneCheckFlag) {
          this.setState({
            editPasswordModalProps: {
              visible: true,
              editRecord,
              validCodeLimitTimeEnd: editPasswordModalProps.validCodeLimitTimeEnd,
              validCodeSendLimitFlag: !!editPasswordModalProps.validCodeLimitTimeEnd,
            },
          });
        } else {
          notification.warning({
            message: intl
              .get('hiam.userInfo.view.confirmBindPhone')
              .d('当前用户未绑定手机号，请先绑定手机号。'),
          });
        }
      } else {
        this.setState({
          editPasswordModalProps: {
            visible: true,
            editRecord,
          },
        });
      }
    });
  }

  /**
   * 重置密码
   * @param {object} editRecord
   */
  @Bind()
  handleRecordResetPassword(editRecord) {
    const {
      user: {
        currentUser: { phoneCheckFlag },
      },
    } = this.props;
    const { editPhoneModalProps, forceCodeVerify } = this.state;
    if (forceCodeVerify) {
      if (phoneCheckFlag) {
        this.setState({
          editPhoneModalProps: {
            visible: true,
            editRecord,
            validCodeLimitTimeEnd: editPhoneModalProps.validCodeLimitTimeEnd,
            validCodeSendLimitFlag: !!editPhoneModalProps.validCodeLimitTimeEnd,
          },
        });
      } else {
        notification.warning({
          message: intl
            .get('hiam.userInfo.view.confirmBindPhone')
            .d('当前用户未绑定手机号，请先绑定手机号。'),
        });
      }
    } else {
      this.setState({
        editPhoneModalProps: {
          visible: false,
          editRecord,
          validCodeLimitTimeEnd: editPhoneModalProps.validCodeLimitTimeEnd,
          validCodeSendLimitFlag: !!editPhoneModalProps.validCodeLimitTimeEnd,
        },
      });
      Modal.confirm({
        title: (
          <span>
            {intl.get('hiam.userInfo.view.confirmResetPassword1').d(`是否确认重置`)}
            <span style={{ color: '#40a9ff99' }}>
              {`${editRecord.realName}(${editRecord.loginName})`}
            </span>
            {intl.get('hiam.userInfo.view.confirmResetPassword2').d('的密码')}
          </span>
        ),
        onOk: () => {
          this.handlePasswordReset();
        },
        onCancel: () => {},
      });
    }
  }

  /**
   * 重置密码
   * @param {object} fieldsValue
   */
  @Bind()
  handlePasswordReset(fieldsValue = {}) {
    const { dispatch } = this.props;
    const {
      editPhoneModalProps: { editRecord = {} },
    } = this.state;
    const { id, organizationId } = editRecord;
    dispatch({
      type: 'trSubAccount/resetPassword',
      payload: {
        userId: id,
        userOrganizationId: organizationId,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handlePhoneModalHidden();
      }
    });
  }

  /**
   * 隐藏密码模态框
   */
  @Bind()
  handlePasswordModalHidden() {
    const { editPasswordModalProps } = this.state;
    this.setState({
      editPasswordModalProps: {
        visible: false,
        validCodeLimitTimeEnd: editPasswordModalProps.validCodeLimitTimeEnd,
        validCodeSendLimitFlag: !!editPasswordModalProps.validCodeLimitTimeEnd,
      },
    });
  }

  /**
   * 发送验证码
   */
  @Bind()
  handleSendCaptcha() {
    const {
      dispatch,
      user: {
        currentUser: { phone },
      },
    } = this.props;
    dispatch({
      type: 'trSubAccount/postCaptcha',
      payload: { phone },
    }).then((res) => {
      if (res) {
        const { editPasswordModalProps, editPhoneModalProps } = this.state;
        this.setState({
          editPasswordModalProps: {
            ...editPasswordModalProps,
            ...res,
          },
          editPhoneModalProps: {
            ...editPhoneModalProps,
            ...res,
          },
        });
      }
    });
  }

  /**
   * 停止计时
   */
  @Bind()
  handleEnd() {
    const { editPasswordModalProps, editPhoneModalProps } = this.state;
    setSession(`tr-sub-account-phone`, 0);
    this.setState({
      editPasswordModalProps: {
        ...editPasswordModalProps,
        validCodeLimitTimeEnd: 0,
        validCodeSendLimitFlag: false,
      },
      editPhoneModalProps: {
        ...editPhoneModalProps,
        validCodeLimitTimeEnd: 0,
        validCodeSendLimitFlag: false,
      },
    });
  }

  /**
   * 隐藏重置密码模态框
   */
  @Bind()
  handlePhoneModalHidden() {
    const { editPhoneModalProps } = this.state;
    this.setState({
      editPhoneModalProps: {
        visible: false,
        validCodeLimitTimeEnd: editPhoneModalProps.validCodeLimitTimeEnd,
        validCodeSendLimitFlag: !!editPhoneModalProps.validCodeLimitTimeEnd,
      },
    });
  }

  /**
   * 更新密码
   * @param {object} fieldsValue
   */
  @Bind()
  handlePasswordUpdate(fieldsValue) {
    const { dispatch } = this.props;
    const {
      editPasswordModalProps: { editRecord = {} },
    } = this.state;
    const { id, organizationId } = editRecord;
    dispatch({
      type: 'trSubAccount/updatePassword',
      payload: {
        userId: id,
        userOrganizationId: organizationId,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handlePasswordModalHidden();
      }
    });
  }

  /**
   * 打开新建模态框
   */
  @Bind()
  showCreateForm() {
    const {
      user: { currentUser: { organizationId } = {} },
    } = this.props;
    this.handlePasswordRule(organizationId);
    this.setState({
      editModalProps: {
        visible: true,
        isCreate: true,
        isAdmin: false,
      },
    });
  }

  @Bind()
  fetchDetailData(record) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/querySubAccount',
      payload: {
        userId: record.id,
      },
    }).then((editRecord) => {
      const { editModalProps } = this.state;
      this.setState({
        editModalProps: {
          ...editModalProps,
          editRecord,
        },
      });
      return editRecord;
    });
  }

  /**
   * 编辑账号
   * 打开账号模态框
   * @param {object} editRecord
   */
  @Bind()
  showEditModal(editRecord) {
    this.setState({
      editModalProps: {
        visible: true,
        isCreate: false,
        isAdmin: editRecord.admin,
        detailRecord: editRecord,
      },
    });
  }

  /**
   * 分配角色
   * 打开角色模态框
   * @param {object} editRecord
   */
  @Bind()
  showAssignRoleModal(editRecord) {
    this.setState({
      assignRoleModalProps: {
        visible: true,
        detailRecord: editRecord,
      },
    });
  }

  /**
   * 分配用户组
   * 打开用户组弹窗
   * @param {object} userRecord
   */
  @Bind()
  showGroupModal(userRecord) {
    this.setState({
      groupModalProps: {
        visible: true,
        isCreate: false,
        userRecord,
      },
    });
  }

  /**
   * 关闭用户组模态框
   */
  @Bind()
  hiddenGroupModal() {
    this.setState({
      groupModalProps: {
        visible: false,
      },
    });
  }

  /**
   * 获取已经分配的角色
   * @param {object} payload
   * @param {number} payload.userId 当前编辑账号id
   * @param {pagination} payload.pagination 分页信息
   */
  @Bind()
  fetchCurrentUserRoles(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/getCurrentUserRoles',
      payload,
    });
  }

  // TODO接口联调
  /**
   * 获取已经分配的用户组
   * @param {object} payload
   * @param {number} payload.userId 当前编辑账号id
   * @param {pagination} payload.pagination 分页信息
   */
  @Bind()
  fetchCurrentUserGroups(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/getCurrentUserGroups',
      payload,
    });
  }

  /**
   * 关闭编辑模态框
   */
  @Bind()
  hiddenEditModal() {
    this.setState({
      editModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  hiddenRoleModal() {
    this.setState({
      assignRoleModalProps: {
        visible: false,
      },
    });
  }

  /**
   * 保存 新建账号 或 更新账号
   * @param saveData
   */
  @Bind()
  handleEditModalOK(saveData) {
    const {
      dispatch,
      trSubAccount: { publicKey },
    } = this.props;
    const {
      editModalProps: { isCreate = true },
    } = this.state;
    if (isCreate) {
      dispatch({
        type: 'trSubAccount/createSubAccount',
        payload: {
          ...saveData,
          password: saveData.password ? encryptPwd(saveData.password, publicKey) : undefined,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.hiddenEditModal();
          this.reloadList();
        }
      });
    } else {
      dispatch({
        type: 'trSubAccount/updateSubAccount',
        payload: saveData,
      }).then((res) => {
        if (res) {
          notification.success();
          this.hiddenEditModal();
          this.reloadList();
        }
      });
    }
  }

  /**
   * 保存 新建账号 或 更新账号
   * @param saveData
   */
  @Bind()
  handleRoleModalOK(saveData) {
    const { dispatch } = this.props;
    dispatch({
      type: 'trSubAccount/saveRole',
      payload: saveData,
    }).then((res) => {
      if (res) {
        notification.success();
        this.hiddenEditModal();
        this.reloadList();
      }
    });
  }

  /**
   * 保存 用户组信息
   * @param saveData
   */
  @Bind()
  handleGroupModalOK(saveData) {
    const { dispatch } = this.props;
    dispatch({
      type: 'trSubAccount/addUserGroup',
      payload: saveData,
    }).then((res) => {
      if (res) {
        notification.success();
        this.hiddenGroupModal();
        this.reloadList();
      }
    });
  }

  /**
   * 解锁账号
   * @param {object} unLockRecord
   */
  @Bind()
  handleUnlock(unLockRecord) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/unlockSubAccount',
      payload: { userId: unLockRecord.id },
    }).then((res) => {
      if (res) {
        this.reloadList();
      }
    });
  }

  /**
   * 查询 可分配角色
   * @param {object} params
   */
  @Bind()
  fetchRoles(params) {
    const { dispatch, currentUserId } = this.props;
    return dispatch({
      type: 'trSubAccount/fetchRoles',
      payload: {
        ...params,
        userId: currentUserId,
      },
    });
  }

  // TODO 接口
  /**
   * 查询 可分配的用户组
   * @param {object} params
   */
  @Bind()
  fetchGroups(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/fetchGroups',
      payload: params,
    });
  }

  /**
   * 删除角色
   * @param {object[]} memberRoleList
   */
  @Bind()
  handleRoleRemove(memberRoleList) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/removeRoles',
      payload: { memberRoleList },
    });
  }

  // TODO 接口
  /**
   * 删除用户组
   * @param {object[]} params
   */
  @Bind()
  handleGroupRemove(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'trSubAccount/deleteUserGroup',
      payload: params,
    });
  }

  // 导出

  /**
   * 获取导出字段查询参数
   */
  @Bind()
  getExportQueryParams() {
    const { checkedKeys } = this.state;
    let fieldsValue = {};
    if (this.filterFormRef) {
      fieldsValue = this.filterFormRef.props.form.getFieldsValue();
      fieldsValue.startDateActive = fieldsValue.startDateActive
        ? fieldsValue.startDateActive.format(DEFAULT_DATE_FORMAT)
        : undefined;
      fieldsValue.endDateActive = fieldsValue.endDateActive
        ? fieldsValue.endDateActive.format(DEFAULT_DATE_FORMAT)
        : undefined;
    }
    return {
      ...fieldsValue,
      authorityTypeQueryParams: join(checkedKeys, ','),
    };
  }

  /**
   * 节点展开
   * @param {string[]} expandedKeys - 展开的节点组成的数组
   */
  @Bind()
  handleExpand(expandedKeys) {
    this.setState({
      expandedKeys,
    });
  }

  /**
   * 选择项变化监控
   * @param {string[]} checkedKeys - 选中项的 key 数组
   */
  @Bind()
  handleSelect(checkedKeys) {
    this.setState({ checkedKeys });
  }

  /**
   * 渲染权限维度的树
   */
  @Bind()
  renderExportTree() {
    const {
      trSubAccount: { dimensionList = [] },
    } = this.props;
    const { expandedKeys, checkedKeys } = this.state;
    if (isEmpty(dimensionList)) {
      return null;
    } else {
      return (
        <Tree
          checkable
          onExpand={this.handleExpand}
          expandedKeys={expandedKeys}
          defaultExpandedKeys={['authorityType']}
          onCheck={this.handleSelect}
          checkedKeys={checkedKeys}
        >
          <TreeNode
            title={intl.get('hiam.subAccount.model.user.authorityType').d('权限维度')}
            key="authorityType"
          >
            {map(dimensionList, (item) => (
              <TreeNode title={item.dimensionName} key={item.dimensionCode} />
            ))}
          </TreeNode>
        </Tree>
      );
    }
  }
}
