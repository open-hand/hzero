/**
 * model 岗位维护与管理
 * @date: 2018-6-19
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse } from 'utils/utils';
import {
  renderTreeData,
  search,
  gainInfo,
  saveAdd,
  saveEdit,
  enabledLine,
  forbindLine,
} from '../services/postService';

export default {
  namespace: 'post',
  state: {
    pathMap: {},
    addData: {},
    renderTree: [],
    unitCode: '',
    unitName: '',
    companyId: '',
    expandedRowKeys: [],
  },
  effects: {
    // 获取部门信息
    * fetchUnitInfo({ payload }, { call, put }) {
      const result = getResponse(yield call(gainInfo, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            unitCode: result.unitCode,
            unitName: result.unitName,
            companyId: result.unitCompanyId,
          },
        });
      }
    },
    // 获取岗位信息
    * fetchPositionData({ payload }, { call, put }) {
      const {
        tenantId,
        unitId,
        positionCode,
        positionName,
        expandFlag,
        expandedRowKeys,
        ...others
      } = payload;

      let expandedRow = [];
      let result = yield call(search, { tenantId, unitId, positionCode, positionName });
      result = getResponse(result);
      const { renderTree, pathMap } = renderTreeData(result, {});
      if (!expandFlag) {
        expandedRow = {
          expandedRowKeys: Object.keys(pathMap).map(item => +item),
        };
      } else {
        const positionIdList = result.map(item => item.positionId) || [];
        expandedRow = {
          expandedRowKeys: Array.from(new Set([...expandedRowKeys, ...positionIdList])),
        };
      }
      yield put({
        type: 'updateState',
        payload: {
          addData: {},
          renderTree,
          pathMap,
          ...expandedRow,
          ...others,
        },
      });
    },
    // 新增岗位信息
    * saveData({ payload }, { call }) {
      const result = yield call(saveAdd, payload);
      return getResponse(result);
    },
    // 更新岗位信息
    * saveEdit({ payload }, { call }) {
      const result = yield call(saveEdit, payload);
      return getResponse(result);
    },
    // 禁用岗位
    * forbindLine({ payload }, { call }) {
      const result = yield call(forbindLine, payload);
      return getResponse(result);
    },
    // 启用岗位
    * enabledLine({ payload }, { call }) {
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
