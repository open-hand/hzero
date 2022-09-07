/**
 * model - 操作审计查询
 * @date: 2019/7/18
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { queryAuditList, queryAuditDetail } from '../services/auditQueryService';

export default {
  namespace: 'auditQuery', // model名称
  state: {
    pagination: {}, // 分页
    auditList: [], // 操作审计列表
    auditDetail: {}, // 操作审计记录行
    statusList: [], // 状态列表
    methodList: [], // 方法列表
  },
  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { statusList, methodList } = res;
      yield put({
        type: 'updateState',
        payload: {
          statusList,
          methodList,
        },
      });
    },

    // 查询操作审计列表
    * fetchAuditList({ payload }, { call, put }) {
      let result = yield call(queryAuditList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            auditList: result.content || [],
            pagination: createPagination(result),
          },
        });
      }
    },

    // 查询操作审计记录行
    * fetchAuditDetail({ payload }, { call, put }) {
      let result = yield call(queryAuditDetail, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            auditDetail: result,
          },
        });
      }
      return result;
    },
  },
  reducers: {
    // 合并state状态数据,生成新的state
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
