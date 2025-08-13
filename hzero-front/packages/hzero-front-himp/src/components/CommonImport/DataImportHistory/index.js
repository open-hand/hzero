/**
 * DataImportHistory
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-21
 * @copyright 2019-08-21 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';

import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { createPagination, getResponse } from 'utils/utils';

import SearchForm from './SearchForm';
import DataTable from './DataTable';

class DataImportHistory extends Component {
  searchFormRef = React.createRef();

  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      pagination: false,
      // cachePagination: {}, // 删除后刷新需要之前的分页信息
    };
  }

  componentDidMount() {
    this.handleSearch();
  }

  async reload() {
    const { cachePagination } = this.state;
    await this.handleSearch(cachePagination);
  }

  async handleSearch(pagination = {}) {
    this.setState({
      queryLoading: true,
    });
    const { queryImportHistory } = this.props;
    const fieldsValue = this.searchFormRef.current
      ? this.searchFormRef.current.props.form.getFieldsValue()
      : {};
    fieldsValue.creationDateTo = fieldsValue.creationDateTo
      ? fieldsValue.creationDateTo.format(DEFAULT_DATETIME_FORMAT)
      : undefined;
    fieldsValue.creationDateFrom = fieldsValue.creationDateFrom
      ? fieldsValue.creationDateFrom.format(DEFAULT_DATETIME_FORMAT)
      : undefined;
    const res = await queryImportHistory({ ...pagination, ...fieldsValue });
    const responseRes = getResponse(res);
    if (responseRes) {
      this.setState({
        dataSource: responseRes.content,
        pagination: createPagination(responseRes),
        cachePagination: pagination,
        queryLoading: false,
      });
    }
  }

  // SearchForm
  @Bind()
  async handleSearchFormSearch() {
    await this.handleSearch();
  }

  // DataTable
  @Bind()
  async handleTableChange(page, filter, sorter) {
    await this.handleSearch({ page, sort: sorter });
  }

  @Bind()
  async handleRecordRestore(record) {
    const { onRecordRestore } = this.props;
    await onRecordRestore(record);
  }

  @Bind()
  async handleRecordDelete(record) {
    const { onRecordDelete } = this.props;
    this.setState({
      deleteLoading: true,
    });
    await onRecordDelete(record);
    await this.reload();
    this.setState({
      deleteLoading: false,
    });
  }

  render() {
    const { dataImportStatus } = this.props;
    const { dataSource, pagination, queryLoading, deleteLoading } = this.state;
    return (
      <>
        <SearchForm
          onSearch={this.handleSearchFormSearch}
          wrappedComponentRef={this.searchFormRef}
        />
        <DataTable
          onRecordRestore={this.handleRecordRestore}
          onRecordDelete={this.handleRecordDelete}
          onChange={this.handleTableChange}
          dataImportStatus={dataImportStatus}
          dataSource={dataSource}
          pagination={pagination}
          queryLoading={queryLoading}
          deleteLoading={deleteLoading}
        />
      </>
    );
  }
}

DataImportHistory.propTypes = {
  queryImportHistory: PropTypes.func.isRequired,
  onRecordRestore: PropTypes.func.isRequired,
  onRecordDelete: PropTypes.func.isRequired,
};

export default DataImportHistory;
