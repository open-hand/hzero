/**
 * nlpTemplate
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import {
  templateCreate,
  templateQuery,
  templateQueryDetail,
  templateRemove,
  templateUpdate,
} from '../services/nlpTemplateService';

export default {
  namespace: 'nlpTemplate',
  state: {
    enums: {}, // 值集
    dataSource: [], // 基础数据表格数据源
    pagination: {}, // 基础数据表格分页
  },
  effects: {
    * init(_, { call, put }) {
      // 不需要的 init 方法
      const res = yield call(queryUnifyIdpValue, 'HNLP.MODEL.ACCURACY');
      const code = getResponse(res);
      if (code) {
        yield put({
          type: 'updateState',
          payload: {
            enums: {
              modelAccuracy: code,
            },
          },
        });
      }
    },
    * create({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(templateCreate, record);
      return getResponse(res);
    },
    * remove({ payload }, { call }) {
      const { record, id } = payload;
      const res = yield call(templateRemove, id, record);
      return getResponse(res);
    },
    * update({ payload }, { call }) {
      const { record, id } = payload;
      const res = yield call(templateUpdate, id, record);
      return getResponse(res);
    },
    * query({ payload }, { call, put }) {
      const { query } = payload;
      const res = yield call(templateQuery, query);
      const retRes = getResponse(res);
      if (retRes) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource: retRes.content,
            pagination: createPagination(retRes),
          },
        });
      }
      return retRes;
    },
    * queryDetail({ payload }, { call }) {
      const { record, id } = payload;
      const res = yield call(templateQueryDetail, id, record);
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
