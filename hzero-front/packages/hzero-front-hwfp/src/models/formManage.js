/**
 * model 流程设置/表单管理
 * @date: 2018-8-15
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import {
  searchCategory,
  searchFormList,
  creatOne,
  editOne,
  deleteOne,
  checkUniqueCode,
} from '../services/formManageService';

export default {
  namespace: 'formManage',
  state: {
    formManageList: {}, // 数据列表
    category: [], // 流程分类
    pagination: {},
  },
  effects: {
    * queryCategory({ payload }, { call, put }) {
      let result = yield call(searchCategory, payload);
      result = getResponse(result);
      const category = result.content.map(item => ({
        value: item.code,
        meaning: item.description,
      }));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            category,
          },
        });
      }
    },
    * fetchFormList({ payload }, { call, put }) {
      let result = yield call(searchFormList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            formManageList: result,
            pagination: createPagination(result),
          },
        });
      }
    },
    // 新建保存
    * creatOne({ payload }, { call }) {
      const { tenantId, ...params } = payload;
      const res = yield call(creatOne, tenantId, { ...params });
      return getResponse(res);
    },
    // 编辑保存
    * editOne({ payload }, { call }) {
      const { tenantId, formDefinitionId, ...params } = payload;
      const res = yield call(editOne, tenantId, formDefinitionId, { ...params });
      return getResponse(res);
    },
    // 删除表单
    * deleteOne({ payload }, { call }) {
      const { tenantId, formDefinitionId, ...params } = payload;
      const res = yield call(deleteOne, tenantId, formDefinitionId, { ...params });
      return getResponse(res);
    },
    // 检查编码唯一性
    * checkUnique({ payload }, { call }) {
      const result = yield call(checkUniqueCode, { ...payload });
      return result;
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
