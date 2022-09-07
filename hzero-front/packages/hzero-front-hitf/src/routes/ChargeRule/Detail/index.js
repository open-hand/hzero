/**
 * ChargeRuleDetail - 计费规则详情
 * @date: 2020-2-21
 * @author fengwanjun<wanjun.feng@hand-china.com>
 * @creationDate 2020-2-21
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Table, DataSet } from 'choerodon-ui/pro';
import formatterCollections from 'utils/intl/formatterCollections';
import ChargeRuleLineDS from '../../../stores/ChargeRule/ChargeRuleLineDS';

@formatterCollections({ code: ['hitf.chargeRule'] })
export default class Detail extends React.Component {
  // 计费规则数据源
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
        this.tableDS.queryParameter.ruleHeaderId = this.props.chargeRuleId;
      }
      this.tableDS.query();
    }
  }

  render() {
    const { match } = this.props;
    let basePath = '';
    // 页面控制 返回页面URL
    if (match && match.params) {
      basePath = match.path.substring(0, match.path.indexOf('/rule'));
    }
    return (
      <>
        {match && match.params && (
          <Header
            title={intl
              .get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule')
              .d('计费规则')}
            backPath={`${basePath}/list`}
          />
        )}
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={[
              {
                header: intl.get('hitf.chargeRule.model.chargeRuleLine.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
              },
              {
                name: 'greaterThan',
                align: 'right',
              },
              {
                name: 'lessAndEquals',
                align: 'right',
              },
              {
                name: 'constantValue',
                align: 'right',
              },
              {
                name: 'price',
                align: 'right',
              },
            ]}
            queryBar="none"
          />
        </Content>
      </>
    );
  }
}
