/**
 * Detail - 详情
 * @date: 2019-7-29
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Card } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import querystring from 'querystring';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';
import DetailForm from './DetailForm';
import DetailTable from './DetailTable';

/**
 * 发起核对弹窗
 * @extends {Component} - React.Component
 * @reactProps {Boolean} infoLoading - 头信息加载完成标记
 * @reactProps {Boolean} loading - 列表数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} isHandling - 是否在处理中
 * @reactProps {Function} onView - 查看配置
 * @reactProps {Function} onHandle - 处理
 */
@connect(({ dataCheck, loading }) => ({
  dataCheck,
  loading: loading.effects['dataCheck/fetchDetailList'],
  infoLoading: loading.effects['dataCheck/fetchDetailInfo'],
}))
@formatterCollections({ code: ['hdtt.dataCheck'] })
export default class Detail extends Component {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      dataCheck: { detailPagination = {} },
    } = this.props;
    this.handleSearchInfo();
    this.handleSearch(detailPagination);
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataCheck/updateState',
      payload: {
        detailInfo: {}, // 详情页表单
        detailList: [], // 详情页表格
        detailPagination: {}, // 详情页分页
      },
    });
  }

  /**
   * 数据核对头详情查询
   */
  @Bind()
  handleSearchInfo() {
    const {
      dispatch,
      location: { search },
    } = this.props;
    const params = querystring.parse(search.substring(1));
    const { dataChkBatchLineId } = params;
    dispatch({
      type: 'dataCheck/fetchDetailInfo',
      payload: dataChkBatchLineId,
    });
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const {
      dispatch,
      location: { search },
    } = this.props;
    const params = querystring.parse(search.substring(1));
    dispatch({
      type: 'dataCheck/fetchDetailList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...params,
      },
    });
  }

  render() {
    const {
      loading,
      infoLoading,
      dataCheck: { detailInfo = {}, detailList = [], detailPagination = {} },
    } = this.props;
    const formProps = { dataSource: detailInfo };
    const listProps = {
      loading,
      dataSource: detailList,
      pagination: detailPagination,
      onChange: this.handleSearch,
    };
    return (
      <>
        <Header
          title={intl.get('hdtt.dataCheck.view.message.title.detail').d('数据核对详情')}
          backPath="/hdtt/data-check/list"
        />
        <Content>
          <Card
            key="data-group-head"
            bordered={false}
            title={<h3>{intl.get('hdtt.dataCheck.view.message.title.dataInfo').d('数据信息')}</h3>}
            className={DETAIL_CARD_CLASSNAME}
            loading={infoLoading}
          >
            <DetailForm {...formProps} />
          </Card>
          <Card
            key="config-detail"
            bordered={false}
            title={
              <h3>{intl.get('hdtt.dataCheck.view.message.title.checkDetail').d('核对详情')}</h3>
            }
            className={DETAIL_CARD_TABLE_CLASSNAME}
          >
            <DetailTable {...listProps} />
          </Card>
        </Content>
      </>
    );
  }
}
