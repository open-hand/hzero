/**
 * 接口字段维护 /hiam/api-field
 * hiamApiField
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */

import { createPagination, getResponse } from 'utils/utils';

import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';

import {
  apiFieldApiQuery,
  apiFieldFieldCreate,
  apiFieldFieldQuery,
  apiFieldFieldRemove,
  apiFieldFieldUpdate,
} from '../services/apiFieldService';

export default {
  namespace: 'hiamApiField',
  state: {
    requestMethod: [], // 请求方式
    fieldType: [], // 字段类型
    apiDataSource: [],
    apiPagination: {},
    fieldDataSource: [],
    fieldPagination: {},
  },
  effects: {
    // 初始化 接口查询 值集 等 初始化 数据
    * init(_, { call, put }) {
      const res = yield call(queryUnifyIdpValue, 'HIAM.REQUEST_METHOD');
      const requestMethod = getResponse(res);
      if (requestMethod) {
        yield put({
          type: 'updateState',
          payload: {
            requestMethod,
          },
        });
      }
    },
    // 初始化 接口查询 值集 等 初始化 数据
    * fieldInit(_, { call, put }) {
      const res = yield call(queryUnifyIdpValue, 'HIAM.FIELD.TYPE');
      const fieldType = getResponse(res);
      if (fieldType) {
        yield put({
          type: 'updateState',
          payload: {
            fieldType,
          },
        });
      }
    },
    // 查询接口
    * query({ payload }, { call, put }) {
      const { params } = payload;
      const res = yield call(apiFieldApiQuery, params);
      const apis = getResponse(res);
      if (apis) {
        yield put({
          type: 'updateState',
          payload: {
            apiDataSource: apis.content,
            apiPagination: createPagination(apis),
          },
        });
      }
    },
    // 查询接口对应字段
    * queryFields({ payload }, { call, put }) {
      const { permissionId, params } = payload;
      const res = yield call(apiFieldFieldQuery, permissionId, params);
      const fields = getResponse(res);
      if (fields) {
        yield put({
          type: 'updateState',
          payload: {
            fieldDataSource: fields.content,
            fieldPagination: createPagination(fields),
          },
        });
      }
    },
    // 更新字段
    * updateField({ payload }, { call }) {
      const { permissionId, record } = payload;
      const res = yield call(apiFieldFieldUpdate, permissionId, record);
      return getResponse(res);
    },
    // 新增字段
    * createField({ payload }, { call }) {
      const { permissionId, record } = payload;
      const res = yield call(apiFieldFieldCreate, permissionId, record);
      return getResponse(res);
    },
    // 删除字段
    * removeField({ payload }, { call }) {
      const { permissionId, record } = payload;
      const res = yield call(apiFieldFieldRemove, permissionId, record);
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
