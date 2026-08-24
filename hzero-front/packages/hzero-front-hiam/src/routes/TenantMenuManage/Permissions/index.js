import React, { PureComponent } from 'react';
import { Button, Drawer, Form, Input, Table, Row, Col } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import PermissionsModal from './PermissionsModal';
import styles from './index.less';

const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const commonPrompt = 'hzero.common';

@Form.create({ fieldNameProp: null })
export default class Permissions extends PureComponent {
  state = {
    tableDataSource: [],
    tablePagination: {},
    tableSelectedRows: [],
    permissionsLovDataSource: [],
    permissionsLovPagination: {},
    permissionsLovVisible: false,
  };

  defautTableRowkey = 'id';

  // getSnapshotBeforeUpdate(prevProps) {
  //   const { visible, permissionSetDataSource } = this.props;
  //   return (
  //     visible &&
  //     isInteger(permissionSetDataSource.key) &&
  //     permissionSetDataSource.key !== prevProps.permissionSetDataSource.key
  //   );
  // }

  // componentDidUpdate(prevProps, prevState, snapshot) {
  //   // If we have a snapshot value, we've just added new items.
  //   // Adjust scroll so these new items don't push the old ones out of view.
  //   // (snapshot here is the value returned from getSnapshotBeforeUpdate)
  //   if (snapshot) {
  //     this.fetchDataSource();
  //   }
  // }
  componentDidMount() {
    this.fetchDataSource();
  }

  // 查询已分配的
  @Bind()
  fetchDataSource(params) {
    const { handleQueryPermissionsBySet = (e) => e, permissionSetDataSource = {} } = this.props;
    const fields = this.props.form.getFieldsValue();
    handleQueryPermissionsBySet(permissionSetDataSource.id, {
      size: 10,
      page: 0,
      ...fields,
      ...params,
    }).then((res) => {
      // const { dataSource, pagination } = res;
      this.setTableDataSource(res);
    });
  }

  @Bind()
  setTableDataSource(res) {
    this.setState({
      tableDataSource: res.dataSource || [],
      tablePagination: res.pagination || {},
      tableSelectedRows: [], // 重新查询后将选中数据清空
    });
  }

  // 查询可分配的
  @Bind()
  fetchPermissionsLovDataSource(params) {
    const { handleQueryPermissions = (e) => e, permissionSetDataSource = {} } = this.props;
    handleQueryPermissions(permissionSetDataSource.id, {
      size: 10,
      page: 0,
      ...params,
      permissionSetId: permissionSetDataSource.id,
    }).then((res) => {
      const { dataSource, pagination } = res;
      this.setState({
        permissionsLovDataSource: dataSource,
        permissionsLovPagination: pagination,
      });
    });
  }

  @Bind()
  onDrawerClose() {
    const { close = (e) => e } = this.props;
    this.setState({
      tableDataSource: [],
      tablePagination: {},
      tableSelectedRows: [],
      permissionsLovDataSource: [],
      permissionsLovPagination: {},
    });
    close();
  }

  @Bind()
  onTableSelectedRowsChange(tableSelectedRowKeys, tableSelectedRows) {
    this.setState({
      tableSelectedRows,
    });
  }

  @Bind()
  deletePermissions() {
    const { tableSelectedRows } = this.state;
    const { onDeletePermissions, permissionSetDataSource } = this.props;
    const permissionCodes = [];
    tableSelectedRows.forEach((n) => {
      permissionCodes.push(n.code);
    });
    const params = {
      menuId: permissionSetDataSource.id,
      permissionCodes,
    };
    if (onDeletePermissions) {
      onDeletePermissions(params).then((res) => {
        if (res) {
          notification.success();
          this.fetchDataSource();
        }
      });
    }
  }

  @Bind()
  openPermissionsLov() {
    this.fetchPermissionsLovDataSource();
    this.setState({
      permissionsLovVisible: true,
    });
  }

  @Bind()
  closePermissionsLov() {
    this.setState({
      permissionsLovVisible: false,
    });
  }

