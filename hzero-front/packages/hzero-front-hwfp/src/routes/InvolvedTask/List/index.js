/**
 * InvolvedTask - 参与流程
 * @date: 2018-8-31
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import moment from 'moment';

import { Content, Header } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';
import { DATETIME_MAX, DATETIME_MIN } from 'utils/constants';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 参会流程组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} involvedTask - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hwfp.involvedTask', 'hwfp.common'] })
@connect(({ involvedTask, loading }) => ({
  involvedTask,
  fetchListLoading: loading.effects['involvedTask/fetchTaskList'],
  tenantId: getCurrentOrganizationId(),
}))
export default class InvolvedTask extends Component {
  form;

  componentDidMount() {
    const {
      involvedTask: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch({ involved: true, page });
    this.props.dispatch({ type: 'involvedTask/queryProcessStatus' });
  }

  /**
   * 传递表单对象
   * @param {object} ref - FilterForm对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    const {
      startedAfter,
      startedBefore,
      processInstanceId,
      processDefinitionNameLike,
      startUserName,
      ...others
    } = filterValues;
    dispatch({
      type: 'involvedTask/fetchTaskList',
      payload: {
        tenantId,
        startedAfter: startedAfter ? moment(startedAfter).format(DATETIME_MIN) : null,
        startedBefore: startedBefore ? moment(startedBefore).format(DATETIME_MAX) : null,
        startUserName,
        processDefinitionNameLike,
        processInstanceId,
        page: isEmpty(fields) ? {} : fields,
        involved: true,
        ...others,
      },
    });
  }

  render() {
    const {
      fetchListLoading,
      tenantId,
      involvedTask: { list, pagination, processStatus = [] },
    } = this.props;
    const filterProps = {
      tenantId,
      processStatus,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      processStatus,
      dataSource: list,
      loading: fetchListLoading,
      onChange: this.handleSearch,
    };
    return (
      <>
        <Header title={intl.get('hwfp.involvedTask.view.message.title').d('我参与的流程')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
