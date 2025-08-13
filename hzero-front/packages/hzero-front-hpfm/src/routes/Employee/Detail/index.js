/**
 * Detail - 员工定义/员工信息明细
 * @date: 2018-7-30
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component, Fragment } from 'react';
import { Form, Spin, Tabs } from 'hzero-ui';
import { connect } from 'dva';
import { includes, isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { createPagination, getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { WithCustomize } from 'components/Customize';

import DataForm from './DataForm';
import PositionList from './PositionList';
import PositionModal from './PositionModal';
import UserList from './UserList';
import UserModal from './UserModal';

/**
 * 业务组件 - 员工明细
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} employee - 数据源
 * @reactProps {!boolean} loading - 信息加载是否完成
 * @reactProps {!String} tenantId - 租户ID
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@WithCustomize({
  unitCode: [
    'HPFM.EMPLOYEE_DETAIL.HEADER',
    'HPFM.EMPLOYEE_DETAIL.USER_LINE',
    'HPFM.EMPLOYEE_DETAIL.POST_LINE',
    'HPFM.EMPLOYEE_DETAIL.TABPANE',
  ],
})
@connect(({ employee, loading }) => ({
  employee,
  positionLoading: loading.effects['employee/fetchPosition'],
  userLoading: loading.effects['employee/fetchUser'],
  saveLoading: loading.effects['employee/saveData'],
  deleteUserLoading: loading.effects['employee/deleteUser'],
  searchDetailLoading: loading.effects['employee/fetchDetail'],
  searchUserLoading: loading.effects['employee/searchUser'],
  searchPositionLoading: loading.effects['employee/searchPosition'],
  updateUserLoading: loading.effects['employee/updateUser'],
  tenantId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.employee', 'entity.employee', 'entity.user'] })
export default class Detail extends Component {
  /**
   * state初始化
   * @param {Object} props
   */
  constructor(props) {
    super(props);
    this.state = {
      selectedPositionKeys: [], // 选中已分配岗位标识
      selectedUserKeys: [], // 选中已分配用户标识
      availableUserKeys: [], // 选中可分配用户标识
      positionVisible: false,
      userVisible: false,
    };
  }

  /**
   * componentDidMount
   * render调用后获取展示数据
   */
  componentDidMount() {
    const { dispatch, tenantId, match } = this.props;
    // todo 是否需要优化, 查询值集
    dispatch({
      type: 'employee/fetchEnum',
    });
    dispatch({
      type: 'employee/fetchDetail',
      payload: {
        tenantId,
        customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.HEADER',
        employeeId: match.params.employeeId,
      },
    });
    dispatch({
      type: 'employee/fetchPosition',
      payload: {
        tenantId,
        customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.POST_LINE',
        employeeId: match.params.employeeId,
      },
    });
    dispatch({
      type: 'employee/fetchUser',
      payload: {
        tenantId,
        customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.USER_LINE',
        employeeId: match.params.employeeId,
      },
    });
  }

  /**
   * 根据节点路径，在树形结构树中的替换对应节点
   * @param {Array} collections 树形结构树
   * @param {Array} cursorList 节点路径
   * @param {Array} data - 节点信息
   * @returns {Array} 新的树形结构
   */
  findAndSetNodeProps(collections, cursorList = [], data) {
    let newCursorList = cursorList;
    const cursor = newCursorList[0];

    return collections.map((n) => {
      let m = n;
      if (m.typeWithId === cursor) {
        if (newCursorList[1]) {
          newCursorList = newCursorList.filter((o) => newCursorList.indexOf(o) !== 0);
          m.children = m.children ? this.findAndSetNodeProps(m.children, newCursorList, data) : [];
        } else {
          m = data;
        }
      }
      return m;
    });
  }

  /**
   * 员工信息维护-保存
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      tenantId,
      form,
      match,
      employee: { employeeInfo = {}, positionList = [] },
    } = this.props;
    // 校验：如果设置了岗位信息，则必须有一个主岗
    const primaryPosition = positionList.find((item) => item.primaryPositionFlag === 1);

    if (positionList.length !== 0 && isUndefined(primaryPosition)) {
      notification.warning({
        message: intl.get('hpfm.employee.view.message.primary.need').d('一个员工必须维护一个主岗'),
      });
      return;
    }

    form.validateFields((err, values) => {
      if (!err) {
        const temp = values;
        temp.entryDate = temp.entryDate && temp.entryDate.format(DEFAULT_DATE_FORMAT);
        // 校验通过，进行保存操作
        dispatch({
          type: 'employee/saveData',
          payload: {
            tenantId,
            customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.HEADER',
            saveData: { ...employeeInfo, ...temp },
            positionId: (primaryPosition || {}).positionId,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            dispatch({
              type: 'employee/fetchDetail',
              payload: {
                tenantId,
                customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.HEADER',
                employeeId: match.params.employeeId,
              },
            });
            dispatch({
              type: 'employee/fetchPosition',
              payload: {
                tenantId,
                customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.POST_LINE',
                employeeId: match.params.employeeId,
              },
            });
          }
        });
      }
    });
  }

  /**
   * 添加岗位
   */
  @Bind()
  handleAddPosition() {
    const { dispatch, tenantId, match } = this.props;
    dispatch({
      type: 'employee/searchPosition',
      payload: {
        tenantId,
        employeeId: match.params.employeeId,
      },
    });
    this.setState({ positionVisible: true });
  }

  /**
   * 移除已维护岗位
   */
  @Bind()
  handleDeletePosition() {
    const { selectedPositionKeys } = this.state;
    if (selectedPositionKeys.length !== 0) {
      const {
        dispatch,
        tenantId,
        match,
        employee: { positionList = [] },
      } = this.props;
      // 移除岗位，不做是否存在主岗校验
      const newPositions = positionList.filter((i) => !selectedPositionKeys.includes(i.positionId));
      dispatch({
        type: 'employee/updatePosition',
        payload: {
          tenantId,
          employeeId: match.params.employeeId,
          positionList: newPositions,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.setState({ selectedPositionKeys: [] });
          dispatch({
            type: 'employee/fetchPosition',
            payload: {
              tenantId,
              customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.POST_LINE',
              employeeId: match.params.employeeId,
              positions: [],
              expandedRowKeys: [],
            },
          });
        }
      });
    }
  }

  /**
   * 已分配岗位信息选择操作
   * @param {array<string>} selectedRowKeys - 岗位唯一标识列表
   */
  @Bind()
  handleSelectPosition(selectedRowKeys) {
    this.setState({ selectedPositionKeys: selectedRowKeys });
  }

  /**
   * 关闭岗位层次结构Model
   */
  @Bind()
  handlePositionModalCancel() {
    const { dispatch } = this.props;
    dispatch({
      type: 'employee/updateState',
      payload: {
        positions: [],
        expandedRowKeys: [],
      },
    });
    this.setState({ positionVisible: false });
  }

  /**
   * 岗位信息变更保存
   */
  @Bind()
  handlePositionModalOk() {
    const { dispatch, tenantId, match, employee } = this.props;
    const { employeeId } = match.params;
    const { positions = [], positionList = [] } = employee;
    // 获取所有assignFlag为1的岗位实体
    const newPositions = [];

    function findPosition(collections, emp, tenant) {
      collections.map((item) => {
        if (item.type === 'P' && item.assignFlag === 1) {
          newPositions.push({
            employeeId: emp,
            tenantId: tenant,
            positionId: item.id,
            primaryPositionFlag: 0,
            unitId: item.unitId,
            enabledFlag: 1,
          });
        }
        if (item.children) {
          findPosition(item.children, emp, tenant);
        }
        return newPositions;
      });
      return newPositions;
    }

    findPosition(positions, employeeId, tenantId);
    let newPositionList = [];
    if (isEmpty(newPositions)) {
      newPositionList = [...newPositions];
    } else {
      // 主岗ID
      const primaryPositionId = (positionList.find((item) => item.primaryPositionFlag === 1) || {})
        .positionId;
      // 如果主岗ID不存在时，更新主岗信息，默认第一个岗位为主岗
      if (isUndefined(primaryPositionId)) {
        newPositionList = newPositions.map((item, index) =>
          index === 0 ? { ...item, primaryPositionFlag: 1 } : item
        );
      } else {
        newPositionList = newPositions.map((item) =>
          item.positionId === primaryPositionId ? { ...item, primaryPositionFlag: 1 } : item
        );
      }
    }
    dispatch({
      type: 'employee/updatePosition',
      payload: {
        tenantId,
        employeeId,
        positionList: newPositionList,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        dispatch({
          type: 'employee/fetchPosition',
          payload: {
            tenantId,
            customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.POST_LINE',
            employeeId: match.params.employeeId,
            positions: [],
            expandedRowKeys: [],
          },
        });
        this.setState({ positionVisible: false });
      }
    });
  }

  /**
   * 更新岗位层次结构
   * @param {Object} record - 岗位信息
   */
  @Bind()
  handleChangeTree(record) {
    const {
      dispatch,
      employee: { positions, pathMap },
    } = this.props;
    const newTree = this.findAndSetNodeProps(positions, pathMap[record.typeWithId], record);
    dispatch({
      type: 'employee/updateState',
      payload: {
        positions: newTree,
      },
    });
  }

  /**
   * 岗位Tree查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handlePositionModalSearch(fields = {}) {
    const { dispatch, tenantId, match } = this.props;
    dispatch({
      type: 'employee/searchPosition',
      payload: {
        tenantId,
        employeeId: match.params.employeeId,
        ...fields,
      },
    });
  }

  /**
   * 展开全部
   */
  @Bind()
  handleExpand() {
    const {
      dispatch,
      employee: { pathMap = {} },
    } = this.props;
    dispatch({
      type: 'employee/updateState',
      payload: {
        expandedRowKeys: Object.keys(pathMap),
      },
    });
  }

  /**
   * 收起全部
   */
  @Bind()
  handleShrink() {
    const { dispatch } = this.props;
    dispatch({
      type: 'employee/updateState',
      payload: { expandedRowKeys: [] },
    });
  }

  /**
   * 点击展开图标，展开行
   *  @param {Boolean} isExpand 展开标记
   *  @param {Object} record 岗位信息
   */
  @Bind()
  handleExpandSubLine(isExpand, record = {}) {
    const {
      dispatch,
      employee: { expandedRowKeys = [] },
    } = this.props;
    // isExpand ? 展开 : 收起
    const rowKeys = isExpand
      ? [...expandedRowKeys, record.typeWithId]
      : expandedRowKeys.filter((item) => item !== record.typeWithId);
    dispatch({
      type: 'employee/updateState',
      payload: {
        expandedRowKeys: [...rowKeys],
      },
    });
  }

  /**
   * 维护主岗信息
   * @param {object} record - 岗位信息
   * @param {boolean} flag - 主岗标记
   */
  @Bind()
  handleEditPrimaryFlag(record = {}, flag) {
    const {
      dispatch,
      employee: { positionList = [] },
    } = this.props;
    let newPositionList = [];
    if (flag) {
      // 将岗位设定为主岗
      newPositionList = positionList.map((i) =>
        i.positionId === record.positionId
          ? { ...i, primaryPositionFlag: 1 }
          : { ...i, primaryPositionFlag: 0 }
      );
    } else {
      // 取消岗位的主岗标记
      newPositionList = positionList.map((i) =>
        i.positionId === record.positionId ? { ...i, primaryPositionFlag: 0 } : { ...i }
      );
    }
    dispatch({
      type: 'employee/updateState',
      payload: {
        positionList: newPositionList,
      },
    });
  }

  /**
   * 添加用户
   */
  @Bind()
  handleAddUser() {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'employee/searchUser',
      payload: { tenantId },
    });
    this.setState({ userVisible: true });
  }

  /**
   * 已分配用户行选择操作
   * @param {array<string>} selectedRowKeys - 用户唯一标识列表
   */
  @Bind()
  handleSelectUser(selectedRowKeys) {
    this.setState({ selectedUserKeys: selectedRowKeys });
  }

  /**
   * 移除已分配的用户
   */
  @Bind()
  handleDeleteUser() {
    const { selectedUserKeys } = this.state;
    if (selectedUserKeys.length !== 0) {
      const {
        dispatch,
        tenantId,
        match,
        employee: { userList = [] },
      } = this.props;
      dispatch({
        type: 'employee/deleteUser',
        payload: {
          tenantId,
          data: userList.filter((item) => includes(selectedUserKeys, item.employeeUserId)),
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.setState({ selectedUserKeys: [] });
          dispatch({
            type: 'employee/fetchUser',
            payload: {
              tenantId,
              customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.USER_LINE',
              employeeId: match.params.employeeId,
            },
          });
        }
      });
    }
  }

  /**
   * 取消按钮-分配用户Modal
   */
  @Bind()
  handleUserModalCancel() {
    this.setState({ userVisible: false, availableUserKeys: [] });
  }

  /**
   * 确认按钮-用户分配Modal
   * @prop {List<Object>} userList - 用户信息类表
   */
  @Bind()
  handleUserModalOk() {
    const { availableUserKeys } = this.state;
    if (availableUserKeys.length !== 0) {
      const {
        dispatch,
        tenantId,
        match,
        employee: { users },
      } = this.props;
      const userData = users.content
        .filter((i) => includes(availableUserKeys, i.id))
        .map((item) => ({
          ...item,
          tenantId,
          userId: item.id,
          employeeId: match.params.employeeId,
        }));
      dispatch({
        type: 'employee/updateUser',
        payload: {
          tenantId,
          data: userData,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          dispatch({
            type: 'employee/fetchUser',
            payload: {
              tenantId,
              customizeUnitCode: 'HPFM.EMPLOYEE_DETAIL.USER_LINE',
              employeeId: match.params.employeeId,
            },
          });
          this.setState({ userVisible: false, availableUserKeys: [] });
        }
      });
    }
  }

  /**
   * 可添加用户行选择
   * @param {array<string>} selectedRowKeys - 用户标识列表
   */
  @Bind()
  handleUserModalChange(selectedRowKeys) {
    this.setState({ availableUserKeys: selectedRowKeys });
  }

  /**
   * 数据查询-分配用户Modal
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleUserModalSearch(fields = {}) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'employee/searchUser',
      payload: {
        tenantId,
        ...fields,
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      userVisible = false,
      positionVisible = false,
      selectedUserKeys = [],
      selectedPositionKeys = [],
      availableUserKeys = [],
    } = this.state;
    const {
      form,
      employee,
      match,
      positionLoading = false,
      userLoading = false,
      saveLoading = false,
      searchDetailLoading = false,
      searchUserLoading = false,
      searchPositionLoading = false,
      updateUserLoading = false,
      deleteUserLoading = false,
      toolboxButton,
      customizeForm,
      customizeTable,
      customizeTabPane,
      custLoading,
    } = this.props;
    const {
      employeeInfo = {},
      positionList = [],
      userList = [],
      positions = [],
      users = {},
      expandedRowKeys = [],
      lov: { employeeStatus = [] },
    } = employee;
    // 员工信息明细Props
    const detailProps = {
      form,
      employeeInfo,
      employeeStatus,
      customizeForm,
    };
    // 已分配岗位信息列表
    const positionProps = {
      custLoading,
      selectedRowKeys: selectedPositionKeys,
      loading: positionLoading,
      match,
      dataSource: positionList,
      onAdd: this.handleAddPosition,
      onDelete: this.handleDeletePosition,
      onChange: this.handleSelectPosition,
      onEditPrimary: this.handleEditPrimaryFlag,
      employeeInfo,
      customizeTable,
    };
    // 已分配用户信息列表
    const userProps = {
      custLoading,
      selectedRowKeys: selectedUserKeys,
      loading: userLoading,
      match,
      dataSource: userList,
      onAdd: this.handleAddUser,
      onDelete: this.handleDeleteUser,
      onChange: this.handleSelectUser,
      deleteUserLoading,
      customizeTable,
      employeeInfo,
    };
    // 岗位维护Modal
    const positionModalProps = {
      expandedRowKeys,
      loading: searchPositionLoading,
      employeeName: employeeInfo.name,
      visible: positionVisible,
      dataSource: positions,
      onCancel: this.handlePositionModalCancel,
      onOk: this.handlePositionModalOk,
      onChange: this.handleChangeTree,
      onShowSubLine: this.handleExpandSubLine,
      onExpand: this.handleExpand,
      onShrink: this.handleShrink,
      onSearch: this.handlePositionModalSearch,
    };
    // 用户分配Modal
    const userModalProps = {
      loading: searchUserLoading,
      selectedRowKeys: availableUserKeys,
      visible: userVisible,
      dataSource: users.content || [],
      pagination: createPagination(users),
      onSearch: this.handleUserModalSearch,
      onCancel: this.handleUserModalCancel,
      onOk: this.handleUserModalOk,
      onChange: this.handleUserModalChange,
      updateUserLoading,
    };
    // const { FlexFieldsButton } = flexFieldsMiddleware;
    return (
      <Fragment>
        <Header
          title={intl.get('hpfm.employee.view.message.title.detail').d('员工明细')}
          backPath="/hpfm/hr/staff/list"
        >
          <ButtonPermission
            icon="save"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '员工定义明细-保存',
              },
            ]}
            onClick={this.handleSave}
            loading={saveLoading}
            disabled={searchDetailLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          {toolboxButton}
        </Header>
        <Content>
          <Spin spinning={searchDetailLoading}>
            <DataForm {...detailProps} />
          </Spin>
          {customizeTabPane(
            { code: 'HPFM.EMPLOYEE_DETAIL.TABPANE' },
            <Tabs defaultActiveKey="user" animated={false}>
              <Tabs.TabPane
                tab={intl.get('hpfm.employee.view.message.tab.user').d('用户信息')}
                key="user"
              >
                <UserList {...userProps} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hpfm.employee.view.message.tab.position').d('岗位信息')}
                key="position"
              >
                <PositionList {...positionProps} />
              </Tabs.TabPane>
            </Tabs>
          )}
          <PositionModal {...positionModalProps} />
          <UserModal {...userModalProps} />
        </Content>
      </Fragment>
    );
  }
}
