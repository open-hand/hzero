import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, getCurrentUser } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import getLang from '@/langs/dataMappingLang';
import {
  CAST_DATA_TYPE,
  CAST_TYPE,
  MAPPING_FIELD_TYPE,
  // CAST_CONJUNCTION_TYPE,
  CAST_EXPR_FIELD_SOURCE_TYPE,
  LANGUAGE,
  CAST_HEADER_STATUS,
} from '@/constants/CodeConstants';
import { DATA_MAPPING_STATUS } from '@/constants/constants';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const headerFormDS = (props = {}) => {
  const { _required = true } = props;
  return {
    primaryKey: 'castHeaderId',
    autoQuery: false,
    autoCreate: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'castCode',
        label: getLang('CAST_CODE'),
        type: 'string',
        required: _required,
      },
      {
        name: 'castName',
        label: getLang('CAST_NAME'),
        type: 'string',
        required: _required,
      },
      {
        name: 'dataType',
        label: getLang('DATA_TYPE'),
        type: 'string',
        required: _required,
        lookupCode: CAST_DATA_TYPE,
      },
      {
        name: 'versionDesc',
        label: getLang('VERSION'),
        type: 'string',
      },
      {
        name: 'statusCode',
        label: getLang('STATUS'),
        type: 'string',
        defaultValue: DATA_MAPPING_STATUS.NEW,
        lookupCode: CAST_HEADER_STATUS,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { castHeaderId, _historyFlag, version } = data;
        return {
          url: !_historyFlag
            ? `${HZERO_HORC}/v1${level}/cast-headers/${castHeaderId}`
            : `${HZERO_HORC}/v1${level}/cast-headers/${castHeaderId}/former-version/${version}`,
          params: null,
          data: null,
          method: 'GET',
        };
      },
      create: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/cast-headers`,
        data: data[0],
        method: 'POST',
      }),
      update: ({ data }) => {
        const { castHeaderId, version, _historyFlag } = data[0];
        return _historyFlag
          ? {
              url: `${HZERO_HORC}/v1${level}/cast-headers/${castHeaderId}/former-version/override/${version}`,
              data: null,
              method: 'POST',
            }
          : {
              url: `${HZERO_HORC}/v1${level}/cast-headers`,
              data: data[0],
              method: 'PUT',
            };
      },
    },
  };
};

const castLineTableDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    primaryKey: 'castLineId',
    fields: [
      {
        name: 'castRoot',
        label: getLang('CAST_ROOT'),
        type: 'string',
      },
      {
        name: 'castField',
        label: getLang('CAST_FIELD'),
        type: 'string',
      },
      {
        name: 'castType',
        label: getLang('CAST_TYPE'),
        type: 'string',
        lookupCode: CAST_TYPE,
      },
      {
        name: 'castExpr',
        label: getLang('CAST_FORMULA'),
        type: 'string',
      },
      {
        name: 'castVal',
        label: getLang('CAST_VAL'),
        type: 'string',
      },
    ],
    transport: {
      read: ({ data }) => {
        const { castHeaderId } = data;
        return {
          url: `${HZERO_HORC}/v1${level}/cast-lines/${castHeaderId}`,
          params: { ...data },
          method: 'GET',
        };
      },
      update: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/cast-lines`,
        data: data[0],
        method: 'PUT',
      }),
      destroy: ({ data }) => {
        return {
          url: `${HZERO_HORC}/v1${level}/cast-lines`,
          method: 'DELETE',
          data: data[0],
        };
      },
    },
  };
};

const castLineFormDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    paging: false,
    autoCreate: false,
    primaryKey: 'castLineId',
    fields: [
      {
        name: 'castRoot',
        label: getLang('CAST_ROOT'),
        type: 'string',
      },
      {
        name: 'castField',
        label: getLang('CAST_FIELD'),
        type: 'string',
        required: true,
      },
      {
        name: 'castType',
        label: getLang('CAST_TYPE'),
        type: 'string',
        lookupCode: CAST_TYPE,
        required: true,
      },
      {
        name: 'castLovCode',
        label: getLang('CAST_LOV_CODE'),
        type: 'string',
        pattern: CODE_UPPER,
        defaultValidationMessages: {
          patternMismatch: getLang('PATTERN_MISMACTH'),
        },
      },
      {
        name: 'castLovField',
        label: getLang('CAST_LOV_FIELD'),
        type: 'string',
      },
      {
        name: 'langLov',
        label: getLang('CAST_LOV_LANG'),
        type: 'object',
        ignore: 'always',
        lovCode: LANGUAGE,
        valueField: 'code',
        textField: 'name',
      },
      {
        name: 'castLovLang',
        type: 'string',
        bind: 'langLov.code',
        defaultValue: getCurrentUser().language,
      },
      {
        name: 'castLovLangMeaning',
        type: 'string',
        bind: 'langLov.name',
        defaultValue: getCurrentUser().languageName,
      },
      {
        name: 'statusCode',
        label: getLang('STATUS'),
        type: 'string',
        defaultValue: DATA_MAPPING_STATUS.NEW,
        lookupCode: CAST_HEADER_STATUS,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { castLineId } = data;
        return {
          url: `${HZERO_HORC}/v1${level}/cast-lines/details/${castLineId}`,
          params: { ...data },
          method: 'GET',
        };
      },
      create: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/cast-lines`,
        data: data[0],
        method: 'POST',
      }),
      update: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/cast-lines`,
        data: data[0],
        method: 'PUT',
      }),
    },
  };
};

const mappingLineTableDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    fields: [
      {
        name: 'targetValue',
        label: getLang('TARGET_VALUE'),
        type: 'string',
      },
      {
        name: 'fieldType',
        label: getLang('FIELD_TYPE'),
        type: 'string',
        lookupCode: MAPPING_FIELD_TYPE,
      },
      {
        name: 'sourceMappingFields',
        label: getLang('CONDITION'),
        type: 'string',
      },
    ],
    transport: {
      read: ({ data }) => {
        const { castLineId } = data;
        return {
          url: `${HZERO_HORC}/v1${level}/data-mapping-targets/${castLineId}`,
          params: { ...data },
          method: 'GET',
        };
      },
      destroy: ({ data }) => {
        return {
          url: `${HZERO_HORC}/v1${level}/data-mapping-targets`,
          method: 'DELETE',
          data: data[0],
        };
      },
    },
  };
};

const mappingLineFormDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    fields: [
      {
        name: 'mappingField',
        type: 'string',
      },
      {
        name: 'targetValue',
        label: getLang('TARGET_VALUE'),
        type: 'string',
        required: true,
      },
      {
        name: 'fieldType',
        label: getLang('FIELD_TYPE'),
        type: 'string',
        required: true,
        lookupCode: MAPPING_FIELD_TYPE,
      },
      // {
      //   name: 'conjunction',
      //   label: getLang(CONJUNCTION,
      //   type: 'string',
      //   required: true,
      //   lookupCode: CAST_CONJUNCTION_TYPE,
      // },
      {
        name: 'statusCode',
        label: getLang('STATUS'),
        type: 'string',
        defaultValue: DATA_MAPPING_STATUS.NEW,
        lookupCode: CAST_HEADER_STATUS,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { mappingTargetId } = data;
        return {
          url: `${HZERO_HORC}/v1${level}/data-mapping-targets/details/${mappingTargetId}`,
          params: { ...data },
          method: 'GET',
        };
      },
      create: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/data-mapping-targets`,
        data: data[0],
        method: 'POST',
      }),
      update: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/data-mapping-targets`,
        data: data[0],
        method: 'PUT',
      }),
    },
  };
};

