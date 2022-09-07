/**
 * LedgerAccount  成本中心
 * @date: 2019-11-13
 * @author: LC <chao.li03@hand-china.com>
 * @version: 0.1.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component, Fragment } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { Debounce, Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';

import { Header, Content } from 'components/Page';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DEBOUNCE_TIME } from 'utils/constants';
import { getCurrentOrganizationId, filterNullValueObject } from 'utils/utils';

import FilterHeader from './FilterHeader';
import TableHeader from './TableHeader';
import Drawer from './Drawer';

/**
 * LedgerAccount  成本中心
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} periodOrg - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {!boolean} saveLoading - 保存操作是否完成
 * @reactProps {!String} tenantId - 租户ID
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ costCenter, loading }) => ({
  costCenter,
  loading: {
    search: loading.effects['costCenter/searchPeriodHeader'],
    initDataLoading: loading.effects['costCenter/getDetail'],
    createDetailLoad: loading.effects['costCenter/create'],
    editDetailLoading: loading.effects['costCenter/edit'],
  },
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.costCenter'] })
export default class PeriodOrg extends Component {
  state = {
    modalVisible: false,
    editflag: false,
  };

  headerForm;

  /**
   * componentDidMount
   * render后加载页面数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  /**
   * 成本中心查询
   * @param {Object} fields - 查询参数
   * @param {?Object} fields.page - 分页查询参数
   * @param {String} [fields.periodSetName] - 会计期名称
   * @param {String} [fields.periodSetCode] - 会计期名称
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.headerForm)
      ? {}
      : filterNullValueObject(this.headerForm.getFieldsValue());
    dispatch({
      type: 'costCenter/searchPeriodHeader',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 获取FilterForm中form对象
   * @param {object} ref - FilterForm组件
   */
  @Bind()
  handleBindHeaderRef(ref = {}) {
    this.headerForm = (ref.props || {}).form;
  }

  /**
   * 添加 - 会计期定义
   */
  @Bind()
  @Debounce(DEBOUNCE_TIME)
  handleAddPeriodHeader() {
    this.showModal();
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}, editflag) {
    this.handleModalVisible(true);
    const { dispatch } = this.props;
    this.setState({ editflag });
    if (editflag) {
      dispatch({
        type: 'costCenter/getDetail',
        payload: record,
      });
    }
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'costCenter/updateState',
      payload: {
        detail: {},
      },
    });
    this.setState({ editflag: false });
    this.handleModalVisible(false);
  }

  /**
   * 模态窗确认
   */
  @Bind()
  handleConfirm(fieldValues) {
    const { dispatch } = this.props;
    const { editflag } = this.state;
    if (editflag) {
      dispatch({
        type: 'costCenter/edit',
        payload: fieldValues,
      }).then((res) => {
        if (res) {
          this.setState({ editflag: false });
          notification.success();
          this.hideModal();
          this.handleSearch();
        } else {
          this.setState({ editflag: true });
        }
      });
    } else {
      dispatch({
        type: 'costCenter/create',
        payload: fieldValues,
      }).then((res) => {
        this.setState({ editflag: false });
        if (res) {
          notification.success();
          this.hideModal();
          this.handleSearch();
        }
      });
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { costCenter, loading } = this.props;
    const { periodHeader, detail = {} } = costCenter;
    const { modalVisible, editflag } = this.state;
    const filterHeader = {
      onSearch: this.handleSearch,
      onRef: this.handleBindHeaderRef,
    };
    const listHeader = {
      loading: loading.search,
      pagination: periodHeader.pagination,
      dataSource: periodHeader.list,
      onSearch: this.handleSearch,
      onEdit: this.showModal,
    };
    return (
      <Fragment>
        <Header title={intl.get('hpfm.costCenter.view.message.title').d('成本中心定义')}>
          <Button icon="plus" onClick={this.handleAddPeriodHeader} type="primary">
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterHeader {...filterHeader} />
          </div>
          <TableHeader {...listHeader} />
        </Content>
        <Drawer
          title={
            editflag
              ? intl.get('hpfm.costCenter.view.message.modal.edit').d('编辑成本中心')
              : intl.get('hpfm.costCenter.view.message.modal.create').d('新建成本中心')
          }
          editflag={editflag}
          modalVisible={modalVisible}
          onOk={this.handleConfirm}
          onCancel={this.hideModal}
          initData={detail}
          initLoading={loading.initDataLoading}
          loading={loading.createDetailLoad || loading.editDetailLoading}
        />
      </Fragment>
    );
  }
}
