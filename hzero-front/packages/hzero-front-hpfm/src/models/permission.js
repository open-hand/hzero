/**
 * @date 2018-09-01
 * @author LJ <jun.li06@hand-china.com>
 */
// import { isEmpty } from 'lodash';
import {
  createPagination,
  getResponse,
  getCurrentOrganizationId,
  isTenantRoleLevel,
} from 'utils/utils';
import {
  queryRangeList,
  queryOrganizationRangeList,
  queryRuleList,
  queryOrganizationRuleList,
  queryCode,
  createRule,
  createOrganizationRule,
  createRange,
  createOrganizationRange,
  updateRule,
  updateOrganizationRule,
  updateRange,
  updateOrganizationRange,
  deleteRule,
  deleteOrganizationRule,
  deleteRange,
  deleteOrganizationRange,
  addPermissionRel,
  queryPermissionRel,
  deletePermissionRel,
} from '../services/permissionService';

export default {
  namespace: 'permission',
  state: {
    code: {},
    range: {
      list: {
        dataSource: [],
        pagination: {
          pageSize: 10,
          total: 0,
          current: 1,
        },
      },
    },
    rule: {
      list: {
        dataSource: [],
        pagination: {
          pageSize: 10,
          total: 0,
          current: 1,
        },
      },
    },
    rangeServiceNameExclList: [],
    rangeSqlidExclList: [],
    rangeTenantExclList: [],
  },
  effects: {
    *queryRangeList({ payload }, { put, call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(
          queryOrganizationRangeList,
          { page: 0, size: 10, ...payload },
          organizationId
        );
      } else {
        res = yield call(queryRangeList, { page: 0, size: 10, ...payload });
      }
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateRangeStateReducer',
          payload: {
            list: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },
    *queryRuleList({ payload }, { put, call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(
          queryOrganizationRuleList,
          { page: 0, size: 10, ...payload },
          organizationId
        );
      } else {
        res = yield call(queryRuleList, { page: 0, size: 10, ...payload });
      }
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateRuleStateReducer',
          payload: {
            list: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },
    *queryCode({ payload }, { put, call }) {
      const res = yield call(queryCode, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'setCodeReducer',
          payload: {
            [payload.lovCode]: response,
          },
        });
      }
    },
    *createRule({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(createOrganizationRule, data, organizationId);
      } else {
        res = yield call(createRule, data, organizationId);
      }
      return getResponse(res);
    },
    *updateRule({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(updateOrganizationRule, data, organizationId);
      } else {
        res = yield call(updateRule, data);
      }
      return getResponse(res);
    },
    *createRange({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(createOrganizationRange, data, organizationId);
      } else {
        res = yield call(createRange, data);
      }
      return getResponse(res);
    },
    *updateRange({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(updateOrganizationRange, data, organizationId);
      } else {
        res = yield call(updateRange, data);
      }
      return getResponse(res);
    },
    *deleteRule({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(deleteOrganizationRule, payload, organizationId);
      } else {
        res = yield call(deleteRule, payload);
      }
      return getResponse(res);
    },
    *deleteRange({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(deleteOrganizationRange, payload, organizationId);
      } else {
        res = yield call(deleteRange, payload);
      }
      return getResponse(res);
    },
    *addPermissionRel({ data }, { call }) {
      const res = yield call(addPermissionRel, data);
      return getResponse(res);
    },
    *queryPermissionRel({ rangeId }, { call }) {
      const res = yield call(queryPermissionRel, rangeId);
      return getResponse(res);
    },
    *deletePermissionRel({ payload }, { call }) {
      const res = yield call(deletePermissionRel, payload);
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
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    updateRangeStateReducer(state, { payload }) {
      return {
        ...state,
        range: {
          ...state.range,
          ...payload,
        },
      };
    },
    updateRuleStateReducer(state, { payload }) {
      return {
        ...state,
        rule: {
          ...state.rule,
          ...payload,
        },
      };
    },
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
  },
};