const fieldMappingTableDS = () => {
  // const { onAddFormItem, onRemoveFormItem, onLoad } = props;
  return {
    autoQuery: false,
    selection: false,
    fields: [
      // {
      //   name: 'mappingField',
      //   label: getLang(CONDITION_FIELD,
      //   type: 'string',
      //   required: true,
      // },
      // {
      //   name: 'comparisonType',
      //   label: getLang('CONDITION'),
      //   type: 'string',
      //   required: true,
      // },
      // {
      //   name: 'sourceValue',
      //   label: getLang(VALUE,
      //   type: 'string',
      //   required: true,
      // },
      {
        name: 'conditionJson',
        type: 'string',
        required: true,
      },
      {
        name: 'evaluateExpression',
        type: 'string',
        required: true,
      },
    ],
    transport: {
      // read: ({ data }) => {
      //   const { mappingTargetId } = data;
      //   return {
      //     url: `${HZERO_HORC}/v1${level}/data-mapping-sources/${mappingTargetId}`,
      //     params: { ...data },
      //     method: 'GET',
      //   };
      // },
      // create: ({ data }) => ({
      //   url: `${HZERO_HORC}/v1${level}/data-mapping-sources/batch-create`,
      //   data,
      //   method: 'POST',
      // }),
      // update: ({ data }) => ({
      //   url: `${HZERO_HORC}/v1${level}/data-mapping-sources/batch-update`,
      //   data,
      //   method: 'PUT',
      // }),
      update: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/data-mapping-targets`,
        data: data[0],
        method: 'PUT',
      }),
      // destroy: ({ data }) => {
      //   return {
      //     url: `${HZERO_HORC}/v1${level}/data-mapping-sources/batch-remove`,
      //     method: 'DELETE',
      //     data,
      //   };
      // },
    },
    // events: {
    //   create: ({ record }) => onAddFormItem(record),
    //   remove: ({ dataSet }) => onRemoveFormItem(dataSet),
    //   load: ({ dataSet }) => onLoad(dataSet),
    // },
  };
};

const formulaLineTableDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    fields: [
      {
        name: 'orderSeq',
        label: getLang('SEQ_NUMBER'),
        type: 'number',
      },
      {
        name: 'exprFieldSourceType',
        label: getLang('EXPR_SOURCE_TYPE'),
        type: 'string',
        lookupCode: CAST_EXPR_FIELD_SOURCE_TYPE,
      },
      {
        name: 'exprFieldSourceValue',
        label: getLang('EXPR_SOURCE_VALUE'),
        type: 'string',
      },
      {
        name: 'statusCode',
        label: getLang('STATUS'),
        type: 'string',
        defaultValue: DATA_MAPPING_STATUS.NEW,
        lookupCode: CAST_HEADER_STATUS,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { castLineId } = data;
        return {
          url: `${HZERO_HORC}/v1${level}/expr-rules/${castLineId}`,
          params: { ...data },
          method: 'GET',
        };
      },
      create: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/expr-rules/batch-create`,
        data,
        method: 'POST',
      }),
      update: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/expr-rules/batch-update`,
        data,
        method: 'PUT',
      }),
      destroy: ({ data }) => {
        return {
          url: `${HZERO_HORC}/v1${level}/expr-rules/batch-remove`,
          method: 'DELETE',
          data,
        };
      },
    },
  };
};

const onlyReadFormDS = () => {
  return {
    autoQuery: false,
    autoCreate: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'castField',
        label: getLang('CAST_FIELD'),
        type: 'string',
      },
      {
        name: 'castType',
        label: getLang('CAST_TYPE'),
        type: 'string',
        lookupCode: CAST_TYPE,
      },
      {
        name: 'castExpr',
        label: getLang('FORMULA'),
        type: 'string',
      },
    ],
  };
};

const sqlFormDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    paging: false,
    autoCreate: false,
    fields: [
      {
        name: 'castSql',
        label: getLang('SQL'),
        type: 'string',
        required: true,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { castLineId } = data;
        return {
          url: `${HZERO_HORC}/v1${level}/cast-lines/details/${castLineId}`,
          params: { ...data },
          method: 'GET',
        };
      },
      update: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/cast-lines`,
        data: data[0],
        method: 'PUT',
      }),
    },
  };
};

export {
  headerFormDS,
  castLineTableDS,
  castLineFormDS,
  mappingLineTableDS,
  mappingLineFormDS,
  fieldMappingTableDS,
  formulaLineTableDS,
  onlyReadFormDS,
  sqlFormDS,
};