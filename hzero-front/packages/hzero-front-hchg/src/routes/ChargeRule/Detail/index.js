/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/1/14
 * @copyright HAND ® 2020
 */
import React from 'react';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import {
  Table,
  DataSet,
  Form,
  TextField,
  Tabs,
  Select,
  DatePicker,
  Modal,
  TextArea,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import { operatorRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { detailFormDS, ruleTableDS, discountTableDS } from '@/stores/ChargeRule/ChargeRuleDS';
import { CHARGE_RULE_CONSTANT } from '@/constants/constants';
import DiscountModal from './DiscountModal';
import styles from './index.less';

let modal;
const discountModalKey = Modal.key();

@formatterCollections({ code: ['hchg.chargeRule'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.ruleTableDS = new DataSet(ruleTableDS);
    this.discountTableDS = new DataSet(discountTableDS);
    this.detailFormDS = new DataSet({
      ...detailFormDS,
      children: {
        lineList: this.ruleTableDS,
        discountList: this.discountTableDS,
      },
    });

    const { disabledFlag = true } = props.location.state || {};
    this.state = {
      disabledFlag,
      organizationId: getCurrentOrganizationId(),
      activeTabKey: 'ruleDetail',
      editRuleItems: [], // 规则详情编辑状态的行
      editDiscountItems: [], // 优惠详情编辑状态的行
    };
  }

  async componentDidMount() {
    const {
      match: { params },
    } = this.props;
    const { id } = params;
    if (!isUndefined(id)) {
      this.detailFormDS.setQueryParameter('ruleHeaderId', id);
      const res = await this.detailFormDS.query();
      this.setState({ filterType: res.methodCode });
      const value = this.detailFormDS.current.get('typeCode');
      const item = this.detailFormDS.current.getField('typeCode').getLookupData(value);
      this.setState({ unitFilterType: item.tag });
    } else {
      this.detailFormDS.create({ statusCode: CHARGE_RULE_CONSTANT.NEW });
    }
  }

  /**
   * tab切换变换
   */
  @Bind()
  handleTabChange(key) {
    this.setState({ activeTabKey: key });
  }

  /**
   * 计量模式下，上条记录的小于等于没有值的时候，需要打开上条记录的编辑状态
   */
  needEditPreRecord(record, editRuleItems) {
    const { previousRecord } = record;
    const methodCode = this.detailFormDS.current.get('methodCode');
    if (
      isUndefined(previousRecord) ||
      methodCode !== CHARGE_RULE_CONSTANT.COUNT ||
      previousRecord.get('lessAndEquals')
    ) {
      return false;
    }
    if (isUndefined(editRuleItems.find(id => id === previousRecord.id))) {
      return true;
    }
    return false;
  }

  /**
   * 新建规则详情/优惠详情
   */
  @Bind()
  async handleAddLine() {
    const { activeTabKey, editRuleItems, editDiscountItems, organizationId } = this.state;
    try {
      if (activeTabKey === 'ruleDetail') {
        const record = await this.ruleTableDS.create({
          tenantId: organizationId,
          paymentModelCode: !isUndefined(this.detailFormDS.current)
            ? this.detailFormDS.current.get('paymentModelCode')
            : null,
        });
        this.ruleTableDS.current.set('seqNumber', record.index + 1);
        if (this.needEditPreRecord(record, editRuleItems)) {
          editRuleItems.push(record.previousRecord.id);
        }
        editRuleItems.push(record.id);
        this.setState({ editRuleItems });
      } else {
        const record = await this.discountTableDS.create({
          tenantId: organizationId,
        });
        this.discountTableDS.current.set('seqNumber', record.index + 1);
        editDiscountItems.push(record.id);
        this.setState({ editDiscountItems });
      }
    } catch (error) {
      notification.error({
        message: intl
          .get('hchg.view.chargeRule.message.createError')
          .d('头未选中记录，不能新建行记录'),
      });
    }
  }

  /**
   * 规则详情/优惠详情 编辑
   */
  @Bind()
  handleEdit(record) {
    const { activeTabKey, editRuleItems, editDiscountItems } = this.state;
    if (activeTabKey === 'ruleDetail') {
      editRuleItems.push(record.id);
      this.setState({ editRuleItems });
    } else {
      editDiscountItems.push(record.id);
      this.setState({ editDiscountItems });
    }
  }

  /**
   * 规则详情/优惠详情 取消编辑
   */
  @Bind()
  handleCancel(record) {
    const { activeTabKey } = this.state;
    let { editRuleItems, editDiscountItems } = this.state;
    if (activeTabKey === 'ruleDetail') {
      editRuleItems = editRuleItems.filter(item => item !== record.id);
      this.setState({ editRuleItems });
    } else {
      editDiscountItems = editDiscountItems.filter(item => item !== record.id);
      this.setState({ editDiscountItems });
    }
  }

  /**
   * 规则详情/优惠详情-删除
   * @param {Object} record 行记录
   */
  @Bind()
  async handleDelete(record) {
    const { activeTabKey } = this.state;
    if (activeTabKey === 'ruleDetail') {
      await this.ruleTableDS.delete(record);
    } else {
      await this.discountTableDS.delete(record);
    }
  }

  /**
   * 头行保存
   */
  @Bind()
  async handleSave() {
    const { organizationId } = this.state;
    const {
      history,
      match: { params },
    } = this.props;
    const validateHeader = await this.detailFormDS.validate();
    if (!validateHeader) {
      return notification.error({
        message: intl.get('hchg.view.chargeRule.message.validateHeader').d('请先完善必输内容'),
      });
    }
    if (this.ruleTableDS.length === 0) {
      return notification.error({
        message: intl.get('hchg.view.chargeRule.message.ruleLineLimit').d('至少维护一条规则详情'),
      });
    }
    try {
      if (this.detailFormDS.current.status === 'add') {
        this.detailFormDS.current.set('tenantId', organizationId);
        const res = await this.detailFormDS.submit();
        const { content, success } = res;
        const { ruleHeaderId } = content[0];
        if (success) {
          history.push({
            pathname: `/hchg/charge-rule/detail/${ruleHeaderId}`,
            state: { disabledFlag: false },
          });
        }
      } else {
        this.ruleTableDS.forEach(record => {
          record.set('ruleHeaderId', params.id);
          record.set('_status', 'update');
        });
        this.discountTableDS.forEach(record => {
          record.set('ruleHeaderId', params.id);
          record.set('_status', 'update');
        });
        const res = await this.detailFormDS.submit();
        if (res.success) {
          this.setState({ editRuleItems: [], editDiscountItems: [] });
        }
      }
      return true;
    } catch (error) {
      return false;
    }
  }

  /**
   * 关闭优惠范围弹窗
   */
  @Bind()
  handleCloseModel() {
    modal.close();
  }

  /**
   * 打开优惠范围弹窗
   */
  @Bind()
  handleShowModel(record) {
    if (record.status === 'add') {
      return notification.error({
        message: intl
          .get('hchg.view.chargeRule.message.maintainMessage')
          .d('新建的优惠详情请先保存后维护'),
      });
    }
    const {
      match: { path },
    } = this.props;
    const { disabledFlag } = this.state;
    const type = record.get('effectiveRangeCode');
    const title =
      type === CHARGE_RULE_CONSTANT.TENANT
        ? disabledFlag
          ? intl.get('hchg.chargeRule.model.chargeRule.viewTenantDiscount').d('优惠详情租户查看')
          : intl.get('hchg.chargeRule.model.chargeRule.editTenantDiscount').d('优惠详情租户维护')
        : disabledFlag
        ? intl.get('hchg.chargeRule.model.chargeRule.viewUserDiscount').d('优惠详情用户查看')
        : intl.get('hchg.chargeRule.model.chargeRule.editUserDiscount').d('优惠详情用户维护');
    modal = Modal.open({
      title,
      closable: true,
      movable: true,
      key: discountModalKey,
      style: { width: 1000 },
      className: styles.editModalWrap,
      children: (
        <DiscountModal
          path={path}
          disabledFlag={disabledFlag}
          ruleDiscountId={record.get('ruleDiscountId')}
          effectiveRange={record.get('effectiveRangeCode')}
          onRef={this.handleBindRef}
        />
      ),
      footer: (
        <>
          <ButtonPermission
            type="button"
            permissionList={[
              {
                code: `${path}.button.cancelRange`,
                type: 'button',
                meaning: '计费规则配置-优惠范围取消',
              },
            ]}
            onClick={() => this.handleCloseModel()}
          >
            {intl.get('hzero.common.button.cancel').d('取消')}
          </ButtonPermission>
          <ButtonPermission
            type="primary"
            permissionList={[
              {
                code: `${path}.button.saveRange`,
                type: 'button',
                meaning: '计费规则配置-优惠范围保存',
              },
            ]}
            disabled={disabledFlag}
            onClick={this.handleModalSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </>
      ),
    });
  }

  /**
   * 优惠范围弹窗保存
   */
  @Bind()
  async handleModalSave() {
    const { modalFormDS, modalTableDS } = this.state;
    const { current = {} } = modalFormDS;
    const validate = modalTableDS.validate();
    if (!validate) {
      return notification.error({
        message: intl.get('hchg.view.chargeRule.message.validateHeader').d('请先完善必输内容'),
      });
    }
    modalTableDS.forEach(record => {
      record.set('tenantId', current.get('tenantId'));
      record.set('ruleDiscountId', current.get('ruleDiscountId'));
      record.set('ruleHeaderId', current.get('ruleHeaderId'));
      record.set('typeCode', current.get('effectiveRangeCode'));
    });
    await modalTableDS.submit();
    await modalTableDS.query();
  }

  /**
   * 绑定discountModal
   */
  @Bind()
  handleBindRef(ref = {}, name) {
    this.setState({ [name]: ref });
  }

  /**
   * 付费模式变更
   */
  @Bind()
  handlePayChange(value) {
    this.ruleTableDS.forEach(record => {
      record.set('paymentModelCode', value);
    });
    if (value === CHARGE_RULE_CONSTANT.BEFORE) {
      this.setState({ filterType: CHARGE_RULE_CONSTANT.PACKAGE });
      this.detailFormDS.current.set('methodCode', CHARGE_RULE_CONSTANT.PACKAGE);
    } else {
      this.setState({ filterType: CHARGE_RULE_CONSTANT.COUNT });
      this.detailFormDS.current.set('methodCode', CHARGE_RULE_CONSTANT.COUNT);
    }
    this.detailFormDS.current.set('typeCode', null);
  }

  /**
   * 计费方式变更
   */
  @Bind()
  handleMethodChange(value) {
    if (value === CHARGE_RULE_CONSTANT.PACKAGE) {
      this.setState({ filterType: CHARGE_RULE_CONSTANT.PACKAGE });
      this.detailFormDS.current.set('paymentModelCode', CHARGE_RULE_CONSTANT.BEFORE);
    } else {
      this.setState({ filterType: CHARGE_RULE_CONSTANT.COUNT });
      this.detailFormDS.current.set('paymentModelCode', CHARGE_RULE_CONSTANT.AFTER);
    }
    this.detailFormDS.current.init('typeCode', null);
  }

  /**
   * 计费类型变更
   */
  @Bind()
  handleTypeChange(value) {
    const item = this.detailFormDS.current.getField('typeCode').getLookupData(value);
    this.detailFormDS.current.init('unitCode', null);
    this.setState({ unitFilterType: item.tag });
  }

  /**
   * 过滤计费类型数据源
   */
  @Bind()
  handleFilterType(record) {
    const { filterType } = this.state;
    return record.get('parentValue') === filterType;
  }

  /**
   * 过滤计量单位的数据源
   */
  @Bind()
  handleFilterUnit(record) {
    const { unitFilterType } = this.state;
    return record.get('tag') === unitFilterType;
  }

  /**
   * 规则详细columns
   */
  get ruleColumns() {
    const { match } = this.props;
    const { editRuleItems, disabledFlag } = this.state;
    const { path } = match;
    return [
      {
        name: 'seqNumber',
        width: 70,
        align: 'center',
        editor: record => editRuleItems.includes(record.id),
      },
      {
        name: 'greaterThan',
        width: 120,
        editor: record =>
          editRuleItems.includes(record.id) &&
          this.detailFormDS.current.get('methodCode') === CHARGE_RULE_CONSTANT.COUNT,
      },
      {
        name: 'lessAndEquals',
        width: 120,
        editor: record =>
          editRuleItems.includes(record.id) &&
          this.detailFormDS.current.get('methodCode') === CHARGE_RULE_CONSTANT.COUNT,
      },
      {
        name: 'constantValue',
        width: 100,
        editor: record =>
          editRuleItems.includes(record.id) &&
          this.detailFormDS.current.get('methodCode') === CHARGE_RULE_CONSTANT.PACKAGE,
      },
      {
        name: 'price',
        width: 120,
        editor: record => editRuleItems.includes(record.id),
      },
      {
        name: 'remark',
        editor: record => editRuleItems.includes(record.id),
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 110,
        renderer: ({ record }) => {
          const actions = [
            !editRuleItems.includes(record.id)
              ? {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.edit`,
                          type: 'button',
                          meaning: '计费规则配置-规则详情编辑',
                        },
                      ]}
                      disabled={disabledFlag}
                      onClick={() => this.handleEdit(record)}
                    >
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </ButtonPermission>
                  ),
                  key: 'edit',
                  len: 2,
                  title: intl.get('hzero.common.button.edit').d('编辑'),
                }
              : {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.cancel`,
                          type: 'button',
                          meaning: '计费规则配置-规则详情取消',
                        },
                      ]}
                      disabled={disabledFlag}
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
                      meaning: '计费规则配置-规则详情删除',
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

  /**
   * 优惠详情columns
   */
  get discountColumns() {
    const {
      match: { path },
    } = this.props;
    const { editDiscountItems, disabledFlag } = this.state;
    return [
      {
        name: 'seqNumber',
        width: 70,
        align: 'center',
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        name: 'typeCode',
        width: 120,
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        name: 'discountValue',
        width: 120,
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        name: 'effectiveRangeCode',
        width: 120,
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        header: intl.get('hchg.chargeRule.view.title.tenant').d('租户'),
        width: 100,
        renderer: ({ record }) => {
          const actions =
            record.get('effectiveRangeCode') === CHARGE_RULE_CONSTANT.TENANT
              ? [
                  {
                    ele: (
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.maintainTenant`,
                            type: 'button',
                            meaning: '计费规则配置-租户优惠查看/维护',
                          },
                        ]}
                        onClick={() => this.handleShowModel(record)}
                      >
                        {disabledFlag
                          ? intl.get('hchg.chargeRule.view.button.view').d('查看')
                          : intl.get('hchg.chargeRule.view.button.maintain').d('维护')}
                      </ButtonPermission>
                    ),
                    key: 'maintain',
                    len: 2,
                    title: disabledFlag
                      ? intl.get('hchg.chargeRule.view.button.view').d('查看')
                      : intl.get('hchg.chargeRule.view.button.maintain').d('维护'),
                  },
                ]
              : [];
          return operatorRender(actions);
        },
        align: 'center',
      },
      {
        header: intl.get('hchg.chargeRule.view.title.user').d('用户'),
        width: 100,
        renderer: ({ record }) => {
          const actions =
            record.get('effectiveRangeCode') === CHARGE_RULE_CONSTANT.USER
              ? [
                  {
                    ele: (
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.maintainUser`,
                            type: 'button',
                            meaning: '计费规则配置-用户优惠查看/维护',
                          },
                        ]}
                        onClick={() => this.handleShowModel(record)}
                      >
                        {disabledFlag
                          ? intl.get('hchg.chargeRule.view.button.view').d('查看')
                          : intl.get('hchg.chargeRule.view.button.maintain').d('维护')}
                      </ButtonPermission>
                    ),
                    key: 'maintain',
                    len: 2,
                    title: disabledFlag
                      ? intl.get('hchg.chargeRule.view.button.view').d('查看')
                      : intl.get('hchg.chargeRule.view.button.maintain').d('维护'),
                  },
                ]
              : [];
          return operatorRender(actions);
        },
        align: 'center',
      },
      {
        name: 'startDate',
        width: 130,
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        name: 'endDate',
        width: 130,
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        name: 'remark',
        editor: record => editDiscountItems.includes(record.id),
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 110,
        renderer: ({ record }) => {
          const actions = [
            !editDiscountItems.includes(record.id)
              ? {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.edit`,
                          type: 'button',
                          meaning: '计费规则配置-优惠详情编辑',
                        },
                      ]}
                      disabled={disabledFlag}
                      onClick={() => this.handleEdit(record)}
                    >
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </ButtonPermission>
                  ),
                  key: 'edit',
                  len: 2,
                  title: intl.get('hzero.common.button.edit').d('编辑'),
                }
              : {
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.cancel`,
                          type: 'button',
                          meaning: '计费规则配置-优惠详情取消',
                        },
                      ]}
                      disabled={disabledFlag}
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
                      meaning: '计费规则配置-优惠详情删除',
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
        // lock: 'right',
        align: 'center',
      },
    ];
  }

  render() {
    const { match } = this.props;
    const { disabledFlag } = this.state;
    const { path, params } = match;
    const isNew = isUndefined(params.id);
    return (
      <>
        <Header
          title={intl.get('hchg.chargeRule.view.title.chargeRuleDetail').d('计费规则配置')}
          backPath="/hchg/charge-rule/list"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '计费规则配置-保存',
              },
            ]}
            icon="save"
            type="primary"
            disabled={disabledFlag}
            onClick={() => this.handleSave()}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hchg.chargeRule.view.title.basicInformation').d('基本信息')}</h3>}
          >
            <Form labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <TextField name="ruleNum" disabled={!isNew} />
              <TextField name="ruleName" disabled={disabledFlag} />
              <Select name="statusCode" disabled />
              <Select name="methodCode" disabled={!isNew} onChange={this.handleMethodChange} />
              <Select disabled name="paymentModelCode" onChange={this.handlePayChange} />
              <Select
                name="typeCode"
                disabled={disabledFlag}
                optionsFilter={this.handleFilterType}
                onChange={this.handleTypeChange}
              />
              <Select
                name="unitCode"
                disabled={disabledFlag}
                optionsFilter={this.handleFilterUnit}
              />
              {/* <Currency name="prepayAmount" disabled={disabledFlag} />
              <TextField name="calculateEngine" disabled={disabledFlag} /> */}
              <DatePicker name="startDate" disabled={disabledFlag} />
              <DatePicker name="endDate" disabled={disabledFlag} />
              <TextArea newLine name="remark" disabled={disabledFlag} colSpan={3} />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hchg.chargeRule.view.title.detailInformation').d('详情信息')}</h3>
            }
          >
            <Tabs
              animated={false}
              tabBarExtraContent={
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.increase`,
                      type: 'button',
                      meaning: '计费规则配行详情-新增',
                    },
                  ]}
                  type="primary"
                  disabled={disabledFlag}
                  onClick={() => this.handleAddLine()}
                >
                  {intl.get('hzero.common.button.increase').d('新增')}
                </ButtonPermission>
              }
              onChange={this.handleTabChange}
            >
              <Tabs.TabPane
                tab={intl.get('hchg.chargeRule.view.title.ruleDetail').d('规则详情')}
                key="ruleDetail"
              >
                <Table dataSet={this.ruleTableDS} columns={this.ruleColumns} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hchg.chargeRule.view.title.discountDetail').d('优惠详情')}
                key="discountDetail"
              >
                <Table dataSet={this.discountTableDS} columns={this.discountColumns} />
              </Tabs.TabPane>
            </Tabs>
          </Card>
        </Content>
      </>
    );
  }
}
