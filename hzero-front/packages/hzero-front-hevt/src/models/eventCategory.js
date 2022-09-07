/**
 * eventCategory - 事件类型定义 - model
 * @date: 2019-3-12
 * @author: Wu <qizheng.wu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { fetchEventList, createEvent } from '@/services/eventCategoryService';

export default {
  namespace: 'eventCategory',

  state: {
    modalVisible: false,
    eventList: [],
    pagination: {}, // 分页对象
  },

  effects: {
    // 获取事件类型信息
    *fetchEventList({ payload }, { call, put }) {
      const res = yield call(fetchEventList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            eventList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 新增事件类型
    *createEvent({ payload }, { call }) {
      const param = payload;
      param.enabledFlag = payload.enabledFlag ? 1 : 0;
      const res = yield call(createEvent, param);
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
