/**
 * model 员工定义
 * @date: 2018-6-27
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  addEmployee,
  deleteUser,
  fetchPosition,
  fetchUser,
  renderTreeData,
  saveEmployee,
  searchEmployee,
  searchEmployeeById,
  searchPositionTree,
  searchUserList,
  updatePosition,
  updateUser,
} from '../services/employeeService';

export default {
  namespace: 'employee',

  state: {
    list: [], // 员工信息列表
    positions: [], // 岗位信息（树形结构）
    users: {}, // 用户信息(含分页信息)
    pagination: {}, // 分页参数
    employeeInfo: {}, // 员工信息明细
    pathMap: {}, // 岗位tree中层级结构信息
    expandedRowKeys: [], // 默认展开的岗位Id列表
    positionList: [], // 已分配岗位信息列表
    userList: [], // 已分配的用户信息
    lov: {}, // 存放查询到的值集
  },

  effects: {
    // 获取值集
    *fetchEnum(_, { call, put }) {
      const res = yield call(queryMapIdpValue, { employeeStatus: 'HPFM.EMPLOYEE_STATUS' });
      const safeRes = getResponse(res);
      if (safeRes) {
        yield put({
          type: 'updateState',
          payload: {
            lov: {
              employeeStatus: safeRes.employeeStatus,
            },
          },
        });
      }
    },
    *fetchEmployeeData({ payload }, { call, put }) {
      const result = getResponse(yield call(searchEmployee, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
            employeeInfo: {},
          },
        });
      }
    },
    // 创建员工
    *saveEmployee({ payload }, { call }) {
      const result = yield call(addEmployee, payload);
      return getResponse(result);
    },
    // 获取员工明细信息
    *fetchDetail({ payload }, { call, put }) {
      let result = yield call(searchEmployeeById, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            employeeInfo: result,
          },
        });
      }
    },
    // 获取员工已分配的岗位信息
    *fetchPosition({ payload }, { call, put }) {
      const { tenantId, employeeId, customizeUnitCode, ...otherProps } = payload;
      let result = yield call(fetchPosition, { tenantId, employeeId, customizeUnitCode });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            positionList: result,
            ...otherProps,
          },
        });
      }
    },
    // 获取员工已分配的用户
    *fetchUser({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchUser, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            userList: result,
          },
        });
      }
    },
    // 员工明细保存兼主岗信息
    *saveData({ payload }, { call }) {
      const result = yield call(saveEmployee, payload);
      return getResponse(result);
    },
    // 获取岗位信息(树形结构)
    *searchPosition({ payload }, { call, put }) {
      let result = yield call(searchPositionTree, payload);
      result = getResponse(result) || [];
      const pathMap = renderTreeData(result, {});
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            pathMap,
            positions: result,
            // expandedRowKeys: Object.keys(pathMap),
          },
        });
      }
    },
    // 更新员工岗位信息
    *updatePosition({ payload }, { call }) {
      const result = yield call(updatePosition, payload);
      return getResponse(result);
    },
    // 查询可分配用户信息列表
    *searchUser({ payload }, { call, put }) {
      const result = getResponse(yield call(searchUserList, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            users: result,
          },
        });
      }
    },
    // 移除员工已分配用户信息
    *deleteUser({ payload }, { call }) {
      const result = yield call(deleteUser, { ...payload });
      return getResponse(result);
    },
    // 更新用户已分配用户信息
    *updateUser({ payload }, { call }) {
      const result = yield call(updateUser, { ...payload });
      return getResponse(result);
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
