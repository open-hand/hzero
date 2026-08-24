/**
 * PersonalLoginRecord.js - 个人（登录记录） model
 * @date: 2019-01-10
 * @author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { fetchRecords } from '../services/personalLoginRecordService';

export default {
  namespace: 'personalLoginRecord',
  state: {
    dataSource: [],
    pagination: [],
  },
  effects: {
    // 获取登录记录
    *fetchRecords({ payload }, { call, put }) {
      let result = yield call(fetchRecords, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource: result.content,
            pagination: createPagination(result),
          },
        });
      }
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
