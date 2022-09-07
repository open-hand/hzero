/**
 * nlpTextExtraction
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-04
 * @copyright 2019-06-04 © HAND
 */

import { getResponse } from 'utils/utils';
import { nlpTextExtractionCreate } from '../services/nlpTextExtractionService';

export default {
  namespace: 'nlpTextExtraction',
  state: {
    result: {}, // 识别结果
  },
  effects: {
    * create({ payload }, { call, put }) {
      const { record } = payload;
      const res = yield call(nlpTextExtractionCreate, record);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            result: res,
          },
        });
      }
      return getResponse(res);
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
