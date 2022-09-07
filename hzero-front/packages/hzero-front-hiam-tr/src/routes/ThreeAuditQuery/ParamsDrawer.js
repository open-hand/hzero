import React from 'react';
import intl from 'utils/intl';
import { Spin, DataSet } from 'choerodon-ui/pro';
import { Table } from 'choerodon-ui';
import { mapKeys, isEmpty, isString } from 'lodash';

import { detailDs as DetailDs } from '../../stores/ThreeAuditQueryDS';

const Drawer = (props) => {
  const detailDs = React.useMemo(() => new DataSet(DetailDs()), []);

  const [arrList, setArrList] = React.useState([]);

  const { currentEditData, editFlag } = props;

  React.useEffect(() => {
    queryData();
  }, []);

  /**
   * 查询头行信息
   */
  const queryData = async () => {
    const { logLineId } = currentEditData;
    detailDs.logLineId = logLineId;
    await detailDs.query().then((res) => {
      const newArrList = [];
      if (res.logContent) {
        const logContent = JSON.parse(res.logContent);
        mapKeys(logContent, (value, key) => {
          if (!isEmpty(value)) {
            if (isString(value)) {
              newArrList.push({ field: key, value });
            } else {
              newArrList.push({ field: key, value: JSON.stringify(value) });
            }
          }
        });
        setArrList(editFlag ? newArrList : { arrList: logContent });
      }
    });
  };

  const columns = [
    {
      title: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.field').d('参数名称'),
      width: 180,
      dataIndex: 'field',
    },
    {
      title: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.value').d('参数值'),
      width: 180,
      onCell: () => onCell(),
      dataIndex: 'value',
    },
  ];

  const onCell = () => {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onDoubleClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  };

  return (
    <>
      <Spin dataSet={detailDs}>
        {editFlag ? (
          <Table
            bordered
            rowKey="Id"
            dataSource={arrList}
            columns={columns}
            pagination={false}
            filterBar={false}
          />
        ) : (
          <pre>{JSON.stringify(arrList, 0, 2)}</pre>
        )}
      </Spin>
    </>
  );
};

export default Drawer;
