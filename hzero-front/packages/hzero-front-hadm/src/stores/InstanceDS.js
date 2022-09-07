import { HZERO_ADM } from 'utils/config';
import intl from 'utils/intl';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

const treeDS = {
  autoQuery: true,
  // paging: false,
  selection: 'single',
  idField: 'instanceId',
  expandField: 'expand',
  parentField: 'service',
  fields: [{ name: 'expand', type: 'boolean' }],
  transport: {
    read: () => {
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/instances`
          : `${HZERO_ADM}/v1/instances`,
        method: 'get',
        transformResponse: (data) => {
          const parsedData = JSON.parse(data);
          const parentNodes = new Set();
          // eslint-disable-next-line no-unused-expressions
          parsedData &&
            parsedData.content &&
            parsedData.content.map((v) => {
              if (!parentNodes.has(v.service)) {
                parentNodes.add(v.service);
              }
              return true;
            });
          parsedData.content =
            parsedData &&
            parsedData.content &&
            parsedData.content.concat(
              [...parentNodes].map((v, index) => {
                if (index === 0) {
                  return { instanceId: v, service: null };
                }
                return { instanceId: v, service: null };
              })
            );
          return parsedData;
        },
      };
    },
  },
};

const detailDS = () => {
  return {
    autoQuery: false,
    selection: 'single',
    paging: false,
    transport: {
      read: ({ dataSet }) => {
        const {
          queryParameter: { instanceId },
        } = dataSet;
        return {
          url: isTenantRoleLevel()
            ? `${HZERO_ADM}/v1/${organizationId}/instances/${instanceId}`
            : `${HZERO_ADM}/v1/instances/${instanceId}`,
          method: 'get',
        };
      },
    },
    fields: [
      {
        name: 'instanceId',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.instanceId').d('实例ID'),
      },
      {
        name: 'hostName',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.hostName').d('主机名'),
      },
      {
        name: 'ipAddr',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.ipAddr').d('IP'),
      },
      {
        name: 'app',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.app').d('所属微服务'),
      },
      {
        name: 'port',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.port').d('端口号'),
      },
      {
        name: 'version',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.version').d('版本'),
      },
      {
        name: 'registrationTime',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.registrationTime').d('注册时间'),
      },
      {
        name: 'metadata',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.metadata').d('元数据'),
      },
    ],
  };
};

const tableDS = () => {
  return {
    selection: false,
    paging: false,
    fields: [
      {
        name: 'key',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.key').d('名字'),
      },
      {
        name: 'value',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.value').d('值'),
      },
    ],
  };
};

const codeDS = () => {
  return {
    selection: false,
    paging: false,
    fields: [
      {
        name: 'configInfo',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.configInfo').d('配置信息'),
      },
      {
        name: 'envInfo',
        type: 'string',
        label: intl.get('hadm.instance.model.instance.env').d('环境信息'),
      },
    ],
  };
};

export { treeDS, detailDS, tableDS, codeDS };
