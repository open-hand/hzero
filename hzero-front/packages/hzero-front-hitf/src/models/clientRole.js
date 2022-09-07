/**
 * @date 2018-09-25
 * @author LJ <jun.li06@hand-china.com>
 */
// import { isEmpty } from 'lodash';
import { createPagination, getResponse } from 'utils/utils';
import {
  queryCode,
  queryCreatedSubroles,
  queryRole,
  queryClientRolesAuthList,
  recycle,
  save,
  queryInterfaceData,
} from '../services/clientRoleService';

export default {
  namespace: 'clientRole',
  state: {
    code: {},
    interfaceList: {
      dataSource: [],
      pagination: {},
    },
  },
  effects: {
    *queryList({ params }, { call }) {
      const res = yield call(queryCreatedSubroles, params);
      const response = getResponse(res);

      return {
        dataSource: response.content || [],
        pagination: createPagination(response),
      };
    },
    *queryDetail({ roleId }, { call }) {
      const res = yield call(queryRole, roleId);
      const response = getResponse(res);
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
    *queryDetailList({ roleId, params }, { call }) {
      const res = yield call(queryClientRolesAuthList, roleId, params);
      const response = getResponse(res || {});

      return {
        dataSource: response.content || [],
        pagination: createPagination(response),
      };
    },
    *save({ roleId, data }, { call }) {
      const res = yield call(save, roleId, data);
      return res;
    },
    *recycle({ roleId, data }, { call }) {
      const res = yield call(recycle, roleId, data);
      return res;
    },

    // 查询接口弹窗数据
    *fetchInterfaceData({ payload }, { call, put }) {
      const res = yield call(queryInterfaceData, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateStateReducer',
          payload: {
            interfaceList: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },
  },
  reducers: {
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
  },
};
