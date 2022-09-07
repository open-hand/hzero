import intl from 'utils/intl';
import { HZERO_HITF } from 'utils/config';
// import { Icon } from 'hzero-ui';
// import { Tooltip } from 'choerodon-ui/pro';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE, CODE_UPPER } from 'utils/regExp';
import {
  TENANT,
  ROUTE_INFORMATION,
  CERTIFICATE,
  SOAP_WSS_PASSWORD_TYPE,
  SERVICE_TYPE,
  SERVICE_CATEGORY,
  PROTOCOL,
  SERVICE_STATUS,
} from '@/constants/CodeConstants';
import { SERVICE_CONSTANT } from '@/constants/constants';
import getLang from '@/langs/serviceLang';
import React from 'react';
import QuestionPopover from '../../components/QuestionPopover';

const prefix = 'hitf.services.model.services';
const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const historyDS = () => {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    primaryKey: 'interfaceServerHisId',
    fields: [
      {
        name: 'version',
        type: 'string',
      },
    ],
    transport: {
      read: ({ data, params }) => {
        const { interfaceServerId } = data;
        return {
          url: `${HZERO_HITF}/v1${level}/interface-server-hiss/${interfaceServerId}/history`,
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

const basicFormDS = () => {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    autoCreate: true,
    fields: [
      {
        name: 'tenantLov',
        label: intl.get(`${prefix}.tenant`).d('所属租户'),
        type: 'object',
        lovCode: TENANT,
        ignore: 'always',
        noCache: true,
        valueField: 'tenantId',
        textField: 'tenantName',
        required: !isTenantRoleLevel(),
      },
      {
        name: 'tenantId',
        type: 'string',
        bind: 'tenantLov.tenantId',
      },
      {
        name: 'tenantName',
        type: 'string',
        bind: 'tenantLov.tenantName',
      },
      {
        name: 'serverCode',
        // label: intl.get(`${prefix}.serverCode`).d('服务代码'),
        label: (
          <QuestionPopover text={getLang('SERVICE_CODE')} message={getLang('SERVICE_CODE_TIP')} />
        ),
        type: 'string',
        required: true,
        maxLength: 128,
        pattern: CODE_UPPER,
        defaultValidationMessages: {
          patternMismatch: getLang('CODE_UPPER'),
        },
      },
      {
        name: 'serverName',
        label: intl.get(`${prefix}.serverName`).d('服务名称'),
        type: 'string',
        required: true,
        maxLength: 250,
      },
      {
        name: 'namespace',
        // label: intl.get(`${prefix}.namespace`).d('服务命名空间'),
        label: <QuestionPopover text={getLang('NAMESPACE')} message={getLang('NAMESPACE_TIP')} />,
        type: 'string',
      },
      {
        name: 'serviceType',
        // label: intl.get(`${prefix}.serviceType`).d('服务类别'),
        label: (
          <QuestionPopover text={getLang('SERVICE_TYPE')} message={getLang('SERVICE_TYPE_TIP')} />
        ),
        type: 'string',
        required: true,
        lookupCode: SERVICE_TYPE,
      },
      {
        name: 'serviceCategory',
        // label: intl.get(`${prefix}.serviceCategory`).d('服务类型'),
        label: (
          <QuestionPopover
            text={getLang('SERVICE_CATEGORY')}
            message={getLang('SERVICE_CATEGORY_TIP')}
          />
        ),
        type: 'string',
        required: true,
        lookupCode: SERVICE_CATEGORY,
      },
      {
        name: 'protocolGroup',
        label: intl.get(`${prefix}.domainUrl`).d('服务地址'),
        type: 'string',
        ignore: 'always',
      },
      {
        name: 'publicFlag',
        // label: intl.get(`${prefix}.publicFlag`).d('公开接口'),
        label: (
          <QuestionPopover text={getLang('PUBLIC_FLAG')} message={getLang('PUBLIC_FLAG_TIP')} />
        ),
        type: 'boolean',
        defaultValue: false,
        trueValue: true,
        falseValue: false,
      },
      {
        name: 'domainUrl',
        type: 'string',
        dynamicProps: {
          required: ({ record }) =>
            [SERVICE_CONSTANT.INTERNAL, SERVICE_CONSTANT.EXTERNAL].includes(
              record.get('serviceCategory')
            ),
          bind: ({ record }) =>
            record.get('serviceCategory') === SERVICE_CONSTANT.INTERNAL
              ? 'addressLov.serviceCode'
              : null,
        },
      },
      {
        name: 'protocol',
        type: 'string',
        lookupCode: PROTOCOL,
        dynamicProps: {
          required: ({ record }) => record.get('serviceCategory') === SERVICE_CONSTANT.EXTERNAL,
        },
      },
      {
        name: 'addressLov',
        // label: intl.get(`${prefix}.address`).d('服务地址'),
        label: <QuestionPopover text={getLang('ADDRESS')} message={getLang('ADDRESS_TIP')} />,
        type: 'object',
        lovCode: ROUTE_INFORMATION,
        ignore: 'always',
        dynamicProps: {
          required: ({ record }) => record.get('serviceCategory') === SERVICE_CONSTANT.INTERNAL,
        },
      },
      {
        name: 'enabledCertificateFlag',
        label: intl.get(`${prefix}.enabledCertificateFlag`).d('是否启用证书'),
        type: 'boolean',
        trueValue: 1,
        falseValue: 0,
      },
      {
        name: 'certificateLov',
        label: intl.get(`${prefix}.certificate`).d('证书'),
        type: 'object',
        required: true,
        ignore: 'always',
        noCache: true,
        lovCode: CERTIFICATE,
        textField: 'domainName',
        valueField: 'certificateId',
        dynamicProps: {
          lovPara: ({ record }) => ({
            serverUrl: `${record.get('protocol')}${record.get('domainUrl')}`,
          }),
          required: ({ record }) => record.get('enabledCertificateFlag') === 1,
        },
      },
      {
        name: 'certificateId',
        type: 'string',
        bind: 'certificateLov.certificateId',
      },
      {
        name: 'swaggerUrl',
        label: intl.get(`${prefix}.swaggerUrl`).d('swagger地址'),
        type: 'string',
      },
      {
        name: 'soapNamespace',
        // label: intl.get(`${prefix}.soapNamespace`).d('命名空间'),
        label: (
          <QuestionPopover
            text={getLang('SOAP_NAMESPACE')}
            message={getLang('SOAP_NAMESPACE_TIP')}
          />
        ),
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('serviceType') === SERVICE_CONSTANT.SOAP,
        },
      },
      {
        name: 'enabledFlag',
        label: intl.get(`${prefix}.enabledFlag`).d('是否启用'),
        type: 'boolean',
        trueValue: 1,
        falseValue: 0,
      },
      {
        name: 'soapElementPrefix',
        // label: intl.get(`${prefix}.soapElementPrefix`).d('参数前缀'),
        label: (
          <QuestionPopover
            text={getLang('SOAP_ELEMENT_PREFIX')}
            message={getLang('SOAP_ELEMENT_PREFIX_TIP')}
          />
        ),
        type: 'string',
        maxLength: 30,
      },
      {
        name: 'soapWssPasswordType',
        // label: intl.get(`${prefix}.soapWssPasswordType`).d('加密类型'),
        label: (
          <QuestionPopover
            text={getLang('SOAP_WSS_PASSWORD_TYPE')}
            message={getLang('SOAP_WSS_PASSWORD_TYPE_TIP')}
          />
        ),
        type: 'string',
        lookupCode: SOAP_WSS_PASSWORD_TYPE,
      },
      {
        name: 'soapUsername',
        label: intl.get(`${prefix}.soapUsername`).d('校验用户名'),
        type: 'string',
      },
      {
        name: 'soapPassword',
        label: intl.get(`${prefix}.soapPassword`).d('校验密码'),
        type: 'string',
      },
      {
        name: 'requestContentType',
        // label: intl.get(`${prefix}.requestContentType`).d('请求ContentType'),
        label: (
          <QuestionPopover
            text={getLang('REQUEST_CONTENT_TYPE')}
            message={getLang('REQUEST_CONTENT_TYPE_TIP')}
          />
        ),
        type: 'string',
      },
      {
        name: 'responseContentType',
        // label: intl.get(`${prefix}.responseContentType`).d('响应ContentType'),
        label: (
          <QuestionPopover
            text={getLang('RESPONSE_CONTENT_TYPE')}
            message={getLang('RESPONSE_CONTENT_TYPE_TIP')}
          />
        ),
        type: 'string',
      },
      {
        name: 'soapDataNode',
        // label: intl.get(`${prefix}.soapDataNode`).d('SoapBody报文标签'),
        label: (
          <QuestionPopover
            text={getLang('SOAP_DATA_NODE')}
            message={getLang('SOAP_DATA_NODE_TIP')}
          />
        ),
        type: 'string',
        defaultValue: 'soap:Body',
      },
      {
        name: 'invokeVerifySignFlag',
        type: 'boolean',
        // label: intl.get(`${prefix}.invokeVerifySignFlag`).d('校验签名'),
        defaultValue: false,
        label: (
          <QuestionPopover
            text={getLang('INVOKE_VERIFY_SIGN_FLAG')}
            message={getLang('INVOKE_VERIFY_SIGN_FLAG_TIP')}
          />
        ),
      },
      {
        name: 'pageInterfaces',
        type: 'object',
        ignore: 'always',
      },
      {
        name: 'historyVersion',
        type: 'string',
        label: getLang('HISTORY_VERSION'),
      },
      {
        name: 'formatVersion',
        type: 'string',
        label: getLang('CURRENT_VERSION'),
      },
      {
        name: 'status',
        type: 'string',
        label: getLang('STATUS'),
        lookupCode: SERVICE_STATUS,
      },
    ],
    transport: {
      read: ({ data, params, dataSet }) => {
        const { interfaceServerId, version, history } = data;
        if (history) {
          dataSet.setQueryParameter('history', false);
          return {
            url: `${HZERO_HITF}/v1${level}/interface-servers/${interfaceServerId}/view/${version}`,
            method: 'GET',
            transformResponse: (response) => {
              try {
                const res = JSON.parse(response);
                const { domainUrl = '', serviceCategory } = res;
                const urlArr = domainUrl.split('//');
                const [pre, ...others] = urlArr;
                let tempUrl = others[0];
                const { length } = others;
                for (let i = 1; i < others.length; i++) {
                  const count = i;
                  if (count + 1 > length) {
                    return;
                  }
                  tempUrl = `${tempUrl}//${others[i]}`;
                }
                const protocol = `${pre}//`;
                return {
                  ...res,
                  protocol: serviceCategory === SERVICE_CONSTANT.EXTERNAL ? protocol : '',
                  domainUrl: serviceCategory === SERVICE_CONSTANT.EXTERNAL ? tempUrl : domainUrl,
                };
              } catch (error) {
                return null;
              }
            },
          };
        }
        return {
          url: `${HZERO_HITF}/v1${level}/interface-servers/${interfaceServerId}`,
          method: 'GET',
          params: { ...data, ...params },
          transformResponse: (response) => {
            try {
              const res = JSON.parse(response);
              const { domainUrl = '', serviceCategory } = res;
              const urlArr = domainUrl.split('//');
              const [pre, ...others] = urlArr;
              let tempUrl = others[0];
              const { length } = others;
              for (let i = 1; i < others.length; i++) {
                const count = i;
                if (count + 1 > length) {
                  return;
                }
                tempUrl = `${tempUrl}//${others[i]}`;
              }
              const protocol = `${pre}//`;
              return {
                ...res,
                protocol: serviceCategory === SERVICE_CONSTANT.EXTERNAL ? protocol : '',
                domainUrl: serviceCategory === SERVICE_CONSTANT.EXTERNAL ? tempUrl : domainUrl,
              };
            } catch (error) {
              return null;
            }
          },
        };
      },
    },
  };
};

export { basicFormDS, historyDS };
