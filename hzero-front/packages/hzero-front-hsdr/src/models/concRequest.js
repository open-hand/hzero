/**
 * model 并发管理器/请求定义（并发程序）
 * @date: 2018-9-10
 * @author: LYZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchRequestList,
  createRequest,
  fetchRequestDetail,
  fetchParams,
  deleteJobInfo,
  pauseJobInfo,
  resumeJobInfo,
  triggerJobInfo,
  stopJob,
  fetchJobLog,
  fetchJobLogError,
  deleteJobLog,
  fetchProgress,
} from '../services/concRequestService';

export default {
  namespace: 'concRequest',
  state: {
    list: [], // 数据列表
    pagination: {}, // 分页器
    concRequestDetail: {}, // 详情数据
    paramList: [], // 请求参数
    modalVisible: false, // 新增请求模态框
    concurrentVisible: false, // 请求ID模态框
    logMsgVisible: false, // 日志信息模态框
    intervalTypeList: [], // 间隔类型
    statusList: [], // 状态
    jobLogList: [], // 调度日志列表
    jobPagination: {}, // 调度日志分页
    jobErrorDetail: {}, // 调度日志错误详情
    jobLogLdp: {}, // 调度日志值集
  },
  effects: {
    * init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          jobResultList: 'HSDR.LOG.JOB_RESULT',
          clientResultList: 'HSDR.LOG.CLIENT_RESULT',
          intervalTypeList: 'HSDR.REQUEST.INTERVAL_TYPE',
          statusList: 'HSDR.JOB_STATUS',
        })
      );
      const { intervalTypeList, statusList, ...jobLogLdp } = res;
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            intervalTypeList,
            statusList,
            jobLogLdp,
          },
        });
      }
    },
    * fetchRequestList({ payload }, { call, put }) {
      let result = yield call(fetchRequestList, payload);
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
    // 创建
    * createRequest({ payload }, { call }) {
      const res = yield call(createRequest, payload);
      return getResponse(res);
    },
    // 查询job详情
    * fetchRequestDetail({ payload }, { call, put }) {
      const res = yield call(fetchRequestDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            concRequestDetail: result,
          },
        });
      }
      return result;
    },
    // 查询请求参数
    * fetchParams({ payload }, { call, put }) {
      const res = yield call(fetchParams, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            paramList: result,
          },
        });
      }
      return result;
    },

    // 查询调度日志
    * fetchJobLog({ payload }, { call, put }) {
      const res = yield call(fetchJobLog, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            jobLogList: result.content,
            jobPagination: createPagination(result),
          },
        });
      }
      return result;
    },

    // 查询调度日志错误详情
    * fetchJobLogError({ payload }, { call, put }) {
      const res = yield call(fetchJobLogError, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            jobErrorDetail: result,
          },
        });
      }
      return result;
    },

    // 删除调度日志
    * deleteJobLog({ payload }, { call }) {
      const res = yield call(deleteJobLog, payload);
      return getResponse(res);
    },

    // 删除
    * deleteJobInfo({ payload }, { call }) {
      const res = yield call(deleteJobInfo, payload);
      return getResponse(res);
    },
    // 暂停
    * pauseJobInfo({ payload }, { call }) {
      const res = yield call(pauseJobInfo, payload);
      return getResponse(res);
    },
    // 恢复
    * resumeJobInfo({ payload }, { call }) {
      const res = yield call(resumeJobInfo, payload);
      return getResponse(res);
    },
    // 执行
    * triggerJobInfo({ payload }, { call }) {
      const res = yield call(triggerJobInfo, payload);
      return getResponse(res);
    },
    // 终止
    * stopJob({ payload }, { call }) {
      const res = yield call(stopJob, payload);
      return getResponse(res);
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
