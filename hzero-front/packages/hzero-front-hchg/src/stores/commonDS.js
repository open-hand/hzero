import intl from 'utils/intl';
import { HZERO_CHG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import CodeConstants from '@/constants/CodeConstants';

const prefix = 'hchg.debitRecord.model.debitRecord';
const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const debitTableDS = {
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'debitId',
  fields: [
    {
      name: 'sequence',
      label: intl.get(`${prefix}.sequence`).d('序号'),
      type: 'number',
    },
    {
      name: 'debitTime',
      label: intl.get(`${prefix}.debitTime`).d('扣款时间'),
      type: 'dateTime',
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
      name: 'debitAmount',
      label: intl.get(`${prefix}.debitAmount`).d('扣款金额'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'balanceAmount',
      label: intl.get(`${prefix}.balanceAmount`).d('账户余额'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'businessCode',
      label: intl.get(`${prefix}.businessType`).d('业务类型'),
      type: 'string',
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
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
    {
      name: 'processMessage',
      label: intl.get(`${prefix}.processMessage`).d('处理消息'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_CHG}/v1${level}/account-debits`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
        transformResponse: (response) => {
          const res = JSON.parse(response);
          const list = res.content.map((item) => {
            const { accountBalance = {}, ...other } = item;
            const { accountName, accountNum } = accountBalance;
            return {
              ...other,
              accountName,
              accountNum,
            };
          });
          return {
            ...res,
            content: list,
          };
        },
      };
    },
  },
};

const rechargeTableDS = {
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'rechargeId',
  fields: [
    {
      name: 'sequence',
      label: intl.get(`${prefix}.sequence`).d('序号'),
      type: 'number',
    },
    {
      name: 'requestTime',
      label: intl.get(`${prefix}.requestTime`).d('充值发起时间'),
      type: 'dateTime',
    },
    {
      name: 'rechargeChannel',
      label: intl.get(`${prefix}.rechargeChannel`).d('充值渠道'),
      type: 'string',
      lookupCode: CodeConstants.PaymentChannel,
    },
    {
      name: 'rechargeAmount',
      label: intl.get(`${prefix}.rechargeAmount`).d('充值金额'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'rechargedTime',
      label: intl.get(`${prefix}.rechargeTime`).d('充值完成时间'),
      type: 'dateTime',
    },
    {
      name: 'transactionSerial',
      label: intl.get(`${prefix}.transactionSerial`).d('交易流水号'),
      type: 'string',
    },
    {
      name: 'balanceAmount',
      label: intl.get(`${prefix}.balanceAmount`).d('账户余额'),
      type: 'number',
      step: 0.01,
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
    {
      name: 'processMessage',
      label: intl.get(`${prefix}.processMessage`).d('处理消息'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_CHG}/v1${level}/account-recharges`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
        transformResponse: (response) => {
          const res = JSON.parse(response);
          const list = res.content.map((item) => {
            const { accountBalance = {}, ...other } = item;
            const { accountName, accountNum } = accountBalance;
            return {
              ...other,
              accountName,
              accountNum,
            };
          });
          return {
            ...res,
            content: list,
          };
        },
      };
    },
  },
};

export { debitTableDS, rechargeTableDS };
