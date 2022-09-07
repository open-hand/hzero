/*
 * @Descripttion:
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-04-12 16:13:23
 * @Copyright: Copyright (c) 2020, Hand
 */
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { API_PREFIX } from '@/utils/constants';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

const consumeDS = () => ({
  multiple: true,
  transport: {
    read: ({ data, params }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-messages`,
        params: { ...params, ...data },
        method: 'get',
      };
    },
  },
  fields: [
    {
      name: 'processHost',
      label: intl.get('hevt.eventMessage.model.eventMessage.processHost').d('实例'),
      type: 'string',
    },
    {
      name: 'eventName',
      label: intl.get('hevt.eventMessage.model.eventMessage.eventName').d('事件'),
      type: 'string',
    },
    {
      name: 'categoryName',
      type: 'string',
      label: intl.get('hevt.eventMessage.model.eventMessage.categoryName').d('事件类型'),
    },
    {
      name: 'processStatus',
      label: intl.get('hevt.eventMessage.model.eventMessage.processStatus').d('状态'),
      type: 'string',
      lookupCode: 'HEVT.CONSUM_DTL_STATUS',
    },
    {
      name: 'processTime',
      label: intl.get('hevt.eventMessage.model.eventMessage.processTime').d('处理时间'),
      type: 'string',
    },
    {
      name: 'remark',
      label: intl.get('hevt.eventMessage.model.eventMessage.remark').d('详情'),
      type: 'string',
    },
  ],
});

export { consumeDS };
