/**
 * Period - 期间定义（平台级）
 * @date: 2018-7-10
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component, Fragment } from 'react';
import { Button, Tabs } from 'hzero-ui';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';
import { Bind, Debounce } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';
import { Content, Header } from 'components/Page';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DEBOUNCE_TIME } from 'utils/constants';
import {
  addItemToPagination,
  delItemToPagination,
  filterNullValueObject,
  getDateFormat,
  getEditTableData,
} from 'utils/utils';
import FilterHeader from './FilterHeader';
import TableHeader from './TableHeader';
import PeriodCreate from './PeriodCreate';
import FilterLine from './FilterLine';
import TableLine from './TableLine';

const contentStyle = {
  paddingTop: 0,
};

/**
 * 期间定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {!Object} period - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ period, loading }) => ({
  period,
  searchLoading: loading.effects['period/searchPeriodHeader'],
  savePeriodLoading: loading.effects['period/savePeriod'],
  saveLoading: loading.effects['period/savePeriodHeader'],
  searchLineLoading: loading.effects['period/searchPeriodLine'],
  fetchPeriodLoading: loading.effects['period/searchPeriodRule'],
}))
@formatterCollections({ code: ['hpfm.period'] })
export default class Period extends Component {
  lineForm;

  headerForm;

  /**
   * state初始化
   */
  constructor(props) {
    super(props);
    this.state = {
      periodItem: '',
      dateFormat: getDateFormat(),
    };
  }

  /**
   * componentDidMount
   * render后加载页面数据
   */
  componentDidMount() {
    this.handleSearchPeriodHeader();
    this.handleSearchPeriodLine();
  }

  /**
   * 会计期查询
   * @param {Object} fields - 查询参数
   * @param {object} fields.page - 分页参数
   * @param {String} fields.periodSetName - 会计期名称
   * @param {String} fields.periodSetCode - 会计期名称
   */
  @Bind()
  handleSearchPeriodHeader(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.headerForm)
      ? {}
      : filterNullValueObject(this.headerForm.getFieldsValue());
    dispatch({
      type: 'period/searchPeriodHeader',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 期间查询
   * @param {Object} fields - 查询参数
   * @param {Object} fields.page- 分页参数
   * @param {String} fields.periodSetCode - 会计期代码
   * @param {String} fields.periodName - 期间
   * @param {String} fields.periodYear - 年
   */
  @Bind()
  handleSearchPeriodLine(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.lineForm)
      ? {}
      : filterNullValueObject(this.lineForm.getFieldsValue());
    dispatch({
      type: 'period/searchPeriodLine',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 获取FilterForm中form对象
   * @param {object} ref - FilterForm组件
   */
  @Bind()
  handleBindHeaderRef(ref = {}) {
    this.headerForm = (ref.props || {}).form;
  }

  /**
   * 获取FilterForm中form对象
   * @param {object} ref - FilterForm组件
   */
  @Bind()
  handleBindLineRef(ref = {}) {
    this.lineForm = (ref.props || {}).form;
  }

  /**
   * 添加 - 会计期定义
   */
  @Bind()
  @Debounce(DEBOUNCE_TIME)
  handleAddPeriodHeader() {
    const { dispatch, period } = this.props;
    const { list = [], pagination = {} } = period.periodHeader;
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...period.periodHeader,
          list: [
            {
              periodSetId: uuidv4(),
              periodSetCode: '',
              periodSetName: '',
              enabledFlag: 1,
              periodTotalCount: 0,
              _status: 'create', // 新建标记
            },
            ...list,
          ],
          pagination: addItemToPagination(list.length, pagination),
        },
      },
    });
  }

  /**
   * 保存：新增行保存、编辑行保存
   * 处于编辑状态的行才可进行保存
   */
  @Bind()
  handleSavePeriodHeader() {
    const { dispatch, period } = this.props;
    const { list = [], pagination = {} } = period.periodHeader;
    const params = getEditTableData(list, ['periodSetId']);
    if (Array.isArray(params) && params.length !== 0) {
      dispatch({
        type: 'period/savePeriodHeader',
        payload: {
          saveData: params || [],
        },
      }).then(res => {
        if (res) {
          notification.success();
          this.handleSearchPeriodHeader(pagination);
        }
      });
    }
  }

  /**
   * 清除新增会计期
   * @param {Object} record  操作对象
   */
  @Bind()
  handleCleanHeader(record) {
    const { dispatch, period } = this.props;
    const { list = [], pagination = {} } = period.periodHeader;
    const newList = list.filter(item => item.periodSetId !== record.periodSetId);
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...period.periodHeader,
          list: [...newList],
          pagination: delItemToPagination(list.length, pagination),
        },
      },
    });
  }

  /**
   * 变更编辑状态
   * @param {Object} record 操作对象
   * @param {Boolean} flag 可编辑标记
   */
  @Bind()
  handleChangeEditable(record, flag) {
    const { dispatch, period } = this.props;
    const { list = [] } = period.periodHeader;
    const newList = list.map(item =>
      item.periodSetId === record.periodSetId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...period.periodHeader,
          list: [...newList],
        },
      },
    });
  }

  /**
   * 会计期的期间维护
   * @param {Object} record 操作对象
   */
  @Bind()
  handleCreateRule(record) {
    const {
      dispatch,
      period: { periodHeader },
    } = this.props;
    if (record._status === 'update') {
      notification.warning({
        message: intl.get('hpfm.period.view.message.edit').d('会计期处于编辑状态'),
      });
      return;
    }
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...periodHeader,
          periodData: [],
        },
      },
    });
    dispatch({
      type: 'period/searchPeriodRule',
      payload: {
        periodSetId: record.periodSetId,
        periodHeader,
      },
    });
    this.setState({ periodItem: record, ruleModalVisible: true });
  }

  /**
   * 关闭期间维护弹窗
   */
  @Bind()
  handleCloseRuleModal() {
    this.setState({ ruleModalVisible: false });
  }

  /**
   * 期间维护弹窗-添加按钮
   */
  @Bind()
  handleAddPeriod() {
    const {
      dispatch,
      period: { periodHeader },
    } = this.props;
    const { periodData } = periodHeader;
    const { periodItem } = this.state;
    const newItem = {
      periodId: uuidv4(),
      periodSetId: periodItem.periodSetId,
      periodYear: '',
      periodName: '',
      startDate: '',
      endDate: '',
      orderSeq: 0,
      periodQuarter: '',
      enabledFlag: 1,
      _status: 'create',
    };
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...periodHeader,
          periodData: [newItem, ...periodData],
        },
      },
    });
  }

  /**
   * 清除期间维护弹窗中新增的行数据
   * @param {Object} record 期间维护行数据
   */
  @Bind()
  handleCleanLine(record) {
    const { dispatch, period } = this.props;
    const { periodData } = period.periodHeader;
    const newPeriodData = periodData.filter(item => item.periodId !== record.periodId);
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...period.periodHeader,
          periodData: [...newPeriodData],
        },
      },
    });
  }

  /**
   * 更改期间维护弹窗中的行编辑状态
   * @param {Objet} record 期间维护行数据
   * @param {Boolean} flag 编辑标记
   */
  @Bind()
  handleChangeEditFlag(record, flag) {
    const { dispatch, period } = this.props;
    const { periodData } = period.periodHeader;
    const newPeriodData = periodData.map(item =>
      item.periodId === record.periodId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    dispatch({
      type: 'period/updateState',
      payload: {
        periodHeader: {
          ...period.periodHeader,
          periodData: [...newPeriodData],
        },
      },
    });
  }

  /**
   * 期间维护弹窗-保存
   * @param {Array} data 保存数据列表
   */
  @Bind()
  handleSavePeriod(data) {
    const {
      dispatch,
      period: { periodHeader },
    } = this.props;
    const { periodItem } = this.state;
    dispatch({
      type: 'period/savePeriod',
      payload: {
        data,
        periodHeader,
        periodSetId: periodItem.periodSetId,
      },
    }).then(res => {
      if (res) {
        notification.success();
        dispatch({
          type: 'period/updateState',
          payload: {
            periodHeader: {
              ...periodHeader,
              periodData: [...res],
            },
          },
        });
      }
    });
  }

  /**
   * 切换页面Tab
   * @param {String} activeKey 当前激活tab面板的 key
   */
  @Bind()
  handleChangeTag(activeKey) {
    if (activeKey === 'define') {
      this.setState({ display: 'block' });
    } else {
      this.setState({ display: 'none' });
    }
  }

  getSaveBtnDisabled() {
    const {
      period: { periodHeader: { list = [] } = {} },
    } = this.props;
    return !(list || []).some(record => record._status === 'create' || record._status === 'update');
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      period,
      match,
      searchLoading,
      saveLoading,
      savePeriodLoading,
      fetchPeriodLoading,
      searchLineLoading,
    } = this.props;
    const { periodHeader, periodLine } = period;
    const { display, dateFormat, ruleModalVisible = false } = this.state;

    const filterHeader = {
      onSearch: this.handleSearchPeriodHeader,
      onRef: this.handleBindHeaderRef,
    };
    const listHeader = {
      match,
      loading: searchLoading,
      pagination: periodHeader.pagination,
      dataSource: periodHeader.list,
      onCleanLine: this.handleCleanHeader,
      onChangeFlag: this.handleChangeEditable,
      onCreateRule: this.handleCreateRule,
      onChange: this.handleSearchPeriodHeader,
    };
    const filterLine = {
      onSearch: this.handleSearchPeriodLine,
      onRef: this.handleBindLineRef,
    };
    const listLine = {
      loading: searchLineLoading,
      pagination: periodLine.pagination,
      dataSource: periodLine.list,
      onChange: this.handleSearchPeriodLine,
    };
    const periodProps = {
      dateFormat,
      match,
      savePeriodLoading,
      fetchPeriodLoading,
      dataSource: periodHeader.periodData,
      visible: ruleModalVisible,
      onCancel: this.handleCloseRuleModal,
      onAddPeriod: this.handleAddPeriod,
      onCleanLine: this.handleCleanLine,
      onChangeFlag: this.handleChangeEditFlag,
      onSave: this.handleSavePeriod,
    };
    return (
      <Fragment>
        <Header title={intl.get('hpfm.period.view.message.tab.define').d('期间定义')}>
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSavePeriodHeader}
            style={{ display }}
            loading={saveLoading}
            disabled={searchLoading || this.getSaveBtnDisabled()}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '期间定义-新建',
              },
            ]}
            onClick={this.handleAddPeriodHeader}
            style={{ display }}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content style={contentStyle}>
          <Tabs defaultActiveKey="define" onChange={this.handleChangeTag} animated={false}>
            <Tabs.TabPane
              tab={intl.get('hpfm.period.view.message.tab.define').d('期间定义')}
              key="define"
            >
              <FilterHeader {...filterHeader} />
              <TableHeader {...listHeader} />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={intl.get('hpfm.period.view.message.tab.search').d('期间查询')}
              key="search"
            >
              <FilterLine {...filterLine} />
              <TableLine {...listLine} />
            </Tabs.TabPane>
          </Tabs>
        </Content>
        {ruleModalVisible && <PeriodCreate {...periodProps} />}
      </Fragment>
    );
  }
}
