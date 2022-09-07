/**
 * EditModal.js
 * 当编辑自己的帐号时, 角色时不可以新增和删除的
 * @date 2018-12-16
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Checkbox, Col, DatePicker, Form, Modal, Row, Spin } from 'hzero-ui';
import { differenceWith, find, forEach, isEmpty, isUndefined, map } from 'lodash';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import PropTypes from 'prop-types';

import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import {
  addItemsToPagination,
  createPagination,
  delItemsToPagination,
  getDateFormat,
  getEditTableData,
  tableScrollWidth,
} from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { VERSION_IS_OP } from 'utils/config';

import styles from '../../index.less';
import RoleModal from './RoleModal';
import SearchForm from './SearchForm';

@Form.create({ fieldNameProp: null })
export default class EditModal extends React.Component {
  state = {
    selectedRowKeys: [],
    roleModalProps: {}, // 新建角色框
    // 角色表格的信息
    dataSource: [],
    pagination: false,
    roleTableFetchLoading: false, // 角色加载数据 和 翻页改变
  };

  searchFormRef = React.createRef();

  rolePaginationCache; // 角色 Table 分页信息的缓存

  // todo 最后 页面的 propTypes 推荐 全部删掉
  static propTypes = {
    fetchRoles: PropTypes.func.isRequired,
    onOk: PropTypes.func.isRequired,
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {};
    if (prevState.pagination !== false) {
      nextState.pagination = false;
    }
    if (isEmpty(nextState)) {
      return null;
    }
    return nextState;
  }

  componentDidMount() {
    this.fetchRolesFirst();
  }

  fetchRolesFirst(params) {
    const { fetchCurrentUserRoles, detailRecord } = this.props;
    this.showRoleTableLoading();
    fetchCurrentUserRoles({ userId: detailRecord.id, ...params })
      .then((roleContent) => {
        // 在前面中已经 getResponse 了
        if (roleContent) {
          this.setState({
            dataSource: map(roleContent.content, (r) => ({
              ...r,
              _status: 'update',
              _isRemote: true,
            })),
            pagination: createPagination(roleContent),
          });
          // 翻页清空 已取消默认角色的租户
        }
      })
      .finally(() => {
        this.hiddenRoleTableLoading();
      });
  }

  /**
   * 角色 table 分页改变
   * 如果是新增用户 分页是
   * @param {object} page
   * @param {object} filter
   * @param {object} sort
   */
  @Bind()
  handleRoleTableChange(page, filter, sort) {
    const { fetchCurrentUserRoles, detailRecord = {} } = this.props;
    this.showRoleTableLoading();
    fetchCurrentUserRoles({ page, sort, userId: detailRecord.id })
      .then((roleContent) => {
        // 在前面中已经 getResponse 了
        if (roleContent) {
          this.setState({
            dataSource: map(roleContent.content, (r) => ({
              ...r,
              _status: 'update',
              _isRemote: true,
            })),
            pagination: createPagination(roleContent),
          });
          // 翻页清空 已取消默认角色的租户
        }
      })
      .finally(() => {
        this.hiddenRoleTableLoading();
      });
  }

  @Bind()
  showRoleTableLoading() {
    this.setState({ roleTableFetchLoading: true });
  }

  @Bind()
  hiddenRoleTableLoading() {
    this.setState({ roleTableFetchLoading: false });
  }

  getRoleColumns() {
    return [
      {
        title: intl.get('hiam.subAccount.model.role.name').d('角色名称'),
        dataIndex: 'name',
      },
      !VERSION_IS_OP && {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
        width: 250,
      },
      {
        title: intl.get('hiam.subAccount.model.role.startDateActive').d('起始时间'),
        key: 'startDateActive',
        width: 140,
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
        width: 140,
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
                  disabled={record.manageableFlag === 0}
                  placeholder={null}
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
      {
        title: intl.get('hiam.subAccount.model.role.defaultRoleId').d('默认'),
        key: 'defaultRoleId',
        width: 80,
        render: (_, record) => {
          const { defaultRole, assignLevel } = record;
          if (assignLevel === 'organization' || assignLevel === 'org') {
            return (
              <Checkbox
                checked={defaultRole}
                onClick={() => {
                  this.handleRoleDefaultChange(record);
                }}
              />
            );
          }
          return null;
        },
      },
    ].filter(Boolean);
  }

  @Bind()
  handleRowSelectChange(_, selectedRows = []) {
    this.setState({
      selectedRowKeys: map(selectedRows, (r) => r.id),
      selectedRows,
    });
  }

  @Bind()
  handleRoleDefaultChange(record) {
    const { dataSource = [] } = this.state;
    const defaultChangeTo = !record.defaultRole;
    const newDataSource = dataSource.map((item) => {
      if (item.id === record.id) {
        return {
          ...item,
          defaultRole: defaultChangeTo,
          defaultRoleIdUpdate: true,
        };
      } else if (defaultChangeTo && item.tenantId === record.tenantId) {
        // item 取消同租户的 defaultRole
        return {
          ...item,
          defaultRole: false,
          defaultRoleIdUpdate: item.defaultRole,
        };
      } else {
        return item;
      }
    });
    this.setState({
      dataSource: newDataSource,
    });
  }

  @Bind()
  handleRoleRemove() {
    Modal.confirm({
      title: intl.get(`hzero.common.message.confirm.title`).d('提示'),
      content: intl.get(`hiam.subAccount.view.message.title.content`).d('确定删除吗？'),
      onOk: () => {
        const { selectedRows = [] } = this.state;
        const { detailRecord = {}, onRoleRemove } = this.props;
        const remoteRemoveDataSource = [];
        forEach(selectedRows, (r) => {
          if (r._isRemote) {
            remoteRemoveDataSource.push({
              roleId: r.id,
              memberId: detailRecord.id,
            });
          }
        });
        if (remoteRemoveDataSource.length > 0) {
          onRoleRemove(remoteRemoveDataSource).then((res) => {
            if (res) {
              this.removeLocaleRoles();
            }
          });
        } else {
          this.removeLocaleRoles();
        }
      },
    });
  }

  @Bind()
  removeLocaleRoles() {
    const nextState = {};
    const { dataSource = [], selectedRowKeys = [], pagination = {} } = this.state;
    nextState.dataSource = differenceWith(dataSource, selectedRowKeys, (r1, r2) => r1.id === r2);
    nextState.pagination = delItemsToPagination(
      selectedRowKeys.length,
      dataSource.length,
      pagination
    );
    nextState.selectedRowKeys = [];
    nextState.selectedRows = [];
    this.setState(nextState);
  }

  @Bind()
  handleRoleCreate() {
    const { detailRecord = {} } = this.props;
    const { dataSource = [] } = this.state;
    const excludeUserIds = []; // 当前编辑帐号的帐号id( 需要的排除帐号对应的角色 )
    const excludeRoleIds = [];
    excludeUserIds.push(detailRecord.id);
    dataSource.forEach((role) => {
      if (role._status === 'create') {
        excludeRoleIds.push(role.id);
      }
    });
    this.setState({
      roleModalProps: {
        visible: true,
        excludeUserIds,
        excludeRoleIds,
      },
    });
  }

  @Bind()
  handleRoleCreateSave(roles) {
    if (roles && roles.length > 0) {
      const { dataSource = [], pagination = {} } = this.state;
      this.setState({
        dataSource: [
          ...dataSource,
          ...roles.map((role) => {
            // 保存时的数据结构
            // assignLevel: "organization" // 层级
            // assignLevelValue: 180149 // 层级值
            // memberType: "user" // 恒定为 user
            // roleId: 150191 // 角色id
            // sourceId: 180149 // 当前用户所属租户
            // sourceType: "organization" // 当前租户层级

            // todo 角色层级值 的取值 到底需要怎么做
            // warn: 如果 角色 的 level 为 site 那么 这个角色的 租户 为 0租户
            const assignLevel = 'organization';
            const assignLevelValue = role.tenantId;
            const assignLevelValueMeaning = role.tenantName;
            return {
              level: role.level, // 需要将 role.level 传递下来, 下面的判断需要
              // 角色所属的租户
              tenantId: role.tenantId,
              // 角色所属的租户名称
              tenantName: role.tenantName,
              // 角色名称 展示使用
              name: role.name,
              // 用来限制 层级值
              parentRoleAssignLevel: role.parentRoleAssignLevel,
              // 角色id
              id: role.id,
              // sourceType 是 选中角色的 level
              sourceType: role.level,
              // memberType 是固定的 user
              memberType: 'user',
              // assignLevel 和 assignLevelValue 是需要自己填写的
              assignLevel,
              assignLevelValue,
              assignLevelValueMeaning,
              _status: 'create',
              // 新加进来的 角色 都是可以管理的
              manageableFlag: 1,
            };
          }),
        ],
        pagination: addItemsToPagination(roles.length, dataSource.length, pagination),
      });
    }
    this.handleRoleCreateCancel();
  }

  @Bind()
  handleRoleCreateCancel() {
    this.setState({
      roleModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  handleSearchFormAction() {
    const params = this.searchFormRef.current
      ? this.searchFormRef.current.props.form.getFieldsValue()
      : {};
    this.fetchRolesFirst(params);
  }

  @Bind()
  handleEditModalOk() {
    const { form, detailRecord } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { dataSource = [] } = this.state;
        const memberRoleList = [];
        // defaultRoleIds 不使用了, 使用 [ { roleId, tenantId, defaultFlag } ]
        const defaultRoles = [];

        const validatingDataSource = dataSource.filter(
          (r) => r._status === 'create' || r._status === 'update'
        );
        const validateDataSource = getEditTableData(validatingDataSource);
        dataSource.forEach((oldR) => {
          // // 如果 assignLevelValue 有值, 则使用他的值, 否则通过 层级 来确定层级值
          // const assignLevelValue = isUndefined(r.assignLevelValue)
          //   ? r.assignLevel === 'site' || r.assignLevel === 'organization'
          //     ? fieldsValue.organizationId
          //     : undefined
          //   : r.assignLevelValue;
          // 去掉前端添加的字段
          const r = find(validateDataSource, (or) => or.id === oldR.id);
          if (r) {
            // 是可以修改的数据
            const { defaultRoleIdUpdate, id, startDateActive, endDateActive, ...oriRecord } = r;
            const newRole = {
              ...oriRecord, // 需要得到之前角色的 id 等信息
              assignLevel: r.assignLevel, // 层级
              // 当 层级 为 组织 和 租户 时, 层级值为 租户id
              assignLevelValue: r.assignLevelValue, // 层级值
              memberType: 'user', // 恒定为 user
              roleId: id, // 角色id
              memberId: detailRecord.id,
              sourceId: fieldsValue.organizationId, // 当前用户所属租户
              sourceType: r.sourceType, // 当前租户层级
              startDateActive: startDateActive && startDateActive.format(DEFAULT_DATE_FORMAT),
              endDateActive: endDateActive && endDateActive.format(DEFAULT_DATE_FORMAT),
            };
            if (defaultRoleIdUpdate) {
              defaultRoles.push({
                roleId: newRole.roleId,
                tenantId: newRole.tenantId,
                defaultFlag: newRole.defaultRole === false ? 0 : 1,
              });
            }
            if (
              // 数据校验
              !isUndefined(newRole.assignLevel) &&
              !isUndefined(newRole.assignLevelValue) &&
              // 重复校验
              (oldR._status === 'create' ||
                (oldR._status === 'update' &&
                  (oldR.assignLevel !== newRole.assignLevel ||
                    oldR.assignLevelValue !== newRole.assignLevelValue)) ||
                (oldR._status === 'update' && oldR.startDateActive !== newRole.startDateActive) ||
                oldR.endDateActive !== newRole.endDateActive)
              // 如果是正常流程 这两个是一定相同的
              // || oldR.sourceId !== newRole.sourceId
              // || oldR.sourceType !== newRole.sourceType
            ) {
              memberRoleList.push(newRole);
            }
          } else {
            if (oldR.defaultRoleIdUpdate && oldR.defaultRole === false) {
              // 该角色是 租户/组织 层 默认角色更新过 且 取消了
              defaultRoles.push({
                roleId: oldR.id,
                tenantId: oldR.tenantId,
                defaultFlag: 0,
              });
            }
            if (oldR.defaultRoleIdUpdate && oldR.defaultRole !== false) {
              defaultRoles.push({
                roleId: oldR.id,
                tenantId: oldR.tenantId,
                defaultFlag: 1,
              });
            }
          }
        });
        if (dataSource.length !== 0 && validatingDataSource.length !== validateDataSource.length) {
          // 必须要有角色, 且校验通过
          return;
        }
        const { onOk } = this.props;
        onOk(memberRoleList);
      }
    });
  }

  renderForm() {
    const {
      path,
      // form,
      roleRemoveLoading,
    } = this.props;
    const {
      selectedRowKeys = [],
      dataSource = [],
      pagination = false,
      roleTableFetchLoading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const roleColumns = this.getRoleColumns();

    return (
      <Form>
        <Row style={{ textAlign: 'right', marginBottom: 16 }}>
          <Col span={23}>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.deleteRole`,
                  type: 'button',
                  meaning: '用户管理-删除角色',
                },
              ]}
              style={{ marginRight: 8 }}
              onClick={this.handleRoleRemove}
              disabled={selectedRowKeys.length === 0}
              loading={roleRemoveLoading}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
            <ButtonPermission
              style={{ display: 'inline' }}
              permissionList={[
                {
                  code: `${path}.button.createRole`,
                  type: 'button',
                  meaning: '用户管理-新增角色',
                },
              ]}
              type="primary"
              onClick={this.handleRoleCreate}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
          </Col>
          <Col span={1} />
        </Row>
        <Row type="flex">
          <Col span={23} className={styles['rule-table']}>
            <EditTable
              bordered
              rowKey="id"
              dataSource={dataSource}
              pagination={pagination}
              loading={roleTableFetchLoading}
              columns={roleColumns}
              scroll={{ x: tableScrollWidth(roleColumns) }}
              onChange={this.handleRoleTableChange}
              rowSelection={rowSelection}
            />
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      form,
      detailRecord,
      fetchRoles,
      queryDetailLoading = false,
      ...modalProps
    } = this.props;
    // todo 租户id 明天问下 明伟哥。
    const tenantId = 0;
    const { roleModalProps = {} } = this.state;
    return (
      <Modal
        title={intl.get('hiam.subAccount.view.message.title.role').d('分配角色')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={1000}
        {...modalProps}
        onOk={this.handleEditModalOk}
      >
        <Spin spinning={queryDetailLoading}>
          <SearchForm
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormAction}
          />
          {this.renderForm()}
          {roleModalProps.visible && (
            <RoleModal
              {...roleModalProps}
              fetchRoles={fetchRoles}
              onSave={this.handleRoleCreateSave}
              onCancel={this.handleRoleCreateCancel}
              tenantId={tenantId}
              detailRecord
            />
          )}
        </Spin>
      </Modal>
    );
  }
}
