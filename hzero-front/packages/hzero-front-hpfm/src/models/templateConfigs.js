/**
 * 系统管理--模板管理
 * @date: 2019-6-28
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';

import {
  deleteTemplateConfigs,
  fetchTemplateConfigsList,
  createTemplateConfigs,
  updateTemplateConfigs,
  getTemplateConfigsDetail,
} from '../services/templateConfigsService';

export default {
  namespace: 'templateConfigs',
  state: {
    templateConfigsList: [], // 模板管理列表
    templateConfigsTypeList: [], // 配置类型列表
    pagination: {}, // 分页对象
    templateConfigsDetail: {}, // 查询明细列表
  },
  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const templateConfigsTypeList = getResponse(
        yield call(queryIdpValue, 'HPFM.CONFIG_TYPE_CODE')
      );
      yield put({
        type: 'updateState',
        payload: {
          templateConfigsTypeList,
        },
      });
    },

    // 获取模板管理列表
    *fetchTemplateConfigsList({ payload }, { call, put }) {
      const res = yield call(fetchTemplateConfigsList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            templateConfigsList: list.content,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 查询模板管理列表
    *getTemplateConfigsDetail({ payload }, { call, put }) {
      const res = yield call(getTemplateConfigsDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            templateConfigsDetail: list,
          },
        });
      }
    },

    // 新增模板管理
    *createTemplateConfigs({ payload }, { call }) {
      const res = yield call(createTemplateConfigs, payload);
      return getResponse(res);
    },

    // 更新模板管理
    *updateTemplateConfigs({ payload }, { call }) {
      const res = yield call(updateTemplateConfigs, payload);
      return getResponse(res);
    },

    // 删除模板管理
    *deleteTemplateConfigs({ payload }, { call }) {
      const res = yield call(deleteTemplateConfigs, payload);
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
