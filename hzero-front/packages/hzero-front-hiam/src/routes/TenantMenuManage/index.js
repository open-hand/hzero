/**
 * 租户菜单管理
 * @since 2019-12-2
 * @author LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject } from 'utils/utils';

// import Editor from './Editor';
import List from './List';
import SearchForm from './SearchForm';
// import PermissionSet from './PermissionSet';
import styles from './index.less';

@connect(({ tenantMenuManage, loading }) => ({
  tenantMenuManage,
  tenantLoading: loading.effects['tenantMenuManage/queryTenant'],
}))
@formatterCollections({ code: ['hiam.tenantMenu'] })
export default class TenantMenuManage extends React.Component {
  tenantForm;

  searchForm = React.createRef();

  componentDidMount() {
    const {
      location: { state: { _back } = {} },
      dispatch,
    } = this.props;
    dispatch({
      type: 'tenantMenuManage/initCustomizeList',
    });
    if (_back !== -1) {
      this.handleSearchTenant();
    }
  }

  componentDidUpdate(prevProps) {
    const {
      location: { state },
    } = this.props;
    const {
      location: { state: prevState },
    } = prevProps;
    if (state !== prevState) {
      this.updateFormAndSearch();
    }
  }

  // componentWillUnmount() {
  //   const { history } = this.props;
  //   history.push({
  //     state: {
  //       _back: 0,
  //     },
  //   });
  // }

  /**
   * 检查 location.search 是否发生变化, 发生变化后 设置查询表单+查询
   */
  @Bind()
  updateFormAndSearch() {
    const {
      location: { state },
    } = this.props;
    if (isEmpty(state)) {
      this.handleSearchTenant();
    } else if (state.tenantName) {
      // TODO: 租户维护这边 暂时只支持 tenantName 更改
      if (this.searchForm.current) {
        this.searchForm.current.props.form.setFieldsValue({ tenantName: state.tenantName });
      }
      this.handleSearchTenant();
    }
  }

  /**
   * 按条件查询
   */
  @Bind()
  handleSearchTenant(payload = {}) {
    const { dispatch } = this.props;
    const form =
      this.searchForm.current &&
      this.searchForm.current.props &&
      this.searchForm.current.props.form;
    const filterValues = isUndefined(form) ? {} : filterNullValueObject(form.getFieldsValue());
    dispatch({
      type: 'tenantMenuManage/queryTenant',
      payload: {
        page: isEmpty(payload) ? {} : payload,
        ...filterValues,
      },
    });
  }

  /**
   * 跳转到详情页
   * @param {object} recrod
   */
  @Bind()
  handleToDetails(recrod) {
    const { history } = this.props;
    history.push(`/hiam/tenant-menu-manage/detail/${recrod.tenantId}`);
  }

  render() {
    const {
      dispatch,
      tenantMenuManage = {},
      match: { path },
      tenantLoading,
    } = this.props;
    const { tenantData = {}, pagination, menuTypeList = [], customizeList = [] } = tenantMenuManage;
    const { content = [] } = tenantData;
    const listProps = {
      path,
      content,
      dispatch,
      menuTypeList,
      tenantLoading,
      tenantPagination: pagination,
      onTenantPageChange: this.handleSearchTenant,
      onHandleToDetails: this.handleToDetails,
    };
    const filterProps = {
      onSearch: this.handleSearchTenant,
      wrappedComponentRef: this.searchForm,
      customizeList,
    };

    return (
      <>
        <Header title={intl.get('hiam.tenantMenu.view.message.title').d('租户菜单管理')} />
        <Content>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <div className={styles['hiam-tenant-menu-table']}>
            <List {...listProps} />
          </div>
        </Content>
      </>
    );
  }
}
