import { HZERO_HEBK } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

const tableDs = () => ({
  selection: false,
  autoQuery: true,
  queryFields: [
    {
      name: 'bankMark',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankMark').d('账号'),
      lookupCode: 'HEBK.BANK_MARK',
    },
    {
      name: 'businessTypeCode',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.businessTypeCode').d('业务类型'),
      lookupCode: 'HEBK.FUND_TRADE_BIZ.BIZ_TYPE',
    },
    {
      name: 'statusCode',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.statusCode').d('状态'),
      lookupCode: 'HEBK.FUND_TRADE_BIZ.STATUS',
    },
    {
      name: 'agentAccountName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.agentAccountName').d('被代理户名'),
    },
    {
      name: 'referenceOrder',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.referenceOrder').d('参考单据编号'),
    },

    {
      name: 'transactionSerialNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.transactionSerialNumber')
        .d('交易流水号'),
    },
    {
      name: 'tradeAmountFrom',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAmountFrom').d('交易金额从'),
    },
    {
      name: 'tradeAmountTo',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAmountTo').d('交易金额至'),
    },
    {
      name: 'tradeDateFrom',
      type: 'date',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeDateFrom').d('交易日期从'),
    },
    {
      name: 'tradeDateTo',
      type: 'date',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeDateTo').d('交易日期至'),
    },
    {
      name: 'oppositeAccountNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountNumber')
        .d('交易对方账号'),
    },
  ],
  fields: [
    {
      name: 'directionMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.directionMeaning').d('来往账标识'),
    },
    {
      name: 'businessTypeMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.businessTypeMeaning').d('业务类型'),
    },
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankMarkMeaning').d('银行标识'),
    },
    {
      name: 'referenceOrder',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.referenceOrder').d('参考单据编号'),
    },
    {
      name: 'tradeAmount',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAmount').d('交易金额'),
    },
    {
      name: 'currency',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.currency').d('交易货币'),
    },
    {
      name: 'tradeDate',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeDate').d('交易日期'),
    },
    {
      name: 'accountBalance',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.accountBalance').d('交易后余额'),
    },
    {
      name: 'statusMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.statusMeaning').d('状态'),
    },
    {
      name: 'bankStatusDesc',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankStatusDesc').d('银行状态说明'),
    },
    {
      name: 'oppositeAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountBankName')
        .d('交易对方银行'),
    },
    {
      name: 'oppositeAccountName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountName')
        .d('交易对方户名'),
    },
    {
      name: 'oppositeAccountNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountNumber')
        .d('交易对方账号'),
    },
    {
      name: 'agentAccountNumber',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.agentAccountNumber').d('被代理账号'),
    },
    {
      name: 'agentAccountName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.agentAccountName').d('被代理户名'),
    },
    {
      name: 'tradeAccountBankName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAccountBankName').d('交易银行'),
    },
    {
      name: 'tradeAccountName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAccountName').d('交易户名'),
    },
    {
      name: 'tradeAccountNumber',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAccountNumber').d('交易账号'),
    },
    {
      name: 'transactionSerialNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.transactionSerialNumber')
        .d('交易流水号'),
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/funds/trade-businesses`;
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
      name: 'directionMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.directionMeaning').d('来往账标识'),
      required: true,
      lookupCode: 'HEBK.BANK_MARK',
    },
    {
      name: 'businessTypeMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.businessTypeMeaning').d('业务类型'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'bankBusinessType',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankBusinessType').d('银行业务类型'),
      maxLength: 10,
    },
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankMarkMeaning').d('银行标识'),
      maxLength: 10,
    },
    {
      name: 'referenceOrder',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.referenceOrder').d('参考单据编号'),
      maxLength: 240,
    },
    {
      name: 'tradeAmount',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAmount').d('交易金额'),
      maxLength: 10,
    },
    {
      name: 'currency',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.currency').d('交易货币'),
      required: true,
      maxLength: 30,
    },
    {
      name: 'tradeDate',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeDate').d('交易日期'),
      lookupCode: 'HEBK.BANK_ACCOUNT_GPT',
    },
    {
      name: 'statusMeaning',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.statusMeaning').d('状态'),
      required: true,
      maxLength: 10,
    },
    {
      name: 'bankStatusCode',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankStatusCode').d('银行状态'),
      required: true,
      maxLength: 160,
    },
    {
      name: 'bankStatusDesc',
      type: 'boolean',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.bankStatusDesc').d('银行状态说明'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
      require: true,
    },
    {
      name: 'tradeBankCode',
      type: 'number',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeBankCode').d('交易联行号'),
    },
    {
      name: 'tradeAccountNumber',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAccountNumber').d('交易账号'),
    },

    {
      name: 'tradeAccountName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAccountName').d('交易户名'),
    },
    {
      name: 'tradeAccountBankName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.tradeAccountBankName').d('交易银行'),
    },
    {
      name: 'oppositeBankCode',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeBankCode')
        .d('交易对方联行号'),
    },
    {
      name: 'oppositeAccountNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountNumber')
        .d('交易对方账号'),
    },
    {
      name: 'oppositeAccountName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountName')
        .d('交易对方户名'),
    },
    {
      name: 'oppositeAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountBankName')
        .d('交易对方银行'),
    },
    {
      name: 'oppositeBankCode',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeBankCode')
        .d('交易对方联行号'),
    },
    {
      name: 'oppositeAccountNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountNumber')
        .d('交易对方账号'),
    },
    {
      name: 'oppositeAccountName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountName')
        .d('交易对方户名'),
    },
    {
      name: 'oppositeAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.oppositeAccountBankName')
        .d('交易对方银行'),
    },
    {
      name: 'agentBankCode',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.agentBankCode').d('被代理联行号'),
    },
    {
      name: 'agentAccountNumber',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.agentAccountNumber').d('被代理账号'),
    },
    {
      name: 'agentAccountName',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.agentAccountName').d('被代理户名'),
    },
    {
      name: 'agentAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.agentAccountBankName')
        .d('被代理银行'),
    },
    {
      name: 'transactionSerialNumber',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.transactionSerialNumber')
        .d('交易流水号'),
    },

    {
      name: 'accountBalance',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.accountBalance').d('交易后余额'),
    },
    {
      name: 'availableBalance',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.availableBalance').d('可用余额'),
    },
    {
      name: 'frozenAmount',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.frozenAmount').d('冻结金额'),
    },
    {
      name: 'overdrawnAmount',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.overdrawnAmount').d('透支额度'),
    },
    {
      name: 'availableOverdrawnAmount',
      type: 'string',
      label: intl
        .get('hebk.tradeBusiness.model.tradeBusiness.availableOverdrawnAmount')
        .d('可用透支额度'),
    },
    {
      name: 'purpose',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.purpose').d('用途'),
    },
    {
      name: 'postscript',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.postscript').d('附言'),
    },
    {
      name: 'feeAccount',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.feeAccount').d('费用账户'),
    },
    {
      name: 'feeAmount',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.feeAmount').d('费用金额'),
    },
    {
      name: 'feeCurrency',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.feeCurrency').d('费用货币'),
    },
    {
      name: 'interestDate',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.interestDate').d('起息日期'),
    },
    {
      name: 'voucherNumber',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.voucherNumber').d('银行凭证类型'),
    },

    {
      name: 'exchangeRate',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.exchangeRate').d('凭证号码'),
    },
    {
      name: 'exchangeRate',
      type: 'string',
      label: intl.get('hebk.tradeBusiness.model.tradeBusiness.exchangeRate').d('汇率'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { businessId },
      } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/funds/trade-businesses/${businessId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

export { tableDs, detailDs };
