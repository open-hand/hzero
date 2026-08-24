/**
 * industryCategory - 国标品类定义 - model
 * @date: 2018-7-24
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse } from 'utils/utils';
import {
  queryTopCategory,
  querySecondCategory,
  queryCategory,
  saveIndustry,
  saveCategory,
  putIndustry,
  putCategory,
} from '../services/industryCategoryService';

export default {
  namespace: 'industryCategory',
  state: {
    topData: [],
    secondData: [],
    categoryData: [],
  },
  effects: {
    *fetchTopCategory({ payload }, { call }) {
      const response = yield call(queryTopCategory, payload);
      return getResponse(response);
    },

    *fetchSecondCategory({ payload }, { call }) {
      const response = yield call(querySecondCategory, payload);
      return getResponse(response);
    },

    *fetchCategory({ payload }, { call }) {
      const response = yield call(queryCategory, payload);
      return getResponse(response);
    },

    *addIndustry({ payload }, { call }) {
      const response = yield call(saveIndustry, payload);
      return getResponse(response);
    },

    *addCategory({ payload }, { call }) {
      const response = yield call(saveCategory, payload);
      return getResponse(response);
    },

    *updateIndustry({ payload }, { call }) {
      const response = yield call(putIndustry, payload);
      return getResponse(response);
    },

    *updateCategory({ payload }, { call }) {
      const response = yield call(putCategory, payload);
      return getResponse(response);
    },
  },
  reducers: {
    queryTopCategory(state, action) {
      return {
        ...state,
        topData: action.payload,
      };
    },
    querySecondCategory(state, action) {
      return {
        ...state,
        secondData: action.payload,
      };
    },
    queryCategory(state, action) {
      return {
        ...state,
        categoryData: action.payload,
      };
    },
    clearCategory(state, action) {
      return {
        ...state,
        categoryData: action.payload,
      };
    },
  },
};
