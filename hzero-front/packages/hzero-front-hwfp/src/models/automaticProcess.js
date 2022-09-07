/**
 * model 我的抄送流程
 * @date: 2018-8-14
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import {
  queryProcessList,
  deleteProcess,
  updateProcess,
  createProcess,
} from '../services/automaticProcessService';

export default {
  namespace: 'automaticProcess',
  state: {},
  effects: {
    // 查询流程
    * fetchProcessList({ params }, { call }) {
      const response = getResponse(yield call(queryProcessList, params));
      const dataSource = (response || {}).content || [];
      const pagination = createPagination(response || {});
      return { dataSource, pagination };
    },

    // 删除流程
    * removeProcess({ params }, { call }) {
      const response = getResponse(yield call(deleteProcess, params));
      return response || {};
    },

    // 更新流程
    * modifyProcess({ params }, { call }) {
      const response = getResponse(yield call(updateProcess, params));
      return response || {};
    },

    // 新建流程
    * addProcess({ params }, { call }) {
      const response = getResponse(yield call(createProcess, params));
      return response || {};
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
