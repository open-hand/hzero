/**
 * 报文模板管理 - 列表页
 * @date: 2020-7-6
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { Modal, Table, DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import { tableDS, testDrawDS } from '@/stores/MessageTemplateDS';
import Drawer from './Drawer';

const MessageTemplate = ({ history, match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);

  let testDrawDs;
  const columns = useMemo(
    () => [
      { name: 'templateCode' },
      { name: 'templateName' },
      {
        name: 'templateTypeCode',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 180,
        renderer: ({ record }) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '报文模板-编辑',
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
              key: 'test',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.test`,
                      type: 'button',
                      meaning: '报文模板-测试',
                    },
                  ]}
                  onClick={() => {
                    handleTest(record);
                  }}
                >
                  {intl.get('hiot.messageTemplate.view.button.test').d('测试')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hiot.messageTemplate.view.button.test').d('测试'),
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
                      meaning: '报文模板-删除',
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
            },
          ];
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  // 新建/编辑
  const handleEdit = (isEdit, record) => {
    if (isEdit) {
      history.push({
        pathname: '/hiot/message-template/detail/edit',
        search: isEdit ? `id=${record.get('templateId')}` : '',
      });
    } else {
      history.push('/hiot/message-template/detail/create');
    }
  };

  // 测试
  const handleTest = (record) => {
    const currentEditData = record && record.toData();
    testDrawDs = new DataSet(testDrawDS());
    testDrawDs.create({}, 0);
    Modal.open({
      drawer: true,
      key: 'detail',
      destroyOnClose: true,
      closable: true,
      style: { width: 650 },
      title: intl.get('hiot.messageTemplate.view.button.test').d('测试'),
      children: <Drawer currentEditData={currentEditData} testDrawDs={testDrawDs} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  return (
    <>
      <Header title={intl.get('hiot.messageTemplate.view.message.MessageTemplate').d('报文模板')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}.button.create`,
              type: 'button',
              meaning: '报文模板-新建',
            },
          ]}
          color="primary"
          icon="add"
          onClick={() => handleEdit(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table queryFieldsLimit={3} dataSet={tableDs} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hiot.messageTemplate', 'hiot.common'] })(
  MessageTemplate
);
