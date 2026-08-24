/**
 * MessageTemplate - 消息模板列表
 * @date: 2018-7-26
 * @author: WH <heng.wei@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isUndefined, isEmpty } from 'lodash';
import { Bind, Debounce } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { DEBOUNCE_TIME } from 'utils/constants';

import ListTable from './ListTable';
import SearchForm from './SearchForm';

/**
 * 消息模板列表组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} messageTemplate - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ messageTemplate, loading }) => ({
  messageTemplate,
  loading: loading.effects['messageTemplate/fetchTemplate'],
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({
  code: ['hmsg.messageTemplate', 'entity.tenant', 'entity.lang', 'hmsg.common'],
})
export default class List extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isCopy: false,
    };
  }

  filterForm;

  /**
   * componentDidMount 生命周期函数
   * render()执行后获取页面数据
   */
  componentDidMount() {
    const {
      dispatch,
      messageTemplate: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    dispatch({
      type: 'messageTemplate/fetchLanguage',
    });
  }

  @Bind()
  handleRef(ref = {}) {
    this.filterForm = ref.props.form;
  }

  /**
   * 新增模板，跳转到明细页面
   */
  @Bind()
  handleAddTemplate() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hmsg/message-template/create`,
      })
    );
  }

  @Bind()
  handleSearchFormSearch(form) {
    this.handleSearch({}, form.getFieldsValue());
  }

  /**
   * 页面查询
   * @param {object} fields - 查询参数
   * @param {object} query - 查询参数
   */
  @Bind()
  handleSearch(fields = {}, query = {}) {
    const { dispatch } = this.props;
    const filterFormValues = this.filterForm ? this.filterForm.getFieldsValue() : {};
    dispatch({
      type: 'messageTemplate/fetchTemplate',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...filterFormValues,
        ...query,
      },
    });
  }

  /**
   * 复制
   */
  @Debounce(DEBOUNCE_TIME)
  @Bind()
  handleCopy(record = {}) {
    const { dispatch } = this.props;
    this.setState(
      {
        isCopy: true,
      },
      () => {
        const { isCopy } = this.state;
        const payload = { ...record, isCopy };
        dispatch(
          routerRedux.push({
            pathname: `/hmsg/message-template/create`,
            payload,
          })
        );
      }
    );
  }

  @Bind()
  handleDelete(record = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageTemplate/deleteItem',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleUpdate(record = {}) {
    const { dispatch } = this.props;
    this.setState(
      {
        isCopy: false,
      },
      () => {
        const { isCopy } = this.state;
        const payload = { ...record, isCopy };
        dispatch(
          routerRedux.push({
            pathname: `/hmsg/message-template/detail/${record.templateId}`,
            payload,
          })
        );
      }
    );
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      messageTemplate: { list = [], pagination = {}, language = [] },
      tenantRoleLevel,
      match: { path },
    } = this.props;
    const filterProps = {
      tenantRoleLevel,
      language,
      onSearch: this.handleSearchFormSearch,
      onRef: this.handleRef,
    };
    const listProps = {
      tenantRoleLevel,
      pagination,
      language,
      loading,
      path,
      dataSource: list,
      onChange: this.handleSearch,
      // onEditOrCopy: this.handleEditOrCopyContent,
      onCopy: this.handleCopy,
      onUpdate: this.handleUpdate,
      onDelete: this.handleDelete,
    };
    return (
      <>
        <Header title={intl.get('hmsg.messageTemplate.view.message.title.list').d('消息模板')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '消息模板-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.handleAddTemplate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
