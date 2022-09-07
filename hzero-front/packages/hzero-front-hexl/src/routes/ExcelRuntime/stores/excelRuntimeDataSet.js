/*
 * @description: excelRuntime数据源配置
 * @Author: suyu.zeng@hand-china.com
 * @Date: 2019-09-17 12:08:34
 * @LastEditors  : shenghao.liu@hand-china.com
 * @LastEditTime : 2020-01-02 09:53:27
 */
import { API_HOST, HZERO_HEXL } from 'utils/config';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';

export default () => ({
  primaryKey: 'runtimeId',
  name: 'excelRuntime',
  autoQuery: true,
  transport: {
    read: ({ data, params }) => {
      let newParams = { ...params };
      if (params.sort === 'templateRecord,asc' || params.sort === 'templateRecord,desc') {
        newParams = {
          ...params,
          sort: params.sort === 'templateRecord,asc' ? 'templateCode,asc' : 'templateCode,desc',
        };
      }
      return {
        url:
          data.length > 1
            ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes/batch`
            : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes`,
        method: 'get',
        params: newParams,
      };
    },
    create: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes`,
      method: 'post',
      data: data.length > 1 ? data : data[0],
    }),
    update: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes`,
      method: 'put',
      data: data.length > 1 ? data : data[0],
    }),
    destroy: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-runtimes`,
      method: 'delete',
      data: data.length > 1 ? data : data[0],
    }),
  },
  queryFields: [
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.hexl.field.runtimeDescription').d('试算描述'),
      maxLength: 255,
    },
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateCode').d('模板代码'),
      format: 'uppercase',
      maxLength: 255,
    },
  ],
  fields: [
    {
      name: 'templateRecord',
      type: 'object',
      label: intl.get('hzero.hexl.field.templateCode').d('模板代码'),
      required: true,
      lovCode: 'HEXL.TEMPLATES',
    },
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateCode').d('模板代码'),
      required: true,
      bind: 'templateRecord.templateCode',
      maxLength: 255,
    },
    {
      name: 'templateVersion',
      type: 'number',
      label: intl.get('hzero.hexl.field.templateVersion').d('模板版本'),
      required: true,
      bind: 'templateRecord.templateVersion',
      max: Number.MAX_SAFE_INTEGER,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.hexl.field.runtimeDescription').d('试算描述'),
      required: true,
      maxLength: 255,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.hexl.field.enabledFlag').d('启用标识'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'runtimeId',
      type: 'string',
      label: intl.get('hzero.hexl.field.runtimeId').d('运行时ID'),
    },
    {
      name: 'sheets',
      type: 'sheets',
      label: intl.get('hzero.hexl.field.sheets').d('excel内容'),
    },
  ],
});
