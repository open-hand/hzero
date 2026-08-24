/**
 * LineData - 打平的组织数据
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-12
 * @copyright 2019 © HAND
 */

import React from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

import SearchForm from './SearchForm';
import EditDrawer from '../components/Drawer';

@cacheComponent({ cacheKey: '/hpfm/hr/org/company/line-data/list' })
export default class LineData extends React.Component {
  state = {
    isCreate: true, // 是否是新建
    drawerVisible: false, // 编辑模态框
    editRecord: {}, // 编辑数据
    cachePagination: {}, // 缓存的分页数据
  };

  searchFormRef = React.createRef();

  componentDidMount() {
    this.reload();
  }

  // base

  handleSearch(pagination = {}) {
    let searchParams = {};
    if (this.searchFormRef.current) {
      searchParams = this.searchFormRef.current.props.form.getFieldsValue();
    }
    const { unitsQueryLine } = this.props;
    this.setState({
      cachePagination: pagination,
    });
    unitsQueryLine({
      params: {
        ...pagination,
        ...searchParams,
      },
    });
  }

  reload() {
    const { cachePagination = {} } = this.state;
    this.handleSearch(cachePagination);
  }

  // Button
  @Bind()
  handleCreateBtnClick() {
    this.setState({
      isCreate: true,
      drawerVisible: true,
      editRecord: {},
    });
  }

  // SearchForm

  @Bind()
  handleSearchFormSearch() {
    this.handleSearch();
  }

  // EditDrawer

  /**
   * 保存 - 单条组织行数据修改后保存
   * @param {Object} values 修改后的数据
   */
  @Bind()
  handleDrawerOk(values) {
    const { saveEditData, saveAddData, organizationId } = this.props;
    const { isCreate } = this.state;
    if (isCreate) {
      //  新建
      saveAddData({
        tenantId: organizationId,
        data: [
          {
            ...values,
            tenantId: organizationId,
            enabledFlag: 1,
          },
        ],
      }).then((res) => {
        if (res) {
          this.setState({
            isCreate: true,
            drawerVisible: false,
            editRecord: {},
          });
          this.reload();
          notification.success();
        }
      });
    } else {
      saveEditData({
        tenantId: organizationId,
        values,
      }).then((res) => {
        if (res) {
          this.setState({
            isCreate: true,
            drawerVisible: false,
            editRecord: {},
          });
          this.reload();
          notification.success();
        }
      });
    }
  }

