/**
 * ocrRecord OCR记录
 * @date: 2019-9-6
 * @author: XL liang.xiong@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse, createPagination } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';

import {
  fetchOcrRecordList,
  fetchOcrRecordDetail,
  fetchRecognizeList,
  fetchRecognizeDetail,
  fetchLicenseDetail,
  fetchIdDetail,
  fetchTaxiDetail,
  fetchTrainDetail,
  fetchTextDetail,
  fetchVatDetail,
  fetchQuotaDetail,
  fetchMultiDetail,
  redirect,
} from '../services/ocrRecordService';

export default {
  namespace: 'ocrRecord',

  state: {
    ocrRecordList: [],
    ocrRecordDetail: {},
    pagination: {},
    ocrTypeList: [],
    recognizeList: [],
    recognizeDetail: {},
    recognizePagination: {},
    editDataSource: [],
    licenseDetail: {},
    idDetail: {},
    taxiDetail: {},
    textDetail: {},
    vatDetail: {},
    quotaDetail: {},
    multiDetail: [],
  },

  effects: {
    // 初始化值集
    * init(_, { call, put }) {
      const ocrTypeList = getResponse(yield call(queryIdpValue, 'HOCR.OCR_TYPE'));
      yield put({
        type: 'updateState',
        payload: {
          ocrTypeList,
        },
      });
    },

    // 获取识别记录列表
    * fetchOcrRecordList({ payload }, { put, call }) {
      const res = yield call(fetchOcrRecordList, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            ocrRecordList: data.content,
            pagination: createPagination(data),
          },
        });
      }
    },

    // 获取识别记录明细
    * fetchOcrRecordDetail({ payload }, { call, put }) {
      const res = yield call(fetchOcrRecordDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            ocrRecordDetail: list,
          },
        });
      }
      return list;
    },

    // 识别记录
    * fetchRecognizeList({ payload }, { call, put }) {
      const res = yield call(fetchRecognizeList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            recognizeList: list,
          },
        });
      }
      return list;
    },

    // 识别记录
    * fetchLicenseDetail({ payload }, { call, put }) {
      const res = yield call(fetchLicenseDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            licenseDetail: list,
          },
        });
      }
      return list;
    },

    * fetchIdDetail({ payload }, { call, put }) {
      const res = yield call(fetchIdDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            idDetail: list,
          },
        });
      }
      return list;
    },

    * fetchTaxiDetail({ payload }, { call, put }) {
      const res = yield call(fetchTaxiDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            taxiDetail: list,
          },
        });
      }
      return list;
    },

    * fetchTextDetail({ payload }, { call, put }) {
      const res = yield call(fetchTextDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            textDetail: list,
          },
        });
      }
      return list;
    },

    * fetchTrainDetail({ payload }, { call, put }) {
      const res = yield call(fetchTrainDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            trainDetail: list,
          },
        });
      }
      return list;
    },

    * fetchVatDetail({ payload }, { call, put }) {
      const res = yield call(fetchVatDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            vatDetail: list,
          },
        });
      }
      return list;
    },

    * fetchQuotaDetail({ payload }, { call, put }) {
      const res = yield call(fetchQuotaDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            quotaDetail: list,
          },
        });
      }
      return list;
    },

    // 识别明细
    * fetchRecognizeDetail({ payload }, { call, put }) {
      const res = yield call(fetchRecognizeDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            recognizeDetail: list,
          },
        });
      }
    },

    // 多票识别明细
    * fetchMultiDetail({ payload }, { call, put }) {
      const res = yield call(fetchMultiDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            multiDetail: list,
          },
        });
      }
    },

    * redirect({ payload }, { call }) {
      const res = yield call(redirect, payload);
      const result = URL.createObjectURL(res);
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
