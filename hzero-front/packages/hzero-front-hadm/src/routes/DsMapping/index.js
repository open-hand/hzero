/**
 * 服务数据源映射
 * @since 2020-5-6
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import Drawer from './Drawer';
import { tableDs as TableDs, detailDs as DetailDs } from '../../stores/DsMappingDS';

const DsMapping = ({ match: { path } }) => {
  const tableDs = React.useMemo(() => new DataSet(TableDs()), []);

  const detailDs = React.useMemo(() => new DataSet(DetailDs()), []);

  const columns = React.useMemo(
    () => [
      { name: 'serviceCode', width: 220 },
      { name: 'serviceVersion', width: 220 },
      { name: 'dsUrl' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
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
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '服务数据源映射-编辑',
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
                <span className="action-link">
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.target`,
                        type: 'button',
                        meaning: '服务数据源映射-删除',
                      },
                    ]}
                    onClick={() => handleDelete(record)}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  const handleEdit = (isEdit, record) => {
    detailDs.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    Modal.open({
      drawer: true,
      key: 'dsMapping',
      destroyOnClose: true,
      closable: true,
      title,
      children: <Drawer currentEditData={currentEditData} isEdit={isEdit} detailDs={detailDs} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: handleOk,
      onCancel: () => {
        detailDs.removeAll();
      },
      onClose: () => {
        detailDs.removeAll();
      },
    });
  };

  const handleOk = async () => {
    try {
      const validate = await detailDs.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    tableDs.query();
  };

  const handleDelete = async (record) => {
    await tableDs.delete(record);
  };

  return (
    <>
      <Header title={intl.get('hadm.dsMapping.view.message.title.dsMapping').d('服务数据源映射')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/create`,
              type: 'button',
              meaning: '服务数据源映射-新建',
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
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.dsMapping', 'hadm.common'] })(DsMapping);
