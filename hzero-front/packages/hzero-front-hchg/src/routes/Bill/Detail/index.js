/**
 * Bill - 账单
 * @date: 2020-2-14
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import {
  DataSet,
  Table,
  Form,
  TextField,
  NumberField,
  Select,
  DatePicker,
  DateTimePicker,
  Lov,
  TextArea,
  Modal,
} from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import { Card } from 'choerodon-ui';
import React, { Component } from 'react';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { Bind } from 'lodash-decorators';
import { BILL_STATUS_FIELDS, PAYMENT_STATUS_FIELDS } from '@/constants/constants';
import { axios } from '../../../components';

import BillHeaderDS from '../../../stores/bill/BillHeaderDS';
import BillLineDS from '../../../stores/bill/BillLineDS';

const organizationId = getCurrentOrganizationId();

/**
 * 账单详单
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hchg.bill'] })
export default class BillLine extends Component {
  tableDS = new DataSet({
    ...BillLineDS(),
    autoQuery: false,
  });

  formDS = new DataSet({
    ...BillHeaderDS(),
    autoQuery: false,
    children: {
      billLineList: this.tableDS,
    },
  });

  constructor(props) {
    super(props);

    this.state = {
      statusCode: BILL_STATUS_FIELDS.UNBILLED,
    };
  }

  async componentDidMount() {
    this.formDS.queryParameter.billNum = this.props.match.params.billNum;
    this.tableDS.queryParameter.billNum = this.props.match.params.billNum;
    await this.formDS.query();

    if (this.formDS.current) {
      this.setState({
        statusCode: this.formDS.current.get('statusCode'),
      });
    }
  }

  /**
   * 账单回调
   */
  @Bind()
  callback() {
    Modal.confirm({
      title: intl.get(`hchg.bill.meaasge.confirm.callback`).d('确定回调？'),
      onOk: async () => {
        const headerIds = [this.formDS.current.get('headerId')];

        const url = isTenantRoleLevel()
          ? `${HZERO_CHG}/v1/${organizationId}/bill-headers/callback`
          : `${HZERO_CHG}/v1/bill-headers/callback`;
        try {
          const res = await axios.put(url, headerIds);
          if (res.failed) {
            notification.error({
              message: res.message,
            });
          } else {
            notification.success({
              message: intl.get(`hchg.bill.meaasge.pay.callback`).d('回调成功'),
            });
          }
          await this.formDS.query();
          this.setState({
            statusCode: this.formDS.current.get('statusCode'),
          });
        } catch (err) {
          notification.error({
            message: intl.get(`hchg.bill.meaasge.pay.callbackWait`).d('回调失败，请稍后再试。'),
          });
        }
      },
    });
  }

  /**
   * 支付
   */
  @Bind()
  pay() {
    Modal.confirm({
      title: intl.get(`hchg.bill.meaasge.confirm.pay`).d('确定支付？'),
      onOk: async () => {
        const headerIds = [this.formDS.current.get('headerId')];

        const url = isTenantRoleLevel()
          ? `${HZERO_CHG}/v1/${organizationId}/bill-headers/pay`
          : `${HZERO_CHG}/v1/bill-headers/pay`;
        try {
          const res = await axios.put(url, headerIds);
          if (res.failed) {
            // intl.get(`hchg.bill.meaasge.pay.failed`).d('支付失败')
            notification.error({
              message: res.message,
            });
          } else {
            notification.success({
              message: intl.get(`hchg.bill.meaasge.pay.success`).d('支付成功'),
            });
          }
          await this.formDS.query();
          this.setState({
            statusCode: this.formDS.current.get('statusCode'),
          });
        } catch (err) {
          notification.error({
            message: intl.get(`hchg.bill.meaasge.pay.wait`).d('支付失败，请稍后再试。'),
          });
        }
      },
    });
  }

  render() {
    const { statusCode } = this.state;
    const {
      match: { path },
    } = this.props;
    return (
      <div>
        <Header
          title={intl.get('hchg.bill.view.title.billDetail').d('账单详情')}
          backPath="/hchg/bill/list"
        >
          {/* 支付状态=已结账，显示回调按钮 */}
          {statusCode === BILL_STATUS_FIELDS.SETTLED && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.callback`,
                  type: 'button',
                  meaning: '账单详情-账单回调',
                },
              ]}
              icon="sync"
              type="default"
              onClick={this.callback}
            >
              {intl.get('hchg.bill.view.button.callback').d('账单回调')}
            </ButtonPermission>
          )}
          {/* 支付状态=已出帐，显示支付按钮 */}
          {statusCode === BILL_STATUS_FIELDS.BILLED && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.pay`,
                  type: 'button',
                  meaning: '账单详情-支付',
                },
              ]}
              icon="check-circle" // payment attach_money
              type="default"
              onClick={this.pay}
            >
              {intl.get('hchg.bill.view.button.pay').d('支付')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hchg.bill.view.title.basicInformation').d('账单基本信息')}</h3>}
          >
            <Form dataSet={this.formDS} columns={3} disabled>
              <TextField name="billNum" />
              <TextField name="billName" />
              <DatePicker name="billDate" />
              <Select name="statusCode" />
              <NumberField name="amount" />
              <Lov name="currencyObject" />
              <TextArea name="remark" colSpan={3} />
              <TextField name="ruleCode" />
              <TextField name="ruleName" />
              <NumberField name="discountTotal" />
              <Select name="paymentStatus" />
              {this.formDS.current &&
              this.formDS.current.get('statusCode') === BILL_STATUS_FIELDS.SETTLED ? (
                <NumberField name="actualPaymentAmount" />
              ) : (
                <NumberField name="paymentAmount" />
              )}
              <DateTimePicker name="actualPaymentTime" />
              {this.formDS.current &&
              this.formDS.current.get('paymentStatus') === PAYMENT_STATUS_FIELDS.FAILED ? (
                <TextArea name="processMessage" colSpan={3} />
              ) : null}
              <TextField name="sourceSystemName" />
              <TextField name="sourceSystemNum" />
              <TextField name="sourceBillNum" />
              <Select name="billEntityType" />
              <TextField name="billEntityName" />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hchg.bill.view.title.billDetail.line').d('账单明细')}</h3>}
          >
            <Table
              dataSet={this.tableDS}
              columns={[
                {
                  header: intl.get('hchg.bill.model.billHeader.sort').d('序号'),
                  align: 'center',
                  width: 70,
                  renderer: ({ record }) =>
                    (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
                },
                {
                  name: 'lineNum',
                  align: 'center',
                  width: 70,
                },
                {
                  name: 'issueDateStart',
                  align: 'center',
                  width: 180,
                },
                {
                  name: 'issueDateEnd',
                  align: 'center',
                  width: 180,
                },
                {
                  name: 'uomMeaning',
                },
                {
                  name: 'value',
                  align: 'right',
                },
                {
                  name: 'discountType',
                },
                {
                  name: 'discountAmount',
                  align: 'right',
                },
                {
                  name: 'issueAmount',
                  align: 'right',
                },
              ]}
              queryBar="none"
            />
          </Card>
        </Content>
      </div>
    );
  }
}
