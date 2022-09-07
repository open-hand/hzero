/**
 * startByTask - 参与流程
 * @date: 2018-8-31
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Modal } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import moment from 'moment';

import { Content, Header } from 'components/Page';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';
import { DATETIME_MAX, DATETIME_MIN } from 'utils/constants';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

@formatterCollections({ code: ['hwfp.startByTask', 'hwfp.common'] })
@connect(({ startByTask, loading }) => ({
  startByTask,
  fetchListLoading: loading.effects['startByTask/fetchTaskList'],
  remindLoading: loading.effects['startByTask/taskRemind'],
  revokeLoading: loading.effects['startByTask/taskRevoke'],
  tenantId: getCurrentOrganizationId(),
}))
export default class StartByTask extends Component {
  form;

  componentDidMount() {
    const {
      startByTask: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch({ involved: true, page });
    this.props.dispatch({ type: 'startByTask/queryProcessStatus' });
  }

  /**
   * 传递表单对象
   * @param {object} ref - FilterForm对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    const {
      startedAfter,
      startedBefore,
      processInstanceId,
      processDefinitionNameLike,
      startUserName,
      ...others
    } = filterValues;

    dispatch({
      type: 'startByTask/fetchTaskList',
      payload: {
        tenantId,
        startedAfter: startedAfter ? moment(startedAfter).format(DATETIME_MIN) : null,
        startedBefore: startedBefore ? moment(startedBefore).format(DATETIME_MAX) : null,
        startUserName,
        processDefinitionNameLike,
        processInstanceId,
        page: isEmpty(fields) ? {} : fields,
        startedBy: true,
        ...others,
      },
    });
  }

  @Bind
  handleRevoke(record) {
    const { tenantId, dispatch } = this.props;
    Modal.confirm({
      title: intl.get('hwfp.common.view.message.confirm').d('确认'),
      content: intl.get('hwfp.startByTask.view.message.title.confirmBack').d(`确认撤回吗?`),
      onOk: () => {
        const params = {
          type: 'startByTask/taskRevoke',
          payload: {
            tenantId,
            id: record.encryptId,
          },
        };
        dispatch(params).then((res) => {
          if (res) {
            notification.success();
            this.handleSearch();
          }
        });
      },
    });
  }

  @Bind
  handleRemind(record) {
    const { tenantId, dispatch } = this.props;
    Modal.confirm({
      title: intl.get('hwfp.common.view.message.confirm').d('确认'),
      content: intl.get('hwfp.startByTask.view.message.title.confirmRemind').d(`确认催办吗?`),
      onOk: () => {
        const params = {
          type: 'startByTask/taskRemind',
          payload: {
            tenantId,
            id: record.encryptId,
          },
        };
        dispatch(params).then((res) => {
          if (res) {
            notification.success();
            this.handleSearch();
          }
        });
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchListLoading,
      revokeLoading = false,
      remindLoading = false,
      startByTask: { list, pagination, processStatus = [] },
    } = this.props;
    const filterProps = {
      processStatus,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      processStatus,
      dataSource: list,
      revokeLoading,
      remindLoading,
      loading: fetchListLoading,
      onChange: this.handleSearch,
      onRevoke: this.handleRevoke,
      onRemind: this.handleRemind,
    };
    return (
      <>
        <Header title={intl.get('hwfp.startByTask.view.message.title').d('我发起的流程')} />
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
