/*
 * LogicalDataSourceGroup 逻辑数据源组管理
 * @date: 2020-05-06
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import { tableDS } from '../../stores/LogicalDataSourceGroupDS';

const LogicalDataSourceGroup = (props) => {
  const {
    history,
    match: { path },
  } = props;
  const tableDs = useMemo(() => new DataSet(tableDS()), []);

  const columns = useMemo(
    () => [
      { name: 'datasourceGroupName' },
      {
        name: 'description',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        lock: 'right',
        renderer: ({ record }) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '逻辑数据源组管理-编辑',
                    },
                  ]}
                  onClick={() => {
                    handleToDetail(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '逻辑数据源组管理-删除',
                    },
                  ]}
                  onClick={() => {
                    handleDelete(record);
                  }}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators);
        },
      },
    ],
    []
  );

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  const handleToDetail = (isEdit, record) => {
    history.push({
      pathname: `/hadm/logical-data-source-group/detail/${
        isEdit ? record.get('datasourceGroupId') : 0
      }`,
      search: `type=${isEdit ? 'edit' : 'create'}`,
    });
  };

  return (
    <>
      <Header
        title={intl
          .get('hadm.LogicalDataSource.view.message.title.LogicalDSGroup')
          .d('逻辑数据源组')}
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}.button.create`,
              type: 'button',
              meaning: 'WebHook配置-新建',
            },
          ]}
          icon="add"
          color="primary"
          onClick={() => handleToDetail(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={2} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.LogicalDataSource'] })(LogicalDataSourceGroup);
