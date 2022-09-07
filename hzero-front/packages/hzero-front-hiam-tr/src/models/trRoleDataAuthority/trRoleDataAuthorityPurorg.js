/**
 * roleDataAuthorityPurorg - 租户级权限维护tab页 - 采购组织 model
 * @date: 2019-6-19
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import {
  deleteData,
  queryData,
  queryPurorgModalData,
  saveData,
} from '../../services/trRoleDataAuthorityService';

export default {
  namespace: 'trRoleDataAuthorityPurorg',

  state: {
    head: {}, // 头部数据
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
    purorgDataSource: [],
    purorgPagination: {},
  },
  effects: {
    *fetchAuthorityPurorg({ payload }, { call, put }) {
      const response = yield call(queryData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            head: data.roleAuthData,
            list: data.roleAuthDataLineList.content,
            pagination: createPagination(data.roleAuthDataLineList),
          },
        });
      }
    },
    *addAuthorityPurorg({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    *deleteAuthorityPurorg({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    *fetchModalData({ payload }, { call, put }) {
      const response = yield call(queryPurorgModalData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            purorgDataSource: data.content,
            purorgPagination: createPagination(data),
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
