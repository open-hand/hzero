/**
 * Company - 租户级权限维护tab页 - 客户
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Button, Modal, Table, Tooltip, Switch } from 'hzero-ui';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';
import Lov from 'components/Lov';
// import Switch from 'components/Switch';
import notification from 'utils/notification';
import { createPagination, tableScrollWidth } from 'utils/utils';
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

@Form.create({ fieldNameProp: null })
@connect(({ authorityCustomer, loading }) => ({
  authorityCustomer,
  loading: loading.models.authorityCustomer,
}))
export default class Customer extends React.Component {
  /**
   *Creates an instance of Customer.
   * @param {*} props 属性
   * @memberof Customer
   */
  constructor(props) {
    super(props);
    this.PageSize = 10;
    this.state = {
      selectedRows: [],
      newCreateRows: [],
      formValues: {},
    };
  }

  componentDidMount() {
    this.queryValue();
  }

  /**
   *增加一行
   *
   * @memberof Customer
   */
  @Bind()
  addData() {
    const { dispatch } = this.props;
    const authorityLineId = `authorityLineId${uuidv4()}`;
    const data = {
      authorityLineId,
      isCreate: true,
      isEditing: true,
    };
    this.setState({
      newCreateRows: [...this.state.newCreateRows, data],
    });
    dispatch({
      type: 'authorityCustomer/addNewData',
      payload: data,
    });
  }

  /**
   *行取消事件
   *
   * @param {*} record 行数据
   * @memberof Customer
   */
  @Bind()
  cancel(record) {
    const { dispatch } = this.props;
    const { newCreateRows } = this.state;
    if (record.isCreate) {
      const listData = newCreateRows.filter(
        item => item.authorityLineId !== record.authorityLineId
      );
      this.setState({
        newCreateRows: listData,
      });
      dispatch({
        type: 'authorityCustomer/removeNewAdd',
        payload: {},
      });
    } else {
      this.edit(record, false);
    }
  }

  /**
   *编辑事件
   *
   * @param {*} record 行数据
   * @param {*} flag 是否编辑状态标记
   * @memberof Customer
   */
  @Bind()
  edit(record, flag) {
    const {
      dispatch,
      authorityCustomer: { data = {} },
    } = this.props;
    const index = data.list.findIndex(item => item.authorityLineId === record.authorityLineId);
    dispatch({
      type: 'authorityCustomer/editRow',
      payload: {
        data: [
          ...data.slice(0, index),
          {
            ...record,
            isEditing: flag,
          },
          ...data.slice(index + 1),
        ],
      },
    });
  }

  /**
   *表格选中事件
   *
   * @param {*} _ 占位
   * @param {*} rows 选中行
   * @memberof Customer
   */
  @Bind()
  handleSelectRows(_, rows) {
    this.setState({
      selectedRows: rows,
    });
  }

  /**
   *删除方法
   *
   * @memberof Customer
   */
  @Bind()
  remove() {
    const { dispatch, userId } = this.props;
    const { selectedRows, newCreateRows } = this.state;
    const onOk = () => {
      const deleteRows = selectedRows.filter(row => !row.isCreate);
      const deleteCreateRows = selectedRows.filter(row => row.isCreate);
      if (deleteCreateRows.length > 0) {
        const listData = newCreateRows.filter(newRow => {
          const arr = deleteCreateRows.filter(
            deleteRow => deleteRow.authorityLineId === newRow.authorityLineId
          );
          return !(arr.length > 0);
        });
        this.setState({
          newCreateRows: listData,
        });
      }
      if (deleteRows.length > 0) {
        dispatch({
          type: 'authorityCustomer/delete',
          payload: {
            userId,
            deleteRows,
          },
        }).then(response => {
          if (response) {
            this.refresh();
            notification.success();
          }
        });
      } else {
        this.refresh();
        notification.success();
      }
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  /**
   *刷新
   *
   * @memberof Customer
   */
  @Bind()
  refresh() {
    const { dispatch, userId } = this.props;
    this.setState({
      selectedRows: [],
      newCreateRows: [],
    });
    const staticData = {
      userId,
      page: 0,
      size: this.PageSize,
      authorityTypeCode: 'CUSTOMER',
    };
    dispatch({
      type: 'authorityCustomer/fetch',
      payload: staticData,
    });
  }

  /**
   *保存数据
   *
   * @memberof Customer
   */
  @Bind()
  dataSave() {
    const {
      form,
      dispatch,
      authorityCustomer: { head = {} },
      userId,
    } = this.props;
    const { newCreateRows } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const arr = [];
        const isNewRowKeys = newCreateRows.filter(v => v.isEditing);
        const fieldsArr = ['dataName', 'dataCode', 'dataId'];
        isNewRowKeys.forEach(item => {
          const itemObj = {};
          fieldsArr.forEach(_item => {
            itemObj[`${_item}`] = values[`${item.authorityLineId}#${_item}`];
          });
          if (!item.isCreate) {
            itemObj.authorityLineId = item.authorityLineId;
            itemObj.objectVersionNumber = item.objectVersionNumber;
          }
          arr.push(itemObj);
        });
        dispatch({
          type: 'authorityCustomer/add',
          payload: {
            authorityTypeCode: 'CUSTOMER',
            userId,
            userAuthority: head,
            userAuthorityLineList: arr,
          },
        }).then(response => {
          if (response) {
            this.refresh();
            notification.success();
          }
        });
      }
    });
  }

  /**
   *查询数据
   *
   * @memberof Customer
   */
  @Bind()
  queryValue() {
    const { form, dispatch, userId } = this.props;
    const staticData = {
      userId,
      page: 0,
      size: this.PageSize,
      authorityTypeCode: 'CUSTOMER',
    };
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        this.setState({
          formValues: fieldsValue,
        });
        dispatch({
          type: 'authorityCustomer/fetch',
          payload: {
            ...fieldsValue,
            ...staticData,
          },
        });
      }
    });
  }

  /**
   *lov选中后渲染同行其他数据
   *
   * @param {*} lovRecord
   * @param {*} tableRecord
   * @memberof Customer
   */
  @Bind()
  setDataCode(lovRecord, tableRecord) {
    this.props.form.setFieldsValue({
      [`${tableRecord.authorityLineId}#dataCode`]: lovRecord.companyNum,
    });
    this.props.form.setFieldsValue({
      [`${tableRecord.authorityLineId}#dataName`]: lovRecord.companyName,
    });
  }

  /**
   *分页change事件
   *
   * @memberof Customer
   */
  handleTableChange = pagination => {
    const { dispatch, userId } = this.props;
    const { formValues } = this.state;
    const params = {
      page: pagination.current - 1,
      size: this.PageSize,
    };

    const staticData = {
      userId,
      ...params,
      ...formValues,
      authorityTypeCode: 'CUSTOMER',
    };

    // if (sorter.field) {
    //   params.sort = {
    //     name: sorter.field,
    //     order: sorter.order,
    //   };
    // }

    dispatch({
      type: 'authorityCustomer/fetch',
      payload: staticData,
    });
  };

  /**
   *点击加入全部后触发事件
   *
   * @param {*} checked switch的value值
   * @memberof Customer
   */
  @Bind()
  includeAllFlag(checked) {
    const {
      dispatch,
      userId,
      authorityCustomer: { head = {} },
    } = this.props;
    dispatch({
      type: 'authorityCustomer/add',
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
      }
    });
  }

  /**
   *渲染查询结构
   *
   * @returns
   * @memberof Customer
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form layout="inline">
        <FormItem>
          {getFieldDecorator('dataName')(
            <Input
              placeholder={intl
                .get('hiam.authority.model.authorityCustomer.dataName')
                .d('客户企业名称')}
            />
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('dataCode')(
            <Input
              placeholder={intl
                .get('hiam.authority.model.authorityCustomer.dataCode')
                .d('客户企业代码')}
            />
          )}
        </FormItem>
        <FormItem>
          <Button type="primary" onClick={() => this.queryValue()} htmlType="submit">
            {intl.get('hzero.common.button.search').d('查询')}
          </Button>
        </FormItem>
        <FormItem>
          <Button type="primary" onClick={() => this.dataSave()}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </FormItem>
      </Form>
    );
  }

  /**
   *渲染事件
   *
   * @returns
   * @memberof Customer
   */
  render() {
    const {
      authorityCustomer: { data = {}, head = {} },
      loading,
    } = this.props;
    const { selectedRows, newCreateRows } = this.state;
    const { getFieldDecorator } = this.props.form;
    const dataSource = [...data.list, ...newCreateRows];
    const columns = [
      {
        title: intl.get('hiam.authority.model.authorityCustomer.dataName').d('客户企业名称'),
        dataIndex: 'dataId',
        width: 150,
        render: (text, tableRecord) =>
          tableRecord.isEditing ? (
            <FormItem>
              {getFieldDecorator(`${tableRecord.authorityLineId}#dataId`, {
                rules: [
                  {
                    required: true,
                  },
                ],
              })(
                <Lov
                  code="HIAM.USER_AUTHORITY_CUSTOMER"
                  queryParams={{ tenantId: 1 }}
                  textValue={text}
                  onChange={(_, record) => this.setDataCode(record, tableRecord)}
                />
              )}
            </FormItem>
          ) : (
            <div>{text}</div>
          ),
      },
      {
        title: intl.get('hiam.authority.model.authorityCustomer.dataCode').d('客户企业代码'),
        dataIndex: 'dataCode',
        width: 100,
        render: (text, record) =>
          record.isEditing ? (
            <FormItem>
              {getFieldDecorator(`${record.authorityLineId}#dataCode`, {
                initialValue: record.dataCode,
              })(<Input disabled />)}
            </FormItem>
          ) : (
            <div>{text}</div>
          ),
      },
      {
        title: intl.get('hiam.authority.model.authorityCustomer.dataName').d('客户企业名称'),
        dataIndex: 'dataName',
        width: 100,
        render: (text, record) =>
          record.isEditing ? (
            <FormItem>
              {getFieldDecorator(`${record.authorityLineId}#dataName`, {
                initialValue: record.dataName,
              })(<Input disabled />)}
            </FormItem>
          ) : (
            <div>{text}</div>
          ),
      },
    ];
    const rowSelection = {
      selectedRowKeys: selectedRows.map(n => n.authorityLineId),
      onChange: this.handleSelectRows,
    };
    const pagination = {
      ...createPagination(data),
      onShowSizeChange: (_, size) => {
        this.PageSize = size;
      },
    };
    return (
      <div>
        <div className="table-list-search">{this.renderForm()}</div>
        <div style={{ textAlign: 'right' }}>
          {!head.includeAllFlag && (
            <Button style={{ margin: '0 8px 16px 0' }} onClick={() => this.addData()} />
          )}
          {/* <Button icon="plus" style={{ margin: '0 8px 16px 0' }} onClick={() => this.addData()} /> */}
          {selectedRows.length > 0 && <Button onClick={() => this.remove()} />}
          <div style={{ display: 'inline-block', margin: '0 8px 16px 0' }}>
            <span style={{ marginRight: '8px' }}>
              {intl.get('hiam.authority.view.message.label').d('加入全部:')}
            </span>
            <Tooltip
              title={intl
                .get('hiam.authority.view.message.title.tooltip.sal')
                .d('“加入全部”即将所有客户权限自动添加至当前账户，无需再手工添加。')}
              placement="right"
            >
              <Switch values={!!head.includeAllFlag} onChange={this.includeAllFlag} />
            </Tooltip>
          </div>
        </div>
        <Table
          bordered
          rowKey="authorityLineId"
          loading={loading}
          dataSource={dataSource}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          rowSelection={head.includeAllFlag ? false : rowSelection}
          onChange={this.handleTableChange}
        />
      </div>
    );
  }
}
