/**
 * 三员操作审计
 * @since 2020-07-14
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';
import { isNil } from 'lodash';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { dateTimeRender, operatorRender, TagRender } from 'utils/renderer';
import { isTenantRoleLevel } from 'utils/utils';

import Drawer from './ParamsDrawer';
import { tableDs as TableDs } from '../../stores/ThreeAuditQueryDS';

const ThreeAuditQuery = ({ history }) => {
  const tableDs = React.useMemo(() => new DataSet(TableDs()), []);

  const columns = React.useMemo(
    () => [
      !isTenantRoleLevel() && {
        width: 180,
        name: 'tenantName',
      },
      !isTenantRoleLevel() && {
        width: 180,
        name: 'serviceName',
      },
      {
        name: 'realName',
        width: 150,
      },
      {
        name: 'auditContent',
        width: 150,
      },
      {
        name: 'businessKey',
        width: 150,
      },
      {
        name: 'auditDatetime',
        width: 180,
        render: dateTimeRender,
      },
      {
        name: 'timeConsuming',
        renderer: ({ value }) => `${value}ms`,
      },
      {
        width: 90,
        name: 'auditResult',
        renderer: ({ value }) => {
          const statusLists = [
            {
              status: 'SUCCESS',
              color: 'green',
              text: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.success').d('成功'),
            },
            {
              status: 'FAILED',
              color: 'red',
              text: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.failed').d('失败'),
            },
          ];
          return TagRender(value, statusLists);
        },
      },
      {
        name: 'menuName',
        width: 200,
      },
      {
        name: 'clientName',
        width: 180,
      },
      {
        name: 'roleName',
        width: 180,
      },
      {
        name: 'requestIp',
        width: 130,
      },
      {
        name: 'requestMethod',
        width: 90,
      },
      {
        name: 'requestUrl',
        width: 300,
      },
      {
        name: 'requestUserAgent',
        width: 300,
      },
      {
        name: 'requestReferrer',
        width: 300,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 250,
        lock: 'right',
        renderer: ({ record }) => {
          const arr = [];
          if (!isNil(record.toData().auditOpLogLineList)) {
            for (
              let i = 0;
              i < (record.toData().auditOpLogLineList && record.toData().auditOpLogLineList.length);
              i++
            ) {
              arr.push({
                logType: record.get('auditOpLogLineList')[i].logType,
                logLineId: record.get('auditOpLogLineList')[i].logLineId,
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
                    showModal(paramsLogLineId, true);
                  }}
                >
                  {intl.get('hmnt.threeAuditQuery.view.message.modal.params').d('操作参数')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.threeAuditQuery.view.message.modal.params').d('操作参数'),
            });
          }
          if (arr.some((item) => item.logType === 'RESULT')) {
            operators.push({
              key: 'response',
              ele: (
                <a
                  onClick={() => {
                    showModal(resultLogLineId, false);
                  }}
                >
                  {intl.get('hmnt.threeAuditQuery.view.message.modal.response').d('操作响应')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.threeAuditQuery.view.message.modal.response').d('操作响应'),
            });
          }
          if (record.get('auditBatchNumber')) {
            operators.push({
              key: 'response',
              ele: (
                <a
                  onClick={() => {
                    gotoDataAudit(record.get('auditBatchNumber'));
                  }}
                >
                  {intl.get('hmnt.threeAuditQuery.view.button.dataAudit').d('数据审计')}
                </a>
              ),
              len: 4,
              title: intl.get('hmnt.threeAuditQuery.view.button.dataAudit').d('数据审计'),
            });
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ],
    []
  );

  const showModal = (record, editFlag) => {
    const currentEditData = record[0];
    const title = editFlag
      ? intl.get('hmnt.threeAuditQuery.view.message.modal.params').d('操作参数')
      : intl.get('hmnt.threeAuditQuery.view.message.modal.response').d('操作响应');
    Modal.open({
      drawer: true,
      key: 'threeAuditQuery',
      destroyOnClose: true,
      closable: true,
      title,
      style: {
        width: 700,
      },
      children: <Drawer currentEditData={currentEditData} editFlag={editFlag} />,
      footer: null,
    });
  };

  const gotoDataAudit = (auditBatchNumber) => {
    history.push({
      pathname: '/hmnt/three-data-audit-log',
      search: `?auditBatchNumber=${auditBatchNumber}`,
    });
  };

  return (
    <>
      <Header
        title={intl.get('hmnt.threeAuditQuery.view.message.title.auditQuery').d('操作审计日志')}
      />
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hmnt.threeAuditQuery'] })(ThreeAuditQuery);
