/* eslint-disable no-unused-vars */
/**
 * TypeApplication - 应用类型定义
 * @date: 2019-8-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import { Modal } from 'hzero-ui';
import { Header, Content } from 'components/Page';
import queryString from 'query-string';
import { filterNullValueObject } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 应用类型定义
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} typeDefinition - 数据源
 * @reactProps {Boolean} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ typeDefinition, loading }) => ({
  typeDefinition,
  loading: loading.effects['typeDefinition/queryList'],
  deleteLoading: loading.effects['typeDefinition/deleteDefinition'],
}))
@formatterCollections({
  code: ['hitf.typeDefinition'],
})
export default class TypeDefinition extends PureComponent {
  form;

  state = {
    selectedRowKeys: [],
    selectedRows: {},
  };

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      typeDefinition: { list = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const { pagination = {} } = list;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
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
      type: 'typeDefinition/queryList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 跳转至新建实例页面
   */
  @Bind()
  redirectToCreate() {
    const {
      history,
      location: { search, pathname },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    history.push({
      pathname:
        pathname.indexOf('/private') === 0
          ? `/private/hitf/application-type-definition/create`
          : `/hitf/application-type-definition/create`,
      search: pathname.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
    });
  }

  /**
   * 跳转至编辑实例页面
   *@param {number} applicationId - 应用ID
   */
  @Bind()
  redirectToEdit(applicationId) {
    const {
      history,
      location: { search, pathname },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    history.push({
      pathname:
        pathname.indexOf('/private') === 0
          ? `/private/hitf/application-type-definition/detail/${applicationId}`
          : `/hitf/application-type-definition/detail/${applicationId}`,
      search: pathname.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
    });
  }

  /**
   * 获取表格选中行
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
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
   * 删除应用定义
   */
  @Bind()
  handleDeleteDefinition() {
    const { dispatch, deleteLoading } = this.props;
    const { selectedRows } = this.state;
    if (deleteLoading) return;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        dispatch({
          type: 'typeDefinition/deleteDefinition', // 删除
          payload: selectedRows,
        }).then((res) => {
          if (res) {
            notification.success();
            that.setState(
              {
                selectedRowKeys: [],
                selectedRows: {},
              },
              () => {
                that.handleSearch();
              }
            );
          }
        });
      },
    });
  }

  render() {
    const {
      loading,
      deleteLoading,
      typeDefinition = {},
      match: { path },
    } = this.props;
    const { selectedRowKeys } = this.state;
    const { list = {} } = typeDefinition;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      loading,
      dataSource: list.dataSource,
      pagination: list.pagination,
      rowSelection,
      onEdit: this.redirectToEdit,
      onChange: this.handleSearch,
    };
    return (
      <>
        <Header
          title={intl
            .get('hitf.typeDefinition.view.message.title.typeDefinition')
            .d('应用类型定义')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '应用类型定义-新建',
              },
            ]}
            icon="add"
            type="c7n-pro"
            color="primary"
            onClick={this.redirectToCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '应用类型定义-删除',
              },
            ]}
            icon="delete"
            type="c7n-pro"
            disabled={isEmpty(selectedRowKeys)}
            onClick={this.handleDeleteDefinition}
            loading={deleteLoading}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
