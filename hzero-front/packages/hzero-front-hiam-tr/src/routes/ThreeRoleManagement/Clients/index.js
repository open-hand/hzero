import React, { PureComponent } from 'react';
import { Form, Modal, Popconfirm, Spin } from 'hzero-ui';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import { getEditTableData, tableScrollWidth } from 'utils/utils';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import QueryForm from './QueryForm';

const FormItem = Form.Item;

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}
@Form.create({ fieldNameProp: null })
export default class Clients extends PureComponent {
  constructor(props) {
    super(props);
    this.oneSearchFormRef = React.createRef();
    this.state = {
      selectedRows: [],
      // okDisabled: false,
    };
  }

  form;

  /**
   * 表单查询
   */
  @Bind()
  handleOneSearchFormSearch() {
    const { onFormSearch } = this.props;
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    onFormSearch(fieldsValue);
  }

  /**
   * 勾选回调
   * @param {*} _
   * @param {*} selectedRows - 勾选的值集行
   */
  @Bind()
  handleSelectRows(_, selectedRows) {
    this.setState({
      selectedRows,
    });
  }

  /**
   * 模态框onCancel方法
   */
  @Bind()
  handleOnCancel() {
    const { close } = this.props;
    close();
    this.setState({
      selectedRows: [],
    });
  }

  /**
   * 编辑行
   * @param {*} record
   * @param {*} flag
   */
  @Bind()
  editRow(record, flag) {
    const { onEdit } = this.props;
    onEdit(record, flag);
  }

  /**
   * 取消编辑行
   * @param {*} record
   * @param {*} flag
   */
  @Bind()
  handleCancelEdit(record, flag) {
    const { onCancelEdit } = this.props;
    onCancelEdit(record, flag);
  }

  /**
   * 删除客户端行
   */
  @Bind()
  deleteRow(record) {
    const { onDeleteRow } = this.props;
    onDeleteRow(record);
  }

  /**
   * 删除客户端
   */
  @Bind()
  handleDeleteClient() {
    const { onDeleteClient, roleDatasource } = this.props;
    const { selectedRows } = this.state;
    const dataSource = selectedRows.map((item) => ({
      roleId: roleDatasource.id,
      memberId: item.id,
      memberType: 'client',
      id: item.memberRoleId,
    }));
    // 批量删除时：把新建的数据给过滤掉
    const filterList = dataSource.filter((item) => !item.memberId.toString().startsWith('create'));
    onDeleteClient(filterList);
    this.setState({
      selectedRows: [],
    });
  }

  /**
   * 添加客户端行
   */
  @Bind()
  handleAddClient() {
    const { onAddClient } = this.props;
    onAddClient();
  }

  /**
   * 保存客户端
   */
  @Bind()
  handleOnOk() {
    const { onSave, clientList = [], roleDatasource } = this.props;
    const params = getEditTableData(clientList, ['_status']);
    if (Array.isArray(params) && params.length > 0) {
      onSave(
        params.map((r) => {
          const { _status, id, ...restR } = r;
          return {
            ...restR,
            roleId: roleDatasource.id,
            assignLevel: 'organization',
            memberId: r.id,
            assignLevelValue: roleDatasource.tenantId,
            memberType: 'client',
          };
        })
      );
    }
    this.setState({
      selectedRows: [],
    });
  }

  /**
   * 客户端分页切换
   * @param {*} pagination
   */
  @Bind()
  onTableChange(pagination = {}) {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    const { onClientPageChange } = this.props;
    onClientPageChange(pagination, fieldsValue);
  }

  render() {
    const {
      clientList = [],
      visible,
      title,
      path,
      roleDatasource,
      saveLoading,
      deleteLoading,
      queryLoading,
      clientPagination,
    } = this.props;
    const roleId = roleDatasource.id;
    const { selectedRows } = this.state;
    // const params = getEditTableData(clientList, ['id', '_status']);
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.id),
      onChange: this.handleSelectRows,
    };

    const columns = [
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.clientName').d('客户端名称'),
        width: 300,
        dataIndex: 'name',
        render: (id, record) => {
          if (record._status === 'create') {
            const { getFieldDecorator, setFieldsValue } = record.$form;
            const onClientChange = function onClientChange(_, client) {
              setFieldsValue({
                id: client.id,
                tenantName: client.tenantName,
              });
            };
            return (
              <FormItem>
                {getFieldDecorator('id', {
                  initialValue: record.name,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.roleManagement.model.roleManagement.clientName')
                          .d('客户端名称'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    queryParams={{ roleId }}
                    code="HIAM.CLIENT"
                    onChange={onClientChange}
                    textValue={id}
                  />
                )}
              </FormItem>
            );
          } else {
            return id;
          }
        },
      },
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.tenant`).d('所属租户'),
        dataIndex: 'tenantName',
        width: 200,
        render: (tenantName, record) => {
          if (record._status === 'create') {
            const { getFieldValue, getFieldDecorator } = record.$form;
            return (
              <>
                {getFieldValue('tenantName')}
                {getFieldDecorator('tenantName')(<div />)}
              </>
            );
          } else {
            return tenantName;
          }
        },
      },
    ];
    return (
      <Modal
        title={title}
        visible={visible}
        destroyOnClose
        onCancel={this.handleOnCancel}
        confirmLoading={saveLoading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onOk={this.handleOnOk}
        // okButtonProps={{disabled: !okDisabled}}
        width="700px"
      >
        <QueryForm
          wrappedComponentRef={this.oneSearchFormRef}
          onSearch={this.handleOneSearchFormSearch}
          roleDatasource={roleDatasource}
        />
        <br />
        <div className="action" style={{ textAlign: 'right' }}>
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
            onConfirm={this.handleDeleteClient}
          >
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.deleteClients`,
                  type: 'button',
                  meaning: '角色管理-删除客户端',
                },
              ]}
              disabled={selectedRows.length === 0}
              style={{ marginRight: 8 }}
              loading={deleteLoading}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </Popconfirm>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.addClients`,
                type: 'button',
                meaning: '角色管理-新建客户端',
              },
            ]}
            type="primary"
            onClick={this.handleAddClient}
          >
            {intl.get(`hzero.common.button.create`).d('新建')}
          </ButtonPermission>
        </div>
        <br />
        <Spin spinning={queryLoading}>
          <EditTable
            rowKey="id"
            dataSource={clientList}
            bordered
            className="editable-table"
            columns={columns}
            onChange={this.onTableChange}
            onRow={this.onTableRow}
            rowSelection={rowSelection}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={clientPagination}
          />
        </Spin>
      </Modal>
    );
  }
}
