/**
 * 单据审计配置
 * @since 2020-07-14
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import Drawer from './Drawer';
import {
  tableDs as TableDs,
  detailDs as DetailDs,
  copyDS,
} from '../../stores/DocumentAuditConfigDS';

const isTenant = isTenantRoleLevel();
const organizationId = getCurrentOrganizationId();
const DocumentAuditConfig = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(TableDs()), []);

  const detailDs = useMemo(() => new DataSet(DetailDs()), []);

  const copyDs = useMemo(() => new DataSet(copyDS()), []);

  const columns = useMemo(
    () => [
      !isTenant && { name: 'tenantName', width: 220 },
      { name: 'documentCode', width: 220 },
      { name: 'documentName', width: 220 },
      { name: 'documentDescription' },
      isTenant && {
        name: 'source',
        width: 150,
        renderer: ({ record }) => {
          return record.get('tenantId') === organizationId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        name: 'enabledFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'left',
        width: 130,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const operators = [];
          if (!isTenant || record.get('tenantId') === organizationId) {
            // 平台级 或者 租户级数据tenantId 和当前租户id相等
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
                        meaning: '单据审计配置-编辑',
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
                        code: `${path}/delete`,
                        type: 'button',
                        meaning: '单据审计配置-删除',
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
          } else if (record.get('enabledFlag') === 1) {
            operators.push({
              key: 'view',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.view`,
                      type: 'button',
                      meaning: '单据审计配置-查看',
                    },
                  ]}
                  onClick={() => {
                    handleEdit(true, record, true);
                  }}
                >
                  {intl.get('hzero.common.button.view').d('查看')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.view').d('查看'),
            });
          } else {
            operators.push({
              key: 'copy',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.copy`,
                      type: 'button',
                      meaning: '单据审计配置-复制',
                    },
                  ]}
                  onClick={() => {
                    handleCopy(record);
                  }}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            });
          }

          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  const handleEdit = (isEdit, record, isView) => {
    detailDs.create({});
    const currentEditData = record && record.toData();
    // eslint-disable-next-line no-nested-ternary
    const title = !isView
      ? !isEdit
        ? intl.get('hzero.common.view.title.create').d('新建')
        : intl.get('hzero.common.view.title.edit').d('编辑')
      : intl.get('hzero.common.button.view').d('查看');
    Modal.open({
      drawer: true,
      key: 'documentAuditConfig',
      destroyOnClose: true,
      closable: true,
      title,
      style: {
        width: 850,
      },
      children: (
        <Drawer
          currentEditData={currentEditData}
          isEdit={isEdit}
          detailDs={detailDs}
          path={path}
          isView={isView}
        />
      ),
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

  /**
   * 删除
   * @param {*} record
   */
  const handleDelete = async (record) => {
    await tableDs.delete(record);
  };

  /**
   * 保存头
   */
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

  /**
   * 复制
   */
  const handleCopy = async (record) => {
    try {
      copyDs.create(record.toData(), 0);
      const validate = await copyDs.submit();
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
        title={intl
          .get('hmnt.documentAuditConfig.view.message.title.documentAudit')
          .d('单据审计配置')}
      >
        {!isTenant && (
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/create`,
                type: 'button',
                meaning: '银行账户-新建',
              },
            ]}
            icon="add"
            color="primary"
            onClick={() => handleEdit(false)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        )}
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hmnt.documentAuditConfig'] })(DocumentAuditConfig);
