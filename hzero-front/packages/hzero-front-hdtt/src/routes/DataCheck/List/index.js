/**
 * DataCheck - 数据核对
 * @date: 2019-7-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import querystring from 'querystring';
import { Button } from 'hzero-ui';
import { Header, Content } from 'components/Page';
import { filterNullValueObject } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import FilterForm from './FilterForm';
import ListTable from './ListTable';
import LanuchModal from './LaunchModal';

/**
 * 数据核对
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} dataCheck - 数据源
 * @reactProps {Boolean} loading - 数据加载是否完成
 * @reactProps {Boolean} sourceListLoading - 来源表数据加载是否完成
 * @reactProps {Boolean} targetListLoading - 目标表数据加载是否完成
 * @reactProps {Boolean} launchLoading - 发起核对加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ dataCheck, loading }) => ({
  dataCheck,
  loading: loading.effects['dataCheck/fetchCheckList'],
  sourceListLoading: loading.effects['dataCheck/fetchProducerList'],
  targetListLoading: loading.effects['dataCheck/fetchConsumerList'],
  launchLoading: loading.effects['dataCheck/confirmLaunch'],
}))
@formatterCollections({ code: ['hdtt.dataCheck'] })
export default class DataCheck extends PureComponent {
  form;

  state = {
    isShowCheckModal: false,
  };

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      dataCheck: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.props.dispatch({ type: 'dataCheck/fetchSelect' });
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
      type: 'dataCheck/fetchCheckList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 查询来源表数据
   * @param {object} fields - 分页参数
   */
  @Bind()
  handleSearchSourceList(payload) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataCheck/fetchProducerList',
      payload,
    });
  }

  /**
   * 查询目标表数据
   * @param {object} fields - 分页参数
   */
  @Bind()
  handleSearchTargetList(payload) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataCheck/fetchConsumerList',
      payload,
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
   * 查看详情
   * @param {object} record - 表格当前行
   */
  @Bind()
  handleViewDetail(record) {
    const { dataChkBatchId, dataChkBatchLineId } = record;
    const params = { dataChkBatchId, dataChkBatchLineId };
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hdtt/data-check/detail`,
        search: querystring.stringify(params),
      })
    );
  }

  /**
   * 打开发起核对模态框
   */
  @Bind()
  handleOpenCheckModal() {
    this.setState({
      isShowCheckModal: true,
    });
  }

  /**
   * 关闭发起核对模态框
   */
  @Bind()
  handleCloseCheckModal() {
    this.setState({
      isShowCheckModal: false,
    });
  }

  /**
   * 确认发起核对
   * @param {array} rows - 选中行
   * @param {stirng} type - 核对层级
   */
  @Bind()
  handleLaunch(rows, type) {
    const { dispatch } = this.props;
    let nextRows = [];
    if (type === 'SOURCE_TABLE') {
      nextRows = rows.map(item => {
        const nextItem = { ...item };
        const { tableName, serviceName, ...rest } = nextItem;
        return {
          ...rest,
          sourceService: serviceName,
          sourceTable: tableName,
        };
      });
    }
    if (type === 'TARGET_TABLE') {
      nextRows = rows.map(item => {
        const nextItem = { ...item };
        const { consumerTable, consumerService, consumerDs, consumerDb, ...rest } = nextItem;
        return {
          ...rest,
          targetTable: consumerTable,
          targetService: consumerService,
          targetDs: consumerDs,
          targetDb: consumerDb,
        };
      });
    }
    dispatch({
      type: 'dataCheck/confirmLaunch',
      payload: {
        chkLevel: type,
        dataChkBatchLineList: nextRows,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleCloseCheckModal();
        this.handleSearch();
      }
    });
  }

  render() {
    const {
      dataCheck: {
        list = [],
        pagination = {},
        levelTypes = [],
        statusTypes = [],
        sourceList = [],
        sourcePagination = {},
        targetList = [],
        targetPagination = {},
      },
      loading,
      sourceListLoading,
      targetListLoading,
      launchLoading = false,
    } = this.props;
    let nextStatusTypes = [];
    if (!isEmpty(statusTypes)) {
      nextStatusTypes = statusTypes.filter(
        item => item.value === 'R' || item.value === 'S' || item.value === 'E' || item.value === 'P'
      );
    }
    const { isShowCheckModal } = this.state;
    const filterProps = {
      levelTypes,
      statusTypes: nextStatusTypes,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      dataSource: list,
      pagination,
      loading,
      onChange: this.handleSearch,
      onView: this.handleViewDetail,
    };
    const lanuchModalProps = {
      isShowModal: isShowCheckModal,
      levelTypes,
      sourceList,
      sourcePagination,
      sourceLoading: sourceListLoading,
      targetList,
      targetPagination,
      targetLoading: targetListLoading,
      confirmLoading: launchLoading,
      onCancel: this.handleCloseCheckModal,
      onSearchSourceList: this.handleSearchSourceList,
      onSearchTargetList: this.handleSearchTargetList,
      onLaunch: this.handleLaunch,
    };
    return (
      <>
        <Header title={intl.get(`hdtt.dataCheck.view.message.title`).d('数据核对')}>
          <Button icon="plus" type="primary" onClick={this.handleOpenCheckModal}>
            {intl.get(`hdtt.dataCheck.view.message.title.launch`).d('发起核对')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <LanuchModal {...lanuchModalProps} />
        </Content>
      </>
    );
  }
}
