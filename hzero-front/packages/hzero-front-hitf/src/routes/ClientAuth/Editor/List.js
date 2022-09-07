/**
 * Table - 菜单配置 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Button, Modal } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { totalRender } from 'utils/renderer';
import { DataSet, Lov } from 'choerodon-ui/pro';
import notification from 'utils/notification';
import { PAGE_SIZE_OPTIONS } from 'utils/constants';
import { tableScrollWidth } from 'utils/utils';

export default class ServiceList extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      selectedRows: [],
    };
  }

  @Bind()
  handleDeleteService() {
    const {
      selectedRowKeys,
      dataSource,
      onRowSelectionChange,
      deleteLines,
      fetchInformation,
    } = this.props;
    if (selectedRowKeys.length > 0) {
      Modal.confirm({
        title: intl.get('hzero.common.message.confirm.title').d('提示'),
        content: intl
          .get('hitf.application.model.application.title.deleteContent')
          .d('未保存的数据将会丢失,确定删除吗?'),
        onOk() {
          const ids = [];
          const newDataSource = [];
          dataSource.forEach((item) => {
            if (
              !isUndefined(item.assignId) &&
              selectedRowKeys.indexOf(item.interfaceServerId) >= 0
            ) {
              ids.push(item.assignId);
            }
            if (selectedRowKeys.indexOf(item.interfaceServerId) < 0) {
              newDataSource.push(item);
            }
          });
          if (ids.length > 0) {
            deleteLines(ids).then((res) => {
              if (res) {
                onRowSelectionChange([], []);
                notification.success();
                fetchInformation();
              }
            });
          } else {
            onRowSelectionChange([], []);
            notification.success();
            fetchInformation();
          }
        },
      });
    }
  }

  @Bind()
  handleSelect({ record }) {
    if (record.get('_currentStatus') === 'loading') {
      return;
    }
    const temp = record.toData();
    const { selectedRows } = this.state;
    this.setState({
      selectedRows: [...selectedRows, { ...temp }],
    });
  }

  @Bind()
  handleUnselect({ record }) {
    const { selectedRows } = this.state;
    this.setState({
      selectedRows: selectedRows.filter((temp) => temp.id !== record.get('id')),
    });
  }

  @Bind()
  handleSelectAll({ dataSet }) {
    this.setState({ selectedRows: dataSet.toData() });
  }

  @Bind()
  handleUnSelectAll() {
    this.setState({ selectedRows: [] });
  }

  @Bind()
  handleAddRoles() {
    const { dataSource, addRoles = () => {} } = this.props;
    const { selectedRows } = this.state;
    for (const key in selectedRows) {
      if (Object.hasOwnProperty.call(selectedRows, key)) {
        const selectedRole = selectedRows[key];
        if (dataSource.find((item) => item.roleId === selectedRole.id)) {
          const { name, levelPath } = selectedRole;
          notification.warning({
            message: intl
              .get('hitf.application.model.application.model.repeatRole')
              .d(`角色【${name}：${levelPath}】已存在，请勿重复添加`),
          });
          return false;
        }
      }
    }
    addRoles(selectedRows);
    this.setState({ selectedRows: [] });
  }

  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
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
      dataSource = [],
      selectedRowKeys = [],
      loading,
      onRowSelectionChange = (e) => e,
      listSelectedRows,
      deleteRoles = () => {},
    } = this.props;
    const tableColumns = [
      {
        title: intl.get('hitf.application.model.application.roleName').d('角色名称'),
        dataIndex: 'name',
        width: 180,
      },
      {
        title: intl.get('hitf.application.model.application.roleCode').d('角色代码'),
        dataIndex: 'viewCode',
      },
      {
        dataIndex: 'levelPath',
        title: intl.get('hitf.application.model.application.levelPath').d('角色路径'),
        onCell: this.onCell.bind(this),
      },
    ];
    const tableProps = {
      dataSource,
      pagination: {
        showSizeChanger: true,
        pageSizeOptions: [...PAGE_SIZE_OPTIONS],
        pageSize: 10, // 每页大小
        total: dataSource.length,
        showTotal: totalRender,
      },
      loading,
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowSelection: {
        selectedRowKeys,
        onChange: onRowSelectionChange,
      },
    };

    const lovBtnDS = new DataSet({
      primaryKey: 'id',
      autoCreate: true,
      fields: [
        {
          name: 'code',
          type: 'object',
          lovCode: 'HITF.USER_ROLE',
          multiple: true,
          required: true,
        },
      ],
      cacheSelection: true,
      selection: 'multiple',
    });

    return (
      <>
        <div className="action" style={{ textAlign: 'right' }}>
          <Button
            icon="minus"
            disabled={isEmpty(listSelectedRows)}
            // onClick={addService}
            onClick={deleteRoles}
            style={{ marginRight: 8 }}
          >
            {intl.get('hitf.application.view.button.deleteRole').d('删除角色')}
          </Button>
          <Lov
            color="primary"
            dataSet={lovBtnDS}
            mode="button"
            name="code"
            clearButton={false}
            icon="add"
            lovEvents={{
              select: this.handleSelect,
              unSelect: this.handleUnselect,
              selectAll: this.handleSelectAll,
              unSelectAll: this.handleUnSelectAll,
            }}
            modalProps={{
              onOk: this.handleAddRoles,
            }}
          >
            {intl.get('hitf.application.view.button.addRole').d('添加角色')}
          </Lov>
        </div>
        <br />
        <Table {...tableProps} />
      </>
    );
  }
}
