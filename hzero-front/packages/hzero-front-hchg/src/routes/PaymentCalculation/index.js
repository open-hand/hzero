/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/24
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { DataSet, Form, TextField, Select, DatePicker, Lov, NumberField } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { dateRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import querystring from 'querystring';
import { Bind } from 'lodash-decorators';
import { calculateFormDS } from '@/stores/PaymentCalculation/PaymentCalculationDS';

@formatterCollections({ code: ['hchg.paymentCalculation'] })
export default class PaymentCalculation extends React.Component {
  constructor(props) {
    super(props);
    this.calculateFormDS = new DataSet(calculateFormDS);
  }

  /**
   * 跳转到计算结果
   */
  @Bind()
  async handleGotoCalculateResult() {
    const { history } = this.props;
    const { current } = this.calculateFormDS;
    const validate = await this.calculateFormDS.validate();
    if (validate) {
      const params = {
        startDate: dateRender(current.get('startDate')),
        endDate: dateRender(current.get('endDate')),
        quantity: current.get('quantity'),
      };
      history.push({
        pathname: `/hchg/payment-calculation/result/${current.get('ruleNum')}/${current.get(
          'accountNum'
        )}`,
        search: querystring.stringify(params),
      });
    }
  }

  render() {
    const { match } = this.props;
    const { path } = match;
    return (
      <>
        <Header title={intl.get('hchg.paymentCalculation.view.message.title').d('计费引擎计算')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.calculate`,
                type: 'button',
                meaning: '费用计算-费用生成',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.handleGotoCalculateResult}
          >
            {intl.get('hchg.paymentCalculation.view.button.calculate').d('费用生成')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hchg.paymentCalculation.view.title.ruleInfo').d('规则信息')}</h3>}
          >
            <Form labelLayout="horizontal" dataSet={this.calculateFormDS} columns={3}>
              <Lov name="ruleLov" />
              <TextField name="ruleNum" disabled />
              <Select name="methodCode" disabled />
              <Select name="paymentModelCode" disabled />
              <Select name="typeCode" disabled />
              <Select name="unitCode" disabled />
              <Select name="statusCode" disabled />
              <TextField name="_startDate" disabled />
              <TextField name="_endDate" disabled />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hchg.paymentCalculation.view.title.accountInfo').d('账号信息')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.calculateFormDS} columns={3}>
              <Lov name="accountLov" />
              <TextField name="accountNum" disabled />
              <Select name="accountType" disabled />
              <Select name="enabledFlag" disabled />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hchg.paymentCalculation.view.title.calculateParam').d('计算参数')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.calculateFormDS} columns={3}>
              <DatePicker name="startDate" />
              <DatePicker name="endDate" />
              <NumberField name="quantity" />
              {/* <TextField name="calculateEngine" /> */}
            </Form>
          </Card>
        </Content>
      </>
    );
  }
}
