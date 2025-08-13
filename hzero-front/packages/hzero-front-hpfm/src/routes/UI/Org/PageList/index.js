/**
 * @date 2018/11/20
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { forEach } from 'lodash';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';

import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

import { Header, Content } from 'components/Page';

import EditModal from '../../components/EditModal';

import SearchForm from './SearchForm';
import DataTable from './DataTable';

@connect(({ loading, uiPageOrg }) => ({
  fetchPageListLoading: loading.effects['uiPageOrg/fetchPageList'],
  createLoading: loading.effects['uiPageOrg/listCreateOne'],
  updateLoading: loading.effects['uiPageOrg/listUpdateOne'],
  uiPage: uiPageOrg,
  organizationId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.ui'] })
export default class PageList extends React.Component {
  state = {
    pagination: {}, // Table 切换分页后的分页信息
    // Modal
    createModalProps: {
      visible: false,
    },
    updateModalProps: {
      visible: false,
    },
  };

  componentDidMount() {
    const { pagination } = this.state;
    this.handleSearch(pagination);
  }

  reloadList() {
    const { pagination } = this.state;
    this.handleSearch(pagination);
  }

  render() {
    const {
      uiPage: { pagination, list },
      match,
      fetchPageListLoading,
      createLoading,
      updateLoading,
    } = this.props;
    const { createModalProps, updateModalProps, editRecord } = this.state;
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.ui.view.list.title').d('页面汇总')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '页面自定义(租户)-新建',
              },
            ]}
            onClick={this.handleCreateBtnClick}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <SearchForm onSearch={this.handleSearch} onRefForm={this.handleRefSearchForm} />
          <DataTable
            onPageEdit={this.handlePageEdit}
            onPageDesign={this.handlePageDesign}
            onTableChange={this.handleTableChange}
            dataSource={list.content}
            match={match}
            pagination={pagination}
            loading={fetchPageListLoading}
          />
          <EditModal
            modalProps={createModalProps}
            confirmLoading={createLoading}
            onOk={this.handlePageCreate}
            onCancel={this.handlePageCreateCancel}
            title={intl.get('hpfm.ui.view.list.modal.create.title').d('新建页面')}
            isCreate
          />
          <EditModal
            modalProps={updateModalProps}
            confirmLoading={updateLoading}
            onOk={this.handlePageUpdate}
            onCancel={this.handlePageUpdateCancel}
            editRecord={editRecord}
            title={intl.get('hpfm.ui.view.list.modal.edit.title').d('编辑页面')}
            wrapClassName="ant-modal-sidebar-right"
            transitionName="move-right"
          />
        </Content>
      </React.Fragment>
    );
  }

  @Bind()
  handleCreateBtnClick() {
    this.setState({
      createModalProps: {
        visible: true,
      },
    });
  }

  @Bind()
  handlePageCreate(data) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'uiPageOrg/listCreateOne',
      payload: {
        organizationId,
        data,
      },
    }).then(res => {
      if (res) {
        this.setState({
          createModalProps: {
            visible: false,
          },
        });
        this.reloadList();
      }
    });
  }

  @Bind()
  handlePageCreateCancel() {
    this.setState({
      createModalProps: {
        visible: false,
      },
    });
  }

  /**
   * 由于@Form.create 创造的高阶组件,所以必须使用自定义方法传递form
   * @param {Object} searchForm - 表单 form
   */
  @Bind()
  handleRefSearchForm(searchForm) {
    this.searchForm = searchForm;
  }

  @Bind()
  handleSearch(pagination = {}) {
    const params = this.searchForm.getFieldsValue();
    const filterUndefinedParams = {};
    forEach(params, (value, key) => {
      if (value !== undefined) {
        filterUndefinedParams[key] = value;
      }
    });
    const { dispatch, organizationId } = this.props;
    this.setState({
      pagination,
    });
    dispatch({
      type: 'uiPageOrg/fetchPageList',
      payload: {
        organizationId,
        params: {
          body: filterUndefinedParams,
          ...pagination,
        },
      },
    });
  }

  @Bind()
  handleTableChange({ pagination, /* filtersArg, */ sorter }) {
    this.handleSearch({ page: pagination, sort: sorter });
  }

  @Bind()
  handlePageEdit(record) {
    this.setState({
      editRecord: record,
      updateModalProps: {
        visible: true,
      },
    });
  }

  @Bind()
  handlePageUpdate(data) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'uiPageOrg/listUpdateOne',
      payload: {
        organizationId,
        data,
      },
    }).then(res => {
      if (res) {
        this.setState({
          updateModalProps: {
            visible: false,
          },
        });
        this.reloadList();
      }
    });
  }

  @Bind()
  handlePageUpdateCancel() {
    this.setState({
      editRecord: null,
      updateModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  handlePageDesign(pageCode) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/ui/page-org/detail/${pageCode}`,
      })
    );
  }
}
