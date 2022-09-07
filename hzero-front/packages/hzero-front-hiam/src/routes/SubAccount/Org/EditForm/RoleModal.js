import React from 'react';
import PropTypes from 'prop-types';
import { isEmpty, isFunction, isUndefined } from 'lodash';
import { Modal, Table, notification, Form, Button, Input, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { createPagination, tableScrollWidth } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';

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
export default class RoleModal extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [],
      selectedRows: [],
      dataSource: [],
      pagination: false,
    };
  }

  static propTypes = {
    onSave: PropTypes.func.isRequired,
  };

  componentDidMount() {
    this.fetchRoleList();
  }

  @Bind()
  fetchRoleList(pagination = { page: 0, size: 10 }) {
    const { fetchRoles, form, excludeRoleIds = [], id } = this.props;
    const params = {
      ...form.getFieldsValue(),
      excludeRoleIds,
      // excludeUserIds,
      ...pagination,
      memberType: 'user',
    };
    if (!isUndefined(id)) {
      params.memberId = id;
    }
    this.setState({
      fetchRolesLoading: true,
    });
    fetchRoles(params).then(
      (res) => {
        const nextState = {
          fetchRolesLoading: false,
        };
        if (res) {
          nextState.dataSource = res.content;
          nextState.pagination = createPagination(res);
          nextState.selectedRowKeys = [];
          nextState.selectedRows = [];
        }
        this.setState(nextState);
      },
      () => {
        this.setState({
          fetchRolesLoading: false,
        });
      }
    );
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
    this.fetchRoleList({ page: current - 1, size: pageSize });
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
          .get('hiam.subAccount.view.message.chooseNewRoleFirst')
          .d('请先选择新增的角色'),
      });
    } else if (isFunction(onSave)) {
      return onSave(selectedRows).then(() => {
        this.setState({
          selectedRowKeys: [],
          selectedRows: [],
        });
      });
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
    this.fetchRoleList();
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
      labelList,
    } = this.props;
    const { selectedRowKeys, dataSource = [], pagination = false, fetchRolesLoading } = this.state;
    const columns = [
      {
        title: intl.get('hiam.subAccount.model.role.name').d('角色名称'),
        dataIndex: 'name',
        width: 200,
      },
      !VERSION_IS_OP && {
        title: intl.get('hiam.subAccount.model.role.tenantName').d('所属租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
    ].filter(Boolean);
    return (
      <Modal
        visible={visible}
        onOk={this.handleSaveBtnClick}
        onCancel={this.handleCancelBtnClick}
        width={720}
        title={intl.get('hiam.subAccount.view.message.title.roleModal').d('选择角色')}
      >
        <Form>
          <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.role.name').d('角色名称')}
              >
                {getFieldDecorator('name')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.role.labels').d('角色标签')}
              >
                {getFieldDecorator('labels')(
                  <Select mode="multiple">
                    {labelList.map((n) => (
                      <Select.Option key={n.name} value={n.name}>
                        {n.name}
                      </Select.Option>
                    ))}
                  </Select>
                )}
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
          loading={fetchRolesLoading}
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
