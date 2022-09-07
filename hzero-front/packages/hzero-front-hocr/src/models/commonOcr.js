/**
 * 通用OCR commonOcr
 * @date: 2019-9-6
 * @author: XL liang.xiong@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import { getResponse } from 'utils/utils';

import {
  fetchOcrIdentifyDetail,
  updateRecognizeDetail,
  updateVatRecognizeDetail,
  updateTextRecognizeDetail,
  updateLicenseRecognizeDetail,
  updateIdRecognizeDetail,
  updateTaxiRecognizeDetail,
  updateTrainRecognizeDetail,
  updateQuotaRecognizeDetail,
} from '../services/commonOcrService.js';

export default {
  namespace: 'commonOcr',

  state: {
    ocrIdentifyDetail: [],
    editDataSource: [],
  },

  effects: {
    // 获取识别记录明细
    * fetchOcrIdentifyDetail({ payload }, { call, put }) {
      const res = yield call(fetchOcrIdentifyDetail, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            ocrIdentifyDetail: list,
          },
        });
      }
      return list;
    },

    // 更新识别明细
    * updateRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateRecognizeDetail, payload);
      return getResponse(res);
    },

    // 更新增值税识别明细
    * updateVatRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateVatRecognizeDetail, payload);
      return getResponse(res);
    },

    * updateTextRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateTextRecognizeDetail, payload);
      return getResponse(res);
    },

    * updateLicenseRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateLicenseRecognizeDetail, payload);
      return getResponse(res);
    },

    * updateIdRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateIdRecognizeDetail, payload);
      return getResponse(res);
    },

    * updateTaxiRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateTaxiRecognizeDetail, payload);
      return getResponse(res);
    },

    * updateTrainRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateTrainRecognizeDetail, payload);
      return getResponse(res);
    },

    * updateQuotaRecognizeDetail({ payload }, { call }) {
      const res = yield call(updateQuotaRecognizeDetail, payload);
      return getResponse(res);
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
