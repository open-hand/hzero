/*
 * @date 2018-07-11
 * @author HB <bin.huang02@hand-china.com>
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryStoreRoom, saveStoreRoom } from '../services/storeRoomService';

export default {
  namespace: 'storeRoom',

  state: {
    storeRoomList: [],
    pagination: {},
  },

  effects: {
    * queryStoreRoomList({ payload }, { call, put }) {
      const storeRoomListData = getResponse(yield call(queryStoreRoom, payload));
      if (storeRoomListData) {
        yield put({
          type: 'updateState',
          payload: {
            storeRoomList: storeRoomListData.content,
            pagination: createPagination(storeRoomListData),
          },
        });
      }
    },
    * saveStoreRoom({ payload }, { call }) {
      const storeRoom = yield call(saveStoreRoom, payload.inventoryList);
      return getResponse(storeRoom);
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
