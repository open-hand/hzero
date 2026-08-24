/*
 * secGrpAuthorityPuragent - 分配安全组-数据权限tab页 - 采购员 - model
 * @date: 2019-12-23
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { createPagination, getResponse } from 'utils/utils';
import { queryData } from '../../services/roleManagementService';

export default {
  namespace: 'secGrpAuthorityPuragent',

  state: {
    list: [], // 请求查询到的数据
    pagination: {}, // 分页信息
  },
  effects: {
    * fetchAuthorityPuragent({ payload }, { call, put }) {
      const response = yield call(queryData, payload);
      const data = getResponse(response);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            list: data.secGrpDclLineList.content,
            pagination: createPagination(data.secGrpDclLineList),
          },
        });
      }
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
