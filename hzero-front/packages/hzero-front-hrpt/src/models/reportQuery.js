/**
 * model 报表平台/报表查询
 * @date: 2018-11-28
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue, queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchReportList,
  fetchParams,
  buildReport,
  createRequest,
  fetchRequestList,
  fetchRequestDetail,
} from '../services/reportQueryService';

export default {
  namespace: 'reportQuery',
  state: {
    reportTypeList: [], // 报表类型
    chartsTypeList: [], // 图表类型
    intervalTypeList: [], // 间隔类型
    requestStatusList: [], // 报表请求运行状态
    list: [], // 数据列表
    paramsData: {}, // 参数数据
    // reportDisplayData: '', // 报表展示数据
    pagination: {}, // 分页器
    request: {
      list: [], // 报表请求数据列表
      requestDetail: {}, // 报表请求详情数据
      pagination: {}, // 报表请求分页器
    },
  },
  effects: {
    // 获取报表类型
    * fetchReportType(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          reportTypeList: 'HRPT.REPORT_TYPE',
          intervalTypeList: 'HSDR.REQUEST.INTERVAL_TYPE',
          requestStatusList: 'HRPT.REQUEST_STATUS',
        })
      );
      const { reportTypeList, intervalTypeList, requestStatusList } = res;
      yield put({
        type: 'updateState',
        payload: {
          reportTypeList,
          intervalTypeList,
          requestStatusList,
        },
      });
    },
    // 获取图标类型
    * fetchChartType(_, { call, put }) {
      const chartsTypeList = getResponse(yield call(queryIdpValue, 'HRPT.GRAPH_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          chartsTypeList,
        },
      });
    },
    // 获取报表列表数据
    * fetchReportList({ payload }, { call, put }) {
      let result = yield call(fetchReportList, payload);
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
    // 报表查看-获取参数
    * fetchParams({ payload }, { call, put }) {
      let result = yield call(fetchParams, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateDetailState',
          payload: {
            paramsData: result,
            id: payload.reportUuid,
          },
        });
      }
      return result;
    },
    // 生成报表
    * buildReport({ payload }, { call }) {
      let result = yield call(buildReport, payload);
      result = getResponse(result);
      // if (result) {
      //   yield put({
      //     type: 'updateState',
      //     payload: {
      //       reportDisplayData: result,
      //     },
      //   });
      // }
      return result;
    },
    // 定时报表
    * createRequest({ payload }, { call }) {
      let result = yield call(createRequest, payload);
      result = getResponse(result);
      return result;
    },
    // 获取报表请求数据
    * fetchRequestList({ payload }, { call, put }) {
      let result = yield call(fetchRequestList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateRequestReducer',
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
          type: 'updateRequestReducer',
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
    updateRequestReducer(state, { payload }) {
      return {
        ...state,
        request: {
          ...state.request,
          ...payload,
        },
      };
    },
    updateDetailState(state, { payload }) {
      const { id } = payload;
      return {
        ...state,
        detail: {
          ...state.detail,
          [id]: payload,
        },
      };
    },
  },
};
