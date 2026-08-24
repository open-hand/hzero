/**
 * @date 2018-12-06
 * @author: CJ <juan.chen01@hand-china.com>
 */
import { isEmpty } from 'lodash';
import { getResponse, createPagination } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchTemplateManageList,
  createTemplateManage,
  editTemplateManage,
  fetchTemplateHeaderDetail,
  fetchTemplateLine,
  deleteTemplateManage,
  createTemplateLine,
  editTemplateLine,
  fetchTemplateLineDetail,
  deleteTemplateLine,
} from '../services/templateManageService';

export default {
  namespace: 'templateManage',
  state: {
    list: {}, // 列表数据
    code: {}, // 值集
    pagination: {}, // 分页数据
    templateTypeCode: [], // 模板类型
    header: {}, // 模板管理头
    line: {}, // 模板管理行
    lineDetail: {}, // 行详情
    linePagination: {}, // 行分页器
    fileList: [], // 文件
    detailWordEditor: {
      // [record.fileUrl]: record, // 存储 word编辑 的记录
    },
  },
  effects: {
    // 获取模板管理列表
    * fetchTemplateManageList({ payload }, { call, put }) {
      const res = yield call(fetchTemplateManageList, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            list: result,
            pagination: createPagination(result),
          },
        });
      }
    },
    // 统一获取值级的数据
    * batchCode({ payload }, { put, call }) {
      const { lovCodes } = payload;
      const code = getResponse(yield call(queryMapIdpValue, lovCodes));
      if (!isEmpty(code)) {
        yield put({
          type: 'updateState',
          payload: {
            code,
          },
        });
      }
    },
    // 获取模板管理头详情
    * fetchTemplateHeaderDetail({ payload }, { call, put }) {
      const res = yield call(fetchTemplateHeaderDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            header: result,
          },
        });
      }
    },
    // 获取模板管理行
    * fetchTemplateLine({ payload }, { call, put }) {
      const res = yield call(fetchTemplateLine, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            line: result,
            linePagination: createPagination(result),
          },
        });
      }
    },
    // 获取模板管理行详情
    * fetchTemplateLineDetail({ payload }, { call, put }) {
      const res = yield call(fetchTemplateLineDetail, payload);
      const result = getResponse(res);
      if (result) {
        yield put({
          type: 'updateState',
          payload: {
            lineDetail: result,
            fileList: [
              {
                uid: '-1',
                name: result.templateFileName,
                status: 'done',
                url: result.templateUrl,
              },
            ],
          },
        });
      }
      return result;
    },
    // 添加模板管理
    * createTemplateManage({ payload }, { call }) {
      const res = yield call(createTemplateManage, { ...payload });
      return getResponse(res);
    },
    // 编辑模板管理
    * editTemplateManage({ payload }, { call }) {
      const res = yield call(editTemplateManage, { ...payload });
      return getResponse(res);
    },
    // 删除模板管理
    * deleteTemplateManage({ payload }, { call }) {
      const res = yield call(deleteTemplateManage, { ...payload });
      return getResponse(res);
    },
    // 新建模板管理行
    * createTemplateLine({ payload }, { call }) {
      const res = yield call(createTemplateLine, { ...payload });
      return getResponse(res);
    },
    // 编辑模板管理行
    * editTemplateLine({ payload }, { call }) {
      const res = yield call(editTemplateLine, { ...payload });
      return getResponse(res);
    },
    // 删除模板管理行
    * deleteTemplateLine({ payload }, { call }) {
      const res = yield call(deleteTemplateLine, { ...payload });
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
