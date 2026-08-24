/*
 * accSecGrpAuthorityCompany - 子账户管理-分配安全组-数据权限tab页 - 公司 - model
 * @date: 2019-12-23
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';
import { queryCompanyPermission } from '../../services/subAccountOrgService';

export default {
  namespace: 'accSecGrpAuthorityCompany',
  state: {
    data: [],
    checkList: [],
    originList: [],
    expandedRowKeys: [],
  },
  effects: {
    * fetchAuthorityCompanyAndExpand({ payload }, { call, put }) {
      const response = yield call(queryCompanyPermission, payload);
      const data = getResponse(response);
      if (data) {
        const expandedRowKeys = data.originList && data.originList.map(list => list.id);
        yield put({
          type: 'queryCompany',
          payload: data,
        });
        yield put({
          type: 'updateExpanded',
          payload: expandedRowKeys,
        });
      }
    },
  },
  reducers: {
    queryCompany(state, action) {
      return {
        ...state,
        data: action.payload.treeList,
        checkList: action.payload.originList.filter(list => list.checkedFlag === 1),
        originList: action.payload.originList,
      };
    },
    updateExpanded(state, action) {
      return {
        ...state,
        expandedRowKeys: action.payload,
      };
    },
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
