/**
 * model - 租户维护
 * @date: 2018-8-9
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';

import {
  queryTenant,
  updateTenant,
  addTenant,
  queryTenantDetail,
} from '../services/tenantsService';

export default {
  namespace: 'tenants',
  state: {
    tenantData: {}, // 租户数据
    tenantDataDetail: {}, // 租户明细数据
    pagination: {}, // 分页参数
  },
  effects: {
    // 查询数据
    *queryTenant({ payload }, { call, put }) {
      const response = yield call(queryTenant, payload);
      const tenantData = getResponse(response);
      if (tenantData) {
        yield put({
          type: 'updateState',
          payload: { tenantData, pagination: createPagination(tenantData) },
        });
      }
    },

    // 查询数据
    *queryTenantDetail({ payload }, { call, put }) {
      const response = yield call(queryTenantDetail, payload);
      const tenantDataDetail = getResponse(response);
      if (tenantDataDetail) {
        yield put({
          type: 'updateState',
          payload: { tenantDataDetail },
        });
      }
      return tenantDataDetail;
    },
    // 编辑数据
    *updateTenant({ payload }, { call }) {
      const response = yield call(updateTenant, payload);
      return getResponse(response);
    },
    // 新增数据
    *addTenant({ payload }, { call }) {
      const response = yield call(addTenant, payload);
      return getResponse(response);
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
