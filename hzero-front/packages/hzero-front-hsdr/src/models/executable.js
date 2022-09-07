/**
 * model 可执行定义
 * @date: 2018-9-7
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchExecutable,
  createExecutable,
  updateExecutable,
  deleteHeader,
  fetchExecutableDetail,
  executorConfig,
  executorConfigByExecutable,
} from '../services/executableService';

export default {
  namespace: 'executable',

  state: {
    groupsList: [], // 执行器列表
    executableList: [], // 列表数据
    executableDetail: {}, // 详情数据
    modalVisible: false, // 控制模态框显示
    pagination: {}, // 分页信息对象
    exeTypeList: [], // 可执行类型
    executorStrategyList: [], // 执行器策略
    failStrategyList: [], // 失败处理策略
    executorConfigList: [], // 执行器配置列表
  },

  effects: {
    // 获取初始化数据
    * init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          exeTypeList: 'HSDR.GLUE_TYPE',
          executorStrategyList: 'HSDR.EXECUTOR_STRATEGY',
          failStrategyList: 'HSDR.FAIL_STRATEGY',
        })
      );
      const { exeTypeList, executorStrategyList, failStrategyList } = res;
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            exeTypeList,
            executorStrategyList,
            failStrategyList,
          },
        });
      }
    },
    * fetchExecutable({ payload }, { call, put }) {
      const res = yield call(fetchExecutable, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            executableList: list.content, // list.content
            pagination: createPagination(list),
          },
        });
      }
    },
    // 查询job详情
    * fetchExecutableDetail({ payload }, { call, put }) {
      const res = yield call(fetchExecutableDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            executableDetail: result,
          },
        });
      }
      return result;
    },
    // 创建
    * createExecutable({ payload }, { call }) {
      const res = yield call(createExecutable, payload);
      return getResponse(res);
    },
    // 更新
    * updateExecutable({ payload }, { call }) {
      const res = yield call(updateExecutable, payload);
      return getResponse(res);
    },
    // 删除头
    * deleteHeader({ payload }, { call }) {
      const result = yield call(deleteHeader, { ...payload });
      return getResponse(result);
    },

    // 执行器配置列表
    * executorConfig({ payload }, { call, put }) {
      const result = yield call(executorConfig, payload);
      const res = getResponse(result);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            executorConfigList: res,
          },
        });
      }
    },

    // 执行器配置列表
    * executorConfigByExecutable({ payload }, { call }) {
      const result = yield call(executorConfigByExecutable, payload);
      const res = getResponse(result);
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
