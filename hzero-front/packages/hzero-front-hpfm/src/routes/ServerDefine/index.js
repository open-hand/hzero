/**
 * server-define 服务器定义
 * @date: 2019-7-1
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth, filterNullValueObject, isTenantRoleLevel } from 'utils/utils';

import FilterForm from './FilterForm';
import Drawer from './Drawer';
import ClusterModal from './ClusterModal';
import PwdModal from './PwdModal';

@connect(({ serverDefine, loading }) => ({
  serverDefine,
  isSiteFlag: !isTenantRoleLevel(),
  fetchServerListLoading: loading.effects['serverDefine/fetchServerList'],
  getServerDetailLoading: loading.effects['serverDefine/getServerDetail'],
  getServerClusterLoading: loading.effects['serverDefine/getServerCluster'],
  createServerLoading: loading.effects['serverDefine/createServer'],
  editServerLoading: loading.effects['serverDefine/editServer'],
  resetPsswordLoading: loading.effects['serverDefine/resetPssword'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.serverDefine', 'hsgp.nodeRule', 'entity.tenant'],
})
export default class ServerDefine extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      serverModalVisible: false,
      pwdModalVisible: false,
      flag: false,
    };
  }

  componentDidMount() {
    this.handleSearch();
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HPFM.PROTOCOL_TYPE' };
    dispatch({
      type: 'serverDefine/init',
      payload: {
        lovCodes,
      },
    });
    // 获取公钥
    dispatch({
      type: 'serverDefine/getPublicKey',
    });
  }

  @Bind()
  cloneAction(action /* record */) {
    // if (record.status === 'operating') {
    //   // 在 record.status === 'operating' 时 之前 Dropdown 是禁用的;
    //   return React.cloneElement(action.ele, { key: action.key, onClick: undefined, onConfirm: undefined });
    // } else {
    return React.cloneElement(action.ele, { key: action.key });
    // }
  }

  /**
   * @function handleSearch - 获取服务器定义列表数据
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/fetchServerList',
      payload: { ...params, ...this.form.getFieldsValue() },
    });
  }

  /**
   * @function handleResetSearch - 重置查询表单
   * @param {object} params - 查询参数
   */
  @Bind()
  handleResetSearch() {
    this.form.resetFields();
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination = {}) {
    this.handleSearch({
      page: pagination,
    });
  }

  /**
   * 控制serverModal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  @Bind()
  handleServerModalVisible(flag) {
    this.setState({ serverModalVisible: !!flag });
  }

  @Bind()
  handlePwdModalVisibleVisible(flag) {
    this.setState({ pwdModalVisible: !!flag });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  @Bind()
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showServerModal(record = {}) {
    this.handleServerModalVisible(true);
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/getServerCluster',
      payload: record,
    });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showPwdModal(record = {}) {
    this.handlePwdModalVisibleVisible(true);
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/getServerDetail',
      payload: record,
    });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}, flag) {
    this.handleModalVisible(true);
    const { dispatch } = this.props;
    this.setState({ flag });
    if (JSON.stringify(record) !== '{}') {
      dispatch({
        type: 'serverDefine/getServerDetail',
        payload: record,
      }).then(() => {});
    }
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideServerModalVisible() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/updateState',
      payload: {
        serverCluster: [],
      },
    });
    this.handleServerModalVisible(false);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hidePwdModalVisibleVisible() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/updateState',
      payload: {
        serverDetail: {},
      },
    });
    this.handlePwdModalVisibleVisible(false);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/updateState',
      payload: {
        serverDetail: {},
      },
    });
    this.setState({ flag: false });
    this.handleModalVisible(false);
  }

  /**
   * 模态窗确认
   */
  @Bind()
  handleConfirm(fieldValues, flag) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(fieldValues) ? {} : filterNullValueObject(fieldValues);
    if (flag) {
      dispatch({
        type: 'serverDefine/createServer',
        payload: filterValues,
      }).then(res => {
        if (res) {
          this.handleResetSearch();
          this.hideModal();
          notification.success();
          this.handleSearch();
        }
      });
    } else if (!flag) {
      dispatch({
        type: 'serverDefine/editServer',
        payload: filterValues,
      }).then(res => {
        if (res) {
          this.handleResetSearch();
          this.hideModal();
          notification.success();
          this.handleSearch();
        }
      });
    }
  }

  @Bind()
  handlePwdConfirm(fieldValues) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(fieldValues) ? {} : filterNullValueObject(fieldValues);
    dispatch({
      type: 'serverDefine/resetPssword',
      payload: filterValues,
    }).then(res => {
      if (res) {
        this.handleResetSearch();
        this.hidePwdModalVisibleVisible();
        notification.success();
        this.handleSearch();
      }
    });
  }

  /**
   * 删除服务器
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serverDefine/deleteServer',
      payload: record,
    }).then(res => {
      if (res) {
        this.handleResetSearch();
        notification.success();
        this.handleSearch();
      }
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  render() {
    const {
      serverDefine: {
        typeList = [],
        pagination = {},
        serverList = [],
        serverDetail = {},
        serverCluster = [],
        publicKey = '',
      },
      match,
      isSiteFlag,
      fetchServerListLoading = false,
      getServerDetailLoading = false,
      getServerClusterLoading = false,
      createServerLoading = false,
      resetPsswordLoading = false,
      editServerLoading = false,
    } = this.props;
    const { modalVisible, serverModalVisible, pwdModalVisible, flag } = this.state;

    const columns = [
      isSiteFlag && {
        title: intl.get('entity.tenant.name').d('租户名称'),
        width: 150,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hpfm.serverDefine.model.serverDefine.serverCode').d('服务器代码'),
        width: 150,
        dataIndex: 'serverCode',
      },
      {
        title: intl.get('hpfm.serverDefine.model.serverDefine.serverName').d('服务器名称'),
        dataIndex: 'serverName',
        Width: 150,
      },
      {
        title: intl.get('hpfm.serverDefine.model.serverDefine.protocolCode').d('服务器协议'),
        width: 100,
        dataIndex: 'protocolMeaning',
      },
      {
        title: intl.get('hpfm.serverDefine.model.serverDefine.serverDescription').d('服务器说明'),
        dataIndex: 'serverDescription',
      },
      {
        title: intl.get('hzero.common.status.ip').d('IP'),
        width: 130,
        dataIndex: 'ip',
      },
      {
        title: intl.get('hzero.common.status.port').d('端口'),
        width: 70,
        dataIndex: 'port',
      },
      {
        title: intl.get('hzero.common.status.loginUser').d('登录用户名'),
        width: 150,
        dataIndex: 'loginUser',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 90,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 240,
        fixed: 'right',
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
                      meaning: '服务器定义-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showModal(record, false);
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
                  title={intl
                    .get('hpfm.nodeRule.view.message.confirm.remove')
                    .d('是否删除此条记录？')}
                  onConfirm={() => this.handleDelete(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '服务器定义-删除',
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
            {
              key: 'checkServerCluster',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.checkServerCluster`,
                      type: 'button',
                      meaning: '服务器定义-查看集群',
                    },
                  ]}
                  onClick={() => {
                    this.showServerModal(record);
                  }}
                >
                  {intl
                    .get('hpfm.serverDefine.model.serverDefine.checkServerCluster')
                    .d('查看集群')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl
                .get('hpfm.serverDefine.model.serverDefine.checkServerCluster')
                .d('查看集群'),
            },
            {
              key: 'action',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.updatePassword`,
                      type: 'button',
                      meaning: '服务器定义-更新密码',
                    },
                  ]}
                  onClick={() => {
                    this.showPwdModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.updatePassword').d('更新密码')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hzero.common.button.updatePassword').d('更新密码'),
            },
          ];
          return operatorRender(operators, record, { limit: 4 });
        },
      },
    ].filter(Boolean);
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.serverDefine.view.message.title.list').d('服务器定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '服务器定义-新建',
              },
            ]}
            onClick={() => this.showModal({}, true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm
            onSearch={this.handleSearch}
            onRef={this.handleBindRef}
            typeList={typeList}
            isSiteFlag={isSiteFlag}
          />
          <Table
            bordered
            rowKey="serverId"
            dataSource={serverList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            loading={fetchServerListLoading}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <Drawer
            title={
              flag
                ? intl.get('hpfm.serverDefine.view.message.modal.create').d('新建')
                : intl.get('hpfm.serverDefine.view.message.modal.edit').d('编辑')
            }
            isSiteFlag={isSiteFlag}
            modalVisible={modalVisible}
            onOk={this.handleConfirm}
            onCancel={this.hideModal}
            typeList={typeList}
            flag={flag}
            initLoading={getServerDetailLoading}
            initData={serverDetail}
            loading={createServerLoading || editServerLoading}
            publicKey={publicKey}
          />
          <PwdModal
            title={intl.get('hpfm.serverDefine.view.message.modal.password').d('更新密码')}
            modalVisible={pwdModalVisible}
            onOk={this.handlePwdConfirm}
            onCancel={this.hidePwdModalVisibleVisible}
            loading={resetPsswordLoading}
            initData={serverDetail}
            publicKey={publicKey}
          />

          <ClusterModal
            title={intl.get('hpfm.serverDefine.view.message.modal.serverCluster').d('服务器集群')}
            modalVisible={serverModalVisible}
            onCancel={this.hideServerModalVisible}
            loading={getServerClusterLoading}
            initData={serverCluster}
          />
        </Content>
      </React.Fragment>
    );
  }
}
