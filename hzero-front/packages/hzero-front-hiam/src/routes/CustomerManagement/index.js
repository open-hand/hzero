/**
 * 客户化管理
 * @since 2019-12-18
 * @author LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import {
  DataSet,
  Table,
  Form,
  Button,
  ModalContainer,
  Modal,
  Tabs,
  Lov,
  TextField,
} from 'choerodon-ui/pro';
import { Popconfirm, Row, Col } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { Button as ButtonPermission } from 'components/Permission';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import {
  formDS,
  tableDS,
  tenantDS,
  refreshDS,
  tenantFormDS,
  tenantTableDS,
} from '@/stores/customerManagementDS';
import {
  distributeApi,
  deleteBatchCustomer,
  deleteTenantCustomer,
} from '@/services/customerManagementService';

import Drawer from './Drawer';
import styles from './index.less';

const { TabPane } = Tabs;
@formatterCollections({ code: ['hiam.customerManage'] })
export default class CustomerManagement extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabFlag: true,
      editItems: [],
      customerFlag: false,
      tenantCustomerFlag: false,
    };
    this.formDS = new DataSet(formDS());
    this.tableDS = new DataSet(tableDS(this.formDS));
    this.tenantDS = new DataSet(tenantDS());
    this.tenantFormDS = new DataSet(tenantFormDS());
    this.tenantTableDS = new DataSet(tenantTableDS(this.tenantFormDS));
    this.refreshDs = null;
  }

  componentDidMount() {
    this.tableDS.query();
    this.tableDS.addEventListener('query', this.handleSearch);
  }

  componentWillUnmount() {
    this.tableDS.removeEventListener('query', this.handleSearch);
  }

  @Bind()
  handleSearch() {
    this.setState({ editItems: [] });
  }

  // 编辑
  @Bind()
  handleEdit(record) {
    const { editItems } = this.state;
    editItems.push(record.get('customPointId'));
    this.setState({
      editItems,
    });
  }

  // 保存
  @Bind()
  async handleSave() {
    if (this.tableDS.isModified()) {
      const res = await this.tableDS.submit();
      if (res && res.success) {
        this.setState({
          editItems: [],
        });
        this.tableDS.query();
      }
    } else {
      notification.warning({
        message: intl.get('hiam.customerManage.view.message.noChange').d('数据未更改！'),
      });
    }
  }

  // 取消编辑
  @Bind()
  handleCancel(record) {
    let { editItems } = this.state;
    editItems = editItems.filter((item) => item !== record.get('customPointId'));
    this.setState({
      editItems,
    });
    record.reset();
  }

  // 删除客户化
  @Bind()
  async deleteCustomer(record) {
    const { tabFlag } = this.state;
    if (tabFlag) {
      await this.tableDS.delete(record);
      this.tableDS.query();
    } else {
      await this.tenantTableDS.delete(record);
      this.tenantTableDS.setQueryParameter('page', this.tenantTableDS.currentPage);
      this.tenantTableDS.query();
    }
  }

  // 批量删除客户化
  @Bind()
  async handleDeleteBatchCustomer() {
    const chooseRecords = this.tableDS.currentSelected.map((item) => item.toData());
    if (chooseRecords.length !== 0) {
      await deleteBatchCustomer(chooseRecords);
      this.tableDS.unSelectAll();
      this.tableDS.clearCachedSelected();
      this.tableDS.query();
    } else {
      notification.warning({
        message: intl
          .get('hiam.customerManage.view.title.chooseDeleteRecord')
          .d('请选中需要删除的记录！'),
      });
    }
  }

  // 批量删除租户客户化
  @Bind()
  async handleDeleteTenantCustomer() {
    const customPoints = this.tenantTableDS.selected.map((item) => item.toData());
    if (customPoints.length !== 0) {
      await deleteTenantCustomer({ customPoints });
      this.tenantTableDS.unSelectAll();
      this.tenantTableDS.clearCachedSelected();
      this.tenantTableDS.query();
    } else {
      notification.warning({
        message: intl
          .get('hiam.customerManage.view.title.chooseDeleteRecord')
          .d('请选中需要删除的记录！'),
      });
    }
  }

  // 客户化管理的表格列
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'customPointCode',
        width: 350,
      },
      {
        name: 'priority',
        align: 'left',
        width: 100,
        editor: (record) => {
          const { editItems } = this.state;
          return editItems.includes(record.get('customPointId'));
        },
      },
      {
        name: 'description',
        width: 300,
        editor: (record) => {
          const { editItems } = this.state;
          return editItems.includes(record.get('customPointId'));
        },
      },
      {
        name: 'className',
        width: 300,
      },
      {
        name: 'methodName',
        minWidth: 200,
      },
      {
        name: 'serviceName',
        width: 150,
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 112,
        renderer: ({ record }) => {
          const { editItems, tabFlag } = this.state;
          const id = record.get('customPointId');
          let actions = [];
          actions = [
            // eslint-disable-next-line no-nested-ternary
            tabFlag
              ? editItems.includes(id)
                ? {
                    ele: (
                      <a onClick={() => this.handleCancel(record)}>
                        {intl.get('hzero.common.button.cancel').d('取消')}
                      </a>
                    ),
                    key: 'cancel',
                    len: 2,
                    title: intl.get('hzero.common.button.cancel').d('取消'),
                  }
                : {
                    ele: (
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.customerEdit`,
                            type: 'button',
                            meaning: '客户化管理-编辑',
                          },
                        ]}
                        onClick={() => this.handleEdit(record)}
                      >
                        {intl.get('hzero.common.button.edit').d('编辑')}
                      </ButtonPermission>
                    ),
                    key: 'edit',
                    len: 2,
                    title: intl.get('hzero.common.button.edit').d('编辑'),
                  }
              : null,
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.customerDelete`,
                      type: 'button',
                      meaning: '客户化管理-删除',
                    },
                  ]}
                  onClick={() => this.deleteCustomer(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'left',
      },
    ];
  }

  // 租户管理页面table的列
  get tenantTableColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'tenantName',
        width: 140,
        lock: 'left',
      },
      {
        name: 'customPointCode',
        width: 350,
      },
      {
        name: 'priority',
        align: 'left',
        width: 100,
      },
      {
        name: 'description',
        width: 300,
      },
      {
        name: 'className',
        width: 300,
      },
      {
        name: 'methodName',
        minWidth: 200,
      },
      {
        name: 'serviceName',
        width: 150,
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 112,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.tenantDelete`,
                      type: 'button',
                      meaning: '客户化管理-删除',
                    },
                  ]}
                  onClick={() => this.deleteCustomer(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'left',
      },
    ];
  }

  // 租户lov表格列
  get tenantColumns() {
    return [
      {
        name: 'tenantNum',
        width: 256,
      },
      {
        name: 'tenantName',
      },
    ];
  }

  // 确认
  @Bind()
  async handleOk() {
    if (this.tenantDS.selected.length !== 0) {
      const tenantIds = this.tenantDS.selected.map((item) => item.toData().tenantId);
      const customPoints = this.tableDS.selected.map((item) => item.toData());
      const res = await distributeApi({ tenantIds, customPoints });
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      } else if (res && !res.failed) {
        notification.success({
          message: intl.get('hzero.common.notification.success').d('操作成功'),
        });
        this.tableDS.unSelectAll();
        this.tableDS.clearCachedSelected();
      }
      return true;
    } else {
      notification.warning({
        message: intl.get('hiam.customerManage.view.message.chooseTenant').d('请选择租户！'),
      });
      return false;
    }
  }

  // 点击分配
  @Bind()
  handleDistribute() {
    if (this.tableDS.selected.length !== 0) {
      this.openModal();
      this.tenantDS.query();
    } else {
      notification.warning({
        message: intl
          .get('hiam.customerManage.view.message.chooseRecord')
          .d('请选择需要分配的数据'),
      });
    }
  }

  /**
   * 打开模态框
   */
  @Bind()
  openModal() {
    Modal.open({
      key: 'tenant-modal',
      title: intl.get('hiam.customerManage.view.title.chooseTenant').d('选择租户'),
      destroyOnClose: true,
      style: {
        width: 700,
      },
      children: <Table dataSet={this.tenantDS} columns={this.tenantColumns} queryFieldsLimit={2} />,
      onOk: () => this.handleOk(),
      onCancel: () => true,
      afterClose: () => {
        this.tenantDS.queryDataSet.reset();
        this.tenantDS.unSelectAll();
        this.tenantDS.clearCachedSelected();
      },
    });
  }

  // 切换tab
  @Bind()
  handleChange(tabItem) {
    const { tabFlag } = this.state;
    if (tabItem === 'tenantCustomerManage') {
      this.tenantTableDS.query();
    } else {
      this.tableDS.query();
    }
    this.setState({ tabFlag: !tabFlag });
  }

  // 客户化管理页面更多查询切换
  @Bind()
  handleMoreSearchItem() {
    const { customerFlag } = this.state;
    this.setState({ customerFlag: !customerFlag });
  }

  // 租户客户化管理页面更多查询切换
  @Bind()
  handleTenantMoreSearchItem() {
    const { tenantCustomerFlag } = this.state;
    this.setState({ tenantCustomerFlag: !tenantCustomerFlag });
  }

  // 客户化管理页面查询
  @Bind()
  handleQuery() {
    this.tableDS.query();
  }

  // 租户客户化管理页面查询
  @Bind()
  handleTenantQuery() {
    this.tenantTableDS.query();
  }

  // 客户化管理回车事件
  @Bind()
  handleEnter() {
    this.tableDS.query();
  }

  // 租户客户化管理回车事件
  @Bind()
  handleTenantEnter() {
    this.tenantTableDS.query();
  }

  /**
   * 打开刷新客户化端点模态框
   */
  @Bind()
  openRefreshModal() {
    this.refreshDs = new DataSet(refreshDS());
    Modal.open({
      closable: true,
      key: 'refresh-customer-point',
      title: intl.get('hiam.customerManage.view.message.refreshCustomer').d('刷新客户化端点'),
      style: {
        width: 500,
      },
      children: <Drawer refreshDs={this.refreshDs} />,
      onOk: this.handleRefresh,
      onCancel: () => {
        this.refreshDs.reset();
      },
      onClose: () => {
        this.refreshDs.reset();
      },
    });
  }

  /**
   * 刷新客户化端点
   */
  @Bind()
  async handleRefresh() {
    const validate = await this.refreshDs.validate();
    if (validate) {
      try {
        await this.refreshDs.submit();
      } catch (e) {
        // do nothing, ds noticed
      }
    } else {
      return false;
    }
    this.tableDS.query();
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { editItems, customerFlag, tenantCustomerFlag } = this.state;
    const saveFlag = editItems.length === 0;
    const { location } = this.props;
    return (
      <>
        <Header title={intl.get('hiam.customerManage.view.title.customerManage').d('客户化管理')} />
        <Content>
          <Tabs defaultActiveKey="customerManage" animated={false} onChange={this.handleChange}>
            <TabPane
              tab={intl.get('hiam.customerManage.view.title.customerManage').d('客户化管理')}
              key="customerManage"
            >
              <Row>
                <Col span={18}>
                  <Form dataSet={this.formDS} columns={3}>
                    <TextField name="customPointCode" onEnterDown={this.handleEnter} />
                    <TextField name="description" onEnterDown={this.handleEnter} />
                    <Lov name="serviceNameLov" onEnterDown={this.handleEnter} />
                    {customerFlag && <TextField name="className" onEnterDown={this.handleEnter} />}
                    {customerFlag && <TextField name="methodName" onEnterDown={this.handleEnter} />}
                  </Form>
                </Col>
                <Col span={6}>
                  <Form dataSet={this.formDS}>
                    <div className={styles['query-buttons']}>
                      <Button
                        onClick={this.handleMoreSearchItem}
                        className={styles['query-buttons-more']}
                      >
                        {intl.get('hzero.common.button.viewMore').d('更多查询')}
                      </Button>
                      <Button type="reset" className={styles['query-buttons-item']}>
                        {intl.get('hzero.common.button.reset').d('重置')}
                      </Button>
                      <Button
                        color="primary"
                        onClick={this.handleQuery}
                        className={styles['query-buttons-item']}
                      >
                        {intl.get('hzero.common.button.search').d('查询')}
                      </Button>
                    </div>
                  </Form>
                </Col>
              </Row>
              <Row type="flex" justify="end" className={styles['action-row']}>
                <Col offset={12}>
                  <Popconfirm
                    placement="topRight"
                    title={intl
                      .get('hzero.common.message.confirm.deleteChooseRecord')
                      .d('是否删除选中记录？')}
                    okText={intl.get('hzero.common.button.ok').d('确定')}
                    cancelText={intl.get('hzero.common.button.cancel').d('取消')}
                    onConfirm={this.handleDeleteBatchCustomer}
                  >
                    <ButtonPermission
                      type="c7n-pro"
                      permissionList={[
                        {
                          code: `${path}.button.customerDelete`,
                          type: 'button',
                          meaning: '客户化管理-删除',
                        },
                      ]}
                      icon="delete"
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                  <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}.button.customerSave`,
                        type: 'button',
                        meaning: '客户化管理-保存',
                      },
                    ]}
                    icon="save"
                    onClick={this.handleSave}
                    disabled={saveFlag}
                  >
                    {intl.get('hzero.common.button.save').d('保存')}
                  </ButtonPermission>
                  <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}.button.customerRefresh`,
                        type: 'button',
                        meaning: '客户化管理-删除',
                      },
                    ]}
                    icon="sync"
                    onClick={this.openRefreshModal}
                  >
                    {intl.get('hzero.common.button.refresh').d('刷新')}
                  </ButtonPermission>
                  <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}.button.customerDistribute`,
                        type: 'button',
                        meaning: '客户化管理-删除',
                      },
                    ]}
                    className={styles['button-apportion']}
                    icon="API_publish"
                    onClick={this.handleDistribute}
                  >
                    {intl.get('hzero.common.button.distribution').d('分配')}
                  </ButtonPermission>
                </Col>
              </Row>
              <Table dataSet={this.tableDS} columns={this.columns} />
            </TabPane>
            <TabPane
              tab={intl
                .get('hiam.customerManage.view.title.tenantCustomerManage')
                .d('租户客户化管理')}
              key="tenantCustomerManage"
            >
              <Row>
                <Col span={18}>
                  <Form dataSet={this.tenantFormDS} columns={3}>
                    <Lov name="tenantIdLov" onEnterDown={this.handleTenantEnter} />
                    <TextField name="customPointCode" onEnterDown={this.handleTenantEnter} />
                    <TextField name="description" onEnterDown={this.handleTenantEnter} />
                    {tenantCustomerFlag && (
                      <Lov name="serviceNameLov" onEnterDown={this.handleTenantEnter} />
                    )}
                    {tenantCustomerFlag && (
                      <TextField name="className" onEnterDown={this.handleTenantEnter} />
                    )}
                    {tenantCustomerFlag && (
                      <TextField name="methodName" onEnterDown={this.handleTenantEnter} />
                    )}
                  </Form>
                </Col>
                <Col span={6}>
                  <Form dataSet={this.tenantFormDS}>
                    <div className={styles['query-buttons']}>
                      <Button
                        onClick={this.handleTenantMoreSearchItem}
                        className={styles['query-buttons-more']}
                      >
                        {intl.get('hzero.common.button.viewMore').d('更多查询')}
                      </Button>
                      <Button type="reset" className={styles['query-buttons-item']}>
                        {intl.get('hzero.common.button.reset').d('重置')}
                      </Button>
                      <Button
                        color="primary"
                        onClick={this.handleTenantQuery}
                        className={styles['query-buttons-item']}
                      >
                        {intl.get('hzero.common.button.search').d('查询')}
                      </Button>
                    </div>
                  </Form>
                </Col>
              </Row>
              <Row type="flex" justify="end" className={styles['action-row']}>
                <Col offset={18}>
                  <Popconfirm
                    placement="topRight"
                    title={intl
                      .get('hzero.common.message.confirm.deleteChooseRecord')
                      .d('是否删除选中记录？')}
                    okText={intl.get('hzero.common.button.ok').d('确定')}
                    cancelText={intl.get('hzero.common.button.cancel').d('取消')}
                    onConfirm={this.handleDeleteTenantCustomer}
                  >
                    <Button
                      type="c7n-pro"
                      permissionList={[
                        {
                          code: `${path}.button.customerDelete`,
                          type: 'button',
                          meaning: '客户化管理-删除',
                        },
                      ]}
                      icon="delete"
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </Button>
                  </Popconfirm>
                </Col>
              </Row>
              <Table dataSet={this.tenantTableDS} columns={this.tenantTableColumns} />
            </TabPane>
          </Tabs>
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
