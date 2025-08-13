/**
 * 事件规则 - 单例DS
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @Date: 2020-08-13
 * @Copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { CODE } from 'utils/regExp';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const nodeDs = (eventId) => {
  return {
    paging: false,
    autoQuery: false,
    primaryKey: 'id',
    autoQueryAfterSubmit: false,
    dataKey: 'ruleList',
    fields: [
      { name: 'isChange', type: 'boolean', ignore: 'always' },
      { name: 'callType', type: 'string' },
      { name: 'current', type: 'string', ignore: 'always' },
      {
        name: 'matchingRule',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '1',
        },
        label: intl.get('hpfm.event.model.eventRule.rule').d('匹配规则'),
      },
      {
        name: 'beanName',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0' && record.get('callType') === 'M',
          pattern: ({ record }) => (record.get('current') !== '0' ? CODE : null),
        },
        defaultValidationMessages: {
          patternMismatch: intl
            .get('hzero.common.validation.code')
            .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
        },
        label: intl.get('hpfm.event.model.eventRule.beanName').d('BeanName'),
      },
      {
        name: 'methodName',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0' && record.get('callType') === 'M',
          pattern: ({ record }) => (record.get('current') !== '0' ? CODE : null),
        },
        defaultValidationMessages: {
          patternMismatch: intl
            .get('hzero.common.validation.code')
            .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
        },
        label: intl.get('hpfm.event.model.eventRule.methodName').d('MethodName'),
      },
      {
        name: 'apiUrl',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0' && record.get('callType') === 'A',
        },
        label: intl.get('hpfm.event.model.eventRule.apiUrl').d('API URL'),
      },
      {
        name: 'apiMethod',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0' && record.get('callType') === 'A',
        },
        lookupCode: 'HPFM.EVENT_RULE.API_METHOD',
        label: intl.get('hpfm.event.model.eventRule.apiMethod').d('API Method'),
      },
      {
        name: 'serverCode',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0' && record.get('callType') === 'W',
        },
        label: intl.get('hpfm.event.model.eventRule.serverCode').d('WebHook接收方'),
      },
      {
        name: 'messageCode',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0' && record.get('callType') === 'W',
        },
        label: intl.get('hpfm.event.model.eventRule.messageCode').d('消息编码'),
      },
      {
        name: 'orderSeq',
        type: 'number',
        label: intl.get('hpfm.event.model.eventRule.orderSeq').d('顺序'),
      },
      {
        name: 'ruleDescription',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => record.get('current') !== '0',
        },
        label: intl.get('hpfm.event.model.eventRule.ruleDescription').d('规则描述'),
      },
      {
        name: 'syncFlag',
        type: 'number',
        falseValue: 0,
        trueValue: 1,
        defaultValue: 0,
        label: intl.get('hpfm.event.model.eventRule.syncFlag').d('是否同步'),
      },
      {
        name: 'resultFlag',
        type: 'number',
        falseValue: 0,
        trueValue: 1,
        defaultValue: 0,
        label: intl.get('hpfm.event.model.eventRule.resultFlag').d('返回结果'),
      },
      {
        name: 'enabledFlag',
        type: 'number',
        falseValue: 0,
        trueValue: 1,
        defaultValue: 1,
        label: intl.get('hzero.common.status.enable').d('启用'),
      },
    ],
    transport: {
      read: (config) => {
        return {
          ...config,
          method: 'get',
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${getCurrentOrganizationId()}/` : `/`
          }events/${eventId}`,
          data: {},
          params: {},
        };
      },
      submit: (config) => {
        const { data } = config;
        const trueData = data.map((item) => {
          return {
            ...item,
            __id: undefined,
            id: undefined,
            status: undefined,
            _status: item.status || item._status,
          };
        });
        return {
          ...config,
          method: 'POST',
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${getCurrentOrganizationId()}/` : `/`
          }events/${eventId}/rules/batch`,
          data: trueData,
          params: {},
        };
      },
    },
  };
};

export { nodeDs };
