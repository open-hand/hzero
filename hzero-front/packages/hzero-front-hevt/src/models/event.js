/**
 * Event - 事件定义 - model
 * @date: 2019-3-12
 * @author: Wu <qizheng.wu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { fetchEvent, createEvent } from '@/services/eventService';

export default {
  namespace: 'event',

  state: {
    modalVisible: false,
    eventData: [],
    pagination: {}, // 分页对象
    queryData: {},
    categoryName: '',
  },

  effects: {
    // 获取事件类型信息
    *fetchEventData({ payload }, { put, call }) {
      const res = yield call(fetchEvent, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            eventData: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    //  新增事件类型
    *createEvent({ payload }, { call }) {
      const param = payload;
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
