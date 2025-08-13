/**
 * TenantInitHandleConfig - 租户初始化处理配置
 * @date: 2019-6-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Tabs } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { Content, Header } from 'components/Page';
import { filterNullValueObject } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import FilterForm from './FilterForm';
import ListTable from './ListTable';
import ProcessorsImg from './ProcessorsImg';

const { TabPane } = Tabs;
const viewTitle = 'hiam.tenantConfig.view.title';

/**
 * 租户初始化处理配置
 * @extends {Component} - React.Component
 * @reactProps {object} tenantInitConfig - 数据源
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ tenantInitConfig, loading }) => ({
  tenantInitConfig,
  listLoading: loading.effects['tenantInitConfig/fetchConfigList'],
  mapLoading: loading.effects['tenantInitConfig/fetchFormatConfig'],
}))
@formatterCollections({ code: 'hiam.tenantConfig' })
export default class TenantInitConfig extends Component {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    this.handleFetchIdpValue();
    this.handleSearch();
  }

  /**
   * 查询值集
   */
  @Bind()
  handleFetchIdpValue() {
    const { dispatch } = this.props;
    dispatch({
      type: 'tenantInitConfig/queryIdpValue',
    });
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch() {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'tenantInitConfig/fetchConfigList',
      payload: { ...fieldValues },
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 切换选项卡
   * @param {string} activeKey - 当前选中选项卡
   */
  @Bind()
  handleChangeTab(activeKey) {
    if (activeKey === 'create' || activeKey === 'update') {
      this.fetchFormatConfig();
    }
  }

  /**
   * 格式化查询租户初始化处理器配置
   */
  @Bind()
  fetchFormatConfig() {
    const { dispatch } = this.props;
    dispatch({
      type: 'tenantInitConfig/fetchFormatConfig',
    });
  }

  render() {
    const {
      tenantInitConfig: { configList, formatConfig, enumMap },
      listLoading,
      mapLoading = false,
    } = this.props;
    const createData = !isEmpty(formatConfig) && formatConfig.CREATE ? formatConfig.CREATE : [];
    const updateData = !isEmpty(formatConfig) && formatConfig.UPDATE ? formatConfig.UPDATE : [];
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      enumMap,
    };
    const listProps = {
      dataSource: configList,
      loading: listLoading,
    };
    const createImgProps = {
      loading: mapLoading,
      type: 'create',
      dataSource: createData,
    };

    const updateImgProps = {
      loading: mapLoading,
      type: 'update',
      dataSource: updateData,
    };

    return (
      <>
        <Header title={intl.get(`${viewTitle}.tenant.init.config`).d('租户初始化处理配置')} />
        <Content>
          <FilterForm {...filterProps} />
          <Tabs animated={false} forceRender onChange={this.handleChangeTab}>
            <TabPane tab={intl.get(`${viewTitle}.tenant.result.list`).d('结果列表')} key="list">
              <ListTable {...listProps} />
            </TabPane>
            <TabPane tab={intl.get(`${viewTitle}.tenant.create`).d('租户创建')} key="create">
              <ProcessorsImg {...createImgProps} />
            </TabPane>
            <TabPane tab={intl.get(`${viewTitle}.tenant.update`).d('租户更新')} key="update">
              <ProcessorsImg {...updateImgProps} />
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