  /**
   * 编辑侧滑框隐藏
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({
      isCreate: true,
      drawerVisible: false,
      editRecord: {},
    });
  }

  // Table
  getColumns() {
    const { match } = this.props;
    return [
      {
        dataIndex: 'unitCode',
        title: intl.get('entity.organization.code').d('组织编码'),
        width: 300,
      },
      {
        dataIndex: 'unitName',
        title: intl.get('entity.organization.name').d('组织名称'),
      },
      {
        dataIndex: 'nameLevelPaths',
        title: intl.get('hpfm.organization.model.unit.nameLevelPaths').d('组织层级'),
        width: 400,
        render: (nameLevelPaths = []) => {
          return nameLevelPaths.join('/');
        },
      },
      {
        dataIndex: 'unitTypeMeaning',
        title: intl.get('entity.organization.type').d('组织类型'),
        width: 130,
      },
      {
        dataIndex: 'orderSeq',
        title: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
        width: 110,
      },
      {
        dataIndex: 'supervisorFlag',
        title: intl.get('hpfm.organization.model.unit.supervisorFlag').d('主管组织'),
        width: 90,
        render: yesOrNoRender,
      },
      {
        key: 'operator',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 190,
        fixed: 'right',
        render: (_, record) => {
          const operators = [];
          switch (record.enabledFlag) {
            case 1:
              operators.push({
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '组织架构维护-编辑',
                      },
                    ]}
                    onClick={() => this.handleEditRecord(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              });
              operators.push({
                key: 'disable',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.disable`,
                        type: 'button',
                        meaning: '组织架构维护-禁用',
                      },
                    ]}
                    onClick={() => this.handleDisabledRecord(record)}
                  >
                    {intl.get('hzero.common.button.disable').d('禁用')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.disable').d('禁用'),
              });
              operators.push({
                key: 'assign-grade',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.assign`,
                        type: 'button',
                        meaning: '组织架构维护-分配部门',
                      },
                    ]}
                    onClick={() => this.handleGotoSubGradeRecord(record)}
                  >
                    {intl.get('hpfm.organization.view.option.assign').d('分配部门')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hpfm.organization.view.option.assign').d('分配部门'),
              });
              break;
            case 0:
              operators.push({
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '组织架构维护-编辑',
                      },
                    ]}
                    onClick={() => this.handleEditRecord(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              });
              operators.push({
                key: 'enable',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.enable`,
                        type: 'button',
                        meaning: '组织架构维护-启用',
                      },
                    ]}
                    onClick={() => this.handleEnableRecord(record)}
                  >
                    {intl.get('hzero.common.status.enable').d('启用')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.status.enable').d('启用'),
              });
              operators.push({
                key: 'assign-grade',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.assign`,
                        type: 'button',
                        meaning: '组织架构维护-分配部门',
                      },
                    ]}
                    onClick={() => this.handleGotoSubGradeRecord(record)}
                  >
                    {intl.get('hpfm.organization.view.option.assign').d('分配部门')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hpfm.organization.view.option.assign').d('分配部门'),
              });
              break;
            default:
              break;
          }
          return operatorRender(operators);
        },
      },
    ];
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({
      page,
      sort,
    });
  }

  @Bind()
  handleEditRecord(record) {
    this.setState({
      isCreate: false,
      drawerVisible: true,
      editRecord: record,
    });
  }

  /**
   * 对保存的数据禁用
   * @param record
   */
  @Bind()
  handleDisabledRecord(record) {
    const { forbidLine, organizationId } = this.props;
    forbidLine({
      tenantId: organizationId,
      unitId: record.unitId,
      objectVersionNumber: record.objectVersionNumber,
      _token: record._token,
    }).then((res) => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
  }

  /**
   * 对保存的数据启用
   * @param record
   */
  @Bind()
  handleEnableRecord(record) {
    const { organizationId, enabledLine } = this.props;
    enabledLine({
      tenantId: organizationId,
      unitId: record.unitId,
      objectVersionNumber: record.objectVersionNumber,
      _token: record._token,
    }).then((res) => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
  }

  /**
   * 分配部门 - 跳转到子路由
   * @param record
   */
  @Bind()
  handleGotoSubGradeRecord(record) {
    const { push } = this.props;
    push({
      pathname: `/hpfm/hr/org/department/${record.unitId}`,
    });
  }

  render() {
    const {
      dataSource = [],
      pagination = {},
      organizationId,
      unitType,
      match,
      createDataLoading = false,
      queryLoading = false,
      saveEditDataLoading = false,
      forbidLineLoading = false,
      enabledLineLoading = false,
    } = this.props;
    const { drawerVisible = false, editRecord = {}, isCreate = true } = this.state;
    const columns = this.getColumns();
    return (
      <React.Fragment>
        <SearchForm
          onSearch={this.handleSearchFormSearch}
          wrappedComponentRef={this.searchFormRef}
          unitType={unitType}
        />
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '组织架构维护-新建',
              },
            ]}
            onClick={this.handleCreateBtnClick}
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </div>
        <Table
          bordered
          rowKey="unitId"
          pagination={pagination}
          dataSource={dataSource}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          loading={queryLoading || forbidLineLoading || enabledLineLoading}
          onChange={this.handleTableChange}
        />
        <EditDrawer
          isCreate={isCreate}
          tenantId={organizationId}
          unitType={unitType}
          loading={saveEditDataLoading || createDataLoading}
          visible={drawerVisible}
          anchor="right"
          title={
            isCreate
              ? intl.get('hpfm.organization.view.title.create').d('组织信息新建')
              : intl.get('hpfm.organization.view.title.edit').d('组织信息修改')
          }
          onCancel={this.handleDrawerCancel}
          onOk={this.handleDrawerOk}
          itemData={editRecord}
        />
      </React.Fragment>
    );
  }
}
