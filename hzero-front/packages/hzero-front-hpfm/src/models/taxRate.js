/**
 * taxRate.js - 税率定义 model
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import {
  queryTaxRate,
  updateTaxRate,
  insertTaxRate,
  queryTaxRateDetail,
  queryTaxRateInfo,
} from '../services/taxRateService';

export default {
  namespace: 'taxRate',

  state: {
    list: {}, // 税率定义列表
    pagination: {}, // 分页参数
  },

  effects: {
    // 查询平台级税率定义
    *queryTaxRate({ payload }, { call, put }) {
      const res = yield call(queryTaxRate, payload);
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

    // 新增或更新平台级税率定义
    *saveTaxRate({ payload }, { call }) {
      let res;
      if (payload.taxId) {
        res = yield call(updateTaxRate, payload);
      } else {
        res = yield call(insertTaxRate, payload);
      }
      return getResponse(res);
    },

    // 查询税率定义明细
    *queryTaxRateDetail({ payload }, { call }) {
      const res = yield call(queryTaxRateDetail, payload);
      return getResponse(res);
    },

    // 查询税率定义信息
    *queryTaxRateInfo({ payload }, { call }) {
      const res = yield call(queryTaxRateInfo, payload);
      return getResponse(res);
    },
  },

  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
