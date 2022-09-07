/* eslint-disable no-unused-vars */
/**
 * ServiceBill - 服务账单
 * @date: 2019-8-30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import { Button, Modal } from 'hzero-ui';

import { Header, Content } from 'components/Page';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

const isTenant = isTenantRoleLevel();

/**
 * 购买详单
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} purchaseDetail - 数据源
 * @reactProps {Boolean} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ serviceBill, loading }) => ({
  serviceBill,
  loading: loading.effects['serviceBill/queryList'],
}))
@formatterCollections({ code: ['hchg.serviceBill', 'hchg.serviceCharge'] })
export default class ServiceBill extends PureComponent {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      serviceBill: { list = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const { pagination = {} } = list;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.handleQueryIdpValue();
  }

  /**
   * 查询值集
   */
  @Bind()
  handleQueryIdpValue() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceBill/queryIdpValue',
    });
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
    fieldValues.chargeDateFrom =
      fieldValues.chargeDateFrom && fieldValues.chargeDateFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.chargeDateTo =
      fieldValues.chargeDateTo && fieldValues.chargeDateTo.format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'serviceBill/queryList',
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
   * 跳转至订单详情页
   * @param {number} chargeOrderId - 订单id
   */
  @Bind()
  redirectToOrderDetail(chargeOrderId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/purchase-detail/detail/${chargeOrderId}`,
      })
    );
  }

  render() {
    const { serviceBill = {}, loading } = this.props;
    const { list = {}, statusList = [] } = serviceBill;
    const filterProps = {
      statusList,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      loading,
      isTenant,
      dataSource: list.dataSource,
      pagination: list.pagination,
      onChange: this.handleSearch,
      onRedirect: this.redirectToOrderDetail,
    };
    return (
      <>
        <Header title={intl.get('hchg.serviceBill.view.title.serviceBill').d('服务账单')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
