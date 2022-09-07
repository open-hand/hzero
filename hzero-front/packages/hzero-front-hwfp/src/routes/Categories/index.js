/**
 * Categories - 流程分类
 * @date: 2018-8-15
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { filterNullValueObject, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import List from './List';
import Search from './Search';

/**
 * 流程分类数据展示
 * @extends {Component} - React.Component
 * @reactProps {Object} formValues - 查询表单值
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {!Object} fetchCategoriesLoading - 列表数据查询是否完成
 * @reactProps {!Object} saving - 保存是否完成
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {Boolean} visible - 模态框是否可见
 * @return React.element
 */
@connect(({ categories, loading }) => ({
  categories,
  isSiteFlag: !isTenantRoleLevel(),
  currentTenantId: getCurrentOrganizationId(),
  fetchCategoriesLoading: loading.effects['categories/fetchCategories'],
  saving:
    loading.effects['categories/createCategories'] || loading.effects['categories/editCategories'],
}))
@formatterCollections({ code: ['hwfp.categories', 'hwfp.common', 'hwfl.common'] })
export default class Categories extends Component {
  searchRef = React.createRef();

  componentDidMount() {
    this.queryCategories();
  }

  // 获取所有表格列表数据
  @Bind()
  queryCategories(page = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.searchRef.current)
      ? {}
      : filterNullValueObject(this.searchRef.current.getFieldsValue());
    dispatch({
      type: 'categories/fetchCategories',
      payload: {
        page: isEmpty(page) ? {} : page,
        ...fieldValues,
      },
    });
  }

  // 删除流程分类
  @Bind()
  handleDelete(values) {
    const {
      dispatch,
      organizationId,
      categories: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'categories/deleteCategories',
      payload: { ...values, organizationId },
    }).then(response => {
      if (response) {
        this.queryCategories(pagination);
        notification.success();
      }
    });
  }

  /**
   * 跳转到详情页
   */
  @Bind()
  handleToDetail(processCategoryId) {
    this.props.history.push(
      processCategoryId
        ? `/hwfp/setting/categories/detail/${processCategoryId}`
        : `/hwfp/setting/categories/create`
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

  render() {
    const {
      isSiteFlag,
      currentTenantId,
      fetchCategoriesLoading,
      categories: { dataSource = [], pagination = {} },
    } = this.props;
    const searchProps = {
      isSiteFlag,
      onSearch: this.queryCategories,
      ref: this.searchRef,
    };
    const tableProps = {
      isSiteFlag,
      currentTenantId,
      dataSource,
      pagination,
      loading: fetchCategoriesLoading,
      onChange: this.queryCategories,
      onDelete: this.handleDelete,
      onDirectToDetail: this.handleToDetail,
    };
    return (
      <>
        <Header title={intl.get('hwfp.common.model.process.class').d('流程分类')}>
          <Button type="primary" icon="plus" onClick={() => this.handleToDetail()}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <Search {...searchProps} />
          <List {...tableProps} />
        </Content>
      </>
    );
  }
}
