/**
 * model 并发管理器/请求定义（并发程序）
 * @date: 2018-9-10
 * @author: LYZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchConcurrentList,
  createConcurrent,
  updateConcurrent,
  fetchConcurrentDetail,
  deleteLine,
  fetchAssignedPermission,
  createPermission,
  updatePermission,
} from '../services/concurrentService';

export default {
  namespace: 'concurrent',
  state: {
    list: [], // 数据列表
    pagination: {}, // 分页器
    concurrentDetail: {}, // 详情数据
    line: [], // 条件行
    paramFormatList: [], // 参数格式
    editTypeList: [], // 编辑类型
  },
  effects: {
    * init(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          paramFormatList: 'HSDR.PARAM_FORMAT',
          editTypeList: 'HSDR.PARAM_EDIT_TYPE',
        })
      );
      const { paramFormatList, editTypeList } = res;
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            paramFormatList,
            editTypeList,
          },
        });
      }
    },
    * fetchConcurrentList({ payload }, { call, put }) {
      let result = yield call(fetchConcurrentList, payload);
      result = getResponse(result);
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
    // 创建
    * createConcurrent({ payload }, { call }) {
      const res = yield call(createConcurrent, payload);
      return getResponse(res);
    },
    // 更新
    * updateConcurrent({ payload }, { call }) {
      const res = yield call(updateConcurrent, payload);
      return getResponse(res);
    },
    // 查询job详情
    * fetchConcurrentDetail({ payload }, { call, put }) {
      const res = yield call(fetchConcurrentDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            concurrentDetail: result,
          },
        });
      }
      return result;
    },

    // 删除行
    * deleteLine({ payload }, { call }) {
      const result = yield call(deleteLine, { ...payload });
      return getResponse(result);
    },

    // 获取已分配的权限
    * fetchAssignedPermission({ payload }, { call, put }) {
      let result = yield call(fetchAssignedPermission, payload);
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            permissionsList: result.content,
            permissionsPagination: createPagination(result),
          },
        });
      }
      return result;
    },
    // 创建权限
    * createPermission({ payload }, { call }) {
      const res = yield call(createPermission, payload);
      return getResponse(res);
    },
    // 更新权限
    * updatePermission({ payload }, { call }) {
      const res = yield call(updatePermission, payload);
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
