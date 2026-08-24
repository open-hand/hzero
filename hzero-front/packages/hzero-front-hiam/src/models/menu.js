/**
 * @date 2018-07-06
 * @author  LJ <jun.li06@hand-china.com>
 */
import { upperFirst, isEmpty, isNumber } from 'lodash';
import { getResponse } from 'utils/utils';
import {
  queryMenuTree,
  createMenuDir,
  saveMenuTree,
  saveMenuDir,
  deleteMenuTree,
  customMenuImport,
  queryMenuTreeByOrganizationId,
  createMenuDirByOrganizationId,
  checkMenuDirExistsByOrganizationId,
  saveMenuTreeByOrganizationId,
} from '../services/menuService';

function defineProperty(obj, property, value) {
  Object.defineProperty(obj, property, {
    value,
    writable: true,
    enumerable: false,
    configurable: true,
  });
}

function defineLevel(obj, level) {
  defineProperty(obj, '$level', level);
}

function defineParentName(obj, name) {
  defineProperty(obj, '$parentName', name);
}

function getDataSource(collections, newLevel = -1, parentName) {
  let level = newLevel;
  level++;
  collections.map(n => {
    const menu = n;
    const { subMenus, name } = menu;
    defineLevel(menu, level);
    if (parentName) {
      defineParentName(menu, parentName);
    }
    if (!isEmpty(menu.subMenus)) {
      getDataSource(subMenus, level, name);
    }
    return menu;
  });
  return collections;
}

function setSort(data) {
  data.forEach((value, index) => {
    Object.assign(value, { sort: index });
    if (value.subMenus) {
      setSort(value.subMenus);
    }
  });
  return data;
}

export default {
  namespace: 'menu',
  state: {
    code: {},
    platform: {
      dataSource: [],
      refresh: false,
    },
    organization: {
      dataSource: [],
      refresh: false,
    },
    project: {
      dataSource: [],
      refresh: false,
    },
    user: {
      dataSource: [],
      refresh: false,
    },
  },
  effects: {
    * queryMenuTree({ activeKey, params, organizationId }, { put, call }) {
      let response;
      if (isNumber(organizationId) && organizationId !== 0) {
        response = yield call(queryMenuTreeByOrganizationId, organizationId, params);
      } else {
        response = yield call(queryMenuTree, params);
      }

      if (response && !response.failed && activeKey) {
        const dataSource = getDataSource(response);
        yield put({
          type: `update${upperFirst(activeKey)}Reducer`,
          payload: {
            dataSource,
            refresh: false,
          },
        });
      }
    },
    * createMenuDir({ payload = {} }, { call }) {
      const response = yield call(
        isNumber(payload.organizationId) ? createMenuDirByOrganizationId : createMenuDir,
        payload
      );
      return !isEmpty(getResponse(response));
    },
    * saveMenuDir({ payload }, { call }) {
      const response = yield call(saveMenuDir, payload);
      return !isEmpty(getResponse(response));
    },
    * deleteMenuTree({ payload }, { call }) {
      const response = yield call(deleteMenuTree, payload);
      return response;
    },
    * customMenuImport({ organizationId, params }, { call }) {
      const res = yield call(customMenuImport, organizationId, params);
      return getResponse(res);
    },
    * updateTableDataSource({ payload }, { put }) {
      const { activeKey, dataSource } = payload;
      yield put({
        type: `update${upperFirst(activeKey)}Reducer`,
        payload: {
          dataSource: getDataSource(dataSource),
        },
      });
    },
    * checkMenuDirExists({ organizationId, customFlag, params }, { call }) {
      const res = yield call(
        checkMenuDirExistsByOrganizationId,
        organizationId,
        customFlag,
        params
      );
      return res;
    },
    * saveMenuTree({ params, level, organizationId }, { call }) {
      let res;
      if (isNumber(organizationId) && organizationId !== 0) {
        res = yield call(saveMenuTreeByOrganizationId, organizationId, params, level);
      } else {
        res = yield call(saveMenuTree, setSort(params), level);
      }
      return getResponse(res);
    },
  },
  reducers: {
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    updatePlatformReducer(state, { payload }) {
      return {
        ...state,
        platform: {
          ...state.platform,
          ...payload,
        },
      };
    },
    updateOrganizationReducer(state, { payload }) {
      return {
        ...state,
        organization: {
          ...state.organization,
          ...payload,
        },
      };
    },
    updateProjectReducer(state, { payload }) {
      return {
        ...state,
        project: {
          ...state.project,
          ...payload,
        },
      };
    },
    updateUserReducer(state, { payload }) {
      return {
        ...state,
        user: {
          ...state.user,
          ...payload,
        },
      };
    },
    updateRefreshReducer(state) {
      return {
        ...state,
        platform: { ...state.platform, refresh: true },
        project: { ...state.project, refresh: true },
        user: { ...state.user, refresh: true },
        organization: { ...state.organization, refresh: true },
      };
    },
  },
};
