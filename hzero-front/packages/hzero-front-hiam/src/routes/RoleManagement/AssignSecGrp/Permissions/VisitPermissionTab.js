/**
 * VisitPermission - 安全组访问权限tab
 * @date: 2019-11-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table, Button, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { tableScrollWidth } from 'utils/utils';
import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender, yesOrNoRender } from 'utils/renderer';
import intl from 'utils/intl';

export default class VisitPermissionTab extends Component {
  constructor(props) {
    super(props);
    this.defaultExpandedRowKeys = [];
    this.state = {
      expandedRowKeys: [],
    };
  }

  /**
   * expandAll - 全部展开
   */
  @Bind()
  expandAll() {
    const { defaultExpandedRowKeys } = this.props;
    this.setState({
      expandedRowKeys: defaultExpandedRowKeys,
    });
  }

  /**
   * 全部收起
   */
  @Bind()
  collapseAll() {
    this.setState({
      expandedRowKeys: [],
    });
  }

  /**
   * 展开树
   * @param {boolean} expanded - 是否展开
   * @param {record} record - 当前行数据
   */
  @Bind()
  onExpand(expanded, record) {
    const { expandedRowKeys = [] } = this.state;
    this.setState({
      expandedRowKeys: expanded
        ? expandedRowKeys.concat(record.key)
        : expandedRowKeys.filter((o) => o !== record.key),
    });
  }

  get columns() {
    const { path, onShield = (e) => e } = this.props;
    return [
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.permissionName`).d('权限名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.permission.Type`).d('权限类型'),
        dataIndex: 'permissionType',
        width: 150,
        render: (value = '', record) => {
          const texts = {
            api: intl.get('hiam.roleManagement.view.message.api').d('API'),
            button: intl.get('hiam.roleManagement.view.message.button').d('按钮'),
            table: intl.get('hiam.roleManagement.view.message.table').d('表格列'),
            formItem: intl.get('hiam.roleManagement.view.message.formItem').d('表单项'),
            formField: intl.get('hiam.roleManagement.view.message.formField').d('表单域'),
          };
          const valueList = value.split(',') || [];
          const text = valueList.map((item) => (texts[item] ? texts[item] : '')) || [];
          return (
            record.type === 'ps' && (
              <Tag color={value === 'api' ? 'green' : 'orange'}>{text.join()}</Tag>
            )
          );
        },
      },
      {
        dataIndex: 'shieldFlag',
        title: intl.get('hiam.roleManagement.model.roleManagement.isShield').d('是否屏蔽'),
        width: 90,
        render: yesOrNoRender,
      },
      {
        key: 'operator',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        fixed: 'right',
        render: (_, { type, secGrpAclId, shieldFlag }) => {
          if (type === 'ps') {
            const shieldBtn = [
              {
                key: 'shield',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.shield`,
                        type: 'button',
                        meaning: '角色管理-屏蔽访问权限',
                      },
                    ]}
                    onClick={() =>
                      onShield({ shieldFlag, authorityId: secGrpAclId, authorityType: 'ACL' })
                    }
                  >
                    {shieldFlag
                      ? intl.get('hiam.roleManagement.view.button.cancelShield').d('取消屏蔽')
                      : intl.get('hiam.roleManagement.view.button.shield').d('屏蔽')}
                  </ButtonPermission>
                ),
                len: shieldFlag ? 4 : 2,
                title: shieldFlag
                  ? intl.get('hiam.roleManagement.view.button.cancelShield').d('取消屏蔽')
                  : intl.get('hiam.roleManagement.view.button.shield').d('屏蔽'),
              },
            ];
            return operatorRender(shieldBtn);
          }
        },
      },
    ];
  }

  render() {
    const { loading, dataSource, shieldLoading = false } = this.props;
    const { expandedRowKeys } = this.state;
    const tableProps = {
      rowKey: 'id',
      columns: this.columns,
      bordered: true,
      dataSource,
      loading: loading || shieldLoading,
      childrenColumnName: 'subMenus',
      pagination: false,
      expandedRowKeys,
      scroll: { x: tableScrollWidth(this.columns) },
      onExpand: this.onExpand,
    };

    return (
      <>
        <div className="action" style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Button onClick={this.collapseAll} style={{ marginRight: 8 }}>
            {intl.get(`hzero.common.button.collapseAll`).d('全部收起')}
          </Button>
          <Button onClick={this.expandAll}>
            {intl.get(`hzero.common.button.expandAll`).d('全部展开')}
          </Button>
        </div>
        <Table {...tableProps} />
      </>
    );
  }
}
