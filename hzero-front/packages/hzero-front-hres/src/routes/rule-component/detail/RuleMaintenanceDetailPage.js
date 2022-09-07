/**
 * 规则维护tab页
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/17 16:14
 * @LastEditTime: 2019/10/25 9:37
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import notification from 'utils/notification';
import {
  Table,
  Button,
  TextField,
  Lov,
  Select,
  DatePicker,
  NumberField,
  Stores,
} from 'choerodon-ui/pro';
import { getCurrentOrganizationId } from 'utils/utils';

export default class RuleFieldDetailPage extends Component {
  sourceList = this.props.fieldDataSet;

  async componentDidMount() {
    const { dataSet } = this.props;
    dataSet.reset();
    this.sourceList.map(async record => {
      if (record.get('editType') === 'LOV') {
        dataSet.addField(`data.${record.get('fieldName')}Lov`, {
          name: `data.${record.get('fieldName')}Lov`,
          type: 'object',
          label: record.get('fieldName'),
          lovCode: record.get('businessModel'),
        });
        const config = await Stores.LovCodeStore.fetchConfig(record.get('businessModel'));
        dataSet.addField(`data.${record.get('fieldName')}.meaning`, {
          name: `data.${record.get('fieldName')}.meaning`,
          type: 'string',
          bind: `data.${record.get('fieldName')}Lov.${config.textField}`,
        });
        dataSet.addField(`data.${record.get('fieldName')}.value`, {
          name: `data.${record.get('fieldName')}.value`,
          type: 'string',
          bind: `data.${record.get('fieldName')}Lov.${config.valueField}`,
        });
        dataSet
          .getField(`data.${record.get('fieldName')}Lov`)
          .set('required', record.get('isRequired') === 'Y');
      } else {
        dataSet.addField(`data.${record.get('fieldName')}.value`, {
          name: `data.${record.get('fieldName')}.meaning`,
          type: record.get('fieldType'),
          label: record.get('fieldName'),
          pattern: record.get('fieldType') === 'DATE' && /\d/,
          lookupCode: record.get('editType') === 'SELECT' && record.get('businessModel'),
          required: record.get('isRequired') === 'Y',
        });
      }
      return null;
    });
    dataSet.query();
  }

  /**
   * 行内操作按钮组
   * @param {object} record - 表格行数据
   * @returns {*}
   */
  @Bind()
  commands({ record }) {
    const { frozenFlag } = this.props;

    return [
      <span className="action-link">
        <a disabled={frozenFlag} onClick={async () => this.delete(record)}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>,
    ];
  }

  @Bind()
  async delete(record) {
    const { dataSet } = this.props;
    await dataSet.delete([record]);
    dataSet.query();
  }

  /**
   * 列表列
   */
  get columns() {
    const valueList = [];
    this.sourceList.forEach(record => {
      valueList.push({
        name:
          record.get('editType') === 'LOV'
            ? `data.${record.get('fieldName')}Lov`
            : `data.${record.get('fieldName')}.value`,
        editor: () => {
          switch (record.get('editType')) {
            case 'LOV':
              return <Lov noCache />;
            case 'SELECT':
              return <Select />;
            case 'DATE':
              return <DatePicker />;
            case 'TEXT':
              return <TextField />;
            case 'NUMBER':
              return <NumberField />;
            default:
              return <TextField />;
          }
        },
      });
    });
    return [
      { name: 'id', hidden: true },
      { name: 'tenantId', hidden: true },
      { name: 'ruleCode', hidden: true },
      { name: 'ruleComponentName', hidden: true },
      ...valueList,
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
    const { frozenFlag } = this.props;
    return [
      <Button
        disabled={frozenFlag}
        key="create-field"
        icon="playlist_add"
        color="primary"
        funcType="flat"
        onClick={() => this.addLineField()}
      >
        {intl.get('hzero.common.button.create').d('新建')}
      </Button>,
    ];
  }

  /**
   * 新增自增序号行
   */
  @Bind()
  async addLineField() {
    const { headerDataSet, dataSet, code } = this.props;
    const componentName = headerDataSet.current.get('ruleComponentName');
    if (!(await dataSet.validate(false, true))) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    }
    dataSet.create(
      {
        tenantId: getCurrentOrganizationId(),
        ruleCode: code,
        ruleComponentName: componentName,
      },
      0
    );
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
