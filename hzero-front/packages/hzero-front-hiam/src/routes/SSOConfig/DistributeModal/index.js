import React from 'react';
import { Form, Modal, Table, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isNil } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import intl from 'utils/intl';

import QueryForm from './QueryForm';
import FormDrawer from './FormDrawer';

@Form.create({ fieldNameProp: null })
export default class DistributeDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedRecords: [],
      visible: false,
    };
  }

  @Bind()
  onSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRecords: selectedRows,
    });
  }

  // 分页
  @Bind()
  onTableChange(pagination = {}) {
    const { current = 1, pageSize = 10 } = pagination;
    const { getFieldsValue = (e) => e } = this.queryForm;
    this.fetchDataSource({ ...getFieldsValue(), page: current - 1, size: pageSize });
  }

  @Bind()
  handleSearch(params) {
    this.fetchDataSource({ page: 0, size: 10, ...params });
  }

  // 查询
  @Bind()
  fetchDataSource(params) {
    const { currentRecord = {}, handleQuery = (e) => e } = this.props;
    handleQuery(currentRecord, params);
  }

  @Bind()
  handleOk(record) {
    const { isSiteFlag, onOk = (e) => e } = this.props;
    this.hideModal();
    if (isSiteFlag) {
      onOk(record);
    } else if (!isNil(record.companyId)) {
      onOk(record);
    }
  }

  // 展示模态框
  @Bind()
  showModal() {
    this.setState({
      visible: true,
    });
  }

  // 隐藏模态框
  @Bind()
  hideModal() {
    this.setState({
      visible: false,
    });
  }

  // 点击删除
  @Bind()
  handleRemoveClick() {
    const { selectedRecords = [] } = this.state;
    this.handleDeleteDistribute(selectedRecords);
  }

  @Bind()
  handleDeleteDistribute(list) {
    if (!isEmpty(list)) {
      const { dispatch, currentRecord } = this.props;
      dispatch({
        type: 'ssoConfig/deleteDistribute',
        payload: { domainId: currentRecord.domainId, body: list },
      }).then((res) => {
        if (res) {
          notification.success();
          this.fetchDataSource();
          this.setState({
            selectedRecords: [],
          });
        }
      });
    }
  }

  render() {
    const {
      path,
      title,
      loading,
      tenantId,
      isSiteFlag,
      modalVisible,
      deleteDistributeLoading,
      initData = [],
      pagination = {},
      onCancel = (e) => e,
    } = this.props;
    const { visible, selectedRecords = [] } = this.state;

    const columns = [
      isSiteFlag && {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.tenantName').d('租户名称'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.companyName').d('公司名称'),
        dataIndex: 'companyName',
      },
    ].filter(Boolean);
    const rowSelection = { onChange: this.onSelectChange };
    const tableProps = {
      rowKey: 'domainAssignId',
      pagination,
      bordered: true,
      dataSource: initData,
      loading,
      columns,
      rowSelection,
      onChange: this.onTableChange,
    };
    const queryFormProps = {
      ref: (node) => {
        this.queryForm = node;
      },
      isSiteFlag,
      prompt,
      tenantId,
      handleFetchData: this.handleSearch,
    };

    return (
      <Modal
        destroyOnClose
        title={title}
        width={700}
        visible={modalVisible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        footer={[
          <Button type="primary" onClick={onCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <QueryForm {...queryFormProps} />
        <div className="table-operator">
          <ButtonPermission
            key="add"
            permissionList={[
              {
                code: `${path}.button.add`,
                type: 'button',
                meaning: '域名配置-分配租户/公司-新增',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.add').d('新增')}
          </ButtonPermission>
          <ButtonPermission
            key="remove"
            disabled={selectedRecords.length === 0}
            loading={deleteDistributeLoading}
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '域名配置-分配租户/公司-删除',
              },
            ]}
            onClick={this.handleRemoveClick}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <Table {...tableProps} />
        <FormDrawer
          title={
            isSiteFlag
              ? intl.get('hiam.ssoConfig.view.message.title.distributeTenant').d('分配租户')
              : intl.get('hiam.ssoConfig.view.message.title.distributeCompany').d('分配公司')
          }
          modalVisible={visible}
          onOk={this.handleOk}
          onCancel={this.hideModal}
          isSiteFlag={isSiteFlag}
        />
      </Modal>
    );
  }
}
