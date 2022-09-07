import React from 'react';
import classnames from 'classnames';
import { DataSet, Form, TextField, Switch, Spin } from 'choerodon-ui/pro';
import { Card, Collapse, Icon } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME, DETAIL_DEFAULT_CLASSNAME } from 'utils/constants';

import { detailDs as DetailDs } from '../../stores/DraftDS';
import styles from './index.less';

const { Panel } = Collapse;

const Draft = (props) => {
  const detailDs = React.useMemo(() => new DataSet(DetailDs()), []);

  const [collapseKeys, setCollapseKeys] = React.useState(['service']);

  React.useEffect(() => {
    const {
      match: {
        params: { draftId },
      },
    } = props;
    detailDs.draftId = draftId;
    detailDs.query();
  }, []);

  const onCollapseChange = (key) => {
    setCollapseKeys(key);
  };

  return (
    <>
      <Header
        title={intl.get('hebk.draft.view.message.draft').d('银行票据')}
        backPath="/hebk/draft"
      />
      <Content>
        <Card
          className={DETAIL_CARD_CLASSNAME}
          bordered={false}
          title={intl.get('hebk.draft.view.message.baseInfo').d('基本信息')}
        >
          <Form
            className={styles['draft-form-label']}
            labelWidth={130}
            columns={3}
            dataSet={detailDs}
          >
            <TextField name="bankMarkMeaning" disabled />
            <TextField name="bankCode" disabled />
            <TextField name="accountNumber" disabled />
            <TextField name="draftTypeMeaning" disabled />
            <TextField name="draftNumber" disabled />
            <TextField name="bankDraftId" disabled />
            <TextField name="draftStatusMeaning" disabled />
            <TextField name="amount" disabled />
            <TextField name="date" disabled />
            <TextField name="dueDate" disabled />
            <TextField name="organizationCode" disabled />
            <TextField name="bankDraftStatusCode" disabled />
            <TextField name="bankDraftStatusDesc" disabled />
            <TextField name="drawerName" disabled />
            <TextField name="drawerAccountNumber" disabled />
            <TextField name="acceptorName" disabled />
            <TextField name="acceptorAccountNumber" disabled />
            <TextField name="payeeName" disabled />
            <TextField name="payeeAccountNumber" disabled />
            <TextField name="applicantName" disabled />
            <Switch name="transferFlag" disabled />
            <TextField name="consignFlag" disabled />
            <TextField name="autoDraftFlagMeaning" disabled />
          </Form>
        </Card>
        <Spin spinning={false} wrapperClassName={classnames(DETAIL_DEFAULT_CLASSNAME)}>
          <Collapse
            className="form-collapse"
            defaultActiveKey={['service']}
            onChange={onCollapseChange}
          >
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.drawer').d('出票人信息')}</h3>
                  <a>
                    {collapseKeys.includes('drawer')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('drawer') ? 'up' : 'down'} />
                </>
              }
              key="drawer"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="drawer.organizationCode" disabled />
                <TextField name="drawer.drawerType" disabled />
                <TextField name="drawer.accountName" disabled />
                <TextField name="drawer.accountBankNumber" disabled />
                <TextField name="drawer.accountBankName" disabled />
                <TextField name="drawer.ratingAgency" disabled />
                <TextField name="drawer.creditRating" disabled />
                <TextField name="drawer.ratingDueDate" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.acceptor').d('承兑人信息')}</h3>
                  <a>
                    {collapseKeys.includes('acceptor')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('acceptor') ? 'up' : 'down'} />
                </>
              }
              key="acceptor"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="acceptor.accountName" disabled />
                <TextField name="acceptor.accountNumber" disabled />
                <TextField name="acceptor.accountBankNumber" disabled />
                <TextField name="acceptor.accountBankName" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.payee').d('收款人信息')}</h3>
                  <a>
                    {collapseKeys.includes('payee')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('payee') ? 'up' : 'down'} />
                </>
              }
              key="payee"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="payee.accountName" disabled />
                <TextField name="payee.accountNumber" disabled />
                <TextField name="payee.accountBankNumber" disabled />
                <TextField name="payee.accountBankName" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.acceptorInfo').d('承兑信息')}</h3>
                  <a>
                    {collapseKeys.includes('acceptorInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('acceptorInfo') ? 'up' : 'down'} />
                </>
              }
              key="acceptorInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="acceptorInfo.acceptDate" disabled />
                <TextField name="acceptorInfo.batchNumber" disabled />
                <TextField name="acceptorInfo.contractNumber" disabled />
                <TextField name="acceptorInfo.invoiceNumber" disabled />
                <TextField name="acceptorInfo.remark" disabled />
                <Switch name="acceptorInfo.contractUsedFlag" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.discountInfo').d('贴现信息')}</h3>
                  <a>
                    {collapseKeys.includes('service')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('discountInfo') ? 'up' : 'down'} />
                </>
              }
              key="discountInfo"
            >
              <Form
                columns={3}
                labelWidth={130}
                className={styles['draft-form-label']}
                dataSet={detailDs}
              >
                <TextField name="discountInfo.discountType" disabled />
                <TextField name="discountInfo.discountAppDate" disabled />
                <TextField name="discountInfo.discountRate" disabled />
                <TextField name="discountInfo.discountAmount" disabled />
                <TextField name="discountInfo.openDate" disabled />
                <TextField name="discountInfo.dueDate" disabled />
                <TextField name="discountInfo.redeemRate" disabled />
                <TextField name="discountInfo.redeemAmount" disabled />
                <TextField name="discountInfo.contractNumber" disabled />
                <TextField name="discountInfo.invoiceNumber" disabled />
                <TextField name="discountInfo.onlineFlagMeaning" disabled />
                <TextField name="discountInfo.remark" disabled />
                <TextField name="discountInfo.inAccountBankNumber" disabled />
                <TextField name="discountInfo.inAccountBankName" disabled />
                <TextField name="discountInfo.inAccountNumber" disabled />
                <TextField name="discountInfo.applicantTypeMeaning" disabled />
                <TextField name="discountInfo.applicantOrgCode" disabled />
                <TextField name="discountInfo.applicantName" disabled />
                <TextField name="discountInfo.applicantAccountNumber" disabled />
                <TextField name="discountInfo.applicantAccountBankNumber" disabled />
                <Switch name="discountInfo.applicantAccountBankName" disabled />
                <TextField name="discountInfo.postName" disabled />
                <TextField name="discountInfo.postAccountNumber" disabled />
                <TextField name="discountInfo.postAccountBankNumber" disabled />
                <TextField name="discountInfo.postAccountBankName" disabled />
                <TextField name="discountInfo.batchNumber" disabled />
                <TextField name="discountInfo.agreementNumber" disabled />
                <TextField name="discountInfo.agreementInterestRate" disabled />
                <TextField name="discountInfo.businessType" disabled />
                <TextField name="discountInfo.agreementInterest" disabled />
                <TextField name="discountInfo.selfPayInterest" disabled />
                <TextField name="discountInfo.vatRate" disabled />
                <TextField name="discountInfo.vatTax" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.promptPayInfo').d('提示付款信息')}</h3>
                  <a>
                    {collapseKeys.includes('promptPayInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('promptPayInfo') ? 'up' : 'down'} />
                </>
              }
              key="promptPayInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="promptPayInfo.onlineFlagMeaning" disabled />
                <TextField name="promptPayInfo.overDueReason" disabled />
                <TextField name="promptPayInfo.promptPayApplicationDate" disabled />
                <TextField name="promptPayInfo.promptPayAmount" disabled />
                <TextField name="promptPayInfo.promptPayerTypeMeaning" disabled />
                <TextField name="promptPayInfo.promptPayerName" disabled />
                <TextField name="promptPayInfo.promptPayerOrgCode" disabled />
                <TextField name="promptPayInfo.promptPayerAccountNumber" disabled />
                <TextField name="promptPayInfo.promptPayerAccountBankName" disabled />
                <TextField name="promptPayInfo.promptPayerAccountBankNumber" disabled />
                <TextField name="promptPayInfo.refuseCodeMeaning" disabled />
                <TextField name="promptPayInfo.refuseRemark" disabled />
                <TextField name="promptPayInfo.remark" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.guaranteeInfo').d('保证信息')}</h3>
                  <a>
                    {collapseKeys.includes('guaranteeInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('guaranteeInfo') ? 'up' : 'down'} />
                </>
              }
              key="guaranteeInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="guaranteeInfo.guaranteedApplicationDate" disabled />
                <TextField name="guaranteeInfo.batchNumber" disabled />
                <TextField name="guaranteeInfo.guaranteeTypeMeaning" disabled />
                <TextField name="guaranteeInfo.guaranteeName" disabled />
                <TextField name="guaranteeInfo.guaranteeOrgCode" disabled />
                <TextField name="guaranteeInfo.guaranteeAccountNumber" disabled />
                <TextField name="guaranteeInfo.guaranteeAccountBankNumber" disabled />
                <TextField name="guaranteeInfo.guaranteeAccountBankName" disabled />
                <TextField name="guaranteeInfo.guaranteeRemark" disabled />
                <TextField name="guaranteeInfo.guarantorName" disabled />
                <TextField name="guaranteeInfo.guarantorAccountNumber" disabled />
                <TextField name="guaranteeInfo.guarantorAccountBankNumber" disabled />
                <TextField name="guaranteeInfo.guarantorAccountBankName" disabled />
                <TextField name="guaranteeInfo.guarantorAddress" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.endorseInfo').d('背书信息')}</h3>
                  <a>
                    {collapseKeys.includes('endorseInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('endorseInfo') ? 'up' : 'down'} />
                </>
              }
              key="endorseInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="endorseInfo.endorseApplicationDate" disabled />
                <TextField name="endorseInfo.endorseTransferFlag" disabled />
                <TextField name="endorseInfo.endorserTypeMeaning" disabled />
                <TextField name="endorseInfo.endorserName" disabled />
                <TextField name="endorseInfo.endorserOrgCode" disabled />
                <TextField name="endorseInfo.endorserAccountNumber" disabled />
                <TextField name="endorseInfo.endorserAccountBankNumber" disabled />
                <TextField name="endorseInfo.endorserAccountBankName" disabled />
                <TextField name="endorseInfo.endorserRemark" disabled />
                <TextField name="endorseInfo.endorseeName" disabled />
                <TextField name="endorseInfo.endorseeAccountNumber" disabled />
                <TextField name="endorseInfo.endorseeAccountBankNumber" disabled />
                <TextField name="endorseInfo.endorseeAccountBankName" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.pledgeInfo').d('质押信息')}</h3>
                  <a>
                    {collapseKeys.includes('pledgeInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('pledgeInfo') ? 'up' : 'down'} />
                </>
              }
              key="pledgeInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="pledgeInfo.pledgeApplicationDate" disabled />
                <TextField name="pledgeInfo.batchNumber" disabled />
                <TextField name="pledgeInfo.pledgorTypeMeaning" disabled />
                <TextField name="pledgeInfo.pledgorName" disabled />
                <TextField name="pledgeInfo.pledgorOrgCode" disabled />
                <TextField name="pledgeInfo.pledgorAccountNumber" disabled />
                <TextField name="pledgeInfo.pledgorAccountBankNumber" disabled />
                <TextField name="pledgeInfo.pledgorAccountBankName" disabled />
                <TextField name="pledgeInfo.pledgorRemark" disabled />
                <TextField name="pledgeInfo.pledgeeName" disabled />
                <TextField name="pledgeInfo.pledgeeAccountNumber" disabled />
                <TextField name="pledgeInfo.pledgeeAccountBankNumber" disabled />
                <TextField name="pledgeInfo.pledgeeAccountBankName" disabled />
                <TextField name="pledgeInfo.pledgeReleaseApplicationDate" disabled />
                <TextField name="pledgeInfo.pledgeeRemark" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.recourseInfo').d('追索信息')}</h3>
                  <a>
                    {collapseKeys.includes('recourseInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('recourseInfo') ? 'up' : 'down'} />
                </>
              }
              key="recourseInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="recourseInfo.recourseApplicationDate" disabled />
                <TextField name="recourseInfo.recourseReasonCodeMeaning" disabled />
                <TextField name="recourseInfo.recourseTypeMeaning" disabled />
                <TextField name="recourseInfo.recourseAmount" disabled />
                <TextField name="recourseInfo.recourseTypeMeaning" disabled />
                <TextField name="recourseInfo.recourseAmount" disabled />
                <TextField name="recourseInfo.recourserTypeMeaning" disabled />

                <TextField name="recourseInfo.recourserName" disabled />
                <TextField name="recourseInfo.recourserOrgCode" disabled />
                <TextField name="recourseInfo.recourserAccountNumber" disabled />
                <TextField name="recourseInfo.recourserAccountBankNumber" disabled />
                <TextField name="recourseInfo.recourserAccountBankName" disabled />
                <TextField name="recourseInfo.recoursePersonName" disabled />
                <TextField name="recourseInfo.recoursePersonAccountNumber" disabled />
                <TextField name="recourseInfo.recoursePersonOrgCode" disabled />
                <TextField name="recourseInfo.recoursePersonAccountBankNumber" disabled />
                <TextField name="recourseInfo.recoursePersonAccountBankName" disabled />
                <TextField name="recourseInfo.recourseRemark" disabled />
                <TextField name="recourseInfo.agreeSettleDate" disabled />
                <TextField name="recourseInfo.agreeSettleAmount" disabled />
                <TextField name="recourseInfo.agreeSettlePersonOrgCode" disabled />
                <TextField name="recourseInfo.agreeSettlePersonTypeMeaning" disabled />
                <TextField name="recourseInfo.agreeSettlePersonName" disabled />
                <TextField name="recourseInfo.agreeSettlePersonAccountNumber" disabled />
                <TextField name="recourseInfo.agreeSettlePersonAccountBankName" disabled />
                <TextField name="recourseInfo.agreeSettlePersonAccountBankNumber" disabled />
                <TextField name="recourseInfo.agreeSettleRemark" disabled />
              </Form>
            </Panel>
            <Panel
              showArrow={false}
              header={
                <>
                  <h3>{intl.get('hebk.draft.view.message.creditInfo').d('支付信用查复信息')}</h3>
                  <a>
                    {collapseKeys.includes('creditInfo')
                      ? intl.get(`hzero.common.button.up`).d('收起')
                      : intl.get(`hzero.common.button.expand`).d('展开')}
                  </a>
                  <Icon type={collapseKeys.includes('creditInfo') ? 'up' : 'down'} />
                </>
              }
              key="creditInfo"
            >
              <Form
                columns={3}
                className={styles['draft-form-label']}
                labelWidth={130}
                dataSet={detailDs}
              >
                <TextField name="creditInfo.inquiredPersonOrgCode" disabled />
                <TextField name="creditInfo.inquiredPersonName" disabled />
                <TextField name="creditInfo.acceptTotalNumber" disabled />
                <TextField name="creditInfo.acceptTotalAmount" disabled />
                <TextField name="creditInfo.settledTotalNumber" disabled />
                <TextField name="creditInfo.settledTotalAmount" disabled />
                <TextField name="creditInfo.unsettledTotalNumber" disabled />
                <TextField name="creditInfo.unsettledTotalAmount" disabled />
                <TextField name="creditInfo.protestTotalNumber" disabled />
                <TextField name="creditInfo.protestTotalAmount" disabled />
                <TextField name="creditInfo.protestYearTotalNumber" disabled />
                <TextField name="creditInfo.protestYearTotalAmount" disabled />
                <TextField name="creditInfo.protestLastYearTotalNumber" disabled />
                <TextField name="creditInfo.protestLastYearTotalAmount" disabled />
              </Form>
            </Panel>
          </Collapse>
        </Spin>
      </Content>
    </>
  );
};

export default Draft;
