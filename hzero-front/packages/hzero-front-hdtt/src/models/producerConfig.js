/**
 * model - 数据消息生产消费配置
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  queryProducer,
  queryConsumerList,
  queryConsumerTenantList,
  queryConsumerDbConfig,
  queryProducerDetail,
  saveProducer,
  saveConsumer,
  updateProducer,
  updateConsumer,
  updateDdl,
  deleteProducer,
  deleteConsumer,
  deleteTenantConsumer,
  initConsumer,
} from '../services/producerConfigService';

export default {
  namespace: 'producerConfig', // model名称
  state: {
    producerList: [], // 数据生产消费配置列表数据
    pagination: {}, // 分页参数
    initStatus: [], // 初始化状态
    producerDetail: {}, // 数据消息生产配置头信息
    consumerList: [], // 消费配置列表
    consumerListPagination: {}, // 消费配置列表分页
    consumerTenantList: [], // 消费租户列表
    consumerTenantPagination: {}, // 消费租户列表分页
    consumerDbConfig: {}, // 数据消息消费DB配置
  },
  effects: {
    // 查询数据生产消费配置列表
    * fetchProducerList({ payload }, { call, put }) {
      let result = yield call(queryProducer, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            producerList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },

    // 查询初始化状态
    * fetchInitStatus(_, { call, put }) {
      const initStatus = getResponse(yield call(queryIdpValue, 'HDTT.EVENT_PROCESS_STATUS'));
      yield put({
        type: 'updateState',
        payload: {
          initStatus,
        },
      });
    },

    // 删除生产消费配置
    * deleteProducer({ payload }, { call }) {
      const result = yield call(deleteProducer, payload);
      return getResponse(result);
    },

    // 保存新建的生产消费配置头部信息
    * saveProducer({ payload }, { call }) {
      const response = yield call(saveProducer, payload);
      return getResponse(response);
    },

    // 更新生产消费配置头部信息
    * updateProducer({ payload }, { call }) {
      const response = yield call(updateProducer, payload);
      return getResponse(response);
    },

    // 保存新建的消费配置
    * createConsumerConfig({ payload }, { call }) {
      const response = yield call(saveConsumer, payload);
      return getResponse(response);
    },

    // 查询数据消息生产配置头信息
    * fetchProducerDetail({ payload }, { call, put }) {
      let result = yield call(queryProducerDetail, { ...payload });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            producerDetail: result,
          },
        });
      }
      return result;
    },

    // 查询数据消息生产配置列表
    * fetchConsumerList({ payload }, { call, put }) {
      let result = yield call(queryConsumerList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            consumerList: result.content,
            consumerListPagination: createPagination(result),
          },
        });
      }
    },

    // 删除消费配置
    * deleteConsumer({ payload }, { call }) {
      const result = yield call(deleteConsumer, payload);
      return getResponse(result);
    },

    // 查询租户消息生产配置列表
    * fetchConsumerTenantList({ payload }, { call, put }) {
      let result = yield call(queryConsumerTenantList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            consumerTenantList: result.content,
            consumerTenantPagination: createPagination(result),
          },
        });
      }
    },

    // 更新消费配置
    * updateConsumer({ payload }, { call }) {
      const response = yield call(updateConsumer, payload);
      return getResponse(response);
    },

    // 删除消费租户配置
    * deleteTenantConsumer({ payload }, { call }) {
      const result = yield call(deleteTenantConsumer, payload);
      return getResponse(result);
    },

    // 初始化消费者
    * initConsumer({ payload }, { call }) {
      const result = yield call(initConsumer, payload);
      return getResponse(result);
    },

    // 更新DDL语句
    * updateDdl({ payload }, { call, put }) {
      let result = yield call(updateDdl, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            producerDetail: result,
          },
        });
      }
    },

    // 详情查询数据消息消费DB配置
    * fetchConsumerDbConfig({ payload }, { call, put }) {
      let result = yield call(queryConsumerDbConfig, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            consumerDbConfig: result,
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
