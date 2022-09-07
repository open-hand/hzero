/**
 * GeneralParams - 除了树形(BODY/XML、JSON)结构之外参数的参数信息
 * @date: 2019/5/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import Drawer from './Drawer';
import AlternativeModal from '../AlternativeModal';

/**
 * 参数信息
 * @extends {Component} - React.Component
 * @reactProps {string} paramType - 参数类型(HEADER/GET/PATH/BODY)
 * @reactProps {string} actionType - HTTP操作类型(REQ/RESP)
 * @reactProps {array} paramValueTypes - BODY参数值的类型值集
 * @reactProps {array} requestHeaderTypes - 请求头参数名值集
 * @reactProps {array} mainColumns - 除了操作列之外的列
 * @reactProps {array} dataSource - 数据源
 * @reactProps {boolean} loading - 列表数据加载标志
 * @reactProps {boolean} confirmLoading - 新建/编辑保存加载标志
 * @reactProps {Function} onDelete - 删除参数
 * @reactProps {Function} onSave - 保存参数
 * @reactProps {number} interfaceId - 接口ID
 * @reactProps {string} mimeType - BODY的MIME类型(form-data/x-www-form-urlencoded/XML/JSON)
 * @return React.element
 */
export default class GeneralParams extends Component {
  state = {
    visible: false,
    currentParamData: {}, // 编辑时选中的行数据
  };

  /**
   * 打开参数侧滑
   */
  @Bind()
  openDrawer(record = {}) {
    this.setState({
      visible: true,
      currentParamData: record,
    });
  }

  /**
   * 关闭参数侧滑
   */
  @Bind()
  closeDrawer() {
    this.setState({ visible: false });
  }

  /**
   * 保存参数
   */
  @Bind()
  handleSaveParams(values, flag) {
    this.props.onSave(values, flag);
  }

  render() {
    const { visible, currentParamData } = this.state;
    const {
      paramType,
      actionType,
      paramValueTypes = [],
      requestHeaderTypes = [],
      mainColumns,
      dataSource,
      loading,
      confirmLoading,
      onDelete,
      interfaceId,
      mimeType = '',
    } = this.props;
    const drawerProps = {
      visible,
      paramType,
      actionType,
      mimeType,
      paramValueTypes,
      requestHeaderTypes,
      confirmLoading,
      currentParamData,
      onCancel: this.closeDrawer,
      onSave: this.handleSaveParams,
      wrappedComponentRef: (form) => {
        this.queryParamsFormRef = form;
      },
    };
    const columns = [
      ...mainColumns,
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        fixed: paramType === 'HEADER' ? false : 'right',
        width: paramType === 'HEADER' ? 90 : 180,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.openDrawer(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (!mimeType) {
            operators.push({
              key: 'alternative',
              ele: <AlternativeModal paramId={record.paramId} interfaceId={interfaceId} />,
              len: 3,
              title: intl.get('hitf.document.view.title.alternative').d('备选值'),
            });
          }
          operators.push({
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
          });
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <>
        <div className="table-list-search" style={{ textAlign: 'right' }}>
          <Button type="primary" onClick={() => this.openDrawer({})}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <Table
          rowKey="paramId"
          bordered
          loading={loading}
          pagination={false}
          columns={columns}
          dataSource={dataSource}
          scroll={{ x: tableScrollWidth(columns) }}
        />
        <Drawer {...drawerProps} />
      </>
    );
  }
}
