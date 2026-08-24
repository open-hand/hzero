/**
 * @date: 2018-09-24
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import {
  fetchPasswordPolicyList,
  updatePasswordPolicy,
  fetchUserCheckList,
  addUserPhoneCheckList,
  deleteUserPhoneCheckList,
  addUserEmailCheckList,
  deleteUserEmailCheckList,
} from '../services/passwordPolicyService';

export default {
  namespace: 'passwordPolicy',
  state: {
    passwordPolicyList: {},
    checkList: [],
    pagination: {},
  },
  effects: {
    // 获取密码策略数据
    *fetchPasswordPolicyList({ payload }, { call, put }) {
      const list = yield call(fetchPasswordPolicyList, payload);
      const res = getResponse(list);
      if (res) {
        yield put({
          type: 'updateState',
          payload: { passwordPolicyList: res },
        });
      }
      return res;
    },
    // 更新
    *updatePasswordPolicy({ payload }, { call }) {
      const res = yield call(updatePasswordPolicy, payload);
      return getResponse(res);
    },
    // 获取二次校验用户数据
    *fetchUserCheckList({ payload }, { call, put }) {
      const list = yield call(fetchUserCheckList, parseParameters(payload));
      const res = getResponse(list);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            checkList: res.content,
            pagination: createPagination(res),
          },
        });
      }
      return res;
    },
    // 新增指定用户
    *addUserPhoneCheckList({ payload }, { call }) {
      const res = yield call(addUserPhoneCheckList, payload);
      return getResponse(res);
    },
    // 删除指定用户
    *deleteUserPhoneCheckList({ payload }, { call }) {
      const res = yield call(deleteUserPhoneCheckList, payload);
      return getResponse(res);
    },
    // 新增指定用户
    *addUserEmailCheckList({ payload }, { call }) {
      const res = yield call(addUserEmailCheckList, payload);
      return getResponse(res);
    },
    // 删除指定用户
    *deleteUserEmailCheckList({ payload }, { call }) {
      const res = yield call(deleteUserEmailCheckList, payload);
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
