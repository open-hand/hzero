/**
 * @since 2019-12-19
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { withRouter } from 'dva/router';
import {
  DataSet,
  Table,
  Form,
  Modal,
  NumberField,
  SelectBox,
  Select,
  TextField,
  Switch,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { uniq } from 'lodash';
import queryString from 'query-string';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import { formDS, tableDS, displayFormDS, flowLimitFormDS } from '@/stores/apiLimitDS';
import { applyAPI, flowLimit } from '@/services/apiLimitService';

import styles from './index.less';

const { Option } = Select;

@withRouter
@formatterCollections({ code: ['hadm.apiLimit'] })
export default class ApiLimit extends React.Component {
  constructor(props) {
    super(props);
    this.formDS = new DataSet(formDS());
    this.displayFormDS = new DataSet(displayFormDS());
    this.flowLimitFormDS = new DataSet(flowLimitFormDS());
    this.tableDS = new DataSet(tableDS());
  }

  get columns() {
    return [
      {
        name: 'urlPattern',
      },
      {
        name: 'timeWindowSize',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          let actions = [];
          actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.flowControl`,
                      type: 'button',
                      meaning: 'API访问控制-流量控制',
                    },
                  ]}
                  onClick={() => this.handleFlowLimit(record)}
                >
                  {intl.get('hadm.apiLimit.view.button.flowControl').d('流量控制')}
                </ButtonPermission>
              ),
              key: 'flowControl',
              len: 4,
              title: intl.get('hadm.apiLimit.view.button.flowControl').d('流量控制'),
            },
            {
              ele: (
                <a onClick={() => this.handleViewMonitor(record)}>
                  {intl.get('hadm.apiLimit.view.button.viewMonitor').d('查看监控')}
                </a>
              ),
              key: 'detail',
              len: 4,
              title: intl.get('hadm.apiLimit.view.button.viewMonitor').d('查看监控'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.delete`,
                      type: 'button',
                      meaning: 'API访问控制-删除',
                    },
                  ]}
                  onClick={() => this.deleteApi(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'left',
      },
    ];
  }

  // 流量控制
  @Bind()
  async handleFlowLimit(record) {
    const { monitorRuleId, urlPattern, timeWindowSize } = record.toData();
    this.displayFormDS.create({});
    this.flowLimitFormDS.create({});
    this.displayFormDS.create({ urlPattern, timeWindowSize });
    this.flowLimitFormDS.setQueryParameter('monitorRuleId', monitorRuleId);
    this.flowLimitFormDS.query();
    Modal.open({
      title: intl.get('hadm.apiLimit.view.title.flowControl').d('流量控制'),
      drawer: true,
      width: 520,
      children: (
        <div>
          <Form dataSet={this.displayFormDS}>
            <TextField name="urlPattern" disabled />
            <TextField name="timeWindowSize" disabled />
          </Form>
          <Form dataSet={this.flowLimitFormDS}>
            <SelectBox name="listMode">
              <Option value="BLACK">
                {intl.get('hadm.apiLimit.model.apiLimit.black').d('黑名单')}
              </Option>
              <Option value="WHITE">
                {intl.get('hadm.apiLimit.model.apiLimit.white').d('白名单')}
              </Option>
            </SelectBox>
            <TextField className={styles['input-item-style']} name="valueList" />
            <NumberField
              name="blacklistThreshold"
              step={1}
              help={intl
                .get('hadm.apiLimit.model.apiLimit.autoAddToBlacklist')
                .d('请求量超过该值,自动进入黑名单')}
              showHelp="tooltip"
              renderer={({ value }) => value}
            />
            <Switch name="enabledFlag" />
          </Form>
        </div>
      ),
      onOk: async () => {
        if (await this.flowLimitFormDS.validate()) {
          const {
            apiLimitId = '',
            blacklistThreshold = '',
            enabledFlag = '',
            listMode = '',
            objectVersionNumber = '',
            valueList = '',
            _token = '',
          } = this.flowLimitFormDS.current.toData();
          const res = await flowLimit({
            apiLimitId,
            blacklistThreshold,
            enabledFlag,
            listMode,
            monitorRuleId,
            objectVersionNumber,
            valueList: uniq(valueList),
            _token,
          });
          this.tableDS.query();
          return res;
        } else {
          return false;
        }
      },
      onCancel: () => true,
      afterClose: () => this.flowLimitFormDS.reset(),
    });
  }

  // 查看监控
  @Bind()
  handleViewMonitor(record) {
    const {
      history,
      match,
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const { urlPattern, timeWindowSize, monitorRuleId } = record.toData();
    history.push(
      match.path.indexOf('/private') === 0
        ? `/private/hadm/api-limit/detail/${monitorRuleId}?timeWindowSize=${timeWindowSize}&urlPattern=${urlPattern}&access_token=${accessToken}`
        : `/hadm/api-limit/detail/${monitorRuleId}?timeWindowSize=${timeWindowSize}&urlPattern=${urlPattern}`
    );
  }

  // 新建
  @Bind()
  handleCreate() {
    this.openModal();
  }

  // 应用
  @Bind()
  async handleApply() {
    if (this.tableDS.selected.length !== 0) {
      const apiSet = this.tableDS.selected.map((item) => item.toData().monitorRuleId).join(',');
      const res = await applyAPI(apiSet);
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      } else if (res && !res.failed) {
        notification.success({
          message: intl.get('hzero.common.notification.success').d('操作成功'),
        });
      }
    } else {
      notification.warning({
        message: intl.get('hadm.apiLimit.view.notification.chooseApi').d('请选择需要应用的API！'),
      });
    }
  }

  /**
   * 打开模态框
   */
  @Bind()
  openModal() {
    this.formDS.create({});
    Modal.open({
      title: intl.get('hzero.common.view.title.create').d('新建'),
      drawer: true,
      width: 520,
      children: (
        <Form dataSet={this.formDS}>
          <TextField name="urlPattern" showHelp="tooltip" />
          <NumberField step={1} name="timeWindowSize" />
        </Form>
      ),
      onOk: async () => {
        if (await this.formDS.validate()) {
          const res = await this.formDS.submit();
          this.tableDS.query();
          return res;
        } else {
          return false;
        }
      },
      onCancel: () => true,
      afterClose: () => this.formDS.reset(),
    });
  }

  // 删除
  @Bind()
  async deleteApi(record) {
    await this.tableDS.delete(record);
    this.tableDS.query();
  }

  render() {
    const { match } = this.props;
    return (
      <>
        <Header title={intl.get('hadm.apiManagement.view.title.apiLimit').d('API访问控制')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: 'API访问控制-新建',
              },
            ]}
            className={styles['button-style']}
            onClick={this.handleCreate}
            icon="add"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.apply`,
                type: 'button',
                meaning: 'API访问控制-应用',
              },
            ]}
            onClick={this.handleApply}
            icon="check"
          >
            {intl.get('hzero.common.button.apply').d('应用')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} columns={this.columns} />
        </Content>
      </>
    );
  }
}
