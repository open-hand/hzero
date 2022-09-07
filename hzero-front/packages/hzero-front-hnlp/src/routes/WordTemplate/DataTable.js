/**
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import Span from '@/components/ProxyComponent/Span';

const actionStyle = {
  cursor: 'pointer',
};

const rowKey = 'templateWordId';

export default class DataTable extends Component {
  static propTypes = {
    onChange: PropTypes.func.isRequired,
    onRecordEdit: PropTypes.func.isRequired,
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
    const { languageMessage, isTenantRoleLevel } = this.props;
    return [
      !isTenantRoleLevel && {
        dataIndex: 'tenantName',
        title: languageMessage.model.templateWord.tenant,
        width: 200,
      },
      {
        dataIndex: 'templateName',
        title: languageMessage.model.templateWord.templateName,
        width: 200,
      },
      {
        dataIndex: 'actualWord',
        title: languageMessage.model.templateWord.actualWord,
        width: 200,
      },
      {
        dataIndex: 'word',
        title: languageMessage.model.templateWord.word,
      },
      {
        key: 'action',
        title: languageMessage.common.btn.action,
        width: 120,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <Span style={actionStyle} record={record} onClick={this.handleRecordEditBtnClick}>
                  {languageMessage.common.btn.edit}
                </Span>
              ),
              len: 2,
              title: languageMessage.common.btn.edit,
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
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
  handleRecordEditBtnClick(record, e) {
    e.preventDefault();
    const { onRecordEdit } = this.props;
    onRecordEdit(record);
  }

  @Bind()
  handleRecordRemoveBtnClick(record, e) {
    e.preventDefault();
    const { onRecordRemove } = this.props;
    onRecordRemove(record);
  }

  // #endregion

  render() {
    const {
      selectedRowKeys = [],
      dataSource = [],
      pagination = false,
      queryLoading = false,
      queryDetailLoading = false,
      removeBatchLoading = false,
      onChange,
      isTenantId,
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
        loading={queryLoading || removeBatchLoading || queryDetailLoading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        isTenantRoleLevel={isTenantId}
      />
    );
  }
}
