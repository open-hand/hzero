/**
 * ldap.js - LDAP model
 * @date: 2018-12-20
 * @author: LZY <zhuyan.luo02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryIdpValue, getPublicKey } from 'hzero-front/lib/services/api';

import {
  fetchLDAP,
  updateLDAP,
  testConnect,
  disabledLDAP,
  enabledLDAP,
  fetchSyncInfo,
  fetchSyncErrorInfo,
  syncUser,
  stopSyncUser,
  querySyncLeave,
  querySyncUser,
  updateSyncLeave,
  updateSyncUser,
} from '../services/ldapService';

export default {
  namespace: 'ldap',

  state: {
    ldapData: {}, // LDAP数据
    testData: {},
    syncInfo: {},
    syncPagination: {},
    syncErrorInfo: {},
    syncErrorPagination: {},
    directoryTypeList: [], // 目录类型
    publicKey: '', // 密码公钥
    frequencyList: [],
    syncUserDetail: {},
    syncLeaveDetail: {},
  },

  effects: {
    // 获取目录类信息
    *queryDirectoryType(_, { call, put }) {
      const res = yield call(queryIdpValue, 'HIAM.LDAP.DIR_TYPE');
      const directoryTypeList = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { directoryTypeList },
      });
    },
    *queryFrequency(_, { call, put }) {
      const res = yield call(queryIdpValue, 'HIAM.LDAP_SYNC_FREQUENCY');
      const frequencyList = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { frequencyList },
      });
    },
    // 查询LDAP数据
    *fetchLDAP({ payload }, { call, put }) {
      const res = yield call(fetchLDAP, payload);
      const ldapData = getResponse(res);
      if (ldapData) {
        yield put({
          type: 'updateState',
          payload: {
            ldapData,
          },
        });
      }
      return ldapData;
    },
    // 更新LDAP
    *updateLDAP({ payload }, { call }) {
      const res = yield call(updateLDAP, payload);
      return getResponse(res);
    },
    // 连接测试
    *testConnect({ payload }, { call, put }) {
      const res = yield call(testConnect, payload);
      const testData = getResponse(res);
      if (testData) {
        yield put({
          type: 'updateState',
          payload: {
            testData,
          },
        });
      }
      return testData;
    },
    // 禁用LDAP
    *disabledLDAP({ payload }, { call }) {
      const res = yield call(disabledLDAP, payload);
      return getResponse(res);
    },
    // 启用LDAP
    *enabledLDAP({ payload }, { call }) {
      const res = yield call(enabledLDAP, payload);
      return getResponse(res);
    },
    // 查询数据同步信息
    *fetchSyncInfo({ payload }, { call, put }) {
      const res = yield call(fetchSyncInfo, parseParameters(payload));
      const syncInfo = getResponse(res);
      if (syncInfo) {
        yield put({
          type: 'updateState',
          payload: {
            syncInfo,
            syncPagination: createPagination(syncInfo),
          },
        });
      }
      return syncInfo;
    },
    // 查询数据同步信息
    *fetchSyncErrorInfo({ payload }, { call, put }) {
      const res = yield call(fetchSyncErrorInfo, parseParameters(payload));
      const syncErrorInfo = getResponse(res);
      if (syncErrorInfo) {
        yield put({
          type: 'updateState',
          payload: {
            syncErrorInfo,
            syncErrorPagination: createPagination(syncErrorInfo),
          },
        });
      }
      return syncErrorInfo;
    },
    // 同步用户
    *syncUser({ payload }, { call }) {
      const res = yield call(syncUser, payload);
      return getResponse(res);
    },
    // 终止同步用户
    *stopSyncUser({ payload }, { call }) {
      const res = yield call(stopSyncUser, payload);
      return getResponse(res);
    },
    // 请求公钥
    *getPublicKey(_, { call, put }) {
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
    // 查询LDAP数据
    *querySyncLeave({ payload }, { call, put }) {
      const res = yield call(querySyncLeave, payload);
      const syncLeaveDetail = getResponse(res);
      if (syncLeaveDetail) {
        yield put({
          type: 'updateState',
          payload: {
            syncLeaveDetail,
          },
        });
      }
      return syncLeaveDetail;
    },
    *querySyncUser({ payload }, { call, put }) {
      const res = yield call(querySyncUser, payload);
      const syncUserDetail = getResponse(res);
      if (syncUser) {
        yield put({
          type: 'updateState',
          payload: {
            syncUserDetail,
          },
        });
      }
      return syncUserDetail;
    },
    *updateSyncLeave({ payload }, { call }) {
      const res = yield call(updateSyncLeave, payload);
      return getResponse(res);
    },
    *updateSyncUser({ payload }, { call }) {
      const res = yield call(updateSyncUser, payload);
      return getResponse(res);
    },
  },

  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
