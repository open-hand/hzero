/**
 * dataSource - 数据源
 * @date: 2018-9-10
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';

import TableList from './TableList';
import FilterForm from './FilterForm';

@connect(({ dataSource, loading }) => ({
  dataSource,
  fetchListLoading: loading.effects['dataSource/fetchDataSourceList'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.dataSource', 'entity.tenant'] })
export default class DataSource extends PureComponent {
  form;

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch({});
    this.fetchBatchCode();
  }

  /**
   * 初始化值集
   */
  @Bind()
  fetchBatchCode() {
    const { dispatch } = this.props;
    // 初始化 值集
    dispatch({
      type: `dataSource/batchCode`,
      payload: {
        lovCodes: {
          dataSourceClass: 'HPFM.DATASOURCE_CLASS', // 数据源分类值集
          dataSourceType: 'HPFM.DATABASE_TYPE', // 数据源类型值集
          purposeType: 'HPFM.DATASOURCE_PURPOSE', // 数据源用途值集
        },
      },
    });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'dataSource/fetchDataSourceList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 编辑数据源
   * @param {object} record
   */
  @Bind()
  handleEditDataSource(record) {
    const { history, dispatch } = this.props;
    dispatch({
      type: 'dataSource/updateState',
      payload: { dataSourceDetail: {}, dbPoolParams: {}, extConfigs: [] },
    });
    history.push(`/hpfm/data-source/detail/${record.datasourceId}`);
  }

  @Bind()
  handleViewDataSource(record) {
    const { history } = this.props;
    history.push(`/hpfm/data-source/detail/${record.datasourceId}`);
  }

  /**
   * 打开新增模态框
   */
  @Bind()
  handleCreate() {
    const { dispatch, history } = this.props;
    dispatch({
      type: 'dataSource/updateState',
      payload: { dataSourceDetail: {}, dbPoolParams: {}, extConfigs: [] },
    });
    history.push(`/hpfm/data-source/detail/create`);
  }

  /**
   * 删除服务
   *
   * @param {*} record
   * @memberof dataSource
   */
  @Bind()
  handleDeleteTenant(record) {
    const {
      dispatch,
      dataSource: { datasourceId },
    } = this.props;
    dispatch({
      type: 'dataSource/deleteService',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchServiceList({ datasourceId });
      }
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  render() {
    const {
      fetchListLoading = false,
      match,
      dataSource: {
        dataSourceData = {},
        pagination = {},
        dataSourceClassList = [],
        dataSourceTypeList = [],
        purposeType = [],
      },
      tenantId,
    } = this.props;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      purposeType,
    };
    // table props
    const listProps = {
      dataSourceData,
      dataSourceClassList,
      dataSourceTypeList,
      pagination,
      match,
      tenantId,
      loading: fetchListLoading,
      onChange: this.handleSearch,
      onEdit: this.handleEditDataSource,
      onView: this.handleViewDataSource,
    };
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.dataSource.view.message.title.dataSource').d('数据源设置')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '数据源-新建',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <TableList {...listProps} />
        </Content>
      </React.Fragment>
    );
  }
}
