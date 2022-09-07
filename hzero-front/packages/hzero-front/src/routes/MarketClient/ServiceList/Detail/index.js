import React, { useEffect, useState } from 'react';
import { connect } from 'dva';
import { Table } from 'hzero-ui';
import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import ServiceItemList from '../components/ServiceItemList';
import { queryServiceDetail } from '../services';

const BACK_TO_SERVICE_LIST_URL = '/market-client/services-list';

function ComponentsDetail({ match }) {
  const { serviceId } = match.params;
  const [serviceDetail, setServiceDetail] = useState({});
  const [serviceList, setServiceList] = useState([]);
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    init();
  }, [serviceId]);

  // 初始化方法
  const init = () => {
    if (!serviceId) return;

    setLoading(true);
    queryServiceDetail(serviceId).then((res) => {
      setLoading(false);
      if (!res) return;

      const { servers = [] } = res;
      setServiceList(servers);
      setServiceDetail(res);
    });
  };

  const columns = [
    {
      title: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
      width: 250,
      dataIndex: 'serviceName',
      render: () => serviceId || '-',
    },
    {
      title: 'GroupId',
      width: 250,
      dataIndex: 'groupId',
    },
    {
      title: 'ArtifactId',
      dataIndex: 'artifactId',
    },
    {
      title: intl.get('hzero.common.components.dataAudit.version').d('版本'),
      width: 200,
      dataIndex: 'version',
    },
  ];
  const tableProps = {
    loading,
    indentSize: 0,
    bordered: true,
    rowKey: 'serviceId',
    dataSource: serviceDetail?.components,
    pagination: false,
    columns,
  };

  return (
    <>
      <Header title="组件详情" backPath={BACK_TO_SERVICE_LIST_URL} />
      <Content>
        <ServiceItemList serviceId={serviceId} serviceList={serviceList} />
        <Table {...tableProps} />
      </Content>
    </>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient', 'hadm.common'],
})(connect()(ComponentsDetail));
