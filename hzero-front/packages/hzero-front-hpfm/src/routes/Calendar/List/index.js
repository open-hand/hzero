/**
 * Calendar - 平台级日历
 * @date: 2018-9-26
 * @author: WH <heng.wei@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 日历定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} calendar - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ calendar, loading }) => ({
  calendar,
  tenantId: getCurrentOrganizationId(),
  loading: loading.effects['calendar/searchCalendar'],
  saveLoading:
    loading.effects['calendar/addCalendar'] || loading.effects['calendar/updateCalendar'],
}))
@formatterCollections({ code: ['hpfm.calendar'] })
export default class List extends Component {
  form;

  /**
   * state初始化
   */
  constructor(props) {
    super(props);
    this.state = {
      isCreate: false,
    };
  }

  /**
   * render()调用后请求数据
   */
  componentDidMount() {
    const {
      calendar: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = _back === -1 ? pagination : {};
    this.handleSearchCalendar(page);
  }

  /**
   * 新增日历信息
   */
  @Bind()
  handleAddOption() {
    this.setState({ drawerVisible: true, targetItem: {}, isCreate: true });
  }

  /**
   * 编辑日历头信息
   * @param {object} record - 日历对象
   */
  @Bind()
  handleCalendarRow(record) {
    this.setState({ drawerVisible: true, targetItem: record, isCreate: false });
  }

  /**
   * 跳转日历详情维护页面
   * @param {object} record - 日历对象
   */
  @Bind()
  handleCalendarDetail(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'calendar/updateState',
      payload: {
        calendarDetail: {},
        holidays: [],
        weekdays: [],
        holidayType: [],
      },
    });
    dispatch(routerRedux.push({ pathname: `/hpfm/mdm/calendar/detail/${record.calendarId}` }));
  }

  /**
   * Drawer Close
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({ drawerVisible: false, targetItem: {} });
  }

  /**
   * Drawer Ok
   * @param {Object} values - 操作数据对象
   */
  @Bind()
  handleDrawerOk(values) {
    const {
      dispatch,
      calendar: { pagination = {} },
      tenantId,
    } = this.props;
    dispatch({
      type: isUndefined(values.calendarId) ? 'calendar/addCalendar' : 'calendar/updateCalendar',
      payload: { tenantId, ...values },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearchCalendar(pagination);
        this.setState({ targetItem: {}, drawerVisible: false });
      }
    });
  }

  /**
   * 查询日历数据
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearchCalendar(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'calendar/searchCalendar',
      payload: {
        tenantId,
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 设置form对象
   * @param {object} ref - FilterForm子组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      tenantId,
      saveLoading,
      match: { path },
      calendar: { calendarList = [], pagination = {} },
    } = this.props;
    const { drawerVisible = false, targetItem = {}, isCreate = false } = this.state;
    const filterProps = {
      tenantId,
      onSearch: this.handleSearchCalendar,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      loading,
      path,
      dataSource: calendarList,
      onSearch: this.handleSearchCalendar,
      onEditDetail: this.handleCalendarDetail,
      onEditRow: this.handleCalendarRow,
    };
    const drawerProps = {
      tenantId,
      loading: saveLoading || loading,
      targetItem,
      title: isCreate
        ? intl.get('hpfm.calendar.view.message.drawerCreate').d('日历新建')
        : intl.get('hpfm.calendar.view.message.drawerEdit').d('日历编辑'),
      anchor: 'right',
      visible: drawerVisible,
      onCancel: this.handleDrawerCancel,
      onOk: this.handleDrawerOk,
    };
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.calendar.view.message.title.define').d('日历定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            onClick={this.handleAddOption}
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '日历定义-新建',
              },
            ]}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <Drawer {...drawerProps} />
        </Content>
      </React.Fragment>
    );
  }
}
