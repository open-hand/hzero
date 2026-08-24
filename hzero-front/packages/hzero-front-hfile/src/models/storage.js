/**
 * model 文件管理配置页面
 * @date: 2018-7-25
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, getCurrentOrganizationId } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchDefaultStorage,
  fetchStorage,
  updateStorage,
  deleteStorage,
  queryLdpTree,
} from '../services/storageService';

export default {
  namespace: 'storage',

  state: {
    storageDataList: [], // 文件配置数据
    tenantId: getCurrentOrganizationId(), // 当前用户所属租户id
    formKey: '',
    serverProviderList: [], // 权限控制父子值集
    prefixStrategyList: [], // 文件名前缀策略
    microsoftEndpointList: [], // 微软后缀
    radioTypeList: [], // 文件类型值集
  },

  effects: {
    * init(_, { put, call }) {
      const { prefixStrategyList, microsoftEndpointList, radioTypeList } = yield getResponse(
        call(queryMapIdpValue, {
          prefixStrategyList: 'HFLE.PREFIX_STRATEGY',
          microsoftEndpointList: 'HFLE.MICROSOFT.ENDPOINT_SUFFIX',
          radioTypeList: 'HFLE.SERVER_PROVIDER',
        })
      );
      const serverProviderList = getResponse(
        yield call(queryLdpTree, {
          'HFLE.SERVER_PROVIDER': 1,
          'HFLE.CAPACITY.ACCESS_CONTROL': 2,
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          serverProviderList,
          prefixStrategyList,
          microsoftEndpointList,
          radioTypeList: radioTypeList.filter(item => item.tag !== 'UNUSE'), // 过滤未使用的
        },
      });
    },

    * fetchDefaultStorage({ payload }, { put, call }) {
      const res = yield call(fetchDefaultStorage, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            storageDataList: data && data.content,
          },
        });
      }
      return data;
    },

    * fetchStorage({ payload }, { put, call }) {
      const res = yield call(fetchStorage, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            storageDataList: data && data.content,
          },
        });
      }
    },

    * updateStorage({ payload }, { call }) {
      const res = yield call(updateStorage, payload);
      return getResponse(res);
    },

    * deleteStorage({ payload }, { call }) {
      const res = yield call(deleteStorage, payload);
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
