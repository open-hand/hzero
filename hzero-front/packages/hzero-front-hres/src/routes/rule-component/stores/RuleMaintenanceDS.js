/**
 * rule-component规则组件规则维护 DadaSet
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/17 15:47
 * @LastEditTime: 2019/10/25 11:02
 * @Copyright: Copyright (c) 2018, Hand
 */

export default () => {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'id',
        type: 'string',
      },
      {
        name: 'tenantId',
        type: 'string',
      },
      {
        name: 'ruleCode',
        type: 'string',
      },
      {
        name: 'ruleComponentName',
        type: 'string',
      },
    ],
  };
};
