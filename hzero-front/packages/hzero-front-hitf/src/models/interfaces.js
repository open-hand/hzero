/**
 * @date 2018-09-25
 * @author LJ <jun.li06@hand-china.com>
 */

import { createPagination, getResponse } from 'utils/utils';
import { getPublicKey } from 'services/api';
import {
  queryList,
  queryCode,
  queryDetail,
  queryAuthSelfList,
  queryAuthSelfListDetail,
  createAuthSelf,
  updateAuthSelf,
  deleteAuthSelf,
  batchAddAuth,
} from '../services/interfacesService';

export default {
  namespace: 'interfaces',
  state: {
    code: {},
    publicKey: '', // 密码公钥
  },
  effects: {
    *queryList({ params }, { call }) {
      const res = yield call(queryList, params);
      const response = getResponse(res);
      if (response) {
        return {
          dataSource: response.content || [],
          pagination: createPagination(response),
        };
      }
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
    *queryDetail({ interfaceId }, { call }) {
      const res = yield call(queryDetail, interfaceId);
      const response = getResponse(res);
      return response;
    },
    *queryAuthSelfList({ interfaceId, params }, { call }) {
      const res = yield call(queryAuthSelfList, interfaceId, params);
      const response = getResponse(res);
      return {
        dataSource: response.content || [],
        pagination: createPagination(response),
      };
    },
    *queryAuthSelfListDetail({ interfaceId, interfaceAuthId }, { call }) {
      const res = yield call(queryAuthSelfListDetail, interfaceId, interfaceAuthId);
      const response = getResponse(res);
      return response;
    },
    *createAuthSelf({ interfaceId, data }, { call }) {
      const res = yield call(createAuthSelf, interfaceId, data);
      return res;
    },
    *updateAuthSelf({ interfaceId, data }, { call }) {
      const res = yield call(updateAuthSelf, interfaceId, data);
      return res;
    },
    *deleteAuthSelf({ interfaceId, data }, { call }) {
      const res = yield call(deleteAuthSelf, interfaceId, data);
      return res;
    },

    // 批量添加授权
    *batchAddAuth({ payload }, { call }) {
      const res = getResponse(yield call(batchAddAuth, payload));
      return res;
    },
    // *save({ data }, { call }) {
    //   const res = yield call(save, data);
    //   return res;
    // },
    // 请求公钥
    *getPublicKey(_, { call, put }) {
      const res = yield call(getPublicKey);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            publicKey: res.publicKey,
          },
        });
      }
      return res;
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
