/**
 * Template - 通用模板
 * @since 2018-08-24
 * @author yuan.tian <yuan.tian@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import notification from 'utils/notification';
import { Button, Form } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, filterNullValueObject } from 'utils/utils';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 通用模板头页面
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@connect(({ template, loading }) => ({
  template,
  loading: {
    query: loading.effects['template/query'],
  },
  organizationId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['himp.template'] })
export default class List extends Component {
  /**
   * componentDidMount 生命周期函数
   * render()执行后获取页面数据
   */
  componentDidMount() {
    const {
      template: { pagination },
    } = this.props;
    this.fetchList(pagination);
  }

  /**
   * fetchList - 查询列表数据
   * @param {object} [params = {}] - 查询参数
   */
  @Bind()
  fetchList(fields = {}) {
    const { dispatch, organizationId, form } = this.props;
    const fieldValues = filterNullValueObject(form.getFieldsValue());
    dispatch({
      type: 'template/query',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        organizationId,
        ...fieldValues,
      },
    });
  }

  /**
   * 新增模板，跳转到明细页面
   */
  @Bind()
  handleAddTemplate() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/himp/template/detail/create`,
      })
    );
  }

  /**
   * 修改通用模板信息，跳转到明细页面
   * @param {object} record - 消息模板对象
   */
  @Bind()
  handleEditContent(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/himp/template/detail/${record.id}`,
      })
    );
  }

  /**
   * 删除通用模板信息后刷新表格数据列表
   * @param {object} record - 消息模板对象
   */
  @Bind()
  handleDelContent(record) {
    const {
      dispatch,
      organizationId,
      template: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'template/removeHeader',
      payload: {
        ...record,
        organizationId,
      },
    }).then(res => {
      if (res) {
        notification.success({
          message: intl.get(`hzero.common.notification.success.delete`).d('删除成功'),
        });
        this.fetchList(pagination);
      }
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
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      loading,
      organizationId,
      template: { headerList, pagination },
    } = this.props;
    const filterProps = {
      form,
      search: this.fetchList,
    };
    const listProps = {
      pagination,
      organizationId,
      dataSource: headerList,
      loading: loading.query,
      onChange: page => this.fetchList(page),
      editContent: this.handleEditContent,
      delContent: this.handleDelContent,
    };
    return (
      <>
        <Header title={intl.get(`himp.template.view.message.title`).d('导入模板管理')}>
          <Button icon="plus" type="primary" onClick={this.handleAddTemplate}>
            {intl.get(`hzero.common.button.create`).d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
