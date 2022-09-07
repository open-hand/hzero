import React, { useMemo, useState } from 'react';
import {
  Form,
  Spin,
  Button,
  TextArea,
  TextField,
  Lov,
  DataSet,
  SelectBox,
  Select,
  DatePicker,
  NumberField,
  DateTimePicker,
} from 'choerodon-ui/pro';
import { Divider } from 'choerodon-ui';
import { isNull, isEmpty } from 'lodash';
import intl from 'utils/intl';
import notification from 'utils/notification';
import styles from './index.less';
import { fetchForm, fetchTest } from '@/services/messageTemplateService';
import { DATA_TYPE } from '@/utils/constants';

const { Option } = Select;
const Drawer = (props) => {
  const { testDrawDs, currentEditData } = props;
  const { templateId, templateCode, templateTypeCode } = currentEditData;
  const renderFormDs = useMemo(() => new DataSet({}), []);

  const [formList, setFormList] = useState([]);
  const [loading, setLoading] = useState(false);

  // 类型是否是数据点相关的
  const isAboutDataPoint = [
    'DOWN_PROPERTY',
    'DOWN_EGK_PROPERTY',
    'UP_PROPERTY',
    'UP_EGK_PROPERTY',
  ].includes(templateTypeCode);
  const reportDs = useMemo(
    () =>
      new DataSet({
        autoCreate: true,
        fields: [
          {
            name: 'reportMsg',
            type: 'string',
            label: intl.get('hiot.messageTemplate.model.messageTemp.reportMsg').d('报文'),
          },
        ],
      }),
    []
  );
  React.useEffect(() => {
    if (isAboutDataPoint) {
      testDrawDs.getField('guidLov').setLovPara('excludeGateway', 1);
    }
    testDrawDs.current.set('templateTypeCode', templateTypeCode);
  }, []);

  // 获取表单
  const fetchFormItem = (value) => {
    const { thingId } = value;
    const params = {
      thingId,
      templateId,
    };
    if (['DOWN_PROPERTY', 'DOWN_EGK_PROPERTY'].includes(templateTypeCode)) {
      params.controlsParameterFlag = 1;
    }
    fetchForm(params).then((res) => {
      if (res && !res.failed) {
        renderFormDs.loadData(res);
        setFormList(res);
      }
    });
  };

  // 渲染表单
  const renderForm = () => {
    return renderFormDs.map((record) => {
      const { dataType, minValue, maxValue, propertyName, guid } = record.toData();
      let options = record.get('options');
      let paramItem;
      switch (dataType) {
        case DATA_TYPE.BOOL:
          try {
            options = JSON.parse(options);
          } catch (e) {
            options = [];
          }
          paramItem = (
            <SelectBox key={guid} record={record} label={propertyName} name="value">
              {options.map(({ code, name }) => (
                <SelectBox.Option key={code} value={code}>
                  {name}
                </SelectBox.Option>
              ))}
            </SelectBox>
          );
          break;
        case DATA_TYPE.NUMBER:
          paramItem = (
            <NumberField
              key={guid}
              record={record}
              name="value"
              label={propertyName}
              max={maxValue ? Number(maxValue) : undefined}
              min={minValue ? Number(minValue) : undefined}
              step={record.get('step') || 1}
              addonAfter={record.get('unitCodeMeaning')}
            />
          );
          break;
        case DATA_TYPE.ENUM:
          paramItem = (
            <Select key={guid} record={record} label={propertyName} name="value">
              {options &&
                JSON.parse(options).map(({ code, name }) => (
                  <Option key={code} value={code}>
                    {name}
                  </Option>
                ))}
            </Select>
          );
          break;
        case DATA_TYPE.DATE:
          paramItem = <DatePicker key={guid} label={propertyName} name="value" record={record} />;
          break;
        case DATA_TYPE.DATE_TIME:
          paramItem = (
            <DateTimePicker key={guid} label={propertyName} name="value" record={record} />
          );
          break;
        default:
          break;
      }
      return paramItem;
    });
  };

  // Lov变化时触发
  const onLovChange = (value) => {
    if (isAboutDataPoint) {
      // 类型为数据点相关类型时才调用
      if (value) {
        fetchFormItem(value);
      } else {
        setFormList([]);
      }
    }
  };

  // 重置
  const handleReset = () => {
    renderFormDs.reset();
    if (!formList) {
      testDrawDs.current.reset();
    }
  };

  // 测试
  const handleTest = async () => {
    const validate = await testDrawDs.current.validate(true);
    const { guidLov, otaTask, __dirty, ...others } = testDrawDs.current.toData();
    const properties = {};
    renderFormDs.toData().forEach((item) => {
      const { guid, value } = item;
      if (!isNull(value)) {
        if (item.dataType === DATA_TYPE.DATE) {
          const arr = value.split(' ') || [];
          // eslint-disable-next-line prefer-destructuring
          properties[guid] = arr[0];
        } else {
          properties[guid] = value;
        }
      }
    });
    if (validate) {
      setLoading(true);
      const newData = {
        ...others,
        properties,
        msgTemplateId: templateId,
        msgTemplateCode: templateCode,
      };
      fetchTest(newData).then((res) => {
        setLoading(false);
        if (res && !res.failed) {
          reportDs.current.set('reportMsg', res.result);
          notification.success();
        }
        if (res && res.failed) {
          notification.error({
            message: res.message,
          });
        }
      });
    } else {
      notification.error({
        message: intl.get('hiot.messageTemplate.view.message.testVerify').d('请先选择设备'),
      });
    }
  };

  return (
    <>
      <Spin spinning={loading}>
        <Form dataSet={testDrawDs}>
          <Lov
            name="guidLov"
            onChange={(value) => {
              onLovChange(value);
            }}
          />
          {['DOWN_OTA_PACKAGE', 'UP_OTA_STEP'].includes(templateTypeCode) && <Lov name="otaTask" />}
          {['UP_OTA_REQUEST'].includes(templateTypeCode) && <TextField name="version" />}
          {['UP_OTA_STEP'].includes(templateTypeCode) && <NumberField name="otaStep" />}
          {['UP_OTA_STEP'].includes(templateTypeCode) && <TextField name="otaDesc" />}
          {!isAboutDataPoint && (
            <div newLine>
              <div className={styles['test-btns-wrap']}>
                <Button type="reset">{intl.get('hzero.common.button.reset').d('重置')}</Button>
                <Button
                  style={{ marginLeft: 8 }}
                  color="primary"
                  loading={loading}
                  onClick={handleTest}
                >
                  {intl.get('hiot.messageTemplate.view.button.test').d('测试')}
                </Button>
              </div>
            </div>
          )}
        </Form>
        {isAboutDataPoint && (
          <div>
            <Divider orientation="left">
              {intl.get('hiot.messageTemplate.view.title.dataPointText').d('数据点')}
            </Divider>
            <div>
              {!isEmpty(formList) ? (
                <Form>{renderForm()}</Form>
              ) : (
                <h3 style={{ color: 'gray', textAlign: 'center' }}>
                  {intl.get('hiot.DeviceSimulation.view.button.NoDataPoint').d('暂无数据点')}
                </h3>
              )}
            </div>
          </div>
        )}
        {/* <Form>{formList && renderForm()}</Form> */}
        {isAboutDataPoint && (
          <div className={styles['test-btns-wrap']}>
            <Button onClick={() => handleReset()}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button
              style={{ marginLeft: 8 }}
              color="primary"
              loading={loading}
              onClick={handleTest}
            >
              {intl.get('hiot.messageTemplate.view.button.test').d('测试')}
            </Button>
          </div>
        )}
        <Form dataSet={reportDs}>
          <TextArea name="reportMsg" style={{ height: 300 }} />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
