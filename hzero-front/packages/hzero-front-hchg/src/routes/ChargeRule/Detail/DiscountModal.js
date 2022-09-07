/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/1/14
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import { Table, Form, DataSet, TextField, Select, DatePicker, NumberField } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { modalFormDS, modalTableDS } from '@/stores/ChargeRule/ChargeRuleDS';

@formatterCollections({ code: ['hchg.chargeRule'] })
export default class DiscountModal extends React.Component {
  constructor(props) {
    super(props);
    this.modalFormDS = new DataSet(modalFormDS);
    this.modalTableDS = new DataSet({ ...modalTableDS(props.effectiveRange) });
    props.onRef(this.modalFormDS, 'modalFormDS');
    props.onRef(this.modalTableDS, 'modalTableDS');
  }

  componentDidMount() {
    const { ruleDiscountId } = this.props;
    if (!isUndefined(ruleDiscountId)) {
      this.modalFormDS.setQueryParameter('ruleDiscountId', ruleDiscountId);
      this.modalFormDS.query();
      this.modalTableDS.setQueryParameter('ruleDiscountId', ruleDiscountId);
      this.modalTableDS.query();
    }
  }

  /**
   * 删除
   */
  @Bind()
  async handleDelete(record) {
    await this.modalTableDS.delete(record);
  }

  get tableColumns() {
    const { path, disabledFlag } = this.props;
    return [
      {
        name: 'seqNumber',
        width: 70,
        align: 'center',
        renderer: ({ record }) =>
          (this.modalTableDS.currentPage - 1) * this.modalTableDS.pageSize + record.index + 1,
      },
      {
        name: 'tenantLov',
        width: 250,
        editor: !disabledFlag,
      },
      {
        name: 'remark',
        editor: !disabledFlag,
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 80,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/delete`,
                      type: 'button',
                      meaning: '优惠详情-删除',
                    },
                  ]}
                  disabled={disabledFlag}
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
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  render() {
    const { disabledFlag } = this.props;
    return (
      <>
        <Form dataSet={this.modalFormDS} columns={3}>
          <TextField name="seqNumber" disabled />
          <Select name="typeCode" disabled />
          <NumberField name="discountValue" disabled />
          <Select name="effectiveRangeCode" disabled />
          <DatePicker name="startDate" disabled />
          <DatePicker name="endDate" disabled />
        </Form>
        <Table
          dataSet={this.modalTableDS}
          buttons={disabledFlag ? [] : ['add']}
          columns={this.tableColumns}
        />
      </>
    );
  }
}
