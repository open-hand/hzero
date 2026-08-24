/**
 * excelAsyncExport Excel异步导出
 * @date: 2019-8-7
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { TagRender, operatorRender, dateTimeRender } from 'utils/renderer';
import notification from 'utils/notification';
import { HZERO_FILE, BKT_PLATFORM } from 'utils/config';
import { tableScrollWidth, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import { downloadFile } from 'services/api';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

@connect(({ excelAsyncExport, loading }) => ({
  excelAsyncExport,
  isSiteFlag: !isTenantRoleLevel,
  fetchListLoading: loading.effects['excelAsyncExport/fetchList'],
  cancelLoading: loading.effects['excelAsyncExport/cancel'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.excelAsyncExport'],
})
export default class ExcelAsyncExport extends React.Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
    this.state = {
      modalVisible: false,
      errorInfo: '',
    };
  }

  componentDidMount() {
    this.handleSearch();
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HPFM.ASYNC.TASK.STATE' };
    dispatch({
      type: 'excelAsyncExport/init',
      payload: {
        lovCodes,
      },
    });
  }

  /**
   * @function handleSearch - 获取单点登陆配置列表数据
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldsValue = this.filterFormRef.current.getFieldsValue();
    dispatch({
      type: 'excelAsyncExport/fetchList',
      payload: { ...fieldsValue, ...params },
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination = {}) {
    this.handleSearch({
      page: pagination,
    });
  }

  /**
   * 删除配置
   */
  @Bind()
  handleCancel(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'excelAsyncExport/cancel',
      payload: { taskCode: decodeURIComponent(record.taskCode) },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleDownload(record) {
    const { isSiteFlag } = this.props;
    const organizationId = getCurrentOrganizationId();
    const api = `${HZERO_FILE}/v1/${isSiteFlag ? '' : `${organizationId}/`}files/download`;
    const queryParams = [{ name: 'url', value: encodeURIComponent(record.downloadUrl) }];
    queryParams.push({ name: 'bucketName', value: BKT_PLATFORM });
    queryParams.push({ name: 'directory', value: 'hpfm01' });
    downloadFile({
      requestUrl: api,
      queryParams,
    });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}) {
    this.setState({ errorInfo: record.errorInfo });
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  render() {
    const {
      fetchListLoading = false,
      cancelLoading = false,
      match,
      excelAsyncExport: { pagination = {}, excelAsyncExportList = [], typeList = [] },
    } = this.props;
    const { modalVisible, errorInfo } = this.state;
    const columns = [
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.taskCode').d('任务编号'),
        width: 240,
        dataIndex: 'taskCode',
      },
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.taskName').d('任务名称'),
        dataIndex: 'taskName',
      },
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.serviceName').d('所属服务'),
        dataIndex: 'serviceName',
        width: 200,
      },
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.state').d('任务状态'),
        width: 120,
        dataIndex: 'state',
        render: (val) => {
          const statusLists = [
            {
              status: 'DONE',
              color: 'green',
              text: intl.get('hzero.common.status.finished').d('已结束'),
            },
            {
              status: 'DOING',
              color: '',
              text: intl.get('hzero.common.status.processing').d('正在进行'),
            },
            {
              status: 'CANCELLED',
              color: 'red',
              text: intl.get('hzero.common.status.canceled').d('已取消'),
            },
          ];
          return TagRender(val, statusLists);
        },
      },
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.loginName').d('导出账户'),
        dataIndex: 'loginName',
        width: 100,
      },
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.creationDate').d('创建时间'),
        dataIndex: 'creationDate',
        width: 200,
        render: dateTimeRender,
      },
      {
        title: intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.errorInfo').d('异常信息'),
        dataIndex: 'errorInfo',
        render: (_, record) => {
          return (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${match.path}.button.errorInfo`,
                  type: 'button',
                  meaning: 'Excel异步导出-异常信息',
                },
              ]}
              onClick={() => {
                this.showModal(record);
              }}
            >
              {record.errorInfo}
            </ButtonPermission>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          if (record.state === 'DONE') {
            operators.push({
              key: 'download',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.download`,
                      type: 'button',
                      meaning: 'Excel异步导出-下载',
                    },
                  ]}
                  onClick={() => this.handleDownload(record)}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.download').d('下载'),
            });
          }
          if (record.state === 'DOING') {
            operators.push({
              key: 'cancel',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.cancel').d('是否取消导出？')}
                  onConfirm={() => this.handleCancel(record)}
                >
                  <a>{intl.get('hzero.common.button.cancel').d('取消')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
    return (
      <React.Fragment>
        <Header
          title={intl
            .get('hpfm.excelAsyncExport.view.message.title.excelAsyncExport')
            .d('异步导出监控')}
        />
        <Content>
          <FilterForm onSearch={this.handleSearch} ref={this.filterFormRef} typeList={typeList} />
          <Table
            bordered
            rowKey="taskId"
            loading={fetchListLoading || cancelLoading}
            dataSource={excelAsyncExportList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <Drawer
            title={intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.errorInfo').d('异常信息')}
            modalVisible={modalVisible}
            onCancel={this.hideModal}
            initData={errorInfo}
            tenantId={getCurrentOrganizationId()}
          />
        </Content>
      </React.Fragment>
    );
  }
}
