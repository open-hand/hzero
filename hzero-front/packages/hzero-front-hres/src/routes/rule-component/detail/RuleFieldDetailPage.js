/**
 * 规则字段tab页
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/17 16:14
 * @LastEditTime: 2019/10/25 9:37
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { Table, Button } from 'choerodon-ui/pro';
import openCreateModel from './CreateModel';

export default class RuleFieldDetailPage extends Component {
  /**
   * 行内操作按钮组
   * @param {object} record - 表格行数据
   * @returns {*}
   */
  @Bind()
  commands({ record }) {
    const { id, dataSet, ruleCode, frozenFlag } = this.props;
    const modelType = 'edit';
    return [
      <span className="action-link">
        <a
          disabled={frozenFlag}
          onClick={async () => {
            openCreateModel({ id, record, ruleCode, modelType, dataSet });
          }}
        >
          {intl.get('hzero.common.button.edit').d('编辑')}
        </a>
        <a disabled={frozenFlag} onClick={() => this.delete(record)}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>,
    ];
  }

  @Bind()
  delete(record) {
    const { dataSet } = this.props;
    dataSet.delete(
      [record],
      intl
        .get('hres.ruleComponent.view.button.field.delete')
        .d('删除通用字段将删除规则维护列表下该字段信息，是否继续?')
    );
  }

  /**
   * 列表列
   */
  get columns() {
    return [
      { name: 'fieldName' },
      { name: 'fieldType' },
      { name: 'editType' },
      { name: 'businessModelName' },
      { name: 'formula' },
      { name: 'isRequired' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        command: this.commands,
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 列表操作按钮组
   */
  get buttons() {
    const { componentName, id, frozenFlag, dataSet, headerDataSet, ruleCode } = this.props;
    return [
      <Button
        key="create-field"
        icon="playlist_add"
        color="primary"
        funcType="flat"
        disabled={frozenFlag}
        onClick={async () => {
          if (!componentName) {
            notification.error({
              message: intl
                .get('hres.ruleComponent.view.message.validate.header')
                .d('请先保存规则组件名称'),
            });
            return false;
          }
          dataSet.setQueryParameter(
            'ruleComponentName',
            headerDataSet.get(0).get('ruleComponentName')
          );
          dataSet.ruleComponentName = headerDataSet.get(0).get('ruleComponentName');
          const modelType = 'new';
          openCreateModel({ id, ruleCode, modelType, dataSet });
        }}
      >
        {intl.get('hzero.common.button.create').d('新建')}
      </Button>,
    ];
  }

  render() {
    const { dataSet, frozenFlag } = this.props;
    return (
      <>
        <Table buttons={!frozenFlag && this.buttons} dataSet={dataSet} columns={this.columns} />
      </>
    );
  }
}
