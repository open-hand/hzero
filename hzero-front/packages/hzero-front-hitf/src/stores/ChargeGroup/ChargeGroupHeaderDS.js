import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { dateRender } from 'utils/renderer';
import { CODE_UPPER } from 'utils/regExp';
import { CHARGE_GROUP_STATUS, CHARGE_GROUP_STATUS_FIELDS } from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 组合计费设置头表 DataSet
 */

export default () => ({
  primaryKey: 'groupHeaderId',
  autoQuery: false,
  pageSize: 10,
  name: DataSet.ChargeGroupHeaderDS,
  selection: 'multiple',
  fields: [
    {
      name: 'groupHeaderId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'groupCode',
      type: 'string',
      required: true,
      pattern: CODE_UPPER,
      format: 'uppercase',
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.groupCode').d('组合代码'),
    },
    {
      name: 'groupName',
      type: 'string',
      required: true,
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.groupName').d('组合名称'),
    },
    {
      name: 'statusCode',
      type: 'string',
      defaultValue: CHARGE_GROUP_STATUS_FIELDS.NEW,
      required: true,
      lookupCode: CHARGE_GROUP_STATUS,
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.statusCode').d('状态'),
    },
    {
      name: 'startDate',
      type: 'date',
      format: 'YYYY-MM-DD',
      transformRequest: (value) => dateRender(value),
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.startDate').d('生效日期'),
    },
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.tenantId').d('租户ID'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.remark').d('备注'),
    },
  ],
  queryFields: [
    {
      name: 'groupCode',
      type: 'string',
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.groupCode').d('组合代码'),
    },
    {
      name: 'groupName',
      type: 'string',
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.groupName').d('组合名称'),
    },
    {
      name: 'statusCode',
      type: 'string',
      lookupCode: CHARGE_GROUP_STATUS,
      label: intl.get('hitf.chargeGroup.model.chargeGroupHeader.statusCode').d('状态'),
    },
  ],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers`
        : `${HZERO_HITF}/v1/charge-group-headers`;
      return {
        data,
        params,
        url: getUrl(url, data.groupHeaderId),
        method: 'GET',
      };
    },
    submit: ({ data, params }) => {
      const _data = data.map((item) => {
        let _chargeGroupLineList = [];
        let _chargeGroupRuleList = [];
        if (item.chargeGroupLineList) {
          _chargeGroupLineList = item.chargeGroupLineList.map((line) => ({
            ...line,
            groupHeaderId: item.groupHeaderId ? item.groupHeaderId : 0,
            tenantId: line.tenantId ? line.tenantId : organizationId,
          }));
        }
        if (item.chargeGroupRuleList) {
          _chargeGroupRuleList = item.chargeGroupRuleList.map((rule) => ({
            ...rule,
            groupHeaderId: item.groupHeaderId ? item.groupHeaderId : 0,
            tenantId: rule.tenantId ? rule.tenantId : organizationId,
          }));
        }
        return {
          ...item,
          chargeGroupLineList: _chargeGroupLineList,
          chargeGroupRuleList: _chargeGroupRuleList,
          tenantId: item.tenantId ? item.tenantId : organizationId,
        };
      });
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers`
          : `${HZERO_HITF}/v1/charge-group-headers`,
        data: _data[0],
        params,
        method: 'POST',
      };
    },
    update: ({ data, params }) => {
      const _data = data.map((item) => {
        let _chargeGroupLineList = [];
        let _chargeGroupRuleList = [];
        if (item.chargeGroupLineList) {
          _chargeGroupLineList = item.chargeGroupLineList.map((line) => ({
            ...line,
            groupHeaderId: item.groupHeaderId,
            tenantId: line.tenantId ? line.tenantId : organizationId,
          }));
        }
        if (item.chargeGroupRuleList) {
          _chargeGroupRuleList = item.chargeGroupRuleList.map((rule) => ({
            ...rule,
            groupHeaderId: item.groupHeaderId,
            tenantId: rule.tenantId ? rule.tenantId : organizationId,
          }));
        }
        return {
          ...item,
          chargeGroupLineList: _chargeGroupLineList,
          chargeGroupRuleList: _chargeGroupRuleList,
          tenantId: item.tenantId ? item.tenantId : organizationId,
        };
      });
      return {
        url: isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers`
          : `${HZERO_HITF}/v1/charge-group-headers`,
        data: _data[0],
        params,
        method: 'PUT',
      };
    },
    destroy: ({ data, params }) => ({
      url: isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers`
        : `${HZERO_HITF}/v1/charge-group-headers`,
      data: {
        groupHeaderId: data[0].groupHeaderId,
        _token: data[0]._token,
      },
      params,
      method: 'DELETE',
    }),
  },
});

function getUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
