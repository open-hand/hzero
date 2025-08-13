/**
 * reportDataSet - 报表平台/数据集
 * @date: 2018-11-19
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';

import ListTable from './ListTable';
import SearchForm from './SearchForm';
import AssignTable from './AssignTable';

/**
 * 数据集
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} approveAuth - 数据源
 * @reactProps {!Object} fetchApproveLoading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ reportDataSet, loading }) => ({
  reportDataSet,
  tenantRoleLevel: isTenantRoleLevel(),
  fetchDataSetListLoading: loading.effects['reportDataSet/fetchDataSetList'],
  assignLoading: loading.effects['reportDataSet/fetchAssignList'],
}))
@formatterCollections({
  code: ['hrpt.reportDataSet', 'entity.tenant', 'hrpt.reportDefinition', 'hrpt.common'],
})
export default class List extends Component {
  form;

  /**
   * state初始化
   */
  state = {
    assignTableVisible: false,
    currentDataSetRecord: {},
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      reportDataSet: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'reportDataSet/fetchDataSetList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 新增，跳转到明细页面
   */
  @Bind()
  handleAddDataSet() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hrpt/data-set/create`,
      })
    );
  }

  /**
   * 数据列表，行删除
   * @param {obejct} record - 操作对象
   */
  @Bind()
  handleDeleteContent(record) {
    const {
      dispatch,
      reportDataSet: { pagination },
    } = this.props;
    dispatch({
      type: 'reportDataSet/deleteDataSet',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 数据列表，行编辑
   *@param {obejct} record - 操作对象
   */
  @Bind()
  handleEditContent(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hrpt/data-set/detail/${record.datasetId}`,
      })
    );
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
   * 显示数据集管理报表
   * @param {object} record - 数据集行数据
   */
  @Bind()
  handleShowTable(record) {
    this.setState({ assignTableVisible: true, currentDataSetRecord: record }, () => {
      this.fetchAssignList();
    });
  }

  @Bind()
  hideAssignTable() {
    this.setState({ assignTableVisible: false });
  }

  @Bind()
  fetchAssignList() {
    const { dispatch } = this.props;
    const { currentDataSetRecord = {} } = this.state;
    dispatch({
      type: 'reportDataSet/fetchAssignList',
      payload: { datasetId: currentDataSetRecord.datasetId },
    });
  }

  render() {
    const {
      tenantRoleLevel,
      fetchDataSetListLoading,
      assignLoading = false,
      reportDataSet: { list = [], pagination = {}, assignList = {}, assignPagination = {} } = {},
    } = this.props;
    const { assignTableVisible } = this.state;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      tenantRoleLevel,
      loading: fetchDataSetListLoading,
      dataSource: list,
      onOk: this.hideAssignTable,
      onEdit: this.handleEditContent,
      onDelete: this.handleDeleteContent,
      onChange: this.handleSearch,
      onShowTable: this.handleShowTable,
    };
    const assignTableProps = {
      fetchLoading: assignLoading,
      visible: assignTableVisible,
      dataSource: assignList,
      pagination: assignPagination,
      onSearch: this.fetchAssignList,
      onOk: this.hideAssignTable,
    };
    return (
      <>
        <Header title={intl.get('hrpt.reportDataSet.view.message.title').d('数据集')}>
          <Button icon="plus" type="primary" onClick={this.handleAddDataSet}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
          {assignTableVisible && <AssignTable {...assignTableProps} />}
        </Content>
      </>
    );
  }
}
