/**
 * Table - 修改服务配置-接口列表表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import uuid from 'uuid/v4';
import { Bind } from 'lodash-decorators';
import { Table, Input, Form as HForm } from 'hzero-ui';
import { Modal, Row, Col } from 'choerodon-ui/pro';
import { connect } from 'dva';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { tableScrollWidth, getEditTableData } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import { Button as ButtonPermission } from 'components/Permission';
import AddDataModal from '@/components/AddDataModal';
import { SERVICE_CONSTANT } from '@/constants/constants';

import InterfaceDrawer from '../Interface';
import DocumentDrawer from '../Document';
import MaintenanceConfigDrawer from '../MaintenanceConfig';
import Search from './Search';

let modal;
const interfaceModalKey = Modal.key();

@connect(({ services }) => ({ services }))
export default class InterfaceList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      interfaceListActionRow: {},
      documentDrawerVisible: false,
      currentInterface: null, // 当前接口
      maintenanceConfigDrawerVisible: false,
      addModalVisible: false,
    };
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  defaultTableRowKey = 'interfaceId';

  /**
   * 先设置当前接口信息然后打开接口编辑弹窗
   * @param {*} [interfaceListActionRow={}]
   */
  @Bind()
  openInterfaceDrawer(interfaceListActionRow = {}) {
    this.setState({
      interfaceListActionRow,
    });
    const {
      match,
      type,
      editable,
      tenantId,
      interfaceServerId,
      onFetchDetail,
      currentInterfaceType,
      fetchMappingClass = () => {},
      testMappingClass = () => {},
      fetchMappingClassLoading,
      testMappingClassLoading,
      operatorList,
      assertionSubjects,
      serverCode,
      namespace,
      codeArr = [],
    } = this.props;
    const interfaceDrawerProps = {
      match,
      type,
      tenantId,
      serverCode,
      namespace,
      currentInterfaceType,
      interfaceListActionRow,
      interfaceServerId,
      onCancel: this.closeInterfaceDrawer,
      onFetchMappingClass: fetchMappingClass,
      onTestMappingClass: testMappingClass,
      fetchMappingClassLoading,
      testMappingClassLoading,
      codeArr,
      operatorList,
      assertionSubjects,
      onFetchDetail,
      title: editable
        ? intl.get('hitf.services.view.message.title.interface.edit').d('编辑接口')
        : intl.get('hitf.services.view.message.title.interface.create').d('创建接口'),
      onRef: (node) => {
        this.interfaceDrawer = node;
      },
    };
    const { path } = match;
    const title = editable
      ? intl.get('hitf.services.view.message.title.interface.edit').d('编辑接口')
      : intl.get('hitf.services.view.message.title.interface.create').d('创建接口');
    const disabledFlag =
      interfaceListActionRow.status === SERVICE_CONSTANT.ENABLED ||
      interfaceListActionRow.status === SERVICE_CONSTANT.DISABLED_INPROGRESS;
    modal = Modal.open({
      title,
      drawer: true,
      closable: true,
      key: interfaceModalKey,
      style: { width: 1000 },
      children: <InterfaceDrawer {...interfaceDrawerProps} />,
      onClose: () => this.interfaceDrawer.onModalCloseValidate(),
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.saveLine`,
                type: 'button',
                meaning: '服务注册-接口配置保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={disabledFlag}
            onClick={this.handleOk}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          {interfaceListActionRow.status === SERVICE_CONSTANT.NEW && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.releaseLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置发布',
                },
              ]}
              type="c7n-pro"
              onClick={this.handleRelease}
            >
              {intl.get('hzero.common.button.release').d('发布')}
            </ButtonPermission>
          )}
          {interfaceListActionRow.status === SERVICE_CONSTANT.ENABLED && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.offlineLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置下线',
                },
              ]}
              type="c7n-pro"
              onClick={this.handleOffline}
            >
              {intl.get('hitf.services.view.button.offline').d('下线')}
            </ButtonPermission>
          )}
          {interfaceListActionRow.status === SERVICE_CONSTANT.DISABLED && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.onlineLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置上线',
                },
              ]}
              type="c7n-pro"
              onClick={this.handleRelease}
            >
              {intl.get('hitf.services.view.button.online').d('上线')}
            </ButtonPermission>
          )}
          {cancelBtn}
        </div>
      ),
    });
  }

  @Bind()
  handleOffline() {
    return this.interfaceDrawer.handleOffline();
  }

  @Bind()
  handleRelease() {
    return this.interfaceDrawer.handleRelease();
  }

  @Bind()
  async handleOk() {
    await this.interfaceDrawer.handleOk();
  }

  /**
   * 关闭滑窗
   */
  @Bind()
  closeInterfaceDrawer() {
    modal.close();
    this.setState({
      interfaceListActionRow: {},
    });
  }

  /**
   * 打开接口文档弹窗
   * @param {object} record 接口表格行数据
   */
  @Bind()
  openDocumentDrawer(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'services/queryDocument',
      payload: record.interfaceId,
    }).then((res) => {
      if (res) {
        dispatch({
          type: 'services/queryParams',
          payload: res.documentId,
        });
        this.setState({ documentDrawerVisible: true, currentInterface: record });
      }
    });
  }

  /**
   * 关闭接口文档弹窗
   */
  @Bind()
  closeDocumentDrawer() {
    this.setState({
      documentDrawerVisible: false,
    });
  }

  handleSaveInterface(item, cb) {
    const { onChangeState, dataSource } = this.props;
    let newDataSource = [];
    if (item.interfaceId) {
      newDataSource = dataSource.map((_item) => {
        if (_item.interfaceId === item.interfaceId) {
          return {
            ..._item,
            ...item,
          };
        }
        return _item;
      });
    } else {
      newDataSource = [{ ...item, interfaceId: uuid(), isNew: true }, ...dataSource];
    }
    onChangeState('interfaceListDataSource', newDataSource);
    // this.setState({
    //   dataSource: newDataSource,
    // });
    cb();
  }

  @Bind()
  handleDeleteService() {
    const {
      selectedRowKeys,
      dataSource,
      onRowSelectionChange,
      deleteLines,
      onChangeState,
    } = this.props;
    if (selectedRowKeys.length > 0) {
      Modal.confirm({
        title: intl.get('hzero.common.message.confirm.title').d('提示'),
        children: intl.get('hitf.services.view.message.title.deleteContent').d('确定删除吗？'),
        onOk() {
          const ids = [];
          const newDataSource = [];
          dataSource.forEach((item) => {
            if (!item.isNew && selectedRowKeys.indexOf(item.interfaceId) >= 0) {
              ids.push(item);
            }
            if (selectedRowKeys.indexOf(item.interfaceId) < 0) {
              newDataSource.push(item);
            }
          });
          if (ids.length > 0) {
            deleteLines(ids).then((res) => {
              if (res) {
                onRowSelectionChange([], []);
                notification.success();
                onChangeState('interfaceListDataSource', newDataSource);
              }
            });
          } else {
            onRowSelectionChange([], []);
            notification.success();
            onChangeState('interfaceListDataSource', newDataSource);
          }
        },
      });
    }
  }

  @Bind()
  openMaintenanceConfigDrawer(interfaceListActionRow) {
    this.setState({
      interfaceListActionRow,
      maintenanceConfigDrawerVisible: true,
    });
  }

  @Bind()
  closeMaintenanceConfigDrawer() {
    this.setState({
      interfaceListActionRow: {},
      maintenanceConfigDrawerVisible: false,
    });
  }

  /**
   * 打开新建内部接口弹窗
   */
  @Bind()
  openInnerFaceModal() {
    this.fetchModalData();
    this.setState({
      addModalVisible: true,
    });
  }

  /**
   * 隐藏新建内部接口弹窗
   */
  @Bind()
  handleCloseModal() {
    this.internalRef.state.addRows = [];
    this.setState({
      addModalVisible: false,
    });
  }

  /**
   * 查询弹窗数据
   * @param {Object} queryData - 查询数据
   */
  @Bind()
  fetchModalData(queryData = {}) {
    const { fetchInternalInterface = () => {} } = this.props;
    fetchInternalInterface(queryData);
  }

  @Bind()
  handleBindRef(ref = {}) {
    this.internalRef = ref;
  }

  /**
   * 批量创建内部接口
   * @param {Aarray} addRows 选择的数据
   */
  @Bind()
  handleCreateLine(addRows) {
    const { saveBatchInterfaces = () => {} } = this.props;
    const editableData = getEditTableData(addRows);
    saveBatchInterfaces(editableData, this.handleCloseModal);
  }

  render() {
    const {
      logTypes,
      serviceTypes,
      interfaceStatus,
      pagination,
      selectedRowKeys = [],
      onChange = (e) => e,
      onRowSelectionChange = (e) => e,
      processing = {},
      dataSource,
      fetchMonitor = () => {},
      createMonitor = () => {},
      updateMonitor = () => {},
      code = {},
      authenticationData = {},
      serverCode,
      editable,
      currentInterfaceType,
      fetchModalLoading,
      saveBatchInterfacesLoading,
      internalList = [],
      internalPagination = {},
      tenantId,
      getTemplateServerDetail = () => {},
      onFetchDetail = () => {},
      match: { path },
      isHistory,
    } = this.props;

    const {
      interfaceListActionRow,
      documentDrawerVisible,
      currentInterface,
      maintenanceConfigDrawerVisible,
      addModalVisible,
    } = this.state;
    // const organizationRoleLevel = isTenantRoleLevel();
    const documentDrawerProps = {
      currentInterface,
      authenticationData,
      visible: documentDrawerVisible,
      currentInterfaceType,
      onCancel: this.closeDocumentDrawer,
      tenantId,
    };
    const columns = [
      {
        title: intl.get('hitf.services.model.services.interfaceCode').d('接口编码'),
        dataIndex: 'interfaceCode',
        width: 300,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <HForm.Item>
              {record.$form.getFieldDecorator('interfaceCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.interfaceCode').d('接口编码'),
                    }),
                  },
                ],
                initialValue: val,
              })(<Input />)}
            </HForm.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hitf.services.model.services.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 250,
      },
      {
        title: intl.get('hitf.services.model.services.requestMethod').d('请求方式'),
        dataIndex: 'requestMethod',
        width: 100,
      },
      {
        title: intl.get('hitf.services.model.services.interfaceAddress').d('接口地址'),
        dataIndex: 'path',
        width: 300,
      },
    ];
    const addModalOptions = {
      columns,
      confirmLoading: saveBatchInterfacesLoading,
      loading: fetchModalLoading,
      title: intl.get('hitf.services.view.title.internal.interface').d('选择内部接口'),
      rowKey: 'id',
      queryName: 'interfaceName',
      queryCode: 'interfaceCode',
      queryNameDesc: intl.get('hitf.services.model.services.interfaceName').d('接口名称'),
      queryCodeDesc: intl.get('hitf.services.model.services.interfaceCode').d('接口编码'),
      dataSource: internalList,
      pagination: internalPagination,
      modalVisible: addModalVisible,
      addData: this.handleCreateLine,
      onHideAddModal: this.handleCloseModal,
      fetchModalData: this.fetchModalData,
      onRef: this.handleBindRef,
    };
    const tableColumns = [
      {
        title: intl.get('hitf.services.model.services.interfaceCode').d('接口编码'),
        dataIndex: 'interfaceCode',
        width: 160,
      },
      {
        title: intl.get('hitf.services.model.services.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 180,
      },
      {
        title: intl.get('hitf.services.model.services.interfaceAddress').d('接口地址'),
        dataIndex: 'interfaceUrl',
        width: 180,
      },
      {
        title: intl.get('hitf.services.model.services.publishType').d('发布类型'),
        dataIndex: 'publishType',
        width: 120,
      },
      {
        title: intl.get('hitf.services.model.services.publishUrl').d('发布地址'),
        dataIndex: 'publishUrl',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'statusMeaning',
      },
      {
        title: intl.get('hitf.services.view.message.current.version').d('当前版本'),
        dataIndex: 'formatVersion',
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
                <a
                  className="edit"
                  disabled={isHistory}
                  onClick={() => this.openInterfaceDrawer(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (record._token) {
            operators.push(
              {
                key: 'editDocument',
                ele: (
                  <a
                    className="edit"
                    disabled={isHistory}
                    onClick={() => this.openDocumentDrawer(record)}
                  >
                    {intl.get('hitf.services.view.button.document').d('编辑接口文档')}
                  </a>
                ),
                len: 6,
                title: intl.get('hitf.services.view.button.document').d('编辑接口文档'),
              },
              {
                key: 'maintenanceConfig',
                ele: (
                  <a disabled={isHistory} onClick={() => this.openMaintenanceConfigDrawer(record)}>
                    {intl.get('hitf.services.view.button.operationalConfig').d('运维配置')}
                  </a>
                ),
                len: 4,
                title: intl.get('hitf.services.view.button.operationalConfig').d('运维配置'),
              }
            );
          }
          return operatorRender(operators, record);
        },
      },
    ];
    const tableProps = {
      dataSource,
      pagination,
      onChange,
      bordered: true,
      rowKey: this.defaultTableRowKey,
      loading: processing.fetchInterfaceList,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowSelection: {
        selectedRowKeys,
        onChange: onRowSelectionChange,
      },
    };

    const maintenanceConfigProps = {
      visible: maintenanceConfigDrawerVisible,
      onCancel: this.closeMaintenanceConfigDrawer,
      interfaceListActionRow,
      fetchMonitor,
      createMonitor,
      updateMonitor,
      code,
      serverCode,
      logTypes,
      processing: {
        query: processing.queryMonitorLoading,
        update: processing.updateMonitorLoading,
        create: processing.createMonitorLoading,
      },
      getTemplateServerDetail,
    };

    const searchProps = {
      serviceTypes,
      interfaceStatus,
      onSearch: onFetchDetail,
      onRef: (ref) => {
        this.searchForm = ref;
      },
    };

    return (
      <>
        <Row>
          <Col span={20}>{editable && <Search {...searchProps} />}</Col>
          <Col span={4}>
            <div style={{ textAlign: 'right' }}>
              <ButtonPermission
                icon="delete"
                permissionList={[
                  {
                    code: `${path}.button.deleteLine`,
                    type: 'button',
                    meaning: '服务注册-接口配置删除',
                  },
                ]}
                type="c7n-pro"
                disabled={isEmpty(selectedRowKeys) || isHistory}
                onClick={this.handleDeleteService}
                style={{ marginRight: 8 }}
                loading={processing.deleteLinesLoading}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
              <ButtonPermission
                icon="add"
                permissionList={[
                  {
                    code: `${path}.button.createLine`,
                    type: 'button',
                    meaning: '服务注册-接口配置新建',
                  },
                ]}
                type="c7n-pro"
                color="primary"
                onClick={
                  currentInterfaceType === 'INTERNAL'
                    ? this.openInnerFaceModal
                    : () => this.openInterfaceDrawer({})
                }
                disabled={!editable || isHistory}
              >
                {intl.get('hzero.common.button.create').d('新建')}
              </ButtonPermission>
            </div>
          </Col>
        </Row>
        <Table {...tableProps} />
        <DocumentDrawer {...documentDrawerProps} />
        <MaintenanceConfigDrawer {...maintenanceConfigProps} />
        <AddDataModal {...addModalOptions} />
      </>
    );
  }
}
