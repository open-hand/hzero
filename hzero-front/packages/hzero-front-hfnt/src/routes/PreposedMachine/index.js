/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/4/26
 * @copyright HAND ® 2020
 */
import React from 'react';
import { connect } from 'dva';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal, TextArea } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { operatorRender, TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import PREPOSED_MACHINE_LANG from '@/langs/preposedMachineLang';
import { tableDS, drawerFormDS, programListDS } from '@/stores/PreposedMachine/PreposedMachineDS';
import { FRONTAL_MACHINE_STATUS, FRONTAL_SERVER_TAG_STATUS } from '@/constants/constants';
import EditDrawer from './EditDrawer';
import FRONTAL_MANAGEMENT_LANG from '@/langs/frontalManagementLang';

@connect(() => ({
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hfnt.preposedMachine'] })
export default class PreposedMachine extends React.Component {
  modal;

  constructor(props) {
    super(props);
    this.state = {
      currentStatusCode: 'OFFLINE',
    };
    this.tableDS = new DataSet(tableDS);
    this.drawerFormDS = new DataSet(drawerFormDS);
    this.programListDS = new DataSet(programListDS);
  }

  /**
   * 查询
   */
  async handleFetchDetail() {
    await this.tableDS.query();
  }

  /**
   * 新建
   */
  @Bind()
  handleCreate() {
    const { tenantRoleLevel, currentTenantId } = this.props;
    this.drawerFormDS.create({
      statusCode: 'NEW',
      tenantId: tenantRoleLevel ? currentTenantId : null,
    });
    this.openConsumerDrawer(true);
  }

  /**
   * 打开前置机滑窗
   */
  openConsumerDrawer(isNew, record) {
    const {
      currentTenantId,
      tenantRoleLevel,
      match: { path },
    } = this.props;
    const preposedMachineProps = {
      isNew,
      record,
      currentTenantId,
      tenantRoleLevel,
      drawerFormDS: this.drawerFormDS,
    };
    this.modal = Modal.open({
      title: isNew ? PREPOSED_MACHINE_LANG.CREATE_LINE : PREPOSED_MACHINE_LANG.EDIT_LINE,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 450 },
      children: <EditDrawer {...preposedMachineProps} />,
      okText: PREPOSED_MACHINE_LANG.SAVE,
      afterClose: () => this.drawerFormDS.reset(),
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '前置机配置-保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            onClick={this.handleOk}
            disabled={!isNew && !FRONTAL_MACHINE_STATUS.EDIT.includes(record.get('statusCode'))}
          >
            {PREPOSED_MACHINE_LANG.SAVE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 前置机保存
   */
  @Bind()
  async handleOk() {
    if (this.drawerFormDS.current.status === 'sync') {
      notification.warning({
        message: PREPOSED_MACHINE_LANG.SAVE_EMPTY,
      });
      return Promise.reject();
    }
    const validate = await this.drawerFormDS.validate();
    if (!validate) {
      notification.error({
        message: PREPOSED_MACHINE_LANG.SAVE_VALIDATE,
      });
      return Promise.reject();
    }
    const res = await this.drawerFormDS.submit();
    if (res) {
      this.handleFetchDetail();
      this.modal.close();
    } else {
      return Promise.reject();
    }
    return Promise.resolve();
  }

  /**
   * 启用/禁用
   */
  @Bind()
  async handleToggle(record, requestType) {
    record.set('_requestType', requestType);
    await this.tableDS.submit();
  }

  _modal;

  @Bind()
  async openProgramListModal(record) {
    const { frontalId, statusCode } = record.toData();
    this.programListDS.setQueryParameter('frontalId', frontalId);
    await this.setState({ currentStatusCode: statusCode });
    await this.programListDS.query();
    this._modal = Modal.open({
      key: Modal.key(),
      title: PREPOSED_MACHINE_LANG.PROGRAM_LIST,
      children: <Table dataSet={this.programListDS} columns={this.programListColumns} />,
      style: {
        width: 900,
      },
    });
  }

  @Bind()
  async handleUnload(statusCode, record) {
    record.set('isLoad', true);
    await this.programListDS.submit();
    this._modal.update({
      children: <Table dataSet={this.programListDS} columns={this.programListColumns} />,
    });
  }

  @Bind()
  async handleLoad(statusCode, record) {
    record.set('isLoad', false);
    await this.programListDS.submit();
    this._modal.update({
      children: <Table dataSet={this.programListDS} columns={this.programListColumns} />,
    });
  }

  openErrorStackModal(errorStack) {
    Modal.open({
      key: Modal.key(),
      title: FRONTAL_MANAGEMENT_LANG.ERROR_STACK,
      children: <TextArea value={errorStack} cols={300} rows={25} />,
      closable: true,
    });
  }

  get preposedMachineColumns() {
    const {
      tenantRoleLevel,
      match: { path },
    } = this.props;
    return [
      {
        name: 'frontalCode',
        width: 250,
      },
      {
        name: 'frontalName',
        // width: 150,
      },
      {
        name: 'requestUrl',
        width: 280,
      },
      {
        name: 'statusCode',
        width: 100,
        align: 'center',
        renderer: ({ value }) => TagRender(value, FRONTAL_SERVER_TAG_STATUS),
      },
      {
        name: 'clientName',
        width: 200,
      },
      !tenantRoleLevel && {
        name: 'tenantName',
        width: 200,
      },
      {
        name: 'remark',
        width: 180,
      },
      {
        header: PREPOSED_MACHINE_LANG.OPERATOR,
        width: 180,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            FRONTAL_MACHINE_STATUS.EDIT.includes(record.get('statusCode')) && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '前置机列表-编辑',
                    },
                  ]}
                  onClick={() => this.openConsumerDrawer(false, record)}
                >
                  {PREPOSED_MACHINE_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: PREPOSED_MACHINE_LANG.EDIT,
            },
            record.get('statusCode') !== 'NEW' &&
              FRONTAL_MACHINE_STATUS.ENABLE.includes(record.get('statusCode')) && {
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.enabled`,
                        type: 'button',
                        meaning: '前置机列表-启用',
                      },
                    ]}
                    onClick={() => this.handleToggle(record, 'enable')}
                  >
                    {PREPOSED_MACHINE_LANG.ENABLED}
                  </ButtonPermission>
                ),
                key: 'enabled',
                len: 2,
                title: PREPOSED_MACHINE_LANG.ENABLED,
              },
            record.get('statusCode') !== 'NEW' &&
              FRONTAL_MACHINE_STATUS.DISABLE.includes(record.get('statusCode')) && {
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.disabled`,
                        type: 'button',
                        meaning: '前置机列表-禁用',
                      },
                    ]}
                    onClick={() => this.handleToggle(record, 'disable')}
                  >
                    {PREPOSED_MACHINE_LANG.DISABLED}
                  </ButtonPermission>
                ),
                key: 'disabled',
                len: 2,
                title: PREPOSED_MACHINE_LANG.DISABLED,
              },
            {
              ele: (
                <ButtonPermission type="text" onClick={() => this.openProgramListModal(record)}>
                  {PREPOSED_MACHINE_LANG.PROGRAM_LIST}
                </ButtonPermission>
              ),
              key: 'programList',
              len: 2,
              title: PREPOSED_MACHINE_LANG.PROGRAM_LIST,
            },
          ];
          return operatorRender(actions);
        },
      },
    ];
  }

  get programListColumns() {
    return [
      {
        name: 'tenantName',
        width: 150,
      },
      {
        name: 'programCode',
        width: 200,
      },
      {
        name: 'programType',
        width: 200,
      },
      {
        name: 'programName',
        width: 150,
      },
      {
        name: 'programDesc',
        width: 200,
      },
      {
        name: 'statusCode',
        width: 120,
      },
      {
        name: 'errorStack',
        width: 240,
        renderer: ({ value }) => <a onClick={() => this.openErrorStackModal(value)}>{value}</a>,
      },
      {
        header: FRONTAL_MANAGEMENT_LANG.OPERATOR,
        lock: 'right',
        align: 'center',
        width: 200,
        renderer: ({ record }) => {
          const { statusCode } = record.toData();
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  onClick={() => this.handleLoad(statusCode, record)}
                  disabled={
                    this.state.currentStatusCode !== 'ONLINE' ||
                    !(statusCode === 'UNLOAD' || statusCode === 'LOAD-FAILED')
                  }
                >
                  {FRONTAL_MANAGEMENT_LANG.LOADED}
                </ButtonPermission>
              ),
              key: 'loaded',
              len: 2,
              title: FRONTAL_MANAGEMENT_LANG.LOADED,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  onClick={() => this.handleUnload(statusCode, record)}
                  disabled={
                    this.state.currentStatusCode !== 'ONLINE' ||
                    !(statusCode === 'LOADED' || statusCode === 'UNLOAD-FAILED')
                  }
                >
                  {FRONTAL_MANAGEMENT_LANG.UNLOAD}
                </ButtonPermission>
              ),
              key: 'unload',
              len: 2,
              title: FRONTAL_MANAGEMENT_LANG.UNLOAD,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={PREPOSED_MACHINE_LANG.HEADER}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '前置机维护-新建',
              },
            ]}
            icon="add"
            type="c7n-pro"
            color="primary"
            onClick={this.handleCreate}
          >
            {PREPOSED_MACHINE_LANG.CREATE}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} columns={this.preposedMachineColumns} />
        </Content>
      </>
    );
  }
}
