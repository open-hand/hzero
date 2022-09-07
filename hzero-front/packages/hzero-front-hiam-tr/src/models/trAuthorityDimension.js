/**
 * authorityDimension.js -  model
 * @date: 2019-9-23
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { getResponse } from 'utils/utils';
import {
  queryUserAuthorityData,
  addUserAuthorityData,
  deleteUserAuthorityData,
  queryRoleAuthorityData,
  addRoleAuthorityData,
  deleteRoleAuthorityData,
} from '../services/trAuthorityDimensionService';
import { queryData } from '../services/trRoleManagementService';
import { queryData as queryAccountData } from '../services/trSubAccountOrgService';

export default {
  namespace: 'trAuthorityDimension',
  state: {},
  effects: {
    *queryUserAuthorityData({ payload }, { call }) {
      const res = yield call(queryUserAuthorityData, payload);
      return getResponse(res);
    },
    *addUserAuthorityData({ payload }, { call }) {
      const res = yield call(addUserAuthorityData, payload);
      return getResponse(res);
    },
    *deleteUserAuthorityData({ payload }, { call }) {
      const res = yield call(deleteUserAuthorityData, payload);
      return getResponse(res);
    },
    *queryRoleAuthorityData({ payload }, { call }) {
      const res = yield call(queryRoleAuthorityData, payload);
      return getResponse(res);
    },
    *addRoleAuthorityData({ payload }, { call }) {
      const res = yield call(addRoleAuthorityData, payload);
      return getResponse(res);
    },
    *deleteRoleAuthorityData({ payload }, { call }) {
      const res = yield call(deleteRoleAuthorityData, payload);
      return getResponse(res);
    },
    *querySecGrpAuthorityData({ payload }, { call }) {
      const res = yield call(queryData, payload);
      return getResponse(res);
    },
    *queryAccountSecGrpAuthorityData({ payload }, { call }) {
      const res = yield call(queryAccountData, payload);
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
