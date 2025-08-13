import React, { useState, useRef, useEffect } from 'react';
import { connect } from 'dva';
import { Table, Popconfirm } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { isTenantRoleLevel } from 'utils/utils';
import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import Editor from './Editor';

function Rule(props) {
  const [editorVisible, setEditorVisible] = useState(false);

  const {
    match,
    dispatch,
    isSiteFlag,
    fetchRuleLoading = false,
    editorLoading = false,
    deleteLoading = false,
    detailLoading = false,
    customize: {
      ruleList = [],
      ruleDetail = {},
      rulePagination = {},
      typeCodeList = [],
      rulePositionList = [],
    },
  } = props;

  const fetchRuleList = (params) => {
    const filterForm = handleBindRef(filterFormRef);
    const filterValue = filterForm.getFieldsValue();
    dispatch({
      type: 'customize/fetchRuleList',
      payload: { ...filterValue, page: rulePagination, ...params },
    });
  };

  useEffect(() => {
    fetchRuleList();
  }, []);

  const filterFormRef = useRef(null);

  const handleSearch = () => {
    fetchRuleList({ page: {} });
  };

  const handleBindRef = (ref) => {
    if (ref.current) {
      return ref.current;
    }
    return {};
  };

  const handlePagination = (page) => {
    fetchRuleList({ page });
  };

  const showEditor = (record) => {
    if (record.ruleId !== undefined) {
      dispatch({
        type: 'customize/fetchRuleDetail',
        payload: { ruleId: record.ruleId },
      });
    }
    if (typeCodeList.length === 0 || rulePositionList.length === 0) {
      dispatch({
        type: 'customize/fetchValueListToRule',
      });
    }
    setEditorVisible(true);
  };

  const hideEditor = () => {
    setEditorVisible(false);
    dispatch({
      type: 'customize/updateState',
      payload: { ruleDetail: {} },
    });
  };

  const handleOk = (data) => {
    dispatch({
      type: `customize/${data.ruleId === undefined ? 'createRule' : 'updateRule'}`,
      payload: data,
    }).then((res) => {
      if (res) {
        hideEditor();
        fetchRuleList();
        notification.success();
      }
    });
  };

  const deleteRule = (record) => {
    dispatch({
      type: 'customize/deleteRule',
      payload: record,
    }).then((res) => {
      if (res) {
        fetchRuleList();
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
      title: intl.get('hpfm.customize.model.customize.rule.tenantName').d('租户'),
      dataIndex: 'tenantName',
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.rule.ruleCode').d('规则编码'),
      dataIndex: 'ruleCode',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.rule.ruleName').d('规则名称'),
      dataIndex: 'ruleName',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.rule.typeCode').d('类别'),
      dataIndex: 'typeCode',
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.rule.rulePositionMeaning').d('位置'),
      dataIndex: 'rulePositionMeaning',
    },
    {
      title: intl.get('hpfm.customize.model.customize.rule.syncFlag').d('同步'),
      dataIndex: 'syncFlag',
      width: 120,
      render: yesOrNoRender,
    },
    {
      title: intl.get('hzero.common.status.enable').d('启用'),
      dataIndex: 'enabledFlag',
      width: 120,
      render: enableRender,
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      dataIndex: 'editor',
      width: 150,
      render: (val, record) => {
        const operators = [
          {
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.rule.edit`,
                    type: 'button',
                    meaning: '个性化规则-编辑',
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
            key: 'delete',
            ele: (
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                onConfirm={() => deleteRule(record)}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.rule.delete`,
                      type: 'button',
                      meaning: '个性化规则-删除',
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
              code: `${match.path}.button.rule.create`,
              type: 'button',
              meaning: '个性化规则-新建',
            },
          ]}
          onClick={showEditor}
        >
          {intl.get('hpfm.customize.button.create').d('新建')}
        </ButtonPermission>
      </div>
      <Table
        bordered
        rowKey="ruleId"
        loading={fetchRuleLoading || deleteLoading}
        dataSource={ruleList}
        columns={columns}
        pagination={rulePagination}
        onChange={handlePagination}
      />
      <Editor
        title={
          ruleDetail.ruleId !== undefined
            ? intl.get('hpfm.customize.view.title.rule.edit').d('编辑规则')
            : intl.get('hpfm.customize.view.title.rule.create').d('新建规则')
        }
        isSiteFlag={isSiteFlag}
        typeCodeList={typeCodeList}
        rulePositionList={rulePositionList}
        visible={editorVisible}
        loading={editorLoading}
        initLoading={detailLoading}
        initData={ruleDetail}
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
    fetchRuleLoading: loading.effects['customize/fetchRuleList'],
    editorLoading:
      loading.effects['customize/updateRule'] || loading.effects['customize/createRule'],
    deleteLoading: loading.effects['customize/deleteRule'],
    detailLoading: loading.effects['customize/fetchRuleDetail'],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(formatterCollections({ code: ['hpfm.customize'] })(Rule));
