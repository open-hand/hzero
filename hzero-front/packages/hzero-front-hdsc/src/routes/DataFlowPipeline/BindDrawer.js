import React, { useMemo, useEffect, useState } from 'react';
import { Table, Lov, DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { uniqBy } from 'lodash';
import { Button as ButtonPermission } from 'components/Permission';
import styles from './index.less';
import { BindTableDS } from '../../stores/DataFlowPipelineDS';

const BindDrawer = (props) => {
  const { path, pipelineStreamId, lovDs, EditBindTableDs } = props;

  lovDs.getField('code').set('multiple', true);

  const [preCheck, setPreCheck] = useState([]);
  const [initData, setInitData] = useState([]);
  const [btnDisabled, setDisabled] = useState(true);
  const columns = useMemo(
    () => [
      {
        name: 'streamCode',
      },
    ],
    []
  );

  const BindTableDs = useMemo(
    () =>
      new DataSet({
        ...BindTableDS(),
        events: {
          select: ({ dataSet }) => {
            setDisabled(dataSet.selected.length === 0);
          },
          unSelect: ({ dataSet }) => {
            setDisabled(dataSet.selected.length === 0);
          },
          selectAll: () => {
            setDisabled(false);
          },
          unSelectAll: () => {
            setDisabled(true);
          },
        },
        feedback: {
          loadSuccess: (resp) => {
            if (Array.isArray(resp) && lovDs.current) {
              lovDs.current.set('code', resp);
              setInitData(resp);
            }
          },
        },
      }),
    []
  );
  useEffect(() => {
    handleQuery();
  }, []);

  // 查询表格数据
  const handleQuery = async () => {
    BindTableDs.setQueryParameter('pipelineStreamId', pipelineStreamId);
    await BindTableDs.query();
    setPreCheck(BindTableDs.toData());
    EditBindTableDs.relationsList = BindTableDs.toData();
    EditBindTableDs.pipelineStreamIds = [];
  };

  // 添加事件
  const onChange = (value) => {
    const list = value || [];
    const obj = {};
    const deleteObj = {};

    list.forEach((item) => {
      obj[item.streamId] = item.streamCode;
    });
    setPreCheck(list);
    preCheck.forEach((item) => {
      if (!obj[item.streamId]) {
        deleteObj[item.streamId] = item.streamCode;
      }
    });
    const data = BindTableDs.toData() || [];
    const filterList = data.filter((item) => !deleteObj[item.streamId]);
    BindTableDs.loadData(uniqBy([...filterList, ...list], 'streamId'));
    EditBindTableDs.relationsList = BindTableDs.toData();
    comparisonData();
  };

  // 删除
  const handleDelete = () => {
    BindTableDs.remove(BindTableDs.selected);
    const data = BindTableDs.toData();
    BindTableDs.loadData(data);
    lovDs.current.set('code', data);
    EditBindTableDs.relationsList = data;
    comparisonData();
  };

  // 拖拽
  const onDragEnd = () => {
    EditBindTableDs.relationsList = BindTableDs.toData();
  };

  // 对比删除数据
  const comparisonData = () => {
    const data = BindTableDs.toData() || [];
    const comparison = {};
    const deleteList = [];
    data.forEach((item) => {
      comparison[item.streamId] = item.streamCode;
    });
    initData.forEach((item) => {
      if (!comparison[item.streamId]) {
        deleteList.push(item.pipelineStreamId);
      }
    });
    EditBindTableDs.pipelineStreamIds = deleteList;
  };

  return (
    <>
      <div className={styles.buttonContainer}>
        <Lov
          name="code"
          mode="button"
          icon="add"
          placeholder={intl.get('hzero.common.button.add').d('新增')}
          dataSet={lovDs}
          clearButton={false}
          onChange={onChange}
        >
          {intl.get('hzero.common.button.add').d('新增')}
        </Lov>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}-unbind-detailDelete`,
              type: 'button',
              meaning: '数据流管道-详情页删除绑定组合',
            },
          ]}
          icon="delete"
          onClick={handleDelete}
          disabled={btnDisabled}
        >
          {intl.get('hzero.common.button.delete').d('删除')}
        </ButtonPermission>
      </div>
      <Table dataSet={BindTableDs} columns={columns} dragRow onDragEnd={onDragEnd} />
    </>
  );
};
export default BindDrawer;
