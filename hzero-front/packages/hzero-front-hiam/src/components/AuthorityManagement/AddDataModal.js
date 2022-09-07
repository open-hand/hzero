/**
 * 数据权限维度动态渲染 - 增加数据弹框
 * @date: 2019-9-11
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Button, Form, Input, Modal, Table, notification, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import qs from 'querystring';
import { isFunction, isArray, isEmpty } from 'lodash';

import intl from 'utils/intl';
import {
  tableScrollWidth,
  getCurrentOrganizationId,
  getResponse,
  createPagination,
} from 'utils/utils';
import { queryLovData } from 'services/api';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

const formItemLayout = {
  labelCol: {
    sm: { span: 8 },
  },
  wrapperCol: {
    sm: { span: 14 },
  },
};

@Form.create({ fieldNameProp: null })
export default class EditForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      addRows: [],
      dataSource: [],
      addTrueRows: [],
      treeKeys: [],
      pagination: {},
      loading: true,
      flag: true,
    };
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { modalVisible, lov } = nextProps;
    if (modalVisible && this.state.flag && !isEmpty(lov)) {
      this.queryValue();
      this.setState({ flag: false });
    }
  }

  /**
   * 点击确定触发事件
   */
  @Bind()
  okHandle() {
    const { addData, tenantId } = this.props;
    const { addTrueRows } = this.state;
    if (!isEmpty(addTrueRows)) {
      if (tenantId !== undefined) {
        const arr = addTrueRows.map((item) => ({ tenantId, ...item }));
        addData(arr);
      } else {
        const arr = addTrueRows;
        addData(arr);
      }
      this.setState({ flag: true, addRows: [], addTrueRows: [] });
    } else {
      notification.warning({
        message: intl.get('hzero.common.message.confirm.selected.atLeast').d('请至少选择一行数据'),
      });
    }
  }

  /**
   * 点击取消触发事件
   */
  @Bind()
  cancelHandle() {
    const { onHideAddModal } = this.props;
    if (onHideAddModal) {
      this.setState({ flag: true, addRows: [], addTrueRows: [] });
      onHideAddModal(false);
    }
  }

  /**
   *分页change事件
   */
  @Bind()
  handleTableChange(pagination = {}) {
    this.queryValue(pagination);
  }

  /**
   * 表格勾选
   * @param {null} _ 占位
   * @param {object} selectedRow 选中行
   */
  @Bind()
  onSelectChange(selectedRowKeys, selectedRow) {
    const {
      defaultFlag,
      lov: { valueField, displayField },
    } = this.props;
    const { addRows } = this.state;
    let newSelectedRow = addRows;
    let addTrueRows = [];
    const arr = [];
    newSelectedRow = newSelectedRow.filter((temp) => selectedRowKeys.includes(temp[valueField]));
    newSelectedRow = [...newSelectedRow, ...selectedRow].filter((item) => {
      if (arr.includes(item[valueField])) {
        return false;
      } else {
        arr.push(item[valueField]);
        return true;
      }
    });
    if (defaultFlag) {
      addTrueRows = newSelectedRow;
    } else {
      addTrueRows = newSelectedRow.map((item) => ({
        dataId: item[valueField],
        dataName: item[displayField],
      }));
    }
    this.setState({ addRows: newSelectedRow, addTrueRows });
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
   * 树 child 属性更改
   * @param {Array} list 原树结构数据
   * @param {String} childName 要替换的 childName
   */

  @Bind()
  setChildren(data, childName) {
    return childName
      ? data.map((n) => {
          const item = n;
          if (!isEmpty(n[childName])) {
            this.defineProperty(item, 'children', [{ ...n[childName] }]);
          }
          if (!isEmpty(item.children)) {
            item.children = this.setChildren(item.children);
          }
          return item;
        })
      : data;
  }

  /**
   * 处理返回列表数据
   * @param {Object|Array} data - 返回的列表数据
   */
  @Bind()
  dataFilter(data) {
    const {
      lov: { valueField: rowkey = 'lovId', childrenFieldName },
    } = this.props;
    const isTree = isArray(data);
    const hasParams = !isEmpty(
      Object.values(this.props.form.getFieldsValue()).filter((e) => e !== undefined && e !== '')
    );
    const list = isTree ? this.setChildren(data, childrenFieldName) : data.content;
    const pagination = !isTree && createPagination(data);
    const treeKeys = []; // 树状 key 列表
    if (isTree && hasParams) {
      /**
       * 遍历生成树列表
       * @param {*} treeList - 树列表数据
       */
      const flatKeys = (treeList) => {
        if (isArray(treeList.children) && !isEmpty(treeList.children)) {
          treeKeys.push(treeList[rowkey]);
          treeList.children.forEach((item) => flatKeys(item));
        } else {
          treeKeys.push(treeList[rowkey]);
        }
      };

      list.forEach((item) => flatKeys(item)); // 遍历生成 key 列表
    }
    this.setState({
      dataSource: list,
      treeKeys,
      pagination,
    });
  }

  /**
   * 查询方法
   */
  @Bind()
  queryValue(pagination = {}) {
    this.setState({ loading: true });
    const filter = this.props.form.getFieldsValue();
    const { queryUrl, pageSize, lovCode, lovTypeCode } = this.props.lov;
    const { queryParams = {} } = this.props;
    let nowQueryParams = queryParams || {};
    if (isFunction(nowQueryParams)) {
      nowQueryParams = nowQueryParams();
    }
    const queryIndex = queryUrl.indexOf('?');
    let sourceQueryParams = {};
    if (queryIndex !== -1) {
      sourceQueryParams = qs.parse(queryUrl.substr(queryIndex + 1));
    }
    const sourceParams = {
      ...filter,
      page: pagination.current - 1 || 0,
      size: pagination.pageSize || pageSize || 10,
      ...sourceQueryParams,
      ...nowQueryParams,
      tenantId: getCurrentOrganizationId(),
    };
    const params =
      lovTypeCode !== 'URL'
        ? Object.assign(sourceParams, {
            lovCode,
          })
        : sourceParams;

    /**
     * 替换查询 Url 中的变量
     * @param {String} url
     * @param {Object} data
     */
    function getUrl(url, data) {
      let ret = url;
      const organizationRe = /\{organizationId\}|\{tenantId\}/g;
      Object.keys(data).map((key) => {
        const re = new RegExp(`{${key}}`, 'g');
        ret = ret.replace(re, data[key]);
        return ret;
      });
      if (organizationRe.test(ret)) {
        ret = ret.replace(organizationRe, getCurrentOrganizationId());
      }
      const index = ret.indexOf('?'); // 查找是否有查询条件
      if (queryIndex !== -1) {
        ret = ret.substr(0, index);
      }
      return ret;
    }
    const url = getUrl(queryUrl, queryParams);
    queryLovData(url, params)
      .then((res) => {
        if (getResponse(res)) {
          this.dataFilter(res);
        }
      })
      .then(() => {
        // this.setState({ addRows: [], addTrueRows: [] });
      })
      .finally(() => {
        this.setState({ loading: false });
      });
  }

  @Bind()
  renderForm() {
    const { lov: { queryFields = [] } = {} } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      // <Form>
      //   <Row type="flex" align="bottom" gutter={24}>
      //     {queryFields &&
      //       queryFields.map(queryItem => (
      //         <Col span={8}>
      //           <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={queryItem.label}>
      //             {getFieldDecorator(queryItem.field)(<Input />)}
      //           </Form.Item>
      //         </Col>
      //       ))}
      //     <Col span={8}>
      //       <FormItem {...SEARCH_FORM_ITEM_LAYOUT}>
      //         <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
      //           {intl.get('hzero.common.button.reset').d('重置')}
      //         </Button>
      //         <Button type="primary" htmlType="submit" onClick={this.queryValue}>
      //           {intl.get('hzero.common.button.search').d('查询')}
      //         </Button>
      //       </FormItem>
      //     </Col>
      //   </Row>
      // </Form>
      <Form>
        <div style={{ display: 'flex', marginBottom: '10px', alignItems: 'flex-start' }}>
          <Row type="flex" align="bottom" gutter={16}>
            {queryFields &&
              queryFields.map((queryItem) => (
                <Col span={12}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={queryItem.label}
                    {...formItemLayout}
                    key={queryItem.field}
                  >
                    {getFieldDecorator(queryItem.field)(<Input />)}
                  </Form.Item>
                </Col>
              ))}
          </Row>
          <div style={{ display: 'flex', flexShrink: 0, alignItems: 'center', height: '40px' }}>
            <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button type="primary" htmlType="submit" onClick={this.queryValue}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </div>
        </div>
      </Form>
    );
  }

  render() {
    const {
      title,
      modalVisible,
      confirmLoading,
      lov: { tableFields = [], valueField: rowKey = '' } = {},
    } = this.props;
    const { addRows, dataSource = [], pagination = {}, treeKeys, loading } = this.state;
    const rowSelection = {
      onChange: this.onSelectChange,
      selectedRowKeys: addRows.map((n) => n[rowKey]),
    };
    const treeProps = isArray(dataSource)
      ? {
          uncontrolled: true,
          expandedRowKeys: treeKeys,
        }
      : {};
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={confirmLoading}
        width={800}
        onOk={this.okHandle}
        onCancel={this.cancelHandle}
      >
        <div className="table-list-search">{this.renderForm()}</div>
        <Table
          bordered
          rowKey={rowKey}
          columns={tableFields}
          scroll={{ x: tableScrollWidth(tableFields) }}
          loading={loading}
          dataSource={dataSource}
          rowSelection={rowSelection}
          pagination={pagination}
          onChange={this.handleTableChange}
          {...treeProps}
        />
      </Modal>
    );
  }
}
