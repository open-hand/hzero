/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/1/14
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Table, DataSet, Form, Tabs, Currency } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import querystring from 'querystring';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  resultFormDS,
  ruleTableDS,
  discountTableDS,
} from '@/stores/PaymentCalculation/PaymentCalculationDS';

@formatterCollections({ code: ['hchg.paymentCalculation'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.ruleTableDS = new DataSet(ruleTableDS);
    this.discountTableDS = new DataSet(discountTableDS);
    this.resultFormDS = new DataSet({
      ...resultFormDS,
      children: {
        chargeDetails: this.ruleTableDS,
        discountDetails: this.discountTableDS,
      },
    });
  }

  async componentDidMount() {
    const {
      location: { search },
      match: { params },
    } = this.props;
    const queryParams = querystring.parse(search.substring(1));
    const { ruleNum, accountNum } = params;
    const { startDate, endDate, quantity } = queryParams;
    this.resultFormDS.setQueryParameter('ruleNum', ruleNum);
    this.resultFormDS.setQueryParameter('accountNum', accountNum);
    this.resultFormDS.setQueryParameter('startDate', startDate);
    this.resultFormDS.setQueryParameter('endDate', endDate);
    this.resultFormDS.setQueryParameter('quantity', quantity);
    await this.resultFormDS.query();
  }

  /**
   * 规则详细columns
   */
  // eslint-disable-next-line class-methods-use-this
  get ruleColumns() {
    return [
      {
        name: 'order',
        width: 70,
        align: 'center',
      },
      {
        name: 'greaterThan',
        width: 120,
      },
      {
        name: 'lessAndEquals',
        width: 120,
      },
      {
        name: 'type',
        width: 100,
      },
      {
        name: 'value',
        width: 120,
      },
      {
        name: 'chargeQuantity',
        width: 100,
      },
      {
        name: 'beforeResultAmount',
        width: 130,
      },
      {
        name: 'chargeAmount',
        width: 100,
      },
    ];
  }

  /**
   * 优惠详情columns
   */
  // eslint-disable-next-line class-methods-use-this
  get discountColumns() {
    return [
      {
        name: 'order',
        width: 70,
        align: 'center',
      },
      {
        name: 'type',
      },
      {
        name: 'discountValue',
      },
      {
        name: 'beforeResultAmount',
      },
      {
        name: 'discountAmount',
      },
    ];
  }

  render() {
    return (
      <>
        <Header
          title={intl
            .get('hchg.paymentCalculation.view.title.paymentCalculationDetail')
            .d('费用计算结果')}
          backPath="/hchg/payment-calculation"
        />
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hchg.paymentCalculation.view.title.basicInformation').d('基本信息')}
              </h3>
            }
          >
            <Form disabled labelLayout="horizontal" dataSet={this.resultFormDS} columns={3}>
              <Currency name="resultTotalAmount" />
              <Currency name="discountAmount" />
              <Currency name="resultAmount" />
              <Currency name="totalAmount" />
              <Currency name="actualDiscountAmount" />
              <Currency name="actualResultAmount" />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hchg.paymentCalculation.view.title.detailInformation').d('详情信息')}
              </h3>
            }
          >
            <Tabs animated={false}>
              <Tabs.TabPane
                tab={intl.get('hchg.paymentCalculation.view.title.rechargeDetail').d('计费详情')}
                key="ruleDetail"
              >
                <Table dataSet={this.ruleTableDS} columns={this.ruleColumns} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hchg.paymentCalculation.view.title.discountDetail').d('优惠详情')}
                key="discountDetail"
              >
                <Table dataSet={this.discountTableDS} columns={this.discountColumns} />
              </Tabs.TabPane>
            </Tabs>
          </Card>
        </Content>
      </>
    );
  }
}
