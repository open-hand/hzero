/**
 * Deparment - 公司分配部门（部门维度）
 * @date: 2018-6-20
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component, Fragment } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import uuidv4 from 'uuid/v4';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject, getCurrentOrganizationId, getEditTableData } from 'utils/utils';

import FilterForm from './FilterForm';
import DataTable from './DataTable';
import Drawer from './Drawer';

/**
 * 部门维护组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} department - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {!boolean} saveLoading - 数据保存是否完成
 * @reactProps {!boolean} editLoading - 数据更新是否完成
 * @reactProps {!String} tenantId - 租户ID
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ department, loading }) => ({
  department,
  loading: loading.effects['department/searchDepartmentData'],
  saveLoading: loading.effects['department/saveAddData'],
  editLoading: loading.effects['department/saveEditData'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: ['hpfm.department', 'hpfm.common', 'entity.company', 'entity.department'],
})
export default class Department extends Component {
  form;

  /**
   * state初始化
   * @param {object} props 组件Props
   */
  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * componentDidMount 生命周期函数
   * render后请求页面数据
   */
  componentDidMount() {
    const { dispatch, match, tenantId } = this.props;
    dispatch({
      type: 'department/updateState',
      payload: {
        companyCode: '',
        companyName: '',
        renderTree: [],
        pathMap: {},
      },
    });
    this.handleSearch();
    dispatch({
      type: 'department/fetchDepartmentInfo',
      payload: {
        tenantId,
        unitId: match.params.companyId,
      },
    });
  }

  /**
   * 根据节点路径，在树形结构树中的对应节点添加或替换children属性
   * @param {Array} collections 树形结构树
   * @param {Array} cursorList 节点路径
   * @param {Array} data  追加或替换的children数据
   * @returns {Array} 新的树形结构
   */
  findAndSetNodeProps(collections, cursorList = [], data) {
    let newCursorList = cursorList;
    const cursor = newCursorList[0];
    return collections.map((n) => {
      const m = n;
      if (m.unitId === cursor) {
        if (newCursorList[1]) {
          if (!m.children) {
            m.children = [];
          }
          newCursorList = newCursorList.filter((o) => newCursorList.indexOf(o) !== 0);
          m.children = this.findAndSetNodeProps(m.children, newCursorList, data);
        } else {
          m.children = [...data];
        }
        if (m.children.length === 0) {
          const { children, ...others } = m;
          return { ...others };
        } else {
          return m;
        }
      }
      return m;
    });
  }

  /**
   * 根据节点路径，在树形结构树中的对应节点
   * @param {Array} collections 树形结构树
   * @param {Array} cursorList 节点路径
   * @param {String} keyName 主键名称
   * @returns {Object} 节点信息
   */
  findNode(collection, cursorList = [], keyName) {
    let newCursorList = cursorList;
    const cursor = newCursorList[0];
    for (let i = 0; i < collection.length; i++) {
      if (collection[i][keyName] === cursor) {
        if (newCursorList[1]) {
          newCursorList = newCursorList.slice(1);
          return this.findNode(collection[i].children, newCursorList, keyName);
        }
        return collection[i];
      }
    }
  }

  /**
   * 查询框-查询数据
   * 根据输入框的内容进行查询，查询结果直接替换内容树
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId, match } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'department/searchDepartmentData',
      payload: {
        tenantId,
        unitCompanyId: match.params.companyId,
        ...fieldValues,
        ...fields,
      },
    });
  }

  /**
   * 添加部门
   */
  @Bind()
  handleAddUnit() {
    const { dispatch, match, department, tenantId } = this.props;
    const { renderTree = [], addData = {}, expandedRowKeys = [] } = department;
    const unitId = uuidv4();
    const newItem = {
      tenantId,
      unitId,
      unitCode: '', // 部门编码
      unitName: '', // 部门名称
      orderSeq: '',
      enabledFlag: 1,
      supervisorFlag: 0,
      unitTypeCode: 'D',
      unitCompanyId: match.params.companyId,
      parentUnitId: match.params.companyId, // 一级部门的父级部门就是所属公司本身
      _status: 'create', // 新增节点的标识
    };
    dispatch({
      type: 'department/updateState',
      payload: {
        ...department,
        renderTree: [newItem, ...renderTree],
        addData: {
          ...addData,
          newItem,
        },
        expandedRowKeys: [...expandedRowKeys, unitId],
      },
    });
  }

  /**
   * 顶部保存
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      tenantId,
      department: { expandedRowKeys = [], renderTree = [] },
    } = this.props;
    const params = getEditTableData(renderTree, ['children', 'unitId']);
    if (Array.isArray(params) && params.length !== 0) {
      dispatch({
        type: 'department/saveAddData',
        payload: {
          tenantId,
          data: params.map((item) => {
            return { ...item, enableBudgetFlag: item.enableBudgetFlag || 0 };
          }),
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch({ expandedRowKeys, addData: {}, expandFlag: true });
        }
      });
    }
  }

  /**
   * 展开全部
   * 将页面展示的数据进行展开
   */
  @Bind()
  handleExpand() {
    const {
      dispatch,
      department: { pathMap = {} },
    } = this.props;
    dispatch({
      type: 'department/updateState',
      payload: {
        expandedRowKeys: Object.keys(pathMap).map((item) => item),
      },
    });
  }

  /**
   * 收起全部
   * 页面顶部收起全部按钮，将内容树收起
   */
  @Bind()
  handleShrink() {
    const { dispatch } = this.props;
    dispatch({
      type: 'department/updateState',
      payload: { expandedRowKeys: [] },
    });
  }

  /**
   * 添加下级部门
   * @param {Object} record  操作对象
   */
  @Bind()
  handleAddLine(record = {}) {
    const { dispatch, match, department, tenantId } = this.props;
    const { renderTree = [], pathMap = {}, addData = {}, expandedRowKeys = [] } = department;
    const unitId = uuidv4();
    const newItem = {
      tenantId,
      unitId,
      unitCode: '', // 部门编码
      unitName: '', // 部门名称
      orderSeq: '',
      enabledFlag: 1,
      supervisorFlag: 0,
      unitTypeCode: 'D',
      unitCompanyId: match.params.companyId,
      parentUnitId: record.unitId,
      parentUnitName: record.unitName,
      _status: 'create', // 新增节点的标识
    };
    const newChildren = [...(record.children || []), newItem];
    const newRenderTree = this.findAndSetNodeProps(renderTree, pathMap[record.unitId], newChildren);
    dispatch({
      type: 'department/updateState',
      payload: {
        renderTree: newRenderTree,
        expandedRowKeys: [...expandedRowKeys, record.unitId],
        addData: {
          ...addData,
          newItem,
        },
      },
    });
  }

  /**
   * 新增部门-清除
   * @param {Object} record 新增部门
   */
  @Bind()
  handleCleanLine(record = {}) {
    const {
      dispatch,
      match,
      department: { renderTree = [], addData = {}, pathMap = {} },
    } = this.props;
    delete addData[record.unitId];
    let newRenderTree = [];
    if (record.parentUnitId && record.parentUnitId !== match.params.companyId) {
      // 找到父节点的children, 更新children数组
      const parent = this.findNode(renderTree, pathMap[record.parentUnitId], 'unitId');
      const newChildren = parent.children.filter((item) => item.unitId !== record.unitId);
      newRenderTree = this.findAndSetNodeProps(
        renderTree,
        pathMap[record.parentUnitId],
        newChildren
      );
    } else {
      newRenderTree = renderTree.filter((item) => item.unitId !== record.unitId);
    }
    dispatch({
      type: 'department/updateState',
      payload: {
        renderTree: newRenderTree,
        addData: {
          ...addData,
        },
      },
    });
  }

  /**
   * 禁用特定部门，同时禁用所有下属部门
   * @param {Object} record 操作对象
   */
  @Bind()
  handleForbidLine(record = {}) {
    const {
      dispatch,
      tenantId,
      department: { expandedRowKeys = [] },
    } = this.props;
    dispatch({
      type: 'department/forbidLine',
      payload: {
        tenantId,
        unitId: record.unitId,
        objectVersionNumber: record.objectVersionNumber,
        _token: record._token,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch({ expandedRowKeys, expandFlag: true });
      }
    });
  }

  /**
   * 启用部门，同时启用所有下属组织
   * @param {Object} record 操作对象
   */
  @Bind()
  handleEnabledLine(record = {}) {
    const {
      dispatch,
      tenantId,
      department: { expandedRowKeys = [] },
    } = this.props;
    dispatch({
      type: 'department/enabledLine',
      payload: {
        tenantId,
        unitId: record.unitId,
        objectVersionNumber: record.objectVersionNumber,
        _token: record._token,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch({ expandedRowKeys, expandFlag: true });
      }
    });
  }

  /**
   * 点击展开图标，展开行
   *  @param {Boolean} isExpand 展开标记
   *  @param {Object} record 组织行信息
   */
  @Bind()
  handleExpandSubLine(isExpand, record = {}) {
    const {
      dispatch,
      department: { expandedRowKeys = [] },
    } = this.props;
    const rowKeys = isExpand
      ? [...expandedRowKeys, record.unitId]
      : expandedRowKeys.filter((item) => item !== record.unitId);
    dispatch({
      type: 'department/updateState',
      payload: {
        expandedRowKeys: [...rowKeys],
      },
    });
  }

  /**
   * 分配岗位
   * 进行岗位分配，跳转到下一级页面
   * @param {*} record 操作对象
   */
  @Bind()
  handleGotoSubGrade(record = {}) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/hr/org/post/${record.unitId}`,
      })
    );
  }

  /**
   * 保存 - 单条部门行数据修改后保存
   * @param {Object} values 修改后的数据
   */
  @Bind()
  handleDrawerOk(values) {
    const {
      dispatch,
      tenantId,
      department: { expandedRowKeys = [] },
    } = this.props;
    dispatch({
      type: 'department/saveEditData',
      payload: { values, tenantId },
    }).then((res) => {
      if (res) {
        this.setState({ activeDepData: {}, drawerVisible: false });
        notification.success();
        this.handleSearch({ expandedRowKeys, expandFlag: true });
      }
    });
  }

  /**
   * 编辑侧滑款隐藏
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({
      drawerVisible: false,
      activeDepData: {},
    });
  }

  /**
   * 更新编辑部门信息
   * @param {Object} record 操作对象
   */
  @Bind()
  handleActiveLine(record) {
    this.setState({
      drawerVisible: true,
      activeDepData: record,
    });
  }

  /**
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 获取 保存按钮是否应该禁用
   */
  @Bind()
  getSaveBtnDisabled() {
    const {
      department: { renderTree = [] },
    } = this.props;
    const travel = [...renderTree];
    while (travel.length !== 0) {
      const item = travel.pop();
      if (item._status === 'create') {
        // 只要有新建的数据 那么就不需要禁用 保存
        return false;
      }
      if (item.children) {
        travel.push(...item.children);
      }
    }
    return true;
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, saveLoading, editLoading, department = {}, match } = this.props;
    const { companyCode, companyName, renderTree = [], expandedRowKeys = [] } = department;
    const { activeDepData = {}, drawerVisible = false } = this.state;
    const filterProps = {
      companyCode,
      companyName,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      expandedRowKeys,
      loading,
      match,
      dataSource: renderTree,
      onAddLine: this.handleAddLine,
      onForbidLine: this.handleForbidLine,
      onEnabledLine: this.handleEnabledLine,
      onClearLine: this.handleCleanLine,
      onShowSubLine: this.handleExpandSubLine,
      gotoSubGrade: this.handleGotoSubGrade,
      onEdit: this.handleActiveLine,
    };
    const drawerProps = {
      onCancel: this.handleDrawerCancel,
      onOk: this.handleDrawerOk,
      visible: drawerVisible,
      anchor: 'right',
      title: intl.get('hpfm.department.view.message.edit').d('部门信息修改'),
      itemData: activeDepData,
      loading: editLoading || loading,
    };
    return (
      <Fragment>
        <Header
          title={intl.get('hpfm.department.view.message.title').d('公司分配部门')}
          backPath="/hpfm/hr/org/company"
        >
          <ButtonPermission
            icon="save"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '公司分配部门-保存',
              },
            ]}
            onClick={this.handleSave}
            disabled={editLoading || loading || this.getSaveBtnDisabled()}
            loading={saveLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '公司分配部门-新建',
              },
            ]}
            onClick={this.handleAddUnit}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <Button icon="down" onClick={this.handleExpand}>
            {intl.get('hzero.common.button.expandAll').d('全部展开')}
          </Button>
          <Button icon="up" onClick={this.handleShrink}>
            {intl.get('hzero.common.button.collapseAll').d('全部收起')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <DataTable {...listProps} />
          <Drawer {...drawerProps} />
        </Content>
      </Fragment>
    );
  }
}
