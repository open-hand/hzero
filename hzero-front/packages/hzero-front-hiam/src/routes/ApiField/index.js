/**
 * 接口字段维护 /hiam/api-field
 * ApiField
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-09
 * @copyright 2019-07-09 © HAND
 */

import React, { Component } from 'react';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import DataTable from './DataTable';
import SearchForm from './SearchForm';

function getFieldsValueByWrappedComponentRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hiam.apiField'] })
@cacheComponent({ cacheKey: '/hiam/api-field/list' })
export default class ApiField extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // 缓存的 分页
      cachePagination: {},
    };
    this.searchFormRef = React.createRef();
  }

  componentDidMount() {
    const { init } = this.props;
    init();
    this.reload();
  }

  reload() {
    const { cachePagination } = this.state;
    this.handleSearch(cachePagination);
  }

  handleSearch(pagination = {}) {
    const { query } = this.props;
    this.setState({
      cachePagination: pagination,
    });
    const params = getFieldsValueByWrappedComponentRef(this.searchFormRef);
    query({
      params: {
        ...params,
        ...pagination,
      },
    });
  }

  // SearchForm
  @Bind()
  handleSearchFormSearch() {
    this.handleSearch();
  }

  // DataTable
  @Bind()
  handleDataTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  @Bind()
  handleApiFieldMaintain(record) {
    const { pagePush } = this.props;
    pagePush({
      pathname: `/hiam/api-field/${record.id}`,
    });
  }

  render() {
    const {
      dataSource,
      pagination,
      loading,
      requestMethod,
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hiam.apiField.view.message.title.apiField').d('接口字段维护')} />
        <Content>
          <SearchForm
            onSearch={this.handleSearchFormSearch}
            requestMethod={requestMethod}
            wrappedComponentRef={this.searchFormRef}
          />
          <DataTable
            dataSource={dataSource}
            pagination={pagination}
            loading={loading}
            onChange={this.handleDataTableChange}
            onApiFieldMaintain={this.handleApiFieldMaintain}
            path={path}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ hiamApiField, loading }) {
  const { apiDataSource, apiPagination, requestMethod } = hiamApiField;
  return {
    requestMethod,
    dataSource: apiDataSource,
    pagination: apiPagination,
    loading: loading.effects['hiamApiField/init'] || loading.effects['hiamApiField/query'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    init(payload) {
      return dispatch({
        type: 'hiamApiField/init',
        payload,
      });
    },
    query(payload) {
      return dispatch({
        type: 'hiamApiField/query',
        payload,
      });
    },
    pagePush(payload) {
      dispatch(routerRedux.push(payload));
    },
  };
}
