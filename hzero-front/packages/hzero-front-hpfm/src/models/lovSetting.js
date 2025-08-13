/**
 * lovSetting - 值集视图配置 - model
 * @date: 2018-6-26
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'services/api';
import {
  queryLovList,
  deleteLovValue,
  addLovValue,
  queryHeadValue,
  queryLineValue,
  addLineData,
  editLine,
  removeLine,
  updateHeadValue,
  copyLovView,
} from '../services/lovService';

export default {
  namespace: 'lovSetting',

  state: {
    list: {},
    pagination: {},
    headData: {},
    rowData: {
      list: [],
      pagination: {},
    },
    mainKey: {
      viewHeaderId: '',
      lovId: '',
    },
    dataTypeList: [],
  },

  effects: {
    *queryValueList(_, { put, call }) {
      const res = getResponse(yield call(queryIdpValue, 'HPFM.VIEW.DATA_TYPE'));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            dataTypeList: res,
          },
        });
      }
    },

    *fetchLovList({ payload }, { call, put }) {
      const response = yield call(queryLovList, payload);
      const list = getResponse(response);
      if (list) {
        yield put({
          type: 'query',
          payload: list,
        });
      }
      return list;
    },

    *addLovValue({ payload }, { call }) {
      const response = yield call(addLovValue, payload);
      return getResponse(response);
    },

    *deleteLovValue({ payload }, { call }) {
      const response = yield call(deleteLovValue, payload);
      return getResponse(response);
    },

    *fetchHead({ payload }, { call, put }) {
      const response = yield call(queryHeadValue, payload);
      yield put({
        type: 'queryHead',
        payload: response,
      });
    },

    *fetchLine({ payload }, { call, put }) {
      const response = yield call(queryLineValue, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'queryLine',
          payload: data,
        });
      }
    },

    *saveHead({ payload }, { call }) {
      const response = yield call(updateHeadValue, payload);
      return getResponse(response);
    },

    *addLine({ payload, callback }, { call }) {
      const response = yield call(addLineData, payload);
      if (response && callback) callback();
    },

    *editLineData({ payload, callback }, { call }) {
      const response = yield call(editLine, payload);
      if (response && callback) callback();
    },

    // 删除行数据
    *removeLineData({ payload }, { call }) {
      const response = yield call(removeLine, payload);
      return getResponse(response);
    },

    // 复制值集视图
    *copyLovView({ payload }, { call }) {
      const res = yield call(copyLovView, payload);
      return getResponse(res);
    },
  },

  reducers: {
    query(state, action) {
      return {
        ...state,
        list: action.payload,
        pagination: createPagination(action.payload),
      };
    },
    queryHead(state, action) {
      return {
        ...state,
        headData: action.payload,
        mainKey: {
          viewHeaderId: action.payload.viewHeaderId,
          lovId: action.payload.lovId,
        },
      };
    },
    queryLine(state, action) {
      return {
        ...state,
        rowData: {
          list: action.payload.content,
          pagination: createPagination(action.payload),
          ...action.payload,
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
