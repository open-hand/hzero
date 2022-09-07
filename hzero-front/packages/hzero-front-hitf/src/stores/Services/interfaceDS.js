import { HZERO_HITF } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { isNull } from 'lodash';
import { CODE } from 'utils/regExp';
import {
  REQUEST_METHOD,
  REQUEST_HEADER,
  SERVICE_TYPE,
  INTERFACE_STATUS,
  SOAP_VERSION,
  DATABASE_TYPE,
  DATA_SOURCE,
  EXPRESSION_TYPE,
  SVC_COL_TYPE,
  SVC_PRIVACY_LEVEL,
  SVC_MODEL_OPERATOR,
  SVC_PARAM_TYPE,
  // TRANSFORM_LIST_SITE,
  // TRANSFORM_LIST,
  // CAST_LIST_SITE,
  // CAST_LIST,
} from '@/constants/CodeConstants';
import { SERVICE_CONSTANT } from '@/constants/constants';
import getLang from '@/langs/serviceLang';
import QuestionPopover from '@/components/QuestionPopover';
import React from 'react';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const historyDS = () => {
  return {
    autoQuery: false,
    paging: false,
    selection: false,
    primaryKey: 'interfaceHisId',
    fields: [
      {
        name: 'interfaceHisId',
        type: 'string',
      },
      {
        name: 'version',
        type: 'number',
        label: getLang('HISTORY_VERSION'),
      },
    ],
    transport: {
      read: (config) => {
        const { data, params } = config;
        const { interfaceId } = data;
        return {
          url: `${HZERO_HITF}/v1${level}/interface-hiss/${interfaceId}/history`,
          params: {
            ...data,
            ...params,
          },
          method: 'GET',
        };
      },
    },
  };
};

