/**
 * InnerCustomerGroup 内部客服群组
 * @date: 2019-11-25
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React from 'react';
import { Table, Modal, ModalContainer, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import { InitDs, InnerGroupDS } from '../../stores/innerCustomerGroupDS';
import Drawer from './Drawer';

@formatterCollections({ code: ['hims.innerCustomerGroup'] })
export default class InnerCustomerGroup extends React.Component {
  initDs = new DataSet(InitDs());

  innerGroupDS = null;

  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'groupKey',
        width: 150,
      },
      {
        name: 'groupName',
        width: 200,
      },
      {
        name: 'description',
      },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        renderer: ({ record }) => (
          <span className="action-link">
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.edit`,
                  type: 'button',
                  meaning: '内部客服群组-编辑',
                },
              ]}
              onClick={() => this.handleUpdate(false, record)}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
          </span>
        ),
      },
    ];
  }

  @Bind()
  async handleOk() {
    const validate = await this.innerGroupDS.submit();
    if (!validate) {
      return false;
    }
    this.initDs.query();
  }

  @Bind()
  handleUpdate(isCreate, record) {
    this.innerGroupDS = new DataSet(InnerGroupDS());
    let id;
    if (isCreate) {
      this.initDs.create();
    }
    if (!isCreate) {
      const { data: { innerCsGroupId } = {} } = record;
      id = innerCsGroupId;
    }
    Modal.open({
      closable: true,
      key: 'customer-group',
      title: isCreate
        ? intl.get('hims.innerCustomerGroup.view.message.crete').d('新建内部群组')
        : intl.get('hims.innerCustomerGroup.view.message.edit').d('编辑内部群组'),
      drawer: true,
      style: {
        width: 720,
      },
      children: <Drawer innerGroupDS={this.innerGroupDS} id={id} />,
      onOk: this.handleOk,
      onCancel: () => {
        this.initDs.reset();
      },
      onClose: () => {
        this.initDs.reset();
      },
    });
  }

  render() {
    const { location, match } = this.props;
    return (
      <>
        <Header title={intl.get('hims.innerCustomerGroup.view.message.title').d('内部客服群组')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '内部客服群组-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleUpdate(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.initDs} columns={this.columns} queryFieldsLimit={2} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
