/**
 * model - 文件编辑日志
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { queryLogList } from '../services/editLogService';

export default {
  namespace: 'editLog',
  state: {
    pagination: {}, // 分页参数
    editLogList: [], // 文件编辑日志列表
    typeList: [], // 编辑类别
  },
  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { typeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          typeList,
        },
      });
    },

    // 获取列表
    * queryLogList({ payload }, { put, call }) {
      const res = yield call(queryLogList, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            editLogList: data.content,
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
