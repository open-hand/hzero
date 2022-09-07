import intl from 'hzero-front/lib/utils/intl';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import CodeConstants from '../../constants/CodeConstants';
import DataSetConstants from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 账单明细表 DataSet
 */

export default () => ({
  primaryKey: 'lineId',
  autoQuery: true,
  pageSize: 10,
  name: DataSetConstants.BillLineDataSet,
  selection: false,
  fields: [
    {
      name: 'lineId',
      type: 'string',
    },
    {
      name: 'lineNum',
      type: 'number',
      order: 'desc',
      label: intl.get('hchg.bill.model.billLine.lineNum').d('行号'),
    },
    {
      name: 'headerId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'issueDateStart',
      type: 'dateTime',
      format: 'YYYY-MM-DD HH:mm:ss',
      max: 'issueDateEnd',
      label: intl.get('hchg.bill.model.billDeteil.issueDateStart').d('发生日期从'),
    },
    {
      name: 'issueDateEnd',
      type: 'dateTime',
      format: 'YYYY-MM-DD HH:mm:ss',
      min: 'issueDateStart',
      label: intl.get('hchg.bill.model.billDeteil.issueDateEnd').d('发生日期至'),
    },
    {
      name: 'uom',
      type: 'string',
      lookupCode: CodeConstants.BillIssueUom,
      label: intl.get('hchg.bill.model.billDeteil.uom').d('计量单位'),
    },
    {
      name: 'uomMeaning',
      type: 'string',
      label: intl.get('hchg.bill.model.billDeteil.uom').d('计量单位'),
    },
    {
      name: 'value',
      type: 'number',
      label: intl.get('hchg.bill.model.billDeteil.value').d('计量值'),
    },
    {
      name: 'discountType',
      type: 'string',
      lookupCode: CodeConstants.BillDiscountType,
      label: intl.get('hchg.bill.model.billDeteil.discountType').d('优惠类型'),
    },
    {
      name: 'discountAmount',
      type: 'string',
      step: 0.01,
      min: 0,
      label: intl.get('hchg.bill.model.billDeteil.discountAmount').d('优惠额度'),
    },
    {
      name: 'issueAmount',
      type: 'number',
      step: 0.01,
      min: 0,
      label: intl.get('hchg.bill.model.billDeteil.issueAmount').d('发生金额'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hchg.bill.model.billDeteil.remark').d('备注'),
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hchg.bill.model.billDeteil.tenantId').d('租户'),
    },
  ],
  queryFields: [
    {
      name: 'headerId',
      type: 'string',
    },
  ],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/bill-lines/detail`
        : `${HZERO_CHG}/v1/bill-lines/detail`;
      return {
        data,
        params,
        url: getUrl(url, data.billNum),
        method: 'GET',
      };
    },
  },
});

function getUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
