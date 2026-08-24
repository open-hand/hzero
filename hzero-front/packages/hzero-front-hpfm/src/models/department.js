/**
 * model 部门分配员工
 * @date: 2018-6-20
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';

import {
  renderTreeData,
  search,
  searchAll,
  saveAdd,
  saveEdit,
  forbindLine,
  enabledLine,
  gainCodeAndName,
} from '../services/departmentService';

export default {
  namespace: 'department',

  state: {
    pathMap: {},
    renderTree: [],
    addData: {},
    companyCode: '',
    companyName: '',
    tenantId: '',
    expandedRowKeys: [],
  },
  effects: {
    // 获取部门信息
    *fetchDepartmentInfo({ payload }, { call, put }) {
      const result = getResponse(yield call(gainCodeAndName, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            companyCode: result.unitCode,
            companyName: result.unitName,
          },
        });
      }
    },
    // 获取部门数据列表(Tree)
    *searchDepartmentData({ payload }, { call, put }) {
      const {
        tenantId,
        unitCompanyId,
        unitName,
        unitCode,
        expandFlag,
        expandedRowKeys,
        ...others
      } = payload;
      let result = {};
      let expandedRow = {};
      if (unitCode || unitName) {
        result = yield call(search, { tenantId, unitCompanyId, unitName, unitCode });
      } else {
        result = yield call(searchAll, { tenantId, unitCompanyId });
      }
      result = getResponse(result);

      const { renderTree, pathMap = {} } = renderTreeData(result, {});
      if (!expandFlag) {
        expandedRow = {
          expandedRowKeys: Object.keys(pathMap).map(item => +item),
        };
      } else {
        const unitIdList = result.map(item => item.unitId) || [];
        expandedRow = {
          expandedRowKeys: Array.from(new Set([...expandedRowKeys, ...unitIdList])),
        };
      }

      yield put({
        type: 'updateState',
        payload: {
          renderTree,
          pathMap,
          ...expandedRow,
          ...others,
        },
      });
    },
    // 添加部门信息(批量)
    *saveAddData({ payload }, { call }) {
      const result = yield call(saveAdd, payload);
      return getResponse(result);
    },
    // 更新部门信息(单条)
    *saveEditData({ payload }, { call }) {
      const result = yield call(saveEdit, payload);
      return getResponse(result);
    },
    // 禁用部门信息
    *forbidLine({ payload }, { call }) {
      const result = yield call(forbindLine, payload);
      return getResponse(result);
    },
    // 启用部门信息
    *enabledLine({ payload }, { call }) {
      const result = yield call(enabledLine, payload);
      return getResponse(result);
    },
  },
  reducers: {
    // 更新state
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
