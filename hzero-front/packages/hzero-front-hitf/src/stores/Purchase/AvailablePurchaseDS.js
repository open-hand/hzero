import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import {
  SETTLEMENT_PERIOD,
  PAYMENT_MODEL,
  CHARGE_METHOD,
  PURCHASE_TYPE,
  AVAILABLE_STATUS,
} from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 用户可购买记录信息 DataSet
 */

export default () => ({
  // primaryKey: 'headerId',
  autoQuery: true,
  pageSize: 10,
  name: DataSet.AvailablePurchase,
  selection: false,
  fields: [
    {
      name: 'headerId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'typeCode',
      type: 'string',
      lookupCode: PURCHASE_TYPE,
      label: intl.get('hitf.purchase.model.availablePurchase.typeCode').d('类型'),
    },
    {
      name: 'chargeName',
      type: 'string',
      label: intl.get('hitf.purchase.model.availablePurchase.chargeName').d('计费名称'),
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hitf.purchase.model.availablePurchase.serverName').d('服务名称'),
    },
    {
      name: 'interfaceName',
      type: 'string',
      label: intl.get('hitf.purchase.model.availablePurchase.interfaceName').d('接口名称'),
    },
    {
      name: 'statusCode',
      type: 'string',
      lookupCode: AVAILABLE_STATUS,
      label: intl.get('hitf.purchase.model.availablePurchase.statusCode').d('状态'),
    },
    {
      name: 'chargeRuleName',
      type: 'string',
      label: intl.get('hitf.purchase.model.availablePurchase.chargeRuleName').d('计费规则名称'),
    },
    {
      name: 'chargeRuleCode',
      type: 'string',
      label: intl.get('hitf.purchase.model.availablePurchase.chargeRuleCOde').d('计费规则代码'),
    },
    {
      name: 'settlementPeriod',
      type: 'string',
      lookupCode: SETTLEMENT_PERIOD,
      label: intl.get('hitf.purchase.model.availablePurchase.settlementPeriod').d('结算周期'),
    },
    {
      name: 'paymentModel',
      type: 'string',
      lookupCode: PAYMENT_MODEL,
      label: intl.get('hitf.purchase.model.availablePurchase.paymentModel').d('付费模式'),
    },
    {
      name: 'chargeMethodCode',
      type: 'string',
      lookupCode: CHARGE_METHOD,
      label: intl.get('hitf.purchase.model.availablePurchase.chargeMethod').d('计费方式'),
    },
    {
      name: 'totalCount',
      type: 'number',
      label: intl.get('hitf.purchase.model.availablePurchase.totalCount').d('总量'),
    },
    {
      name: 'remainCount',
      type: 'number',
      label: intl.get('hitf.purchase.model.availablePurchase.remainCount').d('余量'),
    },
    {
      name: 'chargeUomCode',
      type: 'string',
      label: intl.get('hitf.purchase.model.availablePurchase.chargeUomCode').d('计费单位'),
    },
    {
      name: 'nextBillTime',
      type: 'date',
      format: 'YYYY-MM-DD HH:mm:ss',
      label: intl.get('hitf.purchase.model.availablePurchase.nextBillTime').d('下一个账单时间'),
    },
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hitf.purchase.model.availablePurchase.tenantId').d('租户ID'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.purchase.model.userPurchase.remark').d('备注'),
    },
  ],
  queryFields: [],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/user-purchases/available`
        : `${HZERO_HITF}/v1/user-purchases/available`;
      return {
        data,
        params,
        url: getUrl(url, data.userPurchaseId),
        method: 'GET',
      };
    },
  },
  feedback: {
    loadSuccess: (res) => {
      if (res) {
        if (!res.content) {
          // 明细页面 设置服务代码Lov/接口代码Lov的值
          res.serverObject = {
            serverCode: res.serverCode,
            interfaceServerId: res.interfaceServerId,
            serverName: res.serverName,
          };
          res.interfaceObject = {
            interfaceCode: res.interfaceCode,
            interfaceId: res.interfaceId,
            interfaceName: res.interfaceName,
          };
        }
      }
    },
  },
});

function getUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
