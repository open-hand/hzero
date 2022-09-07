/**
 * profile.js
 * @date 2018/10/11
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  profileSiteQueryPage,
  profileSiteRemoveOne,
  profileSiteQueryProfileValue,
  profileValueSiteRemoveOne,
  profileSiteSaveOne,
} from '../services/profileService';

export default {
  namespace: 'profile',
  state: {
    enumMap: {},
    // 存储 当前页 所有的 配置项
    list: [],
    pagination: {},
    // 编辑框 是否显示
    editModalVisible: false,
    // 配置维护详情
    profileValue: {},
  },
  effects: {
    // 获取值集 并存入 enumMap
    *fetchBatchEnums(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          // 配置维护应用层级
          levelCode: 'HPFM.PROFILE.LEVEL_CODE',
        })
      );
      if (res) {
        yield put({
          type: 'updateState',
          payload: { enumMap: res },
        });
      }
    },
    // 获取配置数据
    *profileFetchList({ payload }, { call, put }) {
      const res = getResponse(yield call(profileSiteQueryPage, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            list: res.content,
            pagination: createPagination(res),
          },
        });
      }
    },
    *profileRemoveOne({ payload }, { call }) {
      return getResponse(yield call(profileSiteRemoveOne, payload));
    },
    *profileSave({ payload }, { call }) {
      return getResponse(yield call(profileSiteSaveOne, payload));
    },
    *openNewModal(_, { put }) {
      yield put({
        type: 'updateState',
        payload: {
          editModalVisible: true,
          profileValue: [],
          isCreate: true,
        },
      });
    },
    *openEditModal({ payload }, { call, put }) {
      const profileValue = getResponse(yield call(profileSiteQueryProfileValue, payload));
      if (profileValue) {
        yield put({
          type: 'updateState',
          payload: {
            editModalVisible: true,
            profileValue,
            isCreate: false,
          },
        });
      }
    },
    *closeModal(_, { put }) {
      yield put({
        type: 'updateState',
        payload: {
          editModalVisible: false,
          profileValue: [],
          isCreate: true,
        },
      });
    },
    *profileValueRemove({ payload }, { call }) {
      return getResponse(yield call(profileValueSiteRemoveOne, payload));
    },
  },
  reducers: {
    // 更新状态树
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
