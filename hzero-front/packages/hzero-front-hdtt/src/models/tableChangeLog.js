/**
 * model - 生表结构变更
 * @date: 2019-7-18
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { queryChangeLog, handleError } from '../services/tableChangeLogService';

export default {
  namespace: 'tableChangeLog', // model名称
  state: {
    list: [], // 表结构变更列表数据
    pagination: {}, // 分页参数
    statusTypes: [], // 状态类型
  },
  effects: {
    // 查询日志列表
    * fetchLogList({ payload }, { call, put }) {
      let result = yield call(queryChangeLog, payload);
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

    // 处理错误
    * handleError({ payload }, { call }) {
      const response = yield call(handleError, payload);
      return getResponse(response);
    },

    // 查询初始化状态
    * fetchInitStatus(_, { call, put }) {
      const statusTypes = getResponse(yield call(queryIdpValue, 'HDTT.EVENT_PROCESS_STATUS'));
      yield put({
        type: 'updateState',
        payload: {
          statusTypes,
        },
      });
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
