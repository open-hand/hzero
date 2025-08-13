/**
 * model 采购组织
 * @date: 2018-7-2
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { fetchPurchaseOrgList, savePurchaseOrg } from '../services/purchaseOrgService';

export default {
  namespace: 'purchaseOrg',

  state: {
    purchaseOrgList: [], // 采购组织列表数据
    pagination: {}, // 分页对象
  },
  effects: {
    // 获取采购组织数据
    *fetchPurchaseOrgList({ payload }, { put, call }) {
      const res = yield call(fetchPurchaseOrgList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            purchaseOrgList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },
    // 批量新增编辑采购组织
    *savePurchaseOrg({ payload }, { call }) {
      const res = yield call(savePurchaseOrg, payload);
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
