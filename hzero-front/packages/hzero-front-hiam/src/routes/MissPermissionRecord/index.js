/**
 * @since 2019-12-20
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { withRouter } from 'dva/router';
import { DataSet, Table, Form, Button, Modal, TextField, Select, Lov } from 'choerodon-ui/pro';
import { Row, Col, Tag } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import withProps from 'utils/withProps';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import {
  checkStatusFormConfigDS,
  searchFormConfigDS,
  tableConfigDS,
  permissionSetDS,
  clearPermissionRecordFormDS,
} from '@/stores/missPermissionRecordDS';
import {
  clearPermissionRecordInterface,
  refreshPermissionSet,
  refreshMenuPermissionSet,
  patchAddPermissionInterface,
} from '@/services/missPermissionRecordService';
import styles from './index.less';

@withRouter
@formatterCollections({ code: ['hiam.missPermission'] })
@withProps(
  // checkStatusFormDS 注入 到 this.porps
  () => {
    const checkStatusFormDS = new DataSet(checkStatusFormConfigDS());
    const searchFormDS = new DataSet(searchFormConfigDS());
    const tableDS = new DataSet(tableConfigDS(checkStatusFormDS, searchFormDS));
    checkStatusFormDS.buttonRefreshFlag = true;
    checkStatusFormDS.buttonAddPermissionFlag = false;
    checkStatusFormDS.buttonMenuRefreshFlag = false;
    return {
      checkStatusFormDS,
      searchFormDS,
      tableDS,
    };
  },
  { cacheState: true }
)
export default class MissPermissionRecord extends React.Component {
  constructor(props) {
    super(props);
    this.permissionSetDS = new DataSet(permissionSetDS());
    this.clearPermissionRecordFormDS = new DataSet(clearPermissionRecordFormDS());
    this.state = {
      moreSearchFlag: false,
      buttonRefreshFlag: null,
      buttonAddPermissionFlag: null,
      buttonMenuRefreshFlag: null,
    };
  }

  componentDidMount() {
    const {
      buttonAddPermissionFlag,
      buttonRefreshFlag,
      buttonMenuRefreshFlag,
    } = this.props.checkStatusFormDS;
    this.setState({ buttonRefreshFlag, buttonAddPermissionFlag, buttonMenuRefreshFlag });
  }

  // 添加权限集列
  get permissionSetColumns() {
    return [
      {
        name: 'code',
        width: 280,
      },
      {
        name: 'name',
      },
      {
        name: 'levelMeaning',
        width: 100,
      },
      {
        name: 'parentName',
      },
    ];
  }

  // 缺失权限管理页面Table列
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'apiPath',
        width: 310,
      },
      {
        width: 100,
        name: 'apiMethodMeaning',
      },
      {
        name: 'permissionCode',
        width: 295,
      },
      {
        name: 'handleStatusMeaning',
        width: 100,
        renderer: ({ value, record }) => {
          const { handleStatus = null } = record.toData();
          if (!handleStatus) {
            return;
          }
          return handleStatus === 'PROCESSED' ? (
            <Tag color="green">{value}</Tag>
          ) : (
            <Tag color="magenta">{value}</Tag>
          );
        },
      },
      {
        name: 'serviceName',
        width: 200,
      },
      {
        name: 'menuName',
        width: 100,
      },
      {
        name: 'levelMeaning',
        width: 100,
      },
      {
        name: 'permissionType',
      },
      {
        name: 'creationDate',
        width: 160,
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 70,
        renderer: ({ record }) => {
          let actions = [];
          actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.detail`,
                      type: 'button',
                      meaning: '缺失权限-详情',
                    },
                  ]}
                  onClick={() => this.handleDetail(record)}
                >
                  {intl.get('hzero.common.button.detail').d('详情')}
                </ButtonPermission>
              ),
              key: 'detail',
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'left',
      },
    ];
  }

  /**
   * 跳转详情页
   */
  @Bind()
  handleDetail(record) {
    const { permissionCheckId = '' } = record.toData();
    const {
      history,
      location: { search },
      match,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const router = {
      pathname:
        match.path.indexOf('/private') === 0
          ? `/private/hiam/miss-permission-record/detail/${permissionCheckId}`
          : `/hiam/miss-permission-record/detail/${permissionCheckId}`,
      search: match.path.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
    };
    history.push(router);
  }

  /**
   * 批量添加权限
   */
  @Bind()
  handlePatchAddPermissionSet() {
    const { tableDS } = this.props;
    if (tableDS.selected.length !== 0) {
      const processedFlag = tableDS.selected
        .map((item) => item.toData().handleStatus)
        .includes('PROCESSED');
      if (!processedFlag) {
        const levelSet = tableDS.selected.map((item) => item.toData().fdLevel);
        const level = levelSet[0];
        const tempFlag = levelSet.every((item) => item === level);
        if (tempFlag) {
          const permissionCodes = tableDS.selected.map((item) => item.toData().permissionCode);
          this.permissionSetDS.setQueryParameter('level', level);
          this.permissionSetDS.query();
          this.openAddPermissionSetModal(
            { permissionCodes, level, checkState: tableDS.selected[0].toData().checkState },
            'patch'
          );
        } else {
          notification.warning({
            message: intl
              .get('hiam.missPermission.message.missPermission.sameLevel')
              .d('请选择同一权限层级的项！'),
          });
        }
      } else {
        notification.warning({
          message: intl
            .get('hzero.common.notification.noAddPermission')
            .d('请选择未添加权限的项！'),
        });
      }
    } else {
      notification.warning({
        message: intl
          .get('hiam.missPermission.message.mp.choosePermissionItem')
          .d('请选择需要添加权限的项！'),
      });
    }
  }

  // 确认批量添加权限集
  @Bind()
  async handlePatchPermissionOk(record) {
    if (this.permissionSetDS.selected.length !== 0) {
      const { checkState, permissionCodes, level } = record;
      const menuIds = this.permissionSetDS.selected.map((item) => item.toData().id);
      const res = await patchAddPermissionInterface({
        level,
        checkState,
        menuIds,
        permissionCodes,
        permissionType: 'PERMISSION',
      });
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      } else if (res && !res.failed) {
        notification.success({
          message: intl.get('hzero.common.notification.success').d('操作成功'),
        });
        this.props.tableDS.query();
      }
      return true;
    }
    notification.warning({
      message: intl
        .get('hiam.missPermission.message.missPermission.choosePermission')
        .d('请选择需要添加的权限！'),
    });
    return false;
  }

  /**
   * 打开添加权限集模态框
   */
  @Bind()
  openAddPermissionSetModal(record, type = '') {
    Modal.open({
      key: 'add-permission-modal',
      title: intl.get('hiam.missPermission.model.missPermission.addPermissionSet').d('添加权限集'),
      destroyOnClose: true,
      style: {
        width: 700,
      },
      children: (
        <Table
          dataSet={this.permissionSetDS}
          columns={this.permissionSetColumns}
          style={{ height: 380 }}
          queryFieldsLimit={2}
        />
      ),
      onOk: () =>
        type !== 'patch' ? this.handlePermissionOk(record) : this.handlePatchPermissionOk(record),
      onCancel: () => true,
      afterClose: () => {
        this.permissionSetDS.reset();
        this.permissionSetDS.unSelectAll();
        this.permissionSetDS.clearCachedSelected();
        this.permissionSetDS.queryDataSet.reset();
      },
    });
  }

  /**
   * 清理缺失权限记录
   */
  @Bind()
  handleClearPermissionRecord() {
    this.clearPermissionRecordFormDS.create({});
    Modal.open({
      title: intl.get('hiam.missPermission.view.title.clearPermissionRecord').d('清理缺失权限记录'),
      drawer: true,
      width: 520,
      children: (
        <Form dataSet={this.clearPermissionRecordFormDS}>
          <Select name="checkType" />
          <Select name="clearType" />
        </Form>
      ),
      onOk: async () => {
        if (await this.clearPermissionRecordFormDS.validate()) {
          const {
            checkType = null,
            clearType = null,
          } = this.clearPermissionRecordFormDS.current.toData();
          const res = await clearPermissionRecordInterface({ checkType, clearType });
          if (res && res.failed) {
            notification.error({
              message: res.message,
            });
          } else if (res && !res.failed) {
            notification.success({
              message: intl.get('hzero.common.notification.success').d('操作成功'),
            });
            this.props.tableDS.query();
          }
          return res;
        }
        return false;
      },
      onCancel: () => true,
      afterClose: () => this.clearPermissionRecordFormDS.reset(),
    });
  }

  // 切换状态
  @Bind()
  handleChangeCheckState(value) {
    if (value === 'PERMISSION_MISMATCH') {
      this.props.checkStatusFormDS.buttonRefreshFlag = true;
      this.props.checkStatusFormDS.buttonAddPermissionFlag = false;
      this.props.checkStatusFormDS.buttonMenuRefreshFlag = false;
      this.setState({
        buttonRefreshFlag: true,
        buttonAddPermissionFlag: false,
        buttonMenuRefreshFlag: false,
      });
    } else if (value === 'PERMISSION_NOT_PASS') {
      this.props.checkStatusFormDS.buttonRefreshFlag = false;
      this.props.checkStatusFormDS.buttonAddPermissionFlag = true;
      this.props.checkStatusFormDS.buttonMenuRefreshFlag = false;
      this.setState({
        buttonRefreshFlag: false,
        buttonAddPermissionFlag: true,
        buttonMenuRefreshFlag: false,
      });
    } else if (value === 'PERMISSION_MENU_MISMATCH') {
      this.props.checkStatusFormDS.buttonRefreshFlag = false;
      this.props.checkStatusFormDS.buttonAddPermissionFlag = false;
      this.props.checkStatusFormDS.buttonMenuRefreshFlag = true;
      this.setState({
        buttonRefreshFlag: false,
        buttonAddPermissionFlag: false,
        buttonMenuRefreshFlag: true,
      });
    } else {
      this.props.checkStatusFormDS.buttonRefreshFlag = false;
      this.props.checkStatusFormDS.buttonAddPermissionFlag = false;
      this.props.checkStatusFormDS.buttonMenuRefreshFlag = false;
      this.setState({
        buttonRefreshFlag: false,
        buttonAddPermissionFlag: false,
        buttonMenuRefreshFlag: false,
      });
    }
    this.props.tableDS.query();
  }

  // 页面更多查询切换
  @Bind()
  handleMoreSearchItem() {
    const { moreSearchFlag } = this.state;
    this.setState({ moreSearchFlag: !moreSearchFlag });
  }

  // 重置
  @Bind()
  handleReset() {
    if (this.props.searchFormDS.current) {
      this.props.searchFormDS.current.reset();
    }
  }

  // 权限缺失查询查询
  @Bind()
  handleSearch() {
    this.props.tableDS.query();
  }

  // 点击批量刷新权限
  @Bind()
  async handleRefresh() {
    const { tableDS } = this.props;
    if (tableDS.selected.length !== 0) {
      const tempFlag = tableDS.selected
        .map((item) => item.toData().handleStatus)
        .includes('PROCESSED');
      if (!tempFlag) {
        const serviceNameSet = tableDS.selected.map((item) => item.toData().serviceName);
        const res = await refreshPermissionSet(serviceNameSet);
        if (res && res.failed) {
          notification.error({
            message: res.message,
          });
        } else if (res && !res.failed) {
          notification.success({
            message: intl.get('hzero.common.notification.success').d('操作成功'),
          });
          tableDS.query();
        }
      } else {
        notification.warning({
          message: intl.get('hzero.common.notification.noFresh').d('请选择未刷新的项！'),
        });
      }
    } else {
      notification.warning({
        message: intl
          .get('hiam.missPermission.view.message.chooseServiceItems')
          .d('请选择需要刷新的服务项！'),
      });
    }
  }

  // 点击批量刷新权限
  @Bind()
  async handleMenuRefresh() {
    const { tableDS } = this.props;
    if (tableDS.selected.length !== 0) {
      const processedFlag = tableDS.selected
        .map((item) => item.toData().handleStatus)
        .includes('PROCESSED');
      if (!processedFlag) {
        // const levelSet = tableDS.selected.map(item => item.toData().fdLevel);
        // const level = levelSet[0];
        // const tempFlag = levelSet.every(item => item === level);
        // if (tempFlag) {
        // const typeSet = tableDS.selected.map(item => item.toData().permissionType);
        // const type = typeSet[0];
        // const typeFlag = typeSet.every(item => item === type);
        // if (typeFlag) {
        const serviceNameSet = tableDS.selected.map((item) => item.toData());
        const obj = {
          // level: serviceNameSet[0].fdLevel,
          permissionCodes: serviceNameSet.map((item) => item.permissionCode),
          // menuIds: serviceNameSet.map(item => item.menuId),
          // permissionType: 'PERMISSION',
        };
        const res = await refreshMenuPermissionSet(obj);
        if (res && res.failed) {
          notification.error({
            message: res.message,
          });
        } else if (res && !res.failed) {
          notification.success({
            message: intl.get('hzero.common.notification.success').d('操作成功'),
          });
          tableDS.query();
        }
        // } else {
        //   notification.warning({
        //     message: intl
        //       .get('hiam.missPermission.message.missPermission.sameType')
        //       .d('请选择同一权限类型的项！'),
        //   });
        // }
        // } else {
        //   notification.warning({
        //     message: intl
        //       .get('hiam.missPermission.message.missPermission.sameLevel')
        //       .d('请选择同一权限层级的项！'),
        //   });
        // }
      } else {
        notification.warning({
          message: intl.get('hzero.common.notification.noFresh').d('请选择未刷新的项！'),
        });
      }
    } else {
      notification.warning({
        message: intl
          .get('hiam.missPermission.view.message.chooseServiceItems')
          .d('请选择需要刷新的服务项！'),
      });
    }
  }

  render() {
    const {
      searchFormDS,
      tableDS,
      match: { path },
    } = this.props;
    const {
      moreSearchFlag,
      buttonRefreshFlag,
      buttonAddPermissionFlag,
      buttonMenuRefreshFlag,
    } = this.state;
    return (
      <>
        <Header
          title={intl.get('hiam.missPermission.view.title.missPermissionControl').d('缺失权限管理')}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.clearPermissionRecord`,
                type: 'button',
                meaning: '缺失权限-清理缺失权限记录',
              },
            ]}
            className={styles['customize-button-style']}
            icon="delete"
            onClick={this.handleClearPermissionRecord}
          >
            {intl.get('hiam.missPermission.view.title.clearPermissionRecord').d('清理缺失权限记录')}
          </ButtonPermission>
          <Select
            dataSet={this.props.checkStatusFormDS}
            name="checkState"
            style={{ width: '210px' }}
            placeholder={intl.get('hiam.missPermission.view.message.chooseStatus').d('请选择状态')}
            onChange={this.handleChangeCheckState}
          />
        </Header>
        <Content>
          <Row type="flex" className="c7n-form-line-with-btn">
            <Col span={18}>
              <Form dataSet={searchFormDS} columns={3}>
                <Select name="permissionType" />
                <Select name="fdLevel" />
                <Select name="handleStatus" />
              </Form>
            </Col>
            <Col
              span={6}
              className="c7n-form-btn"
              style={{ display: 'flex', justifyContent: 'flex-end' }}
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
                  {intl.get('hzero.common.button.viewMore').d('更多查询')}
                </Button>
                <Button onClick={this.handleReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button color="primary" onClick={this.handleSearch}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </div>
            </Col>
            <Col span={18}>
              <Form dataSet={searchFormDS} columns={3}>
                {moreSearchFlag ? (
                  <Lov name="serviceNameLov" onEnterDown={this.handleTenantEnter} />
                ) : null}
                {moreSearchFlag ? (
                  <TextField name="menuName" onEnterDown={this.handleTenantEnter} />
                ) : null}

                {moreSearchFlag ? (
                  <TextField name="apiPath" onEnterDown={this.handleTenantEnter} />
                ) : null}
                {moreSearchFlag ? <Select name="apiMethod" /> : null}
              </Form>
            </Col>
          </Row>
          <Row type="flex" justify="end" style={{ marginBottom: 14 }}>
            <Col offset={12}>
              {buttonRefreshFlag ? (
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}.button.refreshPermission`,
                      type: 'button',
                      meaning: '缺失权限-刷新权限',
                    },
                  ]}
                  icon="sync"
                  className={styles['customize-button-style']}
                  onClick={this.handleRefresh}
                >
                  {intl.get('hiam.missPermission.view.button.refreshPermission').d('刷新权限')}
                </ButtonPermission>
              ) : null}
              {buttonAddPermissionFlag ? (
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}.button.addPermissionSet`,
                      type: 'button',
                      meaning: '缺失权限-添加权限集',
                    },
                  ]}
                  className={styles['customize-button-style']}
                  icon="add"
                  onClick={this.handlePatchAddPermissionSet}
                >
                  {intl.get('hiam.missPermission.view.button.addPermissionSet').d('添加权限集')}
                </ButtonPermission>
              ) : null}
              {buttonMenuRefreshFlag ? (
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}.button.refreshMenuPermission`,
                      type: 'button',
                      meaning: '缺失权限-刷新菜单权限',
                    },
                  ]}
                  icon="sync"
                  className={styles['customize-button-style']}
                  onClick={this.handleMenuRefresh}
                >
                  {intl.get('hiam.missPermission.view.button.refreshPermission').d('刷新权限')}
                </ButtonPermission>
              ) : null}
            </Col>
          </Row>
          <Table dataSet={tableDS} columns={this.columns} />
        </Content>
      </>
    );
  }
}
