/**
 * PurchaseDetail - 购买详单
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

import { Header, Content } from 'components/Page';
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

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
@connect(({ purchaseDetail, loading }) => ({
  purchaseDetail,
  loading: loading.effects['purchaseDetail/queryList'],
}))
@formatterCollections({ code: ['hchg.purchaseDetail', 'hchg.serviceCharge'] })
export default class PurchaseDetail extends PureComponent {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      purchaseDetail: { list = {} },
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
      type: 'purchaseDetail/queryIdpValue',
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
    dispatch({
      type: 'purchaseDetail/queryList',
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
   * 跳转详情页
   * @param {number} chargeOrderId - 订单id
   */
  @Bind()
  redirectToDetail(chargeOrderId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/purchase-detail/detail/${chargeOrderId}`,
      })
    );
  }

  render() {
    const { purchaseDetail = {}, loading } = this.props;
    const { list = {}, statusList = [] } = purchaseDetail;
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
      onRedirect: this.redirectToDetail,
    };
    return (
      <>
        <Header title={intl.get('hchg.purchaseDetail.view.title.purchaseDetail').d('购买详单')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
