/**
 * index - LDAP
 * @date: 2018-12-20
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Form, Modal, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentOrganizationId, encryptPwd } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import intl from 'utils/intl';

import LDAPForm from './LDAPForm';
import SyncUserDrawer from './SyncUserDrawer';
import TestConnectDrawer from './TestConnectDrawer';
import SyncUserErrorDrawer from './SyncUserErrorDrawer';
import SyncModal from './SyncModal';

/**
 * LDAP - 业务组件 - LDAP
 * @extends {Component} - React.Component
 * @reactProps {!Object} [client={}] - 数据源
 * @reactProps {!Object} [loading={}] - 加载是否完成
 * @reactProps {!Object} [loading.effect={}] - 加载是否完成
 * @reactProps {boolean} [fetchLoading=false] - 客户端列表信息加载中
 * @reactProps {boolean} [updateLoading=false] - 更新LDAP数据处理中
 * @reactProps {boolean} [testLoading=false] - 测试连接中
 * @reactProps {boolean} [enabledLoading=false] - 启用处理中
 * @reactProps {boolean} [disabledLoading=false] - 禁用处理中
 * @reactProps {boolean} [fetchSyncLoading=false] - 查询上一次同步用户数据处理中
 * @reactProps {boolean} [syncLoading=false] - 同步用户处理中
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ ldap, loading }) => ({
  ldap,
  fetchLoading: loading.effects['ldap/fetchLDAP'],
  updateLoading: loading.effects['ldap/updateLDAP'],
  testLoading: loading.effects['ldap/testConnect'],
  enabledLoading: loading.effects['ldap/enabledLDAP'],
  disabledLoading: loading.effects['ldap/disabledLDAP'],
  fetchSyncLoading: loading.effects['ldap/fetchSyncInfo'],
  fetchSyncErrorLoading: loading.effects['ldap/fetchSyncErrorInfo'],
  syncLoading: loading.effects['ldap/syncUser'],
  tenantId: getCurrentOrganizationId(),
  querySyncUserLoading: loading.effects['ldap/querySyncUser'],
  querySyncLeaveLoading: loading.effects['ldap/querySyncLeave'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hiam.ldap', 'entity.time'] })
export default class LDAP extends Component {
  form;

  state = {
    testConnectVisible: false,
    syncUserVisible: false,
    syncUserErrorVisible: false,
    showWhich: '',
    ldapData: {},
    ldapHistoryId: 0,
    syncVisible: false,
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ldap/queryDirectoryType',
    });
    dispatch({
      type: 'ldap/queryFrequency',
    });
    this.handleSearch();
    this.fetchPublicKey();
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'ldap/getPublicKey',
    });
  }

  /**
   * 查询LDAP数据
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch() {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'ldap/fetchLDAP',
      payload: {
        tenantId,
      },
    }).then((res) => {
      if (res) {
        this.setState({ ldapData: res });
      }
    });
  }

  // 保存并测试
  @Bind()
  handleSaveAndTest(values = {}) {
    const { dispatch, tenantId, ldap = {} } = this.props;
    const { publicKey } = ldap;
    const { ldapData } = this.state;
    const ldapStatus = values.useSSL === 'Y';
    const params = {
      ...values,
      id: ldapData.id,
      objectVersionNumber: ldapData.objectVersionNumber,
    };
    params.useSSL = ldapStatus;
    if (!params.port) {
      params.port = params.useSSL ? 636 : 389;
    }
    if (params.ldapPassword) {
      params.ldapPassword = encryptPwd(params.ldapPassword, publicKey);
    }
    dispatch({
      type: 'ldap/updateLDAP',
      payload: {
        tenantId,
        ...ldapData,
        ...params,
      },
    }).then((resp) => {
      if (resp) {
        notification.success();
        dispatch({
          type: 'ldap/fetchLDAP',
          payload: {
            tenantId,
          },
        }).then((res) => {
          if (res) {
            this.setState({ ldapData: res }, () => {
              if (res.enabled) {
                this.setState({ testConnectVisible: true, showWhich: 'adminConnect' }, () => {
                  this.handleTest(params);
                });
              }
            });
          }
        });
      }
    });
  }

  // 测试
  @Bind()
  handleTest(params) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'ldap/testConnect',
      payload: {
        tenantId,
        ...params,
      },
    });
  }

  // 启用、禁用
  @Bind()
  enableLdap() {
    const { dispatch, tenantId } = this.props;
    const { ldapData } = this.state;
    if (ldapData.enabled) {
      Modal.confirm({
        title: intl.get('hiam.ldap.view.message.confirm').d('确认'),
        content: intl
          .get('hiam.ldap.view.message.content')
          .d(
            '确定要禁用LDAP吗？禁用LDAP后，之前所同步的用户将无法登录平台，且无法使用测试连接和同步用户功能'
          ),
        onOk: () => {
          dispatch({
            type: 'ldap/disabledLDAP',
            payload: {
              tenantId,
              // id: ldapData.id,
              ...ldapData,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              this.handleSearch();
            }
          });
        },
      });
    } else {
      dispatch({
        type: 'ldap/enabledLDAP',
        payload: {
          tenantId,
          ...ldapData,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch();
        }
      });
    }
  }

  /* 获取同步用户信息 */
  @Bind()
  getSyncInfo() {
    const { dispatch, tenantId } = this.props;
    const { ldapData } = this.state;
    dispatch({
      type: 'ldap/fetchSyncInfo',
      payload: {
        tenantId,
        id: ldapData.id,
      },
    });
  }

  // 同步用户
  @Bind()
  handleSyncUser() {
    const { dispatch, tenantId } = this.props;
    const { ldapData } = this.state;
    dispatch({
      type: 'ldap/syncUser',
      payload: {
        tenantId,
        id: ldapData.id,
      },
    });
  }

  // 打开连接测试弹窗
  @Bind()
  showTestModal() {
    this.setState({ testConnectVisible: true });
  }

  // 关闭连接测试弹窗
  @Bind()
  hiddenTestModal() {
    this.setState({ testConnectVisible: false, showWhich: '' });
  }

  // 打开同步用户弹窗
  @Bind()
  showSyncModal() {
    this.setState({ syncUserVisible: true }, () => {
      this.getSyncInfo(); // 打开弹窗时获取同步用户的信息
    });
  }

  // 打开查看同步记录弹窗
  @Bind()
  showViewSyncModal() {
    this.setState({ syncUserVisible: true }, () => {
      this.getSyncInfo(); // 打开弹窗时获取同步用户的信息
    });
  }

  // 关闭同步用户弹窗
  @Bind()
  hiddenSyncModal() {
    this.setState({ syncUserVisible: false });
  }

  // 打开数据同步错误信息弹窗
  @Bind()
  showSyncErrorModal(record) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'ldap/fetchSyncErrorInfo',
      payload: {
        tenantId,
        ldapHistoryId: record.id,
        page: record.page,
      },
    });
    this.setState({ syncUserErrorVisible: true, ldapHistoryId: record.id });
  }

  // 关闭连接测试弹窗
  @Bind()
  hiddenSyncErrorModal() {
    this.setState({ syncUserErrorVisible: false });
  }

  @Bind()
  handleSync() {
    this.setState({
      syncVisible: true,
    });
    const { dispatch } = this.props;
    dispatch({
      type: 'ldap/querySyncLeave',
      payload: {
        tenantId: getCurrentOrganizationId(),
      },
    });
    dispatch({
      type: 'ldap/querySyncUser',
      payload: {
        tenantId: getCurrentOrganizationId(),
      },
    });
  }

  @Bind()
  handleSyncConfigCancel() {
    this.setState({
      syncVisible: false,
    });
  }

  render() {
    const {
      dispatch,
      tenantId,
      fetchLoading = false,
      updateLoading = false,
      disabledLoading = false,
      enabledLoading = false,
      testLoading = false,
      syncLoading = false,
      fetchSyncLoading = false,
      fetchSyncErrorLoading = false,
      querySyncLeaveLoading = false,
      querySyncUserLoading = false,
      match: { path },
      ldap: {
        testData = {},
        directoryTypeList = [],
        syncErrorInfo,
        syncPagination,
        syncErrorPagination,
        syncInfo = [],
        publicKey,
        frequencyList = [],
        syncUserDetail = {},
        syncLeaveDetail = {},
      },
      ldap,
    } = this.props;
    const {
      testConnectVisible,
      syncUserVisible,
      syncUserErrorVisible,
      showWhich,
      ldapHistoryId,
      ldapData = {},
      syncVisible,
    } = this.state;
    const ldapProps = {
      path,
      updateLoading,
      ldapData,
      directoryTypeList,
      onSaveAndTest: this.handleSaveAndTest,
    };
    const testProps = {
      dispatch,
      tenantId,
      showWhich,
      testConnectVisible,
      testLoading,
      ldapData,
      testData,
      publicKey,
      onCancel: this.hiddenTestModal,
    };
    const syncProps = {
      path,
      dispatch,
      syncUserVisible,
      syncLoading,
      ldapData,
      syncInfoData: syncInfo,
      syncPagination,
      tenantId,
      onShow: this.showSyncErrorModal,
      onSearch: this.getSyncInfo,
      onOk: this.hiddenSyncModal,
      onCancel: this.hiddenSyncModal,
    };
    const syncErrorProps = {
      syncUserErrorVisible,
      lodaing: fetchSyncErrorLoading,
      ldapHistoryId,
      syncErrorInfo,
      syncErrorPagination,
      onShow: this.showSyncErrorModal,
      onCancel: this.hiddenSyncErrorModal,
    };

    const syncConfigProps = {
      visible: syncVisible,
      onCancel: this.handleSyncConfigCancel,
      dispatch,
      frequencyList,
      ldap,
      querySyncUserLoading,
      querySyncLeaveLoading,
      ldapId: ldapData && ldapData.id,
      syncUserDetail,
      syncLeaveDetail,
    };

    return (
      <>
        <Header title="LDAP">
          {ldapData.enabled ? (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.stop`,
                  type: 'button',
                  meaning: 'LDAP-禁用',
                },
              ]}
              type="primary"
              icon="minus-circle"
              onClick={this.enableLdap}
              loading={disabledLoading}
            >
              {intl.get('hzero.common.button.disable').d('禁用')}
            </ButtonPermission>
          ) : (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.enabled`,
                  type: 'button',
                  meaning: 'LDAP-启用',
                },
              ]}
              type="primary"
              icon="check-circle"
              onClick={this.enableLdap}
              loading={enabledLoading}
            >
              {intl.get('hzero.common.button.enable').d('启用')}
            </ButtonPermission>
          )}
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.testConnect`,
                type: 'button',
                meaning: 'LDAP-测试连接',
              },
            ]}
            icon="api"
            onClick={this.showTestModal}
          >
            {intl.get('hiam.ldap.view.option.testConnect').d('测试连接')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.syncUser`,
                type: 'button',
                meaning: 'LDAP-同步用户',
              },
            ]}
            icon="swap"
            onClick={this.handleSyncUser}
            loading={syncLoading}
          >
            {intl.get('hiam.ldap.view.option.syncUser').d('同步用户')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.viewSyncUser`,
                type: 'button',
                meaning: 'LDAP-查看同步记录',
              },
            ]}
            icon="eye-o"
            onClick={this.showViewSyncModal}
          >
            {intl.get('hiam.ldap.view.option.viewSyncUser').d('查看同步记录')}
          </ButtonPermission>
          <Button icon="sync" onClick={this.handleSearch}>
            {intl.get('hzero.common.button.reload').d('重新加载')}
          </Button>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.syncConfig`,
                type: 'button',
                meaning: 'LDAP-定时同步配置',
              },
            ]}
            icon="edit"
            onClick={this.handleSync}
          >
            {intl.get('hiam.ldap.view.option.syncConfig').d('定时同步配置')}
          </ButtonPermission>
        </Header>
        <Content>
          <Spin spinning={fetchLoading}>
            <LDAPForm {...ldapProps} />
          </Spin>
          {testConnectVisible && <TestConnectDrawer {...testProps} />}
          {syncUserVisible && (
            <Spin spinning={fetchSyncLoading}>
              <SyncUserDrawer {...syncProps} />
            </Spin>
          )}
          <SyncUserErrorDrawer {...syncErrorProps} />
          <SyncModal {...syncConfigProps} />
        </Content>
      </>
    );
  }
}
