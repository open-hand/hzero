/**
 * 接口字段维护 /hiam/role/api/:roleId
 * ApiField
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-09
 * @copyright 2019-07-09 © HAND
 */

import React, { Component } from 'react';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

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
@formatterCollections({ code: ['hiam.roleManagement'] })
@cacheComponent({ cacheKey: '/hiam/role/api/list' })
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
        params: { roleId },
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
        roleId,
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
      location: { search },
      match: {
        params: { roleId },
      },
    } = this.props;
    const { fromSource } = queryString.parse(search.substring(1));
    let routerConfig = {
      pathname: `/hiam/tr-role/field/${roleId}/${record.id}`,
    };
    if (fromSource) {
      routerConfig = {
        pathname: `/hiam/role-tree/field/${roleId}/${record.id}`,
        search: queryString.stringify({ fromSource }),
      };
    }
    pagePush(routerConfig);
  }

  render() {
    const {
      dataSource,
      pagination,
      loading,
      requestMethod,
      location: { search },
      match: { path },
    } = this.props;
    const { fromSource, access_token: accessToken } = queryString.parse(search.substring(1));
    const backPath =
      // eslint-disable-next-line no-nested-ternary
      path.indexOf('/private') === 0
        ? fromSource
          ? `/private/hiam/role-tree?access_token=${accessToken}`
          : `/private/hiam/tr-role?access_token=${accessToken}`
        : fromSource
        ? '/hiam/role-tree'
        : '/hiam/tr-role';
    return (
      <>
        <Header
          backPath={backPath}
          title={intl.get('hiam.roleManagement.view.title.fieldPermission').d('接口字段权限配置')}
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

function mapStateToProps({ trRoleManagement, loading }) {
  const { apiDataSource, apiPagination, requestMethod } = trRoleManagement;
  return {
    requestMethod,
    dataSource: apiDataSource,
    pagination: apiPagination,
    loading:
      loading.effects['trRoleManagement/apiInit'] || loading.effects['trRoleManagement/queryApis'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    apiInit(payload) {
      return dispatch({
        type: 'trRoleManagement/apiInit',
        payload,
      });
    },
    queryApis(payload) {
      return dispatch({
        type: 'trRoleManagement/queryApis',
        payload,
      });
    },
    pagePush(payload) {
      dispatch(routerRedux.push(payload));
    },
    updateState(payload) {
      dispatch({
        type: 'trRoleManagement/updateState',
        payload,
      });
    },
  };
}
