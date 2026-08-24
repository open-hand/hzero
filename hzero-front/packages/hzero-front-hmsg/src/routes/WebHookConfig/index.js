/*
 * WebHookConfig WebHook配置
 * @date: 2020-04-28
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo } from 'react';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import intl from 'utils/intl';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';
import { Content, Header } from 'components/Page';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import Drawer from './Drawer';
import { tableDS, drawerDS } from '../../stores/WebHookConfigDS';

const isTenant = isTenantRoleLevel();
const tenantId = getCurrentOrganizationId();
const WebHookConfig = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);

  let drawerDs = null;

  const columns = useMemo(
    () =>
      [
        !isTenant && { name: 'tenantName', width: 180 },
        { name: 'serverCode', width: 200 },
        { name: 'serverName', width: 200 },
        { name: 'serverTypeMeaning', width: 150 },
        {
          name: 'description',
        },
        {
          name: 'enabledFlag',
          renderer: ({ value }) => enableRender(value),
          align: 'left',
          width: 100,
        },
        isTenantRoleLevel() &&
          !VERSION_IS_OP && {
            name: 'tenantId',
            width: 120,
            renderer: ({ value }) =>
              tenantId.toString() === value.toString() ? (
                <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
              ) : (
                <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
              ),
          },
        {
          header: intl.get('hzero.common.button.action').d('操作'),
          width: 120,
          lock: 'right',
          renderer: ({ record }) => {
            const operators = [];
            if (
              tenantId.toString() === record.toData().tenantId.toString() ||
              !isTenantRoleLevel()
            ) {
              operators.push({
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.edit`,
                        type: 'button',
                        meaning: 'WebHook配置-编辑',
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
            }
            if (
              tenantId.toString() !== record.toData().tenantId.toString() &&
              isTenantRoleLevel() &&
              !VERSION_IS_OP
            ) {
              operators.push({
                key: 'copy',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}/copy`,
                        type: 'button',
                        meaning: 'WebHook配置-复制',
                      },
                    ]}
                    onClick={() => {
                      handleEdit(false, record, true);
                    }}
                  >
                    {intl.get('hzero.common.button.copy').d('复制')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.copy').d('复制'),
              });
            }
            if (
              tenantId.toString() === record.toData().tenantId.toString() &&
              isTenantRoleLevel() &&
              !VERSION_IS_OP
            ) {
              operators.push({
                key: 'delete',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: 'WebHook配置-删除',
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
            }
            return operatorRender(operators);
          },
        },
      ].filter(Boolean),
    []
  );

  // 新建或编辑
  const handleEdit = (isEdit, record, isCopy) => {
    drawerDs = new DataSet(drawerDS());
    drawerDs.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    const drawerProps = {
      isEdit,
      drawerDs,
      isTenant,
      currentEditData,
      isCopy,
    };
    Modal.open({
      drawer: true,
      key: 'editDrawer',
      destroyOnClose: true,
      closable: true,
      title: isCopy ? intl.get('hzero.common.title.copy').d('复制') : title,
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

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  return (
    <>
      <Header title={intl.get('hmsg.webhookConfig.view.title.webHookConfig').d('WebHook接收方')}>
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
          onClick={() => handleEdit(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={isTenant ? 2 : 3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hmsg.webhookConfig', 'hmsg.common'] })(WebHookConfig);
