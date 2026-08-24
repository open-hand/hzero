/**
 * CaManagement - CA证书管理
 * @date: 2019/9/10
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Header, Content } from 'components/Page';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import List from './List';
import Upload from './Upload.js';

/**
 * CA证书管理
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} caManagement - 数据源
 * @reactProps {Boolean} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ caManagement, loading }) => ({
  caManagement,
  loading: loading.effects['caManagement/queryList'] || loading.effects['caManagement/deleteCa'],
}))
@formatterCollections({ code: ['hpfm.caManagement'] })
export default class CaManagement extends PureComponent {
  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      caManagement: { list = {} },
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
    dispatch({
      type: 'caManagement/queryList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
      },
    });
  }

  /**
   * 删除证书
   * @param {object} record - 选中的证书
   */
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'caManagement/deleteCa',
      payload: record,
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  render() {
    const {
      caManagement: { list = {} },
      loading,
    } = this.props;
    const { dataSource = [], pagination = {} } = list;
    const listProps = {
      dataSource,
      pagination,
      loading,
      onChange: this.handleSearch,
      onDelete: this.handleDelete,
      onUpdate: this.handleUpdate,
    };
    const uploadProps = {
      isCreate: true,
      onReload: this.handleSearch,
    };
    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.caManagement.view.message.title.caManagement').d('CA证书管理')}
        >
          <Upload {...uploadProps} />
        </Header>
        <Content>
          <List {...listProps} />
        </Content>
      </React.Fragment>
    );
  }
}
