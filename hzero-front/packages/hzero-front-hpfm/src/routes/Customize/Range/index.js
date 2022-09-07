import React, { useState, useRef, useEffect } from 'react';
import { connect } from 'dva';
import { Table, Popconfirm } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { isTenantRoleLevel } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import Editor from './Editor';

function Range(props) {
  const [editorVisible, setEditorVisible] = useState(false);

  const {
    match,
    dispatch,
    isSiteFlag,
    fetchRangeLoading = false,
    detailLoading = false,
    deletePointLoading = false,
    deleteRuleLoading = false,
    editorLoading = false,
    applyLoading = false,
    customize: {
      rangeList = [],
      rangeDetail = {},
      rangePagination = {},
      pointToRangeList = [],
      ruleToRangeList = [],
    },
  } = props;

  const fetchRangeList = (params) => {
    const filterForm = handleBindRef(filterFormRef);
    const filterValue = filterForm.getFieldsValue();
    dispatch({
      type: 'customize/fetchRangeList',
      payload: { ...filterValue, page: rangePagination, ...params },
    });
  };

  useEffect(() => {
    fetchRangeList();
  }, []);

  const filterFormRef = useRef(null);

  const handleSearch = () => {
    fetchRangeList({ page: {} });
  };

  const handleBindRef = (ref) => {
    if (ref.current) {
      return ref.current;
    }
    return {};
  };

  const handlePagination = (page) => {
    fetchRangeList({ page });
  };

  const deleteRange = (record) => {
    dispatch({
      type: 'customize/deleteRange',
      payload: record,
    }).then((res) => {
      if (res) {
        fetchRangeList();
        notification.success();
      }
    });
  };

  const applyRules = (record) => {
    dispatch({
      type: 'customize/applyRules',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  };

  const showEditor = ({ rangeId }) => {
    if (rangeId !== undefined) {
      dispatch({
        type: 'customize/fetchRangeDetail',
        payload: { rangeId },
      }).then((res) => {
        if (res) {
          // 得到详情数据后获取对应的切入点、规则数据
          dispatch({
            type: 'customize/fetchRuleListToRange',
            payload: { rangeId },
          });
          dispatch({
            type: 'customize/fetchPointListToRange',
            payload: { rangeId },
          });
        }
      });
    }
    setEditorVisible(true);
  };

  const hideEditor = () => {
    setEditorVisible(false);
    dispatch({
      type: 'customize/updateState',
      payload: { rangeDetail: {}, pointToRangeList: [], ruleToRangeList: [] },
    });
  };

  const handleOk = (data) => {
    const pointList = pointToRangeList
      .filter((item) => item.isCreate)
      .map((item) => {
        const { rangePointId, ...others } = item;
        return others;
      });
    const ruleList = ruleToRangeList
      .filter((item) => item.isCreate)
      .map((item) => {
        const { rangeRuleId, ...others } = item;
        return others;
      });

    dispatch({
      type: `customize/${data.rangeId === undefined ? 'createRange' : 'updateRange'}`,
      payload: {
        ...data,
        rangePoints: pointList,
        rangeRules: ruleList,
      },
    }).then((res) => {
      if (res) {
        hideEditor();
        fetchRangeList();
        notification.success();
      }
    });
  };

  const onCell = () => {
    return {
      style: {
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  };

  const columns = [
    isSiteFlag && {
      title: intl.get('hpfm.customize.model.customize.range.tenantName').d('租户'),
      width: 200,
      dataIndex: 'tenantName',
    },
    {
      title: intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称'),
      dataIndex: 'description',
      onCell,
    },
    {
      title: intl.get('hzero.common.status.enable').d('启用'),
      dataIndex: 'enabledFlag',
      width: 100,
      render: enableRender,
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      dataIndex: 'editor',
      width: 180,
      render: (val, record) => {
        const operators = [
          {
            key: 'apply',
            ele: (
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.apply').d('是否应用该规则？')}
                onConfirm={() => applyRules(record)}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.range.use`,
                      type: 'button',
                      meaning: '个性化范围-应用规则',
                    },
                  ]}
                >
                  {intl.get('hpfm.customize.range.button.use').d('应用规则')}
                </ButtonPermission>
              </Popconfirm>
            ),
            len: 4,
            title: intl.get('hpfm.customize.range.button.use').d('应用规则'),
          },
          {
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.range.edit`,
                    type: 'button',
                    meaning: '个性化范围-编辑',
                  },
                ]}
                onClick={() => showEditor(record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          },
          {
            key: 'edit',
            ele: (
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                onConfirm={() => deleteRange(record)}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.range.delete`,
                      type: 'button',
                      meaning: '个性化范围-删除',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              </Popconfirm>
            ),
            len: 2,
            title: intl.get('hzero.common.button.delete').d('删除'),
          },
        ];
        return operatorRender(operators, record);
      },
    },
  ].filter(Boolean);

  return (
    <React.Fragment>
      <div className="table-list-search">
        <FilterForm onSearch={handleSearch} ref={filterFormRef} isSiteFlag={isSiteFlag} />
      </div>
      <div className="table-operator">
        <ButtonPermission
          type="primary"
          permissionList={[
            {
              code: `${match.path}.button.range.create`,
              type: 'button',
              meaning: '个性化范围-新建',
            },
          ]}
          onClick={showEditor}
        >
          {intl.get('hpfm.customize.button.create').d('新建')}
        </ButtonPermission>
      </div>
      <Table
        bordered
        rowKey="rangeId"
        loading={fetchRangeLoading || applyLoading}
        dataSource={rangeList}
        columns={columns}
        pagination={rangePagination}
        onChange={handlePagination}
      />
      <Editor
        dispatch={dispatch}
        title={
          rangeDetail.rangeId !== undefined
            ? intl.get('hpfm.customize.view.title.range.edit').d('编辑范围')
            : intl.get('hpfm.customize.view.title.range.create').d('新建范围')
        }
        isSiteFlag={isSiteFlag}
        deletePointLoading={deletePointLoading}
        deleteRuleLoading={deleteRuleLoading}
        pointToRangeList={pointToRangeList}
        ruleToRangeList={ruleToRangeList}
        visible={editorVisible}
        loading={editorLoading}
        initLoading={detailLoading}
        initData={rangeDetail}
        onCancel={hideEditor}
        onOk={handleOk}
      />
    </React.Fragment>
  );
}

function mapStateToProps({ customize, loading }) {
  return {
    customize,
    isSiteFlag: !isTenantRoleLevel(),
    fetchRangeLoading: loading.effects['customize/fetchRangeList'],
    detailLoading: loading.effects['customize/fetchRangeDetail'],
    deletePointLoading: loading.effects['customize/deletePointToRange'],
    deleteRuleLoading: loading.effects['customize/deleteRuleToRange'],
    applyLoading: loading.effects['customize/applyRules'],
    editorLoading:
      loading.effects['customize/createRange'] || loading.effects['customize/updateRange'],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(formatterCollections({ code: ['hpfm.customize', 'hpfm.dataGroup'] })(Range));
