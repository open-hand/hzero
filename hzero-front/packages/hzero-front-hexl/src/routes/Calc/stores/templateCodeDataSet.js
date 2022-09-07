/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-08-29 18:54:08
 * @LastEditTime : 2020-01-02 10:08:38
 * @LastEditors  : shenghao.liu@hand-china.com
 */
import { API_HOST, HZERO_HEXL } from 'utils/config';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';

const codeValidator = (value) => {
  if (/^[A-Z0-9/_/-]+$/.test(value)) {
    return true;
  } else {
    return intl.get('hzero.hexl.validation.code').d('只支持输入大写字母/数字/符号_-');
  }
};

export default () => ({
  primaryKey: 'templateId',
  name: 'templateCode',
  autoQuery: true,
  transport: {
    read: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates`,
      method: 'get',
    }),
    create: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates`,
      method: 'post',
      data: data.length > 1 ? data : data[0],
    }),
    update: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates`,
      method: 'put',
      data: data.length > 1 ? data : data[0],
    }),
    destroy: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates`,
      method: 'delete',
      data: data.length > 1 ? data : data[0],
    }),
  },
  // pageSize: 10,
  queryFields: [
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateCode').d('模板代码'),
      maxLength: 255,
      format: 'uppercase',
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.hexl.field.description').d('描述'),
      maxLength: 255,
    },
    // { name: 'enabledFlag', type: 'boolean', label: '启用', trueValue: 'Y', falseValue: 'N' },
  ],
  fields: [
    {
      name: 'templateId',
      type: 'string',
    },
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateCode').d('模板代码'),
      required: true,
      maxLength: 255,
      format: 'uppercase',
      validator: codeValidator,
    },
    {
      name: 'templateVersion',
      type: 'number',
      label: intl.get('hzero.hexl.field.templateVersion').d('模板版本'),
      required: true,
      max: Number.MAX_SAFE_INTEGER,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.hexl.field.description').d('描述'),
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
  ],
});
