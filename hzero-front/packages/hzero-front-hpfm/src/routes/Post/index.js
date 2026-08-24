/**
 * Post - 部门分配岗位
 * @date: 2018-6-19
 * @author: WH <heng.wei@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component, Fragment } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import uuidv4 from 'uuid/v4';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import queryString from 'query-string';

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
 * 岗位维护组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} post - 数据源
 * @reactProps {!Boolean} loading - 数据加载是否完成
 * @reactProps {!String} tenantId - 租户ID
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ post, loading }) => ({
  post,
  loading: loading.effects['post/fetchPositionData'],
  saveLoading: loading.effects['post/saveData'],
  editLoading: loading.effects['post/saveEdit'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: ['hpfm.position', 'hpfm.common', 'entity.position', 'entity.department'],
})
export default class Post extends Component {
  form;

  /**
   * state初始化
   * @param {object} props 组件Props
   */
  constructor(props) {
    super(props);
    this.state = {
      cacheFormData: {},
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render调用后获取页面数据
   */
  componentDidMount() {
    const { dispatch, match, tenantId } = this.props;
    dispatch({
      type: 'post/updateState',
      payload: {
        unitCode: '',
        unitName: '',
        renderTree: [],
        pathMap: {},
      },
    });
    this.handleSearch();
    dispatch({
      type: 'post/fetchUnitInfo',
      payload: {
        tenantId,
        unitId: match.params.unitId, // 部门Id
      },
    });
  }

  /**
   * 根据节点路径，在树形结构树中的对应节点添加或替换children属性
   * @param {Array} collections 树形结构树
   * @param {Array} cursorList 节点路径
   * @param {Array} data  追加或替换的数据
   * @param {Boolean} flag  操作类型：追加/替换
   * @returns {Array} 新的树形结构
   */
  findAndSetNodeProps(collections, cursorList = [], data) {
    let newCursorList = cursorList;
    const cursor = newCursorList[0];
    return collections.map((n) => {
      const m = n;
      if (m.positionId === cursor) {
        if (newCursorList[1]) {
          if (!m.children) {
            m.children = [];
          }
          newCursorList = newCursorList.filter((o) => newCursorList.indexOf(o) !== 0);
          m.children = this.findAndSetNodeProps(m.children, newCursorList, data);
        } else {
          m.children = data;
        }
        if (m.children.length === 0) {
          const { children, ...others } = m;
          return { ...others };
        }
        return m;
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
  findNode(collection, cursorList, keyName) {
    let newCursorList = cursorList || [];
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
   * 按钮 - 查询
   * @param {object} fields 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId, match } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'post/fetchPositionData',
      payload: {
        tenantId,
        unitId: match.params.unitId, // 部门Id
        ...fieldValues,
        ...fields,
      },
    });
  }

  /**
   *  按钮-添加岗位
   */
  @Bind()
  handleAddPost() {
    const { dispatch, match, post = {} } = this.props;
    const { renderTree = [], addData = {}, expandedRowKeys = [] } = post;
    const positionId = uuidv4();
    const newItem = {
      positionId,
      unitId: match.params.unitId,
      positionCode: '',
      positionName: '',
      orderSeq: '',
      supervisorFlag: 0,
      enabledFlag: 1,
      _status: 'create', // 新增节点的标识
    };
    dispatch({
      type: 'post/updateState',
      payload: {
        ...post,
        renderTree: [newItem, ...renderTree],
        addData: {
          ...addData,
          newItem,
        },
        expandedRowKeys: [...expandedRowKeys, positionId],
      },
    });
  }

  /**
   * 按钮 - 保存
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      tenantId,
      match,
      post: { companyId, expandedRowKeys = [], renderTree = [] },
      location: { search },
    } = this.props;
    const params = getEditTableData(renderTree, ['children', 'positionId']);
    const { fromSource } = queryString.parse(search.substring(1));
    if (Array.isArray(params) && params.length !== 0) {
      dispatch({
        type: 'post/saveData',
        payload: {
          tenantId,
          saveData: params,
          unitId: match.params.unitId,
          unitCompanyId: fromSource === 'company' ? match.params.unitId : companyId,
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
   * 新增岗位-清除
   * @param {Object} record 新增岗位
   */
  @Bind()
  handleCleanLine(record = {}) {
    const {
      dispatch,
      post: { renderTree = [], addData = {}, pathMap = {} },
    } = this.props;
    delete addData[record.positionId];
    let newRenderTree = [];
    if (record.parentPositionId) {
      // 找到父节点的children, 更新children数组
      const parent = this.findNode(renderTree, pathMap[record.parentPositionId], 'positionId');
      const newChildren = parent.children.filter((item) => item.positionId !== record.positionId);
      newRenderTree = this.findAndSetNodeProps(
        renderTree,
        pathMap[record.parentPositionId],
        newChildren
      );
    } else {
      newRenderTree = renderTree.filter((item) => item.positionId !== record.positionId);
    }
    dispatch({
      type: 'post/updateState',
      payload: {
        renderTree: newRenderTree,
        addData: {
          ...addData,
        },
      },
    });
  }

  /**
   * 新增
   * @param {Object} record  操作对象
   */
  @Bind()
  handleAddLine(record = {}) {
    const { dispatch, post, match } = this.props;
    const { renderTree = [], pathMap = {}, addData = {}, expandedRowKeys = [] } = post;
    const positionId = uuidv4();
    const newItem = {
      positionId,
      unitId: match.params.unitId,
      positionCode: '',
      positionName: '',
      orderSeq: '',
      parentPositionId: record.positionId,
      parentPositionName: record.positionName,
      supervisorFlag: 0,
      enabledFlag: 1,
      _status: 'create', // 新增节点的标识
    };
    const newChildren = [newItem, ...(record.children || [])];
    const newRenderTree = this.findAndSetNodeProps(
      renderTree,
      pathMap[record.positionId],
      newChildren
    );

    dispatch({
      type: 'post/updateState',
      payload: {
        renderTree: [...newRenderTree],
        expandedRowKeys: [...expandedRowKeys, record.positionId],
        addData: {
          ...addData,
          newItem,
        },
      },
    });
  }

  /**
   * 禁用
   * @param {Object} record 操作对象
   */
  @Bind()
  handleForbidLine(record = {}) {
    const {
      dispatch,
      tenantId,
      match,
      post: { expandedRowKeys = [], companyId },
      location: { search },
    } = this.props;
    const { fromSource } = queryString.parse(search.substring(1));
    dispatch({
      type: 'post/forbindLine',
      payload: {
        tenantId,
        unitCompanyId: fromSource === 'company' ? match.params.unitId : companyId,
        unitId: match.params.unitId,
        positionId: record.positionId,
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
   * 启用
   * @param {Object} record 操作对象
   */
  @Bind()
  handleEnabledLine(record) {
    const {
      dispatch,
      tenantId,
      match,
      post: { expandedRowKeys = [], companyId },
      location: { search },
    } = this.props;
    const { fromSource } = queryString.parse(search.substring(1));
    dispatch({
      type: 'post/enabledLine',
      payload: {
        tenantId,
        unitCompanyId: fromSource === 'company' ? match.params.unitId : companyId,
        unitId: match.params.unitId,
        positionId: record.positionId,
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
      post: { expandedRowKeys },
    } = this.props;
    const rowKeys = isExpand
      ? [...expandedRowKeys, record.positionId]
      : expandedRowKeys.filter((item) => item !== record.positionId);
    dispatch({
      type: 'post/updateState',
      payload: {
        expandedRowKeys: [...rowKeys],
      },
    });
  }

  /**
   * 分配员工
   * 进行员工分配，跳转到下一级页面
   * @param {Object} record 操作对象
   */
  @Bind()
  handleGotoSubGrade(record = {}) {
    const {
      dispatch,
      location: { search },
    } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/hr/org/staff/${record.positionId}`,
        search,
      })
    );
  }

  /**
   * 保存编辑后的岗位信息
   * @param {Object} record 保存对象
   */
  @Bind()
  handleDrawerOk(record = {}) {
    const {
      dispatch,
      tenantId,
      match,
      post: { expandedRowKeys = [], companyId },
      location: { search },
    } = this.props;
    const { fromSource } = queryString.parse(search.substring(1));
    dispatch({
      type: 'post/saveEdit',
      payload: {
        tenantId,
        unitCompanyId: fromSource === 'company' ? match.params.unitId : companyId,
        unitId: match.params.unitId,
        positionId: record.positionId,
        data: { ...record },
      },
    }).then((res) => {
      if (res) {
        this.setState({ activePostData: {}, drawerVisible: false });
        notification.success();
        this.handleSearch({ expandedRowKeys, expandFlag: true });
      }
    });
  }

  /**
   * 展开全部
   * 将页面展示的数据进行展开
   */
  @Bind()
  handleExpand() {
    const {
      dispatch,
      post: { pathMap = {} },
    } = this.props;
    dispatch({
      type: 'post/updateState',
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
      type: 'post/updateState',
      payload: { expandedRowKeys: [] },
    });
  }

  /**
   * 编辑侧滑款隐藏
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({
      drawerVisible: false,
      activePostData: {},
    });
  }

  /**
   * 更新待编辑的岗位信息
   * @param {Object} record 操作对象
   */
  @Bind()
  handleActiveLine(record) {
    this.setState({
      drawerVisible: true,
      activePostData: record,
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
      post: { renderTree = [] },
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

  @Bind()
  setCacheFormData(data) {
    this.setState(() => ({
      cacheFormData: data,
    }));
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      saveLoading,
      editLoading,
      post = {},
      match,
      location: { search },
    } = this.props;
    const { unitCode, unitName, renderTree = [], expandedRowKeys = [] } = post;
    const { activePostData = {}, drawerVisible = false, cacheFormData } = this.state;
    const filterProps = {
      unitCode,
      unitName,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      setCacheFormData: this.setCacheFormData,
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
      gotoSubGrade: this.handleGotoSubGrade,
      onShowSubLine: this.handleExpandSubLine,
      onEdit: this.handleActiveLine,
      cacheFormData,
    };
    const { fromSource } = queryString.parse(search.substring(1));
    const drawerProps = {
      onCancel: this.handleDrawerCancel,
      onOk: this.handleDrawerOk,
      visible: drawerVisible,
      anchor: 'right',
      title: intl.get('hpfm.position.view.message.edit').d('岗位信息修改'),
      itemData: activePostData,
      loading: editLoading || loading,
    };
    const backPath =
      fromSource === 'company'
        ? '/hpfm/hr/org/company'
        : `/hpfm/hr/org/department/${post.companyId}`;
    return (
      <Fragment>
        <Header
          title={intl.get('hpfm.position.view.message.title').d('部门分配岗位')}
          backPath={backPath}
        >
          <ButtonPermission
            icon="save"
            type="primary"
            onClick={this.handleSave}
            disabled={loading || editLoading || this.getSaveBtnDisabled()}
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
                meaning: '部门分配岗位-新建',
              },
            ]}
            onClick={this.handleAddPost}
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
