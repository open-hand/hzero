/**
 * @date 2018-09-25
 * @author LJ <jun.li06@hand-china.com>
 */

import { createPagination, getResponse } from 'utils/utils';
import {
  queryList,
  queryCode,
  queryDetail,
  save,
  deleteAuthRole,
} from '../services/clientAuthService';

export default {
  namespace: 'clientAuth',
  state: {
    code: {},
  },
  effects: {
    * queryList({ params }, { call }) {
      const res = yield call(queryList, params);
      const response = getResponse(res);
      return {
        dataSource: response.content || [],
        pagination: createPagination(response),
      };
    },
    // 查询值集
    * queryCode({ payload }, { put, call }) {
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
    * queryDetail({ payload }, { call }) {
      const res = yield call(queryDetail, payload);
      const response = getResponse(res);
      return response;
    },
    * save({ data }, { call }) {
      const res = yield call(save, data);
      return res;
    },
    * deleteAuthRole({ clientOauthId, data }, { call }) {
      const res = yield call(deleteAuthRole, clientOauthId, data);
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
