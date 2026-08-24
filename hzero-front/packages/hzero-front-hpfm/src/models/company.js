/**
 * model 公司
 * @date: 2018-7-16
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
// import { fetchCountryList } from '../services/countryService';
import {
  fetchCompany,
  enableCompany,
  disableCompany,
  queryCompany,
  createCompany,
  queryProvinceCity,
  fetchCountryList,
} from '../services/companyService';

export default {
  namespace: 'company',

  state: {
    countryList: [], // 国家列表
    companyType: [], // 企业类型值集
    taxpayerType: [], // 纳税人标识
    companyList: [],
    companyDetail: {},
  },

  effects: {
    *init({ payload }, { call, put }) {
      const vl = yield call(queryMapIdpValue, {
        companyType: 'HPFM.COMPANY_TYPE',
        taxpayerType: 'HPFM.TAXPAYER_TYPE',
      });
      const res = yield call(fetchCountryList, payload);
      yield put({
        type: 'updateState',
        payload: {
          companyType: vl.companyType || [],
          taxpayerType: vl.taxpayerType || [],
          countryList: res,
        },
      });
    },

    *queryProvinceCity({ payload }, { call, put }) {
      const cityList = yield call(queryProvinceCity, payload);
      const safeCityList = getResponse(cityList);
      if (safeCityList) {
        yield put({
          type: 'updateState',
          payload: {
            cityList,
          },
        });
      }
    },

    // 获取公司信息
    *fetchCompany({ payload }, { call, put }) {
      const res = yield call(fetchCompany, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            companyList: list,
          },
        });
      }
    },
    // 设置公司启用
    *enableCompany({ payload }, { call }) {
      const res = yield call(enableCompany, payload);
      return getResponse(res);
    },

    // 设置公司禁用
    *disableCompany({ payload }, { call }) {
      const res = yield call(disableCompany, payload);
      return getResponse(res);
    },
    // 根据id查询公司
    *queryCompany({ payload }, { call, put }) {
      const res = yield call(queryCompany, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            companyDetail: list,
          },
        });
      }
      return list;
    },
    // 新建公司
    *createCompany({ payload }, { call }) {
      const res = yield call(createCompany, payload);
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
