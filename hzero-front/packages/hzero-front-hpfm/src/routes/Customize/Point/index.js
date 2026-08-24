import React, { useState, useRef, useEffect } from 'react';
import { connect } from 'dva';
import { Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import Refresh from './Refresh';

const onCell = () => {
  return {
    style: {
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      whiteSpace: 'nowrap',
    },
    onClick: e => {
      const { target } = e;
      if (target.style.whiteSpace === 'normal') {
        target.style.whiteSpace = 'nowrap';
      } else {
        target.style.whiteSpace = 'normal';
      }
    },
  };
};

function Point(props) {
  const [freshVisible, setFreshVisible] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [selectRows, setSelectRows] = useState([]);

  const {
    match,
    dispatch,
    fetchPointLoading = false,
    refreshLoading = false,
    deleteLoading = false,
    customize: { pointList = [], pointPagination = {} } = {},
  } = props;

  const filterFormRef = useRef(null);

  const columns = [
    {
      title: intl.get('hpfm.customize.model.customize.point.serviceName').d('服务名'),
      dataIndex: 'serviceName',
      width: 150,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.packageName').d('包名'),
      onCell,
      dataIndex: 'packageName',
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.className').d('类名'),
      dataIndex: 'className',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.methodName').d('方法名'),
      dataIndex: 'methodName',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.methodArgs').d('方法参数列表'),
      dataIndex: 'methodArgs',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.methodDescription').d('方法描述'),
      dataIndex: 'methodDescription',
      onCell,
      width: 120,
    },
  ];

  const fetchPointList = params => {
    const { tenantId } = props;
    const filterForm = handleBindRef(filterFormRef);
    const filterValue = filterForm.getFieldsValue();
    dispatch({
      type: 'customize/fetchPointList',
      payload: { ...filterValue, page: pointPagination, tenantId, ...params },
    });
  };

  const handleSearch = () => {
    fetchPointList({ page: {} });
  };

  const handleBindRef = ref => {
    if (ref.current) {
      return ref.current;
    }
    return {};
  };

  const handlePagination = page => {
    fetchPointList({ page });
  };

  useEffect(() => {
    fetchPointList();
  }, []);

  const showFresh = () => {
    setFreshVisible(true);
  };

  const hideFresh = () => {
    setFreshVisible(false);
  };

  const refresh = data => {
    dispatch({
      type: 'customize/refreshPoint',
      payload: data,
    }).then(res => {
      if (res) {
        hideFresh();
        handleSearch();
        notification.success();
      }
    });
  };

  const deletePoint = () => {
    dispatch({
      type: 'customize/deletePoint',
      payload: selectRows,
    }).then(res => {
      if (res) {
        notification.success();
        fetchPointList();
      }
    });
  };

  const handleSelectTable = (keys, rows) => {
    setSelectedRowKeys(keys);
    setSelectRows(rows);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: handleSelectTable,
  };

  return (
    <React.Fragment>
      <div className="table-list-search">
        <FilterForm onSearch={handleSearch} ref={filterFormRef} />
      </div>
      <div className="table-operator">
        <ButtonPermission
          type="primary"
          permissionList={[
            {
              code: `${match.path}.button.point.refresh`,
              type: 'button',
              meaning: '个性化范围-刷新服务',
            },
          ]}
          onClick={showFresh}
        >
          {intl.get('hpfm.customize.button.refresh').d('刷新服务')}
        </ButtonPermission>
        <ButtonPermission
          type="default"
          loading={deleteLoading}
          disabled={selectedRowKeys.length === 0}
          permissionList={[
            {
              code: `${match.path}.button.point.delete`,
              type: 'button',
              meaning: '个性化范围-删除',
            },
          ]}
          onClick={deletePoint}
        >
          {intl.get('hzero.common.button.delete').d('删除')}
        </ButtonPermission>
      </div>
      <Table
        bordered
        rowKey="pointId"
        rowSelection={rowSelection}
        loading={fetchPointLoading}
        dataSource={pointList}
        columns={columns}
        pagination={pointPagination}
        onChange={handlePagination}
      />
      <Refresh
        title={intl.get('hpfm.customize.view.title.refresh').d('刷新服务')}
        visible={freshVisible}
        loading={refreshLoading}
        onCancel={hideFresh}
        onOk={refresh}
      />
    </React.Fragment>
  );
}

function mapStateToProps({ customize, loading }) {
  return {
    customize,
    fetchPointLoading: loading.effects['customize/fetchPointList'],
    refreshLoading: loading.effects['customize/refreshPoint'],
    deleteLoading: loading.effects['customize/deletePoint'],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(formatterCollections({ code: ['hpfm.customize'] })(Point));
