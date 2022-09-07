import React, { useMemo, useEffect, useState } from 'react';
import { Form, TextField, Table, Lov, Switch, DataSet, Modal } from 'choerodon-ui/pro';
import { sortBy, isEmpty } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';

import {
  outputDS,
  inputDS,
  lovInputDS,
  lovOutputDS,
  EditBindTableDS,
  DeleteBindTableDS,
} from '../../stores/DataFlowPipelineDS';
import styles from './index.less';
import BindDrawer from './BindDrawer';

const Drawer = (props) => {
  const outputDs = useMemo(
    () =>
      new DataSet({
        ...outputDS(),
        events: {
          select: ({ dataSet }) => {
            setDisabledFlag2(dataSet.selected.length === 0);
          },
          unSelect: ({ dataSet }) => {
            setDisabledFlag2(dataSet.selected.length === 0);
          },
        },
      }),
    []
  );
  const inputDs = useMemo(
    () =>
      new DataSet({
        ...inputDS(),
        events: {
          select: ({ dataSet }) => {
            setDisabledFlag(dataSet.selected.length === 0);
          },
          unSelect: ({ dataSet }) => {
            setDisabledFlag(dataSet.selected.length === 0);
          },
        },
      }),
    []
  );
  const lovInputDs = useMemo(() => new DataSet(lovInputDS()), []);
  const lovOutputDs = useMemo(() => new DataSet(lovOutputDS()), []);
  const lovDs = useMemo(() => new DataSet(lovInputDS()), []);
  const EditBindTableDs = useMemo(() => new DataSet(EditBindTableDS()), []);
  const DeleteBindTableDs = useMemo(() => new DataSet(DeleteBindTableDS()), []);
  const { pipelineId, isEdit, drawerDs, path } = props;

  const [disabledFlag, setDisabledFlag] = useState(true);
  const [disabledFlag2, setDisabledFlag2] = useState(true);
  useEffect(() => {
    if (isEdit) {
      drawerDs.setQueryParameter('pipelineId', pipelineId);
      inputDs.setQueryParameter('pipelineId', pipelineId);
      outputDs.setQueryParameter('pipelineId', pipelineId);
      lovInputDs.setQueryParameter('pipelineId', pipelineId);
      lovOutputDs.setQueryParameter('pipelineId', pipelineId);
      drawerDs.query();
      inputDs.query();
      outputDs.query();
    } else {
      drawerDs.create({}, 0);
    }
  }, []);

  const columns = useMemo(
    () => [
      {
        name: 'streamCode',
        renderer: ({ value, record }) => {
          if (value.indexOf('COMPOSITE:') === 0) {
            let str = '';
            let list = record.get('relations') || [];
            list = sortBy(list, 'order');
            str = list.map((item) => item.streamCode || '').join(',');
            return str;
          }
          return value;
        },
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'detailEdit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.detailEdit`,
                    type: 'button',
                    meaning: '数据流管道-详情页编辑',
                  },
                ]}
                onClick={() => {
                  handleEdit(record);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ],
    []
  );

  const outputColumns = useMemo(
    () => [
      {
        name: 'streamCode',
      },
    ],
    []
  );

  const handleEdit = (record) => {
    const pipelineStreamId =
      record.get('pipelineStreamId') || record.get('relations')[0].pipelineStreamId;
    const id = record.get('pipelineId') || record.get('relations')[0].pipelineId;
    Modal.open({
      title: intl.get('hdsc.dataFlowPipeline.view.title.viewBound').d('编辑绑定组合'),
      width: 520,
      height: 700,
      children: (
        <BindDrawer
          path={path}
          pipelineStreamId={pipelineStreamId}
          EditBindTableDs={EditBindTableDs}
          lovDs={lovDs}
        />
      ),
      onOk: () => {
        handleOk(id);
      },
      onCancel: () => {
        lovDs.current.set('code', []);
      },
      onClose: () => {
        lovDs.current.set('code', []);
      },
    });
  };

  const handleOk = async (id) => {
    let data = EditBindTableDs.relationsList || [];
    data = data.map((item) => {
      return {
        ...item,
        pipelineId: id,
      };
    });
    EditBindTableDs.create({ relations: data }, 0);
    DeleteBindTableDs.create({ pipelineStreamIds: EditBindTableDs.pipelineStreamIds }, 0);
    if (!isEmpty(EditBindTableDs.pipelineStreamIds)) {
      try {
        await DeleteBindTableDs.submit();
      } catch (e) {
        // e
      }
    }
    try {
      await EditBindTableDs.submit();
    } catch (e) {
      //
    }
    inputDs.query();
  };

  const handleBindInput = async () => {
    //  lovDs.streamPurposeCode= 'INPUT';
    // lovDs.setQueryParameter('streamPurposeCode', 'INPUT');
    try {
      await lovInputDs.submit();
      await inputDs.query();
    } catch {
      setDisabledFlag(true);
    }
    setDisabledFlag(true);
    lovInputDs.reset();
  };

  const handleUnBindInput = async () => {
    await inputDs.delete(inputDs.selected);
    await inputDs.query();
    setDisabledFlag(true);
  };

  const handleBindOutput = async () => {
    //  lovDs.streamPurposeCode='OUTPUT';
    // lovOutputDs.setQueryParameter('streamPurposeCode', 'OUTPUT');
    await lovOutputDs.submit();
    lovOutputDs.reset();
    await outputDs.query();
    setDisabledFlag2(true);
  };

  const handleUnBindOutput = async () => {
    await outputDs.delete(outputDs.selected);
    await outputDs.query();
    setDisabledFlag2(true);
  };

  return (
    <>
      <Form dataSet={drawerDs} labelWidth={120}>
        <TextField name="pipelineCode" maxLength={60} disabled={isEdit} />
        <TextField name="description" maxLength={240} />
        <Switch name="enabledFlag" />
      </Form>
      {isEdit && (
        <>
          <div style={{ textAlign: 'center' }}>
            {intl.get('hdsc.dataFlowPipeline.view.title.input').d('输入流')}
          </div>
          <div className={styles.buttonContainer}>
            <Lov
              name="code"
              mode="button"
              color="primary"
              icon="add"
              placeholder={intl.get('hzero.common.button.bind').d('绑定')}
              dataSet={lovInputDs}
              lovPara={{ streamPurposeCode: 'INPUT' }}
              onChange={handleBindInput}
              clearButton={false}
            >
              {intl.get('hzero.common.button.bind').d('绑定')}
            </Lov>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}-unbind-input`,
                  type: 'button',
                  meaning: '数据流管道-解绑输入流',
                },
              ]}
              icon="delete"
              color="primary"
              onClick={handleUnBindInput}
              disabled={inputDs.selected.length === 0 || disabledFlag}
            >
              {intl.get('hzero.common.button.unbind').d('解绑')}
            </ButtonPermission>
          </div>
          <Table dataSet={inputDs} columns={columns} />
          <div style={{ textAlign: 'center' }}>
            {intl.get('hdsc.dataFlowPipeline.view.title.output').d('输出流')}
          </div>
          <div className={styles.buttonContainer}>
            <Lov
              name="code"
              mode="button"
              color="primary"
              icon="add"
              placeholder={intl.get('hzero.common.button.bind').d('绑定')}
              dataSet={lovOutputDs}
              clearButton={false}
              onChange={handleBindOutput}
            >
              {intl.get('hzero.common.button.bind').d('绑定')}
            </Lov>
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}-unbind-output`,
                  type: 'button',
                  meaning: '数据流管道-解绑输出流',
                },
              ]}
              icon="delete"
              color="primary"
              onClick={handleUnBindOutput}
              disabled={outputDs.selected.length === 0 && disabledFlag2}
            >
              {intl.get('hzero.common.button.unbind').d('解绑')}
            </ButtonPermission>
          </div>
          <Table dataSet={outputDs} columns={outputColumns} />
        </>
      )}
    </>
  );
};

export default Drawer;
