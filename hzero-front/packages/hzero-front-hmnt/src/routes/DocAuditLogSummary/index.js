/**
 * 单据审计日志汇总 - 列表页
 * @date: 2020-08-26
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';

import { isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import { tableDS, formDS } from '../../stores/DocAuditLogSummaryDS';

const isTenant = isTenantRoleLevel();
const DocAuditLogSummary = (props) => {
  const formDs = useMemo(
    () =>
      new DataSet({
        ...formDS(),
        events: {
          update: ({ record, name, value, oldValue }) => {
            if (name === 'tenantLov') {
              const newValue = value || {};
              const preValue = oldValue || {};
              if (newValue.tenantId !== preValue.tenantId) {
                record.set('documentLov', {});
              }
            }
          },
        },
      }),
    []
  );
  const tableDs = useMemo(() => new DataSet({ ...tableDS(), queryDataSet: formDs }), []);

  const columns = useMemo(
    () => [
      !isTenant && { name: 'tenantName', width: 220 },
      {
        name: 'serviceName',
        width: 200,
      },
      {
        name: 'loginName',
      },

      {
        name: 'businessKey',
        width: 200,
        tooltip: 'overflow',
      },
      {
        name: 'auditDatetime',
        width: 200,
      },
      {
        name: 'auditResultMeaning',
      },
      {
        name: 'tableName',
        width: 200,
      },
      {
        name: 'auditTypeMeaning',
      },
      {
        name: 'entityId',
        width: 200,
        tooltip: 'overflow',
      },
      {
        name: 'fieldCode',
        width: 200,
      },
      {
        name: 'lang',
      },
      {
        name: 'fieldValueOld',
        width: 200,
      },
      {
        name: 'fieldValueNew',
        width: 200,
      },
      {
        name: 'auditDocumentId',
        width: 200,
        tooltip: 'overflow',
      },
      {
        name: 'auditContent',
        width: 200,
        tooltip: 'overflow',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const operators = [];
          operators.push(
            {
              key: 'operateDetail',
              ele: (
                <a
                  onClick={() => {
                    handleToPage(record, 'operate');
                  }}
                >
                  {intl.get('hmnt.docLogSummary.view.button.operateDetail').d('操作详情')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.docLogSummary.view.button.operateDetail').d('操作详情'),
            },
            {
              key: 'dataDetail',
              ele: (
                <a
                  onClick={() => {
                    handleToPage(record, 'data');
                  }}
                >
                  {intl.get('hmnt.docLogSummary.view.button.dataDetail').d('数据详情')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.docLogSummary.view.button.dataDetail').d('数据详情'),
            }
          );

          return operatorRender(operators);
        },
      },
    ],
    []
  );

  // 新建或编辑
  const handleToPage = (record, type) => {
    const data = (record && record.toData()) || {};
    const { logId, auditDataId } = data;
    const { history } = props;
    history.push({
      pathname: type === 'operate' ? '/hmnt/audit-query' : '/hmnt/data-audit/list',
      search: type === 'operate' ? `logId=${logId}` : `auditDataId=${auditDataId}`,
    });
  };

  return (
    <>
      <Header title={intl.get('hmnt.docLogSummary.view.title.docLogCollect').d('单据审计日志')} />
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hmnt.docLogSummary'] })(DocAuditLogSummary);
