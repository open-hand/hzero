/**
 * List  - 应用管理 - 首页列表
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import { yesOrNoRender, operatorRender, TagRender, valueMapMeaning } from 'utils/renderer';
import QuestionPopover from '@/components/QuestionPopover';
import { SERVICE_TYPE_TAGS } from '@/constants/constants';

export default class List extends PureComponent {
  defaultTableRowKey = 'id';

  render() {
    const {
      path,
      dataSource = [],
      pagination,
      loading,
      serverTypeCode = [],
      openDocument = (e) => e,
      onChange = (e) => e,
      openAuthConfig = () => {},
      rowSelection = {},
    } = this.props;
    const tableColumns = [
      {
        title: intl.get('hitf.interfaces.model.interfaces.tenantName').d('所属租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.namespace').d('服务命名空间'),
        dataIndex: 'namespace',
        width: 120,
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.interfaceCode').d('接口编码'),
        dataIndex: 'interfaceCode',
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 150,
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.serverCode').d('服务代码'),
        dataIndex: 'serverCode',
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.serverName').d('服务名称'),
        dataIndex: 'serverName',
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.serverType').d('服务类型'),
        dataIndex: 'serverType',
        width: 120,
        align: 'center',
        render: (text) => {
          return TagRender(text, SERVICE_TYPE_TAGS, valueMapMeaning(serverTypeCode, text));
        },
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.isPublicFlag').d('公开接口'),
        dataIndex: 'isPublicFlag',
        width: 100,
        align: 'center',
        render: yesOrNoRender,
      },
      {
        title: (
          <QuestionPopover
            text={intl.get('hitf.interfaces.model.interfaces.authTenant').d('授权租户')}
            message={intl
              .get('hitf.interfaces.model.interfaces.authTenantTip')
              .d('当前租户是否有该接口权限')}
          />
        ),
        dataIndex: 'tenantAuthFlag',
        width: 100,
        align: 'center',
        render: yesOrNoRender,
      },
      {
        title: (
          <QuestionPopover
            text={intl.get('hitf.interfaces.model.interfaces.authRole').d('授权角色')}
            message={intl
              .get('hitf.interfaces.model.interfaces.authRoleTip')
              .d('当前角色是否有该接口权限')}
          />
        ),
        dataIndex: 'authFlag',
        width: 100,
        align: 'center',
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'viewDocument',
              ele: (
                <a onClick={() => openDocument(record)}>
                  {intl.get('hitf.interfaces.view.button.viewDocument').d('查看文档')}
                </a>
              ),
              len: 4,
              title: intl.get('hitf.interfaces.view.button.viewDocument').d('查看文档'),
            },
            {
              key: 'auth',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.auth`,
                      type: 'button',
                      meaning: '认证配置',
                    },
                  ]}
                  onClick={() => openAuthConfig(record)}
                >
                  {intl.get('hitf.interfaces.view.button.auth').d('认证配置')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hitf.interfaces.view.button.auth').d('认证配置'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ]
      .filter(
        (item) => !(!isTenantRoleLevel() && ['tenantAuthFlag', 'authFlag'].includes(item.dataIndex))
      )
      .filter((item) => (isTenantRoleLevel() ? item.dataIndex !== 'tenantName' : true));
    const tableProps = {
      dataSource,
      loading,
      onChange,
      rowSelection,
      pagination,
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowKey: this.defaultTableRowKey,
    };
    return <Table {...tableProps} />;
  }
}
