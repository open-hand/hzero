/*
 * @description: 系统管理中心 - 三方应用绑定表格
 * @date:
 * @author: Lynn <lin.li03@hand-china.com>
 * @copyright: Copyright (c) 2021, Hand
 */

import React, { useEffect, useState } from 'react';
import { Icon, Table, Spin } from 'hzero-ui';
import { isUndefined } from 'lodash';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

import styles from '../../index.less';
import appStyles from './index.less';

const apps = [
  {
    title: intl.get('hiam.userInfo.view.subMain.title.paas.weChatWork').d('企业微信绑定'),
    description: intl
      .get('hiam.userInfo.view.subMain.paas.weChatWorkDesc')
      .d('绑定后可用企业微信扫码登录平台'),
    key: 'WeChatWork',
  },
  {
    title: intl.get('hiam.userInfo.view.subMain.title.paas.weChat').d('微信绑定'),
    description: intl
      .get('hiam.userInfo.view.subMain.paas.weChatDesc')
      .d('绑定后可用微信扫码登录平台'),
    key: 'WeChat',
  },
  {
    title: intl.get('hiam.userInfo.view.subMain.title.paas.hippius').d('海马汇绑定'),
    description: intl
      .get('hiam.userInfo.view.subMain.paas.hippiusDesc')
      .d('绑定后可用海马汇扫码登录平台'),
    key: 'Hippius',
  },
  {
    title: intl.get('hiam.userInfo.view.subMain.title.paas.feiShu').d('飞书绑定'),
    description: intl
      .get('hiam.userInfo.view.subMain.paas.feiShuDesc')
      .d('绑定后可用飞书扫码登录平台'),
    key: 'FeiShu',
  },
];

const OpenAppTable = ({ data = [], onUnBindApp = () => {} }) => {
  const [appMap, setAppMap] = useState({});
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    const openApps = {};

    data.forEach((item) => {
      if (openApps[item.appCode]) {
        openApps[item.appCode].push(item);
      } else {
        openApps[item.appCode] = [item];
      }
    });
    setAppMap(openApps);
  }, [data]);

  const columns = [
    {
      title: intl.get('hiam.userInfo.model.subMain.paas.enterpriseName').d('绑定企业'),
      dataIndex: 'enterpriseName',
      width: 220,
      render: (text) => text || '-',
    },
    {
      title: intl.get('hiam.userInfo.model.subMain.paas.productName').d('绑定产品'),
      dataIndex: 'productName',
      render: (text) => text || '-',
      width: 220,
    },
    {
      title: intl.get('hiam.userInfo.model.subMain.paas.domainName').d('绑定域名'),
      dataIndex: 'domainName',
    },
    {
      title: intl.get('hiam.userInfo.model.subMain.paas.domainTypeMeaning').d('域名类型'),
      dataIndex: 'domainTypeMeaning',
      render: (text) => text || '-',
      width: 120,
    },
    {
      title: intl.get('hiam.userInfo.model.subMain.paas.bindingState').d('绑定状态'),
      dataIndex: 'bindingState',
      width: 120,
      render: (_, record) => {
        // 根据id判断是否已绑定，绑定的app有id
        return (
          <>
            {isUndefined(record.id) ? (
              <span className={styles['color-unbind']}>
                <Icon type="info-circle" />
                &nbsp;
                {intl.get('hiam.userInfo.view.message.unbind').d('未绑定')}
              </span>
            ) : (
              <span className={styles['color-bind']}>
                <Icon type="check-circle" />
                &nbsp;
                {intl.get('hiam.userInfo.view.message.bind').d('已绑定')}
              </span>
            )}
          </>
        );
      },
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      key: 'operator',
      fixed: 'right',
      width: 120,
      render: (_, record) => {
        const button = isUndefined(record.id)
          ? {
              ele: (
                // <a onClick={() => onBound(record)}>
                <a
                  href={`${record.domainName}/oauth2/authorization/${record.openAppId}-${
                    record.appCode
                  }?redirect_uri=${encodeURIComponent(window.location)}&page_type=one&method=bind`}
                  rel="noopener noreferrer"
                >
                  {intl.get('hiam.userInfo.button.subMain.paas.bound').d('绑定')}
                </a>
              ),
              len: 2,
              title: intl.get('hiam.userInfo.button.subMain.paas.bound').d('绑定'),
            }
          : {
              ele: (
                <a
                  onClick={() => {
                    onUnbound(record);
                  }}
                >
                  {intl.get('hiam.userInfo.button.subMain.paas.unbound').d('解除绑定')}
                </a>
              ),
              len: 4,
              title: intl.get('hiam.userInfo.button.subMain.paas.unbound').d('解除绑定'),
            };

        const actions = [button];
        return operatorRender(actions);
      },
    },
  ];

  // 解除绑定
  const onUnbound = (record) => {
    onUnBindApp(record, setLoading);
  };

  // 绑定
  // const onBound = (record) => {
  //   const { openAppId = '', appCode = '', domainName } = record;
  //   window.open(
  //     `${domainName}/oauth2/authorization/${openAppId}-${appCode}?redirect_uri=${encodeURIComponent(
  //       window.location
  //     )}&page_type=one`
  //   );
  // };
  return (
    <Spin spinning={loading}>
      {apps.map((item) => {
        const { key, title, description } = item;
        return appMap[item.key] ? (
          <div className={appStyles['open-app']} key={key}>
            <div className={appStyles['open-app-title']}>{title}</div>
            <div className={appStyles['open-app-desc']}>{description}</div>
            <Table columns={columns} dataSource={appMap[item.key]} pagination={false} />
          </div>
        ) : (
          ''
        );
      })}
    </Spin>
  );
};
export default OpenAppTable;
