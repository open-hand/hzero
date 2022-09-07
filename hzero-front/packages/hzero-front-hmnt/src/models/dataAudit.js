/**
 * model - 数据变更审计
 * @date: 2019/7/10
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryAuditList } from '../services/dataAuditService';

export default {
  namespace: 'dataAudit', // model名称
  state: {
    auditList: {},
  },
  effects: {
    // 查询数据审计列表
    * fetchAuditList({ payload }, { call, put }) {
      let result = yield call(queryAuditList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            auditList: {
              list: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },
  },
  reducers: {
    // 合并state状态数据,生成新的state
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
