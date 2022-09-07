/**
 * Table - 菜单配置 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import { FORM_COL_3_LAYOUT, FORM_COL_2_3_LAYOUT } from 'utils/constants';
import { tableScrollWidth } from 'utils/utils';
import AddDataModal from '@/components/AddDataModal';
import Search from './Search';

export default class ServiceList extends PureComponent {
  state = {
    addModalVisible: false,
  };

  /**
   * 查询弹窗数据
   */
  @Bind()
  fetchModalData(queryData = {}) {
    const { onFetchModalData = () => {}, organizationId, formDataSource = {} } = this.props;
    const payload = {
      roleId: formDataSource.id,
      tenantId: organizationId,
      ...queryData,
    };
    onFetchModalData(payload);
  }

  /**
   * 打开新建内部接口弹窗
   */
  @Bind()
  handleOpenModal() {
    const { loading, addAuthLoading } = this.props;
    if (loading || addAuthLoading) return;
    this.fetchModalData();
    this.setState({
      addModalVisible: true,
    });
  }

  @Bind()
  handleCloseModal() {
    this.interfaceRef.state.addRows = [];
    this.setState({
      addModalVisible: false,
    });
  }

  @Bind()
  handleBindRef(ref = {}) {
    this.interfaceRef = ref;
  }

  /**
   * 批量添加授权接口
   * @param {Aarray} addRows 选择的数据
   */
  @Bind()
  handleCreateLine(addRows) {
    const { add = () => {} } = this.props;
    add(addRows, this.handleCloseModal);
  }

  render() {
    const {
      path,
      dataSource = [],
      selectedRowKeys = [],
      loading,
      onRowSelectionChange = (e) => e,
      listSelectedRows,
      deleteRows = () => {},
      onChange = () => {},
      pagination = {},
      queryInterfaceLoading,
      interfaceList,
      addAuthLoading,
      onBindSeachRef = () => {},
      onSearch = (e) => e,
    } = this.props;
    const { addModalVisible } = this.state;
    const searchProps = {
      onSearch,
      onRef: onBindSeachRef,
    };
    // const { _token = '' } = formDataSource;
    const tableColumns = [
      {
        title: intl.get('hitf.application.model.application.permissionType').d('授权类型'),
        dataIndex: 'permissionTypeMeaning',
        width: 100,
      },
      {
        title: intl.get('hitf.application.model.application.interfaceCode').d('接口编码'),
        dataIndex: 'interfaceCode',
        width: 200,
      },
      {
        title: intl.get('hitf.application.model.application.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 250,
      },
      {
        title: intl.get('hitf.application.model.application.serverCode').d('服务代码'),
        dataIndex: 'serverCode',
        width: 200,
      },
      {
        title: intl.get('hitf.application.model.application.serverName').d('服务名称'),
        dataIndex: 'serverName',
        width: 250,
      },
    ];
    const tableProps = {
      dataSource,
      pagination,
      loading,
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowSelection: {
        selectedRowKeys,
        onChange: onRowSelectionChange,
      },
      onChange,
    };
    const columns = [
      {
        title: intl.get('hitf.clientRole.model.clientRole.interfaceCode').d('接口代码'),
        dataIndex: 'interfaceCode',
        width: 250,
      },
      {
        title: intl.get('hitf.clientRole.model.clientRole.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 250,
      },
      {
        title: intl.get('hitf.clientRole.model.clientRole.serverCode').d('服务代码'),
        dataIndex: 'serverCode',
        width: 100,
      },
      {
        title: intl.get('hitf.clientRole.model.clientRole.serverName').d('服务名称'),
        dataIndex: 'serverName',
        width: 300,
      },
    ];
    const addModalOptions = {
      columns,
      confirmLoading: addAuthLoading,
      loading: queryInterfaceLoading,
      title: intl.get('hitf.clientRole.view.message.title.interface').d('选择接口'),
      rowKey: 'interfaceId',
      queryName: 'interfaceName',
      queryCode: 'interfaceCode',
      queryNameDesc: intl.get('hitf.clientRole.model.clientRole.interfaceName').d('接口名称'),
      queryCodeDesc: intl.get('hitf.clientRole.model.clientRole.interfaceCode').d('接口代码'),
      dataSource: interfaceList.dataSource || [],
      pagination: interfaceList.pagination || {},
      modalVisible: addModalVisible,
      addData: this.handleCreateLine,
      onHideAddModal: this.handleCloseModal,
      fetchModalData: this.fetchModalData,
      onRef: this.handleBindRef,
    };

    return (
      <>
        <Row>
          <Col {...FORM_COL_2_3_LAYOUT}>
            <Search {...searchProps} />
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Row style={{ textAlign: 'right', paddingTop: '3px' }}>
              <Col span={24}>
                <div>
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${path}.button.unauthorized`,
                        type: 'button',
                        meaning: '角色授权-删除授权',
                      },
                    ]}
                    // icon="remove"
                    type="c7n-pro"
                    disabled={isEmpty(listSelectedRows)}
                    onClick={deleteRows}
                    style={{ marginRight: 8 }}
                  >
                    {intl.get('hitf.application.view.button.deleteAuth').d('删除授权')}
                  </ButtonPermission>
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${path}.button.authorized`,
                        type: 'button',
                        meaning: '角色授权-添加授权',
                      },
                    ]}
                    // icon="add"
                    type="c7n-pro"
                    color="primary"
                    onClick={this.handleOpenModal}
                  >
                    {intl.get('hitf.application.view.button.addAuth').d('添加授权')}
                  </ButtonPermission>
                </div>
              </Col>
            </Row>
          </Col>
        </Row>
        <Table {...tableProps} />
        <AddDataModal {...addModalOptions} />
      </>
    );
  }
}
