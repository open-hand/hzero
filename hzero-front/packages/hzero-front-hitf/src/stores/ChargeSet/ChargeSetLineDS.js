import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { dateRender } from 'utils/renderer';
import {
  PAYMENT_MODEL,
  PAYMENT_MODEL_FIELDS,
  CHARGE_METHOD,
  SETTLEMENT_PERIOD,
  CHARGE_RULE,
} from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 接口计费设置行表 DataSet
 */

export default () => ({
  primaryKey: 'setLineId',
  autoQuery: true,
  pageSize: 10,
  name: DataSet.ChargeSetLineDS,
  selection: false,
  fields: [
    {
      name: 'setLineId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'setHeaderId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'seqNumber',
      type: 'number',
      required: true,
      label: intl.get('hitf.chargeSet.model.chargeSetLine.setNumber').d('序号'),
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
      label: intl.get('hitf.chargeSet.model.chargeSetLine.chargeRuleId').d('计费规则头ID'),
    },
    {
      name: 'chargeRuleName',
      type: 'string',
      required: true,
      bind: 'chargeRuleObject.ruleName',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.chargeRuleName').d('计费规则名称'),
    },
    {
      name: 'chargeRuleCode',
      type: 'string',
      required: true,
      bind: 'chargeRuleObject.ruleNum',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.chargeRuleCode').d('计费规则代码'),
    },
    {
      name: 'paymentModel',
      type: 'string',
      required: true,
      lookupCode: PAYMENT_MODEL,
      bind: 'chargeRuleObject.paymentModelCode',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.paymentModel').d('付费模式'),
    },
    {
      name: 'chargeMethodCode',
      type: 'string',
      lookupCode: CHARGE_METHOD,
      bind: 'chargeRuleObject.methodCode',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.chargeMethodCode').d('计费方式'),
    },
    {
      name: 'chargeUomCode',
      type: 'string',
      bind: 'chargeRuleObject.unitCode',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.chargeUomCode').d('计费单位'),
    },
    {
      name: 'settlementPeriod',
      type: 'string',
      required: true,
      lookupCode: SETTLEMENT_PERIOD,
      label: intl.get('hitf.chargeSet.model.chargeSetLine.settlementPeriod').d('结算周期'),
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
      label: intl.get('hitf.chargeSet.model.chargeSetLine.startDate').d('起始日期'),
    },
    {
      name: 'endDate',
      type: 'string',
      format: 'YYYY-MM-DD',
      bind: 'chargeRuleObject.endDate',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeSet.model.chargeSetLine.endDate').d('截止日期'),
    },
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.tenantId').d('租户ID'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.chargeSet.model.chargeSetLine.remark').d('备注'),
    },
  ],
  queryFields: [],
  transport: {
    read: function read(config) {
      const { data } = config;
      const { setHeaderId } = data;
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers/${setHeaderId}/line`
        : `${HZERO_HITF}/v1/charge-set-headers/${setHeaderId}/line`;
      return {
        data: null,
        params: null,
        url,
        method: 'GET',
      };
    },
    destroy: ({ data, params }) => ({
      url: isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-set-headers/line`
        : `${HZERO_HITF}/v1/charge-set-headers/line`,
      data: {
        setLineId: data[0].setLineId,
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
