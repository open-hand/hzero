/**
 * HeaderButtons - 告警规则-按钮
 * @date: 2020-3-26
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { observer, useComputed } from 'mobx-react-lite';
import { Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';

export const HeaderButtons = observer((props) => {
  const isDisabled = useComputed(() => {
    return props.dataSet.selected.length === 0;
  });

  /**
   * 删除规则
   */
  const handleDelete = async () => {
    const { dataSet } = props;
    const res = await dataSet.delete(dataSet.selected);
    if (res && res.success) {
      dataSet.query();
    }
  };

  return (
    <Header title={intl.get('halt.alertRule.view.title.alertRule').d('告警规则配置')}>
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${props.path}/create`,
            type: 'button',
            meaning: '告警规则配置-新建',
          },
        ]}
        icon="add"
        color="primary"
        onClick={() => props.onCreate(false)}
      >
        {intl.get('hzero.common.button.create').d('新建')}
      </ButtonPermission>
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${props.path}/delete`,
            type: 'button',
            meaning: '告警规则配置-删除',
          },
        ]}
        icon="delete"
        disabled={isDisabled}
        onClick={handleDelete}
      >
        {intl.get('hzero.common.button.delete').d('删除')}
      </ButtonPermission>
    </Header>
  );
});
