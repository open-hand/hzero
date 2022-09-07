/**
 * index - 调度日志
 * @date: 2018-9-17
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * TODO: 国际化
 */
import React from 'react';
// import ReactDOM from 'react-dom';
import { connect } from 'dva';
import { isEmpty, isEqual } from 'lodash';
import { Form, Button, Table, DatePicker, Select, Row, Col, Popconfirm, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { Header, Content } from 'components/Page';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import {
  getDateTimeFormat,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';
import {
  DEFAULT_TIME_FORMAT,
  DEFAULT_DATETIME_FORMAT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import { dateTimeRender, TagRender, operatorRender } from 'utils/renderer';
import { HZERO_FILE, BKT_SDR } from 'utils/config';

import { downloadFile } from 'hzero-front/lib/services/api';

import ErrorModal from './ErrorModal';
import LogModal from './LogModal';
import ProgressModal from '../components/ProgressModal';
import ClearLogsDrawer from './ClearLogsDrawer';

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@connect(({ loading, jobLog }) => ({
  jobLog,
  fetchList: loading.effects['jobLog/fetchLogsList'],
  clearLogLoading: loading.effects['jobLog/clearLogs'],
  errorDetailLoading: loading.effects['jobLog/fetchErrorDetail'],
  logDetailLoading: loading.effects['jobLog/fetchLogDetail'],
  dateTimeFormat: getDateTimeFormat(),
  tenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hsdr.jobLog'] })
export default class JobLog extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false, // 控制调度日志模态框显示
      detailVisible: false,
      handleMsgVisible: false, // 执行日志模态框
      clearLogsVisible: false, // 清除日志模态框
      detailData: {}, // 调度详情数据
      triggerMsgData: {}, // 调度日志详情数据
      clearParamsData: {},
      pullLoading: true, // 加载Spin显示与否
      logContent: '', // 拉取到的日志内容
      tipsMessage: '', // 提示内容
      queryParams: {},
      fromLineNum: 1, // [from, to], start as 1
      pullFailCount: 0, // 拉取次数(最大值为20)
      expandForm: false,
      errorVisible: false,
      errorDetail: '', // 错误详情数据
      logVisible: false,
      logDetail: '', // 日志详情数据
      progressVisible: false,
      progressValue: {},
    };
  }

  pullCatTimer;

  progressTimer;

  componentDidMount() {
    const {
      dispatch,
      jobLog: { skipQuery },
    } = this.props;
    dispatch({
      type: 'jobLog/init',
    });

    this.fetchLogsList({ jobId: skipQuery.jobId });

    // this.fetchLogsList({ jobId: params.jobId });
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (!isEqual(nextProps.jobLog.skipQuery, this.props.jobLog.skipQuery)) {
      this.fetchLogsList({
        jobId: nextProps.jobLog.skipQuery.jobId,
        // groupId: nextProps.jobLog.skipQuery.groupId,
      });
    }
  }

  componentWillUnmount() {
    this.handleClearTimer();
    this.clearProgressTimer();
    this.props.dispatch({
      type: 'jobLog/updateState',
      payload: {
        skipQuery: {},
      },
    });
  }

  // 清除定时器
  @Bind()
  handleClearTimer() {
    clearInterval(this.pullCatTimer);
    this.pullCatTimer = null;
  }

  // 清除进度获取定时器
  @Bind()
  clearProgressTimer() {
    clearInterval(this.progressTimer);
    this.progressTimer = null;
  }

  /**
   * 日志清理
   * @param {object} fieldsValue - 请求参数
   */
  @Bind()
  handleClearLogs(fieldsValue) {
    const {
      dispatch,
      jobLog: { pagination },
    } = this.props;
    dispatch({
      type: 'jobLog/clearLogs',
      payload: {
        ...this.state.groupsData,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleClearLogsModalVisible(false);
        this.fetchLogsList({
          page: pagination.current - 1,
          size: pagination.pageSize,
        });
      }
    });
  }

  /**
   * @function fetchPortalAssign - 获取列表数据
   * @param {object} params - 查询参数
   * @param {number} params.page - 页码
   * @param {number} params.size - 页数
   */
  fetchLogsList(params = {}) {
    const {
      dispatch,
      form,
      jobLog: { query },
    } = this.props;

    const values = form.getFieldsValue();
    const { timeStart, timeEnd } = values;
    const jobId = params.jobId ? params.jobId : values.jobId;
    dispatch({
      type: 'jobLog/fetchLogsList',
      payload: {
        ...query,
        ...params,
        ...values,
        jobId,
        timeStart: timeStart ? moment(timeStart).format(DEFAULT_DATETIME_FORMAT) : null,
        timeEnd: timeEnd ? moment(timeEnd).format(DEFAULT_DATETIME_FORMAT) : null,
      },
    });
  }

  /**
   * @function handleSearch - 搜索表单
   */
  @Bind()
  handleSearch() {
    const { form } = this.props;
    form.validateFields((err) => {
      if (isEmpty(err)) {
        this.fetchLogsList({
          page: 0,
          size: 10,
        });
      }
    });
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 多查询条件展示
   */
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  // 调度日志
  @Bind()
  showTriggerMsgModal(record) {
    this.setState(
      {
        triggerMsgData: record,
      },
      () => {
        this.handleModalVisible(true);
      }
    );
  }

  // 拉取执行日志
  @Bind()
  pullLog() {
    const { dispatch } = this.props;
    const {
      logContent,
      fromLineNum,
      pullFailCount,
      queryParams: { logId, executorAddress, triggerTime },
    } = this.state;

    let tempPullFailCount = pullFailCount;

    if (tempPullFailCount++ > 20) {
      this.setState({
        pullLoading: false,
        tipsMessage: {
          color: 'red',
          content: `<span style="color: red;">${intl
            .get('hsdr.jobLog.view.message.pullLogTips')
            .d('终止请求Rolling日志,请求失败次数超上限,可刷新页面重新加载日志')}</span>`,
        },
      });
      this.handleClearTimer();
      return;
    }

    dispatch({
      type: 'jobLog/fetchLogsCat',
      payload: {
        logId,
        executorAddress,
        fromLineNum,
        triggerTime,
      },
    }).then((data) => {
      if (data) {
        if (fromLineNum !== data.fromLineNum) {
          return;
        }
        if (fromLineNum > data.toLineNum) {
          if (data.end) {
            this.setState({
              ...this.state,
              pullLoading: false,
              tipsMessage: '<br><span style="color: green;">[Rolling Log Finish]</span>',
            });
            this.handleClearTimer();
          }
        }

        this.setState({
          ...this.state,
          fromLineNum: data.toLineNum + 1,
          logContent: logContent + data.logContent,
          pullFailCount: 0,
        });

        if (!this.state.handleMsgVisible) return;
        const wrapEl = document.querySelector('.ant-modal-body');
        // const wrapEl = ReactDOM.findDOMNode(this.handleMsgDrawer).querySelector('.ant-modal-body'); // [TODO]
        wrapEl.scrollTo(0, wrapEl.scrollHeight);
      }
    });
  }

  // 执行日志
  @Bind()
  showHandleMsgModal(record) {
    const { id, executorAddress, triggerTime, handleCode } = record;

    this.setState(
      {
        queryParams: {
          logId: id,
          executorAddress,
          triggerTime: triggerTime ? Date.parse(triggerTime.replace(/-/g, '/')) : null,
        },
        tipsMessage:
          handleCode > 0 ? '<br><span style="color: green;">[Load Log Finish]</span>' : '',
        pullLoading: !(handleCode > 0),
      },
      () => {
        this.handleMsgModalVisible(true);
        // 打开模态框的时候要拉取日志
        this.pullLog();

        this.pullCatTimer = setInterval(() => this.pullLog(), 3000);

        if (handleCode > 0) {
          this.setState({
            ...this.state,
            pullLoading: false,
            tipsMessage: '<br><span style="color: green;">[Load Log Finish]</span>',
          });
          this.handleClearTimer();
        }
      }
    );
  }

  /**
   *是否打开详情模态框
   *
   * @param {boolean} flag true--打开 false--关闭
   * @memberof Tenants
   */
  handleDetailModalVisible(flag) {
    this.setState({
      detailVisible: !!flag,
    });
  }

  /**
   *是否打开调度日志模态框
   *
   * @param {boolean} flag true--打开 false--关闭
   * @memberof Tenants
   */
  handleModalVisible(flag) {
    this.setState({
      modalVisible: !!flag,
    });
  }

  /**
   *是否打开执行日志模态框
   *
   * @param {boolean} flag true--打开 false--关闭
   * @memberof Tenants
   */
  handleMsgModalVisible(flag) {
    this.handleClearTimer();
    // 执行日志弹窗关闭后重置参数
    this.setState({
      ...this.state,
      handleMsgVisible: !!flag,
      pullLoading: true,
      logContent: '',
      tipsMessage: '',
      fromLineNum: 1,
      pullFailCount: 0,
    });
  }

  /**
   *是否打开清除日志模态框
   *
   * @param {boolean} flag true--打开 false--关闭
   */
  handleClearLogsModalVisible(flag) {
    this.setState({
      clearLogsVisible: flag,
    });
  }

  @Bind()
  showClearLogDrawer() {
    this.handleClearLogsModalVisible(true);
  }

  @Bind()
  hideClearLogDrawer() {
    this.handleClearLogsModalVisible(false);
  }

  /**
   * handleStandardTableChange-Table 分页信息改变
   */
  @Bind()
  handleStandardTableChange(pagination) {
    const { query } = this.props;
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      ...query,
    };
    this.fetchLogsList(params);
  }

  /**
   * 数据列表，删除
   * @param {obejct} record - 操作对象
   */
  @Bind()
  handleDeleteContent(record) {
    const {
      dispatch,
      jobLog: { pagination },
    } = this.props;
    dispatch({
      type: 'jobLog/deleteLogs',
      payload: {
        logId: record.logId,
        jobId: record.jobId,
        _token: record._token,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchLogsList({
          page: pagination.current - 1,
          size: pagination.pageSize,
        });
      }
    });
  }

  /**
   * 下载
   * @param {object} record - 流程对象
   */
  @Bind()
  downloadLogFile(record) {
    const { tenantRoleLevel } = this.props;
    const { tenantId } = record;
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

  /**
   * @function renderForm - 渲染搜索表单
   */
  renderFilterForm() {
    const {
      dateTimeFormat,
      form: { getFieldDecorator, getFieldValue },
      tenantRoleLevel,
      jobLog: { jobResultList = [], clientResultList = [] },
    } = this.props;
    const { expandForm = true } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.jobId').d('任务ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('taskId', {
                rules: [
                  {
                    pattern: /^[0-9]*$/,
                    message: intl.get('hsdr.concRequest.validation.digital').d('只能输入数字'),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.jobResult').d('调度结果')}
              {...SEARCH_FORM_ITEM_LAYOUT}
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
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.clientResult').d('客户端执行结果')}
              {...SEARCH_FORM_ITEM_LAYOUT}
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
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          type="flex"
          align="bottom"
          gutter={24}
          style={expandForm ? expandFormStyle : noExpandFormStyle}
        >
          {!tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hsdr.jobLog.model.jobLog.tenantName').d('租户')}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.executorName').d('执行器名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('executorName', {})(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.jobCode').d('任务编码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('jobCode')(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          </Col>
          {tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('hsdr.jobLog.model.jobLog.description').d('任务描述')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('description')(<Input trim />)}
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row
          type="flex"
          align="bottom"
          gutter={24}
          style={expandForm ? expandFormStyle : noExpandFormStyle}
        >
          {!tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('hsdr.jobLog.model.jobLog.description').d('任务描述')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('description')(<Input trim />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.startTime').d('执行开始时间')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('timeStart')(
                <DatePicker
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
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
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobLog.model.jobLog.endTime').d('执行结束时间')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('timeEnd')(
                <DatePicker
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
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
      </Form>
    );
  }

  @Bind()
  showErrorModal(record) {
    const { dispatch } = this.props;
    this.setState({ errorVisible: true }, () => {
      dispatch({
        type: 'jobLog/fetchErrorDetail',
        payload: {
          logId: record.logId,
        },
      }).then((res) => {
        if (res) {
          this.setState({ errorDetail: res });
        }
      });
    });
  }

  @Bind()
  closeErrorModal() {
    this.setState({
      errorVisible: false,
      errorDetail: '',
    });
  }

  @Bind()
  showLogModal(record) {
    const { dispatch } = this.props;
    this.setState({ logVisible: true }, () => {
      dispatch({
        type: 'jobLog/fetchLogDetail',
        payload: {
          logId: record.logId,
        },
      }).then((res) => {
        if (res) {
          this.setState({ logDetail: res });
        }
      });
    });
  }

  @Bind()
  closeLogModal() {
    this.setState({
      logVisible: false,
      logDetail: '',
    });
  }

  // 获取任务进度
  @Bind()
  fetchProgress(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobLog/fetchProgress',
      payload: {
        logId: record.logId,
      },
    }).then((res) => {
      if (res) {
        this.setState({ progressValue: JSON.parse(res) });
        if (res.progress === 100) {
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
    const {
      jobLog: { skipQuery, pagination },
    } = this.props;
    this.setState(
      {
        progressVisible: false,
      },
      () => {
        this.clearProgressTimer();
        this.fetchLogsList({
          jobId: skipQuery.jobId,
          page: pagination.current - 1,
          size: pagination.pageSize,
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
      fetchList,
      jobLog: { jobLogList = [], pagination, clearTypeList = [] },
      tenantRoleLevel,
      errorDetailLoading,
      logDetailLoading,
      clearLogLoading = false,
      match: { path },
    } = this.props;
    const {
      errorVisible,
      errorDetail,
      logVisible,
      logDetail,
      progressVisible,
      progressValue,
      clearLogsVisible,
    } = this.state;
    const errorProps = {
      errorDetailLoading,
      errorDetail,
      errorVisible,
      onOk: this.closeErrorModal,
    };
    const logProps = {
      logDetailLoading,
      logDetail,
      logVisible,
      onOk: this.closeLogModal,
    };
    const progressProps = {
      progressVisible,
      progressValue,
      onOk: this.closeProgressModal,
    };
    const columns = [
      {
        title: intl.get('hsdr.jobLog.model.jobLog.jobId').d('任务ID'),
        width: 100,
        dataIndex: 'taskId',
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.jobCode').d('任务编码'),
        width: 150,
        dataIndex: 'jobCode',
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.description').d('任务描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.tenantName').d('租户'),
        width: 150,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hsdr.jobLog.model.jobLog.jobResult').d('调度结果'),
        width: 100,
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
        width: 150,
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
        width: 200,
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
                      meaning: '调度日志-删除',
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
                      meaning: '调度日志-任务进度',
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
                      meaning: '调度日志-错误详情',
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
                      meaning: '调度日志-日志文件',
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
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <>
        <Header title={intl.get('hsdr.jobLog.view.message.title').d('调度日志')}>
          <ButtonPermission
            type="primary"
            icon="delete"
            onClick={this.showClearLogDrawer}
            permissionList={[
              {
                code: `${path}.button.clearLogs`,
                type: 'button',
                meaning: '调度日志-日志清理',
              },
            ]}
          >
            {intl.get('hsdr.jobLog.view.button.clearLogs').d('日志清理')}
          </ButtonPermission>
        </Header>
        <Content>
          {this.renderFilterForm()}
          <Table
            bordered
            rowKey="logId"
            loading={fetchList}
            dataSource={jobLogList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns, 200) }}
            pagination={pagination}
            onChange={this.handleStandardTableChange}
          />
          <ClearLogsDrawer
            title={intl.get('hsdr.jobLog.view.button.clearLogs').d('日志清理')}
            loading={clearLogLoading}
            modalVisible={clearLogsVisible}
            clearTypeList={clearTypeList}
            onCancel={this.hideClearLogDrawer}
            onOk={this.handleClearLogs}
          />
          {errorVisible && <ErrorModal {...errorProps} />}
          {logVisible && <LogModal {...logProps} />}
          {progressVisible && <ProgressModal {...progressProps} />}
        </Content>
      </>
    );
  }
}