function basicFormDS(props) {
  const { currentInterfaceType } = props;
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    autoCreate: true,
    fields: [
      {
        name: 'interfaceCode',
        label: (
          <QuestionPopover
            text={getLang('INTERFACE_CODE')}
            message={getLang('INTERFACE_CODE_TIP')}
          />
        ),
        type: 'string',
        required: true,
        pattern: CODE,
        defaultValidationMessages: {
          patternMismatch: getLang('CODE'),
        },
      },
      {
        name: 'interfaceName',
        label: (
          <QuestionPopover
            text={getLang('INTERFACE_NAME')}
            message={getLang('INTERFACE_NAME_TIP')}
          />
        ),
        type: 'string',
        required: true,
      },
      {
        name: 'interfaceUrl',
        // label: getLang('INTERFACE_URL'),
        label: (
          <QuestionPopover text={getLang('INTERFACE_URL')} message={getLang('INTERFACE_URL_TIP')} />
        ),
        type: 'string',
        dynamicProps: () => ({
          required:
            currentInterfaceType !== SERVICE_CONSTANT.DS &&
            currentInterfaceType !== SERVICE_CONSTANT.COMPOSITE &&
            currentInterfaceType !== SERVICE_CONSTANT.FILE,
        }),
      },
      {
        name: 'soapVersion',
        label: getLang('SOAP_VERSION'),
        type: 'string',
        lookupCode: SOAP_VERSION,
      },
      {
        name: 'requestMethod',
        // label: getLang('REQUEST_METHOD'),
        label: (
          <QuestionPopover
            text={getLang('REQUEST_METHOD')}
            message={getLang('REQUEST_METHOD_TIP')}
          />
        ),
        type: 'string',
        lookupCode: REQUEST_METHOD,
      },
      {
        name: 'requestHeader',
        // label: getLang('REQUEST_HEADER'),
        label: (
          <QuestionPopover
            text={getLang('REQUEST_HEADER')}
            message={getLang('REQUEST_HEADER_TIP')}
          />
        ),
        type: 'string',
        lookupCode: REQUEST_HEADER,
      },
      {
        name: 'publishType',
        // label: getLang('PUBLISH_TYPE'),
        label: (
          <QuestionPopover text={getLang('PUBLISH_TYPE')} message={getLang('PUBLISH_TYPE_TIP')} />
        ),
        type: 'string',
        required: true,
        lookupCode: SERVICE_TYPE,
      },
      {
        name: 'soapAction',
        // label: getLang('SOAP_ACTION'),
        label: (
          <QuestionPopover text={getLang('SOAP_ACTION')} message={getLang('SOAP_ACTION_TIP')} />
        ),
        type: 'string',
      },
      {
        name: 'bodyNamespaceFlag',
        // label: getLang('BODY_NAMESPACE_FLAG'),
        label: (
          <QuestionPopover
            text={getLang('BODY_NAMESPACE_FLAG')}
            message={getLang('BODY_NAMESPACE_FLAG_TIP')}
          />
        ),
        defaultValue: 0,
        trueValue: 1,
        falseValue: 0,
        type: 'boolean',
      },
      {
        name: 'asyncFlag',
        // label: getLang('ASYNC_FLAG'),
        label: <QuestionPopover text={getLang('ASYNC_FLAG')} message={getLang('ASYNC_FLAG_TIP')} />,
        defaultValue: 0,
        trueValue: 1,
        falseValue: 0,
        type: 'boolean',
      },
      {
        name: 'mappingClass',
        // label: getLang('MAPPING_CLASS'),
        label: (
          <QuestionPopover text={getLang('MAPPING_CLASS')} message={getLang('MAPPING_CLASS_TIP')} />
        ),
        type: 'string',
        ignore: 'always',
      },
      {
        name: 'status',
        label: getLang('STATUS'),
        type: 'string',
        required: true,
        lookupCode: INTERFACE_STATUS,
      },
      {
        name: 'httpConfig',
        // label: getLang('HTTP_CONFIG'),
        label: (
          <QuestionPopover text={getLang('HTTP_CONFIG')} message={getLang('HTTP_CONFIG_TIP')} />
        ),
        type: 'string',
        ignore: 'always',
      },
      // {
      //   name: 'requestTransformLov',
      //   label: getLang(REQUEST_TRANSFORM,
      //   type: 'object',
      //   lovCode: isTenantRoleLevel() ? TRANSFORM_LIST : TRANSFORM_LIST_SITE,
      //   ignore: 'always',
      //   noCache: true,
      //   valueField: 'transformId',
      //   textField: 'transformName',
      //   lovPara: { tenantId: organizationId },
      // },
      {
        name: 'requestTransformId',
        type: 'string',
        // label: getLang('REQUEST_TRANSFORM'),
        label: (
          <QuestionPopover
            text={getLang('REQUEST_TRANSFORM')}
            message={getLang('REQUEST_TRANSFORM_TIP')}
          />
        ),
      },
      // {
      //   name: 'requestTransformName',
      //   type: 'string',
      //   bind: 'requestTransformLov.transformName',
      // },
      // {
      //   name: 'responseTransformLov',
      //   label: getLang(RESPONSE_TRANSFORM,
      //   type: 'object',
      //   lovCode: isTenantRoleLevel() ? TRANSFORM_LIST : TRANSFORM_LIST_SITE,
      //   ignore: 'always',
      //   noCache: true,
      //   valueField: 'transformId',
      //   textField: 'transformName',
      //   lovPara: { tenantId: organizationId },
      // },
      {
        name: 'responseTransformId',
        type: 'string',
        // label: getLang('RESPONSE_TRANSFORM'),
        label: (
          <QuestionPopover
            text={getLang('RESPONSE_TRANSFORM')}
            message={getLang('RESPONSE_TRANSFORM_TIP')}
          />
        ),
      },
      // {
      //   name: 'responseTransformName',
      //   type: 'string',
      //   bind: 'responseTransformLov.transformName',
      // },
      // {
      //   name: 'requestCastLov',
      //   label: getLang(REQUEST_CAST,
      //   type: 'object',
      //   lovCode: isTenantRoleLevel() ? CAST_LIST : CAST_LIST_SITE,
      //   ignore: 'always',
      //   noCache: true,
      //   valueField: 'castHeaderId',
      //   textField: 'castName',
      //   lovPara: { tenantId: organizationId },
      // },
      {
        name: 'requestCastId',
        type: 'string',
        // label: getLang('REQUEST_CAST'),
        label: (
          <QuestionPopover text={getLang('REQUEST_CAST')} message={getLang('REQUEST_CAST_TIP')} />
        ),
        // bind: 'requestCastLov.castHeaderId',
      },
      // {
      //   name: 'requestCastName',
      //   type: 'string',
      //   bind: 'requestCastLov.castName',
      // },
      // {
      //   name: 'responseCastLov',
      //   label: getLang(RESPONSE_CAST,
      //   type: 'object',
      //   lovCode: isTenantRoleLevel() ? CAST_LIST : CAST_LIST_SITE,
      //   ignore: 'always',
      //   noCache: true,
      //   valueField: 'castHeaderId',
      //   textField: 'castName',
      //   lovPara: { tenantId: organizationId },
      // },
      {
        name: 'responseCastId',
        type: 'string',
        // label: getLang('RESPONSE_CAST'),
        label: (
          <QuestionPopover text={getLang('RESPONSE_CAST')} message={getLang('RESPONSE_CAST_TIP')} />
        ),
        // bind: 'responseCastLov.castHeaderId',
      },
      // {
      //   name: 'responseCastName',
      //   type: 'string',
      //   bind: 'responseCastLov.castName',
      // },
      {
        name: 'publishUrl',
        label: getLang('PUBLISH_URL'),
        type: 'string',
      },
      {
        name: 'formatVersion',
        type: 'string',
        label: getLang('CURRENT_VERSION'),
      },
      {
        name: 'historyVersion',
        type: 'number',
        label: getLang('HISTORY_VERSION'),
      },
    ],
    transport: {
      read: (config) => {
        const { dataSet, data } = config;
        const { interfaceId, version, history } = data;
        if (history) {
          dataSet.setQueryParameter('history', false);
          return {
            url: `${HZERO_HITF}/v1${level}/interfaces/${interfaceId}/view/${version}`,
            method: 'GET',
            params: {
              ...data,
            },
          };
        }
        return {
          url: `${HZERO_HITF}/v1${level}/interfaces/${interfaceId}`,
          method: 'GET',
        };
      },
      create: ({ data }) => {
        const { interfaceServerId } = data[0];
        return {
          url: `${HZERO_HITF}/v1${level}/interfaces/${interfaceServerId}`,
          method: 'POST',
          data: data[0],
        };
      },
      update: ({ data }) => {
        const { interfaceServerId } = data[0];
        return {
          url: `${HZERO_HITF}/v1${level}/interfaces/${interfaceServerId}`,
          method: 'POST',
          data: data[0],
        };
      },
    },
  };
}

