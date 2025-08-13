/**
 *
 * @date: 2020-04-30
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { DataSet } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { HZERO_ADM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_ADM}/v1/${organizationId}` : `${HZERO_ADM}/v1`;

const typeOptionDs = new DataSet({
  selection: 'single',
  data: [
    {
      value: '1',
      meaning: intl.get('hadm.ruleConfig.model.ruleConfig.shareTable').d('分表'),
    },
    {
      value: '2',
      meaning: intl.get('hadm.ruleConfig.model.ruleConfig.shareLibrary').d('分库'),
    },
    {
      value: '3',
      meaning: intl.get('hadm.ruleConfig.model.ruleConfig.shareAll').d('分库分表'),
    },
  ],
});

const initDS = () => {
  return {
    autoQuery: true,
    selection: false,
    dataKey: 'content',
    fields: [
      {
        name: 'index',
        type: 'number',
        label: intl.get('hzero.common.view.serialNumber').d('序号'),
      },
      {
        name: 'proxySchemaName',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.proxySchemaName').d('proxy数据源schema'),
      },
      {
        name: 'serviceCode',
        type: 'string',
        label: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
      },
      {
        name: 'dsUrl',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.dsUrl').d('服务数据源URL'),
      },
      {
        name: 'datasourceGroupName',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.datasourceGroupName').d('逻辑数据源组名'),
      },
      {
        name: 'proxyDsUrl',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.proxyDsUrl').d('proxy数据源URL'),
      },
      {
        name: 'action',
        label: intl.get('hzero.common.button.action').d('操作'),
      },
    ],
    transport: {
      read: ({ params, data }) => ({
        url: `${apiPrefix}/sp-rule-headers`,
        method: 'GET',
        params: { ...params, ...data },
      }),
      destroy: ({ data }) => ({
        url: `${apiPrefix}/sp-rule-headers`,
        method: 'DELETE',
        data: data[0],
      }),
    },
  };
};

const detailDS = () => {
  return {
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [
      {
        name: 'bind',
        type: 'object',
        lovCode: 'HADM.PROXY_LINE_TABLE',
        ignore: 'always',
        noCache: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.datasourceGroupName').d('逻辑数据源组名'),
      },
      {
        name: 'datasourceGroupObj',
        type: 'object',
        lovCode: 'HADM.DATASOURCE_GROUP',
        ignore: 'always',
        noCache: true,
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.datasourceGroupName').d('逻辑数据源组名'),
      },
      {
        name: 'datasourceGroupId',
        type: 'string',
        bind: 'datasourceGroupObj.datasourceGroupId',
      },
      {
        name: 'datasourceGroupName',
        type: 'string',
        required: true,
        bind: 'datasourceGroupObj.datasourceGroupName',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.datasourceGroupName').d('逻辑数据源组名'),
      },
      {
        name: 'proxySchemaName',
        type: 'string',
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.proxySchemaName').d('proxy数据源schema'),
      },
      {
        name: 'broadcastTableArray',
        type: 'string',
        multiple: true,
        readOnly: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.broadcast').d('广播表'),
      },
      {
        name: 'dbShardingNum',
        type: 'number',
        step: 1,
        min: 2,
        required: true,
        dynamicProps: {
          required({ record }) {
            return record.get('dbShardingType') === 'HADM.SHARDING_RULE_INLINE';
          },
        },
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.subLibraryNum').d('分库数量'),
      },
      {
        name: 'dbShardingColumn',
        type: 'string',
        multiple: true,
        defaultValue: null,
        dynamicProps: {
          required({ record }) {
            return record.get('dbShardingType') !== 'HADM.SHARDING_RULE_NONE';
          },
        },
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.subLibraryField').d('分库字段'),
      },
      {
        name: 'dbShardingTemplate',
        type: 'string',
        required: true,
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.dbShardingTemplate').d('分库规则'),
      },
      {
        name: 'tableShardingNum',
        type: 'number',
        step: 1,
        min: 2,
        dynamicProps: {
          required({ record }) {
            return record.get('tableShardingType') === 'HADM.SHARDING_RULE_INLINE';
          },
        },
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.subTableNum').d('分表数量'),
      },
      {
        name: 'tableShardingColumn',
        multiple: true,
        type: 'string',
        defaultValue: null,
        dynamicProps: {
          required({ record }) {
            return record.get('tableShardingType') !== 'HADM.SHARDING_RULE_NONE';
          },
        },
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.subTableField').d('分表字段'),
      },
      {
        name: 'tableShardingTemplate',
        type: 'string',
        required: true,
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.tableShardingTemplate').d('分表规则'),
      },
      {
        name: 'dbShardingType',
        type: 'string',
        required: true,
        lookupCode: 'HADM.SHARDING_RULE_TYPE',
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.dbRuleType').d('分库规则类型'),
      },
      {
        name: 'tableShardingType',
        type: 'string',
        required: true,
        lookupCode: 'HADM.SHARDING_RULE_TYPE',
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.tableRuleType').d('分表规则类型'),
      },
      {
        name: 'keyGeneratorStrategy',
        type: 'string',
        lookupCode: 'HADM.PROXY_PRIMARY',
        dynamicProps: {
          required({ record }) {
            return record.get('keyGeneratorField') || record.get('keyGeneratorStrategy');
          },
        },
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorStrategy').d('主键策略'),
      },
      {
        name: 'keyGeneratorField',
        type: 'string',
        dynamicProps: {
          required({ record }) {
            return record.get('keyGeneratorStrategy') || record.get('keyGeneratorField');
          },
        },
        label:
          intl.get('hadm.ruleConfig.model.ruleConfig.default').d('默认') +
          intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorExpParam').d('主键策略字段'),
      },
      // {
      //   name: 'tableShardingExp',
      //   type: 'string',
      //   label: intl.get('hadm.ruleConfig.model.ruleConfig.tableShardingExp').d('分表规则'),
      // },
      // {
      //   name: 'dbShardingExp',
      //   type: 'string',
      //   label: intl.get('hadm.ruleConfig.model.ruleConfig.dbShardingExp').d('分库规则'),
      // },
      // {
      //   name: 'keyGeneratorExp',
      //   type: 'string',
      //   label: intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorExp').d('主键策略'),
      // },
    ],
    transport: {
      read: ({ dataSet }) => {
        const { ruleHeaderId } = dataSet;
        return {
          url: `${apiPrefix}/sp-rule-headers/${ruleHeaderId}`,
          method: 'GET',
          data: {},
          params: {},
        };
      },
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-rule-headers/save`,
          method: 'POST',
          data: { ...other },
        };
      },
      update: ({ data, dataSet }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        const { spRuleLines } = dataSet;
        return {
          url: `${apiPrefix}/sp-rule-headers/save`,
          method: 'POST',
          data: { ...other, spRuleLines },
        };
      },
    },
  };
};

// 详情页面下的表格信息DS
const detailTableDS = () => {
  return {
    autoQuery: false,
    autoCreate: false,
    cacheSelection: true,
    selection: 'multiple',
    primaryKey: 'tableName',
    fields: [
      {
        name: 'tableName',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.tableName').d('表名'),
        type: 'string',
        required: true,
      },
      {
        name: 'actualDataNodes',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.actualDataNodes').d('数据节点'),
      },
      {
        name: 'type',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.type').d('分片模式'),
        options: typeOptionDs,
        defaultValue: '3',
        required: true,
      },
      {
        name: 'subLibraryRuleType',
        type: 'string',
        dynamicProps: ({ record }) => ({
          required: record.get('type') !== '1',
        }),
        lookupCode: 'HADM.SHARDING_RULE_TYPE',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.dbRuleType').d('分库规则类型'),
      },
      {
        name: 'subTableRuleType',
        type: 'string',
        dynamicProps: ({ record }) => ({
          required: record.get('type') !== '2',
        }),
        lookupCode: 'HADM.SHARDING_RULE_TYPE',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.tableRuleType').d('分表规则类型'),
      },
      {
        name: 'dsGroupNames',
        type: 'string',
      },
      {
        name: 'subLibraryNum',
        type: 'number',
        step: 1,
        min: 2,
        dynamicProps: ({ record }) => ({
          required:
            record.get('type') !== '1' &&
            record.get('subLibraryRuleType') === 'HADM.SHARDING_RULE_INLINE',
        }),
        label: intl.get('hadm.ruleConfig.model.ruleConfig.subLibraryNum').d('分库数量'),
      },
      {
        name: 'subTableNum',
        type: 'number',
        step: 1,
        min: 2,
        dynamicProps: ({ record }) => ({
          required:
            record.get('type') !== '2' &&
            record.get('subTableRuleType') === 'HADM.SHARDING_RULE_INLINE',
        }),
        label: intl.get('hadm.ruleConfig.model.ruleConfig.subTableNum').d('分表数量'),
      },

      {
        name: 'subLibraryField',
        type: 'string',
        multiple: true,
        max: 50,
        defaultValue: null,
        dynamicProps: ({ record }) => ({
          required:
            record.get('type') !== '1' &&
            record.get('subLibraryRuleType') !== 'HADM.SHARDING_RULE_NONE',
        }),
        label: intl.get('hadm.ruleConfig.model.ruleConfig.subLibraryField').d('分库字段'),
      },
      {
        name: 'dbShardingTemplate',
        type: 'string',
        dynamicProps: ({ record }) => ({
          required:
            record.get('type') !== '1' &&
            record.get('subLibraryRuleType') !== 'HADM.SHARDING_RULE_NONE',
        }),
        label: intl.get('hadm.ruleConfig.model.ruleConfig.dbShardingTemplate').d('分库规则'),
      },
      {
        name: 'subTableField',
        multiple: true,
        type: 'string',
        max: 50,
        defaultValue: null,
        dynamicProps: ({ record }) => ({
          required:
            record.get('type') !== '2' &&
            record.get('subTableRuleType') !== 'HADM.SHARDING_RULE_NONE',
        }),
        label: intl.get('hadm.ruleConfig.model.ruleConfig.subTableField').d('分表字段'),
      },
      {
        name: 'tableShardingTemplate',
        type: 'string',
        dynamicProps: ({ record }) => ({
          required:
            record.get('type') !== '2' &&
            record.get('subTableRuleType') !== 'HADM.SHARDING_RULE_NONE',
        }),
        label: intl.get('hadm.ruleConfig.model.ruleConfig.tableShardingTemplate').d('分表规则'),
      },

      {
        name: 'tableShardingExp',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.tableSharding').d('分表规则'),
      },
      {
        name: 'dbShardingExp',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.dbSharding').d('分库规则'),
      },

      {
        name: 'keyGeneratorExp',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorExp').d('主键策略'),
      },
      {
        name: 'keyGeneratorStrategy',
        type: 'string',
        lookupCode: 'HADM.PROXY_PRIMARY',
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorStrategy').d('主键策略'),
      },
      {
        name: 'keyGeneratorField',
        type: 'string',
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorExpParam').d('主键策略字段'),
      },
      {
        name: 'bindingTables',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.bindingTables').d('关联表'),
      },
    ],
    transport: {
      read: () => {
        return {
          url: `${apiPrefix}/sp-rule-lines/page/rule-line`,
          method: 'GET',
        };
      },
      destroy: ({ data }) => {
        return {
          url: `${apiPrefix}/sp-rule-lines`,
          method: 'DELETE',
          data: data[0],
        };
      },
    },
  };
};

const bindDS = {
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [],
  transport: {
    read: ({ dataSet }) => {
      const { ruleLineId } = dataSet;
      return {
        url: `${apiPrefix}/sp-rule-headers/binding`,
        method: 'POST',
        params: {
          ruleLineId,
          tables: dataSet
            .toData()
            .map((item) => item.tableName)
            .join(','),
        },
        data: {},
      };
    },
  },
};

const createConfigDS = {
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  paging: false,
  fields: [],
  transport: {
    read: () => {
      return {
        url: `${apiPrefix}/sp-rule-headers/generate-config`,
        method: 'GET',
      };
    },
  },
};

const useConfigDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    paging: false,
    fields: [
      {
        name: 'proxyUiIp',
        type: 'string',
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.proxyUiIp').d('proxy-ui地址（ip）'),
      },
      {
        name: 'proxyUiPort',
        type: 'string',
        required: true,
        defaultValue: 8088,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.proxyUiPort').d('端口号'),
      },
      {
        name: 'username',
        type: 'string',
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.username').d('proxy-ui用户名'),
      },
      {
        name: 'password',
        type: 'string',
        required: true,
        label: intl.get('hadm.ruleConfig.model.ruleConfig.password').d('proxy-ui密码'),
      },
    ],
    transport: {
      create: ({ dataSet, data }) => {
        const { proxyUiIp, proxyUiPort, username, password } = data[0];
        return {
          url: `${apiPrefix}/sp-rule-headers/update-proxy-config`,
          method: 'POST',
          data: {},
          params: {
            ruleHeaderId: dataSet.ruleHeaderId,
            proxyUiIp,
            proxyUiPort,
            username,
            password,
          },
        };
      },
    },
  };
};

const useProxyDS = {
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  paging: false,
  fields: [],
  transport: {
    read: ({ data }) => {
      return {
        url: `${apiPrefix}/sp-rule-headers/apply-datasource-config`,
        method: 'POST',
        data: {},
        params: { ruleHeaderId: data.ruleHeaderId },
      };
    },
  },
};

const addBroadcastDS = {
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  paging: false,
  fields: [],
  transport: {
    read: ({ data, dataSet }) => {
      const tables = dataSet
        .toData()
        .map((item) => item.tableName)
        .join(',');
      return {
        url: `${apiPrefix}/sp-rule-headers/broadcast-tables/add`,
        method: 'POST',
        data: {},
        params: {
          ruleHeaderId: data.ruleHeaderId,
          tables,
        },
      };
    },
  },
};

const removeBroadcastDS = {
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  paging: false,
  fields: [],
  transport: {
    read: ({ data, dataSet }) => {
      const tables = dataSet
        .toData()
        .map((item) => item.tableName)
        .join(',');
      return {
        url: `${apiPrefix}/sp-rule-headers/broadcast-tables/remove`,
        method: 'POST',
        data: {},
        params: {
          ruleHeaderId: data.ruleHeaderId,
          tables,
        },
      };
    },
  },
};

const tableDrawerDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [
      {
        name: 'tableName',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.tableName').d('表名'),
        required: true,
        type: 'string',
      },
      {
        name: 'actualDataNodes',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.actualDataNodes').d('数据节点'),
      },
      {
        name: 'dbShardingExp',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.dbShardingExp').d('分库规则表达式'),
      },
      {
        name: 'tableShardingExp',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.tableShardingExp').d('分表规则表达式'),
      },
      {
        name: 'keyGeneratorExp',
        type: 'string',
        label: intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorExp').d('主键策略'),
      },
    ],
    transport: {
      read: ({ dataSet }) => {
        const { urlRuleId } = dataSet;
        return {
          url: `${apiPrefix}/hadm/v1/sp-rule-lines/${urlRuleId}`,
          method: 'GET',
          params: {},
        };
      },
      create: ({ data, dataSet }) => {
        const { urlRuleId } = dataSet;
        const { sourceTenant = [] } = Array.isArray(data) ? data[0] : [];
        const newData = sourceTenant.map((item) => {
          return {
            sourceUserId: item.sourceUserId,
            urlRuleId,
          };
        });
        return {
          url: `${apiPrefix}/url-rule-users/batch-create`,
          method: 'POST',
          data: newData,
        };
      },
    },
  };
};

export {
  initDS,
  detailDS,
  bindDS,
  createConfigDS,
  useConfigDS,
  useProxyDS,
  detailTableDS,
  addBroadcastDS,
  removeBroadcastDS,
  tableDrawerDS,
};
