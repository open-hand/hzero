import { routerRedux } from 'dva/router';
import { query } from '../services/error';

export default {
  namespace: 'error',

  state: {
    error: '',
    isloading: false,
    globalException: {
      history: JSON.parse(localStorage.getItem('hzero-global-exception-history') || '[]'),
    }, // 运行时捕获的异常
    normal501: {}, // 正常的501错误
  },

  effects: {
    *query({ payload }, { call, put }) {
      yield call(query, payload.code);
      // redirect on client when network broken
      yield put(routerRedux.push(`/exception/${payload.code}`));
      yield put({
        type: 'trigger',
        payload: payload.code,
      });
    },
  },

  reducers: {
    trigger(state, action) {
      return {
        error: action.payload,
      };
    },
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
