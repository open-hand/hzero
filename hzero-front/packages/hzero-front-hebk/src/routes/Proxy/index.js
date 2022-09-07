/**
 * 代理
 * @since 2020-02-06
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { HZERO_HEBK } from 'utils/config';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import axios from 'axios';
import Drawer from './Drawer';
import BankDrawer from './BankDrawer';
import { tableDs, detailDs, bankDs } from '../../stores/ProxyDS';

/**
 * FIXME: 为什么这么使用时无法获取到axios的res
 */
// const axios = getConfig('axios');

@formatterCollections({ code: ['hebk.proxy'] })
export default class Proxy extends React.Component {
  tableDs = new DataSet(tableDs());

  detailDs = new DataSet(detailDs());

  bankDs = new DataSet(bankDs());

  get columns() {
    return [
      { name: 'name', width: 220 },
      {
        name: 'enabledFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      {
        name: 'sslEnabledFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      { name: 'remark', width: 200 },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const {
            match: { path },
          } = this.props;
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '代理-编辑',
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
            },
            {
              key: 'bank',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/bank`,
                      type: 'button',
                      meaning: '代理-银行',
                    },
                  ]}
                  onClick={() => {
                    this.handleOpenBankModal(record);
                  }}
                >
                  {intl.get('hebk.proxy.button.bank').d('银行')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hebk.proxy.button.bank').d('银行'),
            }
          );
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ].filter(Boolean);
  }

  @Bind()
  async handleOk() {
    const validate = await this.detailDs.validate();
    if (validate) {
      const dataSource = this.detailDs.toData();
      const {
        name,
        host,
        port,
        enabledFlag,
        sslEnabledFlag,
        remark,
        trustedCaFile,
        certFile,
        keyFile,
        proxyId,
      } = dataSource[0];
      const data = new FormData();
      data.append('name', name);
      data.append('host', host);
      data.append('port', port);
      data.append('enabledFlag', enabledFlag);
      data.append('sslEnabledFlag', sslEnabledFlag);
      data.append('trustedCaFile', trustedCaFile);
      data.append('certFile', certFile);
      data.append('keyFile', keyFile);
      if (remark !== undefined && remark) {
        data.append('remark', remark);
      }
      axios({
        url:
          proxyId !== undefined
            ? `${HZERO_HEBK}/v1/${getCurrentOrganizationId()}/proxies/${proxyId}`
            : `${HZERO_HEBK}/v1/${getCurrentOrganizationId()}/proxies`,
        data,
        method: 'POST',
      })
        .then((res) => {
          if (res) {
            this.tableDs.query();
            notification.success();
          }
        })
        .catch((err) => {
          // console.log(err);
          notification.error({
            message: err.message,
          });
        });
    } else {
      return false;
    }
  }

  @Bind()
  async handleDelete(record) {
    await this.tableDs.delete(record);
  }

  @Bind()
  handleEdit(isEdit, record) {
    this.detailDs.create({});
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    Modal.open({
      drawer: true,
      key: 'proxy',
      destroyOnClose: true,
      closable: true,
      title,
      children: (
        <Drawer currentEditData={currentEditData} isEdit={isEdit} detailDs={this.detailDs} />
      ),
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: this.handleOk,
      onCancel: () => {
        this.detailDs.removeAll();
      },
      onClose: () => {
        this.detailDs.removeAll();
      },
    });
  }

  @Bind()
  handleOpenBankModal(record) {
    const currentBankData = record && record.toData();
    const title = intl.get('hebk.proxy.view.title.bank').d('银行列表');
    Modal.open({
      key: 'proxyBank',
      destroyOnClose: true,
      closable: true,
      title,
      children: <BankDrawer currentBankData={currentBankData} bankDs={this.bankDs} />,
      onClose: () => {
        this.bankDs.removeAll();
      },
      footer: null,
    });
  }

  render() {
    const {
      location,
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hebk.proxy.view.message.proxy').d('代理配置')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/create`,
                type: 'button',
                meaning: '代理-新建',
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
