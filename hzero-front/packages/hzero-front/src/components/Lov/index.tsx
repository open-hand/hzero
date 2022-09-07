import React, { ReactElement, ChangeEvent } from 'react';
import { Modal, Input, Icon, Button, message } from 'hzero-ui';
import { isEmpty, isFunction, omit, isNil } from 'lodash';
import { Bind, Throttle } from 'lodash-decorators';
import uuid from 'uuid/v4';

import intl from 'utils/intl';
import { getResponse } from 'utils/utils';

import LovModal from './LovModal';
import { queryLov, queryMapIdpValue } from '../../services/api';
import './index.less';

const defaultRowKey = 'key';

export interface LovProps {
  value?: any;
  isInput?: boolean;
  disabled?: boolean;
  lovOptions?: any;
  extSetMap?: any;
  form?: any;
  originTenantId?: number;
  code?: string;
  textValue?: string;
  queryParams?: any;
  queryInputProps?: any;
  style?: React.CSSProperties;
  isButton?: boolean;
  className?: string;
  allowClear?: boolean;
  onOk?: any;
  onChange?: any;
  onClear?: any;
  onClick?: any;
  onCancel?: any;
  isDbc2Sbc?: boolean;
  placeholder?: string;
  publicMode?: boolean;
  maskClosable?: boolean;
  textField?: string;
}

export default class Lov extends React.Component<LovProps, any> {
  // 选中记录
  record: any = undefined;

  static displayName = 'Lov';

  loading = false;

  modalRef: React.RefObject<any> = { current: null };

  cacheValue;

  constructor(props) {
    super(props);
    this.state = {
      text: props.isInput ? props.value : props.textValue,
      textField: props.textField,
      lov: {},
      loading: false,
      ldpData: {},
    };
    this.modalRef = React.createRef();
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { text } = this.state;
    let data: any = {
      text: nextProps.value !== this.cacheValue ? undefined : text,
    };

    if (nextProps.value === null || nextProps.value === undefined) {
      data = {
        ...data,
        text: null,
      };
    }
    if (nextProps.isInput) {
      data = {
        ...data,
        text: nextProps.value,
      };
    }

    this.setState({
      ...data,
    });
  }

  @Bind()
  onSelect(record) {
    this.record = record;
  }

