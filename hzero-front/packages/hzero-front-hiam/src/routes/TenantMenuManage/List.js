import React from 'react';
import { Table } from 'hzero-ui';

import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { operatorRender, yesOrNoRender } from 'utils/renderer';

import { Button as ButtonPermission } from 'components/Permission';
import styles from './index.less';

const commonPrompt = 'hzero.common';

export default class List extends React.Component {
  defaultTableRowKey = 'id';

  @Bind()
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 220,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: e => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const {
      content,
      tenantLoading,
      tenantPagination,
      onTenantPageChange = e => e,
      onHandleToDetails = e => e,
    } = this.props;
    const columns = [
      {
        title: intl.get('hiam.tenantMenu.model.tenantMenu.tenantCode').d('租户编码'),
        width: 200,
        dataIndex: 'tenantNum',
      },
      {
        title: intl.get('hiam.tenantMenu.model.tenantMenu.tenantName').d('租户名称'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hiam.tenantMenu.model.tenantMenu.customMenuFlag').d('包含自定义菜单'),
        dataIndex: 'customMenuFlag',
        render: yesOrNoRender,
        width: 120,
        align: 'center',
      },
      {
        title: intl.get(`${commonPrompt}.table.column.option`).d('操作'),
        width: 120,
        fixed: 'right',
        render: (text, record) => {
          const { path } = this.props;
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}/edit`,
                    type: 'button',
                    meaning: '代理-编辑',
                  },
                ]}
                onClick={() => {
                  onHandleToDetails(record);
                }}
              >
                {intl.get('hzero.common.button.details').d('查看详情')}
              </ButtonPermission>
            ),
            len: 4,
            title: intl.get('hzero.common.button.details').d('查看详情'),
          });
          return operatorRender(operators);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="tenantId"
        rowClassName={styles['hiam-tenant-table']}
        loading={tenantLoading}
        dataSource={content}
        columns={columns}
        pagination={tenantPagination}
        onChange={onTenantPageChange}
      />
    );
  }
}
