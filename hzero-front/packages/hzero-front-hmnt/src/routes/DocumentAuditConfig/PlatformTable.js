import React from 'react';
import intl from 'utils/intl';
import axios from 'axios';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';

import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId, getAccessToken } from 'utils/utils';
import { API_HOST, HZERO_MNT } from 'utils/config';
import { getMenuId } from 'utils/menuTab';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import {
  detailTableDs as DetailTableDs,
  addLineDs as AddLineDs,
} from '../../stores/DocumentAuditConfigDS';
import AddDrawer from './AddDrawer';
import ViewDetail from './ViewDetail';

const isTenant = isTenantRoleLevel();
const PlatformTable = (props) => {
  const detailTableDs = React.useMemo(() => new DataSet(DetailTableDs()), []);

  const addLineDs = React.useMemo(() => new DataSet(AddLineDs()), []);

  const { currentEditData, detailDs, path, isView = false } = props;

  React.useEffect(() => {
    queryData();
  }, []);

  /**
   * 查询头行信息
   */
  const queryData = async () => {
    const { auditDocumentId } = currentEditData;
    detailTableDs.auditDocumentId = auditDocumentId;
    await detailTableDs.query();
  };

  /**
   * 删除行信息
   * @param {*} record
   */
  const handleDelete = async (record) => {
    await detailTableDs.delete(record);
    detailTableDs.query();
  };

  const columns = React.useMemo(
    () => [
      { name: 'tenantName', width: 220 },
      { name: 'auditTypeMeaning', width: 200 },
      {
        name: 'auditType',
        width: 220,
        renderer: ({ record }) => {
          if (record.get('auditType') === 'API') {
            return record.get('description');
          } else if (record.get('auditType') === 'USER') {
            return record.get('username');
          } else if (record.get('auditType') === 'ROLE') {
            return record.get('roleName');
          } else if (record.get('auditType') === 'CLIENT') {
            return record.get('clientName');
          }
        },
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const operators = [
            {
              key: 'detail',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.table.viewDetail`,
                      type: 'button',
                      meaning: '单据审计配置-操作审计-查看明细',
                    },
                  ]}
                  onClick={() => {
                    handleDetailView(record);
                  }}
                >
                  {intl.get('hmnt.documentAuditConfig.view.button.auditView').d('查看明细')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hmnt.documentAuditConfig.view.button.auditView').d('查看明细'),
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
                  disabled={isView}
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

  /**
   * 新建操作审计
   */
  const handleAdd = () => {
    const {
      currentEditData: { auditDocumentId },
    } = props;
    const title = intl.get('hzero.common.view.title.create').d('新建');
    Modal.open({
      drawer: false,
      key: 'documentAuditConfig',
      destroyOnClose: true,
      closable: true,
      title,
      style: {
        width: 1000,
      },
      children: <AddDrawer addLineDs={addLineDs} auditDocumentId={auditDocumentId} />,
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
   * 新建操作审计 - 保存
   */
  const handleOk = () => {
    const {
      currentEditData: { auditDocumentId },
    } = props;
    const selectedData = addLineDs.selected.map((item) => item.toData());
    axios({
      url: isTenant
        ? `${HZERO_MNT}/v1/${getCurrentOrganizationId()}/audit-documents/add-op/${auditDocumentId}`
        : `${HZERO_MNT}/v1/audit-documents/add-op/${auditDocumentId}`,
      method: 'POST',
      data: selectedData,
      baseURL: `${API_HOST}`,
      headers: {
        Authorization: `bearer ${getAccessToken()}`,
        'H-Menu-Id': `${getMenuId()}`,
      },
    })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      })
      .then(() => {
        notification.success();
        detailDs.query();
        detailTableDs.query();
      });
  };

  // 查看明细
  const handleDetailView = (record) => {
    const currentData = record.toData() || {};
    Modal.open({
      drawer: true,
      key: 'ViewDetail',
      destroyOnClose: true,
      closable: true,
      title: intl.get('hmnt.documentAuditConfig.view.button.auditView').d('查看明细'),
      style: {
        width: 700,
      },
      children: <ViewDetail currentData={currentData} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };

  return (
    <>
      <Card
        style={{ marginTop: 20 }}
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>{intl.get('hmnt.documentAuditConfig.view.message.title.audit').d('操作审计')}</h3>
        }
      >
        <div className="table-operator">
          {!isView && (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}/create`,
                  type: 'button',
                  meaning: '单据审计配置-新建',
                },
              ]}
              icon="add"
              color="primary"
              onClick={() => handleAdd(false)}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
          )}
        </div>
        <Table dataSet={detailTableDs} columns={columns} queryFieldsLimit={3} />
      </Card>
    </>
  );
};

export default PlatformTable;
