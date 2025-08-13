import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Table, Alert } from 'hzero-ui';
import intl from 'utils/intl';
import styles from './index.less';

function initTotalList(columns) {
  const totalList = [];
  columns.forEach(column => {
    if (column.needTotal) {
      totalList.push({ ...column, total: 0 });
    }
  });
  return totalList;
}

class StandardTable extends PureComponent {
  constructor(props) {
    super(props);
    const { columns } = props;
    const needTotalList = initTotalList(columns);

    this.state = {
      selectedRowKeys: [],
      needTotalList,
    };
  }

  static propTypes = {
    onChange: PropTypes.func,
  };

  static defaultProps = {
    onChange: e => e,
  };

  static propTypes = {
    onChange: PropTypes.func,
  };

  static defaultProps = {
    onChange: e => e,
  };

  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    // clean state
    const { selectedRows = {}, columns = [] } = nextProps;
    if (selectedRows.length === 0) {
      const needTotalList = initTotalList(columns);
      this.setState({
        selectedRowKeys: [],
        needTotalList,
      });
    }
  }

  handleRowSelectChange = (selectedRowKeys, selectedRows) => {
    let needTotalList = [...this.state.needTotalList];
    needTotalList = needTotalList.map(item => ({
      ...item,
      total: selectedRows.reduce((sum, val) => sum + parseFloat(val[item.dataIndex], 10), 0),
    }));

    if (this.props.onSelectRow) {
      this.props.onSelectRow(selectedRows);
    }

    this.setState({ selectedRowKeys, needTotalList });
  };

  handleTableChange = (pagination, filters, sorter) => {
    this.props.onChange(pagination, filters, sorter);
  };

  cleanSelectedKeys = () => {
    this.handleRowSelectChange([], []);
  };

  render() {
    const { selectedRowKeys, needTotalList } = this.state;
    const {
      data = {},
      loading,
      columns,
      rowKey,
      handleSelectRows,
      showTableAlert = false,
    } = this.props;
    const { list = [], pagination } = data;
    const paginationProps = {
      showSizeChanger: true,
      showQuickJumper: true,
      ...pagination,
    };

    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
      getCheckboxProps: record => ({
        disabled: record.disabled,
      }),
    };

    return (
      <div className={styles.standardTable}>
        {handleSelectRows ? (
          handleSelectRows(needTotalList)
        ) : showTableAlert ? (
          <div className={styles.tableAlert}>
            <Alert
              message={
                <>
                  {intl.get('hzero.common.components.standardTable.select').d('已选择')}{' '}
                  <a style={{ fontWeight: 600 }}>{selectedRowKeys.length}</a>{' '}
                  {intl.get('hzero.common.components.standardTable.item').d('项')}&nbsp;&nbsp;
                  {needTotalList.map(item => (
                    <span style={{ marginLeft: 8 }} key={item.dataIndex}>
                      {item.title}
                      {intl.get('hzero.common.components.standardTable.atAll').d('总计')}&nbsp;
                      <span style={{ fontWeight: 600 }}>
                        {item.render ? item.render(item.total) : item.total}
                      </span>
                    </span>
                  ))}
                  <a onClick={this.cleanSelectedKeys} style={{ marginLeft: 24 }}>
                    {intl.get('hzero.common.button.clear').d('清空')}
                  </a>
                </>
              }
              type="info"
              showIcon
            />
          </div>
        ) : null}
        <Table
          loading={loading}
          rowKey={rowKey || 'key'}
          rowSelection={rowSelection}
          dataSource={list}
          columns={columns}
          size="small"
          pagination={paginationProps}
          onChange={this.handleTableChange}
        />
      </div>
    );
  }
}

export default StandardTable;
