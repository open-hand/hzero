/**
 * @date: 2020-6-12
 * @author: HBT <baitao.huang@hand-china.com>
 */

import { getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';
import {
  ORCH_ASSERTION_CONDITION,
  ORCH_ASSERTION_SUBJECT,
  ORCH_FAILURE_STRATEGY,
} from '@/constants/CodeConstants';

export default {
  namespace: 'orchestration',

  state: {
    operatorList: [], // 操作符集合
    assertionSubjects: [], // 断言主题
    failureStrategyList: [], // 失败策略
  },

  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          operatorList: ORCH_ASSERTION_CONDITION,
          assertionSubjects: ORCH_ASSERTION_SUBJECT,
          failureStrategyList: ORCH_FAILURE_STRATEGY,
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          ...res,
        },
      });
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
