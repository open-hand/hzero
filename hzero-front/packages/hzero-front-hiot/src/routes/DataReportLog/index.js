/**
 * 数据上报日志监控 - 列表页
 * @date: 2020-5-20
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { Badge } from 'choerodon-ui';
import { Modal, Table, DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import { tableDS } from '@/stores/DataReportLogDS';
import Drawer from './Drawer';

const DataReportLog = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);

  const columns = useMemo(
    () => [
      { name: 'serviceInstIp', width: 200 },
      { name: 'topicName' },
      {
        name: 'deviceName',
      },
      {
        name: 'deviceCode',
      },
      {
        name: 'processStatus',
        width: 100,
        align: 'center',
        renderer: ({ value }) => (
          <Badge
            status={value === '1' ? 'success' : 'error'}
            text={
              value === '1'
                ? intl.get('hzero.common.status.success').d('成功')
                : intl.get('hzero.common.status.error').d('失败')
            }
          />
        ),
      },
      { name: 'creationDate' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.detail`,
                    type: 'button',
                    meaning: '指令下发日志监控-详情',
                  },
                ]}
                onClick={() => {
                  handleDetail(record);
                }}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  // 查看详情
  const handleDetail = (record) => {
    const currentEditData = record && record.toData();
    Modal.open({
      drawer: true,
      key: 'detail',
      destroyOnClose: true,
      closable: true,
      style: { width: 650 },
      title: intl.get('hzero.common.status.detail').d('查看详情'),
      children: <Drawer currentEditData={currentEditData} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };

  return (
    <>
      <Header
        title={intl.get('hiot.dataReport.view.message.DataReportLog').d('数据上报日志监控')}
      />
      <Content>
        <Table queryFieldsLimit={3} dataSet={tableDs} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hiot.dataReport', 'hiot.common'] })(DataReportLog);