const mainConfigFormDS = () => {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    autoCreate: true,
    fields: [
      {
        name: 'dataSourceLov',
        label: getLang('DATASOURCE_LOV'),
        type: 'object',
        required: true,
        lovCode: DATA_SOURCE,
        ignore: 'always',
        noCache: true,
        valueField: 'datasourceId',
        textField: 'datasourceCode',
        lovPara: { tenantId: organizationId },
      },
      {
        name: 'dsType',
        label: getLang('DS_TYPE'),
        type: 'string',
        required: true,
        lookupCode: DATABASE_TYPE,
        bind: 'dataSourceLov.dbType',
      },
      {
        name: 'datasourceId',
        type: 'string',
        bind: 'dataSourceLov.datasourceId',
      },
      {
        name: 'datasourceCode',
        type: 'string',
        bind: 'dataSourceLov.datasourceCode',
      },
      {
        name: 'dsPurposeCode',
        type: 'string',
        bind: 'dataSourceLov.dsPurposeCode',
      },
      {
        name: 'exprType',
        label: getLang('EXPR_TYPE'),
        type: 'string',
        required: true,
        lookupCode: EXPRESSION_TYPE,
      },
      {
        name: 'remark',
        label: getLang('REMARK'),
        type: 'string',
      },
      {
        name: 'exprContent',
        type: 'string',
        required: true,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { interfaceId } = data;
        return {
          url: `${HZERO_HITF}/v1${level}/model-configs/${interfaceId}`,
          method: 'GET',
        };
      },
    },
  };
};

