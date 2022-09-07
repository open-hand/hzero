import moment from 'moment';

import intl from 'utils/intl';
import { HZERO_CHG } from 'utils/config';
import { dateRender } from 'utils/renderer';
import { getCurrentOrganizationId, isTenantRoleLevel, getDateFormat } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import CodeConstants from '@/constants/CodeConstants';

const prefix = 'hchg.payCal.model.payCal';
const level = isTenantRoleLevel() ? `/${getCurrentOrganizationId()}` : '';
const calculateFormDS = {
  selection: false,
  autoQuery: false,
  autoCreate: true,
  paging: false,
  fields: [
    {
      name: 'ruleLov',
      label: intl.get(`${prefix}.ruleName`).d('规则名称'),
      type: 'object',
      required: true,
      lovCode: isTenantRoleLevel() ? CodeConstants.ruleHeaderOrg : CodeConstants.ruleHeader,
      valueField: 'ruleNum',
      textField: 'ruleName',
    },
    {
      name: 'ruleNum',
      label: intl.get(`${prefix}.ruleNum`).d('规则代码'),
      type: 'string',
      bind: 'ruleLov.ruleNum',
    },
    {
      name: 'methodCode',
      label: intl.get(`${prefix}.methodCode`).d('计费方式'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeMethod,
      bind: 'ruleLov.methodCode',
    },
    {
      name: 'paymentModelCode',
      label: intl.get(`${prefix}.paymentModelCode`).d('付费模式'),
      type: 'string',
      lookupCode: CodeConstants.RulePaymentModel,
      bind: 'ruleLov.paymentModelCode',
    },
    {
      name: 'typeCode',
      label: intl.get(`${prefix}.typeCode`).d('计费类型'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeType,
      bind: 'ruleLov.typeCode',
    },
    {
      name: 'unitCode',
      label: intl.get(`${prefix}.unitCode`).d('计费单位'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeUnit,
      bind: 'ruleLov.unitCode',
    },
    {
      name: 'statusCode',
      label: intl.get(`${prefix}.statusCode`).d('状态'),
      type: 'string',
      lookupCode: CodeConstants.RuleStatus,
      bind: 'ruleLov.statusCode',
    },
    {
      name: '_startDate',
      label: intl.get(`${prefix}._startDate`).d('有效日期从'),
      type: 'string',
      bind: 'ruleLov.startDate',
    },
    {
      name: '_endDate',
      label: intl.get(`${prefix}._endDate`).d('有效日期至'),
      type: 'string',
      bind: 'ruleLov.endDate',
    },
    {
      name: 'accountLov',
      label: intl.get(`${prefix}.accountName`).d('账户名称'),
      type: 'object',
      required: true,
      lovCode: isTenantRoleLevel() ? CodeConstants.AccountBalanceOrg : CodeConstants.AccountBalance,
      valueField: 'accountName',
      textField: 'accountName',
    },
    {
      name: 'accountNum',
      label: intl.get(`${prefix}.accountNum`).d('账户编码'),
      type: 'string',
      bind: 'accountLov.accountNum',
    },
    {
      name: 'accountType',
      label: intl.get(`${prefix}.accountType`).d('账户类型'),
      type: 'string',
      lookupCode: CodeConstants.AccountType,
      bind: 'accountLov.accountType',
    },
    {
      name: 'enabledFlag',
      label: intl.get(`${prefix}.enabledFlag`).d('启用标识'),
      type: 'number',
      lookupCode: CodeConstants.EnabledFlag,
      bind: 'accountLov.enabledFlag',
    },
    {
      name: 'startDate',
      label: intl.get(`${prefix}.startDate`).d('计费时间从'),
      type: 'date',
      required: true,
      dynamicProps: {
        max: ({ record }) => record.get('endDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'endDate',
      label: intl.get(`${prefix}.endDate`).d('计费时间至'),
      type: 'date',
      required: true,
      dynamicProps: {
        min: ({ record }) => record.get('startDate'),
      },
      transformRequest: (value) => dateRender(value),
    },
    {
      name: 'quantity',
      label: intl.get(`${prefix}.calculateAmount`).d('计费数量'),
      type: 'number',
      required: true,
    },
    {
      name: 'calculateEngine',
      label: intl.get(`${prefix}.calculateEngine`).d('计费引擎'),
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const url = `${HZERO_CHG}/v1${level}/charge-engine/calculate`;
      return {
        url,
        params: { ...data },
        method: 'GET',
      };
    },
  },
};

const resultFormDS = {
  selection: false,
  autoQuery: false,
  autoCreate: true,
  paging: false,
  fields: [
    {
      name: 'resultTotalAmount',
      label: intl.get(`${prefix}.resultTotalAmount`).d('计算总金额'),
      type: 'number',
    },
    {
      name: 'discountAmount',
      label: intl.get(`${prefix}.resultDiscountAmount`).d('计算优惠金额'),
      type: 'number',
    },
    {
      name: 'resultAmount',
      label: intl.get(`${prefix}.resultAmount`).d('计算应付金额'),
      type: 'number',
    },
    {
      name: 'totalAmount',
      label: intl.get(`${prefix}.totalAmount`).d('实际总金额'),
      type: 'number',
    },
    {
      name: 'actualDiscountAmount',
      label: intl.get(`${prefix}.actualDiscountAmount`).d('实际优惠金额'),
      type: 'number',
    },
    {
      name: 'actualResultAmount',
      label: intl.get(`${prefix}.actualResultAmount`).d('实际应付金额'),
      type: 'number',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const url = `${HZERO_CHG}/v1${level}/charge-engine/calculate`;
      data.startDate =
        data.startDate && moment(data.startDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      data.endDate =
        data.endDate && moment(data.endDate, getDateFormat()).format(DEFAULT_DATE_FORMAT);
      return {
        url,
        params: { ...data },
        method: 'GET',
        transformResponse: (response) => {
          const res = JSON.parse(response);
          const { chargeDetails = [], discountDetails = [] } = res;
          const temp = {
            ...res,
            resultTotalAmount: res.totalAmount,
            chargeDetails: chargeDetails.map((item) => ({
              ...item,
              ...(item.corePluginParamItem || {}),
            })),
            discountDetails: discountDetails.map((item) => ({
              ...item,
              ...(item.extendsPluginParam || {}),
            })),
          };
          return temp;
        },
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
      name: 'order',
      label: intl.get(`${prefix}.order`).d('序号'),
      type: 'number',
    },
    {
      name: 'greaterThan',
      label: intl.get(`${prefix}.greaterThan`).d('数值从'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'lessAndEquals',
      label: intl.get(`${prefix}.lessAndEquals`).d('数值至'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'type',
      label: intl.get(`${prefix}.chargeType`).d('计费类型'),
      type: 'string',
      lookupCode: CodeConstants.RuleChargeType,
    },
    {
      name: 'value',
      label: intl.get(`${prefix}.chargeValue`).d('计费值'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'chargeQuantity',
      label: intl.get(`${prefix}.chargeQuantity`).d('计费数量'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'beforeResultAmount',
      label: intl.get(`${prefix}.beforeResultAmount`).d('计费前累计金额'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'chargeAmount',
      label: intl.get(`${prefix}.chargeAmount`).d('计费金额'),
      type: 'number',
      step: 0.01,
    },
  ],
};

const discountTableDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  fields: [
    {
      name: 'order',
      label: intl.get(`${prefix}.order`).d('序号'),
      type: 'number',
    },
    {
      name: 'type',
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
      name: 'beforeResultAmount',
      label: intl.get(`${prefix}.beforeDiscountAmount`).d('优惠前累计金额'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'discountAmount',
      label: intl.get(`${prefix}.discountAmount`).d('优惠金额'),
      type: 'number',
      step: 0.01,
    },
  ],
};

export { calculateFormDS, resultFormDS, ruleTableDS, discountTableDS };
