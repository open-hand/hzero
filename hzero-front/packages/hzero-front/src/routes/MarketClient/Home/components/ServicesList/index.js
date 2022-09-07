import React from 'react';
import { Table } from 'hzero-ui';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import styles from './index.less';

const ServiceList = ({ serviceList }) => {
  const renderVersion = (_, record) => {
    const { servers, components } = record;
    if (Array.isArray(servers) && servers.length > 0) {
      return servers[0].version;
    } else if (Array.isArray(components) && components.length > 0) {
      return components[0].version;
    } else {
      return '';
    }
  };

  const columns = [
    {
      title: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
      dataIndex: 'serviceName',
      key: 'serviceName',
    },
    {
      title: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
      dataIndex: 'serviceId',
      key: 'serviceId',
    },
    {
      title: intl.get('hadm.marketclient.view.home.currentVersion').d('当前版本'),
      dataIndex: 'version',
      key: 'version',
      render: renderVersion,
    },
  ];

  return (
    <div className={styles.wrapper}>
      <Table columns={columns} dataSource={serviceList} pagination={false} />
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient', 'hadm.common'],
})(ServiceList);
