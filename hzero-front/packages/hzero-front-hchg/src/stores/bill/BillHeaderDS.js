import intl from 'hzero-front/lib/utils/intl';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { dateRender } from 'utils/renderer';
import CodeConstants from '../../constants/CodeConstants';
import DataSetConstants from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 账单头表 DataSet
 */

export default () => ({
  primaryKey: 'headerId',
  autoQuery: false,
  pageSize: 10,
  name: DataSetConstants.BillHeaderDataSet,
  selection: 'multiple',
  fields: [
    {
      name: 'headerId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'billName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.billName').d('账单名称'),
    },
    {
      name: 'billNum',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.billNum').d('账单编号'),
    },
    {
      name: 'billDate',
      type: 'date',
      format: 'YYYY-MM-DD',
      label: intl.get('hchg.bill.model.billHeader.billDate').d('账单日期'),
    },
    {
      name: 'amount',
      type: 'number',
      step: 0.01,
      min: 0,
      label: intl.get('hchg.bill.model.billHeader.amount').d('账单金额'),
    },
    {
      name: 'currencyObject',
      type: 'object',
      lovCode: CodeConstants.Currency,
      textField: 'currencyName',
      valueField: 'currencyCode',
      ignore: 'always',
      label: intl.get('hchg.bill.model.billHeader.currency').d('币种'),
    },
    {
      name: 'currencyCode',
      type: 'string',
      bind: 'currencyObject.currencyCode',
      label: intl.get('hchg.bill.model.billHeader.currency').d('币种'),
    },
    {
      name: 'currencyName',
      type: 'string',
      bind: 'currencyObject.currencyName',
      label: intl.get('hchg.bill.model.billHeader.currency').d('币种'),
    },
    {
      name: 'discountTotal',
      type: 'number',
      step: 0.01,
      min: 0,
      label: intl.get('hchg.bill.model.billHeader.discountTotal').d('总优惠额度'),
    },
    {
      name: 'statusCode',
      type: 'string',
      lookupCode: CodeConstants.BillStatus,
      label: intl.get('hchg.bill.model.billHeader.statusCode').d('账单状态'),
    },
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.serviceName').d('计费规则代码'),
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.ruleName').d('计费规则名称'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.remark').d('备注'),
    },
    {
      name: 'billEntityType',
      type: 'string',
      lookupCode: CodeConstants.BillEntityType,
      label: intl.get('hchg.bill.model.billHeader.billEntityType').d('账单发生实体'),
    },
    {
      name: 'billEntityName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.billEntityName').d('实体名称'),
    },
    {
      name: 'paymentStatus',
      type: 'string',
      lookupCode: CodeConstants.BillPaymentStatus,
      label: intl.get('hchg.bill.model.billHeader.paymentStatus').d('支付状态'),
    },
    {
      name: 'actualPaymentTime',
      type: 'dateTime',
      format: 'YYYY-MM-DD HH:mm:ss',
      label: intl.get('hchg.bill.model.billHeader.actualPaymentTime').d('实际支付时间'),
    },
    {
      name: 'actualPaymentAmount',
      type: 'number',
      label: intl.get('hchg.bill.model.billHeader.actualPaymentAmount').d('实际支付金额'),
    },
    {
      name: 'paymentAmount',
      type: 'number',
      label: intl.get('hchg.bill.model.billHeader.paymentAmount').d('应付金额'),
    },
    {
      name: 'sourceSystemId',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.sourceSystemId').d('来源系统标志'),
    },
    {
      name: 'sourceBillNum',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.sourceBillNum').d('来源系统单号'),
    },
    {
      name: 'sourceSystemName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.sourceSystemName').d('来源系统名称'),
    },
    {
      name: 'sourceSystemNum',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.sourceSystemNum').d('来源系统编码'),
    },
    {
      name: 'processMessage',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.processMessage').d('处理信息'),
    },
  ],
  queryFields: [
    {
      name: 'billName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.billName').d('账单名称'),
    },
    {
      name: 'billNum',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.billNum').d('账单编号'),
    },
    {
      name: 'statusCode',
      type: 'string',
      lookupCode: CodeConstants.BillStatus,
      label: intl.get('hchg.bill.model.billHeader.statusCode').d('账单状态'),
    },
    {
      name: 'billDateFrom',
      type: 'date',
      format: 'YYYY-MM-DD',
      max: 'billDateTo',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hchg.bill.model.billHeader.billDateFrom').d('账单日期从'),
    },
    {
      name: 'billDateTo',
      type: 'date',
      format: 'YYYY-MM-DD',
      min: 'billDateFrom',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hchg.bill.model.billHeader.billDateTo').d('账单日期至'),
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.ruleName').d('计费规则名称'),
    },
    {
      name: 'sourceSystemName',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.sourceSystemName').d('来源系统名称'),
    },
    {
      name: 'sourceBillNum',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.sourceBillNum').d('来源系统单号'),
    },
    {
      name: 'billEntityType',
      type: 'string',
      lookupCode: CodeConstants.BillEntityType,
      label: intl.get('hchg.bill.model.billHeader.billEntityType').d('账单发生实体'),
    },
    {
      name: 'paymentStatus',
      type: 'string',
      lookupCode: CodeConstants.BillPaymentStatus,
      label: intl.get('hchg.bill.model.billHeader.paymentStatus').d('支付状态'),
    },
    {
      name: 'actualPaymentTimeFrom',
      type: 'dateTime',
      format: 'YYYY-MM-DD HH:mm:ss',
      max: 'actualPaymentTimeTo',
      label: intl.get('hchg.bill.model.billHeader.actualPaymentTimeFrom').d('实际支付时间从'),
    },
    {
      name: 'actualPaymentTimeTo',
      type: 'dateTime',
      format: 'YYYY-MM-DD HH:mm:ss',
      min: 'actualPaymentTimeFrom',
      label: intl.get('hchg.bill.model.billHeader.actualPaymentTimeTo').d('实际支付时间至'),
    },
    {
      name: 'currencyObject',
      type: 'object',
      lovCode: CodeConstants.Currency,
      textField: 'currencyName',
      valueField: 'currencyCode',
      ignore: 'always',
      label: intl.get('hchg.bill.model.billHeader.currency').d('币种'),
    },
    {
      name: 'currencyCode',
      type: 'string',
      bind: 'currencyObject.currencyCode',
      label: intl.get('hchg.bill.model.billHeader.currency').d('币种'),
    },
    {
      name: 'processMessage',
      type: 'string',
      label: intl.get('hchg.bill.model.billHeader.processMessage').d('处理信息'),
    },
  ],
  submit: ({ data, params }) => ({
    url: isTenantRoleLevel()
      ? `${HZERO_CHG}/v1/${organizationId}/bill/pay`
      : `${HZERO_CHG}/v1/bill/pay`,
    data,
    params,
    method: 'POST',
  }),
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/bill-headers`
        : `${HZERO_CHG}/v1/bill-headers`;
      return {
        data,
        params,
        url: getDetailUrl(url, data.billNum),
        method: 'GET',
      };
    },
  },
  feedback: {
    loadSuccess: (resp) => {
      const _resp = resp;
      if (_resp) {
        if (!_resp.content) {
          _resp.currencyObject = {
            currencyCode: resp.currencyCode,
            currencyName: resp.currencyName,
          };
        }
      }
    },
  },
});

function getDetailUrl(url, id) {
  return id ? `${url}/detail/${id}` : `${url}`;
}
