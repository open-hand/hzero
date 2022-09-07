import { HZERO_HEBK } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

const tableDs = () => ({
  selection: false,
  autoQuery: true,
  queryFields: [
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.accountNumber').d('账号'),
    },
  ],
  fields: [
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankMarkMeaning').d('银行标识'),
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.accountNumber').d('账号'),
    },
    {
      name: 'bankCode',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankCode').d('联行号'),
    },
    {
      name: 'bankNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankNumber').d('账户所属银行号'),
    },
    {
      name: 'accountName',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.accountName').d('账户名称'),
    },
    {
      name: 'groupNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.groupNumber').d('账户组编号'),
    },
    {
      name: 'bankName',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankName').d('开户行名称'),
    },
    {
      name: 'childrenFlag',
      type: 'boolean',
      label: intl.get('hebk.bankAccount.model.bankAccount.childrenFlag').d('是否有子账户'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/bank-accounts`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

const detailDs = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'bankMark',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankMark').d('银行标识'),
      required: true,
      lookupCode: 'HEBK.BANK_MARK',
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.accountNumber').d('账号'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'bankCode',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankCode').d('联行号'),
      maxLength: 10,
    },
    {
      name: 'bankNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankNumber').d('账户所属银行'),
      maxLength: 10,
    },
    {
      name: 'accountName',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.sslEnaaccountNamebledFlag').d('账户名称'),
      maxLength: 240,
    },
    {
      name: 'currencyCode',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.currencyCode').d('货币'),
      maxLength: 10,
    },
    {
      name: 'groupNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.groupNumber').d('账户组编号'),
      maxLength: 30,
    },
    {
      name: 'groupProductType',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.groupProductType').d('账户组产品类型'),
      lookupCode: 'HEBK.BANK_ACCOUNT_GPT',
    },
    {
      name: 'branchId',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.branchId').d('开户行机构号'),
      maxLength: 10,
    },
    {
      name: 'bankName',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankName').d('开户行名称'),
      maxLength: 160,
    },
    {
      name: 'childrenFlag',
      type: 'boolean',
      label: intl.get('hebk.bankAccount.model.bankAccount.childrenFlag').d('是否有子账户'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
      require: true,
    },
    {
      name: 'parentAccountIdLov',
      type: 'object',
      label: intl.get('hebk.bankAccount.model.bankAccount.parentAccountIdLov').d('父账户ID'),
      maxLength: 19,
      lovCode: 'HEBK.BANK_ACCOUNT_LIST',
      ignore: 'always',
      noCache: true,
      textField: 'accountName',
    },
    {
      name: 'parentAccountId',
      type: 'string',
      bind: 'parentAccountIdLov.accountId',
    },
    {
      name: 'parentAccountName',
      type: 'string',
      bind: 'parentAccountIdLov.accountName',
    },
  ],
  transport: {
    create: ({ config, data }) => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/bank-accounts`;
      return {
        ...config,
        url,
        method: 'POST',
        data: data[0],
      };
    },
    update: ({ config, data }) => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/bank-accounts/${data[0].accountId}`;
      return {
        ...config,
        url,
        method: 'PUT',
        data: data[0],
      };
    },
    read: ({ dataSet }) => {
      const { accountId } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/bank-accounts/${accountId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

const balanceDs = () => ({
  selection: false,
  autoQuery: false,
  fields: [
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankMarkMeaning').d('银行标识'),
    },
    {
      name: 'bankCode',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bankCode').d('联行号'),
    },
    {
      name: 'accountName',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.accountName').d('账户名称'),
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.accountNumber').d('账号'),
    },
    {
      name: 'currencyCode',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.currencyCode').d('货币'),
    },
    {
      name: 'bookBalance',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.bookBalance').d('账面余额'),
    },
    {
      name: 'availableBalance',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.availableBalance').d('有效余额'),
    },
    {
      name: 'overdrawnAmount',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.overdrawnAmount').d('透支限额'),
    },
    {
      name: 'creditLoadAmount',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.creditLoadAmount').d('圈存金额'),
    },
    {
      name: 'frozenAmount',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.frozenAmount').d('冻结金额'),
    },
    {
      name: 'effectiveUsedQuota',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.effectiveUsedQuota').d('有效已用额度'),
    },
    {
      name: 'effectiveUnusedQuota',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.effectiveUnusedQuota').d('有效未用额度'),
    },
    {
      name: 'excessStartDate',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.excessStartDate').d('超额生效日'),
    },
    {
      name: 'excessEndDate',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.excessEndDate').d('超额到期日'),
    },
    {
      name: 'effectiveQuota',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.effectiveQuota').d('超额额度'),
    },
    {
      name: 'excessUsedQuota',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.excessUsedQuota').d('超额已用超额'),
    },
    {
      name: 'excessAvailableQuota',
      type: 'string',
      label: intl.get('hebk.bankAccount.model.bankAccount.excessAvailableQuota').d('超额可用超额'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { accountNumber, bankMark },
      } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/funds/query-day-balance?bankMark=${bankMark}`;
      return {
        url,
        method: 'POST',
        data: [
          {
            accountNumber,
          },
        ],
      };
    },
  },
});

export { tableDs, detailDs, balanceDs };
