/**
 * model 流程设置/流程启动
 * @date: 2018-8-21
 * @author: CJ <juan.chen@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import uuid from 'uuid/v4';
import { getResponse } from 'utils/utils';
import { startProcess, getVariableList } from '../services/processStartService';

export default {
  namespace: 'processStart',
  state: {
    variables: [], // 表格数据
  },
  effects: {
    // 启动
    * startProcess({ payload }, { call }) {
      const { tenantId, ...params } = payload;
      const res = yield call(startProcess, tenantId, { ...params });
      return getResponse(res);
    },

    // 查询参数
    * getVariableList({ payload }, { call, put }) {
      const res = yield call(getVariableList, payload);
      const result = getResponse(res);
      if (res) {
        yield put({
          type: 'updateState',
          // TODO: 后台无法提供唯一标识
          payload: {
            variables: result.map(item => ({ ...item, variableId: uuid() })),
          },
        });
      }
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
