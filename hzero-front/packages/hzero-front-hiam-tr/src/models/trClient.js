/**
 * client.js - 客户端 model
 * @date: 2018-12-24
 * @author: LZY <zhuyan.luo02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination, addItemsToPagination } from 'utils/utils';
import { queryIdpValue, getPublicKey } from 'hzero-front/lib/services/api';

import {
  fetchClientList,
  fetchDetail,
  checkClient,
  createClient,
  updateClient,
  // deleteClient,
  subAccountRoleQueryAll,
  subAccountOrgRoleCurrent,
  // saveRoleSet,
  deleteRoles,
  roleVisitCurrent,
  deleteVisitRoles,
  saveVisitRoleSet,
  changeStatus,
  saveRole,
} from '../services/trClientService';

export default {
  namespace: 'trClient',

  state: {
    clientList: [], // 列表数据
    pagination: {}, // 分页器
    detailData: {}, // 详情数据
    typeList: [], // 授权类型
    randomInfoData: [], // 需新建客户端时生成的随机数据
    ownedRoleList: [], // 拥有的角色
    paginationRole: {},
    publicKey: '', // 密码公钥
    visitRoleList: [],
    visitRolePagination: {},
  },

  effects: {
    // 获取授权类型信息
    *queryType(_, { call, put }) {
      const res = yield call(queryIdpValue, 'HIAM.GRANT_TYPE');

      const typeList = getResponse(res);
      yield put({
        type: 'updateState',
        payload: { typeList },
      });
    },

    // 查询Client列表数据
    *fetchClientList({ payload }, { call, put }) {
      const res = yield call(fetchClientList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            clientList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },
    // 查询详情
    *fetchDetail({ payload }, { call, put }) {
      const res = yield call(fetchDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            detailData: list,
          },
        });
      }
      return list;
    },
    // 校验
    *checkClient({ payload }, { call }) {
      const res = yield call(checkClient, payload);
      return getResponse(res);
    },
    // 创建
    *createClient({ payload }, { call }) {
      const res = yield call(createClient, payload);
      return getResponse(res);
    },
    // 更新LDAP
    *updateClient({ payload }, { call }) {
      const res = yield call(updateClient, payload);
      return getResponse(res);
    },
    // 删除
    // *deleteClient({ payload }, { call }) {
    //   const res = yield call(deleteClient, payload);
    //   return getResponse(res);
    // },

    // 当前登录用户所拥有的id
    *roleQueryAll({ payload }, { call, put }) {
      const res = yield call(subAccountRoleQueryAll, payload);
      const createSubRoles = getResponse(res);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            createSubRoles,
          },
        });
      }
      return createSubRoles;
    },
    // 查询当前用户所拥有的角色
    *roleCurrent({ payload }, { call, put }) {
      const res = yield call(subAccountOrgRoleCurrent, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            ownedRoleList: list.content, // list.content
            paginationRole: createPagination(list),
          },
        });
      }
      return list;
    },

    // 保存角色
    *saveRoleSet({ payload }, { put, select }) {
      // const res = yield call(saveRoleSet, payload);
      const state = yield select((st) => st.trClient);
      const { ownedRoleList, paginationRole } = state;
      const arr = payload.map((r) => {
        return { ...r, _status: 'create' };
      });
      // const ownedRoleLists = ownedRoleList.concat(arr);
      yield put({
        type: 'updateState',
        payload: {
          // ownedRoleList: ownedRoleLists, // list.content
          paginationRole: addItemsToPagination(
            payload.length,
            ownedRoleList.length,
            paginationRole
          ),
        },
      });
      return arr;
    },
    // 删除角色
    *deleteRoles({ payload }, { call }) {
      const { memberRoleList } = payload;
      const res = getResponse(yield call(deleteRoles, memberRoleList));
      return res;
    },

    // 查询当前用户可访问的角色
    *roleVisitCurrent({ payload }, { call, put }) {
      const res = yield call(roleVisitCurrent, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            visitRoleList: list.content, // list.content
            visitRolePagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 保存角色
    *saveVisitRoleSet({ payload }, { call }) {
      const res = yield call(saveVisitRoleSet, payload);
      return getResponse(res);
    },
    // 删除角色
    *deleteVisitRoles({ payload }, { call }) {
      const res = getResponse(yield call(deleteVisitRoles, payload));
      return res;
    },

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

    // 设置客户端是否启用
    *changeStatus({ payload }, { call }) {
      const response = yield call(changeStatus, payload);
      return response;
    },
    *fetchAssignRole({ payload }, { call }) {
      const res = getResponse(yield call(saveRole, payload));
      return getResponse(res);
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
