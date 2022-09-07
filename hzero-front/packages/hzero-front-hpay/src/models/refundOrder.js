/**
 * refundOrder model文件
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-17
 * @copyright 2018 © HAND
 */

import { getResponse, createPagination, parseParameters } from 'utils/utils';
import { queryMapIdpValue } from 'hzero-front/lib/services/api';

import {
  fetchOrderList,
  refuseOrder,
  confirmOrder,
  getOrderDetail,
} from '../services/refundOrderService';

export default {
  namespace: 'refundOrder',

  state: {
    refundOrderList: [], // 支付订单列表
    pagination: {}, // 分页对象
    channelList: [], // 支付渠道列表
    statusList: [], // 状态列表
    wayList: [], // 方式列表
    orderDetail: {}, // 查询列表
  },

  effects: {
    // 获取初始化数据
    * init(_, { call, put }) {
      const params = {
        channelList: 'HPAY.PAYMENT_CHANNEL',
        statusList: 'HPAY.REFUND_STATUS',
        wayList: 'HPAY.REFUND_WAY',
      };
      const res = getResponse(yield call(queryMapIdpValue, params));
      const { channelList, statusList, wayList } = res;
      yield put({
        type: 'updateState',
        payload: {
          channelList,
          statusList,
          wayList,
        },
      });
    },

    // 获取退款订单列表
    * fetchOrderList({ payload }, { call, put }) {
      const res = yield call(fetchOrderList, parseParameters(payload));
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            refundOrderList: list.content,
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

    // 获取拒绝订单明细
    * getRefusedOrderDetail({ payload }, { call, put }) {
      const res = yield call(fetchOrderList, parseParameters(payload));
      const detail = getResponse(res);
      yield put({
        type: 'updateState',
        payload: {
          orderDetail: detail.content[0],
        },
      });
    },

    // 同意退款订单
    * confirmOrder({ payload }, { call }) {
      const res = yield call(confirmOrder, payload);
      const detail = getResponse(res);
      return detail;
    },

    // 拒绝退款订单
    * refuseOrder({ payload }, { call }) {
      const res = yield call(refuseOrder, payload);
      const detail = getResponse(res);
      return detail;
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