function attrListDS(props) {
  const { onFieldSelect, unFieldSelect, onFieldSelectAll, unFieldSelectAll, columnDs } = props;
  return {
    autoQuery: false,
    primaryKey: 'fieldId',
    paging: false,
    fields: [
      {
        name: 'key',
        type: 'string',
        ignore: 'always',
      },
      {
        name: 'fieldObj',
        type: 'object',
        label: getLang('FIELD_NAME'),
        dynamicProps: () => ({ options: columnDs }),
        valueField: 'fieldName',
        textField: 'fieldName',
        ignore: 'always',
        required: true,
        unique: true,
      },
      {
        name: 'fieldName',
        type: 'string',
        label: getLang('FIELD_NAME'),
        bind: 'fieldObj.fieldName',
      },
      {
        name: 'fieldType',
        bind: 'fieldObj.fieldType',
        type: 'string',
        defaultValue: 'VARCHAR',
        label: getLang('FIELD_TYPE'),
        lookupCode: SVC_COL_TYPE,
        required: true,
      },
      {
        name: 'fieldExpr',
        type: 'string',
        label: getLang('FIELD_EXPR'),
        bind: 'fieldObj.fieldExpr',
        required: true,
      },
      {
        name: 'seqNum',
        type: 'number',
        label: getLang('SEQ_NUM'),
        bind: 'fieldObj.seqNum',
        required: true,
        min: 0,
        step: 1,
      },
      {
        name: 'fieldDesc',
        bind: 'fieldObj.fieldDesc',
        type: 'string',
        label: getLang('FIELD_DESC'),
      },
      {
        name: 'privacyLevel',
        type: 'string',
        lookupCode: SVC_PRIVACY_LEVEL,
        label: getLang('FIELD_LEVEL'),
        required: true,
        defaultValue: 0,
      },
    ],
    events: {
      select: ({ record }) => onFieldSelect({ record }),
      unSelect: ({ record }) => unFieldSelect(record),
      selectAll: onFieldSelectAll,
      unSelectAll: unFieldSelectAll,
    },
    transport: {
      read: ({ data, params }) => ({
        url: `${HZERO_HITF}/v1${level}/model-fields`,
        method: 'GET',
        params: { ...data, ...params, page: -1 },
      }),
      create: ({ data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-fields/batch-save`,
        method: 'POST',
        data,
      }),
      update: ({ data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-fields/batch-save`,
        method: 'POST',
        data,
      }),
      destroy: ({ data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-fields/batch-delete`,
        method: 'DELETE',
        data,
      }),
    },
  };
}

function paramListDS(props) {
  const {
    modelFieldDs,
    modelDs,
    onParamSelect,
    unParamSelect,
    onParamSelectAll,
    unParamSelectAll,
  } = props;
  return {
    autoQuery: false,
    primaryKey: 'paramId',
    fields: [
      { name: 'key', type: 'string', ignore: 'always' },
      {
        name: 'paramName',
        type: 'string',
        label: getLang('PARAM_NAME'),
        unique: true,
        required: true,
      },
      {
        name: 'paramType',
        type: 'string',
        label: getLang('PARAM_TYPE'),
        lookupCode: SVC_PARAM_TYPE,
        required: true,
      },
      {
        name: 'fieldObj',
        type: 'object',
        label: getLang('BIND_FIELD_NAME'),
        dynamicProps: () => fieldObjProps(modelDs, modelFieldDs),
        ignore: 'always',
        valueField: 'fieldId',
        textField: 'fieldName',
      },
      { name: 'fieldId', type: 'string', bind: 'fieldObj.fieldId' },
      {
        name: 'fieldName',
        type: 'string',
        bind: 'fieldObj.fieldName',
        label: getLang('BIND_FIELD_NAME'),
      },
      {
        name: 'seqNum',
        type: 'number',
        label: getLang('SEQ_NUM'),
        bind: 'fieldObj.seqNum',
        unique: true,
        min: 0,
        step: 1,
      },
      {
        name: 'requiredFlag',
        type: 'boolean',
        label: getLang('REQUIRED_FLAG'),
        required: false,
        trueValue: true,
        falseValue: false,
        // defaultValue: 0,
      },
      {
        name: 'operatorCode',
        type: 'string',
        label: getLang('OPERATOR_CODE'),
        lookupCode: SVC_MODEL_OPERATOR,
        defaultValue: 'E',
        dynamicProps: () => operatorCodeProps(modelDs),
      },
      { name: 'defaultValue', type: 'string', label: getLang('DEFAULT_VALUE') },
      {
        name: 'paramDesc',
        type: 'string',
        bind: 'fieldObj.fieldDesc',
        label: getLang('PARAM_DESC'),
      },
    ],
    events: {
      update: ({ record, name, value }) => {
        if (name === 'fieldObj') {
          record.set('paramName', isNull(value) ? '' : value.fieldName);
        }
      },
      select: ({ record }) => onParamSelect({ record }),
      unSelect: ({ record }) => unParamSelect(record),
      selectAll: onParamSelectAll,
      unSelectAll: unParamSelectAll,
    },
    transport: {
      read: ({ params, data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-request-params`,
        method: 'GET',
        params: { ...params, ...data },
      }),
      create: ({ data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-request-params/batch-create`,
        method: 'POST',
        data,
      }),
      update: ({ data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-request-params/batch-update`,
        method: 'PUT',
        data,
      }),
      destroy: ({ data }) => ({
        url: `${HZERO_HITF}/v1${level}/model-request-params/batch-delete`,
        method: 'DELETE',
        data,
      }),
    },
  };
}

