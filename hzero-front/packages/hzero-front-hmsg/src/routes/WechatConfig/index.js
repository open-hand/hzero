/**
 * @since 2019-10-16
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Modal, ModalContainer, Table } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';
import { getPublicKey } from 'services/api';

import Drawer from './Drawer';
import ListDs from './stores/ListDs';
import ModalDs from './stores/ModalDs';

const tenantId = getCurrentOrganizationId();

@formatterCollections({ code: ['hmsg.wechatConfig', 'entity.tenant', 'hmsg.common'] })
export default class WechatConfig extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      publicKey: '', // 密码公钥
    };
  }

  tableDS = new DataSet({
    autoQuery: true,
    dataKey: 'content',
    selection: false,
    ...ListDs(),
  });

  modalDS = new DataSet({
    ...ModalDs(),
  });

  componentDidMount() {
    this.fetchPublicKey();
  }

  get columns() {
    return [
      !isTenantRoleLevel() && { name: 'tenantName', width: 220 },
      { name: 'serverCode' },
      { name: 'serverName', width: 250 },
      { name: 'authTypeMeaning', width: 120 },
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
                      meaning: '微信企业号配置-编辑',
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
                      meaning: '微信企业号配置-复制',
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
          if (
            tenantId.toString() === record.toData().tenantId.toString() &&
            isTenantRoleLevel() &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'delete',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/delete`,
                      type: 'button',
                      meaning: '微信企业号配置-删除',
                    },
                  ]}
                  onClick={() => this.handleDelete(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
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
      const { publicKey } = this.state;
      this.modalDS.setQueryParameter('publicKey', publicKey);
      const validate = await this.modalDS.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    this.modalDS.setQueryParameter('publicKey', '');
    this.tableDS.query();
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
  async handleDelete(record) {
    await this.tableDS.delete(record);
  }

  @Bind()
  handleEdit(isEdit, record, isCopy) {
    this.modalDS.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.button.create').d('新建')
      : intl.get('hzero.common.button.edit').d('编辑');
    Modal.open({
      closable: true,
      key: 'customer-group',
      title: isCopy ? intl.get('hzero.common.title.copy').d('复制') : title,
      drawer: true,
      style: {
        width: 520,
      },
      children: (
        <Drawer
          currentEditData={currentEditData}
          isEdit={isEdit}
          modalDS={this.modalDS}
          isCopy={isCopy}
        />
      ),
      onOk: this.handleOk,
      onCancel: () => {
        this.modalDS.removeAll();
      },
      onClose: () => {
        this.modalDS.removeAll();
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
        <Header title={intl.get('hmsg.wechatConfig.view.message.wechatConfig').d('微信企业号配置')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/add`,
                type: 'button',
                meaning: '微信企业号配置-新建',
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
          <Table dataSet={this.tableDS} columns={this.columns} queryFieldsLimit={3} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
