/**
 * 地区定义
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-10-31
 * @copyright HAND ® 2019
 */

import React from 'react';
import { connect } from 'dva';
import { Tabs } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import LineData from './components/LineData';
import LazyTree from './components/LazyTree';

const TABENUM = {
  lazyTree: 'lazy-tree',
  lineData: 'line-data',
};

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hpfm.region', 'entity.region'] })
export default class Region extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      curTab: TABENUM.lazyTree,
    };
  }

  @Bind()
  handleTabChange(nextActiveTabKey) {
    this.setState({
      curTab: nextActiveTabKey,
    });
  }

  // wrap with countryId

  @Bind()
  regionCreate(payload) {
    const {
      regionCreate,
      match: {
        params: { id: countryId },
      },
    } = this.props;
    return regionCreate({
      ...payload,
      body: payload.body.map((record) => ({ ...record, countryId })),
      countryId,
    });
  }

  @Bind()
  regionUpdate(payload) {
    const {
      regionUpdate,
      match: {
        params: { id },
      },
    } = this.props;
    return regionUpdate({
      ...payload,
      countryId: id,
    });
  }

  @Bind()
  regionEnable(payload) {
    const { regionEnable } = this.props;
    return regionEnable(payload);
  }

  @Bind()
  regionDisable(payload) {
    const { regionDisable } = this.props;
    return regionDisable(payload);
  }

  @Bind()
  regionQueryLine(payload) {
    const {
      regionQueryLine,
      match: {
        params: { id },
      },
    } = this.props;
    return regionQueryLine({
      ...payload,
      countryId: id,
    });
  }

  @Bind()
  regionQueryLazyTree(payload) {
    const {
      regionQueryLazyTree,
      match: {
        params: { id },
      },
    } = this.props;
    return regionQueryLazyTree({
      ...payload,
      countryId: id,
    });
  }

  @Bind()
  regionQueryDetail(payload) {
    const { regionQueryDetail } = this.props;
    return regionQueryDetail(payload);
  }

  // wrap with countryId

  render() {
    const {
      match,
      location: { search },
      treeDataSource,
      expandKeys,
      loadingExpandKeys,
      lineDataSource,
      linePagination,
      regionCreateLoading,
      regionUpdateLoading,
      regionEnableLoading,
      regionDisableLoading,
      regionQueryLineLoading,
      regionQueryLazyTreeLoading,
      regionQueryDetailLoading,
      updateModelState,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const {
      regionCreate,
      regionUpdate,
      regionEnable,
      regionDisable,
      regionQueryLine,
      regionQueryLazyTree,
      regionQueryDetail,
    } = this;
    const { curTab } = this.state;
    return (
      <>
        <Header
          title={intl.get('hpfm.region.view.message.title').d('地区定义')}
          backPath={
            match.path.indexOf('/private') === 0
              ? `/private/hpfm/mdm/country/list?access_token=${accessToken}`
              : '/hpfm/mdm/country/list'
          }
        />
        <Content
          description={
            <>
              <span style={{ marginRight: '8px' }}>
                {intl.get('hpfm.region.model.region.countryCode').d('国家代码')}：
                {match.params.code}
              </span>
              <span>
                {intl.get('hpfm.region.model.region.countryName').d('国家名称')}：
                {match.params.name}
              </span>
            </>
          }
        >
          <Tabs animated={false} activeKey={curTab} onChange={this.handleTabChange}>
            <Tabs.TabPane
              key={TABENUM.lazyTree}
              tab={intl.get('hpfm.region.view.title.lazyTree').d('树形结构')}
            >
              <LazyTree
                loadData={regionQueryLazyTree}
                queryDetail={regionQueryDetail}
                dataSource={treeDataSource}
                expandKeys={expandKeys}
                loadingExpandKeys={loadingExpandKeys}
                match={match}
                updateRecord={regionUpdate}
                enableRecord={regionEnable}
                disableRecord={regionDisable}
                createRecord={regionCreate}
                queryLoading={regionQueryLazyTreeLoading}
                updateLoading={regionUpdateLoading}
                enableLoading={regionEnableLoading}
                disableLoading={regionDisableLoading}
                createLoading={regionCreateLoading}
                queryDetailLoading={regionQueryDetailLoading}
                updateModelState={updateModelState}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              key={TABENUM.lineData}
              tab={intl.get('hpfm.region.view.title.lienData').d('分页结构')}
            >
              <LineData
                match={match}
                dataSource={lineDataSource}
                pagination={linePagination}
                query={regionQueryLine}
                queryDetail={regionQueryDetail}
                updateRecord={regionUpdate}
                createRecord={regionCreate}
                enableRecord={regionEnable}
                disableRecord={regionDisable}
                queryLoading={regionQueryLineLoading}
                queryDetailLoading={regionQueryDetailLoading}
                updateLoading={regionUpdateLoading}
                createLoading={regionCreateLoading}
                enableLoading={regionEnableLoading}
                disableLoading={regionDisableLoading}
              />
            </Tabs.TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}

function mapStateToProps({ loading, region }) {
  const { lineDataSource, linePagination, treeDataSource, expandKeys, loadingExpandKeys } = region;
  return {
    regionCreateLoading: loading.effects['region/regionCreate'],
    regionUpdateLoading: loading.effects['region/regionUpdate'],
    regionEnableLoading: loading.effects['region/regionEnable'],
    regionDisableLoading: loading.effects['region/regionDisable'],
    regionQueryLineLoading: loading.effects['region/regionQueryLine'],
    // lazyTreeLoading 由组件的state 存储, 应该是可以同时更新多个子组织的
    regionQueryLazyTreeLoading: loading.effects['region/regionQueryLazyTree'],
    regionQueryDetailLoading: loading.effects['region/regionQueryDetail'],
    treeDataSource,
    expandKeys,
    loadingExpandKeys,
    lineDataSource,
    linePagination,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    regionQueryLine(payload) {
      return dispatch({
        type: 'region/regionQueryLine',
        payload,
      });
    },
    regionQueryLazyTree(payload) {
      return dispatch({
        type: 'region/regionQueryLazyTree',
        payload,
      });
    },
    regionCreate(payload) {
      return dispatch({
        type: 'region/regionCreate',
        payload,
      });
    },
    regionUpdate(payload) {
      return dispatch({
        type: 'region/regionUpdate',
        payload,
      });
    },
    regionEnable(payload) {
      return dispatch({
        type: 'region/regionEnable',
        payload,
      });
    },
    regionDisable(payload) {
      return dispatch({
        type: 'region/regionDisable',
        payload,
      });
    },
    regionQueryDetail(payload) {
      return dispatch({
        type: 'region/regionQueryDetail',
        payload,
      });
    },
    updateModelState(payload) {
      return dispatch({
        type: 'region/updateState',
        payload,
      });
    },
  };
}
