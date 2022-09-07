/**
 * 告警附加信息表格
 * @date: 2020-3-27
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

export default class AddonTable extends Component {
  /**
   * 表格列
   */
  get columns() {
    const { addonType } = this.props;
    const addonListMapping = {
      tag: {
        addonCode: intl.get('halt.alertRule.model.alertRule.tagName').d('标签名称'),
        addonValue: intl.get('halt.alertRule.model.alertRule.tagValue').d('标签值'),
      },
      comment: {
        addonCode: intl.get('halt.alertRule.model.alertRule.commentName').d('注解名称'),
        addonValue: intl.get('halt.alertRule.model.alertRule.commentValue').d('注解值'),
      },
    };
    return [
      {
        name: 'addonCode',
        header: addonListMapping[addonType].addonCode,
        editor: true,
        width: '30%',
      },
      { name: 'addonValue', header: addonListMapping[addonType].addonValue, editor: true },
    ];
  }

  /**
   * 按钮
   */
  @Bind()
  get buttons() {
    const { addonType } = this.props;
    return [['add', { onClick: () => this.handleCreateTriggerConfig(addonType) }], ['delete']];
  }

  /**
   * 新建
   */
  @Bind()
  async handleCreateTriggerConfig() {
    const { dataSet, addonType } = this.props;
    const res = await dataSet.validate();
    if (res) {
      await dataSet.create({ addonType: addonType === 'tag' ? 'LABEL' : 'ANNOTATION' });
    } else {
      return false;
    }
  }

  render() {
    const { dataSet } = this.props;
    return <Table dataSet={dataSet} columns={this.columns} buttons={this.buttons} />;
  }
}
