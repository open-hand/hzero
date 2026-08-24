/**
 * model 数据模型
 * @date: 2019-12-12
 * @author: xiongjg
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination } from 'utils/utils';

import {
  queryList,
  queryModelDetail,
  queryTablesList,
  createModel,
  queryFieldsList,
  modifyModel,
  refreshField,
  createField,
  queryModelRelationList,
  modifyField,
  removeField,
  createRelation,
  deleteRelation,
  saveRelationn,
} from '@/services/flexModelService';

export default {
  namespace: 'flexModel',
  state: {
    dataSource: [],
    pagination: {},
    tables: [],
  },
  effects: {
    // 获取列表
    *queryList({ params }, { call, put }) {
      const response = getResponse(yield call(queryList, params));
      const dataSource = (response || {}).content || [];
      const pagination = createPagination(response || {});
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource,
            pagination,
          },
        });
      }
      return { dataSource, pagination };
    },

    // 查询模型详情
    *queryModelDetail({ params }, { call }) {
      const response = getResponse(yield call(queryModelDetail, params));
      return response || {};
    },

    *fetchTablesList({ params }, { call, put }) {
      const response = getResponse(yield call(queryTablesList, params));
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            tables: response,
          },
        });
      }
      return response;
    },

    *saveModel({ params }, { call }) {
      const response = getResponse(yield call(createModel, params));
      return response || {};
    },

    *updateModel({ params }, { call }) {
      const response = getResponse(yield call(modifyModel, params));
      return response;
    },

    // 获取字段列表
    *queryFieldsList({ params }, { call }) {
      const response = getResponse(yield call(queryFieldsList, params));
      return {
        dataSource: response || [],
      };
    },

    // 字段初始化
    *initFields({ params }, { call }) {
      const response = getResponse(yield call(refreshField, params));
      return {
        dataSource: response || [],
      };
    },

    // 添加字段
    *addField({ params }, { call }) {
      const response = getResponse(yield call(createField, params));
      return response;
    },

    // 查询关联模型
    *queryModelRelationList({ params }, { call }) {
      const response = getResponse(yield call(queryModelRelationList, params));
      return {
        dataSource: response || [],
      };
    },

    // 修改字段
    *saveField({ params }, { call }) {
      const response = getResponse(yield call(modifyField, params));
      return response;
    },

    // 刪除字段
    *removeField({ params }, { call }) {
      const response = getResponse(yield call(removeField, params));
      return response;
    },

    // 新建关系
    *createRelation({ params }, { call }) {
      const response = getResponse(yield call(createRelation, params));
      return response;
    },

    // 删除关系
    *deleteRelation({ params }, { call }) {
      const response = getResponse(yield call(deleteRelation, params));
      return response;
    },

    // 保存关系
    *saveRelation({ params }, { call }) {
      const response = getResponse(yield call(saveRelationn, params));
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
