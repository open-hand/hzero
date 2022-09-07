/**
 * model 调度任务
 * @date: 2018-9-3
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchGroupsList,
  fetchJobInfo,
  createJobInfo,
  updateJobInfo,
  deleteJobInfo,
  pauseJobInfo,
  resumeJobInfo,
  triggerJobInfo,
  queryJobInfo,
  updateJobGlue,
  queryJobGlueList,
  queryJobGlueDetail,
  stopJob,
  fetchJobLog,
  fetchJobLogError,
  deleteJobLog,
  fetchProgress,
  copyJobInfo,
} from '../services/jobInfoService';
import { checkExecutor, executorConfig, executorConfigByJob } from '../services/jobGroupService';

export default {
  namespace: 'jobInfo',

  state: {
    jobInfoList: [], // 列表数据
    jobInfoDetail: {}, // 详情数据
    modalVisible: false, // 控制模态框显示
    glueList: [], // glue更改日志列表
    pagination: {}, // 分页信息对象
    groupsList: [], // 执行器
    executorRouteList: [], // 路由策略
    glueTypeList: [], // 任务类型
    jobStatusList: [], // 状态
    executorBlockList: [], // 阻塞处理策略
    executorFailList: [], // 失败处理策略
    jobLogList: [], // 调度日志列表
    jobPagination: {}, // 调度日志分页
    jobErrorDetail: {}, // 调度日志错误详情
    jobLogLdp: {}, // 调度日志值集
    executorConfigList: [], // 执行器配置列表
    sourceFlagList: [],
  },

  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          jobResultList: 'HSDR.LOG.JOB_RESULT',
          clientResultList: 'HSDR.LOG.CLIENT_RESULT',
          jobStatusList: 'HSDR.JOB_STATUS',
          executorRouteList: 'HSDR.EXECUTOR_STRATEGY',
          glueTypeList: 'HSDR.GLUE_TYPE',
          executorBlockList: 'HSDR.EXECUTOR_BLOCK_STRATEGY',
          executorFailList: 'HSDR.FAIL_STRATEGY',
          sourceFlagList: 'HSDR.JOB.SOURCE_FLAG',
        })
      );
      const {
        executorRouteList,
        glueTypeList,
        executorBlockList,
        executorFailList,
        sourceFlagList,
        ...jobLogLdp
      } = res;
      const { jobStatusList = [] } = jobLogLdp;
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            jobLogLdp,
            executorRouteList,
            glueTypeList,
            jobStatusList,
            executorBlockList,
            executorFailList,
            sourceFlagList,
          },
        });
      }
    },
    // 获取执行器
    *fetchGroupsList(_, { call, put }) {
      const result = getResponse(yield call(fetchGroupsList));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            groupsList: result,
          },
        });
      }
    },
    *fetchJobInfo({ payload }, { call, put }) {
      const res = yield call(fetchJobInfo, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            jobInfoList: list.content,
            pagination: createPagination(list),
          },
        });
      }
    },
    // 查询job详情
    *queryJobInfo({ payload }, { call, put }) {
      const res = yield call(queryJobInfo, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            jobInfoDetail: list,
          },
        });
      }
      return list;
    },
    // 查询glue详情
    *queryJobGlueDetail({ payload }, { call, put }) {
      const res = yield call(queryJobGlueDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            jobInfoDetail: list,
          },
        });
      }
      return list;
    },
    *queryJobGlueList({ payload }, { call, put }) {
      const res = yield call(queryJobGlueList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            glueList: list,
          },
        });
      }
      return list;
    },

    // 查询调度日志
    *fetchJobLog({ payload }, { call, put }) {
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
    *fetchJobLogError({ payload }, { call, put }) {
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
    *deleteJobLog({ payload }, { call }) {
      const res = yield call(deleteJobLog, payload);
      return getResponse(res);
    },

    // 创建
    *createJobInfo({ payload }, { call }) {
      const res = yield call(createJobInfo, payload);
      return getResponse(res);
    },
    // 更新
    *updateJobInfo({ payload }, { call }) {
      const res = yield call(updateJobInfo, payload);
      return getResponse(res);
    },
    // 更新glue
    *updateJobGlue({ payload }, { call }) {
      const res = yield call(updateJobGlue, payload);
      return getResponse(res);
    },
    // 删除
    *deleteJobInfo({ payload }, { call }) {
      const res = yield call(deleteJobInfo, payload);
      return getResponse(res);
    },
    // 暂停
    *pauseJobInfo({ payload }, { call }) {
      const res = yield call(pauseJobInfo, payload);
      return getResponse(res);
    },
    // 恢复
    *resumeJobInfo({ payload }, { call }) {
      const res = yield call(resumeJobInfo, payload);
      return getResponse(res);
    },
    // 执行
    *triggerJobInfo({ payload }, { call }) {
      const res = yield call(triggerJobInfo, payload);
      return getResponse(res);
    },
    // 终止
    *stopJob({ payload }, { call }) {
      const res = yield call(stopJob, payload);
      return getResponse(res);
    },
    // 获取任务进度
    *fetchProgress({ payload }, { call }) {
      const result = yield call(fetchProgress, payload);
      return getResponse(result);
    },
    // 检查
    *checkJobInfo({ payload }, { call }) {
      const result = yield call(checkExecutor, payload);
      return getResponse(result);
    },

    // 执行器配置列表
    *executorConfig({ payload }, { call }) {
      const result = yield call(executorConfig, payload);
      const res = getResponse(result);
      // if (res) {
      //   yield put({
      //     type: 'updateState',
      //     payload: {
      //       executorConfigList: res,
      //     },
      //   });
      // }
      return res;
    },

    // 执行器配置列表
    *executorConfigByJob({ payload }, { call }) {
      const result = yield call(executorConfigByJob, payload);
      const res = getResponse(result);
      return res;
    },

    // 复制
    *copyJobInfo({ payload }, { call }) {
      const result = yield call(copyJobInfo, payload);
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
