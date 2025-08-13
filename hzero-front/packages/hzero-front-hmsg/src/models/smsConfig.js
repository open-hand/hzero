/**
 * @date: 2018-08-01
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue, getPublicKey } from 'services/api';

import {
  fetchSMSList,
  fetchServerType,
  createSMS,
  editSMS,
  deleteSMS,
  fetchFilterList,
  updateFilter,
  deleteFilter,
} from '../services/smsConfigService';

export default {
  namespace: 'smsConfig',
  state: {
    smsData: {}, // 查询数据列表
    serverTypeList: [], // 服务类型
    pagination: {}, // 分页器
    publicKey: '', // 密码公钥
    filterList: [], // 黑白名单数据列表
    filterPagination: {}, // 黑白名单分页
    enums: {}, // 国际冠码以及安全策略组
  },
  effects: {
    /**
     *  获取值集
     *  enums 国际冠码
     *  filterStrategyList  安全策略组，与邮箱公用值集
     * */
    *fetchEnums(_, { call, put }) {
      const enumsRes = yield call(queryMapIdpValue, {
        iddList: 'HPFM.IDD',
        filterStrategyList: 'HMSG.EMAIL.FILTER_STRATEGY',
      });
      const enums = getResponse(enumsRes);
      if (enums) {
        yield put({
          type: 'updateState',
          payload: {
            enums,
          },
        });
      }
    },
    // 获取短信数据
    *fetchSMSList({ payload }, { call, put }) {
      const res = yield call(fetchSMSList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            smsData: list,
            pagination: createPagination(list),
          },
        });
      }
    },
    // 获取服务类型
    *fetchServerType(_, { call, put }) {
      const response = yield call(fetchServerType);
      const list = getResponse(response);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            serverTypeList: list,
          },
        });
      }
    },
    // 新建保存
    *createSMS({ payload }, { call }) {
      const result = yield call(createSMS, { ...payload });
      return getResponse(result);
    },
    // 编辑保存
    *editSMS({ payload }, { call }) {
      const result = yield call(editSMS, { ...payload });
      return getResponse(result);
    },
    *deleteSMS({ payload }, { call }) {
      const result = yield call(deleteSMS, { ...payload });
      return getResponse(result);
    },
    *getPublicKey(_, { call, put }) {
      const res = yield call(getPublicKey);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            publicKey: res.publicKey,
          },
        });
      }
      return res;
    },
    // 获取黑白名单
    *fetchFilterList({ payload }, { call, put }) {
      const res = yield call(fetchFilterList, parseParameters(payload));
      const filter = getResponse(res);
      if (filter) {
        yield put({
          type: 'updateState',
          payload: {
            filterList: filter && filter.content,
            filterPagination: createPagination(filter),
          },
        });
      }
      return filter;
    },
    // 更新黑白名单
    *updateFilter({ payload }, { call }) {
      const res = yield call(updateFilter, payload);
      return getResponse(res);
    },
    // 删除黑白名单
    *deleteFilter({ payload }, { call }) {
      const res = yield call(deleteFilter, payload);
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
