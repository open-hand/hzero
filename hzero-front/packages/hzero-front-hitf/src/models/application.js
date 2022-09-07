/**
 * @date 2018-09-25
 * @author LJ <jun.li06@hand-china.com>
 */

import { createPagination, getResponse } from 'utils/utils';
import {
  queryList,
  queryDetail,
  queryCode,
  save,
  create,
  deleteApplication,
  deleteLines,
} from '../services/applicationService';

export default {
  namespace: 'application',
  state: {
    code: {},
    list: {
      dataSource: [],
      pagination: createPagination({ number: 0, size: 10, totalElements: 0 }),
    },
  },
  effects: {
    * queryList({ params }, { call, put }) {
      const res = yield call(queryList, params);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateStateReducer',
          payload: {
            list: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },
    * queryDetail({ applicationId }, { call }) {
      const res = yield call(queryDetail, applicationId);
      return getResponse(res);
    },
    * save({ data }, { call }) {
      const res = yield call(save, data);
      return getResponse(res);
    },
    * create({ data }, { call }) {
      const res = yield call(create, data);
      return getResponse(res);
    },
    * deleteApplication({ data }, { call }) {
      const res = yield call(deleteApplication, data);
      return res;
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
    // 删除行
    * deleteLines({ assignIds }, { call }) {
      const res = getResponse(yield call(deleteLines, assignIds));
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
