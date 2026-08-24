/**
 * HistoryData
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/9/29
 * @copyright 2019/9/29 © HAND
 */

import React, { Component, createRef } from 'react';
import { Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import notification from 'utils/notification';
import {
  createPagination,
  getCurrentOrganizationId,
  getRefFormData,
  getResponse,
} from 'utils/utils';
import { getEnvConfig } from 'utils/iocUtils';

import { downloadFile, queryIdpValue } from 'hzero-front/lib/services/api';

import FilterForm from './FilterForm';
import DataTable from './DataTable';

import { historyDataCancel, historyDataQuery } from './historyDataService';

const { BKT_PLATFORM, HZERO_FILE } = getEnvConfig();

export default class HistoryData extends Component {
  constructor(props) {
    super(props);
    this.state = {
      typeList: [], // 任务状态
      errorInfoModalVisible: false, // 错误信息展示弹窗的 页面
      errorInfo: '', // 错误信息
    };
    this.filterFormRef = createRef();
  }

  componentDidMount() {
    queryIdpValue('HPFM.ASYNC.TASK.STATE').then((res) => {
      const typeList = getResponse(res);
      if (typeList) {
        this.setState({ typeList });
      }
    });
    this.handleSearch();
  }

  // base
  handleSearch(pagination = {}) {
    const params = getRefFormData(this.filterFormRef);
    this.setState({ queryLoading: true }, () => {
      historyDataQuery({ ...params, ...pagination }).then(
        (dataSourceRes) => {
          const dataSource = getResponse(dataSourceRes);
          if (dataSource) {
            this.setState({
              dataSource: dataSource.content,
              pagination: createPagination(dataSource),
              cachePagination: pagination,
              queryLoading: false,
              cancelLoading: false,
            });
          } else {
            this.setState({
              queryLoading: false,
              cancelLoading: false,
            });
          }
        },
        () => {
          this.setState({
            queryLoading: false,
            cancelLoading: false,
          });
        }
      );
    });
  }

  reload() {
    const { cachePagination = {} } = this.state;
    this.handleSearch(cachePagination);
  }

  // FilterForm
  @Bind()
  handleFilterFormSearch() {
    this.handleSearch();
  }

  // DataTable
  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  /**
   * 显示错误信息
   * @param {string} errorInfo
   */
  @Bind()
  handleShowErrorInfo(errorInfo) {
    this.setState({
      errorInfoModalVisible: true,
      errorInfo,
    });
  }

  @Bind()
  handleRecordDownload(record) {
    const api = `${HZERO_FILE}/v1/${getCurrentOrganizationId()}/files/download`;
    const queryParams = [{ name: 'url', value: encodeURIComponent(record.downloadUrl) }];
    queryParams.push({ name: 'bucketName', value: BKT_PLATFORM });
    queryParams.push({ name: 'directory', value: 'hpfm01' });
    downloadFile({
      requestUrl: api,
      queryParams,
    });
  }

  @Bind()
  handleRecordCancel(record) {
    historyDataCancel({ taskCode: decodeURIComponent(record.taskCode) }).then(
      (res) => {
        if (getResponse(res)) {
          notification.success();
          this.reload();
        } else {
          this.setState({
            cancelLoading: false,
          });
        }
      },
      () => {
        this.setState({
          cancelLoading: false,
        });
      }
    );
  }

  // ErrorInfo Modal
  @Bind()
  hideErrorInfoModal() {
    this.setState({
      errorInfo: '',
      errorInfoModalVisible: false,
    });
  }

  render() {
    const {
      typeList,
      dataSource,
      pagination,
      errorInfo = '',
      errorInfoModalVisible = false,
      queryLoading,
      cancelLoading,
    } = this.state;
    return (
      <>
        <FilterForm
          wrappedComponentRef={this.filterFormRef}
          onSearch={this.handleFilterFormSearch}
          typeList={typeList}
        />
        <DataTable
          dataSource={dataSource}
          pagination={pagination}
          onChange={this.handleTableChange}
          onShowErrorInfo={this.handleShowErrorInfo}
          onRecordDownload={this.handleRecordDownload}
          onRecordCancel={this.handleRecordCancel}
          loading={queryLoading || cancelLoading}
        />
        <Modal
          title={intl.get('hzero.common.component.excelExport.v.hd.errorInfo').d('异常信息')}
          visible={errorInfoModalVisible}
          onCancel={this.hideErrorInfoModal}
          footer={null}
          wrapClassName="ant-modal-sidebar-right"
          transitionName="move-right"
        >
          {errorInfo}
        </Modal>
      </>
    );
  }
}
