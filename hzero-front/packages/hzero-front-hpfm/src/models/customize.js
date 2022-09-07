import { getResponse, parseParameters, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchPointList,
  deletePoint,
  refreshPoint,
  fetchRuleList,
  fetchRuleDetail,
  deleteRule,
  createRule,
  updateRule,
  fetchRangeList,
  createRange,
  updateRange,
  deleteRange,
  fetchRangeDetail,
  fetchPointListToRange,
  fetchRuleListToRange,
  deletePointToRange,
  deleteRuleToRange,
  applyRules,
} from '../services/customizeService';

export default {
  namespace: 'customize',

  state: {
    pointList: [],
    pointPagination: {},
    ruleList: [],
    rulePagination: {},
    rangeList: [],
    rangePagination: {},
    rangeDetail: {},
    pointToRangeList: [], // 范围对应的切入点数据列表
    ruleToRangeList: [], // 范围对应的规则数据列表
  },

  effects: {
    // 个性化切入
    *fetchPointList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchPointList, parseParameters(payload)));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            pointList: result.content,
            pointPagination: createPagination(result),
          },
        });
      }
      return result;
    },

    *deletePoint({ payload }, { call }) {
      const res = getResponse(yield call(deletePoint, payload));
      return res;
    },

    *refreshPoint({ payload }, { call }) {
      const res = getResponse(yield call(refreshPoint, payload));
      return res;
    },

    // 个性化规则
    *fetchRuleList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchRuleList, parseParameters(payload)));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            ruleList: result.content,
            rulePagination: createPagination(result),
          },
        });
      }
      return result;
    },

    *fetchRuleDetail({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchRuleDetail, payload));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            ruleDetail: result,
          },
        });
      }
      return result;
    },

    *fetchValueListToRule(_, { call, put }) {
      const result = getResponse(
        yield call(queryMapIdpValue, {
          typeCodeList: 'HPFM.CUSTOMIZE_RULE_TYPE',
          rulePositionList: 'HPFM.CUSTOMIZE_RULE_POSITION',
        })
      );
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            typeCodeList: result.typeCodeList,
            rulePositionList: result.rulePositionList,
          },
        });
      }
      return result;
    },

    *createRule({ payload }, { call }) {
      const res = getResponse(yield call(createRule, payload));
      return res;
    },

    *updateRule({ payload }, { call }) {
      const res = getResponse(yield call(updateRule, payload));
      return res;
    },

    *deleteRule({ payload }, { call }) {
      const res = getResponse(yield call(deleteRule, payload));
      return res;
    },

    // 个性化范围
    *fetchRangeList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchRangeList, parseParameters(payload)));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            rangeList: result.content,
            rangePagination: createPagination(result),
          },
        });
      }
      return result;
    },

    *fetchRangeDetail({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchRangeDetail, payload));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            rangeDetail: result,
          },
        });
      }
      return result;
    },

    *fetchPointListToRange({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchPointListToRange, payload));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            pointToRangeList: result,
          },
        });
      }
      return result;
    },

    *fetchRuleListToRange({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchRuleListToRange, payload));
      if (result) {
        yield put({
          type: 'customize/updateState',
          payload: {
            ruleToRangeList: result,
          },
        });
      }
      return result;
    },

    *deletePointToRange({ payload }, { call }) {
      const res = getResponse(yield call(deletePointToRange, payload));
      return res;
    },

    *deleteRuleToRange({ payload }, { call }) {
      const res = getResponse(yield call(deleteRuleToRange, payload));
      return res;
    },

    *createRange({ payload }, { call }) {
      const res = getResponse(yield call(createRange, payload));
      return res;
    },

    *updateRange({ payload }, { call }) {
      const res = getResponse(yield call(updateRange, payload));
      return res;
    },

    *deleteRange({ payload }, { call }) {
      const res = getResponse(yield call(deleteRange, payload));
      return res;
    },

    *applyRules({ payload }, { call }) {
      const res = getResponse(yield call(applyRules, payload));
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
