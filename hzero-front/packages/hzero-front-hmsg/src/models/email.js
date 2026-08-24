/**
 * model 邮箱
 * @date: 2018-7-25
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryIdpValue, queryMapIdpValue, getPublicKey } from 'hzero-front/lib/services/api';

import {
  fetchEmail,
  createEmail,
  updateEmail,
  queryEmailServers,
  fetchFilterList,
  updateFilter,
  deleteFilter,
  deleteEmail,
} from '../services/emailService';

export default {
  namespace: 'email',

  state: {
    emailList: [], // 邮箱数据
    emailProperties: [], // 服务器配置项列表
    tenantId: 0, // 租户id
    pagination: {},
    filterStrategyList: [],
    filterList: [], // 黑白名单列表
    filterPagination: {}, // 黑白名单分页
    publicKey: '', // 密码公钥
  },

  effects: {
    // 获取值集
    * fetchEnums(_, { call, put }) {
      const enumsRes = yield call(queryMapIdpValue, {
        'HMSG.EMAIL_PROTOCOL': 'HMSG.EMAIL_PROTOCOL',
      });
      const filterStrategyList = getResponse(
        yield call(queryIdpValue, 'HMSG.EMAIL.FILTER_STRATEGY')
      );
      const enums = getResponse(enumsRes);
      if (enums) {
        yield put({
          type: 'updateState',
          payload: {
            enums,
            filterStrategyList,
          },
        });
      }
    },
    // 获取邮箱账户
    * fetchEmail({ payload }, { put, call }) {
      const res = yield call(fetchEmail, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            emailList: data.content,
            pagination: createPagination(data),
          },
        });
      }
    },
    * queryEmailServers({ payload }, { put, call }) {
      const res = yield call(queryEmailServers, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            emailProperties: data.emailProperties,
          },
        });
      }
      return data;
    },
    * createEmail({ payload }, { call }) {
      const res = yield call(createEmail, payload);
      return getResponse(res);
    },
    * updateEmail({ payload }, { call }) {
      const res = yield call(updateEmail, payload);
      return getResponse(res);
    },
    * deleteEmail({ payload }, { call }) {
      const res = yield call(deleteEmail, payload);
      return getResponse(res);
    },
    // 获取黑白名单
    * fetchFilterList({ payload }, { call, put }) {
      const res = yield call(fetchFilterList, parseParameters(payload));
      const filter = getResponse(res);
      yield put({
        type: 'updateState',
        payload: {
          filterList: filter && filter.content,
          filterPagination: createPagination(filter),
        },
      });
      return filter;
    },
    // 更新黑白名单
    * updateFilter({ payload }, { call }) {
      const res = yield call(updateFilter, payload);
      return getResponse(res);
    },
    // 删除黑白名单
    * deleteFilter({ payload }, { call }) {
      const res = yield call(deleteFilter, payload);
      return getResponse(res);
    },
    // 请求公钥
    * getPublicKey(_, { call, put }) {
      const res = yield call(getPublicKey);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            publicKey: res.publicKey,
          },
        });
      }
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
