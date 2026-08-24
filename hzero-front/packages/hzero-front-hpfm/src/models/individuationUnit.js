/**
 * model 个性化单元
 * @date: 2019-12-12
 * @author: xiongjg
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';

import {
  queryList,
  queryMenuTree,
  createUnit,
  queryUnitDetail,
  modifyUnit,
  saveUnitField,
  queryRelationModels,
  queryRelatedUnits,
  deleteField,
  queryGroup,
  createGroup,
  modifyGroup,
  queryGroupUnits,
  copyUnit,
} from '@/services/individuationUnitService';

export default {
  namespace: 'individuationUnit',
  state: {
    dataSource: [],
    pagination: {},
    tables: [],
  },
  effects: {
    // 获取列表
    *queryList({ params }, { call }) {
      const response = getResponse(yield call(queryList, params));
      const dataSource = (response || {}).content || [];
      const pagination = createPagination(response || {});
      return { dataSource, pagination };
    },

    // 获取菜单树
    *fetchMenu({ params }, { call }) {
      const response = getResponse(yield call(queryMenuTree, params));
      return response;
    },

    // 创建个性化单元
    *createUnit({ params }, { call }) {
      const response = getResponse(yield call(createUnit, params));
      return response;
    },

    // 个性化单元详情以及字段配置
    *fetchUnitDetail({ params }, { call }) {
      const response = getResponse(yield call(queryUnitDetail, params));
      return response;
    },

    // 修改个性化单元信息
    *modifyUnit({ params }, { call }) {
      const response = getResponse(yield call(modifyUnit, params));
      return response;
    },

    // 保存个性化单元字段
    *saveUnitField({ params }, { call }) {
      const response = getResponse(yield call(saveUnitField, params));
      return response;
    },

    // 查询关联模型
    *queryRelationModels({ params }, { call }) {
      const response = getResponse(yield call(queryRelationModels, params));
      return response;
    },

    // 删除个性化单元字段
    *deleteField({ params }, { call }) {
      const response = getResponse(yield call(deleteField, params));
      return response;
    },

    // 查询分组
    *queryGroup({ params }, { call }) {
      const response = getResponse(yield call(queryGroup, params));
      return response || [];
    },

    // 创建分组
    *createGroup({ params }, { call }) {
      const response = getResponse(yield call(createGroup, params));
      return response || {};
    },

    // 修改分组
    *modifyGroup({ params }, { call }) {
      const response = getResponse(yield call(modifyGroup, params));
      return response || {};
    },

    // 查询分组下的所有个性化单元
    *queryGroupUnits({ params }, { call }) {
      const response = getResponse(yield call(queryGroupUnits, params));
      return response || [];
    },

    // 复制个性化单元
    *copyUnit({ params }, { call }) {
      const response = getResponse(yield call(copyUnit, params));
      return response;
    },

    // 查询关联单元字段配置
    *queryRelatedUnits({ payload }, { call }) {
      return getResponse(yield call(queryRelatedUnits, payload));
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
