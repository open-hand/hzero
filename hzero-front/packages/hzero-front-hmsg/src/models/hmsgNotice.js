/**
 * model 公告管理
 * @date: 2018-8-6
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import {
  createPagination,
  getCurrentOrganizationId,
  getResponse,
  parseParameters,
} from 'utils/utils';
import {
  queryFileList,
  queryUnifyIdpValue,
  queryMapIdpValue,
  queryUUID,
  removeFileList,
} from 'hzero-front/lib/services/api';
import {
  createNotice,
  createReceiver,
  deleteNotice,
  fetchNotice,
  publicNotice,
  publishSystemNotice,
  queryNotice,
  queryNoticeType,
  queryReceiver,
  querySystemHistory,
  removeDraftReceive,
  revokeNotice,
  updateNotice,
  uploadImage,
} from '../services/hmsgNoticeService';

export default {
  namespace: 'hmsgNotice',

  state: {
    noticeList: [], // 公告列表数据
    pagination: {}, // 分页对象
    noticeDetail: {
      // 公告明细信息
      noticeContent: {
        noticeBody: '',
      },
    },
    noticeReceiverType: [], // 公告接受者类型
    noticeCategory: [], // 公告类别
    noticeStatus: [], // 公告状态
    noticeType: [], // 公告类型
    langList: [], // 语言列表
    noticeCascaderType: [], // 级联数据
    receiverRecordType: [], // 接收者类型
    systemNoticePublishModalVisible: false, // 发送系统消息公告
    systemNoticePublishRecord: {}, // 发送当前系统消息公告的记录
    systemNoticeHistoryDataSource: [], // 历史信息 数据
    systemNoticeHistoryPagination: false, // 历史信息 分页
    systemNoticeHistorySelectedRows: [], // 历史信息 选中数据
    systemNoticeHistorySelectedRowKeys: [], // 历史信息 选中数据key
    systemNoticeReceiveDataSource: [], // 接收信息 数据
    systemNoticeReceivePagination: false, // 接收信息 分页
  },

  effects: {
    // 获取初始化数据
    * init(_, { call, put }) {
      const langList = getResponse(yield call(queryUnifyIdpValue, 'HPFM.LANGUAGE'));
      const noticeType = getResponse(
        yield call(queryNoticeType, {
          'HMSG.NOTICE.NOTICE_TYPE': 1,
          'HMSG.NOTICE.NOTICE_TYPE.CH': 2,
        })
      );
      const noticeCascaderType = getResponse(
        yield call(queryNoticeType, {
          'HMSG.NOTICE.RECERVER_TYPE': 1,
          'HMSG.NOTICE.NOTICE_CATEGORY': 2,
        })
      );
      const { noticeReceiverType, noticeCategory, receiverRecordType, noticeStatus } = getResponse(
        yield call(queryMapIdpValue, {
          noticeReceiverType: 'HMSG.NOTICE.RECERVER_TYPE',
          noticeCategory: 'HMSG.NOTICE.NOTICE_CATEGORY',
          receiverRecordType: 'HMSG.NOTICE.RECEIVER_RECORD_TYPE',
          noticeStatus: 'HMSG.NOTICE.STATUS',
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          noticeReceiverType,
          noticeStatus,
          noticeType,
          noticeCascaderType,
          noticeCategory,
          langList,
          receiverRecordType, // 接收者类型
        },
      });
    },
    * fetchNotice({ payload }, { call, put }) {
      const res = yield call(fetchNotice, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            noticeList: list.content,
            pagination: createPagination(list),
          },
        });
      }
      return list;
    },
    // 创建公告信息
    * createNotice({ payload }, { call }) {
      const res = yield call(createNotice, payload);
      return getResponse(res);
    },
    // 更新公告信息
    * updateNotice({ payload }, { call }) {
      const res = yield call(updateNotice, payload);
      return getResponse(res);
    },
    // 删除公告信息
    * deleteNotice({ payload }, { call }) {
      const res = yield call(deleteNotice, payload);
      return getResponse(res);
    },
    // 查询单条公告信息
    * queryNotice({ payload }, { call, put }) {
      const res = yield call(queryNotice, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            noticeDetail: list,
          },
        });
      }
      return list;
    },
    // 发布公告信息
    * publicNotice({ payload }, { call }) {
      const res = yield call(publicNotice, payload);
      return getResponse(res);
    },
    // 撤销删除公告信息
    * revokeNotice({ payload }, { call }) {
      const res = yield call(revokeNotice, payload);
      return getResponse(res);
    },
    // 获取文件
    * queryFileList({ payload }, { call }) {
      const res = yield call(queryFileList, payload);
      return getResponse(res);
    },
    // 查询UUID
    * fetchUuid(_, { call }) {
      const organizationId = getCurrentOrganizationId();
      const res = yield call(queryUUID, { tenantId: organizationId });
      return getResponse(res);
    },
    // 删除文件
    * removeFile({ payload }, { call }) {
      const res = yield call(removeFileList, payload);
      return getResponse(res);
    },
    // 富文本上传图片
    * uploadImage({ payload, file }, { call }) {
      const res = yield call(uploadImage, payload, file);
      return res;
    },
    // 删除草稿中的 接收配置
    * removeDraftReceive({ payload }, { call }) {
      const { record } = payload;
      const res = yield call(removeDraftReceive, record);
      return getResponse(res);
    },
    // 查询历史发布信息
    * querySystemHistory({ payload }, { call, put }) {
      const { noticeId, query } = payload;
      const res = yield call(querySystemHistory, noticeId, query);
      const parseRes = getResponse(res);
      if (parseRes) {
        yield put({
          type: 'updateState',
          payload: {
            systemNoticeHistoryDataSource: parseRes.content,
            systemNoticeHistoryPagination: createPagination(parseRes),
            systemNoticeHistorySelectedRows: [],
            systemNoticeHistorySelectedRowKeys: [],
            systemNoticeReceiveDataSource: [],
            systemNoticeReceivePagination: false,
          },
        });
      }
    },
    // 查询 发布信息 对应的接收配置
    * queryReceiver({ payload }, { call, put }) {
      const { noticeId, query, prevSystemNoticeHistorySelectedRowKeys = [] } = payload;
      const {
        systemNoticeHistorySelectedRowKeys = prevSystemNoticeHistorySelectedRowKeys,
        systemNoticeHistorySelectedRows,
        ...relQuery
      } = query;
      if ((systemNoticeHistorySelectedRowKeys || []).length !== 0) {
        // 分页查询配置信息 或 选中 历史记录
        const res = yield call(
          queryReceiver,
          noticeId,
          relQuery,
          systemNoticeHistorySelectedRowKeys
        );
        const parseRes = getResponse(res);
        const partialState = {
          systemNoticeReceiveDataSource: parseRes.content,
          systemNoticeReceivePagination: createPagination(parseRes),
        };
        if (systemNoticeHistorySelectedRows) {
          partialState.systemNoticeHistorySelectedRows = systemNoticeHistorySelectedRows;
          partialState.systemNoticeHistorySelectedRowKeys = systemNoticeHistorySelectedRowKeys;
        }
        if (parseRes) {
          yield put({
            type: 'updateState',
            payload: partialState,
          });
        }
      } else {
        // 取消选中所有的历史信息
        yield put({
          type: 'updateState',
          payload: {
            systemNoticeReceiveDataSource: [], // 接收信息 数据
            systemNoticeReceivePagination: false, // 接收信息 分页
            systemNoticeHistorySelectedRows: [],
            systemNoticeHistorySelectedRowKeys: [],
          },
        });
      }
    },
    // 新建 接收配置
    * createReceiver({ payload }, { call }) {
      const { noticeId, records } = payload;
      const res = yield call(createReceiver, noticeId, records);
      return getResponse(res);
    },
    // 发布 SystemNotice
    * publishSystemNotice({ payload }, { call }) {
      const { noticeId, records } = payload;
      const res = yield call(publishSystemNotice, noticeId, records);
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
