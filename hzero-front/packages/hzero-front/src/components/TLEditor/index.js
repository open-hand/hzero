import React from 'react';
import { Icon, Input } from 'hzero-ui';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { getCurrentLanguage, getResponse } from 'utils/utils';
import TLModal from './TLModal';
import { queryTL } from '../../services/api';

import './index.less';

/**
 * 多语言组件 TLEditor
 *
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @extends {Component} - React.PureComponent
 * @reactProps {!boolean} [allowClear=false] - 是否允许清除
 * @reactProps {!string} [field=''] - 表单域
 * @reactProps {!string} [label = ''] - 表单label
 * @reactProps {!string} [token = ''] - 数据加密token
 * @reactProps {!string} [width = '520px'] - 模态框宽度
 * @returns React.element
 * @example
 * import TLEditor from 'components/TLEditor';
 *
 *      <FormItem
 *      label={intl.get('hpfm.country.model.country.countryName').d('国家名称')}
 *      {...formLayout}
 *      >
 *        {getFieldDecorator('countryName', {
 *          initialValue: countryName,
 *          rules: [
 *            {
 *              type: 'string',
 *              required: true,
 *              message: intl.get('hzero.common.validation.notNull', {
 *                name: intl.get('hpfm.country.model.country.countryName').d('国家名称'),
 *              }),
 *            },
 *          ],
 *        })(
 *          <TLEditor
 *            label={intl.get('hpfm.country.model.country.countryName').d('国家名称')}
 *            field="countryName"
 *            token={_token}
 *          />
 *        )}
 *      </FormItem>
 *
 */
export default class TLEditor extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      language: getCurrentLanguage() || 'zh_CN', // 获取系统当前语言
      loading: false, // 请求多语言列表loading
      modalVisible: false, // 控制多语言模态框
      list: [], // 多语言列表
      resLangList: [], // 接口返回的多语言数据
      text: props.value, // 输入框显示值
    };
  }

  static displayName = 'TLEditor';

  componentDidMount() {
    // 初始化时清空表单多语言字段
    this.props.form.setFieldsValue({ _tls: null });
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { text } = this.state;
    if (text && text !== nextProps.value) {
      this.setState({ text: nextProps.value });
    }
    if (!text && nextProps.value) {
      this.setState({ text: nextProps.value });
    }
  }

  @Bind()
  showLoading(flag) {
    this.setState({
      loading: flag,
    });
  }

  @Bind()
  onCancel() {
    this.setState({ modalVisible: false });
  }

  @Bind()
  save(data) {
    const { form, field: fieldName } = this.props;
    const { list, language } = this.state;
    // 设置多语言后，构建编辑的数据结构
    const newList = Object.keys(data).map((item) => {
      const filterName = list.find((items) => item === items.code);
      return { code: item, value: data[item], name: filterName.name || '' };
    });
    this.setState({ text: data[language], list: newList, modalVisible: false });
    if (form && isUndefined(form.getFieldValue('_tls'))) {
      // 往外层form配置_tls表单域
      // form.registerField('_tls');
      // 设置_tls值
      form.setFieldsValue({ _tls: { [fieldName]: data } });
    } else {
      const oldTls = form.getFieldValue('_tls');
      const newTls = { ...oldTls, [fieldName]: data };
      form.setFieldsValue({ _tls: newTls });
    }
    // 更新input显示值
    if (this.props.onChange) {
      this.props.onChange(data[language]);
    }
  }

  @Bind()
  openModal() {
    const { form, field: fieldName = '', token: _token = '' } = this.props;
    const { list, text, language, resLangList = [] } = this.state;
    if (Array.isArray(resLangList) && resLangList.length === 0) {
      // 仅调用一次接口，后续更新都使用缓存的语言数据
      queryTL({ fieldName, _token }).then((res) => {
        if (getResponse(res)) {
          this.setState({ resLangList: res });
          this.setCacheList(res, list, language, text);
          const tls = {};
          res.map((item) => {
            tls[item.code] = item.value;
            return null;
          });
          // 往外层form配置_tls表单域
          form.registerField('_tls');
          if (form && isUndefined(form.getFieldValue('_tls'))) {
            // 设置_tls值
            form.setFieldsValue({ _tls: { [fieldName]: tls } });
          } else {
            const oldTls = form.getFieldValue('_tls');
            const newTls = { ...oldTls, [fieldName]: { ...tls, [language]: text } };
            form.setFieldsValue({ _tls: newTls });
          }
        }
      });
    } else {
      this.setCacheList(resLangList, list, language, text);
    }
  }

  @Bind()
  setCacheList(resLangList, list, language, text) {
    const newRes = resLangList.map((item, index) => {
      const obj = { ...item };
      if (Array.isArray(list) && list[index] !== undefined) {
        // 处理修改后值的回显
        if (list[index].value) {
          obj.value = list[index].value;
        }
      }
      // 处理编辑外部输入框之后
      if (language === item.code) {
        obj.value = text;
      }
      return obj;
    });
    this.setState({ modalVisible: true, list: newRes });
  }

  @Bind()
  onChange(e) {
    const { resLangList, list, language } = this.state;
    const { form, field: fieldName = '' } = this.props;
    this.setState({ text: e.target.value });
    const tls = form.getFieldValue('_tls');
    if (!tls) {
      form.setFieldsValue({ _tls: { [fieldName]: { [language]: e.target.value } } });
    } else if (tls[fieldName]) {
      tls[fieldName][language] = e.target.value;
    } else {
      tls[fieldName] = { [language]: e.target.value };
    }
    if (this.props.onChange) {
      this.props.onChange(e.target.value);
      const newRes = resLangList.map((item, index) => {
        const obj = { ...item };
        // 处理编辑外部输入框之后
        if (Array.isArray(list) && list[index] !== undefined) {
          // 处理修改后值的回显
          if (list[index].value) {
            obj.value = list[index].value;
          }
        }
        if (language === item.code) {
          obj.value = e.target.value;
        }
        return obj;
      });
      this.setState({ list: newRes });
    }
  }

  @Bind()
  emitEmpty() {
    if (this.props.onChange) {
      this.setState(
        {
          text: null,
        },
        () => {
          this.props.onChange(null);
        }
      );
    }
  }

  render() {
    const {
      allowClear = false,
      field: fieldName,
      label = '',
      width,
      inputSize = {},
      className = '',
      dbc2sbc = false,
      ...otherProps
    } = this.props;
    const { disabled = false } = this.props;
    const { text, modalVisible, list, loading } = this.state;
    const modalProps = {
      label,
      fieldName,
      modalVisible,
      list,
      width,
      inputSize,
      dbc2sbc,
      onCancel: this.onCancel,
      onOK: this.save,
    };
    const classNames = [className, 'tl-editor'];
    const suffix = (
      <>
        {allowClear && text && (
          <Icon type="close-circle" onClick={this.emitEmpty} className="tl-editor-clear" />
        )}
        <Icon
          type={loading ? 'loading' : 'global'}
          className="tl-editor-trigger"
          // eslint-disable-next-line no-nested-ternary
          style={loading ? {} : disabled ? { cursor: 'not-allowed' } : { cursor: 'pointer' }}
          onClick={!loading && !disabled ? this.openModal : undefined}
        />
      </>
    );
    return (
      <>
        <Input
          {...otherProps}
          dbc2sbc={dbc2sbc}
          onChange={this.onChange}
          value={text}
          className={classNames.join(' ')}
          suffix={suffix}
        />
        <TLModal {...modalProps} />
      </>
    );
  }
}
