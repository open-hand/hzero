import React, { useState } from 'react';
import { Link } from 'dva/router';
import { getResponse } from 'utils/utils';
import { HZERO_ADM } from 'utils/config';
import { CLIENT_JUMP_URL } from 'utils/market-client';
import { downloadFile } from 'hzero-front/lib/services/api';
import { Modal, Table, Icon, Dropdown, Menu } from 'hzero-ui';
import { queryServiceVersionList } from '../../services';

export default function VersionModal({ serviceData = {}, handleCancel = () => {} }) {
  const [loading, setLoading] = useState({});
  const [downloadLoading, setDownloadLoading] = useState(false);
  const [showMenu, setShowMenu] = useState({});
  const [versionList, setVersionList] = useState([]);

  const handleClickCompare = (record) => {
    document.body.onclick = () => setShowMenu({});
    setLoading(record);
    const { groupId, artifactId } = record;
    queryServiceVersionList({ groupId, artifactId }).then((res) => {
      setLoading({});
      if (getResponse(res)) {
        setVersionList(res); // 把查询回来的版本回写到页面中
        setShowMenu(record);
      }
    });
  };

  // 判断是否是下载模式
  const isDownload = () => {
    return serviceData?.type === 'download';
  };

  // 处理离线下载包
  const handleOfflineDownload = (record) => {
    const { version, groupId, artifactId } = record;
    setDownloadLoading(true);

    const queryParams = [
      { name: 'version', value: version },
      { name: 'groupId', value: groupId },
      { name: 'artifactId', value: artifactId },
    ];
    downloadFile({
      requestUrl: `${HZERO_ADM}/v1/market/server/download/${serviceData.serviceId}`,
      queryParams,
    }).then(() => {
      setTimeout(() => {
        setDownloadLoading(false);
        handleCancel();
      }, 800);
    });
  };

  // 渲染菜单每一项
  const renderMenuItem = () => {
    if (!Array.isArray(versionList) || !versionList.length) {
      return <Menu.Item>暂无内容</Menu.Item>;
    }

    return versionList.map((versionItem) => {
      return (
        <Menu.Item>
          <Link
            to={`/market-client/compare/${serviceData.serviceId}/${versionItem.version}__${versionItem.artifactId}`}
          >
            <div style={{ width: '100px' }}>{versionItem.version}</div>
          </Link>
        </Menu.Item>
      );
    });
  };

  const columns = [
    {
      title: 'GroupId',
      dataIndex: 'groupId',
    },
    {
      title: 'ArtifactId',
      dataIndex: 'artifactId',
    },
    {
      title: '当前版本',
      dataIndex: 'version',
    },
    {
      title: '操作',
      dataIndex: 'operation',
      width: 200,
      align: 'center',
      render: (text, record) => {
        const { groupId, artifactId } = record;
        return isDownload() ? (
          <a onClick={() => handleOfflineDownload(record)}>下载离线包</a>
        ) : (
          <Dropdown
            trigger="click"
            visible={groupId === showMenu.groupId && artifactId === showMenu.artifactId}
            overlay={<Menu>{renderMenuItem()}</Menu>}
            onClick={() => handleClickCompare(record)}
          >
            <span style={{ display: 'inline-block', width: '50px' }}>
              {groupId === loading.groupId && artifactId === loading.artifactId ? (
                <Icon type="loading" />
              ) : (
                <a>版本对比</a>
              )}
            </span>
          </Dropdown>
        );
      },
    },
  ];

  return (
    <Modal
      title="对比版本选择"
      width={807}
      onCancel={() => {
        document.body.onclick = null;
        if (handleCancel) handleCancel();
      }}
      visible={!!(serviceData && Object.keys(serviceData).length)}
      footer={null}
    >
      {isDownload() ? (
        <div
          style={{
            background: '#FAFAFA',
            boxSizing: 'border-box',
            padding: '8px',
            marginBottom: '24px',
          }}
        >
          <div style={{ fontSize: '14px', color: '#999' }}>
            <span>对比地址：</span>
            <a
              href={`${CLIENT_JUMP_URL}/my-product/my-authorize/version-compare`}
              rel="noopener noreferrer"
              target="_blank"
            >
              {`${CLIENT_JUMP_URL}/my-product/my-authorize/version-compare`}
            </a>
          </div>
          <div
            style={{
              fontSize: '12px',
              color: '#9B9B9B',
              textAlign: 'left',
              lineHeight: '18px',
            }}
          >
            注：下载本地服务数据文件后，前往该地址上传该服务数据，即可将本地服务版本与新版本进行对比
          </div>
        </div>
      ) : null}
      <Table
        loading={downloadLoading}
        bordered
        rowKey="groupId"
        dataSource={serviceData?.servers}
        columns={columns}
      />
    </Modal>
  );
}
