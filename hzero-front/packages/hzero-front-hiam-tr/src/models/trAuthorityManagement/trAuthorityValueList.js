/**
 * roleDataAuthorityPurorg - 租户级权限维护tab页 - 值集 model
 * @date: 2019-7-10
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import {
  deleteData,
  queryData,
  queryLovModalData,
  saveData,
} from '../../services/trAuthorityManagementService';

export default {
  namespace: 'trAuthorityValueList',

  state: {
    head: {}, // 头部数据
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
    createDataSource: [],
    createPagination: {},
  },
  effects: {
    *fetchAuthorityValueList({ payload }, { call, put }) {
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
    *addAuthorityValueList({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    *deleteAuthorityValueList({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    *fetchAuthorityModalData({ payload }, { call, put }) {
      const response = yield call(queryLovModalData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            createDataSource: data.content,
            createPagination: createPagination(data),
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
