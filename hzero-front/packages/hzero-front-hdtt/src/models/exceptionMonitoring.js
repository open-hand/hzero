/**
 * model - 生产消费异常监控
 * @date: 2019/5/6
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { queryException, handleError } from '../services/exceptionMonitoringService';

export default {
  namespace: 'exceptionMonitoring', // model名称
  state: {
    exceptionList: [], // 生产消费异常监控列表数据
    pagination: {}, // 分页参数
    errorTypes: [], // 错误类型
    eventTypes: [], // 事件类型
  },
  effects: {
    // 查询异常列表
    * fetchExceptionList({ payload }, { call, put }) {
      let result = yield call(queryException, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            exceptionList: result.content,
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

    * fetchSelect(_, { call, put }) {
      const result = getResponse(
        yield call(queryMapIdpValue, {
          errorTypes: 'HDTT.EVENT_ERR_TYPE',
          eventTypes: 'HDTT.EVENT_TYPE',
        })
      );
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            ...result,
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
