import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  Form,
  Lov,
  Output,
  Button,
  SelectBox,
  DataSet,
  TextField,
  IntlField,
  CodeArea,
  Spin,
  Modal,
} from 'choerodon-ui/pro';
import { Steps, Badge } from 'choerodon-ui';
import { isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { detailDS, logDS, lovDS } from '@/stores/apiIndividuationDS';
import styles from './index.less';
import Drawer from './Drawer';

const TargetDrawer = (props) => {
  const detailDs = useMemo(
    () =>
      new DataSet({
        ...detailDS(),
        events: {
          update: ({ dataSet }) => {
            const data = dataSet.current.toData();
            if (!isEmpty(dataSet.current) && data.customizeId) {
              const { __dirty, saveFlag, current, ...originCurrentData } = data.currentData;
              const { currentData, __dirty: d, saveFlag: sf, current: c, ...newCurrentData } = data;
              dataSet.current.set(
                'saveFlag',
                JSON.stringify(originCurrentData) !== JSON.stringify(newCurrentData)
              );
            } else if (!data.customizeId) {
              dataSet.current.set('saveFlag', true);
            }
          },
        },
      }),
    []
  );
  const logDs = useMemo(() => new DataSet(logDS()), []);
  const lovDs = useMemo(() => new DataSet(lovDS()), []);

  const [current, setCurrent] = useState(0);
  const [flag, setFlag] = useState(false);
  const [tooltipFlag, setTooltipFlag] = useState(undefined);
  const [customizePosition, setCustomizePosition] = useState(undefined);

  const lovRef = useRef();

  const {
    match: {
      params: { type, id },
      path,
    },
    history,
  } = props;

  useEffect(() => {
    if (type !== 'create') {
      detailDs.setQueryParameter('customizeId', id);
      detailDs.query().then(() => {
        detailDs.current.set('currentData', detailDs.current.toData());
        detailDs.current.set('saveFlag', false);
        setCustomizePosition(detailDs.current.get('customizePosition'));
      });
    }
    if (type === 'detail') {
      detailDs.current.set('current', 3);
      setCurrent(3);
    } else {
      detailDs.current.set('current', current);
    }
  }, []);

  const handlePointDbClick = (record, modal) => {
    detailDs.current.set('packageName', record.get('packageName'));
    detailDs.current.set('className', record.get('className'));
    detailDs.current.set('methodName', record.get('methodName'));
    detailDs.current.set('methodArgs', record.get('methodArgs'));
    detailDs.current.set('methodReturn', record.get('methodReturn'));
    modal.close(true);
  };

  const handlePoint = () => {
    const pointModal = Modal.open({
      closable: true,
      key: 'point',
      title: intl.get('hpfm.apiIndividuation.view.title.selectPoint').d('选择切入点'),
      drawer: false,
      style: {
        width: 800,
      },
      children: (
        <Drawer
          path={path}
          detailDs={detailDs}
          onDbClick={(e) => {
            handlePointDbClick(e, pointModal);
          }}
          serviceName={detailDs.current.get('serviceName')}
        />
      ),
      footer: null,
    });
  };

  const handleNext = async () => {
    const validateFlag = await detailDs.validate();
    if (validateFlag) {
      detailDs.current.set('current', current + 1);
      setCurrent(current + 1);
    }
  };

  const handleLast = async () => {
    detailDs.current.set('current', current - 1);
    setCurrent(current - 1);
  };

  const handleEdit = () => {
    history.push(`/hpfm/api-customize/edit/${detailDs.current.get('customizeId')}`);
    detailDs.setQueryParameter('customizeId', detailDs.current.get('customizeId'));
    detailDs.query().then(() => {
      detailDs.current.set('currentData', detailDs.current.toData());
      detailDs.current.set('saveFlag', false);
      setCustomizePosition(detailDs.current.get('customizePosition'));
      setTooltipFlag(false);
    });
    detailDs.current.set('current', 0);
    setFlag(false);
    // setDisable(true);
    setCurrent(0);
  };

  const handleSkip = async (num) => {
    if (type === 'edit') {
      const validateFlag = await detailDs.validate();
      if (validateFlag) {
        detailDs.current.set('current', num);
        setCurrent(num);
      }
    }
  };

  const handleSave = () => {
    detailDs.submit().then((res) => {
      if (res) {
        detailDs.setQueryParameter('customizeId', res.content[0].customizeId);
        detailDs.query().then(() => {
          detailDs.current.set('currentData', detailDs.current.toData());
          detailDs.current.set('saveFlag', false);
          setCustomizePosition(detailDs.current.get('customizePosition'));
          setFlag(!flag);
          setTooltipFlag(false);
        });
      }
    });
  };

  const handleApply = () => {
    logDs.setQueryParameter('customizeIds', [detailDs.current.get('customizeId')]);
    detailDs.query().then(() => {
      detailDs.current.set('currentData', detailDs.current.toData());
      detailDs.current.set('saveFlag', false);
      setCustomizePosition(detailDs.current.get('customizePosition'));
      setTooltipFlag(false);
    });
    logDs.query().then((res) => {
      if (res) {
        setCurrent(current + 1);
      }
    });
  };

  const handelChange = (value) => {
    const initValue = customizePosition;
    if ((initValue === 'AFTER' || value === 'AFTER') && initValue !== value && type === 'edit') {
      setTooltipFlag(true);
    } else if (initValue && (initValue === 'AFTER' || value === 'AFTER') && type === 'create') {
      setTooltipFlag(true);
    } else {
      setTooltipFlag(false);
    }
  };

  return (
    <>
      <Header
        backPath="/hpfm/api-customize/list"
        title={
          // eslint-disable-next-line no-nested-ternary
          id === 'create'
            ? intl.get('hpfm.apiIndividuation.view.title.create').d('创建API个性化')
            : type === 'detail'
            ? intl.get('hpfm.apiIndividuation.view.title.detail').d('API个性化详情')
            : intl.get('hpfm.apiIndividuation.view.title.edit').d('编辑API个性化')
        }
      />
      <Content className={styles.content}>
        <Spin dataSet={detailDs}>
          <Spin dataSet={logDs}>
            {tooltipFlag && (
              <div className={styles.tooltip}>
                {intl
                  .get('hpfm.apiIndividuation.view.message.customizePosition')
                  .d('代码结构变化，如果使用了模板，请重新应用模板(自定义代码请妥善备份)。')}
              </div>
            )}
            {type !== 'detail' && (
              <Steps className={styles.steps} current={current}>
                <Steps.Step
                  className={type === 'edit' ? styles.cursor : ''}
                  title={intl.get('hpfm.apiIndividuation.view.title.info').d('个性化信息')}
                  onClick={() => {
                    handleSkip(0);
                  }}
                />
                <Steps.Step
                  className={type === 'edit' ? styles.cursor : ''}
                  title={intl.get('hpfm.apiIndividuation.view.title.point').d('个性化切入点')}
                  onClick={() => {
                    handleSkip(1);
                  }}
                />
                <Steps.Step
                  className={type === 'edit' ? styles.cursor : ''}
                  title={intl.get('hpfm.apiIndividuation.view.title.code').d('个性化代码')}
                  onClick={() => {
                    handleSkip(2);
                  }}
                />
                <Steps.Step
                  className={type === 'edit' ? styles.cursor : ''}
                  title={intl.get('hpfm.apiIndividuation.view.title.apply').d('个性化应用')}
                  onClick={() => {
                    handleSkip(3);
                  }}
                />
                <Steps.Step
                  title={intl.get('hpfm.apiIndividuation.view.title.log').d('个性化日志')}
                />
              </Steps>
            )}
            {current === 0 && (
              <Form dataSet={detailDs} columns={2}>
                <TextField
                  disabled={type !== 'create'}
                  name="customizeCode"
                  maxLength={30}
                  // label={
                  //   <span>
                  //     {intl
                  //       .get('hpfm.apiIndividuation.model.apiIndividuation.customizeCode')
                  //       .d('个性化编码')}
                  //     <Tooltip
                  //       title={intl
                  //         .get('hpfm.apiIndividuation.view.message.customizeCode')
                  //         .d('提示：如果未输入，系统自动生成')}
                  //     >
                  //       <Icon type="help_outline" />
                  //     </Tooltip>
                  //   </span>
                  // }
                />
                <IntlField name="customizeName" maxLength={120} />
                <Lov name="applyTenants" multiple />
                <Lov name="serviceObj" />
                <SelectBox name="syncFlag" />
                {/* <Select.Option value={1}>
                    {intl.get('hzero.common.status.sync').d('同步')}
                  </Select.Option>
                  <Select.Option value={0}>
                    {intl.get('hzero.common.status.async').d('异步')}
                  </Select.Option>
                </SelectBox> */}
                {/* <Switch name="enabledFlag" disabled={type !== 'create'} /> */}
              </Form>
            )}
            {current === 1 && (
              <Form dataSet={detailDs} columns={2}>
                <SelectBox name="customizePosition" onChange={handelChange} />
                <TextField name="packageName" maxLength={240} disabled />
                <TextField name="className" maxLength={180} disabled />
                <TextField name="methodName" maxLength={180} disabled />
                <TextField name="methodArgs" disabled />
                <TextField name="methodReturn" disabled />
              </Form>
            )}
            {current === 2 && (
              <>
                <Form dataSet={detailDs} columns={2}>
                  <SelectBox name="contentType" />
                  <CodeArea
                    name="customizeContent"
                    newLine
                    options={{ lineWrapping: true }}
                    colSpan={2}
                    style={{ height: 300 }}
                  />
                </Form>
              </>
            )}
            {current === 3 && (
              <Form dataSet={detailDs} columns={2}>
                <Output name="customizeCode" />
                <Output name="customizeName" />

                <Lov name="applyTenants" multiple disabled />
                <Lov name="serviceObj" disabled />
                <SelectBox name="syncFlag" disabled />
                {/* <Select.Option value={1}>
                    {intl.get('hzero.common.status.sync').d('同步')}
                  </Select.Option>
                  <Select.Option value={0}>
                    {intl.get('hzero.common.status.async').d('异步')}
                  </Select.Option>
                </SelectBox> */}
                {/* <Switch name="enabledFlag" disabled /> */}
                <SelectBox name="customizePosition" disabled />
                <Output name="packageName" />
                <Output name="className" />
                <Output name="methodName" />
                <Output name="methodArgs" />
                <SelectBox name="contentType" disabled />
                {type !== 'create' && (
                  <Output
                    name="versionNumber"
                    renderer={({ text }) => {
                      return `v${text}.0`;
                    }}
                  />
                )}
                <CodeArea
                  name="customizeContent"
                  options={{ lineWrapping: true }}
                  style={{ height: 250 }}
                  colSpan={2}
                  newLine
                  disabled
                />
              </Form>
            )}

            {current === 4 && (
              <Form dataSet={logDs}>
                <Output
                  name="applyStatus"
                  renderer={({ value, text }) => {
                    return (
                      <Badge status={value === 'APPLY_SUCCESS' ? 'success' : 'error'} text={text} />
                    );
                  }}
                />
                <CodeArea
                  disabled
                  name="logContent"
                  options={{ lineWrapping: true }}
                  style={{ height: '100%' }}
                />
              </Form>
            )}
            <div className={styles.buttonContainer}>
              {current > 0 &&
                current < 4 &&
                type !== 'detail' &&
                !(type === 'create' && !detailDs.current.get('saveFlag')) && (
                  <Button
                    icon="arrow_back"
                    className={styles.button}
                    color="primary"
                    onClick={handleLast}
                  >
                    {intl.get('hpfm.apiIndividuation.button.lastStep').d('上一步')}
                  </Button>
                )}
              {current < 3 && (
                <Button
                  icon="arrow_forward"
                  className={styles.button}
                  color="primary"
                  onClick={handleNext}
                >
                  {intl.get('hpfm.apiIndividuation.button.nextStep').d('下一步')}
                </Button>
              )}
              {current === 4 && type === 'create' && (
                <Button
                  icon="mode_deit"
                  className={styles.button}
                  color="primary"
                  onClick={handleEdit}
                >
                  {intl.get('hpfm.apiIndividuation.button.editAgain').d('重新编辑')}
                </Button>
              )}
              {current === 3 && type !== 'detail' && detailDs.current.get('saveFlag') && (
                <Button icon="save" className={styles.button} color="primary" onClick={handleSave}>
                  {intl.get('hzero.common.button.save').d('保存')}
                </Button>
              )}
              {current === 3 && type !== 'detail' && !detailDs.current.get('saveFlag') && (
                <Button
                  icon="build-o"
                  className={styles.button}
                  color="primary"
                  onClick={handleApply}
                >
                  {intl.get('hpfm.apiIndividuation.button.applyCustomize').d('应用个性化')}
                </Button>
              )}
              {current === 1 && (
                <Button
                  icon="point"
                  className={styles.button}
                  color="primary"
                  onClick={handlePoint}
                >
                  {intl.get('hpfm.apiIndividuation.button.applyCustomize').d('快速选择切入点')}
                </Button>
              )}
              {current === 2 && (
                <Lov
                  icon="template_configuration"
                  ref={lovRef}
                  name="template"
                  mode="button"
                  color="primary"
                  placeholder={intl
                    .get('hpfm.apiIndividuation.button.selectTemplate')
                    .d('选择已有模板')}
                  dataSet={detailDs}
                  clearButton={false}
                  onChange={(e) => {
                    detailDs.current.set('template', null);
                    lovDs.setQueryParameter('lang', e.lang);
                    lovDs.setQueryParameter('templateCode', e.templateCode);
                    lovDs.setQueryParameter('methodArgs', detailDs.current.get('methodArgs'));
                    lovDs.setQueryParameter('methodReturn', detailDs.current.get('methodReturn'));
                    lovDs.setQueryParameter(
                      'customizePosition',
                      detailDs.current.get('customizePosition')
                    );
                    lovDs.query().then((res) => {
                      if (res) {
                        detailDs.current.set('customizeContent', res);
                        setCustomizePosition(detailDs.current.get('customizePosition'));
                        setTooltipFlag(false);
                      }
                    });
                    if (lovRef.current) {
                      lovRef.current.blur();
                    }
                  }}
                  className={styles.button}
                  noCache
                />
              )}
            </div>
          </Spin>
        </Spin>
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hpfm.apiIndividuation'] })(TargetDrawer);
