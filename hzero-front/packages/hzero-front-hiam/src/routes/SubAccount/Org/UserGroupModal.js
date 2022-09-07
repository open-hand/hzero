/**
 * EditModal.js
 * 当编辑自己的账号时, 用户组时不可以新增和删除的
 * @date 2018-1-15
 * @author LZY zhuyan.luo@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Checkbox, Col, Form, Input, Modal, Row } from 'hzero-ui';
import { differenceWith, find, forEach, isEmpty, map } from 'lodash';
import { Bind } from 'lodash-decorators';
import PropTypes from 'prop-types';

import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import notification from 'utils/notification';
import {
  addItemsToPagination,
  createPagination,
  delItemsToPagination,
  getCurrentOrganizationId,
  getEditTableData,
  tableScrollWidth,
} from 'utils/utils';

import styles from '../index.less';
import GroupModal from './GroupModal';

const tenantId = getCurrentOrganizationId();

@Form.create({ fieldNameProp: null })
export default class UserGroupModal extends React.Component {
  state = {
    selectedRowKeys: [],
    groupModalProps: {}, // 新建用户组框
    // 用户组表格的信息
    dataSource: [],
    pagination: false,
    groupTableFetchLoading: false, // 用户组加载数据 和 翻页改变
  };

  groupPaginationCache; // 用户组 Table 分页信息的缓存

  // todo 最后 页面的 propTypes 推荐 全部删掉
  static propTypes = {
    fetchGroups: PropTypes.func.isRequired,
    onOk: PropTypes.func.isRequired,
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const {
      isCreate = true,
      userRecord: { defaultGroupId } = {}, // userRecord 在 新增时 为 undefined
    } = nextProps;
    const nextState = {};
    if (isCreate && prevState.pagination !== false) {
      nextState.pagination = false;
    }
    if (defaultGroupId !== prevState.initDefaultGroupId) {
      nextState.defaultGroupId = defaultGroupId;
      nextState.initDefaultGroupId = defaultGroupId;
    }
    if (isEmpty(nextState)) {
      return null;
    }
    return nextState;
  }

  componentDidMount() {
    const { isCreate = true } = this.props;
    if (!isCreate) {
      this.handleGroupTableChange();
    }
  }

  @Bind()
  changeCountryId() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ regionId: undefined });
  }

  render() {
    const { form, userRecord, isCreate, fetchGroups, loading, ...modalProps } = this.props;
    // todo 租户id 明天问下 明伟哥。
    const { groupModalProps = {} } = this.state;
    return (
      <Modal
        title={intl.get('hiam.subAccount.view.message.title.userGroup').d('用户组分配')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={600}
        closable={false}
        confirmLoading={loading}
        {...modalProps}
        onOk={this.handleEditModalOk}
      >
        {this.renderForm()}
        {groupModalProps.visible && (
          <GroupModal
            {...groupModalProps}
            fetchGroups={fetchGroups}
            onSave={this.handleGroupCreateSave}
            onCancel={this.handleGroupCreateCancel}
            tenantId={tenantId}
          />
        )}
      </Modal>
    );
  }

  renderForm() {
    const { path, form, userRecord = {} } = this.props;
    const {
      selectedRowKeys = [],
      dataSource = [],
      pagination = false,
      groupTableFetchLoading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const groupColumns = this.getGroupColumns();
    const groupNode = (
      <React.Fragment key="group-no-same-user-btn">
        <Row style={{ textAlign: 'right' }}>
          <Col span={23}>
            <Form.Item>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.deleteUserGroup`,
                    type: 'button',
                    meaning: '子账户管理-删除用户组',
                  },
                ]}
                style={{ marginRight: 8 }}
                onClick={this.handleGroupRemove}
                disabled={selectedRowKeys.length === 0}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.createUserGroup`,
                    type: 'button',
                    meaning: '子账户管理-新建用户组',
                  },
                ]}
                type="primary"
                onClick={this.handleGroupCreate}
              >
                {intl.get('hzero.common.button.create').d('新建')}
              </ButtonPermission>
            </Form.Item>
          </Col>
        </Row>
        <Col span={1} />
        <Row type="flex">
          <Col span={3} />
          <Col span={20} className={styles['rule-table']}>
            <EditTable
              bordered
              rowKey="userGroupId"
              dataSource={dataSource}
              pagination={pagination}
              loading={groupTableFetchLoading}
              columns={groupColumns}
              scroll={{ x: tableScrollWidth(groupColumns) }}
              onChange={this.handleGroupTableChange}
              rowSelection={rowSelection}
            />
          </Col>
        </Row>
      </React.Fragment>
    );

    return (
      <Form>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.subAccount.model.user.loginName').d('账号')}
        >
          {form.getFieldDecorator('loginName', {
            initialValue: userRecord.loginName,
          })(<Input disabled />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
        >
          {form.getFieldDecorator('realName', {
            initialValue: userRecord.realName,
          })(<Input disabled />)}
        </Form.Item>
        {/* <Row type="flex">
          {isCreate || (
            <Col key="loginName" {...colLayout}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.user.loginName').d('账号')}
              >
                {form.getFieldDecorator('loginName', {
                  initialValue: userRecord.loginName,
                })(<Input disabled />)}
              </Form.Item>
            </Col>
          )}
          <Col key="realName" {...colLayout}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
            >
              {form.getFieldDecorator('realName', {
                initialValue: userRecord.realName,
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row> */}
        <Row>{groupNode}</Row>
      </Form>
    );
  }

  /**
   * 用户组 table 分页改变
   * 如果是新增用户 分页是
   * @param {object} page
   * @param {object} filter
   * @param {object} sort
   */
  @Bind()
  handleGroupTableChange(page, filter, sort) {
    const { fetchCurrentUserGroups, isCreate = true, userRecord = {} } = this.props;
    if (!isCreate) {
      this.showGroupTableLoading();
      fetchCurrentUserGroups({ page, sort, userId: userRecord.id })
        .then(groupContent => {
          // 在前面中已经 getResponse 了
          if (groupContent) {
            this.setState(
              {
                dataSource: map(groupContent.content, r => ({ ...r, _status: 'update' })),
                pagination: createPagination(groupContent),
              },
              () => {
                for (let i = 0; i < groupContent.content.length; i++) {
                  if (groupContent.content[i].defaultFlag === 1) {
                    this.setState({ defaultGroupId: groupContent.content[i].userGroupId });
                  }
                }
              }
            );
          }
        })
        .finally(() => {
          this.hiddenGroupTableLoading();
        });
    }
  }

  @Bind()
  showGroupTableLoading() {
    this.setState({ groupTableFetchLoading: true });
  }

  @Bind()
  hiddenGroupTableLoading() {
    this.setState({ groupTableFetchLoading: false });
  }

  // @Bind()
  // isGroupCanUpdate(group = {}, account = {}) {
  //   if (group._status === 'create') {
  //     return true;
  //   }
  //   // 只检查了 层级 为 平台 和 租户的情况
  //   if (group.assignLevel === 'site' || group.assignLevel === 'organization') {
  //     if (group.assignLevelValue !== (account.organizationId || account.tenantId)) {
  //       return false;
  //     }
  //   }
  //   return true;
  // }

  getGroupColumns() {
    if (!this.groupColumns) {
      this.groupColumns = [
        {
          title: intl.get('hiam.subAccount.model.group.groupCode').d('用户组编码'),
          dataIndex: 'groupCode',
          width: 200,
        },
        {
          title: intl.get('hiam.subAccount.model.group.groupName').d('用户组名称'),
          dataIndex: 'groupName',
          width: 120,
        },
        {
          title: intl.get('hiam.subAccount.model.group.defaultGroupId').d('默认'),
          key: 'defaultFlag',
          width: 80,
          render: (_, record) => {
            const { defaultGroupId } = this.state;
            return (
              <Checkbox
                checked={record.userGroupId === defaultGroupId}
                onClick={() => {
                  this.handleGroupDefaultChange(record.userGroupId);
                }}
              />
            );
          },
        },
      ];
    }
    return this.groupColumns;
  }

  @Bind()
  handleRowSelectChange(_, selectedRows = []) {
    this.setState({
      selectedRowKeys: map(selectedRows, r => r.userGroupId),
      selectedRows,
    });
  }

  @Bind()
  handleGroupDefaultChange(groupId) {
    this.setState({
      defaultGroupId: groupId,
    });
  }

  @Bind()
  handleGroupRemove() {
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.title').d('提示'),
      content: intl.get(`hiam.subAccount.view.message.title.content`).d('确定删除吗？'),
      onOk: () => {
        const { selectedRows = [], defaultGroupId = -1 } = this.state;
        const { userRecord = {}, onGroupRemove } = this.props;
        const remoteRemoveDataSource = [];
        let updateDefaultGroupId = defaultGroupId;
        forEach(selectedRows, r => {
          if (r.userGroupId === defaultGroupId) {
            updateDefaultGroupId = -1;
          }
          if (r._status === 'update') {
            remoteRemoveDataSource.push({
              ...r,
            });
          }
        });
        if (remoteRemoveDataSource.length > 0) {
          onGroupRemove({ userId: userRecord.id, remoteRemoveDataSource }).then(res => {
            if (res) {
              this.removeLocaleGroups(updateDefaultGroupId);
              notification.success();
            }
          });
        } else {
          this.removeLocaleGroups(updateDefaultGroupId);
        }
      },
    });
  }

  @Bind()
  removeLocaleGroups(updateDefaultGroupId) {
    const nextState = {};
    if (updateDefaultGroupId === -1) {
      nextState.defaultGroupId = -1;
    }
    const { dataSource = [], selectedRowKeys = [], pagination = {} } = this.state;
    const { isCreate = true } = this.props;
    nextState.dataSource = differenceWith(
      dataSource,
      selectedRowKeys,
      (r1, r2) => r1.userGroupId === r2
    );
    nextState.pagination = isCreate
      ? false
      : delItemsToPagination(selectedRowKeys.length, dataSource.length, pagination);
    nextState.selectedRowKeys = [];
    nextState.selectedRows = [];
    this.setState(nextState);
  }

  @Bind()
  handleGroupCreate() {
    const { userRecord = {}, isCreate = true } = this.props;
    const { dataSource = [] } = this.state;
    const userId = []; // 当前编辑账号的账号id( 需要的排除账号对应的用户组 )
    const excludeIds = [];
    if (!isCreate) {
      userId.push(userRecord.id);
    }
    dataSource.forEach(group => {
      if (group._status === 'create') {
        excludeIds.push(group.userGroupId);
      }
    });
    this.setState({
      groupModalProps: {
        visible: true,
        userId,
        excludeIds,
      },
    });
  }

  @Bind()
  handleGroupCreateSave(groups) {
    if (groups && groups.length > 0) {
      const { dataSource = [], pagination = {} } = this.state;
      const { isCreate = true, userRecord = {} } = this.props;
      this.setState({
        dataSource: [
          ...dataSource,
          ...groups.map(group => ({
            groupCode: group.groupCode,
            groupName: group.groupName,
            userGroupId: group.userGroupId,
            sourceType: group.level,
            defaultFlag: group.defaultFlag,
            tenantName: userRecord.tenantName,
            _status: 'create',
          })),
        ],
        pagination: isCreate
          ? false
          : addItemsToPagination(groups.length, dataSource.length, pagination),
      });
    }
    this.handleGroupCreateCancel();
  }

  @Bind()
  handleGroupCreateCancel() {
    this.setState({
      groupModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  handleEditModalOk() {
    const { userRecord = {} } = this.props;
    const { dataSource = [], defaultGroupId } = this.state;
    const memberGroupList = [];
    const validateDataSource = getEditTableData(dataSource);
    validateDataSource.forEach(r => {
      const newGroup = {
        assignId: r.assignId,
        // defaultFlag: r.defaultFlag,
        defaultFlag: r.userGroupId === defaultGroupId ? 1 : 0,
        groupCode: r.groupCode,
        groupName: r.groupName,
        tenantId,
        tenantName: userRecord.tenantName,
        userGroupId: r.userGroupId,
        userId: userRecord.id,
        _token: r._token,
        objectVersionNumber: r.objectVersionNumber,
      };
      const oldR = find(dataSource, or => or.userGroupId === r.userGroupId);
      if (
        oldR._status === 'create' ||
        (oldR._status === 'update' && oldR.defaultFlag !== newGroup.defaultFlag)
      ) {
        memberGroupList.push(newGroup);
      }
    });
    if (dataSource.length !== 0 && dataSource.length !== validateDataSource.length) {
      // 用户组校验 失败
      return;
    }
    const { onOk } = this.props;
    const saveData = {
      userId: userRecord.id,
      memberGroupList,
    };
    onOk(saveData);
  }
}
