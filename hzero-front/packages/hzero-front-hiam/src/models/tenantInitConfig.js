/**
 * model - 租户初始化处理器配置
 * @date: 2019/6/18
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { queryConfig, queryFormatConfig } from '../services/tenantInitConfig';

export default {
  namespace: 'tenantInitConfig', // model名称
  state: {
    configList: [], // 租户初始化处理器配置列表数据
    formatConfig: {}, // 格式化处理器配置数据
    enumMap: {}, // 值集
  },
  effects: {
    * queryIdpValue(_, { call, put }) {
      const enumMap = getResponse(
        yield call(queryMapIdpValue, {
          processorTypes: 'HPFM.TENANT_PROCESSOR_TYPE',
          initTypes: 'HPFM.TENANT_INIT_TYPE',
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          enumMap,
        },
      });
    },

    // 查询租户初始化处理器配置列表
    * fetchConfigList({ payload }, { call, put }) {
      let result = yield call(queryConfig, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            configList: result,
          },
        });
      }
    },

    // 格式化查询租户初始化处理器配置
    * fetchFormatConfig(_, { call, put }) {
      let result = yield call(queryFormatConfig);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            formatConfig: result,
          },
        });
      }
    },
  },
  reducers: {
    // 合并state状态数据,生成新的state
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
