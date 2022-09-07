/**
 * Detail - 数据变更审计配置详情页
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Button, Card } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import querystring from 'querystring';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getEditTableData, isTenantRoleLevel } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';
import { Header, Content } from 'components/Page';
import DetailForm from './DetailForm';
import DetailTable from './DetailTable';

const isTenant = isTenantRoleLevel();

/**
 * 数据变更审计配置详情
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} dataAuditConfig - 数据源
 * @reactProps {boolean} lineDetailLoading - 表单数据加载是否完成
 * @reactProps {boolean} loading - 表格数据加载是否完成
 * @reactProps {boolean} updateLoading - 保存加载标志
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ dataAuditConfig, loading }) => ({
  dataAuditConfig,
  loading:
    loading.effects['dataAuditConfig/fetchDetailList'] ||
    loading.effects['dataAuditConfig/enableDetailList'] ||
    loading.effects['dataAuditConfig/updateDetailList'],
  updateLoading: loading.effects['dataAuditConfig/updateDetailList'],
  lineDetailLoading: loading.effects['dataAuditConfig/fetchLine'],
}))
@formatterCollections({
  code: ['hmnt.dataAuditConfig'],
})
export default class Detail extends Component {
  state = {
    isHandling: false,
  };

  ref = React.createRef();

  componentDidMount() {
    const {
      dataAuditConfig: { detailPage = {} },
    } = this.props;
    this.handleSearchLine();
    this.handleSearch(detailPage);
    this.props.dispatch({ type: 'dataAuditConfig/fetchDisplayTypes' });
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataAuditConfig/updateState',
      payload: {
        detailList: [],
        detailPage: {},
      },
    });
  }

  /**
   * 查询列表行的详情
   */
  @Bind()
  handleSearchLine() {
    const {
      dispatch,
      location: { search },
    } = this.props;
    const params = querystring.parse(search.substring(1));
    dispatch({
      type: 'dataAuditConfig/fetchLine',
      payload: {
        auditDataConfigId: params.id,
      },
    });
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const {
      dispatch,
      location: { search },
      loading = false,
    } = this.props;
    if (loading) return;
    const params = querystring.parse(search.substring(1));
    const payload = {
      page: isEmpty(fields) ? {} : fields,
      auditDataConfigId: params.id,
    };
    if (!isTenant) {
      payload.tenantId = params.tenantId;
    }
    dispatch({
      type: 'dataAuditConfig/fetchDetailList',
      payload,
    });
  }

  /**
   * 编辑详情
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEdit(record, flag) {
    const {
      dispatch,
      dataAuditConfig: { detailList },
    } = this.props;
    const newList = detailList.map(item =>
      item.auditDataConfigLineId === record.auditDataConfigLineId
        ? { ...item, _status: flag ? 'update' : '' }
        : item
    );
    dispatch({
      type: 'dataAuditConfig/updateState',
      payload: {
        detailList: [...newList],
      },
    });
  }

  /**
   * 保存详情列表
   */
  @Bind()
  handleSaveDetailList() {
    const {
      dispatch,
      dataAuditConfig: { detailList },
      updateLoading,
      loading,
    } = this.props;
    if (loading || updateLoading) return;
    const nextConfigList = getEditTableData(detailList, ['_status']);
    if (Array.isArray(nextConfigList) && nextConfigList.length !== 0) {
      dispatch({
        type: 'dataAuditConfig/updateDetailList',
        payload: nextConfigList,
      }).then(res => {
        if (res) {
          notification.success();
          this.handleSearch();
        }
      });
    } else {
      this.ref.current.forceUpdate();
    }
  }

  /**
   * 启停用审计行
   * @param {Object} record - 表格行数据
   */
  @Bind()
  handleEnable(record) {
    const {
      dispatch,
      location: { search },
    } = this.props;
    const params = querystring.parse(search.substring(1));
    const payload = {
      ...record,
      auditFlag: record.auditFlag === 0 ? 1 : 0,
    };
    if (!isTenant) {
      payload.tenantId = +params.tenantId;
    }
    this.setState(
      {
        isHandling: true,
      },
      () => {
        dispatch({
          type: 'dataAuditConfig/enableDetailList',
          payload,
        }).then(res => {
          this.setState({ isHandling: false });
          if (res) {
            notification.success();
            this.handleSearch();
          }
        });
      }
    );
  }

  render() {
    const {
      dataAuditConfig: { detailList = [], displayTypes = [], lineDetail = {} },
      loading,
      lineDetailLoading,
      updateLoading = false,
    } = this.props;
    const { isHandling } = this.state;
    const formProps = { dataSource: lineDetail };
    const listProps = {
      detailList,
      loading,
      isHandling,
      displayTypes,
      ref: this.ref,
      onEdit: this.handleEdit,
      onEnable: this.handleEnable,
    };
    return (
      <>
        <Header
          title={intl.get('hmnt.dataAuditConfig.view.message.title.detail').d('数据审计配置详情')}
          backPath="/hmnt/data-audit-config/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveDetailList}
            loading={updateLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Card
            key="data-group-head"
            bordered={false}
            title={
              <h3>{intl.get('hmnt.dataAuditConfig.view.message.title.head').d('配置信息')}</h3>
            }
            className={DETAIL_CARD_CLASSNAME}
            loading={lineDetailLoading}
          >
            <DetailForm {...formProps} />
          </Card>
          <Card
            key="config-detail"
            bordered={false}
            title={
              <h3>{intl.get('hmnt.dataAuditConfig.view.message.title.line').d('配置详情')}</h3>
            }
            className={DETAIL_CARD_TABLE_CLASSNAME}
          >
            <DetailTable {...listProps} />
          </Card>
        </Content>
      </>
    );
  }
}
