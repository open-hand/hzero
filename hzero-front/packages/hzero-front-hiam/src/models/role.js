/**
 * @date 2018-07-26
 * @author LJ <jun.li06@hand-china.com>
 */

import { isEmpty } from 'lodash';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { getResponse, createPagination, getCurrentOrganizationId } from 'utils/utils';
import {
  queryAdminRoles,
  queryCreateRolesSubtree,
  queryInheritRolesTree,
  queryCode,
  queryRole,
  enableRole,
  queryLabels,
  queryPermissionsAll,
  queryOrganizationPermissionsAll,
  queryRevokedPermissionsAll,
  queryOrganizationRevokedPermissionsAll,
  createRole,
  createOrganizationRole,
  editRole,
  editOrganizationRole,
  copyRole,
  copyOrganizationRole,
  inheritRole,
  inheritOrganizationRole,
  queryLevelPermissions,
  queryOrganizationLevelPermissions,
  unrevoke,
  unrevokeOrganization,
  queryRoleAuth,
  saveRoleAuth,
  deleteRoleAuth,
} from '../services/roleService';

const tableState = {
  list: [],
  pagination: {
    pageSize: 10,
    total: 0,
    current: 1,
  },
};

function defineProperty(obj, property, value) {
  Object.defineProperty(obj, property, {
    value,
    writable: true,
    enumerable: false,
    configurable: true,
  });
}

