/**
 * Company - 租户级权限维护tab页 - 公司
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import lodash from 'lodash';
import { Bind } from 'lodash-decorators';
import { Button, Form, Input, Table, Row, Col } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import styles from './index.less';

const FormItem = Form.Item;
@connect(({ trRoleDataAuthorityCompany, loading }) => ({
  authorityCompany: trRoleDataAuthorityCompany,
  updateLoading: loading.effects['trRoleDataAuthorityCompany/updateAuthorityCompany'],
  fetchLoading: loading.effects['trRoleDataAuthorityCompany/fetchAuthorityCompanyAndExpand'],
  refreshLoading: loading.effects['trRoleDataAuthorityCompany/fetchAuthorityCompany'],
}))
@Form.create({ fieldNameProp: null })
export default class Company extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expanded: true,
      queryParams: {},
    };
  }

  componentDidMount() {
    this.queryValue();
  }

  /**
   *刷新数据
   */
  @Bind()
  refreshValue() {
    const { dispatch, roleId } = this.props;
    const { queryParams } = this.state;
    dispatch({
      type: 'trRoleDataAuthorityCompany/fetchAuthorityCompany',
      payload: {
        roleId,
        ...queryParams,
      },
    });
  }

  /**
   *保存
   */
  @Bind()
  campanySave() {
    const {
      dispatch,
      authorityCompany: { checkList = [] },
      roleId,
    } = this.props;
    dispatch({
      type: 'trRoleDataAuthorityCompany/updateAuthorityCompany',
      payload: {
        checkList,
        roleId,
      },
    }).then((response) => {
      if (response) {
        this.refreshValue();
        notification.success();
      }
    });
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  queryValue() {
    const { form, dispatch, roleId } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        this.setState({
          queryParams: fieldsValue,
          expanded: false,
        });
        dispatch({
          type: 'trRoleDataAuthorityCompany/fetchAuthorityCompanyAndExpand',
          payload: {
            ...fieldsValue,
            roleId,
          },
        });
      }
    });
  }

  /**
   *设置选中
   *
   * @param {*Array} rows 选中的行
   */
  @Bind()
  setSelectRows(rows) {
    const { dispatch } = this.props;
    dispatch({
      type: 'trRoleDataAuthorityCompany/updateCheckList',
      payload: lodash.uniqBy(rows, 'id'),
    });
  }

  /**
   *表格选中事件
   *
   * @param {*} _ 占位
   * @param {*Array} rows 选中行数据
   */
  @Bind()
  handleSelectRows(_, rows) {
    this.setSelectRows(rows);
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
      authorityCompany: { expandedRowKeys = [] },
    } = this.props;
    dispatch({
      type: 'trRoleDataAuthorityCompany/updateExpanded',
      payload: expanded
        ? expandedRowKeys.concat(record.id)
        : expandedRowKeys.filter((o) => o !== record.id),
    });
  }

  /**
   *全部展开和收起
   */
  @Bind()
  handleExpand() {
    const {
      dispatch,
      authorityCompany: { originList = [] },
    } = this.props;
    const { expanded } = this.state;
    dispatch({
      type: 'trRoleDataAuthorityCompany/updateExpanded',
      payload: expanded ? originList.map((list) => list.id) : [],
    });
    this.setState({
      expanded: !expanded,
    });
  }

  /**
   * 获取子节点类型
   *
   * @param {*Object} parentType 父级类型
   * @returns
   */
  @Bind()
  findChildType(parentType) {
    let childType = null;
    if (parentType === 'COMPANY') {
      childType = 'OU';
    } else if (parentType === 'OU') {
      childType = 'INVORG';
    } else {
      childType = null;
    }
    return childType;
  }

  /**
   *选中父级后同时选中子集
   *
   * @param {*Object} record 当前操作的行
   * @param {*boolean} selected 选中标记
   * @param {*Array} selectedRows 已经选中行数据
   */
  @Bind()
  selectChilds(record = {}, selected, selectedRows) {
    const {
      authorityCompany: { originList },
    } = this.props;
    const childType = this.findChildType(record.typeCode);
    let grandsonList = [];
    const childLists = originList.filter(
      (list) => list.parentId === record.dataId && list.typeCode && list.typeCode === childType
    );
    childLists.map((childList) => {
      const grandsonType = this.findChildType(childList.typeCode);
      grandsonList = lodash.unionWith(
        grandsonList,
        originList.filter(
          (list) =>
            list.parentId === childList.dataId && list.typeCode && list.typeCode === grandsonType
        )
      );
      return grandsonList;
    });
    if (selected) {
      this.setSelectRows(
        lodash.unionWith(lodash.unionWith(selectedRows, childLists), grandsonList)
      );
    } else {
      this.setSelectRows(
        lodash.pullAllBy(
          lodash.pullAllBy(selectedRows, childLists, 'dataId'),
          grandsonList,
          'dataId'
        )
      );
    }
  }

  /**
   *渲染查询结构
   *
   * @returns
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    const { updateLoading, path } = this.props;
    const { expanded } = this.state;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom">
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.authority.model.authorityCompany.name').d('名称')}
            >
              {getFieldDecorator('dataName')(<Input />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.authority.model.authorityCompany.dataCode').d('代码')}
            >
              {getFieldDecorator('dataCode')(<Input typeCase="upper" trim inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col span={8}>
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
        <Row>
          <Col>
            <FormItem className={styles['right-btn-group']}>
              <Button onClick={() => this.handleExpand()}>
                {expanded
                  ? intl.get('hzero.common.button.expand').d('展开')
                  : intl.get('hzero.common.button.up').d('收起')}
              </Button>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.saveCompany`,
                    type: 'button',
                    meaning: '角色管理-公司保存',
                  },
                ]}
                type="primary"
                loading={updateLoading}
                onClick={this.campanySave}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </ButtonPermission>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      fetchLoading = false,
      refreshLoading = false,
      authorityCompany: { data = [], checkList = [], expandedRowKeys = [] } = {},
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
    ];
    const rowSelection = {
      selectedRowKeys: checkList.map((n) => n.id),
      onChange: this.handleSelectRows,
      onSelect: this.selectChilds,
    };
    return (
      <div>
        <div className="table-list-search">{this.renderForm()}</div>
        <Table
          bordered
          rowKey="id"
          pagination={false}
          loading={fetchLoading || refreshLoading}
          dataSource={data}
          rowSelection={rowSelection}
          expandedRowKeys={expandedRowKeys}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          rowClassName={(record) =>
            checkList.find((list) => list.id === record.id) ? 'row-active' : 'row-noactive'
          }
          onExpand={this.onExpand}
        />
      </div>
    );
  }
}
