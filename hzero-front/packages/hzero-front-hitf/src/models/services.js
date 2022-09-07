/**
 * @date 2018-09-25
 * @author LJ <jun.li06@hand-china.com>
 */

import { createPagination, getResponse } from 'utils/utils';
import { queryMapIdpValue, queryIdpValue, getPublicKey } from 'hzero-front/lib/services/api';
import {
  ORCH_ASSERTION_CONDITION,
  ORCH_ASSERTION_SUBJECT,
  SERVICE_STATUS,
  LOG_RECORD_TYPE,
} from '@/constants/CodeConstants';
import {
  create,
  createMonitor,
  createParams,
  createTestCase,
  deleteAlternative,
  deleteHeader,
  deleteLines,
  deleteParams,
  deleteTestCase,
  deleteTestCaseParams,
  deleteTestHistory,
  edit,
  executeTestCase,
  importService,
  queryAlternative,
  queryCode,
  queryDocument,
  queryInterfaceDetail,
  queryList,
  queryInvokeAddrList,
  queryMonitor,
  queryParams,
  queryReqDemo,
  queryTestCase,
  queryTestCaseDetail,
  queryTestCaseHistory,
  queryViewCode,
  queryParamsAndAlternative,
  queryDocumentView,
  saveAlternative,
  updateDocument,
  updateMonitor,
  updateParams,
  updateTestCaseParams,
  saveInterfaces,
  saveTestCaseParams,
  queryInterfacesListDetail,
  queryInternal,
  saveBatchInterfaces,
  testAuth,
  queryMappingClass,
  testMappingClass,
  recognizeParam,
  recognizeServiceParam,
  getTemplateServerDetail,
} from '../services/servicesService';

