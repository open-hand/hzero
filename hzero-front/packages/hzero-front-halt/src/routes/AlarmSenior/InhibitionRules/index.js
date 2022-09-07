/*
 * @Description: 抑制规则
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-24 13:51:25
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { Table, Modal, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { InhibitionRulesDS, matchRuleDS } from '@/stores/AlertAdvancedDS';
import { Button as ButtonPermission } from 'components/Permission';

import EditModal from './EditModal';

const modalKey = Modal.key();

export default class InhibitionRules extends Component {
  constructor(props) {
    super(props);
    const matchRuleObj = matchRuleDS();
    this.InhibitionRulesDS = new DataSet({
      ...InhibitionRulesDS(),
      autoQuery: false,
      children: {
        sourceAlertMatchList: new DataSet({
          ...matchRuleObj,
        }),
        targetAlertMatchList: new DataSet({
          ...matchRuleObj,
        }),
      },
    });
  }

  get columns() {
    const { path } = this.props;
    return [
      { name: 'alertInhibitCode' },
      { name: 'alertInhibitName' },
      { name: 'remark' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        renderer: ({ record }) => {
          return (
            <span className="action-link">
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '告警抑制规则配置-编辑',
                  },
                ]}
                onClick={() => this.handleOpenModal(record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
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
    // const { InhibitionRulesDS } = this.props;
    const { alertInhibitId } = record.toData();
    this.InhibitionRulesDS.queryParameter = {
      alertInhibitId,
    };
    await this.InhibitionRulesDS.query();
    const modalPropertys = {
      title: intl
        .get('halt.alertAdvanced.view.title.alertAdvanced.inhibition.edit')
        .d('编辑抑制规则'),
      drawer: true,
      closable: true,
      style: {
        width: 700,
      },
      key: modalKey,
      children: <EditModal record={record} alarmSeniorDS={this.InhibitionRulesDS} />,
      onCancel: () => this.InhibitionRulesDS.reset(),
      onClose: () => this.InhibitionRulesDS.reset(),
      onOk: async () => {
        const res = await this.InhibitionRulesDS.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        } else if (!isEmpty(res) && res.success) {
          this.props.InhibitionRulesDS.query();
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
        dataSet={this.props.InhibitionRulesDS}
        queryFieldsLimit={3}
        columns={this.columns}
      />
    );
  }
}
