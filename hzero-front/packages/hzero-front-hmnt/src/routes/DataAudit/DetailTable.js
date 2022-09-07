/**
 * ListTable - 数据变更审计详情页-列表
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Array} dataSource - 数据源
 * @return React.element
 */

export default class DetailTable extends PureComponent {
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const { dataSource } = this.props;
    const columns = [
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.fieldCode').d('审计字段名'),
        dataIndex: 'displayName',
        width: 200,
        render: (value, record) => value || record.fieldCode,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.columnName').d('审计列名'),
        dataIndex: 'columnName',
        width: 200,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.lang').d('语言'),
        dataIndex: 'lang',
        width: 150,
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.fieldValueOld').d('原值'),
        dataIndex: 'fieldValueOldMeaning',
        width: 150,
        render: (text, record) => {
          if (text && record.fieldValueNewMeaning) {
            return text;
          } else {
            return record.fieldValueOld;
          }
        },
      },
      {
        title: intl.get('hmnt.dataAudit.model.dataAudit.fieldValueNew').d('现值'),
        dataIndex: 'fieldValueNewMeaning',
        width: 150,
        render: (text, record) => {
          if (text && record.fieldValueOldMeaning) {
            return text;
          } else {
            return record.fieldValueNew;
          }
        },
      },
      {
        title: intl.get('hzero.common.remark').d('备注'),
        dataIndex: 'remark',
        width: 150,
        onCell: this.onCell.bind(this),
      },
    ];

    return <Table bordered rowKey="auditDataLineId" columns={columns} dataSource={dataSource} />;
  }
}
