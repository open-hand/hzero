import React from 'react';
import PropTypes from 'prop-types';
import { isEmpty, isFunction, isUndefined } from 'lodash';
import { Modal, Table, notification, Form, Button, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { createPagination } from 'utils/utils';
import { SEARCH_COL_CLASSNAME } from 'utils/constants';

const FormItem = Form.Item;

/**
 * GroupModal-选择新的用户组 弹框
 * @reactProps {Boolean} visible 模态框是否显示
 * @reactProps {Boolean} dataSource 用户组的数据源
 * @reactProps {Function(selectedRows:Object[]):Promise|*} onSave 确认按钮的回调,接收选中的用户组,返回一个Promise对象或者任意值
 * @reactProps {Function} onCancel 取消按钮的回调
 */
@Form.create({ fieldNameProp: null })
export default class GroupModal extends React.PureComponent {
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
    fetchGroups: PropTypes.func.isRequired,
    excludeIds: PropTypes.array.isRequired,
    userId: PropTypes.array.isRequired,
  };

  componentDidMount() {
    this.fetchGroupList();
  }

  @Bind()
  handleSearchBtnClick() {
    this.fetchGroupList();
  }

  @Bind()
  fetchGroupList(pagination = { page: 0, size: 10 }) {
    const {
      fetchGroups,
      form,
      excludeIds = [], // 新分配的用户组
      userId = [], // 当前编辑账号的账号id( 需要排除的账号对应的用户组 )
      tenantId,
    } = this.props;
    const queryParams = {
      ...pagination,
      excludeIds,
      userId,
    };
    const groupName = form.getFieldValue('groupName');
    const groupCode = form.getFieldValue('groupCode');
    if (!isUndefined(groupCode)) {
      queryParams.groupCode = groupCode;
    }
    if (!isUndefined(groupName)) {
      queryParams.groupName = groupName;
    }
    if (!isUndefined(tenantId)) {
      queryParams.tenantId = tenantId;
    }
    this.setState({
      fetchGroupsLoading: true,
    });
    fetchGroups(queryParams)
      .then((res) => {
        if (res) {
          this.setState({
            dataSource: res.content,
            pagination: createPagination(res),
            selectedRowKeys: [],
            selectedRows: [],
          });
        }
      })
      .finally(() => {
        this.setState({
          fetchGroupsLoading: false,
        });
      });
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRowKeys: selectedRows.map((r) => r.userGroupId),
      selectedRows,
    });
  }

  @Bind()
  handleTableChange({ current = 1, pageSize = 10 } = {}) {
    this.fetchGroupList({ page: current - 1, size: pageSize });
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
          .get('hiam.subAccount.view.message.chooseNewGroupFirst')
          .d('请先选择新增的用户组'),
      });
    } else if (isFunction(onSave)) {
      onSave(selectedRows);
      this.setState({
        selectedRowKeys: [],
        selectedRows: [],
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
    } = this.props;
    const { selectedRowKeys, dataSource = [], pagination = false, fetchGroupsLoading } = this.state;
    const columns = [
      {
        title: intl.get('hiam.subAccount.model.group.groupCode').d('用户组编码'),
        dataIndex: 'groupCode',
        width: 200,
      },
      {
        title: intl.get('hiam.subAccount.model.group.groupName').d('用户组名称'),
        dataIndex: 'groupName',
        width: 200,
      },
    ];
    return (
      <Modal
        visible={visible}
        onOk={this.handleSaveBtnClick}
        onCancel={this.handleCancelBtnClick}
        width={720}
        title={intl.get('hiam.subAccount.view.message.title.groupModal').d('选择用户组')}
      >
        <Form layout="inline">
          <FormItem label={intl.get('hiam.subAccount.model.group.groupCode').d('用户组编码')}>
            {getFieldDecorator('groupCode')(<Input />)}
          </FormItem>
          <FormItem label={intl.get('hiam.subAccount.model.group.groupName').d('用户组名称')}>
            {getFieldDecorator('groupName')(<Input />)}
          </FormItem>
          <FormItem className={SEARCH_COL_CLASSNAME}>
            <Button onClick={this.handleResetBtnClick}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </FormItem>
        </Form>
        <Table
          bordered
          rowKey="userGroupId"
          loading={fetchGroupsLoading}
          dataSource={dataSource}
          pagination={pagination}
          columns={columns}
          style={{ marginTop: 14 }}
          // scroll={{ x: tableScrollWidth(columns), y: 400 }}
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
