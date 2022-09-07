/**
 * Detail - 购买详单
 * @date: 2019/9/1
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Card } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { Header, Content } from 'components/Page';

import DetailForm from './DetailForm';

/**
 * 购买详单
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} purchaseDetail - 数据源
 * @reactProps {boolean} loading - 表单数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ purchaseDetail, loading }) => ({
  purchaseDetail,
  loading: loading.effects['purchaseDetail/queryDetail'],
}))
@formatterCollections({ code: ['hchg.purchaseDetail', 'hchg.serviceCharge'] })
export default class Detail extends Component {
  componentDidMount() {
    this.handleSearch();
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'purchaseDetail/updateState',
      payload: {
        detailInfo: {},
      },
    });
  }

  /**
   * 查询详情
   */
  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'purchaseDetail/queryDetail',
      payload: id,
    });
  }

  render() {
    const {
      purchaseDetail: { detailInfo = {} },
      loading = false,
    } = this.props;
    const formProps = { dataSource: detailInfo };
    return (
      <>
        <Header
          title={intl.get('hchg.purchaseDetail.view.title.orderDetail').d('订单详情')}
          backPath="/hchg/purchase-detail/list"
        />
        <Content>
          <Card
            key="order-detail"
            bordered={false}
            title={<h3>{intl.get('hchg.purchaseDetail.view.title.orderInfo').d('订单信息')}</h3>}
            className={DETAIL_CARD_CLASSNAME}
            loading={loading}
          >
            <DetailForm {...formProps} />
          </Card>
        </Content>
      </>
    );
  }
}
