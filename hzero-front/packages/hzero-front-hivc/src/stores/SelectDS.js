/*
 * Select 发票勾选结果
 * @date: 2020-04-28
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import moment from 'moment';
import intl from 'utils/intl';
import { HZERO_INVOICE } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';

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
      label: intl.get('hivc.select.model.select.batchNo').d('批次号'),
    },
    {
      name: 'tickDateFrom',
      type: 'date',
      label: intl.get('hivc.select.model.select.tickDateFrom').d('勾选日期从'),
      // transformRequest: value => moment(value).format(DEFAULT_DATE_FORMAT),
    },
    {
      name: 'tickDateTo',
      type: 'date',
      label: intl.get('hivc.select.model.select.tickDateTo').d('勾选日期至'),
      // transformRequest: value => moment(value).format(DEFAULT_DATE_FORMAT),
    },
  ],
  fields: [
    {
      name: 'buyerNo',
      type: 'string',
      label: intl.get('hivc.select.model.select.buyerNo').d('纳税人识别号'),
    },
    {
      name: 'batchNo',
      type: 'string',
      label: intl.get('hivc.select.model.select.batchNo').d('批次号'),
    },
    {
      name: 'tickDate',
      type: 'date',
      label: intl.get('hivc.select.model.select.tickDate').d('勾选日期'),
    },
    {
      name: 'tickCount',
      type: 'string',
      label: intl.get('hivc.select.model.select.tickCount').d('勾选量'),
    },
    {
      name: 'allSuccess',
      type: 'boolean',
      label: intl.get('hivc.select.model.select.allSuccess').d('是否全部勾选成功'),
    },
  ],
  transport: {
    read: ({ data }) => {
      const tickDateFrom =
        data.tickDateFrom && moment(data.tickDateFrom).format(DEFAULT_DATE_FORMAT);
      const tickDateTo = data.tickDateTo && moment(data.tickDateTo).format(DEFAULT_DATE_FORMAT);
      return {
        url: `${apiPrefix}/tick-results`,
        method: 'GET',
        data: {
          ...data,
          tickDateFrom,
          tickDateTo,
        },
      };
    },
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
      label: intl.get('hivc.select.model.select.buyerNo').d('纳税人识别号'),
    },
    {
      name: 'batchNo',
      type: 'string',
      label: intl.get('hivc.select.model.select.batchNo').d('批次号'),
    },
    {
      name: 'tickDate',
      type: 'date',
      label: intl.get('hivc.select.model.select.tickDate').d('勾选日期'),
    },
    {
      name: 'tickCount',
      type: 'string',
      label: intl.get('hivc.select.model.select.tickCount').d('勾选量'),
    },
    {
      name: 'allSuccess',
      type: 'boolean',
      label: intl.get('hivc.select.model.select.allSuccess').d('是否全部勾选成功'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { id },
      } = dataSet;
      return {
        url: `${apiPrefix}/tick-results/${id}`,
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
      name: 'invoiceCode',
      type: 'string',
      label: intl.get('hivc.select.model.select.invoiceCode').d('发票代码'),
    },
    {
      name: 'invoiceNo',
      type: 'string',
      label: intl.get('hivc.select.model.select.invoiceNo').d('发票号码'),
    },
    {
      name: 'invoiceDate',
      type: 'date',
      format: 'YYYY-MM-DD',
      label: intl.get('hivc.select.model.select.invoiceDate').d('开票日期'),
    },
    {
      name: 'effectiveTax',
      type: 'string',
      label: intl.get('hivc.select.model.select.effectiveTax').d('有效税额'),
    },
    {
      name: 'tickFlag',
      type: 'boolean',
      label: intl.get('hivc.select.model.select.tickFlag').d('勾选标志'),
    },
    {
      name: 'requestStatus',
      type: 'number',
      label: intl.get('hivc.select.model.select.requestStatus').d('请求状态'),
    },
    {
      name: 'requestMessage',
      type: 'string',
      label: intl.get('hivc.select.model.select.requestMessage').d('失败原因'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { id },
      } = dataSet;
      return {
        url: `${apiPrefix}/tick-results/${id}/lines`,
        method: 'GET',
      };
    },
  },
});

export { tableDS, detailDS, detailTableDS };
