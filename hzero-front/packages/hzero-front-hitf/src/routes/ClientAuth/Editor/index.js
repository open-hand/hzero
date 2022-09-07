import React, { PureComponent } from 'react';
import { Button, Drawer, Spin } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';
import intl from 'utils/intl';
import EditorForm from './Form';
import List from './List';

const listRowKey = 'clientRoleId';

export default class Editor extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formDataSource: {},
      listSelectedRows: [],
      listDataSource: [],
    };
  }

  getSnapshotBeforeUpdate(prevProps = {}) {
    const { visible, defaultDataSource = {} } = this.props;
    return (
      visible &&
      !isUndefined(defaultDataSource.id) &&
      defaultDataSource.id !== (prevProps.defaultDataSource || {}).id
    );
  }

  // applicationId !== prevProps.applicationId
  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchDetail();
    }
  }

  // 查询详，将详情数据、接口列表、接口分页设置到state中
  @Bind()
  handleFetchDetail() {
    const { fetchDetail = (e) => e } = this.props;
    fetchDetail().then((res) => {
      if (res) {
        const { clientRoleList = [] } = res || {};
        this.setState({
          formDataSource: res,
          listDataSource: (clientRoleList || []).map((o) => ({ ...o, key: o[listRowKey] })),
        });
      }
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

  @Bind()
  handleSave() {
    const { save = (e) => e } = this.props;
    const { formDataSource, listDataSource = [] } = this.state;
    const { validateFields = (e) => e } = this.editorForm;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        const data = {
          ...formDataSource,
          ...values,
          clientRoleList: listDataSource,
        };
        save(data, () => {
          this.handleFetchDetail();
        });
      }
    });
  }

  @Bind()
  onListRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      listSelectedRows: selectedRows,
    });
  }

  @Bind()
  addRoles(record) {
    const { listDataSource = [] } = this.state;
    const swag = [];
    for (let index = 0; index < record.length; index++) {
      const element = record[index];
      const { id, name, viewCode, levelPath } = element;
      swag.push({
        roleId: id,
        code: viewCode,
        viewCode,
        name,
        levelPath,
        key: uuidv4(),
      });
    }
    this.setState({ listDataSource: listDataSource.concat(swag) });
  }

  @Bind()
  deleteRoles() {
    const { listSelectedRows = [] } = this.state;
    const { deleteAuthRole = () => {} } = this.props;
    deleteAuthRole(listSelectedRows, () => {
      this.handleFetchDetail();
    });
  }

  render() {
    const { visible, processing = {}, currentTenantId, tenantRoleLevel, code = {} } = this.props;
    const { formDataSource = {}, listDataSource = [], listSelectedRows = [] } = this.state;
    const editable = !isUndefined(formDataSource.id);
    const title = editable
      ? intl.get('hitf.application.view.message.title.edit').d('编辑客户端授权')
      : intl.get('hitf.application.view.message.title.create').d('创建客户端授权');
    const drawerProps = {
      title,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.cancel,
      width: 750,
      zIndex: 100,
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
      code,
    };

    const listProps = {
      formProps,
      formDataSource,
      rowKey: listRowKey,
      listSelectedRows,
      dataSource: listDataSource,
      loading: processing.queryDetailLoading || processing.saveLoading || false,
      selectedRowKeys: listSelectedRows.map((n) => n.key),
      onRowSelectionChange: this.onListRowSelectionChange,
      fetchInformation: this.handleFetchDetail,
      addRoles: this.addRoles,
      deleteRoles: this.deleteRoles,
    };

    return (
      <Drawer {...drawerProps}>
        <Spin spinning={processing.queryDetailLoading || processing.saveLoading || false}>
          <EditorForm {...formProps} />
        </Spin>
        <br />
        <List {...listProps} />
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
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            loading={processing.queryDetailLoading || processing.saveLoading || false}
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
