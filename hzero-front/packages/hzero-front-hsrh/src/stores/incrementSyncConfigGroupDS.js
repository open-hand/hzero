import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HSRH } from 'hzero-front/lib/utils/config';

const organizationId = getCurrentOrganizationId();

const tableDS = () => ({
  autoQuery: true,
  dataKey: 'content',
  selection: false,
  queryFields: [
    {
      name: 'indexSet',
      type: 'object',
      lovCode: 'HSRH.INDEX_VERSIONS_LIST',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexSet').d('索引'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'indexId',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.indexId').d('索引ID'),
      bind: 'indexSet.indexId',
    },
    {
      name: 'syncConfCode',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.searchConfig.syncConfCode').d('编码'),
    },
    {
      name: 'sourceFromType',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.searchConfig.sourceFromType').d('来源类型'),
      lookupCode: 'HSRH.SOURCE_FROM_TYPE',
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.remark').d('描述'),
    },
    // {
    //   name: 'sourceFromSet',
    //   type: 'object',
    //   lovCode: 'HSRH.INTERFACE_LIST',
    //   label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.dataSources').d('数据来源'),
    //   ignore: 'always',
    //   noCache: true,
    // },
    {
      name: 'sourceFromDetailCode',
      type: 'string',
      label: intl
        .get('hsrh.incSyncConfig.model.incSyncConfig.sourceFromDetailCode')
        .d('数据来源编码'),
    },
  ],
  fields: [
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexCode').d('索引'),
    },
    {
      name: 'syncConfCode',
      type: 'string',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.syncConfCode').d('编码'),
    },
    {
      name: 'sourceFromTypeMeaning',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.sourceFromType').d('来源类型'),
    },
    {
      name: 'dataSources',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.dataSources').d('数据来源'),
    },
    {
      name: 'sourceFromDetailCode',
      type: 'string',
      label: intl
        .get('hsrh.incSyncConfig.model.incSyncConfig.sourceFromDetailCode')
        .d('数据来源编码'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.remark').d('描述'),
    },
  ],
  transport: {
    read: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/index-sync-configs`;
      return {
        config,
        url,
        method: 'GET',
        transformResponse: (data) => {
          const record = JSON.parse(data);
          record.content.forEach((val) => {
            if (val.sourceFromType === 'DB') {
              // eslint-disable-next-line no-param-reassign
              val.dataSources = val.sourceFromMeaning;
            } else if (val.sourceFromType === 'INTERFACE') {
              // eslint-disable-next-line no-param-reassign
              val.dataSources = val.sourceFromCode;
            }
          });
          return record;
        },
      };
    },
    destroy: (config) => {
      const { params } = config;
      let { data } = config;
      data = { ...data[0] };
      delete data._status;
      delete data.__id;
      const url = `${HZERO_HSRH}/v1/${organizationId}/data-increments`;
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
      name: 'incrementId',
      type: 'number',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.incrementSyncId').d('同步增量ID'),
    },
    {
      name: 'syncConfCode',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.syncConfCode').d('编码'),
      required: true,
      noCache: true,
    },
    {
      name: 'indexSet',
      type: 'object',
      lovCode: 'HSRH.INDEX_VERSIONS_LIST',
      lovPara: { enabledFlag: 1 },
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.indexSet').d('索引'),
      noCache: true,
      required: true,
    },
    {
      name: 'indexId',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.indexId').d('索引ID'),
      bind: 'indexSet.indexId',
    },
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.indexCode').d('索引code'),
      bind: 'indexSet.indexCode',
    },
    {
      name: 'remark',
      type: 'string',
      maxLength: 2000,
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.remark').d('描述'),
    },
    // {
    //   name: 'batchNumber',
    //   type: 'number',
    //   label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.batchNumber').d('批次数量'),
    // },
    {
      name: 'sourceFromType',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.searchConfig.sourceFromType').d('来源类型'),
      lookupCode: 'HSRH.SOURCE_FROM_TYPE',
      defaultValue: 'INTERFACE',
    },
    {
      name: 'dataSourceApiLov',
      type: 'object',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.dataSources').d('数据来源'),
      ignore: 'always',
      required: true,
      noCache: true,
      dynamicProps: ({ record }) => {
        if (record.get('sourceFromType') === 'INTERFACE') {
          return {
            lovCode: 'HSRH.INTERFACE_LIST',
            textField: 'serverCode',
            valueField: 'serverCode',
          };
        } else {
          return {
            lovCode: 'HSRH.DATASOURCE',
            lovPara: { tenantId: organizationId },
            textField: 'dsPurposeCode',
            valueField: 'datasourceCode',
          };
        }
      },
    },
    {
      name: 'sourceFromCode',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.sourceFromCode').d('数据来源code'),
      dynamicProps: ({ record }) => {
        if (record.get('sourceFromType') === 'INTERFACE') {
          return {
            bind: 'dataSourceApiLov.serverCode',
          };
        } else {
          return {
            bind: 'dataSourceApiLov.dsPurposeCode',
          };
        }
      },
    },
    {
      name: 'sourceFromDetailCode',
      type: 'string',
      label: intl
        .get('hsrh.incSyncConfig.model.incSyncConfig.sourceFromDetailCode')
        .d('数据来源编码'),
      dynamicProps: ({ record }) => {
        if (record.get('sourceFromType') === 'INTERFACE') {
          return {
            bind: 'dataSourceApiLov.interfaceCode',
          };
        } else {
          return {
            bind: 'dataSourceApiLov.datasourceCode',
          };
        }
      },
    },
    {
      name: 'isRecord',
      type: 'boolean',
      falseValue: 0,
      trueValue: 1,
      label: intl.get('hsrh.syncLog.model.syncLog.isRecord').d('是否记录到文件'),
    },
    {
      name: 'commandContent',
      type: 'string',
      label: intl.get('hsrh.incSyncConfig.model.searchConfig.commandContent').d('命令文本'),
    },
  ],
  events: {
    update: ({ record, name, value }) => {
      if (name === 'indexCode') {
        if (value) {
          record.set('indexVersion', value.indexVersion);
        } else {
          record.set('indexVersion', null);
        }
      }
    },
  },
  transport: {
    create: (config) => {
      const { params } = config;
      let { data } = config;
      const url = `${HZERO_HSRH}/v1/${organizationId}/index-sync-configs`;
      // const { indexId } = data[0].indexCode;
      delete data[0].__id;
      delete data[0]._status;
      data[0].syncConfCode = data[0].syncConfCode.toUpperCase();
      data = { ...data[0] };
      return {
        data,
        params,
        url,
        method: 'POST',
      };
    },
    read: (config) => {
      const {
        data: { syncConfId = '' },
      } = config;
      const url = `${HZERO_HSRH}/v1/${organizationId}/index-sync-configs/${syncConfId}`;
      return {
        url,
        method: 'GET',
      };
    },
    update: (config) => {
      const { params } = config;
      let { data } = config;

      const url = `${HZERO_HSRH}/v1/${organizationId}/index-sync-configs`;
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

function detailTableDS(formRecord) {
  return {
    autoQuery: false,
    dataKey: 'content',
    selection: false,
    fields: [
      {
        name: 'incrementFieldId',
        type: 'string',
        label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.syncFieldId').d('同步字段ID'),
      },
      {
        name: 'fieldIdSet',
        type: 'object',
        label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.coFieldTable').d('关联字段表'),
        lovCode: 'HSRH.INDEX_FIELD_LIST',
        // lovPara: { ...indexId },
        ignore: 'always',
        noCache: true,
        dynamicProps: {
          lovPara: () => {
            const { indexId = '' } = formRecord.current ? formRecord.current.toData() || {} : {};
            return {
              tenantId: organizationId,
              indexId,
            };
          },
        },
      },
      {
        name: 'fieldName',
        type: 'string',
        label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.fieldName').d('字段名称'),
        bind: 'fieldIdSet.fieldName',
      },
      {
        name: 'indexFieldId',
        type: 'string',
        label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.coFieldTable').d('关联字段表'),
        required: true,
        bind: 'fieldIdSet.indexFieldId',
      },
      {
        name: 'fieldMapping',
        type: 'string',
        label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.fieldMapping').d('映射字段'),
        bind: 'fieldIdSet.fieldMapping',
      },
      {
        name: 'tenantId',
        type: 'string',
        label: intl.get('hsrh.incSyncConfig.model.incSyncConfig.tenantId').d('tenantId'),
        defaultValue: organizationId,
      },
    ],
    transport: {
      create: (config) => {
        const { params } = config;
        let { data } = config;
        const url = `${HZERO_HSRH}/v1/${organizationId}/index-field-sync-configs`;
        delete data[0].__id;
        delete data[0]._status;
        data = [{ ...data[0] }];
        return {
          data,
          params,
          url,
          method: 'POST',
        };
      },
      read: (config) => {
        const url = `${HZERO_HSRH}/v1/${organizationId}/index-field-sync-configs`;
        return {
          ...config,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const { params } = config;
        let { data } = config;
        const url = `${HZERO_HSRH}/v1/${organizationId}/index-field-sync-configs`;
        delete data[0].__id;
        delete data[0]._status;
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
        delete data._status;
        delete data.__id;
        const url = `${HZERO_HSRH}/v1/${organizationId}/index-field-sync-configs`;
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

export { tableDS, detailFormDS, detailTableDS };
