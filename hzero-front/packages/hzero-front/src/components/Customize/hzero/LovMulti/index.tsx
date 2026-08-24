/**
 * 多选lov组件
 * @date: 2020-5-7
 * @version: 0.0.1
 * @author: zhaotong <tong.zhao@hand-china.com>
 * @copyright Copyright (c) 2020, Hands
 */

/* eslint-disable react/no-did-update-set-state */
/* eslint-disable no-unused-expressions */
import React, { ReactElement } from 'react';
import { Modal, Input, Icon, message, Tag } from 'hzero-ui';
import { isEmpty, isFunction, omit, isNil, isArray, isString } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import intl from 'utils/intl';
import { getResponse } from 'utils/utils';
import { queryLov, queryMapIdpValue } from 'services/api';

import TransferModal from './LovModal';
import ViewOnlyModal from './ViewOnlyModal';
import './index.less';

export interface LovProps {
  value?: string;
  disabled?: boolean;
  lovOptions?: any;
  extSetMap?: any;
  form?: any;
  originTenantId?: number;
  code?: string;
  displayData?: string[];
  queryParams?: any;
  queryInputProps?: any;
  style?: React.CSSProperties;
  className?: string;
  allowClear?: boolean;
  delimma?: string;
  onOk?: any;
  onChange?: any;
  onClear?: any;
  onClick?: any;
  onCancel?: any;
  isDbc2Sbc?: boolean;
  translateData: any;
  showAll: boolean;
  viewOnly: boolean;
  queryUsePost: boolean;
}

export default class TransferLov extends React.Component<LovProps, any> {
  // 选中记录
  records: any = undefined;

  state: {
    saveLoading: boolean;
    displayData: string[];
    textField: string;
    lov: any;
    loading: boolean;
    ldpData: any;
    modalVisible: boolean;
    lovModalKey?: string;
    title?: string;
    translateData: any;
    values: string[];
  };

  static displayName = 'LovMulti';

  loading = false;

  modalRef: React.RefObject<any> = { current: null };

  constructor(props) {
    super(props);
    this.state = {
      displayData: [],
      translateData: {},
      values: [],
      textField: props.textField,
      lov: {},
      loading: false,
      ldpData: {},
      modalVisible: false,
      saveLoading: false,
    };
    this.modalRef = React.createRef();
  }

  componentDidMount() {
    const { value, displayData, translateData } = this.props;
    this.initDisplayData(value, displayData, translateData);
  }

  componentDidUpdate(prevProps) {
    const { displayData, value } = this.props;
    const { translateData } = this.props;

    if (value !== prevProps.value || displayData !== prevProps.displayData) {
      this.initDisplayData(value, displayData, translateData);
    }
  }

  @Bind()
  initDisplayData(value, displayData, translateData) {
    let values: any = [];
    const { showAll, viewOnly } = this.props;
    if (isString(value) && value !== '') {
      values = value.split(',');
    }
    if (viewOnly) {
      this.setState({ values });
      return;
    }
    if (values.length > 0) {
      let newTranslateData = {};
      let newDisplayData: any = [];
      if (typeof translateData === 'string' && translateData !== '') {
        newTranslateData[values[0]] = translateData;
      } else {
        newTranslateData = {
          ...translateData,
        };
      }
      newTranslateData = { ...newTranslateData, ...this.state.translateData };
      if (isArray(displayData) && isEmpty(this.state.displayData)) {
        newDisplayData = displayData;
      }
      newDisplayData = (showAll ? values : values.slice(0, 5)).map(
        (i, index) => newTranslateData[i] || newDisplayData[index] || i
      );
      if (values.length > 5 && !showAll) {
        newDisplayData.push('...');
      }
      this.setState({ displayData: newDisplayData, translateData: newTranslateData, values });
    }
  }

