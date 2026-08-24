/**
 * config.js - 系统配置 model
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  queryTenantConfig,
  updateTenantConfig,
  updateOrganizationConfig,
  queryOrganizationConfig,
} from '../services/configService';

export default {
  namespace: 'config',

  state: {
    data: [],
    // tenantList: [],
    lov: {}, // 存放值集
  },

  effects: {
    // 初始化查询值集
    *init(_, { call, put }) {
      const lovBatch = yield call(queryMapIdpValue, {
        menuLayout: 'HPFM.MENU_LAYOUT',
        menuLayoutTheme: 'HPFM.MENU_LAYOUT_THEME',
        roleMergeFlag: 'HPFM.ENABLED_FLAG',
      });
      const safeLovBatch = getResponse(lovBatch);
      if (safeLovBatch) {
        const safeLov = {};
        Object.keys(safeLovBatch).forEach(lovKey => {
          safeLov[lovKey] = safeLovBatch[lovKey] || [];
        });
        yield put({
          type: 'updateState',
          payload: {
            lov: safeLov,
          },
        });
      }
    },
    // 当前为租户下查询系统配置
    *queryOrganizationConfig({ payload }, { call, put }) {
      const res = yield call(queryOrganizationConfig, payload);
      const data = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { data },
      });
      return data;
    },

    // 当前为租户下更新系统配置
    *updateOrganizationConfig({ payload }, { call }) {
      const res = yield call(updateOrganizationConfig, payload);
      return getResponse(res);
    },
    // 当前为平台查询系统配置
    *queryTenantConfig({ payload }, { call, put }) {
      const res = yield call(queryTenantConfig, payload);
      const data = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { data },
      });
      return data;
    },

    // 当前为平台更新系统配置
    *updateTenantConfig({ payload }, { call }) {
      const res = yield call(updateTenantConfig, payload);
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
