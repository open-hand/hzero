/**
 * 入参弹框
 * @Date: 2019-10-16
 * @Author: NJQ <jiangqi.nan@hand-china.com>
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import intl from 'utils/intl';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import { DataSet, Table, Modal, Button, Select, TextField, CheckBox } from 'choerodon-ui/pro';
import RulesEnterDS from '../stores/RulesEnterDS';

const modalKey = Modal.key();
class EnterModal extends Component {
  constructor(props) {
    super(props);
    const { ruleCode, frozenFlag } = this.props.records;
    this.rulesEnterDS = new DataSet({
      ...RulesEnterDS(),
      queryParameter: {
        ruleCode,
        frozenFlag,
        tenantId: getCurrentOrganizationId(),
      },
      autoQuery: true,
    });
    this.props.callback(this.rulesEnterDS);
  }

  /**
   * 新建
   */
  @Bind()
  async handleCreate() {
    const { ruleCode } = this.props.records;
    if (!(await this.rulesEnterDS.validate(false, false))) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    }
    this.rulesEnterDS.create({
      ruleCode,
      tenantId: getCurrentOrganizationId(),
    });
  }

  get buttons() {
    const { records } = this.props;
    const { frozenFlag } = records;
    return [
      <Button
        key="create"
        icon="playlist_add"
        color="primary"
        funcType="flat"
        disabled={frozenFlag === 'Y'}
        onClick={() => this.handleCreate()}
      >
        {intl.get('hzero.common.button.create').d('新建')}
      </Button>,
    ];
  }

  // 入参行删除
  @Bind()
  async handlerDelete(record) {
    try {
      const res = await this.rulesEnterDS.delete(record, null);
      if (!isEmpty(res) && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  }

  get enterColumns() {
    const { records } = this.props;
    const { frozenFlag } = records;
    return [
      { name: 'fieldCode', editor: this.valueEditor },
      { name: 'fieldName', editor: this.valueEditor },
      { name: 'fieldType', editor: frozenFlag !== 'Y' && this.valueEditor },
      {
        name: 'maskCode',
        editor: this.valueEditor,
      },
      {
        name: 'isRequired',
        editor: this.valueEditor,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        renderer: ({ record }) => this.enterCommands(record),
        lock: 'right',
        align: 'center',
        width: '17%',
      },
    ];
  }

  /**
   * 掩码是否可输
   * @param {*} record
   * @param {*} name
   */
  @Bind()
  valueEditor(record, name) {
    const { records } = this.props;
    const { frozenFlag } = records;
    switch (name) {
      case 'maskCode':
        if (frozenFlag !== 'Y' && record.get('fieldType') === 'DATE') {
          return <Select />;
        }
        return false;
      case 'fieldType':
        return <Select onChange={() => this.handleValueChange(record)} />;
      case 'fieldCode':
      case 'fieldName':
        if (!record.get('_token')) {
          return <TextField restrict="\S" />;
        }
        return false;
      case 'isRequired':
        return <CheckBox />;
      default:
        return false;
    }
  }

  /**
   * 当类型为日期时，掩码可输
   * @param {*} record
   */
  @Bind()
  handleValueChange(record) {
    if (['STRING', 'DATE', 'NUMBER'].includes(record.get('fieldType'))) {
      record.set('maskCode', null);
    }
  }

  /**
   * 入参行内操作按钮组
   */
  @Bind()
  enterCommands(record) {
    const { records } = this.props;
    const { frozenFlag } = records;
    return (
      <span className="action-link">
        <a onClick={() => this.handlerDelete(record)} disabled={frozenFlag === 'Y'}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>
    );
  }

  render() {
    return (
      <Table
        buttons={this.buttons}
        dataSet={this.rulesEnterDS}
        columns={this.enterColumns}
        pagination={false}
        style={{ maxHeight: 400 }}
      />
    );
  }
}

let enterDS;
function callback(rulesEnterDS) {
  enterDS = rulesEnterDS;
}

export default function openCreateModel(record) {
  Modal.open({
    key: modalKey,
    title: intl.get('hres.rule.model.rule.enter').d('入参'),
    style: {
      width: 800,
    },
    children: <EnterModal callback={callback} records={record} />,
    destroyOnClose: true,
    onOk: async () => {
      const result = await enterDS.validate();
      if (result === false) {
        notification.error({
          message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
        });
        return false;
      } else {
        enterDS.submit();
      }
    },
  });
}
