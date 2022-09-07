/**
 * Rate - 汇率定义-平台级
 * @date: 2018-7-15
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Button, Form, Table, Row, Col, DatePicker } from 'hzero-ui';
import { divide, multiply, round } from 'lodash';
import { Bind, Throttle } from 'lodash-decorators';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import OptionInput from 'components/OptionInput';
import ExcelExport from 'components/ExcelExport';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import {
  DATETIME_MIN,
  FORM_COL_4_LAYOUT,
  DEBOUNCE_TIME,
  DEFAULT_DATETIME_FORMAT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import { HZERO_PLATFORM } from 'utils/config';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  getDateFormat,
  getDateTimeFormat,
  tableScrollWidth,
} from 'utils/utils';

import RateForm from './RateForm';
import CrossRateDrawer from './CrossRateDrawer';

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@connect(({ loading, rate }) => ({
  rate,
  organizationId: getCurrentOrganizationId(),
  createLoading: loading.effects['rate/createRate'],
  updateLoading: loading.effects['rate/updateRate'],
  initLoading: loading.effects['rate/fetchRateData'],
  crossRateLoading: loading.effects['rate/createCrossRate'],
}))
@formatterCollections({ code: 'hpfm.rate' })
@Form.create({ fieldNameProp: null })
export default class Rate extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      rateFormData: {},
      crossRate: false,
      expandForm: false,
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'rate/init' });
    this.fetchRateData();
  }

  /**
   * @function fetchRateData - 查询汇率列表数据
   * @param {object} params - 查询参数
   */
  fetchRateData(params = {}) {
    const {
      dispatch,
      form,
      rate: { pagination = {} },
    } = this.props;
    const { forCurrency, toCurrency, startDate, endDate } = form.getFieldsValue();
    const formatStartDate = startDate && startDate.format(DEFAULT_DATETIME_FORMAT);
    const formatEndDate = endDate && endDate.format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'rate/fetchRateData',
      payload: {
        ...forCurrency,
        ...toCurrency,
        startDate: formatStartDate,
        endDate: formatEndDate,
        page: pagination,
        ...params,
      },
    });
  }

  /**
   * @function handleModalVisible - 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    const { dispatch } = this.props;
    if (flag === false && this.RateForm) {
      this.RateForm.resetForm();
    }
    dispatch({
      type: 'rate/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
  }

  /**
   * @function showModal - 新建显示模态框
   */
  @Bind()
  showModal() {
    this.setState({
      rateFormData: {},
    });
    this.handleModalVisible(true);
  }

  /**
   * @function hideModal - 隐藏模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * @function handleSearchRate - 搜索表单
   */
  @Bind()
  handleSearchRate() {
    this.fetchRateData({ page: {} });
  }

  /**
   * @function handleResetSearch - 重置查询表单
   */
  @Bind()
  handleResetSearch() {
    this.props.form.resetFields();
  }

  /**
   * @function handleStandardTableChange - 分页操作
   * @param {object} pagination - 分页数据对象
   */
  @Bind()
  handleStandardTableChange(pagination) {
    this.fetchRateData({
      page: pagination,
    });
  }

  /**
   * @function handleUpdateRate - 更新显示模态框
   * @param {object} record - 更新的数据
   */
  @Bind()
  handleUpdateRate(record) {
    this.setState({
      rateFormData: record,
    });
    this.handleModalVisible(true);
  }

  /**
   * @function handleAdd - 更新汇率定义
   * @param {object} record - 更新的数据
   */
  @Bind()
  handleAdd(fieldsValue) {
    const { dispatch } = this.props;
    const { rateFormData } = this.state;
    const params = {
      ...rateFormData,
      ...fieldsValue,
      enabledFlag: fieldsValue.enabledFlag ? 1 : 0,
      startDate: moment(fieldsValue.startDate).format(DATETIME_MIN),
      endDate: moment(fieldsValue.endDate).format(DATETIME_MIN),
      rateDate: moment(fieldsValue.rateDate).format(DATETIME_MIN),
      rate: round(divide(fieldsValue.exchangeNumber, fieldsValue.currencyNumber), 8),
    };
    dispatch({
      type: `rate/${rateFormData.exchangeRateId ? 'updateRate' : 'createRate'}`,
      payload: params,
    }).then((response) => {
      if (Array.isArray(response) && response.length > 0) {
        notification.warning({
          message: intl
            .get('hpfm.rate.view.validation.repeatData', {
              date: response.join('、'),
            })
            .d(`所选日期区间存在重复数据：${response.join('、')}`),
        });
      } else {
        // eslint-disable-next-line
        if (response) {
          notification.success();
          this.hideModal();
          this.fetchRateData();
        }
      }
    });
  }

  /**
   * 多查询条件展示
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  @Bind()
  renderFilterForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { expandForm } = this.state;
    const fromCurrencyArray = [
      {
        queryLabel: intl.get('hpfm.rate.model.rate.fromCurrencyCode').d('币种代码'),
        queryName: 'fromCurrencyCode',
        inputProps: {
          trim: true,
          typeCase: 'upper',
          inputChinese: false,
        },
      },
      {
        queryLabel: intl.get('hpfm.rate.model.rate.fromCurrencyName').d('币种名称'),
        queryName: 'fromCurrencyName',
      },
    ];
    const toCurrencyArray = [
      {
        queryLabel: intl.get('hpfm.rate.model.rate.toCurrencyCode').d('兑换币种代码'),
        queryName: 'toCurrencyCode',
        inputProps: {
          trim: true,
          typeCase: 'upper',
          inputChinese: false,
        },
      },
      {
        queryLabel: intl.get('hpfm.rate.model.rate.toCurrencyName').d('兑换币种名称'),
        queryName: 'toCurrencyName',
      },
    ];
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item>
              {getFieldDecorator('forCurrency')(
                <OptionInput style={{ width: '100%' }} queryArray={fromCurrencyArray} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item>
              {getFieldDecorator('toCurrency')(<OptionInput queryArray={toCurrencyArray} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.rate.model.rate.rateDateFrom').d('兑换日期从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('startDate')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={(currentDate) =>
                    getFieldValue('endDate') &&
                    moment(getFieldValue('endDate')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button style={{ marginRight: 8 }} onClick={this.handleResetSearch}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearchRate}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          type="flex"
          gutter={24}
          align="bottom"
          style={expandForm ? expandFormStyle : noExpandFormStyle}
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.rate.model.rate.rateDateTo').d('兑换日期至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('endDate')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={(currentDate) =>
                    getFieldValue('startDate') &&
                    moment(getFieldValue('startDate')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  @Bind()
  getSearchFormData() {
    const { form } = this.props;
    const { getFieldsValue } = form;
    const { forCurrency, toCurrency } = getFieldsValue();
    return filterNullValueObject({ ...forCurrency, ...toCurrency });
  }

  /**
   * 设置交叉汇率模态框
   * @param {string} value
   * @memberof Rate
   */
  @Bind()
  setCrossRateDrawer(value) {
    this.setState({
      crossRate: value,
    });
  }

  /**
   * 生成交叉汇率
   */
  @Bind()
  saveCrossRate(value) {
    const { dispatch } = this.props;
    const params = {
      ...value,
      rateDate: moment(value.rateDate).format(DATETIME_MIN),
    };
    dispatch({
      type: 'rate/createCrossRate',
      payload: params,
    }).then((response) => {
      if (response) {
        notification.success();
        this.setCrossRateDrawer(false);
        this.fetchRateData();
      }
    });
  }

  render() {
    const {
      match,
      initLoading,
      updateLoading,
      createLoading,
      organizationId,
      crossRateLoading,
      rate: { rateList = [], modalVisible, rateMethodList, pagination = {} },
    } = this.props;
    const { crossRate, rateFormData } = this.state;
    const title = rateFormData.exchangeRateId
      ? intl.get('hpfm.rate.view.message.edit').d('编辑汇率定义')
      : intl.get('hpfm.rate.view.message.create').d('新建汇率定义');
    const rateFormProps = {
      title,
      rateMethodList,
      modalVisible,
      anchor: 'right',
      confirmLoading: updateLoading || createLoading,
      onCancel: this.hideModal,
      onHandleAdd: this.handleAdd,
      initData: rateFormData,
    };
    const crossRateProps = {
      crossRate,
      anchor: 'right',
      confirmLoading: crossRateLoading,
      onCancelDrawer: this.setCrossRateDrawer,
      onSaveCrossRate: this.saveCrossRate,
    };
    const columns = [
      {
        title: intl.get('hpfm.rate.model.rate.fromCurrencyCode').d('币种代码'),
        width: 120,
        dataIndex: 'fromCurrencyCode',
      },
      {
        title: intl.get('hpfm.rate.model.rate.fromCurrencyName').d('币种名称'),
        dataIndex: 'fromCurrencyName',
        minWidth: 200,
      },
      {
        title: intl.get('hpfm.rate.model.rate.toCurrencyCode').d('兑换币种代码'),
        width: 120,
        dataIndex: 'toCurrencyCode',
      },
      {
        title: intl.get('hpfm.rate.model.rate.toCurrencyName').d('兑换币种名称'),
        dataIndex: 'toCurrencyName',
        minWidth: 200,
      },
      {
        title: intl.get('hpfm.rate.model.rate.rateTypeName').d('汇率类型'),
        key: 'rateTypeName',
        width: 100,
        dataIndex: 'rateTypeName',
      },
      {
        title: intl.get('hpfm.rate.model.rate.rateDate').d('兑换日期'),
        width: 150,
        dataIndex: 'rateDate',
        render: (text) => {
          return <span>{moment(text).format(getDateFormat())}</span>;
        },
      },
      {
        title: intl.get('hpfm.rate.model.rate.currencyNumber').d('货币数量'),
        width: 100,
        dataIndex: 'currencyNumber',
        render: () => {
          return <span>1</span>;
        },
      },
      {
        title: intl.get('hpfm.rate.model.rate.exchangeNumber').d('兑换数量'),
        width: 100,
        dataIndex: 'exchangeNumber',
        render: (text, record) => {
          return <span>{round(multiply(1, record.rate), 8)}</span>;
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '汇率定义-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdateRate(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.rate.view.message.title').d('汇率定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '汇率定义-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ExcelExport
            exportAsync
            requestUrl={`${HZERO_PLATFORM}/v1/${
              isTenantRoleLevel()
                ? `${organizationId}/exchange-rates/export`
                : 'exchange-rates/export'
            }`}
            queryParams={this.getSearchFormData}
          />
          <ButtonPermission
            icon="pay-circle"
            permissionList={[
              {
                code: `${match.path}.button.createCrossRate`,
                type: 'button',
                meaning: '汇率定义-生成交叉汇率',
              },
            ]}
            onClick={() => this.setCrossRateDrawer(true)}
          >
            {intl.get('hpfm.rate.view.rate.createCrossRate').d('生成交叉汇率')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">{this.renderFilterForm()}</div>
          <Table
            bordered
            rowKey="exchangeRateId"
            loading={initLoading}
            dataSource={rateList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handleStandardTableChange}
          />
          <RateForm {...rateFormProps} />
          <CrossRateDrawer {...crossRateProps} />
        </Content>
      </React.Fragment>
    );
  }
}
