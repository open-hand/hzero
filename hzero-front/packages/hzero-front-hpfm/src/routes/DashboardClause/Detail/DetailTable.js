import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import classnames from 'classnames';
import { TABLE_OPERATOR_CLASSNAME } from 'utils/constants';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import ModalCardTable from './ModalCardTable';

const promptCode = 'hpfm.dashboardClause';

/**
 * 条目配置详情Table
 * @extends {PureComponent} - React.PureComponent
 * @return React.element
 */

export default class DetailTable extends Component {
  state = {
    selectedRows: [],
    visible: false,
  };

  /**
   * 打开或关闭卡片modal
   */
  @Bind()
  handleCardModalDisplay() {
    const { visible } = this.state;
    this.setState({ visible: !visible });
  }

  /**
   * 删除卡片
   */
  @Bind()
  handleDelete() {
    const { selectedRows } = this.state;
    this.props.onDelete(selectedRows);
  }

  /**
   * 保存选中的行
   * @param {Array} selectedRows 行数据
   */
  @Bind()
  onSelectChange(_, selectedRows) {
    this.setState({ selectedRows });
  }

  render() {
    const {
      clauseId,
      headInfo,
      deleting,
      match,
      dataSource = [],
      pagination = {},
      onTableChange,
    } = this.props;
    const { selectedRows, visible } = this.state;
    const columns = [
      {
        title: intl.get(`${promptCode}.model.dashboard.cardCode`).d('卡片编码'),
        dataIndex: 'code',
      },
      {
        title: intl.get(`${promptCode}.model.dashboard.cardName`).d('卡片名称'),
        dataIndex: 'name',
      },
    ];
    const rowSelection = {
      selectedRowKeys: selectedRows.map(o => o.id),
      onChange: this.onSelectChange,
    };
    const cardTableProps = {
      columns,
      visible,
      clauseId,
      headInfo,
      onCardModalDisplay: this.handleCardModalDisplay,
      excludeCardIds: dataSource.map(o => o.cardId).filter(o => o),
    };
    return (
      <React.Fragment>
        <div className={classnames('table-list-search', TABLE_OPERATOR_CLASSNAME)}>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.assign`,
                type: 'button',
                meaning: '条目配置明细-分配卡片',
              },
            ]}
            onClick={this.handleCardModalDisplay}
          >
            {intl.get(`${promptCode}.view.message.toolTip.create`).d('分配卡片')}
          </ButtonPermission>
          <ButtonPermission
            loading={deleting}
            disabled={isEmpty(selectedRows)}
            permissionList={[
              {
                code: `${match.path}.button.delete`,
                type: 'button',
                meaning: '条目配置明细-取消分配',
              },
            ]}
            onClick={this.handleDelete}
          >
            {intl.get(`${promptCode}.view.message.toolTip.delete`).d('取消分配')}
          </ButtonPermission>
        </div>
        <Table
          bordered
          rowKey="id"
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={onTableChange}
          rowSelection={rowSelection}
        />
        <ModalCardTable {...cardTableProps} />
      </React.Fragment>
    );
  }
}
