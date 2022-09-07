/**
 * @date: 2018-9-29
 * @author: LZH <zhaohui.liu@hand-china.com>
 */

import { createPagination, getResponse, getCurrentOrganizationId } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';
import { fetchLogsList, fetchLogsDetail, clearLogs, retry } from '../services/interfaceLogsService';

export default {
  namespace: 'interfaceLogs',

  state: {
    dataList: [],
    pagination: {},
    detail: {},
    query: {},
    clearTypeList: [], // 清除类型
  },

  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, { clearTypeList: 'HITF.INTERFACE_LOG.CLEAR_TYPE' })
      );
      const { clearTypeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          clearTypeList,
        },
      });
    },

    // 清除日志
    *clearLogs({ payload }, { call }) {
      const result = yield call(clearLogs, payload);
      return getResponse(result);
    },

    *fetchLogsList({ payload }, { call, put }) {
      const { ...query } = payload;
      const result = getResponse(yield call(fetchLogsList, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            query,
            dataList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    *fetchLogsDetail({ payload }, { call, put }) {
      const organizationId = getCurrentOrganizationId();
      const { interfaceLogId, invokeKey } = payload;
      const result = getResponse(
        yield call(fetchLogsDetail, { interfaceLogId, invokeKey }, organizationId)
      );
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detail: result,
          },
        });
      }
    },

    // 重试
    *retry({ payload }, { call }) {
      const result = yield call(retry, payload);
      return getResponse(result);
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
