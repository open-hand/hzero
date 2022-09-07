/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/26
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: OTA升级包列表页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Badge } from 'choerodon-ui';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import {
  fields,
  otaUpgradePackageListDS,
  otaUpgradePackageStatusCtrlDS,
  otaUpgradePackageDetailBasicDS,
} from '@/stores/otaUpgradePackageDS';
import Drawer from './Drawer';
import CreateOTAUpgradePackage from './CreateOTAUpgradePackage';

const prefix = 'hiot.otaPackage';
const modalKey = Modal.key();

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class OTAUpgradePackage extends Component {
  constructor(props) {
    super(props);
    this.otaUpgradePackageListDS = new DataSet(otaUpgradePackageListDS());
    this.otaUpgradePackageStatusCtrlDS = new DataSet(otaUpgradePackageStatusCtrlDS());
    this.otaUpgradePackageDetailBasicDs = null;
  }

  /**
   * 跳转详情页
   */
  @Bind()
  handleDetail(id) {
    const { history } = this.props;
    history.push(`/hiot/ota-upgrade/package/detail/${id}`);
  }

  /**
   * 处理“创建升级任务”
   */
  @Bind()
  handleCreatePackage(record) {
    const { history } = this.props;
    const { packageId: id } = record;
    // 销毁已经打开的窗口重新打开以刷新数据
    window.dvaApp._store
      .dispatch({
        type: 'global/removeTab',
        payload: '/hiot/ota-upgrade/task',
      })
      .then(() => {
        history.push({
          pathname: `/hiot/ota-upgrade/task/create/${id}`,
          state: {
            backPath: '/hiot/ota-upgrade/package/list',
            template: record,
          },
        });
      });
  }

  /**
   * 新建升级包
   */
  @Bind()
  handleNew() {
    Modal.open({
      key: modalKey,
      drawer: true,
      closable: true,
      title: intl.get(`${prefix}.create.upgrade.package`).d('创建OTA升级包'),
      children: (
        <CreateOTAUpgradePackage
          onRef={(child) => {
            this.createComp = child;
          }}
        />
      ),
      onOk: () =>
        this.createComp.handleSubmit().then((validRes) => {
          if (validRes) {
            this.otaUpgradePackageListDS.query();
          } else {
            notification.error({
              message: intl.get(`${prefix}.view.message.valid.error`).d('数据校验不通过'),
            });
          }
          return !!validRes;
        }),
    });
  }

  @Bind()
  handleStatusChange(record) {
    const data = record.toData();
    this.otaUpgradePackageStatusCtrlDS.create(data, 0);
    this.otaUpgradePackageStatusCtrlDS.submit().then(() => {
      this.otaUpgradePackageStatusCtrlDS.create({}, 0);
      this.otaUpgradePackageListDS.query();
    });
  }

  @Bind()
  handleEdit(packageId) {
    this.otaUpgradePackageDetailBasicDs = new DataSet(otaUpgradePackageDetailBasicDS());
    this.otaUpgradePackageDetailBasicDs.create({});
    const drawerProps = {
      packageId,
      ds: this.otaUpgradePackageDetailBasicDs,
    };
    Modal.open({
      closable: true,
      key: 'edit-package',
      title: intl.get(`${prefix}.create.upgrade.editPackage`).d('编辑OTA升级包'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <Drawer {...drawerProps} />,
      onOk: this.handleSave,
    });
  }

  @Bind()
  async handleSave() {
    const validate = await this.otaUpgradePackageDetailBasicDs.submit();
    if (!validate) {
      if (!this.otaUpgradePackageDetailBasicDs.current.get('attachmentUrl')) {
        notification.error({
          message: intl.get(`${prefix}.view.message.validFileError`).d('请上传升级包'),
        });
      }
      return false;
    }
    await this.otaUpgradePackageListDS.query();
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      {
        name: 'packageName',
        width: 200,
      },
      {
        name: 'currentVersion',
      },
      {
        name: 'categoryCodeMeaning',
      },
      {
        name: 'protocolService',
      },
      {
        name: 'enabledFlag',
        align: 'left',
        renderer: ({ value }) =>
          value ? (
            <Badge status="success" text={intl.get('hzero.common.button.enable').d('启用')} />
          ) : (
            <Badge status="error" text={intl.get('hzero.common.button.disable').d('禁用')} />
          ),
      },
      {
        name: 'thingModelName',
        width: 200,
      },
      {
        name: 'thingModelCode',
        width: 150,
      },
      {
        name: 'updateLogs',
        width: 230,
      },
      {
        name: 'creationDate',
        width: 180,
      },
      {
        name: 'action',
        width: 200,
        lock: 'right',
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => {
          const recordData = record.toData();
          const { packageId: id } = recordData;
          const detailView = intl.get('hzero.common.button.detail').d('详情');
          const upgradeTaskView = intl.get(`${prefix}.create.upgrade.task`).d('创建升级任务');
          const isDisabled = Number(record.get(fields.status().name));
          const operators = [
            {
              key: 'edit',
              len: 2,
              title: detailView,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: 'OTA升级包-编辑',
                    },
                  ]}
                  onClick={() => this.handleEdit(id)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
            },
            {
              key: 'detail',
              len: 2,
              title: detailView,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.detail`,
                      type: 'button',
                      meaning: 'OTA升级包-详情',
                    },
                  ]}
                  onClick={() => this.handleDetail(id)}
                >
                  {detailView}
                </ButtonPermission>
              ),
            },
            isDisabled && {
              key: 'createUpgradeTask',
              len: 6,
              title: upgradeTaskView,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.upgrade`,
                      type: 'button',
                      meaning: 'OTA升级包-创建升级任务',
                    },
                  ]}
                  onClick={() => this.handleCreatePackage(recordData)}
                >
                  {upgradeTaskView}
                </ButtonPermission>
              ),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get(`${prefix}.view.upgrade.package`).d('OTA升级包')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: 'OTA升级包-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleNew()}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            selectionMode="click"
            dataSet={this.otaUpgradePackageListDS}
            queryFieldsLimit={3}
            columns={columns}
          />
        </Content>
      </>
    );
  }
}
