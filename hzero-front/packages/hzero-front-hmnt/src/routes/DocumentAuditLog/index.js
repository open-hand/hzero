/**
 * 单据审计日志
 * @since 2020-07-14
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import { isTenantRoleLevel } from 'utils/utils';

import Drawer from './Drawer';
import { tableDs as TableDs, detailDs as DetailDs } from '../../stores/DocumentAuditLogDS';

const documentAuditLog = ({ match: { path }, history }) => {
  const tableDs = React.useMemo(() => new DataSet(TableDs()), []);

  const detailDs = React.useMemo(() => new DataSet(DetailDs()), []);

  let modal = '';

  const columns = React.useMemo(
    () => [
      !isTenantRoleLevel() && { name: 'tenantName', width: 220 },
      { name: 'documentCode', width: 220 },
      { name: 'documentName' },
      { name: 'menuName', width: 220 },
      { name: 'businessKey', width: 150 },
      { name: 'auditDatetime', width: 220 },
      { name: 'realName', width: 220 },
      {
        name: 'auditResult',
        width: 220,
        renderer: ({ value }) => {
          return value === 'SUCCESS' ? (
            <Tag color="green">{intl.get('hzero.common.status.success').d('成功')}</Tag>
          ) : (
            <Tag color="red">{intl.get('hzero.common.status.error').d('失败')}</Tag>
          );
        },
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}/history`,
                    type: 'button',
                    meaning: '单据审计日志-历史记录',
                  },
                ]}
                onClick={() => {
                  handleEdit(record);
                }}
              >
                {intl.get('hzero.common.status.history').d('历史记录')}
              </ButtonPermission>
            ),
            len: 4,
            title: intl.get('hzero.common.status.history').d('历史记录'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  const handleClose = () => {
    modal.close();
  };

  const handleDetail = () => {
    modal.close();
    history.push('/hmnt/audit-query');
  };

  const handleEdit = (record) => {
    detailDs.create({});
    const currentEditData = record && record.toData();
    const title = intl.get('hzero.common.status.detail').d('查看详情');
    modal = Modal.open({
      drawer: true,
      key: 'documentAuditLog',
      destroyOnClose: true,
      closable: true,
      title,
      children: (
        <Drawer
          currentEditData={currentEditData}
          detailDs={detailDs}
          path={path}
          history={history}
          onClose={handleClose}
        />
      ),
      footer: (
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/detail`,
              type: 'button',
              meaning: '单据审计日志-查看详情',
            },
          ]}
          color="primary"
          onClick={() => {
            handleDetail(record);
          }}
        >
          {intl.get('hzero.common.status.detail').d('查看详情')}
        </ButtonPermission>
      ),
      onCancel: () => {
        detailDs.removeAll();
      },
      onClose: () => {
        detailDs.removeAll();
      },
    });
  };

  return (
    <>
      <Header
        title={intl.get('hmnt.documentAuditLog.view.message.title.documentAudit').d('单据审计查询')}
      />
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hmnt.documentAuditLog'] })(documentAuditLog);
