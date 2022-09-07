/**
 * 出参弹框
 * @Date: 2019-10-17
 * @Author: NJQ <jiangqi.nan@hand-china.com>
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Table, Modal, Button, Select, TextField } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { isEmpty } from 'lodash';
import notification from 'utils/notification';
import RulesOutDS from '../stores/RulesOutDS';
import FormulaModal from '../detail/FormulaModal';

const modalKey = Modal.key();

class OutModal extends Component {
  rulesOutDS;

  constructor(props) {
    super(props);
    const { ruleCode, frozenFlag } = this.props.records;
    this.rulesOutDS = new DataSet({
      ...RulesOutDS(),
      queryParameter: {
        ruleCode,
        frozenFlag,
        tenantId: getCurrentOrganizationId(),
      },
      autoQuery: true,
    });
    this.props.callback(this.rulesOutDS);
  }

  /**
   * 新建
   */
  @Bind()
  async handleCreate() {
    const { ruleCode } = this.props.records;
    if (!(await this.rulesOutDS.validate(false, false))) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    }
    this.rulesOutDS.create({
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

  // 出参行删除
  @Bind()
  async handlerDelete(record) {
    try {
      const res = await this.rulesOutDS.delete([record]);
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
        name: 'formula',
        editor: frozenFlag !== 'Y' && this.valueEditor,
        width: '20%',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        renderer: ({ record }) => this.enterCommands(record),
        lock: 'right',
        align: 'center',
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
    switch (name) {
      case 'fieldType':
        return <Select />;
      case 'fieldCode':
      case 'fieldName':
        if (!record.get('_token')) {
          return <TextField restrict="\S" />;
        }
        return false;
      case 'formula':
        return <TextField onClick={() => FormulaModal(record)} clearButton />;
      default:
        return false;
    }
  }

  /**
   * 出参行内操作按钮组
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
        dataSet={this.rulesOutDS}
        columns={this.enterColumns}
        pagination={false}
        style={{ maxHeight: 400 }}
      />
    );
  }
}

let enterDS;
function callback(rulesOutDS) {
  enterDS = rulesOutDS;
}

export default function openCreateModel(record) {
  Modal.open({
    key: modalKey,
    title: intl.get('hres.rule.model.rule.out').d('出参'),
    style: {
      width: 1000,
    },
    children: <OutModal callback={callback} records={record} />,
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
