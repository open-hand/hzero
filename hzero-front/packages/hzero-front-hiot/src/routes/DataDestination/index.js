/**
 * 数据目的地管理 - 列表页
 * @date: 2020-07-17
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import Drawer from './Drawer';
import { tableDS, drawerDS, formConfigDS } from '../../stores/DataDestinationDS';

const DataDestination = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);
  const formConfigDs = useMemo(() => new DataSet(formConfigDS()), []);

  let drawerDs = null;

  const columns = useMemo(
    () => [
      { name: 'dataSinkCode' },
      { name: 'dataSinkName' },
      { name: 'dataSinkTypeCode' },
      {
        name: 'enabledFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'left',
        width: 100,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
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
                    meaning: '数据目的地管理-编辑',
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

          return operatorRender(operators);
        },
      },
    ],
    []
  );

  // 新建或编辑
  const handleEdit = (isEdit, record) => {
    drawerDs = new DataSet(drawerDS());
    drawerDs.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    const drawerProps = {
      isEdit,
      drawerDs,
      formConfigDs,
      currentEditData,
    };
    Modal.open({
      drawer: true,
      key: 'editDrawer',
      destroyOnClose: true,
      closable: true,
      title,
      children: <Drawer {...drawerProps} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: handleOk,
      onCancel: () => {
        drawerDs.removeAll();
      },
      onClose: () => {
        drawerDs.removeAll();
      },
    });
  };

  // 保存
  const handleOk = async () => {
    const formatData = {};
    const baseInfo = drawerDs.toData()[0];
    const formConfigList = formConfigDs.toData();
    if (formConfigList.length) {
      formConfigList.forEach((item) => {
        formatData[item.itemCode] = baseInfo[item.itemCode];
      });
      drawerDs.current.set('dataSinkConfig', JSON.stringify(formatData));
    }
    try {
      const validate = await drawerDs.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    tableDs.query();
  };

  return (
    <>
      <Header
        title={intl.get('hiot.dataDestination.view.title.DataDestination').d('数据目的地管理')}
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}.button.create`,
              type: 'button',
              meaning: '数据目的地管理-新建',
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
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hiot.dataDestination'] })(DataDestination);
