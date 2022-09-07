/**
 * DimensionConfig - 数据维度配置 model
 * @date:  2019-7-16
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  queryLovHeadersList,
  updateLovHeaders,
  insertLovHeaders,
  deleteLovHeaders,
  updateLovValues,
  insertLovValues,
  deleteLovValues,
  queryLovValues,
  queryLovHeader,
  copyLov,
} from '../services/dimensionConfigService';

export default {
  namespace: 'dimensionConfig',

  state: {
    list: {},
    rowData: [],
    lovType: [], // 值集类型快码
    lovId: null,
    pagination: {}, // 列表分页数据
  },

  effects: {
    // 查询值集头列表
    *queryLovHeadersList({ payload }, { call, put, all }) {
      const [listResult, lovTypeResult] = yield all([
        call(queryLovHeadersList, payload),
        call(queryIdpValue, 'HPFM.LOV.LOV_TYPE'),
      ]);
      const list = getResponse(listResult);
      const lovType = getResponse(lovTypeResult);
      const pagination = createPagination(list);
      yield put({
        type: 'updateState',
        payload: { list, lovType, pagination },
      });
    },
    // 根据 ID 查询值集头
    *queryLovHeader({ payload }, { call }) {
      const res = yield call(queryLovHeader, payload);
      // TODO: getResponse
      return res;
    },
    // 查询独立值集值
    *queryLovValues({ payload }, { call }) {
      const res = yield call(queryLovValues, payload);
      return res;
    },
    // 批量插入值集头
    *insertLovHeaders({ payload }, { call }) {
      const res = yield call(insertLovHeaders, payload);
      return getResponse(res);
    },
    // 批量更新值集头
    *updateLovHeaders({ payload }, { call }) {
      const res = yield call(updateLovHeaders, payload);
      return getResponse(res);
    },

    // 保存值集头
    *saveLovHeaders({ payload }, { call }) {
      let res;
      if (payload.lovId) {
        res = yield call(updateLovHeaders, payload);
      } else {
        res = yield call(insertLovHeaders, payload);
      }
      return getResponse(res);
    },

    // 批量删除值集头
    *deleteLovHeaders({ payload }, { call }) {
      const res = yield call(deleteLovHeaders, payload);
      return getResponse(res);
    },

    // 保存值集值
    *saveLovValues({ payload }, { call }) {
      let res;
      if (payload.lovValueId) {
        res = yield call(updateLovValues, payload);
      } else {
        res = yield call(insertLovValues, payload);
      }
      return getResponse(res);
    },

    // 删除值集值
    *deleteLovValues({ payload }, { call }) {
      const res = yield call(deleteLovValues, payload);
      return getResponse(res);
    },

    // 复制值集
    *copyLov({ payload }, { call }) {
      const res = yield call(copyLov, payload);
      return getResponse(res);
    },
  },

  reducers: {
    // 批量条件查询值集头
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
