/**
 * 通用模板管理- DS
 * @date: 2020-8-5
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { CODE } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_PLATFORM}/v1/${organizationId}` : `${HZERO_PLATFORM}/v1`;

const tableDS = () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: () => ({
      url: `${apiPrefix}/common-templates`,
      method: 'get',
    }),
  },
  queryFields: [
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateCode').d('模板编码'),
    },
    {
      name: 'templateName',
      type: 'string',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateName').d('模板名称'),
    },
    {
      name: 'templateCategoryCode',
      type: 'string',
      lookupCode: 'HPFM.TEMPLATE_CATEGORY',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateCategoryCode').d('模板分类'),
    },
    {
      name: 'lang',
      type: 'string',
      lookupCode: 'HPFM.LANGUAGE',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.lang').d('语言'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      lookupCode: 'HPFM.ENABLED_FLAG',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.enabledFlag').d('是否启用'),
    },
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
  ].filter(Boolean),
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
    },
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateCode').d('模板编码'),
    },
    {
      name: 'templateName',
      type: 'string',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateName').d('模板名称'),
    },
    {
      name: 'templateCategoryCode',
      type: 'string',
      lookupCode: 'HPFM.TEMPLATE_CATEGORY',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateCategoryCode').d('模板分类'),
    },
    {
      name: 'lang',
      type: 'string',
      lookupCode: 'HPFM.LANGUAGE',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.lang').d('语言'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.enabledFlag').d('是否启用'),
    },
    {
      name: 'creationDate',
      type: 'string',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.creationDate').d('创建日期'),
    },
  ],
});

const detailDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  transport: {
    read: ({ dataSet }) => {
      const { templateId } = dataSet;
      return {
        url: `${apiPrefix}/common-templates/${templateId}`,
        method: 'get',
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/common-templates`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data, dataSet }) => {
      const { templateId } = dataSet;
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/common-templates/${templateId}`,
        method: 'PUT',
        data: other,
      };
    },
  },
  fields: [
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
      required: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    !isTenant && {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'templateCode',
      type: 'string',
      required: true,
      maxLength: 60,
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateCode').d('模板编码'),
      pattern: CODE,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.code')
          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'templateName',
      type: 'string',
      required: true,
      maxLength: 240,
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateName').d('模板名称'),
    },
    {
      name: 'templateCategoryCode',
      type: 'string',
      required: true,
      lookupCode: 'HPFM.TEMPLATE_CATEGORY',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateCategoryCode').d('模板分类'),
    },
    {
      name: 'lang',
      type: 'string',
      required: true,
      lookupCode: 'HPFM.LANGUAGE',
      label: intl.get('hpfm.generalTemplate.model.generalTemp.lang').d('语言'),
    },
    {
      name: 'templateContent',
      type: 'string',
      required: true,
      label: intl.get('hpfm.generalTemplate.model.generalTemp.templateContent').d('模板内容'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      required: true,
      label: intl.get('hzero.common.status.enable').d('启用'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ].filter(Boolean),
});

export { tableDS, detailDS };
