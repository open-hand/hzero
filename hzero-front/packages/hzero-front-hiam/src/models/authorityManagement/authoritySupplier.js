/**
 * authoritySupplier - 租户级权限维护tab页 - 供应商 - model
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import {
  queryData,
  saveData,
  deleteData,
  querySupplierModalData,
} from '../../services/authorityManagementService';

export default {
  namespace: 'authoritySupplier',

  state: {
    head: {}, // 头部数据
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
    supplierDataSource: [],
    supplierPagination: {},
  },
  effects: {
    * fetchAuthoritySupplier({ payload }, { call, put }) {
      const response = yield call(queryData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            head: data.userAuthority,
            list: data.userAuthorityLineList.content,
            pagination: createPagination(data.userAuthorityLineList),
          },
        });
      }
    },
    * addAuthoritySupplier({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    * deleteAuthoritySupplier({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    * fetchModalData({ payload }, { call, put }) {
      const response = yield call(querySupplierModalData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            supplierDataSource: data.content,
            supplierPagination: createPagination(data),
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
