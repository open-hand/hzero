/**
 * model - 系统消息、平台公告卡片
 * @date: 2019-08-28
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import { getResponse } from 'utils/utils';
import {
  changeRead,
  queryAnnouncement,
  queryUserMessage,
} from '../../services/cards/messageService';

export default {
  namespace: 'messageCard',
  state: {
    systemMessageList: [], // 系统消息
    announcementList: [], // 公告消息
    notificationList: [], // 系统通知
  },

  effects: {
    // #region hzero
    // 查询系统消息
    * hzeroQueryUserMessage({ payload }, { call, put }) {
      const data = getResponse(yield call(queryUserMessage, payload));
      if (data) {
        const { userMessageTypeCode } = payload;
        const obj =
          userMessageTypeCode === 'MSG'
            ? { systemMessageList: data.content }
            : { notificationList: data.content };
        yield put({
          type: 'updateState',
          payload: {
            ...obj,
          },
        });
      }
      return data;
    },
    // 查询平台公告
    * hzeroQueryAnnouncement({ payload }, { call, put }) {
      const data = getResponse(yield call(queryAnnouncement, payload));
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            announcementList: data.content,
          },
        });
      }
      return data;
    },
    // 系统消息变为已读
    * hzeroChangeRead({ payload }, { call }) {
      const response = yield call(changeRead, payload);
      return getResponse(response);
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
