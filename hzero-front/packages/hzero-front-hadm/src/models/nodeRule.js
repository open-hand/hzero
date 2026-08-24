/**
 * model 节点组规则
 * @date: 2018-9-6
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  fetchNodeRule,
  deleteTenant,
  fetchTenantList,
  fetchUrlList,
  fetchUserList,
  updateNodeRule,
  createNodeRule,
  enabledNodeRule,
  disabledNodeRule,
  fetchNodeRuleDetails,
  deleteNodeRule,
  fetchTenantLovList,
} from '../services/nodeRuleService';

export default {
  namespace: 'nodeRule',

  state: {
    nodeRuleList: [],
    nodeRuleDetails: {},
    tenantList: {}, // 租户列表
    userList: [], // 用户列表
    urlList: [], // url列表
    nodeRuleTenantUserList: [], // 选择的用户列表
    selectedUserRowKeys: [], // 选择的用户列表keys
    nodeRuleTenantUrlList: [], // 选择的url列表
    selectedUrlRowKeys: [], // 选择的url列表keys
    productWithEnvList: [], // 产品及环境列表
    isEditSave: false, // 是否编辑
    pagination: {}, // 分页对象
    tenantLovList: {}, // 租户lov数据
  },

  effects: {
    // 查询租户 Lov
    * fetchTenantLovList({ payload }, { call, put }) {
      const res = yield call(fetchTenantLovList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            tenantLovList: list,
          },
        });
      }
      return list;
    },

    // 查询列表数据
    * fetchNodeRule({ payload }, { call, put }) {
      const res = yield call(fetchNodeRule, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            nodeRuleList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 查询详情
    * fetchNodeRuleDetails({ payload }, { call, put }) {
      const res = yield call(fetchNodeRuleDetails, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            nodeRuleDetails: list,
          },
        });
      }
      return list;
    },

    // 查询租户列表
    * fetchTenantList({ payload }, { call, put }) {
      const res = yield call(fetchTenantList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            tenantList: list,
          },
        });
      }
      return list;
    },
    // 查询url列表
    * fetchUrlList({ payload }, { call, put }) {
      const res = yield call(fetchUrlList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            urlList: list,
          },
        });
      }
      return list;
    },
    // 查询用户列表
    * fetchUserList({ payload }, { call, put }) {
      const res = yield call(fetchUserList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            userList: list,
          },
        });
      }
      return list;
    },
    // 删除节点组规则
    * deleteNodeRule({ payload }, { call }) {
      const res = yield call(deleteNodeRule, payload);
      return getResponse(res);
    },
    // 更新
    * updateNodeRule({ payload }, { call }) {
      const res = yield call(updateNodeRule, payload);
      return getResponse(res);
    },
    // 新建
    * createNodeRule({ payload }, { call }) {
      const res = yield call(createNodeRule, payload);
      return getResponse(res);
    },
    // 启用
    * enabledNodeRule({ payload }, { call }) {
      const res = yield call(enabledNodeRule, payload);
      return getResponse(res);
    },
    // 禁用
    * disabledNodeRule({ payload }, { call }) {
      const res = yield call(disabledNodeRule, payload);
      return getResponse(res);
    },
    // 删除租户
    * deleteTenant({ payload }, { call }) {
      const res = yield call(deleteTenant, payload);
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
