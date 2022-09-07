/**
 * Detail - 日志追溯详情
 * @date: 2020/2/26
 * @author: CJ <jing.chen04@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import querystring from 'querystring';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';

import DetailForm from './DetailForm';

@connect(({ purchaseDetail, loading }) => ({
  purchaseDetail,
  loading: loading.effects['purchaseDetail/queryDetail'],
}))
@formatterCollections({ code: ['hadm.traceLog'] })
export default class Detail extends Component {
  render() {
    const {
      location: { state = {} },
    } = this.props;
    const detailInfo = querystring.parse(state) || {};
    return (
      <>
        <Header
          title={intl.get('hadm.traceLog.view.title.logDetail').d('日志追溯详情')}
          backPath="/hadm/trace-log/list"
        />
        <Content>
          <DetailForm dataSource={detailInfo} />
        </Content>
      </>
    );
  }
}
