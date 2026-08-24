/**
 * Message API返回消息管理
 * @date: 2019-1-9
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryUnifyIdpValue, queryIdpValue } from 'hzero-front/lib/services/api';
import {
  deleteMessage,
  fetchMessageList,
  createMessage,
  updateMessage,
  getMessageDetail,
} from '../services/messageService';

export default {
  namespace: 'message',
  state: {
    messageList: [], // 消息列表
    languageList: [], // 语言列表
    pagination: {}, // 分页对象
    messageDetail: {}, // 查询列表
    messageType: [], // 类型列表
  },
  effects: {
    // 获取初始化数据
    *init(_, { call, put }) {
      const languageList = getResponse(yield call(queryUnifyIdpValue, 'HPFM.LANGUAGE'));
      const messageType = getResponse(yield call(queryIdpValue, 'HPFM.MESSAGE_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          languageList,
          messageType,
        },
      });
    },

    // 获取消息列表
    *fetchMessageList({ payload }, { call, put }) {
      const res = yield call(fetchMessageList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            messageList: list.content,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 查询消息列表
    *getMessageDetail({ payload }, { call, put }) {
      const res = yield call(getMessageDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            messageDetail: list,
          },
        });
      }
    },

    // 新增消息
    *createMessage({ payload }, { call }) {
      const res = yield call(createMessage, payload);
      return getResponse(res);
    },

    // 更新消息
    *updateMessage({ payload }, { call }) {
      const res = yield call(updateMessage, payload);
      return getResponse(res);
    },

    // 删除消息
    *deleteMessage({ payload }, { call }) {
      const res = yield call(deleteMessage, payload);
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
