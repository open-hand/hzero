/**
 * DetailList - 数据审计模态框-详情列表
 * @date: 2019/5/9
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';

/**
 * 数据审计模态框-详情列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Array} dataSource - 版本列表数据
 * @reactProps {Array} comparedData - 对比的数据
 * @return React.element
 */
export default class DetailList extends PureComponent {
  /**
   * 渲染字段名
   * @param {string} text - 字段名
   */
  @Bind()
  renderField(text, displayName) {
    if (displayName) return displayName;
    const { comparedData } = this.props;
    let field = text;
    if (comparedData.length && field) {
      // eslint-disable-next-line eqeqeq
      const targetCol = comparedData.find(item => item.dataIndex == field);
      if (!isEmpty(targetCol) && targetCol.title) {
        field = targetCol.title;
      }
    }
    return field;
  }

  render() {
    const { dataSource } = this.props;
    const columns = [
      {
        title: intl.get('hzero.common.components.dataAudit.field').d('字段'),
        dataIndex: 'fieldCode',
        width: 100,
        render: (text, { displayName }) => this.renderField(text, displayName),
      },
      {
        title: intl.get('hzero.common.components.dataAudit.oldValue').d('原值'),
        dataIndex: 'fieldValueOld',
        width: 150,
        render: (text, { fieldValueOldMeaning, fieldValueNewMeaning }) => {
          let showText = text;
          if (fieldValueOldMeaning && fieldValueNewMeaning) {
            showText = fieldValueOldMeaning;
          }
          return showText;
        },
      },
      {
        title: intl.get('hzero.common.components.dataAudit.newValue').d('现值'),
        dataIndex: 'fieldValueNew',
        width: 150,
        render: (text, { fieldValueOldMeaning, fieldValueNewMeaning }) => {
          let showText = text;
          if (fieldValueOldMeaning && fieldValueNewMeaning) {
            showText = fieldValueNewMeaning;
          }
          return showText;
        },
      },
      {
        title: intl.get('hzero.common.explain').d('说明'),
        dataIndex: 'remark',
        width: 100,
      },
    ];
    return (
      <Table
        rowKey="auditDataLineId"
        bordered
        columns={columns}
        dataSource={dataSource}
        style={{ marginTop: '50px' }}
      />
    );
  }
}
