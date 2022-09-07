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
      label: intl.get('hebk.draft.model.draft.bankMark').d('银行标识'),
      lookupCode: 'HEBK.BANK_MARK',
    },
    {
      name: 'draftType',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftType').d('票据类别'),
      lookupCode: 'HEBK.DRAFT_TYPE',
    },
    {
      name: 'draftNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftNumber').d('票据号码'),
    },
  ],
  fields: [
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankMark').d('银行标识'),
    },
    {
      name: 'bankCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankCode').d('联行号'),
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accountNumber').d('账号'),
    },
    {
      name: 'draftTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftTypeMeaning').d('票据类别'),
    },
    {
      name: 'draftNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftNumber').d('票据号码'),
    },
    {
      name: 'draftStatusMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftStatusMeaning').d('票据状态'),
    },
    {
      name: 'amount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.amount').d('票据金额'),
    },
    {
      name: 'date',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.date').d('出票日期'),
    },
    {
      name: 'dueDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dueDate').d('到期日'),
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/drafts`;
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
      name: 'drawer',
      type: 'object',
    },
    {
      name: 'acceptor',
      type: 'object',
    },
    {
      name: 'payee',
      type: 'object',
    },
    {
      name: 'acceptorInfo',
      type: 'object',
    },
    {
      name: 'discountInfo',
      type: 'object',
    },
    {
      name: 'promptPayInfo',
      type: 'object',
    },
    {
      name: 'guaranteeInfo',
      type: 'object',
    },
    {
      name: 'endorseInfo',
      type: 'object',
    },
    {
      name: 'pledgeInfo',
      type: 'object',
    },
    {
      name: 'recourseInfo',
      type: 'object',
    },
    {
      name: 'creditInfo',
      type: 'object',
    },
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankMark').d('银行标识'),
    },
    {
      name: 'bankCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankCode').d('联行号'),
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accountNumber').d('账号'),
    },
    {
      name: 'draftTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftTypeMeaning').d('票据类别'),
    },
    {
      name: 'draftNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftNumber').d('票据号码'),
    },
    {
      name: 'bankDraftId',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankDraftId').d('银行票据标识号'),
    },
    {
      name: 'draftStatusMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.draftStatusMeaning').d('票据状态'),
    },
    {
      name: 'amount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.amount').d('票据金额'),
    },
    {
      name: 'date',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.date').d('出票日期'),
    },
    {
      name: 'dueDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dueDate').d('到期日'),
    },
    {
      name: 'organizationCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.organizationCode').d('组织机构代码'),
    },
    {
      name: 'bankDraftStatusCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankDraftStatusCode').d('银行票据状态'),
    },
    {
      name: 'bankDraftStatusDesc',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.bankDraftStatusDesc').d('银行票据状态说明'),
    },
    {
      name: 'drawerName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.drawerName').d('出票人名称'),
    },
    {
      name: 'drawerAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.drawerAccountNumber').d('出票人账号'),
    },
    {
      name: 'acceptorName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.acceptorName').d('承兑人名称'),
    },
    {
      name: 'acceptorAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.acceptorAccountNumber').d('承兑人账号'),
    },
    {
      name: 'payeeName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.payeeName').d('收款人名称'),
    },
    {
      name: 'payeeAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.payeeAccountNumber').d('收款人账号'),
    },
    {
      name: 'applicantName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.applicantName').d('申请人名称'),
    },
    {
      name: 'transferFlag',
      type: 'boolean',
      label: intl.get('hebk.draft.model.draft.transferFlag').d('能否转让标识'),
      defaultFlag: 1,
      trueFlag: 1,
      falseFlag: 0,
    },
    {
      name: 'consignFlag',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.consignFlag').d('到期无条件支付委托'),
    },
    {
      name: 'autoDraftFlagMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.autoDraftFlagMeaning').d('自动出票标识'),
    },
    {
      name: 'drawer.organizationCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dOrganizationCode').d('出票人组织机构代码'),
      bind: 'drawer.organizationCode',
    },
    {
      name: 'drawer.drawerType',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dDrawerType').d('出票人类别'),
      bind: 'drawer.drawerType',
    },
    {
      name: 'drawer.accountName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dAccountName').d('出票人名称'),
      bind: 'drawer.accountName',
    },
    {
      name: 'drawer.organizationId',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dOrganizationId').d('出票人组织机构代码'),
      bind: 'drawer.organizationCode',
    },
    {
      name: 'drawer.accountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dAccountBankNumber').d('出票人开户行行号'),
      bind: 'drawer.accountBankNumber',
    },
    {
      name: 'drawer.accountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dAccountBankName').d('出票人开户行名称'),
      bind: 'drawer.drawer.accountBankName',
    },
    {
      name: 'drawer.ratingAgency',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dRatingAgency').d('出票人评级机构'),
      bind: 'drawer.ratingAgency',
    },
    {
      name: 'drawer.creditRating',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dCreditRating').d('出票人信用等级'),
      bind: 'drawer.creditRating',
    },
    {
      name: 'drawer.ratingDueDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dRatingDueDate').d('评级到期日'),
      bind: 'drawer.ratingDueDate',
    },
    {
      name: 'acceptor.accountName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.aAccountName').d('承兑人名称'),
      bind: 'acceptor.accountName',
    },
    {
      name: 'acceptor.accountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.aAccountNumber').d('承兑人账号'),
      bind: 'acceptor.accountNumber',
    },
    {
      name: 'acceptor.accountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.aAccountBankNumber').d('承兑人开户行行号'),
      bind: 'acceptor.accountBankNumber',
    },
    {
      name: 'acceptor.accountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.aAccountBankName').d('承兑人开户行名称'),
      bind: 'acceptor.accountBankName',
    },
    {
      name: 'payee.accountName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pAccountName').d('收款人名称'),
      bind: 'payee.accountName',
    },
    {
      name: 'payee.accountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pAccountNumber').d('收款人账号'),
      bind: 'payee.accountNumber',
    },
    {
      name: 'payee.accountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.aAccountBankNumber').d('收款人开户行行号'),
      bind: 'payee.accountBankNumber',
    },
    {
      name: 'payee.accountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pAccountBankName').d('收款人开户行名称'),
      bind: 'payee.accountBankName',
    },
    {
      name: 'acceptorInfo.acceptDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accAcceptDate').d('承兑日期'),
      bind: 'acceptorInfo.acceptDate',
    },
    {
      name: 'acceptorInfo.batchNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accBatchNumber').d('批次号'),
      bind: 'acceptorInfo.batchNumber',
    },
    {
      name: 'acceptorInfo.contractNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accContractNumber').d('交易合同编号'),
      bind: 'acceptorInfo.contractNumber',
    },
    {
      name: 'acceptorInfo.invoiceNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accInvoiceNumber').d('发票号码'),
      bind: 'acceptorInfo.invoiceNumber',
    },
    {
      name: 'acceptorInfo.remark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.accRemark').d('备注'),
      bind: 'acceptorInfo.remark',
    },
    {
      name: 'acceptorInfo.contractUsedFlag',
      type: 'boolean',
      label: intl.get('hebk.draft.model.draft.accContractUsedFlag').d('合同是否使用'),
      bind: 'acceptorInfo.contractUsedFlag',
      defaultFlag: 1,
      trueFlag: 1,
      falseFlag: 0,
    },
    {
      name: 'discountInfo.discountType',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dDiscountType').d('贴现种类'),
      bind: 'discountInfo.discountType',
    },
    {
      name: 'discountInfo.discountAppDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dDiscountAppDate').d('贴现申请日期'),
      bind: 'discountInfo.discountAppDate',
    },
    {
      name: 'discountInfo.discountRate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dDiscountRate').d('贴现利率'),
      bind: 'discountInfo.discountRate',
    },
    {
      name: 'discountInfo.discountAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dDiscountAmount').d('贴现实付金额'),
      bind: 'discountInfo.discountAmount',
    },
    {
      name: 'discountInfo.openDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dOpenDate').d('赎回开放日'),
      bind: 'discountInfo.openDate',
    },
    {
      name: 'discountInfo.dueDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dDueDate').d('赎回截止日'),
      bind: 'discountInfo.dueDate',
    },
    {
      name: 'discountInfo.redeemRate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dRedeemRate').d('赎回利率'),
      bind: 'discountInfo.redeemRate',
    },
    {
      name: 'discountInfo.redeemAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dRedeemAmount').d('贴现赎回金额'),
      bind: 'discountInfo.redeemAmount',
    },
    {
      name: 'discountInfo.contractNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dContractNumber').d('交易合同编号'),
      bind: 'discountInfo.contractNumber',
    },
    {
      name: 'discountInfo.invoiceNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dInvoiceNumber').d('发票号码'),
      bind: 'discountInfo.invoiceNumber',
    },
    {
      name: 'discountInfo.onlineFlagMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dOnlineFlagMeaning').d('线上清算标记'),
      bind: 'discountInfo.onlineFlagMeaning',
    },
    {
      name: 'discountInfo.remark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dRemark').d('贴出人备注'),
      bind: 'discountInfo.remark',
    },
    {
      name: 'discountInfo.inAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dInAccountBankNumber').d('入账行号'),
      bind: 'discountInfo.inAccountBankNumber',
    },
    {
      name: 'discountInfo.inAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dInAccountBankName').d('入账行行名'),
      bind: 'discountInfo.inAccountBankName',
    },
    {
      name: 'discountInfo.inAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dInAccountNumber').d('入账账号'),
      bind: 'discountInfo.inAccountNumber',
    },

    {
      name: 'discountInfo.applicantTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dApplicantTypeMeaning').d('贴现申请人类别'),
      bind: 'discountInfo.applicantTypeMeaning',
    },
    {
      name: 'discountInfo.applicantOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dApplicantOrgCode').d('贴现申请人组织机构代码'),
      bind: 'discountInfo.applicantOrgCode',
    },
    {
      name: 'discountInfo.applicantName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dApplicantName').d('贴现申请人名称'),
      bind: 'discountInfo.applicantName',
    },
    {
      name: 'discountInfo.applicantAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dApplicantAccountNumber').d('贴现申请人账号'),
      bind: 'discountInfo.applicantAccountNumber',
    },
    {
      name: 'discountInfo.applicantAccountBankNumber',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.dApplicantAccountBankNumber')
        .d('贴现申请人开户行行号'),
      bind: 'discountInfo.applicantAccountBankNumber',
    },
    {
      name: 'discountInfo.applicantAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dApplicantAccountBankName').d('贴现申请人承接行行号'),
      bind: 'discountInfo.applicantAccountBankName',
    },
    {
      name: 'discountInfo.postName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dPostName').d('贴入人名称'),
      bind: 'discountInfo.postName',
    },
    {
      name: 'discountInfo.postAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dPostAccountNumber').d('贴入人账号'),
      bind: 'discountInfo.postAccountNumber',
    },
    {
      name: 'discountInfo.postAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dPostAccountBankNumber').d('贴入人开户行行号'),
      bind: 'discountInfo.postAccountBankNumber',
    },
    {
      name: 'discountInfo.postAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dPostAccountBankName').d('贴入人开户行名称'),
      bind: 'discountInfo.postAccountBankName',
    },
    {
      name: 'discountInfo.batchNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dBatchNumber').d('批次号'),
      bind: 'discountInfo.batchNumber',
    },
    {
      name: 'discountInfo.agreementNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dAgreementNumber').d('回复贴现协议编号'),
      bind: 'discountInfo.agreementNumber',
    },
    {
      name: 'discountInfo.agreementInterestRate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dAgreementInterestRate').d('协议付息比例'),
      bind: 'discountInfo.agreementInterestRate',
    },
    {
      name: 'discountInfo.businessType',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dBusinessType').d('业务种类'),
      bind: 'discountInfo.businessType',
    },
    {
      name: 'discountInfo.agreementInterest',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dAgreementInterest').d('协议付息利息'),
      bind: 'discountInfo.agreementInterest',
    },
    {
      name: 'discountInfo.selfPayInterest',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dSelfPayInterest').d('自付利息'),
      bind: 'discountInfo.selfPayInterest',
    },
    {
      name: 'discountInfo.vatRate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dVatRate').d('增值税税率'),
      bind: 'discountInfo.vatRate',
    },
    {
      name: 'discountInfo.vatTax',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dVatTax').d('增值税税额'),
      bind: 'discountInfo.vatTax',
    },
    {
      name: 'promptPayInfo.onlineFlagMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pOnlineFlagMeaning').d('线上清算标记'),
      bind: 'promptPayInfo.onlineFlagMeaning',
    },
    {
      name: 'promptPayInfo.overDueReason',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pOverDueReason').d('逾期原因说明'),
      bind: 'promptPayInfo.overDueReason',
    },
    {
      name: 'promptPayInfo.promptPayApplicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPromptPayApplicationDate').d('提示付款申请日期'),
      bind: 'promptPayInfo.promptPayApplicationDate',
    },
    {
      name: 'promptPayInfo.promptPayAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPromptPayAmount').d('提示付款金额'),
      bind: 'promptPayInfo.promptPayAmount',
    },
    {
      name: 'promptPayInfo.promptPayerTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPromptPayerTypeMeaning').d('提示付款人类别'),
      bind: 'promptPayInfo.promptPayerTypeMeaning',
    },
    {
      name: 'promptPayInfo.promptPayerName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPromptPayerName').d('提示付款人名称'),
      bind: 'promptPayInfo.promptPayerName',
    },
    {
      name: 'promptPayInfo.promptPayerOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPromptPayerOrgCode').d('提示付款人组织机构代码'),
      bind: 'promptPayInfo.promptPayerOrgCode',
    },
    {
      name: 'promptPayInfo.promptPayerAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPromptPayerAccountNumber').d('提示付款人账号'),
      bind: 'promptPayInfo.promptPayerAccountNumber',
    },
    {
      name: 'promptPayInfo.promptPayerAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.pPromptPayerAccountBankName')
        .d('提示付款人开户行行名'),
      bind: 'promptPayInfo.promptPayerAccountBankName',
    },
    {
      name: 'promptPayInfo.promptPayerAccountBankNumber',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.pPromptPayerAccountBankNumber')
        .d('提示付款人开户行行号'),
      bind: 'promptPayInfo.promptPayerAccountBankNumber',
    },
    {
      name: 'promptPayInfo.refuseCodeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pRefuseCodeMeaning').d('拒付代码'),
      bind: 'promptPayInfo.refuseCodeMeaning',
    },
    {
      name: 'promptPayInfo.refuseRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pRefuseRemark').d('拒付备注信息'),
      bind: 'promptPayInfo.refuseRemark',
    },
    {
      name: 'promptPayInfo.remark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pRemark').d('提示付款人备注'),
      bind: 'promptPayInfo.remark',
    },
    {
      name: 'guaranteeInfo.guaranteedApplicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteedApplicationDate').d('保证申请日期'),
      bind: 'guaranteeInfo.guaranteedApplicationDate',
    },
    {
      name: 'guaranteeInfo.batchNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gBatchNumber').d('批次号'),
      bind: 'guaranteeInfo.batchNumber',
    },
    {
      name: 'guaranteeInfo.guaranteeTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeTypeMeaning').d('被保证人类别'),
      bind: 'guaranteeInfo.guaranteeTypeMeaning',
    },
    {
      name: 'guaranteeInfo.guaranteeName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeName').d('被保证人名称'),
      bind: 'guaranteeInfo.guaranteeName',
    },
    {
      name: 'guaranteeInfo.guaranteeOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeOrgCode').d('被保证人组织机构代码'),
      bind: 'guaranteeInfo.guaranteeOrgCode',
    },
    {
      name: 'guaranteeInfo.guaranteeAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeAccountNumber').d('被保证人账号'),
      bind: 'guaranteeInfo.guaranteeAccountNumber',
    },
    {
      name: 'guaranteeInfo.guaranteeAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeAccountBankNumber').d('被保证人开户行行号'),
      bind: 'guaranteeInfo.guaranteeAccountBankNumber',
    },
    {
      name: 'guaranteeInfo.guaranteeAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeAccountBankName').d('被保证人开户行名称'),
      bind: 'guaranteeInfo.guaranteeAccountBankName',
    },
    {
      name: 'guaranteeInfo.guaranteeRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuaranteeRemark').d('被保证人备注'),
      bind: 'guaranteeInfo.guaranteeRemark',
    },
    {
      name: 'guaranteeInfo.guarantorName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuarantorName').d('保证人名称'),
      bind: 'guaranteeInfo.guarantorName',
    },
    {
      name: 'guaranteeInfo.guarantorAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuarantorAccountNumber').d('保证人账号'),
      bind: 'guaranteeInfo.guarantorAccountNumber',
    },
    {
      name: 'guaranteeInfo.guarantorAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuarantorAccountBankNumber').d('保证人开户行行号'),
      bind: 'guaranteeInfo.guarantorAccountBankNumber',
    },
    {
      name: 'guaranteeInfo.guarantorAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuarantorAccountBankName').d('保证人开户行名称'),
      bind: 'guaranteeInfo.guarantorAccountBankName',
    },
    {
      name: 'guaranteeInfo.guarantorAddress',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.gGuarantorAddress').d('保证人地址'),
      bind: 'guaranteeInfo.guarantorAddress',
    },
    {
      name: 'endorseInfo.endorseApplicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorseApplicationDate').d('背书申请日期'),
      bind: 'endorseInfo.endorseApplicationDate',
    },
    {
      name: 'endorseInfo.endorseTransferFlag',
      type: 'boolean',
      label: intl.get('hebk.draft.model.draft.eEndorseTransferFlag').d('背书能否转让'),
      bind: 'endorseInfo.endorseTransferFlag',
      defaultFlag: 1,
      trueFlag: 1,
      falseFlag: 0,
    },
    {
      name: 'endorseInfo.endorserTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserTypeMeaning').d('背书人类别'),
      bind: 'endorseInfo.endorserTypeMeaning',
    },
    {
      name: 'endorseInfo.endorserName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserName').d('背书人名称'),
      bind: 'endorseInfo.endorserName',
    },
    {
      name: 'endorseInfo.endorserOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserOrgCode').d('背书人组织机构代码'),
      bind: 'endorseInfo.endorserOrgCode',
    },
    {
      name: 'endorseInfo.endorserAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserAccountNumber').d('背书人账号'),
      bind: 'endorseInfo.endorserAccountNumber',
    },
    {
      name: 'endorseInfo.endorserAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserAccountBankNumber').d('背书人开户行行号'),
      bind: 'endorseInfo.endorserAccountBankNumber',
    },
    {
      name: 'endorseInfo.endorserAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserAccountBankName').d('背书人开户行行名'),
      bind: 'endorseInfo.endorserAccountBankName',
    },
    {
      name: 'endorseInfo.endorserRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorserRemark').d('背书人备注'),
      bind: 'endorseInfo.endorserRemark',
    },
    {
      name: 'endorseInfo.endorseeName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorseeName').d('被背书人名称'),
      bind: 'endorseInfo.endorseeName',
    },
    {
      name: 'endorseInfo.endorseeAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorseeAccountNumber').d('被背书人账号'),
      bind: 'endorseInfo.endorseeAccountNumber',
    },
    {
      name: 'endorseInfo.endorseeAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorseeAccountBankNumber').d('被背书人开户行行号'),
      bind: 'endorseInfo.endorseeAccountBankNumber',
    },
    {
      name: 'endorseInfo.endorseeAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.eEndorseeAccountBankName').d('被背书人开户行行名'),
      bind: 'endorseInfo.endorseeAccountBankName',
    },
    {
      name: 'pledgeInfo.pledgeApplicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeApplicationDate').d('质押申请日期'),
      bind: 'pledgeInfo.pledgeApplicationDate',
    },
    {
      name: 'pledgeInfo.batchNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pBatchNumber').d('批次号'),
      bind: 'pledgeInfo.batchNumber',
    },
    {
      name: 'pledgeInfo.pledgorTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorTypeMeaning').d('出质人类别'),
      bind: 'pledgeInfo.pledgorTypeMeaning',
    },
    {
      name: 'pledgeInfo.pledgorName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorName').d('出质人名称'),
      bind: 'pledgeInfo.pledgorName',
    },
    {
      name: 'pledgeInfo.pledgorOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorOrgCode').d('出质人组织机构代码'),
      bind: 'pledgeInfo.pledgorOrgCode',
    },
    {
      name: 'pledgeInfo.pledgorAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorAccountNumber').d('出质人账号'),
      bind: 'pledgeInfo.pledgorAccountNumber',
    },
    {
      name: 'pledgeInfo.pledgorAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorAccountBankNumber').d('出质人开户行行号'),
      bind: 'pledgeInfo.pledgorAccountBankNumber',
    },
    {
      name: 'pledgeInfo.pledgorAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorAccountBankName').d('出质人开户行行名'),
      bind: 'pledgeInfo.pledgorAccountBankName',
    },
    {
      name: 'pledgeInfo.pledgorRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgorRemark').d('出质人备注'),
      bind: 'pledgeInfo.pledgorRemark',
    },
    {
      name: 'pledgeInfo.pledgeeName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeeName').d('质权人名称'),
      bind: 'pledgeInfo.pledgeeName',
    },
    {
      name: 'pledgeInfo.pledgeeAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeeAccountNumber').d('质权人账号'),
      bind: 'pledgeInfo.pledgeeAccountNumber',
    },
    {
      name: 'pledgeInfo.pledgeeAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeeAccountBankNumber').d('质权人开户行行号'),
      bind: 'pledgeInfo.pledgeeAccountBankNumber',
    },
    {
      name: 'pledgeInfo.pledgeeAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeeAccountBankName').d('质权人开户行行名'),
      bind: 'pledgeInfo.pledgeeAccountBankName',
    },
    {
      name: 'pledgeInfo.pledgeReleaseApplicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeReleaseApplicationDate').d('质押解除申请日期'),
      bind: 'pledgeInfo.pledgeReleaseApplicationDate',
    },
    {
      name: 'pledgeInfo.pledgeeRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.pPledgeeRemark').d('质权人备注'),
      bind: 'pledgeInfo.pledgeeRemark',
    },
    {
      name: 'recourseInfo.recourseApplicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourseApplicationDate').d('追索申请日期'),
      bind: 'recourseInfo.recourseApplicationDate',
    },
    {
      name: 'recourseInfo.recourseReasonCodeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourseReasonCodeMeaning').d('追索理由代码'),
      bind: 'recourseInfo.recourseReasonCodeMeaning',
    },
    {
      name: 'recourseInfo.recourseTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourseTypeMeaning').d('追索类型'),
      bind: 'recourseInfo.recourseTypeMeaning',
    },
    {
      name: 'recourseInfo.recourseAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourseAmount').d('追索金额'),
      bind: 'recourseInfo.recourseAmount',
    },
    {
      name: 'recourseInfo.recourserTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourserTypeMeaning').d('追索人类别'),
      bind: 'recourseInfo.recourserTypeMeaning',
    },
    {
      name: 'recourseInfo.recourserName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourserName').d('追索人名称'),
      bind: 'recourseInfo.recourserName',
    },
    {
      name: 'recourseInfo.recourserOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourserOrgCode').d('追索人组织机构代码'),
      bind: 'recourseInfo.recourserOrgCode',
    },
    {
      name: 'recourseInfo.recourserAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourserAccountNumber').d('追索人账号'),
      bind: 'recourseInfo.recourserAccountNumber',
    },
    {
      name: 'recourseInfo.recourserAccountBankNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourserAccountBankNumber').d('追索人开户行行号'),
      bind: 'recourseInfo.recourserAccountBankNumber',
    },
    {
      name: 'recourseInfo.recourserAccountBankName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourseAccountBankName').d('追索人开户行行名'),
      bind: 'recourseInfo.recourseAccountBankName',
    },
    {
      name: 'recourseInfo.recoursePersonName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecoursePersonName').d('被追索人名称'),
      bind: 'recourseInfo.recoursePersonName',
    },
    {
      name: 'recourseInfo.recoursePersonAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecoursePersonAccountNumber').d('被追索人账号'),
      bind: 'recourseInfo.recoursePersonAccountNumber',
    },
    {
      name: 'recourseInfo.recoursePersonOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecoursePersonOrgCode').d('被追索人组织机构代码'),
      bind: 'recourseInfo.recoursePersonOrgCode',
    },
    {
      name: 'recourseInfo.recoursePersonAccountBankNumber',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.rRecoursePersonAccountBankNumber')
        .d('被追索人开户行行号'),
      bind: 'recourseInfo.recoursePersonAccountBankNumber',
    },
    {
      name: 'recourseInfo.recoursePersonAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.rRecoursePersonAccountBankName')
        .d('被追索人开户行行名'),
      bind: 'recourseInfo.recoursePersonAccountBankName',
    },
    {
      name: 'recourseInfo.recourseRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rRecourseRemark').d('追索通知备注'),
      bind: 'recourseInfo.recourseRemark',
    },
    {
      name: 'recourseInfo.agreeSettleDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rAgreeSettleDate').d('同意清偿日期'),
      bind: 'recourseInfo.agreeSettleDate',
    },
    {
      name: 'recourseInfo.agreeSettleAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rAgreeSettleAmount').d('同意清偿金额'),
      bind: 'recourseInfo.agreeSettleAmount',
    },
    {
      name: 'recourseInfo.agreeSettlePersonOrgCode',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.rAgreeSettlePersonOrgCode')
        .d('同意清偿人组织机构代码'),
      bind: 'recourseInfo.agreeSettlePersonOrgCode',
    },
    {
      name: 'recourseInfo.agreeSettlePersonTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rAgreeSettlePersonTypeMeaning').d('同意清偿人类别'),
      bind: 'recourseInfo.agreeSettlePersonTypeMeaning',
    },
    {
      name: 'recourseInfo.agreeSettlePersonName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rAgreeSettlePersonName').d('同意清偿人名称'),
      bind: 'recourseInfo.agreeSettlePersonName',
    },
    {
      name: 'recourseInfo.agreeSettlePersonAccountNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rAgreeSettlePersonAccountNumber').d('同意清偿人账号'),
      bind: 'recourseInfo.agreeSettlePersonAccountNumber',
    },
    {
      name: 'recourseInfo.agreeSettlePersonAccountBankName',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.rAgreeSettlePersonAccountBankName')
        .d('同意清偿人开户行行名'),
      bind: 'recourseInfo.agreeSettlePersonAccountBankName',
    },
    {
      name: 'recourseInfo.agreeSettlePersonAccountBankNumber',
      type: 'string',
      label: intl
        .get('hebk.draft.model.draft.rAgreeSettlePersonAccountBankNumber')
        .d('同意清偿人开户行行号'),
      bind: 'recourseInfo.agreeSettlePersonAccountBankNumber',
    },
    {
      name: 'recourseInfo.agreeSettleRemark',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.rAgreeSettleRemark').d('追索同意清偿备注'),
      bind: 'recourseInfo.agreeSettleRemark',
    },
    {
      name: 'creditInfo.inquiredPersonOrgCode',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cInquiredPersonOrgCode').d('被查询人组织机构代码'),
      bind: 'creditInfo.inquiredPersonOrgCode',
    },
    {
      name: 'creditInfo.inquiredPersonName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cInquiredPersonName').d('被查询人名称'),
      bind: 'creditInfo.inquiredPersonName',
    },
    {
      name: 'creditInfo.acceptTotalNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cAcceptTotalNumber').d('承兑总笔数'),
      bind: 'creditInfo.acceptTotalNumber',
    },
    {
      name: 'creditInfo.acceptTotalAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cAcceptTotalAmount').d('承兑总金额'),
      bind: 'creditInfo.acceptTotalAmount',
    },
    {
      name: 'creditInfo.settledTotalNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cSettledTotalNumber').d('已结清总笔数'),
      bind: 'creditInfo.settledTotalNumber',
    },
    {
      name: 'creditInfo.settledTotalAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cSettledTotalAmount').d('已结清总金额'),
      bind: 'creditInfo.settledTotalAmount',
    },
    {
      name: 'creditInfo.unsettledTotalNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cUnsettledTotalNumber').d('未结清总笔数'),
      bind: 'creditInfo.unsettledTotalNumber',
    },
    {
      name: 'creditInfo.unsettledTotalAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cUnsettledTotalAmount').d('未结清款总金额'),
      bind: 'creditInfo.creditInfo.unsettledTotalAmount',
    },
    {
      name: 'creditInfo.protestTotalNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cProtestTotalNumber').d('拒付总笔数'),
      bind: 'creditInfo.protestTotalNumber',
    },
    {
      name: 'creditInfo.protestTotalAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cProtestTotalAmount').d('拒付总金额'),
      bind: 'creditInfo.inquiredPersonOrgCode',
    },
    {
      name: 'creditInfo.protestYearTotalNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cProtestYearTotalNumber').d('今年以来拒付总笔数'),
      bind: 'creditInfo.protestYearTotalNumber',
    },
    {
      name: 'creditInfo.protestYearTotalAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cProtestYearTotalAmount').d('今年以来拒付总金额'),
      bind: 'creditInfo.protestYearTotalAmount',
    },
    {
      name: 'creditInfo.protestLastYearTotalNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cProtestLastYearTotalNumber').d('去年以来拒付总笔数'),
      bind: 'creditInfo.protestLastYearTotalNumber',
    },
    {
      name: 'creditInfo.protestLastYearTotalAmount',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.cProtestLastYearTotalAmount').d('去年以来拒付总金额'),
      bind: 'creditInfo.protestLastYearTotalAmount',
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { draftId } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/drafts/${draftId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

const flowDs = () => ({
  selection: false,
  autoQuery: false,
  fields: [
    {
      name: 'sequenceNumber',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.sequenceNumber').d('序号'),
    },
    {
      name: 'transactionTypeMeaning',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.transactionTypeMeaning').d('交易类型'),
    },
    {
      name: 'applicantName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.applicantName').d('申请人名称'),
    },
    {
      name: 'signerName',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.signerName').d('签收人名称'),
    },
    {
      name: 'overdueReason',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.overdueReason').d('逾期原因说明'),
    },
    {
      name: 'transferFlag',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.transferFlag').d('能否转让标识'),
    },
    {
      name: 'applicationDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.applicationDate').d('申请日期'),
    },
    {
      name: 'openDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.openDate').d('赎回开放日'),
    },
    {
      name: 'dueDate',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.dueDate').d('赎回截止日'),
    },
    {
      name: 'discountType',
      type: 'string',
      label: intl.get('hebk.draft.model.draft.discountType').d('贴现种类'),
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/drafts/query-flow-info`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

export { tableDs, detailDs, flowDs };
