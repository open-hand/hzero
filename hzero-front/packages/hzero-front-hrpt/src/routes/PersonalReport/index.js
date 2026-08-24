/**
 * 个人报表请求
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2020/1/3
 * @copyright HAND ® 2020
 */

import React from 'react';
import { DataSet, DatePicker, Form, Modal, Table, TextArea, TextField } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { BKT_RPT, HZERO_FILE } from 'utils/config';
import { dateTimeRender, operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, getDateTimeFormat, isTenantRoleLevel } from 'utils/utils';
import { downloadFile } from 'hzero-front/lib/services/api';

import { personalReportDetailDS, personalReportDS } from '@/stores/PersonalReportDS';

const modalKey = Modal.key();

const PersonalReportDetail = ({ requestId }) => {
  const ds = React.useMemo(() => new DataSet(personalReportDetailDS(requestId)), [requestId]);
  const dateTimeFormat = React.useMemo(() => getDateTimeFormat(), []);
  return (
    <>
      <Form dataSet={ds} columns={1}>
        <TextField name="requester" disabled />
        <TextArea name="requestParam" rows={5} disabled />
        <TextField name="reportName" disabled />
        <DatePicker name="startDate" format={dateTimeFormat} disabled />
        <DatePicker name="endDate" format={dateTimeFormat} disabled />
        <TextField name="requestStatusMeaning" disabled />
        <TextArea name="requestMessage" rows={5} disabled />
      </Form>
    </>
  );
};

const PersonalReport = ({ match }) => {
  const ds = React.useMemo(() => new DataSet(personalReportDS()), []);
  const handleExport = React.useCallback((record) => {
    const currentTenantId = getCurrentOrganizationId();
    const tenantRoleLevel = isTenantRoleLevel();
    const requestUrl = tenantRoleLevel
      ? `${HZERO_FILE}/v1/${currentTenantId}/files/download`
      : `${HZERO_FILE}/v1/files/download`;
    downloadFile({
      requestUrl,
      queryParams: [
        { name: 'bucketName', value: BKT_RPT },
        { name: 'bucketDirectory', value: 'hrpt02' },
        { name: 'url', value: record.fileUrl },
      ],
    });
  }, []);
  const handleDetail = React.useCallback(
    (record) => {
      Modal.open({
        title: intl.get('hzero.common.view.title.detail').d('详情'),
        drawer: true,
        key: modalKey,
        children: <PersonalReportDetail requestId={record.requestId} />,
        okText: intl.get('hrpt.common.report.export').d('导出结果'),
        cancelText: intl.get('hzero.common.button.close').d('关闭'),
        onOk: () => handleExport(record),
      });
    },
    [handleExport]
  );
  const columns = React.useMemo(
    () => [
      { name: 'reportCode' },
      { name: 'reportName', width: 200 },
      {
        name: 'requestStatusMeaning',
        width: 100,
        renderer({ text, record }) {
          const { requestStatus } = record.data;
          if (requestStatus === 'F') {
            // 已完成
            return (
              <Tag color="green" style={{ margin: 0 }}>
                {text}
              </Tag>
            );
          }
          if (requestStatus === 'E') {
            // 错误
            return (
              <Tag color="red" style={{ margin: 0 }}>
                {text}
              </Tag>
            );
          }
          if (requestStatus === 'W') {
            // 警告
            return (
              <Tag color="gold" style={{ margin: 0 }}>
                {text}
              </Tag>
            );
          }
          if (requestStatus === 'R') {
            // 运行中
            return (
              <Tag color="blue" style={{ margin: 0 }}>
                {text}
              </Tag>
            );
          }
          return <Tag style={{ margin: 0 }}>{text}</Tag>;
        },
      },
      {
        name: 'startDate',
        width: 160,
        renderer({ text }) {
          return dateTimeRender(text);
        },
      },
      {
        name: 'endDate',
        width: 160,
        renderer({ text }) {
          return dateTimeRender(text);
        },
      },
      {
        lock: 'right',
        width: 150,
        header: intl.get('hzero.common.button.action').d('操作'),
        renderer({ record }) {
          const { path } = match;
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}/detail`,
                    type: 'button',
                    meaning: '个人报表请求-详情',
                  },
                ]}
                onClick={() => handleDetail(record.toJSONData())}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          if (record.get('fileUrl')) {
            operators.push({
              key: 'export',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/export`,
                      type: 'button',
                      meaning: '个人报表请求-导出结果',
                    },
                  ]}
                  onClick={() => handleExport(record.toJSONData())}
                >
                  {intl.get('hrpt.common.report.export').d('导出结果')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hrpt.common.report.export').d('导出结果'),
            });
          }
          return operatorRender(operators);
        },
      },
    ],
    [handleDetail, handleExport]
  );
  return (
    <>
      <Header title={intl.get('hrpt.personalReport.view.title').d('个人报表请求')} />
      <Content>
        <Table dataSet={ds} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hrpt.personalReport', 'entity.tenant', 'hrpt.common'],
})(PersonalReport);
