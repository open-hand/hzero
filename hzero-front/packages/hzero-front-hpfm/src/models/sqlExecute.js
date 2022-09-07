/**
 * model - SQL执行界面
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse } from 'utils/utils';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchDataTable,
  fetchTableField,
  fetchExecuteResult,
  fetchSingleResult,
} from '../services/sqlExecuteService';

export default {
  namespace: 'sqlExecute',
  state: {
    // dataTableList: [], // 可访问的数据表
    // tableFieldList: [], // 表字段
    resultData: {}, // 执行结果
    query: {},
    pagination: {}, // 分页信息对象
    clickedValue: '', // 鼠标点击的树节点名称
    serverName: '', // 搜索框选中的服务名称
    executeSql: '', // 执行的SQL
    serverList: [], // 服务
    exportSql: '', // 导出结果的sql参数
  },
  effects: {
    // 获取服务
    *fetchServer({ payload }, { put, call }) {
      const serverList = getResponse(
        yield call(queryUnifyIdpValue, 'HPFM.DBIDE.SERVICE_NAME', {
          tenantId: payload.tenantId,
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          serverList,
        },
      });
    },

    // 查询可访问的数据表
    *fetchDataTable({ payload }, { call }) {
      const { page = 0, size = 15, ...query } = payload;
      const response = yield call(fetchDataTable, { page, size, ...query });
      const result = getResponse(response);
      // if (result) {
      //   yield put({
      //     type: 'updateState',
      //     payload: {
      //       query,
      //       dataTableList: result.content,
      //       pagination: createPagination(result),
      //     },
      //   });
      // }
      return result;
    },
    // 查询表对应的字段
    *fetchTableField({ payload }, { call }) {
      const response = yield call(fetchTableField, payload);
      const list = getResponse(response);
      return list;
    },
    // 执行全部SQL
    *fetchExecuteAllResult({ payload }, { call, put }) {
      const response = yield call(fetchExecuteResult, payload);
      const listData = getResponse(response);
      if (listData) {
        yield put({
          type: 'updateState',
          payload: { resultData: listData },
        });
      }
      return listData;
    },
    // 执行选中的SQL
    *fetchExecuteResult({ payload }, { call, put }) {
      const response = yield call(fetchExecuteResult, payload);
      const listData = getResponse(response);
      if (listData) {
        yield put({
          type: 'updateState',
          payload: { resultData: listData },
        });
      }
      return listData;
    },
    // 获取单条SQL翻页结果
    *fetchSingleResult({ payload }, { call }) {
      const response = yield call(fetchSingleResult, payload);
      const listData = getResponse(response);
      return listData;
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
