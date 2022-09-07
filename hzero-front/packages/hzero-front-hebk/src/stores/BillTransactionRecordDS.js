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
      label: intl.get('hebk.billTR.model.billTR.bankMark').d('银行标识'),
      lookupCode: 'HEBK.BANK_MARK',
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.accountNumber').d('账号'),
    },
    {
      name: 'draftNumber',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.draftNumber').d('票据号码'),
    },
    {
      name: 'bankTransactionId',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.bankTransactionId').d('银行交易流水号'),
    },
  ],
  fields: [
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.bankMarkMeaning').d('银行标识'),
    },
    {
      name: 'draftNumber',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.draftNumber').d('票据号码'),
    },
    {
      name: 'bankCode',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.bankCode').d('联行号'),
    },
    {
      name: 'accountNumber',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.accountNumber').d('账号'),
    },
    {
      name: 'statusMeaning',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.statusMeaning').d('状态'),
    },
    {
      name: 'bankStatusCode',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.bankStatusCode').d('银行状态'),
    },
    {
      name: 'bankTransactionId',
      type: 'string',
      label: intl.get('hebk.billTR.model.billTR.bankTransactionId').d('银行交易流水号'),
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/drafts/list-trade-business`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

export { tableDs };
