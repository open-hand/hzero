/**
 * model - 单点登录配置
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue, queryIdpValue } from 'hzero-front/lib/services/api';

import {
  queryConfig,
  createConfig,
  deleteConfig,
  editConfig,
  getConfigDetail,
  fetchMessageDetail,
  fetchDistribute,
  saveDistribute,
  deleteDistribute,
} from '../services/ssoConfigService';
import {
  fetchTemplateAssign,
  fetchPortalAssignAssignable,
  templateAssignCreate,
  templateAssignDefault,
  templateAssignDelete,
} from '../services/templateAssignService';
import {
  deleteTemplateConfigs,
  fetchTemplateConfigsList,
  createTemplateConfigs,
  updateTemplateConfigs,
  getTemplateConfigsDetail,
} from '../services/templateConfigService';

export default {
  namespace: 'ssoConfig',
  state: {
    pagination: {}, // 分页参数
    ssoConfigList: [], // 配置列表
    ssoConfigDetail: {}, // 配置详情
    typeList: [], // 单点登录类别
    templateAssignList: [], // 分配模板列表
    templateConfigList: [], // 模板配置列表
    templateConfigsTypeList: [], // 配置类型列表
    templateConfigsDetail: {}, // 查询明细列表
    assignableList: [], // 可分配模板列表
    templateConfigPagination: {}, // 模板配置列表分页
    assignablePagination: {}, // 可分配模板列表分页
    messageDetail: {},
    distributeList: [], // 分配租户/公司列表
    distributePagination: {}, // 分配租户/公司列表分页
  },
  effects: {
    // 获取初始化数据
    *init({ payload }, { call, put }) {
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

    // 获取初始化数据
    *initConfigType(_, { call, put }) {
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

    // 获取列表
    *fetchConfigList({ payload }, { put, call }) {
      const res = yield call(queryConfig, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            ssoConfigList: data.content,
            pagination: createPagination(data),
          },
        });
      }
    },

    // 获取列表明细
    *getConfigDetail({ payload }, { put, call }) {
      const res = yield call(getConfigDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            ssoConfigDetail: data,
          },
        });
      }
      return data;
    },

    // 获取分配模板列表
    *getTemplateAssignList({ payload }, { put, call }) {
      const res = yield call(fetchTemplateAssign, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            templateAssignList: data,
          },
        });
      }
      return data;
    },

    // 获取模板分配提示信息详情
    *fetchMessageDetail({ payload }, { put, call }) {
      const res = yield call(fetchMessageDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            messageDetail: data,
          },
        });
      }
    },

    // 获取模板配置列表
    *getTemplateConfigList({ payload }, { put, call }) {
      const res = yield call(fetchTemplateConfigsList, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            templateConfigList: data.content,
            templateConfigPagination: createPagination(data),
          },
        });
      }
      return data;
    },

    // 获取可分配模板列表
    *getAssignableList({ payload }, { put, call }) {
      const res = yield call(fetchPortalAssignAssignable, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            assignableList: data.content,
            assignablePagination: createPagination(data),
          },
        });
      }
      return data;
    },

    // 设置默认模板
    *templateAssignDefault({ payload }, { call }) {
      const res = yield call(templateAssignDefault, payload);
      return getResponse(res);
    },

    // 批量分配模板
    *templateAssignCreate({ payload }, { call }) {
      const res = yield call(templateAssignCreate, payload);
      return getResponse(res);
    },

    // 批量删除模板
    *templateAssignDelete({ payload }, { call }) {
      const res = yield call(templateAssignDelete, payload);
      return getResponse(res);
    },

    // 新建配置
    *createConfig({ payload }, { call }) {
      const res = yield call(createConfig, payload);
      return getResponse(res);
    },
    // 编辑配置
    *editConfig({ payload }, { call }) {
      const res = yield call(editConfig, payload);
      return getResponse(res);
    },
    // 删除配置
    *deleteConfig({ payload }, { call }) {
      const res = yield call(deleteConfig, payload);
      return getResponse(res);
    },

    // 查询模板管理详情
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
      return list;
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

    // 获取分配列表
    *queryDistributeList({ payload }, { put, call }) {
      const res = yield call(fetchDistribute, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            distributeList: data.content,
            distributePagination: createPagination(data),
          },
        });
      }
      return data;
    },
    // 保存分配租户/公司
    *saveDistribute({ payload }, { call }) {
      const res = yield call(saveDistribute, payload);
      return getResponse(res);
    },
    // 删除分配租户/公司
    *deleteDistribute({ payload }, { call }) {
      const res = yield call(deleteDistribute, payload);
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
