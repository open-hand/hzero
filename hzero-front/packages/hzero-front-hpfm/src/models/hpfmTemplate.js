/**
 * 系统管理--模板维护
 * @date 2019-6-26
 * @author: XL <liang.xiong@hand-china.com>
 */
import { createPagination, getResponse } from 'utils/utils';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import { createTemplate, editTemplate, fetchTemplates } from '../services/templatesService';

export default {
  namespace: 'hpfmTemplate',
  state: {
    templateData: {},
    pagination: {},
  },
  effects: {
    *fetchEnum(_, { call, put }) {
      const res = yield call(queryUnifyIdpValue, 'HPFM.DATA_TENANT_LEVEL');
      const dataTenantLevel = getResponse(res);
      if (dataTenantLevel) {
        yield put({
          type: 'updateState',
          payload: {
            dataTenantLevel,
          },
        });
      }
    },
    *fetchTemplates({ payload }, { call, put }) {
      const res = yield call(fetchTemplates, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            templateData: list,
            pagination: createPagination(list),
          },
        });
      }
    },
    // 新建保存
    *createTemplate({ payload }, { call }) {
      const res = yield call(createTemplate, { ...payload });
      return getResponse(res);
    },
    // 编辑保存
    *editTemplate({ payload }, { call }) {
      const res = yield call(editTemplate, { ...payload });
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
