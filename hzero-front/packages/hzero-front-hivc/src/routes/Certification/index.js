/* eslint-disable no-nested-ternary */
import React from 'react';

import { DataSet, Table } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender } from 'utils/renderer';

import { tableDS as tableDs } from '../../stores/CertificationDS';

const Certification = (props) => {
  const {
    match: { path },
    history,
  } = props;

  const tableDS = React.useMemo(() => new DataSet(tableDs()), []);

  const columns = React.useMemo(
    () =>
      [
        {
          name: 'buyerNo',
        },
        {
          name: 'batchNo',
          width: 300,
        },
        {
          name: 'authState',
          width: 180,
          renderer: ({ value }) => {
            return (
              <Tag color={value ? 'green' : 'red'}>
                {value
                  ? intl.get('hivc.certification.status.confirm').d('已确认签名')
                  : intl.get('hivc.certification.status.unConfirm').d('未确认签名')}
              </Tag>
            );
          },
        },
        {
          name: 'authPeriod',
          width: 180,
        },
        {
          name: 'statisticsCompleteTime',
          width: 180,
        },
        {
          name: 'confirmCompleteTime',
          width: 180,
        },
        {
          name: 'requestStatus',
          width: 150,
          align: 'center',
          renderer: ({ value }) => {
            return (
              <Tag color={value === -1 ? 'red' : value === 0 ? 'blue' : 'green'}>
                {value === -1
                  ? intl.get('hzero.common.status.error').d('失败')
                  : value === 0
                  ? intl.get('hzero.common.status.pending').d('请求中')
                  : intl.get('hzero.common.status.success').d('成功')}
              </Tag>
            );
          },
        },
        {
          name: 'resultMessage',
          width: 180,
        },
        {
          header: intl.get('hivc.certification.button.action').d('统计结果'),
          width: 100,
          lock: 'right',
          renderer: ({ record }) => {
            const actions = [
              {
                key: 'view',
                title: '',
                len: 2,
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.view`,
                        type: 'button',
                        meaning: '发票接口类型-查看',
                      },
                    ]}
                    onClick={() => handleView(record)}
                    disabled={record.get('requestStatus') === -1}
                  >
                    {intl.get('hzero.common.button.view').d('查看')}
                  </ButtonPermission>
                ),
              },
            ];
            return operatorRender(actions);
          },
        },
      ].filter(Boolean),
    []
  );

  const handleView = (record) => {
    history.push(`/hivc/certification/detail/${record.get('id')}`);
  };

  return (
    <>
      <Header
        title={intl.get('hivc.certification.view.message.title.certification').d('发票认证结果')}
      >
        {/*  */}
      </Header>
      <Content>
        <Table dataSet={tableDS} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hivc.certification'],
})(Certification);
