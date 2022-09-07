/**
 * jobInfo - 调度任务
 * @date: 2018-9-7
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Form, Popconfirm, Table, Tooltip, Tag } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import cacheComponent from 'components/CacheComponent';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import { TagRender, yesOrNoRender, operatorRender } from 'utils/renderer';

import Drawer from './Drawer';
import LogDrawer from './LogDrawer';
import LogErrorDrawer from './LogErrorDrawer';
import FilterForm from './FilterForm';
import LogModal from './LogModal';

@connect(({ loading, jobInfo, jobLog }) => ({
  jobInfo,
  jobLog,
  fetchList: loading.effects['jobInfo/fetchJobInfo'],
  creating: loading.effects['jobInfo/createJobInfo'],
  updating: loading.effects['jobInfo/updateJobInfo'],
  fetchJobError: loading.effects['jobInfo/fetchJobLogError'],
  fetchJogLoading: loading.effects['jobInfo/fetchJobLog'],
  detailLoading: loading.effects['jobInfo/queryJobInfo'],
  logDetailLoading: loading.effects['jobLog/fetchLogDetail'],
  checkLoading: loading.effects['jobInfo/checkJobInfo'],
  configLoading: loading.effects['jobInfo/executorConfig'],
  deleteLoading: loading.effects['jobInfo/deleteJobInfo'],
  copyLoading: loading.effects['jobInfo/copyJobInfo'],
  tenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@Form.create({
  fieldNameProp: null,
})
@formatterCollections({
  code: ['hsdr.jobInfo', 'hsdr.jobLog'],
})
@cacheComponent({
  cacheKey: '/hsdr/job-info/list',
})
export default class JobInfo extends React.PureComponent {
  form;

  state = {
    jobModalVisible: false,
    jobErrorModalVisible: false,
    jobLogId: '',
    selectedRowKeys: [],
    selectRows: [],
    logVisible: false,
    logDetail: '',
  };

  componentDidMount() {
    const { dispatch } = this.props;
    this.fetchJobInfo();
    dispatch({
      type: 'jobInfo/init',
    });
  }

  @Bind()
  queryGroups() {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobInfo/fetchGroupsList',
    });
  }

  /**
   * @function fetchJobInfo - 获取列表数据
   * @param {object} params - 查询参数
   * @param {number} params.page - 页码
   * @param {number} params.size - 页数
   */
  fetchJobInfo(params = {}) {
    const {
      dispatch,
      jobInfo: { pagination = {} },
    } = this.props;
    const filterValue = this.form === undefined ? {} : this.form.getFieldsValue();
    dispatch({
      type: 'jobInfo/fetchJobInfo',
      payload: {
        page: pagination,
        ...filterValue,
        ...params,
      },
    });
  }

  fetchJobDetail(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobInfo/queryJobInfo',
      payload: {
        id: params.jobId,
      },
    }).then((res) => {
      if (res) {
        if (res.executorStrategy === 'JOB_WEIGHT') {
          this.handleConfig(res);
        }
      }
    });
  }

  /**
   * @function handleSearch - 搜索表单
   */
  @Bind()
  handleSearch() {
    this.fetchJobInfo({
      page: {},
    });
  }

  /**
   * 获取查询表单组件this对象
   * @param {object} ref - 查询表单组件this
   */
  @Bind()
  handleBindRef(ref) {
    this.form = (ref.props || {}).form;
  }

  /**
   * @function handleModalVisible - 控制modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   */
  @Bind()
  handleModalVisible(flag) {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobInfo/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
    // 清空执行器配置列表
    if (!flag) {
      dispatch({
        type: 'jobInfo/updateState',
        payload: {
          executorConfigList: [],
        },
      });
    }
  }

  /**
   * @function showCreateModal - 显示新增modal
   */
  @Bind()
  showCreateModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobInfo/updateState',
      payload: {
        jobInfoDetail: {},
      },
    });
    this.handleModalVisible(true);
  }

  /**
   * 检查执行器
   * @param {string} executorId - 执行器ID
   */
  @Bind()
  handleCheck(executorId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobInfo/checkJobInfo',
      payload: {
        executorId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  @Bind()
  handleAdd(fieldsValue) {
    const {
      dispatch,
      jobInfo: { jobInfoDetail = {} },
    } = this.props;
    const { jobId } = jobInfoDetail;
    const { strategyParam, ...others } = fieldsValue;
    dispatch({
      type: `jobInfo/${jobId ? 'updateJobInfo' : 'createJobInfo'}`,
      payload: {
        ...jobInfoDetail,
        ...others,
        strategyParam: JSON.stringify(fieldsValue.strategyParam),
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleModalVisible(false);
        this.fetchJobInfo();
      }
    });
  }

  /**
   * 获取执行器列表
   * @param {object} data 选择的执行器策略数据
   */
  @Bind()
  handleConfig(data) {
    const { dispatch } = this.props;
    if (data) {
      dispatch({
        type: 'jobInfo/executorConfigByJob',
        payload: {
          jobId: data.jobId,
          executorId: data.executorId,
        },
      }).then((res) => {
        if (res) {
          const arr = [];
          Object.keys(res).forEach((item) => {
            arr.push({
              address: item,
              weight: res[item],
            });
          });
          dispatch({
            type: 'jobInfo/updateState',
            payload: {
              executorConfigList: arr,
            },
          });
        }
      });
    } else {
      dispatch({
        type: 'jobInfo/updateState',
        payload: {
          executorConfigList: [],
        },
      });
    }
  }

  /**
   * @function handleEditJob- 操作job
   * @param {object} record - 行数据
   * @param {string} type - 操作任务的类型
   */
  handleEditJob(record, type) {
    const { dispatch } = this.props;
    dispatch({
      type: `jobInfo/${type}`,
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchJobInfo();
      }
    });
  }

  /**
   * @function handleEditJob- 删除job
   */
  @Bind()
  handleDelete() {
    const { dispatch } = this.props;
    const { selectRows } = this.state;
    dispatch({
      type: 'jobInfo/deleteJobInfo',
      payload: selectRows,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchJobInfo();
        this.setState({
          selectRows: [],
          selectedRowKeys: [],
        });
      }
    });
  }

  /**
   * @function handleUpdateEmail - 编辑
   * @param {object} record - 行数据
   */
  @Bind()
  handleUpdate(record) {
    this.fetchJobDetail(record);
    this.handleModalVisible(true);
  }

  @Bind()
  handleCopy(record) {
    const { dispatch } = this.props;
    this.showCreateModal();
    dispatch({
      type: 'jobInfo/copyJobInfo',
      payload: {
        jobId: record.jobId,
      },
    }).then((res) => {
      if (res) {
        dispatch({
          type: 'jobInfo/updateState',
          payload: {
            jobInfoDetail: res,
          },
        });
      }
    });
  }

  /**
   * @function handlePagination - 分页操作
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchJobInfo({
      page: pagination,
    });
  }

  @Bind()
  hideJobLogErrorDrawer() {
    this.setState({
      jobErrorModalVisible: false,
    });
  }

  @Bind()
  handleJobError(data) {
    const { dispatch } = this.props;
    this.setState({
      jobErrorModalVisible: true,
    });
    dispatch({
      type: 'jobInfo/fetchJobLogError',
      payload: data,
    });
  }

  @Bind()
  handleDeleteJobLog(record) {
    const {
      dispatch,
      jobInfo: { jobPagination = {} },
    } = this.props;
    dispatch({
      type: 'jobInfo/deleteJobLog',
      payload: {
        logId: record.logId,
        jobId: record.jobId,
        _token: record._token,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchJobLog({
          page: jobPagination.current - 1,
          size: jobPagination.pageSize,
        });
      }
    });
  }

  @Bind()
  showJobDrawer(flag) {
    this.setState({
      jobModalVisible: flag,
    });
  }

  @Bind()
  hideJobLogDrawer() {
    this.showJobDrawer(false);
  }

  @Bind()
  fetchJobLog(params = {}) {
    const { dispatch } = this.props;
    const { jobLogId } = this.state;
    return dispatch({
      type: 'jobInfo/fetchJobLog',
      payload: {
        jobId: jobLogId,
        ...params,
      },
    });
  }

  /**
   * 跳转到日志页面
   * @param {object} params - 头数据
   */
  handleLogContent(params) {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobInfo/updateState',
      payload: {
        jobLogList: [],
        jobPagination: {},
      },
    });
    this.setState({
      jobLogId: params.jobId,
    });
    this.showJobDrawer(true);
    this.fetchJobLog(params);
  }

  @Bind()
  handleSelectTable(keys, rows) {
    this.setState({
      selectedRowKeys: keys,
      selectRows: rows,
    });
  }

  @Bind()
  handleFetchLogDetail(record) {
    const { dispatch } = this.props;
    this.setState(
      {
        logVisible: true,
      },
      () => {
        dispatch({
          type: 'jobLog/fetchLogDetail',
          payload: {
            logId: record.logId,
          },
        }).then((res) => {
          if (res) {
            this.setState({
              logDetail: res,
            });
          }
        });
      }
    );
  }

  @Bind()
  closeLogDetailModal() {
    this.setState({
      logVisible: false,
      logDetail: '',
    });
  }

  render() {
    const {
      updating,
      creating,
      fetchList,
      tenantId,
      tenantRoleLevel,
      match: { path },
      fetchJogLoading = false,
      fetchJobError = false,
      jobInfo = {},
      dispatch,
      checkLoading = false,
      configLoading = false,
      detailLoading = false,
      deleteLoading = false,
      copyLoading = false,
      logDetailLoading = false,
    } = this.props;
    const {
      jobErrorModalVisible,
      jobModalVisible,
      selectedRowKeys,
      jobLogFirstLoadedLoading,
      logVisible,
      logDetail,
    } = this.state;
    const {
      modalVisible,
      jobInfoDetail,
      jobInfoList = [],
      groupsList = [],
      pagination = {},
      jobLogList = [],
      jobPagination = {},
      jobErrorDetail = {},
      jobLogLdp = {},
      executorConfigList = {},
      glueTypeList = [],
      jobStatusList = [],
      sourceFlagList = [],
    } = jobInfo;
    const { jobId } = jobInfoDetail;
    const requestLoading = {
      save: updating,
      create: creating,
    };
    const logDrawer = {
      tenantId,
      dispatch,
      jobLogLdp,
      jobLogList,
      jobPagination,
      path,
      title: intl.get('hsdr.jobLog.view.message.title').d('调度日志'),
      initLoading: fetchJogLoading,
      loading: fetchJogLoading,
      modalVisible: jobModalVisible,
      onSearch: this.fetchJobLog,
      onCancel: this.hideJobLogDrawer,
      onError: this.handleJobError,
      onDelete: this.handleDeleteJobLog,
      jobLogFirstLoadedLoading,
      onFetchLogDetail: this.handleFetchLogDetail,
    };
    const logErrorProps = {
      jobErrorDetail,
      initLoading: fetchJobError,
      modalVisible: jobErrorModalVisible,
      onOk: this.hideJobLogErrorDrawer,
      onCancel: this.hideJobLogErrorDrawer,
    };

    const logDetailProps = {
      logDetail,
      logVisible,
      logDetailLoading,
      onOk: this.closeLogDetailModal,
    };

    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleSelectTable,
      getCheckboxProps: (record) => ({
        disabled: record.jobStatus !== 'NONE',
      }),
    };

    const columns = [
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.id').d('任务ID'),
        width: 150,
        dataIndex: 'taskId',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.tenantName').d('所属租户'),
        width: 150,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.jobCode').d('任务编码'),
        width: 150,
        dataIndex: 'jobCode',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.glueType').d('任务类型'),
        width: 100,
        dataIndex: 'glueTypeMeaning',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.jobHandler').d('JobHandler'),
        width: 120,
        dataIndex: 'jobHandler',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.executorName').d('执行器'),
        width: 120,
        dataIndex: 'executorName',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.sourceFlag').d('来源标识'),
        width: 120,
        dataIndex: 'sourceFlag',
        render: (value, record) =>
          value === 'JOB' ? (
            <Tag color="green">{record.sourceFlagMeaning}</Tag>
          ) : (
            <Tag color="blue">{record.sourceFlagMeaning}</Tag>
          ),
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.jobDesc').d('任务描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.jobCron').d('Cron'),
        dataIndex: 'jobCron',
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.parentId').d('父任务ID'),
        dataIndex: 'parentId',
        width: 100,
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.cycleFlag').d('周期性'),
        dataIndex: 'cycleFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hsdr.jobInfo.model.jobInfo.jobStatusMeaning').d('状态'),
        width: 100,
        fixed: 'right',
        dataIndex: 'jobStatusMeaning',
        render: (text, record) => {
          const statusList = [
            {
              status: 'NORMAL',
              color: 'green',
            },
            {
              status: 'PAUSED',
              color: 'gold',
            },
            {
              status: 'BLOCKED',
              color: 'volcano',
            },
            {
              status: 'ERROR',
              color: 'red',
            },
            {
              status: 'NONE',
              color: '',
            },
            {
              status: 'COMPLETE',
              color: '',
            },
          ];
          return TagRender(record.jobStatus, statusList, text);
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 160,
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
                    meaning: '调度任务-日志',
                  },
                ]}
                onClick={this.handleLogContent.bind(this, {
                  jobId: record.jobId,
                })}
              >
                {intl.get('hsdr.jobInfo.view.option.log').d('日志')}{' '}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hsdr.jobInfo.view.option.log').d('日志'),
          });
          operators.push({
            key: 'copy',
            ele: (
              <Popconfirm
                title={`${intl
                  .get('hsdr.jobInfo.view.message.confirm.copy')
                  .d('是否复制该任务')}？`}
                onConfirm={this.handleCopy.bind(this, record)}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.copy`,
                      type: 'button',
                      meaning: '调度任务-复制',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}{' '}
                </ButtonPermission>{' '}
              </Popconfirm>
            ),
            len: 2,
            title: intl.get('hzero.common.button.copy').d('复制'),
          });
          if (record.jobStatus === 'NORMAL') {
            if (record.cycleFlag === 1) {
              operators.push({
                key: 'trigger',
                ele: (
                  <Popconfirm
                    title={`${intl
                      .get('hsdr.jobInfo.view.message.confirm.trigger')
                      .d('是否执行该任务')}？`}
                    onConfirm={this.handleEditJob.bind(this, record, 'triggerJobInfo')}
                  >
                    <Tooltip
                      placement="top"
                      title={intl
                        .get('hsdr.jobInfo.view.message.tooltipMsg')
                        .d('执行：手动执行任务一次')}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.trigger`,
                            type: 'button',
                            meaning: '调度任务-执行',
                          },
                        ]}
                      >
                        {intl.get('hzero.common.button.trigger').d('执行')}{' '}
                      </ButtonPermission>{' '}
                    </Tooltip>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.trigger').d('执行'),
              });
            }
            operators.push({
              key: 'pause',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.jobInfo.view.message.confirm.pause')
                    .d('是否暂停该任务')}？`}
                  onConfirm={this.handleEditJob.bind(this, record, 'pauseJobInfo')}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.pause`,
                        type: 'button',
                        meaning: '调度任务-暂停',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.pause').d('暂停')}{' '}
                  </ButtonPermission>{' '}
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.pause').d('暂停'),
            });
          }
          if (record.jobStatus !== 'NONE') {
            operators.push({
              key: 'stop',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.jobInfo.view.message.confirm.stop')
                    .d('是否终止该任务')}？`}
                  onConfirm={this.handleEditJob.bind(this, record, 'stopJob')}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.stop`,
                        type: 'button',
                        meaning: '调度任务-终止',
                      },
                    ]}
                  >
                    {intl.get('hsdr.jobInfo.option.stop').d('终止')}{' '}
                  </ButtonPermission>{' '}
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hsdr.jobInfo.option.stop').d('终止'),
            });
          }
          if (record.jobStatus === 'PAUSED') {
            operators.push({
              key: 'pause',
              ele: (
                <Popconfirm
                  title={`${intl
                    .get('hsdr.notice.view.message.confirm.resume')
                    .d('是否恢复该任务')}？`}
                  onConfirm={this.handleEditJob.bind(this, record, 'resumeJobInfo')}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.resume`,
                        type: 'button',
                        meaning: '调度任务-恢复',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.resume').d('恢复')}{' '}
                  </ButtonPermission>{' '}
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.resume').d('恢复'),
            });
          }
          if (
            record.jobStatus !== 'PAUSED' &&
            record.jobStatus !== 'COMPLETE' &&
            record.jobStatus !== 'NONE'
          ) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '调度任务-编辑',
                    },
                  ]}
                  onClick={this.handleUpdate.bind(this, record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}{' '}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators, record, {
            limit: 3,
          });
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    const title = jobId
      ? intl.get('hsdr.jobInfo.view.message.edit').d('编辑调度任务')
      : intl.get('hsdr.jobInfo.view.message.create').d('新建调度任务');
    return (
      <>
        <Header title={intl.get('hsdr.jobInfo.view.message.title.list').d('调度任务')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '调度任务-新建',
              },
            ]}
            onClick={this.showCreateModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}{' '}
          </ButtonPermission>{' '}
        </Header>{' '}
        <Content>
          <div className="table-list-search">
            <FilterForm
              onSearch={this.handleSearch}
              glueTypeList={glueTypeList}
              jobStatusList={jobStatusList}
              onRef={this.handleBindRef}
              sourceFlagList={sourceFlagList}
            />{' '}
          </div>{' '}
          <div
            style={{
              margin: '24px 0 16px 0',
              textAlign: 'right',
            }}
          >
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.delete`,
                  type: 'button',
                  meaning: '调度任务-删除',
                },
              ]}
              loading={deleteLoading}
              disabled={selectedRowKeys.length === 0}
              onClick={this.handleDelete}
            >
              {intl.get('hzero.common.button.delete').d('删除')}{' '}
            </ButtonPermission>{' '}
          </div>{' '}
          <Table
            bordered
            rowKey="jobId"
            loading={fetchList}
            dataSource={jobInfoList}
            columns={columns}
            rowSelection={rowSelection}
            scroll={{
              x: tableScrollWidth(columns),
            }}
            pagination={pagination}
            onChange={this.handlePagination}
          />{' '}
          <Drawer
            title={title}
            loading={jobId ? requestLoading.save : requestLoading.create}
            detailLoading={detailLoading || copyLoading}
            checkLoading={checkLoading}
            configLoading={configLoading}
            modalVisible={modalVisible}
            onConfig={this.handleConfig}
            onCancel={() => this.handleModalVisible(false)}
            onOk={this.handleAdd}
            onCheck={this.handleCheck}
            initData={jobInfoDetail}
            jobInfo={jobInfo}
            executorConfigList={executorConfigList}
            groupsList={groupsList}
            fetchJobDetail={this.fetchJobDetail}
            tenantRoleLevel={tenantRoleLevel}
            tenantId={tenantId}
            onSearchGroup={this.queryGroups}
          />{' '}
          <LogDrawer {...logDrawer} /> <LogModal {...logDetailProps} />{' '}
          <LogErrorDrawer {...logErrorProps} />{' '}
        </Content>{' '}
      </>
    );
  }
}
