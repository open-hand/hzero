/*
 * @Description: 顶部删除按钮
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-24 11:55:54
 * @Copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { observer, useComputed } from 'mobx-react-lite';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { Header } from 'components/Page';

export const HeaderButtons = observer((props) => {
  const {
    activeKey,
    SilentRulesDS,
    InhibitionRulesDS,
    onCreateInhibitionRules,
    onCreateSilentRules,
    onCreateRouterConfig,
  } = props;

  const isSilentRulesDisabled = useComputed(() => {
    return SilentRulesDS.selected.length === 0;
  });

  const isInhibitionRulesDisabled = useComputed(() => {
    return InhibitionRulesDS.selected.length === 0;
  });

  /**
   * 删除静默
   */
  const handleSilentRulesDelete = () => {
    SilentRulesDS.delete(SilentRulesDS.selected);
  };

  /**
   * 删除抑制
   */
  const handleInhibitionRulesDelete = () => {
    InhibitionRulesDS.delete(InhibitionRulesDS.selected);
  };

  const getButtons = () => {
    switch (activeKey) {
      case 'SilentRules':
        return (
          <React.Fragment>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${props.path}/create`,
                  type: 'button',
                  meaning: '告警静默规则配置-新建',
                },
              ]}
              icon="add"
              color="primary"
              onClick={() => onCreateSilentRules()}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${props.path}/create`,
                  type: 'button',
                  meaning: '告警静默规则配置-删除',
                },
              ]}
              icon="delete"
              disabled={isSilentRulesDisabled}
              onClick={handleSilentRulesDelete}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </React.Fragment>
        );
      case 'InhibitionRules':
        return (
          <React.Fragment>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${props.path}/create`,
                  type: 'button',
                  meaning: '告警抑制规则配置-新建',
                },
              ]}
              icon="add"
              color="primary"
              onClick={() => onCreateInhibitionRules()}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${props.path}/create`,
                  type: 'button',
                  meaning: '告警抑制规则配置-删除',
                },
              ]}
              icon="delete"
              disabled={isInhibitionRulesDisabled}
              onClick={handleInhibitionRulesDelete}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </React.Fragment>
        );
      default:
        return (
          <React.Fragment>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${props.path}/create`,
                  type: 'button',
                  meaning: '告警路由规则配置-新建',
                },
              ]}
              icon="add"
              color="primary"
              onClick={() => onCreateRouterConfig()}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
          </React.Fragment>
        );
    }
  };

  return (
    <Header title={intl.get('halt.alertAdvanced.view.title.alertAdvanced').d('告警高级配置')}>
      {getButtons()}
    </Header>
  );
});
