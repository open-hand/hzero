/**
 * @date: 2018-09-24
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import { getResponse } from 'utils/utils';
import { queryFileList } from '../services/fileAggregateService';

export default {
  namespace: 'fileAggregate',

  state: {
    fileData: {}, // 文件列表
    fileTypeList: [], // 文件类型
    fileFormatList: [], // 文件格式
    fileUnitList: [], // 单位
    sourceList: [], // 来源类型
    detailWordEditor: {
      // [record.fileUrl]: record, // 存储 word编辑 的记录
    },
  },
  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (res) {
        const { fileTypeList, fileFormatList, fileUnitList, sourceList } = res;
        yield put({
          type: 'updateState',
          payload: {
            fileTypeList,
            fileFormatList,
            fileUnitList,
            sourceList,
          },
        });
      }
    },

    // 获取文件列表
    * queryFileList({ payload }, { call, put }) {
      const { page = 0, size = 10, ...params } = payload;
      const res = yield call(queryFileList, { page, size, ...params });
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            fileData: list,
          },
        });
      }
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
