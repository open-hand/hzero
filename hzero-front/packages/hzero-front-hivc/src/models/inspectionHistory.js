/**
 * InspectionHistory 发票查验历史
 * @date: 2019-8-26
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryList } from '../services/inspectionHistoryService';

export default {
  namespace: 'inspectionHistory',
  state: {
    pagination: {}, // 分页参数
    inspectionHistoryList: [], // 文件编辑日志列表
  },
  effects: {
    // 获取列表
    * queryList({ payload }, { put, call }) {
      const res = yield call(queryList, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            inspectionHistoryList: data.content,
            pagination: createPagination(data),
          },
        });
      }
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
