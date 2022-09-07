/**
 * ManualInspection 手工发票查验
 * @date: 2019-8-25
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getResponse } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';

import { create, getDetail, redirect } from '../services/manualInspectionService';

export default {
  namespace: 'manualInspection',
  state: {
    pagination: {}, // 分页参数
    manualInspectionList: [], // 配置列表
    manualInspectionDetail: [], // 配置详情
    typeList: [], // 发票类别
  },
  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      const { typeList } = res;
      yield put({
        type: 'updateState',
        payload: {
          typeList,
        },
      });
    },

    // 获取明细
    * getDetail({ payload }, { put, call }) {
      const res = yield call(getDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            manualInspectionDetail: data,
          },
        });
      }
      return data;
    },

    // 新建配置
    * create({ payload }, { call }) {
      const res = yield call(create, payload);
      return getResponse(res);
    },

    //
    * redirect({ payload }, { call }) {
      const res = yield call(redirect, payload);
      const result = URL.createObjectURL(res);
      return result;
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
