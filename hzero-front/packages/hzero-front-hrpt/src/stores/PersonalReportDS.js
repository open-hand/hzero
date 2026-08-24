/**
 * 个人报表请求
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2020/1/3
 * @copyright HAND ® 2019
 */

import intl from 'utils/intl';
import { HZERO_RPT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

export const personalReportDS = () => ({
  autoQuery: true,
  selection: false,
  primaryKey: 'requestId',
  queryFields: [
    {
      name: 'reportName',
      label: intl.get('hrpt.common.report.reportName').d('报表名称'),
    },
    {
      name: 'requestStatus',
      label: intl.get('hrpt.common.view.requestStatus').d('运行状态'),
      lookupCode: 'HRPT.REQUEST_STATUS',
    },
    {
      name: 'startDate',
      label: intl.get('hrpt.common.view.startTime').d('开始时间'),
      type: 'dateTime',
      dynamicProps: {
        max: ({ record }) => record.get('endDate'),
      },
    },
    {
      name: 'endDate',
      label: intl.get('hrpt.common.view.endTime').d('结束时间'),
      type: 'dateTime',
      dynamicProps: {
        min: ({ record }) => record.get('startDate'),
      },
    },
  ],
  fields: [
    {
      name: 'orderSeq',
      label: intl.get('hrpt.common.view.serialNumber').d('序号'),
    },
    {
      name: 'reportCode',
      label: intl.get('hrpt.common.report.reportCode').d('报表代码'),
    },
    {
      name: 'reportName',
      label: intl.get('hrpt.common.report.reportName').d('报表名称'),
    },
    {
      name: 'requestStatusMeaning',
      label: intl.get('hrpt.common.view.requestStatus').d('运行状态'),
    },
    {
      name: 'startDate',
      label: intl.get('hrpt.common.view.startTime').d('开始时间'),
    },
    {
      name: 'endDate',
      label: intl.get('hrpt.common.view.endTime').d('结束时间'),
    },
    {
      name: 'remark',
      label: intl.get('hrpt.personalReport.model.reportQuery.remark').d('备注'),
    },
  ],
  transport: {
    read({ config, data }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_RPT}/v1/${getCurrentOrganizationId()}/report-requests/user`
        : `${HZERO_RPT}/v1/report-requests/user`;
      return {
        url,
        ...config,
        method: 'GET',
        query: data,
      };
    },
  },
});

/**
 * @param {number} requestId - 请求id
 */
export const personalReportDetailDS = (requestId) => ({
  autoQuery: true,
  selection: false,
  primaryKey: 'requestId',
  fields: [
    {
      name: 'requester',
      label: intl.get('hrpt.personalReport.model.reportQuery.requester').d('请求人名称'),
    },
    {
      name: 'requestParam',
      label: intl.get('hrpt.personalReport.model.reportQuery.requestParam').d('请求参数'),
    },
    {
      name: 'reportCode',
      label: intl.get('hrpt.common.report.reportCode').d('报表代码'),
    },
    {
      name: 'reportName',
      label: intl.get('hrpt.common.report.reportName').d('报表名称'),
    },
    {
      name: 'startDate',
      label: intl.get('hrpt.common.view.startTime').d('开始时间'),
    },
    {
      name: 'endDate',
      label: intl.get('hrpt.common.view.endTime').d('结束时间'),
    },
    {
      name: 'requestStatusMeaning',
      label: intl.get('hrpt.common.view.requestStatus').d('运行状态'),
    },
    {
      name: 'requestMessage',
      label: intl.get('hrpt.personalReport.model.reportQuery.requestMessage').d('请求消息'),
    },
  ],
  transport: {
    read({ config, data }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_RPT}/v1/${getCurrentOrganizationId()}/report-requests/${requestId}`
        : `${HZERO_RPT}/v1/report-requests/${requestId}`;
      return {
        url,
        ...config,
        method: 'GET',
        query: data,
      };
    },
  },
});
