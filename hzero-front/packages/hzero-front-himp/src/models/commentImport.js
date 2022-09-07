/**
 * @since 2018-10-28
 * @author LJ <jun.li06@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';
import {
  loadTemplate,
  loadDataSource,
  validateData,
  importData,
  updateOne,
  queryStatus,
} from '../services/commentImportService';

export default {
  namespace: 'comment',
  state: {
    template: {},
    detailList: [],
    detailData: {},
    batch: '',
    isloading: false,
    prefixPatch: '',
  },

  effects: {
    *loadTemplate({ payload }, { call }) {
      const res = yield call(loadTemplate, payload);
      return getResponse(res);
    },
    *loadDataSource({ templateCode, batch, prefixPatch, params }, { call, put }) {
      const res = yield call(loadDataSource, templateCode, batch, prefixPatch, params);
      if (getResponse(res)) {
        const dataSource = (res || []).map(n => ({
          id: n.id,
          errorMsg: n.errorMsg,
          imported: n.imported,
          validated: n.validated,
          ...JSON.parse(n.data),
        }));
        yield put({
          type: 'commentUpdateState',
          payload: {
            batch,
          },
        });
        return dataSource;
      }
    },
    *validateData({ payload }, { call }) {
      const res = yield call(validateData, payload);
      return getResponse(res);
    },
    *importData({ payload }, { call }) {
      const res = yield call(importData, payload);
      return getResponse(res);
    },
    *updateOne({ payload }, { call }) {
      const res = yield call(updateOne, payload);
      return getResponse(res);
    },
    *queryStatus({ prefixPatch, params }, { call }) {
      const res = yield call(queryStatus, prefixPatch, params);
      return getResponse(res);
    },
  },

  reducers: {
    commentUpdateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
