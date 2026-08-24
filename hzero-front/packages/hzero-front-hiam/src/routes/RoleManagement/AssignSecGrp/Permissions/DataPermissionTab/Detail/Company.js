/**
 * Company - 分配安全组-数据权限tab页 - 公司
 * @date: 2019-12-23
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button, Form, Input, Table, Row, Col } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender, yesOrNoRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { isUndefined } from 'lodash';
import intl from 'utils/intl';
import {
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

import styles from './index.less';

const FormItem = Form.Item;
@connect(({ secGrpAuthorityCompany, loading }) => ({
  secGrpAuthorityCompany,
  fetchLoading: loading.effects['secGrpAuthorityCompany/fetchAuthorityCompanyAndExpand'],
}))
@Form.create({ fieldNameProp: null })
export default class Company extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expanded: true,
    };
  }

  componentDidMount() {
    this.queryValue();
  }

  @Bind()
  queryValue() {
    const { form, dispatch, secGrpId, roleId } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        this.setState({
          expanded: false,
        });
        dispatch({
          type: 'secGrpAuthorityCompany/fetchAuthorityCompanyAndExpand',
          payload: {
            ...fieldsValue,
            secGrpId,
            roleId,
            authorityTypeCode: 'COMPANY',
          },
        });
      }
    });
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   *点击展开节点触发方法
   *
   * @param {*Boolean} expanded 展开收起标志
   * @param {*Object} record 行记录
   */
  @Bind()
  onExpand(expanded, record = {}) {
    const {
      dispatch,
      secGrpAuthorityCompany: { expandedRowKeys = [] },
    } = this.props;
    dispatch({
      type: 'secGrpAuthorityCompany/updateExpanded',
      payload: expanded
        ? expandedRowKeys.concat(record.id)
        : expandedRowKeys.filter(o => o !== record.id),
    });
  }

  /**
   *全部展开和收起
   */
  @Bind()
  handleExpand() {
    const {
      dispatch,
      secGrpAuthorityCompany: { originList = [] },
    } = this.props;
    const { expanded } = this.state;
    dispatch({
      type: 'secGrpAuthorityCompany/updateExpanded',
      payload: expanded ? originList.map(list => list.id) : [],
    });
    this.setState({
      expanded: !expanded,
    });
  }

  /**
   * 屏蔽/取消屏蔽公司权限
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleShield(record) {
    const { secGrpDclLineId, shieldFlag } = record;
    const { onShield = e => e } = this.props;
    const shieldData = { shieldFlag, authorityId: secGrpDclLineId, authorityType: 'DCL' };
    onShield(shieldData, this.queryValue);
  }

  /**
   *渲染查询结构
   *
   * @returns
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    // const { updateLoading, path } = this.props;
    const { expanded } = this.state;
    return (
      <Form layout="inline">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hiam.authority.model.authorityCompany.name').d('名称')}
              {...EDIT_FORM_ITEM_LAYOUT_COL_2}
            >
              {getFieldDecorator('dataName')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hiam.authority.model.authorityCompany.dataCode').d('代码')}
              {...EDIT_FORM_ITEM_LAYOUT_COL_2}
            >
              {getFieldDecorator('dataCode')(<Input typeCase="upper" trim inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleReset} style={{ marginRight: 8 }}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" onClick={this.queryValue} htmlType="submit">
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT} offset={16}>
            <FormItem className={styles['right-btn-group']}>
              <Button onClick={() => this.handleExpand()}>
                {expanded
                  ? intl.get('hzero.common.button.expand').d('展开')
                  : intl.get('hzero.common.button.up').d('收起')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      fetchLoading = false,
      shieldLoading = false,
      path,
      secGrpAuthorityCompany: { data = [], checkList = [], expandedRowKeys = [] } = {},
    } = this.props;
    const columns = [
      {
        title: intl
          .get('hiam.authority.model.authorityCompany.dataName')
          .d('公司/业务单元/库存组织'),
        dataIndex: 'dataName',
      },
      {
        title: intl.get('hiam.authority.model.authorityCompany.dataCode').d('代码'),
        dataIndex: 'dataCode',
        width: 400,
      },
      {
        dataIndex: 'shieldFlag',
        title: intl.get('hiam.roleManagement.model.roleManagement.isShield').d('是否屏蔽'),
        width: 90,
        render: yesOrNoRender,
      },
      {
        key: 'operator',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        fixed: 'right',
        render: (_, record) => {
          const shieldBtn = [];
          if (!isUndefined(record.shieldFlag)) {
            shieldBtn.push({
              key: 'shield',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.shield`,
                      type: 'button',
                      meaning: '角色管理-屏蔽访问权限',
                    },
                  ]}
                  onClick={() => this.handleShield(record)}
                >
                  {record.shieldFlag
                    ? intl.get('hiam.roleManagement.view.button.cancelShield').d('取消屏蔽')
                    : intl.get('hiam.roleManagement.view.button.shield').d('屏蔽')}
                </ButtonPermission>
              ),
              len: record.shieldFlag ? 4 : 2,
              title: record.shieldFlag
                ? intl.get('hiam.roleManagement.view.button.cancelShield').d('取消屏蔽')
                : intl.get('hiam.roleManagement.view.button.shield').d('屏蔽'),
            });
          }
          return operatorRender(shieldBtn);
        },
      },
    ];

    return (
      <div>
        <div className="table-list-search">{this.renderForm()}</div>
        <Table
          bordered
          rowKey="id"
          pagination={false}
          loading={fetchLoading || shieldLoading}
          dataSource={data}
          expandedRowKeys={expandedRowKeys}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          rowClassName={record =>
            checkList.find(list => list.id === record.id) ? 'row-active' : 'row-noactive'}
          onExpand={this.onExpand}
        />
      </div>
    );
  }
}
