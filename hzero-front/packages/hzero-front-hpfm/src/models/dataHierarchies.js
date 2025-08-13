/**
 * DataHierarchies 数据层级配置
 * @date: 2019-8-14
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { getResponse } from 'utils/utils';
import { queryIdpValue } from 'services/api';

import {
  queryConfig,
  renderTreeData,
  createConfig,
  // deleteConfig,
  editConfig,
  getConfigDetail,
} from '../services/dataHierarchiesService';

export default {
  namespace: 'dataHierarchies',
  state: {
    dataHierarchiesList: [], // 配置列表
    renderTree: [],
    pathMap: {},
    dataHierarchiesDetail: {}, // 配置详情
    expandedRowKeys: [],
  },
  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const res = getResponse(yield call(queryIdpValue, 'HPFM.DATA_HIERARCHY.DISPLAY_STYLE'));
      yield put({
        type: 'updateState',
        payload: {
          displayList: res,
        },
      });
    },

    // 获取列表
    *fetchConfigList({ payload }, { put, call }) {
      const res = yield call(queryConfig, payload);
      const data = getResponse(res);
      const { renderTree, pathMap = {} } = renderTreeData(data, {});
      const expandedRow = {
        expandedRowKeys: Object.keys(pathMap).map((item) => +item),
      };
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            dataHierarchiesList: data.content,
            pathMap,
            ...expandedRow,
            renderTree,
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
            dataHierarchiesDetail: data,
          },
        });
      }
      return data;
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
