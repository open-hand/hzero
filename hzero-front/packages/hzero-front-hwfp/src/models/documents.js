/*
 * document.js - 流程单据
 * @date: 2019-04-28
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchList,
  createDocuments,
  deleteDocuments,
  fetchVariableList,
  fetchDetailHeader,
  updateHeader,
  handleSaveVariables,
  handleSearchCategories,
  deleteVariable,
  handleUpdateVariables,
  fetchFormList,
  fetchEmailList,
  handleSaveForm,
  handleUpdateForm,
  deleteForm,
  deleteEmail,
  handleSaveEmail,
  handleUpdateEmail,
} from '../services/documentsService';

export default {
  namespace: 'documents',
  state: {
    dataSource: [],
    pagination: {},
    enumMap: {},
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
    // 查询列表数据
    * fetchList({ payload }, { call, put }) {
      const res = yield call(fetchList, payload);
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
    * createDocuments({ payload }, { call }) {
      const res = yield call(createDocuments, payload);
      return getResponse(res);
    },
    // 修改头
    * updateHeader({ payload }, { call }) {
      const res = yield call(updateHeader, payload);
      return getResponse(res);
    },
    // 删除流程单据
    * deleteDocuments({ payload }, { call }) {
      const res = yield call(deleteDocuments, payload);
      return getResponse(res);
    },
    // 查询详情头
    * fetchDetailHeader({ payload }, { call }) {
      const res = yield call(fetchDetailHeader, payload);
      return getResponse(res);
    },
    // 查询详情变量列表
    * fetchVariableList({ payload }, { call }) {
      const res = yield call(fetchVariableList, payload);
      return getResponse(res);
    },
    // 查询详情表单列表
    * fetchFormList({ payload }, { call }) {
      const res = yield call(fetchFormList, payload);
      return getResponse(res);
    },
    // 查询详情邮件列表
    * fetchEmailList({ payload }, { call }) {
      const res = yield call(fetchEmailList, payload);
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
    // 保存详情流程表单
    * handleSaveForm({ payload }, { call }) {
      const res = yield call(handleSaveForm, payload);
      return getResponse(res);
    },
    // 更新详情流程表单
    * handleUpdateForm({ payload }, { call }) {
      const res = yield call(handleUpdateForm, payload);
      return getResponse(res);
    },
    // 保存详情邮件表单
    * handleSaveEmail({ payload }, { call }) {
      const res = yield call(handleSaveEmail, payload);
      return getResponse(res);
    },
    // 更新详情邮件表单
    * handleUpdateEmail({ payload }, { call }) {
      const res = yield call(handleUpdateEmail, payload);
      return getResponse(res);
    },
    // 删除流程变量
    * deleteVariable({ payload }, { call }) {
      const res = yield call(deleteVariable, payload);
      return getResponse(res);
    },
    // 删除流程表单
    * deleteForm({ payload }, { call }) {
      const res = yield call(deleteForm, payload);
      return getResponse(res);
    },
    // 删除邮件表单
    * deleteEmail({ payload }, { call }) {
      const res = yield call(deleteEmail, payload);
      return getResponse(res);
    },
    // 查询流程分类
    * handleSearchCategories({ payload }, { call }) {
      const res = yield call(handleSearchCategories, payload);
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
