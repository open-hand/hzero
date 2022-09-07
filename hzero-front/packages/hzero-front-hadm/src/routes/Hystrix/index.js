/*
 * index - 熔断设置
 * @date: 2018/09/11 10:44:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Button, Table } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';

import { filterNullValueObject, tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { dateTimeRender, enableRender, TagRender } from 'utils/renderer';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

/**
 * 熔断规则设置
 * @extends {PureComponent} - React.Component
 * @reactProps {object} [match={}] - react.router match路由信息
 * @reactProps {object} hystrix - 数据源
 * @reactProps {object} loading - 数据加载是否完成
 * @reactProps {object} form - 表单对象
 * return React.element
 */

@connect(({ loading, hadmHystrix }) => ({
  fetchListLoading: loading.effects['hadmHystrix/fetchList'],
  addLoading: loading.effects['hadmHystrix/add'],
  refreshLoading: loading.effects['hadmHystrix/refresh'],
  hadmHystrix,
}))
@formatterCollections({ code: ['hadm.hystrix'] })
export default class Hystrix extends PureComponent {
  filterForm;

  state = {
    selectedRows: [],
    modalVisible: false,
  };

  componentDidMount() {
    const {
      location: { state: { _back } = {} },
      hadmHystrix: { pagination = {} },
      dispatch,
    } = this.props;
    dispatch({
      type: 'hadmHystrix/init',
    });
    if (_back === -1) {
      this.handleSearch({ page: pagination });
    } else {
      this.handleSearch();
    }
    this.fetchConfTypeCode();
  }

  /**
   * 查询平台熔断类型列表
   * @param {object} payload 查询字段
   */
  @Bind()
  handleSearch(params = {}) {
    const {
      dispatch,
      hadmHystrix: { pagination },
    } = this.props;
    const filterValue = isUndefined(this.formDom)
      ? {}
      : filterNullValueObject(this.formDom.getFieldsValue());
    dispatch({
      type: 'hadmHystrix/fetchList',
      payload: {
        page: pagination,
        ...filterValue,
        ...params,
      },
    });
  }

  @Bind()
  fetchConfTypeCode() {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmHystrix/fetchConfTypeCodeList',
      payload: { lovCode: 'HADM.HYSTRIX_CONF_TYPE' },
    });
  }

  /**
   * 新建熔断类型展示模态框
   */
  @Bind()
  showModal() {
    this.setState({ modalVisible: true });
  }

  /**
   * 隐藏模态框
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.setState({ modalVisible: false });
    }
  }

  /**
   * 新增熔断规则
   * @param {object} fields 新增的熔断规则表单内的内容
   */
  @Bind()
  handleAdd(fields) {
    const { dispatch } = this.props;
    const refreshStatus = -1;
    const item = { refreshStatus, ...fields };
    dispatch({
      type: 'hadmHystrix/add',
      payload: item,
    }).then((res) => {
      if (res) {
        this.hideModal();
        this.handleSearch();
        notification.success();
      }
    });
  }

  /**
   * 选择规则/
   * @param {array} selectedRowKeys
   * @param {array} selectedRows
   */
  @Bind()
  handleRowSelectedChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRows });
  }

  /**
   * 勾选刷新
   */
  @Bind()
  handleRefresh() {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;
    const confIdList = selectedRows.map((n) => ({ confId: n.confId }));
    if (selectedRows.length > 0) {
      dispatch({
        type: 'hadmHystrix/refresh',
        payload: confIdList,
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch();
          this.setState({ selectedRows: [] });
        }
      });
    } else {
      notification.warning({
        message: intl.get(`hzero.common.message.confirm.selected.atLeast`).d('请至少选择一行数据'),
      });
    }
  }

  /**
   * 数据查询
   * @param {Object} pagination 查询参数
   * @param {String} [pagination.page] - 分页查询-页码
   * @param {String} [pagination.size] - 分页查询-分页大小
   */
  @Bind()
  searchPaging(pagination) {
    this.handleSearch({ page: pagination });
  }

  render() {
    const {
      history,
      match,
      location: { search },
      fetchListLoading = false,
      addLoading = false,
      refreshLoading = false,
      hadmHystrix: { dataSource, pagination, confTypeCodeList, refreshStatus },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const { selectedRows, modalVisible } = this.state;
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.confId),
      onChange: this.handleRowSelectedChange,
    };
    const filterProps = {
      confTypeCodeList,
      fetchListLoading,
      onRef: (node) => {
        this.formDom = node.props.form;
      },
      onFilterChange: this.handleSearch,
      refreshStatus,
    };
    const drawerProps = {
      addLoading,
      anchor: 'right',
      confTypeCodeList,
      visible: modalVisible,
      onCancel: this.hideModal,
      onOk: this.handleAdd,
      title: intl.get(`hadm.hystrix.view.message.title.modal.create`).d(`新建熔断类型`),
    };
    const columns = [
      {
        title: intl.get(`hadm.hystrix.model.hystrix.confTypeCode`).d('代码'),
        dataIndex: 'confTypeCode',
        fixed: 'left',
        width: 120,
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.confKey`).d('类型'),
        dataIndex: 'confKey',
        width: 120,
        render: (val, record) => {
          const data = confTypeCodeList.find((e) => e.value === record.confKey);
          if (data) {
            return data.meaning;
          }
        },
      },
      {
        title: intl.get(`hzero.common.view.description`).d('描述'),
        dataIndex: 'remark',
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.serviceName`).d('服务'),
        dataIndex: 'serviceName',
        width: 120,
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.serviceConfLabel`).d('服务配置标签'),
        dataIndex: 'serviceConfLabel',
        width: 160,
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.serviceConfProfile`).d('服务配置Profile'),
        dataIndex: 'serviceConfProfile',
        width: 160,
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.refreshTime`).d('刷新时间'),
        dataIndex: 'refreshTime',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.refreshMessage`).d('刷新消息'),
        dataIndex: 'refreshMessage',
      },
      {
        title: intl.get(`hadm.hystrix.model.hystrix.refreshStatus`).d('刷新状态'),
        dataIndex: 'refreshStatus',
        width: 120,
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
        width: 80,
        fixed: 'right',
        render: (val, record) => (
          <span className="action-link">
            <a
              onClick={() => {
                history.push(
                  match.path.indexOf('/private') === 0
                    ? `/private/hadm/hystrix/detail/${record.confId}?access_token=${accessToken}`
                    : `/hadm/hystrix/detail/${record.confId}`
                );
              }}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
          </span>
        ),
      },
    ];

    return (
      <>
        <Header title={intl.get(`hadm.hystrix.view.message.title.hystrix`).d('熔断规则')}>
          <Button icon="plus" type="primary" onClick={this.showModal}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
          <Button
            icon="sync"
            onClick={this.handleRefresh}
            disabled={isEmpty(selectedRows)}
            loading={refreshLoading || fetchListLoading}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <Table
            bordered
            rowSelection={rowSelection}
            loading={fetchListLoading}
            rowKey="confId"
            columns={columns}
            dataSource={dataSource}
            pagination={pagination}
            onChange={this.searchPaging}
            scroll={{ x: tableScrollWidth(columns) }}
          />
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
