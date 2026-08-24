import React from 'react';
import { Table, Button } from 'hzero-ui';
import { connect } from 'dva';
import querystring from 'querystring';

import { Header, Content } from 'components/Page';

import { downloadFile } from 'hzero-front/lib/services/api';
import { HZERO_ADM } from 'hzero-front/lib/utils/config';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getSession } from 'utils/utils';

const TraceLog = (props = {}) => {
  const traceList = getSession('traceLog') || [];
  //  导出
  const handleExport = () => {
    const api = `${HZERO_ADM}/v1/trace/export`;
    downloadFile({
      requestUrl: api,
      queryParams: [
        {
          name: 'exportType',
          value: 'DATA',
        },
        {
          name: 'ids',
          value: '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22',
        },
        {
          name: 'traceGroupId',
          value: props.traceGroupId,
        },
      ],
    });
  };

  const columns = [
    {
      title: intl.get('hadm.traceLog.model.traceLog.logDate').d('日期'),
      dataIndex: 'logDate',
      width: 200,
    },
    {
      title: intl.get('hadm.traceLog.model.traceLog.appName').d('应用名'),
      dataIndex: 'appName',
      width: 200,
    },
    {
      title: intl.get('hadm.traceLog.model.traceLog.traceType').d('调用类型'),
      dataIndex: 'traceType',
      width: 140,
    },
    {
      title: intl.get('hadm.traceLog.model.traceLog.threadName').d('线程名'),
      dataIndex: 'threadName',
    },
    {
      title: intl.get('hadm.traceLog.model.traceLog.resultCode').d('响应码'),
      dataIndex: 'resultCode',
      width: 120,
    },
    {
      title: intl.get('hadm.traceLog.model.traceLog.traceId').d('调用ID'),
      dataIndex: 'traceId',
      width: 200,
    },
    {
      title: intl.get('hadm.traceLog.model.traceLog.sqlTimeConsuming').d('耗时'),
      dataIndex: 'cost',
      width: 200,
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      width: 110,
      fixed: 'right',
      render: (_, record) => (
        <span className="action-link">
          <a
            onClick={() => {
              props.history.push({
                pathname: `/hadm/trace-log/detail/${record.traceId}`,
                state: querystring.stringify({ ...record, _back: -1 }),
              });
            }}
          >
            {intl.get('hzero.common.button.detail').d('查看详情')}
          </a>
        </span>
      ),
    },
  ].filter(Boolean);

  return (
    <>
      <Header title={intl.get('hadm.traceLog.view.title').d('日志追溯分析')}>
        <Button type="primary" onClick={handleExport} disabled={!props.traceGroupId}>
          {intl.get('hzero.common.button.export').d('导出')}
        </Button>
      </Header>
      <Content>
        <Table
          bordered
          rowKey="uuid"
          dataSource={traceList.map((item) => {
            return { ...item, uuid: `${item.traceId}${item.spanId}` };
          })}
          columns={columns}
          pagination={false}
        />
      </Content>
    </>
  );
};

function mapStateToProps({ global }) {
  return {
    traceGroupId: global.traceGroupId,
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(formatterCollections({ code: ['hadm.traceLog'] })((props) => TraceLog(props)));
