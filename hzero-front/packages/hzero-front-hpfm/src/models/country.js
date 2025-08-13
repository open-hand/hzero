/**
 * model 地区定义
 * @date: 2018-6-19
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { fetchCountryList, createCountry, updateCountry } from '../services/countryService';

export default {
  namespace: 'country',

  state: {
    modalVisible: false,
    countryList: [],
    pagination: {}, // 分页对象
  },

  effects: {
    // 获取国家信息
    *fetchCountryList({ payload }, { call, put }) {
      const res = yield call(fetchCountryList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            countryList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 新增国家
    *createCountry({ payload }, { call }) {
      const param = payload;
      param.enabledFlag = payload.enabledFlag ? 1 : 0;
      const res = yield call(createCountry, param);
      return getResponse(res);
    },

    // 更新国家
    *updateCountry({ payload }, { call }) {
      const param = payload;
      param.enabledFlag = payload.enabledFlag ? 1 : 0;
      const res = yield call(updateCountry, param);
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
