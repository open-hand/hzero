/**
 * Company - 租户级权限维护tab页 - 销售产品 model
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse } from 'utils/utils';
import { deleteData, queryData, saveData } from '../../services/trAuthorityManagementService';

export default {
  namespace: 'trAuthorityProduction',

  state: {
    head: {},
    data: {
      list: [],
    },
  },
  effects: {
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'queryauthorityProduction',
          payload: data,
        });
      }
    },
    *add({ payload }, { call }) {
      const response = yield call(saveData, payload);
      return getResponse(response);
    },
    *delete({ payload }, { call }) {
      const response = yield call(deleteData, payload);
      return getResponse(response);
    },
  },
  reducers: {
    queryauthorityProduction(state, action) {
      return {
        ...state,
        data: {
          list: action.payload.userAuthorityLineList.content,
          ...action.payload.userAuthorityLineList,
        },
        head: action.payload.userAuthority,
      };
    },
    addNewData(state) {
      return {
        ...state,
        data: {
          ...state.data,
          size: state.data.list.length >= state.data.size ? state.data.size + 1 : state.data.size,
          totalElements: state.data.totalElements + 1,
        },
      };
    },
    removeNewAdd(state) {
      return {
        ...state,
        data: {
          ...state.data,
          size: state.data.list.length >= state.data.size ? state.data.size - 1 : state.data.size,
          totalElements: state.data.totalElements - 1,
        },
      };
    },
    editRow(state, action) {
      return {
        ...state,
        data: {
          ...state.data,
          list: action.payload.data,
        },
      };
    },
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
