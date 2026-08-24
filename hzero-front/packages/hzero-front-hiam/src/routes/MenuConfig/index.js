/**
 * MenuConfig - 菜单配置
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { isEmpty, filter, uniqBy, uniq, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import moment from 'moment';
import { Button } from 'hzero-ui';

import ExcelExport from 'components/ExcelExport';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import {
  getCurrentOrganizationId,
  filterNullValueObject,
  getCurrentRole,
  isTenantRoleLevel,
  getSession,
  setSession,
} from 'utils/utils';

import Editor from './Editor';
import MenuImport from './MenuImport';
import MenuExport from './MenuExport';
import PermissionSet from './PermissionSet';
import QueryForm from './Form';
import List from './List';
// import styles from './index.less';

const commonPrompt = 'hzero.common';

/**
 * index - 菜单配置
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} menuConfig - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ loading = {}, menuConfig }) => ({
  saveDir: loading.effects['menuConfig/saveDir'],
  createDir: loading.effects['menuConfig/createDir'],
  queryPermissions: loading.effects['menuConfig/queryPermissions'],
  queryParentDir: loading.effects['menuConfig/queryParentDir'],
  deleteMenu: loading.effects['menuConfig/deleteMenu'],
  queryTreeList: loading.effects['menuConfig/queryTreeList'],
  setMenuEnable: loading.effects['menuConfig/setMenuEnable'],
  queryPermissionSetTree: loading.effects['menuConfig/queryPermissionSetTree'],
  queryPermissionsById: loading.effects['menuConfig/queryPermissionsById'],
  queryPermissionsByIdAll: loading.effects['menuConfig/queryPermissionsByIdAll'], // 已分配的权限
  queryPermissionsByMenuId: loading.effects['menuConfig/queryPermissionsByMenuId'], // 可分配的权限
  queryLovsByIdAll: loading.effects['menuConfig/queryLovsByIdAll'], // 已分配的Lov
  queryLovByMenuId: loading.effects['menuConfig/queryLovByMenuId'], // 可分配的Lov
  assignPermissions: loading.effects['menuConfig/assignPermissions'], // 分配权限
  deletePermissions: loading.effects['menuConfig/deletePermissions'], // 删除权限
  setPermissionLoading: loading.effects['menuConfig/setPermissionSetEnable'], // 启用/禁用 权限集
  customMenuLoading: loading.effects['menuConfig/queryOrganizationCustomMenu'], // 客户化菜单
  exportCustomMenuLoading: loading.effects['menuConfig/exportCustomMenu'], // 客户化菜单导出
  // refreshRoutePermissionSetLoading: loading.effects['menuConfig/refreshRoutePermissionSet'], // 刷新 UI组件权限集
  queryCopyMenuListLoading: loading.effects['menuConfig/queryCopyMenuList'], // 复制菜单列表
  copyCreateLoading: loading.effects['menuConfig/copyMenu'], // 复制并创建菜单
  menuConfig,
  currentRoleCode: getCurrentRole().code,
  isSiteFlag: !isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hiam.menuConfig', 'hiam.menu'] })
export default class MenuConfig extends React.Component {
  constructor(props) {
    super(props);
    this.fetchList = this.fetchList.bind(this);
    // this.closeMenuImportDrawer = this.closeMenuImportDrawer.bind(this);
  }

  state = {
    recordCreateFlag: false, // 标识是否是行上新建
    editorVisible: false,
    // TODO:
    // menuImportVisible: false,
    menuExportVisible: false,
    exportSelectedRowKeys: [],
    exportSelectedRows: [],
    permissionSetVisible: false,
    currentRowData: {},
    expandedRowKeys: getSession('hiam-menuConfig-expandedRowKeys') || [],
    processingDeleteRow: null,
    processingEnableRow: null,
    copyFlag: false, // 是否为复制新建
  };

  /**
   * componentDidMount 生命周期函数
   * render后请求页面数据
   */
  componentDidMount() {
    this.fetchList({ level: 'site', enabledFlag: '1' });
    this.fetchAssignLevelCode();
    this.handleQueryLabel();
    this.fetchSearchLabels();
    const lovCodes = {
      menuPrefix: 'HIAM.MENU_PREFIX', // 目录编码前缀
      menuType: 'HIAM.MENU_TYPE', // 菜单类型
      controllerType: 'HIAM.CONTROLLER_TYPE', // 权限集控制类型
      enabledFlag: 'HPFM.ENABLED_FLAG', // 权限集控制类型
    };
    // 初始化 值集
    this.props.dispatch({
      type: 'menuConfig/init',
      payload: {
        lovCodes,
      },
    });
  }

  /**
   * componentWillUnmount  生命周期函数
   * 组件消失时，清除session缓存
   */
  componentWillUnmount() {
    window.sessionStorage.removeItem('hiam-menuConfig-expandedRowKeys');
  }

  /**
   * @function getRowKeys - 获取查询到数据的parentId
   * @param {!array} collections - 查询获取的数据
   */
  @Bind()
  getRowKeys(collections = [], param = {}, rowKeys = []) {
    const { name, quickIndex, parentName } = param;
    const arr = rowKeys;
    const renderTree = collections.map((item) => {
      const temp = item;
      if (
        ((!isEmpty(name) && temp.name.includes(name)) ||
          (!isEmpty(temp.quickIndex) &&
            !isEmpty(quickIndex) &&
            temp.quickIndex.includes(quickIndex)) ||
          (!isEmpty(temp.parentName) &&
            !isEmpty(parentName) &&
            temp.parentName.includes(parentName))) &&
        !arr.includes(temp.parentId)
      ) {
        arr.push(temp.parentId);
      }
      if (temp.subMenus) {
        temp.subMenus = [...this.getRowKeys(temp.subMenus || [], param, arr).renderTree];
      }
      return temp;
    });
    const renderTree2 = collections.map((item) => {
      const temp = item;
      if (rowKeys.includes(temp.id) && isEmpty(temp.parentId) && !arr.includes(temp.parentId)) {
        arr.push(temp.parentId);
      }
      if (temp.subMenus) {
        temp.subMenus = [...this.getRowKeys(temp.subMenus || [], param, arr).renderTree2];
      }
      return temp;
    });
    return {
      renderTree,
      renderTree2,
      param,
      rowKeys,
    };
  }

  /**
   * 查询搜索字段 标签 数据
   */
  @Bind()
  fetchSearchLabels() {
    const { dispatch } = this.props;
    dispatch({ type: 'menuConfig/querySearchLabels', payload: { type: 'MENU' } });
  }

  /**
   * fetchList - 查询列表数据
   * @param {object} [params = {}] - 查询参数
   * @param {string} params.name - 目录/菜单名
   * @param {string} params.parentName - 上级目录
   */
  fetchList(params = {}, flag) {
    const { dispatch } = this.props;
    dispatch({ type: 'menuConfig/queryTreeList', params }).then((res) => {
      if (res) {
        const { rowKeys } = this.getRowKeys(res, params);
        if (flag) {
          this.setState({
            expandedRowKeys: rowKeys,
          });
          window.sessionStorage.removeItem('hiam-menuConfig-expandedRowKeys');
        } else {
          const { expandedRowKeys } = this.state;
          const arr = uniq(rowKeys.concat(expandedRowKeys));
          this.setState({
            expandedRowKeys: arr,
          });
        }
      }
    });
  }

  /**
   * fetchAssignLevelCode - 查询层级<HIAM.RESOURCE_LEVEL>code
   * @return {Array}
   */
  fetchAssignLevelCode() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/queryCode',
      payload: { lovCode: 'HIAM.RESOURCE_LEVEL' },
    });
  }

  /**
   * fetchDir - 查询上级目录
   * @param {object} [params = {}] - 查询参数
   * @param {!string} params.level - 层级
   * @param {Function} cb - 获取成功的回调函数
   */
  fetchDir(params, cb = (e) => e) {
    const { dispatch } = this.props;
    dispatch({ type: 'menuConfig/queryParentDir', params }).then((res) => {
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
  fetchMenuPermissions(params) {
    const { dispatch } = this.props;
    const { currentRowData } = this.state;
    return dispatch({
      type: 'menuConfig/queryPermissions',
      params: { ...params, excludeHasAssignedMenuId: currentRowData.id },
    });
  }

  /**
   * fetchPermissionSetTree - 查询菜单权限集树
   * @param {!number} menuId - 菜单ID
   * @param {object} [params = {}] - 查询参数
   * @return {Array}
   */
  fetchPermissionSetTree(menuId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'menuConfig/queryPermissionSetTree', menuId, params });
  }

  // 查询权限集下可分配的所有权限
  fetchPermissionsByMenuId(menuId, params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/queryPermissionsByMenuId',
      menuId,
      params,
    });
  }

  // 可分配的Lov
  fetchLovByMenuId(menuId, params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/queryLovByMenuId',
      menuId,
      params,
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

  fetchPermissionsById(permissionSetId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'menuConfig/queryPermissionsById', permissionSetId, params });
  }

  // 查询权限集下已分配的权限
  fetchPermissionsByIdAll(permissionSetId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'menuConfig/queryPermissionsByIdAll', permissionSetId, params });
  }

  // 查询权限集下已分配的Lov
  fetchLovsByIdAll(permissionSetId, params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'menuConfig/queryLovsByIdAll', permissionSetId, params });
  }

  /**
   * create - 创建菜单/打开编辑右侧弹窗
   */
  @Bind()
  create(record = {}, recordCreateFlag = false) {
    const { id, code, level } = record;
    const initData =
      id !== undefined
        ? {
            level,
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
    } = record;
    const initData = {
      currentId: id,
      level,
      levelPath,
      code,
      parentId,
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
      copyFlag: true,
      recordCreateFlag: true,
      editorVisible: true,
      currentRowData: initData,
    });
  }

  /**
   * 获取当前节点及其子节点
   */
  fetchCopyMenuList(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/queryCopyMenuList',
      params,
    });
  }

  /**
   * createDir - 创建目录
   * @param {!object} [params = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  createDir(params, cb = (e) => e) {
    const { dispatch } = this.props;
    const { getFieldsValue = (e) => e } = this.search || {};
    dispatch({ type: 'menuConfig/createDir', params }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.fetchList(getFieldsValue());
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

  /**
   * handleCopyCreate - 复制并创建菜单
   * @param {!object} [payload = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  handleCopyCreate(payload, cb = (e) => e) {
    const { dispatch } = this.props;
    const {
      currentRowData: { level },
    } = this.state;
    const { getFieldsValue = (e) => e } = this.search || {};
    dispatch({
      type: 'menuConfig/copyMenu',
      payload: { data: payload, level: level.toUpperCase() },
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.fetchList(getFieldsValue());
      }
    });
  }

  /**
   * handleQueryLabel - 查询label列表
   * @param {!object} [payload = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  @Bind()
  handleQueryLabel() {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuConfig/queryLabelList',
    });
  }

  /**
   * handleQueryLabel - 查询label列表
   * @param {!object} [payload = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  handleQueryMenuLabel(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/queryMenuLabel',
      payload,
    });
  }

  /**
   * saveDir - 保存目录
   * @param {!object} [params = {}] - form数据
   * @param {Function} cb - 获取成功的回调函数
   */
  saveDir(params, cb = (e) => e) {
    const { dispatch } = this.props;

    const { getFieldsValue = (e) => e } = this.search || {};
    dispatch({ type: 'menuConfig/saveDir', params }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.fetchList(getFieldsValue());
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
   * edit - 编辑菜单/打开编辑右侧弹窗
   * @param {!object} [record = {}] - 当前行数据
   */
  edit(record) {
    this.setState({
      editorVisible: true,
      recordCreateFlag: false,
      currentRowData: record,
    });
  }

  /**
   * delete - 编辑菜单/打开编辑右侧弹窗
   * @param {!object} [record = {}] - 当前行数据
   */
  delete(record) {
    const { dispatch } = this.props;
    const { getFieldsValue = (e) => e } = this.search || {};
    this.setState({
      processingDeleteRow: record.id,
    });
    dispatch({
      type: 'menuConfig/deleteMenu',
      id: record.id,
    }).then((res) => {
      if (res && res.failed) {
        notification.error({ description: res.message });
      } else {
        notification.success();
        this.fetchList(getFieldsValue());
      }
      this.setState({
        processingDeleteRow: null,
      });
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

  setMenuEnable(record, paramType) {
    const { dispatch } = this.props;
    const { getFieldsValue = (e) => e } = this.search || {};
    this.setState({
      processingEnableRow: record.id,
    });
    dispatch({
      type: 'menuConfig/setMenuEnable',
      payload: {
        id: record.id,
        _token: record._token,
        paramType,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchList(getFieldsValue());
      }
      this.setState({
        processingEnableRow: record.id,
      });
    });
  }

  /**
   * closeEditor - 关闭编辑右侧弹窗
   */
  closeEditor() {
    this.setState({
      editorVisible: false,
      currentRowData: {},
      copyFlag: false,
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
  checkMenuDirExists(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'menuConfig/checkMenuDirExists',
      params,
    });
  }

  // TODO:
  // /**
  //  * openMenuImportDrawer - 打开菜单导入抽屉
  //  */
  // openMenuImportDrawer() {
  //   this.setState({
  //     menuImportVisible: true,
  //   });
  // }

  // TODO:
  // /**
  //  * closeMenuImportDrawer - 关闭菜单导入抽屉
  //  */
  // closeMenuImportDrawer() {
  //   this.setState({
  //     menuImportVisible: false,
  //   });
  // }

  /**
   * 查询客户化菜单
   */
  @Bind()
  fetchCustomMenu() {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuConfig/queryOrganizationCustomMenu',
      payload: {},
    });
  }

  /**
   * 打开菜单导出抽屉
   */
  openMenuExportDrawer() {
    this.fetchCustomMenu();
    this.setState({
      menuExportVisible: true,
    });
  }

  /**
   * 关闭菜单导出抽屉
   */
  closeMenuExportDrawer() {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuConfig/updateStateReducer',
      payload: {
        customMenu: {
          list: [],
        },
      },
    });
    this.setState({
      menuExportVisible: false,
      exportSelectedRowKeys: [],
      exportSelectedRows: [],
    });
  }

  /**
   * 选中或取消选中导出的菜单
   * @param {object} record - 当前行数据
   * @param {boolean} selected - 是否选中
   */
  @Bind()
  handleExportRowSelect(record, selected) {
    const { exportSelectedRowKeys, exportSelectedRows } = this.state;
    // 目前只能通过rootNode为true,或parentId=0来判断为根节点。只有根节点为类型为根目录才可以勾选
    if (selected && !record.rootNode) {
      // 子节点被勾选时，其父节点必须已经被勾选，否则会造成断层
      const targetParent = exportSelectedRowKeys.includes(record.parentId);
      if (!targetParent) return;
    }
    const setIdList = [];
    const setRowList = [];
    let nextRows = [];
    const getSubSetIdList = (collections = []) => {
      collections.forEach((n) => {
        setIdList.push(n.id);
        if (selected) {
          setRowList.push(n);
        }
        if (!isEmpty(n.subMenus)) {
          getSubSetIdList(n.subMenus);
        }
      });
    };

    if (!isEmpty(record.subMenus)) {
      getSubSetIdList(record.subMenus);
    }
    setIdList.push(record.id);
    const filteredRowKeys = filter(exportSelectedRowKeys, (item) => !setIdList.includes(item));
    if (selected) {
      setRowList.push(record);
      nextRows = uniqBy(exportSelectedRows.concat(setRowList), 'id');
    } else {
      nextRows = filter(exportSelectedRows, (item) => filteredRowKeys.includes(item.id));
    }
    // exportSelectedRows中存储的是打平的树形数据 不含subMenus
    const nextExportSelectedRows = nextRows.map((row) => {
      const nextRow = { ...row };
      const { subMenus, ...rest } = nextRow;
      const newValue = { ...rest };
      return newValue;
    });
    this.setState({
      exportSelectedRowKeys: selected ? exportSelectedRowKeys.concat(setIdList) : filteredRowKeys,
      exportSelectedRows: nextExportSelectedRows,
    });
  }

  /**
   * 导出客户化菜单
   */
  @Bind()
  handleExport() {
    const { dispatch } = this.props;
    const { exportSelectedRows } = this.state;
    const timeStamp = moment().format('YYYYMMDDhhmmss');
    // 非根节点勾选时，父节点必须勾选
    dispatch({
      type: 'menuConfig/exportCustomMenu',
      payload: exportSelectedRows,
    }).then((res) => {
      if (res) {
        const bl = new Blob([res], { type: 'application/json' });
        const fileName = intl
          .get('hiam.menuConfig.view.message.title.customMenu', { name: timeStamp })
          .d(`客户化菜单_${timeStamp}`);
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(bl);
        link.download = fileName;
        link.click();
      }
    });
  }

  /**
   * 导入客户化菜单后刷新页面
   */
  @Bind()
  refreshList() {
    const { getFieldsValue = (e) => e } = this.search || {};
    this.fetchList(getFieldsValue());
  }

  /**
   * expandAll - 全部展开
   */
  expandAll() {
    const { menuConfig } = this.props;
    const { list } = menuConfig;
    setSession('hiam-menuConfig-expandedRowKeys', list.rowKeys);
    this.setState({
      expandedRowKeys: list.rowKeys,
    });
  }

  /**
   * collapseAll - 全部收起
   */
  collapseAll() {
    setSession('hiam-menuConfig-expandedRowKeys', []);
    this.setState({
      expandedRowKeys: [],
    });
  }

  /**
   * 点击图标时触发
   * @param { boolean } expanded
   * @param { object } record
   */
  @Bind()
  expandClick(expanded, record) {
    let { expandedRowKeys } = this.state;
    const { id } = record;
    expandedRowKeys = expanded
      ? expandedRowKeys.concat(id)
      : expandedRowKeys.filter((item) => item !== id);
    setSession('hiam-menuConfig-expandedRowKeys', expandedRowKeys);
    this.setState({ expandedRowKeys });
  }

  openPermissionSetDrawer(record) {
    this.setState({
      currentRowData: record,
      permissionSetVisible: true,
    });
  }

  closePermissionSetDrawer() {
    this.setState({
      currentRowData: {},
      permissionSetVisible: false,
    });
  }

  /**
   * 获取form数据
   */
  @Bind()
  handleGetFormValue() {
    const formValue = isUndefined(this.search) ? {} : this.search.getFieldsValue();
    const filterValues = filterNullValueObject(formValue);
    return filterValues;
  }

  render() {
    const {
      isSiteFlag,
      menuConfig = {},
      currentRoleCode,
      // loading
      saveDir,
      createDir,
      queryPermissions,
      queryParentDir,
      deleteMenu,
      queryTreeList,
      setMenuEnable,
      queryPermissionSetTree,
      queryPermissionsById,
      queryPermissionsByIdAll,
      queryPermissionsByMenuId,
      queryLovsByIdAll,
      queryLovByMenuId,
      assignPermissions,
      deletePermissions,
      setPermissionLoading,
      customMenuLoading,
      exportCustomMenuLoading,
      // refreshRoutePermissionSetLoading,
      queryCopyMenuListLoading,
      copyCreateLoading,
      match: { path },
      // loading
    } = this.props;
    const {
      recordCreateFlag,
      editorVisible,
      currentRowData,
      menuExportVisible,
      expandedRowKeys,
      processingDeleteRow,
      processingEnableRow,
      permissionSetVisible,
      exportSelectedRowKeys,
      copyFlag,
    } = this.state;
    const {
      code = {},
      list,
      searchLabels = [],
      menuPrefixList = [],
      menuTypeList = [],
      controllerType = [],
      enabledFlag = [],
      customMenu = {},
      siteLabelList = [],
      tenantLabelList = [],
    } = menuConfig;
    const effects = {
      saveDir,
      createDir,
      queryPermissions,
      queryParentDir,
      deleteMenu,
      queryTreeList,
      setMenuEnable,
      queryPermissionSetTree,
      queryPermissionsById,
      queryPermissionsByIdAll,
      queryPermissionsByMenuId,
      queryLovsByIdAll,
      queryLovByMenuId,
      assignPermissions,
      deletePermissions,
      setPermissionLoading,
      // refreshRoutePermissionSetLoading,
      queryCopyMenuListLoading,
      copyCreateLoading,
    };
    const organizationId = getCurrentOrganizationId();
    const formProps = {
      ref: (ref) => {
        this.search = ref;
      },
      handleQueryList: this.fetchList.bind(this),
      levelCode: code['HIAM.RESOURCE_LEVEL'],
      enabledFlag,
      searchLabels,
    };
    const editorProps = {
      path,
      recordCreateFlag,
      isSiteFlag,
      copyFlag,
      menuPrefixList,
      menuTypeList,
      visible: editorVisible,
      handleCheckMenuDirExists: this.checkMenuDirExists.bind(this),
      levelCode: code['HIAM.RESOURCE_LEVEL'],
      onCancel: this.closeEditor.bind(this),
      dataSource: currentRowData,
      fetchCopyMenuList: this.fetchCopyMenuList.bind(this),
      handleQueryDir: this.fetchDir.bind(this),
      handleSave: this.saveDir.bind(this),
      handleCreate: this.createDir.bind(this),
      handleQueryPermissions: this.fetchMenuPermissions.bind(this),
      handleQueryPermissionsBySet: this.fetchPermissionsById.bind(this),
      handleCopyCreate: this.handleCopyCreate.bind(this),
      queryLabel: this.handleQueryMenuLabel.bind(this),
      siteLabelList,
      tenantLabelList,
      processing: {
        save: effects.saveDir,
        create: effects.createDir,
        queryPermissions: effects.queryPermissions,
        queryDir: effects.queryParentDir,
        queryCopyMenu: effects.queryCopyMenuListLoading,
        copyCreate: effects.copyCreateLoading,
      },
    };
    const exportRowSelection = {
      selectedRowKeys: exportSelectedRowKeys,
      onSelect: this.handleExportRowSelect,
    };
    const menuExportProps = {
      visible: menuExportVisible,
      loading: customMenuLoading,
      confirmLoading: exportCustomMenuLoading,
      dataSource: customMenu.list || [],
      menuTypeList,
      rowSelection: exportRowSelection,
      onFecthCustomMenu: this.fetchCustomMenu,
      onCancel: this.closeMenuExportDrawer.bind(this),
      onExport: this.handleExport,
    };
    const menuImportProps = {
      path,
      onReload: this.refreshList,
    };
    const listProps = {
      path,
      menuTypeList,
      levelCode: code['HIAM.RESOURCE_LEVEL'],
      dataSource: list.dataSource,
      handleEdit: this.edit.bind(this),
      handleDelete: this.delete.bind(this),
      handleEditPermissionSet: this.openPermissionSetDrawer.bind(this),
      handleEnable: this.setMenuEnable.bind(this),
      expandClick: this.expandClick,
      processing: {
        delete: effects.deleteMenu,
        query: effects.queryTreeList,
        setEnable: effects.setMenuEnable,
      },
      expandedRowKeys,
      uncontrolled: true,
      processingDeleteRow,
      processingEnableRow,
      organizationId,
      onCreate: this.create,
      onCopy: this.copy,
    };
    const permissionSetProps = {
      path,
      controllerType,
      currentRoleCode,
      visible: permissionSetVisible,
      menuDataSource: currentRowData,
      close: this.closePermissionSetDrawer.bind(this),
      handleQueryList: this.fetchPermissionSetTree.bind(this),
      handleQueryPermissionsBySet: this.fetchPermissionsByIdAll.bind(this), // 查询权限集下已分配的权限
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
        <Header title={intl.get('hiam.menu.view.message.title').d('菜单配置')}>
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
          <Button icon="up" onClick={this.collapseAll.bind(this)} disabled={effects.queryTreeList}>
            {intl.get(`${commonPrompt}.button.collapseAll`).d('全部收起')}
          </Button>
          <Button icon="down" onClick={this.expandAll.bind(this)} disabled={effects.queryTreeList}>
            {intl.get(`${commonPrompt}.button.expandAll`).d('全部展开')}
          </Button>
          {!isSiteFlag && !VERSION_IS_OP && (
            <>
              <MenuImport {...menuImportProps} />
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.exportMenu`,
                    type: 'button',
                    meaning: '菜单配置-导出客户化菜单',
                  },
                ]}
                icon="export"
                onClick={this.openMenuExportDrawer.bind(this)}
              >
                {intl.get('hiam.menuConfig.view.message.title.exportMenu').d('导出客户化菜单')}
              </ButtonPermission>
            </>
          )}
          {isSiteFlag && (
            <ExcelExport
              exportAsync
              requestUrl="/iam/hzero/v1/menus/export-site-data"
              queryParams={this.handleGetFormValue()}
            />
          )}
        </Header>
        <Content>
          <div className="table-list-search">
            <QueryForm {...formProps} />
          </div>
          <List {...listProps} />
        </Content>
        {editorVisible && <Editor {...editorProps} />}
        <MenuExport {...menuExportProps} />
        <PermissionSet {...permissionSetProps} />
      </>
    );
  }
}
