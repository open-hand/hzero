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
  fetchLineList,
  fetchLineById,
  createLine,
  updateLine,
  removeLine,
} from '../../services/dynamicForm/lineService';

export default {
  namespace: 'line',

  state: {
    list: [],
    keyTypeList: [],
    pagination: {},
    lineDetail: {},
  },

  effects: {
    // 查询字段配置类型
    *fetchKeyTypeList(_, { call, put }) {
      // 查询配置信息
      const res = yield call(queryUnifyIdpValue, 'HPFM.ITEM_TYPE');
      const keyTypeList = getResponse(res);
      if (keyTypeList) {
        yield put({
          type: 'updateState',
          payload: {
            keyTypeList,
          },
        });
      }
    },
    // 查询列表
    *fetchLineList({ payload }, { call, put }) {
      const res = yield call(fetchLineList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            pagination: createPagination(list),
            list: list.content,
          },
        });
      }
    },
    // 通过 ID 查询明细
    *fetchLineById({ payload }, { call, put }) {
      const res = yield call(fetchLineById, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            lineDetail: list,
          },
        });
      }
    },



    // 创建
    *createLine({ payload }, { call }) {
      const res = yield call(createLine, payload);
      return getResponse(res);
    },
    // 更新
    *updateLine({ payload }, { call }) {
      const res = yield call(updateLine, payload);
      return getResponse(res);
    },
    // 删除
    *removeLine({ payload }, { call }) {
      const res = yield call(removeLine, payload);
      return getResponse(res);
    },
    // 查询所有
    // *fetchAllLine({ payload }, { call }) {
    //   const res = yield call(fetchAllLine, payload);
    //   return getResponse(res);
    // },
    // // 通过Code查询所有
    // *fetchAllLineByCode({ payload }, { call }) {
    //   const res = yield call(fetchAllLineByCode, payload);
    //   return getResponse(res);
    // },
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
