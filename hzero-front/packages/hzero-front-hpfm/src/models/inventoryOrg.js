/**
 * model 库存组织
 * @date: 2018-7-5
 * @version: 0.0.1
 * @author:  邓婷敏 <tingmin.deng@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import {
  queryInventoryData,
  insertInventoryData,
  updateInventoryData,
  disableInventory,
  enableInventory,
  updateAllInventoryData,
} from '../services/InventoryService';

export default {
  // 库存组织model
  namespace: 'inventoryOrg',
  state: {
    fetchInventoryData: {
      content: [],
      pagination: {},
    },
  },
  effects: {
    // 查询库存组织，获取库存组织数据
    *fetchInventoryData({ payload }, { call, put }) {
      const res = yield call(queryInventoryData, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateStateReducer',
          payload: {
            fetchInventoryData: list,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 新建库存组织
    *insertInventoryData({ payload }, { call }) {
      const res = yield call(insertInventoryData, payload);
      return getResponse(res);
    },

    // 更新库存组织
    *updateInventoryData({ payload }, { call }) {
      const res = yield call(updateInventoryData, payload);
      return getResponse(res);
    },

    // 批量新建、编辑库存组织
    *updateAllInventoryData({ payload }, { call }) {
      const res = yield call(updateAllInventoryData, payload);
      return getResponse(res);
    },

    // 禁用库存组织（此方法暂不单用，禁用与编辑一起）
    *disbledInventory({ payload }, { call }) {
      const res = yield call(disableInventory, payload);
      return getResponse(res);
    },
    // 启用库存组织（此方法暂不单用，启用与编辑一起）
    *enabledInventory({ payload }, { call }) {
      const res = yield call(enableInventory, payload);
      return getResponse(res);
    },
  },
  reducers: {
    // 用于合并state,并且返回一个新的state
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    editData(state, action) {
      return {
        ...state,
        fetchInventoryData: {
          content: action.payload.content,
        },
      };
    },
  },
};
