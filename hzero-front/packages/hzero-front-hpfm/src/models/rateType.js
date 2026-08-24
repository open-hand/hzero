/**
 * rateType.js - 汇率类型定义 model
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  queryRateType,
  insertRateType,
  deleteRateType,
  updateRateType,
  queryRateTypeDetail,
  queryRateTypeInfo,
} from '../services/rateTypeService';

export default {
  namespace: 'rateType',

  state: {
    list: {}, // 汇率类型列表
    detail: {}, // 汇率类型引用明细
    rateMethodList: [], // 汇率类型方式
    tenantRateTypeList: {}, // 租户汇率类型定义列表
    pagination: {}, // 分页参数对象
  },

  effects: {
    // 查询汇率类型方式
    *queryRateTypeMethod({ payload }, { call, put }) {
      const res = yield call(queryIdpValue, payload);
      const rateMethodList = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { rateMethodList },
      });
    },

    // 查询平台级汇率类型
    *queryRateType({ payload }, { call, put }) {
      const res = yield call(queryRateType, payload);
      const list = getResponse(res);
      const pagination = createPagination(list);
      yield put({
        type: 'updateState',
        payload: {
          list,
          pagination,
        },
      });
    },

    // 新增或更新平台级汇率类型
    *saveRateType({ payload }, { call }) {
      let res;
      if (payload.rateTypeId) {
        res = yield call(updateRateType, payload);
      } else {
        res = yield call(insertRateType, payload);
      }
      return getResponse(res);
    },

    // 删除平台级汇率类型
    *deleteRateType({ payload }, { call }) {
      const res = yield call(deleteRateType, payload);
      return getResponse(res);
    },

    // 查询汇率类型明细
    *queryRateTypeDetail({ payload }, { call, put }) {
      const res = yield call(queryRateTypeDetail, payload);
      const detail = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { detail },
      });
      return getResponse(res);
    },

    // 查询汇率类型信息
    *queryRateTypeInfo({ payload }, { call }) {
      const res = yield call(queryRateTypeInfo, payload);
      return getResponse(res);
    },
  },

  reducers: {
    // 批量条件查询值集头
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
