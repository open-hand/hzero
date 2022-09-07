/*
 * DataDefine 数据流定义
 * @date: 2020-07-15
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo, useEffect } from 'react';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { tableDS, drawerDS, configDS } from '../../stores/DataDefineDS';
import Drawer from './Drawer';

const DataDefine = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);
  const drawerDs = useMemo(() => new DataSet(drawerDS()), []);
  const configDs = useMemo(() => new DataSet(configDS()), []);

  useEffect(() => {
    tableDs.query();
  }, []);

  const columns = useMemo(
    () => [
      { name: 'streamCode', width: 200 },
      { name: 'streamPurposeCodeMeaning', width: 200 },
      { name: 'streamTypeCodeMeaning', width: 200 },
      {
        name: 'configValue',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
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
                      meaning: '数据流定义-编辑',
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
                      meaning: '数据流定义-删除',
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
          return operatorRender(operators, record, { limit: 3 });
        },
        lock: 'right',
      },
    ],
    []
  );

  // 新建或编辑
  const handleEdit = (isEdit, record) => {
    const streamId = isEdit ? record.get('streamId') : undefined;
    Modal.open({
      title: isEdit
        ? intl.get('hdsc.dataDefine.view.title.edit').d('编辑')
        : intl.get('hdsc.dataDefine.view.title.create').d('新建'),
      drawer: true,
      width: 700,
      style: { width: 700 },
      children: (
        <Drawer drawerDs={drawerDs} configDs={configDs} isEdit={isEdit} streamId={streamId} />
      ),
      onOk: async () => {
        const flag = await drawerDs.validate();
        if (flag) {
          const obj = {};
          configDs.toData().forEach((item) => {
            obj[item.key] = item.value;
          });
          const str = JSON.stringify(obj);
          drawerDs.current.set('configValue', str);
          await drawerDs.submit();
          tableDs.query();
        }
        return flag;
      },
      afterClose: () => {
        configDs.reset();
        drawerDs.reset();
      },
    });
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  return (
    <>
      <Header title={intl.get('hdsc.data.view.title.service').d('数据流定义')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}-create`,
              type: 'button',
              meaning: '数据流定义-新建',
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
        <Table dataSet={tableDs} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hdsc.dataDefine'] })(DataDefine);
