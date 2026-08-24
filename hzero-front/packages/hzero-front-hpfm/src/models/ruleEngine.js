/**
 * @date 2018-09-28
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchTableList,
  createRuleEngine,
  editRuleEngine,
  deleteRuleEngine,
  searchDetail,
  testRuleEngine,
} from '../services/ruleEngineService';

export default {
  namespace: 'ruleEngine',
  state: {
    ruleEngineData: {}, // 查询数据列表
    scriptTypeCode: [], // 脚本类型
    detail: {}, // 详情数据
    testContent: undefined, // 测试结果
    pagination: {}, // 分页信息
    categoryList: [], // 脚本分类值集
  },
  effects: {
    // 获取表格数据
    *queryTableList({ payload }, { call, put }) {
      const res = yield call(fetchTableList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            ruleEngineData: list,
            pagination: createPagination(list),
          },
        });
      }
    },
    // 获取脚本类型
    *queryScriptTypeCode(_, { call, put }) {
      const scriptTypeCode = getResponse(yield call(queryIdpValue, 'HPFM.RULE_SCRIPT_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          scriptTypeCode,
        },
      });
    },
    // 获取脚本分类值集
    *queryCategoryList(_, { call, put }) {
      const categoryList = getResponse(yield call(queryIdpValue, 'HPFM.RULE_SCRIPT_CATEGORY'));
      yield put({
        type: 'updateState',
        payload: {
          categoryList,
        },
      });
    },
    // 编辑获取头
    *fetchDetail({ payload }, { call, put }) {
      let result = yield call(searchDetail, payload);
      result = getResponse(result);
      yield put({
        type: 'updateState',
        payload: {
          detail: result,
        },
      });
    },
    // 新建保存
    *createRuleEngine({ payload }, { call }) {
      const res = getResponse(yield call(createRuleEngine, { ...payload }));
      return getResponse(res);
    },
    // 编辑保存
    *editRuleEngine({ payload }, { call }) {
      const res = getResponse(yield call(editRuleEngine, { ...payload }));
      return getResponse(res);
    },
    // 删除
    *deleteRuleEngine({ payload }, { call }) {
      const res = getResponse(yield call(deleteRuleEngine, payload));
      return getResponse(res);
    },
    // 测试
    *testRuleEngine({ payload }, { call, put }) {
      let result = yield call(testRuleEngine, { ...payload });
      result = getResponse(result);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            testContent: result.content,
          },
        });
      }
      return result;
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
