/**
 * @date: 2020-6-12
 * @author: HBT <baitao.huang@hand-china.com>
 */

import { getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';
import { CAST_COMPARISON_TYPE } from '@/constants/CodeConstants';

export default {
  namespace: 'dataMapping',

  state: {
    comparisonTypeList: [], // 条件列表
  },

  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          comparisonTypeList: CAST_COMPARISON_TYPE,
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
