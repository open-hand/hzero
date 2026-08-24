/**
 * editLog 二级域名单点登录配置
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { dateTimeRender } from 'utils/renderer';

import FilterForm from './FilterForm';

@connect(({ editLog, loading }) => ({
  editLog,
  isSiteFlag: !isTenantRoleLevel(),
  queryLogListLoading: loading.effects['editLog/queryLogList'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hfile.editLog'],
})
export default class EditLog extends React.PureComponent {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
  }

  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HFLE.FILE.EDIT_TYPE' };
    dispatch({
      type: 'editLog/init',
      payload: {
        lovCodes,
      },
    });
    this.handleSearch();
  }

  /**
   * @function handleSearch - 获取单点登陆配置列表数据
   * @param {object} params - 查询参数
   */
  @Bind
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldValues = this.filterFormRef.current.getFieldsValue();
    fieldValues.changeDateFrom =
      fieldValues.changeDateFrom && fieldValues.changeDateFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.changeDateTo =
      fieldValues.changeDateTo && fieldValues.changeDateTo.format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'editLog/queryLogList',
      payload: { ...fieldValues, ...params },
    });
  }

  /**
   * @function handleResetSearch - 重置查询表单
   * @param {object} params - 查询参数
   */
  @Bind()
  handleResetSearch() {
    const { form } = this.props;
    form.resetFields();
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

  render() {
    const {
      isSiteFlag,
      queryLogListLoading = false,
      editLog: { pagination = {}, editLogList = [], typeList = [] },
    } = this.props;
    const columns = [
      isSiteFlag && {
        title: intl.get('hfile.editLog.model.editLog.tenantName').d('租户'),
        width: 120,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hfile.editLog.model.editLog.fileName').d('文件名'),
        dataIndex: 'fileName',
      },
      {
        title: intl.get('hfile.editLog.model.editLog.realName').d('提交人'),
        dataIndex: 'realName',
        width: 180,
      },
      {
        title: intl.get('hfile.editLog.model.editLog.editType').d('编辑类型'),
        width: 140,
        dataIndex: 'editType',
      },
      {
        title: intl.get('hfile.editLog.model.editLog.changeDate').d('提交时间'),
        width: 210,
        dataIndex: 'changeDate',
        render: dateTimeRender,
      },
    ].filter(Boolean);
    return (
      <>
        <Header title={intl.get('hfile.editLog.view.message.title').d('文件编辑日志')} />
        <Content>
          <FilterForm
            onSearch={this.handleSearch}
            typeList={typeList}
            isSiteFlag={isSiteFlag}
            ref={this.filterFormRef}
          />
          <Table
            bordered
            rowKey="logId"
            loading={queryLogListLoading}
            dataSource={editLogList}
            columns={columns}
            pagination={pagination}
            onChange={this.handlePagination}
          />
        </Content>
      </>
    );
  }
}
