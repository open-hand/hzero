// import { isEmpty } from 'lodash';
import {
  // setSession,
  getResponse,
  // getAccessToken,
} from 'utils/utils';

import {
  query as queryUsers,
  queryCurrent,
  queryTenant,
  // queryDefaultRole,
  queryRoleList,
  updateCurrentRole,
  updateCurrentTenant,
  updateDefaultTenant,
  fetchDataHierarchiesList,
  switchDataHierarchies,
  fetchDataHierarchiesSelectList,
  queryDataHierarchies,
} from '../services/user';

export default {
  namespace: 'user',

  state: {
    list: [],
    currentUser: {},
    tenantList: [], // 租户列表
    // defaultRole: {}, // 默认当前用户角色信息
    roleList: [], // 组织层查询分配给当前登录用户的角色列表
    hierarchicalList: [],
    hierarchicalSelectList: [],
    isModal: 0,
    isSelect: 0,
  },

  effects: {
    *fetch(_, { call, put }) {
      const response = yield call(queryUsers);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *fetchCurrent(_, { call, put }) {
      const user = yield call(queryCurrent);
      if (user && !(user instanceof Error)) {
        yield put({
          type: 'saveCurrentUser',
          payload: {
            ...user,
          },
        });
      }
      // yield put({
      //   type: 'global/changeLanguage',
      //   payload: user.language,
      // });
      return user;
    },
    *fetchTenantList(_, { call, put }) {
      const tenant = yield call(queryTenant);
      yield put({
        type: 'saveTenant',
        payload: tenant,
      });
      return tenant;
    },
    *fetchRoleList({ payload: { organizationId } }, { call, put }) {
      const res = yield call(queryRoleList, organizationId);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'saveRoleList',
          payload: response,
        });
      }
    },
    *updateCurrentRole({ payload: { roleId } }, { call }) {
      const res = yield call(updateCurrentRole, roleId);
      return getResponse(res);
    },
    *updateCurrentTenant({ payload: { tenantId } }, { call }) {
      const res = yield call(updateCurrentTenant, tenantId);
      return getResponse(res);
    },
    *updateDefaultTenant({ payload: { tenantId } }, { call }) {
      const res = yield call(updateDefaultTenant, tenantId);
      return getResponse(res);
    },

    *fetchDataHierarchiesList({ payload }, { call, put }) {
      const hierarchicalList = yield call(fetchDataHierarchiesList, payload);
      const response = getResponse(hierarchicalList);
      yield put({
        type: 'updateState',
        payload: { hierarchicalList: response },
      });
      return response;
    },

    *queryDataHierarchies({ payload }, { select, call, put }) {
      const dataHierarchyFlag = yield select(
        (state) => state.user.currentUser && state.user.currentUser.dataHierarchyFlag
      );
      if (dataHierarchyFlag) {
        const hierarchicalList = yield call(queryDataHierarchies, payload);
        const response = getResponse(hierarchicalList);
        if (response) {
          yield put({
            type: 'updateState',
            payload: {
              hierarchicalSelectList: response.selectList || [],
              isModal: response.hasModal,
              isSelect: response.hasSelect,
            },
          });
        }
        return response;
      }
    },

    *fetchDataHierarchiesSelectList({ payload }, { call }) {
      const hierarchicalList = yield call(fetchDataHierarchiesSelectList, payload);
      const response = getResponse(hierarchicalList);
      // yield put({
      //   type: 'updateState',
      //   payload: { hierarchicalList: response },
      // });
      return response;
    },

    *switchDataHierarchies({ payload }, { call }) {
      const hierarchicalList = yield call(switchDataHierarchies, payload);
      const response = getResponse(hierarchicalList);
      return response;
    },
  },

  reducers: {
    save(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
    saveCurrentUser(state, action) {
      return {
        ...state,
        currentUser: action.payload,
      };
    },
    updateCurrentUser(state, action) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          ...action.payload,
        },
      };
    },
    changeNotifyCount(state, action) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          notifyCount: action.payload,
        },
      };
    },
    saveTenant(state, action) {
      return {
        ...state,
        tenantList: action.payload,
      };
    },
    // saveDefaultRole(state, action) {
    //   return {
    //     ...state,
    //     currentUser: action.payload,
    //   };
    // },
    saveRoleList(state, action) {
      return {
        ...state,
        roleList: action.payload,
      };
    },
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
