/**
 * sqlExecute - SQL执行界面/左侧可访问表格树
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Form, Tree, Button, Select, Icon, Spin, Tooltip, Input, Pagination, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

import emitter from './ev';
import style from './index.less';

const FormItem = Form.Item;
const { TreeNode } = Tree;
//
// const getParentKey = (key, tree) => {
//   let parentKey;
//   for (let i = 0; i < tree.length; i++) {
//     const node = tree[i];
//     if (node.children) {
//       if (node.children.some(item => item.key === key)) {
//         parentKey = node.key;
//       } else if (getParentKey(key, node.children)) {
//         parentKey = getParentKey(key, node.children);
//       }
//     }
//   }
//   return parentKey;
// };

/**
 * 对象property属性定义方法
 * @function defineProperty
 * @param {!object} obj - 目标对象
 * @param {!string} property - 对象属性名称
 * @param {any} value - 属性值
 * @returns
 */
function defineProperty(obj, property, value) {
  Object.defineProperty(obj, property, {
    value,
    writable: true,
    enumerable: false,
    configurable: true,
  });
}
@connect(({ sqlExecute, loading }) => ({
  sqlExecute,
  fetchTable: loading.effects['sqlExecute/fetchDataTable'],
  tenantId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hpfm.sqlExecute' })
export default class TreeTable extends PureComponent {
  state = {
    treeData: [],
    currentPage: 1,
    totalElements: 1,
    loadedKeys: [], // 已经加载的节点，需要配合 loadData 使用
    expandedKeys: [], // 展开指定的树节点
  };

  componentDidMount() {
    const { dispatch, tenantId } = this.props;
    this.queryDataTable();
    dispatch({
      type: 'sqlExecute/fetchServer',
      payload: {
        tenantId,
      },
    });
  }

  /**
   * 查询数据表
   */
  @Bind()
  queryDataTable(params = {}) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'sqlExecute/fetchDataTable',
      payload: { ...params, tenantId },
    }).then(res => {
      if (res) {
        this.setState({
          treeData: res.content.map(item => ({
            title: item.tableName,
            key: item.tableName,
            isLeaf: false,
            ...item,
          })),
          totalElements: res.totalElements,
        });
      }
    });
  }

  /**
   * 查询数据表对应的字段
   */
  @Bind()
  queryTableField() {
    const { dispatch } = this.props;
    dispatch({
      type: 'sqlExecute/fetchTableField',
      payload: {},
    });
  }

  // 异步加载表字段数据
  @Bind()
  onLoadData(treeNode) {
    const { dispatch } = this.props;
    return new Promise(resolve => {
      if (treeNode.props.children) {
        resolve();
        return;
      }
      dispatch({
        type: 'sqlExecute/fetchTableField',
        payload: {
          tableSchema: treeNode.props.dataRef.tableSchema,
          tableName: treeNode.props.dataRef.tableName,
        },
      }).then(res => {
        if (res) {
          defineProperty(
            treeNode.props.dataRef,
            'children',
            res.map(item => ({
              title: item.columnName,
              key: `${treeNode.props.dataRef.tableName}.${item.columnName}`,
              isLeaf: true,
              ...item,
            }))
          );
          this.setState({
            treeData: [...this.state.treeData],
          });
        }
        resolve();
      });
    });
  }

  @Bind()
  onExpand(expandedKeys) {
    this.setState({
      loadedKeys: expandedKeys,
      expandedKeys,
    });
  }

  // 点击树节点触发
  @Bind()
  onSelect(selectedKeys, info) {
    const { dispatch } = this.props;
    const isLast = info.node.props.dataRef.isLeaf;
    if (isLast) {
      dispatch({
        type: 'sqlExecute/updateState',
        payload: {
          clickedValue: info.node.props.dataRef.columnName,
        },
      });
      emitter.emit('treeNodeClick', info.node.props.dataRef.columnName);
    } else {
      dispatch({
        type: 'sqlExecute/updateState',
        payload: {
          clickedValue: info.node.props.dataRef.tableName,
        },
      });
      emitter.emit('treeNodeClick', info.node.props.dataRef.tableName);
    }
  }

  @Bind()
  renderTreeNodes(data) {
    return data.map(item => {
      const tableTitle = (
        <Tooltip title={item.tableComment}>
          <span className={style['table-title']}>{item.tableName}</span>
        </Tooltip>
      );
      const fieldTitle = (
        <Tooltip title={item.columnComment}>
          <span>
            {item.columnName}
            <span className={style['field-type-title']}>
              &nbsp;&nbsp;{item.dataType}&nbsp;({item.characterOctetLength})
            </span>
          </span>
        </Tooltip>
      );

      if (item.children) {
        return (
          <TreeNode
            title={item.isLeaf ? fieldTitle : tableTitle}
            key={item.key}
            dataRef={item}
            icon={item.isLeaf ? <Icon type="profile" /> : <Icon type="table" theme="outlined" />}
          >
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return (
        <TreeNode
          {...item}
          title={item.isLeaf ? fieldTitle : tableTitle}
          dataRef={item}
          icon={item.isLeaf ? <Icon type="profile" /> : <Icon type="table" theme="outlined" />}
        />
      );
    });
  }

  /**
   * @function handleChange - 表格树分页操作
   * @param {Object} page - 分页
   * @param {Object} pageSize - 分页大小
   */
  @Bind()
  handleChange(page, pageSize) {
    this.setState({
      loadedKeys: [],
      expandedKeys: [],
      currentPage: page,
    });
    this.queryDataTable({
      page: page - 1,
      size: pageSize,
      ...this.props.form.getFieldsValue(),
    });
  }

  @Bind()
  handSearch() {
    const { form } = this.props;
    form.validateFields((err, fieldsValues) => {
      if (!err) {
        this.queryDataTable({ ...fieldsValues });
        this.setState({
          loadedKeys: [],
          expandedKeys: [],
          currentPage: 1,
        });
      }
    });
  }

  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  onServerChange(value) {
    const { dispatch } = this.props;
    dispatch({
      type: 'sqlExecute/updateState',
      payload: {
        serverName: value,
      },
    });
  }

  @Bind()
  renderForm() {
    const {
      sqlExecute: { serverList = [] },
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form>
        <FormItem
          label={intl.get('hpfm.sqlExecute.model.sqlExecute.serverName').d('服务名')}
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 18 }}
        >
          {getFieldDecorator('serverName')(
            <Select onChange={this.onServerChange} allowClear>
              {serverList.map(item => {
                return (
                  <Select.Option value={item.serviceName} key={item.serviceName}>
                    {item.serviceName}
                  </Select.Option>
                );
              })}
            </Select>
          )}
        </FormItem>
        <FormItem
          label={intl.get('hpfm.sqlExecute.model.sqlExecute.tableName').d('表名称')}
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 18 }}
        >
          {getFieldDecorator('tableName')(<Input />)}
        </FormItem>
        <FormItem>
          <Col span={18} offset={5}>
            <Button
              type="primary"
              htmlType="submit"
              onClick={this.handSearch}
              style={{ marginRight: 10 }}
            >
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
            <Button onClick={this.handleFormReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
          </Col>
        </FormItem>
      </Form>
    );
  }

  render() {
    const { fetchTable } = this.props;
    const { treeData, currentPage, totalElements, loadedKeys, expandedKeys } = this.state;
    return (
      <React.Fragment>
        <div className="table-list-search">{this.renderForm()}</div>
        <Spin spinning={fetchTable}>
          <Tree
            showIcon
            loadData={this.onLoadData}
            onExpand={this.onExpand}
            onSelect={this.onSelect}
            loadedKeys={loadedKeys}
            expandedKeys={expandedKeys}
          >
            {this.renderTreeNodes(treeData)}
          </Tree>
        </Spin>
        <Pagination
          simple
          pageSize={15}
          // defaultCurrent={1}
          current={currentPage}
          total={totalElements}
          onChange={this.handleChange}
          style={{ marginTop: 10 }}
        />
      </React.Fragment>
    );
  }
}
