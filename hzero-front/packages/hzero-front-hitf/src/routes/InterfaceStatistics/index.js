/**
 * index - 接口平台-应用配置
 * @date: 2018-7-26
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import Search from './Search';
import List from './List';

@connect(({ loading, interfaceStatistics }) => ({
  queryListLoading: loading.effects['interfaceStatistics/queryList'],
  interfaceStatistics,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hitf.interfaceStatistics', 'hitf.application'] })
export default class InterfaceStatistics extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      pagination: {},
    };
  }

  componentDidMount() {
    this.fetchList();
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  @Bind()
  fetchList(params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'interfaceStatistics/queryList', params }).then((res = {}) => {
      const { dataSource, pagination } = res;
      this.setState({
        dataSource,
        pagination,
      });
    });
  }

  @Bind()
  onTableChange(pagination) {
    this.fetchList({ page: pagination });
  }

  render() {
    const { queryListLoading = {} } = this.props;
    const { dataSource = [], pagination } = this.state;
    const searchProps = {
      fetchList: this.fetchList,
    };
    const listProps = {
      loading: queryListLoading,
      onChange: this.onTableChange,
      dataSource,
      pagination,
    };

    return (
      <>
        <Header
          title={intl.get('hitf.interfaceStatistics.view.message.title.header').d('健康状况监控')}
        />
        <Content>
          <Search
            ref={node => {
              this.search = node;
            }}
            {...searchProps}
          />
          <List {...listProps} />
        </Content>
      </>
    );
  }
}
