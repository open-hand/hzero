/**
 * LazyTree
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-09
 * @copyright 2019 © HAND
 */
import React from 'react';
import classNames from 'classnames';
import uuid from 'uuid/v4';
import { Icon, Input, Form, Select, InputNumber } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';
import { isNil, isEmpty } from 'lodash';
import { routerRedux } from 'dva/router';
import queryString from 'query-string';

import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';
import Checkbox from 'components/Checkbox';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth, getEditTableData } from 'utils/utils';
import { DEBOUNCE_TIME } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

import EditDrawer from '../components/Drawer';

import styles from './styles.less';

function buildNewTreeDataSource(treeDataSource = [], iterFunc) {
  return treeDataSource.map((item) => {
    if (item.children) {
      const newItem = iterFunc(item);
      return {
        ...newItem,
        children: buildNewTreeDataSource(newItem.children, iterFunc),
      };
    } else {
      return iterFunc(item);
    }
  });
}

const INDENT_SIZE = 16;

export default class LazyTree extends React.Component {
  state = {
    drawerVisible: false,
    editRecord: {},
  };

  componentDidMount() {
    const { loadData } = this.props;
    loadData();
  }

  componentWillUnmount() {
    this.handleTableExpand.cancel();
  }

  // Button

  /**
   * 新建一个顶级组织
   */
  @Bind()
  handleCreateBtnClick() {
    // TODO: 新增
    const { updateModelState, dataSource = [], organizationId, expandKeys = [] } = this.props;
    // 先获取直接下级节点，将新增的节点添加在子节点列表末位
    const unitId = uuid();
    const newItem = {
      unitId,
      tenantId: organizationId,
      unitCode: '',
      unitName: '',
      unitTypeMeaning: '',
      unitTypeCode: '',
      orderSeq: '',
      distribute: '',
      operator: '',
      indent: 0,
      supervisorFlag: 0, // 默认非主管组织
      enabledFlag: 1, // 新增节点默认启用
      _status: 'create', // 新增节点的标识
    };
    updateModelState({
      treeDataSource: [newItem, ...dataSource],
      expandKeys: [...expandKeys, unitId],
    });
  }

  /**
   * 表格行内编辑的保存
   */
  @Bind()
  handleSaveBtnClick() {
    const { organizationId, dataSource = [], saveAddData } = this.props;
    // 处理表单效验，获取处理后的表单数据
    const params = getEditTableData(dataSource, ['children', 'unitId']);
    if (Array.isArray(params) && params.length !== 0) {
      saveAddData({
        tenantId: organizationId,
        data: params,
      }).then((res) => {
        if (res) {
          notification.success();
          const { loadData } = this.props;
          loadData();
        }
      });
    }
  }

  /**
   * 有新增的数据才可以 保存
   * @return {boolean}
   */
  getSaveBtnDisabled() {
    const { dataSource = [] } = this.props;
    let createCount = 0;
    buildNewTreeDataSource(dataSource, (item) => {
      if (item._status === 'create') {
        createCount += 1;
      }
      return item;
    });
    return createCount === 0;
  }

  // Table

  /**
   * 清除新增的数据
   * @param record
   */
  @Bind()
  handleClearRecord(record) {
    const { updateModelState, dataSource = [], expandKeys = [] } = this.props;
    let needRemoveExpandKey = false;
    const newDataSource = isNil(record.parentUnitId)
      ? dataSource.filter((item) => item.unitId !== record.unitId)
      : buildNewTreeDataSource(dataSource, (item) => {
          if (record.parentUnitId === item.unitId) {
            const newChildren = item.children.filter((child) => child.unitId !== record.unitId);
            const newItem = { ...item };
            if (newChildren.length === 0) {
              needRemoveExpandKey = true;
              newItem.hasNextFlag = item._prevHasNextFlag;
            }
            return {
              ...newItem,
              children: newChildren,
            };
          } else {
            return item;
          }
        });

    updateModelState({
      treeDataSource: newDataSource,
      expandKeys: needRemoveExpandKey
        ? expandKeys.filter((item) => item !== record.parentUnitId)
        : expandKeys,
    });
  }

  /**
   * 编辑已经保存的数据
   * @param record
   */
  @Bind()
  handleEditRecord(record) {
    this.setState({
      drawerVisible: true,
      editRecord: record,
    });
  }

