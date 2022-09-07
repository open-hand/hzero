/*
 * module - 规则列表
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-10
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table, Button, Form, Modal, TextField, CheckBox } from 'choerodon-ui/pro';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import querystring from 'querystring';
import { openTab } from 'utils/menuTab';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import request from 'utils/request';
import { getCurrentOrganizationId } from 'utils/utils';
import { commandDownload } from '@/utils/downloadFile';
import { HZERO_HRES } from 'utils/config';
import RulesDS from '../stores/RulesDS';
import openEnterModal from '../detail/EnterModal';
import openOutModal from '../detail/OutModal';
import exportModal from '../list/ExportModal';

const modalKey = Modal.key();

export async function queryColumn(params) {
  return request(`${params.requestUrl}`, {
    method: 'POST',
    body: params.queryParams,
    responseType: 'blob',
  });
}
@connect()
@formatterCollections({ code: ['hres.rule', 'hres.common'] })
export default class ListPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentPage: 0, // 当前页码
    };
  }

  rulesDS = new DataSet({
    ...RulesDS(),
    autoQuery: true,
    queryParameter: {
      tenantId: getCurrentOrganizationId(),
    },
  });

  /**
   * 新建
   * @param {object} record - 列表行数据
   * @param {object} parameter - 点击类型
   */
  @Bind()
  handleCreate(parameter, record) {
    this.setState({
      currentPage: this.rulesDS.currentPage,
    });
    if (parameter === 'create') {
      this.rulesDS.create({
        tenantId: getCurrentOrganizationId(),
      });
    }
    Modal.open({
      key: modalKey,
      drawer: true,
      title:
        parameter === 'create'
          ? intl.get('hres.rule.view.title.create').d('新建规则')
          : intl.get('hres.rule.view.title.edit').d('编辑规则'),
      children: (
        <Form record={record || this.rulesDS.current} style={{ width: '4rem' }}>
          <TextField name="ruleCode" disabled={parameter === 'edit'} />
          <TextField name="ruleName" restrict="\S" />
          <CheckBox name="enableFlag" />
          <CheckBox name="frozenFlag" />
        </Form>
      ),
      closable: true,
      onClose: this.handleCloseModal,
      onCancel: this.handleCloseModal,
      onOk: () => this.handleOkModal(parameter),
      destroyOnClose: true,
    });
  }

  /**
   * 取消
   */
  @Bind()
  handleCloseModal() {
    this.rulesDS.reset();
  }

  /**
   * 确认
   */
  @Bind()
  async handleOkModal(type) {
    const { currentPage } = this.state;
    const isValidate = await this.rulesDS.validate(false, true);
    const isModified = this.rulesDS.isModified();
    if (!isValidate) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      this.rulesDS.reset();
    } else if (type && type === 'edit' && !isModified) {
      notification.warning({
        message: intl.get('hres.common.notification.unmodified').d('表单未做修改'),
      });
    } else {
      const res = await this.rulesDS.submit();
      if (res && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
      } else {
        this.rulesDS.query(currentPage);
        this.rulesDS.setQueryParameter('tenantId', getCurrentOrganizationId());
      }
    }
  }

  get columns() {
    return [
      { name: 'ruleCode' },
      { name: 'ruleName' },
      { name: 'enableFlag' },
      { name: 'frozenFlag' },
      {
        width: 300,
        header: intl.get('hzero.common.button.action').d('操作'),
        command: ({ record }) => this.commands(record),
        lock: 'right',
      },
    ];
  }

  /**
   * 行内操作按钮组
   */
  @Bind()
  commands(record) {
    return [
      <span className="action-link">
        <a onClick={() => openEnterModal(record.pristineData)}>
          {intl.get('hres.rule.model.rule.enter').d('入参')}
        </a>
        <a onClick={() => openOutModal(record.pristineData)}>
          {intl.get('hres.rule.model.rule.out').d('出参')}
        </a>
        <a onClick={() => this.handleCreate('edit', record)}>
          {intl.get('hzero.common.button.edit').d('编辑')}
        </a>
        <a onClick={() => this.goToFlowPage(record)}>
          {intl.get('hres.rule.model.rule.flow').d('流程')}
        </a>
        <a onClick={() => this.goToTestPage(record)}>
          {intl.get('hzero.common.button.test').d('测试')}
        </a>
        <a onClick={() => this.goToDelete(record)}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>,
    ];
  }

  /**
   * 跳转流程图
   * @param {object} record - 列表行数据
   */
  @Bind()
  goToFlowPage(record) {
    const { dispatch } = this.props;
    const pathname = `/hres/rules/flow/detail/${record.get('ruleCode')}`;
    const frozenFlag = this.rulesDS.current.get('frozenFlag');
    dispatch(
      routerRedux.push({
        pathname,
        search: querystring.stringify({
          pageType: frozenFlag === 'Y' ? 'view' : 'edit',
          refresh: true,
          ruleName: record.get('ruleName'),
        }),
      })
    );
  }

  /**
   * 跳转测试界面
   * @param {object} record - 列表行数据
   */
  @Bind()
  goToTestPage(record) {
    const pathname = `/hres/rules/test/${record.get('ruleCode')}`;
    openTab({
      key: pathname,
      title: `${intl.get('hzero.common.button.test').d('测试')}_${record.get('ruleName')}`,
      search: querystring.stringify({
        isFlow: false,
      }),
    });
  }

  /**
   * 删除单行规则
   * @param {*} record
   */
  @Bind()
  goToDelete(record) {
    this.rulesDS.delete(record);
  }

  /**
   * 导出文件
   */
  @Bind()
  handleExport() {
    const { selected } = this.rulesDS;
    const queryParams = [];
    if (selected && selected.length > 0) {
      selected.forEach((item) => {
        queryParams.push(item.toData().ruleCode);
      });
      const requestUrl = `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/config/export?tenantId=${getCurrentOrganizationId()}`;
      queryColumn({
        requestUrl,
        queryParams,
      })
        .then((blob) => {
          commandDownload(blob, `RuleEngineConf.json`);
          this.rulesDS.query();
          this.rulesDS.setQueryParameter('tenantId', getCurrentOrganizationId());
        })
        .catch((error) => {
          notification.error({
            description: error,
          });
        });
    } else {
      notification.warning({
        message: intl.get('hres.rule.view.title.least.checklist').d('请至少勾选一条数据！'),
      });
    }
  }

  render() {
    return (
      <React.Fragment>
        <Header title={intl.get('hres.rule.view.message.title.ruleManagement').d('规则管理')}>
          <Button
            key="create"
            color="primary"
            funcType="raised"
            icon="add"
            onClick={() => this.handleCreate('create', null)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
          <Button icon="get_app" onClick={exportModal}>
            {intl.get('hzero.common.button.import').d('导入')}
          </Button>
          <Button icon="file_upload" onClick={this.handleExport}>
            {intl.get('hzero.common.button.export').d('导出')}
          </Button>
        </Header>
        <Content>
          <Table dataSet={this.rulesDS} columns={this.columns} queryFieldsLimit={2} />
        </Content>
      </React.Fragment>
    );
  }
}
