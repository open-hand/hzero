/**
 * model - 租户菜单管理
 * @date: 2019-12-10
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import { isEmpty } from 'lodash';
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue, queryIdpValue } from 'hzero-front/lib/services/api';
import {
  queryTenant,
  queryMenuTree,
  queryCode,
  queryDir,
  checkMenuDirExists,
  queryPermissionSetTree,
  queryPermissionsByMenuIdAll,
  queryLovsByMenuIdAll,
  queryCopyMenuList,
  copyMenu,
  createMenu,
  saveMenu,
  enable,
  disable,
  queryPermissionsByMenuId,
  queryLovByMenuId,
  queryLabelList,
  queryMenuLabel,
} from '../services/tenantMenuManageService';

function defineProperty(obj, property, value) {
  Object.defineProperty(obj, property, {
    value,
    writable: true,
    enumerable: false,
    configurable: true,
  });
}

export default {
  namespace: 'tenantMenuManage',
  state: {
    tenantData: {}, // 租户数据
    pagination: {}, // 分页参数
    code: {}, // 值集集合
    list: {
      // 列表页面数据集合
      dataSource: [], // 表格数据
      pagination: {}, // 分页配置
      flatKey: [], // 用于控制是否全部展开/收起的行数据key集合
    },
    menuPrefixList: [], // 菜单前缀
    menuTypeList: [], // 菜单类型
    customMenu: {
      list: [],
    },
    customizeList: [],
    siteLabelList: [],
    tenantLabelList: [],
  },
  effects: {
    // 获取初始化数据
    *initCustomizeList(_, { call, put }) {
      const customizeList = getResponse(yield call(queryIdpValue, 'HPFM.FLAG'));
      yield put({
        type: 'updateState',
        payload: {
          customizeList,
        },
      });
    },
    *init({ payload }, { put, call }) {
      const { lovCodes } = payload;
      const code = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (code) {
        const { menuPrefix: menuPrefixList = [], menuType: menuTypeList = [] } = code;
        yield put({
          type: 'updateStateReducer',
          payload: {
            menuPrefixList,
            menuTypeList,
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

    // 查询数据
    *queryTenant({ payload }, { call, put }) {
      const response = yield call(queryTenant, payload);
      const tenantData = getResponse(response);
      if (tenantData) {
        yield put({
          type: 'updateState',
          payload: { tenantData, pagination: createPagination(tenantData) },
        });
      }
    },
    // 查询菜单列表树形数据
    *queryTreeList({ params }, { put, call }) {
      const res = yield call(queryMenuTree, { ...params });
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
      const res = yield call(queryDir, params);
      const response = getResponse(res);
      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
    },

    // 检查目录编码是否存在
    *checkMenuDirExists({ params }, { call }) {
      const res = yield call(checkMenuDirExists, params);
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

    // 查询当前复制的节点及其子节点
    *queryCopyMenuList({ params }, { call }) {
      const res = yield call(queryCopyMenuList, params);
      const response = getResponse(res);
      const defaultExpandedRowKeys = [];
      const defaultSelectedRows = [];

      function traverseTree(node) {
        if (!node) {
          return;
        }
        defaultExpandedRowKeys.push(node.id);
        defaultSelectedRows.push(node);
        if (node.subMenus && node.subMenus.length > 0) {
          for (let i = 0; i < node.subMenus.length; i++) {
            traverseTree(node.subMenus[i]);
          }
        }
      }

      if (response) {
        response.forEach((item) => traverseTree(item));
        const copyMenuListDefaultRows = defaultSelectedRows.map((row) => {
          const nextRow = { ...row };
          const { subMenus, ...rest } = nextRow;
          const newValue = { ...rest };
          return newValue;
        });
        return {
          copyMenuList: response || [],
          copyMenuListDefaultRowKeys: defaultExpandedRowKeys || [],
          copyMenuListDefaultRows: copyMenuListDefaultRows || [],
        };
      }
    },

    // 复制并创建菜单
    *copyMenu({ payload }, { call }) {
      const res = yield call(copyMenu, payload);
      return getResponse(res);
    },
    // 新建目录
    *createMenu({ payload }, { call }) {
      const res = yield call(createMenu, payload);
      return getResponse(res);
    },
    // 更新目录
    *saveMenu({ payload }, { call }) {
      const res = yield call(saveMenu, payload);
      return getResponse(res);
    },
    *enable({ payload }, { call }) {
      const res = yield call(enable, payload);
      return getResponse(res);
    },
    *disable({ payload }, { call }) {
      const res = yield call(disable, payload);
      return getResponse(res);
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

    *queryLovByMenuId({ menuId, params }, { call }) {
      const res = yield call(queryLovByMenuId, menuId, params);
      const response = getResponse(res);

      return {
        dataSource: response ? response.content : [],
        pagination: response ? createPagination(response) : {},
      };
    },
  },
  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
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
