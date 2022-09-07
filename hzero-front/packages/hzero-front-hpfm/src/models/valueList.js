/**
 * valueList.js - 值集定义 model
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, isTenantRoleLevel, createPagination } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';
import { queryUnifyIdpValue, queryMapIdpValue } from 'hzero-front/lib/services/api';
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
} from '../services/valueListService';

export default {
  namespace: 'valueList',

  state: {
    list: {},
    rowData: [],
    lovType: [], // 值集类型快码
    lovTypeFilter: [], // 屏蔽SQL的值集类型编码
    lovId: null,
    requestMethods: [], // 请求方式值集
    pagination: {}, // 列表分页数据
  },

  effects: {
    // 查询值集头列表
    *queryLovHeadersList({ payload }, { call, put, all }) {
      const params = [
        call(queryLovHeadersList, payload),
        call(queryMapIdpValue, {
          lovType: 'HPFM.LOV.LOV_TYPE',
          requestMethods: 'HPFM.REQUEST_METHOD',
        }),
      ];
      if (!VERSION_IS_OP && isTenantRoleLevel()) {
        params.push(call(queryUnifyIdpValue, 'HPFM.LOV.LOV_TYPE', { tag: 'permit_all' }));
      }
      const [listResult, lovTypeResult, lovTypeFilterResult] = yield all(params);
      const list = getResponse(listResult);
      const { lovType, requestMethods } = getResponse(lovTypeResult);
      const lovTypeFilter = getResponse(lovTypeFilterResult);
      const pagination = createPagination(list);
      yield put({
        type: 'updateState',
        payload: { list, lovType, requestMethods, lovTypeFilter, pagination },
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
