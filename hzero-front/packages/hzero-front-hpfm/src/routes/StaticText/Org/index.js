/**
 * 静态文本管理 列表
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { Popconfirm, Table, Tag } from 'hzero-ui';
import querystring from 'querystring';
import { find } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentOrganizationId, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import SearchForm from './SearchForm';

function exTreeKey(list, exList) {
  if (list) {
    for (let i = 0; i < list.length; i++) {
      exList.push(list[i].textId);
      if (list[i].children) {
        exTreeKey(list[i].children, exList);
      }
    }
  }
}

const checkboxDisabledProps = {
  disabled: true,
};

const checkboxEmptyProps = {};

@connect(({ loading, staticTextOrg }) => {
  return {
    fetchStaticTextListLoading: loading.effects['staticTextOrg/fetchStaticTextList'],
    removeStaticTextListLoading: loading.effects['staticTextOrg/removeStaticTextList'],
    removeStaticTextOneLoading: loading.effects['staticTextOrg/removeStaticTextOne'],
    staticText: staticTextOrg.list,
    organizationId: getCurrentOrganizationId(),
    isTenant: isTenantRoleLevel(),
  };
})
@formatterCollections({ code: ['entity.company', 'entity.lang', 'hpfm.staticText'] })
export default class StaticTextOrg extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      rowSelection: {
        onChange: this.handleRowSelectionChange,
        onSelectAll: this.handleRowSelectionChange.bind(this, 'all_none'),
        getCheckboxProps: this.getCheckboxProps,
        selectedRowKeys: [],
      },
      expandedList: [],
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { dataSource } = nextProps.staticText;
    const { prevDataSource } = prevState;
    if (dataSource !== prevDataSource) {
      return {
        dataSource,
        prevDataSource: dataSource,
      };
    }
    return null;
  }

  componentDidMount() {
    this.reloadList();
  }

  @Bind()
  getCheckboxProps(record) {
    return record.hitAncestor ? checkboxDisabledProps : checkboxEmptyProps;
  }

  render() {
    const {
      organizationId,
      match,
      staticText: { pagination = false },
      fetchStaticTextListLoading,
      removeStaticTextListLoading,
      removeStaticTextOneLoading,
    } = this.props;
    const { selectedRows = [], rowSelection = null, dataSource = [] } = this.state;
    const removeBtnDisabled = selectedRows.length === 0;
    const removeBtnLoading = removeStaticTextListLoading || removeStaticTextOneLoading;
    const tableLoading = removeBtnLoading || fetchStaticTextListLoading;
    const columns = [
      {
        title: intl.get('hpfm.staticText.model.staticText.title').d('标题'),
        width: 200,
        dataIndex: 'title',
      },
      {
        title: intl.get('hpfm.staticText.model.staticText.code').d('编码'),
        dataIndex: 'textCode',
      },
      {
        title: intl.get('hpfm.staticText.model.staticText.description').d('描述'),
        width: 150,
        dataIndex: 'description',
      },
      {
        title: intl.get('entity.company.tag').d('公司'),
        width: 150,
        dataIndex: 'companyName',
      },
      {
        title: intl.get('hzero.common.date.active.from').d('有效日期从'),
        width: 160,
        dataIndex: 'startDate',
      },
      {
        title: intl.get('hzero.common.date.active.to').d('有效日期至'),
        width: 160,
        dataIndex: 'endDate',
      },
      {
        title: intl.get('hzero.common.source').d('来源'),
        align: 'center',
        width: 100,
        render: (_, record) => {
          return organizationId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        fixed: 'right',
        render: (text, record) => {
          const { isTenant } = this.props;
          const actions = [
            {
              key: 'create',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.listCreate`,
                      type: 'button',
                      meaning: '静态文本管理(租户)-列表新建',
                    },
                  ]}
                  onClick={() => {
                    this.handleCreateClick(record);
                  }}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.create').d('新建'),
            },
          ];
          if (isTenant && organizationId === record.tenantId) {
            // 预定义
            actions.push(
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '静态文本管理(租户)-编辑',
                      },
                    ]}
                    onClick={() => {
                      this.handleEditClick(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl
                      .get('hpfm.staticText.view.message.confirm.delete')
                      .d('确定删除该文本吗?')}
                    onConfirm={() => {
                      this.handleRemoveClick(record);
                    }}
                  >
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.delete`,
                          type: 'button',
                          meaning: '静态文本管理(租户)-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
          } else {
            actions.push({
              key: 'view',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.view`,
                      type: 'button',
                      meaning: '静态文本管理(租户)-查看',
                    },
                  ]}
                  onClick={() => {
                    this.handleViewClick(record);
                  }}
                >
                  {intl.get('hzero.common.button.view').d('查看')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.view').d('查看'),
            });
          }
          return operatorRender(actions, record, { limit: 3 });
        },
      },
    ];
    return (
      <React.Fragment>
        <Header
          key="header"
          title={intl.get('hpfm.staticText.view.message.title').d('静态文本管理')}
        >
          <ButtonPermission
            key="create"
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '静态文本管理(租户)-新建',
              },
            ]}
            onClick={this.handleCreateBtnClick}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            key="remove"
            icon="delete"
            disabled={removeBtnDisabled}
            permissionList={[
              {
                code: `${match.path}.button.remove`,
                type: 'button',
                meaning: '静态文本管理(租户)-移除',
              },
            ]}
            onClick={this.handleRemoveBtnClick}
            loading={removeBtnLoading}
          >
            {intl.get('hzero.common.button.remove').d('移除')}
          </ButtonPermission>
        </Header>
        <Content key="content">
          <SearchForm
            key="searchForm"
            onRef={this.handleSearchFormRef}
            onSearch={this.handleFetchList}
          />
          <Table
            bordered
            key="table"
            rowKey="textId"
            uncontrolled
            expandedRowKeys={this.state.expandedList}
            rowSelection={rowSelection}
            dataSource={dataSource}
            pagination={pagination}
            loading={tableLoading}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            onChange={this.handleTableChange}
          />
        </Content>
      </React.Fragment>
    );
  }

  @Bind()
  handleCreateBtnClick() {
    this.handleGotoDetail('create');
  }

  @Bind()
  handleGotoDetail(action, params) {
    const { dispatch } = this.props;
    dispatch({
      type: 'staticTextOrg/clearDetail',
    });
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/static-text-org/detail/${action}`,
        search: querystring.stringify(params),
      })
    );
  }

  @Bind()
  handleRemoveBtnClick() {
    const { selectedRows = [] } = this.state;
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'staticTextOrg/removeStaticTextList',
      payload: {
        organizationId,
        params: selectedRows,
      },
    }).then(res => {
      if (res) {
        this.handleFetchList();
      }
    });
  }

  @Bind()
  handleSearchFormRef(searchFormRef) {
    this.searchFormRef = searchFormRef;
  }

  @Bind()
  handleFetchList(pagination = {}) {
    this.setState({ pagination });
    this.handleRowSelectionChange(); // 清空选中的数据
    const { organizationId, dispatch } = this.props;
    let params = {};
    const exList = [];
    if (this.searchFormRef) {
      params = this.searchFormRef.props.form.getFieldsValue();
    }
    dispatch({
      type: 'staticTextOrg/fetchStaticTextList',
      payload: {
        organizationId,
        params: {
          ...pagination,
          ...params,
        },
      },
    }).then(res => {
      const { title = '', textCode = '' } = params;
      if ((title || textCode) && res && res.content && Array.isArray(res.content)) {
        exTreeKey(res.content, exList);
        this.setState({ expandedList: exList });
      } else {
        this.setState({ expandedList: [] });
      }
    });
  }

  @Bind()
  reloadList() {
    const { pagination } = this.state;
    this.handleFetchList(pagination);
  }

  @Bind()
  handleRemoveOne(record) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'staticTextOrg/removeStaticTextOne',
      payload: {
        organizationId,
        params: [record],
      },
    }).then(res => {
      if (res) {
        this.handleFetchList();
      }
    });
  }

  // table

  @Bind()
  handleCreateClick(record) {
    this.handleGotoDetail('create', {
      parentId: record.textId,
      parentTitle: record.title,
      parentDescription: record.description,
      parentTextCode: record.textCode,
    });
  }

  @Bind()
  handleViewClick(record) {
    this.handleGotoDetail('view', { textId: record.textId });
  }

  @Bind()
  handleEditClick(record) {
    this.handleGotoDetail('edit', { textId: record.textId });
  }

  @Bind()
  handleRemoveClick(record) {
    this.handleRemoveOne(record);
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleFetchList({ page, sort });
  }

  /**
   * 遍历树结构的数据
   * @param {object[]} ds - 数据
   * @param {boolean} hit - 是否命中 选中, 由本函数自动传参
   * @param {{hitFunc: Function, travelFunc: Function, travelDeep: boolean}} options - 配置
   */
  @Bind()
  travelDataSource(ds, hit = false, options) {
    const { hitFunc, travelFunc, travelDeep = false } = options;
    const newDs = [];
    if (ds) {
      ds.forEach(r => {
        const newR = { ...r };
        newDs.push(newR);
        if (hitFunc(newR)) {
          newR.children = this.travelDataSource(r.children, true, options);
          newR.hitAncestor = travelFunc(newR, { hit: true, hitParent: hit });
          return;
        }
        if (travelDeep) {
          newR.children = this.travelDataSource(r.children, hit, options);
          newR.hitAncestor = travelFunc(newR, { hit: false, hitParent: hit });
          return;
        }
        if (hit) {
          newR.children = this.travelDataSource(r.children, false, options);
          newR.hitAncestor = travelFunc(newR, { hit: false, hitParent: true });
        } else {
          newR.children = this.travelDataSource(r.children, false, options);
          newR.hitAncestor = travelFunc(newR, { hit: false, hitParent: false });
        }
      });
    }
    if (newDs.length === 0) {
      return undefined;
    }
    return newDs;
  }

  @Bind()
  handleRowSelectionChange(selectStatus, selectedRows = []) {
    // TODO: 要做复杂的判断
    const { rowSelection, dataSource = [] } = this.state;
    const prevRowSelection = rowSelection || {
      onChange: this.handleRowSelectionChange,
      onSelectAll: this.handleRowSelectionChange.bind(this, 'all_none'),
      getCheckboxProps: this.getCheckboxProps,
    };
    const { selectedRowKeys: preSelectedRowKeys = [] } = prevRowSelection;
    const diffObj = {
      diffLen: selectedRows.length - preSelectedRowKeys.length,
    };

    const nextPartialState = {
      rowSelection: {
        ...prevRowSelection,
        selectedRowKeys: [],
        onChange: this.handleRowSelectionChange,
        onSelectAll: this.handleRowSelectionChange.bind(this, 'all_none'),
        getCheckboxProps: this.getCheckboxProps,
      },
      selectedRows: [],
    };

    if (selectStatus === 'all_none') {
      // 全选或者取消全选
      if (selectedRows) {
        diffObj.selectAllStatus = 'all';
      } else {
        diffObj.selectAllStatus = 'none';
      }
    }

    // 选中项目新增
    // 如果是选中的父节点, 子节点对应选中
    // 新增额外的
    nextPartialState.dataSource = this.travelDataSource(dataSource, false, {
      hitFunc(record) {
        if (diffObj.selectAllStatus === 'all') {
          return true;
        }
        if (diffObj.selectAllStatus === 'none') {
          return false;
        }
        return !!find(selectedRows, r => r.textId === record.textId);
      },
      travelFunc(record, { hit, hitParent }) {
        if (hit || hitParent) {
          nextPartialState.selectedRows.push(record);
          nextPartialState.rowSelection.selectedRowKeys.push(record.textId);
        }
        return !!hitParent;
      },
      travelDeep: true,
    });

    this.setState(nextPartialState);
  }
}
