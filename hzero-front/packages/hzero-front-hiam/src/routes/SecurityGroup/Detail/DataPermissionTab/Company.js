/**
 * Company - 公司tab
 * @date: 2019-11-4
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table, Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { uniqBy, unionWith, pullAllBy, isEmpty, xorBy } from 'lodash';

import intl from 'utils/intl';
import { getResponse } from 'utils/utils';
import notification from 'utils/notification';
import { FORM_COL_3_LAYOUT, SEARCH_COL_CLASSNAME, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import { queryCompanyPermission, updateCompanyPermission } from '@/services/securityGroupService';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class DimensionListPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      companyData: [], // 公司tab数据源
      originList: [],
      loading: true,
      updateLoading: false,
      expanded: true,
      checkedList: [],
    };
  }

  componentDidMount() {
    this.queryCompanyData();
  }

  /**
   * 查询公司tab数据
   */
  @Bind()
  async queryCompanyData(params = {}) {
    const { secGrpId, secGrpSource, roleId, isSelf } = this.props;
    let companyData = [];
    const newParams = {
      ...params,
      isSelf,
      roleId,
      secGrpId,
      secGrpSource,
      authorityTypeCode: 'COMPANY',
    };

    companyData = await queryCompanyPermission(newParams);
    const expandedRowKeys = companyData.originList && companyData.originList.map((item) => item.id);
    const checkedList =
      companyData.originList && companyData.originList.filter((item) => item.checkedFlag === 1);
    this.setState({
      companyData: companyData.treeList || [],
      originList: companyData.originList || [],
      checkedList,
      loading: false,
      expanded: false,
      expandedRowKeys,
    });
  }

  /**
   * 重置
   */
  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询
   */
  @Bind()
  queryValue() {
    const { form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        this.queryCompanyData(fieldsValue);
      }
    });
  }

  /**
   * 点击展开节点触发方法
   * @param {*Boolean} expanded 展开收起标志
   * @param {*Object} record 行记录
   */
  @Bind()
  onExpand(expanded, record = {}) {
    const { expandedRowKeys } = this.state;
    this.setState({
      expandedRowKeys: expanded
        ? expandedRowKeys.concat(record.id)
        : expandedRowKeys.filter((o) => o !== record.id),
    });
  }

  /**
   *全部展开和收起
   */
  @Bind()
  handleExpand() {
    const { expanded, originList } = this.state;
    this.setState({
      expandedRowKeys: expanded ? originList.map((list) => list.id) : [],
      expanded: !expanded,
    });
  }

  /**
   * 设置选中
   * @param {Array} rows 选中的行
   */
  @Bind()
  setSelectRows(rows) {
    this.setState({
      checkedList: uniqBy(rows, 'id'),
    });
  }

  /**
   * 全选
   * @param selected 是否选中
   * @param _
   * @param changeRows 改变的行
   * @returns {Promise<void>}
   */
  @Bind()
  async handleSelectAll(selected, _, changeRows) {
    const { secGrpId } = this.props;
    this.setState({
      updateLoading: true,
    });
    const changedList = changeRows.map((uniqItem) => {
      const { checkedFlag, ...rest } = uniqItem;
      return {
        ...rest,
        checkedFlag: selected ? 1 : 0,
      };
    });
    const payload = {
      secGrpId,
      checkedList: changedList,
      authorityTypeCode: 'COMPANY',
    };
    const res = await updateCompanyPermission(payload);
    const response = getResponse(res);
    if (response) {
      notification.success();
      this.queryValue();
    }
    this.setState({
      updateLoading: false,
    });
  }

  // /**
  //  * 表格选中事件
  //  * @param {*Array} rows 选中行数据
  //  */
  // @Bind()
  // handleSelectRows(_, rows) {
  //   this.setSelectRows(rows);
  // }

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
   * 选中父级后同时选中子集
   * @param {*Object} record 当前操作的行
   * @param {*boolean} selected 选中标记
   * @param {*Array} selectedRows 已经选中行数据
   */
  @Bind()
  selectChilds(record = {}, selected, selectedRows) {
    const { loading } = this.props;
    const { updateLoading, originList } = this.state;
    if (updateLoading || loading) return;
    this.setState({
      updateLoading: true,
    });
    let grandsonList = [];
    const childType = this.findChildType(record.typeCode);
    const childLists = originList.filter(
      (list) => list.parentId === record.dataId && list.typeCode && list.typeCode === childType
    );
    childLists.map((childList) => {
      const grandsonType = this.findChildType(childList.typeCode);
      grandsonList = unionWith(
        grandsonList,
        originList.filter(
          (list) =>
            list.parentId === childList.dataId && list.typeCode && list.typeCode === grandsonType
        )
      );
      return grandsonList;
    });
    let rows;
    if (selected) {
      rows = unionWith(unionWith(selectedRows, childLists), grandsonList);
    } else {
      rows = pullAllBy(pullAllBy(selectedRows, childLists, 'dataId'), grandsonList, 'dataId');
    }
    this.setState(
      {
        checkedList: uniqBy(rows, 'id'),
      },
      () => {
        this.handleSaveCompany();
      }
    );
  }

  // @Bind()
  // handleSelectAll(selected) {
  //   const { companyData } = this.state;
  //   this.setState({
  //     checkedList: selected ? companyData : [],
  //   }, () => {
  //     this.handleSaveCompany();
  //   });
  // }

  /**
   * 保存
   */
  @Bind()
  async handleSaveCompany() {
    const { secGrpId } = this.props;
    const { checkedList, originList } = this.state;
    const originCheckedList = originList.filter((item) => item.checkedFlag === 1); // 初始选中的数据
    const uniqArr = xorBy(originCheckedList, checkedList, 'id');
    const changedList = uniqArr.map((uniqItem) => {
      const { checkedFlag, ...rest } = uniqItem;
      return {
        ...rest,
        checkedFlag: checkedList.find((item) => item.id === uniqItem.id) ? 1 : 0,
      };
    });
    const payload = {
      secGrpId,
      checkedList: changedList,
      authorityTypeCode: 'COMPANY',
    };
    const res = await updateCompanyPermission(payload);
    const response = getResponse(res);
    if (response) {
      notification.success();
      this.queryValue();
    }
    this.setState({
      updateLoading: false,
    });
  }

  render() {
    const {
      companyData,
      loading,
      expandedRowKeys,
      expanded,
      checkedList,
      updateLoading,
    } = this.state;
    const {
      form: { getFieldDecorator },
      isSelf,
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
      },
    ];
    const rowSelection = isSelf
      ? {
          selectedRowKeys: isEmpty(checkedList) ? [] : checkedList.map((n) => n.id),
          onChange: (_, rows) => this.setSelectRows(rows),
          onSelect: this.selectChilds,
          onSelectAll: this.handleSelectAll,
        }
      : null;
    return (
      <>
        <Form layout="inline">
          <Row type="flex" gutter={24} align="bottom">
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                style={{ width: '100%', minWidth: 'auto' }}
                label={intl.get('hiam.authority.model.authorityCompany.name').d('名称')}
              >
                {getFieldDecorator('dataName')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                style={{ width: '100%', minWidth: 'auto' }}
                label={intl.get('hiam.authority.model.authorityCompany.dataCode').d('代码')}
              >
                {getFieldDecorator('dataCode')(
                  <Input typeCase="upper" trim inputChinese={false} />
                )}
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
        </Form>
        <Row style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Col>
            <Button onClick={() => this.handleExpand()} style={{ marginRight: '8px' }}>
              {expanded
                ? intl.get('hzero.common.button.expand').d('展开')
                : intl.get('hzero.common.button.up').d('收起')}
            </Button>
            {/* <Button type="primary" loading={updateLoading} onClick={this.handleSaveCompany}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button> */}
          </Col>
        </Row>
        <Table
          bordered
          rowKey="id"
          pagination={false}
          loading={loading || updateLoading}
          dataSource={companyData}
          columns={columns}
          rowSelection={rowSelection}
          onExpand={this.onExpand}
          expandedRowKeys={expandedRowKeys}
        />
      </>
    );
  }
}
