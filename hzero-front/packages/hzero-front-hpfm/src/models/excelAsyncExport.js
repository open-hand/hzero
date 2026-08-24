/**
 * model - 单点登录配置
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';

import { query, cancel, download } from '../services/excelAsyncExportService';

export default {
  namespace: 'excelAsyncExport',
  state: {
    pagination: {}, // 分页参数
    excelAsyncExportList: [], // 配置列表
    // excelAsyncExportDetail: {}, // 配置详情
    typeList: [], // 单点登录类别
  },
  effects: {
    // 获取初始化数据
    *init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { typeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          typeList,
        },
      });
    },

    // 获取列表
    *fetchList({ payload }, { put, call }) {
      const res = yield call(query, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            excelAsyncExportList: data.content,
            pagination: createPagination(data),
          },
        });
      }
    },

    // 新建配置
    *cancel({ payload }, { call }) {
      const res = yield call(cancel, payload);
      return getResponse(res);
    },
    // 新建配置
    *download({ payload }, { call }) {
      const res = yield call(download, payload);
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
