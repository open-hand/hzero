/**
 * model - 服务计费配置
 * @date: 2019/8/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';
import {
  queryList,
  publishGroup,
  cancelGroup,
  queryGroupDetail,
  createGroup,
  updateGroup,
  queryChargeRule,
  createChargeRule,
  updateChargeRule,
  deleteChargeRule,
  queryRuleDetail,
  queryChargeScope,
  createChargeScope,
  deleteChargeScope,
} from '../services/serviceChargeService';

export default {
  namespace: 'serviceCharge', // model名称
  state: {
    enumMap: {}, // 值集
    list: {
      dataSource: [], // 计费组列表
      pagination: {}, // 计费组分页
    },
    groupDetail: {}, // 计费组详情
    preList: {
      dataSource: [], // 预付费列表
      pagination: {}, // 预付费分页
    },
    postList: {
      dataSource: [], // 后付费列表
      pagination: {}, // 后付费分页
    },
    ruleDetail: {}, // 规则详情
    chargeScopeList: {
      dataSource: [], // 服务范围列表
      pagination: {}, // 分页
    },
  },
  effects: {
    // 查询值集
    * queryIdpValue(params, { call, put }) {
      const enumMap = getResponse(
        yield call(queryMapIdpValue, {
          cycleTypes: 'HCHG.INTERVAL_CYCLE',
          chargeMethodTypes: 'HCHG.CHARGE_METHOD', // 计费方式
          chargeTypes: 'HCHG.CHARGE_TYPE', // 计费类型
          measureBasisTypes: 'HCHG.MEASURE_BASIS', // 计量单位
          chargeBasisTypes: 'HCHG.CHARGE_BASIS', // 计费单位
          intervalCycleTypes: 'HCHG.INTERVAL_CYCLE', // 阶梯周期
          intervalMeasureTypes: 'HCHG.INTERVAL_MEASURE', // 阶梯单位
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          enumMap,
        },
      });
    },

    // 查询服务计费组列表
    * fetchList({ payload }, { call, put }) {
      let result = yield call(queryList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 发布服务计费组
    * publishGroup({ payload }, { call }) {
      const response = yield call(publishGroup, payload);
      return getResponse(response);
    },

    // 取消发布服务计费组
    * cancelGroup({ payload }, { call }) {
      const response = yield call(cancelGroup, payload);
      return getResponse(response);
    },

    // 查询计费组详情
    * queryGroupDetail({ payload }, { call, put }) {
      const result = getResponse(yield call(queryGroupDetail, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            groupDetail: result,
          },
        });
      }
    },

    // 创建服务计费组
    * createGroup({ payload }, { call }) {
      const response = yield call(createGroup, payload);
      return getResponse(response);
    },

    // 更新服务计费组
    * updateGroup({ payload }, { call }) {
      const response = yield call(updateGroup, payload);
      return getResponse(response);
    },

    // 删除计费规则
    * deleteChargeRule({ payload }, { call }) {
      const response = yield call(deleteChargeRule, payload);
      return getResponse(response);
    },

    // 查询预付费规则列表
    * queryPreRuleList({ payload }, { call, put }) {
      let result = yield call(queryChargeRule, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            preList: {
              dataSource: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 查询后付费规则列表
    * queryPostRuleList({ payload }, { call, put }) {
      let result = yield call(queryChargeRule, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            postList: {
              dataSource: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 查询规则详情
    * queryRuleDetail({ chargeRuleId }, { call, put }) {
      let result = yield call(queryRuleDetail, chargeRuleId);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            ruleDetail: result,
          },
        });
      }
    },

    // 创建规则
    * createChargeRule({ payload }, { call }) {
      const response = yield call(createChargeRule, payload);
      return getResponse(response);
    },

    // 更新规则
    * updateChargeRule({ payload }, { call }) {
      const response = yield call(updateChargeRule, payload);
      return getResponse(response);
    },

    // 查询服务计费范围列表
    * queryChargeScope({ payload }, { call, put }) {
      let result = yield call(queryChargeScope, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            chargeScopeList: {
              dataSource: result.content || [],
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 创建服务计费范围
    * createChargeScope({ payload }, { call }) {
      const response = yield call(createChargeScope, payload);
      return getResponse(response);
    },

    // 删除服务计费范围
    * deleteChargeScope({ payload }, { call }) {
      const response = yield call(deleteChargeScope, payload);
      return getResponse(response);
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
