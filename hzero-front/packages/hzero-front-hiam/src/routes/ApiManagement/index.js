/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
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
  Select,
} from 'choerodon-ui/pro';
import { Badge, Popconfirm, Row, Col } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, sortBy } from 'lodash';
import { Button as ButtonPermission } from 'components/Permission';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';

import {
  formDS,
  tableDS,
  drawerDS,
  tenantDS,
  // refreshDS,
  tenantFormDS,
  tenantTableDS,
} from '@/stores/apiManagementGroupDS';
import {
  distributeApi,
  deleteBatchApi,
  deleteSingleTenantAPI,
  queryApiLabel,
} from '@/services/apiManagementService';

import styles from './index.less';

// import Drawer from './Drawer';
import EditDrawer from './EditDrawer';

const { TabPane } = Tabs;
@formatterCollections({ code: ['hiam.apiManagement'] })
export default class ApiManagement extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabFlag: true,
      initTenantFlag: true,
      apiManagementFlag: false,
      tenantApiManagementFlag: false,
    };
    this.formDS = new DataSet(formDS());
    this.tableDS = new DataSet(tableDS(this.formDS));
    this.tenantDS = new DataSet(tenantDS());
    this.drawerDS = new DataSet(drawerDS());
    this.tenantFormDS = new DataSet(tenantFormDS());
    this.tenantTableDS = new DataSet(tenantTableDS(this.tenantFormDS));
    // this.refreshDs = null;
  }

  componentDidMount() {
    this.tableDS.query();
    this.drawerDS.query().then((res) => {
      if (res) {
        this.tableDS.labelList = res;
      }
    });
  }

  // 编辑
  @Bind()
  async handleEdit(record) {
    const id = record.get('id');
    let frontLabels = (await queryApiLabel(id)) || [];
    frontLabels = isEmpty(frontLabels) ? [] : frontLabels;
    const labelList = this.drawerDS.toData() || [];
    record.set('apiTags', {
      PAGE: isEmpty(frontLabels) ? [] : frontLabels.map((item) => item.label.name).sort(),
    });
    const drawerProps = {
      labelList: sortBy(
        labelList.map((item) => {
          return {
            type: (frontLabels.find((temp) => temp.label.name === item.name) || {}).assignType,
            ...item,
          };
        }),
        (item) => item.name
      ),
      id,
      record,
    };
    // const originalData = this.tableDS.toData();
    this.tableDS.frontLabels = frontLabels
      .filter((item) => {
        return item.assignType === 'A';
      })
      .map((tmp) => {
        return tmp.label.name;
      });
    Modal.open({
      closable: true,
      key: 'knowledge-category',
      title: intl.get('hiam.apiManagement.view.message.edit').d('编辑API'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <EditDrawer {...drawerProps} />,
      onOk: this.handleSave,
      onCancel: () => {
        // this.tableDS.loadData(originalData);
        record.reset();
      },
    });
  }

  // 保存
  @Bind()
  async handleSave() {
    if (this.tableDS.isModified()) {
      const res = await this.tableDS.submit();
      if (res && res.success) {
        this.tableDS.query();
      }
    } else {
      notification.warning({
        message: intl.get('hiam.apiManagement.view.message.noChange').d('数据未更改！'),
      });
    }
  }

  // 删除Api
  @Bind()
  async deleteApi(record) {
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

  // 批量删除Api
  @Bind()
  async handleDeleteBatchApi() {
    const chooseRecords = this.tableDS.currentSelected.map((item) => item.toData());
    if (chooseRecords.length !== 0) {
      await deleteBatchApi(chooseRecords).then((res) => {
        if (res) {
          notification.success();
        }
      });
      this.tableDS.unSelectAll();
      this.tableDS.clearCachedSelected();
      this.tableDS.query();
    } else {
      notification.warning({
        message: intl
          .get('hiam.apiManagement.view.title.chooseDeleteRecord')
          .d('请选中需要删除的记录！'),
      });
    }
  }

  // 批量删除单租户Api
  @Bind()
  async handleDeleteSingleTenantApi() {
    const permissions = this.tenantTableDS.selected.map((item) => item.toData());
    if (permissions.length !== 0) {
      await deleteSingleTenantAPI({ permissions }).then((res) => {
        if (res) {
          notification.success();
        }
      });
      this.tenantTableDS.unSelectAll();
      this.tenantTableDS.clearCachedSelected();
      this.tenantTableDS.query();
    } else {
      notification.warning({
        message: intl
          .get('hiam.apiManagement.view.title.chooseDeleteRecord')
          .d('请选中需要删除的记录！'),
      });
    }
  }

  // Api管理的表格列
  get columns() {
    const {
      match: { path },
    } = this.props;
    const statusMap = ['error', 'success'];
    return [
      {
        name: 'description',
        width: 200,
        // editor: record => {
        //   const { editItems } = this.state;
        //   return editItems.includes(record.get('id'));
        // },
      },
      {
        name: 'code',
        width: 310,
      },
      {
        name: 'path',
        width: 230,
      },
      {
        name: 'methodMeaning',
      },
      {
        name: 'levelMeaning',
      },
      {
        name: 'serviceName',
        width: 120,
      },
      // {
      //   name: 'tag',
      //   renderer: ({ record }) => {
      //     const apiTags = record.get('apiTags') || { BACKEND: [], PAGE: [] };
      //     // const tags = (apiTags.BACKEND || []).concat(apiTags.PAGE || []);
      //     const arr = (apiTags.PAGE || [])
      //       .map(item => ({ name: item, color: 'green' }))
      //       .concat((apiTags.BACKEND || []).map(item => ({ name: item, color: 'red' })));
      //     // tags.map((item) =>
      //     //   (apiTags.BACKEND || []).includes(item)
      //     //     ? { name: item, color: 'red' }
      //     //     : { name: item, color: 'green' }
      //     // );
      //     return (
      //       <div>
      //         {arr.map(item => (
      //           <Tag color={item.color}>{item.name}</Tag>
      //         ))}
      //       </div>
      //     );
      //   },
      // },
      {
        name: 'publicAccess',
        width: 120,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        name: 'loginAccess',
        width: 130,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        name: 'within',
        width: 120,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        name: 'signAccess',
        width: 120,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 112,
        renderer: ({ record }) => {
          const { tabFlag } = this.state;
          let actions = [];
          actions = [
            tabFlag
              ? {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.apiEdit`,
                          type: 'button',
                          meaning: 'API管理-编辑',
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
                      code: `${path}.button.apiDelete`,
                      type: 'button',
                      meaning: 'API管理-删除',
                    },
                  ]}
                  onClick={() => this.deleteApi(record)}
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

  // 租户API管理页面table的列
  get tenantApiColumns() {
    const statusMap = ['error', 'success'];
    return [
      {
        name: 'tenantName',
        width: 128,
        lock: 'left',
      },
      {
        name: 'description',
        width: 192,
      },
      {
        name: 'code',
        width: 304,
      },
      {
        name: 'path',
        width: 224,
      },
      {
        name: 'methodMeaning',
      },
      {
        name: 'levelMeaning',
      },
      {
        name: 'serviceName',
        width: 128,
      },
      {
        name: 'publicAccess',
        width: 128,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        name: 'loginAccess',
        width: 128,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        name: 'within',
        width: 128,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        name: 'signAccess',
        width: 128,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={statusMap[value ? 1 : 0]}
            text={
              value
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 112,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <a onClick={() => this.deleteApi(record)}>
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
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

  // tenantApi表格列
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
      const permissions = this.tableDS.selected.map((item) => item.toData());
      const res = await distributeApi({ tenantIds, permissions });
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
        message: intl.get('hiam.apiManagement.view.message.chooseTenant').d('请选择租户！'),
      });
      return false;
    }
  }

  // 点击分配
  @Bind()
  handleDistribute() {
    if (this.tableDS.selected.length !== 0) {
      const fdLevelSet = this.tableDS.selected.map((item) => item.toData().fdLevel);
      const organizationFlag = fdLevelSet.every(
        (item) => item === 'organization' || item === 'project'
      );
      if (organizationFlag) {
        this.openModal();
        this.tenantDS.query();
      } else {
        notification.warning({
          message: intl
            .get('hiam.apiManagement.view.message.chooseNotSiteApi')
            .d('请选择非平台级的 API！'),
        });
      }
    } else {
      notification.warning({
        message: intl.get('hiam.apiManagement.view.message.chooseApi').d('请选择需要分配的 API！'),
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
      title: intl.get('hiam.apiManagement.view.title.chooseTenant').d('选择租户'),
      destroyOnClose: true,
      style: {
        width: 600,
      },
      children: (
        <Table
          dataSet={this.tenantDS}
          columns={this.tenantColumns}
          style={{ height: 380 }}
          queryFieldsLimit={2}
        />
      ),
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
    const { tabFlag, initTenantFlag } = this.state;
    if (tabItem === 'tenantApiManagement') {
      this.tenantTableDS.query();
      if (initTenantFlag) {
        this.setState({ initTenantFlag: false });
      }
    }
    this.setState({ tabFlag: !tabFlag });
  }

  // 更改层级
  // @Bind()
  // handleChangeLevel(val) {
  // const tag = this.formDS.getField('tag');
  // tag.setLovPara('fdLevel', val);
  // }

  // API管理页面更多查询切换
  @Bind()
  handleMoreSearchItem() {
    const { apiManagementFlag } = this.state;
    this.setState({ apiManagementFlag: !apiManagementFlag });
  }

  // 租户API管理页面更多查询切换
  @Bind()
  handleTenantMoreSearchItem() {
    const { tenantApiManagementFlag } = this.state;
    this.setState({ tenantApiManagementFlag: !tenantApiManagementFlag });
  }

  // API管理页面查询
  @Bind()
  handleQuery() {
    this.tableDS.query();
  }

  // 租户API管理页面查询
  @Bind()
  handleTenantQuery() {
    this.tenantTableDS.query();
  }

  // API管理回车事件
  @Bind()
  handleEnter() {
    this.tableDS.query();
  }

  // 租户API管理回车事件
  @Bind()
  handleTenantEnter() {
    this.tenantTableDS.query();
  }

  // /**
  //  * 打开刷新IAM权限模态框
  //  */
  // @Bind()
  // openRefreshModal() {
  //   this.refreshDs = new DataSet(refreshDS);
  //   Modal.open({
  //     closable: true,
  //     key: 'refresh-customer-point',
  //     title: intl.get('hiam.apiManagement.view.message.refreshApi').d('刷新IAM权限'),
  //     style: {
  //       width: 500,
  //     },
  //     children: <Drawer refreshDs={this.refreshDs} />,
  //     onOk: this.handleRefresh,
  //     onCancel: () => {
  //       this.refreshDs.reset();
  //     },
  //     onClose: () => {
  //       this.refreshDs.reset();
  //     },
  //   });
  // }

  // /**
  //  * 刷新客户化端点
  //  */
  // @Bind()
  // async handleRefresh() {
  //   const validate = await this.refreshDs.validate();
  //   if (validate) {
  //     try {
  //       await this.refreshDs.submit();
  //     } catch (e) {
  //       // do nothing, ds noticed
  //     }
  //   } else {
  //     return false;
  //   }
  //   this.tableDS.query();
  // }

  @Bind()
  handleReset() {
    if (this.tenantFormDS.current) {
      this.tenantFormDS.current.reset();
    }
  }

  @Bind()
  handleFormDSReset() {
    if (this.formDS.current) {
      this.formDS.current.reset();
    }
  }

  @Bind()
  renderSaas() {
    const {
      match: { path },
    } = this.props;
    const { apiManagementFlag, tenantApiManagementFlag } = this.state;
    const { location } = this.props;
    return (
      <>
        <Header title={intl.get('hiam.apiManagement.view.title.apiManagement').d('API 管理')} />
        <Content>
          <Tabs defaultActiveKey="apiManagement" animated={false} onChange={this.handleChange}>
            <TabPane
              tab={intl.get('hiam.apiManagement.view.title.apiManagement').d('API 管理')}
              key="apiManagement"
            >
              <Row type="flex" className="c7n-form-line-with-btn">
                <Col span={18}>
                  <Form dataSet={this.formDS} columns={3}>
                    <TextField name="code" onEnterDown={this.handleEnter} />
                    <TextField name="path" onEnterDown={this.handleEnter} />
                    <Lov name="serviceNameLov" onEnterDown={this.handleEnter} />
                  </Form>
                </Col>
                <Col
                  span={6}
                  style={{ display: 'flex', justifyContent: 'flex-end' }}
                  className="c7n-form-btn"
                >
                  <div
                    style={{
                      // marginTop: '10px',
                      flexShrink: 0,
                      display: 'flex',
                      alignItems: 'center',
                    }}
                  >
                    <Button onClick={this.handleMoreSearchItem}>
                      {apiManagementFlag
                        ? intl.get('hzero.common.button.collected').d('收起查询')
                        : intl.get('hzero.common.button.viewMore').d('更多查询')}
                    </Button>
                    <Button onClick={this.handleFormDSReset}>
                      {intl.get('hzero.common.button.reset').d('重置')}
                    </Button>
                    <Button color="primary" onClick={this.handleQuery}>
                      {intl.get('hzero.common.button.search').d('查询')}
                    </Button>
                  </div>
                </Col>
                {apiManagementFlag && (
                  <Col span={18}>
                    <Form dataSet={this.formDS} columns={3}>
                      <Select name="fdLevel" onEnterDown={this.handleEnter} />
                      <Select name="method" onEnterDown={this.handleEnter} />
                      <Select name="labels" onEnterDown={this.handleEnter} />
                      <Select name="publicAccess" onEnterDown={this.handleEnter} />
                      <Select name="loginAccess" onEnterDown={this.handleEnter} />
                      <Select name="within" onEnterDown={this.handleEnter} />
                      <Select name="signAccess" onEnterDown={this.handleEnter} />
                    </Form>
                  </Col>
                )}
              </Row>
              <Row type="flex" justify="end" style={{ marginBottom: 14 }}>
                <Col offset={12}>
                  <Popconfirm
                    placement="topRight"
                    title={intl
                      .get('hzero.common.message.confirm.deleteChooseRecord')
                      .d('是否删除选中记录？')}
                    okText={intl.get('hzero.common.button.ok').d('确定')}
                    cancelText={intl.get('hzero.common.button.cancel').d('取消')}
                    onConfirm={this.handleDeleteBatchApi}
                  >
                    <ButtonPermission
                      type="c7n-pro"
                      permissionList={[
                        {
                          code: `${path}.button.sassDelete`,
                          type: 'button',
                          meaning: 'API管理-删除',
                        },
                      ]}
                      icon="delete"
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                  {/* <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}.button.sassRefresh`,
                        type: 'button',
                        meaning: 'API管理-刷新',
                      },
                    ]}
                    icon="sync"
                    onClick={this.openRefreshModal}
                  >
                    {intl.get('hzero.common.button.refresh').d('刷新')}
                  </ButtonPermission> */}
                  <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}.button.sassDistribute`,
                        type: 'button',
                        meaning: 'API管理-分配',
                      },
                    ]}
                    className={styles['customize-button-style']}
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
              tab={intl.get('hiam.apiManagement.view.title.tenantApiManagement').d('租户 API 管理')}
              key="tenantApiManagement"
            >
              <Row type="flex" className="c7n-form-line-with-btn">
                <Col span={18}>
                  <Form dataSet={this.tenantFormDS} columns={3}>
                    <Lov name="tenantIdLov" onEnterDown={this.handleTenantEnter} />
                    <TextField name="code" onEnterDown={this.handleTenantEnter} />
                    <Lov name="serviceNameLov" onEnterDown={this.handleTenantEnter} />
                  </Form>
                </Col>
                <Col
                  span={6}
                  style={{ display: 'flex', justifyContent: 'flex-end' }}
                  className="c7n-form-btn"
                >
                  <div
                    style={{
                      flexShrink: 0,
                      display: 'flex',
                      alignItems: 'center',
                    }}
                  >
                    <Button onClick={this.handleTenantMoreSearchItem}>
                      {tenantApiManagementFlag
                        ? intl.get('hzero.common.button.collected').d('收起查询')
                        : intl.get('hzero.common.button.viewMore').d('更多查询')}
                    </Button>
                    <Button onClick={this.handleReset}>
                      {intl.get('hzero.common.button.reset').d('重置')}
                    </Button>
                    <Button color="primary" onClick={this.handleTenantQuery}>
                      {intl.get('hzero.common.button.search').d('查询')}
                    </Button>
                  </div>
                </Col>
                {tenantApiManagementFlag && (
                  <Col span={18}>
                    <Form dataSet={this.tenantFormDS} columns={3}>
                      <TextField name="path" onEnterDown={this.handleTenantEnter} />
                      <Select name="method" onEnterDown={this.handleTenantEnter} />
                      <Select name="publicAccess" onEnterDown={this.handleEnter} />
                      <Select name="loginAccess" onEnterDown={this.handleEnter} />
                      <Select name="within" onEnterDown={this.handleEnter} />
                      <Select name="signAccess" onEnterDown={this.handleEnter} />
                      <Select name="labels" onEnterDown={this.handleEnter} />
                    </Form>
                  </Col>
                )}
              </Row>
              <Row type="flex" justify="end" style={{ marginBottom: 14 }}>
                <Col offset={18}>
                  <Popconfirm
                    placement="topRight"
                    title={intl
                      .get('hzero.common.message.confirm.deleteChooseRecord')
                      .d('是否删除选中记录？')}
                    okText={intl.get('hzero.common.button.ok').d('确定')}
                    cancelText={intl.get('hzero.common.button.cancel').d('取消')}
                    onConfirm={this.handleDeleteSingleTenantApi}
                  >
                    <ButtonPermission
                      type="c7n-pro"
                      permissionList={[
                        {
                          code: `${path}.button.sassDelete`,
                          type: 'button',
                          meaning: 'API管理-删除',
                        },
                      ]}
                      icon="delete"
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                </Col>
              </Row>
              <Table dataSet={this.tenantTableDS} columns={this.tenantApiColumns} />
            </TabPane>
          </Tabs>
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }

  @Bind()
  renderTenant() {
    const {
      match: { path },
    } = this.props;
    const { apiManagementFlag } = this.state;
    const { location } = this.props;
    return (
      <>
        <Header title={intl.get('hiam.apiManagement.view.title.apiManagement').d('API 管理')} />
        <Content>
          <Row>
            <Col span={18}>
              <Form dataSet={this.formDS} columns={3}>
                <TextField name="code" onEnterDown={this.handleEnter} />
                <TextField name="path" onEnterDown={this.handleEnter} />
                <Lov name="serviceNameLov" onEnterDown={this.handleEnter} />
              </Form>
            </Col>
            <Col span={6} style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <div
                style={{
                  marginTop: '10px',
                  flexShrink: 0,
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                <Button onClick={this.handleMoreSearchItem}>
                  {apiManagementFlag
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get('hzero.common.button.viewMore').d('更多查询')}
                </Button>
                <Button onClick={this.handleFormDSReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button color="primary" onClick={this.handleQuery}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </div>
            </Col>
            {apiManagementFlag && (
              <Col span={18}>
                <Form dataSet={this.formDS} columns={3}>
                  <Select name="fdLevel" onEnterDown={this.handleEnter} />
                  <Select name="method" onEnterDown={this.handleEnter} />
                  <Select name="labels" onEnterDown={this.handleEnter} />
                  <Select name="publicAccess" onEnterDown={this.handleEnter} />
                  <Select name="loginAccess" onEnterDown={this.handleEnter} />
                  <Select name="within" onEnterDown={this.handleEnter} />
                  <Select name="signAccess" onEnterDown={this.handleEnter} />
                </Form>
              </Col>
            )}
          </Row>
          <Row type="flex" justify="end" style={{ marginBottom: 14 }}>
            <Col offset={12}>
              <Popconfirm
                placement="topRight"
                title={intl
                  .get('hzero.common.message.confirm.deleteChooseRecord')
                  .d('是否删除选中记录？')}
                okText={intl.get('hzero.common.button.ok').d('确定')}
                cancelText={intl.get('hzero.common.button.cancel').d('取消')}
                onConfirm={this.handleDeleteBatchApi}
              >
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}.button.tenantDelete`,
                      type: 'button',
                      meaning: 'API管理-删除',
                    },
                  ]}
                  icon="delete"
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              </Popconfirm>
              {/* <ButtonPermission
                type="c7n-pro"
                permissionList={[
                  {
                    code: `${path}.button.tenantRefresh`,
                    type: 'button',
                    meaning: 'API管理-刷新',
                  },
                ]}
                className={styles['customize-button-style']}
                icon="sync"
                onClick={this.openRefreshModal}
              >
                {intl.get('hzero.common.button.refresh').d('刷新')}
              </ButtonPermission> */}
            </Col>
          </Row>
          <Table dataSet={this.tableDS} columns={this.columns} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }

  render() {
    return isTenantRoleLevel() ? this.renderTenant() : this.renderSaas();
  }
}
