/**
 * authorityPurcat - 租户级权限维护tab页 - 采购品类 model
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
  queryPurcatModalData,
} from '../../services/trAuthorityManagementService';

export default {
  namespace: 'trAuthorityPurcat',

  state: {
    head: {},
    list: [],
    pagination: {},
    purcatDataSource: [],
    purcatPagination: {},
  },
  effects: {
    *fetchAuthorityPurcat({ payload }, { call, put }) {
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
    *addAuthorityPurcat({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    *deleteAuthorityPurcat({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    *fetchModalData({ payload }, { call, put }) {
      const response = yield call(queryPurcatModalData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            purcatDataSource: data.content,
            purcatPagination: createPagination(data),
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
