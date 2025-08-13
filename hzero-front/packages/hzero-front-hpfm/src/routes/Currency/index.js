/**
 * Currency - 币种定义列表页
 * @date: 2018-7-3
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Button, Form, Input, Table, Row, Col } from 'hzero-ui';
import lodash from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import cacheComponent from 'components/CacheComponent';
import OptionInput from 'components/OptionInput';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import CurrencyDrawer from './CurrencyDrawer';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

/**
 * 币种定义
 * @extends {Component} - React.Component
 * @reactProps {Object} currency - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ currency, loading }) => ({
  currency,
  fetchLoading: loading.effects['currency/fetchCurrencyList'],
  updateLoading: loading.effects['currency/updateCurrency'],
  addLoading: loading.effects['currency/addCurrency'],
  detailLoading: loading.effects['currency/fetchDetail'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.currency'] })
@cacheComponent({ cacheKey: '/hpfm/mdm/Currency' })
export default class CurrencyList extends PureComponent {
  /**
   * 内部状态
   */
  state = {
    modalVisible: false,
    isCreateFlag: false,
    editRowData: {},
  };

  /**
   * 控制弹出框显示隐藏
   * @param {boolean} flag 显/隐标记
   * @param {boolean} createFlag 新建标记
   * @param {Object} record 行数据
   */
  @Bind()
  showEditModal(flag, createFlag, record) {
    this.setState({
      modalVisible: !!flag,
      isCreateFlag: createFlag,
      editRowData: flag ? record || {} : {},
    });
  }

  /**
   * 新增币种定义
   * @param {Object} fieldsValue 传递的filedvalue
   * @param {Object} form 表单
   */
  @Bind()
  handleAdd(fieldsValue) {
    const { dispatch } = this.props;
    const { editRowData } = this.state;
    if (editRowData.currencyId) {
      dispatch({
        type: 'currency/updateCurrency',
        payload: {
          ...editRowData,
          ...fieldsValue,
          currencyCode: lodash.trim(fieldsValue.currencyCode),
        },
      }).then(response => {
        if (response) {
          notification.success();
          this.showEditModal(false);
          this.refresh();
        }
      });
    } else {
      dispatch({
        type: 'currency/addCurrency',
        payload: {
          ...editRowData,
          ...fieldsValue,
          currencyCode: lodash.trim(fieldsValue.currencyCode),
        },
      }).then(response => {
        if (response) {
          notification.success();
          this.showEditModal(false);
          this.refresh();
        }
      });
    }
  }

  /**
   * 查询数据
   * @param {Object} pageData 页面信息数据
   */
  @Bind()
  fetchData(pageData = {}) {
    const { form, dispatch } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        dispatch({
          type: 'currency/fetchCurrencyList',
          payload: {
            ...fieldsValue.option,
            countryName: fieldsValue.countryName,
            page: pageData,
          },
        });
      }
    });
  }

  /**
   * 点击查询按钮事件
   */
  @Bind()
  queryValue() {
    const data = {
      page: 0,
    };
    this.fetchData(data);
  }

  /**
   * 控制明细弹框的显示隐藏，并且查询明细数据
   * @param {boolean} flag  显/隐标记
   * @param {Object} record 行数据
   */
  @Bind()
  handleDetail(flag, record) {
    const { dispatch } = this.props;
    this.setState({
      modalVisible: flag,
    });
    if (flag) {
      dispatch({
        type: 'currency/fetchDetail',
        payload: {
          currencyId: record.currencyId,
        },
      }).then(res => {
        if (res) {
          this.setState({ editRowData: res });
        }
      });
    }
  }

  /**
   * 刷新数据
   */
  @Bind()
  refresh() {
    const {
      currency: { data = {} },
    } = this.props;
    this.fetchData(data.pagination);
  }

  /**
   * 组件挂载后执行方法
   */
  componentDidMount() {
    this.fetchData({});
  }

  /**
   * 分页切换事件
   */
  @Bind()
  handleStandardTableChange(pagination = {}) {
    this.fetchData(pagination);
  }

  /**
   * 表单重置
   */
  handleFormReset = () => {
    const { form } = this.props;
    form.resetFields();
  };

  /**
   * 渲染查询结构
   * @returns
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    const queryArray = [
      {
        queryLabel: intl.get('hpfm.currency.model.currency.currencyCode').d('币种代码'),
        queryName: 'currencyCode',
        inputProps: {
          typeCase: 'upper',
          trim: true,
          inputChinese: false,
        },
      },
      {
        queryLabel: intl.get('hpfm.currency.model.currency.currencyName').d('币种名称'),
        queryName: 'currencyName',
      },
    ];
    return (
      <Form>
        <Row type="flex" align="bottom" gutter={24}>
          <Col span={6}>
            <FormItem>
              {getFieldDecorator('option')(<OptionInput queryArray={queryArray} />)}
            </FormItem>
          </Col>
          <Col span={6}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.currency.model.currency.countryName').d('国家/地区')}
            >
              {getFieldDecorator('countryName')(<Input />)}
            </FormItem>
          </Col>
          <Col span={6}>
            <FormItem>
              <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" onClick={() => this.queryValue()} htmlType="submit">
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 渲染方法
   * @returns
   */
  render() {
    const {
      detailLoading = false,
      currency: { data = {} },
      match,
      fetchLoading,
      updateLoading,
      addLoading,
    } = this.props;
    const { modalVisible, editRowData, isCreateFlag } = this.state;
    const columns = [
      {
        title: intl.get('hpfm.currency.model.currency.currencyCode').d('币种代码'),
        dataIndex: 'currencyCode',
        width: 200,
      },
      {
        title: intl.get('hpfm.currency.model.currency.currencyName').d('币种名称'),
        dataIndex: 'currencyName',
      },
      {
        title: intl.get('hpfm.currency.model.currency.countryName').d('国家/地区'),
        dataIndex: 'countryName',
        width: 200,
      },
      {
        title: intl.get('hpfm.currency.model.currency.financialPrecision').d('财务精度'),
        dataIndex: 'financialPrecision',
        width: 100,
      },
      {
        title: intl.get('hpfm.currency.model.currency.defaultPrecision').d('精度'),
        dataIndex: 'defaultPrecision',
        width: 100,
      },
      {
        title: intl.get('hpfm.currency.model.currency.currencySymbol').d('货币符号'),
        dataIndex: 'currencySymbol',
        width: 90,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 100,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        fixed: 'right',
        width: 60,
        render: (_, record) => {
          const operators = [
            {
              key: 'detail',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '币种定义-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleDetail(true, record);
                  }}
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

    const parentMethods = {
      isCreateFlag,
      onOk: this.handleAdd,
      onCancel: () => this.showEditModal(false, false),
    };

    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.currency.view.message.title.list').d('币种定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '币种定义-新建',
              },
            ]}
            onClick={() => this.showEditModal(true, true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">{this.renderForm()}</div>
          <Table
            bordered
            loading={fetchLoading}
            rowKey="currencyId"
            dataSource={data.list}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={data.pagination}
            onChange={this.handleStandardTableChange}
          />
          <CurrencyDrawer
            initLoading={detailLoading}
            loading={updateLoading || addLoading}
            modalVisible={modalVisible}
            editRowData={editRowData}
            {...parentMethods}
          />
        </Content>
      </React.Fragment>
    );
  }
}
