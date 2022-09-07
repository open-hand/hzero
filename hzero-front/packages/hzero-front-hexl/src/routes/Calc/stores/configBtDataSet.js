/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-08-29 18:54:08
 * @LastEditTime : 2019-12-18 11:27:56
 * @LastEditors  : shenghao.liu@hand-china.com
 */
import { API_HOST, HZERO_HEXL } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

export default () => ({
  primaryKey: 'configBtId',
  name: 'configBt',
  autoQuery: false,
  transport: {
    read: ({ config }) => ({
      ...config,
      url: `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts`,
      method: 'get',
    }),
    create: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts`,
      method: 'post',
      data: data.length > 1 ? data : data[0],
    }),
    update: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts`,
      method: 'put',
      data: data.length > 1 ? data : data[0],
    }),
    destroy: ({ config, data }) => ({
      ...config,
      url:
        data.length > 1
          ? `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts/batch`
          : `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/template-config-bts`,
      method: 'delete',
      data: data.length > 1 ? data : data[0],
    }),
  },
  // pageSize: 5,
  fields: [
    {
      name: 'templateId',
      type: 'string',
      label: intl.get('hzero.hexl.field.templateId').d('模板代码'),
    },
    {
      name: 'buttonName',
      type: 'string"',
      label: intl.get('hzero.hexl.field.buttonName').d('按钮名称'),
      required: true,
      maxLength: 120,
    },
    {
      name: 'buttonType',
      type: 'string"',
      label: intl.get('hzero.hexl.field.buttonType').d('按钮类型'),
      required: true,
      lookupCode: 'HEXL.BUTTON_TYPE',
      maxLength: 120,
    },
    {
      name: 'configBtId',
      type: 'string',
      label: intl.get('hzero.hexl.field.configBtId').d('配置按钮ID'),
    },
    {
      name: 'guessValue',
      type: 'number',
      label: intl.get('hzero.hexl.field.guessValue').d('猜测值'),
      max: Number.MAX_SAFE_INTEGER,
    },
    {
      name: 'targetExcelCode',
      type: 'string"',
      label: intl.get('hzero.hexl.field.targetExcelCode').d('目标单元格'),
      maxLength: 30,
    },
    {
      name: 'targetExcelValue',
      type: 'number',
      label: intl.get('hzero.hexl.field.targetExcelValue').d('目标值'),
      max: Number.MAX_SAFE_INTEGER,
    },
    {
      name: 'variableExcelCode',
      type: 'string',
      label: intl.get('hzero.hexl.field.variableExcelCode').d('可变单元格'),
      maxLength: 30,
    },
    {
      name: 'buttonAction',
      type: 'url',
      label: intl.get('hzero.hexl.field.interfaceAddress').d('接口地址'),
      maxLength: 120,
      help: intl
        .get('hzero.hexl.help.httpUrl')
        .d(
          '接口地址前必须添加协议，http或者https，例如：http://hzero-webexcel/v1/0/xxx ; 仅支持post请求'
        ),
    },
  ],
});
