import { getResponse, createPagination } from 'utils/utils';
import {
  queryPurchaseAgent,
  savePurchaseAgent,
  fetchUserList,
  deleteUser,
  updateUser,
} from '../services/purchaseAgentService';

export default {
  namespace: 'purchaseAgent',

  state: {
    list: {}, // 采购员信息列表
    pagination: {}, // 采购员列表分页数据
    userList: [], // 用户列表
    userPagination: {}, // 用户列表分页
  },
  effects: {
    // 获取采购员数据
    *queryPurchaseAgent({ payload }, { put, call }) {
      const res = yield call(queryPurchaseAgent, payload);
      const list = getResponse(res);
      const pagination = createPagination(list);
      if (list) {
        yield put({
          type: 'updateState',
          payload: { list, pagination },
        });
        return list;
      }
    },
    // 新增或更新采购员
    *savePurchaseAgent({ payload }, { call }) {
      const res = yield call(savePurchaseAgent, payload);
      return getResponse(res);
    },

    *fetchUserList({ payload }, { call, put }) {
      const res = yield getResponse(call(fetchUserList, payload));
      const userPagination = createPagination(res);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            userList: res.content,
            userPagination,
          },
        });
      }
    },

    // 更新用户数据
    *updateUser({ payload }, { call }) {
      const res = yield call(updateUser, payload);
      return getResponse(res);
    },

    // 删除用户数据
    *deleteUser({ payload }, { call }) {
      const res = yield call(deleteUser, payload);
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
