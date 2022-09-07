/* paymentOrder model文件
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-14
 * @copyright 2018 © HAND
 */

import { createPagination, getResponse, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';

import { fetchOrderList, getOrderDetail, refundOrder } from '../services/paymentOrderService';

export default {
  namespace: 'paymentOrder',

  state: {
    channelList: [], // 支付渠道列表
    statusList: [], // 状态列表
    paymentOrderList: [], // 支付订单列表
    pagination: {}, // 分页对象
    orderDetail: {}, // 查询列表
  },

  effects: {
    // 获取初始化数据
    * init(_, { call, put }) {
      const params = { channelList: 'HPAY.PAYMENT_CHANNEL', statusList: 'HPAY.PAYMENT_STATUS' };
      const res = getResponse(yield call(queryMapIdpValue, params));
      const { channelList, statusList } = res;
      yield put({
        type: 'updateState',
        payload: {
          channelList,
          statusList,
        },
      });
    },

    // 获取支付订单列表
    * fetchOrderList({ payload }, { call, put }) {
      const res = yield call(fetchOrderList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            paymentOrderList: list.content,
            pagination: createPagination(list),
          },
        });
      }
    },

    // 获取支付订单明细
    * getOrderDetail({ payload }, { call, put }) {
      const res = yield call(getOrderDetail, payload);
      const detail = getResponse(res);
      if (detail) {
        yield put({
          type: 'updateState',
          payload: {
            orderDetail: detail,
          },
        });
      }
    },

    // 支付订单退款申请
    * refundOrder({ payload }, { call }) {
      const res = yield call(refundOrder, payload);
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
