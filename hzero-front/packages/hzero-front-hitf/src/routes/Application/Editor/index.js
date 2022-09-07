/* eslint-disable no-nested-ternary */
import React, { PureComponent } from 'react';
import { Button, Drawer, Spin } from 'hzero-ui';
import { isEmpty, isUndefined, uniqBy } from 'lodash';
import { createPagination } from 'utils/utils';
import intl from 'utils/intl';
import EditorForm from './Form';
import ServiceList from './ServiceList';
import styles from './index.less';

export default class Editor extends PureComponent {
  constructor(props) {
    super(props);
    this.handleFetchDetail = this.handleFetchDetail.bind(this);
  }

  state = {
    formDataSource: {},
    serviceListSelectedRows: [],
    serviceListDataSource: [],
    serviceListPagination: createPagination({ number: 0, size: 10, totalElements: 0 }),
  };

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, applicationId } = this.props;
    return visible
      ? applicationId !== prevProps.applicationId
        ? !isUndefined(applicationId)
          ? 'edit'
          : 'create'
        : null
      : null;
  }

  // applicationId !== prevProps.applicationId
  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot === 'edit') {
      this.handleFetchDetail();
    }
  }

  // 查询详，将详情数据、接口列表、接口分页设置到state中
  handleFetchDetail() {
    const { fetchDetail = (e) => e, applicationId } = this.props;
    fetchDetail(applicationId).then((res) => {
      if (res) {
        const { serverList } = res;
        this.setState({
          formDataSource: res,
          serviceListDataSource: serverList || [],
          serviceListPagination: createPagination(serverList),
        });
      }
    });
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    const { resetFields = (e) => e } = this.editorForm;
    onCancel(() => {
      resetFields();
      this.setState({
        formDataSource: {},
        serviceListSelectedRows: [],
        serviceListDataSource: [],
        serviceListPagination: createPagination({ number: 0, size: 10, totalElements: 0 }),
      });
    });
  }

  handleSave() {
    const { save = (e) => e, currentTenantId, tenantRoleLevel } = this.props;
    const { formDataSource, serviceListDataSource } = this.state;
    const { validateFields = (e) => e } = this.editorForm;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        const { tenantId, applicationCode, applicationName, oauthClientId } = values;
        const data = {
          tenantId: tenantRoleLevel ? currentTenantId : tenantId,
          applicationCode,
          applicationName,
          serverList: serviceListDataSource,
          oauthClientId,
        };
        save(
          {
            ...formDataSource,
            ...data,
          },
          this.cancel.bind(this)
        );
      }
    });
  }

  handleCreate() {
    const { create = (e) => e, currentTenantId, tenantRoleLevel } = this.props;
    const { formDataSource, serviceListDataSource } = this.state;
    const { validateFields = (e) => e } = this.editorForm;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        const { tenantId, applicationCode, applicationName, oauthClientId } = values;
        const data = {
          tenantId: tenantRoleLevel ? currentTenantId : tenantId,
          applicationCode,
          applicationName,
          serverList: serviceListDataSource,
          oauthClientId,
        };
        create(
          {
            ...formDataSource,
            ...data,
          },
          this.cancel.bind(this)
        );
      }
    });
  }

  addService(selectedRow, record) {
    const { serviceListDataSource = [], serviceListPagination = {} } = this.state;
    const { current = 1, pageSize = 10 } = serviceListPagination;
    const newServiceListDataSource = uniqBy(
      serviceListDataSource.concat(record),
      'interfaceServerId'
    );
    this.setState({
      serviceListDataSource: newServiceListDataSource,
      serviceListPagination: createPagination({
        number: current - 1,
        size: pageSize,
        totalElements: newServiceListDataSource.length,
      }),
    });
  }

  handleDeleteService(assignIds) {
    // const {
    //   serviceListSelectedRows = [],
    //   serviceListDataSource = [],
    //   serviceListPagination = {},
    // } = this.state;
    // const { current = 1, pageSize = 10 } = serviceListPagination;
    // const newServiceListDataSource = pullAllBy(
    //   [...serviceListDataSource],
    //   serviceListSelectedRows,
    //   'interfaceServerId'
    // );
    // this.setState({
    //   serviceListSelectedRows: [],
    //   serviceListDataSource: newServiceListDataSource,
    //   serviceListPagination: createPagination({
    //     number: current - 1,
    //     size: pageSize,
    //     totalElements: newServiceListDataSource.length,
    //   }),
    // });

    const { deleteLines } = this.props;
    return deleteLines(assignIds);
  }

  onServiceListChange(params = {}) {
    const { fetchServiceList = (e) => e } = this.props;
    const { current = 1, pageSize = 10 } = params;
    fetchServiceList({ page: current - 1, size: pageSize });
  }

  onServiceListRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      serviceListSelectedRows: selectedRows,
    });
  }

  setTenantId(record) {
    const { tenantId } = record;
    const { formDataSource = {} } = this.state;
    this.setState({
      formDataSource: { ...formDataSource, tenantId },
    });
  }

  render() {
    const {
      visible,
      processing = {},
      applicationId,
      currentTenantId,
      tenantRoleLevel,
    } = this.props;
    const {
      formDataSource = {},
      serviceListDataSource = [],
      serviceListPagination,
      serviceListSelectedRows = {},
    } = this.state;
    const editable = !isUndefined(applicationId);
    const title = editable
      ? intl
          .get('hitf.application.view.message.title.editor.withEditedName', {
            name: formDataSource.applicationName || '',
          })
          .d(`修改“${formDataSource.applicationName || ''}”`)
      : intl.get('hitf.application.view.message.title.editor.create').d('创建应用');
    const drawerProps = {
      title,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.cancel.bind(this),
      width: 850,
    };

    const formProps = {
      dataSource: {
        ...formDataSource,
        tenantId: tenantRoleLevel ? currentTenantId : formDataSource.tenantId,
      },
      ref: (node) => {
        this.editorForm = node;
      },
      editable,
      tenantRoleLevel,
      setTenantId: this.setTenantId.bind(this),
    };

    const serviceListProps = {
      formProps,
      formDataSource,
      applicationId,
      serviceListSelectedRows,
      dataSource: serviceListDataSource,
      pagination: serviceListPagination,
      selectedRowKeys: serviceListSelectedRows.map((n) => n.interfaceServerId),
      onChange: this.onServiceListChange.bind(this),
      onRowSelectionChange: this.onServiceListRowSelectionChange.bind(this),
      addService: this.addService.bind(this),
      deleteLines: this.handleDeleteService.bind(this),
      fetchInformation: this.handleFetchDetail.bind(this),
      // loading: processing.queryDetail,
    };

    return (
      <Drawer {...drawerProps}>
        <div className={styles['hitf-application-editor']}>
          <Spin spinning={processing.queryDetail || false}>
            <EditorForm {...formProps} />
            <br />
            <ServiceList {...serviceListProps} />
          </Spin>
        </div>
        <div
          style={{
            position: 'absolute',
            bottom: 0,
            width: '100%',
            borderTop: '1px solid #e8e8e8',
            padding: '10px 16px',
            textAlign: 'right',
            left: 0,
            background: '#fff',
            borderRadius: '0 0 4px 4px',
            zIndex: 1,
          }}
        >
          <Button
            onClick={this.cancel.bind(this)}
            disabled={processing.save || processing.create}
            style={{ marginRight: 8 }}
          >
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          {editable ? (
            <Button type="primary" loading={processing.save} onClick={this.handleSave.bind(this)}>
              {intl.get('hzero.common.button.ok').d('确定')}
            </Button>
          ) : (
            <Button
              type="primary"
              loading={processing.create}
              onClick={this.handleCreate.bind(this)}
            >
              {intl.get('hzero.common.button.ok').d('确定')}
            </Button>
          )}
        </div>
      </Drawer>
    );
  }
}
