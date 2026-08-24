/**
 * model - 库位
 * @date: 2018-8-9
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';

import { queryLibPosition, saveLibraryPosition } from '../services/libraryPositionService';

export default {
  namespace: 'libraryPosition',
  state: {
    query: {}, // 查询参数
    libraryList: [], // 库位信息列表
    pagination: {}, // 分页信息
  },
  effects: {
    // 查询数据
    *fetchLibraryPosition({ payload }, { call, put }) {
      const { page, ...query } = payload;
      const response = yield call(queryLibPosition, payload);
      const libraryData = getResponse(response);
      if (libraryData) {
        yield put({
          type: 'updateState',
          payload: {
            query,
            libraryList: libraryData.content,
            pagination: createPagination(libraryData),
          },
        });
      }
    },
    // 批量新增和编辑数据
    *saveLibraryPosition({ payload }, { call }) {
      const response = yield call(saveLibraryPosition, payload);
      return getResponse(response);
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
