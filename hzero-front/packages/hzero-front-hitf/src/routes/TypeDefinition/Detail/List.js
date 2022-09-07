/**
 * TypeApplication - 应用类型定义-表格
 * @date: 2019/8/22
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Modal } from 'hzero-ui';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import InstanceDrawer from './InstanceDrawer';

/**
 * 应用类型定义-详情表格
 * @extends {Component} - React.Component
 * @reactProps {boolean} loading - 表格是否在加载中
 * @reactProps {array[Object]} dataSource - 表格数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Object} instanceDetail - 实例详情
 * @reactProps {Function} onChange - 表格变化时的回调
 * @reactProps {Boolean} isCreate - 是否为新建
 * @reactProps {Boolean} saveInstanceLoading - 新建、更新实例加载标志
 * @reactProps {Boolean} deleteInstanceLoading - 删除实例标志
 * @reactProps {Boolean} fetchInstanceDetailLoading - 实例详情加载标志
 * @reactProps {Boolean} refreshInstanceLoading - 刷新实例标志
 * @reactProps {Function} onDelete - 删除实例
 * @reactProps {Function} fetchInstanceDetail - 查询实例详情
 * @reactProps {Function} onClean - 清除数据
 * @reactProps {Function} onSave - 新建、更新实例
 * @reactProps {Function} onEditLine - 编辑实例
 * @return React.element
 */

export default class DetailTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [], // 选中行key集合
      selectedRows: {}, // 选中行集合
      modalVisible: false, // 是否显示实例配置弹窗
      currentInstance: {}, // 当前编辑的行
    };
  }

  /**
   * 获取选中行
   * @param {array} selectedRowKeys 选中行的key值集合
   * @param {object} selectedRows 选中行集合
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  /**
   * 打开应用实例弹窗
   */
  @Bind()
  handleOpenModal(record = {}) {
    // this.fetchModalData();
    this.setState({
      modalVisible: true,
      currentInstance: record,
    });
  }

  /**
   * 关闭应用实例弹窗
   */
  @Bind()
  handleCloseModal() {
    const { onClean = () => {} } = this.props;
    this.setState(
      {
        modalVisible: false,
        currentInstance: {},
      },
      () => {
        onClean();
      }
    );
  }

  /**
   * 删除实例配置行
   */
  @Bind()
  handleDeleteLine() {
    const { onDelete = () => {}, onChange = () => {}, deleteInstanceLoading = false } = this.props;
    if (deleteInstanceLoading) return;
    const { selectedRows } = this.state;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        onDelete(selectedRows).then((response) => {
          if (response) {
            notification.success();
            that.setState(
              {
                selectedRowKeys: [],
                selectedRows: {},
              },
              () => {
                onChange({});
              }
            );
          }
        });
      },
    });
  }

  /**
   * 创建实例配置
   * @param {object} payload - 表单内容
   */
  @Bind()
  handleSaveInstance(payload) {
    const {
      onSave = () => {},
      onChange = () => {},
      fetchInstanceDetail = () => {},
      saveInstanceLoading,
    } = this.props;
    if (saveInstanceLoading) return;
    onSave(payload).then((res) => {
      if (res) {
        notification.success();
        this.setState(
          {
            currentInstance: res,
          },
          () => {
            fetchInstanceDetail(res.applicationInstId);
          }
        );
        onChange();
      }
    });
  }

  render() {
    const {
      path,
      loading,
      dataSource,
      pagination,
      instanceDetail,
      isCreate,
      interfaceId,
      composePolicy,
      onChange = () => {},
      fetchInstanceDetail = () => {},
      fetchMappingClass = () => {},
      refreshInstance = () => {},
      onEditLine = () => {},
      testMappingClass = () => {},
      saveInstanceLoading,
      deleteInstanceLoading,
      fetchInstanceDetailLoading,
      refreshInstanceLoading,
      fetchMappingClassLoading,
      testMappingClassLoading,
    } = this.props;
    const { selectedRowKeys, modalVisible, currentInstance } = this.state;
    const columns = [
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.instance.code').d('实例接口代码'),
        dataIndex: 'interfaceCode',
        width: 150,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.instance.name').d('实例接口名称'),
        dataIndex: 'interfaceName',
        width: 150,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.serverCode').d('服务代码'),
        dataIndex: 'serverCode',
        width: 150,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.serverName').d('服务名称'),
        dataIndex: 'serverName',
        width: 150,
      },
      {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      composePolicy === 'WEIGHT' && {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.instance.weight').d('权重'),
        dataIndex: 'weight',
        width: 150,
      },
      composePolicy === 'ROUND_ROBIN' && {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.instance.orderSeq').d('优先级'),
        dataIndex: 'orderSeq',
        width: 150,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.remark').d('说明'),
        width: 150,
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 120,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 108,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleOpenModal(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const tableProps = {
      loading,
      columns,
      dataSource,
      pagination,
      bordered: true,
      rowSelection,
      rowKey: 'applicationInstId',
      onChange: (page) => onChange(page),
    };
    const instanceDrawerProps = {
      visible: modalVisible,
      interfaceId,
      composePolicy,
      currentInstance,
      isCreate: isEmpty(instanceDetail),
      fetchInstanceDetailLoading,
      instanceDetail,
      confirmLoading: saveInstanceLoading,
      refreshInstanceLoading,
      fetchMappingClassLoading,
      testMappingClassLoading,
      onEditLine,
      onCancel: this.handleCloseModal,
      onSaveInstance: this.handleSaveInstance,
      onFetchDetail: fetchInstanceDetail,
      onRefresh: refreshInstance,
      onFetchMappingClass: fetchMappingClass,
      onTestMappingClass: testMappingClass,
    };
    return (
      <>
        <div className="table-list-operator">
          <ButtonPermission
            icon="add"
            permissionList={[
              {
                code: `${path}.button.createLine`,
                type: 'button',
                meaning: '应用类型定义-实例配置新建',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={isCreate}
            onClick={() => this.handleOpenModal({})}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            icon="delete"
            permissionList={[
              {
                code: `${path}.button.deleteLine`,
                type: 'button',
                meaning: '应用类型定义-实例配置删除',
              },
            ]}
            type="c7n-pro"
            disabled={isEmpty(rowSelection.selectedRowKeys)}
            onClick={this.handleDeleteLine}
            loading={deleteInstanceLoading}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <Table {...tableProps} />
        <InstanceDrawer {...instanceDrawerProps} />
      </>
    );
  }
}