export default {
  namespace: 'services',
  state: {
    code: {},
    list: {}, // 列表数据
    enumMap: {}, // 值集
    documentData: {}, // 接口文档
    requestParamsAll: null, // 和请求相关的所有参数
    responseParamsAll: null, // 和响应相关的所有参数
    alternativeData: [], // 参数备选值列表
    testCaseList: {}, // 测试用例列表数据
    testCaseDetail: {}, // 测试用例详情
    testCaseParams: [],
    testCaseHistory: {}, // 测试历史
    reqDemo: '', // 请求示例
    viewCode: '', // 预览的代码
    paramsWithValues: {}, // 所有参数及备选值
    viewData: {}, // 接口文档预览数据
    mimeTypes: {}, // mimeTypes值集
    internalList: [], // 内部接口列表
    internalPagination: {}, // 内部接口分页
    publicKey: '', // 密码公钥
    // serviceTypes: [], // 服务类型值集、发布类型？
    // soapVersionTypes: [], // SOAP版本值集
    // requestTypes: [], // 请求方式值集
    // authTypes: [], // 认证方式值集
    // grantTypes: [], // 授权模式值集
    // wssPasswordTypes: [], // 加密方式
    // interfaceStatus: [], // 接口状态
    operatorList: [], // 操作符集合
    assertionSubjects: [], // 断言主题
    logTypes: [],
  },
  effects: {
    *queryIdpValue(params, { call, put }) {
      const enumMap = getResponse(
        yield call(queryMapIdpValue, {
          serviceTypes: 'HITF.SERVICE_TYPE',
          soapVersionTypes: 'HITF.SOAP_VERSION',
          requestTypes: 'HITF.REQUEST_METHOD',
          authTypes: 'HITF.AUTH_TYPE',
          grantTypes: 'HITF.GRANT_TYPE',
          statusList: SERVICE_STATUS,
          wssPasswordTypes: 'HITF.SOAP_WSS_PASSWORD_TYPE',
          interfaceStatus: 'HITF.INTERFACE_STATUS',
          requestHeaderTypes: 'HITF.HTTP_HEADER',
          respContentTypes: 'HITF.CONTENT_TYPE',
          paramValueTypes: 'HITF.PARAM_VALUE_TYPE',
          mimeTypes: 'HITF.MIME_TYPE',
          rawMimeTypes: 'HITF.RAW_MIME_TYPE',
          rootTypes: 'HITF.ROOT_TYPE',
          codeTypes: 'HITF.CODE_TYPE',
          usecaseTypes: 'HITF.USECASE_TYPE',
          serviceCategory: 'HITF.SERVICE_CATEGORY',
          passwordTypes: 'HITF.PASSWORD_ENCODE_TYPE',
          charsetList: 'HITF.CHARSET',
          oauthContentType: 'HITF.OAUTH_CONTENT_TYPE',
          operatorList: ORCH_ASSERTION_CONDITION,
          assertionSubjects: ORCH_ASSERTION_SUBJECT,
          logTypes: LOG_RECORD_TYPE,
        })
      );
      yield put({
        type: 'updateState',
        payload: {
          enumMap,
        },
      });
    },
    // 查询服务列表
    *queryList({ payload }, { call, put }) {
      const res = yield call(queryList, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            list: {
              dataSource: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },
    // 查询透传地址列表
    *queryInvokeAddrList({ payload }, { call }) {
      const res = yield call(queryInvokeAddrList, payload);
      return getResponse(res) || {};
    },
    // 修改服务
    *edit({ params }, { call }) {
      const res = getResponse(yield call(edit, params));
      return res;
    },
    // 新增服务
    *create({ params }, { call }) {
      const res = yield call(create, params);
      return getResponse(res);
    },
    // 删除服务
    *deleteHeader({ payload: { deleteRecord } }, { call }) {
      const res = getResponse(yield call(deleteHeader, deleteRecord));
      return res;
    },
    // 删除行
    *deleteLines({ interfaceIds }, { call }) {
      const res = getResponse(yield call(deleteLines, interfaceIds));
      return res;
    },
    // 查询行详情
    *queryInterfaceDetail({ payload }, { call }) {
      const { page = 0, size = 10, ...query } = payload;
      const res = getResponse(yield call(queryInterfaceDetail, { ...query, page, size }));
      return res;
    },
    // 导入服务
    *importService({ payload }, { call }) {
      const res = getResponse(yield call(importService, payload));
      return res;
    },

    // 查询接口文档
    *queryDocument({ payload }, { call, put }) {
      const res = yield call(queryDocument, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: { documentData: response },
        });
        return response;
      }
    },

    // 编辑接口文档
    *updateDocument({ payload }, { call }) {
      const res = yield call(updateDocument, payload);
      return getResponse(res);
    },

    // 查询请求示例
    *queryReqDemo({ payload }, { call, put }) {
      const res = yield call(queryReqDemo, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: { reqDemo: response.code },
        });
      }
    },

    // 查询参数
    *queryParams({ payload }, { call, put }) {
      const res = yield call(queryParams, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            requestParamsAll: response.request,
            responseParamsAll: response.response,
          },
        });
      }
    },

    // 新建参数
    *createParams({ payload }, { call }) {
      const res = yield call(createParams, payload);
      return getResponse(res);
    },

    // 编辑参数
    *updateParams({ payload }, { call }) {
      const res = yield call(updateParams, payload);
      return getResponse(res);
    },

    // 删除参数
    *deleteParams({ payload }, { call }) {
      const res = yield call(deleteParams, payload);
      return getResponse(res);
    },

    // 查询参数的所有备选值
    *queryAlternative({ payload }, { call, put }) {
      const res = yield call(queryAlternative, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            alternativeData: response,
          },
        });
      }
    },

    // 保存接口文档参数备选值
    *saveAlternative({ payload }, { call }) {
      const res = yield call(saveAlternative, payload);
      return getResponse(res);
    },

    // 删除接口文档参数备选值
    *deleteAlternative({ payload }, { call }) {
      const res = yield call(deleteAlternative, payload);
      return getResponse(res);
    },

    // 查询测试用例列表
    *queryTestCase({ payload }, { call, put }) {
      const res = yield call(queryTestCase, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            testCaseList: {
              list: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },

    // 新建测试用例
    *createTestCase({ payload }, { call }) {
      const res = yield call(createTestCase, payload);
      return getResponse(res);
    },

    // 查询测试用例详情
    *queryTestCaseDetail({ payload }, { call, put }) {
      const res = yield call(queryTestCaseDetail, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            testCaseDetail: response,
            testCaseParams: response.interfaceUsecaseParamList,
          },
        });
      }
    },

    // 更新测试用例参数
    *updateTestCaseParams({ payload }, { call }) {
      const res = yield call(updateTestCaseParams, payload);
      return getResponse(res);
    },

    // 保存测试用例参数
    *saveTestCaseParams({ payload }, { call }) {
      const res = yield call(saveTestCaseParams, payload);
      return getResponse(res);
    },

    // 删除测试用例参数
    *deleteTestCaseParams({ payload }, { call }) {
      const res = yield call(deleteTestCaseParams, payload);
      return getResponse(res);
    },

    // 删除测试用例
    *deleteTestCase({ payload }, { call }) {
      const res = yield call(deleteTestCase, payload);
      return getResponse(res);
    },

    // 执行测试用例
    *executeTestCase({ payload }, { call }) {
      const res = yield call(executeTestCase, payload);
      return getResponse(res);
    },

    // 查询测试历史列表
    *queryTestCaseHistory({ payload }, { call, put }) {
      const res = yield call(queryTestCaseHistory, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            testCaseHistory: {
              list: response.content || [],
              pagination: createPagination(response),
            },
          },
        });
      }
    },

    // 删除测试历史
    *deleteTestHistory({ payload }, { call }) {
      const res = yield call(deleteTestHistory, payload);
      return getResponse(res);
    },

    // 查询测试用例代码预览
    *queryViewCode({ payload }, { call, put }) {
      const res = yield call(queryViewCode, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            viewCode: response.code,
          },
        });
      }
    },

    // 查询文档的所有参数信息及参数备选值
    *queryParamsAndAlternative({ payload }, { call, put }) {
      const res = yield call(queryParamsAndAlternative, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            paramsWithValues: response,
          },
        });
      }
    },

    *queryMonitor({ interfaceId }, { call }) {
      const res = yield call(queryMonitor, interfaceId);
      const response = getResponse(res);
      return response;
    },
    *createMonitor({ interfaceId, data }, { call }) {
      const res = yield call(createMonitor, interfaceId, data);
      return res;
    },
    *updateMonitor({ interfaceId, interfaceMonitorId, data }, { call }) {
      const res = yield call(updateMonitor, interfaceId, interfaceMonitorId, data);
      return res;
    },
    // 查询值集
    *queryCode({ payload }, { put, call }) {
      const response = yield call(queryCode, payload);
      if (response && !response.failed) {
        yield put({
          type: 'setCodeReducer',
          payload: {
            [payload.lovCode]: response,
          },
        });
      }
    },
    *saveInterfaces({ interfaceServerId, data }, { call }) {
      const res = getResponse(yield call(saveInterfaces, interfaceServerId, data));
      return res;
    },

    // 批量创建内部接口
    *saveBatchInterfaces({ interfaceServerId, data }, { call }) {
      const res = yield call(saveBatchInterfaces, interfaceServerId, data);
      const response = getResponse(res);
      return response;
    },

    *queryInterfacesListDetail({ interfaceId }, { call }) {
      const res = yield call(queryInterfacesListDetail, interfaceId);
      const response = getResponse(res);
      return response;
    },

    // 查询接口文档预览数据
    *queryViewData({ payload }, { call, put }) {
      const res = yield call(queryDocumentView, payload);
      const response = getResponse(res);
      if (response) {
        yield put({
          type: 'updateState',
          payload: {
            viewData: response,
          },
        });
      }
    },

    // 查询mimeType
    *queryMimeTypes(_, { call, put }) {
      const mimeTypes = getResponse(yield call(queryIdpValue, 'HITF.MIME_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          mimeTypes,
        },
      });
    },

    // 查询内部接口列表数据
    *queryInternal({ payload }, { call, put }) {
      const { editable, ...otherPayload } = payload;
      const res = yield call(queryInternal, otherPayload);
      const response = getResponse(res);
      if (response) {
        const { content = [] } = response;
        yield put({
          type: 'updateState',
          payload: {
            internalList: editable
              ? content.map((item) => ({ ...item, _status: 'update' }))
              : content,
            internalPagination: createPagination(response),
          },
        });
      }
    },

    // 测试服务认证配置
    *testAuth({ payload }, { call }) {
      const res = getResponse(yield call(testAuth, payload));
      return res;
    },

    // 查询映射类
    *queryMappingClass(_, { call }) {
      const res = getResponse(yield call(queryMappingClass));
      return res;
    },

    // 测试映射类
    *testMappingClass({ payload }, { call }) {
      const res = getResponse(yield call(testMappingClass, payload));
      return res;
    },

    // 接口参数识别
    *recognizeParam({ payload }, { call }) {
      const res = getResponse(yield call(recognizeParam, payload));
      return res;
    },

    // 服务参数识别
    *recognizeServiceParam({ interfaceServerId }, { call }) {
      const res = getResponse(yield call(recognizeServiceParam, interfaceServerId));
      return res;
    },

    // 请求公钥
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

    // 运维配置-消息模板账户关联：查询模板行
    *getTemplateServerDetail(params, { call }) {
      const res = yield call(getTemplateServerDetail, params);
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
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
  },
};
