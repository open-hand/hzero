/**
 * model 待办事项列表
 * @date: 2018-8-14
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import uuid from 'uuid/v4';

import { getResponse, createPagination, getCurrentOrganizationId } from 'utils/utils';
import {
  // queryFileList,
  // queryIdpValue,
  // queryUnifyIdpValue,
  queryUUID,
  // removeFileList,
} from 'services/api';
import {
  fetchTaskList,
  searchDetail,
  fetchEmployeeList,
  fetchForecast,
  saveTask,
  getJumpList,
  jumpActivity,
  getAllUser,
  fetchOrderFlowJump,
  fetchOrderFlowNode,
  batchApproveTasks,
  fetchFileCount,
  rollBack,
  fetchHistoryApproval,
  saveTaskComment,
} from '../services/taskService';

export default {
  namespace: 'task',
  state: {
    list: [], // 数据列表
    // employeeList: [], // 员工数据列表
    // employeeQuery: {},
    pagination: {}, // 分页器
    // detail: {}, // 待办事项明细
    // forecast: [], // 流程图数据
    // changeEmployee: [], // 选择的转交人或者加签人
    // uselessParam: 'init', // 确保获取最新流程图的参数
    // jumpList: [], // 可跳转节点列表
    allUserList: [],
    userPagination: {},
    fileCount: null,
  },
  effects: {
    *fetchEmployeeList({ payload }, { call, put }) {
      const { page = 0, size = 10, ...employeeQuery } = payload;
      let result = yield call(fetchEmployeeList, { page, size, ...employeeQuery });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateDetailState',
          payload: {
            employeeQuery,
            employeeList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    *fetchTaskList({ payload }, { call, put }) {
      let result = yield call(fetchTaskList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    *fetchFileCount({ payload }, { call, put }) {
      let result = yield call(fetchFileCount, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            fileCount: result,
          },
        });
      }
      return result;
    },
    *fetchDetail({ payload }, { call, put }) {
      let result = yield call(searchDetail, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateDetailState',
          payload: {
            taskId: payload.taskId,
            detail: result,
            uselessParam: uuid(),
          },
        });
      }
      return result;
    },

    *fetchForecast({ payload }, { call, put }) {
      let result = yield call(fetchForecast, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateDetailState',
          payload: {
            forecast: result,
            taskId: payload.taskId,
          },
        });
      }
      return result;
    },

    // 同意
    *taskAgree({ payload }, { call }) {
      const res = yield call(saveTask, payload);
      // return res;
      return getResponse(res);
    },
    // 拒绝
    *taskRefuse({ payload }, { call }) {
      const res = yield call(saveTask, payload);
      return getResponse(res);
    },

    // 获取指定节点列表
    *getJumpList({ payload }, { call, put }) {
      const res = yield call(getJumpList, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateDetailState',
          payload: {
            taskId: payload.taskId,
            jumpList: result,
          },
        });
      }
      return result;
    },

    /**
     * 获取所有用户列表
     * @param {*} param0
     * @param {*} param1
     */
    *getAllUserList({ payload }, { call, put }) {
      const res = yield call(getAllUser, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            allUserList: result.content,
            userPagination: createPagination(result),
          },
        });
      }
      return result;
    },

    // 跳转指定节点
    *jumpActivity({ payload }, { call }) {
      const res = yield call(jumpActivity, payload);
      return getResponse(res);
    },

    // 批量审批
    *batchApproveTasks({ payload }, { call }) {
      const res = yield call(batchApproveTasks, payload);
      return getResponse(res);
    },
    // 查询UUID
    *fetchUuid(_, { call }) {
      const organizationId = getCurrentOrganizationId();
      const res = yield call(queryUUID, { tenantId: organizationId });
      return getResponse(res);
    },
    // 查询顺序流可选跳转条件
    *fetchOrderFlowJump({ payload }, { call, put }) {
      const res = yield call(fetchOrderFlowJump, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateDetailState',
          payload: {
            orderFlowList: result,
          },
        });
      }
      return result;
    },
    // 选中顺序流节点
    *fetchOrderFlowNode({ payload }, { call }) {
      const res = yield call(fetchOrderFlowNode, payload);
      return getResponse(res);
    },

    *rollBack({ params }, { call }) {
      const res = yield call(rollBack, params);
      return getResponse(res);
    },

    // 查询审批历史记录
    *fetchHistoryApproval({ params }, { call }) {
      const res = yield call(fetchHistoryApproval, params);
      return getResponse(res);
    },

    // 保存未审批完的审批意见
    *saveTaskComment({ params }, { call }) {
      const res = yield call(saveTaskComment, params);
      return getResponse(res);
    },
  },
  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    updateDetailState(state, { payload }) {
      const taskId = payload.taskId || state.taskId;
      const object = {
        [taskId]: {
          employeeList: [],
          employeeQuery: {},
          detail: {},
          forecast: [],
          changeEmployee: [],
          uselessParam: 'init',
          jumpList: [],
          ...state[taskId],
          ...payload,
        },
      };
      return {
        ...state,
        ...object,
        taskId,
      };
    },
  },
};
