/**
 * model - 数据变更审计配置
 * @date: 2019/7/10
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  queryConfigList,
  updateConfigList,
  enableConfigList,
  queryDetailList,
  updateDetailList,
  enableDetailList,
  queryLineDetail,
  fetchAboutConfigList,
  updateDataAuditConfig,
  createAboutConfigAudit,
  deleteAboutConfigAudit,
} from '../services/dataAuditConfigService';

export default {
  namespace: 'dataAuditConfig', // model名称
  state: {
    configList: [],
    configPage: {},
    detailList: [],
    lineDetail: {}, // 列表行详情
    displayTypes: [], // 展示类型
    dataAuditDetail: {}, // 编辑详情
    aboutConfigAuditList: [], // 关联操作审计列表
    currentTenant: {
      tenantName: null,
      tenantId: null,
    },
  },
  effects: {
    // 查询数据审计配置列表
    *fetchConfigList({ payload }, { call, put }) {
      let result = yield call(queryConfigList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            configList: result.content || [],
            configPage: createPagination(result),
          },
        });
      }
    },

    // 更新数据审计配置列表
    *updateConfigList({ payload }, { call }) {
      const result = yield call(updateConfigList, payload);
      return getResponse(result);
    },

    // 数据审计配置列表启用/禁用
    *enableConfigList({ payload }, { call }) {
      const result = yield call(enableConfigList, payload);
      return getResponse(result);
    },

    // 查询数据审计配置详情列表
    *fetchDetailList({ payload }, { call, put }) {
      let result = yield call(queryDetailList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            detailList: result || [],
          },
        });
      }
    },

    // 更新详情列表
    *updateDetailList({ payload }, { call }) {
      const result = yield call(updateDetailList, payload);
      return getResponse(result);
    },

    // 详情列表启用/禁用
    *enableDetailList({ payload }, { call }) {
      const result = yield call(enableDetailList, payload);
      return getResponse(result);
    },

    // 查询初始化状态
    *fetchDisplayTypes(_, { call, put }) {
      const displayTypes = getResponse(yield call(queryIdpValue, 'HMNT.COLUMN.DISPLAY_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          displayTypes,
        },
      });
    },

    // 查询列表行详情
    *fetchLine({ payload }, { call, put }) {
      let result = yield call(queryLineDetail, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            lineDetail: result,
          },
        });
      }
    },
    // 获取关联操作审计列表
    *fetchAboutConfigList({ payload }, { call, put }) {
      const res = yield call(fetchAboutConfigList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            aboutConfigAuditList: list,
          },
        });
      }
    },
    // 新增关联数据审计
    *createAboutConfigAudit({ payload }, { call }) {
      const res = yield call(createAboutConfigAudit, payload);
      return getResponse(res);
    },
    // 删除关联数据审计
    *deleteAboutConfigAudit({ payload }, { call }) {
      const res = yield call(deleteAboutConfigAudit, payload);
      return getResponse(res);
    },
    *updateDataAuditConfig({ payload }, { call }) {
      const res = yield call(updateDataAuditConfig, payload);
      return getResponse(res);
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
