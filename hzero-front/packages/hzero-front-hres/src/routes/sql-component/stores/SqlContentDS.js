/**
 * SQL组件
 * @Author: NJQ <jiangqi.nan@hand-china.com>
 * @Date: 2019-10-18
 * @LastEditTime: 2019-10-18
 * @Copyright: Copyright (c) 2018, Hand
 */
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { getNodeConfig } from '@/utils/saveNode';

export default {
  autoQuery: false,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/sql-component`,
      method: 'GET',
      params: { ...params, ...data },
    }),
    submit: ({ dataSet, data }) => {
      const nodeInfo = getNodeConfig(
        { ...data[0], id: dataSet.current.get('id') },
        'sql',
        'sqlName'
      );
      return {
        url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/sql-component/submit`,
        data: { sqlComponentList: data, processNode: nodeInfo },
      };
    },
  },
  fields: [
    {
      name: 'id',
      type: 'string',
      ignore: 'always',
    },
    {
      name: 'sqlName',
      type: 'string',
      label: intl.get('hres.flow.model.flow.sqlName').d('sql名称'),
      pattern: /[\u4e00-\u9fa5]|[a-z0-9A-Z\-_]/,
      required: true,
    },
    {
      name: 'dataSourceCodelov',
      label: intl.get('hres.flow.model.flow.dataSource').d('数据源'),
      type: 'object',
      lovCode: 'HPFM.DATASOURCE',
      ignore: 'always',
      required: true,
      dynamicProps: () => ({
        lovPara: {
          // dsPurposeCode: 'DR',
          enabledFlag: 1,
          tenantId: getCurrentOrganizationId(),
        },
      }),
    },
    {
      name: 'dataSourceDescription',
      type: 'string',
      label: intl.get('hres.flow.model.flow.dataSource').d('数据源'),
      bind: 'dataSourceCodelov.description',
    },
    {
      name: 'dataSourceCode',
      type: 'string',
      label: intl.get('hres.flow.model.flow.dataSource').d('数据源'),
      bind: 'dataSourceCodelov.datasourceCode',
    },
    {
      name: 'sqlText',
      type: 'string',
      label: intl.get('hres.flow.model.flow.sqlContent').d('sql内容'),
      required: true,
    },
    { name: 'tenantId', type: 'number' },
  ],
};
