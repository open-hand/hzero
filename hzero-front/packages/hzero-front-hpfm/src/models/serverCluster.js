/**
 * 服务器集群管理
 * @date: 2019-7-9
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  deleteServerCluster,
  fetchServerClusterList,
  createServerCluster,
  updateServerCluster,
  getServerClusterDetail,
} from '../services/serverClusterService';
import {
  fetchServerList,
  fetchCanAssignList,
  deleteServerAssign,
  createServerAssign,
} from '../services/serverService';

export default {
  namespace: 'serverCluster',
  state: {
    serverClusterList: [], // 服务器集群列表
    pagination: {}, // 分页对象
    serverClusterDetail: {}, // 服务器集群列表明细
    serverList: [], // 根据clusterid查询到的服务器列表
    canAssignList: [], // 可分配的服务器列表
    // serverAssignDetail: {}, // 服务器关联集群列表明细
    serverClusterPagination: {},
    serverPagination: {},
    canAssignPagination: {},
  },
  effects: {
    // 获取服务器集群列表
    *fetchServerClusterList({ payload }, { call, put }) {
      const res = yield call(fetchServerClusterList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serverClusterList: list.content,
            serverClusterPagination: createPagination(list),
          },
        });
      }
    },

    // 查询服务器集群列表详情
    *getServerClusterDetail({ payload }, { call, put }) {
      const res = yield call(getServerClusterDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serverClusterDetail: list,
          },
        });
      }
    },

    // 新增服务器集群
    *createServerCluster({ payload }, { call }) {
      const res = yield call(createServerCluster, payload);
      return getResponse(res);
    },

    // 更新服务器集群
    *updateServerCluster({ payload }, { call }) {
      const res = yield call(updateServerCluster, payload);
      return getResponse(res);
    },

    // 删除服务器集群
    *deleteServerCluster({ payload }, { call }) {
      const res = yield call(deleteServerCluster, payload);
      return getResponse(res);
    },

    // 根据clusterid查询服务器列表
    *fetchServerList({ payload }, { call, put }) {
      const res = yield call(fetchServerList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serverList: list.content,
            serverPagination: createPagination(list),
          },
        });
      }
    },

    // 查询可分配的服务器列表
    *fetchCanAssignList({ payload }, { call, put }) {
      const res = yield call(fetchCanAssignList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            canAssignList: list.content,
            canAssignPagination: createPagination(list),
          },
        });
      }
    },

    // 删除服务器的集群分配
    *deleteServerAssign({ payload }, { call }) {
      const res = yield call(deleteServerAssign, payload);
      return getResponse(res);
    },

    // 创建服务器的集群分配
    *createServerAssign({ payload }, { call }) {
      const res = yield call(createServerAssign, payload);
      return getResponse(res);
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
