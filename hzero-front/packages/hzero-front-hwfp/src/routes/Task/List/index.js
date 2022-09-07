/**
 *  List- 待办事项列表
 * @date: 2018-8-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { DATETIME_MAX, DATETIME_MIN } from 'utils/constants';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import ApproveDrawer from './ApproveDrawer';

/**
 * 待办事项列表组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} task - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ task, loading }) => ({
  task,
  fetchListLoading: loading.effects['task/fetchTaskList'],
  batchApproveTasksLoading: loading.effects['task/batchApproveTasks'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hwfp.task', 'hwfp.common'] })
export default class List extends Component {
  constructor(props) {
    super(props);
    this.state = {
      approveVisible: false,
      selectedRows: [],
    };
  }

  form;

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      task: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
  }

  /**
   * 传递表单对象
   * @param {object} ref - FilterForm对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());

    const { createdBefore, createdAfter, priority, ...others } = filterValues;
    let minimumPriority = null;
    let maximumPriority = null;
    if (priority === 'low') {
      minimumPriority = 0;
      maximumPriority = 33;
    } else if (priority === 'medium') {
      minimumPriority = 34;
      maximumPriority = 66;
    } else if (priority === 'height') {
      minimumPriority = 67;
      maximumPriority = 100;
    }

    dispatch({
      type: 'task/fetchTaskList',
      payload: {
        tenantId,
        createdBefore: createdBefore ? moment(createdBefore).format(DATETIME_MAX) : null,
        createdAfter: createdAfter ? moment(createdAfter).format(DATETIME_MIN) : null,
        minimumPriority,
        maximumPriority,
        page: isEmpty(fields) ? {} : fields,
        ...others,
      },
    });
  }

  @Bind()
  handleApprove() {
    this.setState({
      approveVisible: true,
    });
  }

  @Bind()
  handleCancel() {
    this.setState({
      approveVisible: false,
    });
  }

  @Bind()
  handleChangeSelected(selectedRows) {
    this.setState({
      selectedRows,
    });
  }

  @Bind()
  handleOk(fieldsValue) {
    const { selectedRows } = this.state;
    const { dispatch } = this.props;
    this.setState({
      approveVisible: false,
    });
    const dataSource = selectedRows.map((item) => ({
      action: 'complete',
      currentTaskId: item.id,
      variables: [
        { name: 'approveResult', value: 'Approved' },
        { name: 'comment', value: fieldsValue.comment },
      ],
    }));
    dispatch({
      type: 'task/batchApproveTasks',
      payload: dataSource,
    }).then(() => {
      this.handleSearch();
      notification.success();
      this.setState({
        selectedRows: [],
      });
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchListLoading,
      batchApproveTasksLoading,
      tenantId,
      match: { path },
      task: { list = [], pagination },
    } = this.props;
    const { approveVisible, selectedRows } = this.state;
    const filterProps = {
      tenantId,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      dataSource: list,
      loading: fetchListLoading,
      batchApproveTasksLoading,
      onChange: this.handleSearch,
      onChangeSelected: this.handleChangeSelected,
    };
    const drawerProps = {
      visible: approveVisible,
      onCancel: this.handleCancel,
      onOk: this.handleOk,
    };
    return (
      <>
        <Header title={intl.get('hwfp.task.view.message.title.task').d('我的待办事项')}>
          {/* <Button icon="sync" onClick={() => this.handleSearch()}>
            {intl.get('hzero.common.button.reload').d('刷新')}
          </Button> */}
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.approveTrue`,
                type: 'button',
                meaning: '我的待办事项-审批通过',
              },
            ]}
            icon="check"
            type="primary"
            onClick={this.handleApprove}
            disabled={selectedRows.length === 0}
          >
            {intl.get('hwfp.task.button.approveTrue').d('审批通过')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <ApproveDrawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
