/*
 * @Author: your name
 * @Date: 2020-02-24 20:08:02
 * @LastEditTime: 2020-03-09 11:40:39
 * @LastEditors: your name
 * @Description: In User Settings Edit
 * @FilePath: \hzero-front\packages\hzero-front-hiam\src\models\securityGroupAuthority\secGrpAuthorityCompany.js
 */
/**
 * secGrpAuthorityCompany - 分配安全组-数据权限tab页 - 公司 - model
 * @date: 2019-12-23
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';
import { queryCompanyPermission } from '../../services/roleManagementService';

export default {
  namespace: 'secGrpAuthorityCompany',
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
