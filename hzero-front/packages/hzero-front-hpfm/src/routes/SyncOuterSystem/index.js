import React from 'react';
import { DataSet, Modal, ModalContainer, Table } from 'choerodon-ui/pro';

import { CustBoxC7N as CustButton, WithCustomizeC7N as WithCustomize } from 'components/Customize';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { Bind } from 'lodash-decorators';
import { getPublicKey } from 'services/api';

import {
  handleUpdateDs,
  handleUpdateDsLocal,
  tableDetailDs,
  tableDs,
} from '../../stores/syncOuterSystemDS';
import Drawer from './Drawer';

@formatterCollections({ code: ['hpfm.syncOuterSystem'] })
@WithCustomize({
  unitCode: [
    'HPFM.SYNC_OUTER_SYSTEM.ORG_INFO.LINE',
    'HPFM.SYNC_OUTER_SYSTEM.EDIT_FORM',
    'HPFM.SYNC_OUTER_SYSTEM.FILTER',
  ],
})
export default class SyncOuterSystem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      publicKey: '', // 密码公钥
    };
  }

  tableDs = new DataSet(tableDs());

  tableDetailDs = new DataSet(tableDetailDs());

  handleUpdateDs = new DataSet(handleUpdateDs());

  handleUpdateDsLocal = new DataSet(handleUpdateDsLocal());

  componentDidMount() {
    this.fetchPublicKey();
  }

  get columns() {
    return [
      {
        name: 'syncTypeCode',
        header: intl.get('hpfm.syncOuterSystem.model.sync.syncTypeCodeMeaning').d('同步类型'),
        width: 100,
      },
      {
        name: 'appId',
        header: intl.get('hpfm.syncOuterSystem.model.sync.appId').d('用户凭证'),
        width: 200,
      },
      {
        name: 'authTypeMeaning',
        header: intl.get('hpfm.syncOuterSystem.model.sync.authType').d('授权类型'),
        width: 150,
      },
      {
        name: 'authAddress',
        header: intl.get('hpfm.syncOuterSystem.model.sync.authAddress').d('三方授权地址'),
      },
      {
        name: 'enabledFlag',
        header: intl.get('hzero.common.status').d('状态'),
        width: 100,
        renderer: ({ value }) => enableRender(value),
      },
      {
        name: 'opeation',
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 300,
        renderer: ({ record }) => {
          const {
            match: { path },
          } = this.props;
          const actions = [];
          actions.push(
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '组织信息同步-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleEdit(false, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/seeLog`,
                      type: 'button',
                      meaning: '组织信息同步-查看日志',
                    },
                  ]}
                  onClick={() => {
                    this.handleSeeLog(record);
                  }}
                >
                  {intl.get('hpfm.syncOuterSystem.view.button.seeLog').d('查看日志')}
                </ButtonPermission>
              ),
              key: 'seeLog',
              len: 4,
              title: intl.get('hpfm.syncOuterSystem.view.button.seeLog').d('查看日志'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/syncManual`,
                      type: 'button',
                      meaning: '组织信息同步-手动同步',
                    },
                  ]}
                  onClick={() => {
                    this.handleUpdate(record);
                  }}
                >
                  {intl.get('hpfm.syncOuterSystem.view.button.toOuter').d('平台到外部')}
                </ButtonPermission>
              ),
              key: 'syncManual',
              len: 5,
              title: intl.get('hpfm.syncOuterSystem.view.button.toOuter').d('平台到外部'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/syncToPlatform`,
                      type: 'button',
                      meaning: '组织信息同步-同步到平台',
                    },
                  ]}
                  onClick={() => {
                    this.handleSyncToPlatform(record);
                  }}
                >
                  {intl.get('hpfm.syncOuterSystem.view.button.toPlatform').d('外部到平台')}
                </ButtonPermission>
              ),
              key: 'syncToPlatform',
              len: 5,
              title: intl.get('hpfm.syncOuterSystem.view.button.toPlatform').d('外部到平台'),
            }
          );
          return [operatorRender(actions, record, { limit: 4 })];
        },
        lock: 'right',
      },
    ];
  }

  @Bind()
  handleEdit(isCreate, record) {
    this.tableDetailDs.create({});
    const currentEditData = record && record.toData();
    Modal.open({
      closable: true,
      key: 'customer-group',
      title: isCreate
        ? intl.get('hpfm.syncOuterSystem.view.message.title.crete').d('新建')
        : intl.get('hpfm.syncOuterSystem.view.message.title.edit').d('编辑'),
      drawer: true,
      style: {
        width: 520,
      },
      children: (
        <Drawer
          currentEditData={currentEditData}
          isCreate={isCreate}
          tableDetailDs={this.tableDetailDs}
          customizeForm={this.props.customizeForm}
        />
      ),
      onOk: this.handleOk,
      onCancel: () => {
        this.tableDetailDs.reset();
      },
      onClose: () => {
        this.tableDetailDs.reset();
      },
    });
  }

  /**
   * 模态框确定
   */
  @Bind()
  async handleOk() {
    try {
      const { publicKey } = this.state;
      this.tableDetailDs.setQueryParameter('publicKey', publicKey);
      const validate = await this.tableDetailDs.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    this.tableDetailDs.setQueryParameter('publicKey', '');
    this.tableDs.query();
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    getPublicKey().then((res) => {
      if (res) {
        this.setState({
          publicKey: res.publicKey,
        });
      }
    });
  }

  @Bind()
  handleUpdate(record) {
    this.handleUpdateDs.requestData = record.toData();
    this.handleUpdateDs.query().then(() => {
      notification.success();
    });
  }

  @Bind()
  handleSyncToPlatform(record) {
    this.handleUpdateDsLocal.requestData = record.toData();
    this.handleUpdateDsLocal.query().then(() => {
      notification.success();
    });
  }

  @Bind()
  handleSeeLog(record) {
    const { history } = this.props;
    const {
      data: { syncId },
    } = record;
    history.push(`/hpfm/sync-to-outer-system/detail/${syncId}`);
  }

  render() {
    const {
      location,
      match: { path },
      customizeTable = () => {},
    } = this.props;
    return (
      <>
        <Header
          title={intl.get('hpfm.syncOuterSystem.view.message.title.syncOuterSys').d('通讯录同步')}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/create`,
                type: 'button',
                meaning: '组织信息同步-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleEdit(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <CustButton
            unit={[
              {
                code: 'HPFM.SYNC_OUTER_SYSTEM.ORG_INFO.LINE',
              },
            ]}
          />
        </Header>
        <Content>
          {customizeTable(
            {
              code: 'HPFM.SYNC_OUTER_SYSTEM.ORG_INFO.LINE',
              filterCode: 'HPFM.SYNC_OUTER_SYSTEM.FILTER',
            },
            <Table dataSet={this.tableDs} columns={this.columns} queryBar="none" />
          )}
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
