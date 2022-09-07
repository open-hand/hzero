/**
 * rule-component规则组件头 DadaSet
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/28 9:47
 * @LastEditTime: 2019/10/28 9:58
 * @Copyright: Copyright (c) 2018, Hand
 */
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { getNodeConfig } from '@/utils/saveNode';

function submitRequest({ dataSet, data }) {
  const nodeInfo = getNodeConfig(
    { ...data[0], id: dataSet.current.get('id') },
    'rule',
    'ruleComponentName'
  );
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component/submit`,
    data: { ruleComponentList: data, processNode: nodeInfo },
  };
}

export default () => ({
  autoCreate: false,
  transport: {
    submit: submitRequest,
  },
  fields: [
    {
      name: 'ruleCode',
      type: 'string',
    },
    {
      name: 'ruleComponentName',
      type: 'object',
      label: intl.get('hres.ruleComponent.model.ruleCmp.ruleCmpName').d('规则组件名称'),
      required: true,
      pattern: /[\u4e00-\u9fa5]|[a-z0-9A-Z\-_]/,
      lovCode: 'HSCS_AE_EVENT_BATCH_NAME',
    },
  ],
});