  @Bind()
  async selectAndClose() {
    const { form, onChange, viewOnly } = this.props;
    const { translateData } = this.state;
    this.setState({ saveLoading: true });
    if (viewOnly) {
      this.setState({
        saveLoading: false,
        modalVisible: false,
      });
      return;
    }
    const { values, meaningMap } = await this.modalRef.current.getSelectedData();
    const value = values.length > 0 ? values.join(',') : undefined;
    const textField = this.getTextField();

    if (!isNil(textField)) {
      form && form.setFieldsValue({ [textField]: value });
    }
    typeof onChange === 'function' && onChange(value);
    const displayData = (values.length > 5 ? values.slice(0, 5) : values).map((i) => meaningMap[i]);
    if (values.length > 5) {
      displayData.push('...');
    }
    this.setState({
      saveLoading: false,
      translateData: { ...translateData, ...meaningMap },
      displayData,
      values,
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

  @Bind()
  onCancel() {
    const { onCancel = (e) => e } = this.props;
    this.setState({
      modalVisible: false,
    });
    if (isFunction(onCancel)) {
      onCancel();
    }
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
  onSearchBtnClick(): void {
    const {
      disabled = false,
      onClick = (e) => e,
      queryUsePost = false,
      lovOptions: { valueField: customValueField, displayField: customDisplayField } = {} as any,
    } = this.props;
    if (disabled || this.loading) return; // 节流

    const { code: viewCode, originTenantId: tenantId } = this.props;
    this.loading = true;
    this.showLoading({
      loading: true,
      modalVisible: true,
      lovModalKey: uuid(),
    });

    queryLov({ viewCode, tenantId })
      .then((oriLov) => {
        const lov = { ...oriLov, queryUsePost };
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
            queryMapIdpValue(valueCode).then((res) => {
              if (getResponse(res, () => {})) {
                this.setState({ ldpData: res });
              }
            });
          }

          if (hasCode) {
            this.setState({ lov, title });
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
    const { form, onClear = (e) => e, value, onChange } = this.props;
    if (onChange) {
      const records = {};
      this.setState(
        {
          displayData: [],
        },
        () => {
          onChange(undefined, records);
          const textField = this.getTextField();
          if (form && textField) {
            form.setFieldsValue({
              [textField]: undefined,
            });
          }
        }
      );
    }
    if (isFunction(onClear)) {
      onClear(value);
    }
  }

  render() {
    const { displayData: stateDisplay, ldpData = {}, values, saveLoading } = this.state;
    const {
      value,
      queryParams,
      queryInputProps,
      style,
      className,
      isDbc2Sbc,
      delimma = '/',
      displayData = [],
      allowClear = true,
      viewOnly,
      code,
    } = this.props;
    const omitProps = ['onOk', 'onCancel', 'onClick', 'onClear', 'textField', 'lovOptions'];
    // eslint-disable-next-line no-nested-ternary
    const text = isNil(value)
      ? ''
      : (stateDisplay.length > 0 ? stateDisplay : displayData).join(delimma);
    const inputStyle: React.CSSProperties | undefined = {
      ...style,
      verticalAlign: 'middle',
      position: 'relative',
      top: -1,
    };
    const isDisabled = this.props.disabled !== undefined && !!this.props.disabled;
    const showSuffix = text && allowClear && !isDisabled;
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
      width: viewOnly ? 680 : 1325,
      destroyOnClose: true,
      wrapClassName: 'lov-modal',
      maskClosable: false,
      onOk: this.selectAndClose,
      okButtonProps: { loading: saveLoading },
      bodyStyle: title ? { padding: '16px 32px 0' } : { padding: '56px 32px 0' },
      onCancel: this.onCancel,
      style: {
        minWidth: 400,
      },
      visible: modalVisible,
    };
    const { disabled, onChange } = this.props;
    const inputProps = {
      disabled,
      onChange,
      // onClick,
    };
    return (
      <>
        {viewOnly ? (
          <a onClick={this.onSearchBtnClick}>
            {intl.get('hzero.common.button.view').d('查看')}
            {values.length > 0 ? (
              <Tag
                color="#108ee9"
                style={{
                  height: 'auto',
                  lineHeight: '15px',
                  marginLeft: '4px',
                }}
              >
                {values.length}
              </Tag>
            ) : null}
          </a>
        ) : (
          <Input
            readOnly
            value={text}
            style={inputStyle} // Lov 组件垂直居中样式，作用于 ant-input-group-wrapper
            suffix={suffix}
            {...omit(inputProps, omitProps)}
            className={lovClassNames.join(' ')}
          />
        )}
        <Modal {...modalProps}>
          {viewOnly ? (
            <ViewOnlyModal
              lov={lov}
              currentValue={value}
              ldpData={ldpData}
              queryParams={queryParams}
              queryInputProps={queryInputProps}
              lovLoadLoading={loading}
              wrappedComponentRef={this.modalRef}
              isDbc2Sbc={isDbc2Sbc}
              code={code}
            />
          ) : (
            <TransferModal
              key={lovModalKey}
              lov={lov}
              currentValue={value}
              ldpData={ldpData}
              queryParams={queryParams}
              queryInputProps={queryInputProps}
              lovLoadLoading={loading}
              wrappedComponentRef={this.modalRef}
              isDbc2Sbc={isDbc2Sbc}
              code={code}
            />
          )}
        </Modal>
      </>
    );
  }
}
