/**
 * 电话语音账号配置
 * @since 2019-11-14
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';

import Drawer from './Drawer';
import { tableDs, detailDs, formConfigDs } from '../../stores/CallServerDS';

const tenantId = getCurrentOrganizationId();

const newDetailDs = new DataSet(detailDs());

@formatterCollections({ code: ['hmsg.callServer', 'entity.tenant', 'hmsg.common'] })
export default class CallServer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      otherFields: [],
    };
  }

  formConfigDs = new DataSet(formConfigDs());

  tableDs = new DataSet(tableDs());

  get columns() {
    return [
      !isTenantRoleLevel() && {
        name: 'tenantName',
        width: 220,
      },
      {
        name: 'serverCode',
        sortable: true,
      },
      {
        name: 'serverName',
        width: 250,
        sortable: true,
      },
      {
        name: 'serverTypeMeaning',
        width: 120,
      },
      isTenantRoleLevel() &&
        !VERSION_IS_OP && {
          name: 'tenantId',
          width: 120,
          renderer: ({ value }) =>
            tenantId.toString() === value.toString() ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            ),
        },
      {
        name: 'enabledFlag',
        width: 120,
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const {
            match: { path },
          } = this.props;
          const operators = [];
          if (tenantId.toString() === record.toData().tenantId.toString() || !isTenantRoleLevel()) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '电话语音-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleEdit(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          if (
            tenantId.toString() !== record.toData().tenantId.toString() &&
            isTenantRoleLevel() &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'copy',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/copy`,
                      type: 'button',
                      meaning: '电话配置-复制',
                    },
                  ]}
                  onClick={() => {
                    this.handleEdit(false, record, true);
                  }}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            });
          }
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ].filter(Boolean);
  }

  @Bind()
  async handleOk() {
    try {
      const validate = await this.testDS.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    this.tableDs.query();
  }

  @Bind()
  async handleTest(ds) {
    this.testDS = ds;
  }

  @Bind()
  async handleEdit(isEdit, record, isCopy) {
    const { otherFields } = this.state;
    newDetailDs.create({});
    const currentEditData = record && record.toData();
    // const newFields = otherFields.map(item => ({
    //   name: item.itemCode,
    //   label: item.itemName,
    //   type: 'string',
    //   required: item.requiredFlag === 1,
    // }));
    // if (isEdit) {
    //   const { serverId } = currentEditData;
    //   newDetailDs.setQueryParameter('serverId', serverId);
    //   await newDetailDs.query().then(res => {
    //     if (res) {
    //       const ext = `${res.extParam}`;
    //       const dataSource = {
    //         ...res,
    //         ...JSON.parse(`${ext}`),
    //       };
    //       const fields = [...detailDs.fields, ...newFields];
    //       newDetailDs = new DataSet({
    //         ...detailDs,
    //         fields,
    //         data: [
    //           {
    //             ...dataSource,
    //           },
    //         ],
    //       });
    //     }
    //   });
    // }
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    Modal.open({
      drawer: true,
      key: 'createDingTalkServer',
      destroyOnClose: true,
      closable: true,
      title: isCopy ? intl.get('hzero.common.view.title.copy').d('复制') : title,
      children: (
        <Drawer
          currentEditData={currentEditData}
          isEdit={isEdit}
          detailDs={newDetailDs}
          isCopy={isCopy}
          otherFields={otherFields}
          formConfigDs={this.formConfigDs}
          onTest={this.handleTest}
        />
      ),
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: this.handleOk,
      onCancel: () => true,
      onClose: () => true,
      afterClose: () => {
        this.testDS.reset();
        this.testDS.removeAll();
      },
    });
  }

  render() {
    const {
      location,
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hmsg.callServer.view.message.callServer').d('电话语音配置')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/add`,
                type: 'button',
                meaning: '电话语音-新建',
              },
            ]}
            icon="add"
            color="primary"
            onClick={() => this.handleEdit(false)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDs} columns={this.columns} queryFieldsLimit={3} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
