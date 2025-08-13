/**
 * uomType - 计量单位类型定义
 * @date: 2018/10/13 09:06:36
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { createPagination, getResponse } from 'utils/utils';
import { fetchUomList, addUom } from '../services/uomTypeService';

export default {
  namespace: 'uomType',

  state: {
    uomList: [],
    pagination: {},
    dataSourceMap: {}, // 选中的所有项，参考李宁
    selectedRowKeys: [],
  },

  effects: {
    // 查询Uom
    *fetchUomList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchUomList, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            uomList: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    // 新增Uom
    *addUomTypes({ payload }, { call }) {
      const data = yield call(addUom, { ...payload });
      return getResponse(data);
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
