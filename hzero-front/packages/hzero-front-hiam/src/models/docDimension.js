/**
 * docDimension-单据维度
 * @date: 2019-09-19
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { queryMapIdpValue } from 'services/api';
import { getResponse, createPagination } from 'utils/utils';
import { query, queryDetail, create, update } from '../services/docDimensionService';

export default {
  namespace: 'docDimension', // docDimension 单据维度

  /**
   * state
   */
  state: {
    dimensionList: [], // 单据维度列表
    dimensionDetail: {},
    pagination: {}, // 分页
    valueSourceTypeList: [], // 数据源列表
    dimensionTypeList: [],
  },

  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { dimensionTypeList, valueSourceTypeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          valueSourceTypeList,
          dimensionTypeList,
        },
      });
    },
    // 查询数据驱动列表
    * query({ payload }, { call, put }) {
      const result = getResponse(yield call(query, payload));
      yield put({
        type: 'updateState',
        payload: {
          pagination: createPagination(result),
        },
      });
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            pagination: createPagination(result),
            dimensionList: result.content,
          },
        });
      }
    },
    // 通过Id查询数据驱动
    * queryDetail({ payload }, { call, put }) {
      const res = yield call(queryDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            dimensionDetail: result,
          },
        });
      }
    },
    // 创建数据驱动
    * create({ payload }, { call }) {
      const res = yield call(create, payload);
      return getResponse(res);
    },
    // 更新数据驱动
    * update({ payload }, { call }) {
      const res = yield call(update, payload);
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
