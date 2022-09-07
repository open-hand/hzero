/**
 * refundOrder 入口文件
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-17
 * @copyright 2018 © HAND
 */

import React from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import { Content, Header } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { TagRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';

import FilterForm from './FilterForm';
import RefundOrderDrawer from './RefundOrderDrawer';
import RefuseModal from './RefuseModal';

@connect(({ loading, refundOrder }) => ({
  refundOrder,
  fetchOrderListLoading: loading.effects['refundOrder/fetchOrderList'],
  getOrderDetailLoading: loading.effects['refundOrder/getOrderDetail'],
  refuseOrderloading: loading.effects['refundOrder/refuseOrder'],
}))
@formatterCollections({ code: 'hpay.refundOrder' })
export default class RefundOrder extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      refuseModalVisible: false,
      currentRecord: {},
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'refundOrder/init' });
    this.fetchOrderList();
  }

  /**
   * 获取订单列表
   * @param {object} params
   */
  @Bind()
  fetchOrderList(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'refundOrder/fetchOrderList',
      payload: { ...params },
    });
  }

  /**
   * 查询列表
   * @param {object} form
   */
  @Bind()
  handleSearch(fieldsValue) {
    this.fetchOrderList({ ...fieldsValue, page: {} });
  }

  /**
   * 获取订单明细
   */
  @Bind()
  getOrderDetail(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'refundOrder/updateState',
      payload: {
        orderDetail: {},
      },
    });
    dispatch({
      type: 'refundOrder/getOrderDetail',
      payload: record,
    });
  }

  /**
   * 确认退款
   * @param {object} record
   */
  @Bind()
  handleConfirm(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'refundOrder/confirmOrder',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleGetForm();
      }
    });
  }

  /**
   * 拒绝退款
   * @param {object} record
   */
  @Bind()
  handleRefuse(record) {
    this.handleRefuseModalVisible(true);
    const { dispatch } = this.props;
    dispatch({
      type: 'refundOrder/updateState',
      payload: {
        orderDetail: {},
      },
    });
    this.setState({
      currentRecord: record,
    });
    // dispatch({
    //   type: 'refundOrder/getRefusedOrderDetail',
    //   payload: record,
    // });
  }

  /**
   * 确认拒绝退款
   */
  @Bind()
  handleConfirmRefuse(params) {
    const { dispatch } = this.props;
    dispatch({
      type: 'refundOrder/refuseOrder',
      payload: params,
    }).then((res) => {
      if (res) {
        this.handleRefuseModalVisible(false);
        notification.success();
        this.handleGetForm();
      }
    });
  }

  /**
   * 获取表单刷新
   */
  @Bind()
  handleGetForm(params = {}) {
    const {
      refundOrder: { pagination = {} },
    } = this.props;
    const fieldValues = this.filterForm.getFieldsValue();
    fieldValues.refundDatetimeFrom =
      fieldValues.refundDatetimeFrom &&
      fieldValues.refundDatetimeFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.refundDatetimeTo =
      fieldValues.refundDatetimeTo && fieldValues.refundDatetimeTo.format(DEFAULT_DATETIME_FORMAT);
    this.fetchOrderList({ ...fieldValues, page: { pagination, ...params } });
  }

  /**
   * 重置表单
   */
  @Bind()
  handleResetSearch(form) {
    form.resetFields();
  }

  /**
   * 分页
   */
  @Bind()
  handleTableChange(pagination) {
    this.handleGetForm(pagination);
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 控制refuseModal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleRefuseModalVisible(flag) {
    this.setState({ refuseModalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record) {
    this.handleModalVisible(true);
    this.getOrderDetail(record);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideRefuseModal() {
    this.handleRefuseModalVisible(false);
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  render() {
    const {
      fetchOrderListLoading = false,
      getOrderDetailLoading = false,
      refuseOrderloading = false,
      refundOrder: {
        channelList = [],
        statusList = [],
        refundOrderList = [],
        orderDetail = [],
        pagination = {},
        wayList = [],
      },
    } = this.props;
    const { modalVisible, refuseModalVisible, currentRecord } = this.state;
    const orderColumns = [
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.refundOrderNum').d('退款订单号'),
        width: 180,
        dataIndex: 'refundOrderNum',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.paymentOrderNum').d('支付订单号'),
        width: 180,
        dataIndex: 'paymentOrderNum',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.merchantOrderNum').d('商户订单号'),
        width: 100,
        dataIndex: 'merchantOrderNum',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.channelTradeNo').d('支付流水号'),
        width: 220,
        dataIndex: 'channelTradeNo',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.channelCode').d('支付渠道'),
        width: 90,
        dataIndex: 'channelMeaning',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.refundAmount').d('退款金额'),
        dataIndex: 'refundAmount',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.refundReason').d('退款原因'),
        dataIndex: 'refundReason',
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.status').d('退款状态'),
        width: 120,
        dataIndex: 'status',
        render: (val) => {
          const statusLists = [
            {
              status: 'UNREF',
              text: intl.get('hpay.refundOrder.model.refundOrder.unref').d('待退款'),
            },
            {
              status: 'REFUNDING',
              text: intl.get('hpay.refundOrder.model.refundOrder.refunding').d('退款中'),
            },
            {
              status: 'REFUNDED',
              color: 'green',
              text: intl.get('hpay.refundOrder.model.refundOrder.refunded').d('已退款'),
            },
            {
              status: 'FAILED',
              color: 'red',
              text: intl.get('hpay.refundOrder.model.refundOrder.failed').d('退款失败'),
            },
            {
              status: 'REFUSED',
              color: 'red',
              text: intl.get('hpay.refundOrder.model.refundOrder.refused').d('退款拒接'),
            },
          ];
          return TagRender(val, statusLists);
        },
      },
      {
        title: intl.get('hpay.refundOrder.model.refundOrder.refundDatetime').d('退款成功时间'),
        width: 150,
        dataIndex: 'refundDatetime',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        fixed: 'right',
        dataIndex: 'action',
        render: (_, record) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <a
                onClick={() => {
                  this.showModal(record);
                }}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          if (record.status === 'UNREF') {
            operators.push(
              {
                key: 'refund',
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.refund').d('是否确认退款？')}
                    onConfirm={() => {
                      this.handleConfirm(record);
                    }}
                  >
                    <a>{intl.get('hzero.common.button.refund').d('退款')}</a>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.refund').d('退款'),
              },
              {
                key: 'refuse',
                ele: (
                  <a
                    onClick={() => {
                      this.handleRefuse(record);
                    }}
                  >
                    {intl.get('hzero.common.button.refuse').d('拒绝')}
                  </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.refuse').d('拒绝'),
              }
            );
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];
    const scroll = {
      x: tableScrollWidth(orderColumns, 200),
    };
    return (
      <>
        <Header title={intl.get('hpay.refundOrder.view.message.title').d('退款订单')} />

        <Content>
          <FilterForm
            search={this.handleGetForm}
            channelList={channelList}
            statusList={statusList}
            ref={(Form) => {
              this.filterForm = Form;
            }}
          />
          <Table
            bordered
            columns={orderColumns}
            dataSource={refundOrderList}
            scroll={scroll}
            loading={fetchOrderListLoading}
            pagination={pagination}
            onChange={this.handleTableChange}
          />
          <RefundOrderDrawer
            modalVisible={modalVisible}
            title={intl.get('hzero.common.button.detail').d('详情')}
            initLoading={getOrderDetailLoading}
            onCancel={this.hideModal}
            onOk={this.hideModal}
            initData={orderDetail}
            wayList={wayList}
          />
          <RefuseModal
            title={intl.get('hzero.common.button.refuse').d('拒绝')}
            modalVisible={refuseModalVisible}
            loading={refuseOrderloading}
            initData={currentRecord}
            onCancel={this.hideRefuseModal}
            onOk={this.handleConfirmRefuse}
          />
        </Content>
      </>
    );
  }
}
