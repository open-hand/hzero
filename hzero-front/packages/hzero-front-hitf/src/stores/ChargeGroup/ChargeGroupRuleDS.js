import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { dateRender } from 'utils/renderer';
import {
  PAYMENT_MODEL,
  CHARGE_METHOD,
  SETTLEMENT_PERIOD,
  PAYMENT_MODEL_FIELDS,
  CHARGE_RULE,
} from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 组合计费规则配置表 DataSet
 */

export default () => ({
  primaryKey: 'groupRuleId',
  autoQuery: false,
  pageSize: 10,
  name: DataSet.ChargeGroupRuleDS,
  selection: false,
  fields: [
    {
      name: 'groupRuleId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'groupHeaderId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'chargeRuleObject',
      type: 'object',
      required: true,
      lovCode: CHARGE_RULE,
      textField: 'ruleName',
      valueField: 'ruleHeaderId',
      ignore: 'always',
      dynamicProps: () => ({
        lovPara: {
          statusCode: 'RELEASED',
          startDate: dateRender(new Date()),
        },
      }),
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.chargeRuleName').d('规则名称'),
    },
    {
      name: 'chargeRuleId',
      type: 'string',
      required: true,
      bind: 'chargeRuleObject.ruleHeaderId',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.chargeRuleId').d('规则ID'),
    },
    {
      name: 'chargeRuleCode',
      type: 'string',
      required: true,
      bind: 'chargeRuleObject.ruleNum',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.chargeRuleCOde').d('规则代码'),
    },
    {
      name: 'chargeRuleName',
      type: 'string',
      required: true,
      bind: 'chargeRuleObject.ruleName',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.chargeRuleName').d('规则名称'),
    },
    {
      name: 'paymentModel',
      type: 'string',
      lookupCode: PAYMENT_MODEL,
      bind: 'chargeRuleObject.paymentModelCode',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.paymentModel').d('付费模式'),
    },
    {
      name: 'chargeMethodCode',
      type: 'string',
      lookupCode: CHARGE_METHOD,
      bind: 'chargeRuleObject.methodCode',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.chargeMethodCode').d('计费方式'),
    },
    {
      name: 'chargeUomCode',
      type: 'string',
      bind: 'chargeRuleObject.unitCode',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.chargeUomCode').d('计费单位'),
    },
    {
      name: 'settlementPeriod',
      type: 'string',
      required: true,
      lookupCode: SETTLEMENT_PERIOD,
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.settlementPeriod').d('结算周期'),
      dynamicProps: ({ record }) => {
        let required = true;
        let readOnly = true;
        // 付费模式=预付费，结算周期不可编辑
        if (PAYMENT_MODEL_FIELDS.BEFORE === record.get('paymentModel')) {
          required = false;
          readOnly = true;
        } else {
          required = true;
          readOnly = false;
        }
        return {
          required,
          readOnly,
        };
      },
    },
    {
      name: 'startDate',
      type: 'string',
      format: 'YYYY-MM-DD',
      bind: 'chargeRuleObject.startDate',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeSet.model.chargeGroupRule.startDate').d('起始日期'),
    },
    {
      name: 'endDate',
      type: 'string',
      format: 'YYYY-MM-DD',
      bind: 'chargeRuleObject.endDate',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeSet.model.chargeGroupRule.endDate').d('截止日期'),
    },
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.tenantId').d('租户ID'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.chargeGroup.model.chargeGroupRule.remark').d('备注'),
    },
  ],
  queryFields: [],
  transport: {
    read: function read({ data, params }) {
      const { groupHeaderId } = data;
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/${groupHeaderId}/rule`
        : `${HZERO_HITF}/v1/charge-group-headers/${groupHeaderId}/rule`;
      return {
        data,
        params,
        url,
        method: 'GET',
      };
    },
    destroy: ({ data, params }) => ({
      url: isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/rule`
        : `${HZERO_HITF}/v1/charge-group-headers/rule`,
      data: {
        groupRuleId: data[0].groupRuleId,
        _token: data[0]._token,
      },
      params,
      method: 'DELETE',
    }),
  },
  feedback: {
    loadSuccess: (res) => {
      if (res) {
        if (res.content) {
          // 明细页面 设置服务代码Lov/接口代码Lov的值
          res.content.map((item) => {
            let _item = item;
            // 设置计费规则Lov的值
            _item = {
              ...item,
              chargeRuleObject: {
                ruleNum: item.chargeRuleCode,
                ruleHeaderId: item.chargeRuleId,
                ruleName: item.chargeRuleName,
              },
            };
            return _item;
          });
        }
      }
    },
  },
});
