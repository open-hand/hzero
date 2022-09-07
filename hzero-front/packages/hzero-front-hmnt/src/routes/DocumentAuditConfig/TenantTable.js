import React, { useMemo } from 'react';
import intl from 'utils/intl';
import { DataSet, Table, Button } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';

import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import {
  tenChildTableDS,
  tenParentTableDS,
  saveAuditContentDS,
} from '../../stores/DocumentAuditConfigDS';
import styles from './index.less';

const PlatformTable = (props) => {
  const { currentData = {}, isView = false } = props;
  const { auditDocumentId } = currentData;
  let currentRecord;

  const saveAuditContentDs = useMemo(() => new DataSet(saveAuditContentDS()), []);
  const tenChildTableDs = useMemo(
    () =>
      new DataSet({
        ...tenChildTableDS(),
        events: {
          load: ({ dataSet }) => {
            dataSet.forEach((record) => {
              if (record.get('selected') === 1) {
                dataSet.select(record);
              }
            });
          },
          selectAll: ({ dataSet }) => {
            dataSet.forEach((record) => {
              record.set('selected', 1);
            });
            if (currentRecord) {
              currentRecord.set('selected', 1);
              tenParentTableDs.select(currentRecord);
            }
          },
          select: ({ record }) => {
            record.set('selected', 1);
            if (currentRecord) {
              currentRecord.set('selected', 1);
            }
            tenParentTableDs.select(currentRecord);
          },
          unSelect: ({ record }) => {
            record.set('selected', 0);
          },
          unSelectAll: ({ dataSet }) => {
            dataSet.forEach((record) => {
              record.set('selected', 0);
            });
          },
        },
      }),
    []
  );

  const tenParentTableDs = useMemo(
    () =>
      new DataSet({
        ...tenParentTableDS(),
        events: {
          load: ({ dataSet }) => {
            dataSet.forEach((record) => {
              if (record.get('selected') === 1) {
                dataSet.select(record);
              }
            });
            const data = dataSet.toData() || [];
            if (data.length > 0) {
              const { dataConfigList = [] } = data[0] || {};
              // eslint-disable-next-line prefer-destructuring
              currentRecord = dataSet.records[0];
              tenChildTableDs.loadData(dataConfigList);
            }
          },
          selectAll: ({ dataSet }) => {
            dataSet.forEach((record) => {
              record.set('selected', 1);
            });
          },
          unSelectAll: ({ dataSet }) => {
            dataSet.forEach((record) => {
              const dataConfigList = record.get('dataConfigList') || [];
              const list = dataConfigList.map((n) => {
                return {
                  ...n,
                  selected: 0,
                };
              });
              record.set('dataConfigList', list);
              record.set('selected', 0);
            });
            if (currentRecord) {
              const dataConfigList = currentRecord.get('dataConfigList') || [];
              tenChildTableDs.loadData(dataConfigList);
            }
          },
          select: ({ record }) => {
            record.set('selected', 1);
          },
          unSelect: ({ record }) => {
            const dataConfigList = record.get('dataConfigList') || [];
            const list = dataConfigList.map((n) => {
              return {
                ...n,
                selected: 0,
              };
            });
            record.set('dataConfigList', list);
            record.set('selected', 0);
            if (record.get('auditOpConfigId') === currentRecord.get('auditOpConfigId')) {
              tenChildTableDs.loadData(list);
            }
          },
        },
      }),
    []
  );

  React.useEffect(() => {
    queryData();
  }, []);

  /**
   * 查询头行信息
   */
  const queryData = async () => {
    tenParentTableDs.auditDocumentId = auditDocumentId;
    await tenParentTableDs.query();
  };

  const parentColumns = React.useMemo(
    () => [
      { name: 'auditTypeMeaning' },
      {
        name: 'operationalContent',
      },
    ],
    []
  );

  const childColumns = React.useMemo(
    () => [
      {
        name: 'tableName',
      },
      {
        name: 'displayName',
      },
    ],
    []
  );

  /**
   * 点击表格行
   */
  const handleClick = (record) => {
    const data = record.toData() || {};
    const { dataConfigList = [] } = data;
    if (currentRecord && record.get('auditOpConfigId') !== currentRecord.get('auditOpConfigId')) {
      const list = tenChildTableDs.toData() || [];
      const configList = list.map((item) => {
        const { __dirty, ...other } = item;
        return other;
      });
      currentRecord.set('dataConfigList', configList);
    }
    currentRecord = record;
    tenChildTableDs.loadData(dataConfigList);
  };

  /**
   * 保存
   */
  const handleSave = async () => {
    const list = tenChildTableDs.toData() || [];
    const configList = list.map((item) => {
      const { __dirty, ...other } = item;
      return other;
    });
    if (currentRecord) {
      currentRecord.set('dataConfigList', configList);
    }
    const data = tenParentTableDs.toData();
    saveAuditContentDs.auditDocumentId = auditDocumentId;
    saveAuditContentDs.create({ opDataConfig: data }, 0);
    try {
      const validate = await saveAuditContentDs.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    tenParentTableDs.query();
    tenChildTableDs.loadData([]);
  };

  return (
    <>
      <Card
        style={{ marginTop: 20 }}
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={
          <h3>{intl.get('hmnt.documentAuditConfig.view.title.auditContent').d('审计内容')}</h3>
        }
      >
        <div className="table-operator">
          {!isView && (
            <Button onClick={handleSave}>{intl.get('hzero.common.button.save').d('保存')}</Button>
          )}
        </div>
        <div className={styles['tables-wrap']}>
          <Table
            dataSet={tenParentTableDs}
            columns={parentColumns}
            onRow={({ record }) => ({
              onClick: () => handleClick(record),
            })}
          />
          <Table
            dataSet={tenChildTableDs}
            columns={childColumns}
            className={styles['right-table']}
          />
        </div>
      </Card>
    </>
  );
};

export default PlatformTable;
