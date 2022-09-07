import React, { PureComponent } from 'react';
import { Popconfirm, Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { dateTimeRender, operatorRender, TagRender, yesOrNoRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

/**
 * 监控流程数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  /**
   * 请求ID
   * @param {object} record - 消息模板对象
   */
  concurrentMsgOption(record) {
    this.props.concurrentMsgContent(record);
  }

  // /**
  //  * 日志信息
  //  * @param {object} record - 消息模板对象
  //  */
  // logMsgOption(record) {
  //   this.props.logMsgContent(record);
  // }
  /**
   * 日志
   * @param {object} record - 头数据
   */
  changeLog(record) {
    this.props.logContent(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      onChange,
      path,
      tenantRoleLevel,
      dataSource = [],
      pagination = {},
      changeOptionContent = (e) => e,
    } = this.props;

    const columns = [
      {
        title: intl.get('hsdr.concRequest.model.concRequest.jobId').d('任务ID'),
        dataIndex: 'taskId',
        width: 100,
      },
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.concCode').d('请求编码'),
        dataIndex: 'concCode',
        width: 150,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.concName').d('请求名称'),
        dataIndex: 'concName',
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.username').d('提交人'),
        dataIndex: 'username',
        width: 150,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.cycleFlag').d('周期性'),
        dataIndex: 'cycleFlag',
        width: 90,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.requestParam').d('请求参数'),
        dataIndex: 'requestParam',
        width: 300,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.startDate').d('周期开始时间'),
        dataIndex: 'startDate',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.endDate').d('周期结束时间'),
        dataIndex: 'endDate',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.parentId').d('父任务ID'),
        dataIndex: 'parentId',
        width: 100,
      },
      {
        title: intl.get('hsdr.concRequest.model.concRequest.jobStatus').d('状态'),
        dataIndex: 'jobStatusMeaning',
        width: 100,
        render: (text, record) => {
          const statusList = [
            { status: 'NORMAL', color: 'green' },
            { status: 'PAUSED', color: 'gold' },
            { status: 'BLOCKED', color: 'volcano' },
            { status: 'ERROR', color: 'red' },
            { status: 'COMPLETE', color: '' },
            { status: 'NONE', color: '' },
          ];
          return TagRender(record.jobStatus, statusList, text);
        },
        fixed: 'right',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'log',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.log`,
                    type: 'button',
                    meaning: '并发请求-日志',
                  },
                ]}
                onClick={() => this.changeLog(record)}
              >
                {intl.get('hsdr.concRequest.view.option.log').d('日志')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hsdr.concRequest.view.option.log').d('日志'),
          });
          if (record.cycleFlag === 1 && record.jobStatus === 'NORMAL') {
            operators.push({
              key: 'excute',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.jobInfo.view.message.confirm.trigger')
                    .d('是否执行该任务')}？`}
                  onConfirm={changeOptionContent.bind(
                    this,
                    { jobId: record.jobId },
                    'triggerJobInfo'
                  )}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.trigger`,
                        type: 'button',
                        meaning: '并发请求-执行',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.trigger').d('执行')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hsdr.jobInfo.view.message.tooltipMsg').d('执行：手动执行任务一次'),
            });
            operators.push({
              key: 'pause',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.jobInfo.view.message.confirm.pause')
                    .d('是否暂停该任务')}？`}
                  onConfirm={changeOptionContent.bind(
                    this,
                    { jobId: record.jobId },
                    'pauseJobInfo'
                  )}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.pause`,
                        type: 'button',
                        meaning: '并发请求-暂停',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.pause').d('暂停')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.pause').d('暂停'),
            });
          }
          if (record.cycleFlag === 1 && record.jobStatus !== 'NONE') {
            operators.push({
              key: 'stop',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.jobInfo.view.message.confirm.stop')
                    .d('是否终止该任务')}？`}
                  onConfirm={changeOptionContent.bind(this, { jobId: record.jobId }, 'stopJob')}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.stop`,
                        type: 'button',
                        meaning: '并发请求-终止',
                      },
                    ]}
                  >
                    {intl.get('hsdr.jobInfo.option.stop').d('终止')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hsdr.jobInfo.option.stop').d('终止'),
            });
          }
          if (record.cycleFlag === 1 && record.jobStatus === 'PAUSED') {
            operators.push({
              key: 'resume',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.notice.view.message.confirm.resume')
                    .d('是否恢复该任务')}？`}
                  onConfirm={changeOptionContent.bind(
                    this,
                    { jobId: record.jobId },
                    'resumeJobInfo'
                  )}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.resume`,
                        type: 'button',
                        meaning: '并发请求-恢复',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.resume').d('恢复')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.resume').d('恢复'),
            });
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <Table
        bordered
        rowKey="requestId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
      />
    );
  }
}
