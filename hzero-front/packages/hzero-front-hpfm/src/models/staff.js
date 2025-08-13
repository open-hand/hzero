/**
 * model 员工维护与管理
 * @date: 2018-6-19
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';

import {
  searchPositionInfo,
  searchAddibleStaff,
  searchAddedStaff,
  deleteStaff,
  addStaff,
} from '../services/staffService';

export default {
  namespace: 'staff',
  state: {
    addedStaff: {
      // 已添加的员工
      list: [],
      pagination: {},
    },
    addibleStaff: {
      // 可添加的员工
      list: [],
      pagination: {},
    },
    positionCode: '',
    positionName: '',
    unitId: '',
  },

  effects: {
    // 获取岗位信息
    *fetchPositionInfo({ payload }, { call, put }) {
      const result = getResponse(yield call(searchPositionInfo, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            positionCode: result.positionCode,
            positionName: result.positionName,
            unitId: result.unitId,
          },
        });
      }
    },
    // 获取岗位可已添加的员工列表
    *fetchAddibleStaff({ payload }, { call, put }) {
      let result = yield call(searchAddibleStaff, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            addibleStaff: {
              list: result.content,
              pagination: createPagination(result),
            },
          },
        });
      }
    },
    // 获取岗位已添加的员工列表
    *fetchAddedStaff({ payload }, { call, put }) {
      let result = yield call(searchAddedStaff, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            addedStaff: {
              list: result.content,
              pagination: createPagination(result),
            },
          },
        });
      }
    },
    // 删除员工信息
    *deleteStaff({ payload }, { call }) {
      const result = yield call(deleteStaff, payload);
      return getResponse(result); // 操作成功时，接口返回空对象
    },
    // 添加员工信息
    *addStaff({ payload }, { call }) {
      const result = yield call(addStaff, payload);
      return getResponse(result); // 操作成功时，接口返回空对象
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
