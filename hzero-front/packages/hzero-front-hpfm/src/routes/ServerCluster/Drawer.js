/**
 * 服务器集群管理
 * @date: 2019-7-9
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Input, Modal, Spin, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import { Button as ButtonPermission } from 'components/Permission';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import ModalFilter from './ModalFilter';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}

@Form.create({ fieldNameProp: null })
export default class Drawer extends Component {
  constructor(props) {
    super(props);
    this.oneSearchFormRef = React.createRef();
    this.state = {
      selectedDeleteRows: [],
      selectedAddRows: [],
    };
  }

  @Bind()
  handleOK() {
    const { form, onOk = (e) => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
    // form;
  }

  /**
   * 表单查询
   */
  @Bind()
  handleOneSearchFormSearch() {
    const { onFormSearch } = this.props;
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    onFormSearch(fieldsValue);
  }

  /**
   * 勾选回调
   * @param {*} _
   * @param {*} selectedRows - 勾选的值集行
   */
  @Bind()
  handleSelectDeleteRows(_, selectedDeleteRows) {
    this.setState({
      selectedDeleteRows,
    });
  }

  @Bind()
  handleSelectAddRows(_, selectedAddRows) {
    this.setState({
      selectedAddRows,
    });
  }

  // 编辑模态框取消
  @Bind
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
    this.setState({
      selectedDeleteRows: [],
    });
  }

  // 显示服务器模态框
  @Bind()
  handleAddBtnClick() {
    const { showServer } = this.props;
    showServer();
  }

  // 添加服务器
  @Bind()
  handleAddOk() {
    const { addServer } = this.props;
    const { selectedAddRows } = this.state;
    addServer(selectedAddRows);
    this.setState({
      selectedAddRows: [],
    });
  }

  /**
   * 删除服务器
   */
  @Bind()
  handleDeleteValues() {
    const { deleteServer } = this.props;
    const { selectedDeleteRows } = this.state;
    deleteServer(selectedDeleteRows);
    this.setState({
      selectedAddRows: [],
      selectedDeleteRows: [],
    });
  }

  // 添加服务器分页
  @Bind()
  canAssignPageChange(pagination = {}) {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    const { canAssignTableChange } = this.props;
    canAssignTableChange(pagination, fieldsValue);
  }

  // 服务器分页
  @Bind
  serverPageChange(pagination = {}) {
    const { serverTableChange } = this.props;
    serverTableChange(pagination);
  }

  render() {
    const {
      form,
      match,
      initData,
      title,
      modalVisible,
      addModalVisible,
      isSiteFlag,
      loading,
      onAddCancel,
      initLoading,
      modalLoading,
      createServerAssignLoading,
      serverList,
      canAssignList,
      serverPagination,
      canAssignPagination,
      isCreate,
    } = this.props;
    const { selectedDeleteRows, selectedAddRows } = this.state;
    const rowDeleteSelection = {
      selectedRowKeys: selectedDeleteRows.map((n) => n.serverId),
      onChange: this.handleSelectDeleteRows,
    };
    const rowAddSelection = {
      selectedRowKeys: selectedAddRows.map((n) => n.serverId),
      onChange: this.handleSelectAddRows,
    };
    const {
      clusterCode,
      clusterDescription,
      clusterName,
      enabledFlag,
      clusterId,
      tenantId,
      tenantName,
    } = initData;
    const { getFieldDecorator } = form;
    const columns = [
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.serverCode').d('服务器代码'),
        dataIndex: 'serverCode',
        width: 180,
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.serverName').d('服务器名称'),
        dataIndex: 'serverName',
        width: 180,
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.protocolCode').d('服务器协议'),
        dataIndex: 'protocolCode',
        width: 180,
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.serverDescription').d('服务器说明'),
        dataIndex: 'serverDescription',
        width: 180,
      },
    ];

    const addColumns = [
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.serverCode').d('服务器代码'),
        dataIndex: 'serverCode',
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.serverName').d('服务器名称'),
        dataIndex: 'serverName',
      },
      {
        title: intl.get('hpfm.serverCluster.model.serverCluster.serverDescription').d('服务器说明'),
        dataIndex: 'serverDescription',
      },
    ];

    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={this.handleCancel}
        onOk={this.handleOK}
        width={isCreate ? '520px' : '1000px'}
      >
        <Spin spinning={initLoading}>
          <Form>
            {isSiteFlag && (
              <Form.Item {...formLayout} label={intl.get('entity.tenant.tag').d('租户')}>
                {getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hzero.common.model.tenantName').d('租户'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={tenantName}
                    disabled={!!tenantId || String(tenantId) === '0'}
                  />
                )}
              </Form.Item>
            )}
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.serverCluster.model.serverCluster.clusterCode').d('集群编码')}
            >
              {getFieldDecorator('clusterCode', {
                initialValue: clusterCode,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.serverCluster.model.message.code').d('集群编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', { max: 30 }),
                  },
                ],
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={clusterId !== undefined}
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.serverCluster.model.serverCluster.clusterName').d('集群名称')}
            >
              {getFieldDecorator('clusterName', {
                initialValue: clusterName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.serverCluster.model.serverCluster.clusterName')
                        .d('集群名称'),
                    }),
                  },
                  {
                    max: 80,
                    message: intl.get('hzero.common.validation.max', { max: 80 }),
                  },
                ],
              })(<Input />)}
            </FormItem>
            <FormItem
              className="lang"
              {...formLayout}
              label={intl
                .get('hpfm.serverCluster.model.serverCluster.clusterDescription')
                .d('集群描述')}
            >
              {getFieldDecorator('clusterDescription', {
                initialValue: clusterDescription,
                rules: [
                  // {
                  //   type: 'string',
                  //   required: true,
                  //   message: intl.get('hzero.common.validation.notNull', {
                  //     name: intl
                  //       .get('hpfm.serverCluster.model.serverCluster.clusterDescription')
                  //       .d('集群描述'),
                  //   }),
                  // },
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', { max: 240 }),
                  },
                ],
              })(<Input />)}
            </FormItem>
            <FormItem {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
              {getFieldDecorator('enabledFlag', {
                initialValue: clusterId ? enabledFlag : 1,
              })(<Switch />)}
            </FormItem>
          </Form>
        </Spin>
        {clusterId && (
          <div>
            <div className="table-list-operator" style={{ textAlign: 'right' }}>
              <ButtonPermission
                key="add"
                permissionList={[
                  {
                    code: `${match.path}.button.addServer`,
                    type: 'button',
                    meaning: '服务器集群-添加服务器',
                  },
                ]}
                onClick={this.handleAddBtnClick}
              >
                {intl.get('hzero.common.button.addServer').d('添加服务器')}
              </ButtonPermission>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${match.path}.button.deleteServer`,
                    type: 'button',
                    meaning: '服务器集群-删除服务器',
                  },
                ]}
                onClick={this.handleDeleteValues}
                disabled={this.state.selectedDeleteRows.length === 0}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
            </div>
            <Table
              rowKey="serverId"
              bordered
              loading={initLoading}
              dataSource={serverList}
              rowSelection={rowDeleteSelection}
              pagination={serverPagination}
              columns={columns}
              onChange={this.serverPageChange}
            />
          </div>
        )}
        <Modal
          destroyOnClose
          visible={addModalVisible}
          title={intl.get('hpfm.customize.model.customize.serviceChoice').d('服务选择')}
          onCancel={onAddCancel}
          onOk={this.handleAddOk}
          confirmLoading={createServerAssignLoading}
          width={800}
        >
          <ModalFilter
            wrappedComponentRef={this.oneSearchFormRef}
            onSearch={this.handleOneSearchFormSearch}
          />
          <Table
            rowKey="serverId"
            bordered
            loading={modalLoading}
            dataSource={canAssignList}
            rowSelection={rowAddSelection}
            pagination={canAssignPagination}
            columns={addColumns}
            onChange={this.canAssignPageChange}
          />
        </Modal>
      </Modal>
    );
  }
}
