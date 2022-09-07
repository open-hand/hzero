/**
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-28
 * @copyright 2019-05-28 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table, Tooltip } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { tableScrollWidth } from 'utils/utils';

import A from '@/components/ProxyComponent/A';

import styles from './styles.less';

const rowKey = 'id';

export default class DataTable extends Component {
  static propTypes = {
    onChange: PropTypes.func.isRequired,
    onRecordActualValueEdit: PropTypes.func.isRequired,
    onRowSelectionChange: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
    selectedRowKeys: PropTypes.array,
    dataSource: PropTypes.array,
    pagination: PropTypes.any,
  };

  static defaultProps = {
    selectedRowKeys: [],
    dataSource: [],
    pagination: false,
  };

  getColumns() {
    const { languageMessage } = this.props;
    return [
      {
        dataIndex: 'id',
        title: languageMessage.model.basicData.id,
        width: 200,
      },
      {
        dataIndex: 'dataKey',
        title: languageMessage.model.basicData.dataKey,
        width: 200,
      },
      {
        dataIndex: 'dataType',
        title: languageMessage.model.basicData.dataType,
        width: 200,
      },
      {
        dataIndex: 'value',
        title: languageMessage.model.basicData.value,
      },
      {
        dataIndex: 'context',
        title: languageMessage.model.basicData.context,
        width: 240,
        render: (context = []) => {
          if (context.length >= 2) {
            const title = (
              <ul className={styles['hnlp-basic-data-list']}>
                {context.map(({ contextKey, contextType }) => (
                  <li key={contextKey}>
                    <div>
                      <p>contextKey: {contextKey}</p>
                      <p>contextType: {contextType}</p>
                    </div>
                  </li>
                ))}
              </ul>
            );
            return (
              <Tooltip title={title}>
                <ul className={styles['hnlp-basic-data-list']}>
                  <li>
                    <div>
                      <p>contextKey: {context[0].contextKey}</p>
                      <p>contextType: {context[0].contextType}</p>
                    </div>
                  </li>
                  <li>...</li>
                </ul>
              </Tooltip>
            );
          } else {
            return (
              <ul className={styles['hnlp-basic-data-list']}>
                {context.map(({ contextKey, contextType }) => (
                  <li key={contextKey}>
                    <div>
                      <p>contextKey: {contextKey}</p>
                      <p>contextType: {contextType}</p>
                    </div>
                  </li>
                ))}
              </ul>
            );
          }
        },
      },
      {
        dataIndex: 'actualValue',
        title: languageMessage.model.basicData.actualValue,
        width: 200,
        fixed: 'right',
        render: (actualValue = [], record) => {
          const actualValueStr = actualValue.join(', ');
          return (
            <A
              onClick={this.handleRecordActualValueEditBtnClick}
              record={record}
              className={styles['text-over-flow']}
            >
              {actualValueStr || languageMessage.common.btn.edit}
            </A>
          );
        },
      },
    ];
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    const { onRowSelectionChange } = this.props;
    onRowSelectionChange({
      selectedRows,
      selectedRowKeys: selectedRows.map(record => record[rowKey]),
    });
  }

  // #region record operator
  @Bind()
  handleRecordActualValueEditBtnClick(record, e) {
    e.preventDefault();
    const { onRecordActualValueEdit } = this.props;
    onRecordActualValueEdit(record);
  }

  // #endregion

  render() {
    const {
      selectedRowKeys = [],
      dataSource = [],
      pagination = false,
      queryLoading = false,
      removeBatchLoading = false,
      onChange,
    } = this.props;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    const columns = this.getColumns();
    return (
      <Table
        bordered
        rowKey={rowKey}
        rowSelection={rowSelection}
        onChange={onChange}
        dataSource={dataSource}
        pagination={pagination}
        loading={queryLoading || removeBatchLoading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
