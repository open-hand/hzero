/**
 * Customer - 租户级权限维护tab页 - 客户
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Button, Modal, Tooltip, Switch, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import notification from 'utils/notification';
import { tableScrollWidth, getCurrentOrganizationId } from 'utils/utils';

import AddDataModal from '@/components/AuthorityManagement/AddDataModal';
/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

/**
 * 租户级权限管理 - 客户
 * @extends {Component} - React.Component
 * @reactProps {Object} authorityCustomer - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ authorityCustomer, loading }) => ({
  authorityCustomer,
  organizationId: getCurrentOrganizationId(),
  addLoading: loading.effects['authorityCustomer/addAuthorityCustomer'],
  fetchLoading: loading.effects['authorityCustomer/fetchAuthorityCustomer'],
}))
@Form.create({ fieldNameProp: null })
export default class Customer extends React.Component {
  /**
   *Creates an instance of Customer.
   * @param {Object} props 属性
   */
  constructor(props) {
    super(props);
    this.state = {
      selectRows: [],
      switchLoading: false,
      addModalVisible: false,
    };
  }

  componentDidMount() {
    this.fetchData();
  }

  /**
   *查询数据
   *
   * @param {Object} pageData
   */
  @Bind()
  fetchData(pageData) {
    const { form, dispatch, userId } = this.props;
    const staticData = {
      userId,
      authorityTypeCode: 'CUSTOMER',
    };
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        dispatch({
          type: 'authorityCustomer/fetchAuthorityCustomer',
          payload: {
            ...fieldsValue,
            ...staticData,
            ...pageData,
          },
        });
      }
    });
  }

  /**
   * 添加数据
   * @param {Aarray} addRows 选择的数据
   */
  @Bind()
  addCompany(addRows) {
    const {
      dispatch,
      authorityCustomer: { head = {} },
      userId,
    } = this.props;
    dispatch({
      type: 'authorityCustomer/addAuthorityCustomer',
      payload: {
        authorityTypeCode: 'CUSTOMER',
        userId,
        userAuthority: head,
        userAuthorityLineList: addRows,
      },
    }).then(response => {
      if (response) {
        this.onHideAddModal();
        notification.success();
        this.refresh();
      }
    });
  }

  /**
   *删除方法
   */
  @Bind()
  remove() {
    const { dispatch, userId } = this.props;
    const { selectRows } = this.state;
    const onOk = () => {
      dispatch({
        type: 'authorityCustomer/deleteAuthorityCustomer',
        payload: {
          userId,
          deleteRows: selectRows,
        },
      }).then(response => {
        if (response) {
          this.refresh();
          notification.success();
        }
      });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  /**
   *刷新
   */
  @Bind()
  refresh() {
    this.fetchData();
    this.setState({
      selectRows: [],
    });
  }

  /**
   * 表格勾选
   * @param {null} _ 占位
   * @param {object} selectedRow 选中行
   */
  @Bind()
  onSelectChange(_, selectedRows) {
    this.setState({ selectRows: selectedRows });
  }

  /**
   * 展示弹出框
   */
  @Bind()
  onShowAddModal() {
    this.setState({
      addModalVisible: true,
    });
  }

  /**
   * 隐藏弹出框
   */
  @Bind()
  onHideAddModal() {
    this.setState({
      addModalVisible: false,
    });
  }

  /**
   *点击查询按钮事件
   */
  @Bind()
  queryValue() {
    this.fetchData();
    this.setState({ selectRows: [] });
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   *分页change事件
   */
  @Bind()
  handleTableChange(pagination = {}) {
    this.fetchData({
      page: pagination,
    });
  }

  /**
   *点击加入全部后触发事件
   *
   * @param {*Boolean} checked switch的value值
   */
  @Bind()
  includeAllFlag(checked) {
    this.setState({
      switchLoading: true,
    });
    const {
      dispatch,
      userId,
      authorityCustomer: { head = {} },
    } = this.props;
    dispatch({
      type: 'authorityCustomer/addAuthorityCustomer',
      payload: {
        authorityTypeCode: 'CUSTOMER',
        userId,
        userAuthority: {
          ...head,
          includeAllFlag: checked ? 1 : 0,
        },
        userAuthorityLineList: [],
      },
    }).then(response => {
      if (response) {
        this.refresh();
        notification.success();
        this.setState({
          switchLoading: false,
        });
      }
    });
  }

  /**
   *渲染查询结构
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form layout="inline">
        <FormItem
          label={intl.get('hiam.authority.model.authorityCustomer.dataName').d('客户企业名称')}
        >
          {getFieldDecorator('dataName')(<Input />)}
        </FormItem>
        <FormItem
          label={intl.get('hiam.authority.model.authorityCustomer.dataCode').d('客户企业代码')}
        >
          {getFieldDecorator('dataCode')(<Input typeCase="upper" trim inputChinese={false} />)}
        </FormItem>
        <FormItem>
          <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
            {intl.get('hzero.common.button.reset').d('重置')}
          </Button>
          <Button type="primary" onClick={() => this.queryValue()} htmlType="submit">
            {intl.get('hzero.common.button.search').d('查询')}
          </Button>
        </FormItem>
      </Form>
    );
  }

  render() {
    const {
      authorityCustomer: { list = [], head = {}, pagination },
      userId,
      organizationId,
      addLoading,
      fetchLoading,
    } = this.props;
    const { switchLoading, addModalVisible, selectRows } = this.state;
    const columns = [
      {
        title: intl.get('hiam.authority.model.authorityCustomer.dataName').d('客户企业名称'),
        dataIndex: 'dataName',
      },
      {
        title: intl.get('hiam.authority.model.authorityCustomer.dataCode').d('客户企业代码'),
        dataIndex: 'dataCode',
        width: 300,
      },
    ];

    const addModalOptions = {
      defaultFlag: true,
      rowKey: 'dataId',
      title: intl.get('hiam.authority.view.title.modal.customer').d('选择客户'),
      lov: {
        lovTypeCode: 'URL',
        valueField: 'dataId',
        displayField: 'dataName',
        tableFields: columns,
        queryFields: [
          {
            label: intl.get('hiam.authority.model.authorityCustomer.dataCode').d('客户企业代码'),
            field: 'dataCode',
          },
          {
            label: intl.get('hiam.authority.model.authorityCustomer.dataName').d('客户企业名称'),
            field: 'dataName',
          },
        ],
        queryUrl: `${HZERO_IAM}/v1/${organizationId}/users/${userId}/data/customers`,
      },
      confirmLoading: addLoading,
      modalVisible: addModalVisible,
      onHideAddModal: this.onHideAddModal,
      addData: this.addCompany,
    };

    const rowSelection = {
      onChange: this.onSelectChange,
      selectedRowKeys: selectRows.map(n => n.authorityLineId),
    };

    return (
      <div>
        <div className="table-list-search">{this.renderForm()}</div>
        <div style={{ textAlign: 'right' }}>
          {!head.includeAllFlag && (
            <>
              <Button style={{ margin: '0 8px 16px 0' }} onClick={() => this.onShowAddModal()}>
                {intl.get('hiam.authority.view.button.table.create.customer').d('新建客户权限')}
              </Button>
              <Button
                style={{ margin: '0 8px 16px 0' }}
                disabled={selectRows.length <= 0}
                onClick={() => this.remove()}
              >
                {intl.get('hiam.authority.view.button.table.delete.customer').d('删除客户权限')}
              </Button>
            </>
          )}
          <div style={{ display: 'inline-block', margin: '0 8px 16px 0' }}>
            <span style={{ marginRight: '8px' }}>
              {intl.get('hiam.authority.view.message.label').d('加入全部:')}
            </span>
            <Tooltip
              title={intl
                .get('hiam.authority.view.message.title.tooltip.cus')
                .d('“加入全部”即将所有客户权限自动添加至当前账户，无需再手工添加。')}
              placement="right"
            >
              <Switch
                loading={switchLoading || fetchLoading}
                checked={!!head.includeAllFlag}
                onChange={this.includeAllFlag}
              />
            </Tooltip>
          </div>
        </div>
        <Table
          bordered
          rowKey="authorityLineId"
          loading={fetchLoading}
          dataSource={list}
          rowSelection={rowSelection}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.handleTableChange}
        />
        <AddDataModal {...addModalOptions} />
      </div>
    );
  }
}
