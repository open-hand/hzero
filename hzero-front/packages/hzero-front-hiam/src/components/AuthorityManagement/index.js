/**
 *  数据权限维度动态渲染 __组件
 * @date: 2019-9-11
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { connect } from 'dva';
import React, { useEffect, useRef, useState } from 'react';
import { Button, Form, Input, message, Modal, Switch, Table, Tooltip, Row, Col } from 'hzero-ui';
import lodash from 'lodash';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { createPagination } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

import { queryLov } from 'services/api';

import AddDataModal from './AddDataModal';

function AuthorityTab(props) {
  const dataSourceRef = useRef(null);

  const [selectRows, setSelectRows] = useState([]);
  const [addModalVisible, setAddModalVisible] = useState(false);
  const [switchLoading, setSwitchLoading] = useState(false);
  const [list, setList] = useState([]);
  const [head, setHead] = useState({});
  const [pagination, setPagination] = useState({});
  const [lov, setLov] = useState({});
  const [lovModal, setLovModal] = useState(false);

  const {
    form,
    code,
    name,
    tenantId = undefined,
    dispatch,
    queryParams, // 额外参数
    authorityTypeCode, // 权限类型
    addLoading,
    fetchLoading,
    isSecGrp = false, // 来源是否为角色分配安全组
    isAccount = false, // 是否来自子账户的分配安全组页面
  } = props;

  const columns = [
    {
      title: intl
        .get('hiam.authorityManagement.model.authorityManagement.name', { name })
        .d(`${name}名称`),
      dataIndex: 'dataName',
    },
  ];

  /**
   *查询数据
   *
   * @param {Object} pageData
   */
  const fetchData = (pageData = {}) => {
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const payload = {
          authorityTypeCode,
          ...fieldsValue,
          ...queryParams,
          ...pageData,
        };
        if (isSecGrp) {
          if (!isAccount) {
            dispatch({
              type: `authorityDimension/querySecGrpAuthorityData`,
              payload,
            }).then((res) => {
              if (res) {
                setList(res.content);
                setPagination(createPagination(res));
              }
            });
          } else {
            dispatch({
              type: `authorityDimension/queryAccountSecGrpAuthorityData`,
              payload,
            }).then((res) => {
              if (res) {
                setList(res.content);
                setPagination(createPagination(res));
              }
            });
          }
        } else {
          dispatch({
            type: `authorityDimension/query${
              !lodash.isUndefined(queryParams.userId) ? `User` : `Role`
            }AuthorityData`,
            payload,
          }).then((res) => {
            if (res) {
              setHead(res.userAuthority || res.roleAuthData);
              setList(
                res.userAuthorityLineList
                  ? res.userAuthorityLineList.content
                  : res.roleAuthDataLineList.content
              );
              setPagination(
                createPagination(res.userAuthorityLineList || res.roleAuthDataLineList)
              );
            }
          });
        }
      }
    });
  };

  useEffect(() => {
    queryLov({ viewCode: code }).then((res) => {
      const lovInfo = { ...res };
      if (!lodash.isEmpty(lovInfo)) {
        const { viewCode: hasCode } = lovInfo;
        if (hasCode) {
          setLov(lovInfo);
        } else {
          message.error(
            intl.get('hzero.common.components.lov.notification.undefined').d('值集视图未定义!')
          );
          setLovModal(true);
        }
      }
    });
    fetchData();
  }, []);

  /**
   * 添加数据
   * @param {Array} addRows 选择的数据
   */
  const addData = (addRows) => {
    dispatch({
      type: `authorityDimension/add${
        !lodash.isUndefined(queryParams.userId) ? `User` : `Role`
      }AuthorityData`,
      payload: {
        ...queryParams,
        authorityTypeCode,
        authData: head,
        authDataLineList: addRows,
      },
    }).then((response) => {
      if (response) {
        onHideAddModal();
        notification.success();
        refresh();
      }
    });
  };

  /**
   *删除方法
   */
  const remove = () => {
    const onOk = () => {
      dispatch({
        type: `authorityDimension/delete${
          !lodash.isUndefined(queryParams.userId) ? `User` : `Role`
        }AuthorityData`,
        payload: {
          ...queryParams,
          deleteRows: selectRows,
        },
      }).then((response) => {
        if (response) {
          refresh();
          notification.success();
        }
      });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  };

  /**
   *刷新
   */
  const refresh = () => {
    fetchData();
    setSelectRows([]);
  };

  /**
   * 表格勾选
   * @param {null} _ 占位
   * @param {object} selectedRow 选中行
   */
  const onSelectChange = (_, selectedRows) => {
    setSelectRows(selectedRows);
  };

  const rowSelection = isSecGrp
    ? null
    : {
        onChange: onSelectChange,
        selectedRowKeys: selectRows.map((n) => n.dataId),
      };

  /**
   * 展示弹出框
   */
  const onShowAddModal = () => {
    if (!lovModal) {
      setAddModalVisible(true);
    } else {
      message.error(
        intl.get('hzero.common.components.lov.notification.undefined').d('值集视图未定义!')
      );
    }
  };

  /**
   * 隐藏弹出框
   */
  const onHideAddModal = () => {
    dataSourceRef.current.state.addRows = [];
    setAddModalVisible(false);
  };

  /**
   *点击查询按钮事件
   */
  const queryValue = () => {
    fetchData();
    setSelectRows([]);
  };

  /**
   *分页change事件
   */
  const handleTableChange = (page = {}) => {
    fetchData({
      page,
    });
  };

  /**
   * 表单重置
   */
  const handleFormReset = () => {
    form.resetFields();
  };

  /**
   *点击加入全部后触发事件
   *
   * @param {Boolean} checked switch的value值
   */
  const includeAllFlag = (checked) => {
    setSwitchLoading(true);
    dispatch({
      type: `authorityDimension/add${
        !lodash.isUndefined(queryParams.userId) ? `User` : `Role`
      }AuthorityData`,
      payload: {
        ...queryParams,
        authorityTypeCode,
        authData: {
          ...head,
          includeAllFlag: checked ? 1 : 0,
        },
        authDataLineList: [],
      },
    }).then((response) => {
      if (response) {
        refresh();
        notification.success();
        setSwitchLoading(false);
      }
    });
  };

  const addModalOptions = {
    rowKey: 'dataId',
    title: intl
      .get('hiam.authorityManagement.model.authorityManagement.select', { name })
      .d(`选择${name}`),
    lov,
    tenantId,
    confirmLoading: addLoading,
    modalVisible: addModalVisible,
    onShowAddModal,
    onHideAddModal,
    addData,
    ref: dataSourceRef,
  };

  /**
   *渲染查询结构
   *
   * @returns
   */
  const renderForm = () => (
    <Form>
      <Row type="flex" gutter={24} align="bottom">
        <Col span={12}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl
              .get('hiam.authorityManagement.model.authorityManagement.name', { name })
              .d(`${name}名称`)}
          >
            {form.getFieldDecorator('dataName')(<Input />)}
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item {...SEARCH_FORM_ITEM_LAYOUT}>
            <Button style={{ marginRight: 8 }} onClick={handleFormReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button type="primary" onClick={() => queryValue()} htmlType="submit">
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Col>
      </Row>
    </Form>
  );

  /**
   *渲染查询结构
   *
   * @returns
   */
  // const renderColumns = () => {
  //   // return (

  //   // );
  // };

  return (
    <div>
      <div className="table-list-search">{renderForm()}</div>
      {!isSecGrp && (
        <div style={{ textAlign: 'right' }}>
          {!head.includeAllFlag && (
            <>
              <Button style={{ margin: '0 8px 16px 0' }} onClick={() => onShowAddModal()}>
                {intl
                  .get('hiam.authorityManagement.model.authorityManagement.create', { name })
                  .d(`新建${name}权限`)}
              </Button>
              <Button
                style={{ margin: '0 8px 16px 0' }}
                disabled={selectRows.length <= 0}
                onClick={() => remove()}
              >
                {intl
                  .get('hiam.authorityManagement.model.authorityManagement.delete', { name })
                  .d(`删除${name}权限`)}
              </Button>
            </>
          )}
          <div style={{ display: 'inline-block', margin: '0 8px 16px 0' }}>
            <span style={{ marginRight: '8px' }}>
              {intl.get('hiam.authority.view.message.label').d('加入全部:')}
            </span>
            <Tooltip
              title={intl
                .get('hiam.authorityManagement.view.message.title.tooltip.dg')
                .d('“加入全部”即将所有数据组权限自动添加至当前账户，无需再手工添加。')}
              placement="right"
            >
              <Switch
                loading={switchLoading || fetchLoading}
                checked={!!head.includeAllFlag}
                onChange={includeAllFlag}
              />
            </Tooltip>
          </div>
        </div>
      )}
      <Table
        bordered
        rowKey="dataId"
        dataSource={list}
        columns={columns}
        loading={fetchLoading}
        pagination={pagination}
        rowSelection={rowSelection}
        onChange={handleTableChange}
      />
      <AddDataModal {...addModalOptions} />
    </div>
  );
}

function mapStateToProps({ authorityDimension, loading }) {
  return {
    authorityDimension,
    addLoading:
      loading.effects[`authorityDimension/addUserAuthorityData`] ||
      loading.effects[`authorityDimension/addRoleAuthorityData`],
    fetchLoading:
      loading.effects[`authorityDimension/queryUserAuthorityData`] ||
      loading.effects[`authorityDimension/queryRoleAuthorityData`] ||
      loading.effects[`authorityDimension/querySecGrpAuthorityData`] ||
      loading.effects[`authorityDimension/queryAccountSecGrpAuthorityData`],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(
  Form.create({ fieldNameProp: null })(
    formatterCollections({ code: ['hiam.authorityManagement', 'entity.tag'] })(AuthorityTab)
  )
);
