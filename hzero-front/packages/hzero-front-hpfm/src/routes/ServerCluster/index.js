/**
 * 服务器集群管理
 * @date: 2019-7-9
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table, Popconfirm, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';

import Drawer from './Drawer';
import FilterForm from './FilterForm';

@connect(({ loading, serverCluster }) => ({
  serverCluster,
  isSiteFlag: !isTenantRoleLevel(),
  fetchServerClusterLoading: loading.effects['serverCluster/fetchServerClusterList'],
  saving: loading.effects['serverCluster/createServerCluster'],
  getServerClusterLoading: loading.effects['serverCluster/getServerClusterDetail'],
  updateServerClusterLoading: loading.effects['serverCluster/updateServerCluster'],
  fetchCanAssignListLoading: loading.effects['serverCluster/fetchCanAssignList'],
  createServerAssignLoading: loading.effects['serverCluster/createServerAssign'],
  organizationId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.serverCluster,entity.tenant'] })
export default class serverCluster extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      fieldsValue: {},
      detail: '', // 编辑时record.clusterId
      addModalVisible: false,
      isCreate: true, // 编辑表单是否是新建
    };
  }

  componentDidMount() {
    this.fetchServerClusterList();
  }

  /**
   * 获取服务器集群列表
   * @param {object} params
   */
  @Bind()
  fetchServerClusterList(params = {}) {
    const {
      dispatch,
      serverCluster: { serverClusterPagination = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'serverCluster/fetchServerClusterList',
      payload: { ...fieldsValue, page: serverClusterPagination, ...params },
    });
  }

  /**
   * 编辑打开模态框
   */
  @Bind()
  handleUpdateServerCluster(record) {
    const {
      dispatch,
      serverCluster: { serverPagination = {} },
    } = this.props;
    this.setState({ detail: record.clusterId, isCreate: false });
    this.handleModalVisible(true);
    dispatch({
      type: 'serverCluster/updateState',
      payload: {
        getServerClusterDetail: {},
      },
    });
    dispatch({
      type: 'serverCluster/getServerClusterDetail',
      payload: { clusterId: record.clusterId },
    });
    const { fieldsValue } = this.state;
    dispatch({
      type: 'serverCluster/fetchServerList',
      payload: {
        ...fieldsValue,
        page: serverPagination,
        clusterId: record.clusterId,
        // eslint-disable-next-line no-dupe-keys
        page: record.page, // 表格切换分页时的分页信息
      },
    });
  }

  /**
   * 显示服务器
   * @param {Object} params
   */
  @Bind()
  handleAddBtnClick(params = {}) {
    const {
      dispatch,
      serverCluster: { canAssignPagination = {} },
    } = this.props;
    this.handleAddModalVisible(true);
    dispatch({
      type: 'serverCluster/fetchCanAssignList',
      payload: { clusterId: this.state.detail, page: canAssignPagination, ...params },
    });
  }

  /**
   * 添加服务器
   * @param {array} selectedAddRows
   */
  @Bind()
  handleAddServer(selectedAddRows) {
    const {
      dispatch,
      serverCluster: { canAssignPagination = {}, serverClusterDetail: { tenantId } = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    if (selectedAddRows.length > 0) {
      dispatch({
        type: 'serverCluster/createServerAssign',
        payload: selectedAddRows.map((item) => {
          return {
            clusterId: this.state.detail,
            tenantId,
            serverId: item.serverId,
          };
        }),
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleAddModalVisible(false);
          dispatch({
            type: 'serverCluster/fetchServerList',
            payload: { ...fieldsValue, page: canAssignPagination, clusterId: this.state.detail },
          });
        }
      });
    } else {
      notification.warning({
        message: intl
          .get('hpfm.serverCluster.view.message.selectedAtLeast')
          .d('请至少选择一个服务'),
      });
    }
  }

  /**
   * 删除服务器
   */
  @Bind()
  handleDeleteValues(selectedDeleteRows) {
    const {
      dispatch,
      serverCluster: { serverPagination = {}, serverClusterDetail: { tenantId } = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    const onOk = () => {
      dispatch({
        type: 'serverCluster/deleteServerAssign',
        payload: selectedDeleteRows.map((item) => {
          return {
            clusterId: this.state.detail,
            tenantId,
            serverId: item.serverId,
          };
        }),
      }).then((res) => {
        if (res) {
          notification.success();
          dispatch({
            type: 'serverCluster/fetchServerList',
            payload: { ...fieldsValue, page: serverPagination, clusterId: this.state.detail },
          });
        }
      });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  /**
   * 保存服务器集群
   */
  @Bind()
  handleSaveServerCluster(fieldsValue) {
    const {
      dispatch,
      serverCluster: { serverClusterDetail = {} },
      organizationId,
    } = this.props;
    dispatch({
      type: `serverCluster/${
        serverClusterDetail.clusterId !== undefined ? 'updateServerCluster' : 'createServerCluster'
      }`,
      payload: { tenantId: organizationId, ...serverClusterDetail, ...fieldsValue },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchServerClusterList();
      }
    });
  }

  /**
   * 删除服务器集群信息
   * @param {Object} record
   */
  @Bind()
  handleDeleteServerCluster(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serverCluster/deleteServerCluster',
      payload: { ...record, clusterId: record.clusterId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchServerClusterList();
      }
    });
  }

  /**
   * 添加服务器分页切换
   * @param {Object} pagination
   */
  @Bind()
  canAssignTableChange(pagination = {}, fieldsValue) {
    this.handleAddBtnClick({
      page: pagination,
      ...fieldsValue,
    });
  }

  @Bind()
  handleFormSearch(fieldsValue) {
    this.handleAddBtnClick(fieldsValue);
  }

  /**
   * 服务器分页
   *  */
  @Bind()
  serverTableChange(pagination = {}) {
    const { detail } = this.state;
    this.handleUpdateServerCluster({
      page: pagination,
      clusterId: detail,
    });
    return true;
  }

  /**
   * 列表分页
   */
  @Bind()
  handleTableChange(pagination = {}) {
    this.fetchServerClusterList({
      page: pagination,
    });
  }

  /**
   * 查询服务器集群
   */
  @Bind()
  handleSearch(form) {
    const fieldsValue = form.getFieldsValue();
    this.setState({ fieldsValue }, () => {
      this.fetchServerClusterList({ ...fieldsValue, page: {} });
    });
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

  handleAddModalVisible(flag) {
    this.setState({ addModalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}) {
    const { dispatch } = this.props;
    this.setState({ isCreate: true });
    if (record.clusterId !== undefined) {
      dispatch({
        type: 'serverCluster/getServerClusterDetail',
        payload: { clusterId: record.clusterId },
      });
    }
    dispatch({
      type: 'serverCluster/updateState',
      payload: { serverClusterDetail: {} },
    });
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
    const { dispatch } = this.props;
    dispatch({
      type: 'serverCluster/updateState',
      payload: { serverList: [], canAssignList: [] },
    });
  }

  @Bind()
  hideAddModal() {
    this.handleAddModalVisible(false);
  }

  render() {
    const {
      updateServerClusterLoading = false,
      fetchServerClusterLoading = false,
      getServerClusterLoading = false,
      createServerAssignLoading = false,
      saving = false,
      fetchCanAssignListLoading = false,
      isSiteFlag,
      match,
      serverCluster: {
        serverClusterList = [],
        serverClusterPagination = {},
        serverPagination = {},
        canAssignPagination = {},
        serverClusterDetail = {},
        serverList = [],
        canAssignList = [],
      },
    } = this.props;
    const { modalVisible, addModalVisible, detailFirstLoadLoading, isCreate } = this.state;
    const serverClusterColumns = [
      isSiteFlag && {
        title: intl.get('entity.tenant.name').d('租户名称'),
        width: 150,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.clusterCode').d('集群编码'),
        width: 200,
        dataIndex: 'clusterCode',
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.clusterName').d('集群名称'),
        width: 200,
        dataIndex: 'clusterName',
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.clusterDescription').d('集群描述'),
        width: 200,
        dataIndex: 'clusterDescription',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '服务器集群-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleUpdateServerCluster(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.handleDeleteServerCluster(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '服务器集群-删除',
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
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);
    const scroll = {
      x: 549,
    };
    return (
      <React.Fragment>
        <Header
          title={intl
            .get('hpfm.serverCluster.view.message.title.serverCluster')
            .d('服务器集群定义')}
        >
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '服务器集群-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm
            search={this.handleSearch}
            reset={this.handleResetSearch}
            isSiteFlag={isSiteFlag}
          />
          <Table
            bordered
            rowKey="clusterId"
            loading={fetchServerClusterLoading}
            dataSource={serverClusterList}
            columns={serverClusterColumns}
            pagination={serverClusterPagination}
            onChange={this.handleTableChange}
            scroll={scroll}
          />
          <Drawer
            title={
              serverClusterDetail.clusterId
                ? intl.get('hpfm.serverCluster.view.message.edit').d('编辑集群')
                : intl.get('hpfm.serverCluster.view.message.create').d('新建集群')
            }
            isSiteFlag={isSiteFlag}
            initLoading={getServerClusterLoading}
            loading={
              serverClusterDetail.clusterId !== undefined ? updateServerClusterLoading : saving
            }
            match={match}
            modalLoading={fetchCanAssignListLoading}
            createServerAssignLoading={createServerAssignLoading}
            modalVisible={modalVisible}
            addModalVisible={addModalVisible}
            initData={serverClusterDetail}
            onCancel={this.hideModal}
            onAddCancel={this.hideAddModal}
            onOk={this.handleSaveServerCluster}
            detailFirstLoadLoading={detailFirstLoadLoading}
            serverList={serverList}
            canAssignList={canAssignList}
            showServer={this.handleAddBtnClick}
            addServer={this.handleAddServer}
            deleteServer={this.handleDeleteValues}
            serverPagination={serverPagination}
            canAssignPagination={canAssignPagination}
            isCreate={isCreate}
            serverTableChange={this.serverTableChange}
            canAssignTableChange={this.canAssignTableChange}
            onFormSearch={this.handleFormSearch} // TODO: 查询表单
          />
        </Content>
      </React.Fragment>
    );
  }
}
