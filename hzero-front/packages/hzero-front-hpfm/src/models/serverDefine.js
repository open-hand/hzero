/**
 * model - 服务器定义
 * @date: 2019-7-1
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue, getPublicKey } from 'hzero-front/lib/services/api';

import {
  fetchServerList,
  getServerDetail,
  createServer,
  editServer,
  getServerCluster,
  resetPssword,
  deleteServer,
} from '../services/serverDefineService';

export default {
  namespace: 'serverDefine',
  state: {
    serverList: [], // 服务器定义列表
    typeList: [], // 服务器协议类别
    pagination: {}, // 页数
    serverDetail: {}, // 服务器明细
    serverCluster: [], // 服务器集群
    publicKey: '', // 公钥
  },
  effects: {
    // 获取初始化数据
    *init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { typeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          typeList,
        },
      });
    },

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

    // 获取列表
    *fetchServerList({ payload }, { call, put }) {
      const res = getResponse(yield call(fetchServerList, parseParameters(payload)));
      yield put({
        type: 'updateState',
        payload: {
          serverList: res.content,
          pagination: createPagination(res),
        },
      });
    },

    // 获取服务器明细
    *getServerDetail({ payload }, { call, put }) {
      const res = getResponse(yield call(getServerDetail, payload));
      yield put({
        type: 'updateState',
        payload: {
          serverDetail: res,
        },
      });
    },

    // 获取服务器集群
    *getServerCluster({ payload }, { call, put }) {
      const res = getResponse(yield call(getServerCluster, payload));
      yield put({
        type: 'updateState',
        payload: {
          serverCluster: res.content,
        },
      });
    },

    // 新建服务器
    *createServer({ payload }, { call }) {
      const res = getResponse(yield call(createServer, payload));
      return res;
    },

    // 编辑服务器
    *editServer({ payload }, { call }) {
      const res = getResponse(yield call(editServer, payload));
      return res;
    },

    // 删除服务器
    *deleteServer({ payload }, { call }) {
      const res = getResponse(yield call(deleteServer, payload));
      return res;
    },

    // 重置密码
    *resetPssword({ payload }, { call }) {
      const res = getResponse(yield call(resetPssword, payload));
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
