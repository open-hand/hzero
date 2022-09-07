/**
 * Orgination - 组织架构维护
 * @date: 2018-6-19
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Tabs } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import cacheComponent from 'components/CacheComponent';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import { openTab } from 'utils/menuTab';
import { HZERO_PLATFORM } from 'utils/config';

import LazyTree from './LazyTree';
import LineData from './LineData';
import styles from './index.less';

const TABENUM = {
  lazyTree: 'lazy-tree',
  lineData: 'line-data',
};

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hpfm.organization', 'entity.organization', 'hpfm.common'] })
@cacheComponent({ cacheKey: '/hpfm/hr/org/company/index' })
export default class Organization extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // tab
      curTab: TABENUM.lazyTree,
    };
  }

  componentDidMount() {
    const { fetchOrgInfo } = this.props;
    fetchOrgInfo();
  }

  @Bind()
  handleTabChange(nextActiveTabKey) {
    this.setState({
      curTab: nextActiveTabKey,
    });
  }

  @Bind()
  handleImport() {
    openTab({
      key: `/hpfm/prompt/import-data/HPFM.UNIT_POSITION`,
      title: 'hzero.common.title.departmentPositionImport',
      search: queryString.stringify({
        action: 'hzero.common.title.departmentPositionImport',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  render() {
    const {
      unitsQueryLazyTree,
      treeDataSource,
      expandKeys,
      loadingExpandKeys,
      updateModelState,
      organizationId,
      saveEditData,
      forbidLine,
      enabledLine,
      saveAddData,
      push,
      match,
      fetchOrgInfoLoading,
      saveEditDataLoading,
      saveAddDataLoading,
      forbidLineLoading,
      enabledLineLoading,
      unitType,
      unitsQueryLine,
      lineDataSource,
      linePagination,
      unitsQueryLineLoading,
      groupName,
      dispatch,
    } = this.props;
    const { curTab } = this.state;
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.organization.view.message.title.organization').d('组织架构')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.Import`,
                type: 'button',
                meaning: '组织架构维护-导入',
              },
            ]}
            icon="upload"
            onClick={this.handleImport}
          >
            {intl.get('hzero.common.button.import').d('导入')}
          </ButtonPermission>
        </Header>
        <Content>
          <p className={styles['hpfm-organization-title']}>
            <span />
            {intl
              .get('hpfm.organization.view.message.tips', { name: groupName })
              .d(`当前正在为「${groupName}」集团，分配组织`)}
          </p>
          <Tabs animated={false} activeKey={curTab} onChange={this.handleTabChange}>
            <Tabs.TabPane
              key={TABENUM.lazyTree}
              tab={intl.get('hpfm.organization.view.title.lazyTree').d('树形结构')}
            >
              <LazyTree
                loadData={unitsQueryLazyTree}
                dataSource={treeDataSource}
                expandKeys={expandKeys}
                loadingExpandKeys={loadingExpandKeys}
                updateModelState={updateModelState}
                organizationId={organizationId}
                push={push}
                match={match}
                saveEditData={saveEditData}
                forbidLine={forbidLine}
                enabledLine={enabledLine}
                saveAddData={saveAddData}
                fetchOrgInfoLoading={fetchOrgInfoLoading}
                saveEditDataLoading={saveEditDataLoading}
                saveAddDataLoading={saveAddDataLoading}
                forbidLineLoading={forbidLineLoading}
                enabledLineLoading={enabledLineLoading}
                unitType={unitType}
                dispatch={dispatch}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              key={TABENUM.lineData}
              tab={intl.get('hpfm.organization.view.title.lienData').d('分页结构')}
            >
              <LineData
                organizationId={organizationId}
                unitType={unitType}
                match={match}
                dataSource={lineDataSource}
                pagination={linePagination}
                unitsQueryLine={unitsQueryLine}
                saveEditData={saveEditData}
                saveAddData={saveAddData}
                forbidLine={forbidLine}
                enabledLine={enabledLine}
                push={push}
                queryLoading={unitsQueryLineLoading}
                saveEditDataLoading={saveEditDataLoading}
                forbidLineLoading={forbidLineLoading}
                enabledLineLoading={enabledLineLoading}
                saveAddDataLoading={saveAddDataLoading}
              />
            </Tabs.TabPane>
          </Tabs>
        </Content>
      </React.Fragment>
    );
  }
}

function mapStateToProps({ loading, organization }) {
  const {
    lineDataSource,
    linePagination,
    treeDataSource,
    expandKeys,
    loadingExpandKeys,
    unitType,
    groupName,
  } = organization;
  return {
    unitType,
    treeDataSource,
    // lazyTreeLoading 由组件的state 存储, 应该是可以同时更新多个子组织的
    // lazyTreeLoading: loading.effects['organization/unitsQueryLazyTree'],
    saveEditDataLoading: loading.effects['organization/saveEditData'],
    saveAddDataLoading: loading.effects['organization/saveAddData'],
    forbidLineLoading: loading.effects['organization/forbidLine'],
    enabledLineLoading: loading.effects['organization/enabledLine'],
    unitsQueryLineLoading: loading.effects['organization/unitsQueryLine'],
    fetchOrgInfoLoading: loading.effects['organization/fetchOrgInfo'],
    expandKeys,
    loadingExpandKeys,
    lineDataSource,
    linePagination,
    organizationId: getCurrentOrganizationId(),
    groupName,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    fetchOrgInfo(payload) {
      return dispatch({
        type: 'organization/fetchOrgInfo',
        payload,
      });
    },
    unitsQueryLazyTree(payload) {
      return dispatch({
        type: 'organization/unitsQueryLazyTree',
        payload,
      });
    },
    saveEditData(payload) {
      return dispatch({
        type: 'organization/saveEditData',
        payload,
      });
    },
    forbidLine(payload) {
      return dispatch({
        type: 'organization/forbidLine',
        payload,
      });
    },
    enabledLine(payload) {
      return dispatch({
        type: 'organization/enabledLine',
        payload,
      });
    },
    saveAddData(payload) {
      return dispatch({
        type: 'organization/saveAddData',
        payload,
      });
    },
    unitsQueryLine(payload) {
      return dispatch({
        type: 'organization/unitsQueryLine',
        payload,
      });
    },
    updateModelState(payload) {
      return dispatch({
        type: 'organization/updateState',
        payload,
      });
    },
    push(loc) {
      dispatch(routerRedux.push(loc));
    },
  };
}
