import React from 'react';
import { Button, Col, DatePicker, Form, Modal, Popconfirm, Row, Select, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getDateTimeFormat, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import { dateTimeRender, TagRender, operatorRender } from 'utils/renderer';
import { HZERO_FILE, BKT_SDR } from 'utils/config';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import notification from 'utils/notification';

import { downloadFile } from 'hzero-front/lib/services/api';
import ProgressModal from '../components/ProgressModal';

const formLayout = {
  labelCol: { span: 11 },
  wrapperCol: { span: 13 },
};

@Form.create({ fieldNameProp: null })
export default class LogDrawer extends React.Component {
  state = {
    tenantRoleLevel: isTenantRoleLevel(),
    progressVisible: false,
    progressValue: {},
  };

  progressTimer;

  componentWillUnmount() {
    this.clearProgressTimer();
  }

  // 清除进度获取定时器
  @Bind()
  clearProgressTimer() {
    clearInterval(this.progressTimer);
    this.progressTimer = null;
  }

  @Bind()
  showErrorModal(record) {
    const { onError = (e) => e } = this.props;
    onError({ logId: record.logId });
  }

  @Bind()
  handleSearch() {
    const { form, onSearch = (e) => e } = this.props;
    const fieldsValue = form.getFieldsValue();
    fieldsValue.timeStart =
      fieldsValue.timeStart && fieldsValue.timeStart.format(DEFAULT_DATETIME_FORMAT);
    fieldsValue.timeEnd =
      fieldsValue.timeEnd && fieldsValue.timeEnd.format(DEFAULT_DATETIME_FORMAT);
    onSearch(fieldsValue);
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  showLogModal(record) {
    const { onFetchLogDetail } = this.props;
    onFetchLogDetail(record);
  }

  /**
   * 下载
   * @param {object} record - 流程对象
   */
  @Bind()
  downloadLogFile(record) {
    const { tenantId } = record;
    const { tenantRoleLevel } = this.state;
    const api = tenantRoleLevel
      ? `${HZERO_FILE}/v1/${tenantId}/files/download`
      : `${HZERO_FILE}/v1/files/download`;
    if (record.logFlag === 1) {
      this.showLogModal(record);
    }
    if (record.logFlag === 0 && record.logUrl !== '') {
      downloadFile({
        requestUrl: api,
        queryParams: [
          { name: 'bucketName', value: BKT_SDR },
          { name: 'directory', value: 'hsdr01' },
          { name: 'url', value: record.logUrl },
        ],
      });
    }
  }

  @Bind()
  handleDeleteContent(record) {
    const { onDelete = (e) => e } = this.props;
    onDelete(record);
  }

  @Bind()
  handleTableChange(page) {
    const { onSearch = (e) => e } = this.props;
    onSearch({ page });
  }

  /**
   * @function renderForm - 渲染搜索表单
   */
  renderFilterForm() {
    const {
      form: { getFieldDecorator, getFieldValue },
      jobLogLdp = {},
    } = this.props;
    const dateTimeFormat = getDateTimeFormat();
    const { jobResultList = [], clientResultList = [] } = jobLogLdp;
    return (
      <Form layout="inline" className="more-fields-form">
        <Row>
          <Col span={18}>
            <Row>
              <Col span={8}>
                <Form.Item
                  label={intl.get('hsdr.jobLog.model.jobLog.jobResult').d('调度结果')}
                  {...formLayout}
                >
                  {getFieldDecorator('jobResult')(
                    <Select allowClear>
                      {jobResultList.map((item) => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  label={intl.get('hsdr.jobLog.model.jobLog.clientResult').d('客户端执行结果')}
                  {...formLayout}
                >
                  {getFieldDecorator('clientResult')(
                    <Select allowClear>
                      {clientResultList.map((item) => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  label={intl.get('hsdr.jobLog.model.jobLog.startTime').d('执行开始时间')}
                  {...formLayout}
                >
                  {getFieldDecorator('timeStart')(
                    <DatePicker
                      format={dateTimeFormat}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        getFieldValue('timeEnd') &&
                        moment(getFieldValue('timeEnd')).isBefore(currentDate, 'day')
                      }
                    />
                  )}
                </Form.Item>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <Form.Item
                  label={intl.get('hsdr.jobLog.model.jobLog.endTime').d('执行结束时间')}
                  {...formLayout}
                >
                  {getFieldDecorator('timeEnd')(
                    <DatePicker
                      format={dateTimeFormat}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        getFieldValue('timeStart') &&
                        moment(getFieldValue('timeStart')).isAfter(currentDate, 'day')
                      }
                    />
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Col>
          <Col span={6} className="search-btn-more">
            <Form.Item>
              <Button style={{ marginRight: 8 }} onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  // 获取任务进度
  @Bind()
  fetchProgress(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'concRequest/fetchProgress',
      payload: {
        logId: record.logId,
      },
    }).then((res) => {
      if (res) {
        this.setState({ progressValue: JSON.parse(res) });
        if (res === 100) {
          this.clearProgressTimer();
          notification.info({
            message: intl.get('hsdr.jobLog.model.jobLog.showInfo').d('任务已执行完成！'),
          });
        }
      }
    });
  }

  // 打开任务进度弹窗
  @Bind()
  showProgressModal(record) {
    this.setState({ progressVisible: true }, () => {
      this.fetchProgress(record);
      this.progressTimer = setInterval(() => this.fetchProgress(record), 5000);
    });
  }

  // 关闭任务进度弹窗
  @Bind()
  closeProgressModal() {
    const { onSearch, jobPagination, form } = this.props;
    this.setState(
      {
        progressVisible: false,
      },
      () => {
        this.clearProgressTimer();
        onSearch({
          page: jobPagination.current - 1,
          size: jobPagination.pageSize,
          ...form.getFieldsValue(),
        });
      }
    );
  }

  // 导出
  @Bind()
  handleDownload(record) {
    const { tenantRoleLevel } = this.props;
    const { tenantId } = record;
    const api = tenantRoleLevel
      ? `${HZERO_FILE}/v1/${tenantId}/files/download`
      : `${HZERO_FILE}/v1/files/download`;
    downloadFile({
      requestUrl: api,
      queryParams: [
        { name: 'bucketName', value: BKT_SDR },
        { name: 'url', value: record.outputFile },
      ],
    });
  }

  render() {
    const {
      title,
      modalVisible,
      initLoading,
      onCancel,
      path,
      jobLogList = [],
      jobPagination = {},
    } = this.props;
    const { progressVisible, progressValue } = this.state;
    const progressProps = {
      progressVisible,
      progressValue,
      onOk: this.closeProgressModal,
    };
    const columns = [
      {
        title: intl.get('hsdr.jobLog.model.jobLog.tenantName').d('租户'),
        width: 150,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.jobResult').d('调度结果'),
        width: 90,
        dataIndex: 'jobResultMeaning',
        render: (text, record) => {
          const statusList = [
            { status: 'SUCCESS', color: 'green' },
            { status: 'FAILURE', color: 'red' },
            { status: 'DOING', color: '' },
          ];
          return TagRender(record.jobResult, statusList, text);
        },
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.clientResult').d('客户端执行结果'),
        dataIndex: 'clientResultMeaning',
        width: 120,
        render: (text, record) => {
          const statusList = [
            { status: 'SUCCESS', color: 'green' },
            { status: 'FAILURE', color: 'red' },
            { status: 'WARNING', color: 'orange' },
            { status: 'DOING', color: '' },
          ];
          return TagRender(record.clientResult, statusList, text);
        },
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.executorName').d('执行器名称'),
        dataIndex: 'executorName',
        width: 150,
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.address').d('执行地址'),
        dataIndex: 'address',
        width: 200,
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.startTime').d('执行开始时间'),
        width: 150,
        dataIndex: 'startTime',
        render: dateTimeRender,
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.endTime').d('执行结束时间'),
        width: 150,
        dataIndex: 'endTime',
        render: dateTimeRender,
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.message').d('错误信息'),
        dataIndex: 'messageHeader',
        width: 250,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 220,
        dataIndex: 'operator',
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'delete',
            ele: (
              <Popconfirm
                placement="topRight"
                title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                onConfirm={() => this.handleDeleteContent(record)}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '并发请求-删除',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              </Popconfirm>
            ),
            len: 2,
            title: intl.get('hzero.common.button.delete').d('删除'),
          });
          if (record.clientResult === 'DOING') {
            operators.push({
              key: 'taskProgress',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.taskProgress`,
                      type: 'button',
                      meaning: '并发请求-任务进度',
                    },
                  ]}
                  onClick={() => this.showProgressModal(record)}
                >
                  {intl.get('hsdr.jobLog.model.jobLog.taskProgress').d('任务进度')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hsdr.jobLog.model.jobLog.taskProgress').d('任务进度'),
            });
          }
          if (record.clientResult === 'FAILURE') {
            operators.push({
              key: 'errorDetail',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.errorDetail`,
                      type: 'button',
                      meaning: '并发请求-错误详情',
                    },
                  ]}
                  onClick={() => this.showErrorModal(record)}
                >
                  {intl.get('hsdr.jobLog.model.jobLog.errorDetail').d('错误详情')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hsdr.jobLog.model.jobLog.errorDetail').d('错误详情'),
            });
          }
          if ((record.logUrl && record.logFlag === 0) || record.logFlag === 1) {
            operators.push({
              key: 'logDetail',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.logUrl`,
                      type: 'button',
                      meaning: '并发请求-日志文件',
                    },
                  ]}
                  onClick={() => this.downloadLogFile(record)}
                >
                  {record.logFlag === 1
                    ? intl.get('hsdr.jobLog.model.jobLog.logDetail').d('日志详情')
                    : intl.get('hsdr.jobLog.model.jobLog.logUrl').d('日志文件')}
                </ButtonPermission>
              ),
              len: 4,
              title:
                record.logFlag === 1
                  ? intl.get('hsdr.jobLog.model.jobLog.logDetail').d('日志详情')
                  : intl.get('hsdr.jobLog.model.jobLog.logUrl').d('日志文件'),
            });
          }
          if (record.outputFile) {
            operators.push({
              key: 'export',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.export`,
                      type: 'button',
                      meaning: '调度日志-导出文件',
                    },
                  ]}
                  onClick={() => this.handleDownload(record)}
                >
                  {intl.get('hsdr.jobLog.view.button.exportFile').d('导出文件')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hsdr.jobLog.view.button.exportFile').d('导出文件'),
            });
          }

          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter((col) => (isTenantRoleLevel() ? col.dataIndex !== 'tenantName' : true));
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width={1000}
        visible={modalVisible}
        onCancel={onCancel}
        footer={false}
      >
        <div className="table-list-search">{this.renderFilterForm()}</div>
        <Table
          bordered
          rowKey="logId"
          loading={initLoading}
          dataSource={jobLogList}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={jobPagination}
          onChange={this.handleTableChange}
        />
        {progressVisible && <ProgressModal {...progressProps} />}
      </Modal>
    );
  }
}
