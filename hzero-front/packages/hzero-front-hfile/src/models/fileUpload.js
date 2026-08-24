/**
 * @date: 2018-09-24
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { queryMapIdpValue } from 'hzero-front/lib//services/api';
import { getResponse, createPagination } from 'utils/utils';
import {
  queryFileList,
  addConfigDetail,
  editConfigDetail,
  deleteConfigDetail,
  saveHeader,
  getUploadDetail,
} from '../services/fileUploadService';

export default {
  namespace: 'fileUpload',
  state: {
    fileData: {}, // 文件列表
    fileTypeList: [], // 文件类型
    fileFormatsList: [], // 文件格式
    fileUnitList: [], // 单位
    pagination: {}, // 分页信息
    fileDetail: {}, // 详情数据
  },
  effects: {
    // 获取初始化数据
    * init({ payload }, { call, put }) {
      const { lovCodes } = payload;
      const res = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (res) {
        const { fileTypeList, fileFormatsList, fileUnitList } = res;
        yield put({
          type: 'updateState',
          payload: {
            fileTypeList,
            fileFormatsList,
            fileUnitList,
          },
        });
      }
    },

    // 获取文件列表
    * queryFileList({ payload }, { call, put }) {
      const res = yield call(queryFileList, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            fileData: list,
            pagination: createPagination(list.listConfig),
          },
        });
      }
    },

    // 获取文件上传详情
    * getUploadDetail({ payload }, { call, put }) {
      const res = yield call(getUploadDetail, payload);
      const data = getResponse(res);
      if (data) {
        yield put({
          type: 'updateState',
          payload: {
            fileDetail: data,
          },
        });
      }
      return data;
    },

    // 新增保存
    * addConfigDetail({ payload }, { call }) {
      const result = yield call(addConfigDetail, { ...payload });
      return getResponse(result);
    },
    // 编辑保存
    * editConfigDetail({ payload }, { call }) {
      const result = yield call(editConfigDetail, { ...payload });
      return getResponse(result);
    },
    // 删除
    * deleteConfigDetail({ payload }, { call }) {
      const result = yield call(deleteConfigDetail, payload);
      return getResponse(result);
    },
    // 保存头
    * saveHeader({ payload }, { call }) {
      const result = yield call(saveHeader, { ...payload });
      return getResponse(result);
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
