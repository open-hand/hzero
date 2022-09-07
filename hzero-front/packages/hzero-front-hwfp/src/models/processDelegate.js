/**
 * model - 流程指定
 */
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { getResponse, createPagination } from 'utils/utils';
import { queryProcess, delegateProcess } from '@/services/processDelegateServices';

export default {
  namespace: 'processDelegate',
  state: {
    processStatus: [], // 流程状态
  },
  effects: {
    // 查询流程状态
    *queryProcessStatus(_, { call, put }) {
      const processStatus = getResponse(yield call(queryIdpValue, 'HWFP.PROCESS_APPROVE_STATUS'));
      yield put({
        type: 'updateState',
        payload: { processStatus },
      });
    },

    // 流程指定查询
    *fetchProcess({ params }, { call }) {
      const response = getResponse(yield call(queryProcess, params));
      const dataSource = (response || {}).content || [];
      const pagination = createPagination(response || {});
      return { dataSource, pagination };
    },

    // 流程转交
    *delegatePorcess({ params }, { call }) {
      const response = getResponse(yield call(delegateProcess, params));
      return response;
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
