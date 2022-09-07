/**
 * model 调度日志
 * @date: 2018-9-3
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchLogsList,
  deleteLogs,
  fetchErrorDetail,
  fetchLogDetail,
  fetchProgress,
  clearLogs,
} from '../services/jobLogService';

export default {
  namespace: 'jobLog',

  state: {
    groupsList: [], // 执行器列表
    jobsList: [], // 执行器任务列表
    jobLogList: [], // 列表数据
    clearTypeList: [], // 清理方式
    pagination: {}, // 分页信息对象
    // addressTypeList: [], // 注册方式
    // logStatusList: [], // 任务状态
    jobResultList: [], // 调度结果
    clientResultList: [], // 客户端执行结果
    handleMsgData: {}, // 执行日志
    query: {}, // 查询参数
    skipQuery: {}, // 从调度任务或者并发请求页面跳转过来时携带的参数
  },

  effects: {
    // 获取初始化数据
    * init(_, { call, put }) {
      // const addressTypeList = getResponse(yield call(queryIdpValue, 'HSDR.SHCEDULER.ADDRESS_TYPE'));
      // const logStatusList = getResponse(yield call(queryIdpValue, 'HSDR.JOB_STATUS'));
      // const clearTypeList = getResponse(yield call(queryIdpValue, 'HSDR.JOB_LOG_CLEAR_TYPE'));
      const jobLogMap = getResponse(
        yield call(queryMapIdpValue, {
          clientResultList: 'HSDR.LOG.CLIENT_RESULT',
          jobResultList: 'HSDR.LOG.JOB_RESULT',
          clearTypeList: 'HSDR.JOB_LOG_CLEAR_TYPE',
        })
      );
      const { jobResultList = [], clientResultList = [], clearTypeList = [] } = jobLogMap;
      yield put({
        type: 'updateState',
        payload: {
          // addressTypeList,
          // logStatusList,
          // clearTypeList,
          jobResultList,
          clientResultList,
          clearTypeList,
        },
      });
    },
    * fetchLogsList({ payload }, { call, put }) {
      const { page = 0, size = 10, ...query } = payload;
      const res = yield call(fetchLogsList, { page, size, ...query });
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            query,
            jobLogList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    * deleteLogs({ payload }, { call }) {
      const result = yield call(deleteLogs, { ...payload });
      return getResponse(result);
    },

    * clearLogs({ payload }, { call }) {
      const result = yield call(clearLogs, { ...payload });
      return getResponse(result);
    },

    // 获取错误详情
    * fetchErrorDetail({ payload }, { call }) {
      const result = yield call(fetchErrorDetail, payload);
      return getResponse(result);
    },
    // 获取错误详情
    * fetchLogDetail({ payload }, { call }) {
      const result = yield call(fetchLogDetail, payload);
      return getResponse(result);
    },
    // 获取任务进度
    * fetchProgress({ payload }, { call }) {
      const result = yield call(fetchProgress, payload);
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
