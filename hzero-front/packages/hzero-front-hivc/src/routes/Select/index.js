import React from 'react';

import { DataSet, Table } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender } from 'utils/renderer';

import { tableDS as tableDs } from '../../stores/SelectDS';

const Select = (props) => {
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
          name: 'tickDate',
          width: 180,
        },
        {
          name: 'allSuccess',
          width: 150,
          renderer: ({ value }) => {
            return (
              <Tag color={value ? 'green' : 'red'}>
                {value
                  ? intl.get('hzero.common.status.success').d('成功')
                  : intl.get('hzero.common.status.error').d('失败')}
              </Tag>
            );
          },
        },
        {
          header: intl.get('hzero.common.button.action').d('操作'),
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
    history.push(`/hivc/select/detail/${record.get('id')}`);
  };

  return (
    <>
      <Header title={intl.get('hivc.select.view.message.title.select').d('发票勾选结果')}>
        {/*  */}
      </Header>
      <Content>
        <Table dataSet={tableDS} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hivc.select'],
})(Select);
