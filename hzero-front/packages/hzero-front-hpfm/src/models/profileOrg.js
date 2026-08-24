/**
 * profileOrg.js
 * @date 2018/10/11
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  profileOrgQueryPage,
  profileOrgRemoveOne,
  profileOrgQueryProfileValue,
  profileValueOrgRemoveOne,
  profileOrgSaveOne,
  // profileSiteQueryProfileValue,
} from '../services/profileService';

export default {
  namespace: 'profileOrg',
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
          // 应用维度
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
    *profileFetchList(
      {
        payload: { organizationId, payload },
      },
      { call, put }
    ) {
      const res = getResponse(yield call(profileOrgQueryPage, organizationId, payload));
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
    *profileRemoveOne(
      {
        payload: { organizationId, payload },
      },
      { call }
    ) {
      return getResponse(yield call(profileOrgRemoveOne, organizationId, payload));
    },
    *profileSave(
      {
        payload: { organizationId, payload },
      },
      { call }
    ) {
      return getResponse(yield call(profileOrgSaveOne, organizationId, payload));
    },
    *openNewModal(_, { put }) {
      yield put({
        type: 'updateState',
        payload: {
          editModalVisible: true,
          profileValue: [],
          isCreate: true,
          editModalEditable: true,
        },
      });
    },
    *openEditModal(
      {
        payload: { organizationId, payload },
      },
      { call, put }
    ) {
      const profileValue = getResponse(
        yield call(profileOrgQueryProfileValue, organizationId, payload)
      );
      if (profileValue) {
        yield put({
          type: 'updateState',
          payload: {
            editModalVisible: true,
            profileValue,
            isCreate: false,
            editModalEditable: true,
          },
        });
      }
    },
    *openViewModal(
      {
        payload: { organizationId, payload },
      },
      { call, put }
    ) {
      const profileValue = getResponse(
        yield call(profileOrgQueryProfileValue, organizationId, payload)
      );
      if (profileValue) {
        yield put({
          type: 'updateState',
          payload: {
            editModalVisible: true,
            profileValue,
            isCreate: false,
            editModalEditable: false,
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
          editModalEditable: true,
        },
      });
    },
    *profileValueRemove(
      {
        payload: { organizationId, payload },
      },
      { call }
    ) {
      return getResponse(yield call(profileValueOrgRemoveOne, organizationId, payload));
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
