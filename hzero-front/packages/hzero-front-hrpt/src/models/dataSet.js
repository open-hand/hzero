/**
 * model 报表平台/数据集
 * @date: 2018-11-19
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { isEmpty } from 'lodash';
import uuid from 'uuid/v4';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchDataSetList,
  getMetadata,
  getParameters,
  previewSql,
  handleGetXmlSample,
  handleExportXmlFile,
  fetchDataSetDetail,
  createDataSet,
  deleteDataSet,
  updateDataSet,
  fetchAssignList,
} from '../services/dataSetService';

export default {
  namespace: 'reportDataSet',
  state: {
    list: [], // 数据列表
    code: {}, // 值集
    pagination: {}, // 分页器
    header: {}, // 数据集头
    sqlContent: {}, // sql校验内容
    xmlSampleContent: {}, // xml示例内容
    assignList: [], // 数据集管理报表列表
    assignPagination: {},
  },
  effects: {
    // 获取数据集列表
    *fetchDataSetList({ payload }, { call, put }) {
      let result = yield call(fetchDataSetList, payload);
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
    // 获取数据集关联报表
    *fetchAssignList({ payload }, { call, put }) {
      let result = yield call(fetchAssignList, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            assignList: result.content,
            assignPagination: createPagination(result),
          },
        });
      }
    },
    // 初始化元数据
    *getMetadata({ payload }, { call, put }) {
      const { header, ...otherValues } = payload;
      let result = yield call(getMetadata, otherValues);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            header: { ...header, metaColumns: result },
          },
        });
      }
      return result;
    },
    // 初始化参数
    *getParameters({ payload }, { call, put }) {
      const { header, ...otherValues } = payload;
      let result = yield call(getParameters, otherValues);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            header: { ...header, queryParams: [...result] },
          },
        });
      }
      return result;
    },
    // 预览sql
    *previewSql({ payload }, { call, put }) {
      let result = yield call(previewSql, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            sqlContent: result,
          },
        });
      }
      return result;
    },
    // 获取xml示例数据
    *handleGetXmlSample({ payload }, { call, put }) {
      let result = yield call(handleGetXmlSample, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            xmlSampleContent: result,
          },
        });
      }
      return result;
    },
    // 导出xml文件
    *handleExportXmlFile({ payload }, { call }) {
      const result = yield call(handleExportXmlFile, payload);
      return result;
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
    // 获取数据集明细
    *fetchDataSetDetail({ payload }, { call, put }) {
      let result = yield call(fetchDataSetDetail, { ...payload });
      result = getResponse(result);
      if (result) {
        const { queryParams, metaColumns, ...otherResults } = result;
        const tempQueryParams = JSON.parse(queryParams || '[]').map((item) => ({
          ...item,
          uuid: uuid(),
        }));
        const temMetaColumns = JSON.parse(metaColumns || '[]').map((item) => ({
          ...item,
          uuid: uuid(),
        }));
        yield put({
          type: 'updateState',
          payload: {
            header: {
              queryParams: tempQueryParams,
              metaColumns: temMetaColumns,
              ...otherResults,
            },
          },
        });
      }
      return result;
    },
    // 新增数据集
    *createDataSet({ payload }, { call }) {
      const result = yield call(createDataSet, { ...payload });
      return getResponse(result);
    },
    // 更新数据集
    *updateDataSet({ payload }, { call }) {
      const result = yield call(updateDataSet, { ...payload });
      return getResponse(result);
    },
    // 删除数据集
    *deleteDataSet({ payload }, { call }) {
      const result = getResponse(yield call(deleteDataSet, { ...payload }));
      return result;
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
