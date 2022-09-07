/**
 * ExceptionMonitoring - 生产消费异常监控
 * @date: 2019-5-6
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
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

const promptCode = 'hdtt.exception';
/**
 * 生产消费异常监控
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} exceptionMonitoring - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ exceptionMonitoring, loading }) => ({
  exceptionMonitoring,
  loading:
    loading.effects['exceptionMonitoring/fetchExceptionList'] ||
    loading.effects['exceptionMonitoring/handleError'],
}))
@formatterCollections({ code: ['hdtt.exception', 'entity.tenant'] })
export default class ExceptionMonitoring extends Component {
  form;

  state = {
    isHandling: false,
  };

  componentDidMount() {
    const {
      exceptionMonitoring: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.props.dispatch({ type: 'exceptionMonitoring/fetchSelect' });
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
    fieldValues.errorTimeFrom =
      fieldValues.errorTimeFrom && fieldValues.errorTimeFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.errorTimeTo =
      fieldValues.errorTimeTo && fieldValues.errorTimeTo.format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'exceptionMonitoring/fetchExceptionList',
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
    const { creationDate, createdBy, lastUpdateDate, lastUpdatedBy, ...others } = record;
    dispatch({
      type: 'exceptionMonitoring/handleError', // 处理错误
      payload: { ...others },
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
      exceptionMonitoring: { exceptionList, pagination, errorTypes, eventTypes },
      loading,
    } = this.props;
    const { isHandling } = this.state;
    const isTenant = isTenantRoleLevel();
    const filterProps = {
      isTenant,
      errorTypes,
      eventTypes,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      isTenant,
      pagination,
      loading,
      dataSource: exceptionList,
      onView: this.handleGoDetail,
      onHandle: this.handleError,
      onChange: this.handleSearch,
      isHandling,
    };

    return (
      <>
        <Header title={intl.get(`${promptCode}.view.message.title`).d('生产消费异常监控')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
