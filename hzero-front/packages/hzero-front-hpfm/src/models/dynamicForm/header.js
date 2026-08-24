/**
 * 动态配置行
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchHeaderList,
  fetchHeaderById,
  createHeader,
  updateHeader,
  removeHeader,
} from '../../services/dynamicForm/headerService';

export default {
  namespace: 'header',

  state: {
    list: [],
    configGroupList: [],
    pagination: {},
    headerDetail: {},
  },

  effects: {
    // 查询配置分类
    *fetchConfigGroupList(_, { call, put }) {
      // 查询配置信息
      const res = yield call(queryUnifyIdpValue, 'HPFM.FORM_GROUP');
      const configGroupList = getResponse(res);
      if (configGroupList) {
        yield put({
          type: 'updateState',
          payload: {
            configGroupList,
          },
        });
      }
    },
    // 查询列表
    *fetchHeaderList({ payload }, { call, put }) {
      // 查询配置信息
      const res = yield call(fetchHeaderList, payload);
      const headerList = getResponse(res);
      if (headerList) {
        yield put({
          type: 'updateState',
          payload: {
            pagination: createPagination(headerList),
            list: headerList.content,
          },
        });
      }
    },
    // 通过 ID 查询
    *fetchHeaderById({ payload }, { call, put }) {
      const res = yield call(fetchHeaderById, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            headerDetail: list,
          },
        });
      }
    },
    // 创建
    *createHeader({ payload }, { call }) {
      const res = yield call(createHeader, payload);
      return getResponse(res);
    },
    // 更新
    *updateHeader({ payload }, { call }) {
      const res = yield call(updateHeader, payload);
      return getResponse(res);
    },
    // 删除
    *removeHeader({ payload }, { call }) {
      const res = yield call(removeHeader, payload);
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
