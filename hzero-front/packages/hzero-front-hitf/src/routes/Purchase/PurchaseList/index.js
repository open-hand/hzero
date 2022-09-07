/**
 * PurchaseList - 购买列表
 * @date: 2020-2-24
 * @author fengwanjun<wanjun.feng@hand-china.com>
 * @creationDate 2020-2-21
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import { Header, Content } from 'components/Page';
import { Table, DataSet, Modal } from 'choerodon-ui/pro';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import axios from 'axios';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import ChargeRuleLineDS from '../../../stores/ChargeRule/ChargeRuleLineDS';

const organizationId = getCurrentOrganizationId();

@formatterCollections({ code: ['hitf.chargeRule'] })
export default class PurchaseList extends React.Component {
  // 购买列表数据源
  tableDS = new DataSet({ ...ChargeRuleLineDS() });

  componentDidMount() {
    const { tableDS, match } = this.props;
    if (!tableDS) {
      // 页面控制
      if (match && match.params) {
        // 设置查询参数
        this.tableDS.queryParameter.ruleHeaderId = this.props.match.params.chargeRuleId;
      } else {
        // Modal控制
        // 设置查询参数
        this.tableDS.queryParameter.ruleHeaderId = this.props.data.chargeRuleId;
      }
      this.tableDS.query();
    }
  }

  /**
   * 购买
   * @param record
   */
  @Bind()
  purchase(record) {
    const { closeModal } = this.props;
    Modal.confirm({
      title: intl.get('hitf.purchase.view.meaasge.confirm.purchase').d('确定购买？'),
      onOk: async () => {
        const data = {
          ...this.props.data,
          ...record.data,
          chargeRuleLineId: record.get('ruleLineId'),
          totalCount: record.get('constantValue'),
        };
        const url = isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/user-purchases`
          : `${HZERO_HITF}/v1/user-purchases`;
        try {
          const res = await axios.post(url, data);
          if (res && res.failed) {
            // intl.get('hitf.purchase.view.meaasge.purchase.failed').d('购买失败')
            notification.error({
              message: res.message,
            });
          } else {
            notification.success({
              message: intl.get('hitf.purchase.view.meaasge.purchase.success').d('购买成功'),
            });
            closeModal(this.props.data, res.billNum);
          }
        } catch (err) {
          notification.error({
            message: intl
              .get('hitf.purchase.view.meaasge.purchase.wait')
              .d('购买失败，请稍后再试。'),
          });
        }
      },
    });
  }

  render() {
    const { match, data } = this.props;
    let basePath = '';
    if (match && match.params) {
      basePath = match.path.substring(0, match.path.indexOf('/rule'));
    }
    return (
      <>
        {match && match.params && (
          <Header
            title={intl.get('hitf.purchase.view.message.title.purchaseList').d('购买列表')}
            backPath={`${basePath}/list`}
          />
        )}
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={[
              {
                header: intl.get('hitf.purchase.model.purchaseList.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
              },
              {
                header: intl.get('hitf.purchase.model.purchaseList.constantValue').d('数量'),
                name: 'constantValue',
              },
              {
                header: intl.get('hitf.purchase.model.purchaseList.chargeUomCode').d('单位'),
                renderer: () => data.chargeUomMeaning,
              },
              {
                name: 'price',
                header: intl.get('hitf.purchase.model.purchaseList.price').d('金额'),
              },
              {
                header: intl.get('hzero.common.button.action').d('操作'),
                lock: 'right',
                align: 'center',
                width: 200,
                renderer: ({ record }) =>
                  record.get('userPurchaseId') ? (
                    intl.get('hitf.purchase.view.meaasge.purchased').d('已购买')
                  ) : (
                    <span className="action-link">
                      <a onClick={() => this.purchase(record)}>
                        {intl.get('hitf.purchase.view.message.operation.purchase').d('购买')}
                      </a>
                    </span>
                  ),
              },
            ]}
            queryBar="none"
          />
        </Content>
      </>
    );
  }
}
