/**
 * @date 2018-12-25
 * @author WJC <jiacheng.wang@hand-china.com>
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchSagaInstanceList,
  fetchSagaInstanceTaskList,
  querySagaInstanceDetail,
  queryInstanceStatistic,
  queryInstanceRun,
  instanceRetry,
  instanceUnlock,
} from '../services/sagaInstanceService';

export default {
  namespace: 'sagaInstance',

  state: {
    sagaInstanceList: [], // 事务实例列表
    sagaInstanceDetail: {}, // 详情数据
    sagaInstanceRunDetail: {}, // 实例运行详情
    instanceStatistic: {}, // 实例统计数据
    pagination: {}, // 事务定义分页对象
    statusList: [], // 状态值集

    sagaInstanceTaskList: [], // 任务列表
    taskPagination: {}, // 任务列表分页
  },

  effects: {
    * init(_, { call, put }) {
      const statusList = getResponse(yield call(queryIdpValue, 'HAGD.SAGA_INSTANCE.STATUS'));
      if (statusList) {
        yield put({
          type: 'updateState',
          payload: {
            statusList,
          },
        });
      }
    },

    // 获取列表数据
    * fetchSagaInstanceList({ payload }, { call, put }) {
      const res = getResponse(yield call(fetchSagaInstanceList, parseParameters(payload)));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            sagaInstanceList: res.content,
            pagination: createPagination(res),
          },
        });
      }
      return res;
    },

    // 查询详情
    * querySagaInstanceDetail({ payload }, { call, put }) {
      const res = getResponse(yield call(querySagaInstanceDetail, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            sagaInstanceDetail: res,
          },
        });
      }
      return res;
    },

    // 查询运行详情
    * queryInstanceRun({ payload }, { call, put }) {
      const res = getResponse(yield call(queryInstanceRun, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            sagaInstanceRunDetail: res,
          },
        });
      }
      return res;
    },

    // 查询实例统计数据
    * queryInstanceStatistic(_, { call, put }) {
      const res = getResponse(yield call(queryInstanceStatistic));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            instanceStatistic: res,
          },
        });
      }
      return res;
    },

    // 查询任务列表
    * fetchSagaInstanceTaskList({ payload }, { call, put }) {
      const res = getResponse(yield call(fetchSagaInstanceTaskList, parseParameters(payload)));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            sagaInstanceTaskList: res.content,
            taskPagination: createPagination(res),
          },
        });
      }
      return res;
    },

    // 重试
    * instanceRetry({ payload }, { call }) {
      const res = getResponse(yield call(instanceRetry, payload));
      return res;
    },

    // 解锁
    * instanceUnlock({ payload }, { call }) {
      const res = getResponse(yield call(instanceUnlock, payload));
      return res;
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
