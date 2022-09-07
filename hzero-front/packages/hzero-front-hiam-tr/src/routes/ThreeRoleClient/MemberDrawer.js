/**
 * MemberDrawer - 客户端管理 - 分配角色
 * @date: 2020-7-20
 * @author: Nemo <yingbin.jiang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Modal, Row, Col, Form, DatePicker, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import { isEmpty, forEach, find } from 'lodash';

import { getDateFormat, getEditTableData } from 'utils/utils';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
import notification from 'utils/notification';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';

import RoleModal from './RoleModal';
import SearchForm from './SearchForm';
import styles from './index.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class MemberDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      ownedRoleList: [],
      selectedRowKeys: [],
      selectedRows: [],
      currentClientId: '',
      visibleRole: false,
      roleType: 'permission',
      selectedVisitRowKeys: [],
    };
  }

  searchFormRef = React.createRef();

  componentDidMount() {
    this.queryOwnedRole();
  }

  // 查询当前已分配角色
  @Bind()
  queryOwnedRole(fields = {}) {
    const { dispatch, detailStatus, detailRecord } = this.props;
    switch (detailStatus) {
      case 'update':
        Promise.all([
          dispatch({
            type: 'trClient/roleCurrent',
            payload: {
              clientId: detailRecord.id,
              memberType: 'client',
              page: isEmpty(fields) ? {} : fields,
              ...fields,
            },
          }).then((detailRes) => {
            const { form } = this.props;
            if (detailRes) {
              form.resetFields();
              this.setState({
                // 对获取到的用户列表进行处理，加入_status属性
                ownedRoleList: detailRes.content.map((r) => ({
                  ...r,
                  _status: 'update',
                })),
                currentClientId: detailRecord.id,
              });
            }
          }),
          dispatch({
            type: 'trClient/roleVisitCurrent',
            payload: {
              clientId: detailRecord.id,
              page: isEmpty(fields) ? {} : fields,
              organizationId: detailRecord.organizationId,
            },
          }),
        ]).catch((error) => {
          notification.error(error);
        });
        break;
      default:
        break;
    }
  }

  /**
   * 模态框确定-保存客户端
   */
  @Bind()
  onOk() {
    const { onOk, form, detailRecord } = this.props;
    form.validateFields((error) => {
      if (!error) {
        // ownedRoleList 获取到的用户列表
        const { ownedRoleList = [] } = this.state;
        // validatingDataSource 可编辑的数据
        const validatingDataSource = ownedRoleList.filter(
          (r) => r._status === 'create' || (r._status === 'update' && r.manageableFlag === 1)
        );
        // validateSource 编辑后的数据
        // TODO: 没有编辑的数据不传给后端，这里是把所有可编辑的数据全部都传了
        const validateDataSource = getEditTableData(validatingDataSource, ['_status']);
        const memberRoleList = validateDataSource
          .filter((i) => {
            const j = find(validatingDataSource, (or) => or.id === i.id);
            return (
              (j._status === 'update' &&
                (j.startDateActive !== i.startDateActive || j.endDateActive !== i.endDateActive)) ||
              j._status === 'create'
            );
          })
          .map((r) => ({
            ...r,
            roleId: r.roleId || r.id,
            // roleId: r.id,
            assignLevel: r.assignLevel,
            assignLevelValue: r.assignLevelValue,
            memberId: detailRecord.id,
            memberType: 'client',
            startDateActive: r.startDateActive && r.startDateActive.format(DEFAULT_DATE_FORMAT),
            endDateActive: r.endDateActive && r.endDateActive.format(DEFAULT_DATE_FORMAT),
          }));

        // const newClient = {
        //   ...detailRecord,
        //   memberRoleList: memberRoleList.map((item) => {
        //     if (item.createFlag) {
        //       const { id, createFlag, ...rest } = item;
        //       return rest;
        //     } else {
        //       const { id, ...res } = item;
        //       return res;
        //     }
        //   }),
        // : memberRoleList
        // .concat(ownedRoleList.filter(
        //   r => r._status === 'create'
        // )),
        // };
        // if (validatingDataSource.length === validateDataSource.length) {
        onOk(memberRoleList);
        // }
      }
    });
  }

  /**
   * 打开可访问角色 选择模态框
   */
  @Bind()
  handleRoleAddBtnClick() {
    const { ownedRoleList = [] } = this.state;
    const roleModalProps = {
      excludeRoleIds: [],
      excludeUserIds: [],
    };
    ownedRoleList.forEach((r) => {
      roleModalProps.excludeRoleIds.push(r.id);
    });
    this.setState({
      visibleRole: true,
      roleModalProps,
    });
  }

  // 删除选中的角色
  @Bind()
  handleRoleRemoveBtnClick() {
    const { dispatch, paginationRole, detailRecord } = this.props;
    const { selectedRows, selectedRowKeys, ownedRoleList } = this.state;
    const that = this;
    if (selectedRows.filter((i) => i._status !== 'create').length === 0) {
      if (selectedRowKeys.length === 0) {
        Modal.error({
          content: intl.get('hiam.client.view.message.chooseRoleFirst').d('请先选择要删除的角色'),
        });
        return;
      } else {
        that.setState({
          ownedRoleList: ownedRoleList.filter((item) => !selectedRowKeys.includes(item.id)),
        });
        return;
      }
    }
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.title').d('提示'),
      content: intl.get(`hiam.client.view.message.title.content`).d('确定删除吗？'),
      onOk() {
        const ids = [];
        selectedRows
          .filter((i) => i._status !== 'create')
          .forEach((item) => {
            ids.push({
              roleId: item.id,
              memberType: 'client',
              memberId: detailRecord.id,
            });
          });
        dispatch({
          type: 'trClient/deleteRoles',
          payload: {
            memberRoleList: ids,
          },
        }).then((res) => {
          if (res) {
            that.queryOwnedRole(paginationRole);
            that.setState({ selectedRowKeys: [] });
            notification.success();
          }
        });
      },
    });
  }

  /**
   * 打开授权角色角色 选择模态框
   */
  @Bind()
  handleVisitRoleAddBtnClick() {
    // const { ownedRoleList = [] } = this.state;
    const { visitRoleList } = this.props;
    const roleModalProps = {
      excludeVisitRoleIds: [],
      excludeUserIds: [],
    };
    visitRoleList.forEach((r) => {
      roleModalProps.excludeVisitRoleIds.push(r.id);
    });
    this.setState({
      visibleRole: true,
      roleModalProps,
    });
  }

  // 删除选中的角色
  @Bind()
  handleVisitRoleRemoveBtnClick() {
    const { initData = {}, dispatch, paginationRole } = this.props;
    const { roleType, selectedVisitRowKeys } = this.state;
    const that = this;
    if (selectedVisitRowKeys.length === 0) {
      Modal.error({
        content: intl.get('hiam.client.view.message.chooseRoleFirst').d('请先选择要删除的角色'),
      });
      return;
    }
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.title').d('提示'),
      content: intl.get(`hiam.client.view.message.title.content`).d('确定删除吗？'),
      onOk() {
        const ids = [];
        selectedVisitRowKeys.forEach((item) => {
          ids.push({
            roleId: item,
            memberId: initData.id,
          });
        });
        dispatch({
          type: roleType === 'permission' ? 'trClient/deleteRoles' : 'trClient/deleteVisitRoles',
          payload: {
            clientId: initData.id,
            memberRoleList: ids.map((item) => {
              const { id, ...rest } = item;
              return rest;
            }),
          },
        }).then((res) => {
          if (res) {
            that.queryOwnedRole(paginationRole);
            that.setState({ selectedRowKeys: [] });
            notification.success();
          }
        });
      },
    });
  }

  @Bind()
  handleAfterAddPermissionRole(fields = {}) {
    const { dispatch } = this.props;
    const { currentClientId, roleType } = this.state;
    if (roleType === 'permission') {
      dispatch({
        type: 'trClient/roleCurrent',
        payload: {
          clientId: currentClientId,
          memberType: 'client',
          page: isEmpty(fields) ? {} : fields,
        },
      }).then((detailRes) => {
        const { form } = this.props;
        if (detailRes) {
          form.resetFields();
          this.setState({
            // 对获取到的用户列表进行处理，加入_status属性
            ownedRoleList: detailRes.content.map((r) => ({
              ...r,
              _status: 'update',
            })),
          });
        }
      });
    } else {
      dispatch({
        type: 'trClient/roleVisitCurrent',
        payload: {
          clientId: currentClientId,
          page: isEmpty(fields) ? {} : fields,
        },
      });
    }
  }

  @Bind()
  handleSearchFormAction() {
    const params = this.searchFormRef.current
      ? this.searchFormRef.current.props.form.getFieldsValue()
      : {};
    this.queryOwnedRole(params);
  }

  /**
   * 渲染 分配角色Table
   */
  @Bind()
  renderRoleTable() {
    const { ownedRoleList = [], selectedRowKeys = [] } = this.state;
    const { isSameUser, fetchOwnedLoading, paginationRole } = this.props;
    const rowSelection = isSameUser
      ? null
      : {
          selectedRowKeys,
          onChange: this.handleRoleSelectionChange,
        };
    const columns = [
      {
        title: intl.get('hiam.client.model.client.roleName').d('角色名称'),
        dataIndex: 'name',
        width: 100,
      },
      {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hiam.subAccount.model.role.startDateActive').d('起始时间'),
        key: 'startDateActive',
        width: 160,
        render: (_, record) => {
          const { $form } = record;
          const { getFieldDecorator } = $form;
          const dateFormat = getDateFormat();
          return (
            <Form.Item>
              {getFieldDecorator('startDateActive', {
                initialValue: record.startDateActive
                  ? moment(record.startDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabled={record.manageableFlag === 0}
                  disabledDate={(currentDate) => {
                    return (
                      $form.getFieldValue('endDateActive') &&
                      moment($form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                    );
                  }}
                />
              )}
            </Form.Item>
          );
        },
      },
      {
        title: intl.get('hiam.subAccount.model.role.endDateActive').d('失效时间'),
        key: 'endDateActive',
        width: 160,
        render: (_, record) => {
          const { $form } = record;
          const { getFieldDecorator } = $form;
          const dateFormat = getDateFormat();
          return (
            <Form.Item>
              {getFieldDecorator('endDateActive', {
                initialValue: record.endDateActive
                  ? moment(record.endDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabled={record.manageableFlag === 0}
                  disabledDate={(currentDate) =>
                    $form.getFieldValue('startDateActive') &&
                    moment($form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          );
        },
      },
    ];
    return (
      <EditTable
        rowKey="id"
        bordered
        onChange={this.handleAfterAddPermissionRole}
        loading={fetchOwnedLoading}
        rowSelection={rowSelection}
        dataSource={ownedRoleList}
        columns={columns}
        pagination={paginationRole}
      />
    );
  }

  /**
   * 渲染 分配角色Table
   */
  @Bind()
  renderVisitRoleTable() {
    const { selectedVisitRowKeys = [] } = this.state;
    const { isSameUser, fetchOwnedLoading, visitRoleList, visitRolePagination } = this.props;
    const rowSelection = isSameUser
      ? null
      : {
          selectedVisitRowKeys,
          onChange: this.handleVisitRoleSelectionChange,
        };
    const columns = [
      {
        title: intl.get('hiam.client.model.client.roleName').d('角色名称'),
        dataIndex: 'name',
        width: 300,
      },
      {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
      },
    ];
    return (
      <EditTable
        rowKey="id"
        bordered
        onChange={this.handleAfterAddPermissionRole}
        loading={fetchOwnedLoading}
        rowSelection={rowSelection}
        dataSource={visitRoleList}
        columns={columns}
        pagination={visitRolePagination}
      />
    );
  }

  @Bind()
  fetchRoles(fields) {
    const { fetchAllRoles } = this.props;
    return fetchAllRoles(fields);
  }

  @Bind()
  fetchVisitRoles(fields) {
    const { fetchAllRoles } = this.props;
    return fetchAllRoles(fields);
  }

  /**
   * 新增角色模态框确认按钮点击
   */
  @Bind()
  handleRoleAddSaveBtnClick(roles) {
    const { tenantId, dispatch, paginationRole, detailRecord } = this.props;
    const { roleType } = this.state;
    const memberRoleList = [];
    forEach(roles, (record) => {
      const newRecord = {
        id: record.id,
        roleId: record.id,
        assignLevel: 'organization',
        memberType: 'client',
        memberId: detailRecord.id,
        sourceId: tenantId,
        sourceType: record.level,
        assignLevelValue: record.tenantId,
        name: record.name,
        assignLevelValueMeaning: record.assignLevelValueMeaning,
        tenantName: record.tenantName,
        createFlag: true,
      };
      if (roleType === 'visit') {
        delete newRecord.memberType;
      }
      // newRecord.assignLevelValue = record.assignLevelValue || tenantId;
      if (!isEmpty(newRecord.assignLevel) && !isEmpty(newRecord.assignLevelValue)) {
        memberRoleList.push(newRecord);
      }
    });
    const payload =
      roleType === 'permission'
        ? [...memberRoleList]
        : {
            memberRoleList,
            clientId: detailRecord.id,
            // organizationId: initData.organizationId,
          };
    return dispatch({
      type: roleType === 'permission' ? 'trClient/saveRoleSet' : 'trClient/saveVisitRoleSet',
      payload,
    }).then((res) => {
      if (res) {
        if (roleType === 'permission') {
          this.setState(
            {
              visibleRole: false,
            },
            () => {
              this.setState({
                ownedRoleList: this.state.ownedRoleList.concat(res),
              });
              // this.handleAfterAddPermissionRole(paginationRole);
            }
          );
        } else {
          this.setState(
            {
              visibleRole: false,
            },
            () => {
              notification.success();
              this.queryOwnedRole(paginationRole);
              // this.handleAfterAddPermissionRole(paginationRole);
            }
          );
        }
      }
    });
  }

  /**
   * 新增角色模态框取消按钮点击
   */
  @Bind()
  handleRoleAddCancelBtnClick() {
    this.setState({
      visibleRole: false,
    });
  }

  /**
   * @param {String[]} selectedRowKeys 选中的rowKey
   */
  @Bind()
  handleRoleSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  @Bind()
  handleVisitRoleSelectionChange(selectedVisitRowKeys) {
    this.setState({ selectedVisitRowKeys });
  }

  @Bind()
  handleChangeRoleType(type) {
    this.setState({
      roleType: type,
    });
  }

  render() {
    const {
      visible,
      detailStatus,
      path,
      loadingDistributeUsers,
      saveRoleLoading,
      fetchLoading,
      onCancel,
      loading,
    } = this.props;
    const updateFlag = detailStatus === 'update';
    const {
      selectedRowKeys,
      visibleRole,
      roleModalProps = {},
      roleType,
      // selectedVisitRowKeys,
    } = this.state;

    return (
      <Modal
        width={700}
        title={intl.get('hiam.client.model.client.assign.role').d('分配角色')}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onOk={this.onOk}
        onCancel={onCancel}
        confirmLoading={loading}
        destroyOnClose
      >
        <Spin spinning={fetchLoading}>
          <SearchForm
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormAction}
          />
          <Form>
            {updateFlag && (
              <Row>
                <Col>
                  <FormItem style={{ textAlign: 'right' }}>
                    <Col span={23}>
                      <ButtonPermission
                        permissionList={[
                          {
                            code: `${path}.button.deleteRole`,
                            type: 'button',
                            meaning: '客户端-删除角色',
                          },
                        ]}
                        style={{ marginRight: 8 }}
                        onClick={this.handleRoleRemoveBtnClick}
                        disabled={selectedRowKeys.length === 0}
                      >
                        {intl.get('hzero.common.button.delete').d('删除')}
                      </ButtonPermission>
                      <ButtonPermission
                        permissionList={[
                          {
                            code: `${path}.button.createRole`,
                            type: 'button',
                            meaning: '客户端-新建角色',
                          },
                        ]}
                        type="primary"
                        onClick={() => this.handleRoleAddBtnClick()}
                      >
                        {intl.get('hzero.common.button.create').d('新建')}
                      </ButtonPermission>
                    </Col>
                  </FormItem>
                </Col>
              </Row>
            )}
            {updateFlag && (
              <Row type="flex">
                <Col span={24} className={styles['rule-table']}>
                  {this.renderRoleTable()}
                </Col>
              </Row>
            )}
          </Form>
        </Spin>
        {!!visibleRole && (
          <RoleModal
            {...roleModalProps}
            visible={visibleRole}
            fetchLoading={loadingDistributeUsers}
            saveLoading={saveRoleLoading}
            fetchRoles={this.fetchRoles}
            onSave={this.handleRoleAddSaveBtnClick}
            onCancel={this.handleRoleAddCancelBtnClick}
            roleType={roleType}
          />
        )}
      </Modal>
    );
  }
}
