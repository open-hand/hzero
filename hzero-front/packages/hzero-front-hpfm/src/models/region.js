/**
 * model 地区定义
 * @date: 2018-6-19
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { isNil } from 'lodash';
import { getResponse, createPagination } from 'utils/utils';

import {
  regionQueryLine,
  regionQueryLazyTree,
  regionCreate,
  regionUpdate,
  regionEnable,
  regionDisable,
  regionQueryDetail,
} from '../services/regionService';

function buildNewTreeDataSource(treeDataSource = [], iterFunc) {
  return treeDataSource.map(item => {
    if (item.children) {
      const newItem = iterFunc(item);
      return {
        ...newItem,
        children: buildNewTreeDataSource(newItem.children, iterFunc),
      };
    } else {
      return iterFunc(item);
    }
  });
}

function transformData(dataSource = [], indent = 0) {
  return dataSource.map(item => {
    if (item.hasNextFlag === 1) {
      return {
        ...item,
        indent,
        children: [],
      };
    } else {
      return {
        ...item,
        indent,
      };
    }
  });
}

export default {
  namespace: 'region',

  state: {
    // 地区 打平数据存储
    lineDataSource: [],
    linePagination: {},

    // 地区 懒加载树结构
    treeDataSource: [], // 树结构数据
    expandKeys: [], // 树结构数据展开的数据
    loadingExpandKeys: [], // 加载数据中的行
  },

  effects: {
    // 查询 打平的地区
    *regionQueryLine({ payload }, { call, put }) {
      const { countryId, query } = payload;
      const res = yield call(regionQueryLine, countryId, query);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            lineDataSource: data.content,
            linePagination: createPagination(data),
          },
        });
      }
    },
    // 查询懒加载树结构
    *regionQueryLazyTree({ payload }, { call, put }) {
      const { countryId, regionId, indent = -1 } = payload;
      const queryChild = !isNil(regionId);
      yield put({
        type: 'updateLoadingExpandKeys',
        payload: {
          queryChild,
          regionId,
          type: 'load',
        },
      });
      const res = yield call(regionQueryLazyTree, countryId, regionId);
      const responseRes = getResponse(res);
      if (responseRes) {
        yield put({
          type: 'updateTreeDataSource',
          payload: {
            queryChild,
            regionId,
            treeData: transformData(responseRes, indent + 1),
          },
        });
      }
      yield put({
        type: 'updateLoadingExpandKeys',
        payload: {
          queryChild,
          regionId,
          type: 'unload',
        },
      });
    },
    *regionCreate({ payload }, { call }) {
      const { countryId, body } = payload;
      const res = yield call(regionCreate, countryId, body);
      return getResponse(res);
    },
    *regionUpdate({ payload }, { call }) {
      const { countryId, body, regionId } = payload;
      const res = yield call(regionUpdate, countryId, regionId, body);
      return getResponse(res);
    },
    *regionEnable({ payload }, { call }) {
      const { body, regionId } = payload;
      const res = yield call(regionEnable, regionId, body);
      return getResponse(res);
    },
    *regionDisable({ payload }, { call }) {
      const { body, regionId } = payload;
      const res = yield call(regionDisable, regionId, body);
      return getResponse(res);
    },
    *regionQueryDetail({ payload }, { call }) {
      const { regionId } = payload;
      const res = yield call(regionQueryDetail, regionId);
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
    // 由于 可能异步问题 导致 数据不一致, 所以更新放到 reducer 中
    updateTreeDataSource(state, { payload }) {
      const { queryChild, regionId, treeData } = payload;
      return {
        ...state,
        treeDataSource: queryChild
          ? buildNewTreeDataSource(state.treeDataSource, item => {
              if (item.regionId === regionId) {
                return {
                  ...item,
                  children: treeData,
                };
              } else {
                return item;
              }
            })
          : treeData,
        expandKeys: queryChild ? [...state.expandKeys, regionId] : [],
      };
    },
    // 由于 expand 问题, expand 由自己管理
    updateLoadingExpandKeys(state, { payload }) {
      const { queryChild, regionId, type } = payload;
      if (!queryChild) {
        return {
          ...state,
          loadingExpandKeys: [],
        };
      }
      switch (type) {
        case 'load':
          return {
            ...state,
            loadingExpandKeys: [...state.loadingExpandKeys, regionId],
          };
        case 'unload':
          return {
            ...state,
            loadingExpandKeys: state.loadingExpandKeys.filter(k => k !== regionId),
          };
        default:
          return state;
      }
    },
  },
};
