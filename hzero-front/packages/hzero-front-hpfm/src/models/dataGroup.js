/**
 * model - 数据组管理
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import {
  queryList,
  queryLineList,
  queryModalList,
  queryDataGroupHead,
  queryValueModalList,
  queryAssignedValueList,
  createDataGroup,
  createAssignedValue,
  createDataGroupLine,
  deleteDataGroupLine,
  deleteAssignedValue,
  updateDataGroupHead,
} from '../services/dataGroupService';

export default {
  namespace: 'dataGroup', // model名称
  state: {
    dataGroupList: [], // 数据组列表
    pagination: {}, // 数据组列表分页参数
    lineList: [], // 数据组行列表
    linePagination: {}, // 数据组行列表分页
    assignedValueList: [], // 分配值列表
    assignedValuePagination: {}, // 分配值分页
    dataGroupHeadInfo: {}, // 数据组头信息
    createDataSource: [], // 新建数据组行的弹窗列表数据
    createPagination: {}, // 新建数据组行的弹窗列表分页
    valueDataSource: [], // 新建数据组值的弹窗列表数据
    valuePagination: {}, // 新建数据组值的弹窗列表分页
  },
  effects: {
    // 查询数据组列表
    *fetchDataGroupList({ payload }, { call, put }) {
      let result = yield call(queryList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            dataGroupList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    // 新建数据组
    *createDataGroup({ payload }, { call }) {
      const response = yield call(createDataGroup, payload);
      return getResponse(response);
    },

    // 查询数据组头信息
    *fetchDataGroupHead({ payload }, { call, put }) {
      let result = yield call(queryDataGroupHead, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            dataGroupHeadInfo: result,
          },
        });
      }
    },

    // 更新数据组头信息
    *updateDataGroupHead({ payload }, { call }) {
      const result = yield call(updateDataGroupHead, payload);
      return getResponse(result);
    },

    // 查询数据组行列表数据
    *fetchDataGroupLineList({ payload }, { call, put }) {
      let result = yield call(queryLineList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            lineList: result.content,
            linePagination: createPagination(result),
          },
        });
      }
    },

    // 获取选择维度代码弹窗数据
    *fetchModalData({ payload }, { call, put }) {
      let result = yield call(queryModalList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            createDataSource: result.content,
            createPagination: createPagination(result),
          },
        });
      }
    },

    // 新建数据组行
    *createDataGroupLine({ payload }, { call }) {
      const response = yield call(createDataGroupLine, payload);
      return getResponse(response);
    },

    // 删除数据组行
    *deleteDataGroupLine({ payload }, { call }) {
      const result = yield call(deleteDataGroupLine, payload);
      return getResponse(result);
    },

    // 查询分配值列表
    *fetchAssignedValueList({ payload }, { call, put }) {
      let result = yield call(queryAssignedValueList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            assignedValueList: result.content,
            assignedValuePagination: createPagination(result),
          },
        });
      }
    },

    // 查询选择维度值的弹窗数据
    *fetchValueModalData({ payload }, { call, put }) {
      let result = yield call(queryValueModalList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            valueDataSource: result.content,
            valuePagination: createPagination(result),
          },
        });
      }
    },

    // 新建分配值
    *createAssignedValue({ payload }, { call }) {
      const response = yield call(createAssignedValue, payload);
      return getResponse(response);
    },

    // 删除分配值
    *deleteAssignedValue({ payload }, { call }) {
      const result = yield call(deleteAssignedValue, payload);
      return getResponse(result);
    },
  },
  reducers: {
    // 合并state状态数据,生成新的state
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
