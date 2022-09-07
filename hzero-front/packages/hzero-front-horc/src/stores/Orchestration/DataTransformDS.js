import { CODE_UPPER } from 'utils/regExp';
import { getCurrentLanguage } from 'utils/utils';
import DATA_TRANSFORM_LANG from '@/langs/orchDataTransformLang';
import {
  CAST_DATA_TYPE,
  CAST_TYPE,
  MAPPING_FIELD_TYPE,
  LANGUAGE,
  CAST_EXPR_FIELD_SOURCE_TYPE,
  CAST_HEADER_STATUS,
} from '@/constants/CodeConstants';
import { DATA_MAPPING_STATUS } from '@/constants/constants';

const headerFormDS = () => {
  return {
    autoQuery: false,
    autoCreate: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'castDataType',
        label: DATA_TRANSFORM_LANG.DATA_TYPE,
        type: 'string',
        required: true,
        lookupCode: CAST_DATA_TYPE,
      },
    ],
  };
};

const castLineTableDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    fields: [
      {
        name: 'castRoot',
        label: DATA_TRANSFORM_LANG.CAST_ROOT,
        type: 'string',
      },
      {
        name: 'castField',
        label: DATA_TRANSFORM_LANG.CAST_FIELD,
        type: 'string',
      },
      {
        name: 'castType',
        label: DATA_TRANSFORM_LANG.CAST_TYPE,
        type: 'string',
        lookupCode: CAST_TYPE,
      },
      {
        name: 'castExpr',
        label: DATA_TRANSFORM_LANG.CAST_FORMULA,
        type: 'string',
      },
      {
        name: 'castVal',
        label: DATA_TRANSFORM_LANG.CAST_VAL,
        type: 'string',
      },
    ],
  };
};

const castLineFormDS = (props) => {
  const { onFieldUpdate } = props;
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    paging: false,
    autoCreate: false,
    fields: [
      {
        name: 'castRoot',
        label: DATA_TRANSFORM_LANG.CAST_ROOT,
        type: 'string',
      },
      {
        name: 'castField',
        label: DATA_TRANSFORM_LANG.CAST_FIELD,
        type: 'string',
        required: true,
      },
      {
        name: 'castType',
        label: DATA_TRANSFORM_LANG.CAST_TYPE,
        type: 'string',
        lookupCode: CAST_TYPE,
        required: true,
      },
      {
        name: 'castLovCode',
        label: DATA_TRANSFORM_LANG.CAST_LOV_CODE,
        type: 'string',
        pattern: CODE_UPPER,
        defaultValidationMessages: {
          patternMismatch: DATA_TRANSFORM_LANG.PATTERN_MISMACTH,
        },
      },
      {
        name: 'castLovField',
        label: DATA_TRANSFORM_LANG.CAST_LOV_FIELD,
        type: 'string',
      },
      {
        name: 'langLov',
        label: DATA_TRANSFORM_LANG.CAST_LOV_LANG,
        type: 'object',
        ignore: 'always',
        lovCode: LANGUAGE,
        valueField: 'code',
        textField: 'name',
        defaultValue: getCurrentLanguage(),
      },
      {
        name: 'castLovLang',
        type: 'string',
        bind: 'langLov.code',
      },
    ],
    events: {
      update: onFieldUpdate,
    },
  };
};

const mappingLineTableDS = () => {
  return {
    autoQuery: false,
    pageSize: 10,
    selection: false,
    primaryKey: 'mappingLineId',
    fields: [
      {
        name: 'targetValue',
        label: DATA_TRANSFORM_LANG.TARGET_VALUE,
        type: 'string',
        required: true,
      },
      {
        name: 'fieldType',
        label: DATA_TRANSFORM_LANG.FIELD_TYPE,
        type: 'string',
        required: true,
        lookupCode: MAPPING_FIELD_TYPE,
      },
      {
        name: 'sourceMappingFields',
        label: DATA_TRANSFORM_LANG.CONDITION,
        type: 'string',
      },
    ],
  };
};

const fieldMappingTableDS = () => {
  return {
    autoQuery: false,
    selection: false,
    autoCreate: false,
    fields: [
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
        label: DATA_TRANSFORM_LANG.SEQ_NUMBER,
        type: 'number',
      },
      {
        name: 'exprFieldSourceType',
        label: DATA_TRANSFORM_LANG.EXPR_SOURCE_TYPE,
        type: 'string',
        lookupCode: CAST_EXPR_FIELD_SOURCE_TYPE,
      },
      {
        name: 'exprFieldSourceValue',
        label: DATA_TRANSFORM_LANG.EXPR_SOURCE_VALUE,
        type: 'string',
      },
      {
        name: 'statusCode',
        label: DATA_TRANSFORM_LANG.STATUS,
        type: 'string',
        defaultValue: DATA_MAPPING_STATUS.NEW,
        lookupCode: CAST_HEADER_STATUS,
      },
    ],
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
        label: DATA_TRANSFORM_LANG.CAST_FIELD,
        type: 'string',
      },
      {
        name: 'castType',
        label: DATA_TRANSFORM_LANG.CAST_TYPE,
        type: 'string',
        lookupCode: CAST_TYPE,
      },
      {
        name: 'highlightedCastExpr',
        label: DATA_TRANSFORM_LANG.FORMULA,
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
        label: DATA_TRANSFORM_LANG.SQL,
        type: 'string',
        required: true,
      },
    ],
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
        label: DATA_TRANSFORM_LANG.TARGET_VALUE,
        type: 'string',
        required: true,
      },
      {
        name: 'fieldType',
        label: DATA_TRANSFORM_LANG.FIELD_TYPE,
        type: 'string',
        required: true,
        lookupCode: MAPPING_FIELD_TYPE,
      },
      {
        name: 'statusCode',
        label: DATA_TRANSFORM_LANG.STATUS,
        type: 'string',
        defaultValue: DATA_MAPPING_STATUS.NEW,
        lookupCode: CAST_HEADER_STATUS,
      },
    ],
  };
};

export {
  headerFormDS,
  castLineTableDS,
  castLineFormDS,
  mappingLineTableDS,
  fieldMappingTableDS,
  formulaLineTableDS,
  onlyReadFormDS,
  sqlFormDS,
  mappingLineFormDS,
};
