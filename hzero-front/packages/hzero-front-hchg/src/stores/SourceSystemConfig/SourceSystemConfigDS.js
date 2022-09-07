import intl from 'hzero-front/lib/utils/intl';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import CodeConstants from '../../constants/CodeConstants';
import DataSetConstants from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 计费来源系统配置表 DataSet
 */

export default () => ({
  primaryKey: 'sourceSystemId',
  autoQuery: false,
  pageSize: 10,
  name: DataSetConstants.SourceSystemConfig,
  selection: false,
  fields: [
    {
      name: 'sourceSystemId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'systemNum',
      type: 'string',
      pattern: CODE_UPPER,
      format: 'uppercase',
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.systemNum').d('系统编号'),
    },
    {
      name: 'systemName',
      type: 'string',
      required: true,
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.systemName').d('系统名称'),
    },
    {
      name: 'callbackUrl',
      type: 'string',
      required: true,
      pattern: '^(https?|ftp)://[^\\s/$.?#].[^\\s]*$',
      defaultValidationMessages: {
        patternMismatch: intl.get('hchg.sourceSystem.validation.callback.url').d('回调URL格式有误'),
      },
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.callbackUrl').d('账单回调URL'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      required: true,
      falseValue: 0,
      trueValue: 1,
      defaultValue: 1,
      lookupCode: CodeConstants.EnabledFlag,
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.enabledFlag').d('是否启用'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.remark').d('备注'),
    },
  ],
  queryFields: [
    {
      name: 'systemNum',
      type: 'string',
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.systemNum').d('系统编号'),
    },
    {
      name: 'systemName',
      type: 'string',
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.systemName').d('系统名称'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      falseValue: 0,
      trueValue: 1,
      lookupCode: CodeConstants.EnabledFlag,
      label: intl.get('hchg.sourceSystem.model.sourceSystemConfig.enabledFlag').d('是否启用'),
    },
  ],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/source-system-configs`
        : `${HZERO_CHG}/v1/source-system-configs`;
      return {
        data,
        params,
        url: getDetailUrl(url, data.sourceSystemId),
        method: 'GET',
      };
    },
    submit: ({ data }) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/source-system-configs`
        : `${HZERO_CHG}/v1/source-system-configs`;
      return {
        url,
        data: data[0],
        method: 'POST',
      };
    },
    update: ({ data }) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/source-system-configs`
        : `${HZERO_CHG}/v1/source-system-configs`;
      return {
        url,
        data: data[0],
        method: 'PUT',
      };
    },
    destroy: ({ data }) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/source-system-configs`
        : `${HZERO_CHG}/v1/source-system-configs`;
      return {
        url,
        data: data[0],
        method: 'DELETE',
      };
    },
  },
});

function getDetailUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
