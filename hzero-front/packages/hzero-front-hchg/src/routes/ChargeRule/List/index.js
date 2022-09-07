/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/1/14
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import withProps from 'utils/withProps';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import { operatorRender, TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { chargeRuleTableDS } from '@/stores/ChargeRule/ChargeRuleDS';
import { STATUS_LIST, CHARGE_RULE_CONSTANT } from '@/constants/constants';

@withProps(
  () => {
    const tableDS = new DataSet(chargeRuleTableDS);
    return { tableDS };
  },
  { cacheState: true }
)
@formatterCollections({ code: ['hchg.chargeRule'] })
export default class ChargeRule extends React.Component {
  componentDidMount() {
    this.props.tableDS.query();
  }

  /**
   * 调整到新建/编辑明细
   */
  @Bind()
  handleGotoDetail(id, disabledFlag) {
    const { history } = this.props;
    const url = isUndefined(id) ? 'create' : `detail/${id}`;
    history.push({
      pathname: `/hchg/charge-rule/${url}`,
      state: { disabledFlag },
    });
  }

  /**
   * 发布
   */
  @Bind()
  async handleRelease(record) {
    record.set('_status', 'update');
    record.set('_type', 'release');
    await this.props.tableDS.submit();
  }

  /**
   * 取消发布
   */
  @Bind()
  async handleCancel(record) {
    record.set('_status', 'update');
    record.set('_type', 'cancel');
    await this.props.tableDS.submit();
  }

  /**
   * 删除
   * @param {Object} record 删除的行数据
   */
  async handleDelete(record) {
    const res = await this.props.tableDS.delete(record);
    if (res.success) {
      await this.props.tableDS.query();
    }
  }

  get chargeRuleColumns() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    return [
      {
        name: 'seqNumber',
        width: 70,
        align: 'center',
        renderer: ({ record }) => (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
      },
      {
        name: 'ruleNum',
        width: 150,
        renderer: ({ record }) => (
          <a onClick={() => this.handleGotoDetail(record.get('ruleHeaderId'), true)}>
            {record.get('ruleNum')}
          </a>
        ),
      },
      {
        name: 'ruleName',
      },
      {
        name: 'statusCode',
        width: 100,
        align: 'center',
        renderer: ({ value }) => TagRender(value, STATUS_LIST),
      },
      {
        name: 'paymentModelCode',
        width: 100,
      },
      {
        name: 'methodCode',
        width: 100,
      },
      {
        name: 'typeCode',
        width: 100,
      },
      {
        name: 'unitCode',
        width: 100,
      },
      {
        name: 'startDate',
        width: 130,
      },
      {
        name: 'endDate',
        width: 130,
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 160,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '计费规则配置-编辑',
                    },
                  ]}
                  onClick={() => this.handleGotoDetail(record.get('ruleHeaderId'), false)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.release`,
                      type: 'button',
                      meaning: '计费规则配置-发布',
                    },
                  ]}
                  onClick={() => this.handleRelease(record)}
                >
                  {intl.get('hzero.common.button.release').d('发布')}
                </ButtonPermission>
              ),
              key: 'release',
              len: 2,
              title: intl.get('hzero.common.button.release').d('发布'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.cancel`,
                      type: 'button',
                      meaning: '计费规则配置-取消',
                    },
                  ]}
                  onClick={() => this.handleCancel(record)}
                >
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </ButtonPermission>
              ),
              key: 'cancel',
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '计费规则配置-删除',
                    },
                  ]}
                  onClick={() => this.handleDelete(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          // 新建显示编辑、删除和发布按钮，已发布显示取消按钮，已取消显示删除按钮
          const tempActions = actions.filter(item =>
            record.get('statusCode') === CHARGE_RULE_CONSTANT.NEW
              ? ['edit', 'release', 'delete'].includes(item.key)
              : record.get('statusCode') === CHARGE_RULE_CONSTANT.RELEASED
              ? item.key === 'cancel'
              : false
          );
          return operatorRender(tempActions);
        },
      },
    ];
  }

  render() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hchg.chargeRule.view.title.chargeRule').d('计费规则配置')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '计费规则配置-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={() => this.handleGotoDetail(undefined, false)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={tableDS} columns={this.chargeRuleColumns} />
        </Content>
      </>
    );
  }
}
