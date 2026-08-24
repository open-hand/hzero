/**
 * 调整为多个类型，由于详情只会打开一个, 所以不需要调整结构
 * message    notice announce
 * 之前的消息  通知    公告
 * model - 站内消息
 * @date: 2018-8-9
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';

import { queryFileList } from 'hzero-front/lib/services/api';
import {
  changeRead,
  deleteMessage,
  queryMessage,
  queryMessageDetail,
} from '../services/userMessageService';

export default {
  namespace: 'userMessage',

  state: {
    // type: {
    //   dataSource: [], // 消息数据
    //   pagination: false, // 分页参数
    // },
    currentType: 'message', // 当前切换的tab
    messageDetail: {}, // 消息详情
    userMessageId: '',
  },

  effects: {
    // 查询数据
    * queryMessage({ payload }, { call, put }) {
      const response = yield call(queryMessage, payload);
      const messageData = getResponse(response);
      if (messageData) {
        const { type } = payload;
        yield put({
          type: 'updateState',
          payload: {
            [type]: {
              dataSource: messageData.content,
              pagination: createPagination(messageData),
            },
            currentType: type,
          },
        });
      }
    },
    // 获取文件
    * queryFileList({ payload }, { call }) {
      const res = yield call(queryFileList, payload);
      return getResponse(res);
    },
    // 改变已读
    * changeRead({ payload }, { call }) {
      const response = yield call(changeRead, payload);
      return getResponse(response);
    },
    // 删除消息
    * deleteMessage({ payload }, { call }) {
      const response = yield call(deleteMessage, payload);
      return getResponse(response);
    },
    // 查询消息详情
    * queryDetail({ payload }, { call, put }) {
      const response = yield call(queryMessageDetail, payload);
      const messageDetail = getResponse(response);
      if (messageDetail) {
        yield put({
          type: 'updateState',
          payload: { messageDetail },
        });
      }
      return messageDetail;
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
