/**
 * producerConfig - 数据消息生产消费配置
 * @date: 2019-4-15
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import { Button } from 'hzero-ui';

import { Header, Content } from 'components/Page';
import notification from 'utils/notification';
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 数据生产消费配置
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} ProducerConfig - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ producerConfig, loading }) => ({
  producerConfig,
  loading:
    loading.effects['producerConfig/fetchProducerList'] ||
    loading.effects['producerConfig/deleteProducer'],
  saving: loading.effects['producerConfig/saveProducer'],
}))
@formatterCollections({ code: 'hdtt.producerConfig' })
export default class ProducerConfig extends PureComponent {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      producerConfig: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.props.dispatch({ type: 'producerConfig/fetchInitStatus' });
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    fieldValues.processTimeFrom =
      fieldValues.processTimeFrom && fieldValues.processTimeFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.processTimeTo =
      fieldValues.processTimeTo && fieldValues.processTimeTo.format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'producerConfig/fetchProducerList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 删除生产消费配置
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'producerConfig/deleteProducer',
      payload: record,
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  /**
   * 新增配置
   */
  @Bind()
  handleAddConfig() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hdtt/producer-config/create`,
      })
    );
  }

  /**
   * 编辑配置
   *@param {number} producerConfigId - 生产配置Id
   */
  @Bind()
  handleEdit(producerConfigId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hdtt/producer-config/detail/${producerConfigId}`,
      })
    );
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
      producerConfig: { pagination, producerList, initStatus = [] },
      loading,
    } = this.props;
    const isTenant = isTenantRoleLevel();
    const filterProps = {
      isTenant,
      initStatus,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      isTenant,
      pagination,
      loading,
      dataSource: producerList,
      onEdit: this.handleEdit,
      onChange: this.handleSearch,
      onDelete: this.handleDelete,
    };

    return (
      <>
        <Header
          title={intl.get(`hdtt.producerConfig.view.message.title`).d('数据消息生产消费配置')}
        >
          <Button icon="plus" type="primary" onClick={this.handleAddConfig}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
