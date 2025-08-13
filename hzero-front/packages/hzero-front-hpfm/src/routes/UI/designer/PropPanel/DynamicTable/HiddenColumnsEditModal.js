/**
 * HiddenColumnsEditModal.js
 * 隐藏域编辑
 * @date 2018/10/26
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { Modal, Input, Button, Popconfirm } from 'hzero-ui';
import { map, set, filter, uniqWith, forEach } from 'lodash';
import uuid from 'uuid/v4';

import EditTable from 'components/EditTable';

import {
  hiddenColumnKey,
  hiddenColumnPrefix,
  hiddenColumnSep,
} from 'components/DynamicComponent/DynamicTable/utils';

import { getEditTableData } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

const editRecordField = '_status';
const editCreate = 'create';
const editEditing = 'update';

const modalBodyStyle = {
  minHeight: 300,
  overflowY: 'auto',
};

export default class HiddenColumnsEditModal extends React.Component {
  state = {
    // hiddenColumns: [], // 用于比较 和 props 的区别
    // hiddenColumnsDataSource: [], // 编辑的数据源
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const { hiddenColumns } = nextProps;
    const { hiddenColumns: prevHiddenColumns } = prevState;
    if (hiddenColumns !== prevHiddenColumns) {
      return {
        hiddenColumns,
        hiddenColumnsDataSource: map(hiddenColumns, hiddenColumn => {
          const c = {};
          set(c, hiddenColumn[attributeNameProp], hiddenColumn);
          const columnKey = Object.keys(c[hiddenColumnKey])[0];
          const [columnName, columnLabel] = hiddenColumn[attributeValueProp].split(hiddenColumnSep);
          return {
            columnKey,
            columnLabel,
            columnName,
            [editRecordField]: editEditing,
          };
        }),
      };
    }
    return null;
  }

  constructor(props) {
    super(props);
    this.handleHiddenColumnRemove = this.handleHiddenColumnRemove.bind(this);
    this.handleHiddenColumnAdd = this.handleHiddenColumnAdd.bind(this);
    this.handleSaveHiddenColumns = this.handleSaveHiddenColumns.bind(this);
  }

  render() {
    const { hiddenColumns, ...modalProps } = this.props;
    const { hiddenColumnsDataSource = [] } = this.state;
    return (
      <Modal
        title="隐藏域"
        bodyStyle={modalBodyStyle}
        {...modalProps}
        onOk={this.handleSaveHiddenColumns}
      >
        <div style={{ marginBottom: 10 }}>
          <Button icon="plus" onClick={this.handleHiddenColumnAdd} />
        </div>
        <EditTable
          rowKey="columnKey"
          columns={this.getColumns()}
          dataSource={hiddenColumnsDataSource}
          bordered
          pagination={false}
        />
      </Modal>
    );
  }

  getColumns() {
    if (!this.columns) {
      this.columns = [
        {
          key: 'action',
          title: intl.get('hzero.common.button.action').d('操作'),
          width: 80,
          render: (item, record) => {
            return (
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
                onConfirm={() => this.handleHiddenColumnRemove(record)}
              >
                <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
              </Popconfirm>
            );
          },
        },
        {
          title: '标题',
          dataIndex: 'columnLabel',
          render: (item, record) => {
            const { getFieldDecorator } = record.$form;
            return getFieldDecorator('columnLabel', {
              initialValue: item,
              rules: [
                {
                  pattern: new RegExp(`[^${hiddenColumnSep}]`),
                  message: `不能包含: ${hiddenColumnSep}`,
                },
              ],
            })(<Input />);
          },
        },
        {
          title: '字段名',
          dataIndex: 'columnName',
          width: 200,
          render: (item, record) => {
            const { getFieldDecorator } = record.$form;
            return getFieldDecorator('columnName', {
              initialValue: item,
              rules: [
                {
                  pattern: new RegExp(`[^${hiddenColumnSep}]`),
                  message: `不能包含: ${hiddenColumnSep}`,
                },
              ],
            })(<Input />);
          },
        },
      ];
    }
    return this.columns;
  }

  handleHiddenColumnRemove(removeRecord) {
    const { hiddenColumnsDataSource = [] } = this.state;
    this.setState({
      hiddenColumnsDataSource: filter(hiddenColumnsDataSource, record => removeRecord !== record),
    });
  }

  handleHiddenColumnAdd() {
    const { hiddenColumnsDataSource = [] } = this.state;
    this.setState({
      hiddenColumnsDataSource: [
        ...hiddenColumnsDataSource,
        { columnKey: uuid(), [editRecordField]: editCreate },
      ],
    });
  }

  handleSaveHiddenColumns() {
    const { onOk } = this.props;
    const { hiddenColumnsDataSource = [] } = this.state;
    const saveDataSource = getEditTableData(hiddenColumnsDataSource, []);
    let errMessage;
    const uniqDataSource = uniqWith(saveDataSource, (r1, r2) => r1.columnName === r2.columnName);
    if (uniqDataSource.length !== saveDataSource.length) {
      errMessage = '隐藏域字段名 必须唯一';
      return false;
    }
    if (errMessage) {
      notification.error({ message: errMessage });
      return;
    }
    const hiddenColumns = [];
    forEach(saveDataSource, hiddenColumn => {
      hiddenColumns.push({
        [attributeNameProp]: `${hiddenColumnPrefix}[${hiddenColumn.columnKey}]`,
        [attributeValueProp]: `${hiddenColumn.columnName}${hiddenColumnSep}${hiddenColumn.columnLabel}`,
        [attributeTypeProp]: DataType.String,
      });
    });
    onOk(hiddenColumns);
  }
}
