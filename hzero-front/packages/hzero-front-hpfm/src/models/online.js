import { getResponse } from 'utils/utils';
import { logout } from '../services/onlineService';

export default {
  namespace: 'online',
  state: {},
  effects: {
    // 登出
    *logout({ payload }, { call }) {
      const res = yield call(logout, payload);
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
