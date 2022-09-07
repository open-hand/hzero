/**
 * List  - 应用管理 - 首页列表
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Table, Modal, Button } from 'hzero-ui';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

export default class List extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      statisticsDetailModalVisible: false,
    };
  }

  defaultTableRowKey = 'interfaceId';

  @Bind()
  closeStatisticsDetailModal() {
    this.setState({
      statisticsDetailModalVisible: false,
      statisticsDetailModalMessage: null,
    });
  }

  @Bind()
  openStatisticsDetailModal(statisticsDetailModalMessage) {
    this.setState({
      statisticsDetailModalVisible: true,
      statisticsDetailModalMessage,
    });
  }

  @Bind()
  statisticsDetailRender(text) {
    return <a onClick={() => this.openStatisticsDetailModal(text)}>{text}</a>;
  }

  /**
   * 变更单元格样式
   * @param {number} length - 单元格长度
   */
  handleCell(length) {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: length,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: e => {
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
    const { dataSource = [], pagination, loading, onChange = e => e } = this.props;
    const { statisticsDetailModalVisible, statisticsDetailModalMessage } = this.state;
    const tableColumns = [
      {
        title: intl.get('hitf.interfaceStatistics.model.statistics.serverCode').d('服务代码'),
        dataIndex: 'serverCode',
        width: 180,
      },
      {
        title: intl.get('hitf.interfaceStatistics.model.statistics.interfaceCodes').d('接口编码'),
        dataIndex: 'interfaceCode',
        width: 220,
      },
      {
        title: intl.get('hitf.interfaceStatistics.model.statistics.count').d('异常次数'),
        width: 90,
        dataIndex: 'count',
      },
      {
        title: intl
          .get('hitf.interfaceStatistics.model.statistics.statisticsDetail')
          .d('最近异常信息'),
        dataIndex: 'statisticsDetail',
        // width: 260,
        render: this.statisticsDetailRender,
      },
      {
        title: intl.get('hitf.interfaceStatistics.model.statistics.latestTime').d('最近异常时间'),
        dataIndex: 'latestTime',
        width: 160,
      },
    ];
    const tableProps = {
      dataSource,
      loading,
      onChange,
      pagination,
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowKey: this.defaultTableRowKey,
    };
    return (
      <>
        <Table {...tableProps} />
        <Modal
          visible={statisticsDetailModalVisible}
          destroyOnClose
          maskClosable
          title={intl
            .get('hitf.interfaceStatistics.model.statistics.statisticsDetail')
            .d('最近异常信息')}
          onCancel={this.closeStatisticsDetailModal}
          footer={[
            <Button key="cancel" onClick={this.closeStatisticsDetailModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <div style={{ maxWidth: 472, maxHeight: 400, overflowY: 'scroll' }}>
            {statisticsDetailModalMessage}
          </div>
        </Modal>
      </>
    );
  }
}
