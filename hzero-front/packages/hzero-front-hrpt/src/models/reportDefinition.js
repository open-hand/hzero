/**
 * model 报表平台/报表定义
 * @date: 2018-11-22
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { isEmpty } from 'lodash';
import { queryMapIdpValue, queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchReportDefList,
  getMetaMetaColumns,
  getInitMetaColumn,
  fetchInitTemplate,
  fetchReportDefinitionDetail,
  fetchTemplateDetail,
  createReportDefinition,
  deleteReportDefinition,
  updateReportDefinition,
  createTemplate,
  deleteTemplate,
  changeDefaultTemplate,
  fetchAssignedPermission,
  createPermission,
  fetchPermissionDetail,
  updatePermission,
  deletePermission,
  copyReportDefinition,
  fetchExportType,
} from '../services/reportDefinitionService';

export default {
  namespace: 'reportDefinition',
  state: {
    list: [], // 数据列表
    code: {}, // 值集
    pagination: {}, // 分页器
    header: {}, // 数据集头
    template: [], // 模板明细数据
    templatePagination: {}, // 模板明细分页器
    templateList: [], // 模板数据列表
    templateListPagination: {}, // 模板列表分页器
    permissionsList: [], // 已分配的权限
    permissionsPagination: {}, // 模板列表分页器
    reportTypeCode: [], // 报表类型
  },
  effects: {
    // 获取报表定义列表
    *fetchReportDefList({ payload }, { call, put }) {
      let result = yield call(fetchReportDefList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    // 获取报表类型
    *fetchReportTypeCode(_, { call, put }) {
      const reportTypeCode = getResponse(yield call(queryIdpValue, 'HRPT.REPORT_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          reportTypeCode,
        },
      });
    },
    // 统一获取值级的数据
    *batchCode({ payload }, { put, call }) {
      const { lovCodes } = payload;
      const code = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (!isEmpty(code)) {
        yield put({
          type: 'updateState',
          payload: {
            code,
          },
        });
      }
    },
    // 初始化列信息
    *getMetaMetaColumns({ payload }, { call, put }) {
      const { header, tenantId, reportTypeCode, ...otherValues } = payload;
      let result = yield call(getMetaMetaColumns, otherValues);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            header: { ...header, metaColumns: [...result] },
          },
        });
      }
      return result;
    },
    // 新建列信息，初始化数据
    *getInitMetaColumn(_, { call }) {
      const result = yield call(getInitMetaColumn);
      return getResponse(result);
    },
    // 获取报表定义明细
    *fetchReportDefinitionDetail({ payload }, { call, put }) {
      let result = yield call(fetchReportDefinitionDetail, { ...payload });
      result = getResponse(result);
      if (result) {
        const { metaColumns, options, ...otherResults } = result;
        yield put({
          type: 'updateState',
          payload: {
            header: {
              metaColumns: JSON.parse(metaColumns || '[]'),
              options: JSON.parse(options || '{}'),
              ...otherResults,
            },
          },
        });
      }
      return result;
    },
    // 获取模板信息
    *fetchInitTemplate({ payload }, { call, put }) {
      let result = yield call(fetchInitTemplate, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            templateList: result.content,
            templateListPagination: createPagination(result),
          },
        });
      }
    },
    // 获取模板明细
    *fetchTemplateDetail({ payload }, { call, put }) {
      let result = yield call(fetchTemplateDetail, { ...payload });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            template: result.content,
            templatePagination: createPagination(result),
          },
        });
      }
      return result;
    },
    // 新增报表定义
    *createReportDefinition({ payload }, { call }) {
      const result = yield call(createReportDefinition, { ...payload });
      return getResponse(result);
    },
    // 获取报表类型
    *fetchExportType(_, { call }) {
      const result = yield call(fetchExportType);
      return getResponse(result);
    },
    // 复制报表定义
    *copyReportDefinition({ payload }, { call }) {
      const result = yield call(copyReportDefinition, payload);
      return getResponse(result);
    },

    // 更新报表定义
    *updateReportDefinition({ payload }, { call }) {
      const result = yield call(updateReportDefinition, { ...payload });
      return getResponse(result);
    },
    // 删除报表定义
    *deleteReportDefinition({ payload }, { call }) {
      const result = getResponse(yield call(deleteReportDefinition, { ...payload }));
      return result;
    },
    // 模板新增
    *createTemplate({ payload }, { call }) {
      const result = yield call(createTemplate, { ...payload });
      return getResponse(result);
    },
    // 模板删除
    *deleteTemplate({ payload }, { call }) {
      const result = yield call(deleteTemplate, { ...payload });
      return getResponse(result);
    },
    // 改变默认模板
    *changeDefaultTemplate({ payload }, { call }) {
      const result = yield call(changeDefaultTemplate, { ...payload });
      return getResponse(result);
    },
    // 获取已分配的权限
    *fetchAssignedPermission({ payload }, { call, put }) {
      let result = yield call(fetchAssignedPermission, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            permissionsList: result.content,
            permissionsPagination: createPagination(result),
          },
        });
      }
      return result;
    },
    // 创建权限
    *createPermission({ payload }, { call }) {
      const res = yield call(createPermission, payload);
      return getResponse(res);
    },
    // 查询报表权限详情
    *fetchPermissionDetail({ payload }, { call, put }) {
      const res = yield call(fetchPermissionDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            header: result,
          },
        });
      }
      return result;
    },
    // 更新权限
    *updatePermission({ payload }, { call }) {
      const res = yield call(updatePermission, payload);
      return getResponse(res);
    },
    // 删除权限
    *deletePermission({ payload }, { call }) {
      const result = yield call(deletePermission, payload);
      return getResponse(result);
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
