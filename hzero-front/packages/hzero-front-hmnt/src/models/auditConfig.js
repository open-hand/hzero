/**
 * audit-config 操作审计配置
 * @date: 2019-7-18
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryUnifyIdpValue, queryIdpValue } from 'hzero-front/lib/services/api';
import {
  deleteAuditConfig,
  fetchAuditConfigList,
  createAuditConfig,
  updateAuditConfig,
  getAuditConfigDetail,
  fetchAboutDataList,
  createAboutDataAudit,
  deleteAboutDataAudit,
} from '../services/auditConfigService';

export default {
  namespace: 'auditConfig',
  state: {
    auditConfigList: [], // 操作审计列表
    pagination: {}, // 分页对象
    auditConfigDetail: {}, // 操作审计详情
    auditTypeList: [],
    aboutDataAuditList: [], // 关联数据审计表格
  },
  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const languageList = getResponse(yield call(queryUnifyIdpValue, 'HPFM.LANGUAGE'));
      const messageType = getResponse(yield call(queryIdpValue, 'HPFM.MESSAGE_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          languageList,
          messageType,
        },
      });
    },

    // 获取初始化数据
    *initAuditType(_, { call, put }) {
      const auditTypeList = getResponse(yield call(queryIdpValue, 'HMNT.AUDIT_OP.TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          auditTypeList,
        },
      });
    },

    // 获取操作审计配置列表
    *fetchAuditConfigList({ payload }, { call, put }) {
      const res = yield call(fetchAuditConfigList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            auditConfigList: list.content,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 查询操作审计配置详情
    *getAuditConfigDetail({ payload }, { call, put }) {
      const res = yield call(getAuditConfigDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            auditConfigDetail: list,
          },
        });
      }
    },

    // 新增操作审计配置
    *createAuditConfig({ payload }, { call }) {
      const res = yield call(createAuditConfig, payload);
      return getResponse(res);
    },

    // 更新操作审计配置
    *updateAuditConfig({ payload }, { call }) {
      const res = yield call(updateAuditConfig, payload);
      return getResponse(res);
    },

    // 删除操作审计配置
    *deleteAuditConfig({ payload }, { call }) {
      const res = yield call(deleteAuditConfig, payload);
      return getResponse(res);
    },
    *fetchAboutDataList({ payload }, { call, put }) {
      const res = yield call(fetchAboutDataList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            aboutDataAuditList: list,
          },
        });
      }
    },
    // 新增关联数据审计
    *createAboutDataAudit({ payload }, { call }) {
      const res = yield call(createAboutDataAudit, payload);
      return getResponse(res);
    },
    // 删除关联数据审计
    *deleteAboutDataAudit({ payload }, { call }) {
      const res = yield call(deleteAboutDataAudit, payload);
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
