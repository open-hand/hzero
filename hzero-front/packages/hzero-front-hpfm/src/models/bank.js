/**
 * @date 2018-06-25
 * @author NJQ
 */
import { stringify } from 'qs';
import { getResponse, isTenantRoleLevel } from 'utils/utils';
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { queryIdpValue } from 'hzero-front/lib/services/api';

export const SERVICE_URL = `${HZERO_PLATFORM}/v1/${isTenantRoleLevel()}banks`;

export function serviceUrl(params = {}) {
  return `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${params.tenantId}/` : ''}banks`;
}

export const service = {
  async init() {
    return queryIdpValue('HPFM.BANK_TYPE');
  },

  async queryBanks(params = {}) {
    const {
      page = { current: 1, pageSize: 10 },
      sort = { name: 'bankCode', order: 'asc' },
      body,
    } = params;
    return request(
      `${serviceUrl(params)}?page=${page.current - 1}&size=${page.pageSize}&sort=${sort.name},${
        sort.order
      }&${stringify(body)}`
    );
  },

  async updateBank(params = {}) {
    return request(serviceUrl(params), {
      method: params.bankId ? 'PUT' : 'POST',
      body: params,
    });
  },

  async remove(params) {
    return request(serviceUrl(params), {
      method: 'DELETE',
      body: params,
    });
  },
};

export default {
  namespace: 'bank',

  state: {
    modalVisible: false,
    bankTypeList: [],
    list: {},
  },

  effects: {
    *init({ payload }, { call, put }) {
      const response = yield call(service.init, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            bankTypeList: data,
          },
        });
      }
    },

    *fetch({ payload }, { call, put }) {
      const response = yield call(service.queryBanks, payload);
      const list = getResponse(response);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            list,
          },
        });
      }
    },
    *action({ method, payload }, { call }) {
      const response = yield call(service[method], payload);
      return getResponse(response);
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
