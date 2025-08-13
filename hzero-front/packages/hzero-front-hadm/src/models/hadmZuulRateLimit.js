/**
 * zuulRateLimit - zuul限流配置
 * @date: 2018/09/10 17:37:52
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { VERSION_IS_OP } from 'utils/config';
import { createPagination, getResponse } from 'utils/utils';

import { queryMapIdpValue } from 'hzero-front/lib/services/api';

import {
  addRateLimit,
  deleteDimensionConfigs,
  deleteHeaders,
  deleteLines,
  detailSave,
  fetchHeaderInformation,
  fetchLineDetail,
  fetchLines,
  fetchRateLimitList,
  insertDimensionConfigs,
  queryDimensionConfigs,
  queryDimensionConfigsDetail,
  queryGateWayRateLimitDimensionAllowChange,
  refresh,
  updateDimensionConfigs,
  insertLine,
  updateLine,
} from '../services/zuulRateLimitService';

function transformRateLimitLineRouteToModel(record) {
  const { 'data-extra__rateLimitDimension_url': extraUrl, ...saveRecord } = record || {};
  const rateLimitDimensionArr = [];
  (record.rateLimitDimension || []).forEach(dimension => {
    if (dimension === 'url') {
      if (record['data-extra__rateLimitDimension_url']) {
        rateLimitDimensionArr.push(`url(${record['data-extra__rateLimitDimension_url']})`);
      } else {
        rateLimitDimensionArr.push('url({1})');
      }
    } else {
      rateLimitDimensionArr.push(dimension);
    }
  });
  return {
    ...saveRecord,
    // 后端存储是以,分割的
    rateLimitDimension: rateLimitDimensionArr.join(','),
  };
}

/**
 * 将 行数据转化为 需要的数据
 * rateLimitDimension
 * data-extra__rateLimitDimension_url
 * @param record
 * @returns {*}
 */
function transformRateLimitLineModelToRoute(record) {
  const newDimension = [];
  let dataExtraRateLimitDimensionUrl = '';
  (record.rateLimitDimension || '').split(',').forEach(dimension => {
    if (!dimension) {
      return;
    }
    if (dimension.startsWith('url')) {
      newDimension.push('url');
      const m = dimension.match(/^url\((.*)\)$/);
      dataExtraRateLimitDimensionUrl = (m && m[1]) || '{1}';
    } else {
      newDimension.push(dimension);
    }
  });
  return record
    ? {
        ...record,
        // 后端存储是以,分割的
        rateLimitDimension: newDimension,
        'data-extra__rateLimitDimension_url': dataExtraRateLimitDimensionUrl,
      }
    : record;
}

function transformDimensionRouteToModel(record) {
  const newRecord = {};
  const dimensionKeys = [];
  const { rateLimitLine } = record;
  const { rateLimitDimension } = transformRateLimitLineRouteToModel(rateLimitLine);
  Object.keys(record).forEach(fieldName => {
    if (fieldName.startsWith('data-field__dimension')) {
      if (fieldName.startsWith('data-field__dimension-')) {
        const field = record[fieldName];
        if (field === 'url') {
          dimensionKeys.push((record[`data-field__dimensionValue-${field}`] || []).join(';'));
        } else {
          dimensionKeys.push(record[`data-field__dimensionValue-${field}`]);
        }
      }
    } else {
      newRecord[fieldName] = record[fieldName];
    }
  });
  return {
    ...newRecord,
    // 后端存储是以,分割的
    rateLimitDimension,
    dimensionKey: dimensionKeys.join(','),
  };
}

function transformDimensionModelToRoute(record) {
  if (record) {
    const { rateLimitDimension, dimensionKey, dimensionMeaning, ...newRecord } = record;
    // 对应的维度
    const rateLimitDimensions = (record.rateLimitDimension || '').split(',');
    // 对应的值
    const dimensionKeys = (record.dimensionKey || '').split(',');
    // 对应的显示值
    // const dimensionMeanings = (record.dimensionMeaning || '').split(',');

    rateLimitDimensions.forEach((field, index) => {
      const correctField = field.startsWith('url') ? 'url' : field;
      const dimension = {
        dimension: field,
        dimensionValue: dimensionKeys[index],
        // TODO: 如果没有查询出显示值, 则使用存储值
        dimensionValueMeaning: record[`${correctField}KeyMeaning`] || dimensionKeys[index],
      };
      if (dimension.dimension.startsWith('url')) {
        // 维度
        newRecord[`data-field__dimension-${correctField}`] = correctField;
        // 维度值
        newRecord[`data-field__dimensionValue-${correctField}`] = (
          dimension.dimensionValue || ''
        ).split(';');
        // 显示值
        newRecord[`data-field__dimensionMeaning-${correctField}`] = dimension.dimensionValueMeaning;
      } else {
        // 维度
        newRecord[`data-field__dimension-${correctField}`] = dimension.dimension;
        // 维度值
        newRecord[`data-field__dimensionValue-${correctField}`] = dimension.dimensionValue;
        // 显示值
        newRecord[`data-field__dimensionMeaning-${correctField}`] = dimension.dimensionValueMeaning;
      }
    });
    return newRecord;
  } else {
    return record;
  }
}

/**
 * 转化值集
 * 在OP下没有租户
 */
function transformDimensionTypes(dimensionTypes) {
  if (dimensionTypes && VERSION_IS_OP) {
    return dimensionTypes.filter(item => item.value !== 'tenant');
  } else {
    return dimensionTypes;
  }
}

