/**
 * 租户菜单管理
 * @since 2019-12-2
 * @author LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentRole } from 'utils/utils';
import Editor from '../Editor';
import ListTable from './ListTable';
import PermissionSet from '../PermissionSet';
import styles from '../index.less';

const commonPrompt = 'hzero.common';

@connect(({ tenantMenuManage, menuConfig, loading }) => ({
  tenantMenuManage,
  menuConfig,
  queryTreeListLoading: loading.effects['tenantMenuManage/queryTreeList'],
  queryPermissions: loading.effects['tenantMenuManage/queryPermissions'],
  queryParentDir: loading.effects['tenantMenuManage/queryParentDir'],
  queryPermissionSetTree: loading.effects['tenantMenuManage/queryPermissionSetTree'],
  queryPermissionsById: loading.effects['tenantMenuManage/queryPermissionsById'],
  queryPermissionsByIdAll: loading.effects['tenantMenuManage/queryPermissionsByIdAll'], // 已分配的权限
  queryLovsByIdAll: loading.effects['tenantMenuManage/queryLovsByIdAll'], // 已分配的Lov
  queryCopyMenuListLoading: loading.effects['tenantMenuManage/queryCopyMenuList'], // 复制菜单列表
  copyCreateLoading: loading.effects['tenantMenuManage/copyMenu'], // 复制并创建菜单
  saveMenuLoading: loading.effects['tenantMenuManage/saveMenu'], // 复制并创建菜单
  createMenuLoading: loading.effects['tenantMenuManage/createMenu'], // 复制并创建菜单
  enableLoading: loading.effects['tenantMenuManage/enable'], // 复制并创建菜单
  disableLoading: loading.effects['tenantMenuManage/disable'], // 复制并创建菜单

  queryPermissionsByMenuId: loading.effects['tenantMenuManage/queryPermissionsByMenuId'], // 可分配的权限
  queryLovByMenuId: loading.effects['tenantMenuManage/queryLovByMenuId'], // 可分配的Lov
  assignPermissions: loading.effects['menuConfig/assignPermissions'], // 分配权限
  deletePermissions: loading.effects['menuConfig/deletePermissions'], // 删除权限
  setPermissionLoading: loading.effects['menuConfig/setPermissionSetEnable'], // 启用/禁用 权限集

  customMenuLoading: loading.effects['menuConfig/queryOrganizationCustomMenu'], // 客户化菜单
  currentRoleCode: getCurrentRole().code,
}))
@formatterCollections({ code: ['hiam.tenantMenu'] })
export default class TenantMenuManage extends React.Component {
  tenantForm;

  searchForm = React.createRef();

  constructor(props) {
    super(props);
    this.state = {
      permissionSetVisible: false,
      currentRowData: {},
      isCopy: false, // 是否复制
      recordCreateFlag: false,
      editorVisible: false,
      processingEnableRow: null,
      isEdit: false,
      expandedRowKeys: [],
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = {
      menuPrefix: 'HIAM.MENU_PREFIX', // 目录编码前缀
      menuType: 'HIAM.MENU_TYPE', // 菜单类型
    };
    // 初始化 值集
    dispatch({
      type: 'tenantMenuManage/updateState',
      payload: {
        list: {},
      },
    });
    this.props.dispatch({
      type: 'tenantMenuManage/init',
      payload: {
        lovCodes,
      },
    });
    this.fetchAssignLevelCode();
    this.handleFetchData();
    this.handleQueryLabel();
  }

  /**
   * handleQueryLabel - 查询label列表
   * @param {!object} [payload = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  @Bind()
  handleQueryLabel() {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    dispatch({
      type: 'tenantMenuManage/queryLabelList',
      payload: { tenantId: id },
    });
  }

  /**
   * handleQueryLabel - 查询label列表
   * @param {!object} [payload = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  handleQueryMenuLabel(payload) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    return dispatch({
      type: 'tenantMenuManage/queryMenuLabel',
      payload: { payload, tenantId: id },
    });
  }

  /**
   * 查询数据
   */
  @Bind()
  handleFetchData() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'tenantMenuManage/queryTreeList',
      params: { tenantId: id },
    });
  }

  /**
   * fetchAssignLevelCode - 查询层级<HIAM.RESOURCE_LEVEL>code
   * @return {Array}
   */
  @Bind()
  fetchAssignLevelCode() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'tenantMenuManage/queryCode',
      payload: { lovCode: 'HIAM.RESOURCE_LEVEL' },
    });
  }

  /**
   * fetchDir - 查询上级目录
   * @param {object} [params = {}] - 查询参数
   * @param {!string} params.level - 层级
   * @param {Function} cb - 获取成功的回调函数
   */
  @Bind()
  fetchDir(params, cb = (e) => e) {
    const { dispatch } = this.props;
    dispatch({ type: 'tenantMenuManage/queryParentDir', params }).then((res) => {
      if (res) {
        cb(res);
      }
    });
  }

  /**
   * fetchMenuPermissions - 查询菜单权限
   * @param {object} [params = {}] - 查询参数
   * @param {!string} params.level - 层级
   * @param {!number} [params.size=10] - 分页数目
   * @param {!number} [params.page=0] - 当前页
   * @return {Array}
   */
  @Bind()
  fetchMenuPermissions(params) {
    const { dispatch } = this.props;
    const { currentRowData } = this.state;
    return dispatch({
      type: 'tenantMenuManage/queryPermissions',
      params: { ...params, excludeHasAssignedMenuId: currentRowData.id },
    });
  }

  /**
   * fetchPermissionSetTree - 查询菜单权限集树
   * @param {!number} menuId - 菜单ID
   * @param {object} [params = {}] - 查询参数
   * @return {Array}
   */
  @Bind()
  fetchPermissionSetTree(menuId, params) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    return dispatch({
      type: 'tenantMenuManage/queryPermissionSetTree',
      menuId,
      params: { tenantId: id, ...params },
    });
  }

  @Bind()
  fetchPermissionsById(permissionSetId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'tenantMenuManage/queryPermissionsById', permissionSetId, params });
  }

  // 查询权限集下已分配的权限
  @Bind()
  fetchPermissionsByIdAll(permissionSetId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'tenantMenuManage/queryPermissionsByIdAll', permissionSetId, params });
  }

  // 查询权限集下已分配的Lov
  @Bind()
  fetchLovsByIdAll(permissionSetId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'tenantMenuManage/queryLovsByIdAll', permissionSetId, params });
  }

  /**
   * 复制菜单
   * @param {object} record - 表格行数据
   */
  @Bind()
  copy(record) {
    const {
      id,
      code,
      level,
      type,
      levelPath = '',
      description,
      icon,
      quickIndex,
      route,
      sort,
      name,
      parentName,
      virtualFlag,
      parentId,
      tenantId,
    } = record;
    const initData = {
      currentId: id,
      level,
      levelPath,
      code,
      parentId,
      tenantId,
      parentName,
      type,
      description,
      icon,
      quickIndex,
      route,
      sort,
      name,
      virtualFlag,
    };
    this.setState({
      editorVisible: true,
      currentRowData: initData,
      isCopy: true,
      recordCreateFlag: true,
    });
  }

  /**
   * 获取当前节点及其子节点
   */
  @Bind()
  fetchCopyMenuList(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'tenantMenuManage/queryCopyMenuList',
      params,
    });
  }

  /**
   * handleCopyCreate - 复制并创建菜单
   * @param {!object} [payload = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  @Bind()
  handleCopyCreate(payload, cb = (e) => e) {
    const { dispatch } = this.props;
    dispatch({ type: 'tenantMenuManage/copyMenu', payload }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.handleFetchData();
      }
    });
  }

  /**
   * checkMenuDirExists - 校验目录编码是否存在
   * @param {!object} [params = {}] - 条件
   * @param {!string} params.code - 编码
   * @param {!string} params.level - 层级
   * @param {!number} params.tenantId - 租户编码
   * @param {!string} params.type - 类别
   * @return {null|object}
   */
  @Bind()
  checkMenuDirExists(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'tenantMenuManage/checkMenuDirExists',
      params,
    });
  }

  @Bind()
  openPermissionSetDrawer(record) {
    this.setState({
      currentRowData: record,
      permissionSetVisible: true,
    });
  }

  @Bind()
  closePermissionSetDrawer() {
    this.setState({
      currentRowData: {},
      permissionSetVisible: false,
    });
  }

  /**
   * closeEditor - 关闭编辑右侧弹窗
   */
  @Bind()
  closeEditor() {
    this.setState({
      editorVisible: false,
      currentRowData: {},
      isEdit: false,
      isCopy: false,
    });
  }

  /**
   * create - 创建菜单/打开编辑右侧弹窗
   */
  @Bind()
  create(record = {}, recordCreateFlag = false) {
    const { id, code } = record;
    const initData =
      id !== undefined
        ? {
            level: 'organization',
            code,
            parentId: id,
            parentName: record.name,
          }
        : {};
    this.setState({
      recordCreateFlag,
      editorVisible: true,
      currentRowData: initData,
    });
  }

  /**
   * createDir - 创建目录
   * @param {!object} [params = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  createDir(params, cb = (e) => e) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    dispatch({
      type: 'tenantMenuManage/createMenu',
      payload: {
        menu: params,
        tenantId: id,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.handleFetchData();
      }
    });
  }

  /**
   * edit - 编辑菜单/打开编辑右侧弹窗
   * @param {!object} [record = {}] - 当前行数据
   */
  edit(record, isEdit) {
    this.setState({
      editorVisible: true,
      recordCreateFlag: false,
      currentRowData: record,
      isEdit,
    });
  }

  /**
   * 启用 禁用
   * @param {*} record
   * @param {*} paramType
   */
  setMenuEnable(record, paramType) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    this.setState({
      processingEnableRow: record.id,
    });
    dispatch({
      type: paramType === 'disable' ? 'tenantMenuManage/disable' : 'tenantMenuManage/enable',
      payload: {
        id: record.id,
        tenantId: id,
        _token: record._token,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleFetchData();
      }
      this.setState({
        processingEnableRow: record.id,
      });
    });
  }

  /**
   * saveDir - 保存目录
   * @param {!object} [params = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  saveDir(params, cb = (e) => e) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    dispatch({
      type: 'tenantMenuManage/saveMenu',
      payload: {
        menu: params,
        tenantId: id,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.handleFetchData();
      }
    });
  }

  /**
   * savePermissionSet - 更新权限集
   * @param {!object} [params = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  savePermissionSet(params, cb = (e) => e) {
    const { dispatch } = this.props;
    dispatch({ type: 'menuConfig/saveDir', params }).then((res) => {
      if (res) {
        notification.success();
        cb();
      }
    });
  }

  /**
   * createPermissionSet - 新建权限集
   * @param {!object} [params = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  createPermissionSet(params, cb = (e) => e) {
    const { dispatch } = this.props;
    dispatch({ type: 'menuConfig/createDir', params }).then((res) => {
      if (res) {
        notification.success();
        cb();
      }
    });
  }

  setPermissionSetEnable(record, paramType, cb = (e) => e) {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuConfig/setPermissionSetEnable',
      payload: {
        id: record.id,
        _token: record._token,
        paramType,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
      }
    });
  }

  // 查询权限集下可分配的所有权限
  fetchPermissionsByMenuId(menuId, params) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    return dispatch({
      type: 'tenantMenuManage/queryPermissionsByMenuId',
      menuId,
      params: {
        ...params,
        tenantId: id,
      },
    });
  }

  assignPermissions(params = {}) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/assignPermissions',
      payload: {
        ...params,
      },
    });
  }

  deletePermissions(params = {}) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/deletePermissions',
      payload: {
        ...params,
      },
    });
  }

  // 可分配的Lov
  fetchLovByMenuId(menuId, params) {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    return dispatch({
      type: 'tenantMenuManage/queryLovByMenuId',
      menuId,
      params: {
        ...params,
        tenantId: id,
      },
    });
  }

  /**
   * expandAll - 全部展开
   */
  expandAll() {
    const { tenantMenuManage } = this.props;
    const { list } = tenantMenuManage;
    // setSession('hiam-menuConfig-expandedRowKeys', list.rowKeys);
    this.setState({
      expandedRowKeys: list.rowKeys,
    });
  }

  /**
   * collapseAll - 全部收起
   */
  collapseAll() {
    // setSession('hiam-menuConfig-expandedRowKeys', []);
    this.setState({
      expandedRowKeys: [],
    });
  }

  handleRefresh() {
    this.handleFetchData();
  }

  render() {
    const {
      dispatch,
      tenantMenuManage = {},
      match: {
        path,
        params: { id },
      },
      queryTreeListLoading,
      queryPermissions,
      queryParentDir,
      queryPermissionSetTree,
      queryPermissionsById,
      queryPermissionsByIdAll,
      queryLovsByIdAll,
      queryCopyMenuListLoading,
      copyCreateLoading,
      createMenuLoading,
      saveMenuLoading,
      queryPermissionsByMenuId,
      queryLovByMenuId,
      assignPermissions,
      deletePermissions,
      setPermissionLoading,
      enableLoading,
      disableLoading,
      menuConfig = {},
      currentRoleCode,
    } = this.props;
    const { controllerType = [] } = menuConfig;
    const {
      code = {},
      list,
      tenantData = {},
      menuPrefixList = [],
      menuTypeList = [],
      siteLabelList = [],
      tenantLabelList = [],
    } = tenantMenuManage;
    const { content = [] } = tenantData;
    const {
      editorVisible,
      currentRowData,
      permissionSetVisible,
      isCopy,
      recordCreateFlag,
      processingEnableRow,
      isEdit,
      expandedRowKeys = [],
    } = this.state;
    const effects = {
      queryPermissions,
      queryParentDir,
      queryTreeListLoading,
      queryPermissionSetTree,
      queryPermissionsById,
      queryPermissionsByIdAll,
      queryLovsByIdAll,
      queryCopyMenuListLoading,
      copyCreateLoading,
      saveMenuLoading,
      createMenuLoading,
      queryPermissionsByMenuId,
      queryLovByMenuId,
      assignPermissions,
      deletePermissions,
      setPermissionLoading,
    };
    const listProps = {
      path,
      content,
      dispatch,
      menuTypeList,
      onCopy: this.copy,
      queryTreeListLoading,
      menuDataSource: list.dataSource,
      levelCode: code['HIAM.RESOURCE_LEVEL'],
      onExpand: this.onClickExpand,
      handleEdit: this.edit.bind(this),
      handleEnable: this.setMenuEnable.bind(this),
      handleEditPermissionSet: this.openPermissionSetDrawer,
      onCreate: this.create,
      processingEnableRow,
      processing: {
        delete: effects.deleteMenu,
        query: effects.queryTreeList,
        setEnable: effects.setMenuEnable,
        enableLoading,
        disableLoading,
      },
      id,
      expandedRowKeys,
    };
    const editorProps = {
      path,
      menuPrefixList,
      menuTypeList,
      visible: editorVisible,
      handleCheckMenuDirExists: this.checkMenuDirExists,
      levelCode: code['HIAM.RESOURCE_LEVEL'],
      onCancel: this.closeEditor,
      dataSource: currentRowData,
      fetchCopyMenuList: this.fetchCopyMenuList,
      handleQueryDir: this.fetchDir,
      handleQueryPermissions: this.fetchMenuPermissions,
      handleQueryPermissionsBySet: this.fetchPermissionsById,
      handleCopyCreate: this.handleCopyCreate,
      handleCreate: this.createDir.bind(this),
      queryLabel: this.handleQueryMenuLabel.bind(this),
      siteLabelList,
      tenantLabelList,
      processing: {
        queryPermissions: effects.queryPermissions,
        queryDir: effects.queryParentDir,
        queryCopyMenu: effects.queryCopyMenuListLoading,
        copyCreate: effects.copyCreateLoading,
        save: effects.saveMenuLoading,
        create: effects.createMenuLoading,
      },
      isCopy,
      recordCreateFlag,
      processingEnableRow,
      id,
      isEdit,
      handleSave: this.saveDir.bind(this),
    };
    const permissionSetProps = {
      path,
      controllerType,
      currentRoleCode,
      visible: permissionSetVisible,
      menuDataSource: currentRowData,
      close: this.closePermissionSetDrawer,
      handleQueryList: this.fetchPermissionSetTree,
      handleQueryPermissionsBySet: this.fetchPermissionsByIdAll, // 查询权限集下已分配的权限
      handleQueryPermissions: this.fetchPermissionsByMenuId.bind(this), // 查询权限集下可分配的所有权限
      handleQueryLovsBySet: this.fetchLovsByIdAll.bind(this), // 查询权限集下已分配的Lov
      handleQueryLovs: this.fetchLovByMenuId.bind(this), // 查询权限集下可分配的所有Lov
      onAssignPermissions: this.assignPermissions.bind(this),
      onDeletePermissions: this.deletePermissions.bind(this),
      handleSave: this.savePermissionSet.bind(this),
      handleCreate: this.createPermissionSet.bind(this),
      handleEnable: this.setPermissionSetEnable.bind(this),
      // refreshRoutePermissionSetLoading: effects.refreshRoutePermissionSetLoading,
      processing: {
        query: effects.queryPermissionSetTree,
        save: effects.saveDir,
        create: effects.createDir,
        queryPermissionsById: effects.queryPermissionsById,
        queryPermissionsByIdAll: effects.queryPermissionsByIdAll, // 已分配的权限
        queryPermissionsByMenuId: effects.queryPermissionsByMenuId, // 可分配的权限
        queryLovsByIdAll: effects.queryLovsByIdAll, // 已分配的Lov
        queryLovByMenuId: effects.queryLovByMenuId, // 可分配的Lov
        assignPermissions: effects.assignPermissions, // 分配权限
        deletePermissions: effects.deletePermissions, // 删除权限
        setPermissionLoading: effects.setPermissionLoading, // 启用/禁用 权限集
      },
      // save:
    };
    return (
      <>
        <Header
          backPath="/hiam/tenant-menu-manage/list"
          title={intl.get('hiam.tenantMenu.view.message.detailTitle').d('菜单详情')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '菜单配置-新建',
              },
            ]}
            type="primary"
            icon="plus"
            onClick={() => this.create({}, false)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <Button
            icon="sync"
            onClick={this.handleRefresh.bind(this)}
            disabled={effects.queryTreeListLoading}
          >
            {intl.get(`${commonPrompt}.button.refresh`).d('刷新')}
          </Button>
          <Button
            icon="up"
            onClick={this.collapseAll.bind(this)}
            disabled={effects.queryTreeListLoading}
          >
            {intl.get(`${commonPrompt}.button.collapseAll`).d('全部收起')}
          </Button>
          <Button
            icon="down"
            onClick={this.expandAll.bind(this)}
            disabled={effects.queryTreeListLoading}
          >
            {intl.get(`${commonPrompt}.button.expandAll`).d('全部展开')}
          </Button>
        </Header>
        <Content>
          <div className={styles['hiam-tenant-menu-table']}>
            <ListTable {...listProps} />
          </div>
          {editorVisible && <Editor {...editorProps} />}
          <PermissionSet {...permissionSetProps} />
        </Content>
      </>
    );
  }
}
