export default {
  namespace: 'market',

  state: {
    isAgree: null, // 是否同意协议
  },

  effects: {},

  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
