/**
 * Company - 租户级权限维护tab页 - 公司
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import lodash from 'lodash';
import { Bind } from 'lodash-decorators';
import { Button, Form, Input, Row, Col } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';
import VirtualTable from 'components/VirtualTable';

import notification from 'utils/notification';
import intl from 'utils/intl';

import styles from './index.less';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

/**
 * 租户级权限管理 - 公司
 * @extends {Component} - React.Component
 * @reactProps {Object} authorityCompany - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ authorityCompany, loading }) => ({
  authorityCompany,
  updateLoading: loading.effects['authorityCompany/updateAuthorityCompany'],
  fetchLoading: loading.effects['authorityCompany/fetchAuthorityCompanyAndExpand'],
  refreshLoading: loading.effects['authorityCompany/fetchAuthorityCompany'],
}))
@Form.create({ fieldNameProp: null })
export default class Company extends React.Component {
  /**
   *Creates an instance of Company.
   * @param {Object} props 属性
   * @memberof Company
   */
  constructor(props) {
    super(props);
    this.state = {
      expanded: true,
      queryParams: {},
      checkListState: [],
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
    const { dispatch, userId } = this.props;
    const { queryParams } = this.state;
    dispatch({
      type: 'authorityCompany/fetchAuthorityCompany',
      payload: {
        userId,
        ...queryParams,
      },
    }).then(() => {
      const {
        authorityCompany: { checkList = [] },
      } = this.props;
      this.setState({
        checkListState: checkList,
      });
    });
  }

  /**
   *保存
   */
  @Bind()
  companySave() {
    const {
      dispatch,
      authorityCompany: { checkList = [] },
      userId,
    } = this.props;
    const { checkListState } = this.state;
    const newCheckList = lodash.xorWith(
      checkList,
      checkListState,
      lodash.isEqualWith((cl, cls) => cl.id === cls.id)
    );
    const newList = newCheckList.map((e) => {
      if (checkList.find((v) => v.id === e.id)) {
        return {
          ...e,
          checkedFlag: 1,
        };
      }
      return {
        ...e,
        checkedFlag: 0,
      };
    });
    dispatch({
      type: 'authorityCompany/updateAuthorityCompany',
      payload: {
        checkList: lodash.uniqBy(newList, 'id'),
        userId,
      },
    }).then((response) => {
      if (response) {
        this.refreshValue();
        notification.success();
      }
    });
  }

  /**
   *查询数据
   */
  @Bind()
  queryValue() {
    const { form, dispatch, userId } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        this.setState({
          queryParams: fieldsValue,
          expanded: false,
        });
        dispatch({
          type: 'authorityCompany/fetchAuthorityCompanyAndExpand',
          payload: {
            ...fieldsValue,
            userId,
          },
        }).then(() => {
          const {
            authorityCompany: { checkList = [] },
          } = this.props;
          this.setState({
            checkListState: checkList,
          });
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
      type: 'authorityCompany/updateCheckList',
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
      type: 'authorityCompany/updateExpanded',
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
      type: 'authorityCompany/updateExpanded',
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
      childType = 'INV_ORGANIZATION';
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
    const parentList = this.getParentList(
      record.parentId,
      this.findParentType(record.typeCode),
      originList
    );
    if (selected) {
      this.setSelectRows(
        lodash.unionWith(
          parentList,
          lodash.unionWith(lodash.unionWith(selectedRows, childLists), grandsonList)
        )
      );
    } else {
      this.setSelectRows(
        lodash.pullAllBy(lodash.pullAllBy(selectedRows, childLists, 'id'), grandsonList, 'id')
      );
    }
  }

  @Bind()
  getParentList(parentId, parentTypeCode, originList, oldList = []) {
    if (lodash.isNil(parentId)) {
      return oldList;
    }
    const obj = originList.find(
      (list) => parentId === list.dataId && list.typeCode && list.typeCode === parentTypeCode
    );
    let resArr = oldList;
    if (obj) {
      resArr = resArr.concat([lodash.omit(obj, 'children')]);
      const parentCode = this.findParentType(obj.typeCode);
      return this.getParentList(obj.parentId, parentCode, originList, resArr);
    }
    return oldList;
  }

  /**
   * 获取父节点类型
   *
   * @param {*Object} childType 子级类型
   * @returns
   */
  // eslint-disable-next-line class-methods-use-this
  findParentType(childType) {
    let parentType = null;
    if (childType === 'OU') {
      parentType = 'COMPANY';
    } else if (childType === 'INV_ORGANIZATION') {
      parentType = 'OU';
    } else {
      parentType = null;
    }
    return parentType;
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
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
      <Form layout="inline">
        <FormItem label={intl.get('hiam.authority.model.authorityCompany.name').d('名称')}>
          {getFieldDecorator('dataName')(<Input />)}
        </FormItem>
        <FormItem label={intl.get('hiam.authority.model.authorityCompany.dataCode').d('代码')}>
          {getFieldDecorator('dataCode')(<Input typeCase="upper" trim inputChinese={false} />)}
        </FormItem>
        <FormItem>
          <Button onClick={this.handleReset} style={{ marginRight: 8 }}>
            {intl.get('hzero.common.button.reset').d('重置')}
          </Button>
          <Button type="primary" onClick={() => this.queryValue()} htmlType="submit">
            {intl.get('hzero.common.button.search').d('查询')}
          </Button>
        </FormItem>
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
                    meaning: '权限维护-保存公司',
                  },
                ]}
                type="primary"
                loading={updateLoading}
                onClick={() => this.companySave()}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </ButtonPermission>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   *渲染方法
   *
   * @returns
   */
  render() {
    const {
      fetchLoading = false,
      refreshLoading = false,
      authorityCompany: { data = [], checkList = [], expandedRowKeys = [] },
    } = this.props;
    const columns = [
      {
        title: intl
          .get('hiam.authority.model.authorityCompany.dataName')
          .d('公司/业务单元/库存组织'),
        dataIndex: 'dataName',
        flexGrow: 1,
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
        <VirtualTable
          isTree
          virtualized
          bordered
          rowKey="id"
          pagination={false}
          loading={fetchLoading || refreshLoading}
          data={data}
          autoInitHeight
          height={400}
          minHeight={400}
          rowSelection={rowSelection}
          expandedRowKeys={expandedRowKeys}
          columns={columns}
          // scroll={{ x: tableScrollWidth(columns) }}
          rowClassName={(record) => {
            if (record) {
              return checkList.find((list) => list.id === record.id)
                ? 'row-active'
                : 'row-noactive';
            }
          }}
          onExpandChange={this.onExpand}
        />
      </div>
    );
  }
}
