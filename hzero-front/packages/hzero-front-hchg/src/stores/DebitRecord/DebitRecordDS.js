import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { debitTableDS } from '@/stores/commonDS';
import CodeConstants from '@/constants/CodeConstants';

const prefix = 'hchg.debitRecord.model.debitRecord';

export default () => ({
  ...debitTableDS,
  fields: [
    {
      name: 'accountName',
      label: intl.get(`${prefix}.accountName`).d('账户名'),
      type: 'string',
    },
    {
      name: 'accountNum',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'string',
    },
    ...debitTableDS.fields,
  ],
  queryFields: [
    {
      name: 'accountLov',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'object',
      noCache: true,
      ignore: 'always',
      lovCode: isTenantRoleLevel() ? CodeConstants.AccountOrg : CodeConstants.Account,
      valueField: 'accountNum',
      textField: 'accountNum',
    },
    {
      name: 'balanceId',
      type: 'string',
      bind: 'accountLov.balanceId',
    },
    {
      name: 'accountNum',
      type: 'string',
      bind: 'accountLov.accountNum',
    },
    {
      name: 'costName',
      label: intl.get(`${prefix}.costName`).d('费用名称'),
      type: 'string',
    },
    {
      name: 'transactionSerial',
      label: intl.get(`${prefix}.transactionSerial`).d('交易流水号'),
      type: 'string',
    },
    {
      name: 'debitTimeFrom',
      label: intl.get(`${prefix}.debitTimeFrom`).d('扣款时间从'),
      type: 'dateTime',
      dynamicProps: {
        max: ({ record }) => record.get('debitTimeTo'),
      },
    },
    {
      name: 'debitTimeTo',
      label: intl.get(`${prefix}.debitTimeTo`).d('扣款时间至'),
      type: 'dateTime',
      dynamicProps: {
        min: ({ record }) => record.get('debitTimeFrom'),
      },
    },
    {
      name: 'businessKey',
      label: intl.get(`${prefix}.businessKey`).d('业务唯一键'),
      type: 'string',
    },
    {
      name: 'debitStatus',
      label: intl.get(`${prefix}.debitStatus`).d('扣款状态'),
      type: 'string',
      lookupCode: CodeConstants.AccountDebitStatus,
    },
    {
      name: 'processMessage',
      label: intl.get(`${prefix}.processMessage`).d('处理消息'),
      type: 'string',
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
});
