/**
 * OCRInspectionService OCR识别发票查验
 * @date: 2019-8-25
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { getResponse } from 'utils/utils';

import { create } from '../services/ocrInspectionService';

export default {
  namespace: 'ocrInspection',
  state: {
    pagination: {}, // 分页参数
    ocrInspectionList: [], // 配置列表
    ocrInspectionDetail: {}, // 配置详情
    typeList: [], // 单点登录类别
  },
  effects: {
    // 新建配置
    * create({ payload }, { call }) {
      const res = yield call(create, payload);
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
