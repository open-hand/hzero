/**
 * model - 服务器上传配置
 * @date: 2019-7-5
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  queryConfig,
  createConfig,
  fetchConfigDetail,
  saveConfig,
} from '../services/serverUploadService';

export default {
  namespace: 'serverUpload',
  state: {
    pagination: {}, // 分页参数
    serverUploadList: [], // 配置列表
    serverUploadDetail: {}, // 配置明细
    typeList: [], // 上传类别
    serverConfigLineList: [], // 配置明细行数据
  },
  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
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

    // 获取列表
    * fetchConfigList({ payload }, { put, call }) {
      const res = yield call(queryConfig, parseParameters(payload));
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            serverUploadList: data.content,
            pagination: createPagination(data),
          },
        });
      }
    },
    // 获取配置明细
    * fetchConfigDetail({ payload }, { put, call }) {
      const res = yield call(fetchConfigDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            serverUploadDetail: data,
            serverConfigLineList: data.serverConfigLineList,
          },
        });
      }
    },

    // 新建配置
    * createConfig({ payload }, { call }) {
      const res = yield call(createConfig, payload);
      return getResponse(res);
    },
    // 保存配置
    * saveConfig({ payload }, { call }) {
      const res = yield call(saveConfig, payload);
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
