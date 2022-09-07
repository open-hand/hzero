import React from 'react';
import { Icon, Input } from 'hzero-ui';
import { isUndefined, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { getCurrentLanguage, getResponse } from 'utils/utils';
import { queryTL } from 'services/api';
import TLModal from './TLModal';

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
      isSave: false,
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
    const { form = {} } = this.props;
    if (form && !isUndefined(form.getFieldValue('_tls'))) {
      form.resetFields(['_tls']);
    }
    this.setState({ modalVisible: false, isSave: false });
  }

  @Bind()
  save(data) {
    const { form, field: fieldName } = this.props;
    const { list, language } = this.state;
    // 设置多语言后，构建编辑的数据结构
    const newList = Object.keys(data).map(item => {
      const filterName = list.find(items => item === items.code);
      return { code: item, value: data[item], name: filterName.name || '' };
    });
    this.setState({ text: data[language], list: newList, modalVisible: false, isSave: true });
    if (form && isUndefined(form.getFieldValue('_tls'))) {
      // 往外层form配置_tls表单域
      form.registerField('_tls');
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
    const { data, field: fieldName = '', token: _token = '' } = this.props;
    const { list, text, language } = this.state;
    let { resLangList = [] } = this.state;
    if (Array.isArray(resLangList) && resLangList.length === 0 && isEmpty(data)) {
      // 仅调用一次接口，后续更新都使用缓存的语言数据
      queryTL({ fieldName, _token }).then(res => {
        if (getResponse(res)) {
          this.setState({ resLangList: res });
          this.setCacheList(res, list, language, text);
        }
      });
    } else {
      if (!isEmpty(data) && data[fieldName]) {
        resLangList = Object.keys(data[fieldName]).map(key => {
          let name = '';
          if (key === 'zh_CN') {
            name = '简体中文';
          } else if (key === 'en_US') {
            name = 'English';
          } else {
            name = '日本語';
          }
          return {
            name,
            code: key,
            value: data[fieldName][key],
          };
        });
      }
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
    const { isSave } = this.state;
    this.setState({ text: e.target.value });
    if (this.props.onChange) {
      this.props.onChange(e.target.value);
      // 未保存的多语言数据，清空_tls字段，清除缓存
      if (!isSave) {
        this.props.form.setFieldsValue({ _tls: null });
      }
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
