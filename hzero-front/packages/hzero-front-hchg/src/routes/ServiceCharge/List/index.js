/**
 * ServiceCharge - 服务计费配置
 * @date: 2019-8-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { routerRedux } from 'dva/router';
import { isUndefined, isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

const isTenant = isTenantRoleLevel();

/**
 * 服务计费配置
 * @extends {Component} - Component
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} serviceCharge - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} handleLoading - 发布/取消加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ serviceCharge, loading }) => ({
  serviceCharge,
  loading: loading.effects['serviceCharge/fetchList'],
  handleLoading:
    loading.effects['serviceCharge/publishGroup'] ||
    loading.effects['serviceCharge/cancelGroup'] ||
    loading.effects['serviceCharge/fetchList'],
}))
@formatterCollections({ code: ['hchg.serviceCharge'] })
export default class ServiceCharge extends Component {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      serviceCharge: { list = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const { pagination = {} } = list;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'serviceCharge/fetchList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
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
   * 发布服务组
   * @param {object} record - 表格行数据
   */
  @Bind()
  handlePublish(record) {
    const {
      dispatch,
      serviceCharge: { list = {} },
    } = this.props;
    const { pagination = {} } = list;
    dispatch({
      type: 'serviceCharge/publishGroup',
      payload: record,
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 取消发布服务组
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleCancel(record) {
    const {
      dispatch,
      serviceCharge: { list = {} },
    } = this.props;
    const { pagination = {} } = list;
    dispatch({
      type: 'serviceCharge/cancelGroup',
      payload: record,
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 跳转至新建页面
   */
  @Bind()
  redirectToCreate() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/service-charge/create`,
      })
    );
  }

  /**
   * 跳转至详情页
   * @param {number} chargeGroupId - 计费组ID
   */
  @Bind()
  redirectToDetail(chargeGroupId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/service-charge/detail/${chargeGroupId}`,
      })
    );
  }

  render() {
    const {
      serviceCharge: {
        list: { dataSource = [], pagination = {} },
      },
      loading,
      handleLoading,
    } = this.props;

    const filterProps = {
      isTenant,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      loading,
      isTenant,
      dataSource,
      pagination,
      handleLoading,
      onChange: this.handleSearch,
      onPublish: this.handlePublish,
      onCancel: this.handleCancel,
      onRedirectToEdit: this.redirectToDetail,
    };
    return (
      <>
        <Header title={intl.get('hchg.serviceCharge.view.title.serviceCharge').d('服务计费配置')}>
          <Button icon="plus" type="primary" onClick={this.redirectToCreate}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
