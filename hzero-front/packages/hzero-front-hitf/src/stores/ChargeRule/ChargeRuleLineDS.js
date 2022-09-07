import intl from 'hzero-front/lib/utils/intl';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 接口计费设置头表 DataSet
 */

export default () => ({
  primaryKey: 'setHeaderId',
  autoQuery: false,
  pageSize: 10,
  name: DataSet.ChargeRuleLine,
  selection: false,
  fields: [
    {
      name: 'greaterThan',
      label: intl.get(`hitf.chargeRule.model.chargeRuleLine.greaterThan`).d('大于'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'lessAndEquals',
      label: intl.get(`hitf.chargeRule.model.chargeRuleLine.lessAndEquals`).d('小于等于'),
      type: 'number',
      step: 0.01,
    },
    {
      name: 'constantValue',
      label: intl.get(`hitf.chargeRule.model.chargeRuleLine.constantValue`).d('总包'),
      type: 'number',
      step: 0.01,
      min: 0,
    },
    {
      name: 'price',
      label: intl.get(`hitf.chargeRule.model.chargeRuleLine.price`).d('价格'),
      type: 'number',
      step: 0.01,
      min: 0,
    },
  ],
  transport: {
    read: ({ data, params }) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_CHG}/v1/${organizationId}/rule-lines`
        : `${HZERO_CHG}/v1/rule-lines`;
      return {
        data,
        params,
        url,
        method: 'GET',
      };
    },
  },
});
