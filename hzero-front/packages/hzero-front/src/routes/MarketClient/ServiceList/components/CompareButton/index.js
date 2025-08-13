import React, { useState, useEffect } from 'react';
import { Link } from 'dva/router';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Dropdown, Icon, Menu, Tooltip } from 'hzero-ui';

function CompareButton({ menuVisibleId, record, menuList, handleVisibleChange, reachAble = true }) {
  const [versionList, setVersionList] = useState([]);
  useEffect(() => {
    if (menuVisibleId === record.serviceId) {
      setVersionList(menuList);
    } else {
      setVersionList([]);
    }
  }, [menuList, menuVisibleId]);

  // 渲染菜单每一项
  const renderMenuItem = () => {
    if (!Array.isArray(versionList) || !versionList.length) {
      return <Menu.Item>暂无内容</Menu.Item>;
    }

    return versionList.map((versionItem) => {
      return (
        <Menu.Item>
          <Link
            to={`/market-client/compare/${record.serviceId}/${versionItem.version}__${versionItem.artifactId}`}
          >
            <div style={{ width: '100px' }}>{versionItem.version}</div>
          </Link>
        </Menu.Item>
      );
    });
  };

  // 状态切换
  const _handleVisibleChange = (visible) => {
    if (!reachAble) return;

    if (handleVisibleChange) {
      handleVisibleChange(visible, record);
    }
  };

  return (
    <Dropdown
      trigger="click"
      visible={menuVisibleId === record.serviceId}
      overlay={<Menu>{renderMenuItem()}</Menu>}
      onVisibleChange={_handleVisibleChange}
    >
      {!reachAble ? (
        <Tooltip title="无网络连接，在线对比功能不可用">
          <span style={{ color: '#999', cursor: 'not-allowed' }}>
            {intl.get('hadm.marketclient.view.service.versionContrast').d('版本对比')}{' '}
            <Icon type="down" />
          </span>
        </Tooltip>
      ) : (
        <a>
          {intl.get('hadm.marketclient.view.service.versionContrast').d('版本对比')}{' '}
          <Icon type="down" />
        </a>
      )}
    </Dropdown>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient', 'hadm.common'],
})(CompareButton);
