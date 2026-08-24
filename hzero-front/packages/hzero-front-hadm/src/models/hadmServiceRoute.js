/**
 * @date 2019-01-23
 * @author WJC <jiacheng.wang@hand-china.com>
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  fetchServiceRouteList,
  fetchServiceRouteDetail,
  createServiceRoute,
  updateServiceRoute,
  refreshServiceRoute,
  deleteServiceRoute,
} from '../services/serviceRouteService';

export default {
  namespace: 'hadmServiceRoute',

  state: {
    serviceRouteList: [], // 服务路由列表
    serviceRouteDetail: {}, // 服务路由详情
    pagination: {}, // 分页对象
  },

  effects: {
    * fetchServiceRouteList({ payload }, { call, put }) {
      const res = yield call(fetchServiceRouteList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serviceRouteList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 查询详情数据
    * fetchServiceRouteDetail({ payload }, { call, put }) {
      const res = yield call(fetchServiceRouteDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            serviceRouteDetail: data,
          },
        });
      }
      return data;
    },

    * createServiceRoute({ payload }, { call }) {
      const res = yield call(createServiceRoute, payload);
      return getResponse(res);
    },

    * updateServiceRoute({ payload }, { call }) {
      const res = yield call(updateServiceRoute, payload);
      return getResponse(res);
    },

    * refreshServiceRoute(_, { call }) {
      const res = yield call(refreshServiceRoute);
      return getResponse(res);
    },

    * deleteServiceRoute({ payload }, { call }) {
      const res = yield call(deleteServiceRoute, payload);
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
