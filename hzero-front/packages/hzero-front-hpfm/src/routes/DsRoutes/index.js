/*
 * DataSourceApplication 数据源路由规则
 * @date: 2020-04-22
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Table, DataSet, Modal, ModalContainer } from 'choerodon-ui/pro';
import axios from 'axios';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';

import { tableDS as tableDs, pushTableDS as pushTableDs } from '../../stores/DsRoutesDS';

const DsRoutes = (props) => {
  const {
    match: { path },
  } = props;

  const tableDS = React.useMemo(() => new DataSet(tableDs()), []);

  const pushTableDS = React.useMemo(() => new DataSet(pushTableDs()), []);

  const columns = React.useMemo(
    () => [
      { name: 'serviceName', width: 150 },
      { name: 'tenantName', width: 150 },
      { name: 'url' },
      { name: 'method', width: 150 },
      { name: 'dsNames', width: 200 },
      {
        name: 'enabledFlag',
        align: 'left',
        renderer: ({ value }) => enableRender(value),
        width: 100,
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
                      meaning: '数据源路由规则-编辑',
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
            record.get('enabledFlag') === 0 && {
              key: 'enabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enabled`,
                      type: 'button',
                      meaning: '数据源路由规则-启用',
                    },
                  ]}
                  onClick={() => {
                    handleEnabled(record);
                  }}
                >
                  {intl.get('hzero.common.button.enabled').d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.enabled').d('启用'),
            },
            record.get('enabledFlag') === 1 && {
              key: 'unEnabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.unEnabled`,
                      type: 'button',
                      meaning: '数据源路由规则-禁用',
                    },
                  ]}
                  onClick={() => {
                    handleDisabled(record);
                  }}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
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
                        meaning: '数据源路由规则-删除',
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
          return operatorRender(operators, record, { limit: 3 });
        },
        lock: 'right',
      },
    ],
    []
  );

  const pushColumns = React.useMemo(() => [{ name: 'serviceCode' }], []);

  /**
   *
   * @param {*} record
   * 路由规则删除
   */
  const handleDelete = async (record) => {
    await tableDS.delete(record);
  };

  /**
   *
   * @param {*} isEdit
   * @param {*} record
   * 路由规则 编辑 && 新建
   */
  const handleEdit = (isEdit, record) => {
    //
    const { history } = props;
    const dsRouteId = isEdit ? record.get('dsRouteId') : 'create';
    tableDS.setQueryParameter('dsRouteId', dsRouteId);
    history.push(`/hpfm/ds-routes/detail/${dsRouteId}`);
  };

  /**
   *
   * @param {*} record
   * 路由规则-启用
   */
  const handleEnabled = (record) => {
    axios({
      url: isTenantRoleLevel()
        ? `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ds-routes/enable`
        : `${HZERO_PLATFORM}/v1/ds-routes/enable`,
      method: 'POST',
      params: {
        dsRouteId: record.get('dsRouteId'),
      },
    })
      .then(() => {
        notification.success();
        tableDS.query();
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  };

  /**
   *
   * @param {*} record
   * 禁用
   */
  const handleDisabled = (record) => {
    axios({
      url: isTenantRoleLevel()
        ? `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ds-routes/disable`
        : `${HZERO_PLATFORM}/v1/ds-routes/disable`,
      method: 'POST',
      params: {
        dsRouteId: record.get('dsRouteId'),
      },
    })
      .then(() => {
        notification.success();
        tableDS.query();
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  };

  /**
   * 推送查询
   */
  const handlePush = () => {
    pushTableDS.query();
    const title = intl.get('hpfm.dsRoutes.view.title.push').d('数据源推送');
    Modal.open({
      key: 'push',
      destroyOnClose: true,
      title,
      style: {
        width: 700,
      },
      children: <Table dataSet={pushTableDS} queryFieldsLimit={2} columns={pushColumns} />,
      onOk: handlePushOk,
    });
  };

  /**
   * 推送确定
   */
  const handlePushOk = async () => {
    const selectedData = pushTableDS.selected.map((item) => item.toData());
    if (Array.isArray(selectedData) && selectedData.length === 0) {
      notification.warning({
        message: intl.get('hpfm.dsRoutes.view.message.title.select').d('至少选中一条数据'),
      });
      return false;
    } else {
      axios({
        url: isTenantRoleLevel()
          ? `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ds-routes/push/service`
          : `${HZERO_PLATFORM}/v1/ds-routes/push/service`,
        method: 'POST',
        data: selectedData,
      })
        .then(() => {
          notification.success();
        })
        .catch((err) => {
          notification.error({
            message: err.message,
          });
        });
    }
  };

  return (
    <>
      <Header title={intl.get('hpfm.dsRoutes.view.message.title.dsRouteRule').d('数据源路由规则')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/create`,
              type: 'button',
              meaning: '数据源路由规则-新建',
            },
          ]}
          icon="add"
          color="primary"
          onClick={() => handleEdit(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/push`,
              type: 'button',
              meaning: '数据源路由规则-数据源推送',
            },
          ]}
          icon="email-o"
          color="primary"
          onClick={() => handlePush(false)}
        >
          {intl.get('hpfm.common.button.push').d('数据源推送')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table dataSet={tableDS} columns={columns} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hpfm.dsRoutes'] })(DsRoutes);
