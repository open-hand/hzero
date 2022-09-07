import moment from 'moment';

import intl from 'utils/intl';
import { HZERO_CHG } from 'utils/config';
import { dateRender } from 'utils/renderer';
import { isUndefined } from 'lodash';
import { getCurrentOrganizationId, isTenantRoleLevel, getDateFormat } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import CodeConstants from '@/constants/CodeConstants';
import { CHARGE_RULE_CONSTANT } from '@/constants/constants';

const prefix = 'hchg.chargeRule.model.chargeRule';
const level = isTenantRoleLevel() ? `/${getCurrentOrganizationId()}` : '';
const chargeRuleTableDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  autoQueryAfterSubmit: true,
  primaryKey: 'ruleHeaderId',
  queryFields: [
    {
      name: 'ruleNum',
      label: intl.get(`${prefix}.ruleNum`).d('规则代码'),
      type: 'string',
      format: 'uppercase',
    },
    {
      name: 'ruleName',
      label: intl.get(`${prefix}.ruleName`).d('规则名称'),
      type: 'string',
    },
    {
      name: 'statusCode',
      label: intl.get(`${prefix}.statusCode`).d('状态'),
      type: 'string',
      lookupCode: CodeConstants.RuleStatus,
    },
    {
      name: 'paymentModelCode',
      label: intl.get(`${prefix}.paymentModelCode`).d('付费模式'),
      type: 'string',
      lookupCode: CodeConstants.RulePaymentModel,
    },
    {
      name: 'methodCode',
      label: intl.get(`${prefix}.methodCode`).d('计费方式'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeMethod,
    },
    {
      name: 'typeCode',
      label: intl.get(`${prefix}.typeCode`).d('计费类型'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeType,
    },
    {
      name: 'startDate',
      label: intl.get(`${prefix}.startDate`).d('有效日期从'),
      type: 'date',
      dynamicProps: {
        max: ({ record }) => record.get('endDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'endDate',
      label: intl.get(`${prefix}.endDate`).d('有效日期至'),
      type: 'date',
      dynamicProps: {
        min: ({ record }) => record.get('startDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
  ],
  fields: [
    {
      name: 'seqNumber',
      label: intl.get(`${prefix}.seqNumber`).d('序号'),
      type: 'number',
    },
    {
      name: 'ruleNum',
      label: intl.get(`${prefix}.ruleNum`).d('规则代码'),
      type: 'string',
    },
    {
      name: 'ruleName',
      label: intl.get(`${prefix}.ruleName`).d('规则名称'),
      type: 'string',
    },
    {
      name: 'statusCode',
      label: intl.get(`${prefix}.statusCode`).d('状态'),
      type: 'string',
      lookupCode: CodeConstants.RuleStatus,
    },
    {
      name: 'paymentModelCode',
      label: intl.get(`${prefix}.paymentModelCode`).d('付费模式'),
      type: 'string',
      lookupCode: CodeConstants.RulePaymentModel,
    },
    {
      name: 'methodCode',
      label: intl.get(`${prefix}.methodCode`).d('计费方式'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeMethod,
    },
    {
      name: 'typeCode',
      label: intl.get(`${prefix}.typeCode`).d('计费类型'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeType,
    },
    {
      name: 'unitCode',
      label: intl.get(`${prefix}.unitCode`).d('计量单位'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeUnit,
    },
    {
      name: 'startDate',
      label: intl.get(`${prefix}.startDate`).d('有效日期从'),
      type: 'string',
    },
    {
      name: 'endDate',
      label: intl.get(`${prefix}.endDate`).d('有效日期至'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      data.startDate =
        data.startDate && moment(data.startDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      data.endDate =
        data.endDate && moment(data.endDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      return {
        url: `${HZERO_CHG}/v1${level}/rule-headers`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: (config) => {
      const { data } = config;
      const { ruleHeaderId, _type } = data[0];
      const url = `${HZERO_CHG}/v1${level}/rule-headers/${_type}/${ruleHeaderId}`;
      return {
        url,
        data: data[0],
        method: 'PUT',
      };
    },
    destroy: (config) => {
      const { data } = config;
      const url = `${HZERO_CHG}/v1${level}/rule-headers`;
      return {
        url,
        data: data[0],
        method: 'DELETE',
      };
    },
  },
};

const detailFormDS = {
  primaryKey: 'ruleHeaderId',
  selection: false,
  autoQuery: false,
  paging: false,
  autoQueryAfterSubmit: true,
  fields: [
    {
      name: 'ruleNum',
      label: intl.get(`${prefix}.ruleNum`).d('规则代码'),
      type: 'string',
      required: true,
      format: 'uppercase',
      pattern: CODE_UPPER,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'ruleName',
      label: intl.get(`${prefix}.ruleName`).d('规则名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'statusCode',
      label: intl.get(`${prefix}.statusCode`).d('状态'),
      type: 'string',
      lookupCode: CodeConstants.RuleStatus,
    },
    {
      name: 'paymentModelCode',
      label: intl.get(`${prefix}.paymentModelCode`).d('付费模式'),
      type: 'string',
      lookupCode: CodeConstants.RulePaymentModel,
      required: true,
    },
    {
      name: 'methodCode',
      label: intl.get(`${prefix}.methodCode`).d('计费方式'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeMethod,
      required: true,
    },
    {
      name: 'typeCode',
      label: intl.get(`${prefix}.typeCode`).d('计费类型'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeType,
      required: true,
    },
    {
      name: 'unitCode',
      label: intl.get(`${prefix}.unitCode`).d('计量单位'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeUnit,
      required: true,
    },
    {
      name: 'prepayAmount',
      label: intl.get(`${prefix}.prepayAmount`).d('预付金额'),
      type: 'number',
    },
    {
      name: 'calculateEngine',
      label: intl.get(`${prefix}.calculateEngine`).d('计费引擎'),
      type: 'string',
    },
    {
      name: 'startDate',
      label: intl.get(`${prefix}.startDate`).d('有效日期从'),
      type: 'date',
      required: true,
      dynamicProps: {
        max: ({ record }) => record.get('endDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'endDate',
      label: intl.get(`${prefix}.endDate`).d('有效日期至'),
      type: 'date',
      dynamicProps: {
        min: ({ record }) => record.get('startDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { ruleHeaderId } = data;
      const url = `${HZERO_CHG}/v1${level}/rule-headers/${ruleHeaderId}`;
      return {
        url,
        params: { ...data },
        method: 'GET',
      };
    },
    create: (config) => {
      const { data } = config;
      const startDate =
        data[0].startDate && moment(data[0].startDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      const endDate =
        data[0].endDate && moment(data[0].endDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      const url = `${HZERO_CHG}/v1${level}/rule-headers`;
      return {
        url,
        data: {
          ...data[0],
          startDate,
          endDate,
        },
        method: 'POST',
      };
    },
    update: (config) => {
      const { data } = config;
      const url = `${HZERO_CHG}/v1${level}/rule-headers`;
      const startDate =
        data[0].startDate && moment(data[0].startDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      const endDate =
        data[0].endDate && moment(data[0].endDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      return {
        url,
        data: {
          ...data[0],
          startDate,
          endDate,
        },
        method: 'PUT',
      };
    },
  },
};

const ruleTableDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  fields: [
    {
      name: 'seqNumber',
      label: intl.get(`${prefix}.seqNumber`).d('序号'),
      type: 'number',
      required: true,
      unique: true,
    },
    {
      name: 'greaterThan',
      label: intl.get(`${prefix}.greaterThan`).d('大于'),
      type: 'number',
      step: 0.01,
      validator: (value, name, record) => {
        const { previousRecord } = record;
        const expectedValue = isUndefined(previousRecord) ? 0 : previousRecord.get('lessAndEquals');
        if (previousRecord && isUndefined(previousRecord.get('lessAndEquals'))) {
          return '请先填写上一条规则详情的小于等于';
        }
        if (
          record.dataSet.parent.current.get('methodCode') === 'COUNT' &&
          value !== expectedValue
        ) {
          return `大于必须为${expectedValue}`;
        }
        return true;
      },
      dynamicProps: ({ dataSet }) => ({
        required: dataSet.parent.current.get('methodCode') === 'COUNT',
      }),
    },
    {
      name: 'lessAndEquals',
      label: intl.get(`${prefix}.lessAndEquals`).d('小于等于'),
      type: 'number',
      step: 0.01,
      dynamicProps: ({ record, dataSet }) => ({
        min: record.get('greaterThan'),
        required:
          dataSet.parent.current.get('methodCode') === 'COUNT' && !isUndefined(record.nextRecord),
      }),
    },
    {
      name: 'constantValue',
      label: intl.get(`${prefix}.constantValue`).d('固定数值'),
      type: 'number',
      step: 0.01,
      dynamicProps: ({ dataSet }) => ({
        required: dataSet.parent.current.get('methodCode') === 'PACKAGE',
      }),
    },
    {
      name: 'price',
      label: intl.get(`${prefix}.price`).d('价格'),
      type: 'number',
      required: true,
      step: 0.01,
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_CHG}/v1${level}/rule-lines`,
      params: { ...data, ...params },
      method: 'get',
    }),
    // create: ({ data }) => ({
    //   url: `${HZERO_CHG}/v1${level}/rule-lines`,
    //   data,
    //   method: 'POST',
    // }),
    // update: ({ data }) => ({
    //   url: `${HZERO_CHG}/v1${level}/rule-lines`,
    //   data,
    //   method: 'POST',
    // }),
    destroy: ({ data }) => ({
      url: `${HZERO_CHG}/v1${level}/rule-lines`,
      data: data[0],
      method: 'DELETE',
    }),
  },
};

const discountTableDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  fields: [
    {
      name: 'seqNumber',
      label: intl.get(`${prefix}.seqNumber`).d('序号'),
      type: 'number',
      required: true,
      unique: true,
    },
    {
      name: 'typeCode',
      label: intl.get(`${prefix}.type`).d('类型'),
      type: 'string',
      lookupCode: CodeConstants.DiscountType,
      required: true,
    },
    {
      name: 'discountValue',
      label: intl.get(`${prefix}.discountValue`).d('数值'),
      type: 'number',
      required: true,
      step: 0.01,
      dynamicProps: ({ record }) => {
        if (record.get('typeCode') === 'DISCOUNT') {
          return {
            min: 0.01,
            max: 1,
          };
        }
        return {};
      },
    },
    {
      name: 'effectiveRangeCode',
      label: intl.get(`${prefix}.effectiveRangeCode`).d('生效范围'),
      type: 'string',
      lookupCode: CodeConstants.DiscountEffectiveRange,
      required: true,
    },
    {
      name: 'startDate',
      label: intl.get(`${prefix}.startDate`).d('有效日期从'),
      type: 'date',
      dynamicProps: {
        max: ({ record }) => record.get('endDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'endDate',
      label: intl.get(`${prefix}.endDate`).d('有效日期至'),
      type: 'date',
      dynamicProps: {
        min: ({ record }) => record.get('startDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'remark',
      label: intl.get(`${prefix}.remark`).d('备注'),
      type: 'string',
    },
  ],
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_CHG}/v1${level}/rule-discounts`,
      params: { ...data, ...params },
      method: 'get',
    }),
    // create: ({ data }) => {
    //   return {
    //     data,
    //     url: `${HZERO_CHG}/v1${level}/rule-discounts`,
    //     method: 'POST',
    //   };
    // },
    // update: ({ data }) => {
    //   return {
    //     data,
    //     url: `${HZERO_CHG}/v1${level}/rule-discounts`,
    //     method: 'POST',
    //   };
    // },
    destroy: ({ data }) => ({
      url: `${HZERO_CHG}/v1${level}/rule-discounts`,
      data: data[0],
      method: 'DELETE',
    }),
  },
};

const modalFormDS = {
  autoQuery: false,
  selection: false,
  primaryKey: 'ruleDiscountId',
  pageSize: 10,
  fields: [
    {
      name: 'seqNumber',
      label: intl.get(`${prefix}.seqNumber`).d('序号'),
      type: 'number',
    },
    {
      name: 'typeCode',
      label: intl.get(`${prefix}.type`).d('类型'),
      type: 'string',
      lookupCode: CodeConstants.DiscountType,
    },
    {
      name: 'discountValue',
      label: intl.get(`${prefix}.discountValue`).d('数值'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'effectiveRangeCode',
      label: intl.get(`${prefix}.effectiveRangeCode`).d('生效范围'),
      type: 'string',
      lookupCode: CodeConstants.DiscountEffectiveRange,
    },
    {
      name: 'startDate',
      label: intl.get(`${prefix}.startDate`).d('有效日期从'),
      type: 'string',
    },
    {
      name: 'endDate',
      label: intl.get(`${prefix}.endDate`).d('有效日期至'),
      type: 'string',
    },
  ],
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_CHG}/v1${level}/rule-discounts/${data.ruleDiscountId}`,
      params: { ...data, ...params },
      method: 'get',
    }),
  },
};

function modalTableDS(effectiveRange) {
  return {
    autoQuery: false,
    selection: false,
    primaryKey: 'discountListId',
    autoQueryAfterSubmit: true,
    fields: [
      {
        name: 'seqNumber',
        label: intl.get(`${prefix}.seqNumber`).d('序号'),
        type: 'number',
      },
      {
        name: 'tenantLov',
        label: intl.get(`${prefix}.name`).d('名称'),
        dynamicProps: () => {
          const isUser = effectiveRange === CHARGE_RULE_CONSTANT.USER;
          return {
            type: 'object',
            noCache: true,
            required: true,
            ignore: 'always',
            lovCode: isUser ? CodeConstants.User : CodeConstants.Tenant,
            valueField: isUser ? 'id' : 'tenantId',
            textField: isUser ? 'realName' : 'tenantName',
          };
        },
      },
      {
        name: 'valueId',
        type: 'number',
        dynamicProps: () => {
          const isUser = effectiveRange === CHARGE_RULE_CONSTANT.USER;
          return {
            bind: isUser ? 'tenantLov.id' : 'tenantLov.tenantId',
          };
        },
      },
      {
        name: 'valueName',
        type: 'string',
        dynamicProps: () => {
          const isUser = effectiveRange === CHARGE_RULE_CONSTANT.USER;
          return {
            bind: isUser ? 'tenantLov.realName' : 'tenantLov.tenantName',
          };
        },
      },
      {
        name: 'remark',
        label: intl.get(`${prefix}.remark`).d('备注'),
        type: 'string',
      },
    ],
    transport: {
      read: ({ data, params }) => ({
        url: `${HZERO_CHG}/v1${level}/rule-discount-lists`,
        params: { ...data, ...params },
        method: 'get',
      }),
      create: ({ data }) => ({
        url: `${HZERO_CHG}/v1${level}/rule-discount-lists`,
        data,
        method: 'POST',
      }),
      update: ({ data }) => ({
        url: `${HZERO_CHG}/v1${level}/rule-discount-lists`,
        data,
        method: 'PUT',
      }),
      destroy: ({ data }) => ({
        url: `${HZERO_CHG}/v1${level}/rule-discount-lists`,
        data: data[0],
        method: 'DELETE',
      }),
    },
  };
}

export { chargeRuleTableDS, detailFormDS, ruleTableDS, discountTableDS, modalFormDS, modalTableDS };
