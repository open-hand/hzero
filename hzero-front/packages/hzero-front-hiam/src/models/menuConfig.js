/**
 * menuConfig - 菜单配置model
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { isEmpty } from 'lodash';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  getResponse,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  createPagination,
} from 'utils/utils';
import {
  queryMenuTree,
  queryOrganizationMenuTree,
  checkMenuDirExists,
  queryCode,
  queryDir,
  queryOrganizationDir,
  createDir,
  createOrganizationDir,
  saveDir,
  saveOrganizationDir,
  importMenu,
  importOrganizationMenu,
  queryPermissions,
  deleteMenu,
  deleteOrganizationMenu,
  queryPermissionSetTree,
  setPermissionSetEnable,
  queryPermissionsByMenuId,
  queryPermissionsByMenuIdAll,
  queryLovsByMenuIdAll,
  queryLovByMenuId,
  assignPermissions,
  deletePermissions,
  queryOrganizationCustomMenu,
  exportCustomMenu,
  // refreshRoutePermissionSet,
  queryCopyMenuList,
  copyMenu,
  queryLabelList,
  queryMenuLabel,
  querySearchLabels, // 查询搜索字段标签数据
} from '../services/menuConfigService';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

function defineProperty(obj, property, value) {
  Object.defineProperty(obj, property, {
    value,
    writable: true,
    enumerable: false,
    configurable: true,
  });
}

export default {
  namespace: 'menuConfig',
  state: {
    code: {}, // 值集集合
    list: {
      // 列表页面数据集合
      dataSource: [], // 表格数据
      pagination: {}, // 分页配置
      flatKey: [], // 用于控制是否全部展开/收起的行数据key集合
    },
    menuPrefixList: [], // 菜单前缀
    menuTypeList: [], // 菜单类型
    controllerType: [], // 权限集控制类型
    customMenu: {
      list: [],
    },
    siteLabelList: [],
    tenantLabelList: [],
    searchLabels: [], // 搜索字段标签
  },
  effects: {
    *init({ payload }, { put, call }) {
      const { lovCodes } = payload;
      const code = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (code) {
        const {
          menuPrefix: menuPrefixList = [],
          menuType: menuTypeList = [],
          controllerType = [],
          enabledFlag = [],
        } = code;
        yield put({
          type: 'updateStateReducer',
          payload: {
            menuPrefixList,
            controllerType,
            menuTypeList,
            enabledFlag,
          },
        });
      }
      return code;
    },

    *queryLabelList(_, { put, call }) {
      const res = yield call(queryLabelList, { level: 'SITE', type: 'MENU' });
      const res2 = yield call(queryLabelList, { level: 'TENANT', type: 'MENU' });
      yield put({
        type: 'updateStateReducer',
        payload: {
          siteLabelList: res,
          tenantLabelList: res2,
        },
      });
    },

    *queryMenuLabel({ payload }, { call }) {
      const res = yield call(queryMenuLabel, payload);

      return res;
    },
    *querySearchLabels({ payload }, { put, call }) {
      const response = yield call(querySearchLabels, payload);
      if (response && !response.failed) {
        yield put({
          type: 'updateStateReducer',
          payload: {
            searchLabels: response,
          },
        });
      }
    },
    // 查询菜单列表树形数据
    *queryTreeList({ params }, { put, call }) {
      let res;
      if (organizationRoleLevel) {
        res = yield call(queryOrganizationMenuTree, organizationId, {
          ...params,
          scope: 'CUSTOM',
        });
      } else {
        res = yield call(queryMenuTree, { ...params });
      }
      const response = getResponse(res);
      const rowKeys = [];

      /**
       * 组装新dataSource
       * @function getDataSource
       * @param {!Array} [collections = []] - 树节点集合
       * @param {string} parentName - 上级目录名称
       * @returns {Array} - 新的dataSourcee
       */
      function getDataSource(collections = []) {
        return collections.map((n) => {
          const m = {
            ...n,
          };
          if (!isEmpty(m.subMenus)) {
            rowKeys.push(n.id);
            m.subMenus = getDataSource(m.subMenus);
          } else {
            m.subMenus = null;
          }
          return m;
        });
      }

      const dataSource = getDataSource(response);

      if (response) {
        yield put({
          type: 'updateListReducer',
          payload: {
            dataSource,
            rowKeys,
          },
        });
      }
      return response;
    },

    // 检查目录编码是否存在
    *checkMenuDirExists({ params }, { call }) {
      const res = yield call(checkMenuDirExists, params);
      return res;
    },

    // 查询值集
    *queryCode({ payload }, { put, call }) {
      const response = yield call(queryCode, payload);
      if (response && !response.failed) {
        yield put({
          type: 'setCodeReducer',
          payload: {
            [payload.lovCode]: response,
          },
        });
      }
    },

    // 查询上级目录
    *queryParentDir({ params }, { call }) {
      let res;
      if (organizationRoleLevel) {
        res = yield call(queryOrganizationDir, params, organizationId);
      } else {
        res = yield call(queryDir, params);
      }
      const response = getResponse(res);
      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
    },

    // 创建目录
    *createDir({ params }, { call }) {
      let res;
      if (organizationRoleLevel) {
        res = yield call(createOrganizationDir, params, organizationId);
      } else {
        res = yield call(createDir, params);
      }
      return getResponse(res);
    },

    // 修改目录
    *saveDir({ params }, { call }) {
      let res;
      if (organizationRoleLevel) {
        res = yield call(saveOrganizationDir, params, organizationId);
      } else {
        res = yield call(saveDir, params);
      }
      return getResponse(res);
    },

    // 导入菜单
    *importMenu({ params }, { call }) {
      let res;
      if (organizationRoleLevel) {
        res = yield call(importOrganizationMenu, organizationId, params);
      } else {
        res = yield call(importMenu, params);
      }
      return getResponse(res);
    },

    // 查询菜单权限
    *queryPermissions({ params }, { call }) {
      const res = yield call(queryPermissions, params);
      const response = getResponse(res);
      return {
        dataSource: (response || {}).content || [],
        pagination: createPagination(response || {}),
      };
    },

    // 删除菜单
    *deleteMenu({ id }, { call }) {
      let res;
      if (organizationRoleLevel) {
        res = yield call(deleteOrganizationMenu, id, organizationId);
      } else {
        res = yield call(deleteMenu, id);
      }
      return res;
    },

    // 查询菜单下已分解的权限集树
    *queryPermissionSetTree({ menuId, params }, { call }) {
      const res = yield call(queryPermissionSetTree, menuId, params);
      const response = getResponse(res);
      const defaultExpandedRowKeys = [];

      /**
       * 组装新dataSource
       * @function getDataSource
       * @param {!Array} [collections = []] - 树节点集合
       * @param {string} parentName - 上级目录名称
       * @returns {Array} - 新的dataSourcee
       */
      function getDataSource(collections = [], parentId, keyPath = []) {
        return collections.map((n) => {
          const m = {
            ...n,
            key: n.id,
          };
          if (isEmpty(m.keyPath)) {
            defineProperty(m, 'keyPath', isEmpty(keyPath) ? [] : keyPath);
          }
          m.keyPath = keyPath.concat(parentId);
          if (!isEmpty(m.subMenus)) {
            m.subMenus = getDataSource(m.subMenus, m.id, m.keyPath);
            defaultExpandedRowKeys.push(m.id);
          } else {
            m.subMenus = null;
          }
          return m;
        });
      }

      const dataSource = getDataSource(response || []);
      return { dataSource, defaultExpandedRowKeys };
    },

    // 启用禁用权限集及其下的所有子权限集
    *setPermissionSetEnable({ payload }, { call }) {
      const res = yield call(setPermissionSetEnable, payload);
      return getResponse(res);
    },

    // 启用菜单/目录及其下的所有子权限集
    *setMenuEnable({ payload }, { call }) {
      const res = yield call(setPermissionSetEnable, payload);
      return getResponse(res);
      // return res === 'success' ? res : JSON.parse(res || '{}');
    },

    // 查询权限集下可分配的所有权限
    *queryPermissionsByMenuId({ menuId, params }, { call }) {
      const res = yield call(queryPermissionsByMenuId, menuId, params);
      const response = getResponse(res);

      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
    },
    *queryPermissionsById({ permissionSetId, params }, { call }) {
      const res = yield call(queryPermissionsByMenuId, permissionSetId, params);
      const response = getResponse(res);

      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
    },
    // 查询权限集下已分配的权限
    *queryPermissionsByIdAll({ permissionSetId, params }, { call }) {
      const res = yield call(queryPermissionsByMenuIdAll, permissionSetId, params);
      const response = getResponse(res);
      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
      // return getResponse(res);
    },
    // 查询权限集下已分配的Lov
    *queryLovsByIdAll({ permissionSetId, params }, { call }) {
      const res = yield call(queryLovsByMenuIdAll, permissionSetId, params);
      const response = getResponse(res);
      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
      // return getResponse(res);
    },
    // 查询权限集下可分配的所有Lov
    *queryLovByMenuId({ menuId, params }, { call }) {
      const res = yield call(queryLovByMenuId, menuId, params);
      const response = getResponse(res);

      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
    },
    // 为权限集分配权限(包括Lov)
    *assignPermissions({ payload }, { call }) {
      const res = yield call(assignPermissions, payload);
      return getResponse(res);
    },
    // 回收权限集的权限(包括Lov)
    *deletePermissions({ payload }, { call }) {
      const res = yield call(deletePermissions, payload);
      return getResponse(res);
    },

    // 查询客户化菜单
    *queryOrganizationCustomMenu({ payload }, { call, put }) {
      const res = yield call(queryOrganizationCustomMenu, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateStateReducer',
          payload: {
            customMenu: {
              list: response,
            },
          },
        });
      }
    },

    // 导出客户化菜单
    *exportCustomMenu({ payload }, { call }) {
      const res = yield call(exportCustomMenu, payload);
      return getResponse(res);
    },

    // // 刷新菜单对应你 UI组件权限集
    // * refreshRoutePermissionSet({ payload }, { call }) {
    //   const { menuId } = payload;
    //   const res = yield call(refreshRoutePermissionSet, menuId);
    //   return getResponse(res);
    // },

    // 查询当前复制的节点及其子节点
    *queryCopyMenuList({ params }, { call }) {
      const res = yield call(queryCopyMenuList, params);
      const response = getResponse(res);
      const defaultExpandedRowKeys = [];
      const defaultSelectedRows = [];
      const relatedRowKeys = [];

      function traverseTree(node) {
        if (!node) {
          return;
        }
        defaultExpandedRowKeys.push(node.id);
        relatedRowKeys.push({ id: node.id, parentId: node.parentId });
        defaultSelectedRows.push(node);
        if (node.subMenus && node.subMenus.length > 0) {
          for (let i = 0; i < node.subMenus.length; i++) {
            traverseTree(node.subMenus[i]);
          }
        }
      }

      if (response) {
        response.forEach((item) => traverseTree(item));
        return {
          copyMenuList: response || [],
          copyMenuListDefaultRowKeys: defaultExpandedRowKeys || [],
          relatedRowKeys: relatedRowKeys || [],
        };
      }
    },

    // 复制并创建菜单
    *copyMenu({ payload }, { call }) {
      const res = yield call(copyMenu, payload);
      return getResponse(res);
    },
  },
  reducers: {
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    updateListReducer(state, { payload }) {
      return {
        ...state,
        list: {
          ...state.list,
          ...payload,
        },
      };
    },
  },
};
