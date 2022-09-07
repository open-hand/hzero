/*
 * source - 结算规则 DataSet
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-11
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES } from 'utils/config';

export default {
  autoQuery: false,
  selection: false,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/in-parameter`,
      data,
      params: { tenantId: getCurrentOrganizationId(), ...params },
      method: 'GET',
    }),
  },
  fields: [],
};
