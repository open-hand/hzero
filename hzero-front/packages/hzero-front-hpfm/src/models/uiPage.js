/**
 * uiPageService.js
 * @date 2018/9/29
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import { forEach } from 'lodash';

import { createPagination, getResponse } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  uiPageSiteCreate,
  uiPageSiteQueryPaging,
  uiPageSiteUpdate,
  // detail
  uiPageSiteQueryDetail,
  uiPageSiteDetailUpdate,
} from '../services/uiPageService';

export default {
  namespace: 'uiPage',
  state: {
    // 列表
    list: {},
    pagination: {},
    // 详情
    config: {},
    // 值集
    detailIdp: {},
  },
  effects: {
    *fetchList({ payload }, { put, call }) {
      const res = yield call(uiPageSiteQueryPaging, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            list,
            pagination: createPagination(list),
          },
        });
      }
    },
    *listCreateOne({ payload }, { call }) {
      return getResponse(yield call(uiPageSiteCreate, payload));
    },
    *listUpdateOne({ payload }, { call }) {
      return getResponse(yield call(uiPageSiteUpdate, payload));
    },
    *fetchDetail({ payload }, { put, call }) {
      const config = getResponse(yield call(uiPageSiteQueryDetail, payload));
      const componentType = getResponse(yield call(queryIdpValue, 'HPFM.UI.COMPONENT'));
      if (config) {
        yield put({
          type: 'updateState',
          payload: {
            config,
          },
        });
      }
      if (componentType) {
        const componentTypeMeaning = {};
        forEach(componentType, idp => {
          componentTypeMeaning[idp.value] = idp.meaning;
        });
        yield put({
          type: 'updateDetailIdp',
          payload: {
            componentType,
            componentTypeMeaning,
          },
        });
      }
    },
    *detailUpdate({ payload }, { call }) {
      const res = getResponse(yield call(uiPageSiteDetailUpdate, payload));
      return res;
    },
  },
  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    updateDetailIdp(state, { payload }) {
      return {
        ...state,
        detailIdp: {
          ...state.detailIdp,
          ...payload,
        },
      };
    },
  },
};
