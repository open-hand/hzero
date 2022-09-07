import React, { PureComponent } from 'react';
import { Button, Drawer, Modal, Spin } from 'hzero-ui';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import EditorForm from './Form';
import List from './List';

const listRowKey = 'interfaceId';

export default class Editor extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formDataSource: {},
      listSelectedRows: [],
      listDataSource: [],
      listPagination: {},
    };
  }

  getSnapshotBeforeUpdate(prevProps = {}) {
    const { visible, activeRowData = {}, detailPrimaryKey } = this.props;
    return (
      visible &&
      !isUndefined(activeRowData[detailPrimaryKey]) &&
      activeRowData[detailPrimaryKey] !== (prevProps.activeRowData || {})[detailPrimaryKey]
    );
  }

  // applicationId !== prevProps.applicationId
  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchDetail();
      this.handleFetchList();
    }
  }

  // 查询详，将详情数据、接口列表、接口分页设置到state中
  @Bind()
  handleFetchDetail() {
    const { fetchDetail = (e) => e } = this.props;
    fetchDetail().then((res) => {
      if (res) {
        this.setState({
          formDataSource: res,
        });
      }
    });
  }

  @Bind()
  handleFetchList(params = {}) {
    const { fetchDetailList = (e) => e } = this.props;
    const { getFieldsValue = (e) => e } = this.filterForm;
    const searchData = getFieldsValue() || {};
    fetchDetailList({
      ...searchData,
      ...params,
    }).then((res) => {
      if (res) {
        const { dataSource = [], pagination = {} } = res;
        this.setState({
          listDataSource: dataSource.map((n) => ({ ...n, key: n[listRowKey] })),
          listPagination: pagination,
          listSelectedRows: [],
        });
      }
    });
  }

  @Bind()
  handleDeleteRows() {
    const { deleteRows = () => {} } = this.props;
    const { listSelectedRows = [] } = this.state;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        deleteRows(listSelectedRows, () => {
          this.handleFetchList();
        });
      },
    });
  }

  @Bind()
  handleSave(data, cb = () => {}) {
    const { save = () => {} } = this.props;
    save(data, () => {
      cb();
      this.handleFetchList();
    });
  }

  @Bind()
  cancel() {
    const { onCancel = (e) => e } = this.props;
    const { resetFields = (e) => e } = this.editorForm;
    onCancel();
    resetFields();
    this.setState({
      formDataSource: {},
      listDataSource: [],
      listSelectedRows: [],
    });
  }

  // onServiceListChange(params = {}) {
  //   const { fetchServiceList = e => e } = this.props;
  //   const { current = 1, pageSize = 10 } = params;
  //   fetchServiceList({ page: current - 1, size: pageSize });
  // }

  @Bind()
  onListRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      listSelectedRows: selectedRows,
    });
  }

  @Bind()
  onListChange(page) {
    this.handleFetchList({ page });
  }

  @Bind()
  handleBindSearchRef(form) {
    this.filterForm = form;
  }

  render() {
    const {
      visible,
      processing = {},
      organizationId,
      tenantRoleLevel,
      fetchInterfaceData = () => {},
      queryInterfaceLoading,
      addAuthLoading,
      interfaceList,
      path,
    } = this.props;
    const {
      formDataSource = {},
      listDataSource = [],
      listPagination = {},
      listSelectedRows = [],
    } = this.state;
    const editable = !isUndefined(formDataSource.id);
    const title = editable
      ? intl.get('hitf.application.view.message.title.role.edit').d('编辑角色授权')
      : intl.get('hitf.application.view.message.title.role.create').d('创建角色授权');
    const drawerProps = {
      title,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.cancel,
      width: 800,
    };

    const formProps = {
      dataSource: formDataSource,
      ref: (node) => {
        this.editorForm = node;
      },
      editable,
      tenantRoleLevel,
    };

    const listProps = {
      path,
      formProps,
      formDataSource,
      primaryKey: listRowKey,
      listSelectedRows,
      dataSource: listDataSource,
      pagination: listPagination,
      loading: processing.queryDetailListLoading || processing.saveLoading || false,
      selectedRowKeys: listSelectedRows.map((n) => n.key),
      onRowSelectionChange: this.onListRowSelectionChange,
      deleteRows: this.handleDeleteRows,
      add: this.handleSave,
      onChange: this.onListChange,
      onSearch: this.handleFetchList,
      organizationId,
      onFetchModalData: fetchInterfaceData,
      queryInterfaceLoading,
      addAuthLoading,
      interfaceList,
      // loading: processing.queryDetail,
      onBindSeachRef: this.handleBindSearchRef,
    };

    return (
      <Drawer {...drawerProps}>
        <Spin spinning={processing.queryDetailLoading || processing.saveLoading || false}>
          <EditorForm {...formProps} />
        </Spin>
        <br />
        <List {...listProps} />
        <br />
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
            onClick={this.cancel}
            disabled={processing.saveLoading}
            style={{ marginRight: 8 }}
          >
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
