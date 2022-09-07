import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HSRH } from 'hzero-front/lib/utils/config';
import { DataSet } from 'choerodon-ui/pro';
import { isObject } from 'lodash';

const organizationId = getCurrentOrganizationId();

const enabledFlagList = [
  { meaning: intl.get('hzero.common.status.valid').d('有效'), value: 1, orderSeq: 10 },
  { meaning: intl.get('hzero.common.status.Invalid').d('失效'), value: 0, orderSeq: 20 },
];

const enabledFlagDs = new DataSet({
  data: enabledFlagList,
  selection: 'single', // 选择的模式, 可选值：false 'multiple' 'single'
});

const tableDS = () => ({
  autoQuery: true,
  dataKey: 'content',
  selection: false,
  queryFields: [
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.indexCode').d('索引'),
    },
    // {
    //   name: 'enabledFlagCode',
    //   type: 'string',
    //   lookupCode: 'HPFM.EFFECTIVE_FLAG',
    //   label: intl.get('hsrh.searchConfig.model.searchConfig.isEnabled').d('状态'),
    // },
    // {
    //   name: 'dataSourceApiIdData',
    //   type: 'object',
    //   lovCode: 'HSRH.INTERFACE_LIST',
    //   label: intl.get('hsrh.searchConfig.model.searchConfig.dataSource').d('数据来源'),
    //   ignore: 'always',
    //   noCache: true,
    // },
    // {
    //   name: 'dataSourceApiId',
    //   type: 'number',
    //   bind: 'dataSourceApiIdData.interfaceId',
    // },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.remark').d('描述'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hsrh.searchConfig.model.searchConfig.enabledFlag').d('是否有效'),
      options: enabledFlagDs,
    },
  ],
  fields: [
    // {
    //   name: 'indexId',
    //   type: 'number',
    //   label: intl.get('hsrh.searchConfig.model.searchConfig.indexId').d('版本控制ID'),
    // },
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.indexCode').d('索引'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.enabledFlag').d('是否有效'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.remark').d('描述'),
    },
    {
      name: 'createUser',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.createUser').d('创建人'),
    },
    {
      name: 'creationDate',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.creationDate').d('创建时间'),
    },
    {
      name: 'updateUser',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.updateUser').d('最近更新人'),
    },
    {
      name: 'lastUpdateDate',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.lastUpdateDate').d('最近更新时间'),
    },

    // {
    //   name: 'dataSourceApiCode',
    //   type: 'string',
    //   label: intl.get('hsrh.searchConfig.model.searchConfig.dataSource').d('数据来源'),
    // },
  ],
  transport: {
    read: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/indices`;
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
        dataSourceApiId,
        remark,
        enabledFlag,
        indexCode,
        indexId,
        tenantId,
        objectVersionNumber,
        shardsNum,
        replicasNum,
        shards,
        replicas,
        _token,
      } = data[0];
      data = {
        dataSourceApiId,
        remark,
        enabledFlag,
        indexCode,
        indexId,
        tenantId,
        objectVersionNumber,
        shardsNum,
        replicasNum,
        shards,
        replicas,
        _token,
      };
      const url = `${HZERO_HSRH}/v1/${organizationId}/indices`;
      return {
        data,
        params,
        url,
        method: 'PUT',
      };
    },
  },
});

const detailFormDS = () => ({
  fields: [
    {
      name: 'indexId',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.IndexId').d('版本控制ID'),
    },
    {
      name: 'indexCode',
      type: 'string',
      pattern: /^[a-z0-9][a-z0-9-_./]*$/,
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexCode').d('索引'),
      required: true,
    },
    {
      name: 'remark',
      type: 'string',
      maxLength: 2000,
      label: intl.get('hsrh.searchConfig.model.searchConfig.remark').d('描述'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      falseValue: 0,
      trueValue: 1,
      label: intl.get('hsrh.searchConfig.model.searchConfig.status').d('状态'),
    },
    {
      name: 'shards',
      type: 'number',
      max: 255,
      label: intl.get('hsrh.searchConfig.model.searchConfig.shards').d('分片数量'),
    },
    {
      name: 'replicas',
      type: 'number',
      max: 255,
      label: intl.get('hsrh.searchConfig.model.searchConfig.replicas').d('副本数量'),
    },
    {
      name: 'createUser',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.createUser').d('创建人'),
    },
    {
      name: 'updateUser',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.updateUser').d('最近更新人'),
    },
    {
      name: 'lastUpdateDate',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.lastUpdateDate').d('最近更新时间'),
    },
    {
      name: 'creationDate',
      type: 'string',
      label: intl.get('hsrh.searchConfig.model.searchConfig.creationDate').d('创建时间'),
    },
    // {
    //   name: 'dataSourceApiIdLov',
    //   type: 'object',
    //   lovCode: 'HSRH.INTERFACE_LIST',
    //   label: intl.get('hsrh.searchConfig.model.searchConfig.dataSource').d('数据来源'),
    //   noCache: true,
    //   required: true,
    //   ignore: 'always',
    // },
  ],
  transport: {
    read: (config) => {
      const {
        data: { indexId },
      } = config;
      const url = `${HZERO_HSRH}/v1/${organizationId}/indices/${indexId}`;
      return {
        url,
        method: 'GET',
        params: {},
      };
    },
    update: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/index-versions`;
      const { params } = config;
      let { data } = config;
      const tempDataSourceApiId = isObject(data[0].dataSourceApiId)
        ? data[0].dataSourceApiId.interfaceId
        : data[0].dataSourceApiId;
      data = { ...data[0], dataSourceApiId: tempDataSourceApiId };
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
        name: 'fieldId',
        type: 'string',
        label: intl.get('hsrh.searchConfig.model.searchConfig.syncFieldId').d('同步字段ID'),
      },
      {
        name: 'fieldName',
        type: 'string',
        label: intl.get('hsrh.searchConfig.model.searchConfig.fieldName').d('字段名'),
        required: true,
      },
      // {
      //   name: 'fieldMapping',
      //   type: 'string',
      //   label: intl.get('hsrh.searchConfig.model.searchConfig.fieldMapping').d('映射字段'),
      //   required: true,
      // },
      {
        name: 'fieldType',
        type: 'string',
        lookupCode: 'HSRH.FIELD_TYPE',
        label: intl.get('hsrh.searchConfig.model.searchConfig.fieldType').d('字段类型'),
        required: true,
      },
      {
        name: 'analyzerFlag',
        type: 'string',
        lookupCode: 'HPFM.FLAG',
        label: intl.get('hsrh.searchConfig.model.searchConfig.isParticiple').d('是否索引'),
      },
      {
        name: 'filedAnalyzerCode',
        type: 'string',
        lookupCode: 'HSRH.FIELD_ANALYZER',
        label: intl.get('hsrh.searchConfig.model.searchConfig.wordSegmenter').d('分词器'),
        dynamicProps: {
          readOnly: ({ record }) => {
            const { fieldType } = record.toData();
            return !(fieldType === 'TEXT');
          },
        },
      },
      {
        name: 'pkFlag',
        type: 'string',
        lookupCode: 'HPFM.FLAG',
        label: intl.get('hsrh.searchConfig.model.searchConfig.pkFlag').d('是否主键'),
        defaultValue: 0,
        required: true,
      },
    ],

    events: {
      update: ({ record, name, value }) => {
        if (name === 'fieldType' && value !== 'TEXT') {
          record.set('analyzerFlag', '1');
          record.set('filedAnalyzerCode', null);
        }
        if (name === 'analyzerFlag' && (value === '0' || value === '1')) {
          record.set('filedAnalyzerCode', 'STANDARD');
        }
      },
    },
    transport: {
      create: (config) => {
        const { params } = config;
        let { data } = config;
        let tempFlag;
        const tempData = data.map((item) => {
          tempFlag = item.analyzerFlag === '0' || item.analyzerFlag === '1';
          return {
            // eslint-disable-next-line no-nested-ternary
            analyzerFlag: tempFlag ? (item.analyzerFlag === '1' ? 1 : 0) : null,
            fieldMapping: item.fieldMapping,
            fieldName: item.fieldName,
            fieldType: item.fieldType,
            filedAnalyzerCode: item.filedAnalyzerCode,
            pkFlag: item.pkFlag === '1' ? 1 : 0,
          };
        });
        const {
          indexCode,
          enabledFlag,
          remark,
          dataSourceApiId,
          shards,
          replicas,
        } = formRecord.current.toData();
        const tempIndexCode = isObject(indexCode) ? indexCode.indexCode : indexCode;
        const tempDataSourceApiId = isObject(dataSourceApiId)
          ? dataSourceApiId.interfaceId
          : dataSourceApiId;
        data = {
          indexCode: tempIndexCode,
          enabledFlag,
          remark,
          shards,
          replicas,
          dataSourceApiId: tempDataSourceApiId,
          indexFieldList: tempData,
        };
        const url = `${HZERO_HSRH}/v1/${organizationId}/indices/with-fields`;
        return {
          data,
          params,
          url,
          method: 'POST',
        };
      },
      read: (config) => {
        const url = `${HZERO_HSRH}/v1/${organizationId}/index-fields`;
        return {
          config,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const { params } = config;
        let { data } = config;
        let tempFlag;
        const tempData = data.map((item) => {
          tempFlag = item.analyzerFlag === '0' || item.analyzerFlag === '1';
          return {
            // eslint-disable-next-line no-nested-ternary
            analyzerFlag: tempFlag ? (item.analyzerFlag === '1' ? 1 : 0) : null,
            fieldMapping: item.fieldMapping,
            fieldName: item.fieldName,
            fieldType: item.fieldType,
            filedAnalyzerCode: item.filedAnalyzerCode,
            pkFlag: item.pkFlag === '1' ? 1 : 0,
          };
        });
        const { indexCode, enabledFlag, remark, dataSourceApiId } = formRecord.current.toData();
        const tempIndexCode = isObject(indexCode) ? indexCode.indexCode : indexCode;
        const tempDataSourceApiId = isObject(dataSourceApiId)
          ? dataSourceApiId.interfaceId
          : dataSourceApiId;
        data = {
          indexCode: tempIndexCode,
          enabledFlag,
          remark,
          dataSourceApiId: tempDataSourceApiId,
          indexFieldList: tempData,
        };
        const url = `${HZERO_HSRH}/v1/${organizationId}/indices/with-fields`;
        const method = 'POST';
        return {
          data,
          params,
          url,
          method,
        };
      },
      destroy: (config) => {
        const { data, params } = config;
        const url = `${HZERO_HSRH}/v1/${organizationId}/index-fields`;
        return {
          data: data[0],
          params,
          url,
          method: 'DELETE',
        };
      },
    },
  };
}
export { tableDS, detailFormDS, detailTableDS };
