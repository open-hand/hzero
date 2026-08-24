/**
 * 接口字段权限维护 /hiam/sub-account-org/api/:usedId
 * ApiField
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-09
 * @copyright 2019-07-09 © HAND
 */

import React, { Component } from 'react';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

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

@connect(
  mapStateToProps,
  mapDispatchToProps
)
@formatterCollections({ code: ['hiam.subAccount'] })
@cacheComponent({ cacheKey: '/hiam/sub-account-org/api/list' })
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
    const { apiInit } = this.props;
    apiInit();
    this.reload();
  }

  componentWillUnmount() {
    const { updateState } = this.props;
    updateState({
      apiDataSource: [],
      apiPagination: {},
    });
  }

  reload() {
    const { cachePagination } = this.state;
    this.handleSearch(cachePagination);
  }

  handleSearch(pagination = {}) {
    const {
      queryApis,
      match: {
        params: { userId },
      },
    } = this.props;
    this.setState({
      cachePagination: pagination,
    });
    const params = getFieldsValueByWrappedComponentRef(this.searchFormRef);
    queryApis({
      params: {
        ...params,
        ...pagination,
        userId,
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
  handleApiFieldPermissionMaintain(record) {
    const {
      pagePush,
      match: {
        params: { userId },
        path,
      },
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    pagePush({
      pathname:
        path.indexOf('/private') === 0
          ? `/private/hiam/sub-account-org/field/${userId}/${record.id}`
          : `/hiam/sub-account-org/field/${userId}/${record.id}`,
      search: path.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
    });
  }

  render() {
    const {
      dataSource,
      pagination,
      loading,
      requestMethod,
      match: { path },
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    return (
      <>
        <Header
          backPath={
            path.indexOf('/private') === 0
              ? `/private/hiam/sub-account-org?access_token=${accessToken}`
              : '/hiam/sub-account-org'
          }
          title={intl.get('hiam.subAccount.view.title.fieldPermission').d('接口字段权限配置')}
        />
        <Content>
          <SearchForm
            onSearch={this.handleSearchFormSearch}
            requestMethod={requestMethod}
            wrappedComponentRef={this.searchFormRef}
          />
          <DataTable
            path={path}
            dataSource={dataSource}
            pagination={pagination}
            loading={loading}
            onChange={this.handleDataTableChange}
            onApiFieldPermissionMaintain={this.handleApiFieldPermissionMaintain}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ subAccountOrg, loading }) {
  const { apiDataSource, apiPagination, requestMethod } = subAccountOrg;
  return {
    requestMethod,
    dataSource: apiDataSource,
    pagination: apiPagination,
    loading: loading.effects['subAccountOrg/apiInit'] || loading.effects['subAccountOrg/queryApis'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    apiInit(payload) {
      return dispatch({
        type: 'subAccountOrg/apiInit',
        payload,
      });
    },
    queryApis(payload) {
      return dispatch({
        type: 'subAccountOrg/queryApis',
        payload,
      });
    },
    pagePush(payload) {
      dispatch(routerRedux.push(payload));
    },
    updateState(payload) {
      dispatch({
        type: 'subAccountOrg/updateState',
        payload,
      });
    },
  };
}
