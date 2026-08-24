/**
 * model 三方应用管理
 * @date: 2018-9-29
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';
import {
  fetchOpenAppList,
  deleteOpenApp,
  updateOpenApp,
  createOpenApp,
  enabledOpenApp,
  disabledOpenApp,
  fetchOpenAppDetail,
} from '../services/openAppService';

export default {
  namespace: 'openApp',

  state: {
    openAppList: [], // 列表数据
    openAppDetail: {}, // 详情数据
    pagination: {}, // 分页对象
    channelTypes: [], // 渠道列表
    codeList: [], // 应用编码列表
  },

  effects: {
    // *init(_, { call, put }) {
    //   const res = yield call(queryUnifyIdpValue, 'HIAM.CHANNEL');
    //   const data = getResponse(res);
    //   if (data) {
    //     yield put({
    //       type: 'updateState',
    //       payload: {
    //         channelTypes: data,
    //       },
    //     });
    //   }
    // },

    * init(_, { put, call }) {
      const list = getResponse(
        yield call(queryMapIdpValue, {
          channelTypes: 'HIAM.CHANNEL',
          codeList: 'HIAM.OPEN_APP_CODE',
        })
      );
      if (list) {
        const { channelTypes, codeList } = list;
        yield put({
          type: 'updateState',
          payload: {
            channelTypes,
            codeList,
          },
        });
      }
    },
    // 获取列表信息
    * fetchOpenAppList({ payload }, { call, put }) {
      const res = yield call(fetchOpenAppList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            openAppList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },

    // 查询详情
    * fetchOpenAppDetail({ payload }, { call, put }) {
      const res = yield call(fetchOpenAppDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            openAppDetail: list,
          },
        });
      }
      return list;
    },

    // 更新
    * updateOpenApp({ payload }, { call }) {
      const res = yield call(updateOpenApp, payload);
      return getResponse(res);
    },
    // 新建
    * createOpenApp({ payload }, { call }) {
      const res = yield call(createOpenApp, payload);
      return getResponse(res);
    },
    // 启用
    * enabledOpenApp({ payload }, { call }) {
      const res = yield call(enabledOpenApp, payload);
      return getResponse(res);
    },
    // 禁用
    * disabledOpenApp({ payload }, { call }) {
      const res = yield call(disabledOpenApp, payload);
      return getResponse(res);
    },
    // 删除
    * deleteOpenApp({ payload }, { call }) {
      const res = yield call(deleteOpenApp, payload);
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
