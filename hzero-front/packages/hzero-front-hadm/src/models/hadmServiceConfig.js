/**
 * @date 2019-01-23
 * @author WJC <jiacheng.wang@hand-china.com>
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  fetchServiceConfigList,
  fetchServiceConfigDetail,
  createServiceConfig,
  updateServiceConfig,
  refreshServiceConfig,
  deleteServiceConfig,
} from '../services/serviceConfigService';

export default {
  namespace: 'hadmServiceConfig',

  state: {
    serviceConfigList: [], // 服务配置列表
    serviceConfigDetail: {}, // 服务配置详情
    pagination: {}, // 分页对象
  },

  effects: {
    * fetchServiceConfigList({ payload }, { call, put }) {
      const res = yield call(fetchServiceConfigList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serviceConfigList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 查询详情数据
    * fetchServiceConfigDetail({ payload }, { call, put }) {
      const res = yield call(fetchServiceConfigDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            serviceConfigDetail: data,
          },
        });
      }
      return data;
    },

    * createServiceConfig({ payload }, { call }) {
      const res = yield call(createServiceConfig, payload);
      return getResponse(res);
    },

    * updateServiceConfig({ payload }, { call }) {
      const res = yield call(updateServiceConfig, payload);
      return getResponse(res);
    },

    // 刷新
    * refreshServiceConfig({ payload }, { call }) {
      const res = yield call(refreshServiceConfig, payload);
      return getResponse(res);
    },

    // 删除
    * deleteServiceConfig({ payload }, { call }) {
      const res = yield call(deleteServiceConfig, payload);
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
