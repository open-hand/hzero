/**
 * refundOrderService 服务
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-17
 * @copyright 2018 © HAND
 */
import request from 'utils/request';
import { HZERO_HPAY } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询退款订单列表
 * @async
 * @function fetchOrderList
 * @param {Object} params - 查询参数
 */
export async function fetchOrderList(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/refund-orders`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询退款订单明细
 * @async
 * @function getOrderDetail
 * @param {Object} params - 查询参数
 */
export async function getOrderDetail(params) {
  const { refundOrderId } = params;
  return request(`${HZERO_HPAY}/v1/${organizationId}/refund-orders/${refundOrderId}`, {
    method: 'GET',
  });
}

/**
 * 拒绝退款订单
 * @async
 * @function refuseOrder
 * @param {Object} params - 查询参数
 */
export async function refuseOrder(params) {
  const { _token, refundOrderId, remark, objectVersionNumber } = params;
  const req = {
    refundOrderId,
    _token,
    remark,
    objectVersionNumber,
  };
  return request(`${HZERO_HPAY}/v1/${organizationId}/refund-orders/refused`, {
    method: 'PUT',
    body: req,
  });
}

/**
 * 确认退款申请
 * @async
 * @function confirmOrder
 * @param {Object} params - 查询参数
 */
export async function confirmOrder(params) {
  const {
    refundOrderNum,
    merchantOrderNum,
    channelTradeNo,
    refundAmount,
    channelCode,
    totalAmount,
  } = params;
  const req = {
    outTradeNo: merchantOrderNum,
    tradeNo: channelTradeNo,
    refundAmount,
    refundNo: refundOrderNum,
    totalAmount,
  };
  switch (channelCode) {
    case 'alipay':
      return request(`${HZERO_HPAY}/v1/${organizationId}/alipay/refund`, {
        method: 'POST',
        body: req,
      });
    case 'wxpay':
      return request(`${HZERO_HPAY}/v1/${organizationId}/wxpay/refund`, {
        method: 'POST',
        body: req,
      });
    case 'unionpay':
      return request(`${HZERO_HPAY}/v1/${organizationId}/unionpay/refund`, {
        method: 'POST',
        body: req,
      });
    default:
      break;
  }
}
