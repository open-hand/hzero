/*
 * @Description: 字段配置头及行信息
 * @Author: suyu.zeng@hand-china.com
 * @Date: 2019-10-08 11:15:35
 * @LastEditTime : 2019-12-18 15:50:35
 * @LastEditors  : shenghao.liu@hand-china.com
 */
import { API_HOST, HZERO_HEXL } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

export default () => ({
  // primaryKey: 'configHdId',
  name: 'configHdAll',
  autoQuery: false,
  transport: {
    submit: ({ config, data, dataSet }) => {
      const arr = dataSet.toData();
      arr.forEach((i, index) => {
        if (i.status) {
          arr[index]._status = i.status;
        }
      });
      return {
        ...config,
        url:
          data.length > 1
            ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/batch/all`
            : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/all`,
        method: arr[0] && arr[0].status && arr[0].status === 'update' ? 'put' : 'post',
        data: arr.length > 1 ? arr : arr[0],
      };
    },
    // update: ({ config, data }) => ({
    //   ...config,
    //   url:
    //     data.length > 1
    //       ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/batch/all`
    //       : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-hds/all`,
    //   method: 'put',
    //   data: data.length > 1 ? data : data[0],
    // }),
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
      name: 'tableName',
      type: 'string',
      label: intl.get('hzero.hexl.field.tableName').d('数据库表名'),
      maxLength: 32,
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
      lovCode: 'HADM.ROUTE.SERVICE_CODE.ORG',
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
    { name: 'excelTemplateConfigLns', type: 'array' },
    { name: 'excelTemplateConfigBts', type: 'array' },
  ],
});
