import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import {
  ENABLED_FLAG,
  ENABLED_FLAG_FIELDS,
  STRUCTURE_FIELD_TYPE_FIELDS,
  STRUCTURE_FIELD_TYPE_VALUES,
} from '@/constants/CodeConstants';
import { DataSet } from 'choerodon-ui/pro';
import DataSetStr from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();

// 字段类型下拉框数据
const fieldTypeArrAll = [
  STRUCTURE_FIELD_TYPE_VALUES.OBJECT,
  STRUCTURE_FIELD_TYPE_VALUES.ARRAY,
  STRUCTURE_FIELD_TYPE_VALUES.STRING,
  STRUCTURE_FIELD_TYPE_VALUES.DIGITAL,
  STRUCTURE_FIELD_TYPE_VALUES.BOOL,
];

/**
 * 结构字段行表 DataSet
 */
export default (fieldTypeArr = fieldTypeArrAll) => ({
  primaryKey: 'lineId',
  autoQuery: false,
  pageSize: 10,
  name: DataSetStr.StructureFieldLineDS,
  selection: false,
  parentField: 'parentId',
  idField: 'lineId',
  fields: [
    {
      name: 'parentId',
      type: 'string',
    },
    {
      name: 'parentName',
      type: 'string',
      ignore: 'always',
      label: intl.get('hitf.structureField.model.structureFieldLine.parentName').d('父字段名称'),
    },
    {
      name: 'lineId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'headerId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'fieldName',
      type: 'string',
      required: true,
      pattern: /^[a-zA-Z]*$/,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hitf.structureField.view.validation.format.fieldName')
          .d('全字母'),
      },
      label: intl.get('hitf.structureField.model.structureFieldLine.fieldName').d('字段名称'),
    },
    {
      name: 'fieldDesc',
      type: 'string',
      label: intl.get('hitf.structureField.model.structureFieldLine.fieldDesc').d('字段描述'),
    },
    {
      name: 'fieldType',
      type: 'string',
      required: true,
      options: new DataSet({
        selection: 'single',
        paging: false,
        data: fieldTypeArr,
      }),
      label: intl.get('hitf.structureField.model.structureFieldLine.fieldType').d('字段类型'),
    },
    {
      name: 'seqNum',
      type: 'number',
      required: true,
      min: 0,
      step: 10,
      defaultValue: 10,
      label: intl.get('hitf.structureField.model.structureFieldLine.seqNum').d('排序号'),
    },
    {
      name: 'formatMask',
      type: 'string',
      label: intl.get('hitf.structureField.model.structureFieldLine.formatMask').d('格式掩码'),
    },
    {
      name: 'defaultValue',
      type: 'string',
      label: intl.get('hitf.structureField.model.structureFieldLine.defaultValue').d('默认值'),
      dynamicProps: ({ record }) => {
        let pattern = '';
        let defaultValidationMessages = '';
        // 字段类型=【布尔】 只能输入true/false
        if (STRUCTURE_FIELD_TYPE_FIELDS.BOOL === record.get('fieldType')) {
          pattern = /^(?:(true)|(false))*$/;
          defaultValidationMessages = {
            patternMismatch: intl
              .get('hitf.structureField.view.validation.format.defaultValue')
              .d('只能输入true/false'),
          };
        }
        return {
          pattern,
          defaultValidationMessages,
        };
      },
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.structureField.model.structureFieldLine.remark').d('备注'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      trueValue: 1,
      defaultValue: ENABLED_FLAG_FIELDS.YES,
      required: true,
      lookupCode: ENABLED_FLAG,
      label: intl.get('hitf.structureField.model.structureFieldLine.enabledFlag').d('是否启用'),
    },
  ],
  queryFields: [],
  transport: {
    read: function read({ data, params }) {
      let url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines`
        : `${HZERO_HITF}/v1/structure-field-lines`;
      if (data.lineId) {
        url = isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines/detail`
          : `${HZERO_HITF}/v1/structure-field-lines/detail`;
      }
      return {
        data,
        params,
        url: getUrl(url, data.lineId || data.headerId),
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
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines`
          : `${HZERO_HITF}/v1/structure-field-lines`,
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
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines`
          : `${HZERO_HITF}/v1/structure-field-lines`,
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
          ? `${HZERO_HITF}/v1/${organizationId}/structure-field-lines`
          : `${HZERO_HITF}/v1/structure-field-lines`,
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
