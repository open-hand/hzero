/**
 * TableChangeLog - 表结构变更
 * @date: 2019-7-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import { Header, Content } from 'components/Page';
import { filterNullValueObject } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 表结构变更
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} tableChangeLog - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ tableChangeLog, loading }) => ({
  tableChangeLog,
  loading:
    loading.effects['tableChangeLog/fetchLogList'] || loading.effects['tableChangeLog/handleError'],
}))
@formatterCollections({
  code: [
    'hdtt.tableChangeLog',
    'hdtt.exceptionMonitoring',
    'hdtt.producerConfig',
    'hdtt.exception',
  ],
})
export default class TableChangeLog extends Component {
  form;

  state = {
    isHandling: false,
  };

  componentDidMount() {
    const {
      tableChangeLog: { pagination = {} },
      location: { state: { _back } = {} },
      dispatch,
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    dispatch({ type: 'tableChangeLog/fetchInitStatus' });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'tableChangeLog/fetchLogList',
      payload: {
        page: fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 查看配置
   * @param {number} producerConfigId - 生产配置Id
   */
  @Bind()
  handleGoDetail(producerConfigId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hdtt/producer-config/detail/${producerConfigId}`,
      })
    );
  }

  /**
   * 处理错误
   * @param record
   */
  @Bind()
  handleError(record) {
    this.setState({ isHandling: true });
    const { dispatch } = this.props;
    dispatch({
      type: 'tableChangeLog/handleError', // 处理错误
      payload: { ...record },
    }).then(res => {
      this.setState({ isHandling: false });
      if (res) {
        notification.success();
        this.handleSearch();
      }
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

  render() {
    const {
      tableChangeLog: { list, pagination, statusTypes },
      loading,
    } = this.props;
    const { isHandling } = this.state;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      statusTypes,
    };
    const listProps = {
      pagination,
      loading,
      dataSource: list,
      onView: this.handleGoDetail,
      onHandle: this.handleError,
      onChange: this.handleSearch,
      isHandling,
    };

    return (
      <>
        <Header title={intl.get(`hdtt.tableChangeLog.view.message.title`).d('表结构变更日志')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
