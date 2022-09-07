/**
 * model - 数据核对
 * @date: 2019/7/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  queryList,
  queryDetailInfo,
  queryDetailList,
  queryProducer,
  queryConsumer,
  confirmLaunch,
} from '../services/dataCheckService';

export default {
  namespace: 'dataCheck', // model名称
  state: {
    list: [], // 数据核对列表数据
    pagination: {}, // 分页参数
    levelTypes: [], // 层级类型
    statusTypes: [], // 状态类型
    detailInfo: {}, // 详情页表单
    detailList: [], // 详情页表格
    detailPagination: {}, // 详情页分页
    sourceList: [], // 来源表数据
    sourcePagination: {}, // 来源表分页
    targetList: [], // 目标表数据
    targetPagination: {}, // 目标表分页
  },
  effects: {
    // 查询数据核对列表
    * fetchCheckList({ payload }, { call, put }) {
      let result = yield call(queryList, payload);
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

    // 查询数据生产消费配置列表
    * fetchProducerList({ payload }, { call, put }) {
      let result = yield call(queryProducer, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            sourceList: result.content,
            sourcePagination: createPagination(result),
          },
        });
      }
    },

    // 查询数据生产消费配置列表
    * fetchConsumerList({ payload }, { call, put }) {
      let result = yield call(queryConsumer, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            targetList: result.content,
            targetPagination: createPagination(result),
          },
        });
      }
    },

    // 发起核对
    * confirmLaunch({ payload }, { call }) {
      const response = yield call(confirmLaunch, payload);
      return getResponse(response);
    },

    * fetchSelect(_, { call, put }) {
      const result = getResponse(
        yield call(queryMapIdpValue, {
          levelTypes: 'HDTT.DATA_CHK_LEVEL',
          statusTypes: 'HDTT.EVENT_PROCESS_STATUS',
        })
      );
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            ...result,
          },
        });
      }
    },

    // 查询数据核对详情表单
    * fetchDetailInfo({ payload }, { call, put }) {
      let result = yield call(queryDetailInfo, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detailInfo: result,
          },
        });
      }
    },

    // 查询数据核对详情列表
    * fetchDetailList({ payload }, { call, put }) {
      let result = yield call(queryDetailList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detailList: result.content,
            detailPagination: createPagination(result),
          },
        });
      }
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
