/**
 * 子账户管理 租户级
 * todo 角色，部分： 1。 别的公司管理员分配的角色怎么办
 * @date 2018-0-07
 * @author WY
 * @email  yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Modal, Tree } from 'hzero-ui';
import { isEmpty, isUndefined, join, map } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import cacheComponent, { deleteCache } from 'components/CacheComponent';
import ExcelExport from 'components/ExcelExport';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import {
  filterNullValueObject,
  getCurrentOrganizationId,
  getCurrentUser,
  encryptPwd,
  setSession,
} from 'utils/utils';
import { openTab } from 'utils/menuTab';
import { HZERO_IAM } from 'utils/config';
import withCustomize from 'components/Customize';

import Search from './Search';
import List from './List';
import EditForm from './EditForm';
import Password from './Password';
import Phone from './Phone';
import UserGroupModal from './UserGroupModal';
import AssignSecGrpDrawer from './AssignSecGrp';
import EmployeeModal from './EmployeeModal';

const { TreeNode } = Tree;

@withCustomize({
  unitCode: [
    'HIAM.SUB_ACCOUND.FILTER',
    'HIAM.SUB_ACCOUND.GRID',
    'HIAM.SUB_ACCOUND.EDIT.FORM_CREATE',
    'HIAM.SUB_ACCOUND.EDIT.FORM_EDIT',
  ],
})
@connect(({ subAccountOrg, loading, userInfo, user }) => ({
  user,
  userInfo,
  subAccountOrg,
  loading: loading.effects,
  addUserGroupLoading: loading.effects['subAccountOrg/addUserGroup'],
  updatingOne: loading.effects['subAccountOrg/updateOne'],
  creatingOne: loading.effects['subAccountOrg/createOne'],
  loadingList: loading.effects['subAccountOrg/fetchList'],
  loadingDetail: loading.effects['subAccountOrg/fetchDetail'],
  updatingPassword: loading.effects['subAccountOrg/updatePassword'],
  loadingCurrentUser: loading.effects['subAccountOrg/roleCurrent'], // 查询当前用户的角色
  loadingDistributeUsers: loading.effects['subAccountOrg/roleQueryAll'], // 查询可分配的所有角色
  deleteRolesLoading: loading.effects['subAccountOrg/deleteRoles'], // 删除已经分配的角色
  querySecGrpLoading: loading.effects['subAccountOrg/querySecurityGroup'],
  deleteSecGrpLoading: loading.effects['subAccountOrg/deleteSecGrp'],
  assignSecGrpLoading: loading.effects['subAccountOrg/assignSecGrp'],
  queryFieldConfigLoading: loading.effects['subAccountOrg/queryFieldConfig'],
  queryDataDimensionLoading: loading.effects['subAccountOrg/queryDataDimension'],
  queryFieldPermissionLoading: loading.effects['subAccountOrg/queryFieldPermission'],
  queryAssignableSecGrpLoading: loading.effects['subAccountOrg/fetchAssignableSecGrp'],
  queryDataPermissionTabLoading: loading.effects['subAccountOrg/queryDataPermissionTab'],
  fetchEmployeeLoading: loading.effects['subAccountOrg/queryEmployee'],
  postCaptchaLoading: loading.effects['subAccountOrg/postCaptcha'],
  phoneLoading: loading.effects['subAccountOrg/resetPassword'],
  organizationId: getCurrentOrganizationId(),
  currentUser: getCurrentUser(),
}))
@formatterCollections({ code: ['hiam.subAccount', 'hiam.securityGroup'] })
@cacheComponent({ cacheKey: '/hiam/sub-account-org/users' })
export default class SubAccountOrg extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      editRecord: {},
      editModalProps: {},
      editFormProps: {},
      groupModalProps: {},
      currentRowData: {}, // 当前选中行数据
      drawerVisible: false, // 安全组模态框是否显示
      employeeVisible: false,
      forceCodeVerify: false,
    };
  }

  /**
   * 组件加载完成后,初始化数据
   */
  componentDidMount() {
    const {
      dispatch,
      organizationId,
      subAccountOrg: { pagination },
    } = this.props;
    dispatch({
      type: 'subAccountOrg/fetchEnum',
    });
    dispatch({
      type: 'subAccountOrg/queryDimension',
    });
    // 获取公钥
    dispatch({
      type: 'subAccountOrg/getPublicKey',
    });
    dispatch({
      type: 'subAccountOrg/getPasswordRule',
      payload: { organizationId },
    }).then((res) => {
      if (res) {
        this.setState({ forceCodeVerify: res.forceCodeVerify });
      }
    });
    this.handleSearch(pagination);
  }

  /**
   * 根据查询条件调用查询接口
   * @param {Object} [pagination={}] 分页信息
   */
  @Bind()
  handleSearch(pagination = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.filterForm)
      ? {}
      : filterNullValueObject(this.filterForm.getFieldsValue());
    dispatch({
      type: 'subAccountOrg/fetchList',
      payload: {
        customizeUnitCode: 'HIAM.SUB_ACCOUND.GRID',
        page: isEmpty(pagination) ? {} : pagination,
        ...filterValues,
      },
    });
  }

  @Bind()
  fetchAllRoles(fields) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'subAccountOrg/roleQueryAll',
      payload: fields,
    });
  }

  /**
   * 查询对应用户 所拥有的角色
   * 给EditForm 使用
   * @param {!Number} payload.userId 用户id
   */
  @Bind()
  fetchUserRoles(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'subAccountOrg/roleCurrent',
      payload,
    });
  }

  /**
   * 获取用户组织树
   * @param {Number} userId - 用户id
   * @memberof SubAccountOrg
   */
  @Bind()
  fetchUserOrgTree(userId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/queryUnitsTree',
      payload: { params: { userId } },
    });
  }

  /**
   * 新建按钮点击
   * 打开编辑弹框,并将isCreate(新建标志)置为true
   */
  @Bind()
  handleCreateBtnClick() {
    const { currentUser, dispatch } = this.props;
    const { forceCodeVerify } = this.state;
    dispatch({
      type: 'subAccountOrg/queryLabelList',
      payload: { level: 'TENANT', type: 'ROLE' },
    }).then(() => {
      this.setState({
        editModalProps: {
          visible: true,
        },
        editFormProps: {
          isCreate: true,
          key: uuid(),
          tenantName: currentUser && currentUser.tenantName,
        },
      });
    });
    dispatch({
      type: 'subAccountOrg/getPasswordRule',
      payload: { organizationId: currentUser.organizationId, forceCodeVerify },
    });
  }

  /**
   * 编辑
   * 打开编辑弹框,并将isCreate(新建标志)置为false
   * 并将 initialValue 设置成当前的账号
   * @param {Object} record 当前操作的记录
   */
  @Bind()
  handleRecordEditBtnClick(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/fetchDetail',
      payload: {
        userId: record.id,
        customizeUnitCode: 'HIAM.SUB_ACCOUND.EDIT.FORM_EDIT',
      },
    }).then((res) => {
      if (res) {
        dispatch({
          type: 'subAccountOrg/queryLabelList',
          payload: { level: record.level === 'site' ? 'SITE' : 'TENANT', type: 'ROLE' },
        });
        this.setState({
          editModalProps: {
            visible: true,
          },
          editFormProps: {
            initialValue: res,
            key: uuid(),
            isAdmin: record.admin,
            tenantName: res.tenantName,
            isCreate: false,
          },
        });
      }
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
   * 保存 用户组信息
   * @param saveData
   */
  @Bind()
  handleGroupModalOK(saveData) {
    const {
      dispatch,
      subAccountOrg: { pagination },
    } = this.props;
    dispatch({
      type: 'subAccountOrg/addUserGroup',
      payload: saveData,
    }).then((res) => {
      if (res) {
        notification.success();
        this.hiddenGroupModal();
        this.handleSearch(pagination);
      }
    });
  }

  // TODO 接口
  /**
   * 删除用户组
   * @param {object[]} memberGroupList
   */
  @Bind()
  handleGroupRemove(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'subAccountOrg/deleteUserGroup',
      payload: params,
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
      type: 'subAccountOrg/fetchGroups',
      payload: params,
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
      type: 'subAccountOrg/getCurrentUserGroups',
      payload,
    });
  }

  /**
   * 授权管理
   * // todo
   * @param {Object} record 当前操作的记录
   */
  @Bind()
  handleRecordAuthManageBtnClick(record) {
    const {
      history,
      match,
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    history.push({
      pathname:
        match.path.indexOf('/private') === 0
          ? '/private/hiam/sub-account-org/authority-management'
          : '/hiam/sub-account-org/authority-management',
      search:
        match.path.indexOf('/private') === 0
          ? `userId=${record.id}&access_token=${accessToken}`
          : `userId=${record.id}`,
    });
  }

  /**
   * 字段权限维护
   * @param record
   */
  @Bind()
  handleApiFieldPermission(record) {
    const {
      dispatch,
      match,
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    deleteCache('/hiam/sub-account-org/api/list');
    deleteCache('/hiam/sub-account-org/api/search-form');
    dispatch(
      routerRedux.push({
        pathname:
          match.path.indexOf('/private') === 0
            ? `/private/hiam/sub-account-org/api/${record.id}`
            : `/hiam/sub-account-org/api/${record.id}`,
        search: match.path.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
      })
    );
  }

  // /**
  //  * // FIXME: 这个按钮的事件没有用到, 到时候需要看下 到底需不需要
  //  * 授权维护
  //  * @param {Object} record 当前操作的记录
  //  */
  // @Bind()
  // handleRecordGrantBtnClick(record) {
  //   // eslint-disable-next-line
  //   console.log(record);
  // }

  /**
   * 重置密码
   * @param {object} editRecord
   */
  @Bind()
  handleRecordResetPassword(record) {
    const {
      dispatch,
      user: {
        currentUser: { phoneCheckFlag },
      },
      subAccountOrg: { phoneProps },
    } = this.props;
    const { forceCodeVerify } = this.state;
    if (forceCodeVerify) {
      if (phoneCheckFlag) {
        dispatch({
          type: 'subAccountOrg/openPhone',
          payload: {
            userInfo: record,
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
      dispatch({
        type: 'subAccountOrg/updateState',
        payload: {
          phoneProps: {
            ...phoneProps,
            visible: false,
            userInfo: record,
          },
        },
      });
      Modal.confirm({
        title: (
          <span>
            {intl.get('hiam.userInfo.view.confirmResetPassword1').d(`是否确认重置`)}
            <span style={{ color: '#40a9ff99' }}>{`${record.realName}(${record.loginName})`}</span>
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
  handlePasswordReset(params = {}) {
    const {
      dispatch,
      subAccountOrg: { phoneProps: { userInfo = {} } = {} },
    } = this.props;
    const { id, organizationId: userOrganizationId } = userInfo;
    dispatch({
      type: 'subAccountOrg/resetPassword',
      payload: { id, userOrganizationId, ...params },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleClosePhone();
      }
    });
  }

  /**
   * handleClosePassword-关闭密码修改模态框
   */
  @Bind()
  handleClosePhone() {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/closePhone',
    });
  }

  /**
   * handleRecordUpdatePassword-修改密码按钮点击
   * @param {Object} record 账号
   */
  @Bind()
  handleRecordUpdatePassword(record) {
    const {
      dispatch,
      user: {
        currentUser: { phoneCheckFlag },
      },
    } = this.props;
    const { forceCodeVerify } = this.state;
    dispatch({
      type: 'subAccountOrg/getPasswordRule',
      payload: { organizationId: record.organizationId, forceCodeVerify },
    }).then((res) => {
      if (res && forceCodeVerify) {
        if (res && phoneCheckFlag) {
          dispatch({
            type: 'subAccountOrg/openPassword',
            payload: {
              userInfo: record,
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
        dispatch({
          type: 'subAccountOrg/openPassword',
          payload: {
            userInfo: record,
          },
        });
      }
    });
    this.setState({ editRecord: record });
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
      type: 'subAccountOrg/postCaptcha',
      payload: { phone },
    });
  }

  /**
   * 停止计时
   */
  @Bind()
  handleEnd() {
    const {
      dispatch,
      subAccountOrg: { passwordProps, phoneProps },
    } = this.props;
    setSession(`sub-account-org-phone`, 0);
    dispatch({
      type: 'subAccountOrg/updateState',
      payload: {
        passwordProps: {
          ...passwordProps,
          validCodeLimitTimeEnd: 0,
          validCodeSendLimitFlag: false,
        },
        phoneProps: {
          ...phoneProps,
          validCodeLimitTimeEnd: 0,
          validCodeSendLimitFlag: false,
        },
      },
    });
  }

  /**
   * handleClosePassword-关闭密码修改模态框
   */
  @Bind()
  handleClosePassword() {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/closePassword',
    });
  }

  /**
   * handlePasswordUpdate-修改密码
   * @param {!Object} params 密码信息
   * @param {!String} params.password 新密码
   * @param {!String} params.password 确认新密码
   */
  @Bind()
  handlePasswordUpdate(params) {
    const {
      dispatch,
      subAccountOrg: { passwordProps: { userInfo = {} } = {} },
    } = this.props;
    const { id, organizationId: userOrganizationId } = userInfo;
    dispatch({
      type: 'subAccountOrg/updatePassword',
      payload: { id, userOrganizationId, ...params },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleClosePassword();
      }
    });
  }

  /**
   * 编辑框确认按钮点击
   * 调用获取数据的接口, 根据isCreate 调用不同的 effect
   * 接口调用成功后,调用查询数据接口,刷新数据的objectVersionNumber
   */
  @Bind()
  handleEditModalOkBtnClick() {
    const {
      subAccountOrg: { pagination, publicKey },
      dispatch,
    } = this.props;
    const {
      editFormProps: { isCreate },
    } = this.state;
    const data = this.editForm.getEditFormData();
    if (!isEmpty(data)) {
      let updateOrCreatePromise;
      if (isCreate) {
        // 创建新的账号
        updateOrCreatePromise = dispatch({
          type: 'subAccountOrg/createOne',
          payload: {
            userInfo: {
              ...data,
              password: data.password ? encryptPwd(data.password, publicKey) : undefined,
            },
            customizeUnitCode: 'HIAM.SUB_ACCOUND.EDIT.FORM_CREATE',
          },
        });
      } else {
        // 更新之前的账号
        updateOrCreatePromise = dispatch({
          type: 'subAccountOrg/updateOne',
          payload: { userInfo: data, customizeUnitCode: 'HIAM.SUB_ACCOUND.EDIT.FORM_EDIT' },
        });
      }
      updateOrCreatePromise.then((res) => {
        if (res) {
          this.setState({
            editModalProps: {
              visible: false,
            },
          });
          notification.success();
          // 更新或保存完毕, 需要刷新 objectVersionNumber
          this.handleSearch(pagination);
        }
      });
    }
  }

  /**
   * 编辑框取消按钮点击
   * 将模态框影藏
   */
  @Bind()
  handleEditModalCancelBtnClick() {
    this.setState({
      editModalProps: {
        visible: false,
      },
    });
  }

  // 导出
  /**
   * 获取导出字段查询参数
   */
  @Bind()
  getExportQueryParams() {
    const { checkedKeys } = this.state;
    const fieldsValue = this.filterForm ? this.filterForm.getFieldsValue() : {};
    return {
      ...fieldsValue,
      authorityTypeQueryParams: join(checkedKeys, ','),
    };
  }

  /**
   * @function handleExpand - 节点展开
   * @param {array} expandedKeys - 展开的节点组成的数组
   */
  @Bind()
  handleExpand(expandedKeys) {
    this.setState({
      expandedKeys,
    });
  }

  /**
   * @function handleSelect - 选择项变化监控
   * @param {array}} checkedKeys - 选中项的 key 数组
   */
  @Bind()
  handleSelect(checkedKeys) {
    this.setState({ checkedKeys });
  }

  // 删除子账户下的角色
  @Bind()
  deleteRoles(memberRoleList) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'subAccountOrg/deleteRoles',
      payload: {
        memberRoleList,
      },
    });
  }

  /**
   * 渲染权限维度的树
   */
  @Bind()
  renderExportTree() {
    const {
      subAccountOrg: { dimensionList = [] },
    } = this.props;
    const { expandedKeys, checkedKeys } = this.state;
    if (isEmpty(dimensionList)) {
      return null;
    }
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

  handleBatchImport() {
    openTab({
      key: `/hiam/sub-account-org/data-import/HIAM.ACCOUNT_CREATE`,
      title: 'hzero.common.title.subAccountImport',
      search: queryString.stringify({
        action: 'hiam.subAccount.view.button.subAccountImport',
        prefixPatch: HZERO_IAM,
      }),
    });
  }

  handleRoleImport() {
    openTab({
      key: `/hiam/sub-account-org/data-import/HIAM.ROLE_CREATE`,
      title: 'hzero.common.title.roleImport',
      search: queryString.stringify({
        action: 'hiam.subAccount.view.button.roleImport',
        prefixPatch: HZERO_IAM,
      }),
    });
  }

  handlePermissionImport() {
    openTab({
      key: `/hiam/sub-account-org/data-import/HIAM.AUTH_CREATE`,
      title: 'hzero.common.title.permissionImport',
      search: queryString.stringify({
        action: 'hiam.subAccount.view.button.permissionImport',
        prefixPatch: HZERO_IAM,
      }),
    });
  }

  handleAuthorityCodeImport() {
    // openTab({
    //   key: `/hiam/sub-account-org/data-import/AUTH_CODE_CREATE`,
    //   search: queryString.stringify({
    //     title: 'hzero.common.title.authorityCodeImport',
    //     action: intl.get('hiam.subAccount.view.button.authorityCodeImport').d('授权编码导入'),
    //   }),
    // });
  }

  /**
   * 显示分配安全组侧滑
   * @param {object} role - 角色管理列表当前行数据
   */
  @Bind()
  openSecurityGroupDrawer(role) {
    const {
      dispatch,
      subAccountOrg: { secGrpPagination = {} },
    } = this.props;
    this.setState({
      currentRowData: role,
    });
    dispatch({
      type: 'subAccountOrg/querySecurityGroup',
      payload: {
        roleId: role.id,
        page: secGrpPagination,
      },
    });
    this.setDrawerVisible(true);
  }

  /**
   * 分配安全组侧滑开关控制
   * @param {boolean} drawerVisible - 是否显示
   */
  @Bind()
  setDrawerVisible(drawerVisible = false, cb = (e) => e) {
    this.setState(
      {
        drawerVisible,
      },
      () => {
        cb();
      }
    );
  }

  /**
   * 关闭安全组侧滑
   */
  @Bind()
  closeSecGrpDrawer(callBack) {
    this.setDrawerVisible(false, callBack);
    this.resetSecGrp();
  }

  /**
   * 清空安全组侧滑的数据
   */
  @Bind()
  resetSecGrp() {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/updateState',
      payload: {
        secGrpList: [], // 安全组列表
        secGrpPagination: {}, // 安全组分页
        secGrpAddModalList: [], // 可分配安全组列表
        secGrpAddModalPagination: {}, // 可分配安全组分页
        secGrpFieldPermissionList: [], // 安全组字段权限列表
        secGrpFieldPermissionPagination: {}, // 安全组字段权限分页
        secGrpDimensionList: [], // 安全组数据权限维度列表
        secGrpDimensionPagination: {}, // 安全组数据权限维度分页
        secGrpDataPermissionTabList: [], // 安全组数据权限tab页
      },
    });
  }

  /**
   * 清空安全组权限数据
   */
  @Bind()
  resetPermissions() {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/updateState',
      payload: {
        secGrpFieldPermissionList: [], // 安全组字段权限列表
        secGrpFieldPermissionPagination: {}, // 安全组字段权限分页
        secGrpDimensionList: [], // 安全组数据权限维度列表
        secGrpDimensionPagination: {}, // 安全组数据权限维度分页
        secGrpDataPermissionTabList: [], // 安全组数据权限tab页
      },
    });
  }

  /**
   * 分配安全组查询表单
   * @param {object} fieldsValue - 查询条件
   */
  @Bind()
  handleSecGrpSearch(fieldsValue) {
    this.handleQuerySecGrps(fieldsValue);
  }

  /**
   * 查询分配安全组列表
   * @param {object} params - 查询参数
   */
  @Bind()
  handleQuerySecGrps(params = {}) {
    const { currentRowData } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/querySecurityGroup',
      payload: {
        roleId: currentRowData.id,
        ...params,
      },
    });
  }

  /**
   * 角色分配安全组
   * @param {array} secGrpList - 选中的安全组
   * @param {function} cb - 回调函数
   */
  @Bind()
  handleAssignSecGrp(secGrpList = [], cb = () => {}) {
    const { currentRowData } = this.state;
    const { dispatch } = this.props;
    const list = secGrpList.map((item) => item.secGrpId);
    return dispatch({
      type: 'subAccountOrg/assignSecGrp',
      roleId: currentRowData.id,
      secGrpList: list,
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.handleQuerySecGrps();
      }
    });
  }

  /**
   * 角色取消分配安全组
   * @param {array} selectedRows - 选中的安全组集合
   */
  @Bind()
  handleDeleteSecGrp(selectedRows = []) {
    const { currentRowData } = this.state;
    const { dispatch } = this.props;
    const list = selectedRows.map((item) => item.secGrpId);
    return dispatch({
      type: 'subAccountOrg/deleteSecGrp',
      payload: { roleId: currentRowData.id, secGrpList: list },
    });
  }

  /**
   * 分配安全组分页切换
   * @param {object} pagination - 分页
   * @param {object} fieldsValue - 表单查询条件
   */
  @Bind()
  handleSecGrpPageChange(pagination = {}, fieldsValue) {
    // 分配客户端分页切换
    this.handleQuerySecGrps({
      page: pagination,
      ...fieldsValue,
    });
  }

  /**
   * 查询角色已分配的指定安全组的字段权限
   * @param {number} secGrpId - 安全组ID
   */
  @Bind()
  fetchFieldPermission(secGrpId) {
    const { dispatch } = this.props;
    const { currentRowData } = this.state;
    dispatch({
      type: 'subAccountOrg/queryFieldPermission',
      payload: { secGrpId, roleId: currentRowData.id },
    });
  }

  /**
   * 查询角色已分配的指定安全组的字段配置列表
   * @param {object} params - 请求参数
   */
  @Bind()
  fetchFieldConfigList(params = {}) {
    const { dispatch } = this.props;
    const { currentRowData } = this.state;
    return dispatch({
      type: 'subAccountOrg/queryFieldConfig',
      payload: { roleId: currentRowData.id, ...params },
    });
  }

  /**
   * 查询角色已分配的指定安全组的数据权限维度
   * @param {number} secGrpId - 安全组ID
   */
  @Bind()
  fetchDataDimension(secGrpId) {
    const { dispatch } = this.props;
    const { currentRowData } = this.state;
    dispatch({
      type: 'subAccountOrg/queryDataDimension',
      payload: { secGrpId, roleId: currentRowData.id },
    });
  }

  /**
   * 查询角色已分配的指定安全组的数据权限的tab页
   * @param {number} secGrpId - 安全组ID
   */
  @Bind()
  fetchDataPermission(secGrpId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/queryDataPermissionTab',
      secGrpId,
    });
  }

  /**
   * 查询可分配的安全组
   * @param {object} params - 查询参数
   */
  @Bind()
  fetchAssignableSecGrp(params = {}) {
    const { currentRowData } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'subAccountOrg/fetchAssignableSecGrp',
      payload: {
        roleId: currentRowData.id,
        ...params,
      },
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
      type: 'subAccountOrg/unlockUser',
      payload: { userId: unLockRecord.id, organizationId: unLockRecord.organizationId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleViewEmployee(record) {
    const { dispatch } = this.props;
    this.setState({
      employeeVisible: true,
      currentUserId: record.id,
    });
    dispatch({
      type: 'subAccountOrg/queryEmployee',
      payload: {
        userId: record.id,
      },
    });
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
      type: 'subAccountOrg/queryEmployee',
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
      type: 'subAccountOrg/queryEmployee',
      payload: {
        userId: currentUserId,
        ...params,
      },
    });
  }

  render() {
    const {
      subAccountOrg: {
        publicKey = '',
        passwordTipMsg = {},
        createSubRoles = [],
        enumMap = {},
        passwordProps = {},
        phoneProps = {},
        unitsTree = [],
        pagination = false,
        dataSource = [],
        secGrpList = [],
        secGrpPagination = {},
        secGrpAddModalList = [],
        secGrpAddModalPagination = {},
        secGrpFieldPermissionList = [],
        secGrpFieldPermissionPagination = {},
        secGrpDimensionList = [],
        secGrpDimensionPagination = {},
        secGrpDataPermissionTabList = [],
        employeeList = [],
        employeePagination = {},
        labelList = [],
      },
      user: {
        currentUser: { phone },
      },
      phoneLoading = false,
      querySecGrpLoading = false,
      postCaptchaLoading = false,
      deleteSecGrpLoading = false,
      assignSecGrpLoading = false,
      queryFieldConfigLoading = false,
      queryDataDimensionLoading = false,
      queryFieldPermissionLoading = false,
      queryAssignableSecGrpLoading = false,
      queryDataPermissionTabLoading = false,
      addUserGroupLoading,
      deleteRolesLoading,
      updatingPassword,
      loadingList,
      loadingDetail,
      updatingOne,
      creatingOne,
      loadingDistributeUsers,
      loadingCurrentUser,
      organizationId,
      currentUser = {},
      match: { path },
      customizeForm,
      customizeTable,
      customizeFilterForm,
      fetchEmployeeLoading,
    } = this.props;
    const {
      editFormProps,
      editModalProps,
      groupModalProps = {},
      editRecord = {},
      drawerVisible,
      currentRowData,
      employeeVisible,
    } = this.state;
    const { visible } = editModalProps;
    const filterProps = {
      customizeFilterForm,
      onFilterChange: this.handleSearch,
      userType: enumMap.userType || [],
      onRef: (node) => {
        this.filterForm = node.props.form;
      },
    };
    const listProps = {
      customizeTable,
      path,
      pagination,
      dataSource,
      currentUserId: currentUser.id,
      loading: loadingList || loadingDetail,
      handleRecordEditBtnClick: this.handleRecordEditBtnClick,
      showGroupModal: this.showGroupModal,
      handleRecordAuthManageBtnClick: this.handleRecordAuthManageBtnClick,
      onApiFieldPermission: this.handleApiFieldPermission,
      // handleRecordGrantBtnClick: this.handleRecordGrantBtnClick,
      handleRecordUpdatePassword: this.handleRecordUpdatePassword,
      searchPaging: this.handleSearch,
      openSecurityGroupDrawer: this.openSecurityGroupDrawer,
      handleUnlock: this.handleUnlock,
      handleViewEmployee: this.handleViewEmployee,
      handleRecordResetPassword: this.handleRecordResetPassword,
      labelList,
    };
    const modalProps = {
      ...editModalProps,
      deleteRolesLoading,
      width: 1000,
      confirmLoading: creatingOne || updatingOne,
      wrapClassName: 'ant-modal-sidebar-right',
      transitionName: 'move-right',
      onOk: this.handleEditModalOkBtnClick,
      onCancel: this.handleEditModalCancelBtnClick,
      title: editFormProps.isCreate
        ? intl.get('hiam.subAccount.view.message.title.userCreate').d('账号新建')
        : intl.get('hiam.subAccount.view.message.title.userEdit').d('账号编辑'),
    };
    const editProps = {
      ...editFormProps,
      passwordTipMsg,
      currentUser,
      path,
      idd: enumMap.idd || [],
      gender: enumMap.gender || [],
      organizationId,
      createSubRoles,
      unitsTree,
      loadingDistributeUsers,
      loadingCurrentUser,
      deleteRoles: this.deleteRoles,
      level: enumMap.level || [],
      userType: enumMap.userType || [],
      fetchUserRoles: this.fetchUserRoles,
      fetchAllRoles: this.fetchAllRoles,
      fetchUserOrgTree: this.fetchUserOrgTree,
      defaultRoleId: editFormProps.initialValue && editFormProps.initialValue.defaultRoleId,
      onRef: (node) => {
        this.editForm = node;
      },
      customizeForm,
      labelList,
    };
    const secGrpDrawerProps = {
      path,
      roleId: currentRowData.id,
      tenantId: currentRowData.tenantId,
      visible: drawerVisible,
      secGrpList,
      secGrpPagination,
      secGrpAddModalList,
      secGrpAddModalPagination,
      secGrpFieldPermissionList,
      secGrpFieldPermissionPagination,
      secGrpDimensionList,
      secGrpDimensionPagination,
      secGrpDataPermissionTabList,
      queryFieldConfigLoading,
      queryDataDimensionLoading,
      queryFieldPermissionLoading,
      queryDataPermissionTabLoading,
      queryLoading: querySecGrpLoading,
      deleteLoading: deleteSecGrpLoading,
      saveModalLoading: assignSecGrpLoading,
      queryModalLoading: queryAssignableSecGrpLoading,
      onCancel: this.closeSecGrpDrawer,
      onDelete: this.handleDeleteSecGrp,
      onFormSearch: this.handleSecGrpSearch,
      onAssignSecGrp: this.handleAssignSecGrp,
      onResetPermissions: this.resetPermissions,
      onFetchDataDimension: this.fetchDataDimension,
      onFetchDataPermission: this.fetchDataPermission,
      onSecGrpPageChange: this.handleSecGrpPageChange,
      fetchAssignableSecGrp: this.fetchAssignableSecGrp,
      onFetchFieldPermission: this.fetchFieldPermission,
      onFetchFieldConfigList: this.fetchFieldConfigList,
    };
    return (
      <>
        <Header title={intl.get('hiam.subAccount.view.message.title').d('子账户管理')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '子账户管理-新建',
              },
            ]}
            type="primary"
            icon="plus"
            onClick={this.handleCreateBtnClick}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.batchImport`,
                type: 'button',
                meaning: '子账户管理-账户导入',
              },
            ]}
            icon="to-top"
            onClick={this.handleBatchImport.bind(this)}
          >
            {intl.get('hiam.subAccount.view.button.batchImport').d('账户导入')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.roleImport`,
                type: 'button',
                meaning: '子账户管理-角色导入',
              },
            ]}
            icon="to-top"
            onClick={this.handleRoleImport.bind(this)}
          >
            {intl.get('hiam.subAccount.view.button.roleImport').d('角色导入')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.permissionImport`,
                type: 'button',
                meaning: '子账户管理-权限导入',
              },
            ]}
            icon="to-top"
            onClick={this.handlePermissionImport.bind(this)}
          >
            {intl.get('hiam.subAccount.view.button.permissionImport').d('权限导入')}
          </ButtonPermission>
          <ExcelExport
            exportAsync
            requestUrl={`${HZERO_IAM}/hzero/v1/${organizationId}/users/export`}
            queryParams={this.getExportQueryParams()}
            queryFormItem={this.renderExportTree()}
            otherButtonProps={{ className: 'label-btn' }}
          />
        </Header>
        <Content>
          <Search {...filterProps} />
          <List {...listProps} />
        </Content>
        {!!visible && (
          <Modal {...modalProps}>
            <EditForm {...editProps} />
          </Modal>
        )}
        <Password
          {...passwordProps}
          phone={phone}
          publicKey={publicKey}
          editRecord={editRecord}
          passwordTipMsg={passwordTipMsg}
          confirmLoading={updatingPassword}
          postCaptchaLoading={postCaptchaLoading}
          onEnd={this.handleEnd}
          onSend={this.handleSendCaptcha}
          onOk={this.handlePasswordUpdate}
          onCancel={this.handleClosePassword}
        />
        <Phone
          {...phoneProps}
          phone={phone}
          editRecord={editRecord}
          confirmLoading={phoneLoading}
          postCaptchaLoading={postCaptchaLoading}
          onEnd={this.handleEnd}
          onSend={this.handleSendCaptcha}
          onOk={this.handlePasswordReset}
          onCancel={this.handleClosePhone}
        />
        <AssignSecGrpDrawer {...secGrpDrawerProps} />
        {groupModalProps.visible && (
          <UserGroupModal
            key="group-modal"
            path={path}
            loading={addUserGroupLoading}
            {...groupModalProps}
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
}
