/**
 * nlpTextExtractionLog
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-30
 * @copyright 2019-05-30 © HAND
 */

import { createPagination, getResponse } from 'utils/utils';
import { textExtractionLogQuery } from '../services/nlpTextExtractionLogService';

export default {
  namespace: 'nlpTextExtractionLog',
  state: {
    enums: {}, // 值集
    dataSource: [], // 基础数据表格数据源
    pagination: {}, // 基础数据表格分页
  },
  effects: {
    * query({ payload }, { call, put }) {
      const { query } = payload;
      const res = yield call(textExtractionLogQuery, query);
      const retRes = getResponse(res);
      if (retRes) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource: retRes.content,
            pagination: createPagination(retRes),
          },
        });
      }
      return retRes;
    },
  },
  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
