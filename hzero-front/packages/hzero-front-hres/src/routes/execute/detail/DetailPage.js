/*
 * 执行记录 - 详情页
 * @date: 2019-10-24
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import {
  DataSet,
  Table,
  Form,
  TextField,
  Select,
  TextArea,
  DatePicker,
  Modal,
} from 'choerodon-ui/pro';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import { Header, Content } from 'components/Page';
import ExecuteDetailDS from '../stores/ExecuteDetailDS';
import ExecuteRecordDS from '../stores/ExecuteRecordDS';

const modalKey = Modal.key();
@connect()
@formatterCollections({ code: ['hres.record', 'hres.common'] })
export default class DetailPage extends Component {
  constructor(props) {
    super(props);
    this.executeDetailDS = new DataSet({
      ...ExecuteDetailDS(),
      queryParameter: {
        historyUuid: props.match.params.id,
        tenantId: getCurrentOrganizationId(),
      },
    });

    this.executeRecordDS = new DataSet({
      ...ExecuteRecordDS(),
    });
  }

  componentDidMount() {
    this.executeDetailDS.query();
  }

  get columns() {
    return [
      { name: 'lineId', width: 60 },
      {
        name: 'ruleName',
        style: { color: '#29bece' },
        renderer: ({ record, value }) => <a onClick={() => this.commands(record)}>{value}</a>,
      },
      { name: 'componentName' },
      { name: 'componentTypeDesc' },
      { name: 'status', width: 90 },
      { name: 'message', tooltip: 'overflow', width: 150 },
      { name: 'startDatetime', tooltip: 'overflow' },
      { name: 'endDatetime', tooltip: 'overflow' },
      { name: 'inParameter' },
      { name: 'outParameter' },
    ];
  }

  /**
   * 行内操作按钮组
   */
  @Bind()
  commands(record) {
    const { lineId = undefined, componentName = undefined } = record.toData();
    const { id = undefined } = this.props.match.params;
    this.executeRecordDS.setQueryParameter('lineId', lineId);
    this.executeRecordDS.setQueryParameter('componentName', componentName);
    this.executeRecordDS.setQueryParameter('historyUuid', id);
    this.executeRecordDS.setQueryParameter('tenantId', getCurrentOrganizationId());
    this.executeRecordDS.query();
    Modal.open({
      key: modalKey,
      drawer: true,
      style: {
        width: 1000,
      },
      title: intl.get('hres.record.view.title.component.execution.record').d('组件执行记录'),
      children: (
        <Form dataSet={this.executeRecordDS} disabled columns={4}>
          <TextField name="componentName" colSpan={1} />
          <Select name="status" colSpan={1} />
          <DatePicker name="startDatetime" colSpan={1} />
          <DatePicker name="endDatetime" colSpan={1} />
          <TextArea name="inParameter" colSpan={2} rows={10} />
          <TextArea name="outParameter" colSpan={2} rows={10} />
          <TextArea name="message" colSpan={4} />
        </Form>
      ),
      closable: true,
      okCancel: false,
      onOk: this.handleCloseModal,
      destroyOnClose: true,
    });
  }

  /**
   * 跳转查看明细页
   */
  @Bind()
  gotoDetail(record) {
    const { dispatch } = this.props;
    const { lineId = undefined, componentName = undefined } = record.toData();
    const { id = undefined } = this.props.match.params;
    const pathname = `/hres/execute/record/${id}/${componentName}/${lineId}`;
    dispatch(
      routerRedux.push({
        pathname,
      })
    );
  }

  render() {
    const { code, id } = this.props.match.params;
    return (
      <React.Fragment>
        <Header
          title={intl.get('hres.record.view.title.execution.process.details').d('执行流程详细信息')}
          backPath={code === 'code' ? `/hres/execute/list` : `/hres/execute/flow/${code}/${id}`}
        />
        <Content>
          <Table dataSet={this.executeDetailDS} columns={this.columns} queryFieldsLimit={2} />
        </Content>
      </React.Fragment>
    );
  }
}
