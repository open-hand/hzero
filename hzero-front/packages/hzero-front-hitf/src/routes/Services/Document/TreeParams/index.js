/**
 * TreeParams - XML和JSON类型的BODY
 * @date: 2019-6-4
 * @author: hulingfangzi <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Popconfirm, Select, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import Drawer from './Drawer';

const { Option } = Select;
/**
 * XML和JSON类型的BODY参数信息
 * @extends {Component} - React.Component
 * @reactProps {array} dataSource - 数据源
 * @reactProps {array} mainColumns - 除了操作列之外的列
 * @reactProps {boolean} loading - 列表数据加载标志
 * @reactProps {boolean} confirmLoading - 新建/编辑保存加载标志
 * @reactProps {array} paramValueTypes- BODY参数值的类型值集
 * @reactProps {string} actionType - HTTP操作类型(REQ/RESP)
 * @reactProps {string} paramTypes - 参数类型(HEADER/GET/PATH/BODY)
 * @reactProps {string} mimeType - BODY的MIME类型(form-data/x-www-form-urlencoded/xml/json/raw)
 * @reactProps {Function} onDelete - 删除参数
 * @reactProps {Function} onSave - 保存参数
 * @reactProps {number} interfaceId - 接口ID
 * @return React.element
 */
export default class TreeParams extends Component {
  state = {
    visible: false,
    currentParamData: null,
    currentAction: 'create',
  };

  /**
   * 打开参数侧滑
   */
  @Bind()
  openDrawer() {
    this.setState({ visible: true });
  }

  /**
   * 关闭参数侧滑
   */
  @Bind()
  closeDrawer() {
    this.setState({ visible: false });
  }

  /**
   * 更改JSON根节点
   * @param {string} value 选中的值
   */
  @Bind()
  changeRootType(value) {
    const { actionType } = this.props;
    const lower = actionType.toLowerCase();
    this.props.onChangeRoot({
      [`${lower}RootType`]: value,
      [`${lower}MimeType`]: 'application/json',
    });
  }

  /**
   * 保存参数
   */
  @Bind()
  handleSaveParams(values, flag) {
    this.props.onSave(values, flag);
  }

  /**
   * 创建参数
   * @param {boolean} recordCreateFlag - 是否在行上创建
   * @param {object} record - 当前行数据
   */
  @Bind()
  handleCreate(recordCreateFlag = false, record = null) {
    this.setState({
      visible: true,
      currentParamData: record,
      currentAction: recordCreateFlag ? 'createRecord' : 'create',
    });
  }

  /**
   * 编辑参数
   * @param {object} record - 当前行数据
   */
  @Bind()
  handleEdit(record) {
    this.setState({
      visible: true,
      currentParamData: record,
      currentAction: 'edit',
    });
  }

  /**
   * 获取JSON根节点类型
   */
  @Bind()
  getRootType() {
    const { actionType, reqRootType, respRootType } = this.props;
    let rootType = 'object';
    if (actionType === 'REQ' && reqRootType) {
      rootType = reqRootType;
    }

    if (actionType === 'RESP' && respRootType) {
      rootType = respRootType;
    }
    return rootType;
  }

  render() {
    const {
      dataSource,
      mainColumns,
      loading,
      confirmLoading,
      paramValueTypes,
      actionType,
      paramType,
      mimeType,
      rootTypes,
      onDelete,
    } = this.props;
    const { visible, currentParamData, recordCreateFlag, currentAction } = this.state;
    const isHaveRoot = !!dataSource.length;
    const columns = [
      ...mainColumns,
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        fixed: 'right',
        width: 180,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleEdit(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            (record.paramValueType === 'OBJECT' || record.paramValueType === 'ARRAY') && {
              key: 'create',
              ele: (
                <a onClick={() => this.handleCreate(true, record)}>
                  {intl.get('hzero.common.button.create').d('新建')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.create').d('新建'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => onDelete(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ].filter(Boolean);

          return operatorRender(operators, record);
        },
      },
    ];
    const tableProps = {
      columns,
      loading,
      dataSource,
      pagination: false,
      bordered: true,
      style: { marginBottom: '50px' },
      scroll: { x: tableScrollWidth(columns) },
      defaultExpandAllRows: true,
    };

    const drawerProps = {
      visible,
      paramType,
      mimeType,
      actionType,
      recordCreateFlag,
      currentAction,
      currentParamData,
      confirmLoading,
      isHaveRoot,
      paramValueTypes,
      onCancel: this.closeDrawer,
      onSave: this.handleSaveParams,
    };
    return (
      <>
        <div className="table-list-search" style={{ textAlign: 'right', marginTop: '10ox' }}>
          {mimeType === 'application/json' && (
            <>
              <span>{intl.get('hitf.document.view.title.rootType').d('JSON根类型')}:</span>
              <Select
                style={{ margin: '10px 8px 0', width: '90px' }}
                onChange={this.changeRootType}
                value={this.getRootType()}
              >
                {rootTypes.length &&
                  rootTypes.map(({ value, meaning }) => (
                    <Option key={value} value={value}>
                      {meaning}
                    </Option>
                  ))}
              </Select>
            </>
          )}
          <Button
            type="primary"
            onClick={() => this.handleCreate(false)}
            disabled={mimeType !== 'application/json' && isHaveRoot}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <Table {...tableProps} />
        <Drawer {...drawerProps} />
      </>
    );
  }
}
