/**
 * model 任务执行器
 * @date: 2018-9-3
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchGroupsList,
  createGroups,
  updateGroups,
  deleteGroups,
  fetchExecutorList,
  deleteExecutor,
  updateExecutor,
} from '../services/jobGroupService';

export default {
  namespace: 'jobGroup',
  state: {
    groupsList: [], // 执行器列表
    jobsList: [], // 执行器任务列表
    pagination: {}, // 分页器
    modalVisible: false, // 控制模态框显示
    statusList: [],
    executorList: [], // 执行器配置列表
  },
  effects: {
    *init(_, { call, put }) {
      const statusList = getResponse(yield call(queryIdpValue, 'HSDR.EXECUTOR_STATUS'));
      yield put({
        type: 'updateState',
        payload: {
          statusList,
        },
      });
    },
    *fetchGroupsList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchGroupsList, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            groupsList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    *createGroups({ payload }, { call }) {
      const result = getResponse(yield call(createGroups, { ...payload }));
      return result;
    },
    *updateGroups({ payload }, { call }) {
      const result = getResponse(yield call(updateGroups, { ...payload }));
      return result;
    },
    *deleteGroups({ payload }, { call }) {
      const result = getResponse(yield call(deleteGroups, { ...payload }));
      return result;
    },

    *fetchExecutorList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchExecutorList, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            executorList: result.map(item => {
              return { ...item, uuid: `${item.executorId}-${item.address}` };
            }),
          },
        });
      }
    },

    *updateExecutor({ payload }, { call }) {
      const result = getResponse(yield call(updateExecutor, payload));
      return result;
    },

    *deleteExecutor({ payload }, { call }) {
      const result = getResponse(yield call(deleteExecutor, payload));
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
