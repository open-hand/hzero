import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchList,
  fetchDetail,
  createInterface,
  updateInterface,
  resultInterface,
  deleteInterface,
  createParam,
  updateParam,
  deleteParam,
} from '../services/interfaceDefinitionService';

export default {
  namespace: 'interfaceDefinition',

  state: {
    interfaceDefinitionList: [], // 列表数据
    pagination: {}, // 分页对象
    interfaceDefinitionDetail: {}, // 详情数据
    parameterList: [], // 参数列表
    paramTypeList: [], // 参数类型值集
  },

  effects: {
    * init(_, { call, put }) {
      const res = yield call(queryMapIdpValue, { paramTypeList: 'HWFP.INTERFACE.PARAM_TYPE' });
      const resList = getResponse(res);
      if (resList) {
        yield put({
          type: 'updateState',
          payload: {
            paramTypeList: resList.paramTypeList,
          },
        });
      }
    },

    // 列表数据
    * fetchList({ payload }, { call, put }) {
      const res = yield call(fetchList, parseParameters(payload));
      const resList = getResponse(res);
      if (resList) {
        yield put({
          type: 'updateState',
          payload: {
            interfaceDefinitionList: resList.content,
            pagination: createPagination(resList),
          },
        });
      }
      return resList;
    },

    // 详情数据
    * fetchDetail({ payload }, { call, put }) {
      const res = yield call(fetchDetail, payload);
      const detail = getResponse(res);
      if (detail) {
        yield put({
          type: 'updateState',
          payload: {
            interfaceDefinitionDetail: detail,
            parameterList: detail.parameterList,
          },
        });
      }
      return detail;
    },

    * createInterface({ payload }, { call }) {
      const res = yield call(createInterface, payload);
      return getResponse(res);
    },

    * updateInterface({ payload }, { call }) {
      const res = yield call(updateInterface, payload);
      return getResponse(res);
    },

    * deleteInterface({ payload }, { call }) {
      const res = yield call(deleteInterface, payload);
      return getResponse(res);
    },

    * createParam({ payload }, { call }) {
      const res = yield call(createParam, payload);
      return getResponse(res);
    },

    * updateParam({ payload }, { call }) {
      const res = yield call(updateParam, payload);
      return getResponse(res);
    },

    * deleteParam({ payload }, { call }) {
      const res = yield call(deleteParam, payload);
      return getResponse(res);
    },

    // 测试
    * resultInterface({ payload }, { call }) {
      const res = yield call(resultInterface, payload);
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
