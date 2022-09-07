/**
 * staticTextOrg.js
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import {
  staticTextCreateOne,
  staticTextFetchList,
  staticTextFetchOne,
  staticTextRemoveList,
  staticTextUpdateOne,
} from '../services/staticTextOrgService';

export default {
  namespace: 'staticTextOrg',
  state: {
    lov: {}, // 存放值集
    list: {}, // 存放 list 页面信息
    detail: {}, // 存放 detail 页面信息
  },
  effects: {
    *fetchStaticTextList({ payload }, { call, put }) {
      const { organizationId, params } = payload;
      const res = yield call(staticTextFetchList, organizationId, params);
      const staticTextList = getResponse(res);
      if (staticTextList) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: staticTextList.content,
              pagination: createPagination(staticTextList),
            },
          },
        });
      }
      return staticTextList;
    },
    *fetchStaticTextOne({ payload }, { call, put }) {
      const { organizationId, textId, params } = payload;
      const res = yield call(staticTextFetchOne, organizationId, textId, params);
      const staticTextDetail = getResponse(res);
      if (staticTextDetail) {
        yield put({
          type: 'updateState',
          payload: {
            detail: {
              record: staticTextDetail,
            },
          },
        });
      }
    },
    *removeStaticTextList({ payload }, { call }) {
      const { organizationId, params } = payload;
      const res = yield call(staticTextRemoveList, organizationId, params);
      return getResponse(res);
    },
    *removeStaticTextOne({ payload }, { call }) {
      const { organizationId, params } = payload;
      const res = yield call(staticTextRemoveList, organizationId, params);
      return getResponse(res);
    },
    *updateStaticTextOne({ payload }, { call }) {
      const { organizationId, params } = payload;
      const res = yield call(staticTextUpdateOne, organizationId, params);
      return getResponse(res);
    },
    *createStaticTextOne({ payload }, { call }) {
      const { organizationId, params } = payload;
      const res = yield call(staticTextCreateOne, organizationId, params);
      return getResponse(res);
    },
    *fetchLanguage(_, { call, put }) {
      const res = yield call(queryUnifyIdpValue, 'HPFM.LANGUAGE');
      const languageRes = getResponse(res);
      if (languageRes) {
        yield put({
          type: 'updateState',
          payload: {
            lov: {
              language: languageRes,
            },
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
    clearDetail(state) {
      return {
        ...state,
        detail: {},
      };
    },
  },
};
