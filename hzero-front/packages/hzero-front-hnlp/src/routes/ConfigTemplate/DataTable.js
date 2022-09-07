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
import Popconfirm from '@/components/ProxyComponent/Popconfirm';

const actionStyle = {
  cursor: 'pointer',
};

const rowKey = 'dataId';

export default class DataTable extends Component {
  static propTypes = {
    onChange: PropTypes.func.isRequired,
    onRecordEdit: PropTypes.func.isRequired,
    onRecordRemove: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
    dataSource: PropTypes.array,
    pagination: PropTypes.any,
  };

  static defaultProps = {
    dataSource: [],
    pagination: false,
  };

  getColumns() {
    const { languageMessage, isSiteFlag } = this.props;
    return [
      isSiteFlag && {
        dataIndex: 'tenantName',
        width: 200,
        title: languageMessage.model.configTemplate.tenant,
      },
      {
        dataIndex: 'templateCode',
        title: languageMessage.model.configTemplate.templateCode,
        width: 150,
      },
      {
        dataIndex: 'templateName',
        title: languageMessage.model.configTemplate.templateName,
        width: 150,
      },
      {
        dataIndex: 'actualType',
        title: languageMessage.model.configTemplate.actualType,
        width: 150,
      },
      {
        dataIndex: 'type',
        title: languageMessage.model.configTemplate.type,
      },
      {
        dataIndex: 'typeNum',
        title: languageMessage.model.configTemplate.typeNum,
        width: 120,
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
                  record={record}
                  title={languageMessage.common.message.confirm.del}
                  onConfirm={this.handleRecordRemoveConfirm}
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
  handleRecordEditBtnClick(record) {
    const { onRecordEdit } = this.props;
    onRecordEdit(record);
  }

  @Bind()
  handleRecordRemoveConfirm(record) {
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
      />
    );
  }
}
