/**
 * model 我的抄送流程
 * @date: 2018-8-14
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import uuid from 'uuid/v4';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { getResponse, createPagination } from 'utils/utils';
import {
  fetchTaskList,
  searchDetail,
  fetchForecast,
  taskRecall,
  fetchHistoryApproval,
} from '../services/carbonCopyTaskService';

export default {
  namespace: 'carbonCopyTask',
  state: {
    list: [], // 数据列表
    pagination: {}, // 分页器
    processStatus: [], // 流程状态
    detail: {}, // 明细
    forecast: [], // 流程图数据
    uselessParam: 'init', // 确保获取最新流程图的参数
  },
  effects: {
    // 查询流程状态
    *queryProcessStatus(_, { call, put }) {
      const processStatus = getResponse(yield call(queryIdpValue, 'HWFP.PROCESS_APPROVE_STATUS'));
      yield put({
        type: 'updateState',
        payload: { processStatus },
      });
    },
    *fetchTaskList({ payload }, { call, put }) {
      let result = yield call(fetchTaskList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    *fetchDetail({ payload }, { call, put }) {
      let result = yield call(searchDetail, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detail: result,
            uselessParam: uuid(),
          },
        });
      }
      return result;
    },

    *fetchForecast({ payload }, { call, put }) {
      let result = yield call(fetchForecast, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            forecast: result,
          },
        });
      }
      return result;
    },

    // 撤回
    *taskRecall({ payload }, { call }) {
      const res = yield call(taskRecall, payload);
      return getResponse(res);
    },

    // 查询审批历史记录
    *fetchHistoryApproval({ params }, { call }) {
      const res = yield call(fetchHistoryApproval, params);
      return getResponse(res);
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
