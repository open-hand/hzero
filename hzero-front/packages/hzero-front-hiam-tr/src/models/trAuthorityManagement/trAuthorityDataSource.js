/**
 * roleDataAuthorityPurorg - 租户级权限维护tab页 - 数据源 model
 * @date: 2019-7-17
 * @author: xl <liang.xiong@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import {
  deleteData,
  queryData,
  queryDataSourceModalData,
  saveData,
} from '../../services/trAuthorityManagementService';

export default {
  namespace: 'trAuthorityDataSource',

  state: {
    head: {}, // 头部数据
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
    dataSourceDataSource: [],
    dataSourcePagination: {},
  },
  effects: {
    *fetchAuthorityDataSource({ payload }, { call, put }) {
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
    *addAuthorityDataSource({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    *deleteAuthorityDataSource({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    *fetchAuthorityModalData({ payload }, { call, put }) {
      const response = yield call(queryDataSourceModalData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            dataSourceDataSource: data.content,
            dataSourcePagination: createPagination(data),
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
