/**
 * @date 2019-11-21
 * @author LZY <zhuyan.luo@hand-china.com>
 */
import { isArray, isEmpty } from 'lodash';
import { getResponse } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import {
  fetchReceiveConfig,
  saveConfig,
  editConfig,
  deleteConfig,
  renderTreeData,
} from '../services/receiveConfigService';

export default {
  namespace: 'receiveConfig',
  state: {
    messageTypeList: [],
    configList: [], // 消息配置列表
    renderTree: [], // 页面渲染数据结构
    addData: {}, // 新增数据
    pathMap: {}, // 节点层级路径
  },
  effects: {
    // 获取消息类型
    * fetchMessageType(_, { call, put }) {
      const messageTypeList = getResponse(yield call(queryIdpValue, 'HMSG.MESSAGE_TYPE'));
      if (messageTypeList) {
        yield put({
          type: 'updateState',
          payload: {
            messageTypeList,
          },
        });
      }
    },
    // 查询接收配置信息
    * fetchReceiveConfig({ payload }, { call, put }) {
      const res = yield call(fetchReceiveConfig, payload);
      const configList = getResponse(res);
      if (configList) {
        const { pathMap } = renderTreeData(configList, {});
        const configKeys = [];
        const flatKeys = configItem => {
          if (isArray(configItem.children) && !isEmpty(configItem.children)) {
            configKeys.push(configItem.receiveId);
            configItem.children.forEach(item => flatKeys(item));
          } else {
            configKeys.push(configItem.receiveId);
          }
        };
        configList.forEach(item => flatKeys(item));
        yield put({
          type: 'updateState',
          payload: {
            configList,
            configKeys,
            pathMap,
          },
        });
      }
    },
    // 保存下级配置
    * saveConfig({ payload }, { call }) {
      const result = yield call(saveConfig, payload);
      return getResponse(result);
    },
    // 编辑
    * editConfig({ payload }, { call }) {
      const result = yield call(editConfig, payload);
      return getResponse(result);
    },
    // 删除
    * deleteConfig({ payload }, { call }) {
      const result = yield call(deleteConfig, payload);
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