const assertionDS = (props) => {
  const {
    onAddAssertionFormItem,
    onRemoveAssertionFormItem,
    onFiledUpdate,
    onLoadAssertionForm,
  } = props;
  return {
    autoQuery: false,
    paging: false,
    selection: false,
    fields: [
      {
        name: 'subject',
        label: getLang('KEY'),
        type: 'string',
        required: true,
      },
      {
        name: 'field',
        label: getLang('VALUE'),
        type: 'string',
      },
      {
        name: 'condition',
        label: getLang('TYPE'),
        type: 'string',
        required: true,
      },
      {
        name: 'expectation',
        label: getLang('EXPECTATION'),
        type: 'string',
        required: true,
      },
    ],
    events: {
      create: ({ record }) => onAddAssertionFormItem(record),
      remove: ({ dataSet }) => onRemoveAssertionFormItem(dataSet),
      update: ({ dataSet, record, name, value }) => onFiledUpdate(dataSet, record, name, value),
      load: ({ dataSet }) => onLoadAssertionForm(dataSet),
    },
  };
};

const retryFormDs = () => {
  return {
    fields: [
      {
        name: 'retryTimes',
        label: getLang('RETRY_TIMES'),
        type: 'number',
        defaultValue: 0,
      },
      {
        name: 'retryInterval',
        label: getLang('RETRY_INTERVAL'),
        type: 'number',
        defaultValue: 1,
      },
    ],
  };
};

function operatorCodeProps(modelDs) {
  return {
    required: modelDs.current && modelDs.current.get('exprType') === 'DBO',
    ignore: modelDs.current && modelDs.current.get('exprType') === 'DBO' ? 'never' : 'always',
  };
}
function fieldObjProps(modelDs, modelFieldDs) {
  return {
    options: modelFieldDs,
    required: modelDs.current && modelDs.current.get('dsType') === 'ASSET',
  };
}

export {
  basicFormDS,
  mainConfigFormDS,
  attrListDS,
  paramListDS,
  assertionDS,
  retryFormDs,
  historyDS,
};
