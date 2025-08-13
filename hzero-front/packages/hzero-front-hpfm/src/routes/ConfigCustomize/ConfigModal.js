/* eslint-disable eqeqeq */
/* eslint-disable react/jsx-props-no-spreading */
import React from 'react';
import {
  Drawer,
  Select,
  Form,
  InputNumber,
  Button,
  Input,
  Checkbox,
  Col,
  Row,
  Badge,
  Tooltip,
  Switch,
  DatePicker,
  Tabs,
} from 'hzero-ui';
import moment from 'moment';
import intl from 'utils/intl';
import TLEditor from 'components/TLEditor';
import Lov from 'components/Lov';
import { isNil, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { connect } from 'dva';
import {
  colOptions,
  getFieldCodeAlias,
  getFieldNameAlias,
  getFieldConfigAlias,
  getDefaultActiveAlias,
} from '@/utils/constConfig.js';
import ParamsModal from '@/components/CommonModal/ParamsConfigModal';
import ComputeRuleModal from '@/components/CommonModal/ComputeRuleModal';
import { getContextParams } from 'components/Customize/hzero/customizeTool';
import { FlexSelect } from 'components/Customize/hzero/FlexComponents';
import LovMulti from 'components/Customize/hzero/LovMulti';
import RelatedModal from './RelatedModal';
import ConditionModal from './ConditionModal';
import SelfConditionModal from './SelfConditionModal';
import DefaultValueModal from './DefaultValueModal';
import styles from './index.less';

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const formLayout2 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
const { TabPane } = Tabs;
const FormItem = Form.Item;
const { Option } = Select;

@connect(({ configCustomize, loading }) => {
  const {
    moduleList,
    codes,
    unitAlias,
    conditionList,
    validatorList,
    headerProps,
    defaultValueProps,
    defaultConList,
    defaultValidList,
  } = configCustomize;
  return {
    moduleList,
    codes,
    unitAlias,
    headerProps,
    conditionList,
    validatorList,
    defaultValueProps,
    defaultConList,
    defaultValidList,
    saveLoading: loading.effects['configCustomize/saveFieldIndividual'],
  };
})
@Form.create({ fieldNameProp: null })
export default class ConfigModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fieldLovMaps: [],
      conditionHeaders: {
        required: {},
        visible: {},
        editable: {},
      },
      backUpParamList: [],
      backUpRenderRule: '',
      condOptions: [],
    };
    const { form } = props;
    ['fieldWidget', 'fieldId', 'fieldCategory', 'fieldType', 'fieldName'].forEach((i) =>
      form.registerField(i)
    );
  }

  componentDidUpdate(prevProps) {
    const {
      dispatch,
      record = {},
      id,
      modelId,
      codes: { condOptions = [] },
      // type,
      visible,
      form,
    } = this.props;
    const { record: { configFieldId } = {}, visible: previsible } = prevProps;
    if (visible === true && previsible === false) {
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({
        condOptions,
        // condOptions:
        //   record.custType === 'EXT' || type === 'new'
        //     ? // eslint-disable-next-line eqeqeq
        //       condOptions.filter((i) => i.value != '-1')
        //     : condOptions,
      });
    }
    if (visible === false && previsible === true) {
      form.resetFields();
    }
    if (record.configFieldId !== undefined && configFieldId !== record.configFieldId && visible) {
      dispatch({
        type: 'configCustomize/queryConditions',
        payload: { configFieldId: record.configFieldId, unitId: id },
      }).then((res) => {
        if (isEmpty(res)) return;
        const conditionHeaders = {
          required: {},
          visible: {},
          editable: {},
        };
        res.forEach((i) => {
          conditionHeaders[i.conType] = i;
        });
        this.setState({ conditionHeaders });
        record.conditionHeaders = res;
      });
      dispatch({
        type: 'configCustomize/queryFieldMapping',
        payload: { configFieldId: record.configFieldId },
      }).then((res) => {
        if (isEmpty(res)) return;
        this.setState({ fieldLovMaps: res });
        record.fieldLovMaps = res;
      });
      dispatch({
        type: 'configCustomize/querySelfValidator',
        payload: { configFieldId: record.configFieldId, unitId: id, conType: 'valid' },
      });
      dispatch({
        type: 'configCustomize/queryDefaultValueFx',
        payload: { configFieldId: record.configFieldId, unitId: id, conType: 'defaultValue' },
      });
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({
        backUpParamList: (record.paramList || []).map((i) => i),
        backUpRenderRule: record.renderRule,
      });
    }
    if (prevProps.id !== id) {
      dispatch({
        type: 'configCustomize/queryModule',
        payload: { unitId: id, modelId },
      });
    }
  }

  getDefaultValueRender(child, visibleFx) {
    const { defaultValueFx } = this.state;
    const { defaultValidList } = this.props;
    const valids = defaultValueFx === undefined ? defaultValidList : defaultValueFx.valids;
    return (
      <Row className={styles['flex-center-vertical']}>
        <Col span={18}>{child}</Col>
        <Col
          span={6}
          style={{ display: visibleFx ? 'block' : 'none', marginBottom: '14px', marginLeft: '8px' }}
          className={styles['fx-alink']}
        >
          <Tooltip
            placement="right"
            title={intl.get('hpfm.individual.model.config.condition').d('条件配置')}
          >
            <a
              className={(valids || []).length > 0 ? 'active' : ''}
              onClick={() => this.toggleDefaultValueModal()}
            >
              fx
            </a>
            <Badge count={(valids || []).length} />
          </Tooltip>
        </Col>
      </Row>
    );
  }

  getSelectConfig(_, visibleFx) {
    const {
      form,
      // record,
      record: { widget = {}, paramList },
    } = this.props;
    return (
      <>
        <FormItem
          label={intl.get('hpfm.individual.model.config.multipleFlag').d('启用多选')}
          {...formLayout2}
        >
          {form.getFieldDecorator('multipleFlag', {
            initialValue: Number(widget.multipleFlag || 0),
          })(
            <Checkbox
              checkedValue={1}
              unCheckedValue={0}
              onChange={() => {
                form.setFieldsValue({ defaultValue: undefined });
                this.updateLovMappings([]);
              }}
            />
          )}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.valueCode').d('值集编码')}
        >
          {form.getFieldDecorator('sourceCode', {
            initialValue: widget.sourceCode,
          })(
            <Lov
              code="HPFM.LOV.LOV_DETAIL_CODE.ORG"
              textValue={widget.sourceCode}
              textField="sourceCode"
              onChange={() => form.setFieldsValue({ defaultValue: undefined })}
            />
          )}
        </FormItem>
        {this.getDefaultValueRender(
          <FormItem
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 15 }}
            label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
          >
            {form.getFieldDecorator('defaultValue', {
              initialValue: widget.defaultValue,
            })(
              <FlexSelect
                lovCode={form.getFieldValue('sourceCode')}
                fieldCode="defaultValue"
                multipleFlag={form.getFieldValue('multipleFlag')}
                params={getContextParams(paramList, { isConfig: true })}
              />
            )}
          </FormItem>,
          visibleFx
        )}
        <Button
          icon="setting"
          type="primary"
          onClick={this.toggleParamsModal}
          style={{ marginBottom: '8px' }}
        >
          {intl.get('hpfm.individual.common.setLovParams').d('设置值集参数')}
          <Badge
            style={{ marginLeft: '8px', height: '16px', lineHeight: '16px' }}
            count={(paramList || []).length}
          />
        </Button>
        {/* <Button
          icon="share-alt"
          type="primary"
          onClick={() => this.toggleRelatedModal(record)}
          style={{ marginBottom: '8px' }}
        >
          {intl.get('hpfm.individual.model.config.fieldMapping').d('关联字段设置')}
          <Badge
            style={{ marginLeft: '8px', height: '16px', lineHeight: '16px' }}
            count={fieldLovMaps.length}
          />
        </Button> */}
      </>
    );
  }

  getLovConfig(_, visibleFx) {
    const { fieldLovMaps } = this.state;
    const {
      form,
      record,
      record: { widget = {}, paramList },
    } = this.props;
    return (
      <>
        <FormItem
          label={intl.get('hpfm.individual.model.config.multipleFlag').d('启用多选')}
          {...formLayout2}
        >
          {form.getFieldDecorator('multipleFlag', {
            initialValue: Number(widget.multipleFlag || 0),
          })(
            <Checkbox
              checkedValue={1}
              unCheckedValue={0}
              onChange={() => form.setFieldsValue({ defaultValue: undefined })}
            />
          )}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.valueCode').d('值集编码')}
        >
          {form.getFieldDecorator('sourceCode', {
            initialValue: widget.sourceCode,
          })(
            <Lov
              code="HPFM.LOV.VIEW.ORG"
              textValue={widget.sourceCode}
              textField="sourceCode"
              onChange={() => form.setFieldsValue({ defaultValue: undefined })}
              lovOptions={{ displayField: 'viewCode', valueField: 'viewCode' }}
            />
          )}
        </FormItem>
        {this.getDefaultValueRender(
          <FormItem
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 15 }}
            label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
          >
            {form.getFieldDecorator('defaultValue', {
              initialValue: widget.defaultValue,
            })(
              form.getFieldValue('multipleFlag') === 1 ? (
                <LovMulti
                  code={form.getFieldValue('sourceCode')}
                  queryParams={getContextParams(paramList, { isConfig: true })}
                  translateData={widget.defaultValueMeaning || {}}
                />
              ) : (
                <Lov
                  code={form.getFieldValue('sourceCode')}
                  queryParams={getContextParams(paramList, { isConfig: true })}
                  textValue={widget.defaultValueMeaning || widget.defaultValue}
                />
              )
            )}
          </FormItem>,
          visibleFx
        )}
        <Button
          icon="setting"
          type="primary"
          onClick={this.toggleParamsModal}
          style={{ marginBottom: '8px' }}
        >
          {intl.get('hpfm.individual.common.setLovParams').d('设置值集参数')}
          <Badge
            style={{ marginLeft: '8px', height: '16px', lineHeight: '16px' }}
            count={(paramList || []).length}
          />
        </Button>
        <Button
          icon="share-alt"
          type="primary"
          onClick={() => this.toggleRelatedModal(record)}
          style={{ marginBottom: '8px' }}
          disabled={form.getFieldValue('multipleFlag') === 1}
        >
          {intl.get('hpfm.individual.model.config.fieldMapping').d('关联字段设置')}
          <Badge
            style={{ marginLeft: '8px', height: '16px', lineHeight: '16px' }}
            count={fieldLovMaps.length}
          />
        </Button>
      </>
    );
  }

  getInputConfig(_, visibleFx) {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return (
      <>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.maxLength').d('最大长度')}
        >
          {form.getFieldDecorator('textMaxLength', {
            initialValue: widget.textMaxLength,
          })(<InputNumber precision={0} min={1} />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.minLength').d('最小长度')}
        >
          {form.getFieldDecorator('textMinLength', {
            initialValue: widget.textMinLength,
          })(<InputNumber precision={0} min={1} />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.placeholder').d('背景文字')}
        >
          {form.getFieldDecorator('placeholder', {
            initialValue: widget.placeholder,
          })(<Input trim />)}
        </FormItem>
        {this.getDefaultValueRender(
          <FormItem
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 15 }}
            label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
          >
            {form.getFieldDecorator('defaultValue', {
              initialValue: widget.defaultValue,
            })(<Input trim />)}
          </FormItem>,
          visibleFx
        )}
      </>
    );
  }

  getInputNumberConfig(_, visibleFx) {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return (
      <>
        <FormItem {...formLayout2} label={intl.get('hpfm.individual.model.config.max').d('最大值')}>
          {form.getFieldDecorator('numberMax', {
            initialValue: widget.numberMax,
          })(<InputNumber precision={0} />)}
        </FormItem>
        <FormItem {...formLayout2} label={intl.get('hpfm.individual.model.config.min').d('最小值')}>
          {form.getFieldDecorator('numberMin', {
            initialValue: widget.numberMin,
          })(<InputNumber precision={0} />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.decimal').d('精度')}
        >
          {form.getFieldDecorator('numberDecimal', {
            initialValue: widget.numberDecimal,
          })(<InputNumber precision={0} min={0} />)}
        </FormItem>
        {this.getDefaultValueRender(
          <FormItem
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 15 }}
            label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
          >
            {form.getFieldDecorator('defaultValue', {
              initialValue: widget.defaultValue,
            })(<InputNumber />)}
          </FormItem>,
          visibleFx
        )}
      </>
    );
  }

  getTLEditorConfig() {}

  getDatePickerConfig(_, visibleFx) {
    const {
      form,
      record: { widget = {} },
      codes,
    } = this.props;
    const format = form.getFieldValue('dateFormat') || widget.dateFormat || DEFAULT_DATE_FORMAT;
    return (
      <>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.dateFormat').d('日期格式')}
        >
          {form.getFieldDecorator('dateFormat', {
            initialValue: widget.dateFormat,
          })(
            <Select style={{ width: '100%' }}>
              {codes.dateFormat.map((i) => (
                <Option value={i.value}>{i.meaning}</Option>
              ))}
            </Select>
          )}
        </FormItem>
        {this.getDefaultValueRender(
          <FormItem
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 15 }}
            label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
          >
            {form.getFieldDecorator('defaultValue', {
              initialValue: widget.defaultValue ? moment(widget.defaultValue) : '',
              getValueProps: (dateStr) => ({
                value: dateStr ? moment(dateStr, format) : dateStr,
              }),
              getValueFromEvent(e) {
                if (!e || !e.target) {
                  return e && e.format ? e.format(format) : e;
                }
                const { target } = e;
                return target.type === 'checkbox' ? target.checked : target.value;
              },
            })(<DatePicker format={format} showTime style={{ width: '100%' }} />)}
          </FormItem>,
          visibleFx
        )}
      </>
    );
  }

  getTextAreaConfig(_, visibleFx) {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return (
      <>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.maxLength').d('最大长度')}
        >
          {form.getFieldDecorator('textMaxLength', {
            initialValue: widget.textMaxLength,
          })(<InputNumber precision={0} min={1} />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.minLength').d('最小长度')}
        >
          {form.getFieldDecorator('textMinLength', {
            initialValue: widget.textMinLength,
          })(<InputNumber precision={0} min={1} />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.textAreaMaxLine').d('文本域行数')}
        >
          {form.getFieldDecorator('textAreaMaxLine', {
            initialValue: widget.textAreaMaxLine,
          })(<InputNumber precision={0} min={1} />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.placeholder').d('背景文字')}
        >
          {form.getFieldDecorator('placeholder', {
            initialValue: widget.placeholder,
          })(<Input.TextArea trim />)}
        </FormItem>
        {this.getDefaultValueRender(
          <FormItem
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 15 }}
            label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
          >
            {form.getFieldDecorator('defaultValue', {
              initialValue: widget.defaultValue,
            })(<Input trim />)}
          </FormItem>,
          visibleFx
        )}
      </>
    );
  }

  getCheckboxConfig(_, visibleFx) {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return this.getDefaultValueRender(
      <FormItem
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 15 }}
        label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
      >
        {form.getFieldDecorator('defaultValue', {
          initialValue: Number(widget.defaultValue || 0),
        })(<Checkbox checkedValue={1} unCheckedValue={0} />)}
      </FormItem>,
      visibleFx
    );
  }

  getSwitchConfig(_, visibleFx) {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return this.getDefaultValueRender(
      <FormItem
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 15 }}
        label={intl.get('hpfm.individual.model.config.defaultValue').d('默认值')}
      >
        {form.getFieldDecorator('defaultValue', {
          initialValue: Number(widget.defaultValue || 0),
        })(<Switch checkedValue={1} unCheckedValue={0} />)}
      </FormItem>,
      visibleFx
    );
  }

  getLinkConfig() {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return (
      <>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.linkTitle').d('链接标题')}
        >
          {form.getFieldDecorator('linkTitle', {
            initialValue: widget.linkTitle,
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formLayout2}
          label={intl.get('hpfm.individual.model.config.linkHref').d('URL')}
        >
          {form.getFieldDecorator('linkHref', {
            initialValue: widget.linkHref,
          })(<Input />)}
        </FormItem>
        <FormItem {...formLayout2}>
          {form.getFieldDecorator('linkNewWindow', {
            initialValue: isNil(widget.linkNewWindow) ? 1 : widget.linkNewWindow,
            valuePropsName: 'checked',
          })(
            <Checkbox checkedValue={1} unCheckedValue={0}>
              {intl.get('hpfm.individual.model.config.linkNewWindow').d('在新窗口中打开')}
            </Checkbox>
          )}
        </FormItem>
      </>
    );
  }

  getUploadConfig() {
    const {
      form,
      record: { widget = {} },
    } = this.props;
    return (
      <Tabs defaultActiveKey="hzero" animated={false}>
        <TabPane
          tab={intl.get('hpfm.individual.common.title.generalConfig').d('通用配置')}
          key="hzero"
        >
          <FormItem label={intl.get('hpfm.individual.model.config.bucketName').d('桶名')}>
            {form.getFieldDecorator('bucketName', {
              initialValue: widget.bucketName,
            })(<Input />)}
          </FormItem>
          <FormItem label={intl.get('hpfm.individual.model.config.bucketDirectory').d('目录名')}>
            {form.getFieldDecorator('bucketDirectory', {
              initialValue: widget.bucketDirectory,
            })(<Input />)}
          </FormItem>
        </TabPane>
        <TabPane tab={intl.get('hpfm.individual.common.title.c7nConfig').d('C7N配置')} key="c7n">
          <FormItem label={intl.get('hpfm.individual.model.config.linkHref').d('URL')}>
            {form.getFieldDecorator('linkHref', {
              initialValue: widget.linkHref,
            })(<Input />)}
          </FormItem>
          <FormItem label={intl.get('hpfm.individual.model.config.uploadAccept').d('上传类型')}>
            {form.getFieldDecorator('uploadAccept', {
              initialValue: widget.bucketDirectory,
            })(<Select mode="tags" style={{ width: '100%' }} />)}
          </FormItem>
        </TabPane>
      </Tabs>
    );
  }

  configMap(visibleFx) {
    const { record = {}, form, unitType } = this.props;
    if (unitType === 'TABPANE') return null;
    switch (record.fieldWidget || form.getFieldValue('fieldWidget')) {
      case 'RADIO_GROUP':
      case 'SELECT':
        return this.getSelectConfig(record, visibleFx);
      case 'LOV':
        return this.getLovConfig(record, visibleFx);
      case 'INPUT':
        return this.getInputConfig(record, visibleFx);
      case 'INPUT_NUMBER':
        return this.getInputNumberConfig(record, visibleFx);
      case 'TL_EDITOR':
        return this.getTLEditorConfig(record, visibleFx);
      case 'DATE_PICKER':
        return this.getDatePickerConfig(record, visibleFx);
      case 'TEXT_AREA':
        return this.getTextAreaConfig(record, visibleFx);
      case 'CHECKBOX':
        return this.getCheckboxConfig(record, visibleFx);
      case 'SWITCH':
        return this.getSwitchConfig(record, visibleFx);
      case 'UPLOAD':
        return this.getUploadConfig(record, visibleFx);
      case 'LINK':
        return this.getLinkConfig(record, visibleFx);
      default:
        return null;
    }
  }

  @Bind()
  toggleRelatedModal() {
    const { relatedVisible } = this.state;
    this.setState({
      relatedVisible: !relatedVisible,
    });
  }

  @Bind()
  toggleParamsModal() {
    const { paramVisible } = this.state;
    this.setState({
      paramVisible: !paramVisible,
    });
  }

  @Bind()
  saveParamList(paramList) {
    const { record } = this.props;
    record.paramList = paramList;
  }

  @Bind()
  toggleConditionModal(targetProp) {
    const { conditionVisible } = this.state;
    this.setState({
      targetProp,
      conditionVisible: !conditionVisible,
    });
  }

  @Bind()
  toggleComputeRuleModal() {
    const { compRuleVisible } = this.state;
    this.setState({
      compRuleVisible: !compRuleVisible,
    });
  }

  @Bind()
  toggleSelfConditionModal() {
    const { selfConditionVisible } = this.state;
    this.setState({
      selfConditionVisible: !selfConditionVisible,
    });
  }

  @Bind()
  toggleDefaultValueModal() {
    const { defaultValueVisible } = this.state;
    this.setState({
      defaultValueVisible: !defaultValueVisible,
    });
  }

  @Bind()
  updateLovMappings(fieldLovMaps) {
    this.setState({
      fieldLovMaps,
    });
  }

  @Bind()
  updateConditionHeaders(targetProp, newHeaderProps) {
    const { conditionHeaders } = this.state;
    this.setState({
      conditionHeaders: {
        ...conditionHeaders,
        [targetProp]: newHeaderProps,
      },
    });
  }

  @Bind()
  updateSelfValidator(newSelfValidator) {
    this.setState({
      selfValidator: newSelfValidator,
    });
  }

  @Bind()
  updateDefaultValueFx(newSelfValidator) {
    this.setState({
      defaultValueFx: newSelfValidator,
    });
  }

  @Bind()
  saveRenderRule({ renderRule }) {
    const { record } = this.props;
    record.renderRule = renderRule;
  }

  @Bind()
  onOk() {
    const {
      record,
      form,
      refreshLineData,
      id,
      dispatch,
      headerProps,
      conditionList,
      validatorList,
      defaultValueProps,
      defaultConList,
      defaultValidList,
    } = this.props;
    const { selfValidator, defaultValueFx } = this.state;
    form.validateFields((err, values) => {
      if (err) return;
      const { field = {}, widget, ...others } = record;
      const oldConValid = {
        ...headerProps,
        conType: 'valid',
        lines: conditionList,
        valids: validatorList,
      };
      const oldDefaultValueFx = {
        ...defaultValueProps,
        lines: defaultConList,
        valids: defaultValidList,
      };
      const allData = { ...field, ...widget, ...values };
      const {
        defaultValue,
        fieldName,
        modelId,
        fieldId,
        fieldCode,
        fieldCategory,
        fieldType,
        fieldWidget,
        sourceCode,
        numberMax,
        numberMin,
        numberDecimal,
        textMaxLength,
        textMinLength,
        textAreaMaxLine,
        dateFormat,
        bucketName,
        bucketDirectory,
        linkTitle,
        linkHref,
        multipleFlag,
        linkNewWindow,
        ...rest
      } = values;
      const payload = {
        modelId,
        unitId: id,
        fieldId,
        ...others,
        fieldName,
        field: {
          ...field,
          fieldCategory,
          fieldType,
          fieldCode,
        },
        widget: {
          ...widget,
          ...getWidgetConfig(fieldWidget, allData),
        },
        conValid: selfValidator || oldConValid,
        defaultValueCon: defaultValueFx || oldDefaultValueFx,
        ...rest,
      };
      dispatch({
        type: 'configCustomize/saveFieldIndividual',
        payload,
      }).then((res) => {
        if (res) {
          notification.success();
          refreshLineData(id);
          this.onClose();
        }
      });
    });
  }

  @Bind()
  setFieldInfo(_, record) {
    const { form } = this.props;
    const {
      fieldWidget,
      fieldCategory,
      fieldType,
      fieldName,
      fieldMultiLang,
      fieldId,
      fieldCodeCamel,
    } = record;
    form.setFieldsValue({
      fieldWidget,
      fieldMultiLang,
      fieldCategory,
      fieldType,
      fieldName,
      fieldAlias: fieldCodeCamel,
      fieldId,
    });
  }

  @Bind()
  onComponentChange() {
    const { form, record = {} } = this.props;
    form.setFieldsValue({
      bucketName: undefined,
      bucketDirectory: undefined,
      sourceCode: undefined,
      textMaxLength: undefined,
      textMinLength: undefined,
      textAreaMaxLine: undefined,
      numberMax: undefined,
      numberMin: undefined,
      dateFormat: undefined,
      lovMappings: undefined,
      defaultValue: undefined,
      multipleFlag: undefined,
    });
    this.setState({ fieldLovMaps: [] });
    record.fieldLovMaps = [];
    record.paramList = [];
  }

  @Bind()
  onClose() {
    const { record, onClose = () => {}, form, dispatch } = this.props;
    record.paramList = this.state.backUpParamList;
    record.renderRule = this.state.backUpRenderRule;
    form.setFieldsValue({
      fieldName: undefined,
      modelId: undefined,
      fieldId: undefined,
      fieldCode: undefined,
      fieldCategory: undefined,
      fieldType: undefined,
      fieldWidget: undefined,
      sourceCode: undefined,
      numberMax: undefined,
      numberMin: undefined,
      numberDecimal: undefined,
      textMaxLength: undefined,
      textMinLength: undefined,
      textAreaMaxLine: undefined,
      dateFormat: undefined,
      bucketName: undefined,
      bucketDirectory: undefined,
      linkTitle: undefined,
      linkHref: undefined,
      linkNewWindow: undefined,
      lovMappings: undefined,
      defaultValue: undefined,
    });
    this.setState({
      fieldLovMaps: [],
      conditionHeaders: {
        required: {},
        visible: {},
        editable: {},
      },
      backUpParamList: [],
      backUpRenderRule: '',
      condOptions: [],
      selfValidator: undefined,
      defaultValueFx: undefined,
    });
    dispatch({
      type: 'configCustomize/updateState',
      payload: {
        headerProps: {},
        conditionList: [],
        validatorList: [],
      },
    });
    // eslint-disable-next-line no-unused-expressions
    typeof onClose === 'function' && onClose({ field: {}, widget: {} });
  }

  shouldComponentUpdate(prev) {
    return (
      prev.visible !== this.props.visible ||
      this.props.visible === true ||
      prev.id !== this.props.id
    );
  }

  @Bind()
  handleChangeModel() {
    const { form } = this.props;
    form.setFieldsValue({
      fieldCode: '',
      fieldAlias: '',
      fieldType: '',
      fieldName: '',
    });
  }

  render() {
    const {
      relatedVisible,
      fieldLovMaps,
      conditionVisible,
      paramVisible,
      compRuleVisible,
      selfConditionVisible,
      defaultValueVisible,
      targetProp,
      conditionHeaders,
      condOptions,
      selfValidator,
      defaultValueFx,
    } = this.state;
    const {
      codes,
      visible,
      form,
      record,
      unitType,
      type,
      id,
      moduleList,
      saveLoading,
      unitList,
      unitAlias,
      fieldList,
      unitCode,
      conditionList = [],
      validatorList = [],
    } = this.props;
    const lines = selfValidator === undefined ? conditionList : selfValidator.lines;
    const valids = selfValidator === undefined ? validatorList : selfValidator.valids;
    const pureVirtual = unitType === 'TABPANE' || unitType === 'COLLAPSE';
    const isCreate = type === 'new';
    const isVirtual = record.modelId == -1 || form.getFieldValue('isModelField') == 0;
    const visibleFx = unitType !== 'FILTER' && unitType !== 'QUERYFORM';
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    return (
      <Drawer
        destroyOnClose
        maskClosable
        width={400}
        visible={visible}
        onClose={this.onClose}
        wrapClassName={styles['drawer-form']}
        title={getFieldConfigAlias(unitType)}
      >
        <FormItem style={{ display: isCreate && !pureVirtual ? 'block' : 'none' }}>
          {form.getFieldDecorator('isModelField', {
            initialValue: pureVirtual || (!isCreate && record.modelId == -1) ? 0 : 1,
          })(
            <Checkbox
              checkedValue={1}
              unCheckedValue={0}
              onChange={(v) =>
                form.setFieldsValue({
                  modelId: !v.target.checked ? -1 : (moduleList[0] || {}).modelId,
                  fieldId: !v.target.checked ? -1 : undefined,
                })
              }
            >
              {intl
                .get('hpfm.individuationUnit.model.individuationUnit.isModelField')
                .d('创建模型字段')}
            </Checkbox>
          )}
        </FormItem>
        {pureVirtual && (
          <FormItem label={getDefaultActiveAlias(unitType)}>
            {form.getFieldDecorator('defaultActive', {
              initialValue: isCreate ? -1 : record.defaultActive,
            })(
              <Select style={{ width: '100%' }}>
                {condOptions.map((item) => (
                  <Option value={Number(item.value)}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        )}
        <FormItem
          label={intl.get('hpfm.individual.model.config.modelCategory').d('所属模型')}
          style={{ display: isVirtual ? 'none' : 'block' }}
        >
          {form.getFieldDecorator('modelId', {
            initialValue: type === 'new' ? (moduleList[0] || {}).modelId : record.modelId,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.individual.model.config.modelCategory').d('所属模型'),
                }),
              },
            ],
          })(
            <Select
              style={{ width: '100%' }}
              disabled={type !== 'new'}
              onChange={this.handleChangeModel}
            >
              {moduleList.map((i) => (
                <Option value={i.modelId}>{i.modelName}</Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem label={getFieldCodeAlias(unitType)}>
          {form.getFieldDecorator('fieldCode', {
            initialValue: (record.field || {}).fieldCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: getFieldCodeAlias(unitType),
                }),
              },
            ],
          })(
            isVirtual ? (
              <Input
                trimAll
                inputChinese={false}
                disabled={type !== 'new' || record.custType === 'STD'}
              />
            ) : (
              <Lov
                code="HPFM.CUST.FIELD.NOT_CONFIG"
                queryParams={{ modelId: form.getFieldValue('modelId'), unitId: id }}
                lovOptions={{ displayField: 'fieldCode' }}
                textValue={(record.field || {}).fieldCode}
                onChange={this.setFieldInfo}
                disabled={type !== 'new'}
              />
            )
          )}
          {form.getFieldDecorator('fieldMultiLang', {
            initialValue: (record.field || {}).fieldMultiLang,
          })}
        </FormItem>
        {!isCreate && (
          <FormItem label={intl.get('hpfm.individual.model.config.custType').d('类型')}>
            {form.getFieldDecorator('custType', {
              initialValue: record.custType,
            })(
              <Select style={{ width: '100%' }} disabled>
                {codes.custType.map((i) => (
                  <Option value={i.value}>{i.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        )}
        {!isVirtual && (
          <FormItem label={intl.get('hpfm.individual.model.config.fieldAlias').d('编码别名')}>
            {form.getFieldDecorator('fieldAlias', {
              initialValue: record.fieldAlias,
            })(
              <Input
                disabled={
                  form.getFieldValue('fieldCode') === undefined || record.custType === 'STD'
                }
              />
            )}
          </FormItem>
        )}

        {!isVirtual ? (
          <FormItem label={intl.get('hpfm.individual.model.config.fieldType').d('存储类型')}>
            {form.getFieldDecorator('fieldType', {
              initialValue: (record.field || {}).fieldType,
            })(
              <Select style={{ width: '100%' }} disabled>
                {codes.fieldType.map((i) => (
                  <Option value={i.value}>{i.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        ) : null}
        <FormItem label={getFieldNameAlias(unitType)}>
          {form.getFieldDecorator('fieldName', {
            initialValue: record.fieldName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: getFieldNameAlias(unitType),
                }),
              },
            ],
          })(
            <TLEditor
              label={getFieldNameAlias(unitType)}
              field="fieldName"
              token={record._token}
              disabled={!isVirtual && form.getFieldValue('fieldCode') === undefined}
            />
          )}
        </FormItem>
        {isFormType ? (
          <FormItem
            label={intl.get('hpfm.individual.model.config.labelWrapperCol').d('标签组件比例')}
          >
            {form.getFieldDecorator('labelCol', {
              initialValue: record.labelCol,
            })(
              <Select
                allowClear
                showSearch
                style={{ width: '46%', marginRight: '8%', float: 'left' }}
                placeholder={intl.get('hpfm.individual.model.config.label').d('标签')}
              >
                {colOptions.map((i) => (
                  <Option value={i}>{i}</Option>
                ))}
              </Select>
            )}
            {form.getFieldDecorator('wrapperCol', {
              initialValue: record.wrapperCol,
            })(
              <Select
                allowClear
                showSearch
                style={{ width: '46%' }}
                placeholder={intl.get('hpfm.individual.model.config.wrapper').d('组件')}
              >
                {colOptions.map((i) => (
                  <Option value={i}>{i}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        ) : null}
        {!visibleFx && !isVirtual ? (
          <FormItem
            label={intl.get('hpfm.individual.model.config.whereOption').d('查询关系类型')}
            style={{ display: pureVirtual ? 'none' : 'block' }}
          >
            {form.getFieldDecorator('whereOption', {
              initialValue: record.whereOption || '=',
            })(
              <Select style={{ width: '100%' }} disabled={record.custType === 'STD'}>
                {codes.whereOptions.map((i) => (
                  <Option value={i.value}>{i.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        ) : null}

        {!pureVirtual ? (
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.bindField')
              .d('字段绑定')}
            style={{ display: !pureVirtual ? 'block' : 'none' }}
          >
            {form.getFieldDecorator('bindField', {
              initialValue: record.bindField,
            })(<Input trim inputChinese={false} />)}
          </FormItem>
        ) : null}
        <FormItem
          label={intl.get('hpfm.individual.model.config.renderOptions').d('渲染方式')}
          style={{ display: pureVirtual ? 'none' : 'block' }}
        >
          {form.getFieldDecorator('renderOptions', {
            initialValue: record.renderOptions || (isVirtual ? 'TEXT' : 'WIDGET'),
          })(
            <Select style={{ width: '100%' }} disabled={!isVirtual && record.custType === 'STD'}>
              {codes.renderOptions.map((i) => (
                <Option value={i.value}>{i.meaning}</Option>
              ))}
            </Select>
          )}
        </FormItem>
        <Row className={styles['flex-center-vertical']}>
          <Col span={12}>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.individual.model.config.visible').d('显示')}
            >
              {form.getFieldDecorator('visible', {
                initialValue: isCreate ? 1 : record.visible,
              })(
                <Select style={{ width: '93%' }}>
                  {condOptions.map((item) => (
                    <Option value={Number(item.value)}>{item.meaning}</Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col
            span={12}
            style={{ display: visibleFx ? 'block' : 'none', marginBottom: '14px' }}
            className={styles['fx-alink']}
          >
            <Tooltip
              placement="right"
              title={intl.get('hpfm.individual.model.config.condition').d('条件配置')}
            >
              <a
                className={(conditionHeaders.visible.lines || []).length > 0 ? 'active' : ''}
                onClick={() => this.toggleConditionModal('visible')}
              >
                fx
              </a>
              <Badge count={(conditionHeaders.visible.lines || []).length} />
            </Tooltip>
          </Col>
        </Row>
        <Row
          className={styles['flex-center-vertical']}
          style={{
            display:
              (form.getFieldValue('renderOptions') || record.renderOptions) === 'TEXT' ||
              pureVirtual
                ? 'none'
                : 'block',
          }}
        >
          <Col span={12}>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.individual.model.config.editable').d('编辑')}
            >
              {form.getFieldDecorator('fieldEditable', {
                initialValue: isCreate ? 1 : record.fieldEditable,
              })(
                <Select style={{ width: '93%' }}>
                  {condOptions.map((item) => (
                    <Option value={Number(item.value)}>{item.meaning}</Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col
            span={12}
            style={{ display: visibleFx ? 'block' : 'none', marginBottom: '14px' }}
            className={styles['fx-alink']}
          >
            <Tooltip
              placement="right"
              title={intl.get('hpfm.individual.model.config.condition').d('条件配置')}
            >
              <a
                className={(conditionHeaders.editable.lines || []).length > 0 ? 'active' : ''}
                onClick={() => this.toggleConditionModal('editable')}
              >
                fx
              </a>
              <Badge count={(conditionHeaders.editable.lines || []).length} />
            </Tooltip>
          </Col>
        </Row>
        <Row
          className={styles['flex-center-vertical']}
          style={{
            display:
              (form.getFieldValue('renderOptions') || record.renderOptions) === 'TEXT' ||
              pureVirtual
                ? 'none'
                : 'block',
          }}
        >
          <Col span={12}>
            <FormItem
              {...formLayout}
              label={intl.get('hpfm.individual.model.config.required').d('必输')}
            >
              {form.getFieldDecorator('fieldRequired', {
                initialValue: isCreate ? 0 : record.fieldRequired,
              })(
                <Select style={{ width: '93%' }}>
                  {condOptions.map((item) => (
                    <Option value={Number(item.value)}>{item.meaning}</Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col
            span={12}
            style={{ display: visibleFx ? 'block' : 'none', marginBottom: '14px' }}
            className={styles['fx-alink']}
          >
            <Tooltip
              placement="right"
              title={intl.get('hpfm.individual.model.config.condition').d('条件配置')}
            >
              <a
                className={(conditionHeaders.required.lines || []).length > 0 ? 'active' : ''}
                onClick={() => this.toggleConditionModal('required')}
              >
                fx
              </a>
              <Badge count={(conditionHeaders.required.lines || []).length} />
            </Tooltip>
          </Col>
        </Row>
        {!isFormType ? (
          <Row>
            <Col span={12}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.position').d('位置')}
                {...formLayout}
              >
                {form.getFieldDecorator('gridSeq', {
                  initialValue: record.gridSeq,
                })(<InputNumber style={{ width: '93%' }} precision={0} min={1} />)}
              </FormItem>
            </Col>
            <Col span={12} style={{ display: unitType === 'GRID' ? 'block' : 'none' }}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.gridWidth').d('宽度')}
                {...formLayout}
              >
                {form.getFieldDecorator('gridWidth', {
                  initialValue: record.gridWidth,
                })(<InputNumber style={{ width: '93%' }} precision={0} min={0} />)}
              </FormItem>
            </Col>
            <Col span={12} style={{ display: unitType === 'GRID' ? 'block' : 'none' }}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.gridFixed').d('冻结')}
                {...formLayout}
              >
                {form.getFieldDecorator('gridFixed', {
                  initialValue: record.gridFixed,
                })(
                  <Select style={{ width: '93%' }} allowClear>
                    {codes.fixed.map((i) => (
                      <Option value={i.value}>{i.meaning}</Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12} style={{ display: unitType === 'GRID' ? 'block' : 'none' }}>
              <FormItem
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 15 }}
                label={intl.get('hpfm.individual.model.config.sorter').d('可排序')}
              >
                {form.getFieldDecorator('sorter', {
                  initialValue: Number(record.sorter || 0),
                })(<Checkbox checkedValue={1} unCheckedValue={0} />)}
              </FormItem>
            </Col>
          </Row>
        ) : (
          <Row>
            <Col span={12}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.row').d('行')}
                {...formLayout}
              >
                {form.getFieldDecorator('formRow', {
                  initialValue: record.formRow,
                })(<InputNumber style={{ width: '93%' }} precision={0} min={1} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.col').d('列')}
                {...formLayout}
              >
                {form.getFieldDecorator('formCol', {
                  initialValue: record.formCol,
                })(<InputNumber style={{ width: '93%' }} precision={0} min={1} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.rowSpan').d('跨行')}
                {...formLayout}
              >
                {form.getFieldDecorator('rowSpan', {
                  initialValue: record.rowSpan || 1,
                })(<InputNumber style={{ width: '93%' }} precision={0} min={1} />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                label={intl.get('hpfm.individual.model.config.colSpan').d('跨列')}
                {...formLayout}
              >
                {form.getFieldDecorator('colSpan', {
                  initialValue: record.colSpan || 1,
                })(<InputNumber style={{ width: '93%' }} precision={0} min={1} />)}
              </FormItem>
            </Col>
          </Row>
        )}
        <FormItem
          label={intl.get('hpfm.individual.model.config.componentType').d('组件类型')}
          style={{
            display:
              (form.getFieldValue('renderOptions') || record.renderOptions) === 'WIDGET' &&
              !pureVirtual
                ? 'block'
                : 'none',
            marginBottom: '14px',
          }}
        >
          {form.getFieldDecorator('fieldWidget', {
            initialValue: (record.widget || {}).fieldWidget,
            rules: [
              {
                required:
                  (form.getFieldValue('renderOptions') || record.renderOptions) === 'WIDGET' &&
                  (record.custType !== 'STD' || (isVirtual && !pureVirtual)),
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.individual.model.config.componentType').d('组件类型'),
                }),
              },
              {
                use:
                  record.custType !== 'STD' &&
                  (form.getFieldValue('fieldMultiLang') ||
                    // eslint-disable-next-line eqeqeq
                    (record.field || {}).fieldMultiLang) == 1,
                validator: (_, val, cb) => {
                  if (!_.use) {
                    cb();
                    return;
                  }
                  if (val !== 'TL_EDITOR') {
                    cb(
                      intl
                        .get('hpfm.individual.view.message.validate1')
                        .d('该字段只能选择国际化组件')
                    );
                  }
                  cb();
                },
              },
            ],
          })(
            <Select
              allowClear
              style={{ width: '100%' }}
              onChange={this.onComponentChange}
              disabled={
                form.getFieldValue('fieldCode') === undefined ||
                (record.custType === 'STD' && !isVirtual)
              }
            >
              {codes.fieldWidget.map((i) => (
                <Option value={i.value}>{i.meaning}</Option>
              ))}
            </Select>
          )}
        </FormItem>
        {(form.getFieldValue('renderOptions') || record.renderOptions) === 'WIDGET'
          ? this.configMap(visibleFx)
          : null}
        <Button
          icon="setting"
          type="primary"
          onClick={this.toggleComputeRuleModal}
          style={{
            marginBottom: '8px',
            display:
              isVirtual &&
              !pureVirtual &&
              (form.getFieldValue('renderOptions') || record.renderOptions) === 'TEXT'
                ? 'block'
                : 'none',
          }}
        >
          {intl.get('hpfm.individual.common.setComputeRule').d('计算规则配置')}
        </Button>
        <Button
          icon="info-circle"
          type="primary"
          onClick={this.toggleSelfConditionModal}
          style={{
            display:
              (form.getFieldValue('renderOptions') || record.renderOptions) === 'TEXT' ||
              pureVirtual
                ? 'none'
                : 'block',
          }}
        >
          {intl.get('hpfm.individual.model.config.selfRule').d('自定义校验')}
          <Badge
            dot={lines.length > 0 || valids.length > 0}
            style={{ marginLeft: '8px', backgroundColor: 'red' }}
          />
        </Button>
        <footer className={styles.footer}>
          <Button onClick={this.onClose}>{intl.get('hzero.common.button.cancel').d('取消')}</Button>
          <Button type="primary" loading={saveLoading} onClick={this.onOk}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </footer>
        {relatedVisible && (
          <RelatedModal
            id={id}
            updateLovMappings={this.updateLovMappings}
            record={record}
            fieldLovMaps={fieldLovMaps}
            visible={relatedVisible}
            onClose={this.toggleRelatedModal}
          />
        )}
        {conditionVisible && (
          <ConditionModal
            id={id}
            unitList={unitList}
            fieldList={fieldList}
            unitType={unitType}
            fieldConditions={conditionHeaders[targetProp]}
            updateConditionHeaders={this.updateConditionHeaders}
            record={record}
            targetProp={targetProp}
            visible={conditionVisible}
            onClose={this.toggleConditionModal}
          />
        )}
        {paramVisible && (
          <ParamsModal
            type="unit"
            id={id}
            unitList={unitList}
            fieldList={fieldList}
            paramList={record.paramList}
            onSave={this.saveParamList}
            visible={paramVisible}
            readOnly={type !== 'new' && record.custType === 'STD'}
            onClose={this.toggleParamsModal}
          />
        )}
        {compRuleVisible && (
          <ComputeRuleModal
            rule={record.renderRule}
            unitType={unitType}
            unitCode={unitCode}
            unitList={unitList}
            record={record}
            unitAlias={unitAlias}
            visible={compRuleVisible}
            onOk={this.saveRenderRule}
            onClose={this.toggleComputeRuleModal}
          />
        )}
        {selfConditionVisible && (
          <SelfConditionModal
            destroyOnClose
            visible={selfConditionVisible}
            unitType={unitType}
            unitId={id}
            unitList={unitList}
            selfValidator={selfValidator}
            updateSelfValidator={this.updateSelfValidator}
            fieldId={record.configFieldId}
            fieldList={fieldList}
            onClose={this.toggleSelfConditionModal}
          />
        )}
        {defaultValueVisible && (
          <DefaultValueModal
            destroyOnClose
            extForm={form}
            visible={defaultValueVisible}
            unitType={unitType}
            unitId={id}
            unitList={unitList}
            selfValidator={defaultValueFx}
            updateSelfValidator={this.updateDefaultValueFx}
            fieldId={record.configFieldId}
            paramList={record.paramList}
            fieldList={fieldList}
            onClose={this.toggleDefaultValueModal}
          />
        )}
      </Drawer>
    );
  }
}

function getWidgetConfig(type, allData) {
  const {
    sourceCode,
    numberMax,
    numberMin,
    numberDecimal,
    textMaxLength,
    textMinLength,
    textAreaMaxLine,
    dateFormat,
    bucketName,
    bucketDirectory,
    linkTitle,
    linkHref,
    linkNewWindow,
    multipleFlag,
    placeholder,
  } = allData;

  const config = {
    bucketName: undefined,
    bucketDirectory: undefined,
    sourceCode: undefined,
    textMaxLength: undefined,
    textMinLength: undefined,
    textAreaMaxLine: undefined,
    numberMax: undefined,
    numberMin: undefined,
    dateFormat: undefined,
    lovMappings: undefined,
    multipleFlag: undefined,
    placeholder: undefined,
    defaultValue: allData.defaultValue,
    fieldWidget: allData.fieldWidget,
  };
  switch (type) {
    case 'RADIO_GROUP':
    case 'SELECT':
    case 'LOV':
      config.multipleFlag = multipleFlag;
      config.sourceCode = sourceCode;
      break;
    case 'CHECKBOX':
    case 'SWITCH':
      break;
    case 'INPUT':
      config.textMaxLength = textMaxLength;
      config.textMinLength = textMinLength;
      config.placeholder = placeholder;
      break;
    case 'INPUT_NUMBER':
      config.numberMax = numberMax;
      config.numberMin = numberMin;
      config.numberDecimal = numberDecimal;
      break;
    case 'DATE_PICKER':
      config.dateFormat = dateFormat;
      break;
    case 'TEXT_AREA':
      config.textMaxLength = textMaxLength;
      config.textMinLength = textMinLength;
      config.textAreaMaxLine = textAreaMaxLine;
      config.placeholder = placeholder;
      break;
    case 'UPLOAD':
      config.bucketDirectory = bucketDirectory;
      config.bucketName = bucketName;
      config.defaultValue = undefined;
      break;
    case 'LINK':
      config.linkHref = linkHref;
      config.linkTitle = linkTitle;
      config.linkNewWindow = linkNewWindow;
      break;
    default:
      config.defaultValue = undefined;
  }
  return config;
}
