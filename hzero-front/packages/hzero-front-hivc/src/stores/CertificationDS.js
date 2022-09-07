/*
 * certification 发票认证结果
 * @date: 2020-04-28
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_INVOICE } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_INVOICE}/v1/${organizationId}` : `${HZERO_INVOICE}/v1`;

// 表格ds
const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'batchNo',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.batchNo').d('批次号'),
    },
    {
      name: 'authPeriod',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.authPeriod').d('认证所属期'),
    },
    {
      name: 'requestStatus',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.resultFlag').d('请求状态'),
      lookupCode: 'HIVC.REQUEST_STATUS',
    },
  ],
  fields: [
    {
      name: 'buyerNo',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.buyerNo').d('纳税人识别号'),
    },
    {
      name: 'batchNo',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.batchNo').d('批次号'),
    },
    {
      name: 'authState',
      type: 'boolean',
      label: intl.get('hivc.certification.model.certification.authState').d('企业认证状态'),
    },
    {
      name: 'authPeriod',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.authPeriod').d('认证所属期'),
    },
    {
      name: 'statisticsCompleteTime',
      type: 'dateTime',
      label: intl.get('hivc.certification.model.certification.countCompleteTime').d('统计完成时间'),
    },
    {
      name: 'confirmCompleteTime',
      type: 'dateTime',
      label: intl
        .get('hivc.certification.model.certification.confirmCompleteTime')
        .d('确认完成时间'),
    },
    {
      name: 'requestStatus',
      type: 'number',
      label: intl.get('hivc.certification.model.certification.requestStatus').d('请求状态'),
    },
    {
      name: 'resultMessage',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.resultMessage').d('失败原因'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/auth-results`,
      method: 'GET',
    }),
  },
});

const detailDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      name: 'buyerNo',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.buyerNo').d('纳税人识别号'),
    },
    {
      name: 'batchNo',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.batchNo').d('批次号'),
    },
    {
      name: 'authState',
      type: 'boolean',
      label: intl.get('hivc.certification.model.certification.authState').d('企业认证状态'),
    },
    {
      name: 'authPeriod',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.authPeriod').d('认证所属期'),
    },
    {
      name: 'statisticsCompleteTime',
      type: 'dateTime',
      label: intl.get('hivc.certification.model.certification.countCompleteTime').d('统计完成时间'),
    },
    {
      name: 'confirmCompleteTime',
      type: 'dateTime',
      label: intl
        .get('hivc.certification.model.certification.confirmCompleteTime')
        .d('确认完成时间'),
    },
    {
      name: 'requestStatus',
      type: 'number',
      label: intl.get('hivc.certification.model.certification.requestStatus').d('请求状态'),
    },
    {
      name: 'resultMessage',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.resultMessage').d('失败原因'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { id },
      } = dataSet;
      return {
        url: `${apiPrefix}/auth-results/${id}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
  },
});

// 详情页面下的表格信息DS
const detailTableDS = () => ({
  selection: false,
  autoQuery: false,
  autoCreate: false,
  fields: [
    {
      name: 'typeMeaning',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.typeMeaning').d('发票类型'),
    },
    {
      name: 'deductionInvoiceNum',
      type: 'string',
      label: intl
        .get('hivc.certification.model.certification.deductionInvoiceNum')
        .d('抵扣发票份数'),
    },
    {
      name: 'deductionTotalAmount',
      type: 'string',
      label: intl
        .get('hivc.certification.model.certification.deductionTotalAmount')
        .d('抵扣总金额'),
    },
    {
      name: 'deductionTotalValidTax',
      type: 'string',
      label: intl.get('hivc.certification.model.certification.dDuctionTotalVT').d('抵扣有效税额'),
    },
    {
      name: 'nonDeductionInvoiceNum',
      type: 'string',
      label: intl
        .get('hivc.certification.model.certification.nDeductionInvoiceNum')
        .d('不抵扣发票份数'),
    },
    {
      name: 'nonDeductionTotalAmount',
      type: 'string',
      label: intl
        .get('hivc.certification.model.certification.nDeductionTotalAmount')
        .d('不抵扣总金额'),
    },
    {
      name: 'nonDeductionTotalValidTax',
      type: 'string',
      label: intl
        .get('hivc.certification.model.certification.nDeductionTotalVT')
        .d('不抵扣有效税额'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { id },
      } = dataSet;
      return {
        url: `${apiPrefix}/auth-results/${id}/detail`,
        method: 'GET',
      };
    },
  },
});

export { tableDS, detailDS, detailTableDS };
