/**
 * AssignDrawer - 分配角色
 * @date: 2019-10-24
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Modal, Table, DataSet, Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { assignRoleDS } from '@/stores/SecurityGroupDS';
import openAddModal from '../Components/AddModal';

const dataModalKey = Modal.key();
let modal;

class AssignDrawer extends Component {
  constructor(props) {
    super(props);
    const { record = {} } = props;
    const { secGrpId } = record.toData();
    this.assignRoleDS = new DataSet(assignRoleDS(secGrpId));
  }

  @Bind()
  handleOpenAddModal() {
    const { record = {} } = this.props;
    const { secGrpId } = record.toData();
    openAddModal({
      title: intl.get('hiam.securityGroup.view.title.choose.role').d('选择角色'),
      secGrpId,
      isAddRole: true,
      pageDataSet: this.assignRoleDS,
    });
  }

  get columns() {
    const columns = [{ name: 'name' }, { name: 'parentRoleName' }, { name: 'tenantName' }];

    return columns;
  }

  get buttons() {
    return [
      <Button onClick={this.handleOpenAddModal} icon="playlist_add" funcType="flat" color="primary">
        {intl.get('hzero.common.button.create').d('新建')}
      </Button>,
      'delete',
    ];
  }

  render() {
    return (
      <Table
        dataSet={this.assignRoleDS}
        columns={this.columns}
        buttons={this.buttons}
        queryFieldsLimit={1}
      />
    );
  }
}

function handleCancel() {
  modal.close();
}

export default function openCreateModal(record) {
  modal = Modal.open({
    title: intl.get('hiam.securityGroup.view.title.securityGroup.assign').d('分配角色'),
    drawer: true,
    closable: true,
    key: dataModalKey,
    children: <AssignDrawer record={record} />,
    footer: (
      <Button type="primary" onClick={handleCancel}>
        {intl.get('hzero.common.button.close').d('关闭')}
      </Button>
    ),
    destroyOnClose: true,
  });
}
