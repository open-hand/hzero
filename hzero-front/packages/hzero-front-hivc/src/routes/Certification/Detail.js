/* eslint-disable no-nested-ternary */
import React from 'react';

import { DataSet, Form, Output, Table } from 'choerodon-ui/pro';
import { Card, Tag } from 'choerodon-ui';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailDS as detailDs, detailTableDS as detailTableDs } from '../../stores/CertificationDS';

const Detail = (props) => {
  const detailDS = React.useMemo(() => new DataSet(detailDs()), []);

  const detailTableDS = React.useMemo(() => new DataSet(detailTableDs()), []);

  React.useEffect(() => {
    const {
      match: {
        params: { id },
      },
    } = props;
    detailDS.setQueryParameter('id', id);
    detailDS.query();
    detailTableDS.setQueryParameter('id', id);
    detailTableDS.query();
  });

  const columns = React.useMemo(
    () =>
      [
        {
          name: 'typeMeaning',
        },
        {
          name: 'deductionInvoiceNum',
          width: 200,
        },
        {
          name: 'deductionTotalAmount',
          width: 180,
          align: 'right',
        },
        {
          name: 'deductionTotalValidTax',
          width: 180,
          align: 'right',
        },
        {
          name: 'nonDeductionInvoiceNum',
          width: 200,
        },
        {
          name: 'nonDeductionTotalAmount',
          width: 180,
          align: 'right',
        },
        {
          name: 'nonDeductionTotalValidTax',
          width: 180,
          align: 'right',
        },
      ].filter(Boolean),
    []
  );

  return (
    <>
      <Header
        title={intl.get('hivc.certification.view.message.title.detail').d('发票认证详情')}
        backPath="/hivc/certification"
      >
        {/*  */}
      </Header>
      <Content>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hivc.certification.view.message.title.info').d('认证结果')}</h3>}
        >
          <Form dataSet={detailDS} columns={3}>
            <Output name="buyerNo" />
            <Output name="batchNo" />
            <Output
              name="authState"
              renderer={({ value }) => {
                return (
                  <Tag color={value ? 'green' : 'red'}>
                    {value
                      ? intl.get('hivc.certification.status.confirm').d('已确认签名')
                      : intl.get('hivc.certification.status.unConfirm').d('未确认签名')}
                  </Tag>
                );
              }}
            />
            <Output name="authPeriod" />
            <Output name="statisticsCompleteTime" />
            <Output name="confirmCompleteTime" />
            <Output
              name="requestStatus"
              renderer={({ value }) => {
                return (
                  <Tag color={value === -1 ? 'red' : value === 0 ? 'blue' : 'green'}>
                    {value === -1
                      ? intl.get('hzero.common.status.error').d('失败')
                      : value === 0
                      ? intl.get('hzero.common.status.pending').d('请求中')
                      : intl.get('hzero.common.status.success').d('成功')}
                  </Tag>
                );
              }}
            />
            <Output name="resultMessage" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={
            <h3>
              {intl.get('hivc.certification.view.message.title.certificationDetail').d('统计结果')}
            </h3>
          }
        >
          <Table dataSet={detailTableDS} columns={columns} queryBar="none" />
        </Card>
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hivc.certification'],
})(Detail);