export default {
  namespace: 'hadmZuulRateLimit',

  state: {
    list: [],
    headerInformation: {}, // 头详情
    zuulRateLimitLineList: [], // 行数据
    pagination: {},
    detailPagination: {}, // 行分页信息
    dataSourceMap: {},
    // selectedRowKeys: [],
    selectedDetailRows: [],
    selectedDetailRowKeys: [],
    modalVisible: false,
    limitTypes: [], // 限流类型 值集
    dimensionTypes: [],
    lineDetail: {},
    refreshStatus: [],
    // 维度信息
    dimensionConfigsDataSource: [], // 数据源
    dimensionConfigsPagination: {}, // 分页信息
    dimensionAllowChange: false, // 维度是否允许更改
  },

  effects: {
    * init(_, { call, put }) {
      const result = getResponse(
        yield call(queryMapIdpValue, {
          limitTypes: 'HADM.RATE_LIMIT_TYPE',
          dimensionTypes: 'HADM.GATEWAY_RATE_LIMIT_DIMENSION',
          refreshStatus: 'HADM.REFRESH_STATUS',
        })
      );
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            limitTypes: result.limitTypes,
            dimensionTypes: transformDimensionTypes(result.dimensionTypes),
            refreshStatus: result.refreshStatus,
          },
        });
      }
    },
    // 查询配置列表
    * fetchList({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchRateLimitList, payload));
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
    // 新增配置
    * addRateLimit({ payload }, { call }) {
      const data = yield call(addRateLimit, { ...payload });
      return getResponse(data);
    },
    // 查询配置头行信息
    * fetchHeaderInformation({ payload }, { call, put }) {
      const { page = 0, size = 10, rateLimitId } = payload;
      const result = getResponse(yield call(fetchHeaderInformation, { rateLimitId, page, size }));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            headerInformation: result,
            zuulRateLimitLineList: (result.gatewayRateLimitLineList.content || []).map(record =>
              transformRateLimitLineModelToRoute(record)
            ),
            detailPagination: createPagination(result.gatewayRateLimitLineList),
          },
        });
      }
    },
    // 查询配置行信息
    * fetchLines({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchLines, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            zuulRateLimitLineList: (result.content || []).map(record =>
              transformRateLimitLineModelToRoute(record)
            ),
            detailPagination: createPagination(result),
          },
        });
      }
    },

    * fetchLineDetail({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchLineDetail, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            lineDetail: transformRateLimitLineModelToRoute(result),
          },
        });
      }
    },

    // 头行保存
    * detailSave({ payload }, { call }) {
      const data = yield call(detailSave, {
        ...payload,
        gatewayRateLimitLineList: (payload.gatewayRateLimitLineList || []).map(record =>
          transformRateLimitLineRouteToModel(record)
        ),
      });
      return getResponse(data);
    },
    // 删除头
    * deleteHeaders({ payload }, { call }) {
      const data = yield call(deleteHeaders, payload);
      return getResponse(data);
    },
    // 新增行
    * insertLine({ payload }, { call }) {
      const { dimensionConfig } = payload;
      const data = yield call(
        insertLine,
        transformRateLimitLineRouteToModel(dimensionConfig || {})
      );
      return getResponse(data);
    },
    // 修改行
    * updateLine({ payload }, { call }) {
      const { dimensionConfig } = payload;
      const data = yield call(
        updateLine,
        transformRateLimitLineRouteToModel(dimensionConfig || {})
      );
      return getResponse(data);
    },
    // 删除行
    * deleteLines({ payload }, { call }) {
      const data = yield call(
        deleteLines,
        (payload || []).map(dimensionConfig => transformRateLimitLineRouteToModel(dimensionConfig))
      );
      return getResponse(data);
    },
    // 刷新头
    * refresh({ payload }, { call }) {
      const data = yield call(refresh, payload);
      return getResponse(data);
    },
    // 新增维度信息
    * insertDimensionConfigs({ payload }, { call }) {
      const { dimensionConfig } = payload;
      const res = yield call(
        insertDimensionConfigs,
        transformDimensionRouteToModel(dimensionConfig)
      );
      return getResponse(res);
    },
    // 删除维度信息
    * deleteDimensionConfigs({ payload }, { call }) {
      const { dimensionConfig } = payload;
      const res = yield call(
        deleteDimensionConfigs,
        transformRateLimitLineRouteToModel(dimensionConfig)
      );
      return getResponse(res);
    },
    // 更新维度信息
    * updateDimensionConfigs({ payload }, { call }) {
      const { dimensionConfig } = payload;
      const res = yield call(
        updateDimensionConfigs,
        transformDimensionRouteToModel(dimensionConfig)
      );
      return getResponse(res);
    },
    // 查询维度信息
    * queryDimensionConfigs({ payload }, { call, put }) {
      const { rateLimitLineId, pagination } = payload;
      const res = yield call(queryDimensionConfigs, rateLimitLineId, pagination);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            dimensionConfigsDataSource: data.content.map(record =>
              transformDimensionModelToRoute(record)
            ),
            dimensionConfigsPagination: createPagination(data),
          },
        });
      }
    },
    // 查询维度信息详情
    * queryDimensionConfigsDetail({ payload }, { call }) {
      const { rateLimitDimId } = payload;
      const res = yield call(queryDimensionConfigsDetail, rateLimitDimId);
      return transformDimensionModelToRoute(getResponse(res));
    },
    // 查询限制方式 的维度是否允许修改
    * queryGateWayRateLimitDimensionAllowChange({ payload }, { call, put }) {
      const { rateLimitLineId } = payload;
      const res = yield call(queryGateWayRateLimitDimensionAllowChange, rateLimitLineId);
      yield put({
        type: 'updateState',
        payload: {
          dimensionAllowChange: res,
        },
      });
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
