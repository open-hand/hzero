/**
 * PlatformManager.js - 平台管理员 model
 * @date: 2019-01-10
 * @author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';
import { clearLogs, fetchMembers } from '../services/platformManagerService';

export default {
  namespace: 'platformManager',
  state: {
    list: [],
    pagination: [],
    typeList: [], // 审计类型
    clearTypeList: [], // 清除类型
  },
  effects: {
    // 获取初始化数据
    *init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(
        yield call(queryMapIdpValue, { ...lovCodes, clearTypeList: 'HPFM.AUDIT_LOG.CLEAR_TYPE' })
      );
      const { typeList, clearTypeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          typeList,
          clearTypeList,
        },
      });
    },

    // 清除日志
    *clearLogs({ payload }, { call }) {
      const result = yield call(clearLogs, payload);
      return getResponse(result);
    },

    // 获取登录记录
    *fetchMembers({ payload }, { call, put }) {
      let result = yield call(fetchMembers, payload);
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
