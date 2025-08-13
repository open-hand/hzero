import React from 'react';
import PropTypes from 'prop-types';
import { isEmpty, isFunction } from 'lodash';
import { Modal, Table, notification, Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
// import { VERSION_IS_OP } from 'utils/config';
import {
  FORM_COL_3_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

const rowProps = {
  style: {
    cursor: 'pointer',
  },
};

/**
 * RoleModal-选择新的角色 弹框
 * @reactProps {Boolean} visible 模态框是否显示
 * @reactProps {Boolean} dataSource 角色的数据源
 * @reactProps {Function(selectedRows:Object[]):Promise|*} onSave 确认按钮的回调,接收选中的角色,返回一个Promise对象或者任意值
 * @reactProps {Function} onCancel 取消按钮的回调
 */
@Form.create({ fieldNameProp: null })
export default class ClientModal extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [],
      selectedRows: [],
      fetchClientLoading: false,
    };
  }

  static propTypes = {
    onSave: PropTypes.func.isRequired,
  };

  componentDidMount() {
    this.fetchClientList();
  }

  @Bind()
  fetchClientList(pagination = { page: 0, size: 10 }) {
    const { fetchClient, form, roleId } = this.props;
    const params = {
      ...form.getFieldsValue(),
      ...pagination,
      roleId,
    };
    this.setState({
      fetchClientLoading: true,
    });
    fetchClient(params).then(() => {
      this.setState({
        fetchClientLoading: false,
      });
    });
  }

  @Bind()
  handleRow() {
    return rowProps;
  }

  @Bind()
  handleRowClick(record) {
    const { selectedRowKeys = [], selectedRows = [] } = this.state;
    if (selectedRowKeys.some((id) => id === record.id)) {
      // 已经选中 需要移除
      this.setState({
        selectedRowKeys: selectedRowKeys.filter((id) => id !== record.id),
        selectedRows: selectedRows.filter((item) => item !== record),
      });
    } else {
      // 没有选中 需要新增
      this.setState({
        selectedRowKeys: [...selectedRowKeys, record.id],
        selectedRows: [...selectedRows, record],
      });
    }
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRowKeys: selectedRows.map((r) => r.id),
      selectedRows,
    });
  }

  @Bind()
  handleTableChange({ current = 1, pageSize = 10 } = {}) {
    this.fetchClientList({ page: current - 1, size: pageSize });
  }

  /**
   * 保存
   */
  @Bind()
  handleSaveBtnClick() {
    const { selectedRows } = this.state;
    this.save(selectedRows);
  }

  /**
   *
   * @param {Object[]} selectedRows
   */
  @Bind()
  save(selectedRows) {
    const { onSave } = this.props;
    if (isEmpty(selectedRows)) {
      notification.warning({
        message: intl
          .get('hiam.roleManagement.model.roleManagement.member.warn.message')
          .d('请先选择需要添加的用户'),
      });
    } else if (isFunction(onSave)) {
      return onSave(selectedRows);
    }
  }

  /**
   * 取消保存
   */
  @Bind()
  handleCancelBtnClick() {
    const { onCancel } = this.props;
    if (isFunction(onCancel)) {
      this.setState({
        selectedRowKeys: [],
        selectedRows: [],
      });
      onCancel();
    }
  }

  @Bind()
  handleSearchBtnClick() {
    this.fetchClientList();
  }

  @Bind()
  handleResetBtnClick() {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      visible,
      form: { getFieldDecorator },
      dataSource,
      pagination,
    } = this.props;
    const { selectedRowKeys, fetchClientLoading } = this.state;
    const columns = [
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.clientName').d('客户端名称'),
        dataIndex: 'name',
        width: 100,
      },
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.tenant').d('所属租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
    ].filter(Boolean);
    return (
      <Modal
        visible={visible}
        onOk={this.handleSaveBtnClick}
        onCancel={this.handleCancelBtnClick}
        destroyOnClose
        width={720}
        title={intl
          .get('hiam.roleManagement.model.roleManagement.client.modal.title')
          .d('选择客户端')}
      >
        <Form>
          <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.roleManagement.model.roleManagement.clientName')
                  .d('客户端名称')}
              >
                {getFieldDecorator('name')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem>
                <Button style={{ marginRight: 8 }} onClick={this.handleResetBtnClick}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </FormItem>
            </Col>
          </Row>
        </Form>
        <Table
          bordered
          rowKey="id"
          style={{ marginTop: 14 }}
          loading={fetchClientLoading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={pagination}
          dataSource={dataSource}
          onRow={this.handleRow}
          onRowClick={this.handleRowClick}
          rowSelection={{
            selectedRowKeys,
            onChange: this.handleRowSelectionChange,
          }}
          onChange={this.handleTableChange}
        />
      </Modal>
    );
  }
}
