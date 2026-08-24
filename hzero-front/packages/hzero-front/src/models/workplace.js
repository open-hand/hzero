/**
 * model 工作台配置
 * @date: 2018-9-25
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';

import { queryIdpValue } from 'services/api';

import {
  workplaceLayoutQuery, // 查询当前的 Layout
  workplaceLayoutUpdate, // 保存当前编辑的 Layout
  workplaceCardsQuery, // 查询当前 拥有的卡片
} from '../services/workplaceService';

export default {
  namespace: 'workplace',
  state: {
    prevRoleCards: undefined, // 前一个可分配卡片
    roleCards: [], // 可分配卡片
    catalogType: [], // 卡片类型
  },
  effects: {
    *fetchLayoutAndInit(_, { call, put, all }) {
      const allRequest = [
        call(workplaceLayoutQuery),
        call(workplaceCardsQuery),
        call(queryIdpValue, 'HPFM.DASHBOARD_CARD.TYPE'),
      ];
      const [layout, roleCards, catalogType] = yield all(allRequest);
      const realLayout = getResponse(layout);
      const realCatalogType = getResponse(catalogType);
      const realRoleCards = getResponse(roleCards);
      const nextState = {};
      if (realRoleCards) {
        nextState.roleCards = realRoleCards;
      }
      if (realCatalogType) {
        nextState.catalogType = realCatalogType;
      }
      yield put({
        type: 'updateState',
        payload: nextState,
      });
      return realLayout;
    },
    *fetchLayout(_, { call }) {
      const res = yield call(workplaceLayoutQuery);
      return getResponse(res);
    },
    *saveLayout({ payload }, { call }) {
      const res = yield call(workplaceLayoutUpdate, payload);
      return getResponse(res);
    },
    *fetchCards({ payload }, { call, put }) {
      const { roleCards, ...params } = payload;
      const res = yield call(workplaceCardsQuery, params);
      const realRes = getResponse(res);
      if (realRes) {
        yield put({
          type: 'updateState',
          payload: {
            prevRoleCards: roleCards,
            roleCards: realRes,
          },
        });
      }
      return realRes;
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
