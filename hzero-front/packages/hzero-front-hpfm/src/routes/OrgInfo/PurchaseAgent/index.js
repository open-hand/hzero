/**
 * index.js - 采购员维护
 * @date: 2018-10-26
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React, { Fragment, PureComponent } from 'react';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  addItemToPagination,
  delItemToPagination,
  getCurrentOrganizationId,
  getEditTableData,
} from 'utils/utils';

import ListTable from './ListTable';
import FilterForm from './FilterForm';
import UserModal from './UserModal';

@connect(({ loading, purchaseAgent }) => ({
  purchaseAgent,
  loading: loading.effects['purchaseAgent/queryPurchaseAgent'],
  saving: loading.effects['purchaseAgent/savePurchaseAgent'],
  fetchUserLoading: loading.effects['purchaseAgent/fetchUserList'],
  saveUserLoading: loading.effects['purchaseAgent/updateUser'],
  deleteUserLoading: loading.effects['purchaseAgent/deleteUser'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: 'hpfm.purchaseAgent',
})
export default class PurchaseAgent extends PureComponent {
  constructor(props) {
    super(props);
    this.filterForm = {}; // 获取查询表单对象
    this.rowKey = 'purchaseAgentId';
    this.queryPageSize = 10;
    this.state = {
      userModalVisible: false,
      selectUserRecord: {}, // 指定用户操作的行
    };
  }

  componentDidMount() {
    const {
      purchaseAgent: { pagination },
    } = this.props;
    this.handleSearch(pagination);
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        pagination: {},
        list: {},
      },
    });
  }

  /**
   * 查询采购员列表数据
   * @param {Object} params - 查询条件及分页参数对象
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch, tenantId } = this.props;
    const values = this.filterForm.props && this.filterForm.props.form.getFieldsValue();
    dispatch({
      type: 'purchaseAgent/queryPurchaseAgent',
      payload: {
        tenantId,
        page,
        ...values,
      },
    });
  }

  /**
   * 编辑行
   * @param {Obj} record
   */
  @Bind()
  handleEdit(record) {
    const {
      purchaseAgent: { list = {} },
      dispatch,
    } = this.props;
    const index = list.content.findIndex(item => item[this.rowKey] === record[this.rowKey]);
    const newList = {
      ...list,
      content: [
        ...list.content.slice(0, index),
        {
          ...record,
          _status: 'update',
        },
        ...list.content.slice(index + 1),
      ],
    };

    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        list: newList,
      },
    });
  }

  /**
   * 新建行
   */
  @Bind()
  handleCreate() {
    const {
      dispatch,
      commonSourceCode,
      commonExternalSystemCode,
      purchaseAgent: { list = {}, pagination = {} },
    } = this.props;
    const newLine = {
      isCreate: true,
      enabledFlag: 1,
      purchaseAgentCode: '',
      purchaseAgentName: '',
      contactInfo: '',
      userId: '',
      purchaseAgentId: uuidv4(),
      sourceCode: commonSourceCode,
      externalSystemCode: commonExternalSystemCode,
      _status: 'create',
    };
    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        list: {
          ...list,
          content: [newLine, ...list.content],
        },
        pagination: addItemToPagination(list.content.length, pagination),
      },
    });
  }

  /**
   * 保存，校验成功保存新增行和修改行
   * @memberof StoreRoom
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      purchaseAgent: { list = {}, pagination = {} },
      tenantId,
    } = this.props;
    const { content } = list;
    const params = getEditTableData(content, [this.rowKey]);
    if (Array.isArray(params) && params.length === 0) {
      return;
    }
    dispatch({
      type: 'purchaseAgent/savePurchaseAgent',
      payload: {
        tenantId,
        list: params,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 取消编辑行
   * @param {Obj} record
   * @memberof StoreRoom
   */
  @Bind()
  handleCancel(record) {
    const {
      purchaseAgent: { list = {} },
      dispatch,
    } = this.props;
    const index = list.content.findIndex(item => item[this.rowKey] === record[this.rowKey]);
    const { _status, ...other } = record;
    const newList = {
      ...list,
      content: [...list.content.slice(0, index), other, ...list.content.slice(index + 1)],
    };

    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        list: newList,
      },
    });
  }

  /**
   * 删除新建行
   * @param {*} record
   */
  @Bind()
  handleDelete(record) {
    const {
      dispatch,
      purchaseAgent: { list = {}, pagination },
    } = this.props;
    const newList = list.content.filter(item => item[this.rowKey] !== record[this.rowKey]);
    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        list: { ...list, content: newList },
        pagination: delItemToPagination(list.content.length, pagination),
      },
    });
  }

  @Bind()
  showUserModal(flag) {
    this.setState({ userModalVisible: flag });
  }

  @Bind()
  handleSelectUser(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        userPagination: {},
        userList: [],
      },
    });
    this.showUserModal(true);
    this.fetchUserList({ purchaseAgentId: record.purchaseAgentId });
    this.setState({ selectUserRecord: record });
  }

  @Bind()
  fetchUserList(params = {}) {
    const { dispatch } = this.props;
    const { selectUserRecord = {} } = this.state;
    const { purchaseAgentId } = selectUserRecord;
    dispatch({
      type: 'purchaseAgent/fetchUserList',
      payload: { purchaseAgentId, ...params },
    });
  }

  @Bind()
  handleSaveUser() {
    const { dispatch, purchaseAgent = {} } = this.props;
    const { userList = [] } = purchaseAgent;
    const params = getEditTableData(userList, ['userKey']);
    if (Array.isArray(params) && params.length > 0) {
      dispatch({
        type: 'purchaseAgent/updateUser',
        payload: params,
      }).then(res => {
        if (res) {
          notification.success();
          this.fetchUserList();
        }
      });
    }
  }

  @Bind()
  handleDeleteUser(data = [], keys = []) {
    const {
      dispatch,
      purchaseAgent: { userList = [] },
    } = this.props;
    const filterData = data.filter(item => item._status !== 'create');
    // 删除未保存的数据
    const createList = data.filter(item => item._status === 'create');
    if (createList.length > 0) {
      const deleteList = userList.filter(item => !keys.includes(item.userId));
      dispatch({
        type: 'purchaseAgent/updateState',
        payload: { userList: deleteList },
      });
      notification.success();
    }
    if (filterData.length > 0) {
      dispatch({
        type: 'purchaseAgent/deleteUser',
        payload: filterData,
      }).then(res => {
        if (res) {
          notification.success();
          this.fetchUserList();
        }
      });
    }
  }

  @Bind()
  handleCancelUser() {
    this.showUserModal(false);
  }

  @Bind()
  handleCreateUser(data) {
    const {
      dispatch,
      purchaseAgent: { userList = [], userPagination = {} },
    } = this.props;
    dispatch({
      type: 'purchaseAgent/updateState',
      payload: {
        userList: [data, ...userList],
        userPagination: addItemToPagination(userList.length, userPagination),
      },
    });
  }

  render() {
    const {
      form,
      loading,
      saving,
      tenantId,
      commonSourceCode,
      match,
      deleteUserLoading = false,
      saveUserLoading = false,
      fetchUserLoading = false,
      purchaseAgent: {
        list: { content = [] },
        pagination = {},
        userList = [],
        userPagination = {},
      },
    } = this.props;
    const { userModalVisible = false, selectUserRecord = {} } = this.state;
    const hasEdit = content.findIndex(item => !!item._status) !== -1;
    const listProps = {
      commonSourceCode,
      loading,
      pagination,
      form,
      tenantId,
      match,
      rowKey: this.rowKey,
      dataSource: content,
      onEdit: this.handleEdit,
      onDelete: this.handleDelete,
      onCancel: this.handleCancel,
      onSearch: this.handleSearch,
      onSelectUser: this.handleSelectUser,
    };
    const userProps = {
      match,
      userPagination,
      selectUserRecord,
      modalVisible: userModalVisible,
      saveLoading: saveUserLoading || deleteUserLoading,
      initLoading: fetchUserLoading,
      dataSource: userList,
      onCreate: this.handleCreateUser,
      onDelete: this.handleDeleteUser,
      onOk: this.handleSaveUser,
      onCancel: this.handleCancelUser,
      onChange: this.fetchUserList,
    };

    return (
      <Fragment>
        <Header title={intl.get('hpfm.purchaseAgent.view.title.purchaseAgent').d('采购员')}>
          <ButtonPermission
            type="primary"
            icon="save"
            onClick={this.handleSave}
            loading={(saving || loading) && hasEdit}
            disabled={!hasEdit}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '采购员-新建',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">
            <FilterForm
              onRef={ref => {
                this.filterForm = ref;
              }}
              onSearch={this.handleSearch}
            />
          </div>
          <ListTable {...listProps} />
          <UserModal {...userProps} />
        </Content>
      </Fragment>
    );
  }
}
