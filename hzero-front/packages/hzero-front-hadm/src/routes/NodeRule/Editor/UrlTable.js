/**
 * nodeRule - 节点组规则 - 新增/编辑 - url表格
 * @date: 2018-9-7
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Table, Form, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { createPagination, tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

const { Search } = Input;

@Form.create({ fieldNameProp: null })
export default class UrlTable extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      value: '',
    };
  }

  /**
   * @function handleSearch - 搜索
   * @param {string} value - 搜索条件
   */
  @Bind()
  handleSearch(value) {
    const { dispatch, productEnvId } = this.props;
    dispatch({
      type: 'nodeRule/fetchUrlList',
      payload: { productEnvId, path: value, page: 0, size: 10 },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, urlList, standardTable, selectedUrlRowKeys, onUrlSelectChange } = this.props;
    const { value } = this.state;
    const columns = [
      {
        title: intl.get('hadm.nodeRule.model.nodeRule.serviceName').d('服务名'),
        dataIndex: 'serviceName',
      },
      {
        title: 'URL',
        dataIndex: 'path',
      },
      {
        title: 'HttpMethod',
        width: 80,
        dataIndex: 'method',
      },
    ];
    const rowSelection = {
      selectedRowKeys: selectedUrlRowKeys,
      onChange: onUrlSelectChange,
    };
    return (
      <>
        <Search
          style={{ width: 200, marginBottom: 12 }}
          placeholder={`${intl.get('hadm.nodeRule.model.nodeRule.path').d('URL')}`}
          value={value}
          onChange={(e) => {
            this.setState({ value: e.target.value });
          }}
          onSearch={this.handleSearch}
        />
        <Table
          bordered
          rowKey="id"
          rowSelection={rowSelection}
          loading={loading}
          dataSource={urlList.content || []}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={createPagination(urlList)}
          onChange={(pagination) => {
            standardTable(pagination, value);
          }}
        />
      </>
    );
  }
}
