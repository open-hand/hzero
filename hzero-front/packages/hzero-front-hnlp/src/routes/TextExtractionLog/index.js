/**
 * TextExtraction
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel } from 'utils/utils';
import { getFieldsValueByRef } from '@/utils/utils';

import SearchForm from './SearchForm';
import DataTable from './DataTable';
import ViewModal from './ViewModal';

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hnlp.textExtractionLog'] })
export default class TextExtraction extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // pagination,
      viewModalVisible: false,
      viewRecord: {},
    };
    this.searchFormRef = React.createRef();
  }

  getLanguageMessage() {
    return {
      model: {
        textExtractionLog: {
          text: intl.get('hnlp.textExtractionLog.model.textExtractionLog.text').d('识别文本'),
          tenant: intl.get('hzero.common.model.tenantName').d('租户'),
          convert: intl.get('hnlp.textExtractionLog.model.textExtractionLog.convert').d('转换文本'),
          contexts: intl.get('hnlp.textExtractionLog.model.textExtractionLog.contexts').d('上下文'),
          tagType: intl.get('hnlp.textExtractionLog.model.textExtractionLog.tagType').d('标记类型'),
          tagValue: intl.get('hnlp.textExtractionLog.model.textExtractionLog.tagValue').d('标记值'),
          templateCode: intl
            .get('hnlp.textExtractionLog.model.textExtractionLog.templateCode')
            .d('模板编码'),
        },
      },
      view: {
        title: {
          view: intl.get('hnlp.textExtractionLog.view.title.view').d('查看详情'),
          extractionResult: intl
            .get('hnlp.textExtractionLog.view.title.extractionResult')
            .d('识别结果'),
          finalResult: intl.get('hnlp.textExtractionLog.view.title.finalResult').d('转换结果'),
          viewOriData: intl.get('hnlp.textExtractionLog.view.title.viewOriData').d('查看原始数据'),
        },
        btn: {
          viewOriData: intl.get('hnlp.textExtractionLog.view.btn.viewOriData').d('查看原始数据'),
        },
      },
      common: {
        btn: {
          search: intl.get('hzero.common.button.search').d('查询'),
          reset: intl.get('hzero.common.button.reset').d('重置'),
          view: intl.get('hzero.common.button.view').d('查看'),
          close: intl.get('hzero.common.button.close').d('关闭'),
          action: intl.get('hzero.common.button.action').d('操作'),
          viewMore: intl.get('hzero.common.button.viewMore').d('更多查询'),
          collected: intl.get('hzero.common.button.collected').d('收起查询'),
          maintenance: intl
            .get('hnlp.textExtractionLog.model.textExtractionLog.maintain')
            .d('数据维护'),
        },
      },
    };
  }

  componentDidMount() {
    this.handleSearch();
  }

  // common
  handleSearch(pagination = {}) {
    const { query } = this.props;
    const queryData = getFieldsValueByRef(this.searchFormRef);
    this.setState({
      pagination,
    });
    return query({
      query: { ...queryData, ...pagination },
    });
  }

  reload() {
    const { pagination } = this.state;
    this.handleSearch(pagination);
  }

  // SearchForm
  @Bind()
  handleSearchFormSubmit() {
    this.handleSearch();
  }

  // Table

  @Bind()
  handleRecordView(record) {
    this.setState({
      viewModalVisible: true,
      viewRecord: record,
    });
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  // Modal
  @Bind()
  handleViewModalClose() {
    this.setState({
      viewModalVisible: false,
      viewRecord: {},
    });
  }

  render() {
    const languageMessage = this.getLanguageMessage();
    const { queryLoading, dataSource, pagination, isSiteFlag } = this.props;
    const { viewModalVisible, viewRecord } = this.state;
    return (
      <>
        <Header
          title={intl
            .get('hnlp.textExtractionLog.view.message.title.textExtractionLog')
            .d('识别结果')}
        />
        <Content>
          <SearchForm
            languageMessage={languageMessage}
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormSubmit}
            isSiteFlag={isSiteFlag}
          />
          <DataTable
            onRecordView={this.handleRecordView}
            onChange={this.handleTableChange}
            dataSource={dataSource}
            pagination={pagination}
            languageMessage={languageMessage}
            queryLoading={queryLoading}
            isSiteFlag={isSiteFlag}
          />
          <ViewModal
            languageMessage={languageMessage}
            visible={viewModalVisible}
            record={viewRecord}
            onClose={this.handleViewModalClose}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ nlpTextExtractionLog = {}, loading = {} }) {
  const { dataSource, pagination } = nlpTextExtractionLog;
  return {
    dataSource,
    pagination,
    isSiteFlag: !isTenantRoleLevel(),
    initLoading: loading.effects['nlpTextExtractionLog/init'],
    queryLoading: loading.effects['nlpTextExtractionLog/query'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    init: (payload) =>
      dispatch({
        type: 'nlpTextExtractionLog/init',
        payload,
      }),
    query: (payload) =>
      dispatch({
        type: 'nlpTextExtractionLog/query',
        payload,
      }),
  };
}