export default {
  namespace: 'role',
  state: {
    code: {},
    adminRoleList: [],
    currentAdminRole: {},
    rolesList: {
      managedRoleList: [],
      awardedRoleList: [],
    },
    roleDetail: {
      form: {},
      permissions: {
        ...tableState,
      },
      revokedPermissions: {
        ...tableState,
      },
      permissionsDrawer: {
        ...tableState,
      },
    },
    roleAuth: {},
    roleAuthScopeCode: [],
    roleAuthScopeTypeCode: [],
  },
  effects: {
    * queryAdminRoles(action, { put, call }) {
      const response = yield call(queryAdminRoles);
      if (response && !response.failed) {
        yield put({
          type: 'updateStateReducer',
          payload: {
            adminRoleList: response.map(n => ({
              value: n.id,
              title: n.name,
              assignLevel: n.assignLevel,
              assignLevelValue: n.assignLevelValue,
              assignLevelValueMeaning: n.assignLevelValueMeaning,
              level: n.level,
            })),
          },
        });
      }
    },
    * queryManagedRoleList({ payload }, { put, call }) {
      const response = yield call(queryCreateRolesSubtree, payload);
      if (response && !response.failed) {
        const content = isEmpty(response) ? [] : [response];
        const getList = collections => {
          const dataSource = collections || content;
          return dataSource.map(n => {
            const m = n;
            m.key = m.id;
            if (isEmpty(m.childRoles)) {
              return m;
            } else {
              m.children = getList(m.childRoles);
              return m;
            }
          });
        };
        const managedRoleList = getList() || [];
        yield put({
          type: 'updateStateReducer',
          payload: {
            rolesList: {
              managedRoleList,
            },
          },
        });
      }
    },
    * queryAwardedRoleList({ payload }, { put, call }) {
      const response = yield call(queryInheritRolesTree, payload);
      if (response && !response.failed) {
        const setAwardedRoleListChildren = list =>
          list.map(n => {
            const item = n;
            if (!isEmpty(n.inheritedRole)) {
              defineProperty(item, 'children', [{ ...n.inheritedRole }]);
            }
            if (!isEmpty(item.children)) {
              item.children = setAwardedRoleListChildren(item.children);
            }
            return item;
          });
        const awardedRoleList = setAwardedRoleListChildren(
          (response || []).map(n => {
            const item = {
              key: n.id,
              ...n,
              inherited: !isEmpty(n.inheritedRole) ? n.inheritedRole.name : '',
            };
            return item;
          })
        );

        yield put({
          type: 'updateStateReducer',
          payload: {
            rolesList: {
              awardedRoleList,
            },
          },
        });
      }
    },
    * setRoleEnabled({ payload }, { call }) {
      const response = yield call(enableRole, payload);
      return response && !response.failed;
    },
    * queryCode({ payload }, { put, call }) {
      const response = yield call(queryCode, payload);
      if (response && !response.failed) {
        yield put({
          type: 'setCodeReducer',
          payload: {
            [payload.lovCode]: response,
          },
        });
      }
    },
    * queryRole({ payload }, { put, call }) {
      const response = yield call(queryRole, payload);
      if (response && !response.failed) {
        yield put({
          type: 'updateRoleDetailReducer',
          payload: {
            form: response,
          },
        });
      }
    },
    * queryLabels({ payload }, { call }) {
      const response = yield call(queryLabels, payload);
      return response && !response.failed ? response : [];
    },
    * queryPermissionsAll({ payload }, { put, call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(queryOrganizationPermissionsAll, payload, organizationId);
      } else {
        response = yield call(queryPermissionsAll, payload);
      }
      if (response && !response.failed) {
        yield put({
          type: 'updateRoleDetailReducer',
          payload: {
            permissions: {
              list: response || [],
              pagination: createPagination({ number: 0, totalElements: response.length }),
            },
          },
        });
      }
    },
    * queryLevelPermissions({ id, level, params = {} }, { call, put }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (organizationId !== 0) {
        res = yield call(queryOrganizationLevelPermissions, id, level, params, organizationId);
      } else {
        res = yield call(queryLevelPermissions, id, level, params);
      }

      const response = getResponse(res);
      if (response && !response.failed) {
        const { content } = response;
        const pagination = createPagination(response);
        yield put({
          type: 'updateRoleDetailPermissionsDrawerReducer',
          payload: {
            list: content,
            pagination,
          },
        });
      }
    },
    * queryRevokedPermissionsAll({ roleId, payload }, { put, call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(
          queryOrganizationRevokedPermissionsAll,
          roleId,
          payload,
          organizationId
        );
      } else {
        response = yield call(queryRevokedPermissionsAll, roleId, payload);
      }
      if (response && !response.failed) {
        const { content } = response;
        yield put({
          type: 'updateRoleDetailReducer',
          payload: {
            revokedPermissions: {
              list: content.map(n => ({ key: n.id, ...n })) || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },
    * createRole({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(createOrganizationRole, payload, organizationId);
      } else {
        response = yield call(createRole, payload);
      }
      return getResponse(response);
    },
    * editRole({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(editOrganizationRole, payload, organizationId);
      } else {
        response = yield call(editRole, payload);
      }
      return getResponse(response);
    },
    * copyRole({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(copyOrganizationRole, payload, organizationId);
      } else {
        response = yield call(copyRole, payload);
      }
      return getResponse(response);
    },
    * inheritRole({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(inheritOrganizationRole, payload, organizationId);
      } else {
        response = yield call(inheritRole, payload);
      }
      return getResponse(response);
    },
    * unrevoke({ roleId, payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (organizationId !== 0) {
        response = yield call(unrevokeOrganization, roleId, payload, organizationId);
      } else {
        response = yield call(unrevoke, roleId, payload);
      }
      return response && !response.failed;
    },
    * queryRoleAuthScopeCode(_, { call, put }) {
      const res = yield call(queryIdpValue, 'HIAM.AUTHORITY_SCOPE_CODE');
      const roleAuthScopeCode = getResponse(res);
      yield put({
        type: 'updateStateReducer',
        payload: { roleAuthScopeCode },
      });
      return roleAuthScopeCode;
    },
    * queryRoleAuthScopeTypeCode(_, { call, put }) {
      const res = yield call(queryIdpValue, 'HIAM.AUTHORITY_TYPE_CODE');
      const roleAuthScopeTypeCode = getResponse(res);
      yield put({
        type: 'updateStateReducer',
        payload: { roleAuthScopeTypeCode },
      });
      return roleAuthScopeTypeCode;
    },
    * queryRoleAuth({ payload }, { call, put }) {
      const res = yield call(queryRoleAuth, payload);
      const roleAuth = getResponse(res);
      yield put({
        type: 'updateStateReducer',
        payload: { roleAuth },
      });
      return roleAuth;
    },
    * saveRoleAuth({ payload }, { call }) {
      const res = yield call(saveRoleAuth, payload);
      return getResponse(res);
    },
    * deleteRoleAuth({ payload }, { call }) {
      const res = yield call(deleteRoleAuth, payload);
      return getResponse(res);
    },
  },
  reducers: {
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
    updateRoleDetailReducer(state, { payload }) {
      return {
        ...state,
        roleDetail: {
          ...state.roleDetail,
          ...payload,
        },
      };
    },
    initRoleDetailReducer(state) {
      return {
        ...state,
        roleDetail: Object.assign(state.roleDetail, {
          form: {},
          permissions: {
            ...tableState,
          },
          revokedPermissions: {
            ...tableState,
          },
        }),
      };
    },
    updateRoleDetailPermissionsDrawerReducer(state, { payload }) {
      return {
        ...state,
        roleDetail: {
          ...state.roleDetail,
          permissionsDrawer: {
            ...state.roleDetail.permissionsDrawer,
            ...payload,
          },
        },
      };
    },
  },
};
