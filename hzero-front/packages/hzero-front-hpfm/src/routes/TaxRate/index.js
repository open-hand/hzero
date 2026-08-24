/**
 * index.js - 平台级税率类型定义
 * @date: 2018-10-27
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Col, Row, Table } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import { open } from 'components/Modal/ModalContainer';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { Bind } from 'lodash-decorators';
import { createPagination, tableScrollWidth } from 'utils/utils';
import { enableRender, numberRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import CreateForm from './ListForm';

@connect(({ taxRate, loading }) => ({
  taxRate,
  loading: loading.effects['taxRate/queryTaxRate'],
  saving: loading.effects['taxRate/saveTaxRate'],
  loadingDetail: loading.effects['taxRate/queryTaxRateDetail'],
}))
@formatterCollections({
  code: 'hpfm.taxRate',
})
export default class TaxRate extends Component {
  createForm; // 侧栏组件内引用

  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      editValue: {},
    };
  }

  componentDidMount() {
    const {
      taxRate: { pagination },
    } = this.props;
    this.handleSearch(pagination);
  }

  /**
   * 查询平台级税率类型
   * @param {Object} pagination - 分页查询参数
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch } = this.props;
    const values = this.filterForm.props.form.getFieldsValue();

    dispatch({
      type: 'taxRate/queryTaxRate',
      payload: {
        page,
        ...values,
      },
    });
  }

  /**
   *
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.filterForm = ref;
  }

  /**
   * 新增或编辑税率
   * @param {Object} fieldsValue - 税率编辑表单数据
   */
  @Bind()
  handleAdd(fieldsValue) {
    const {
      dispatch,
      taxRate: { pagination },
    } = this.props;
    const { editValue } = this.state;
    // TODO: 校验表单

    dispatch({
      type: 'taxRate/saveTaxRate',
      payload: {
        ...editValue,
        ...fieldsValue,
      },
    }).then(response => {
      if (response) {
        this.hideModal();
        notification.success();
        this.setState({
          editValue: {},
        });
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 显示侧边栏编辑
   * @param {Object} record - 当前行数据
   */
  @Bind()
  showEditModal(record) {
    this.setState({
      editValue: record,
    });
    this.showModal();
  }

  /**
   * 显示侧栏
   */
  @Bind()
  showModal() {
    this.handleModalVisible(true);
  }

  /**
   * 隐藏侧栏
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.handleModalVisible(false);
    }
    this.setState({
      editValue: {},
    });
  }

  /**
   * 侧边栏显示隐藏参数控制
   * @param {Boolean} flag - 控制侧边栏显示参数
   */
  handleModalVisible(flag) {
    if (flag === false && this.createForm) {
      this.createForm.resetForm();
    }
    this.setState({
      modalVisible: !!flag,
    });
  }

  /**
   * 显示详情 Modal
   * @param {Object} record - 当前行数据
   */
  showDetailModal(record) {
    const { taxCode, description, taxId, taxRate } = record;
    const { dispatch, loadingDetail } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.taxRate.model.taxRate.tenantNum').d('企业集团编码'),
        dataIndex: 'tenantNum',
      },
      {
        title: intl.get('hpfm.taxRate.model.taxRate.tenantName').d('企业集团名称'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
    ];

    const params = {
      taxId,
      page: 0,
      size: 10,
    };

    const detailTableChange = pagination => {
      const detailParams = {
        taxId,
        page: pagination.current - 1, // 服务器接口从 0 开始分页
        size: pagination.pageSize,
      };

      dispatch({
        type: 'taxRate/queryTaxRateDetail',
        payload: detailParams,
      });
    };

    dispatch({
      type: 'taxRate/queryTaxRateDetail',
      payload: params,
    }).then(res => {
      if (res) {
        open({
          title: intl.get('hpfm.taxRate.view.title.quoteDetail').d('税率引用明细'),
          width: 800,
          footer: null,
          children: (
            <div>
              <Row style={{ fontSize: 14 }} gutter={24}>
                <Col span={8}>
                  {intl.get('hpfm.taxRate.model.taxRate.taxCode').d('税种代码')}:{' '}
                  <span>{taxCode}</span>
                </Col>
                <Col span={8}>
                  {intl.get('hpfm.taxRate.model.taxRate.description').d('描述')}:{' '}
                  <span>{description}</span>
                </Col>
                <Col span={8}>
                  {intl.get('hpfm.taxRate.model.taxRate.taxRate').d('税率')}: <span>{taxRate}</span>
                </Col>
              </Row>
              <Table
                bordered
                rowKey="taxRefId"
                loading={loadingDetail}
                dataSource={res.content}
                columns={columns}
                onChange={detailTableChange}
                pagination={createPagination(res)}
                style={{
                  marginTop: 10,
                }}
              />
            </div>
          ),
        });
      }
    });
  }

  render() {
    const {
      taxRate: { list = {}, rateMethodList = [], pagination = {} },
      loading,
      saving,
      match,
    } = this.props;
    const { editValue } = this.state;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const columns = [
      {
        title: intl.get('hpfm.taxRate.model.taxRate.taxCode').d('税种代码'),
        width: 200,
        align: 'left',
        dataIndex: 'taxCode',
      },
      {
        title: intl.get('hpfm.taxRate.model.taxRate.description').d('描述'),
        align: 'left',
        dataIndex: 'description',
      },
      {
        title: `${intl.get('hpfm.taxRate.model.taxRate.taxRate').d('税率')}（%）`,
        width: 100,
        dataIndex: 'taxRate',
        render: value => numberRender(value, 2, false),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        fixed: 'right',
        render: (_, record) => {
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
                      meaning: '平台级税率类型定义-编辑',
                    },
                  ]}
                  onClick={() => this.showEditModal(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];

    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.taxRate.view.title.taxRate').d('税率定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '平台级税率类型定义-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <Table
            bordered
            rowKey="taxId"
            loading={loading}
            dataSource={list.content}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            onChange={this.handleSearch}
            pagination={pagination}
          />
        </Content>
        <CreateForm
          sideBar
          destroyOnClose
          title={intl.get('hpfm.taxRate.view.title.taxRate').d('税率定义')}
          onRef={ref => {
            this.createForm = ref;
          }}
          rateMethodList={rateMethodList}
          handleAdd={this.handleAdd}
          confirmLoading={saving}
          editValue={editValue}
          modalVisible={this.state.modalVisible}
          hideModal={this.hideModal}
        />
      </React.Fragment>
    );
  }
}
