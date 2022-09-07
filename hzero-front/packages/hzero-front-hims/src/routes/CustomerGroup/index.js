import React from 'react';
import { Table, Modal, ModalContainer, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import { InitDs } from '../../stores/customerGroupDS';
import Drawer from './Drawer';

@formatterCollections({ code: ['hims.customerGroup'] })
export default class CustomerGroup extends React.Component {
  initDs = new DataSet(InitDs());

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
                  meaning: '客服群组-编辑',
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
    const validate = await this.initDs.submit();
    if (!validate) {
      return false;
    }
    this.initDs.query();
  }

  @Bind()
  handleUpdate(isCreate, record) {
    const {
      match: { path },
    } = this.props;
    let customerGroupId;
    if (isCreate) {
      this.initDs.create();
    }
    if (!isCreate) {
      const { data: { csGroupId } = {} } = record;
      customerGroupId = csGroupId;
    }
    Modal.open({
      closable: true,
      key: 'customer-group',
      title: isCreate
        ? intl.get('hims.customerGroup.view.message.crete').d('新建群组')
        : intl.get('hims.customerGroup.view.message.edit').d('编辑群组'),
      drawer: true,
      style: {
        width: 720,
      },
      children: (
        <Drawer editData={record || this.initDs.current} id={customerGroupId} path={path} />
      ),
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
        <Header title={intl.get('hims.customerGroup.view.message.title').d('客服群组')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '客服群组-新建',
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
