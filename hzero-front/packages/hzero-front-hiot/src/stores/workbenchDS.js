/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HIOT } from '@/utils/constants';

const orgId = getCurrentOrganizationId();

const workbenchTreeDS = () => ({
  paging: false,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/guide/thing-group-tree`,
      method: 'get',
    }),
  },
});

const deviceTrendDS = () => ({
  paging: false,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/guide/thing-trend`,
      method: 'get',
    }),
  },
});

const warnEventDS = () => ({
  paging: false,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/guide/alert-trend`,
      method: 'get',
    }),
  },
});

const deviceCardsDS = () => ({
  pageSize: 9,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/guide/thing-card`,
      method: 'get',
    }),
  },
});

export {
  workbenchTreeDS, // 导览工作台层级树
  deviceTrendDS, // 设备、网关在线趋势
  warnEventDS, // 告警事件趋势
  deviceCardsDS, // 设备卡片
};
