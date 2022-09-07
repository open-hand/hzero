/**
 * index - 财务代码设置页面
 * @date: 2019-3-4
 * @author: lxl <xiaolong.li02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2019, Hand
 */
import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import Search from './Search';
import List from './List';
import AddDrawer from './AddDrawer';
import EditDrawer from './EditDrawer';

/**
 * 财务代码设置
 * @export {Component} - React.Component
 * @reactProps {object} financialCode - 数据源
 * @reactProps {function} dispatch - dispatch方法
 * @reactProps {boolean} searchLoading - 查询状态
 * @reactProps {boolean} saveLoading - 新增保存状态
 * @reactProps {boolean} updateLoading - 编辑保存状态
 */
@connect(({ financialCode, loading }) => ({
  financialCode,
  searchLoading: loading.effects['financialCode/fetchList'],
  saveLoading: loading.effects['financialCode/saveCreate'],
  updateLoading: loading.effects['financialCode/saveUpdate'],
}))
@formatterCollections({ code: ['hpfm.financialCode'] })
export default class FinancialCode extends Component {
  constructor(props) {
    super(props);
    this.state = {
      hideDrawer: true,
      isCreateDrawer: true,
      editRecord: {},
    };
  }

  /**
   * 组件加载完成是触发
   */
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'financialCode/fetchValue',
      payload: { valueType: 'HPFM.FINANCIAL_CODE_TYPE' },
    });
    this.handleSearch();
  }

  /**
   * handleCreate - 新建财务代码
   */
  @Bind()
  handleCreate() {
    this.setState({
      hideDrawer: false,
      isCreateDrawer: true,
      editRecord: {},
    });
  }

  /**
   * handleEdit - 编辑财务代码
   * @param {*} record - 行数据
   */
  @Bind()
  handleEdit(record = {}) {
    this.setState({
      hideDrawer: false,
      isCreateDrawer: false,
      editRecord: record,
    });
  }

  /**
   * handleCloseDrawer - 关闭侧滑弹窗
   */
  @Bind()
  handleCloseDrawer() {
    this.setState({
      hideDrawer: true,
    });
  }

  /**
   * handleDrawerOk - 保存侧滑弹窗
   */
  @Bind()
  handleDrawerOk() {
    const { isCreateDrawer, editRecord } = this.state;
    const {
      dispatch,
      financialCode: { typeList },
    } = this.props;
    const {
      props: { form },
      state: { checked: editChecked },
    } = this.slideDrawer;
    form.validateFields((err, value) => {
      if (!err) {
        const type = isCreateDrawer ? 'financialCode/saveCreate' : 'financialCode/saveUpdate';
        const { types = [], codeId, enabledFlag: addChecked, ...rest } = value;
        const typeObj = typeList.filter(n => types.indexOf(n.orderSeq) >= 0);
        dispatch({
          type,
          payload: {
            ...editRecord,
            ...rest,
            type: typeObj.map(n => n.value).join(','),
            enabledFlag: (isCreateDrawer ? addChecked : editChecked) ? 1 : 0,
            parentId: codeId,
          },
        }).then(res => {
          if (res) {
            this.handleCloseDrawer();
            this.handleSearch();
          }
        });
      }
    });
  }

  /**
   * handleSearch - 查询
   * @param {object} page - 分页信息
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch } = this.props;
    const fields = this.searchForm.props.form.getFieldsValue();
    dispatch({
      type: 'financialCode/fetchList',
      payload: {
        page,
        ...fields,
      },
    });
  }

  render() {
    const { hideDrawer, isCreateDrawer, editRecord } = this.state;
    const { searchLoading, saveLoading, updateLoading, match, financialCode = {} } = this.props;
    const { dataSource, pagination, typeList } = financialCode;
    const searchProps = {
      onSearch: this.handleSearch,
      onRef: node => {
        this.searchForm = node;
      },
      typeList,
    };
    const listProps = {
      onEdit: this.handleEdit,
      onChange: this.handleSearch,
      loading: searchLoading,
      match,
      dataSource,
      pagination,
      typeList,
    };
    const drawerProps = {
      typeList,
      visible: !hideDrawer,
      onOk: this.handleDrawerOk,
      onClose: this.handleCloseDrawer,
      onRef: node => {
        this.slideDrawer = node;
      },
      saveLoading,
      updateLoading,
    };
    return (
      <Fragment>
        <Header title={intl.get('hpfm.financialCode.view.message.title').d('财务代码设置')}>
          <ButtonPermission
            type="primary"
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '财务代码设置-新建',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get(`hzero.common.button.create`).d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Spin spinning={saveLoading || updateLoading || false}>
            <div className="table-list-search">
              <Search {...searchProps} />
            </div>
            <List {...listProps} />
            {isCreateDrawer ? (
              <AddDrawer {...drawerProps} />
            ) : (
              <EditDrawer {...drawerProps} editRecord={editRecord} />
            )}
          </Spin>
        </Content>
      </Fragment>
    );
  }
}
