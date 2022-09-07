/**
 * model - 应用类型定义
 * @date: 2019-8-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { isNull } from 'lodash';
import {
  queryList,
  deleteDefinition,
  queryDefinition,
  saveDefinition,
  queryInstanceLineList,
  deleteInstanceLineList,
  queryInstanceDetail,
  saveInstance,
  refreshInstance,
  queryMappingClass,
  testMappingClass,
  fetchMinorCategory,
} from '../services/typeDefinitionService';

export default {
  namespace: 'typeDefinition',
  state: {
    list: {
      dataSource: [], // 应用列表
      pagination: {}, // 应用分页
    },
    instanceHeadInfo: {}, // 实例头信息
    instanceList: {
      dataSource: [], // 实例列表
      pagination: {}, // 实例分页
    },
    instanceDetail: {}, // 实例详情
    enumMap: [], // 值集
    minorCategoryList: [], // 应用小类
  },
  effects: {
    *queryIdpValue(params, { call, put }) {
      const enumMap = getResponse(
        yield call(queryMapIdpValue, {
          composePolicyTypes: 'HITF.COMPOSE_POLICY',
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          enumMap,
        },
      });
    },

    // 查询应用列表
    *queryList({ payload }, { call, put }) {
      const res = yield call(queryList, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },

    // 删除应用定义
    *deleteDefinition({ payload }, { call }) {
      const res = getResponse(yield call(deleteDefinition, payload));
      return res;
    },

    // 查询应用类型详情
    *queryDefinition({ applicationId }, { call, put }) {
      const res = yield call(queryDefinition, applicationId);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            instanceHeadInfo: response,
          },
        });
      }
      return response;
    },

    // 创建/修改应用类型
    *saveDefinition({ payload }, { call }) {
      const res = getResponse(yield call(saveDefinition, payload));
      return res;
    },

    // 查询应用实例列表
    *queryInstanceLineList({ payload }, { call, put }) {
      const res = yield call(queryInstanceLineList, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            instanceList: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },

    // 删除应用实例
    *deleteInstanceLineList({ payload }, { call }) {
      const res = getResponse(yield call(deleteInstanceLineList, payload));
      return res;
    },

    // 查询应用实例详情
    *queryInstanceDetail({ payload }, { call, put }) {
      const res = yield call(queryInstanceDetail, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            instanceDetail: {
              ...response,
              // 把null转换为[]
              applicationInstMapList: isNull(response.applicationInstMapList)
                ? []
                : response.applicationInstMapList,
            },
          },
        });
      }
    },

    // 创建/更新应用实例
    *saveInstance({ payload }, { call }) {
      const res = getResponse(yield call(saveInstance, payload));
      return res;
    },

    // 刷新实例
    *refreshInstance({ payload }, { call, put }) {
      const res = yield call(refreshInstance, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            instanceDetail: response,
          },
        });
      }
    },

    // 查询映射类
    *queryMappingClass({ payload }, { call }) {
      const res = getResponse(yield call(queryMappingClass, payload));
      return res;
    },

    // 测试映射类
    *testMappingClass({ payload }, { call }) {
      const res = getResponse(yield call(testMappingClass, payload));
      return res;
    },

    // 查询应用小类
    *fetchMinorCategory({ payload }, { call, put }) {
      const res = yield call(fetchMinorCategory, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            minorCategoryList: response,
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
