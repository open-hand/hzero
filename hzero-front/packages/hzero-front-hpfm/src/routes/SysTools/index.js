/**
 * SysTools - 系统工具
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/20
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Icon } from 'choerodon-ui';
import { Spin, Tabs } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import RefreshCache from './RefreshCache';
import RefreshPermission from './RefreshPermission';
import PasswordPolicy from './PasswordPolicy';

import styles from './index.less';

const SysTools = ({
  pageLoading = false, // 页面loading组合
  match, // react-router 注入的信息
}) => {
  const [activeTabKey, setActiveTabKey] = React.useState('refreshPermission');
  return (
    <>
      <Header title={intl.get('hpfm.sysTools.view.message.title').d('系统工具')} />
      <Content className={styles['sys-tools']}>
        <Spin spinning={pageLoading}>
          <Tabs
            activeKey={activeTabKey}
            onChange={setActiveTabKey}
            animated={false}
            tabPosition="left"
          >
            <Tabs.TabPane
              key="refreshPermission"
              tab={
                <React.Fragment>
                  <div className={styles['sys-tools-tab-refresh-permission']}>
                    <Icon type="lock" className={styles['sys-tools-tab-refresh-permission-icon']} />
                    <span>
                      {intl.get('hpfm.sysTools.view.message.title.refreshPermission').d('刷新权限')}
                    </span>
                  </div>
                </React.Fragment>
              }
            >
              <RefreshPermission match={match} />
            </Tabs.TabPane>
            <Tabs.TabPane
              key="refreshCache"
              tab={
                <React.Fragment>
                  <div className={styles['sys-tools-tab-refresh-cache']}>
                    <Icon type="cached" className={styles['sys-tools-tab-refresh-cache-icon']} />
                    <span>
                      {intl.get('hpfm.sysTools.view.message.title.refreshCache').d('刷新缓存')}
                    </span>
                  </div>
                </React.Fragment>
              }
            >
              <RefreshCache match={match} />
            </Tabs.TabPane>
            <Tabs.TabPane
              key="passwordPolicy"
              tab={
                <React.Fragment>
                  <div className={styles['sys-tools-tab-password-policy']}>
                    <Icon
                      type="password"
                      className={styles['sys-tools-tab-password-policy-icon']}
                    />
                    <span>
                      {intl.get('hpfm.sysTools.view.message.title.passwordPolicy').d('密码工具')}
                    </span>
                  </div>
                </React.Fragment>
              }
            >
              <PasswordPolicy match={match} />
            </Tabs.TabPane>
          </Tabs>
        </Spin>
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hpfm.sysTools'] })(SysTools);
