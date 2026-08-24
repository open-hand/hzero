/*
 * index - Zuul限流配置
 * @date: 2018/09/10 17:31:09
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Button, Table } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import PropTypes from 'prop-types';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { tableScrollWidth, getRefFormData } from 'utils/utils';
import { dateTimeRender, enableRender, TagRender } from 'utils/renderer';

import ListFilter from './ListFilter';
import ListForm from './ListForm';

/**
 * Zuul限流配置
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} zuulRateLimit - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ loading, hadmZuulRateLimit }) => ({
  loading: loading.effects['hadmZuulRateLimit/fetchList'],
  deletingHeader: loading.effects['hadmZuulRateLimit/deleteHeaders'],
  saving: loading.effects['hadmZuulRateLimit/addRateLimit'],
  hadmZuulRateLimit,
}))
@formatterCollections({ code: ['hadm.zuulRateLimit'] })
export default class ZuulRateLimit extends Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
  }

  zuulForm;

  static propTypes = {
    dispatch: PropTypes.func,
  };

  static defaultProps = {
    dispatch: (e) => e,
  };

  componentDidMount() {
    const {
      location: { state: { _back } = {} },
      dispatch,
    } = this.props;
    dispatch({
      type: 'hadmZuulRateLimit/init',
    });
    if (_back === -1) {
      this.handleSearchCache();
    } else {
      this.handleSearch();
    }
  }

  /**
   * 查询限流设置列表
   * @param {obj} page 查询字段
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.filterFormRef.current)
      ? {}
      : getRefFormData(this.filterFormRef);
    dispatch({
      type: 'hadmZuulRateLimit/fetchList',
      payload: {
        page,
        ...filterValues,
      },
    });
  }

  /**
   * 从缓存中查询限流列表
   */
  @Bind()
  handleSearchCache() {
    const {
      hadmZuulRateLimit: { pagination = {} },
    } = this.props;
    this.handleSearch(pagination);
  }

  /**
   * 新建限流设置
   */
  @Bind()
  handleCreateHeader() {
    this.handleModalVisible(true);
  }

  /**
   * 隐藏模态框
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.handleModalVisible(false);
    }
  }

  /**
   * 改变当前模态框显示状态
   * @param {boolean} flag
   */
  @Bind()
  handleModalVisible(flag) {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmZuulRateLimit/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
  }

  /**
   * 新增限流规则头
   * @param {Object} fields
   */
  @Bind()
  handleAdd(fields) {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmZuulRateLimit/addRateLimit',
      payload: fields,
    }).then((res) => {
      if (!isEmpty(res)) {
        this.hideModal();
        this.handleSearchCache();
        notification.success();
      }
    });
  }

  // /**
  //  * 删除
  //  */
  // @Bind()
  // handleDelete() {
  //   const that = this;
  //   const {
  //     zuulRateLimit: { selectedRowKeys=[], list },
  //     dispatch,
  //   } = this.props;
  //   const zuulRateLimitDtoList = [];
  //   list.forEach(item => {
  //     if (selectedRowKeys.indexOf(item.rateLimitId) >= 0) {
  //       zuulRateLimitDtoList.push({
  //         rateLimitId: item.rateLimitId,
  //         serviceName: item.serviceName,
  //       });
  //     }
  //   });
  //   dispatch({
  //     type: 'hadmZuulRateLimit/refresh',
  //     payload: zuulRateLimitDtoList,
  //   }).then(res => {
  //     if (res) {
  //       notification.success();
  //       dispatch({
  //         type: 'hadmZuulRateLimit/updateState',
  //         payload: {
  //           selectedRowKeys: [],
  //         },
  //       });
  //       that.handleSearchCache();
  //     }
  //   });
  // }

  render() {
    const {
      hadmZuulRateLimit: { list, modalVisible, pagination, limitTypes, refreshStatus },
      loading,
      saving,
      history,
      location: { search },
      match,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const filterProps = {
      loading,
      limitTypes,
      refreshStatus,
      onFilterChange: this.handleSearch,
      // onRef: node => {
      //   this.filterForm = node.props.form;
      // },
      wrappedComponentRef: this.filterFormRef,
    };
    const columns = [
      {
        title: intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitKey`).d('代码'),
        dataIndex: 'rateLimitKey',
      },
      {
        title: intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitType`).d('限流方式'),
        dataIndex: 'rateLimitTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hzero.common.explain').d('说明'),
        dataIndex: 'remark',
        width: 150,
      },
      {
        title: intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.refreshMessage`).d('刷新消息'),
        dataIndex: 'refreshMessage',
        width: 100,
      },
      {
        title: intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.refreshTime`).d('刷新时间'),
        dataIndex: 'refreshTime',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.refreshStatus`).d('刷新状态'),
        dataIndex: 'refreshStatus',
        width: 150,
        render: (val) => {
          const statusList = [
            {
              status: 1,
              color: 'green',
              text: intl.get('hadm.zuulRateLimit.model.zuulRateLimit.refreshSuccess').d('刷新成功'),
            },
            {
              status: 0,
              color: 'red',
              text: intl.get('hadm.zuulRateLimit.model.zuulRateLimit.refreshFailed').d('刷新失败'),
            },
            {
              status: 'default',
              text: intl.get('hadm.zuulRateLimit.model.zuulRateLimit.noRefresh').d('未刷新'),
            },
          ];
          return TagRender(val, statusList);
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 120,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 85,
        fixed: 'right',
        render: (val, record) => (
          <a
            onClick={() => {
              const url =
                match.path.indexOf('/private') === 0
                  ? `/private/hadm/rate-limit/detail/${record.rateLimitId}?access_token=${accessToken}`
                  : `/hadm/rate-limit/detail/${record.rateLimitId}`;
              history.push(url);
            }}
          >
            {intl.get('hzero.common.button.edit').d('编辑')}
          </a>
        ),
      },
    ];
    return (
      <>
        <Header
          title={intl.get(`hadm.zuulRateLimit.view.message.title.zuulRateLimit`).d('限流规则')}
        >
          <Button icon="plus" type="primary" onClick={this.handleCreateHeader}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <ListFilter {...filterProps} />
          <Table
            bordered
            loading={loading}
            rowKey="rateLimitId"
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            dataSource={list}
            pagination={pagination}
            onChange={this.handleSearch}
          />
        </Content>
        <ListForm
          anchor="right"
          title={intl.get(`hadm.zuulRateLimit.view.message.title.listForm`).d('新增限流配置')}
          onRef={(node) => {
            this.zuulForm = node;
          }}
          onHandleAdd={this.handleAdd}
          confirmLoading={saving}
          visible={modalVisible}
          onCancel={this.hideModal}
          limitTypes={limitTypes}
        />
      </>
    );
  }
}
