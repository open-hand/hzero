/**
 * 三员数据审计日志 - 列表页
 * @date: 2020-07-21
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo, useEffect, useState } from 'react';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import queryString from 'querystring';
import ExcelExport from 'components/ExcelExport';
import intl from 'utils/intl';
import { HZERO_MNT } from 'utils/config';
import { Content, Header } from 'components/Page';
import { operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';

import { formDS, tableDS, detailTableDS } from '../../stores/ThreeDataAuditLogDS';

const isTenant = isTenantRoleLevel();
const organizationId = getCurrentOrganizationId();
const ThreeDataAuditLog = ({ location: { search } }) => {
  const { auditBatchNumber = '' } = queryString.parse(search.substring(1));

  const formDs = useMemo(() => new DataSet(formDS()), []);
  const tableDs = useMemo(() => new DataSet({ ...tableDS(), queryDataSet: formDs }), []);
  const detailTableDs = useMemo(() => new DataSet(detailTableDS()), []);
  const [preAuditBatchNumber, setNumber] = useState(undefined);

  useEffect(() => {
    if (auditBatchNumber !== preAuditBatchNumber) {
      if (formDs.current) {
        formDs.current.set('auditBatchNumber', auditBatchNumber);
      }
      setNumber(auditBatchNumber);
      tableDs.query();
    }
  });

  const columns = useMemo(
    () =>
      [
        !isTenant && {
          width: 200,
          name: 'tenantName',
        },
        {
          name: 'serviceName',
          width: 200,
        },
        {
          name: 'entityCode',
          width: 400,
        },
        {
          name: 'tableName',
          width: 200,
        },
        {
          name: 'entityId',
          width: 150,
          tooltip: 'overflow',
        },
        {
          name: 'auditTypeMeaning',
          width: 150,
        },
        {
          name: 'menuName',
          width: 200,
        },
        {
          name: 'entityVersion',
          width: 150,
        },
        {
          name: 'processUserName',
          width: 150,
        },
        {
          name: 'processTime',
        },
        {
          name: 'remark',
          width: 300,
          tooltip: 'overflow',
        },
        {
          header: intl.get('hzero.common.button.action').d('操作'),
          width: 80,
          lock: 'right',
          renderer: ({ record }) => {
            const operators = [];
            operators.push({
              key: 'detail',
              ele: (
                <a
                  onClick={() => {
                    handleDetail(record);
                  }}
                >
                  {intl.get('hzero.common.button.detail').d('详情')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            });

            return operatorRender(operators);
          },
        },
      ].filter(Boolean),
    []
  );

  const detailColumns = useMemo(
    () => [
      {
        name: 'displayName',
        width: 200,
        renderer: ({ value, record }) => value || record.get('fieldCode'),
      },
      {
        name: 'columnName',
        width: 200,
      },
      {
        name: 'lang',
        width: 150,
      },
      {
        name: 'fieldValueOldMeaning',
        width: 150,
        renderer: ({ value, record }) => {
          if (value && record.get('fieldValueNewMeaning')) {
            return value;
          } else {
            return record.get('fieldValueOld');
          }
        },
      },
      {
        name: 'fieldValueNewMeaning',
        width: 150,
        renderer: ({ value, record }) => {
          if (value && record.get('fieldValueOldMeaning')) {
            return value;
          } else {
            return record.get('fieldValueNew');
          }
        },
      },
      {
        name: 'remark',
        minWidth: 300,
        tooltip: 'overflow',
      },
    ],
    []
  );

  // 查看详情
  const handleDetail = (record) => {
    const title = intl.get('hmnt.threeDataAudit.view.message.audit.detail').d('审计详情');
    const { auditDataLineList } = (record && record.toData()) || {};
    const data = Array.isArray(auditDataLineList) ? auditDataLineList : [];
    detailTableDs.loadData(data);
    Modal.open({
      key: 'editDrawer',
      destroyOnClose: true,
      closable: true,
      title,
      style: { width: 600 },
      children: <Table dataSet={detailTableDs} columns={detailColumns} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };
  const getSearchFormData = () => {
    const data = formDs.toData()[0] || {};
    return data;
  };

  return (
    <>
      <Header
        title={intl.get('hmnt.threeDataAudit.view.title.threeDataAuditLog').d('数据审计日志')}
      >
        <ExcelExport
          requestUrl={`${HZERO_MNT}/v1/${
            isTenantRoleLevel() ? `${organizationId}/tr/audit-data/export` : '/tr/audit-data/export'
          }`}
          queryParams={getSearchFormData}
        />
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hmnt.threeDataAudit'] })(ThreeDataAuditLog);
