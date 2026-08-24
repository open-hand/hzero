/**
 * codeRule - 编码规则 - model
 * @date: 2018-6-29
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  queryCodeList,
  deleteCodeRule,
  addCodeValue,
  queryDist,
  deleteDist,
  queryDetail,
  addDistValue,
  addCodeDetail,
  deleteCodeDetail,
} from '../services/codeRuleOrgService';

export default {
  namespace: 'codeRuleOrg',

  state: {
    list: {
      data: {
        content: [],
        pagination: {},
      },
    },
    dist: {
      head: {},
      line: {
        list: [],
        pagination: {},
      },
    },
    detail: {
      data: {
        content: [],
        pagination: {},
      },
    },
    tenantStatus: {
      display: 'none',
      required: false,
    },
    keyValue: {
      ruleId: null,
      ruleDistId: null,
    },
    code: {
      LEVEL: [],
      UNITTYPE: [],
      FieldType: [],
      ResetFrequency: [],
      DateMask: [],
      Variable: [],
    },
  },

  effects: {
    *init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { FieldType, DateMask, ResetFrequency, UNITTYPE, Variable } = res;
      yield put({
        type: 'queryInit',
        payload: {
          FieldType,
          DateMask,
          ResetFrequency,
          UNITTYPE,
          Variable,
        },
      });
      return res;
    },

    *fetchCode({ payload }, { call, put }) {
      const response = yield call(queryCodeList, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'queryCode',
          payload: data,
        });
      }
    },

    *addCodeRule({ payload }, { call }) {
      const response = yield call(addCodeValue, payload);
      return getResponse(response);
    },

    *removeCode({ payload }, { call }) {
      const response = yield call(deleteCodeRule, payload);
      return getResponse(response);
    },

    *fetchDist({ payload }, { call, put }) {
      const response = yield call(queryDist, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'queryDist',
          payload: data,
        });
      }
    },

    *addDist({ payload }, { call }) {
      const response = yield call(addDistValue, payload);
      return getResponse(response);
    },

    *removeDist({ payload }, { call }) {
      const response = yield call(deleteDist, payload);
      return getResponse(response);
    },

    *fetchDetail({ payload }, { call, put }) {
      const response = yield call(queryDetail, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'queryDetail',
          payload: data,
        });
      }
    },

    *saveCodeDetail({ payload }, { call }) {
      const response = yield call(addCodeDetail, payload);
      return getResponse(response);
    },

    *removeCodeDetail({ payload }, { call }) {
      const response = yield call(deleteCodeDetail, payload);
      return getResponse(response);
    },
  },

  reducers: {
    settingOrgId(state, action) {
      return {
        ...state,
        organizationId: action.payload.organizationId,
      };
    },
    queryCode(state, action) {
      return {
        ...state,
        list: {
          data: {
            content: action.payload.content,
            pagination: createPagination(action.payload),
            ...action.payload,
          },
          ...state.data,
        },
      };
    },
    queryDist(state, action) {
      return {
        ...state,
        dist: {
          head: action.payload,
          line: {
            ...action.payload.codeRuleDistDTOList,
            list: action.payload.codeRuleDistDTOList.content,
            pagination: createPagination(action.payload.codeRuleDistDTOList),
          },
        },
        keyValue: {
          ruleId: action.payload.ruleId,
        },
      };
    },
    queryDetail(state, action) {
      return {
        ...state,
        detail: {
          ...state.detail,
          data: {
            content: action.payload.codeRuleDetailDTOList,
            pagination: createPagination(action.payload),
          },
        },
        keyValue: {
          ruleDistId: action.payload.ruleDistId,
        },
      };
    },
    addNewDatail(state, action) {
      return {
        ...state,
        detail: {
          ...state.detail,
          data: {
            ...state.detail.data,
            content: [...state.detail.data.content, action.payload],
          },
        },
      };
    },
    changeFileType(state, action) {
      return {
        ...state,
        code: {
          ...state.code,
          FieldType: state.code.FieldType.filter(type => type.value !== action.payload),
        },
      };
    },
    resetFileType(state, { payload }) {
      return {
        ...state,
        code: {
          ...state.code,
          FieldType: payload,
        },
      };
    },
    queryInit(state, action) {
      return {
        ...state,
        code: {
          ...state.code,
          ...action.payload,
        },
      };
    },
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
