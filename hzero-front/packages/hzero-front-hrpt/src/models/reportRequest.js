/**
 * model 报表平台/报表请求
 * @date: 2018-11-28
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { fetchRequestList, fetchRequestDetail } from '../services/reportRequestService';

export default {
  namespace: 'reportRequest',
  state: {
    requestStatusList: [], // 运行状态
    list: [], // 数据列表
    requestDetail: {}, // 详情数据
    pagination: {}, // 分页器
  },
  effects: {
    // 获取报表类型
    * init(_, { call, put }) {
      const requestStatusList = getResponse(yield call(queryIdpValue, 'HRPT.REQUEST_STATUS'));
      yield put({
        type: 'updateState',
        payload: {
          requestStatusList,
        },
      });
    },
    // 获取报表请求列表数据
    * fetchRequestList({ payload }, { call, put }) {
      let result = yield call(fetchRequestList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    // 查询请求详情
    * fetchRequestDetail({ payload }, { call, put }) {
      const res = yield call(fetchRequestDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            requestDetail: result,
          },
        });
      }
      return result;
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
