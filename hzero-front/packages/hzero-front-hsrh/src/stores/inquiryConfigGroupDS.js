import intl from 'utils/intl';
import { HZERO_HSRH } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'configCode',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.configCode').d('编码'),
    },
    {
      name: 'indexCodeSet',
      type: 'object',
      lovCode: 'HSRH.INDEX_VERSIONS_LIST',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexCode').d('索引'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'indexId',
      type: 'string',
      bind: 'indexCodeSet.indexId',
    },
    {
      name: 'enabledFlag',
      type: 'string',
      lookupCode: 'HPFM.EFFECTIVE_FLAG',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.enabledFlag').d('状态'),
    },
    {
      name: 'activeStartTime',
      type: 'dateTime',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.activeStartTime').d('生效开始时间'),
    },
    {
      name: 'activeEndTime',
      type: 'dateTime',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.activeEndTime').d('生效结束时间'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.remark').d('描述'),
    },
  ],
  fields: [
    {
      name: 'configCode',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.configCode').d('编码'),
    },
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexCode').d('索引'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.enabledFlag').d('状态'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.remark').d('描述'),
    },
    {
      name: 'activeStartTime',
      type: 'dateTime',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.activeStartTime').d('生效开始时间'),
    },
    {
      name: 'activeEndTime',
      type: 'dateTime',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.activeEndTime').d('生效结束时间'),
    },
    // {
    //   name: 'indexVersion',
    //   type: 'string',
    //   label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.version').d('版本号'),
    // },
  ],
  transport: {
    read: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs`;
      return {
        config,
        url,
        method: 'GET',
      };
    },
    update: (config) => {
      const { params = {} } = config;
      let { data = {} } = config;
      const {
        indexId,
        configId,
        configCode,
        indexCode,
        indexVersion,
        enabledFlagCode,
        remark,
        objectVersionNumber,
        enabledFlag,
        _token,
      } = data[0];
      data = {
        indexId,
        configId,
        configCode,
        indexCode,
        indexVersion,
        enabledFlagCode,
        remark,
        objectVersionNumber,
        enabledFlag,
        _token,
      };
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs`;
      return {
        data,
        params,
        url,
        method: 'PUT',
      };
    },
    destroy: (config) => {
      const { params } = config;
      let { data } = config;
      data = { ...data[0] };
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs`;
      return {
        params,
        data,
        url,
        method: 'DELETE',
      };
    },
  },
});

const detailFormDS = () => ({
  fields: [
    {
      name: 'configId',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.queryConfigId').d('查询配置ID'),
    },
    {
      name: 'configCode',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.configCode').d('编码'),
      required: true,
    },
    {
      name: 'indexCodeSet',
      type: 'object',
      lovCode: 'HSRH.INDEX_VERSIONS_LIST',
      lovPara: { enabledFlag: 1 },
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexCode').d('索引'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'indexCode',
      type: 'string',
      bind: 'indexCodeSet.indexCode',
    },
    {
      name: 'indexId',
      type: 'string',
      bind: 'indexCodeSet.indexId',
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      falseValue: 0,
      trueValue: 1,
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.enabledFlag').d('状态'),
    },
    {
      name: 'remark',
      type: 'string',
      maxLength: 2000,
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.remark').d('描述'),
    },
    {
      name: 'activeStartTime',
      type: 'dateTime',
      required: true,
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.activeStartTime').d('生效开始时间'),
    },
    {
      name: 'activeEndTime',
      type: 'dateTime',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.activeEndTime').d('生效结束时间'),
    },
    // {
    //   name: 'queryJson',
    //   type: 'string',
    //   label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.queryJson').d('查询JSON'),
    // },
  ],
  transport: {
    create: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs`;
      let { data } = config;
      data = { ...data[0] };
      return {
        data,
        url,
        method: 'POST',
      };
    },
    read: (config) => {
      const {
        data: { configId = '' },
      } = config;
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs/${configId}`;
      return {
        url,
        method: 'GET',
      };
    },
    update: (config) => {
      const { params } = config;
      let { data } = config;
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs`;
      data = { ...data[0] };
      return {
        data,
        params,
        url,
        method: 'PUT',
      };
    },
  },
});

const detailTestDS = () => ({
  fields: [],
  transport: {
    read: (config) => {
      const { dataSet } = config;
      let { data } = config;
      const { configCode } = data;
      const tempData = dataSet.toData();
      data = { ...tempData[0] };
      const url = `${HZERO_HSRH}/v1/${organizationId}/query-configs/${configCode}/query`;
      return {
        data,
        url,
        method: 'POST',
      };
    },
  },
});

function detailTableDS(formRecord) {
  return {
    autoQuery: false,
    selection: false,
    dataKey: 'content',
    fields: [
      {
        name: 'incrementId',
        type: 'number',
        label: intl.get('hsrh.incSyncConfig.view.inquiryConfig.incrementSyncId').d('同步增量ID'),
      },
      {
        name: 'fieldConfigId',
        type: 'string',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.fieldConfigId').d('查询配置字段ID'),
      },
      {
        name: 'fieldIdSet',
        type: 'object',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.fieldName').d('字段名称'),
        lovCode: 'HSRH.INDEX_FIELD_LIST',
        ignore: 'always',
        noCache: true,
        required: true,
        dynamicProps: {
          lovPara: () => {
            const { indexId = '' } = formRecord.current.toData() || {};
            return {
              indexId,
            };
          },
        },
      },
      {
        name: 'fieldName',
        type: 'string',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.fieldName').d('字段名称'),
        bind: 'fieldIdSet.fieldName',
      },
      {
        name: 'indexFieldId',
        type: 'number',
        bind: 'fieldIdSet.indexFieldId',
      },
      {
        name: 'visibleFlag',
        type: 'number',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.isShow').d('是否显示'),
        required: true,
      },
      {
        name: 'sortFlag',
        type: 'number',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.isSort').d('是否排序'),
        required: true,
      },
      {
        name: 'sortDirect',
        type: 'number',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.sortDirection').d('排序方向'),
      },
      {
        name: 'weight',
        type: 'number',
        label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.weight').d('排序优先级'),
        required: true,
      },
    ],
    transport: {
      create: (config) => {
        const { params } = config;
        let { data } = config;
        const url = `${HZERO_HSRH}/v1/${organizationId}/query-field-configs`;
        data = { ...data[0] };
        return {
          data,
          params,
          url,
          method: 'POST',
        };
      },
      read: (config) => {
        const url = `${HZERO_HSRH}/v1/${organizationId}/query-field-configs`;
        return {
          ...config,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const { params } = config;
        let { data } = config;
        const url = `${HZERO_HSRH}/v1/${organizationId}/query-field-configs`;
        data = { ...data[0] };
        return {
          data,
          params,
          url,
          method: 'PUT',
        };
      },
      destroy: (config) => {
        const { params } = config;
        let { data } = config;
        data = { ...data[0] };
        const url = `${HZERO_HSRH}/v1/${organizationId}/query-field-configs`;
        return {
          params,
          data,
          url,
          method: 'DELETE',
        };
      },
    },
  };
}

export { tableDS, detailFormDS, detailTestDS, detailTableDS };
