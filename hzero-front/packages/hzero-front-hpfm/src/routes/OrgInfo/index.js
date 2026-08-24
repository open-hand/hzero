import React, { PureComponent } from 'react';
import { Divider } from 'hzero-ui';
import classNames from 'classnames';
import { connect } from 'dva';

import { Content } from 'components/Page';

import { Route, Switch, Link, Redirect } from 'dva/router';
import { getRoutes } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import style from './index.less';

@connect(({ global }) => ({
  routerData: global.routerData,
}))
@formatterCollections({ code: 'hpfm.orgInfo' })
export default class OrgInfo extends PureComponent {
  state = {
    commonSourceCode: 'SYSTEM',
    commonExternalSystemCode: 'SYSTEM',
  };

  render() {
    const { routerData, match, location } = this.props;
    const { commonSourceCode, commonExternalSystemCode } = this.state;
    const { path } = match;
    const hpfmLeftRouter = {
      '/hpfm/org-info/company': intl.get('hpfm.orgInfo.view.message.route.company').d('公司'),
      '/hpfm/org-info/operation-unit': intl
        .get('hpfm.orgInfo.view.message.route.operationUnit')
        .d('业务实体'),
      '/hpfm/org-info/inventory-org': intl
        .get('hpfm.orgInfo.view.message.route.inventoryOrg')
        .d('库存组织'),
      '/hpfm/org-info/store-room': intl.get('hpfm.orgInfo.view.message.route.storeRoom').d('库房'),
      '/hpfm/org-info/library-position': intl
        .get('hpfm.orgInfo.view.message.route.libraryPosition')
        .d('库位'),
    };
    const spfmLeftRouter = {
      '/spfm/org-info/company': intl.get('hpfm.orgInfo.view.message.route.company').d('公司'),
      '/spfm/org-info/operation-unit': intl
        .get('hpfm.orgInfo.view.message.route.operationUnit')
        .d('业务实体'),
      '/spfm/org-info/inventory-org': intl
        .get('hpfm.orgInfo.view.message.route.inventoryOrg')
        .d('库存组织'),
      '/spfm/org-info/store-room': intl.get('hpfm.orgInfo.view.message.route.storeRoom').d('库房'),
      '/spfm/org-info/library-position': intl
        .get('hpfm.orgInfo.view.message.route.libraryPosition')
        .d('库位'),
    };
    return (
      <React.Fragment>
        <Content className={style['org-info']} noCard>
          <div className={style['org-info-left']}>
            <div
              className={classNames({
                [style['orgchart-item']]: true,
                [style['orgchart-light']]:
                  location.pathname.includes('group') || location.pathname === '/hpfm/org-info',
              })}
            >
              <Link
                to={`/${path.includes('spfm') ? 'spfm' : 'hpfm'}/org-info/group`}
                className={style.link}
              >
                {intl.get('hpfm.orgInfo.view.message.route.group').d('集团')}
              </Link>
            </div>
            <Divider dashed type="vertical" className={style.divider} />
            <div className={style['divider-horizontal']} />
            <div className={style.orgchart}>
              <div style={{ width: '100px' }}>
                {Object.keys(path.includes('spfm') ? spfmLeftRouter : hpfmLeftRouter).map(
                  (item, index) => {
                    return (
                      <React.Fragment key={item}>
                        {index !== 0 && (
                          <Divider dashed type="vertical" className={style.divider} />
                        )}
                        <div
                          className={classNames({
                            [style['orgchart-item']]: true,
                            [style['orgchart-light']]: location.pathname.includes(item),
                          })}
                        >
                          <Link to={item} className={style.link}>
                            {path.includes('spfm') ? spfmLeftRouter[item] : hpfmLeftRouter[item]}
                          </Link>
                        </div>
                      </React.Fragment>
                    );
                  }
                )}
              </div>
              <div style={{ width: 100 }}>
                <div
                  className={classNames({
                    [style['orgchart-item']]: true,
                    [style['orgchart-light']]: location.pathname.includes('purchase-org'),
                  })}
                >
                  <Link
                    to={`/${path.includes('spfm') ? 'spfm' : 'hpfm'}/org-info/purchase-org`}
                    className={style.link}
                  >
                    {intl.get('hpfm.orgInfo.view.message.route.purchaseOrg').d('采购组织')}
                  </Link>
                </div>
                <Divider type="vertical" className={style.divider} />
                <div
                  className={classNames({
                    [style['orgchart-item']]: true,
                    [style['orgchart-light']]: location.pathname.includes('purchase-agent'),
                  })}
                >
                  <Link
                    to={`/${path.includes('spfm') ? 'spfm' : 'hpfm'}/org-info/purchase-agent`}
                    className={style.link}
                  >
                    {intl.get('hpfm.orgInfo.view.message.route.purchaseAgent').d('采购员')}
                  </Link>
                </div>
              </div>
            </div>
          </div>
          <div className={style['org-info-right']}>
            <Switch>
              {getRoutes(match.path, routerData).map(item => {
                const InfoComp = item.component;
                return (
                  <Route
                    key={item.key}
                    path={item.path}
                    render={props => (
                      <InfoComp
                        {...props}
                        key={item.key}
                        commonSourceCode={commonSourceCode}
                        commonExternalSystemCode={commonExternalSystemCode}
                      />
                    )}
                    exact={item.exact}
                  />
                );
              })}
              <Redirect
                form={`/${path.includes('spfm') ? 'spfm' : 'hpfm'}/org-info`}
                to={`/${path.includes('spfm') ? 'spfm' : 'hpfm'}/org-info/group`}
              />
            </Switch>
          </div>
        </Content>
      </React.Fragment>
    );
  }
}
