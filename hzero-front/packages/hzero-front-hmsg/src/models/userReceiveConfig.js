/**
 *
 * @date 2018-11-23
 * @author LZY <zhuyan.luo@hand-china.com>
 *
 * @notice 有数据转化
 */
import { getResponse, mapTree } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';
import { fetchReceiveConfig, saveConfig } from '../services/userReceiveConfigService';

function transformReceviConfig(dataSource) {
  if (dataSource) {
    return mapTree(
      dataSource,
      (item) => {
        const { defaultReceiveTypeList = [], receiveTypeList = [] } = item;
        const newReceiveTypeList = defaultReceiveTypeList.filter(
          (tItem) => !receiveTypeList.includes(tItem)
        );
        return {
          ...item,
          receiveTypeList: newReceiveTypeList || [],
          receiveType: newReceiveTypeList.join(','),
        };
      },
      { childrenNullable: true }
    );
  }
  return dataSource;
}

export default {
  namespace: 'userReceiveConfig',
  state: {
    messageTypeList: [], // 所有的消息类型
    configList: [], // 消息配置列表
    addData: {}, // 新增数据
    pathMap: {}, // 节点层级路径
    configKeys: [], // 所有配置的的Id
  },
  effects: {
    // 获取消息类型
    *fetchMessageType(_, { call, put }) {
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
    *fetchReceiveConfig(_, { call, put }) {
      const res = yield call(fetchReceiveConfig);
      const configList = transformReceviConfig(getResponse(res));
      if (configList) {
        yield put({
          type: 'updateState',
          payload: {
            // configList: configList[0].children,
            configList,
          },
        });
      }
      return configList;
    },
    // 保存下级配置
    *saveConfig({ payload }, { call }) {
      const result = yield call(saveConfig, payload);
      return transformReceviConfig(getResponse(result));
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
