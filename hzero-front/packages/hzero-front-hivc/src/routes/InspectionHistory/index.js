/**
 * InspectionHistory 发票查验历史
 * @date: 2019-8-26
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React, { useRef, useEffect } from 'react';
import { connect } from 'dva';

import { Table, Button } from 'hzero-ui';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { dateTimeRender, dateRender, TagRender } from 'utils/renderer';
import { getCurrentOrganizationId, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';

function InspectionHistory(props) {
  const {
    history,
    queryListLoading = false,
    inspectionHistory: { pagination = {}, inspectionHistoryList = [] },
  } = props;
  const isSiteFlag = !isTenantRoleLevel();
  const filterFormRef = useRef(null);

  const handleSearch = (params) => {
    const { dispatch } = props;

    const fieldValues = filterFormRef.current.getFieldsValue();
    dispatch({
      type: 'inspectionHistory/queryList',
      payload: { ...fieldValues, ...params },
    });
  };

  useEffect(() => {
    handleSearch();
  }, []);

  const handleRedirect = (record = {}, flag) => {
    if (flag) {
      history.push(`/hivc/inspection-history/detail/${record.resultId}`);
    }
  };

  const handlePagination = (page) => {
    handleSearch({
      page,
    });
  };

  const columns = [
    isSiteFlag && {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.tenantName').d('租户'),
      width: 120,
      dataIndex: 'tenantName',
    },
    {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.invoiceCode').d('发票代码'),
      width: 130,
      dataIndex: 'invoiceCode',
    },
    {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.invoiceNo').d('发票号码'),
      width: 180,
      dataIndex: 'invoiceNo',
    },
    {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.invoiceDate').d('开票时间'),
      dataIndex: 'invoiceDate',
      width: 100,
      render: dateRender,
    },
    {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.checkCode').d('校验码'),
      width: 160,
      dataIndex: 'checkCode',
    },
    {
      title: intl
        .get('hivc.inspectionHistory.model.inspectionHistory.invoiceAmount')
        .d('不含税金额'),
      width: 120,
      align: 'right',
      dataIndex: 'invoiceAmount',
    },
    {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.creationDate').d('查验时间'),
      width: 150,
      dataIndex: 'creationDate',
      render: dateTimeRender,
    },
    {
      title: intl.get('hzero.common.status').d('状态'),
      width: 90,
      dataIndex: 'status',
      render: (val) => {
        const statusLists = [
          {
            status: 'S',
            color: 'green',
            text: intl.get('hivc.inspectionHistory.model.inspectionHistory.success').d('成功'),
          },
          {
            status: 'E',
            color: 'red',
            text: intl.get('hivc.inspectionHistory.model.inspectionHistory.failure').d('失败'),
          },
        ];
        return TagRender(val, statusLists);
      },
    },
    {
      title: intl.get('hivc.inspectionHistory.model.inspectionHistory.errorMessage').d('查验结果'),
      dataIndex: 'message',
      // render: (_, record)=>{
      //   return(
      //     <span>{record.errorMessage}</span>
      //   );
      // },
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      width: 90,
      fixed: 'right',
      render: (_, record) => (
        <span className="action-link">
          {record.resultId && (
            <a
              onClick={() => {
                handleRedirect(record, true);
              }}
            >
              {intl.get('hzero.common.button.checkResult').d('查看结果')}
            </a>
          )}
        </span>
      ),
    },
  ].filter(Boolean);

  return (
    <>
      <Header title={intl.get('hivc.inspectionHistory.view.message.title').d('发票查验历史')}>
        {!isSiteFlag && (
          <>
            <Button
              type="primary"
              icon="plus"
              onClick={() => {
                history.push(`/hivc/ocr-inspection/create`);
              }}
            >
              {intl.get('hivc.manualInspection.model.manualInspection.OCR').d('OCR发票查验')}
            </Button>
            <Button
              type="primary"
              icon="plus"
              onClick={() => {
                history.push(`/hivc/manual-inspection/create`);
              }}
            >
              {intl.get('hivc.manualInspection.model.manualInspection.manual').d('手工发票查验')}
            </Button>
          </>
        )}
      </Header>
      <Content>
        <FilterForm onSearch={handleSearch} ref={filterFormRef} isSiteFlag={isSiteFlag} />
        <Table
          bordered
          rowKey="id"
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={pagination}
          loading={queryListLoading}
          dataSource={inspectionHistoryList}
          onChange={handlePagination}
        />
      </Content>
    </>
  );
}

function mapStateToProps({ inspectionHistory, loading }) {
  return {
    inspectionHistory,
    tenantId: getCurrentOrganizationId(),
    queryListLoading: loading.effects['inspectionHistory/queryList'],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(formatterCollections({ code: ['hivc.inspectionHistory'] })(InspectionHistory));
