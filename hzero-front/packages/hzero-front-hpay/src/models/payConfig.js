import { createPagination, getResponse, parseParameters } from 'utils/utils';
import { queryIdpValue, getPublicKey } from 'hzero-front/lib/services/api';

import {
  updateConfig,
  createConfig,
  deleteConfig,
  fetchConfigDetail,
  fetchConfigList,
  deleteCert,
  fetchCert,
} from '../services/payConfigService';

export default {
  namespace: 'payConfig',

  state: {
    payConfigDetail: {}, // 明细数据
    payConfigList: [], // 列表数据
    pagination: {}, // 分页对象
    channelCodeList: [], // 支付渠道列表
    certDetail: {}, // 证书详情
    publicKey: '', // 密码公钥
  },

  effects: {
    * init(_, { put, call }) {
      const channelCodeList = getResponse(yield call(queryIdpValue, 'HPAY.PAYMENT_CHANNEL'));
      yield put({
        type: 'updateState',
        payload: {
          channelCodeList,
        },
      });
    },

    // 获取列表
    * fetchConfigList({ payload }, { put, call }) {
      const res = yield call(fetchConfigList, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            payConfigList: data.content,
            pagination: createPagination(data),
          },
        });
      }
    },

    // 获取明细
    * fetchConfigDetail({ payload }, { put, call }) {
      const res = yield call(fetchConfigDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            payConfigDetail: data,
          },
        });
      }
    },

    // 获取证书
    * fetchCert({ payload }, { put, call }) {
      const res = yield call(fetchCert, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            certDetail: data,
          },
        });
      }
    },

    // 创建
    * createConfig({ payload }, { call }) {
      const res = yield call(createConfig, payload);
      return getResponse(res);
    },

    // 更新
    * updateConfig({ payload }, { call }) {
      const res = yield call(updateConfig, payload);
      return getResponse(res);
    },

    // 删除
    * deleteConfig({ payload }, { call }) {
      const res = yield call(deleteConfig, payload);
      return getResponse(res);
    },

    // 删除证书
    * deleteCert({ payload }, { call }) {
      const res = yield call(deleteCert, payload);
      return getResponse(res);
    },

    // 获取公钥
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
