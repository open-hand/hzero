import intl from 'utils/intl';
import { HZERO_CHG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import CodeConstants from '@/constants/CodeConstants';

const prefix = 'hchg.billCbRecord.model.billCbRecord';
const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

export default () => ({
  autoQuery: true,
  pageSize: 10,
  selection: 'multiple',
  primaryKey: 'callbackLogId',
  queryFields: [
    {
      name: 'sourceSystemLov',
      label: intl.get(`${prefix}.sourceSystem`).d('来源系统'),
      type: 'object',
      noCache: true,
      ignore: 'always',
      valueField: 'sourceSystemId',
      textField: 'systemNum',
      lovCode: isTenantRoleLevel() ? 'HCHG.SOURCE_SYSTEM.ORG' : CodeConstants.SourceSystem,
    },
    {
      name: 'sourceSystemId',
      type: 'string',
      bind: 'sourceSystemLov.sourceSystemId',
    },
    {
      name: 'billLov',
      label: intl.get(`${prefix}.bill`).d('账单'),
      type: 'object',
      noCache: true,
      ignore: 'always',
      valueField: 'headerId',
      textField: 'billNum',
      lovCode: isTenantRoleLevel() ? 'HCHG.BILL_HEADER.ORG' : CodeConstants.BillHeader,
    },
    {
      name: 'billHeaderId',
      type: 'string',
      bind: 'billLov.headerId',
    },
    {
      name: 'callbackStatus',
      label: intl.get(`${prefix}.callbackStatus`).d('回调状态'),
      type: 'string',
      lookupCode: CodeConstants.BillCallbackStatus,
    },
    {
      name: 'callbackUrl',
      label: intl.get(`${prefix}.callbackUrl`).d('账单回调URL'),
      type: 'string',
    },
    {
      name: 'callbackTimeFrom',
      label: intl.get(`${prefix}.callbackTimeFrom`).d('回调时间从'),
      type: 'dateTime',
      dynamicProps: {
        max: ({ record }) => record.get('callbackTimeTo'),
      },
    },
    {
      name: 'callbackTimeTo',
      label: intl.get(`${prefix}.callbackTimeTo`).d('回调时间至'),
      type: 'dateTime',
      dynamicProps: {
        min: ({ record }) => record.get('callbackTimeFrom'),
      },
    },
  ],
  fields: [
    {
      name: 'sequence',
      label: intl.get(`${prefix}.sequence`).d('序号'),
      type: 'number',
    },
    {
      name: 'systemNum',
      label: intl.get(`${prefix}.systemNum`).d('系统编码'),
      type: 'string',
    },
    {
      name: 'billNum',
      label: intl.get(`${prefix}.billNum`).d('账单编码'),
      type: 'string',
    },
    {
      name: 'callbackUrl',
      label: intl.get(`${prefix}.callbackUrl`).d('账单回调URL'),
      type: 'string',
    },
    {
      name: 'callbackStatus',
      label: intl.get(`${prefix}.callbackStatus`).d('回调状态'),
      type: 'string',
    },
    {
      name: 'callbackCount',
      label: intl.get(`${prefix}.callbackCount`).d('回调次数'),
      type: 'number',
    },
    {
      name: 'callbackTime',
      label: intl.get(`${prefix}.callbackTime`).d('回调时间'),
      type: 'dateTime',
    },
    {
      name: 'callbackMessage',
      label: intl.get(`${prefix}.callbackMessage`).d('回调消息'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_CHG}/v1${level}/bill-callback-logs`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: (config) => {
      const { data } = config;
      const url = `${HZERO_CHG}/v1${level}/bill-callback-logs/callback`;
      const list = data.map((item) => item.callbackLogId);
      return {
        url,
        data: list,
        method: 'PUT',
      };
    },
  },
});
