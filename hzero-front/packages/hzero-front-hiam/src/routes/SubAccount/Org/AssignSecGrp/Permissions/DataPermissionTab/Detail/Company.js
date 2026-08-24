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
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import {
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

import styles from './index.less';

const FormItem = Form.Item;
@connect(({ accSecGrpAuthorityCompany, loading }) => ({
  accSecGrpAuthorityCompany,
  fetchLoading: loading.effects['accSecGrpAuthorityCompany/fetchAuthorityCompanyAndExpand'],
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
          type: 'accSecGrpAuthorityCompany/fetchAuthorityCompanyAndExpand',
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
   * 点击展开节点触发方法
   * @param {*Boolean} expanded 展开收起标志
   * @param {*Object} record 行记录
   */
  @Bind()
  onExpand(expanded, record = {}) {
    const {
      dispatch,
      accSecGrpAuthorityCompany: { expandedRowKeys = [] },
    } = this.props;
    dispatch({
      type: 'accSecGrpAuthorityCompany/updateExpanded',
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
      accSecGrpAuthorityCompany: { originList = [] },
    } = this.props;
    const { expanded } = this.state;
    dispatch({
      type: 'accSecGrpAuthorityCompany/updateExpanded',
      payload: expanded ? originList.map(list => list.id) : [],
    });
    this.setState({
      expanded: !expanded,
    });
  }

  /**
   *渲染查询结构
   *
   * @returns
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    const { expanded } = this.state;
    return (
      <Form layout="inline">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
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
      accSecGrpAuthorityCompany: { data = [], checkList = [], expandedRowKeys = [] } = {},
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
        width: 300,
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
