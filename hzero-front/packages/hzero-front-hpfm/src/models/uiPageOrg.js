/**
 * uiPageOrg
 * @date 2018/9/29
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import { forEach } from 'lodash';

import { createPagination, getResponse } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  uiPageOrgCreate,
  uiPageOrgQueryPaging,
  uiPageOrgUpdate,
  // detail
  uiPageOrgQueryDetail,
  uiPageOrgDetailUpdate,
} from '../services/uiPageOrgService';

export default {
  namespace: 'uiPageOrg',
  state: {
    // 列表
    list: {},
    pagination: false,
    // 详情
    config: {},
    // 值集
    detailIdp: {},
  },
  effects: {
    *fetchPageList({ payload }, { put, call }) {
      const { organizationId, params } = payload;
      const list = getResponse(yield call(uiPageOrgQueryPaging, organizationId, params));
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            list,
            pagination: createPagination(list),
          },
        });
      }
    },
    *listCreateOne({ payload }, { call }) {
      const { organizationId, data } = payload;
      return getResponse(yield call(uiPageOrgCreate, organizationId, data));
    },
    *listUpdateOne({ payload }, { call }) {
      const { organizationId, data } = payload;
      return getResponse(yield call(uiPageOrgUpdate, organizationId, data));
    },
    *fetchDetail({ payload }, { put, call }) {
      const { organizationId, pageCode } = payload;
      const res = yield call(uiPageOrgQueryDetail, organizationId, pageCode);
      const config = getResponse(res);
      const idpRes = yield call(queryIdpValue, 'HPFM.UI.COMPONENT');
      const componentType = getResponse(idpRes);
      // if (config) {
      //   const orgConfig = {...config};
      //   const tpls = orgConfig.fields || [];
      //   const newTpls = [];
      //   for (let tplIndex = 0; tplIndex < tpls.length; tplIndex += 1) {
      //     const tpl = tpls[tplIndex];
      //     const newTpl = {...tpl};
      //     const removeSiteFields = [];
      //     const noRemoveFields = [];
      //     newTpl.removeSiteFields = removeSiteFields;
      //     const fields = tpl.fields || [];
      //     switch (tpl.templateType) {
      //       case 'DynamicForm':
      //       case 'DynamicTable':
      //       case 'DynamicToolbar':
      //         for (let fieldIndex = 0; fieldIndex <
      //         fields.length; fieldIndex += 1) {
      //           const field = fields[fieldIndex];
      //           if (field.visiableFlag === 1) {
      //             noRemoveFields.push(field);
      //           } else if (field.siteFlag === 1) {
      //             removeSiteFields.push(field);
      //           }
      //         }
      //         newTpl.removeSiteFields = removeSiteFields;
      //         newTpl.fields = noRemoveFields;
      //         break;
      //       default:
      //         break;
      //     }
      //     newTpls.push(newTpl);
      //   }
      //   orgConfig.fields = newTpls;
      //   yield put({
      //     type: 'updateState',
      //     payload: {
      //       config: orgConfig,
      //     },
      //   });
      // }
      if (config) {
        yield put({
          type: 'updateState',
          payload: {
            config,
          },
        });
      }
      if (componentType) {
        const componentTypeMeaning = {};
        forEach(componentType, idp => {
          componentTypeMeaning[idp.value] = idp.meaning;
        });
        yield put({
          type: 'updateDetailIdp',
          payload: {
            componentType,
            componentTypeMeaning,
          },
        });
      }
      return config;
    },
    *detailUpdate({ payload }, { call }) {
      const { organizationId, config } = payload;
      const res = getResponse(yield call(uiPageOrgDetailUpdate, organizationId, config));
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
    updateDetailIdp(state, { payload }) {
      return {
        ...state,
        detailIdp: {
          ...state.detailIdp,
          ...payload,
        },
      };
    },
  },
};
