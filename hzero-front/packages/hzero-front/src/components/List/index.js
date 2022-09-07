import React, { PureComponent } from 'react';
import { Table, Alert } from 'hzero-ui';
// import PropTypes from 'prop-types';
import classnames from 'classnames';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import styles from './index.less';

@Bind()
export default class List extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [],
    };
  }
  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    // 将selectedRowKeys 置为空数组,防止 antd 遍历时出错
    if (!nextProps.selectedRows || nextProps.selectedRows.length === 0) {
      this.setState({
        selectedRowKeys: [],
      });
    }
  }

  /**
   * 分页或者排序改变
   * @param {Object} pagination 分页信息
   * @param {Array} filters 过滤的字段
   * @param {Object} sortInfo 排序的信息
   */
  handleOnChange = (pagination, filters, sortInfo) => {
    const { onChange } = this.props;
    if (!onChange) {
      return;
    }

    onChange({
      ...pagination,
      orderField: sortInfo.columnKey,
      orderMethod: sortInfo.order,
    });
  };

  /**
   * 选中记录改变
   * @param {*[]} selectedRowKeys 选中记录的key组成的数组
   * @param {Object[]} selectedRows 选中记录组成的数组
   */
  handleRowSelectChange = (selectedRowKeys, selectedRows) => {
    if (this.props.onSelectRow) {
      this.props.onSelectRow(selectedRows);
    }
    if (this.props.rowSelection && this.props.rowSelection.onChange) {
      this.props.rowSelection.onChange(selectedRowKeys, selectedRows);
    }
    if (!this.props.rowSelection) {
      this.setState({ selectedRowKeys });
    }
  };

  render() {
    const { description, scrollWidth, wrapStyle, singleCol, sortInfo, ...tableProps } = this.props;
    const newTableProps = {
      ...tableProps,
    };
    if (sortInfo) {
      // 判断是否有排序字段
      newTableProps.columns = tableProps.columns.map(item => {
        if (sortInfo[item.dataIndex]) {
          return {
            ...item,
            sorter: true,
          };
        }
        return item;
      });
    }
    const { selectedRowKeys } = this.state;
    if (newTableProps.pagination) {
      // 分页信息
      newTableProps.pagination = {
        showQuickJumper: true,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100', '300', '500'],
        showTotal: (total, range) =>
          intl
            .get('hzero.common.pagination.total', {
              range1: range[0],
              range2: range[1],
              total,
            })
            .d(`显示 ${range[0]} - ${range[1]} 共 ${total} 条`),
        ...tableProps.pagination,
      };
    }

    if (newTableProps.bordered) {
      newTableProps.size = 'small';
    }

    // 设置列的宽度
    if (singleCol) {
      newTableProps.scroll = { x: false };
    } else if (newTableProps.scroll === false) {
      delete newTableProps.scroll;
    } else {
      const newScrollWidth =
        scrollWidth ||
        tableProps.columns.reduce(
          (result, column) =>
            // 列表字段 默认宽度 120
            result + column.width || 120,
          0
        );
      newTableProps.scroll = { ...newTableProps.scroll, x: newScrollWidth };
    }

    // 设置选中记录状态,当记录中有 disabled 属性时 会禁止选中
    const rowSelection =
      tableProps.rowSelection === null
        ? undefined
        : tableProps.rowSelection || {
            selectedRowKeys,
            onChange: this.handleRowSelectChange,
            getCheckboxProps: record => ({
              disabled: record.disabled,
            }),
          };
    return (
      <div style={{ width: '100%', ...wrapStyle }}>
        {rowSelection !== undefined && (
          <Alert
            message={
              <div className={styles.info}>
                {intl.get('hzero.common.components.list.select').d('已选择')}{' '}
                <a style={{ fontWeight: 600 }}>{rowSelection.selectedRowKeys.length} </a>
                {intl.get('hzero.common.components.list.item').d('项')}&nbsp;&nbsp;
                {rowSelection.selectedRowKeys.length > 0 && (
                  <a onClick={this.cleanSelectedKeys} style={{ marginLeft: 24 }}>
                    {intl.get('hzero.common.button.clear').d('清空')}
                  </a>
                )}
                {description && (
                  <span className={styles.descript}>&nbsp;&nbsp;&nbsp;&nbsp;{description}</span>
                )}
              </div>
            }
            type="info"
            showIcon
            style={{ marginBottom: 8 }}
          />
        )}
        <Table
          rowSelection={rowSelection}
          {...newTableProps}
          onChange={this.handleOnChange}
          className={classnames({
            [styles.table]: true,
            [styles.singleCol]: singleCol,
          })}
        />
      </div>
    );
  }
}
