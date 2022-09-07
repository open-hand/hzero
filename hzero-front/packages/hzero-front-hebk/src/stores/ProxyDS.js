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
      label: intl.get('hebk.proxy.model.proxy.bankMark').d('银行标识'),
      lookupCode: 'HEBK.BANK_MARK',
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.enabledFlag').d('启用标识'),
      lookupCode: 'HPFM.ENABLED_FLAG	',
    },
    {
      name: 'sslEnabledFlag',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.sslEnabledFlag').d('SSL启用标识'),
      lookupCode: 'HPFM.ENABLED_FLAG',
    },
  ],
  fields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.name').d('名称'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hebk.proxy.model.proxy.enabledFlag').d('是否启用'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'sslEnabledFlag',
      type: 'boolean',
      label: intl.get('hebk.proxy.model.proxy.sslEnabledFlag').d('是否启用SSL'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.remark').d('备注'),
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/proxies`;
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
      name: 'name',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.name').d('名称'),
      required: true,
      maxLength: 120,
    },
    {
      name: 'host',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.host').d('地址'),
      required: true,
      maxLength: 30,
    },
    {
      name: 'port',
      type: 'number',
      label: intl.get('hebk.proxy.model.proxy.port').d('端口'),
      required: true,
      maxLength: 5,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hebk.proxy.model.proxy.enabledFlag').d('是否启用'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
      required: true,
    },
    {
      name: 'sslEnabledFlag',
      type: 'boolean',
      label: intl.get('hebk.proxy.model.proxy.sslEnabledFlag').d('是否启用SSL'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
      required: true,
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.remark').d('备注'),
      maxLength: 240,
    },
    {
      name: 'trustedCaFile',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.trustedCaFile').d('CA证书'),
      dynamicProps: ({ record }) => {
        if (record.get('sslEnabledFlag') === 1) {
          return {
            required: true,
          };
        }
      },
    },
    {
      name: 'certFile',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.certFile').d('公钥'),
      dynamicProps: ({ record }) => {
        if (record.get('sslEnabledFlag') === 1) {
          return {
            required: true,
          };
        }
      },
    },
    {
      name: 'keyFile',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.keyFile').d('私钥'),
      dynamicProps: ({ record }) => {
        if (record.get('sslEnabledFlag') === 1) {
          return {
            required: true,
          };
        }
      },
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { proxyId } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/proxies/${proxyId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

const bankDs = () => ({
  selection: false,
  autoQuery: false,
  queryFields: [],
  fields: [
    {
      name: 'bankMark',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.bankMark').d('银行标识'),
    },
    {
      name: 'bankMarkMeaning',
      type: 'string',
      label: intl.get('hebk.proxy.model.proxy.bankMarkMeaning').d('银行标识含义'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { proxyId } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/proxies/${proxyId}/banks`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

export { tableDs, detailDs, bankDs };
