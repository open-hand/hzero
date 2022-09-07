import React from 'react';
import PropTypes from 'prop-types';
import { isEmpty, isFunction } from 'lodash';
import { Modal, Table, notification, Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { createPagination, tableScrollWidth } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

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

  // 查询所有可分配的角色
  @Bind()
  fetchRoleList(pagination = { page: 0, size: 10 }) {
    const {
      fetchRoles,
      form,
      excludeRoleIds = [],
      excludeUserIds = [],
      excludeVisitRoleIds = [],
      roleType,
      id,
    } = this.props;
    const params =
      roleType === 'permission'
        ? {
            level: 'organization',
            memberType: 'client',
            ...form.getFieldsValue(),
            excludeRoleIds,
            excludeUserIds,
            memberId: id,
            ...pagination,
          }
        : {
            level: 'organization',
            ...form.getFieldsValue(),
            ...pagination,
            memberId: id,
            excludeRoleIds: excludeVisitRoleIds,
          };
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

  // 选中记录改变
  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRowKeys: selectedRows.map((r) => r.id),
      selectedRows,
    });
  }

  // 分页改变
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
        message: intl.get('hiam.client.view.message.chooseNewRoleFirst').d('请先选择新增的角色'),
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

  // 查询
  @Bind()
  handleSearchBtnClick() {
    this.fetchRoleList();
  }

  // 重置
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
      saveLoading,
    } = this.props;
    const { selectedRowKeys, dataSource = [], pagination = false, fetchRolesLoading } = this.state;
    const columns = [
      {
        title: intl.get('hiam.client.model.client.roleName').d('角色名称'),
        dataIndex: 'name',
        width: 200,
      },
      {
        title: intl.get('hiam.client.model.role.tenantName').d('所属租户'),
        dataIndex: 'tenantName',
        // width: 400,
      },
    ];
    return (
      <Modal
        visible={visible}
        onOk={this.handleSaveBtnClick}
        onCancel={this.handleCancelBtnClick}
        confirmLoading={saveLoading}
        destroyOnClose
        width={700}
        title={intl.get('hiam.client.view.message.title.roleModal').d('选择角色')}
      >
        <Form>
          <Row type="flex" align="bottom" gutter={24}>
            <Col span={8}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.client.model.client.roleName').d('角色名称')}
              >
                {getFieldDecorator('name')(<Input />)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.client.model.role.tenantName').d('所属租户')}
              >
                {getFieldDecorator('tenantName')(<Input />)}
              </FormItem>
            </Col>
            <Col>
              <FormItem>
                <Button onClick={this.handleResetBtnClick} style={{ marginRight: 8 }}>
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
          loading={fetchRolesLoading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns, 400), y: 400 }}
          dataSource={dataSource}
          rowKey="id"
          rowSelection={{
            selectedRowKeys,
            onChange: this.handleRowSelectionChange,
          }}
          pagination={pagination}
          onChange={this.handleTableChange}
          style={{ marginTop: 15 }}
        />
      </Modal>
    );
  }
}
