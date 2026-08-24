/**
 * dataSourceDriver-数据源驱动
 * @date: 2019-08-22
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { queryMapIdpValue } from 'services/api';
import { getResponse, createPagination } from 'utils/utils';
import {
  fetchDriversList,
  fetchDriverById,
  createDriver,
  updateDriver,
  removeDriver,
} from '../services/datasourceDriverService';

export default {
  namespace: 'dataSourceDriver', // dataSourceDriver 数据源驱动

  /**
   * state
   */
  state: {
    driverList: [], // 数据源驱动列表
    pagination: {}, // 分页
    dataSourceTypeList: [], // 数据源列表
  },

  effects: {
    // 获取初始化数据
    *init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { typeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          dataSourceTypeList: typeList,
        },
      });
    },
    // 查询数据驱动列表
    *fetchDriversList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchDriversList, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            pagination: createPagination(result),
            driverList: result.content,
          },
        });
      }
    },
    // 通过DriverId查询数据驱动
    *fetchDriverById({ payload }, { call }) {
      const res = yield call(fetchDriverById, payload);
      return getResponse(res);
    },
    // 创建数据驱动
    *createDriver({ payload }, { call }) {
      const res = yield call(createDriver, payload);
      return getResponse(res);
    },
    // 更新数据驱动
    *updateDriver({ payload }, { call }) {
      const res = yield call(updateDriver, payload);
      return getResponse(res);
    },

    // 删除数据驱动
    *removeDriver({ payload }, { call }) {
      const res = yield call(removeDriver, payload);
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
