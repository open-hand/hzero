import intl from 'hzero-front/lib/utils/intl';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { PURCHASE_TYPE, AVAILABLE_STATUS } from '@/constants/CodeConstants';
import DataSet from '../../constants/DataSetConstants';

const organizationId = getCurrentOrganizationId();
/**
 * 用户可购买记录信息 DataSet
 */

export default () => ({
  primaryKey: 'serverAvailableId',
  autoQuery: true,
  pageSize: 10,
  name: DataSet.ServerAvailable,
  selection: false,
  fields: [
    {
      name: 'serverAvailableId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'userPurchaseId',
      type: 'string',
      order: 'desc',
    },
    {
      name: 'statusCode',
      type: 'string',
      lookupCode: AVAILABLE_STATUS,
      label: intl.get('hitf.purchase.model.serverAvailable.statusCode').d('状态'),
    },
    {
      name: 'serverTypeCode',
      type: 'string',
      lookupCode: PURCHASE_TYPE,
      label: intl.get('hitf.purchase.model.serverAvailable.typeCode').d('类型'),
    },
    {
      name: 'interfaceServerId',
      type: 'string',
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hitf.purchase.model.serverAvailable.serverName').d('服务名称'),
    },
    {
      name: 'interfaceId',
      type: 'string',
    },
    {
      name: 'interfaceName',
      type: 'string',
      label: intl.get('hitf.purchase.model.serverAvailable.interfaceName').d('接口名称'),
    },
    {
      name: 'remainCount',
      type: 'number',
      label: intl.get('hitf.purchase.model.serverAvailable.remainCount').d('余量'),
    },
    {
      name: 'tenantId',
      type: 'number',
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hitf.purchase.model.serverAvailable.remark').d('备注'),
    },
  ],
  queryFields: [],
  transport: {
    read: function read({ data, params }) {
      const url = isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${organizationId}user-purchases//server-available`
        : `${HZERO_HITF}/v1/user-purchases/server-available`;
      return {
        data,
        params,
        url: getUrl(url, data.userPurchaseId),
        method: 'GET',
      };
    },
  },
});

function getUrl(url, id) {
  return id ? `${url}/${id}` : `${url}`;
}
