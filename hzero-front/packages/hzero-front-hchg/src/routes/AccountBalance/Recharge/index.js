/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/20
 * @copyright HAND ® 2020
 */
import React from 'react';
import QRCode from 'qrcode.react';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import { Modal as PayModal } from 'hzero-ui';
import { DataSet, Form, TextField, Select, Currency, Lov, Modal, Tooltip } from 'choerodon-ui/pro';
import { Card, Divider } from 'choerodon-ui';
import { Button as ButtonPermission } from 'components/Permission';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import { Bind } from 'lodash-decorators';
import { isUndefined, isNumber } from 'lodash';
import { getResponse } from 'utils/utils';
// import Icon from '@/components/Icon/index.tsx';
import { convertCurrency } from '@/utils/utils';
import { rechargeFormDS } from '@/stores/AccountBalance/AccountBalanceDS';
import { accountRecharge } from '@/services/accountBalanceService';
import { ACCOUNT_BALANCE_CONSTANT } from '@/constants/constants';

const viewModalKey = Modal.key();
let modal;
let payWindow;

@formatterCollections({ code: ['hchg.accountBalance'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.rechargeFormDS = new DataSet(rechargeFormDS);
  }

  async componentDidMount() {
    const {
      match: { params },
    } = this.props;
    const { balanceId } = params;
    if (!isUndefined(balanceId)) {
      this.rechargeFormDS.setQueryParameter('balanceId', balanceId);
      await this.rechargeFormDS.query();
      this.rechargeFormDS.current.set('_status', 'update');
    } else {
      this.rechargeFormDS.create({});
    }
  }

  /**
   * 跳转到明细页
   */
  @Bind()
  handleGotoRecharge() {
    const { history } = this.props;
    const id = this.rechargeFormDS.current.get('balanceId');
    history.push(`/hchg/account-balance/detail/${id}`);
  }

  /**
   * 关闭消息弹窗
   */
  @Bind()
  closeModal() {
    modal.close();
  }

  /**
   * 处理日志
   */
  @Bind()
  handleShowWxQrcode(payData) {
    const {
      match: { path },
    } = this.props;
    modal = Modal.open({
      title: intl.get('hchg.accountBalance.view.title.weChatPay').d('微信支付'),
      closable: false,
      movable: false,
      key: viewModalKey,
      children: (
        <div style={{ textAlign: 'center' }}>
          {payData && (
            <div>
              <QRCode value={payData} style={{ margin: '0 auto' }} />
              <p style={{ marginTop: '8px' }}>
                {intl
                  .get('hchg.accountBalance.view.title.weChatTip')
                  .d('打开微信，扫描二维码进行支付')}
              </p>
              <p>
                {intl
                  .get('hchg.accountBalance.view.title.weChatWarning')
                  .d('充值完成前请不要关闭窗口')}
              </p>
            </div>
          )}
        </div>
      ),
      footer: (
        <div>
          <a onClick={() => this.closeModal()}>
            {intl.get('hchg.accountBalance.view.title.rechargeProblem').d('充值遇到问题？')}
          </a>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.finishRecharge`,
                type: 'button',
                meaning: '账号充值-已完成充值',
              },
            ]}
            type="primary"
            onClick={() => {
              this.closeModal();
              this.handleGotoRecharge();
            }}
          >
            {intl.get('hchg.accountBalance.view.button.finishRecharge').d('已完成充值')}
          </ButtonPermission>
        </div>
      ),
    });
  }

  /**
   * 支付方式
   */
  async handlePay(type) {
    const {
      match: {
        params: { balanceId },
      },
    } = this.props;
    await this.rechargeFormDS.validate();
    if (!this.rechargeFormDS.current.get('balanceId')) {
      return notification.error({
        message: intl.get('hchg.accountBalance.view.validate.validateAccount').d('请填写账号信息'),
      });
    }
    if (!this.rechargeFormDS.current.get('rechargeAmount')) {
      return notification.error({
        message: intl.get('hchg.accountBalance.view.validate.validateAmount').d('请填写充值金额'),
      });
    }
    if (type === ACCOUNT_BALANCE_CONSTANT.UNIONPAY) {
      return notification.warning({
        message: intl.get('hchg.accountBalance.view.title.payTypeWarning').d('暂不支持该方式'),
      });
    }
    const id = isUndefined(balanceId) ? this.rechargeFormDS.current.get('balanceId') : balanceId;
    notification.success({
      message: intl.get('hzero.common.notification.success').d('操作成功'),
      description: intl
        .get('hchg.accountBalance.view.message.title.push')
        .d('正在跳转支付，请勿关闭窗口...'),
    });
    accountRecharge({
      balanceId: id,
      rechargeChannel: type,
      rechargeAmount: this.rechargeFormDS.current.get('rechargeAmount'),
    }).then((result) => {
      if (getResponse(result)) {
        if (type === ACCOUNT_BALANCE_CONSTANT.WXPAY) {
          this.handleShowWxQrcode(result);
        } else if (type === ACCOUNT_BALANCE_CONSTANT.ALIPAY) {
          this.showPayTip();
          if (!isUndefined(payWindow)) {
            payWindow.close();
          }
          payWindow = window.open('', '_blank', '', true);
          payWindow.document.write(result);
          payWindow.focus();
        }
      }
    });
  }

  /**
   * 支付遮罩
   */
  @Bind()
  showPayTip() {
    const ref = PayModal.warning({
      title: intl.get('hchg.accountBalance.view.title.rechargeTip').d('充值提示'),
      closable: false,
      content: (
        <div>
          <p>
            {intl
              .get('hchg.accountBalance.view.title.payWarning')
              .d('请在新打开的页面完成充值，充值完成前请不要关闭窗口')}
          </p>
          <a onClick={() => ref.destroy()}>
            {intl.get('hchg.accountBalance.view.title.rechargeProblem').d('充值遇到问题？')}
          </a>
        </div>
      ),
      okText: intl.get('hchg.accountBalance.view.button.finishRecharge').d('已完成充值'),
      onOk: () => {
        ref.destroy();
        this.handleGotoRecharge();
      },
    });
  }

  /**
   * 金额转换为大写
   */
  @Bind()
  handleCurrencyChange(value) {
    const temp = isNumber(value) ? value.toFixed(2) : '';
    this.rechargeFormDS.current.set('upperAmount', convertCurrency(temp));
  }

  render() {
    const {
      match: {
        params: { balanceId },
      },
    } = this.props;
    const selectable = isUndefined(balanceId);
    return (
      <>
        <Header
          title={intl
            .get('hchg.accountBalance.view.title.accountBalanceRecharge')
            .d('账户余额充值')}
          backPath={
            selectable ? '/hchg/account-balance/list' : `/hchg/account-balance/detail/${balanceId}`
          }
        />
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hchg.accountBalance.view.title.basicInformation').d('基本信息')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.rechargeFormDS} columns={3}>
              <Lov name="accountLov" disabled={!selectable} />
              <Select name="accountType" disabled />
              <TextField name="accountName" disabled />
              <Currency name="balanceAmount" disabled />
              <TextField name="remark" disabled colSpan={2} />
              <Currency name="rechargeAmount" newLine onChange={this.handleCurrencyChange} />
              <TextField name="upperAmount" disabled />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hchg.accountBalance.view.title.payType').d('付款方式')}</h3>}
          >
            <div style={{ textAlign: 'center' }}>
              {/* <Tooltip
                placement="top"
                title={intl.get('hchg.accountBalance.view.title.weChatPay').d('微信支付')}
              >
                <Icon
                  kind="wxpay"
                  color="#1afa29"
                  size="64"
                  style={{ marginTop: '24px', cursor: 'hand' }}
                  onClick={() => this.handlePay(ACCOUNT_BALANCE_CONSTANT.WXPAY)}
                />
              </Tooltip>
              <Tooltip
                placement="top"
                title={intl.get('hchg.accountBalance.view.title.alipay').d('支付宝支付')}
              >
                <Icon
                  kind="alipay"
                  color="#1296db"
                  size="64"
                  style={{ marginTop: '24px', marginLeft: '18px', cursor: 'hand' }}
                  onClick={() => this.handlePay(ACCOUNT_BALANCE_CONSTANT.ALIPAY)}
                />
              </Tooltip>
              <Tooltip
                placement="top"
                title={intl.get('hchg.accountBalance.view.title.unionpay').d('中国银联支付')}
              >
                <Icon
                  kind="unionpay"
                  size="64"
                  style={{ marginTop: '24px', marginLeft: '18px', cursor: 'hand' }}
                  onClick={() => this.handlePay(ACCOUNT_BALANCE_CONSTANT.UNIONPAY)}
                />
              </Tooltip> */}

              <Tooltip
                placement="top"
                title={intl.get('hchg.accountBalance.view.title.weChatPay').d('微信支付')}
              >
                <svg
                  t="1582616519863"
                  className="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="4709"
                  width="48"
                  height="48"
                  style={{ cursor: 'hand' }}
                  onClick={() => this.handlePay(ACCOUNT_BALANCE_CONSTANT.WXPAY)}
                >
                  <path
                    d="M0 488.96v-30.72C2.56 430.08 7.68 404.48 15.36 378.88c20.48-66.56 53.76-122.88 99.84-171.52C179.2 138.24 256 94.72 340.48 66.56c48.64-15.36 97.28-23.04 145.92-25.6 40.96-2.56 79.36 0 120.32 7.68 33.28 5.12 64 12.8 97.28 25.6 79.36 28.16 148.48 71.68 207.36 133.12 7.68 7.68 12.8 15.36 20.48 23.04-2.56 0-2.56 2.56-5.12 2.56-20.48 10.24-40.96 20.48-61.44 28.16-143.36 66.56-286.72 133.12-430.08 197.12-28.16 12.8-56.32 10.24-81.92-5.12-20.48-12.8-38.4-25.6-58.88-38.4-12.8-10.24-25.6-17.92-40.96-28.16-12.8-7.68-20.48-2.56-20.48 12.8v2.56c2.56 12.8 5.12 25.6 10.24 38.4 25.6 61.44 51.2 120.32 76.8 181.76 7.68 17.92 20.48 25.6 35.84 23.04 10.24 0 20.48-2.56 28.16-7.68 30.72-15.36 58.88-33.28 87.04-51.2 156.16-92.16 309.76-184.32 465.92-276.48 10.24-7.68 23.04-12.8 33.28-20.48 0 2.56 2.56 2.56 2.56 5.12 5.12 10.24 10.24 17.92 12.8 28.16 28.16 61.44 40.96 125.44 35.84 192-2.56 28.16-7.68 56.32-15.36 84.48-17.92 61.44-48.64 115.2-92.16 163.84-51.2 56.32-115.2 99.84-184.32 128-40.96 15.36-81.92 28.16-122.88 35.84-28.16 5.12-56.32 7.68-81.92 7.68h-46.08c-20.48-2.56-40.96-2.56-58.88-5.12-30.72-5.12-61.44-12.8-89.6-23.04H322.56c-12.8 7.68-23.04 15.36-35.84 23.04-28.16 17.92-53.76 33.28-81.92 48.64-5.12 2.56-12.8 5.12-17.92 5.12s-10.24-2.56-10.24-10.24c0-5.12 0-12.8 2.56-17.92 5.12-38.4 12.8-74.24 20.48-112.64 0-5.12 0-7.68-5.12-10.24-15.36-12.8-30.72-23.04-46.08-38.4-51.2-46.08-92.16-102.4-117.76-166.4C20.48 599.04 12.8 568.32 7.68 537.6c-2.56-12.8-2.56-25.6-2.56-40.96-5.12 0-5.12-2.56-5.12-7.68z"
                    fill="#1afa29"
                    p-id="4710"
                  />
                </svg>
              </Tooltip>
              <Tooltip
                placement="top"
                title={intl.get('hchg.accountBalance.view.title.alipay').d('支付宝支付')}
              >
                <svg
                  t="1582617121268"
                  className="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="5152"
                  width="48"
                  height="48"
                  style={{ marginLeft: '18px', cursor: 'hand' }}
                  onClick={() => this.handlePay(ACCOUNT_BALANCE_CONSTANT.ALIPAY)}
                >
                  <path
                    d="M998.912 694.23616 998.912 215.06048c0-103.296-83.82464-187.12064-187.18208-187.12064L212.72064 27.93984C109.42976 27.93984 25.6 111.76448 25.6 215.06048l0 599.00928c0 103.29088 83.7632 187.11552 187.12064 187.11552l599.00928 0c92.03712 0 168.6272-66.54976 184.25856-154.08128-49.6384-21.4784-264.73984-114.37056-376.79616-167.84384-85.27872 103.30112-174.58688 165.28896-309.20704 165.28896s-224.47104-82.9184-213.70368-184.38656c7.1168-66.6112 52.80256-175.49824 251.23328-156.8256 104.57088 9.79456 152.448 29.32736 237.73696 57.48736 22.07744-40.44288 40.38144-84.98176 54.31296-132.36736L261.38624 428.45696l0-37.47328 187.11552 0L448.50176 323.70176 220.2624 323.70176l0-41.2416 228.23936 0L448.50176 185.37472c0 0 2.06848-15.21152 18.86208-15.21152l93.55776 0 0 112.29696 243.31776 0 0 41.2416-243.31776 0 0 67.28192 198.49728 0c-18.2528 74.27584-45.93152 142.52544-80.60416 202.14272C736.48128 613.9904 998.912 694.23616 998.912 694.23616L998.912 694.23616 998.912 694.23616 998.912 694.23616zM295.08608 780.30336c-142.22336 0-164.73088-89.7792-157.18912-127.31904 7.48032-37.3504 48.6656-86.07232 127.74912-86.07232 90.88 0 172.27264 23.23456 269.96736 70.79936C466.9952 727.08096 382.68416 780.30336 295.08608 780.30336L295.08608 780.30336 295.08608 780.30336z"
                    p-id="5153"
                    fill="#1296db"
                  />
                </svg>
              </Tooltip>
              <Tooltip
                placement="top"
                title={intl.get('hchg.accountBalance.view.title.unionpay').d('中国银联支付')}
              >
                <svg
                  t="1582617503308"
                  className="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="11350"
                  width="64"
                  height="64"
                  style={{ marginLeft: '18px', cursor: 'hand' }}
                  onClick={() => this.handlePay(ACCOUNT_BALANCE_CONSTANT.UNIONPAY)}
                >
                  <path
                    d="M229.643 233.89c-26.054 3.28-52.107 24.846-60.636 49.688-5.683 15.47-107.536 454.219-108.005 462.19-0.949 22.968 12.314 39.844 34.104 44.064 8.055 1.874 266.703 1.874 275.232 0 24.631-4.69 47.843-23.908 56.372-47.345 3.316-8.906 108.48-456.093 108.48-463.595 1.422-21.096-11.372-38.44-31.738-44.533-4.739-0.934-262.912-1.874-273.81-0.47z"
                    fill="#E60012"
                    p-id="11351"
                  />
                  <path
                    d="M470.762 233.89c-26.054 3.28-52.107 24.846-60.632 49.688-5.214 15.47-107.534 454.219-107.534 462.19-0.95 22.968 12.314 39.844 34.108 44.064 8.05 1.874 266.698 1.874 275.227 0 24.635-4.69 47.847-23.908 56.372-47.345 3.317-8.906 108.479-456.093 108.479-463.595 1.424-21.096-11.366-38.44-31.736-44.533-5.214-0.934-263.387-1.874-274.284-0.47z"
                    fill="#00508E"
                    p-id="11352"
                  />
                  <path
                    d="M722.308 233.89c-26.054 3.28-52.112 24.846-60.637 49.688-5.209 15.47-107.534 454.219-107.534 462.19-0.945 22.968 12.32 39.844 34.108 44.064 8.055 1.874 200.383 1.874 208.908 0 24.634-4.69 47.847-23.908 56.372-47.345 3.316-8.906 108.484-456.093 108.484-463.595 1.418-21.096-11.371-38.44-31.743-44.533-4.734-1.404-220.748-2.343-231.645-0.934h23.687v0.465z"
                    fill="#00908C"
                    p-id="11353"
                  />
                  <path
                    d="M221.589 376.39c0 0.47-0.476 3.282-1.42 6.095-10.896 36.562-21.793 85.781-20.37 91.874 3.79 18.283 30.793 16.876 40.266-2.343 2.842-5.154 23.212-90.936 23.212-97.028 0-0.47 32.212 0 32.212 0.464 0 0 0 1.408-0.475 2.817-0.474 1.403-5.683 21.091-11.366 44.529-12.795 49.688-14.213 54.842-20.845 64.686-21.793 31.878-94.746 30.94-100.429-1.404-1.422-7.032 14.213-88.124 21.32-110.159 0-1.403 37.895-0.469 37.895 0.47z m484.139 0c21.79 4.69 28.42 22.03 18.472 47.816-9.947 25.78-31.262 37.028-68.686 37.028-11.371 0-10.423-1.874-17.529 33.282-1.419 6.562-2.841 12.656-2.841 13.594-0.476 2.343-34.11 2.343-33.635 0 28.426-120.468 30.793-130.78 30.793-131.72l0.474-1.402h34.104c27.003 0.464 34.583 0.464 38.848 1.403z m-292.282 46.408c4.735 0.938 8.05 4.69 8.05 7.97 0 11.717-24.16 19.688-33.16 10.779-8.525-8.905 9.475-22.5 25.11-18.75z m-83.849 8.904c0 0.94-0.474 2.817-0.474 4.22l-0.474 1.878 5.683-2.816c15.16-7.497 29.844-6.094 34.583 3.281 2.841 5.629 2.367 8.91-5.21 43.595-1.422 6.094-3.315 14.534-3.79 18.748-1.897 9.38-0.474 8.91-17.528 8.91-14.687 0-14.687 0-14.212-1.408 0-0.938 1.896-8.435 3.79-17.345 7.58-33.277 8.055-37.967 1.422-37.967-3.79 0-9.004 2.343-9.473 3.75-0.948 3.282-9.478 44.06-9.952 47.812l-0.945 4.22-14.687 0.938c-17.998 0.47-16.58 1.873-12.79-14.064 5.21-20.626 8.055-35.154 9.949-48.28 2.367-14.063 0.948-12.655 14.212-14.532 6.158-0.94 12.315-1.874 14.213-2.343 4.735-1.409 5.683-0.94 5.683 1.403z m225.014-0.464c0 0.933-0.474 2.811-0.474 4.216l-0.476 2.346 5.688-2.816c29.37-14.998 40.737-2.813 32.212 35.628-1.893 8.436-4.265 20.623-5.683 26.25-0.949 6.094-2.372 11.248-2.842 11.717-1.898 1.874-29.844 1.41-29.375 0 0-0.938 1.898-8.435 3.791-16.875 8.056-34.216 8.056-38.436 0.949-38.436-5.683 0-8.525 1.873-9.473 6.092-1.424 5.155-8.53 38.906-9.475 45l-0.948 5.158-14.687 0.47c-17.999 0.465-16.58 2.342-12.316-15.003 4.74-18.75 8.056-36.094 10.423-48.749 1.893-12.187 0.474-10.782 12.315-12.656 5.213-0.938 11.846-1.878 14.214-2.342 4.738-2.348 6.157-1.878 6.157 0z m287.547-0.47c1.892 28.592 2.368 37.028 2.368 37.498 0 0.47 4.264-7.032 8.999-16.406 9.473-18.749 7.58-16.876 18.002-18.28 2.842-0.469 8.525-1.409 12.79-2.342 10.423-1.878 10.423-2.817-1.423 17.81-16.105 27.658-38.368 66.564-46.423 80.627-24.157 43.591-24.157 43.591-44.527 44.06l-12.316 0.47 0.945-3.282c0.474-1.873 1.897-5.623 2.37-8.435l1.42-5.159h3.79c4.265 0 5.209-0.94 9-7.502 1.423-2.342 3.79-6.093 4.738-8.435 1.42-2.343 6.158-9.844 9.949-17.345l7.58-13.125-1.897-17.34c-2.367-20.158-5.209-44.065-6.631-51.097-0.95-6.562-0.95-6.562 7.58-7.5 3.79-0.466 9.948-1.874 13.264-2.343 8.999-2.812 9.947-2.812 10.422-1.874z m-357.183 0.47c36.476 6.562 23.686 69.37-16.106 78.28-27.003 6.094-45.475-4.22-45.475-24.847 0.47-36.093 27.472-59.53 61.58-53.433z m272.86 1.873c1.893 0.938 4.739 2.812 6.158 4.22 2.367 2.342 2.367 2.342 2.367 0.934 0.475-1.873 0-1.873 18.951-4.685 15.158-2.342 14.684-2.342 13.739 1.874-6.158 26.249-11.371 49.217-13.739 60.47-3.315 16.876-0.948 14.998-19.421 14.529h-15.635v-1.874c0-1.873-0.945-2.812-1.894-1.407-5.213 8.44-30.792 5.158-37.898-5.155-17.525-26.25 19.896-81.562 47.371-68.906z m-340.129 13.595s0 2.342-0.474 4.219c-3.786 14.999-10.418 45.469-11.842 51.092l-1.422 7.032-15.632 0.469c-18.472 0.47-17.528 1.404-13.738-9.843 3.316-10.783 6.633-23.908 8.53-37.972 1.892-12.186 0-10.313 13.738-12.186 6.157-0.94 12.79-1.873 14.208-2.342 3.79-0.939 6.158-0.939 6.632-0.47z m82.9 97.028c0 0.47-0.944 2.348-2.367 4.69-0.95 2.342-2.367 4.22-2.367 4.22 27.946 0.464 28.895 0.464 28.42 1.873l-5.209 16.876h-40.74l-2.367 1.873c-5.214 4.689-32.686 10.782-32.686 7.031l5.21-16.875h3.789c6.632 0 8.05-1.404 13.738-11.247l4.735-8.91c24.636-0.465 29.843 0 29.843 0.47z m62.06 0c0 0.47-0.475 2.812-1.424 5.629-0.948 2.342-1.417 4.685-1.417 5.154 0 0 2.366-0.94 5.207-3.28 10.423-7.033 19.422-8.437 45.95-8.437 10.423 0 19.422 0 19.896 0.465 0.475 0.939-15.156 51.565-17.528 56.25-3.316 6.098-6.633 9.379-11.84 11.721l-4.74 1.874-26.998 0.47-27.003 0.468-4.738 15.937c-9.474 30.47-9.474 28.128 4.264 26.72 10.897-0.94 10.423-1.873 7.107 9.374l-2.842 9.375h-13.738c-29.844 0.47-33.634-1.404-30.793-13.594 1.423-6.094 35.528-117.656 36.002-118.595 0.474-0.465 24.635-0.465 24.635 0.47z m124.584 0c0 0.47-0.474 1.877-0.944 3.75-1.423 4.69-1.423 4.69 4.735 1.41 8.054-4.221 27.002-5.629 65.845-5.629h12.32v5.629c0 6.562 0.475 7.03 6.158 7.966l4.264 0.469-2.372 8.44-2.366 8.435h-8.53c-21.789 0.47-25.104-1.873-25.58-14.528v-6.098l-1.418 4.22-1.423 4.69H733.2c-2.367 0-4.735 0-4.735 0.47 0 0-23.211 76.401-26.528 87.184-0.474 0.94 0 1.409 2.843 1.409 4.264 0 4.264 0 2.841 3.745-1.419 4.221-1.419 4.221 3.316 4.221 3.316 0 5.214-0.47 7.58-1.873 3.317-1.878 3.317-1.408 18.473-22.5l6.158-8.909h-12.79c-15.631 0-14.207 0.939-11.366-8.435l2.367-7.502h31.268c2.841-9.844 3.785-12.656 3.785-13.125 0-0.47-6.632-0.47-15.156-0.47h-15.158l4.735-16.875h42.633c23.212 0 42.638 0 42.638 0.47 0 0.469-0.948 4.22-2.37 8.44l-2.368 7.966-14.213 0.469-14.21 0.469c-2.37 7.032-3.315 10.313-3.789 11.252l-0.474 1.873h13.738c16.106 0 15.157-0.938 11.84 8.436l-2.367 7.501h-31.266l-4.735 5.624h12.316l1.892 11.252c1.898 12.656 1.898 12.656 8.055 12.656 4.74 0 4.74-0.94 1.424 10.312l-2.847 9.375h-8.999c-15.631 0-18.473-2.342-21.32-18.28l-1.418-10.313-5.683 7.502c-15.636 21.091-16.58 21.56-36.476 21.56-12.794 0-12.794 0-10.897-3.75 0.475-1.873 0.475-1.873-3.316-1.873-3.79 0-3.79 0-4.738 2.812l-0.475 2.811H666.88l0.474-1.408c1.424-4.684 3.79-4.215-25.105-4.215-25.109 0-26.527 0-26.053-1.409l2.368-8.435c2.841-8.44 2.367-8.44 5.209-8.44 2.37 0 2.37 0 3.315-3.281 22.268-73.592 29.374-97.03 30.319-100.31l1.897-6.094h13.264c8.525 0 14.682 0.465 14.682 0.934z m-168.167 40.314l-5.213 16.406h-28.42c-2.842 9.375-3.79 12.187-4.266 13.125-0.474 1.409 0.475 1.409 14.214 1.409 8.054 0 14.686 0.47 14.686 0.47 0 0.464-0.474 1.402-0.95 2.81-0.473 0.935-1.422 4.686-2.365 8.436l-1.898 6.093H475.5l-3.316 11.252c-4.738 15.937-4.264 16.406 13.738 14.064 7.581-0.939 7.107-1.877 3.79 9.375l-2.841 9.374h-19.9c-31.263 0-31.737-0.939-23.212-27.658 2.372-8.436 4.74-15.468 4.74-15.468s-3.317-0.47-8.056-0.47c-4.26 0-8.05 0-8.05-0.469 3.315-11.716 4.734-15.468 4.734-16.406 0.474-0.934 1.423-1.403 8.53-1.403h8.05l3.79-14.534h-7.576c-5.688 0-7.58 0-7.58-0.939 0-0.933 4.264-14.528 4.733-15.937 0.95-0.933 72.483-0.469 72.008 0.47z m91.9 33.752c0 0.938-0.949 3.75-1.42 6.093-2.846 13.125-6.631 15.937-21.792 16.875-5.209 0.47-9.948 0.938-9.948 0.938-0.949 1.405-0.949 12.657 0 14.53l1.423 1.877 9.473-0.47c5.21-0.469 9.474-0.469 9.474-0.469 0 0.939-5.21 17.345-5.684 17.81-0.948 0.94-29.843 0.47-33.634-0.934-5.683-1.878-5.683-1.409-5.214-30.94l0.476-25.78h24.16v9.844h4.74c5.208 0 5.682-0.47 8.05-7.033l1.422-3.75h9.475c8.524 0.47 9.473 0.47 8.999 1.409z m44.53-200.626l-5.213 22.5h7.107c36.476 0.938 51.637-40.784 16.105-43.596-5.684-0.47-10.893-0.939-11.367-0.939-1.422 0-1.422 1.41-6.631 22.035z m-188.542 31.874c-8.999 3.75-17.524 37.501-10.417 42.656 5.208 4.22 12.79-2.812 16.58-14.529 6.158-21.565 4.26-31.878-6.163-28.127z m282.339 2.342c-9.478 4.69-16.58 37.971-9.478 41.721 9.478 5.155 21.794-12.654 21.794-31.877 0-8.905-5.214-13.125-12.316-9.844zM557.927 562.957l-2.368 8.436c-1.422 4.22-2.367 7.97-2.841 8.435 0 0.47 2.367-0.465 5.683-2.343 9.473-4.685 12.316-5.623 26.053-6.092l11.846-0.47c1.893-5.623 2.368-7.966 2.368-8.435 0.474-0.47-40.267-0.47-40.741 0.47z m-8.05 27.185l-1.899 7.5 40.741-0.47 2.368-7.5c-32.212 0-41.21 0-41.21 0.47z m128.848-16.876c-1.896 6.097-3.315 10.782-2.841 10.782l5.683-1.873c2.842-0.94 8.055-2.347 10.897-2.812 2.841-0.469 5.209-0.94 5.683-0.94 0 0 4.74-14.532 4.74-15.001 0 0-4.74-0.465-10.423-0.465H682.04l-3.316 10.31z m-7.58 23.907c0 0.469-1.893 5.159-3.317 10.782-1.892 5.624-3.315 10.783-3.315 10.783 0 0.465 2.367-0.47 5.683-1.878 3.316-1.403 8.055-2.812 10.897-3.28 6.157-0.94 6.631-1.404 7.107-3.751 0.474-0.935 1.422-4.685 2.366-7.497l1.898-5.628H682.04c-5.683 0-10.897 0-10.897 0.469z m-16.58 53.908l20.845 0.47c4.264-14.064 5.683-18.754 5.683-19.223l-20.844-0.934-5.684 19.687z"
                    fill="#FFFFFF"
                    p-id="11354"
                  />
                </svg>
              </Tooltip>
              <Divider dashed />
              <div style={{ color: '#A9A9A9' }}>
                {intl
                  .get('hchg.accountBalance.view.tile.payMessage1')
                  .d('请您选择支付方式，支付请直接点击相应的按钮。')}
              </div>
              <div style={{ color: '#A9A9A9' }}>
                {intl
                  .get('hchg.accountBalance.view.tile.payMessage2')
                  .d(
                    '如果您使用微信账号或者支付宝支付，请点击微信图标或者支付宝图标。如果您使用银行卡，请点击银联图标。'
                  )}
              </div>
            </div>
          </Card>
        </Content>
      </>
    );
  }
}