  /**
   * 对保存的数据新增下级
   * @param record
   */
  @Bind()
  handleAddRecordChild(record) {
    const { updateModelState, dataSource = [], expandKeys = [], organizationId } = this.props;
    const unitId = uuid();
    const newItem = {
      unitId,
      tenantId: organizationId,
      unitCode: '',
      unitName: '',
      unitTypeMeaning: '',
      unitTypeCode: '',
      orderSeq: '',
      supervisorFlag: 0, // 默认非主管组织
      enabledFlag: 1, // 新增节点默认启用
      parentUnitName: record.unitName,
      parentUnitId: record.unitId,
      _status: 'create', // 新增节点的标识
      indent: record.indent + 1,
    };
    let needAddExpandKey = false;
    const newDataSource = buildNewTreeDataSource(dataSource, (item) => {
      if (item.unitId === record.unitId) {
        if (item.hasNextFlag !== 1) {
          // 本身没有子节点
          needAddExpandKey = true;
        } else if (!expandKeys.includes(item.unitId)) {
          // 本身有子节点但是没有展开
          needAddExpandKey = true;
        }
        return {
          ...item,
          hasNextFlag: 1,
          // 存储之前的 标记, 用于 清除后 还原
          _prevHasNextFlag: isNil(item._prevHasNextFlag) ? item.hasNextFlag : item._prevHasNextFlag,
          children: item.children ? [newItem, ...item.children] : [newItem],
        };
      } else {
        return item;
      }
    });
    updateModelState({
      treeDataSource: newDataSource,
      expandKeys: needAddExpandKey ? [...expandKeys, record.unitId] : expandKeys,
    });
  }

  /**
   * 对保存的数据禁用
   * @param record
   */
  @Bind()
  handleDisabledRecord(record) {
    const { forbidLine, organizationId } = this.props;
    forbidLine({
      tenantId: organizationId,
      unitId: record.unitId,
      objectVersionNumber: record.objectVersionNumber,
      _token: record._token,
    }).then((res) => {
      if (res) {
        notification.success();
        const { loadData } = this.props;
        loadData();
      }
    });
  }

  /**
   * 对保存的数据启用
   * @param record
   */
  @Bind()
  handleEnableRecord(record) {
    const { organizationId, enabledLine } = this.props;
    enabledLine({
      tenantId: organizationId,
      unitId: record.unitId,
      objectVersionNumber: record.objectVersionNumber,
      _token: record._token,
    }).then((res) => {
      if (res) {
        notification.success();
        const { loadData } = this.props;
        loadData();
      }
    });
  }

