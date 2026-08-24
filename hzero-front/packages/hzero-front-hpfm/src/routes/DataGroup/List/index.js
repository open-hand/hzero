/**
 * DataGroup- 数据组管理
 * @date: 2019-7-11
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { routerRedux } from 'dva/router';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { filterNullValueObject } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 数据组管理
 * @extends {Component} - Component
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} dataAuditConfig - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ dataGroup, loading }) => ({
  dataGroup,
  loading: loading.effects['dataGroup/fetchDataGroupList'],
  createLoading: loading.effects['dataGroup/createDataGroup'],
}))
@formatterCollections({ code: ['hpfm.dataGroup', 'hpfm.permission'] })
export default class DataGroup extends Component {
  state = {
    modalVisible: false,
  };

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      dataGroup: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
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
      type: 'dataGroup/fetchDataGroupList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 编辑
   * @param {number} entityId - 实体ID
   */
  @Bind()
  handleEdit(groupId) {
    const {
      dispatch,
      location: { search, pathname },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    dispatch(
      routerRedux.push({
        pathname:
          pathname.indexOf('/private') === 0
            ? `/private/hpfm/data-group/detail/${groupId}`
            : `/hpfm/data-group/detail/${groupId}`,
        search: pathname.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
      })
    );
  }

  /**
   * 显示弹窗
   */
  @Bind()
  handleShowModal() {
    this.setState({ modalVisible: true });
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
   * 创建数据组
   * @param {object} fieldsValue - 表单值
   */
  @Bind()
  handleCreate(fieldsValue) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataGroup/createDataGroup',
      payload: { ...fieldsValue },
    }).then(res => {
      if (res) {
        this.handleClose();
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleClose() {
    this.setState({ modalVisible: false });
  }

  render() {
    const {
      dataGroup: { dataGroupList = [], pagination = {} },
      match,
      loading,
      createLoading,
    } = this.props;
    const { modalVisible } = this.state;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };

    const listProps = {
      dataSource: dataGroupList,
      pagination,
      loading,
      match,
      onChange: this.handleSearch,
      onEdit: this.handleEdit,
    };
    const drawProps = {
      modalVisible,
      loading: createLoading,
      onOk: this.handleCreate,
      onCancel: this.handleClose,
    };
    return (
      <>
        <Header title={intl.get('hpfm.dataGroup.view.message.title').d('数据组管理')}>
          <ButtonPermission
            type="primary"
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '数据组管理-新建',
              },
            ]}
            onClick={this.handleShowModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <Drawer {...drawProps} />
        </Content>
      </>
    );
  }
}
