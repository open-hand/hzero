/*
 * @Description: 路由配置
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-05-18 14:31:10
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { Table, Modal, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import { getResponse } from 'utils/utils';
import { RouterConfigDS, matchRuleDS } from '@/stores/AlertAdvancedDS';
import EditModal from './EditModal';

const modalKey = Modal.key();

export default class RouterConfig extends Component {
  constructor(props) {
    super(props);
    const matchRuleObj = matchRuleDS();
    this.RouterConfigDS = new DataSet({
      ...RouterConfigDS(),
      autoQuery: false,
      children: {
        alertMatchList: new DataSet({
          ...matchRuleObj,
        }),
      },
    });
  }

  /**
   * 删除分类
   */
  @Bind()
  async handleDelete(record) {
    const res = getResponse(await this.props.RouterConfigDS.delete([record]));
    if (res) this.props.RouterConfigDS.query();
  }

  get columns() {
    const { path } = this.props;
    return [
      { name: 'alertRouteCode' },
      { name: 'remark' },
      { name: 'sendConfigName' },
      { name: 'receiverGroupName' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          return (
            <span className="action-link">
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '告警路由规则配置-编辑',
                  },
                ]}
                onClick={() => this.handleOpenModal(record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '告警路由规则配置-删除',
                  },
                ]}
                onClick={() => this.handleDelete(record)}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '告警路由规则配置-新建子路由',
                  },
                ]}
                onClick={() => this.handleRouterConfigModal(record, 'sub')}
              >
                {intl.get('halt.alertAdvanced.view.button.route.create').d('新建子路由')}
              </ButtonPermission>
            </span>
          );
        },
        lock: 'right',
      },
    ];
  }

  /**
   * 编辑弹框
   */
  @Bind
  async handleOpenModal(record) {
    const { alertRouteId } = record.toData();
    this.RouterConfigDS.queryParameter = {
      alertRouteId,
    };
    await this.RouterConfigDS.query();
    const modalPropertys = {
      title: intl.get('halt.alertAdvanced.view.title.alertAdvanced.RC.edit').d('编辑路由规则'),
      drawer: true,
      closable: true,
      style: {
        width: 700,
      },
      key: modalKey,
      children: <EditModal record={record} RouterConfigDS={this.RouterConfigDS} />,
      onCancel: () => this.RouterConfigDS.reset(),
      onClose: () => this.RouterConfigDS.reset(),
      onOk: async () => {
        const res = await this.RouterConfigDS.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        } else if (!isEmpty(res) && res.success) {
          this.props.RouterConfigDS.query();
        } else if (res === undefined) {
          notification.warning({
            message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
          });
          return false;
        } else if (res === false) {
          notification.error({
            message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
          });
          return false;
        } else {
          return false;
        }
      },
    };
    Modal.open(modalPropertys);
  }

  /**
   * 新建路由配置弹框
   */
  @Bind
  handleRouterConfigModal(record) {
    const { alertRouteId } = record.toData();
    this.RouterConfigDS.create({
      parentId: alertRouteId,
    });
    const modalPropertys = {
      title: intl.get('halt.alertAdvanced.view.title.alertAdvanced.RC.create').d('创建路由规则'),
      drawer: true,
      closable: true,
      style: {
        width: 700,
      },
      key: modalKey,
      children: <EditModal RouterConfigDS={this.RouterConfigDS} />,
      onCancel: () => this.RouterConfigDS.reset(),
      onClose: () => this.RouterConfigDS.reset(),
      onOk: async () => {
        const { alertMatchList } = this.RouterConfigDS.current.toData();
        if (alertMatchList.length === 0) {
          notification.warning({
            message: intl
              .get('halt.common.validation.messsage.alertMatchList.notNull')
              .d('匹配条件不能为空'),
          });
          return false;
        }
        const res = await this.RouterConfigDS.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        } else if (!isEmpty(res) && res.success) {
          this.props.RouterConfigDS.query();
        } else if (res === undefined) {
          notification.warning({
            message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
          });
          return false;
        } else if (res === false) {
          notification.error({
            message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
          });
          return false;
        } else {
          return false;
        }
      },
    };
    Modal.open(modalPropertys);
  }

  render() {
    return (
      <Table
        pristine
        mode="tree"
        dataSet={this.props.RouterConfigDS}
        queryBar="none"
        columns={this.columns}
      />
    );
  }
}
