/**
 * ReceiverType - 接收者类型维护
 * @date: 2018-7-31
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { isTenantRoleLevel, getEditTableData } from 'utils/utils';

import ListTable from './ListTable';
import Drawer from './Drawer';
import SearchForm from './SearchForm';

/**
 * 接收者类型维护
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} receiverType - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ receiverType, loading }) => ({
  receiverType,
  loading: loading.effects['receiverType/fetchReceiverType'],
  saveLoading:
    loading.effects['receiverType/addType'] || loading.effects['receiverType/updateType'],
  queryAssignedListLoading: loading.effects['receiverType/queryAssignedList'],
  assignListToReceiverTypeLoading: loading.effects['receiverType/assignListToReceiverType'],
  removeReceiverTypeListLoading: loading.effects['receiverType/removeReceiverTypeList'],
  queryNoAssignUnitListLoading: loading.effects['receiverType/queryNoAssignUnitList'],
  queryNoAssignUserGroupListLoading: loading.effects['receiverType/queryNoAssignUserGroupList'],
}))
@formatterCollections({ code: ['hmsg.receiverType', 'entity.tenant'] })
export default class ReceiverType extends Component {
  /**
   * 初始化state
   */
  constructor(props) {
    super(props);
    this.state = {
      tableRecord: {},
      drawerVisible: false,
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render()执行后获取页面数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'receiverType/init',
    });
    dispatch({
      type: 'receiverType/initExtType',
    });
    this.handleSearch();
  }

  /**
   * 新增按钮，右侧滑窗显示
   */
  @Bind()
  handleAddType() {
    this.setState({ tableRecord: {}, drawerVisible: true });
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
  handleSearch(fields = {}, query) {
    const { dispatch } = this.props;
    dispatch({
      type: 'receiverType/fetchReceiverType',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...query,
      },
    });
  }

  /**
   * 行内编辑，右侧滑窗显示
   * @param {object} record - 接收者类型对象
   */
  @Bind()
  handleEditContent(record) {
    this.setState({ tableRecord: record, drawerVisible: true });
  }

  // /**
  //  * 编辑 组织
  //  * @param {object} record
  //  */
  // @Bind()
  // handleEditUnit(record) {
  //   this.setState({
  //     unitEditRecord: record,
  //     unitEditDrawerVisible: true,
  //   });
  // }

  // /**
  //  * 编辑 用户组
  //  * @param {object} record
  //  */
  // @Bind()
  // handleEditUserGroup(record) {
  //   this.setState({
  //     userGroupEditRecord: record,
  //     userGroupEditDrawerVisible: true,
  //   });
  // }

  /**
   * 滑窗保存
   * @param {Object} values  - 修改后的数据
   */
  @Bind()
  handleDrawerOk(values) {
    const {
      dispatch,
      receiverType: { pagination = {}, assignDataSource = [] },
    } = this.props;
    const { tableRecord = {} } = this.state;
    const { receiverTypeId, typeModeCode } = tableRecord;
    const validData = assignDataSource.filter(
      (item) => item.receiverTypeLineId && item.receiverTypeLineId.toString().startsWith('create-')
    );
    const data = getEditTableData(validData);
    let type = 'receiverType/addType'; // 默认新增
    if (values.receiverTypeId) {
      type = 'receiverType/updateType'; // 修改
    }
    /**
     * 类型为外部用户时，需要在确定时调两次接口，一次保存头，一次保存行，优先处理保存行
     */
    if (typeModeCode === 'EXT_USER') {
      if (values.receiverTypeId) {
        if ((Array.isArray(data) && data.length > 0) || !validData) {
          // 含有新建的数据，并且校验通过了，调保存行的接口
          this.assignListToReceiverType({
            records: data.map((item) => {
              return {
                receiverDetail: {
                  accountNum: item.accountNum,
                  accountTypeCode: item.accountTypeCode,
                  description: item.description,
                  receiverTypeId,
                },
                receiverTypeId,
                receiverTargetCode: typeModeCode,
              };
            }),
            id: values.receiverTypeId,
          }).then((r) => {
            if (r) {
              // 行保存成功后保存头
              dispatch({
                type,
                payload: {
                  ...values,
                },
              }).then((res) => {
                if (res) {
                  notification.success();
                  this.handleDrawerCancel();
                  this.handleSearch(pagination);
                }
              });
            }
          });
        } else if ((data && data.length) === (validData && validData.length)) {
          // 不含有新建数据，单独保存头
          dispatch({
            type,
            payload: {
              ...values,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              this.handleDrawerCancel();
              this.handleSearch(pagination);
            }
          });
        }
      }
    } else {
      // 其他类型，单独保存行
      dispatch({
        type,
        payload: {
          ...values,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleDrawerCancel();
          this.handleSearch(pagination);
        }
      });
    }
  }

  /**
   * 滑窗隐藏
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({ tableRecord: {}, drawerVisible: false });
  }

  // 接收者配置 行数据操作
  @Bind()
  queryAssignedList(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'receiverType/queryAssignedList',
      payload,
    });
  }

  @Bind()
  assignListToReceiverType(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'receiverType/assignListToReceiverType',
      payload,
    });
  }

  @Bind()
  removeReceiverTypeList(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'receiverType/removeReceiverTypeList',
      payload,
    });
  }

  @Bind()
  queryNoAssignUnitList(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'receiverType/queryNoAssignUnitList',
      payload,
    });
  }

  @Bind()
  queryNoAssignUserGroupList(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'receiverType/queryNoAssignUserGroupList',
      payload,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      saveLoading,
      receiverType: {
        list = [],
        pagination = {},
        typeModes,
        newTypeModes,
        extTypeList = [],
        assignDataSource = [],
        assignPagination = {},
      },
      match: { path },
      queryAssignedListLoading,
      assignListToReceiverTypeLoading,
      removeReceiverTypeListLoading,
      queryNoAssignUnitListLoading,
      queryNoAssignUserGroupListLoading,
      dispatch,
      receiverType,
    } = this.props;
    const { tableRecord = {}, drawerVisible = false } = this.state;
    const filterProps = {
      onSearch: this.handleSearchFormSearch,
    };
    const listProps = {
      pagination,
      loading,
      path,
      dataSource: list,
      onChange: this.handleSearch,
      onEditType: this.handleEditContent,
      // onEditUnit: this.handleEditUnit,
      // onEditUserGroup: this.handleEditUserGroup,
    };
    const drawerProps = {
      path,
      tableRecord,
      saveLoading,
      visible: drawerVisible,
      anchor: 'right',
      title: intl.get('hmsg.receiverType.view.message.option').d('接收组维护'),
      onCancel: this.handleDrawerCancel,
      onOk: this.handleDrawerOk,
      // 接收者模式值集
      typeModes: isTenantRoleLevel() ? newTypeModes : typeModes,
      // 接收者模式
      // 接收者模式 行数据 model 相关
      queryAssignedList: this.queryAssignedList,
      queryAssignedListLoading,
      assignListToReceiverType: this.assignListToReceiverType,
      assignListToReceiverTypeLoading,
      removeReceiverTypeList: this.removeReceiverTypeList,
      removeReceiverTypeListLoading,
      queryNoAssignUnitList: this.queryNoAssignUnitList,
      queryNoAssignUnitListLoading,
      queryNoAssignUserGroupList: this.queryNoAssignUserGroupList,
      queryNoAssignUserGroupListLoading,
      extTypeList,
      dispatch,
      assignDataSource,
      assignPagination,
      receiverType,
    };
    return (
      <>
        <Header title={intl.get('hmsg.receiverType.view.message.title').d('接收组维护')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '接收组维护-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.handleAddType}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
