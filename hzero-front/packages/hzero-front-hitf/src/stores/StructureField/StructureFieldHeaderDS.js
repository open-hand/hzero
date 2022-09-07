import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import {
  STRUCTURE_CATEGORY,
  STRUCTURE_BIZ_USAGE,
  ENABLED_FLAG,
  ENABLED_FLAG_FIELDS,
  STRUCTURE_COMPOSITION,
} from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 结构字段头表 DataSet
 */

export default () => ({
  primaryKey: 'headerId',
  autoQuery: false,
  pageSize: 10,
  name: DataSet.StructureFieldHeaderDS,
  selection: false,
  fields: [
    {
      name: 'headerId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'structureNum',
      type: 'string',
      required: true,
      pattern: /^[a-zA-Z]*$/,
      format: 'uppercase',
      defaultValidationMessages: {
        patternMismatch: intl.get('view.validation.format.structureNum').d('全大写字母'),
      },
      label: intl.get('hitf.structureField.model.structureFieldHeader.structureNum').d('结构代码'),
    },
    {
      name: 'structureName',
      type: 'string',
      required: true,
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.structureName').d('结构名称'),
    },
    {
      name: 'structureCategory',
      type: 'string',
      required: true,
      lookupCode: STRUCTURE_CATEGORY,
      label: intl.get('hitf.structureField.model.structureFieldHeader.strCategory').d('结构分类'),
    },
    {
      name: 'composition',
      type: 'string',
      required: true,
      lookupCode: STRUCTURE_COMPOSITION,
      label: intl.get('hitf.structureField.model.structureFieldHeader.composition').d('构建方式'),
    },
    {
      name: 'structureDesc',
      type: 'string',
      label: intl.get('hitf.structureField.model.structureFieldHeader.structureDesc').d('结构描述'),
    },
    {
      name: 'bizUsage',
      type: 'string',
      required: true,
      lookupCode: STRUCTURE_BIZ_USAGE,
      label: intl.get('hitf.structureField.model.structureFieldHeader.bizUsage').d('业务用途'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      trueValue: 1,
      defaultValue: ENABLED_FLAG_FIELDS.YES,
      required: true,
      lookupCode: ENABLED_FLAG,
      label: intl.get('hitf.structureField.model.structureFieldHeader.enabledFlag').d('是否启用'),
    },
  ],
  queryFields: [
    {
      name: 'structureName',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetHeader.structureName').d('结构名称'),
    },
    {
      name: 'structureNum',
      type: 'string',
      pattern: /^[a-zA-Z]*$/,
      format: 'uppercase',
      defaultValidationMessages: {
        patternMismatch: intl.get('view.validation.format.structureNum').d('全大写字母'),
      },
      label: intl.get('hitf.structureField.model.structureFieldHeader.structureNum').d('结构代码'),
    },
    {
      name: 'structureCategory',
      type: 'string',
      lookupCode: STRUCTURE_CATEGORY,
      label: intl.get('hitf.structureField.model.structureFieldHeader.strCategory').d('结构分类'),
    },
    {
      name: 'structureDesc',
      type: 'string',
      label: intl.get('hitf.structureField.model.structureFieldHeader.structureDesc').d('结构描述'),
    },
    {
      name: 'bizUsage',
      type: 'string',
      lookupCode: STRUCTURE_BIZ_USAGE,
      label: intl.get('hitf.structureField.model.structureFieldHeader.bizUsage').d('业务用途'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      lookupCode: ENABLED_FLAG,
      label: intl.get('hitf.structureField.model.structureFieldHeader.enabledFlag').d('是否启用'),
    },
    {
      name: 'composition',
      type: 'string',
      lookupCode: STRUCTURE_COMPOSITION,
      label: intl.get('hitf.structureField.model.structureFieldHeader.composition').d('构建方式'),
    },
  ],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/structure-field-headers`
        : `${HZERO_HITF}/v1/structure-field-headers`;
      return {
        data,
        params,
        url: getUrl(url, data.headerId),
        method: 'GET',
      };
    },
    submit: ({ data, params }) => {
      const _data = {
        ...data[0],
        tenantId: organizationId,
      };
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-headers`
          : `${HZERO_HITF}/v1/structure-field-headers`,
        data: _data,
        params,
        method: 'POST',
      };
    },
    update: ({ data, params }) => {
      const _data = {
        ...data[0],
        tenantId: organizationId,
      };
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-headers`
          : `${HZERO_HITF}/v1/structure-field-headers`,
        data: _data,
        params,
        method: 'PUT',
      };
    },
    destroy: ({ data, params }) => {
      const _data = {
        ...data[0],
        tenantId: organizationId,
      };
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-headers`
          : `${HZERO_HITF}/v1/structure-field-headers`,
        data: _data,
        params,
        method: 'DELETE',
      };
    },
  },
  feedback: {},
});

function getUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
