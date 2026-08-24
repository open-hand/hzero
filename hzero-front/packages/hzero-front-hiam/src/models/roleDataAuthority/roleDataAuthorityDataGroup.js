/**
 * roleDataAuthorityDataGroup - 租户级权限维护tab页 - 数据组 model
 * @date: 2019-7-23
 * @author: hulingdangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import {
  deleteData,
  queryData,
  queryDataGroupModalData,
  saveData,
} from '../../services/roleDataAuthorityService';

export default {
  namespace: 'roleDataAuthorityDataGroup',

  state: {
    head: {}, // 头部数据
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
    createDataSource: [],
    createPagination: {},
  },
  effects: {
    * fetchAuthorityDataGroup({ payload }, { call, put }) {
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
    * addAuthorityDataGroup({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    * deleteAuthorityDataGroup({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    * fetchModalData({ payload }, { call, put }) {
      const response = yield call(queryDataGroupModalData, payload);
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
