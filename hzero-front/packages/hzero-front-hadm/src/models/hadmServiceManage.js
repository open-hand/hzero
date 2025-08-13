/**
 * @date 2019-01-23
 * @author WJC <jiacheng.wang@hand-china.com>
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  fetchServiceManageList,
  fetchServiceRouteDetail,
  fetchServiceVersionsList,
  fetchServiceManageDetail,
  createService,
  updateService,
  deleteService,
  updateServiceRoute,
  createServiceRoute,
  deleteServiceRoute,
  refreshRoute,
} from '../services/serviceManageService';

export default {
  namespace: 'hadmServiceManage',

  state: {
    serviceList: [], // 服务列表数据
    serviceDetail: [], // 服务详情数据
    serviceVersionList: [], // 服务版本数据
    pagination: {},
    versionPagination: {},
  },

  effects: {
    // 查询列表
    *fetchServiceManageList({ payload }, { call, put }) {
      const res = yield call(fetchServiceManageList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serviceList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 查询详细数据
    *fetchServiceManageDetail({ payload }, { call, put }) {
      const res = yield call(fetchServiceManageDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            serviceDetail: data,
          },
        });
      }
      return data;
    },

    // 查询服务版本数据
    *fetchServiceVersionsList({ payload }, { call, put }) {
      const res = yield call(fetchServiceVersionsList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serviceVersionList: list.content,
            versionPagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 查询路由详情数据
    *fetchServiceRouteDetail({ payload }, { call, put }) {
      const res = yield call(fetchServiceRouteDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            serviceDetail: data,
          },
        });
      }
      return data;
    },

    *createService({ payload }, { call }) {
      const res = yield call(createService, payload);
      return getResponse(res);
    },

    *updateService({ payload }, { call }) {
      const res = yield call(updateService, payload);
      return getResponse(res);
    },

    *deleteService({ payload }, { call }) {
      const res = yield call(deleteService, payload);
      return getResponse(res);
    },

    *updateServiceRoute({ payload }, { call }) {
      const res = yield call(updateServiceRoute, payload);
      return getResponse(res);
    },
    *deleteServiceRoute({ payload }, { call }) {
      const res = yield call(deleteServiceRoute, payload);
      return getResponse(res);
    },
    *createServiceRoute({ payload }, { call }) {
      const res = yield call(createServiceRoute, payload);
      return getResponse(res);
    },
    *refreshRoute({ payload }, { call }) {
      const res = yield call(refreshRoute, payload);
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
