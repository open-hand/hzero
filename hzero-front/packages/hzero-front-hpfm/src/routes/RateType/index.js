/**
 * index.js - 平台级汇率类型定义
 * @date: 2018-10-27
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Table } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';

import CreateForm from './ListForm';
import FilterForm from './FilterForm';

@connect(({ rateType, loading }) => ({
  rateType,
  loading: loading.effects['rateType/queryRateType'],
  saving: loading.effects['rateType/saveRateType'],
  loadingDetail: loading.effects['rateType/queryRateTypeDetail'],
}))
@formatterCollections({
  code: 'hpfm.rateType',
})
export default class RateType extends Component {
  constructor(props) {
    super(props);
    this.filterForm = {}; // 汇率类型查询表单对象
    this.state = {
      modalVisible: false,
      editValue: {},
    };
  }

  componentDidMount() {
    const {
      dispatch,
      rateType: { pagination = {} },
    } = this.props;

    dispatch({
      type: 'rateType/queryRateTypeMethod',
      payload: 'HPFM.EXCHANGE_RATE_METHOD',
    });

    this.handleSearch(pagination);
  }

  /**
   * 查询汇率类型列表
   * @param {Object} pagination - 查询参数
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch } = this.props;
    const values = this.filterForm.props.form.getFieldsValue();

    dispatch({
      type: 'rateType/queryRateType',
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
   * 保存汇率类型
   * @param {Object} fieldsValue - 汇率类型表单数据
   */
  @Bind()
  handleAdd(fieldsValue) {
    const {
      dispatch,
      rateType: { pagination = {} },
    } = this.props;
    const { editValue } = this.state;
    // TODO: 校验表单
    dispatch({
      type: 'rateType/saveRateType',
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
   * 显示侧栏，设定当前编辑数据
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
   * 显示侧边栏
   */
  @Bind()
  showModal() {
    this.handleModalVisible(true);
  }

  /**
   * 隐藏侧边栏
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
   * 侧边栏显示隐藏控制
   * @param {Boolean} flag - 侧边栏显示参数
   */
  handleModalVisible(flag) {
    if (flag === false && this.createForm) {
      this.createForm.resetForm();
    }
    this.setState({
      modalVisible: !!flag,
    });
  }

  render() {
    const {
      rateType: { list = {}, rateMethodList = [], pagination = {} },
      loading,
      saving,
      match,
    } = this.props;
    const { editValue } = this.state;
    const filterProps = {
      rateMethodList,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const columns = [
      {
        title: intl.get('hpfm.rateType.model.rateType.typeCode').d('类型编码'),
        width: 200,
        dataIndex: 'typeCode',
      },
      {
        title: intl.get('hpfm.rateType.model.rateType.typeName').d('类型名称'),
        dataIndex: 'typeName',
      },
      {
        title: intl.get('hpfm.rateType.model.rateType.rateMethodCode').d('方式'),
        width: 150,
        dataIndex: 'rateMethodCode',
        render: text => {
          const ret = rateMethodList.find(m => m.value === text);
          return ret ? ret.meaning : text;
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
                      meaning: '汇率类型定义-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showEditModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];

    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.rateType.view.title.rateType').d('汇率类型定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '汇率类型定义-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <Table
            bordered
            rowKey="rateTypeId"
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
          title={intl.get('hpfm.rateType.view.title.rateType').d('汇率类型定义')}
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
