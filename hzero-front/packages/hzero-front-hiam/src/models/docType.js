/**
 * docType.js - 单据权限 model
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import uuidv4 from 'uuid/v4';
import { map } from 'lodash';
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  queryDocType,
  queryDocTypeDetail,
  addDocType,
  updateDocType,
  queryDocTypeAuth,
  updateDocTypeAuth,
} from '../services/docTypeService';

export default {
  namespace: 'docType',

  state: {
    docTypeList: {}, // 单据类型定义列表
    docTypeDetail: {}, // 单据类型定义详情
    docTypeAuth: [], // 单据类型权限维度
    docTypeLevelCode: [], // 单据权限层级编码
    // typeList: [],
    pagination: {}, // 单据权限列表分页对象
    personalList: [],
    businessList: [],
    ruleTypeList: [],
    docTypeSqlidList: [],
  },

  effects: {
    *queryLevelCode(_, { call, put }) {
      const res = yield call(queryMapIdpValue, {
        docTypeLevelCode: 'HIAM.DOC_TYPE_LEVEL_CODE',
        typeList: 'HIAM.DOC.AUTH_TYPE',
        ruleTypeList: 'HIAM.DOC.RULE_TYPE',
      });
      const response = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { ...response },
      });
    },
    // 查询单据类型定义列表
    *queryDocType({ payload }, { call, put }) {
      const res = yield call(queryDocType, payload);
      const docTypeList = getResponse(res);
      const pagination = createPagination(docTypeList);

      yield put({
        type: 'updateState',
        payload: {
          docTypeList,
          pagination,
        },
      });
    },
    // 查询单据类型定义详情
    *queryDocTypeDetail({ payload }, { call, put }) {
      const res = yield call(queryDocTypeDetail, payload);
      const docTypeDetail = getResponse(res);
      const { docTypeSqlidList = [] } = docTypeDetail;
      yield put({
        type: 'updateState',
        payload: {
          docTypeDetail,
          docTypeSqlidList: map(docTypeSqlidList, (r) => ({ ...r, tableId: uuidv4() })),
        },
      });
    },
    // 新增或更新单据类型定义
    *saveDocType({ payload }, { call }) {
      let res;
      if (payload.docTypeId) {
        res = yield call(updateDocType, payload);
      } else {
        res = yield call(addDocType, payload);
      }
      return getResponse(res);
    },
    // 查询单据类型权限维度
    *queyDocTypeAuth({ payload }, { call, put }) {
      const docTypeAuth = yield call(queryDocTypeAuth, payload);
      const result = getResponse(docTypeAuth);
      yield put({
        type: 'updateState',
        payload: { docTypeAuth: result },
      });
      return result;
    },
    // 更新单据类型权限维度
    *updateDocTypeAuth({ payload }, { call }) {
      const res = yield call(updateDocTypeAuth, payload);
      return getResponse(res);
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
