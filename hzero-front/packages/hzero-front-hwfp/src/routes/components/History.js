import React, { Component } from 'react';
import { Table, Collapse, Form, Tag } from 'hzero-ui';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import { dateTimeRender, approveNameRender } from 'utils/renderer';
import { tableScrollWidth, getResponse } from 'utils/utils';

import { fetchHistoryList } from '../../services/api';

const { Panel } = Collapse;

export default class History extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
    };
  }

  /**
   * 组件加载时：调用接口获取审批历史数据
   */
  componentDidMount() {
    const { businessKey } = this.props;
    const params = { businessKey };
    fetchHistoryList(params).then((res) => {
      if (res) {
        const result = getResponse(res);
        this.setState({
          dataSource: result,
        });
      }
    });
  }

  @Bind()
  renderProcessStatus(item) {
    // eslint-disable-next-line
    return item.processEndTime ? (
      <Tag style={{ marginTop: '8px' }}>{intl.get('hzero.common.status.finished').d('已结束')}</Tag>
    ) : item.suspended ? (
      <Tag color="orange" style={{ marginTop: '8px' }}>
        {intl.get('hzero.common.status.suspended').d('挂起中')}
      </Tag>
    ) : (
      <Tag color="green" style={{ marginTop: '8px' }}>
        {intl.get('hzero.common.status.running').d('运行中')}
      </Tag>
    );
  }

  @Bind()
  renderTable() {
    const { dataSource = [] } = this.state;
    const columns = [
      {
        title: intl.get('hwfp.common.model.approval.activityStartTime').d('审批时间'),
        dataIndex: 'activityStartTime',
        width: 180,
        render: dateTimeRender,
      },
      {
        title: intl.get('hwfp.common.model.approval.action').d('审批动作'),
        dataIndex: 'action',
        width: 150,
        render: approveNameRender,
        // TODO: 自己写一个render
      },
      {
        title: intl.get('hwfp.common.model.approval.step').d('审批环节'),
        dataIndex: 'activityName',
        width: 200,
      },
      {
        title: intl.get('hwfp.common.model.approval.owner').d('审批人'),
        dataIndex: 'assigneeName',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.approval.opinion', { title: '审批意见' }).d('审批意见'),
        dataIndex: 'comment',
      },
    ];
    return (
      <Collapse className="form-collapse" style={{ marginBottom: '24px', marginTop: '24px' }}>
        {dataSource.map((item) => (
          <Panel
            header={
              <Form layout="inline" style={{ height: '30px', marginTop: '-8px' }}>
                {this.renderProcessStatus(item)}
                <Form.Item label={intl.get('hwfp.common.model.approval.processName').d('流程名称')}>
                  {item.processName}
                </Form.Item>
                <Form.Item
                  label={intl.get('hwfp.common.model.approval.processStartTime').d('流程开始时间')}
                >
                  {dateTimeRender(item.processStartTime)}
                </Form.Item>
                {item.processEndTime && (
                  <Form.Item
                    label={intl.get('hwfp.common.model.approval.processEndTime').d('流程结束时间')}
                  >
                    {dateTimeRender(item.processEndTime)}
                  </Form.Item>
                )}
              </Form>
            }
          >
            <Table
              bordered
              rowKey="processInstanceId"
              pagination={false}
              dataSource={item.activityInstanceList}
              columns={columns}
              scroll={{ x: tableScrollWidth(columns) }}
            />
          </Panel>
        ))}
      </Collapse>
    );
  }

  render() {
    return <>{this.renderTable()}</>;
  }
}
