/*
 * index.js - 流程单据
 * @date: 2019-04-28
 * @author: HB <bin.huang02@hand-china.com>
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
 * 流程单据数据展示
 * @extends {Component} - React.Component
 * @reactProps {Object} formValues - 查询表单值
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {!Object} fetchCategoriesLoading - 列表数据查询是否完成
 * @reactProps {!Object} saving - 保存是否完成
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {Boolean} visible - 模态框是否可见
 * @return React.element
 */
@connect(({ documents, loading }) => ({
  documents,
  currentTenantId: getCurrentOrganizationId(),
  isSiteFlag: !isTenantRoleLevel(),
  fetchCategoriesLoading: loading.effects['documents/fetchList'],
}))
@formatterCollections({ code: ['hwfp.documents', 'hwfp.common'] })
export default class Document extends Component {
  searchRef = React.createRef();

  // 初始化
  componentDidMount() {
    const {
      location: { state: { _back } = {} },
      documents: { pagination },
    } = this.props;
    this.fetchList(_back === -1 ? pagination : {});
  }

  // 获取所有表格列表数据
  @Bind()
  fetchList(page = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.searchRef.current)
      ? {}
      : filterNullValueObject(this.searchRef.current.getFieldsValue());
    dispatch({
      type: 'documents/fetchList',
      payload: {
        page: isEmpty(page) ? {} : page,
        ...fieldValues,
      },
    });
  }

  // 删除流程分类
  @Bind()
  handleDelete(payload) {
    const {
      dispatch,
      documents: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'documents/deleteDocuments',
      payload,
    }).then(response => {
      if (response) {
        this.fetchList(pagination);
        notification.success();
      }
    });
  }

  /**
   * 跳转到详情页
   */
  @Bind()
  handleToDetail(documentId) {
    this.props.history.push(
      documentId ? `/hwfp/setting/documents/detail/${documentId}` : `/hwfp/setting/documents/create`
    );
  }

  render() {
    const {
      isSiteFlag,
      currentTenantId,
      documents: { dataSource = [], pagination = {} },
      fetchCategoriesLoading,
    } = this.props;
    const formProps = {
      isSiteFlag,
      onSearch: this.fetchList,
      onStore: this.storeFormValues,
      ref: this.searchRef,
    };
    const tableProps = {
      isSiteFlag,
      pagination,
      dataSource,
      currentTenantId,
      loading: fetchCategoriesLoading,
      onChange: this.fetchList,
      onDirectToDetail: this.handleToDetail,
      onDeleteRecord: this.handleDelete,
    };
    return (
      <>
        <Header title={intl.get('hwfp.common.view.message.title.document').d('流程单据')}>
          <Button type="primary" icon="plus" onClick={() => this.handleToDetail()}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <Search {...formProps} />
          <List {...tableProps} />
        </Content>
      </>
    );
  }
}
