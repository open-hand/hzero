/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-08-29 18:54:08
 * @LastEditTime: 2019-12-02 18:02:54
 * @LastEditors: shenghao.liu@hand-china.com
 */
import { API_HOST, HZERO_HEXL } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

const codeValidator = (value) => {
  if (/^[A-Z0-9/_/-]+$/.test(value)) {
    return true;
  } else {
    return intl.get('hzero.hexl.validation.code').d('只支持输入大写字母/数字/符号_-');
  }
};

export default () => ({
  primaryKey: 'configLnId',
  name: 'configLn',
  autoQuery: false,
  transport: {
    read: ({ config }) => ({
      ...config,
      url: `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns`,
      method: 'get',
    }),
    create: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns`,
      method: 'post',
      data: data.length > 1 ? data : data[0],
    }),
    update: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns`,
      method: 'put',
      data: data.length > 1 ? data : data[0],
    }),
    destroy: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-lns`,
      method: 'delete',
      data: data.length > 1 ? data : data[0],
    }),
  },
  // pageSize: 5,
  fields: [
    {
      name: 'excelCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.excelCode').d('Excel编码'),
      required: true,
      maxLength: 30,
      format: 'uppercase',
      validator: codeValidator,
    },
    {
      name: 'columnName',
      type: 'string',
      label: intl.get('hzero.hexl.field.columnName').d('字段名'),
      required: true,
      maxLength: 255,
    },
    {
      name: 'columnType',
      type: 'string',
      label: intl.get('hzero.hexl.field.columnType').d('字段类型'),
      lookupCode: 'HEXL.COLUMN_TYPE',
      required: true,
      maxLength: 30,
    },
    {
      name: 'configHdId',
      type: 'string',
      label: intl.get('hzero.hexl.field.configHdId').d('配置头id'),
    },
    {
      name: 'configLnId',
      type: 'string',
      label: intl.get('hzero.hexl.field.configLnId').d('配置行id'),
    },
    {
      name: 'hideColumnFlag',
      type: 'boolean',
      label: intl.get('hzero.hexl.field.hideColumnFlag').d('隐藏单元格列'),
      trueValue: 'Y',
      falseValue: 'N',
    },
    {
      name: 'hideRowFlag',
      type: 'boolean',
      label: intl.get('hzero.hexl.field.hideRowFlag').d('隐藏单元格行'),
      trueValue: 'Y',
      falseValue: 'N',
    },
    {
      name: 'precisions',
      type: 'number',
      label: intl.get('hzero.hexl.field.precisions').d('精度'),
      max: 2 ** 31 - 1,
    },
    {
      name: 'prompt',
      type: 'string',
      label: intl.get('hzero.hexl.field.prompt').d('描述'),
      maxLength: 255,
    },
    {
      name: 'validateSql',
      type: 'string',
      label: intl.get('hzero.hexl.field.validateSql').d('验证sql'),
    },
    {
      name: 'validateType',
      type: 'string',
      label: intl
        .get('hzero.hexl.field.validateType')
        .d('验证方式syscode：hls.price_list_column_validate_type'),
    },
    // { name: 'vatColumnCode', type: 'string', label: '现金流税额编码' },
  ],
});
