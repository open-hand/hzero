/**
 * currency - 币种定义Modal
 * @date: 2018-7-3
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import {
  queryCurrency,
  addCurrency,
  updateCurrency,
  fetchDetail,
} from '../services/currencyService';

export default {
  namespace: 'currency',
  state: {
    data: {
      list: [],
      pagination: {},
    },
    detail: {
      currencyCode: '',
      currencyName: '',
      list: [],
    },
    currencyDetail: {},
    code: {},
  },
  effects: {
    *fetchCurrencyList({ payload }, { call, put }) {
      const response = yield call(queryCurrency, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'queryCurrency',
          payload: data,
        });
      }
    },

    *addCurrency({ payload }, { call }) {
      const response = yield call(addCurrency, payload);
      return getResponse(response);
    },

    *updateCurrency({ payload }, { call }) {
      const response = yield call(updateCurrency, payload);
      return getResponse(response);
    },

    *fetchDetail({ payload }, { call, put }) {
      const response = yield call(fetchDetail, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: { currencyDetail: data },
        });
      }
      return data;
    },
  },

  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    queryCurrency(state, action) {
      return {
        ...state,
        data: {
          ...action.payload,
          list: action.payload.content,
          pagination: createPagination(action.payload),
        },
      };
    },
    currencyDetail(state, action) {
      return {
        ...state,
        detail: {
          ...action.payload,
          list: action.payload.content,
          currencyCode: action.payload.content[0] && action.payload.content[0].currencyCode,
          currencyName: action.payload.content[0] && action.payload.content[0].currencyName,
        },
      };
    },
  },
};
