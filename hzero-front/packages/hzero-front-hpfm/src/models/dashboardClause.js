/**
 * model - 条目配置
 * @date: 2019-01-28
 * @author: YKK <kaikai.yang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  queryClause,
  addClause,
  updateClause,
  cardTenantQueryPage,
  cardTenantInsert,
  cardTenantDelete,
  queryClauseDetail,
  fetchHead,
  fetchTable,
  saveClause,
  fetchCard,
  deleteCard,
} from '../services/dashboardClauseService';

export default {
  namespace: 'dashboardClause',

  state: {
    flags: [],
    clauseList: [], // 条目配置查询数据
    clausePagination: {}, // 条目配置查询分页参数
    clauseDetail: {},
    clauseDetailHead: {}, // 条目配置明细
    clauseDetailTableList: [], // 条目详情列表
    clauseDetailPagination: {}, // 条目详情分页
    cardList: [], // 卡片列表
    cardPagination: {}, // 卡片分页
  },

  effects: {
    // 查询值集
    *init(params, { call, put }) {
      const flags = getResponse(yield call(queryIdpValue, 'HPFM.DATA_TENANT_LEVEL'));
      yield put({
        type: 'updateState',
        payload: {
          flags: flags || [],
        },
      });
    },

    // 查询条目配置列表
    *queryClause({ payload }, { call, put }) {
      const data = getResponse(yield call(queryClause, payload));
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            clauseList: data.content,
            clausePagination: createPagination(data),
          },
        });
      }
    },

    // 查询条目配置列表
    *queryClauseDetail({ payload }, { call, put }) {
      const data = getResponse(yield call(queryClauseDetail, payload));
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            clauseDetail: data,
          },
        });
      }
      return data;
    },

    // 添加条目配置
    *addClause({ payload }, { call }) {
      const orderType = yield call(addClause, payload);
      return getResponse(orderType);
    },

    // 添加条目配置
    *updateClause({ payload }, { call }) {
      const res = yield call(updateClause, payload);
      return getResponse(res);
    },

    // 查询已经分配的租户
    *cardTenantFetch({ payload }, { call }) {
      const res = yield call(cardTenantQueryPage, payload);
      return getResponse(res);
    },

    *cardTenantAdd({ payload }, { call }) {
      const res = yield call(cardTenantInsert, payload);
      return getResponse(res);
    },

    *cardTenantRemove({ payload }, { call }) {
      const res = yield call(cardTenantDelete, payload);
      return getResponse(res);
    },

    // 查询卡片明细头
    *fetchHead({ payload }, { call, put }) {
      const data = yield call(fetchHead, payload);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            clauseDetailHead: data,
          },
        });
      }
    },

    // 查询明细table
    *fetchTable({ payload }, { call, put }) {
      const data = yield call(fetchTable, payload);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            clauseDetailTableList: data.content,
            clauseDetailPagination: createPagination(data),
          },
        });
      }
    },

    // 保存条目
    *saveClause({ payload }, { call }) {
      const res = yield call(saveClause, payload);
      return getResponse(res);
    },

    // 删除卡片
    *deleteCard({ payload }, { call }) {
      const res = yield call(deleteCard, payload);
      return getResponse(res);
    },

    // 查询卡片
    *fetchCard({ payload }, { call, put }) {
      const data = yield call(fetchCard, payload);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            cardList: data.content,
            cardPagination: createPagination(data),
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
