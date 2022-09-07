/**
 * EventMessage - 事件查询 - model
 * @date: 2019-3-22
 * @author: Wu <qizheng.wu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, parseParameters, createPagination } from 'utils/utils';
import { queryMessageList, resendMessage } from '@/services/eventMessageService';

export default {
  namespace: 'eventMessage',

  state: {
    messageData: [],
    pagination: {},
  },

  effects: {
    *queryMessageList({ payload }, { call, put }) {
      const res = yield call(queryMessageList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            messageData: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },
    *resendMessage({ payload }, { call }) {
      const res = yield call(resendMessage, payload);
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
