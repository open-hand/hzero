/**
 * 查询配置models
 * @date: 2020-1-6
 * @author: MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import { getResponse } from 'utils/utils';
// import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchList,
  sqlRequest,
  httpRequest,
  configRequest,
} from '../services/inquiryConfigService';

export default {
  namespace: 'inquiryConfig',

  state: {
    indexList: [], // 字段列表
    // respondParam: JSON.stringify(
    //   {
    //     query: { match_all: {} },
    //   }
    // ), // 返回
  },

  effects: {
    // 字段列表
    * fetchList({ payload }, { call, put }) {
      const res = yield call(fetchList, payload);
      const resList = getResponse(res);
      if (resList) {
        yield put({
          type: 'updateState',
          payload: {
            indexList: resList,
          },
        });
      }
      return resList;
    },
    * request({ payload }, { call }) {
      let res = yield call(httpRequest, payload);
      res = getResponse(res);
      return res;
    },
    * sqlRequest({ payload }, { call }) {
      let res = yield call(sqlRequest, payload);
      res = getResponse(res);
      // if (res && !res.error) {
      //   yield put({
      //     type: 'updateState',
      //     payload: {
      //       respondParam: JSON.stringify(res),
      //     },
      //   });
      // }
      return res;
    },
    * configRequest({ payload }, { call }) {
      let res = yield call(configRequest, payload);
      res = getResponse(res);
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
  },
};
