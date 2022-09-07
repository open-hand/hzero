import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import CodeConstants from '@/constants/CodeConstants';
import { rechargeTableDS } from '@/stores/commonDS';

const prefix = 'hchg.rechargeRecord.model.rechargeRecord';

export default () => ({
  ...rechargeTableDS,
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
    ...rechargeTableDS.fields,
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
      name: 'requestTimeFrom',
      label: intl.get(`${prefix}.requestTimeFrom`).d('充值发起时间从'),
      type: 'dateTime',
      dynamicProps: {
        max: ({ record }) => record.get('requestTimeTo'),
      },
    },
    {
      name: 'requestTimeTo',
      label: intl.get(`${prefix}.requestTimeTo`).d('充值发起时间至'),
      type: 'dateTime',
      dynamicProps: {
        min: ({ record }) => record.get('requestTimeFrom'),
      },
    },
    {
      name: 'rechargeChannel',
      label: intl.get(`${prefix}.rechargeChannel`).d('充值渠道'),
      type: 'string',
      lookupCode: CodeConstants.PaymentChannel,
    },
    {
      name: 'rechargedTimeFrom',
      label: intl.get(`${prefix}.rechargedTimeFrom`).d('充值完成时间从'),
      type: 'dateTime',
      dynamicProps: {
        max: ({ record }) => record.get('rechargedTimeFrom'),
      },
    },
    {
      name: 'rechargedTimeTo',
      label: intl.get(`${prefix}.rechargedTimeTo`).d('充值完成时间至'),
      type: 'dateTime',
      dynamicProps: {
        min: ({ record }) => record.get('rechargedTimeTo'),
      },
    },
    {
      name: 'transactionSerial',
      label: intl.get(`${prefix}.transactionSerial`).d('交易流水号'),
      type: 'string',
    },
    {
      name: 'rechargeStatus',
      label: intl.get(`${prefix}.rechargeStatus`).d('充值状态'),
      type: 'string',
      lookupCode: CodeConstants.AccountRechargeStatus,
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
});
