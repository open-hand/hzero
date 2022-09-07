/**
 * roleDataAuthorityLovView - 租户级权限维护tab页 - 值集 model
 * @date: 2019-6-19
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import {
  deleteData,
  queryData,
  queryLovViewModalData,
  saveData,
} from '../../services/roleDataAuthorityService';

export default {
  namespace: 'roleDataAuthorityLovView',

  state: {
    head: {}, // 头部数据
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
    createDataSource: [],
    createPagination: {},
  },
  effects: {
    * fetchAuthorityLovView({ payload }, { call, put }) {
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
    * addAuthorityLovView({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    * deleteAuthorityLovView({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
    * fetchModalData({ payload }, { call, put }) {
      const response = yield call(queryLovViewModalData, payload);
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