  /**
   * 分配部门 - 跳转到子路由
   * @param record
   */
  @Bind()
  handleGotoSubGradeRecord(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/hr/org/department/${record.unitId}`,
      })
    );
  }

  /**
   * 分配岗位
   * 进行岗位分配，跳转到下一级页面
   * @param {*} record 操作对象
   */
  @Bind()
  handleGotoSubPostRecord(record = {}) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/hr/org/post/${record.unitId}`,
        search: queryString.stringify({ fromSource: 'company' }),
      })
    );
  }

  /**
   * 树数据展开点击
   * @param {boolean} expanded - 是否展开
   * @param {object} record - 记录
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  handleTableExpand(expanded, record) {
    // 如果能显示展开按钮, 那么一定有children
    if (expanded) {
      if (record.children.length === 0) {
        const { loadData } = this.props;
        loadData({ unitId: record.unitId, indent: record.indent });
      } else {
        const { updateModelState, expandKeys = [] } = this.props;
        updateModelState({
          expandKeys: [...expandKeys, record.unitId],
        });
      }
    }
    if (!expanded) {
      const { updateModelState, expandKeys = [] } = this.props;
      updateModelState({
        expandKeys: expandKeys.filter((k) => k !== record.unitId),
      });
    }
  }

  /**
   * 须由 自己 手动设置 expand
   * 行内编辑只有新增
   * @return {*[]}
   */
  getColumns() {
    const { match } = this.props;
    return [
      {
        dataIndex: 'unitCode',
        title: intl.get('entity.organization.code').d('组织编码'),
        width: 300,
        render: (unitCode, record) => {
          const { expandKeys = [], loadingExpandKeys = [] } = this.props;
          const { unitId, indent = 0, _status, $form: form } = record;
          const loading = loadingExpandKeys.includes(unitId);
          const isExpand = expandKeys.includes(unitId);
          const hasExpand = record.hasNextFlag === 1;
          const expandIcon = hasExpand && (
            <Icon
              className={classNames(styles['hpfm-organization-lazy-tree-expand-icon'], {
                [styles['hpfm-organization-lazy-tree-expand-icon-loading']]: loading,
              })}
              // eslint-disable-next-line no-nested-ternary
              type={loading ? 'loading' : isExpand ? 'minus-square-o' : 'plus-square-o'}
              onClick={loading ? undefined : () => this.handleTableExpand(!isExpand, record)}
            />
          );
          const isCreate = _status === 'create';
          const content = isCreate ? (
            <Form.Item>
              {form.getFieldDecorator('unitCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.organization.code').d('组织编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', { max: 30 }),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          ) : (
            unitCode
          );
          return (
            <span
              style={{ paddingLeft: indent * INDENT_SIZE, display: 'block' }}
              className={classNames({
                [styles['hpfm-organization-lazy-tree-no-child']]: hasExpand,
              })}
            >
              {expandIcon}
              {content}
            </span>
          );
        },
      },
      {
        dataIndex: 'unitName',
        title: intl.get('entity.organization.name').d('组织名称'),
        render: (unitName, record) => {
          const { _status, $form: form } = record;
          return _status === 'create' ? (
            <Form.Item>
              {form.getFieldDecorator('unitName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.organization.name').d('组织名称'),
                    }),
                  },
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', { max: 240 }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('entity.organization.name').d('组织名称')}
                  field="unitName"
                />
              )}
            </Form.Item>
          ) : (
            unitName
          );
        },
      },
      {
        dataIndex: 'unitTypeMeaning',
        title: intl.get('entity.organization.type').d('组织类型'),
        width: 130,
        render: (unitTypeMeaning, record) => {
          const { _status, $form: form } = record;
          const { unitType = [] } = this.props;
          return _status === 'create' ? (
            <Form.Item>
              {form.getFieldDecorator('unitTypeCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.organization.type').d('组织类型'),
                    }),
                  },
                ],
              })(
                <Select style={{ width: 100 }}>
                  {unitType.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            unitTypeMeaning
          );
        },
      },
      {
        dataIndex: 'orderSeq',
        title: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
        width: 110,
        render: (orderSeq, record) => {
          const { _status, $form: form } = record;
          return _status === 'create' ? (
            <Form.Item>
              {form.getFieldDecorator('orderSeq', {
                initialValue: 1,
              })(<InputNumber min={1} precision={0} />)}
            </Form.Item>
          ) : (
            orderSeq
          );
        },
      },
      {
        dataIndex: 'supervisorFlag',
        title: intl.get('hpfm.organization.model.unit.supervisorFlag').d('主管组织'),
        width: 90,
        render: (supervisorFlag, record) => {
          const { _status, $form: form } = record;
          return _status === 'create' ? (
            <Form.Item>
              {form.getFieldDecorator('supervisorFlag', {
                initialValue: 0,
              })(<Checkbox />)}
            </Form.Item>
          ) : (
            yesOrNoRender(supervisorFlag)
          );
        },
      },
      {
        key: 'operator',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 190,
        fixed: 'right',
        render: (_, record) => {
          const { _status, enabledFlag } = record;
          const operators = [];
          if (_status) {
            operators.push({
              key: 'clear',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.clean`,
                      type: 'button',
                      meaning: '组织架构维护-清除',
                    },
                  ]}
                  onClick={() => this.handleClearRecord(record)}
                >
                  {intl.get('hzero.common.button.clean').d('清除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.clean').d('清除'),
            });
          } else {
            switch (enabledFlag) {
              case 1:
                operators.push({
                  key: 'edit',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.edit`,
                          type: 'button',
                          meaning: '组织架构维护-编辑',
                        },
                      ]}
                      onClick={() => this.handleEditRecord(record)}
                    >
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.edit').d('编辑'),
                });
                operators.push({
                  key: 'new-child',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.addChildren`,
                          type: 'button',
                          meaning: '组织架构维护-新增下级',
                        },
                      ]}
                      onClick={() => this.handleAddRecordChild(record)}
                    >
                      {intl.get('hzero.common.button.addChildren').d('新增下级')}
                    </ButtonPermission>
                  ),
                  len: 4,
                  title: intl.get('hzero.common.button.addChildren').d('新增下级'),
                });
                operators.push({
                  key: 'disable',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.disable`,
                          type: 'button',
                          meaning: '组织架构维护-禁用',
                        },
                      ]}
                      onClick={() => this.handleDisabledRecord(record)}
                    >
                      {intl.get('hzero.common.button.disable').d('禁用')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.disable').d('禁用'),
                });
                operators.push({
                  key: 'assign-grade',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.assign`,
                          type: 'button',
                          meaning: '组织架构维护-分配部门',
                        },
                      ]}
                      onClick={() => this.handleGotoSubGradeRecord(record)}
                    >
                      {intl.get('hpfm.organization.view.option.assign').d('分配部门')}
                    </ButtonPermission>
                  ),
                  len: 4,
                  title: intl.get('hpfm.organization.view.option.assign').d('分配部门'),
                });
                operators.push({
                  key: 'assign-post',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.assign.post`,
                          type: 'button',
                          meaning: '组织架构维护-分配岗位',
                        },
                      ]}
                      onClick={() => this.handleGotoSubPostRecord(record)}
                    >
                      {intl.get('hpfm.organization.view.option.assign.post').d('分配岗位')}
                    </ButtonPermission>
                  ),
                  len: 4,
                  title: intl.get('hpfm.organization.view.option.assign.post').d('分配岗位'),
                });
                break;
              case 0:
                operators.push({
                  key: 'edit',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.edit`,
                          type: 'button',
                          meaning: '组织架构维护-编辑',
                        },
                      ]}
                      onClick={() => this.handleEditRecord(record)}
                    >
                      {intl.get('hzero.common.button.edit').d('编辑')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.edit').d('编辑'),
                });
                operators.push({
                  key: 'enable',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.enable`,
                          type: 'button',
                          meaning: '组织架构维护-启用',
                        },
                      ]}
                      onClick={() => this.handleEnableRecord(record)}
                    >
                      {intl.get('hzero.common.status.enable').d('启用')}
                    </ButtonPermission>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.status.enable').d('启用'),
                });
                operators.push({
                  key: 'assign-grade',
                  ele: (
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.assign.post`,
                          type: 'button',
                          meaning: '组织架构维护-分配岗位',
                        },
                      ]}
                      onClick={() => this.handleGotoSubPostRecord(record)}
                    >
                      {intl.get('hpfm.organization.view.option.assign.post').d('分配岗位')}
                    </ButtonPermission>
                  ),
                  len: 4,
                  title: intl.get('hpfm.organization.view.option.assign.post').d('分配岗位'),
                });
                break;
              default:
                break;
            }
          }
          return operatorRender(operators);
        },
      },
    ];
  }

  // EditDrawer

  /**
   * 保存 - 单条组织行数据修改后保存
   * @param {Object} values 修改后的数据
   */
  @Bind()
  handleDrawerOk(values) {
    const { saveEditData, organizationId } = this.props;
    saveEditData({
      tenantId: organizationId,
      values,
    }).then((res) => {
      if (res) {
        this.setState({
          drawerVisible: false,
          editRecord: {},
        });
        const { loadData } = this.props;
        loadData();
        notification.success();
      }
    });
  }

  /**
   * 编辑侧滑框隐藏
   */
  @Bind()
  handleDrawerCancel() {
    this.setState({
      drawerVisible: false,
      editRecord: {},
    });
  }

  render() {
    const {
      dataSource = [],
      expandKeys = [],
      loadingExpandKeys = [],
      organizationId,
      unitType = [],
      match,
      fetchOrgInfoLoading = false,
      saveEditDataLoading = false,
      saveAddDataLoading = false,
      forbidLineLoading = false,
      enabledLineLoading = false,
    } = this.props;
    const { drawerVisible = false, editRecord = {} } = this.state;
    const columns = this.getColumns();
    return (
      <div>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '组织架构维护-新建',
              },
            ]}
            onClick={this.handleCreateBtnClick}
            type="primary"
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '组织架构维护-保存',
              },
            ]}
            onClick={this.handleSaveBtnClick}
            loading={saveAddDataLoading}
            disabled={this.getSaveBtnDisabled()}
            icon="save"
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          pagination={false}
          rowKey="unitId"
          className={styles['hpfm-organization-lazy-tree']}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          expandedRowKeys={expandKeys}
          loading={
            (fetchOrgInfoLoading && isEmpty(loadingExpandKeys)) ||
            forbidLineLoading ||
            enabledLineLoading
          }
        />
        <EditDrawer
          tenantId={organizationId}
          unitType={unitType}
          loading={saveEditDataLoading}
          visible={drawerVisible}
          anchor="right"
          title={intl.get('hpfm.organization.view.message.edit').d('组织信息修改')}
          onCancel={this.handleDrawerCancel}
          onOk={this.handleDrawerOk}
          itemData={editRecord}
        />
      </div>
    );
  }
}
