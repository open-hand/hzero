/**
 * TenantInitLog - 租户初始化处理日志
 * @date: 2019-6-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty, orderBy, groupBy } from 'lodash';
import { Content, Header } from 'components/Page';
import { filterNullValueObject } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import FilterForm from './FilterForm';
import ListTable from './ListTable';

const viewTitle = 'hiam.tenantLog.view.title';

/**
 * 租户初始化处理日志
 * @extends {Component} - React.Component
 * @reactProps {object} tenantInitLog - 数据源
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ tenantInitLog, loading }) => ({
  tenantInitLog,
  listLoading: loading.effects['tenantInitLog/fetchLog'],
  picLoading: loading.effects['tenantInitLog/fetchLogPic'],
}))
@formatterCollections({ code: ['hiam.tenantConfig', 'hiam.tenantLog'] })
export default class TenantInitLog extends Component {
  form;

  state = {
    currentLog: {},
    isShowImg: false, // 是否显示错误消息
  };

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      tenantInitLog: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleFetchIdpValue();
    this.handleSearch(page);
  }

  /**
   * 查询值集
   */
  @Bind()
  handleFetchIdpValue() {
    const { dispatch } = this.props;
    dispatch({
      type: 'tenantInitLog/queryIdpValue',
    });
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
    dispatch({
      type: 'tenantInitLog/fetchLog',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 显示图形弹窗
   * @param {string} record - 表格行数据
   */
  @Bind()
  handleOpenImgModal(record) {
    const { instanceKey } = record;
    const { dispatch } = this.props;
    this.setState(
      {
        isShowImg: true,
        currentLog: record,
      },
      () => {
        dispatch({
          type: 'tenantInitLog/fetchLogPic',
          payload: instanceKey,
        });
      }
    );
  }

  /**
   * 关闭图形弹窗
   */
  @Bind()
  handleCloseImgModal() {
    const { dispatch } = this.props;
    this.setState(
      {
        isShowImg: false,
      },
      () => {
        dispatch({
          type: 'tenantInitLog/updateState',
          payload: {
            picData: {},
          },
        });
      }
    );
  }

  render() {
    const {
      tenantInitLog: { logList, pagination, enumMap, picData },
      listLoading,
      picLoading = false,
    } = this.props;
    const { currentLog, isShowImg } = this.state;
    let imgData = {};
    if (!isEmpty(picData)) {
      imgData = groupBy(orderBy(picData, 'orderSeq'), 'processorType');
    }
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      enumMap,
    };
    const listProps = {
      isShowImg,
      dataSource: logList,
      pagination,
      picProps: {
        dataSource: imgData,
        loading: picLoading,
        type: !isEmpty(currentLog) && currentLog.initType.toLowerCase(),
      },
      loading: listLoading,
      onChange: this.handleSearch,
      onSearchPic: this.handleSearchPic,
      onOpenPic: this.handleOpenImgModal,
      onClosePic: this.handleCloseImgModal,
    };
    return (
      <>
        <Header title={intl.get(`${viewTitle}.tenant.init.log`).d('租户初始化处理日志')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
