/**
 * model - 租户初始化处理日志
 * @date: 2019/6/18
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { queryLog, queryLogPic } from '../services/tenantInitLog';

export default {
  namespace: 'tenantInitLog', // model名称
  state: {
    logList: [], // 租户初始化处理器配置列表数据
    pagination: {}, // 分页参数
    enumMap: {}, // 值集
    picData: {}, // 图形的数据
  },
  effects: {
    * queryIdpValue(_, { call, put }) {
      const enumMap = getResponse(
        yield call(queryMapIdpValue, {
          processorTypes: 'HPFM.TENANT_PROCESSOR_TYPE',
          initTypes: 'HPFM.TENANT_INIT_TYPE',
          processStatusTypes: 'HPFM.TENANT_INIT_STATUS',
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          enumMap,
        },
      });
    },

    // 查询租户初始化处理日志列表
    * fetchLog({ payload }, { call, put }) {
      let result = yield call(queryLog, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            logList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    // 查询租户初始化处理图形数据
    * fetchLogPic({ payload }, { call, put }) {
      let result = yield call(queryLogPic, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            picData: result.content,
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
