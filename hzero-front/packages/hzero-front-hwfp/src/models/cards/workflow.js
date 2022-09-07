/**
 * model - 工作台卡片
 * @date: 2019-08-26
 * @author: Wang Tao
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse } from 'utils/utils';
import { queryWorkflow } from '../../services/cards/workflowService';

export default {
  namespace: 'cardWorkflow',
  state: {
    total: 0,
    page: 1,
    pageSize: 10,
    workflowList: [], // 工作流
  },

  effects: {
    // 查询工作流
    * queryWorkflow({ payload }, { call, put }) {
      const data = getResponse(yield call(queryWorkflow, payload));
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            page: data.number + 1,
            pageSize: data.size,
            workflowList: data.content,
            total: data.totalElements,
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
