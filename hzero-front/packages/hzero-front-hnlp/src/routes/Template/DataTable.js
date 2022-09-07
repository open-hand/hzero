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

import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import Span from '@/components/ProxyComponent/Span';
import Popconfirm from '@/components/ProxyComponent/Popconfirm';

const actionStyle = {
  cursor: 'pointer',
};

const rowKey = 'templateId';

export default class DataTable extends Component {
  static propTypes = {
    onChange: PropTypes.func.isRequired,
    onRecordEdit: PropTypes.func.isRequired,
    onRecordRemove: PropTypes.func.isRequired,
    // onRowSelectionChange: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
    // selectedRowKeys: PropTypes.array,
    dataSource: PropTypes.array,
    pagination: PropTypes.any,
  };

  static defaultProps = {
    dataSource: [],
    pagination: false,
  };

  getColumns() {
    const { languageMessage, isTenantRoleLevel } = this.props;
    return [
      !isTenantRoleLevel && {
        dataIndex: 'tenantName',
        title: languageMessage.model.template.tenantName,
        width: 200,
      },
      {
        dataIndex: 'templateCode',
        title: languageMessage.model.template.templateCode,
        width: 200,
      },
      {
        dataIndex: 'templateName',
        title: languageMessage.model.template.templateName,
        width: 200,
      },
      {
        dataIndex: 'replaceChar',
        title: languageMessage.model.template.replaceChar,
        width: 200,
      },
      {
        dataIndex: 'maxGramMeaning',
        title: languageMessage.model.template.maxGram,
        width: 120,
      },
      {
        dataIndex: 'description',
        title: languageMessage.model.template.description,
      },
      {
        dataIndex: 'enabledFlag',
        title: languageMessage.common._status,
        width: 120,
        render: enableRender,
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
            {
              key: 'remove',
              ele: (
                <Popconfirm
                  title={languageMessage.common.message.confirm.del}
                  record={record}
                  onConfirm={this.handleRecordRemoveBtnClick}
                >
                  <span style={actionStyle}>{languageMessage.common.btn.del}</span>
                </Popconfirm>
              ),
              len: 2,
              title: languageMessage.common.btn.del,
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
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
      dataSource = [],
      pagination = false,
      queryLoading = false,
      queryDetailLoading = false,
      removeLoading = false,
      onChange,
      organizationId,
      isTenant,
    } = this.props;
    const columns = this.getColumns();
    return (
      <Table
        bordered
        rowKey={rowKey}
        onChange={onChange}
        dataSource={dataSource}
        pagination={pagination}
        loading={queryLoading || removeLoading || queryDetailLoading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        organizationId={organizationId}
        isTenantRoleLevel={isTenant}
      />
    );
  }
}
