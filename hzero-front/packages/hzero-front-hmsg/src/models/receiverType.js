/**
 * model 接收者类型维护
 * @date: 2018-7-31
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import {
  addReceiverType,
  assignListToReceiverType,
  fetchReceiverType,
  queryAssignedList,
  queryNoAssignUnitList,
  queryNoAssignUserGroupList,
  removeReceiverTypeList,
  updateReceiverType,
} from '../services/receiverTypeService';

/**
 * 定义接收者类型维护数据源及处理方法
 */
export default {
  namespace: 'receiverType', // model名称
  state: {
    list: [], // 数据展示列表
    pagination: {}, // 分页器
    typeModes: [], // 接收组模式 值集
    newTypeModes: [], // 接收组模式 值集
    extTypeList: [],
    assignDataSource: [],
    assignPagination: {},
  },
  effects: {
    *init(_, { call, put }) {
      const enumTypeMode = yield call(queryUnifyIdpValue, 'HMSG.RECEIVER.TYPE_MODE');
      const relEnumTypeMode = getResponse(enumTypeMode);
      if (relEnumTypeMode) {
        yield put({
          type: 'updateState',
          payload: {
            typeModes: relEnumTypeMode,
            newTypeModes: relEnumTypeMode.filter((t) => t.tag === 'ORG'),
          },
        });
      }
    },
    *initExtType(_, { call, put }) {
      const ext = yield call(queryUnifyIdpValue, 'HMSG.RECEIVER.ACCOUNT_TYPE');
      const extTypeList = getResponse(ext);
      if (extTypeList) {
        yield put({
          type: 'updateState',
          payload: {
            extTypeList,
          },
        });
      }
    },
    /**
     * 获取接收者类型数据
     */ *fetchReceiverType({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchReceiverType, payload));
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result.content,
            pagination: createPagination(result),
          },
        });
      }
    },
    /**
     * 更新接收者类型信息
     */ *updateType({ payload }, { call }) {
      const result = yield call(updateReceiverType, payload);
      return getResponse(result);
    },
    /**
     * 添加接收者类型信息
     */ *addType({ payload }, { call }) {
      const result = yield call(addReceiverType, payload);
      return getResponse(result);
    },
    // 接收者 模块配置
    *queryAssignedList({ payload }, { call }) {
      const { id, query } = payload;
      const result = yield call(queryAssignedList, id, query);
      return getResponse(result);
    },
    *assignListToReceiverType({ payload }, { call }) {
      const { id, records } = payload;
      const result = yield call(assignListToReceiverType, id, records);
      return getResponse(result);
    },
    *removeReceiverTypeList({ payload }, { call }) {
      const { id, records } = payload;
      const result = yield call(removeReceiverTypeList, id, records);
      return getResponse(result);
    },
    *queryNoAssignUnitList({ payload }, { call }) {
      const { id, query } = payload;
      const result = yield call(queryNoAssignUnitList, id, query);
      return getResponse(result);
    },
    *queryNoAssignUserGroupList({ payload }, { call }) {
      const { id, query } = payload;
      const result = yield call(queryNoAssignUserGroupList, id, query);
      return getResponse(result);
    },
  },
  reducers: {
    /**
     * 合并state状态数据,生成新的state
     */
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
