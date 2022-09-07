/**
 * @model 卡片管理
 * @modelState {object} [pagination=false] - 分页信息
 * @modelState {object[]} [dataSource=[]] - 数据
 * @modelState {object[]} [fdLevel=[]] - 层级的值集
 * @modelState {object[]} [catalogType=[]] - 分类的值集
 * @modelState {object} queryPagination - 上一次查询成功的分页信息
 *
 * @modelEffect cardFetch - 查询分页的卡片信息
 * @modelEffect cardCreate - 新建卡片
 * @modelEffect cardUpdate - 更新卡片
 * @modelEffect cardDetails - 获取卡片详情
 * @modelEffect cardTenantFetch - 查询分页的卡片分配的租户
 * @modelEffect cardTenantAdd - 给卡片新增租户
 * @modelEffect cardTenantRemove - 删除卡片已经分配的租户
 *
 * @modelReducer updateState - 更新 model
 *
 * @date 2019-01-24
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  cardInsert,
  cardQueryPage,
  cardTenantDelete,
  cardTenantInsert,
  cardTenantQueryPage,
  cardUpdate,
  cardDetails,
} from '../services/cardManageService';

export default {
  namespace: 'cardManage',
  state: {
    // 卡片管理界面数据
    pagination: false,
    dataSource: [],
    fdLevel: [], // 层级的值集
    catalogType: [], // 分类的值集
    // queryPagination: undefined, // 存储的上一次 查询成功的分页信息
    // 分配卡片模态框数据
    // cardTenantDataSource: [], // 卡片租户的数据
    // cardTenantPagination: false, // 卡片租户的分页
  },
  effects: {
    *init(_, { call, put }) {
      const res = yield call(queryMapIdpValue, {
        fdLevel: 'HPFM.DATA_TENANT_LEVEL',
        catalogType: 'HPFM.DASHBOARD_CARD.TYPE',
      });
      const realRes = getResponse(res);
      if (realRes) {
        yield put({
          type: 'updateState',
          payload: {
            fdLevel: realRes.fdLevel,
            catalogType: realRes.catalogType,
          },
        });
      }
    },
    // 查询卡片
    *cardFetch({ payload }, { call, put }) {
      const res = yield call(cardQueryPage, payload);
      const realRes = getResponse(res);
      if (realRes) {
        const { page, sort } = payload;
        yield put({
          type: 'updateState',
          payload: {
            queryPagination: { page, sort },
            pagination: createPagination(realRes),
            dataSource: realRes.content,
          },
        });
      }
    },
    // 新建卡片
    *cardCreate({ payload }, { call }) {
      const res = yield call(cardInsert, payload);
      return getResponse(res);
    },
    // 更新卡片
    *cardUpdate({ payload }, { call }) {
      const res = yield call(cardUpdate, payload);
      return getResponse(res);
    },
    // 获取卡片详情
    *cardDetails({ payload }, { call }) {
      const res = yield call(cardDetails, payload);
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
