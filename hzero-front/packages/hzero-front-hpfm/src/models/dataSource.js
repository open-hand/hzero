/**
 * @date 2018-09-17
 * @author CJ <juan.chen01@hand-china.com>
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue, getPublicKey } from 'hzero-front/lib/services/api';

import {
  fetchDataSourceList,
  createDataSource,
  editDataSource,
  handleTestDataSource,
  getDbPoolParams,
  getDriverClass,
  fetchDataSourceDetail,
  fetchFormParams,
  testConnection,
} from '../services/dataSourceService';

export default {
  namespace: 'dataSource',
  state: {
    dataSourceData: {}, // 查询数据列表
    detaSourceDetail: {}, // 数据源详情数据
    datasourceId: undefined, // 数据源id
    dbPoolParams: {}, // 连接池参数数据
    dataSourceClassList: [], // 数据库分类值集
    dataSourceTypeList: [], // 数据库类型值集
    purposeType: [], // 数据源用途
    dbPoolTypeList: [], // 连接池类型值集
    dsPurposeCodeList: [], // 数据源用途值集
    driverTypeCodeList: [], // 数据源用途值集
    pagination: {}, // 分页对象
    extConfigs: [], // 表单配置信息
    publicKey: '', // 密码公钥
  },
  effects: {
    // 统一获取值级的数据
    *batchCode({ payload }, { put, call }) {
      const { lovCodes } = payload;
      const code = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (code) {
        const {
          dataSourceClass: dataSourceClassList = [],
          dataSourceType: dataSourceTypeList = [],
          dbPoolType: dbPoolTypeList = [],
          dsPurposeCode: dsPurposeCodeList = [],
          driverTypeCode: driverTypeCodeList = [],
          purposeType = [],
        } = code;
        yield put({
          type: 'updateState',
          payload: {
            dataSourceClassList,
            dataSourceTypeList,
            dbPoolTypeList,
            dsPurposeCodeList,
            purposeType,
            driverTypeCodeList,
          },
        });
      }
      return code;
    },

    // 获取连接池参数
    *getDbPoolParams({ payload }, { call, put }) {
      const res = yield call(getDbPoolParams, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            dbPoolParams: list,
          },
        });
      }
      return list;
    },

    // 获取驱动类
    *getDriverClass({ payload }, { call }) {
      const res = yield call(getDriverClass, payload);
      return getResponse(res);
    },

    // 获取数据源列表数据
    *fetchDataSourceList({ payload }, { call, put }) {
      const res = yield call(fetchDataSourceList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            dataSourceData: list,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 获取数据源详情
    *fetchDataSourceDetail({ payload }, { call, put }) {
      const res = yield call(fetchDataSourceDetail, payload);
      const data = getResponse(res);
      if (data) {
        const { options = '{}' } = data;
        yield put({
          type: 'updateState',
          payload: {
            dataSourceDetail: data,
            dbPoolParams: JSON.parse(options),
          },
        });
      }
      return data;
    },

    // 新建保存
    *createDataSource({ payload }, { call, put }) {
      const res = getResponse(yield call(createDataSource, { ...payload }));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            datasourceId: res.datasourceId,
          },
        });
      }
      return res;
    },

    // 编辑保存
    *editDataSource({ payload }, { call }) {
      const result = yield call(editDataSource, { ...payload });
      return getResponse(result);
    },

    // 测试数据源
    *handleTestDataSource({ payload }, { call }) {
      const result = yield call(handleTestDataSource, { ...payload });
      return getResponse(result);
    },

    *fetchFormParams({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchFormParams, { ...payload }));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            extConfigs: result,
          },
        });
      }
      return result;
    },

    *testConnection({ payload }, { call }) {
      const result = yield call(testConnection, payload);
      return result;
    },

    // 请求公钥
    *getPublicKey(_, { call, put }) {
      const res = yield call(getPublicKey);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            publicKey: res.publicKey,
          },
        });
      }
      return res;
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
