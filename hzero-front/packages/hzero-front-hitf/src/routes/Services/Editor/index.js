/*
 * index - 服务注册编辑页
 * @date: 2018-10-25
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Button, Drawer, Spin } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
// import { Content } from 'components/Page';
import { createPagination } from 'utils/utils';
import intl from 'utils/intl';
import EditorForm from './Form';
import InterfaceList from './InterfaceList';
import AuthenticationServiceModal from './AuthenticationServiceModal';
import styles from './index.less';

export default class ServiceEditor extends PureComponent {
  constructor(props) {
    super(props);
    this.handleFetchInterfaceDetail = this.handleFetchInterfaceDetail.bind(this);
  }

  state = {
    formDataSource: {}, // 表单数据
    interfaceListSelectedRows: [],
    interfaceListDataSource: [], // 接口数据列表
    interfaceListPagination: createPagination({ number: 0, size: 10, totalElements: 0 }), // 接口分页信息
    authenticationServiceModalVisible: false,
  };

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, interfaceServerId } = this.props;
    return (
      visible &&
      !isUndefined(interfaceServerId) &&
      interfaceServerId !== prevProps.interfaceServerId
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchInterfaceDetail();
    }
  }

  // 查询详，将详情数据、接口列表、接口分页设置到state中
  handleFetchInterfaceDetail(params = {}) {
    const { fetchInterfaceDetail = (e) => e, interfaceServerId } = this.props;
    fetchInterfaceDetail({ ...params, interfaceServerId }).then((res) => {
      if (res) {
        const { pageInterfaces } = res;
        this.setState({
          formDataSource: res,
          interfaceListDataSource: pageInterfaces.content || [],
          interfaceListPagination: createPagination(pageInterfaces),
        });
      }
    });
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    const { resetFields = (e) => e } = this.editorForm;
    resetFields();
    this.setState({
      formDataSource: {},
      interfaceListSelectedRows: [],
      interfaceListDataSource: [],
      interfaceListPagination: createPagination({ number: 0, size: 10, totalElements: 0 }),
    });
    onCancel();
  }

  handleSave() {
    const { edit = (e) => e, currentTenantId, tenantRoleLevel } = this.props;
    const { formDataSource, interfaceListDataSource: dataSource } = this.state;
    const { validateFields = (e) => e } = this.editorForm.props.form; // 上面接口定义DOM节点
    const interfaces = dataSource.map((item) => {
      if (item.isNew) {
        const { interfaceId, ...otherParams } = item;
        return { ...otherParams };
      } else {
        return item;
      }
    });
    validateFields((err, values) => {
      const { pageInterfaces, ...otherFormDataSource } = formDataSource;
      const tenantId = !tenantRoleLevel ? values.tenantId : currentTenantId;
      if (isEmpty(err)) {
        const interfaceServerList = {
          ...otherFormDataSource,
          ...values,
          tenantId,
          interfaces,
        };
        edit(interfaceServerList, this.cancel.bind(this));
      }
    });
  }

  handleCreate() {
    const { create = (e) => e, currentTenantId, tenantRoleLevel } = this.props;
    const { formDataSource, interfaceListDataSource } = this.state;
    const { validateFields = (e) => e } = this.editorForm.props.form;
    validateFields((err, values) => {
      const tenantId = !tenantRoleLevel ? values.tenantId : currentTenantId;
      if (isEmpty(err)) {
        const interfaces = interfaceListDataSource.map((item) => {
          if (item.isNew) {
            const { interfaceId, ...otherParams } = item;
            return { ...otherParams };
          } else {
            return item;
          }
        });
        const interfaceServerList = {
          authType: 'NONE',
          ...formDataSource,
          ...values,
          tenantId,
          interfaces,
        };
        create(interfaceServerList, this.cancel.bind(this));
      }
    });
  }

  handleDeleteService(interfaceIds) {
    const { deleteLines } = this.props;
    return deleteLines(interfaceIds);
  }

  onInterfaceListChange(params = {}) {
    const { interfaceServerId } = this.props;
    const { current = 1, pageSize = 10 } = params;
    this.handleFetchInterfaceDetail({ interfaceServerId, page: current - 1, size: pageSize });
  }

  onInterfaceListRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      interfaceListSelectedRows: selectedRows,
    });
  }

  onTypeChange(value) {
    const { formDataSource = {} } = this.state;
    this.setState({
      formDataSource: { ...formDataSource, serviceType: value },
    });
  }

  onEncryptionTypeChange(value) {
    const { formDataSource = {} } = this.state;
    this.setState({
      formDataSource: { ...formDataSource, encryptionType: value },
    });
  }

  openAuthenticationServiceModal() {
    this.setState({
      authenticationServiceModalVisible: true,
    });
  }

  closeAuthenticationServiceModal() {
    this.setState({
      authenticationServiceModalVisible: false,
    });
  }

  handleAuthData(res) {
    const { formDataSource } = this.state;
    this.setState({
      formDataSource: {
        ...formDataSource,
        ...res,
      },
    });
  }

  handleChangeListTenant(value) {
    const { interfaceListDataSource } = this.state;
    const newDataSource = interfaceListDataSource.map((item) => ({ ...item, tenantId: value }));
    this.setState({
      interfaceListDataSource: newDataSource,
    });
  }

  @Bind()
  handleChangeState(key, value) {
    this.setState({ [key]: value });
  }

  render() {
    const {
      visible,
      processing = {},
      interfaceServerId,
      realName,
      resetClientKey = (e) => e,
      serviceTypes, // 服务类型
      authTypes, // 认证方式
      wssPasswordTypes, // 加密方式
      requestTypes,
      soapVersionTypes,
      interfaceStatus,
      grantTypes,
      currentTenantId,
      tenantRoleLevel,
      fetchMonitor = () => {},
      createMonitor = () => {},
      updateMonitor = () => {},
      code = {},
    } = this.props;
    const {
      formDataSource = {},
      interfaceListDataSource = [],
      interfaceListPagination,
      interfaceListSelectedRows = {},
      authenticationServiceModalVisible,
    } = this.state;
    const editable = !isUndefined(interfaceServerId);
    const title = !isUndefined(interfaceServerId)
      ? intl.get('hitf.services.view.message.title.editor.edit').d('修改服务配置')
      : intl.get('hitf.services.view.message.title.editor.create').d('注册服务');
    const drawerProps = {
      title,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.cancel.bind(this),
      width: 1000,
      style: {
        height: 'calc(100% - 55px)',
        overflow: 'auto',
        padding: 12,
      },
    };

    const formProps = {
      serviceTypes,
      wssPasswordTypes,
      editable,
      resetClientKey,
      changeListTenant: this.handleChangeListTenant.bind(this),
      dataSource: {
        ...formDataSource,
        tenantId: !tenantRoleLevel ? formDataSource.tenantId : currentTenantId,
        realName: !tenantRoleLevel ? formDataSource.tenantName : realName,
      },
      onRef: (node) => {
        this.editorForm = node;
      },
      tenantRoleLevel,
      onTypeChange: this.onTypeChange.bind(this),
      onEncryptionTypeChange: this.onEncryptionTypeChange.bind(this),
      openAuthenticationServiceModal: this.openAuthenticationServiceModal.bind(this),
    };

    const interfaceListProps = {
      currentTenantId,
      tenantRoleLevel,
      serviceTypes,
      requestTypes,
      soapVersionTypes,
      interfaceStatus,
      processing,
      onChangeState: this.handleChangeState,
      dataSource: interfaceListDataSource,
      pagination: interfaceListPagination,
      selectedRowKeys: interfaceListSelectedRows.map((n) => n.interfaceId),
      onChange: this.onInterfaceListChange.bind(this),
      onRowSelectionChange: this.onInterfaceListRowSelectionChange.bind(this),
      deleteLines: this.handleDeleteService.bind(this),
      fetchInformation: this.handleFetchInterfaceDetail.bind(this),
      type: formDataSource.serviceType,
      serverCode: formDataSource.serverCode,
      authenticationData: {
        accessTokenUrl: formDataSource.accessTokenUrl,
        authType: formDataSource.authType,
        clientId: formDataSource.clientId,
        clientSecret: formDataSource.clientSecret,
        grantType: formDataSource.grantType,
      },
      onRef: (node) => {
        this.interfaceForm = node;
      },
      editorHeaderForm: this.editorForm,
      fetchMonitor,
      createMonitor,
      updateMonitor,
      code,
    };

    const authenticationServiceModalProps = {
      authTypes,
      grantTypes,
      visible: authenticationServiceModalVisible,
      dataSource: formDataSource,
      onCancel: this.closeAuthenticationServiceModal.bind(this),
      onRef: (node) => {
        this.authForm = node;
      },
      onOk: this.handleAuthData.bind(this),
    };

    return (
      <Drawer {...drawerProps} wrapClassName={styles['services-editor']}>
        <Spin spinning={processing.fetchInterfaceDetail || false}>
          <EditorForm {...formProps} />
        </Spin>
        <br />
        <InterfaceList {...interfaceListProps} />
        <div className={styles['hiam-interface-model-btns']}>
          <Button
            onClick={this.cancel.bind(this)}
            disabled={processing.save || processing.create}
            style={{ marginRight: 8 }}
          >
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          {editable ? (
            <Button type="primary" loading={processing.edit} onClick={this.handleSave.bind(this)}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          ) : (
            <Button
              type="primary"
              loading={processing.create}
              onClick={this.handleCreate.bind(this)}
            >
              {intl.get('hitf.services.view.button.create').d('注册')}
            </Button>
          )}
        </div>
        <AuthenticationServiceModal {...authenticationServiceModalProps} />
      </Drawer>
    );
  }
}
