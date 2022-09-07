/* eslint-disable no-nested-ternary */
import React from 'react';

import { DataSet, Form, Output, Table } from 'choerodon-ui/pro';
import { Card, Tag } from 'choerodon-ui';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailDS as detailDs, detailTableDS as detailTableDs } from '../../stores/SelectDS';

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
          name: 'invoiceCode',
        },
        {
          name: 'invoiceNo',
          width: 200,
        },
        {
          name: 'effectiveTax',
          width: 180,
          align: 'right',
        },
        {
          name: 'invoiceDate',
          width: 180,
        },
        {
          name: 'tickFlag',
          width: 150,
          align: 'center',
          renderer: ({ value }) => {
            return (
              <Tag color={value ? 'green' : 'red'}>
                {value
                  ? intl.get('hivc.select.status.tick').d('勾选')
                  : intl.get('hivc.select.status.unTick').d('取消勾选')}
              </Tag>
            );
          },
        },
        {
          name: 'requestStatus',
          width: 150,
          align: 'center',
          renderer: ({ value }) => {
            return (
              <Tag color={value === -1 ? 'red' : value === 0 ? 'blue' : 'green'}>
                {value === -1
                  ? intl.get('hzero.common.status.error').d('失败')
                  : value === 0
                  ? intl.get('hzero.common.status.pending').d('请求中')
                  : intl.get('hzero.common.status.success').d('成功')}
              </Tag>
            );
          },
        },
        {
          name: 'requestMessage',
          width: 200,
        },
      ].filter(Boolean),
    []
  );

  return (
    <>
      <Header
        title={intl.get('hivc.select.view.message.title.detail').d('发票勾选详情')}
        backPath="/hivc/select"
      >
        {/*  */}
      </Header>
      <Content>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hivc.select.view.message.title.info').d('勾选结果')}</h3>}
        >
          <Form dataSet={detailDS} columns={3}>
            <Output name="buyerNo" />
            <Output name="batchNo" />
            <Output name="tickDate" />
            <Output name="tickCount" />
            <Output
              name="allSuccess"
              renderer={({ value }) => {
                return (
                  <Tag color={value ? 'green' : 'red'}>
                    {value
                      ? intl.get('hzero.common.status.success').d('成功')
                      : intl.get('hzero.common.status.error').d('失败')}
                  </Tag>
                );
              }}
            />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={
            <h3>{intl.get('hivc.select.view.message.title.selectDetail').d('勾选结果详情')}</h3>
          }
        >
          <Table dataSet={detailTableDS} columns={columns} queryBar="none" />
        </Card>
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hivc.select'],
})(Detail);
