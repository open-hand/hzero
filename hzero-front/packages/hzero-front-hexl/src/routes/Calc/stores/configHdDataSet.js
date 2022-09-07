/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-08-29 18:54:08
 * @LastEditTime : 2019-12-23 21:46:25
 * @LastEditors  : shenghao.liu@hand-china.com
 */
import { API_HOST, HZERO_HEXL } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

export default () => ({
  primaryKey: 'configHdId',
  name: 'configHd',
  autoQuery: false,
  transport: {
    read: ({ config }) => ({
      ...config,
      url: `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds`,
      method: 'get',
    }),
    create: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds`,
      method: 'post',
      data: data.length > 1 ? data : data[0],
    }),
    update: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds`,
      method: 'put',
      data: data.length > 1 ? data : data[0],
    }),
    destroy: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds`,
      method: 'delete',
      data: data.length > 1 ? data : data[0],
    }),
  },
  fields: [
    {
      name: 'templateId',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateId').d('模板代码'),
    },
    {
      name: 'sheetName',
      type: 'string',
      label: intl.get('hzero.hexl.field.sheetName').d('标签页'),
    },
    {
      name: 'sheetCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.sheetCode').d('标签代码'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateDescription').d('模板描述'),
      maxLength: 255,
    },
    {
      name: 'configHdId',
      type: 'string',
      label: intl.get('hzero.hexl.field.configHdId').d('配置头id'),
    },
    {
      name: 'multiLineFrom',
      type: 'number',
      label: intl.get('hzero.hexl.field.multilineFrom').d('多行范围从'),
      max: 2 ** 31 - 1,
    },
    {
      name: 'multiLineTo',
      type: 'number',
      label: intl.get('hzero.hexl.field.multilineTo').d('多行范围到'),
      max: 2 ** 31 - 1,
    },
    {
      name: 'priceListConfigLns',
      type: 'array',
      label: intl.get('hzero.hexl.field.priceListConfigLns').d('模板配置行'),
    },
    {
      name: 'tableNameLov',
      type: 'object',
      label: intl.get('hzero.hexl.field.tableName').d('数据库表名'),
      maxLength: 32,
      ignore: 'always',
      noCache: true,
      lovCode: 'HEXL.TABLE',
    },
    {
      name: 'tableName',
      type: 'string',
      bind: 'tableNameLov.table',
    },
    {
      name: 'tableType',
      type: 'string',
      label: intl.get('hzero.hexl.field.tableType').d('数据库表类型'),
      maxLength: 120,
    },
    {
      name: 'callbackUrl',
      type: 'string',
      label: intl.get('hzero.hexl.field.callbackUrl').d('回调地址'),
      maxLength: 480,
    },
    {
      name: 'serviceCodeLov',
      type: 'object',
      label: intl.get('hzero.hexl.field.serviceCodeLov').d('服务编码'),
      lovCode: 'HADM.SERVICE',
      ignore: 'always',
    },
    {
      name: 'serviceCode',
      type: 'string',
      bind: 'serviceCodeLov.serviceCode',
      maxLength: 255,
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceCodeLov.serviceName',
    },
  ],
});
