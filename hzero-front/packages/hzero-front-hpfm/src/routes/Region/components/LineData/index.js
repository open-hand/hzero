/**
 * LineData - 打平的组织数据
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-12
 * @copyright 2019 © HAND
 */

import React from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';

import SearchForm from './SearchForm';
import EditDrawer from '../EditDrawer';

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
    const { query } = this.props;
    this.setState({
      cachePagination: pagination,
    });
    query({
      query: {
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
      editRecord: {
        enabledFlag: 1, // 新增地区默认启用
      },
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
    const { updateRecord, createRecord } = this.props;
    const { isCreate } = this.state;
    if (isCreate) {
      //  新建
      createRecord({
        body: [
          {
            ...values,
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
      updateRecord({
        regionId: values.regionId,
        body: values,
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
        title: intl.get('hpfm.region.model.region.regionCode').d('区域代码'),
        dataIndex: 'regionCode',
        width: 150,
      },
      {
        title: intl.get('hpfm.region.model.region.regionName').d('区域名称'),
        dataIndex: 'regionName',
      },
      {
        title: intl.get('hpfm.region.model.region.quickIndex').d('快速索引'),
        dataIndex: 'quickIndex',
        width: 150,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'isDisable',
        width: 200,
        render: (text, record) => {
          const actions = [];
          if (record.enabledFlag === 1) {
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.add`,
                      type: 'button',
                      meaning: '地区定义-树形结构-新建下级地区',
                    },
                  ]}
                  onClick={() => {
                    this.handleCreateChild(record);
                  }}
                >
                  {intl.get('hpfm.region.button.add').d('新建下级地区')}
                </ButtonPermission>
              ),
              key: 'create',
              len: 6,
              title: intl.get('hpfm.region.button.add').d('新建下级地区'),
            });
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.disable`,
                      type: 'button',
                      meaning: '地区定义-树形结构-禁用',
                    },
                  ]}
                  onClick={() => this.handleDisabledRecord(record)}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              key: 'disable',
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            });
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '地区定义-树形结构-编辑',
                    },
                  ]}
                  onClick={() => this.handleEditRecord(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          } else {
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.enable`,
                      type: 'button',
                      meaning: '地区定义-树形结构-启用',
                    },
                  ]}
                  onClick={() => this.handleEnableRecord(record)}
                >
                  {intl.get('hzero.common.button.enable').d('启用')}
                </ButtonPermission>
              ),
              key: 'enable',
              len: 2,
              title: intl.get('hzero.common.button.enable').d('启用'),
            });
          }
          return operatorRender(actions);
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
  handleCreateChild(record) {
    this.setState({
      isCreate: true,
      drawerVisible: true,
      editRecord: {
        parentRegionId: record.regionId,
        enabledFlag: 1, // 默认启用
      },
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
    const { disableRecord } = this.props;
    disableRecord({
      regionId: record.regionId,
      body: {
        regionId: record.regionId,
        objectVersionNumber: record.objectVersionNumber,
        _token: record._token,
        enabledFlag: 0,
      },
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
    const { enableRecord } = this.props;
    enableRecord({
      regionId: record.regionId,
      body: {
        regionId: record.regionId,
        objectVersionNumber: record.objectVersionNumber,
        _token: record._token,
        enabledFlag: 1,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
  }

  render() {
    const {
      queryDetail,
      dataSource = [],
      pagination = {},
      match,
      createLoading = false,
      queryLoading = false,
      updateLoading = false,
      disableLoading = false,
      enableLoading = false,
      queryDetailLoading = false,
    } = this.props;
    const { drawerVisible = false, editRecord = {}, isCreate = true } = this.state;
    const columns = this.getColumns();
    return (
      <React.Fragment>
        <SearchForm
          onSearch={this.handleSearchFormSearch}
          wrappedComponentRef={this.searchFormRef}
        />
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '地区定义-分页结构-新建',
              },
            ]}
            onClick={this.handleCreateBtnClick}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </div>
        <Table
          bordered
          rowKey="regionId"
          pagination={pagination}
          dataSource={dataSource}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          loading={queryLoading || disableLoading || enableLoading}
          onChange={this.handleTableChange}
        />
        <EditDrawer
          isCreate={isCreate}
          loading={updateLoading || createLoading}
          visible={drawerVisible}
          anchor="right"
          title={
            isCreate
              ? intl.get('hpfm.region.view.title.create').d('地区新增')
              : intl.get('hpfm.region.view.title.edit').d('地区修改')
          }
          onCancel={this.handleDrawerCancel}
          onOk={this.handleDrawerOk}
          itemData={editRecord}
          queryDetail={queryDetail}
          queryDetailLoading={queryDetailLoading}
        />
      </React.Fragment>
    );
  }
}
