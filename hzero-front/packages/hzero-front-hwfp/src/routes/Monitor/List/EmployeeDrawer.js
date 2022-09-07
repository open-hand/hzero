import React, { PureComponent } from 'react';
import { Modal, Form, Input, Row, Col, Table, Button } from 'hzero-ui';
import { remove, map } from 'lodash';
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
    paging: { page: 0, size: 10 },
    employeeNum: '',
    // name: '',
  };

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { form, retryData, onSearch = (e) => e } = this.props;
    const { paging } = this.state;
    form.validateFields((err, values) => {
      onSearch({
        ...paging,
        tenantId: retryData.tenantId,
        lovCode: 'HPFM.EMPLOYEE',
        employeeNum: values.employeeNum,
        name: values.name,
        enabledFlag: 1,
      });
    });
  }

  @Bind()
  handleFormSearchSearch() {
    this.setState(
      {
        paging: {
          size: 10,
          page: 0,
        },
      },
      () => {
        this.handleSearch();
      }
    );
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
    const { employeeNum } = this.state;
    const { onAction, retryData = {} } = this.props;
    if (employeeNum) {
      onAction({ processInstanceId: retryData.encryptId, employeeCode: employeeNum });
    } else {
      Modal.warning({
        title: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
    }
    this.setState({
      selectedRowKeys: [],
      paging: { page: 0, size: 10 },
    });
  }

  @Bind()
  handleChangeEmployee(selected, record, employees, employeeCodes) {
    const { changeEmployee, dispatch } = this.props;
    if (this.props.isCarbon) {
      if (selected) {
        dispatch({
          type: 'monitor/updateState',
          payload: { changeEmployee: employees },
        });
      } else {
        const centerChangeEmployee = changeEmployee.slice();
        remove(centerChangeEmployee, (item) => employeeCodes.indexOf(item.employeeNum) !== -1);
        dispatch({
          type: 'monitor/updateState',
          payload: { changeEmployee: centerChangeEmployee },
        });
      }
    } else {
      this.setState({
        employeeNum: record.employeeNum,
        // name: record.name,
      });
    }
  }

  @Bind()
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
      paging: { page: 0, size: 10 },
    });
  }

  render() {
    const {
      form: { getFieldDecorator },
      loading,
      submitLoading,
      pagination,
      drawerVisible,
      isCarbon,
      employeeList = [],
    } = this.props;
    const { selectedRowKeys } = this.state;
    let rowSelection = {};
    if (isCarbon) {
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
        type: 'radio',
      };
    }

    const columns = [
      {
        title: intl.get('entity.employee.code').d('员工编码'),
        dataIndex: 'employeeNum',
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
        title={intl.get('hwfp.monitor.view.option.retry').d('指定审批人')}
        visible={drawerVisible}
        onCancel={this.handleCancel}
        onOk={this.handleSubmit}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={submitLoading}
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
            <Button
              data-code="search"
              type="primary"
              htmlType="submit"
              onClick={this.handleFormSearchSearch}
            >
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Form>
        <Row type="flex" justify="end" style={{ marginTop: '16px' }}>
          <Col span={24}>
            <Table
              bordered
              rowKey="employeeId"
              dataSource={employeeList}
              pagination={pagination}
              rowSelection={rowSelection}
              loading={loading}
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
