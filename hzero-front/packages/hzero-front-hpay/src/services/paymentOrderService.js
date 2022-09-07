/**
 * paymentOrderService 服务
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-14
 * @copyright 2018 © HAND
 */
import request from 'utils/request';
import { HZERO_HPAY } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询支付订单列表
 * @async
 * @function fetchOrderList
 * @param {Object} params - 查询参数
 */
export async function fetchOrderList(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/payment-orders`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询查询支付订单明细
 * @async
 * @function getOrderDetail
 * @param {Object} params - 查询参数
 */
export async function getOrderDetail(params) {
  const { paymentOrderId } = params;
  return request(`${HZERO_HPAY}/v1/${organizationId}/payment-orders/${paymentOrderId}`, {
    method: 'GET',
  });
}

/**
 * 退款申请
 * @async
 * @function refundOrder
 * @param {Object} params - 查询参数
 */
export async function refundOrder(params) {
  const { paymentOrderNum, channelCode } = params;
  switch (channelCode) {
    case 'alipay':
      return request(`${HZERO_HPAY}/v1/${organizationId}/alipay/refund/apply/${paymentOrderNum}`, {
        method: 'POST',
      });
    case 'wxpay':
      return request(`${HZERO_HPAY}/v1/${organizationId}/wxpay/refund/apply/${paymentOrderNum}`, {
        method: 'POST',
      });
    case 'unionpay':
      return request(
        `${HZERO_HPAY}/v1/${organizationId}/unionpay/refund/apply/${paymentOrderNum}`,
        {
          method: 'POST',
        }
      );
    default:
      break;
  }
}
