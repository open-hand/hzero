/**
 * model 组织架构维护
 * @date: 2018-6-19
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { isNil } from 'lodash';

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchOrgInfo,
  saveEdit,
  forbindLine,
  enabledLine,
  saveAdd,
  organizationQueryLazyTree,
  organizationQueryLine,
} from '../services/organizationService';

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
  namespace: 'organization',

  state: {
    groupCode: '',
    groupName: '',
    unitType: [],
    treeDataSource: [], // 树结构数据
    expandKeys: [], // 树结构数据展开的数据
    loadingExpandKeys: [], // 加载数据中的行
    lineDataSource: [], // 打平分页的数据
    linePagination: {}, // 打平分页
  },

  effects: {
    // 获取组织信息
    *fetchOrgInfo(_, { call, put }) {
      const organization = getResponse(yield call(fetchOrgInfo));
      const unitType = yield call(queryIdpValue, 'HPFM.HR.UNIT_TYPE');
      if (organization) {
        yield put({
          type: 'updateState',
          payload: {
            groupCode: organization.tenantNum,
            groupName: organization.tenantName,
            // TODO: 这里为什么要过滤 这个 D
            unitType: unitType.filter(item => item.value !== 'D'),
          },
        });
      }
    },
    // // 获取组织数据（树形展示）
    // * searchOrganizationData({payload}, {call, put}) {
    //   const {tenantId, unitName, unitCode, expandFlag, expandedRowKeys, ...others} = payload;
    //   let result = {};
    //   let expandedRow = {};
    //   if (unitCode || unitName) {
    //     result = yield call(search, {tenantId, unitName, unitCode});
    //   } else {
    //     result = yield call(searchAll, {tenantId});
    //   }
    //   result = getResponse(result);
    //   const {renderTree, pathMap} = renderTreeData(result, {});
    //   if (!expandFlag) {
    //     expandedRow = {
    //       expandedRowKeys: Object.keys(pathMap).map(item => +item),
    //     };
    //   } else {
    //     const unitIdList = result.map(item => item.unitId) || [];
    //     expandedRow = {
    //       expandedRowKeys: Array.from(
    //         new Set([...expandedRowKeys, ...unitIdList])),
    //     };
    //   }
    //   yield put({
    //     type: 'updateState',
    //     payload: {
    //       renderTree,
    //       pathMap,
    //       addData: {},
    //       ...expandedRow,
    //       ...others,
    //     },
    //   });
    // },
    // 更新组织信息
    *saveEditData({ payload }, { call }) {
      const result = yield call(saveEdit, payload);
      return getResponse(result);
    },
    // 新增组织信息
    *saveAddData({ payload }, { call }) {
      const result = yield call(saveAdd, payload);
      return getResponse(result);
    },
    // 禁用“组织行”
    *forbidLine({ payload }, { call }) {
      const result = yield call(forbindLine, payload);
      return getResponse(result);
    },
    // 启用“组织行”
    *enabledLine({ payload }, { call }) {
      const result = yield call(enabledLine, payload);
      return getResponse(result);
    },
    *unitsQueryLazyTree({ payload = {} }, { call, put }) {
      const { unitId, indent = -1 } = payload;
      const params = {};
      const queryChild = !isNil(unitId);
      if (queryChild) {
        params.unitId = unitId;
      }
      yield put({
        type: 'updateLoadingExpandKeys',
        payload: {
          queryChild,
          unitId,
          type: 'load',
        },
      });
      const res = yield call(organizationQueryLazyTree, params);
      const responseRes = getResponse(res);
      if (responseRes) {
        yield put({
          type: 'updateTreeDataSource',
          payload: {
            queryChild,
            unitId,
            treeData: transformData(responseRes, indent + 1),
          },
        });
      }
      yield put({
        type: 'updateLoadingExpandKeys',
        payload: {
          queryChild,
          unitId,
          type: 'unload',
        },
      });
    },
    *unitsQueryLine({ payload }, { call, put }) {
      const { params } = payload;
      const res = yield call(organizationQueryLine, params);
      const responseRes = getResponse(res);
      if (responseRes) {
        yield put({
          type: 'updateState',
          payload: {
            lineDataSource: responseRes.content,
            linePagination: createPagination(responseRes),
          },
        });
      }
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
      const { queryChild, unitId, treeData } = payload;
      return {
        ...state,
        treeDataSource: queryChild
          ? buildNewTreeDataSource(state.treeDataSource, item => {
              if (item.unitId === unitId) {
                return {
                  ...item,
                  children: treeData,
                };
              } else {
                return item;
              }
            })
          : treeData,
        expandKeys: queryChild ? [...state.expandKeys, unitId] : [],
      };
    },
    // 由于 expand 问题, expand 由自己管理
    updateLoadingExpandKeys(state, { payload }) {
      const { queryChild, unitId, type } = payload;
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
            loadingExpandKeys: [...state.loadingExpandKeys, unitId],
          };
        case 'unload':
          return {
            ...state,
            loadingExpandKeys: state.loadingExpandKeys.filter(k => k !== unitId),
          };
        default:
          return state;
      }
    },
  },
};
