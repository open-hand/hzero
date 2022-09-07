import intl from 'utils/intl';
import { HZERO_CHG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import CodeConstants from '@/constants/CodeConstants';
import { debitTableDS, rechargeTableDS } from '@/stores/commonDS';
import { ACCOUNT_BALANCE_CONSTANT } from '@/constants/constants';

const prefix = 'hchg.accountBalance.model.accountBalance';
const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';
const accountBalanceTableDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  primaryKey: 'balanceId',
  queryFields: [
    {
      name: 'accountNum',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'string',
    },
    {
      name: 'accountType',
      label: intl.get(`${prefix}.accountType`).d('账户类型'),
      type: 'string',
      lookupCode: CodeConstants.AccountType,
    },
    {
      name: 'accountName',
      label: intl.get(`${prefix}.accountName`).d('账户名称'),
      type: 'string',
    },
    {
      name: 'enabledFlag',
      label: intl.get(`${prefix}.enabledFlag`).d('是否启用'),
      type: 'number',
      lookupCode: CodeConstants.EnabledFlag,
    },
  ],
  fields: [
    {
      name: 'sequence',
      label: intl.get(`${prefix}.sequence`).d('序号'),
      type: 'number',
    },
    {
      name: 'accountNum',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'string',
    },
    {
      name: 'accountType',
      label: intl.get(`${prefix}.accountType`).d('账户类型'),
      type: 'string',
      lookupCode: CodeConstants.AccountType,
    },
    {
      name: 'accountName',
      label: intl.get(`${prefix}.accountName`).d('账户名称'),
      type: 'string',
    },
    {
      name: 'balanceAmount',
      label: intl.get(`${prefix}.balanceAmount`).d('账户余额'),
      type: 'number',
    },
    {
      name: 'enabledFlag',
      label: intl.get(`${prefix}.enabledFlag`).d('是否启用'),
      type: 'number',
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_CHG}/v1${level}/account-balances`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: (config) => {
      const { data } = config;
      const { _type } = data[0];
      const url = `${HZERO_CHG}/v1${level}/account-balances/${_type}`;
      const list = data.map((item) => item.balanceId);
      return {
        url,
        data: list,
        method: 'PUT',
      };
    },
  },
};

const createFormDS = {
  primaryKey: 'balanceId',
  autoQuery: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'accountNum',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'string',
      format: 'uppercase',
      pattern: CODE_UPPER,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'accountType',
      label: intl.get(`${prefix}.accountType`).d('账户类型'),
      type: 'string',
      required: true,
      lookupCode: CodeConstants.AccountType,
    },
    {
      name: 'accountLov',
      required: true,
      dynamicProps: ({ record }) => {
        const isUser = record.get('accountType') === ACCOUNT_BALANCE_CONSTANT.USER;
        return {
          type: 'object',
          noCache: true,
          ignore: 'always',
          label: isUser
            ? intl.get(`${prefix}.user`).d('关联用户')
            : intl.get(`${prefix}.tenant`).d('关联租户'),
          lovCode: isUser ? CodeConstants.User : CodeConstants.TenantEncrypt,
          valueField: isUser ? 'id' : 'tenantId',
          textField: isUser ? 'realName' : 'tenantName',
        };
      },
    },
    {
      name: 'accountId',
      type: 'string',
      dynamicProps: ({ record }) => {
        const isUser = record.get('accountType') === ACCOUNT_BALANCE_CONSTANT.USER;
        return {
          bind: isUser ? 'accountLov.id' : 'accountLov.tenantId',
        };
      },
    },
    {
      name: 'accountName',
      label: intl.get(`${prefix}.accountName`).d('账户名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'enabledFlag',
      label: intl.get(`${prefix}.enabledFlag`).d('是否启用'),
      type: 'number',
      lookupCode: CodeConstants.EnabledFlag,
      transformRequest: (value) => value === 1,
    },
    {
      name: 'balanceAmount',
      label: intl.get(`${prefix}.balanceAmount`).d('账户余额'),
      type: 'number',
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
  transport: {
    create: ({ data }) => ({
      url: `${HZERO_CHG}/v1${level}/account-balances`,
      method: 'POST',
      data: data[0],
    }),
  },
};

const detailFormDS = {
  primaryKey: 'balanceId',
  autoQuery: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'accountNum',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'string',
    },
    {
      name: 'accountType',
      label: intl.get(`${prefix}.accountType`).d('账户类型'),
      type: 'string',
      lookupCode: CodeConstants.AccountType,
    },
    {
      name: 'accountName',
      label: intl.get(`${prefix}.accountName`).d('账户名称'),
      type: 'string',
    },
    {
      name: 'enabledFlag',
      label: intl.get(`${prefix}.enabledFlag`).d('是否启用'),
      type: 'number',
      lookupCode: CodeConstants.EnabledFlag,
    },
    {
      name: 'balanceAmount',
      label: intl.get(`${prefix}.balanceAmount`).d('账户余额'),
      type: 'number',
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { balanceId } = data;
      return {
        url: `${HZERO_CHG}/v1${level}/account-balances/${balanceId}`,
        params: { ...data },
        method: 'GET',
      };
    },
  },
};

const debitRecordTableDS = {
  ...debitTableDS,
  autoQuery: false,
};

const rechargeRecordTableDS = {
  ...rechargeTableDS,
  autoQuery: false,
};

const rechargeFormDS = {
  primaryKey: 'balanceId',
  autoQuery: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'accountLov',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'object',
      required: true,
      lovCode: isTenantRoleLevel()
        ? CodeConstants.EffectiveAccountOrg
        : CodeConstants.EffectiveAccount,
      noCache: true,
      ignore: 'always',
      valueField: 'balanceId',
      textField: 'accountNum',
    },
    {
      name: 'accountNum',
      type: 'string',
      bind: 'accountLov.accountNum',
    },
    {
      name: 'balanceId',
      type: 'string',
      bind: 'accountLov.balanceId',
    },
    {
      name: 'accountType',
      label: intl.get(`${prefix}.accountType`).d('账户类型'),
      type: 'string',
      lookupCode: CodeConstants.AccountType,
      bind: 'accountLov.accountType',
    },
    {
      name: 'accountName',
      label: intl.get(`${prefix}.accountName`).d('账户名称'),
      type: 'string',
      bind: 'accountLov.accountName',
    },
    {
      name: 'enabledFlag',
      label: intl.get(`${prefix}.enabledFlag`).d('是否启用'),
      type: 'number',
      lookupCode: CodeConstants.EnabledFlag,
      bind: 'accountLov.enabledFlag',
    },
    {
      name: 'balanceAmount',
      label: intl.get(`${prefix}.balanceAmount`).d('账户余额'),
      type: 'number',
      bind: 'accountLov.balanceAmount',
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
      bind: 'accountLov.remark',
    },
    {
      name: 'rechargeAmount',
      label: intl.get(`${prefix}.rechargeAmount`).d('充值金额'),
      type: 'number',
      required: true,
    },
    {
      name: 'upperAmount',
      label: intl.get(`${prefix}.upperAmount`).d('金额大写'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { balanceId } = data;
      return {
        url: `${HZERO_CHG}/v1${level}/account-balances/${balanceId}`,
        params: { ...data },
        method: 'GET',
      };
    },
  },
};

export {
  accountBalanceTableDS,
  createFormDS,
  detailFormDS,
  debitRecordTableDS,
  rechargeRecordTableDS,
  rechargeFormDS,
};
