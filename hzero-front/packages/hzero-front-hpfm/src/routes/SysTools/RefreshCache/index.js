/* eslint-disable react/jsx-props-no-spreading */
/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/20
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Card, Popconfirm } from 'choerodon-ui';
import { Col, Row } from 'choerodon-ui/pro';
import axios from 'axios';
import { isUndefined } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME, EDIT_FORM_ROW_LAYOUT, FORM_COL_3_LAYOUT } from 'utils/constants';

import { sysToolsCacheAxiosConfig } from '@/stores/sysToolsDS';

import { btnStyle } from '../utils';

function buildRefreshCacheLoadingName(module, code) {
  return `refresh-cache-${module}-${code}-loading`;
}

const RefreshCache = ({ match }) => {
  const refreshCacheAxionsConfig = React.useMemo(() => sysToolsCacheAxiosConfig(), []);
  const [loading, setLoading] = React.useState({});
  // make sure loadingProxy is always same, so loading is always newest
  const loadingProxy = React.useMemo(() => ({}), []);
  loadingProxy.loading = loading;
  const refreshCacheProxyCall = React.useCallback((module, code) => {
    const newLoading = {
      ...loadingProxy.loading,
      [buildRefreshCacheLoadingName(module, code)]: true,
    };
    setLoading(newLoading);
    if (refreshCacheAxionsConfig[module] && refreshCacheAxionsConfig[module][code]) {
      axios(refreshCacheAxionsConfig[module][code]())
        .then((res) => {
          if (isUndefined(res)) {
            setLoading({
              ...loadingProxy.loading,
              [buildRefreshCacheLoadingName(module, code)]: false,
            });
            notification.success();
          } else {
            // call error, let always loading
            // maybe axios config had notification
            setLoading({
              ...loadingProxy.loading,
              [buildRefreshCacheLoadingName(module, code)]: false,
            });
            notification.error();
          }
        })
        .catch((e) => {
          setLoading({
            ...loadingProxy.loading,
            [buildRefreshCacheLoadingName(module, code)]: false,
          });
          if (e && e.message) {
            notification.error({ message: e.message });
          }
        });
    } else {
      notification.warning({
        message: intl.get('hpfm.sysTools.view.message.no-config').d('没有配置'),
        description: intl
          .get('hpfm.sysTools.view.message.no-config-detail', { module, code })
          .d(`没有找到对应 module: ${module},code: ${code} 的配置`),
      });
      // no config for module, code
      setLoading({
        ...loadingProxy.loading,
        [buildRefreshCacheLoadingName(module, code)]: false,
      });
    }
  }, []);
  return (
    <>
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={<h3>{intl.get('hpfm.sysTools.view.title.refreshCache.hpfm').d('平台服务')}</h3>}
      >
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.config')
                    .d('刷新系统配置'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.config')
                    .d('刷新系统配置')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'config');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.config`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-系统配置',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'config')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hpfm.config').d('刷新系统配置')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.prompt')
                    .d('刷新平台多语言'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.prompt')
                    .d('刷新平台多语言')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'prompt');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.prompt`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-平台多语言',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'prompt')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hpfm.prompt').d('刷新平台多语言')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.permission-range')
                    .d('刷新数据权限'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.permission-range')
                    .d('刷新数据权限')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'permission-range');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.permission-range`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-数据权限',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'permission-range')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hpfm.permission-range')
                  .d('刷新数据权限')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.profile')
                    .d('刷新配置维护'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.profile')
                    .d('刷新配置维护')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'profile');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.profile`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-配置维护',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'profile')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hpfm.profile').d('刷新配置维护')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.code-rule')
                    .d('刷新编码规则'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.code-rule')
                    .d('刷新编码规则')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'code-rule');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.code-rule`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-编码规则',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'code-rule')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hpfm.code-rule')
                  .d('刷新编码规则')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.entityTable')
                    .d('刷新实体信息'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.entityTable')
                    .d('刷新实体信息')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'entity-table');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.entityTable`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-刷新实体信息',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'entity-table')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hpfm.entityTable')
                  .d('刷新实体信息')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.datasource')
                    .d('刷新数据源配置'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.datasource')
                    .d('刷新数据源配置')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'datasource');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.datasource`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-数据源配置',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'datasource')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hpfm.datasource')
                  .d('刷新数据源配置')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.customize')
                    .d('刷新个性化配置'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.customize')
                    .d('刷新个性化配置')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'customize');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.customize`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-个性化配置',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'customize')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hpfm.customize')
                  .d('刷新个性化配置')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.message')
                    .d('刷新返回消息多语言'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hpfm.message')
                    .d('刷新返回消息多语言')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hpfm', 'message');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hpfm.message`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-返回消息多语言',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hpfm', 'message')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hpfm.message')
                  .d('刷新返回消息多语言')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
        </Row>
      </Card>
      <Card
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>{intl.get('hpfm.sysTools.view.message.title.refreshCache.hiam').d('IAM服务')}</h3>
        }
      >
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.user')
                    .d('刷新用户'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.user')
                    .d('刷新用户')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'user');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.user`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-用户',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'user')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hiam.user').d('刷新用户')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.password-policy')
                    .d('刷新密码策略'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.password-policy')
                    .d('刷新密码策略')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'password-policy');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.password-policy`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-密码策略',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'password-policy')]}
              >
                {intl
                  .get('hpfm.sysTools.view.button.refreshCache.hiam.password-policy')
                  .d('刷新密码策略')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.client')
                    .d('刷新客户端'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.client')
                    .d('刷新客户端')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'client');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.client`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-客户端',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'client')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hiam.client').d('刷新客户端')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.open-app')
                    .d('刷新三方应用'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.open-app')
                    .d('刷新三方应用')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'open-app');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.open-app`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-三方应用',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'open-app')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hiam.open-app').d('刷新三方应用')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.ldap')
                    .d('刷新LDAP'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.ldap')
                    .d('刷新LDAP')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'ldap');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.ldap`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-LDAP',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'ldap')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hiam.ldap').d('刷新LDAP')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.domain')
                    .d('刷新域名配置'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.domain')
                    .d('刷新域名配置')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'domain');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.domain`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-刷新域名配置',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'domain')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hiam.domain').d('刷新域名配置')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Popconfirm
              title={intl
                .get('hpfm.sysTools.view.message.refreshCache', {
                  action: intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.doctype')
                    .d('刷新单据权限'),
                })
                .d(
                  `是否${intl
                    .get('hpfm.sysTools.view.button.refreshCache.hiam.doctype')
                    .d('刷新单据权限')}缓存`
                )}
              onConfirm={() => {
                refreshCacheProxyCall('hiam', 'doctype');
              }}
            >
              <ButtonPermission
                style={btnStyle}
                icon="sync"
                permissionList={[
                  {
                    code: `${match.path}.button.refreshCache.hiam.doctype`,
                    type: 'button',
                    meaning: '系统工具-刷新缓存-刷新单据权限',
                  },
                ]}
                loading={loading[buildRefreshCacheLoadingName('hiam', 'doctype')]}
              >
                {intl.get('hpfm.sysTools.view.button.refreshCache.hiam.doctype').d('刷新单据权限')}
              </ButtonPermission>
            </Popconfirm>
          </Col>
        </Row>
      </Card>
    </>
  );
};

export default RefreshCache;
