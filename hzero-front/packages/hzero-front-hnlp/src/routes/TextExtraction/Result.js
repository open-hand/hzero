/**
 * Result
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-04
 * @copyright 2019-06-04 © HAND
 */

import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { isEmpty } from 'lodash';

import { tableScrollWidth } from 'utils/utils';

const rowKey = (record, index) => index;

export default class Result extends Component {
  getColumns() {
    const { languageMessage } = this.props;
    return [
      {
        dataIndex: 'tagType',
        title: languageMessage.model.textTraction.tagType,
        width: 150,
      },
      {
        dataIndex: 'tagValue',
        title: languageMessage.model.textTraction.tagValue,
      },
    ];
  }

  render() {
    const { languageMessage, result = {} } = this.props;
    const columns = this.getColumns();
    const dataSource = isEmpty(result) ? [] : result.statusCode === 'S' ? result.result || [] : [];
    return (
      <>
        <div>
          {languageMessage.view.title.resultStatus}: {result.statusCode}
        </div>
        <div>
          {languageMessage.view.title.resultMsg}: {result.msg}
        </div>
        <Table
          bordered
          pagination={false}
          rowKey={rowKey}
          dataSource={dataSource}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
        />
      </>
    );
  }
}
