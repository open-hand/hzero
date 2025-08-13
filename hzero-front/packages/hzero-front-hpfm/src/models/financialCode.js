/**
 * financialCode - 财务代码
 * @date: 2019-3-7
 * @author: lixiaolong <xiaolong.li02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2019, Hand
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { queryList, saveCreate, saveUpdate } from '../services/financialCodeService';

export default {
  namespace: 'financialCode',

  state: {
    dataSource: [], // 表格数据源
    pagination: {}, // 表格分页信息
    typeList: [], // 类型值集
  },

  effects: {
    // 获取类型值集
    *fetchValue({ payload }, { call, put }) {
      const { valueType } = payload;
      const res = getResponse(yield call(queryIdpValue, valueType));
      yield put({
        type: 'updateState',
        payload: {
          typeList: res,
        },
      });
    },
    // 获取表格数据
    *fetchList({ payload }, { call, put }) {
      const res = getResponse(yield call(queryList, payload));
      const markChildren = (array = [], parentEnableFlag) => {
        return array.map(item => {
          const data = { ...item, parentEnableFlag };
          if (Array.isArray(data.children) && data.children.length) {
            const parentEnable = data.enabledFlag;
            data.children = markChildren(data.children, parentEnable);
          }
          return data;
        });
      };
      if (res) {
        const markData = markChildren(res);
        yield put({
          type: 'updateState',
          payload: {
            dataSource: markData,
            pagination: createPagination(res),
          },
        });
      }
    },
    // 保存新增财务代码
    *saveCreate({ payload }, { call }) {
      const res = getResponse(yield call(saveCreate, payload));
      return res;
    },
    // 保存更新的财务代码
    *saveUpdate({ payload }, { call }) {
      const res = getResponse(yield call(saveUpdate, payload));
      return res;
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
