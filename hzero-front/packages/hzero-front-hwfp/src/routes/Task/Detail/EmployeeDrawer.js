import React, { PureComponent } from 'react';
import { Modal, Form, Input, Row, Col, Table, Button } from 'hzero-ui';
import { remove, map, indexOf, isEmpty, uniqWith, isEqual } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

const FormItem = Form.Item;

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class EmployeeDrawer extends PureComponent {
  state = {
    selectedRowKeys: [],
    selectedApprovers: [],
    paging: { page: 0, size: 10 },
    employeeNum: '',
    name: '',
  };

  /*
  componentDidMount() {
    const { form } = this.props;
    this.handleSearch();
    form.resetFields();
  } */

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const {
      form,
      dispatch,
      tenantId,
      needAppoint,
      filterCandidates,
      isAssignNextApprover,
    } = this.props;
    const { paging } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        if (isAssignNextApprover && needAppoint === 'N') {
          filterCandidates(values);
        } else {
          dispatch({
            type: 'task/fetchEmployeeList',
            payload: {
              ...paging,
              tenantId,
              lovCode: 'HPFM.EMPLOYEE',
              employeeNum: values.employeeNum,
              name: values.name,
              enabledFlag: 1,
            },
          });
        }
      }
    });
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleSubmit() {
    const { employeeNum, name, selectedApprovers } = this.state;
    const {
      delegateOrAddUser,
      isCarbon,
      processCarbonCopy,
      taskAction,
      isAssignNextApprover,
      handleApproverOk,
    } = this.props;
    if (isCarbon) {
      processCarbonCopy();
    } else if (isAssignNextApprover) {
      handleApproverOk(isEmpty(selectedApprovers) ? [] : selectedApprovers);
    } else if (employeeNum && employeeNum.length > 0) {
      taskAction({
        action: delegateOrAddUser,
        targetUser: employeeNum,
        targetUserName: name,
      });
    } else {
      Modal.warning({
        title: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
    }
    this.setState({
      selectedRowKeys: [],
      selectedApprovers: [],
      paging: { page: 0, size: 10 },
    });
  }

  @Bind()
  handleChangeEmployee(selected, record, employees, employeeCodes) {
    const { changeEmployee, dispatch, taskId, delegateOrAddUser } = this.props;
    const { selectedApprovers = [] } = this.state;
    // eslint-disable-next-line no-param-reassign
    employees = uniqWith([...selectedApprovers, ...employees], isEqual);
    if (!selected) {
      remove(employees, (item) => employeeCodes.indexOf(item.employeeNum) !== -1);
    }
    if (this.props.isCarbon) {
      if (selected) {
        // const newChangeEmployee = changeEmployee.concat(employees);
        // const temp = uniqBy(newChangeEmployee, 'employeeNum');
        dispatch({
          type: 'task/updateDetailState',
          payload: { taskId, changeEmployee: employees },
        });
      } else {
        const centerChangeEmployee = changeEmployee.slice();
        remove(centerChangeEmployee, (item) => employeeCodes.indexOf(item.employeeNum) !== -1);
        dispatch({
          type: 'task/updateDetailState',
          payload: { taskId, changeEmployee: centerChangeEmployee },
        });
      }
    }
    if (delegateOrAddUser === 'addSign' || delegateOrAddUser === 'ApproveAndAddSign') {
      this.setState({
        employeeNum: employees.map((item) => item.employeeNum),
        name: record.name,
        selectedApprovers: employees,
      });
    } else {
      this.setState({
        employeeNum: record.employeeNum,
        name: record.name,
        selectedApprovers: employees,
      });
    }
  }

  @Bind()
  // 选中事件
  onSelectChange(selectedRowKeys) {
    this.setState({ selectedRowKeys });
  }

  @Bind()
  handleTableChange(pagination) {
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    this.setState({ paging: params }, () => {
      this.handleSearch();
    });
  }

  @Bind()
  handleCancel() {
    this.props.onCancel();
    this.setState({
      selectedRowKeys: [],
      selectedApprovers: [],
      paging: { page: 0, size: 10 },
    });
  }

  render() {
    const {
      form: { getFieldDecorator },
      loading,
      approveLoading,
      pagination,
      drawerVisible,
      anchor,
      isCarbon,
      isAssignNextApprover,
      needAppoint,
      delegateOrAddUser,
      getActionName,
      employeeList = [],
      appointerList = [],
      disabledUserList,
    } = this.props;
    const { selectedRowKeys } = this.state;
    let rowSelection = {};
    if (
      isAssignNextApprover ||
      isCarbon ||
      delegateOrAddUser === 'addSign' ||
      delegateOrAddUser === 'ApproveAndAddSign'
    ) {
      rowSelection = {
        selectedRowKeys,
        onChange: this.onSelectChange,
        onSelect: (record, selected, selectedRows) => {
          this.handleChangeEmployee(selected, record, selectedRows, [record.employeeNum]);
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
          const ids = map(changeRows, (item) => item.employeeNum);
          this.handleChangeEmployee(selected, {}, selectedRows, ids);
        },
        type: 'checkbox',
      };
    } else {
      rowSelection = {
        onSelect: (record, selected, selectedRows) => {
          this.handleChangeEmployee(selected, record, selectedRows);
        },
        getCheckboxProps: (record) => ({
          // 转交和加签不能选择已添加抄送的员工
          disabled: indexOf(disabledUserList, record.employeeId) > -1,
          employeeId: record.employeeId,
        }),
        type: 'radio',
      };
    }

    const title = isAssignNextApprover ? '' : getActionName(delegateOrAddUser);
    const columns = [
      {
        title: intl.get('entity.employee.code').d('员工编码'),
        dataIndex: isAssignNextApprover && needAppoint === 'N' ? 'employeeCode' : 'employeeNum',
        width: 100,
      },
      {
        title: intl.get('entity.employee.name').d('员工姓名'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('entity.department.name').d('部门名称'),
        dataIndex: 'unitName',
        width: 150,
      },
      {
        title: intl.get('entity.position.name').d('岗位名称'),
        dataIndex: 'positionName',
        width: 200,
      },
    ];
    return (
      <Modal
        destroyOnClose
        width={800}
        title={title}
        visible={drawerVisible}
        onCancel={this.handleCancel}
        onOk={this.handleSubmit}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        confirmLoading={approveLoading}
      >
        <Form layout="inline">
          <FormItem label={intl.get('entity.employee.code').d('员工编码')}>
            {getFieldDecorator('employeeNum', {})(<Input />)}
          </FormItem>
          <FormItem label={intl.get('entity.employee.name').d('员工姓名')}>
            {getFieldDecorator('name', {})(<Input />)}
          </FormItem>
          <Form.Item>
            <Button data-code="reset" style={{ marginRight: 8 }} onClick={this.handleFormReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button data-code="search" type="primary" htmlType="submit" onClick={this.handleSearch}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Form>
        <Row type="flex" justify="end" style={{ marginTop: '16px' }}>
          <Col span={24}>
            <Table
              bordered
              rowKey="employeeId"
              loading={loading}
              dataSource={
                isAssignNextApprover && needAppoint === 'N' ? appointerList : employeeList
              }
              pagination={pagination}
              rowSelection={rowSelection}
              columns={columns}
              scroll={{ x: tableScrollWidth(columns) }}
              onChange={this.handleTableChange}
            />
          </Col>
        </Row>
      </Modal>
    );
  }
}
