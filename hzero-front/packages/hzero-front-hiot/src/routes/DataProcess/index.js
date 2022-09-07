/**
 * 数据处理 - 列表页
 * @date: 2020-07-20
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

// import Drawer from './Drawer';
import { tableDS, enableDS } from '../../stores/DataProcessDS';

const DataProcess = (props) => {
  const {
    match: { path },
  } = props;
  const tableDs = useMemo(() => new DataSet(tableDS()), []);
  const enableDs = useMemo(() => new DataSet(enableDS()), []);

  const columns = useMemo(
    () => [
      { name: 'dataProcessCode' },
      { name: 'dataProcessName' },
      { name: 'remarks' },
      {
        name: 'enabledFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'center',
        width: 100,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '数据处理-编辑',
                  },
                ]}
                onClick={() => {
                  handleEdit(true, record);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          if (record.get('enabledFlag') === 1) {
            operators.push({
              key: 'disable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disable`,
                      type: 'button',
                      meaning: '数据处理-禁用',
                    },
                  ]}
                  onClick={() => {
                    handleEnabled(record, false);
                  }}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            });
          } else {
            operators.push({
              key: 'enabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enabled`,
                      type: 'button',
                      meaning: '数据处理-启用',
                    },
                  ]}
                  onClick={() => {
                    handleEnabled(record, true);
                  }}
                >
                  {intl.get('hzero.common.status.enable').d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.status.enable').d('启用'),
            });
          }
          operators.push({
            key: 'delete',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.delete`,
                    type: 'button',
                    meaning: '数据处理-删除',
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
          });
          return operatorRender(operators);
        },
      },
    ],
    []
  );

  // 新建或编辑
  const handleEdit = (isEdit, record) => {
    const { history } = props;
    if (isEdit) {
      history.push({
        pathname: '/hiot/data-process/detail/edit',
        search: isEdit ? `id=${record.get('dataProcessId')}` : '',
      });
    } else {
      history.push('/hiot/data-process/detail/create');
    }
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  // 启用/禁用
  const handleEnabled = async (record, isEnable) => {
    const data = record.toData();
    enableDs.isEnable = isEnable;
    enableDs.create(data, 0);
    try {
      const validate = await enableDs.submit();
      if (validate) {
        tableDs.query();
      }
    } catch (e) {
      // return e
    }
  };

  return (
    <>
      <Header title={intl.get('hiot.dataProcess.view.title.DataProcess').d('数据处理规则')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}.button.create`,
              type: 'button',
              meaning: '数据处理-新建',
            },
          ]}
          icon="add"
          color="primary"
          onClick={() => handleEdit(false)}
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

export default formatterCollections({ code: ['hiot.dataProcess', 'hiot.common'] })(DataProcess);