  @Bind()
  selectAndClose() {
    if (!this.record) {
      Modal.warning({
        title: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
      return false;
    }
    this.selectRecord();
    this.setState({
      modalVisible: false,
    });
  }

  getTextField() {
    const { form } = this.props;
    const { textField } = this.state;
    if (form && textField) {
      form.registerField(textField);
    }
    return textField;
  }

  selectRecord() {
    const { isInput } = this.props;
    const { valueField: rowkey = defaultRowKey, displayField: displayName } = this.state.lov;
    this.cacheValue = this.parseField(this.record, rowkey); // 记录lov变更时对应props中的value， 一旦此value改变，便说明此时、state中的text无效
    // TODO: 值为 0 -0 '' 等的判断

    this.setState(
      {
        text: this.parseField(this.record, displayName),
      },
      () => {
        const { form } = this.props;
        const textField = this.getTextField();
        if (form && textField) {
          form.setFieldsValue({
            [textField]: this.parseField(this.record, displayName),
          });
        }
        // 设置额外表单值
        if (form && this.props.extSetMap) {
          this.setExtMapToForm(this.record, this.props.extSetMap, form);
        }

        if (this.props.onChange) {
          const valueField = isInput ? displayName : rowkey;
          this.props.onChange(this.parseField(this.record, valueField), this.record);
        }
        if (isFunction(this.props.onOk)) {
          this.props.onOk(this.record);
        }
        this.record = null;
      }
    );
  }

  /**
   * 设置额外表单值
   * @param {Object} record 数据对象
   * @param {String} extSetMap 额外字段映射, 可以有多个, 以逗号分隔 bankId,bankName->bankDescription
   * @param {表单对象} form 表单对象
   */
  setExtMapToForm(record, extSetMap, form) {
    const dataSet = {};
    extSetMap.split(/\s*,\s*/g).forEach((entryStr) => {
      const [recordField, formFieldTmp] = entryStr.split('->');
      const formField = formFieldTmp || recordField;
      form.getFieldDecorator(formField);
      dataSet[formField] = record[recordField];
    });
    form.setFieldsValue(dataSet);
  }

  @Bind()
  onCancel() {
    const { onCancel = (e) => e } = this.props;
    this.setState({
      modalVisible: false,
    });
    if (isFunction(onCancel)) {
      onCancel();
    }
    this.record = null;
  }

  showLoading(partialState = {}) {
    this.setState({
      loading: true,
      ...partialState,
    });
  }

  hideLoading() {
    this.setState({
      loading: false,
    });
  }

  @Bind()
  modalWidth(tableFields) {
    let width = 100;
    tableFields.forEach((n) => {
      width += n.width;
    });
    return width;
  }

  @Bind()
  onSearchBtnClick(): void {
    const {
      disabled = false,
      onClick = (e) => e,
      lovOptions: { valueField: customValueField, displayField: customDisplayField } = {} as any,
      publicMode = false,
      textField,
    } = this.props;
    if (disabled || this.loading) return; // 节流

    this.record = null;
    const { code: viewCode, originTenantId: tenantId } = this.props;
    this.loading = true;
    this.showLoading({
      loading: true,
      modalVisible: true,
      lovModalKey: uuid(),
    });
    queryLov({ viewCode, tenantId, publicMode })
      .then((oriLov) => {
        const lov = { ...oriLov };
        if (customValueField) {
          lov.valueField = customValueField;
        }
        if (customDisplayField) {
          lov.displayField = customDisplayField;
        }
        if (!isEmpty(lov)) {
          const { viewCode: hasCode, title = '' } = lov;
          // 获取独立值集编码
          const valueList = lov.queryFields.filter((item) => item.dataType === 'SELECT');
          if (valueList.length > 0) {
            const valueCode = {};
            valueList.forEach(({ sourceCode }) => {
              if (sourceCode) {
                valueCode[sourceCode] = sourceCode;
              }
            });
            queryMapIdpValue({ ...valueCode, publicMode }).then((res) => {
              if (getResponse(res)) {
                this.setState({ ldpData: res });
              }
            });
          }

          if (hasCode) {
            // const width = this.modalWidth(tableFields);
            this.setState(
              {
                lov,
                title,
                textField: textField || `__${lov.displayField}`,
                // width,
              },
              () => {
                const { modalVisible: lovModalVisible } = this.state;
                if (lovModalVisible && this.modalRef.current) {
                  this.modalRef.current.loadOnFirstVisible();
                }
              }
            );
            if (isFunction(onClick)) {
              onClick();
            }
          } else {
            this.hideLoading();
            message.error(
              intl.get('hzero.common.components.lov.notification.undefined').d('值集视图未定义!')
            );
          }
        }
      })
      .finally(() => {
        this.hideLoading();
        this.loading = false;
      });
  }

  searchButton(): ReactElement {
    if (this.state.loading) {
      return <Icon key="search" type="loading" />;
    }
    return (
      <Icon
        key="search"
        type="search"
        onClick={this.onSearchBtnClick as any}
        style={{ cursor: 'pointer', color: '#666' }}
      />
    );
  }

  @Bind()
  emitEmpty() {
    const { text, lov } = this.state;
    const { form, onClear = (e) => e, value, onChange } = this.props;
    if (onChange) {
      const record = {};
      this.setState(
        {
          text: '',
        },
        () => {
          onChange(undefined, record);
          const textField = this.getTextField();
          if (form && textField) {
            form.setFieldsValue({
              [textField]: undefined,
            });
          }
        }
      );
    }
    // TODO: 当初次进入时的情况
    if (isFunction(onClear)) {
      const record = {
        [lov.displayField]: text,
        [lov.valueField]: value,
      };
      onClear(record);
    }
  }

  /**
   * 访问对象由字符串指定的多层属性
   * @param {Object} obj 访问的对象
   * @param {String} str 属性字符串，如 'a.b.c.d'
   */
  @Bind()
  parseField(obj, str) {
    if (/[.]/g.test(str)) {
      const arr = str.split('.');
      const newObj = obj[arr[0]];
      const newStr = arr.slice(1).join('.');
      return this.parseField(newObj, newStr);
    }
    return obj[str];
  }

  /**
   * 同步 Lov 值节流以提高性能
   * @param {String} value - Lov 组件变更值
   */
  @Bind()
  @Throttle(500)
  setValue(value) {
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  }

  /**
   * 同步输入值至 Input 及 Lov
   * @param {String} value - 输入框内的值
   */
  @Bind()
  setText(value) {
    const { isInput } = this.props;
    if (isInput) {
      this.setState(
        {
          text: value,
        },
        () => {
          this.setValue(value);
        }
      );
    }
  }

  render() {
    const { text: stateText, ldpData = {} } = this.state;
    const {
      form,
      value,
      textValue,
      queryParams,
      queryInputProps,
      style,
      isButton,
      isInput,
      className,
      isDbc2Sbc,
      allowClear = true,
      maskClosable = false,
      ...otherProps
    } = this.props;
    const textField = this.getTextField();
    let text;
    const omitProps = ['onOk', 'onCancel', 'onClick', 'onClear', 'textField', 'lovOptions'];
    if (isInput) {
      text = stateText;
      omitProps.push('onChange');
    } else {
      const texts = textField ? form && form.getFieldValue(textField) : stateText;
      // eslint-disable-next-line no-nested-ternary
      text = isNil(value) ? '' : texts === 0 ? 0 : texts || textValue;
      if (isNil(text)) text = value;
    }
    const inputStyle: React.CSSProperties | undefined = isButton
      ? style
      : {
          ...style,
          verticalAlign: 'middle',
          position: 'relative',
          top: -1,
        };
    const isDisabled = this.props.disabled !== undefined && !!this.props.disabled;
    const showSuffix = text && allowClear && !isButton && !isDisabled;
    const suffix = (
      <>
        <Icon key="clear" className="lov-clear" type="close-circle" onClick={this.emitEmpty} />
        {this.searchButton()}
      </>
    );

    const lovClassNames = [className, 'lov-input'];
    if (showSuffix) {
      lovClassNames.push('lov-suffix');
    }
    if (isDisabled) {
      lovClassNames.push('lov-disabled');
    }
    const { title = '', lov = {}, modalVisible, loading, lovModalKey } = this.state;
    const modalProps = {
      title,
      width: 700,
      destroyOnClose: true,
      wrapClassName: 'lov-modal',
      maskClosable,
      onOk: this.selectAndClose,
      bodyStyle: title ? { padding: '16px' } : { padding: '56px 16px 0' },
      onCancel: this.onCancel,
      style: {
        minWidth: 700,
      },
      visible: modalVisible,
    };
    const { disabled, onChange, placeholder } = this.props;
    const inputProps = {
      disabled,
      onChange,
      placeholder,
      // onClick,
    };
    return (
      <>
        {isButton ? (
          <Button
            onClick={this.onSearchBtnClick as any}
            disabled={this.props.disabled}
            {...otherProps}
            style={style}
            className={lovClassNames.join(' ')}
          />
        ) : (
          // ts-ignore
          <Input
            readOnly={!isInput}
            // addonAfter={this.searchButton()}
            value={text}
            style={inputStyle} // Lov 组件垂直居中样式，作用于 ant-input-group-wrapper
            suffix={suffix}
            onChange={(e: ChangeEvent<HTMLInputElement>): void => {
              this.setText(e.target.value);
            }}
            {...omit(inputProps, omitProps)}
            className={lovClassNames.join(' ')}
          />
        )}
        <Modal {...modalProps}>
          <LovModal
            key={lovModalKey}
            lov={lov}
            ldpData={ldpData}
            queryParams={queryParams}
            queryInputProps={queryInputProps}
            onSelect={this.onSelect}
            onClose={this.selectAndClose}
            lovLoadLoading={loading}
            wrappedComponentRef={this.modalRef}
            isDbc2Sbc={isDbc2Sbc}
            // lovLoadLoading={loading}
          />
        </Modal>
      </>
    );
  }
}
