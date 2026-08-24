/**
 * CardTab - 工作台tab页
 * @date: 2019-11-1
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import {
  DataSet,
  Table,
  Modal,
  Form,
  TextField,
  NumberField,
  Switch,
  Button,
  Lov,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import notification from 'utils/notification';
import { yesOrNoRender } from 'utils/renderer';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import { cardDS } from '@/stores/SecurityGroupDS';
import openAddModal from '../Components/AddModal';

const modalKey = Modal.key();
const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';
let modal;

export default class CardTab extends Component {
  cardDS = new DataSet({
    ...cardDS(),
    selection: this.props.isSelf ? 'multiple' : false,
    transport: {
      read: ({ data, params }) => {
        const { secGrpId } = this.props;
        return {
          url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-dashboards/${secGrpId}`,
          params: { ...data, ...params },
          method: 'get',
        };
      },
      submit: ({ data }) => {
        const { secGrpId } = this.props;
        const { __id, _status, ...rest } = data[0];
        return {
          url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-dashboards/${secGrpId}`,
          data: rest,
          method: _status === 'create' ? 'post' : 'put',
        };
      },
      destroy: ({ data }) => {
        const { secGrpId } = this.props;
        return {
          url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-dashboards/${secGrpId}`,
          data,
          method: 'delete',
        };
      },
    },
  });

  @Bind()
  handleOpenDrawer(record) {
    const { isSelf } = this.props;
    const isCreate = isEmpty(record);
    if (isCreate) {
      this.cardDS.create();
    }
    const modalContent = {
      // eslint-disable-next-line no-nested-ternary
      title: isSelf
        ? intl.get('hiam.securityGroup.view.title.card.detail').d('查看卡片')
        : isCreate
        ? intl.get('hiam.securityGroup.view.title.card.create').d('新建卡片')
        : intl.get('hiam.securityGroup.view.title.card.edit').d('编辑卡片'),
      drawer: true,
      closable: true,
      key: modalKey,
      children: (
        <Form record={this.cardDS.current} columns={1}>
          <Lov name="cardLov" disabled={!isCreate} />
          <TextField name="name" disabled />
          <TextField name="catalogMeaning" disabled />
          <NumberField name="h" disabled />
          <NumberField name="w" disabled />
          <NumberField name="x" />
          <NumberField name="y" />
          <Switch name="defaultDisplayFlag" />
          <TextField name="remark" />
        </Form>
      ),
      destroyOnClose: true,
      onOk: this.handleSubmit,
      onCancel: this.handleCancel,
    };
    if (!isSelf) {
      modalContent.footer = (
        <Button
          onClick={() => {
            modal.close();
          }}
        >
          {intl.get('hzero.common.button.close').d('关闭')}
        </Button>
      );
    }
    modal = Modal.open(modalContent);
  }

  @Bind()
  async handleSubmit() {
    const res = await this.cardDS.submit();
    if (!isEmpty(res) && res.failed && res.message) {
      notification.error({
        message: res.message,
      });
      return false;
    }
    if (!isEmpty(res) && res.success) {
      this.cardDS.query();
      return true;
    }
    if (res === false) {
      notification.error({
        message: intl.get('hiam.securityGroup.view.message.required').d('存在必输字段未填写'),
      });
      return false;
    }
    notification.warning({
      message: intl.get('hiam.securityGroup.view.message.form.noChange').d('表单未做修改'),
    });
    return false;
  }

  @Bind()
  handleCancel() {
    this.cardDS.reset();
  }

  /**
   * 显示新建弹窗
   */
  @Bind()
  handleOpenAddModal() {
    const { secGrpId, roleId } = this.props;
    openAddModal({
      title: intl.get('hiam.roleManagement.view.title.choseCards').d('选择卡片'),
      nameTitle: intl.get('hiam.roleManagement.model.roleManagement.cardName').d('卡片名称'),
      codeTitle: intl.get('hiam.roleManagement.model.roleManagement.cardCode').d('卡片代码'),
      roleId,
      secGrpId,
      pageDataSet: this.cardDS,
      isAddCard: true,
    });
  }

  get columns() {
    const { isSelf } = this.props;
    return [
      { name: 'code', width: 200 },
      { name: 'name', width: 150 },
      { name: 'catalogMeaning' },
      { name: 'h' },
      { name: 'w' },
      { name: 'x' },
      { name: 'y' },
      {
        name: 'defaultDisplayFlag',
        renderer: ({ value }) => yesOrNoRender(value),
      },
      { name: 'remark' },
      isSelf && {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        command: ({ record }) => [
          <a onClick={() => this.handleOpenDrawer(record)}>
            {intl.get('hzero.common.button.edit').d('编辑')}
          </a>,
        ],
        lock: 'right',
      },
    ].filter(Boolean);
  }

  get buttons() {
    const { isSelf } = this.props;
    return isSelf
      ? [
          [
            'add',
            {
              onClick: () => this.handleOpenAddModal({}),
            },
          ],
          ['delete'],
        ]
      : [];
  }

  render() {
    return (
      <Table
        dataSet={this.cardDS}
        columns={this.columns}
        queryFieldsLimit={2}
        buttons={this.buttons}
      />
    );
  }
}
