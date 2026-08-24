import { getResponse } from 'utils/utils';
import {
  queryTree,
  queryGroup,
  queryUnitDetails,
  queryCode,
  queryModule,
  saveFieldIndividual,
  deleteFieldIndividual,
  queryFieldMapping,
  queryConditions,
  queryRelatedUnits,
  querySelfValidator,
  saveSelfValidator,
  saveHeaderIndividual,
  querySameModelUnit,
  copyFiled,
  saveUnitConfigHeader,
} from '@/services/customizeConfigService';
import { queryMapIdpValue } from 'services/api';
import { omit } from 'lodash';

export default {
  namespace: 'configCustomize',
  state: {
    treeData: [],
    unitGroup: [],
    currentUnit: {
      config: {},
    },
    lineData: [],
    unitAlias: [],
    moduleList: [],
    codes: {
      dateFormat: [],
      whereOptions: [],
      renderOptions: [],
      fieldWidget: [],
      custType: [],
      custTypeObj: {},
      fieldType: [],
      fixed: [],
      fixedObj: {},
      fieldWidgetObj: {},
      unitType: [],
      unitTypeObj: {},
      condOptions: [],
    },

    // 高级校验-条件列表 & 校验器列表
    headerProps: {},
    conditionList: [],
    validatorList: [],

    // 默认值条件 & 默认值列表
    defaultValueProps: {},
    defaultConList: [],
    defaultValidList: [],
  },
  effects: {
    *queryCodes({ payload }, { call, put }) {
      const res = getResponse(yield call(queryMapIdpValue, payload));
      if (res) {
        const fixedObj = {};
        const fieldWidgetObj = {};
        const unitTypeObj = {};
        const custTypeObj = {};
        res.fixed.forEach((i) => {
          fixedObj[i.value] = i.meaning;
        });
        res.fieldWidget.forEach((i) => {
          fieldWidgetObj[i.value] = i.meaning;
        });
        res.unitType.forEach((i) => {
          unitTypeObj[i.value] = i.meaning;
        });
        res.custType.forEach((i) => {
          custTypeObj[i.value] = i.meaning;
        });
        yield put({
          type: 'updateState',
          payload: {
            codes: {
              ...res,
              fixedObj,
              fieldWidgetObj,
              unitTypeObj,
              custTypeObj,
            },
          },
        });
      }
    },
    *queryTree({ payload }, { call, put }) {
      const res = getResponse(yield call(queryTree, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            treeData: res,
          },
        });
        return res;
      }
    },
    *queryCode({ payload }, { call, put }) {
      const res = getResponse(yield call(queryCode, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: { codes: res },
        });
      }
    },
    *queryGroup({ payload }, { call, put }) {
      const res = getResponse(yield call(queryGroup, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: { unitGroup: res || [] },
        });
      }
      return res;
    },
    *queryUnitDetails({ payload }, { call, put }) {
      const res = getResponse(yield call(queryUnitDetails, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            currentUnit: res.unit || { config: {} },
            lineData: res.configFields || [],
            unitAlias: res.unitAlias || [],
          },
        });
      } else {
        yield put({
          type: 'updateState',
          payload: {
            currentUnit: { config: {} },
            lineData: [],
            unitAlias: [],
          },
        });
      }
      return res;
    },
    *queryModule({ payload }, { call, put }) {
      const res = getResponse(yield call(queryModule, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: { moduleList: res },
        });
      }
    },
    *queryFieldMapping({ payload }, { call }) {
      return getResponse(yield call(queryFieldMapping, payload));
    },
    *queryConditions({ payload }, { call }) {
      return getResponse(yield call(queryConditions, payload));
    },
    *queryRelatedUnits({ payload }, { call }) {
      return getResponse(yield call(queryRelatedUnits, payload));
    },
    *saveFieldIndividual({ payload }, { call }) {
      return getResponse(yield call(saveFieldIndividual, payload));
    },
    *saveHeaderIndividual({ payload }, { call }) {
      return getResponse(yield call(saveHeaderIndividual, payload));
    },
    *deleteFieldIndividual({ payload }, { call }) {
      return getResponse(yield call(deleteFieldIndividual, payload));
    },
    *querySelfValidator({ payload }, { call, put }) {
      const res = getResponse(yield call(querySelfValidator, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            headerProps: omit(res, ['lines', 'valids']),
            conditionList: res.lines || [],
            validatorList: res.valids || [],
          },
        });
      }
      return res;
    },
    *queryDefaultValueFx({ payload }, { call, put }) {
      const res = getResponse(yield call(querySelfValidator, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            defaultValueProps: omit(res, ['lines', 'valids']),
            defaultConList: res.lines || [],
            defaultValidList: res.valids || [],
          },
        });
      }
      return res;
    },
    *saveSelfValidator({ payload }, { call }) {
      return getResponse(yield call(saveSelfValidator, payload));
    },
    *fetchSameModelUnit({ params }, { call }) {
      return getResponse(yield call(querySameModelUnit, params));
    },
    *copyFiled({ payload }, { call }) {
      return getResponse(yield call(copyFiled, payload));
    },
    *saveUnitConfigHeader({ payload }, { call }) {
      return getResponse(yield call(saveUnitConfigHeader, payload));
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
