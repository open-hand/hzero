/**
 * templateManage - 模板管理
 * @date: 2018-11-26
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
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

/**
 * 模板管理
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} approveAuth - 数据源
 * @reactProps {!Object} fetchApproveLoading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ templateManage, loading }) => ({
  templateManage,
  tenantRoleLevel: isTenantRoleLevel(),
  fetchTemplateManageLoading: loading.effects['templateManage/fetchTemplateManageList'],
}))
@formatterCollections({ code: ['hrpt.templateManage', 'entity.tenant', 'entity.template'] })
export default class List extends Component {
  form;

  /**
   * state初始化
   */
  state = {};

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      templateManage: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.fetchTemplateManageList(page);
    const lovCodes = {
      templateTypeCode: 'HRPT.TEMPLATE_TYPE', // 模板类型
    };
    // 初始化 值集
    this.props.dispatch({
      type: 'templateManage/batchCode',
      payload: {
        lovCodes,
      },
    });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  fetchTemplateManageList(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'templateManage/fetchTemplateManageList',
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
  handleAddTemplate() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hrpt/template-manage/create`,
      })
    );
  }

  /**
   * 删除模板管理
   */
  @Bind()
  handleDeleteContent(values) {
    const {
      dispatch,
      templateManage: { pagination },
    } = this.props;
    dispatch({
      type: 'templateManage/deleteTemplateManage',
      payload: values,
    }).then((res) => {
      if (res) {
        this.fetchTemplateManageList(pagination);
        notification.success();
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
        pathname: `/hrpt/template-manage/detail/${record.templateId}`,
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
   * render
   * @returns React.element
   */
  render() {
    const {
      tenantRoleLevel,
      fetchTemplateManageLoading,
      templateManage: {
        list = {},
        pagination,
        code: { templateTypeCode = [] },
      },
    } = this.props;
    const filterProps = {
      templateTypeCode,
      onSearch: this.fetchTemplateManageList,
      onRef: this.handleBindRef,
    };
    const listProps = {
      tenantRoleLevel,
      pagination,
      loading: fetchTemplateManageLoading,
      dataSource: list.content,
      onEdit: this.handleEditContent,
      onDelete: this.handleDeleteContent,
      onChange: this.fetchTemplateManageList,
    };
    return (
      <>
        <Header
          title={intl
            .get('hrpt.templateManage.view.message.title.templateManage')
            .d('报表模板管理')}
        >
          <Button type="primary" onClick={this.handleAddTemplate} icon="plus">
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <SearchForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
