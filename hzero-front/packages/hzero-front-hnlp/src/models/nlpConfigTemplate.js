/**
 * nlpConfigTemplate
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */

import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';

import { createPagination, getResponse } from 'utils/utils';
import {
  configTemplateCreate,
  configTemplateQuery,
  configTemplateQueryDetail,
  configTemplateRemove,
  configTemplateUpdate,
} from '../services/nlpConfigTemplateService';

export default {
  namespace: 'nlpConfigTemplate',
  state: {
    enums: {}, // 值集
    dataSource: [], // 基础数据表格数据源
    pagination: {}, // 基础数据表格分页
  },
  effects: {
    * init({ payload }, { call, put }) {
      const res = yield call(queryUnifyIdpValue, 'HNLP.BASIC_DATA_TYPE', {
        tenantId: payload,
      });
      const enumsBasicDataType = getResponse(res);
      if (enumsBasicDataType) {
        yield put({
          type: 'updateState',
          payload: {
            enums: {
              basicDataType: enumsBasicDataType,
            },
          },
        });
      }
    },
    * create({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(configTemplateCreate, record);
      return getResponse(res);
    },
    * remove({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(configTemplateRemove, record);
      return getResponse(res);
    },
    * update({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(configTemplateUpdate, record);
      return getResponse(res);
    },
    * query({ payload }, { call, put }) {
      const { query } = payload;
      const res = yield call(configTemplateQuery, query);
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
    * queryDetail({ payload }, { call, put }) {
      const { record, id } = payload;
      const res = yield call(configTemplateQueryDetail, id, record);
      const retRes = getResponse(res);
      if (retRes) {
        yield put({
          type: 'updateState',
          payload: {
            detail: retRes,
          },
        });
      }
      return retRes;
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
