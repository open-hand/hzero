/**
 * EditDataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-14
 * @copyright 2019 © HAND
 */
import React from 'react';
import { DatePicker, Form, Tooltip, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
// import uuid from 'uuid/v4';

import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
// import Lov from 'components/Lov';
// import notification from 'utils/notification';

import intl from 'utils/intl';
import { tableScrollWidth, getDateFormat } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { VERSION_IS_OP } from 'utils/config';
import UserModal from './UserModal';

const rowKey = '_mockId';

export default class EditDataTable extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);

    this.state = {
      userModalVisible: false,
    };
  }

  @Bind()
  handleAddBtnClick() {
    const { onTableAdd } = this.props;
    onTableAdd();
  }

  @Bind()
  async handleDeleteBtnClick() {
    const { onTableDelete } = this.props;
    const { selectedRows = [] } = this.state;
    await onTableDelete(selectedRows);
  }

  // Table
  getColumns() {
    // const { role = {}, tenantRoleLevel } = this.props;
    // const { id: roleId } = role;
    const dateFormat = getDateFormat();
    return [
      {
        dataIndex: 'loginName',
        title: intl.get('hiam.roleManagement.model.roleManagement.loginName').d('账户'),
        render: (v, record) => {
          if (record.tipMessage) {
            return (
              <>
                <span>{v}</span>
                <Tooltip title={record.tipMessage}>
                  &nbsp;
                  <Icon type="exclamation-circle-o" />
                </Tooltip>
              </>
            );
          } else {
            return v;
          }
        },
      },
      {
        dataIndex: 'realName',
        title: intl.get('hiam.roleManagement.model.roleManagement.userLoginName').d('用户名'),
        // render: (realName, record) => {
        //   const { _status } = record;
        //   if (_status === 'create') {
        //     const { $form: form } = record;
        //     return (
        //       <Form.Item>
        //         {form.getFieldDecorator('id', {
        //           initialValue: record.id,
        //           rules: [
        //             {
        //               required: true,
        //               message: intl.get('hzero.common.validation.notNull', {
        //                 name: intl
        //                   .get('hiam.roleManagement.model.roleManagement.userLoginName')
        //                   .d('用户名'),
        //               }),
        //             },
        //           ],
        //         })(
        //           <Lov
        //             allowClear={false}
        //             queryParams={{ roleId }}
        //             style={{ width: '100%' }}
        //             code={tenantRoleLevel ? 'HIAM.USER.ORG' : 'HIAM.USER'}
        //             textValue={realName}
        //             onChange={(userId, userRecord) => {
        //               this.handleUserChange(userId, userRecord, record);
        //             }}
        //           />
        //         )}
        //       </Form.Item>
        //     );
        //   } else {
        //     return realName;
        //   }
        // },
      },
      !VERSION_IS_OP && {
        title: intl.get('hiam.roleManagement.model.roleManagement.tenant').d('所属租户'),
        dataIndex: 'tenantName',
        render: (tenantName, record) => {
          const { _status } = record;
          if (_status === 'create' || _status === 'update') {
            const { $form: form } = record;
            return form.getFieldDecorator('tenantName', {
              initialValue: tenantName,
            })(<>{form.getFieldValue('tenantName')}</>);
          } else {
            return tenantName;
          }
        },
      },
      {
        title: intl.get('hiam.subAccount.model.role.startDateActive').d('起始时间'),
        key: 'startDateActive',
        width: 140,
        render: (_, record) => {
          const { _status } = record;
          if (_status === 'create' || _status === 'update') {
            const { $form: form } = record;
            return (
              <Form.Item>
                {form.getFieldDecorator('startDateActive', {
                  initialValue: record.startDateActive
                    ? moment(record.startDateActive, DEFAULT_DATE_FORMAT)
                    : undefined,
                })(
                  <DatePicker
                    format={dateFormat}
                    style={{ width: '100%' }}
                    disabled={record.removableFlag === 0 || record.manageableFlag === 0}
                    placeholder={null}
                    disabledDate={(currentDate) => {
                      return (
                        form.getFieldValue('endDateActive') &&
                        moment(form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                      );
                    }}
                  />
                )}
              </Form.Item>
            );
          } else {
            return _;
          }
        },
      },
      {
        title: intl.get('hiam.subAccount.model.role.endDateActive').d('失效时间'),
        key: 'endDateActive',
        width: 140,
        render: (_, record) => {
          const { _status } = record;
          if (_status === 'create' || _status === 'update') {
            const { $form: form } = record;
            return (
              <Form.Item>
                {form.getFieldDecorator('endDateActive', {
                  initialValue: record.endDateActive
                    ? moment(record.endDateActive, DEFAULT_DATE_FORMAT)
                    : undefined,
                })(
                  <DatePicker
                    format={dateFormat}
                    style={{ width: '100%' }}
                    disabled={record.removableFlag === 0 || record.manageableFlag === 0}
                    placeholder={null}
                    disabledDate={(currentDate) =>
                      form.getFieldValue('startDateActive') &&
                      moment(form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                    }
                  />
                )}
              </Form.Item>
            );
          } else {
            return _;
          }
        },
      },
    ].filter(Boolean);
  }

  @Bind()
  handleUserChange(userId, userRecord, record) {
    const { $form: form } = record;
    form.setFieldsValue({
      // assignLevel: undefined,
      // assignLevelMeaning: undefined,
      // assignLevelValue: undefined,
      // assignLevelValueMeaning: undefined,
      tenantName: userRecord.tenantName,
    });
  }

  @Bind()
  handleRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys: selectedRows.map((record) => record[rowKey]),
    });
  }

  // @Bind()
  // getCheckboxProps(record) {
  //   if (record.admin) {
  //     return {
  //       disabled: true,
  //     };
  //   } else {
  //     return {};
  //   }
  // }

  @Bind()
  handleUserModal() {
    const { clearMemberList } = this.props;
    const { userModalVisible } = this.state;
    if (userModalVisible) {
      clearMemberList();
    }
    this.setState({
      userModalVisible: !userModalVisible,
    });
  }

  @Bind()
  saveUser(data) {
    const { handleSave } = this.props;
    handleSave(data);
  }

  render() {
    const {
      dataSource = [],
      pagination,
      onChange,
      path,
      queryLoading = false,
      saveLoading = false,
      deleteLoading = false,
      memberModalList,
      memberModalPagination,
      fetchUserList,
      roleId,
    } = this.props;
    const { selectedRowKeys = [], userModalVisible } = this.state;
    const columns = this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
      getCheckboxProps: (record) => ({
        disabled: record.removableFlag === 0,
      }),
    };
    const userModalProps = {
      visible: userModalVisible,
      dataSource: memberModalList,
      pagination: memberModalPagination,
      fetchUser: fetchUserList,
      roleId,
      onCancel: this.handleUserModal,
      onSave: this.saveUser,
    };
    return (
      <>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.addAssign`,
                type: 'button',
                meaning: '角色管理-新增',
              },
            ]}
            type="primary"
            onClick={this.handleUserModal}
          >
            {intl.get('hzero.common.button.add').d('新增')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deleteAssign`,
                type: 'button',
                meaning: '角色管理-删除',
              },
            ]}
            onClick={this.handleDeleteBtnClick}
            disabled={selectedRowKeys.length === 0 || saveLoading || queryLoading}
            loading={deleteLoading}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          rowKey={rowKey}
          dataSource={dataSource}
          pagination={pagination}
          loading={queryLoading}
          rowSelection={rowSelection}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={onChange}
        />
        {userModalVisible && <UserModal {...userModalProps} />}
      </>
    );
  }
}
