/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/19
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { DataSet, Form, TextField, Select, Currency, Lov, TextArea } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { Bind } from 'lodash-decorators';
import { createFormDS } from '@/stores/AccountBalance/AccountBalanceDS';
import { ACCOUNT_BALANCE_CONSTANT } from '@/constants/constants';

@formatterCollections({ code: ['hchg.accountBalance'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.createFormDS = new DataSet(createFormDS);
  }

  componentDidMount() {
    this.createFormDS.create({
      accountType: ACCOUNT_BALANCE_CONSTANT.USER,
      enabledFlag: true,
    });
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const validate = await this.createFormDS.validate();
    if (!validate) {
      return notification.error({
        message: intl.get('hchg.accountBalance.view.validate.message').d('请完善必输信息'),
      });
    }
    const result = await this.createFormDS.submit();
    if (result.success) {
      this.props.history.push(`/hchg/account-balance/detail/${result.content[0].balanceId}`);
    }
  }

  /**
   * 用户Lov/ 租户Lov变更
   */
  @Bind()
  handleAccountChange(lovRecord) {
    if (lovRecord) {
      const { current } = this.createFormDS;
      const isUser = current.get('accountType') === ACCOUNT_BALANCE_CONSTANT.USER;
      if (isUser) {
        current.set('accountName', lovRecord.realName);
      } else {
        current.set('accountName', lovRecord.tenantName);
      }
    }
  }

  /**
   * 账户类型
   */
  @Bind()
  handleTypeChange() {
    this.createFormDS.current.init('accountId', null);
    this.createFormDS.current.init('accountName', null);
    this.createFormDS.current.init('accountLov', null);
  }

  render() {
    const { match } = this.props;
    const { path } = match;
    return (
      <>
        <Header
          title={intl.get('hchg.accountBalance.view.title.accountDetail').d('创建账户')}
          backPath="/hchg/account-balance/list"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '创建账户-保存',
              },
            ]}
            icon="save"
            type="primary"
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hchg.accountBalance.view.title.basicInformation').d('基本信息')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.createFormDS} columns={3}>
              <TextField
                name="accountNum"
                placeholder={intl
                  .get('hchg.accountBalance.view.title.accountNumTip')
                  .d('若留空则自动生成账户编码')}
              />
              <Select name="accountType" onChange={this.handleTypeChange} />
              <Lov name="accountLov" onChange={this.handleAccountChange} />
              <TextField name="accountName" />
              <Select name="enabledFlag" />
              <Currency name="balanceAmount" disabled />
              <TextArea name="remark" colSpan={3} />
            </Form>
          </Card>
        </Content>
      </>
    );
  }
}
