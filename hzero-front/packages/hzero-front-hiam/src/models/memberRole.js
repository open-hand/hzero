/**
 * @date 2018-07-23
 * @author  LJ <jun.li06@hand-china.com>
 */
import { isEmpty } from 'lodash';
import uuidv1 from 'uuid/v1';
import { getResponse, getCurrentOrganizationId, createPagination } from 'utils/utils';
import {
  queryMembers,
  queryRolesLevelUserCount,
  queryRoles,
  // queryUser,
  queryCreateRolesSublist,
  queryCode,
  deleteMember,
  deleteOrganizationMember,
  batchAssign,
  queryHrunitsTree,
} from '../services/memberRoleService';

const tableState = {
  list: [],
  pagination: {
    pageSize: 10,
    total: 0,
    current: 1,
  },
};

export default {
  namespace: 'memberRole',
  state: {
    code: {},
    currentAdminRole: {},
    members: {
      ...tableState,
    },
    roles: {
      list: [],
    },
    createRolesSublist: [],
    hrunitsTree: [],
  },
  effects: {
    * queryMembers({ payload }, { put, call }) {
      const organizationId = getCurrentOrganizationId();
      const response = yield call(queryMembers, payload, organizationId);
      if (response && !response.failed) {
        const { content } = response;
        const pagination = createPagination(response);

        yield put({
          type: 'updateStateReducer',
          payload: {
            members: {
              list: content || [],
              pagination,
            },
          },
        });
      }
    },
    * queryRolesLevelUserCount({ payload }, { call, put }) {
      const organizationId = getCurrentOrganizationId();
      const response = yield call(queryRolesLevelUserCount, payload, organizationId);
      yield put({
        type: 'updateStateReducer',
        payload: {
          roles: {
            list: getResponse(response) || [],
          },
        },
      });
    },
    * setRoleMember({ payload }, { call, put }) {
      const organizationId = getCurrentOrganizationId();
      const res = yield call(queryRoles, { roleId: payload }, organizationId);
      const response = getResponse(res) || {};
      const { content = [] } = response;

      yield put({
        type: 'updateMemberNodeChildrenReducer',
        payload: {
          id: payload,
          children: content.map(n => ({
            ...n,
            name: n.name || n.loginName,
            key: `member-${uuidv1()}`,
            roleId: payload,
          })),
        },
      });
    },
    * queryMember({ payload }, { call }) {
      const res = yield call(queryMembers, payload);
      const response = getResponse(res) || {};
      const { content = [] } = response;
      return content[0];
    },
    * queryRoles({ payload }, { call }) {
      const res = yield call(queryRoles, payload);
      return getResponse(res);
    },
    // *queryUser({ payload }, { call }) {
    //   const organizationId = getCurrentOrganizationId();
    //   const res = yield call(queryUser, payload, organizationId);
    //   return res;
    // },
    * queryCreateRolesSublist({ payload }, { call, put }) {
      const organizationId = getCurrentOrganizationId();
      const res = yield call(queryCreateRolesSublist, payload, organizationId);
      const response = getResponse(res);
      yield put({
        type: 'updateStateReducer',
        payload: {
          createRolesSublist: response || [],
        },
      });
    },
    * queryCode({ payload }, { put, call }) {
      const response = yield call(queryCode, payload);
      if (response && !response.failed) {
        yield put({
          type: 'setCodeReducer',
          payload: {
            [payload.lovCode]: response || [],
          },
        });
        return response;
      }
    },
    * createMember({ memberIds, params, isEdit }, { call }) {
      const organizationId = getCurrentOrganizationId();
      const res = yield call(batchAssign, memberIds, params, isEdit, organizationId);
      return getResponse(res);
    },
    * deleteMember({ params }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (organizationId !== 0) {
        res = yield call(deleteOrganizationMember, params, organizationId);
      } else {
        res = yield call(deleteMember, params);
      }
      return res;
    },
    * queryHrunitsTree({ organizationId, payload }, { call, put }) {
      const res = yield call(queryHrunitsTree, payload, organizationId);
      const response = getResponse(res);
      yield put({
        type: 'updateStateReducer',
        payload: {
          hrunitsTree: !isEmpty(response) ? response : [],
        },
      });
    },
  },
  reducers: {
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    updateMemberNodeChildrenReducer(state, { payload }) {
      const { id, children } = payload;
      return {
        ...state,
        roles: {
          list: state.roles.list.map(n => {
            const m = n;
            if (m.id === id) {
              m.children = children;
            }
            return m;
          }),
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
