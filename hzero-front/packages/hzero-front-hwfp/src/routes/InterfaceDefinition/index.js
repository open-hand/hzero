import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button, Table, Tag, Popconfirm, Icon } from 'hzero-ui';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import {
  filterNullValueObject,
  tableScrollWidth,
  isTenantRoleLevel,
  getCurrentOrganizationId,
} from 'utils/utils';

import FilterForm from './FilterForm';

@connect(({ interfaceDefinition, loading }) => ({
  interfaceDefinition,
  isSiteFlag: !isTenantRoleLevel(),
  currentTenantId: getCurrentOrganizationId(),
  listLoading: loading.effects['interfaceDefinition/fetchList'],
  deleteLoading: loading.effects['interfaceDefinition/deleteInterface'],
}))
@formatterCollections({ code: ['hwfp.interfaceDefinition', 'hwfp.interfaceMap', 'entity.tenant'] })
export default class InterfaceDefinition extends React.Component {
  filterForm;

  state = {
    currentRecord: {},
  };

  componentDidMount() {
    this.fetchList();
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.filterForm = (ref.props || {}).form;
  }

  @Bind()
  fetchList(params = {}) {
    const {
      dispatch,
      interfaceDefinition: { pagination = {} },
    } = this.props;
    const fieldValues =
      this.filterForm === undefined ? {} : filterNullValueObject(this.filterForm.getFieldsValue());
    dispatch({
      type: 'interfaceDefinition/fetchList',
      payload: { ...fieldValues, page: pagination, ...params },
    });
  }

  @Bind()
  handleSearch() {
    this.fetchList({ page: {} });
  }

  @Bind()
  handlePagination(page) {
    this.fetchList({ page });
  }

  @Bind()
  deleteInterface(record) {
    const { dispatch } = this.props;
    this.setState({ currentRecord: record });
    dispatch({
      type: 'interfaceDefinition/deleteInterface',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchList();
      }
    });
  }

  @Bind()
  handleCreate() {
    const { history } = this.props;
    history.push('/hwfp/interface-definition/detail/create');
  }

  @Bind()
  handleEdit(record) {
    const { history, dispatch } = this.props;
    // 清除缓存
    dispatch({
      type: 'interfaceDefinition/updateState',
      payload: { interfaceDefinitionDetail: {}, parameterList: [], paramTypeList: [] },
    });
    history.push(`/hwfp/interface-definition/detail/${record.interfaceId}`);
  }

  render() {
    const {
      currentTenantId,
      isSiteFlag,
      listLoading = false,
      deleteLoading = false,
      interfaceDefinition = {},
    } = this.props;
    const { currentRecord = {} } = this.state;
    const { interfaceDefinitionList = [], pagination = {} } = interfaceDefinition;
    const filterProps = {
      isSiteFlag,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };

    const columns = [
      isSiteFlag && {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.interface.interfaceCode').d('接口编码'),
        dataIndex: 'interfaceCode',
        width: 200,
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.interface.serviceName').d('服务名称'),
        dataIndex: 'serviceName',
        width: 250,
      },
      {
        title: intl
          .get('hwfp.interfaceDefinition.model.interface.permissionCode')
          .d('接口权限编码'),
        dataIndex: 'permissionCode',
        width: 300,
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.interface.description').d('接口说明'),
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
          currentTenantId.toString() === record.tenantId.toString() ? (
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
          if (isSiteFlag || currentTenantId.toString() === record.tenantId.toString()) {
            if (!(record.interfaceId === currentRecord.interfaceId && deleteLoading)) {
              operators.push({
                key: 'delete',
                ele: (
                  <Popconfirm
                    placement="topRight"
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => this.deleteInterface(record)}
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

    return (
      <>
        <Header title={intl.get('hwfp.interfaceDefinition.view.message.title').d('接口定义')}>
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
            rowKey="interfaceId"
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            dataSource={interfaceDefinitionList}
            pagination={pagination}
            loading={listLoading}
            onChange={this.handlePagination}
          />
        </Content>
      </>
    );
  }
}
