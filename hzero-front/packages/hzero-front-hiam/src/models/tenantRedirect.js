import { getResponse } from 'utils/utils';

import { tenantRedirect } from '../services/tenantRedirectService';

export default {
  namespace: 'tenantRedirect',
  state: {
    tenantData: {}, // 租户数据
    tenantDataDetail: {}, // 租户明细数据
    pagination: {}, // 分页参数
  },
  effects: {
    * redirect({ payload }, { call }) {
      const response = yield call(tenantRedirect, payload);
      const tenantData = getResponse(response);
      return tenantData;
    },
  },

  reducers: {
    updateState(state, action) {
      return {
        ...state,
        ...action.payload,
      };
    },
  },
};
