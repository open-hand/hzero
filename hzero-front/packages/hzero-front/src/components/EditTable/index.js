import React from 'react';
import { Form, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import styles from './index.less';

const { TableRowContext } = Table;

const EditableRow = ({ form, fixed, record, ...props }) => {
  if (!fixed && (record._status === 'create' || record._status === 'update')) {
    // eslint-disable-next-line
    record.$form = form;
    return (
      <TableRowContext.Provider value={form}>
        <tr {...props} />
      </TableRowContext.Provider>
    );
  }
  return <tr {...props} />;
};

const EditableFormRow = Form.create({
  fieldNameProp: null,
  // 表单值 改变 出发 onDataChange
  onValuesChange: (props, changeValues, allValues) => {
    const { onDataChange, record } = props;
    if (onDataChange) {
      onDataChange(record, changeValues, allValues);
    }
  },
  // 将 onDataChange 解构出来, 不应该传到 Form 下
  mapProps: props => {
    const { onDataChange, ...passProps } = props || {};
    return passProps;
  },
})(EditableRow);

/**
 * 行内编辑表格 EditTable
 *
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @extends {Component} - React.PureComponent
 * @reactProps {!boolean} [editing] - 新增、编辑的行数据需要提供该标识
 * @reactProps {!boolean} [isCreate] - 新增的行数据需要提供该标识
 * @reactProps {any} [otherProps] - 表格其他属性
 * @returns React.element
 * @example
 * 使用方式跟 Hzero-ui Table 无异，唯一的区别是被编辑的行数据的record将会被添加$form属性。
 */
export default class EditTable extends React.Component {
  // 记录 当前 dataSource 对应的 数据是否编辑,
  // 会在 dataSource更新时 置为 false,
  // 会在表单编辑后置为 true
  dataHasChange = false;

  /**
   * 如果 dataSource 的 引用 发生了变化, 则重置 dataHasChange
   */
  componentDidUpdate(prevProps) {
    const { prevDataSource } = prevProps;
    const { dataSource } = this.props;
    if (prevDataSource !== dataSource) {
      if (this.dataHasChange) {
        this.dataHasChange = false;
      }
    }
  }

  render() {
    // antd table property
    const { onRow, ...otherProps } = this.props;
    const components = {
      body: {
        row: EditableFormRow,
      },
    };
    const editTableProps = {
      components,
      rowClassName: styles['hzero-edit-table'],
      onRow: this.onRow,
      ...otherProps,
    };
    return <Table {...editTableProps} />;
  }

  /**
   * 表单变化触发的回调
   * @param {object} record - 有变化的 行
   * @param {object} changeValues - 行 表单 改变的值
   * @param {object} allValues - 所有的表单值
   */
  @Bind()
  handleDataChange(record, changeValues, allValues) {
    if (!this.dataHasChange) {
      this.dataHasChange = true;
    }
    const { onDataChange } = this.props;
    if (onDataChange) {
      onDataChange(record, changeValues, allValues);
    }
  }

  /**
   * 设置 hzero-ui Table onRow
   */
  @Bind()
  onRow(record, index) {
    const { onRow } = this.props;
    let userRowProps;
    if (onRow) {
      userRowProps = onRow(record, index);
    } else {
      userRowProps = {};
    }
    return {
      record,
      ...userRowProps,
      onDataChange: this.handleDataChange,
    };
  }

  /**
   * 数据是否改变过
   * 给获得 ref 的 使用使用
   */
  @Bind()
  hasDataChange() {
    return this.dataHasChange;
  }
}
