/**
 * AuditQuery 操作审计查询
 * @date: 2019-7-18
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { mapKeys, isEmpty, isNil, isString } from 'lodash';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';
import ExcelExport from 'components/ExcelExport';

import intl from 'utils/intl';
import { HZERO_MNT } from 'utils/config';
import { TagRender, dateTimeRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { tableScrollWidth, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

let prevLogId = '';

@connect(({ auditQuery, loading }) => ({
  auditQuery,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  fetchAuditListLoading: loading.effects['auditQuery/fetchAuditList'],
  fetchAuditDetailLoading: loading.effects['auditQuery/fetchAuditDetail'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hmnt.auditQuery'],
})
export default class auditQuery extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      editflag: false,
      arrList: [],
    };
  }

  componentDidMount() {
    const { dispatch, location: { search = '' } = {} } = this.props;
    const { logId = '' } = queryString.parse(search.substring(1));
    const lovCodes = { statusList: 'HMNT.AUDIT_RESULT', methodList: 'HIAM.REQUEST_METHOD' };
    dispatch({
      type: 'auditQuery/init',
      payload: {
        lovCodes,
      },
    });
    this.handleSearch({ logId });
  }

  componentDidUpdate() {
    const { location: { search } = {} } = this.props;
    const { logId = '' } = queryString.parse(search.substring(1));
    if (logId !== prevLogId && logId !== undefined) {
      this.handleSearch({ logId });
    }
  }

  /**
   * @function handleSearch - 获取列表数据
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const { logId = '' } = params;
    const fieldValues = this.form.getFieldsValue();
    fieldValues.auditDateStart =
      fieldValues.auditDateStart && fieldValues.auditDateStart.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.auditDateEnd =
      fieldValues.auditDateEnd && fieldValues.auditDateEnd.format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'auditQuery/fetchAuditList',
      payload: { ...fieldValues, ...params },
    });
    prevLogId = logId;
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
  showModal(record = {}, editflag) {
    this.handleModalVisible(true);
    const { dispatch } = this.props;
    this.setState({ editflag });
    dispatch({
      type: 'auditQuery/fetchAuditDetail',
      payload: record,
    }).then((res) => {
      const arrList = [];
      if (res.logContent) {
        const logContent = JSON.parse(res.logContent);
        mapKeys(logContent, (value, key) => {
          if (!isEmpty(value)) {
            if (isString(value)) {
              arrList.push({ field: key, value });
            } else {
              arrList.push({ field: key, value: JSON.stringify(value) });
            }
          }
        });
        this.setState(editflag ? { arrList } : { arrList: logContent });
      }
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'auditQuery/updateState',
      payload: {
        auditDetail: [],
      },
    });
    this.setState({ editflag: false, arrList: [] });
    this.handleModalVisible(false);
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 跳转到 数据审计页面
   */
  @Bind()
  gotoDataAudit(auditBatchNumber) {
    const { history } = this.props;
    history.push({
      pathname: '/hmnt/data-audit/list',
      search: `?auditBatchNumber=${auditBatchNumber}`,
    });
  }

  @Bind()
  getSearchFormData() {
    const fieldValues = this.form.getFieldsValue();
    fieldValues.auditDateStart =
      fieldValues.auditDateStart && fieldValues.auditDateStart.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.auditDateEnd =
      fieldValues.auditDateEnd && fieldValues.auditDateEnd.format(DEFAULT_DATETIME_FORMAT);
    return fieldValues;
  }

  render() {
    const {
      fetchAuditListLoading = false,
      fetchAuditDetailLoading = false,
      isSiteFlag,
      tenantId,
      history,
      auditQuery: { pagination = {}, auditList = [], statusList = [], methodList = [] },
    } = this.props;
    const { modalVisible, editflag, arrList } = this.state;
    const columns = [
      isSiteFlag && {
        title: intl.get('hmnt.auditQuery.model.auditQuery.tenantName').d('租户'),
        width: 180,
        dataIndex: 'tenantName',
      },
      isSiteFlag && {
        title: intl.get('hmnt.auditQuery.model.auditQuery.serviceName').d('服务名称'),
        width: 180,
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.userId').d('用户'),
        dataIndex: 'realName',
        width: 150,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.auditContent').d('操作内容'),
        dataIndex: 'auditContent',
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.businessKey').d('业务主键'),
        dataIndex: 'businessKey',
        width: 200,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.auditDatetime').d('操作时间'),
        dataIndex: 'auditDatetime',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.timeConsuming').d('操作耗时'),
        dataIndex: 'timeConsuming',
        width: 100,
        render: (val) => `${val}ms`,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.auditResult').d('操作结果'),
        width: 90,
        dataIndex: 'auditResult',
        render: (val) => {
          const statusLists = [
            {
              status: 'SUCCESS',
              color: 'green',
              text: intl.get('hmnt.auditQuery.model.auditQuery.success').d('成功'),
            },
            {
              status: 'FAILED',
              color: 'red',
              text: intl.get('hmnt.auditQuery.model.auditQuery.failed').d('失败'),
            },
          ];
          return TagRender(val, statusLists);
        },
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.menuName').d('菜单名称'),
        dataIndex: 'menuName',
        width: 200,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.clientName').d('客户端'),
        dataIndex: 'clientName',
        width: 150,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.roleName').d('角色名称'),
        dataIndex: 'roleName',
        width: 150,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.requestIp').d('请求IP'),
        dataIndex: 'requestIp',
        width: 130,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.requestMethod').d('请求方式'),
        dataIndex: 'requestMethod',
        width: 90,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.requestUrl').d('请求路径'),
        dataIndex: 'requestUrl',
        width: 180,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.requestUserAgent').d('用户代理'),
        dataIndex: 'requestUserAgent',
        width: 180,
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.requestReferrer').d('Referrer'),
        dataIndex: 'requestReferrer',
        width: 180,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 220,
        fixed: 'right',
        render: (text, record) => {
          const arr = [];
          if (!isNil(record.auditOpLogLineList)) {
            for (let i = 0; i < record.auditOpLogLineList.length; i++) {
              arr.push({
                logType: record.auditOpLogLineList[i].logType,
                logLineId: record.auditOpLogLineList[i].logLineId,
              });
            }
          }
          const paramsLogLineId = arr.filter((item) => item.logType === 'PARAMETER');
          const resultLogLineId = arr.filter((item) => item.logType === 'RESULT');
          const operators = [];
          if (arr.some((item) => item.logType === 'PARAMETER')) {
            operators.push({
              key: 'params',
              ele: (
                <a
                  onClick={() => {
                    this.showModal(paramsLogLineId, true);
                  }}
                >
                  {intl.get('hmnt.auditQuery.view.message.modal.params').d('操作参数')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.auditQuery.view.message.modal.params').d('操作参数'),
            });
          }
          if (arr.some((item) => item.logType === 'RESULT')) {
            operators.push({
              key: 'response',
              ele: (
                <a
                  onClick={() => {
                    this.showModal(resultLogLineId, false);
                  }}
                >
                  {intl.get('hmnt.auditQuery.view.message.modal.response').d('操作响应')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.auditQuery.view.message.modal.response').d('操作响应'),
            });
          }
          if (record.auditBatchNumber) {
            operators.push({
              key: 'dataAudit',
              ele: (
                <a
                  onClick={() => {
                    this.gotoDataAudit(record.auditBatchNumber);
                  }}
                >
                  {intl.get('hmnt.auditQuery.view.button.dataAudit').d('数据审计')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.auditQuery.view.button.dataAudit').d('数据审计'),
            });
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);
    return (
      <>
        <Header title={intl.get('hmnt.auditQuery.view.message.title.list').d('操作审计查询')}>
          <ExcelExport
            requestUrl={`${HZERO_MNT}/v1/${
              isTenantRoleLevel()
                ? `${tenantId}/audit/operational/logs/export`
                : '/audit/operational/logs/export'
            }`}
            queryParams={this.getSearchFormData}
          />
        </Header>
        <Content>
          <FilterForm
            onSearch={this.handleSearch}
            isSiteFlag={isSiteFlag}
            tenantId={tenantId}
            statusList={statusList}
            methodList={methodList}
            onRef={this.handleBindRef}
            history={history}
          />
          <Table
            bordered
            loading={fetchAuditListLoading}
            dataSource={auditList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <Drawer
            title={
              editflag
                ? intl.get('hmnt.auditQuery.view.message.modal.params').d('操作参数')
                : intl.get('hmnt.auditQuery.view.message.modal.response').d('操作响应')
            }
            editflag={editflag}
            initLoading={fetchAuditDetailLoading}
            modalVisible={modalVisible}
            initData={arrList}
            onCancel={this.hideModal}
          />
        </Content>
      </>
    );
  }
}
