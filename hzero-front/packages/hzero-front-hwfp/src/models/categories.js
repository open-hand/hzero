/**
 * @date: 2018-8-16
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchCategories,
  createCategories,
  editCategories,
  deleteCategories,
  handleSearchDocuments,
  fetchDetailHeader,
  updateHeader,
  fetchDetailList,
  handleSaveVariables,
  handleUpdateVariables,
  deleteVariable,
} from '../services/categoriesService';

export default {
  namespace: 'categories',
  state: {
    dataSource: [],
    pagination: {},
  },
  effects: {
    // 查询值集
    * fetchEnumMap(params, { call, put }) {
      const enumMap = getResponse(
        yield call(queryMapIdpValue, {
          variableTypes: 'HWFP.PROCESS.VARIABLE_TYPE',
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          enumMap: enumMap || {},
        },
      });
    },
    // 查询分类列表
    * fetchCategories({ payload }, { call, put }) {
      const res = yield call(fetchCategories, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource: list.content || [],
            pagination: createPagination(list),
          },
        });
      }
    },
    // 新建保存
    * createCategories({ payload }, { call }) {
      const res = yield call(createCategories, payload);
      return getResponse(res);
    },
    // 修改头
    * updateHeader({ payload }, { call }) {
      const res = yield call(updateHeader, payload);
      return getResponse(res);
    },
    // 编辑保存
    * editCategories({ payload }, { call }) {
      const { processCategoryId, ...params } = payload;
      const res = yield call(editCategories, processCategoryId, { ...params });
      return getResponse(res);
    },
    // 删除流程分类
    * deleteCategories({ payload }, { call }) {
      const { categoryId, ...params } = payload;
      const res = yield call(deleteCategories, categoryId, { ...params });
      return getResponse(res);
    },
    // 查询流程单据
    * handleSearchDocuments({ payload }, { call }) {
      const res = yield call(handleSearchDocuments, payload);
      return getResponse(res);
    },
    // 查询详情头
    * fetchDetailHeader({ payload }, { call }) {
      const res = yield call(fetchDetailHeader, payload);
      return getResponse(res);
    },
    // 查询详情列表
    * fetchDetailList({ payload }, { call }) {
      const res = yield call(fetchDetailList, payload);
      return getResponse(res);
    },
    // 保存详情流程变量
    * handleSaveVariables({ payload }, { call }) {
      const res = yield call(handleSaveVariables, payload);
      return getResponse(res);
    },
    // 更新详情流程变量
    * handleUpdateVariables({ payload }, { call }) {
      const res = yield call(handleUpdateVariables, payload);
      return getResponse(res);
    },
    // 删除流程变量
    * deleteVariable({ payload }, { call }) {
      const res = yield call(deleteVariable, payload);
      return getResponse(res);
    },
  },
  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
