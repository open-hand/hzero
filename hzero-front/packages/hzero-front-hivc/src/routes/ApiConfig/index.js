import React from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { yesOrNoRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';

import { apiConfigDS } from './stores/apiConfigDS';

const ApiConfig = ({ match, history }) => {
  const isTenant = isTenantRoleLevel();
  const ds = new DataSet(apiConfigDS());

  const handleCreate = () => {
    history.push(`/hivc/api-config/edit/create`);
  };

  const handleEdit = ({ data }) => {
    history.push(`/hivc/api-config/edit/${data.apiConfigId}`);
  };

  const handleDelete = async (record) => {
    try {
      const res = await ds.delete(record, null);
      if (res && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  };

  const columns = React.useMemo(
    () =>
      [
        !isTenant && {
          header: intl.get('hivc.apiConfig.model.apiConfig.tenantId').d('租户'),
          name: 'tenantName',
        },
        {
          name: 'apiTypeMeaning',
        },
        {
          name: 'combineFlag',
          width: 100,
          align: 'left',
          renderer: ({ value }) => yesOrNoRender(value),
        },
        {
          name: 'defaultFlag',
          width: 100,
          align: 'left',
          renderer: ({ value }) => yesOrNoRender(value),
        },
        {
          header: intl.get('hzero.common.button.action').d('操作'),
          width: 150,
          renderer: ({ record }) => {
            const actions = [
              {
                key: '',
                title: '',
                len: 2,
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '发票接口类型-编辑',
                      },
                    ]}
                    onClick={() => handleEdit(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
              },
              {
                key: '',
                title: '',
                len: 2,
                ele: (
                  <ButtonPermission
                    type="text"
                    onClick={() => handleDelete(record)}
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '发票接口类型-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                ),
              },
            ];
            return operatorRender(actions);
          },
        },
      ].filter(Boolean),
    [handleEdit, handleDelete]
  );

  return (
    <>
      <Header title={intl.get('hivc.apiConfig.view.title').d('发票接口配置')}>
        <ButtonPermission type="c7n-pro" color="primary" icon="add" onClick={handleCreate}>
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table columns={columns} dataSet={ds} />
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hivc.apiConfig'],
})(ApiConfig);
