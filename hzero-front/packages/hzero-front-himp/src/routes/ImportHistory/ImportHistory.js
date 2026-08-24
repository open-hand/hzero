/**
 * @since 2019-10-10
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Button, Col, DataSet, Form, Row, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { TagRender } from 'utils/renderer';
import { HZERO_IMP } from 'utils/config';
import notification from 'utils/notification';
import { openTab } from 'utils/menuTab';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';

import queryString from 'querystring';

@formatterCollections({ code: ['himp.commentImport', 'himp.importHistory'] })
export default class ImportHistory extends React.Component {
  tableDS = new DataSet({
    autoQuery: false,
    dataKey: 'content',
    selection: false,
    queryFields: [
      {
        name: 'templateCode',
        type: 'object',
        label: intl.get('himp.importHistory.view.title.importPlatform').d('导入模板'),
        lovCode: 'HIMP.TEMPLATE',
        lovPara: { tenantId: getCurrentOrganizationId() },
        required: true,
        noCache: true,
      },
      {
        name: 'creationDateFrom',
        type: 'dateTime',
        label: intl.get('himp.importHistory.view.title.createIncidentFrom').d('创建时间从'),
      },
      {
        name: 'creationDateTo',
        type: 'dateTime',
        label: intl.get('himp.importHistory.view.title.createIncidentTo').d('创建时间至'),
      },
    ],
    fields: [
      {
        name: 'batch',
        type: 'string',
        label: intl.get('himp.importHistory.view.title.importBatch').d('导入批次号'),
      },
      {
        name: 'dataCount',
        type: 'string',
        label: intl.get('himp.importHistory.view.title.totalData').d('数据总量'),
      },
      {
        name: 'status',
        type: 'string',
        label: intl.get('himp.importHistory.view.title.status').d('状态'),
      },
      {
        name: 'createdUserName',
        type: 'string',
        label: intl.get('himp.importHistory.view.title.createUser').d('创建人'),
      },
      {
        name: 'creationDate',
        type: 'string',
        label: intl.get('himp.importHistory.view.title.createTime').d('创建时间'),
      },
    ],
    transport: {
      read: (config) => {
        const { creationDateFrom = '', creationDateTo = '' } = config.data;
        const { templateCode = {}, prefixPatch = '', templateType = '' } = config.data.templateCode
          ? config.data.templateCode
          : {};
        const data = filterNullValueObject({ templateCode, creationDateFrom, creationDateTo });
        const params = { ...config.params };
        const url =
          templateType === 'C'
            ? `${prefixPatch}/v1/${getCurrentOrganizationId()}/import/manager`
            : `${HZERO_IMP}/v1/${getCurrentOrganizationId()}/import/manager`;
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
    },
  });

  constructor(props) {
    super(props);
    this.state = {
      id: null,
      flag: true,
      prefixPatch: '',
    };
  }

  componentDidMount() {
    this.tableDS.queryDataSet.addEventListener('update', this.templateCodeQuery);
  }

  componentWillUnmount() {
    this.tableDS.queryDataSet.removeEventListener('update', this.templateCodeQuery);
  }

  @Bind()
  templateCodeQuery() {
    const { flag } = this.state;
    if (
      this.tableDS.queryDataSet.current &&
      this.tableDS.queryDataSet.current.get('templateCode')
    ) {
      const { id, prefixPatch } = this.tableDS.queryDataSet.current.get('templateCode');
      this.setState({ prefixPatch });
      if (id !== this.state.id || flag) {
        this.tableDS.query();
        this.setState({ id, flag: false });
      }
    }
  }

  /**
   * 历史记录详情页
   * @param {*} record
   */
  @Bind()
  openDetailPage(record) {
    // 跳转到当前行详情页
    const {
      data: { importId = '', templateCode = '', batch = '' },
    } = record;
    const { prefixPatch = '' } = this.state;
    openTab({
      key: `/himp/history/detail/${importId}/${templateCode}/${batch}`,
      title: 'hzero.common.title.historyDetail',
      closable: true,
      search: queryString.stringify({
        action: intl.get('hzero.common.title.historyDetail').d('历史详情'),
        prefixPatch,
      }),
    });
  }

  @Bind()
  clearContent() {
    this.tableDS.queryDataSet.current.set({ creationDateFrom: null, creationDateTo: null });
  }

  get columns() {
    return [
      { name: 'batch' },
      { name: 'dataCount' },
      {
        name: 'status',
        renderer: (records) => {
          const statusList = [
            { status: 'UPLOADING', color: 'blue' /* , text: 'Excel导入' */ },
            { status: 'UPLOADED', color: 'green' /* , text: '验证成功' */ },
            { status: 'CHECKING', color: 'blue' /* , text: '验证失败' */ },
            { status: 'CHECKED', color: 'green' /* , text: '导入成功' */ },
            { status: 'IMPORTING', color: 'blue' /* , text: '导入失败' */ },
            { status: 'IMPORTED', color: 'green' /* , text: '数据异常' */ },
          ];
          const tagItem = statusList.find((t) => t.status === records.value) || {};
          return TagRender(records.value, [
            {
              status: records.value,
              text: records.record.data.statusMeaning,
              color: tagItem.color,
            },
          ]);
        },
      },
      { name: 'createdUserName' },
      { name: 'creationDate' },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 200,
        renderer: this.commands,
        lock: 'right',
        align: 'center',
      },
    ];
  }

  @Bind
  commands({ record }) {
    return (
      <div>
        <Row>
          <Col>
            <span className="action-link">
              <a onClick={() => this.openDetailPage(record)}>
                {intl.get('hzero.common.button.detail').d('详情')}
              </a>
            </span>
          </Col>
        </Row>
      </div>
    );
  }

  renderBar = ({ queryFields, buttons, queryFieldsLimit, dataSet, queryDataSet }) => {
    if (queryDataSet) {
      return (
        <Form columns={queryFieldsLimit} dataSet={queryDataSet}>
          {queryFields}
          <div>
            <Button onClick={() => this.clearContent()}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button
              dataSet={null}
              color="primary"
              onClick={() => {
                queryDataSet.validate().then((val) => {
                  if (val && queryDataSet.current && queryDataSet.current.get('templateCode')) {
                    dataSet.query();
                  } else {
                    notification.error({
                      message: intl
                        .get('himp.modelType.model.modelType.notificationErrMsg')
                        .d('存在必输字段未填写！'),
                    });
                  }
                });
              }}
            >
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
            {buttons}
          </div>
        </Form>
      );
    }
  };

  render() {
    return (
      <>
        <Header title={intl.get('himp.commentImport.view.message.title').d('导入历史')} />
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={this.columns}
            queryFieldsLimit={3}
            // queryBar={this.renderBar}
            queryFields={{ templateCode: { clearButton: false } }}
            autoHeight={{ type: 'maxHeight' }}
          />
        </Content>
      </>
    );
  }
}
