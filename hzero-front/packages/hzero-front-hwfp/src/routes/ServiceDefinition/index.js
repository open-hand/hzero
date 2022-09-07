import React from 'react';
import { connect } from 'dva';
import { Button, Icon, Popconfirm, Table, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import {
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import FilterForm from './FilterForm';

@connect(({ loading, serviceDefinition }) => ({
  serviceDefinition,
  isSiteFlag: !isTenantRoleLevel(),
  currentTenantId: getCurrentOrganizationId(),
  listLoading: loading.effects['serviceDefinition/fetchList'],
  deleteLoading: loading.effects['serviceDefinition/deleteService'],
}))
@formatterCollections({ code: ['hwfp.serviceDefinition', 'hwfp.interfaceMap', 'entity.tenant'] })
export default class ServiceDefinition extends React.Component {
  filterForm;

  state = {
    currentRecord: {},
  };

  componentDidMount() {
    const { dispatch, currentTenantId } = this.props;
    dispatch({
      type: 'serviceDefinition/init',
      payload: {
        tenantId: currentTenantId,
      },
    });
    this.fetchList();
  }

  @Bind()
  fetchList(params = {}) {
    const {
      dispatch,
      serviceDefinition: { pagination = {} },
    } = this.props;
    const fieldValues =
      this.filterForm === undefined ? {} : filterNullValueObject(this.filterForm.getFieldsValue());
    dispatch({
      type: 'serviceDefinition/fetchList',
      payload: { ...fieldValues, page: pagination, ...params },
    });
  }

  @Bind()
  handleSearch() {
    this.fetchList({ page: {} });
  }

  /**
   *
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.filterForm = (ref.props || {}).form;
  }

  @Bind()
  handlePagination(page) {
    this.fetchList({ page });
  }

  @Bind()
  handleCreate() {
    const { history } = this.props;
    history.push(`/hwfp/service-definition/detail/create`);
  }

  @Bind()
  handleEdit(record) {
    const { history, dispatch } = this.props;
    // 清除缓存
    dispatch({
      type: 'serviceDefinition/updateState',
      payload: { serviceDetail: {}, parameterList: [] },
    });
    history.push(`/hwfp/service-definition/detail/${record.serviceId}`);
  }

  @Bind()
  deleteService(record = {}) {
    const { dispatch } = this.props;
    this.setState({ currentRecord: record });
    dispatch({
      type: 'serviceDefinition/deleteService',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchList();
      }
    });
  }

  @Bind()
  getColumns() {
    const { isSiteFlag, currentTenantId } = this.props;
    if (!this.columns) {
      this.columns = [
        isSiteFlag && {
          title: intl.get('hzero.common.model.tenantName').d('租户'),
          dataIndex: 'tenantName',
          width: 200,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.categoryDescription').d('流程分类'),
          dataIndex: 'categoryDescription',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.serviceCode').d('服务编码'),
          dataIndex: 'serviceCode',
          width: 250,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.serviceModeMeaning').d('服务方式'),
          dataIndex: 'serviceModeMeaning',
          width: 250,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.serviceTypeMeaning').d('服务类别'),
          dataIndex: 'serviceTypeMeaning',
          width: 120,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.documentDescription').d('流程单据'),
          dataIndex: 'documentDescription',
          width: 200,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.description').d('服务描述'),
          dataIndex: 'description',
        },
        {
          title: intl.get('hzero.common.status').d('状态'),
          width: 100,
          align: 'center',
          dataIndex: 'enabledFlag',
          render: enableRender,
        },
        !isSiteFlag && {
          title: intl.get('hzero.common.source').d('来源'),
          width: 100,
          align: 'center',
          render: (_, record) =>
            currentTenantId === record.tenantId ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            ),
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          dataIndex: 'operator',
          width: 130,
          fixed: 'right',
          render: (val, record) => {
            const { deleteLoading = false } = this.props;
            const { currentRecord } = this.state;
            const operators = [
              {
                key: 'edit',
                ele: (
                  <a
                    onClick={() => {
                      this.handleEdit(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ),
                len: 3,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
            ];
            if (isSiteFlag || currentTenantId === record.tenantId) {
              if (!(record.serviceId === currentRecord.serviceId && deleteLoading)) {
                operators.push({
                  key: 'delete',
                  ele: (
                    <Popconfirm
                      placement="topRight"
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.deleteService(record)}
                    >
                      <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                    </Popconfirm>
                  ),
                  len: 3,
                  title: intl.get('hzero.common.button.delete').d('删除'),
                });
              } else {
                operators.push({
                  key: 'loading',
                  ele: <Icon type="loading" style={{ marginLeft: 20 }} />,
                  len: 3,
                });
              }
            }
            return operatorRender(operators, record);
          },
        },
      ].filter(Boolean);
    }
    return this.columns;
  }

  render() {
    const {
      isSiteFlag,
      currentTenantId,
      listLoading = false,
      serviceDefinition: {
        pagination = {},
        serviceList = [],
        serviceTypeList = [],
        serviceModeList = [],
        processCategoryList = [],
      },
    } = this.props;
    const filterProps = {
      isSiteFlag,
      currentTenantId,
      processCategoryList,
      serviceModeList,
      serviceTypeList,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    return (
      <>
        <Header
          title={intl.get('hwfp.serviceDefinition.view.title.serviceDefinition').d('服务定义')}
        >
          <Button icon="plus" type="primary" onClick={this.handleCreate}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <Table
            bordered
            rowKey="serviceId"
            columns={this.getColumns()}
            scroll={{ x: tableScrollWidth(this.getColumns()) }}
            dataSource={serviceList}
            pagination={pagination}
            loading={listLoading}
            onChange={this.handlePagination}
          />
        </Content>
      </>
    );
  }
}
