/**
 * userGroupManagement 用户组管理
 * @date: 2019-1-14
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';

import MessageDrawer from './Drawer';
import FilterForm from './FilterForm';

@connect(({ userGroupManagement, loading }) => ({
  userGroupManagement,
  fetchUserGroupLoading: loading.effects['userGroupManagement/fetchUserGroupList'],
  createUserGroupLoading: loading.effects['userGroupManagement/createUserGroup'],
  getUserGroupLoading: loading.effects['userGroupManagement/getUserGroupDetail'],
  updateUserGroupLoading: loading.effects['userGroupManagement/updateUserGroup'],
  getCurrentRestGroupUsersLoading: loading.effects['userGroupManagement/getCurrentRestGroupUsers'],
  assignUsersToGroupLoading: loading.effects['userGroupManagement/assignUsersToGroup'],
  delCurrentGroupUsersLoading: loading.effects['userGroupManagement/delCurrentGroupUsers'],
  getCurrentGroupUsersLoading: loading.effects['userGroupManagement/getCurrentGroupUsers'],
}))
@formatterCollections({ code: ['hiam.userGroupManagement', 'hiam.database', 'hiam.nodeRule'] })
@Form.create({ fieldNameProp: null })
export default class userGroupManagement extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      fieldsValue: {},
    };
  }

  componentDidMount() {
    this.fetchUserGroupList();
  }

  /**
   * 获取用户组列表
   * @param {object} params
   * @memberof userGroupManagement
   */
  @Bind()
  fetchUserGroupList(params = {}) {
    const {
      dispatch,
      userGroupManagement: { pagination = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'userGroupManagement/fetchUserGroupList',
      payload: { ...fieldsValue, page: pagination, ...params },
    });
  }

  /**
   * 编辑打开模态框
   */
  @Bind()
  handleUpdateUserGroup(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'userGroupManagement/updateState',
      payload: {
        userGroupDetail: {},
      },
    });
    this.handleModalVisible(true);
    dispatch({
      type: 'userGroupManagement/getUserGroupDetail',
      payload: { userGroupId: record.userGroupId },
    });
  }

  /**
   * 保存用户组
   */
  @Bind()
  handleSaveUserGroup(fieldsValue) {
    const {
      dispatch,
      userGroupManagement: { userGroupDetail = {} },
    } = this.props;
    dispatch({
      type: `userGroupManagement/${
        userGroupDetail.userGroupId !== undefined ? 'updateUserGroup' : 'createUserGroup'
      }`,
      payload: { ...userGroupDetail, ...fieldsValue },
    }).then(res => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchUserGroupList();
      }
    });
  }

  /**
   * 删除
   */
  @Bind()
  handleDeleteUserGroup(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'userGroupManagement/deleteUserGroup',
      payload: { ...record, userGroupId: record.userGroupId },
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchUserGroupList();
      }
    });
  }

  /**
   * 分页
   */
  @Bind()
  handleTableChange(pagination) {
    this.fetchUserGroupList({
      page: pagination,
    });
  }

  /**
   * 查询消息
   */
  @Bind()
  handleSearch(form) {
    const fieldsValue = form.getFieldsValue();
    this.setState({ fieldsValue });
    this.fetchUserGroupList({ ...fieldsValue, page: {} });
  }

  /**
   * 重置表单
   */
  @Bind()
  handleResetSearch(form) {
    this.setState({ fieldsValue: {} });
    form.resetFields();
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}) {
    const { dispatch } = this.props;
    if (record.userGroupId !== undefined) {
      dispatch({
        type: 'userGroupManagement/getMessageDetail',
        payload: { userGroupId: record.userGroupId },
      });
    }
    dispatch({
      type: 'userGroupManagement/updateState',
      payload: { userGroupDetail: {} },
    });
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  @Bind()
  concatColumns() {
    if (!isTenantRoleLevel()) {
      return [
        {
          title: intl.get('hiam.database.model.database.tenantName').d('租户名称'),
          width: 200,
          dataIndex: 'tenantName',
        },
      ];
    }
    return [];
  }

  @Bind()
  getCurrentRestGroupUsers(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'userGroupManagement/getCurrentRestGroupUsers', payload });
  }

  @Bind()
  assignUsersToGroup(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'userGroupManagement/assignUsersToGroup', payload });
  }

  @Bind()
  delCurrentGroupUsers(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'userGroupManagement/delCurrentGroupUsers', payload });
  }

  @Bind()
  getCurrentGroupUsers(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'userGroupManagement/getCurrentGroupUsers', payload });
  }

  render() {
    const {
      updateUserGroupLoading = false,
      fetchUserGroupLoading = false,
      getUserGroupLoading = false,
      createUserGroupLoading = false,
      getCurrentRestGroupUsersLoading,
      assignUsersToGroupLoading,
      delCurrentGroupUsersLoading,
      getCurrentGroupUsersLoading,
      userGroupManagement: { userGroupList = [], pagination = {}, userGroupDetail = {} },
      match: { path },
    } = this.props;
    const { modalVisible } = this.state;
    const defaultUserGroupColumns = [
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.groupCode').d('用户组编码'),
        width: 200,
        dataIndex: 'groupCode',
      },
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.groupName').d('用户组名称'),
        width: 200,
        dataIndex: 'groupName',
      },
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.remark').d('备注说明'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 90,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        fixed: 'right',
        width: 110,
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.editList`,
                      type: 'button',
                      meaning: '用户组管理-编辑列表',
                    },
                  ]}
                  onClick={() => this.handleUpdateUserGroup(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'detele',
              ele: (
                <Popconfirm
                  title={intl
                    .get('hiam.nodeRule.view.message.confirm.remove')
                    .d('是否删除此条记录？')}
                  onConfirm={() => this.handleDeleteUserGroup(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.deleteList`,
                        type: 'button',
                        meaning: '用户组管理-删除列表',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    const userGroupColumns = this.concatColumns().concat(defaultUserGroupColumns);
    const scroll = {
      x: tableScrollWidth(userGroupColumns),
    };
    return (
      <>
        <Header title={intl.get('hiam.userGroupManagement.view.message.title').d('用户组管理')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '用户组管理-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm search={this.handleSearch} reset={this.handleResetSearch} />
          </div>
          <Table
            bordered
            rowKey="userGroupId"
            loading={fetchUserGroupLoading}
            dataSource={userGroupList}
            columns={userGroupColumns}
            pagination={pagination}
            onChange={this.handleTableChange}
            scroll={scroll}
          />
          <MessageDrawer
            title={
              userGroupDetail.userGroupId
                ? intl.get('hiam.userGroupManagement.view.message.edit').d('编辑用户组')
                : intl.get('hiam.userGroupManagement.view.message.create').d('新建用户组')
            }
            path={path}
            initLoading={getUserGroupLoading}
            loading={
              userGroupDetail.userGroupId !== undefined
                ? updateUserGroupLoading
                : createUserGroupLoading
            }
            modalVisible={modalVisible}
            initData={userGroupDetail}
            onCancel={this.hideModal}
            onOk={this.handleSaveUserGroup}
            // UserGroup'users
            getCurrentRestGroupUsers={this.getCurrentRestGroupUsers}
            assignUsersToGroup={this.assignUsersToGroup}
            delCurrentGroupUsers={this.delCurrentGroupUsers}
            getCurrentGroupUsers={this.getCurrentGroupUsers}
            getCurrentRestGroupUsersLoading={getCurrentRestGroupUsersLoading}
            assignUsersToGroupLoading={assignUsersToGroupLoading}
            delCurrentGroupUsersLoading={delCurrentGroupUsersLoading}
            getCurrentGroupUsersLoading={getCurrentGroupUsersLoading}
          />
        </Content>
      </>
    );
  }
}