  @Bind()
  onPermissionsLovOk(selectedRows) {
    if (!(selectedRows.length > 0)) {
      notification.warning({
        message: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
      return;
    }
    const { onAssignPermissions, permissionSetDataSource } = this.props;
    const permissionCodes = [];
    selectedRows.forEach((n) => {
      permissionCodes.push(n.code);
    });
    const params = {
      menuId: permissionSetDataSource.id,
      permissionType: 'PERMISSION',
      permissionCodes,
    };
    if (onAssignPermissions) {
      onAssignPermissions(params).then((res) => {
        if (res) {
          notification.success();
          this.setState({ permissionsLovVisible: false }, () => {
            this.fetchDataSource();
          });
        }
      });
    }
  }

  @Bind()
  onTableChange(pagination = {}) {
    const {
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const { current = 1, pageSize = 10 } = pagination;
    this.fetchDataSource({ page: current - 1, size: pageSize, ...getFieldsValue() });
  }

  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
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
    const { visible, processing = {}, form, title, path } = this.props;
    const {
      tableDataSource = [],
      tablePagination = {},
      tableSelectedRows,
      permissionsLovVisible,
      permissionsLovDataSource = [],
      permissionsLovPagination = {},
    } = this.state;
    const drawerProps = {
      title,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.onDrawerClose,
      width: 800,
      wrapClassName: styles['hiam-menu-config-permissions'],
    };
    const tableColumns = [
      {
        title: intl.get(`${modelPrompt}.permissionCode`).d('权限编码'),
        dataIndex: 'code',
        onCell: this.onCell,
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.description`).d('描述'),
        dataIndex: 'description',
        onCell: this.onCell,
      },
      {
        title: intl.get(`${modelPrompt}.path`).d('路径'),
        dataIndex: 'path',
        onCell: this.onCell,
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.method`).d('方法'),
        dataIndex: 'method',
        width: 80,
      },
      {
        title: intl.get(`${modelPrompt}.level`).d('层级'),
        dataIndex: 'levelMeaning',
        width: 80,
      },
    ];
    const tableProps = {
      rowKey: this.defautTableRowkey,
      dataSource: tableDataSource,
      loading: processing.queryPermissionsByIdAll,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      pagination: tablePagination,
      onChange: this.onTableChange,
      bordered: true,
      rowSelection: {
        selectedRowKeys: tableSelectedRows.map((n) => n[this.defautTableRowkey]),
        onChange: this.onTableSelectedRowsChange,
      },
    };
    const permissionsLovProps = {
      visible: permissionsLovVisible,
      dataSource: permissionsLovDataSource,
      pagination: permissionsLovPagination,
      selectedRows: tableDataSource,
      handleFetchDataSource: this.fetchPermissionsLovDataSource,
      onCancel: this.closePermissionsLov,
      onOk: this.onPermissionsLovOk,
      queryPermissionsByMenuId: processing.queryPermissionsByMenuId,
      assignPermissions: processing.assignPermissions,
    };
    return (
      <Drawer {...drawerProps}>
        <Form style={{ marginBottom: 15 }}>
          <Row type="flex" gutter={24} align="bottom">
            <Col span={8}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.catalogCode`).d('目录编码')}
              >
                {form.getFieldDecorator('code')(<Input />)}
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.conditionPermission`).d('描述/路径')}
              >
                {form.getFieldDecorator('condition')(<Input />)}
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item>
                <Button onClick={this.handleFormReset} style={{ marginRight: 8 }}>
                  {intl.get(`${commonPrompt}.button.reset`).d('重置')}
                </Button>
                <Button
                  type="primary"
                  htmlType="submit"
                  onClick={() => this.fetchDataSource()}
                  loading={processing.queryPermissionsById}
                >
                  {intl.get(`${commonPrompt}.button.search`).d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
        {/* {(currentRoleCode === 'role/site/default/administrator' ||
          currentRoleCode === 'role/organization/default/administrator') && ( */}
        <div className="action" style={{ marginBottom: 15 }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.addPermission`,
                type: 'button',
                meaning: '菜单配置-添加权限',
              },
            ]}
            icon="plus"
            style={{ marginRight: 8 }}
            onClick={this.openPermissionsLov}
          >
            {intl.get(`${modelPrompt}.createPermission`).d('添加权限')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deletePermission`,
                type: 'button',
                meaning: '菜单配置-删除权限',
              },
            ]}
            icon="delete"
            onClick={this.deletePermissions}
            disabled={isEmpty(tableSelectedRows)}
            loading={processing.deletePermissions}
          >
            {intl.get(`${commonPrompt}.button.delete`).d('删除')}
          </ButtonPermission>
        </div>
        {/* )} */}
        <Table {...tableProps} />
        <br />
        <div className="footer">
          <Button onClick={this.onDrawerClose} disabled={processing.save} type="primary">
            {intl.get(`${commonPrompt}.button.close`).d('关闭')}
          </Button>
        </div>
        {permissionsLovVisible && <PermissionsModal {...permissionsLovProps} />}
      </Drawer>
    );
  }
}
