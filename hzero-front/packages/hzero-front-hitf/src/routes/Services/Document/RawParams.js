/**
 * RawParams - raw类型的BODY参数信息
 * @date: 2019/6/5
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Spin, Select } from 'hzero-ui';
import intl from 'utils/intl';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import { Bind } from 'lodash-decorators';
import style from './index.less';

const { Option } = Select;

/**
 * raw类型的BODY参数信息
 * @extends {Component} - React.Component
 * @reactProps {boolean} loading - 加载中标志
 * @reactProps {boolean} confirmLoading - 保存中标志
 * @reactProps {array} rawMimeTypes - raw的类型值集
 * @reactProps {string} paramType - 参数类型
 * @reactProps {string} mimeType - body的MIMETYPE
 * @reactProps {string} resRawType - 请求体中raw类型的MIMETYPE
 * @reactProps {string} respRawType - 响应体中raw类型的MIMETYPE
 * @reactProps {array} dataSource - 数据源
 * @reactProps {string} actionType - 请求方式
 * @reactProps {number} interfaceId - 接口ID
 * @reactProps {number} reqRawFlag - 请求体中是否为raw类型
 * @reactProps {Function} onSave - 保存参数
 * @reactProps {Function} onRawChange - 切换raw的类型
 * @return React.element
 */
export default class RawParams extends Component {
  editor;

  constructor() {
    super();
    this.state = {
      rawValue: '',
    };
  }

  componentDidMount() {
    const { dataSource } = this.props;
    this.setState({
      rawValue: dataSource ? dataSource[0].defaultValueLongtext : '',
    });
  }

  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.dataSource !== this.props.dataSource) {
      this.setState({
        rawValue: nextProps.dataSource[0].defaultValueLongtext,
      });
    }
    return null;
  }

  /**
   * 代码编辑器输入时
   * @param {object} editor 代码编辑器
   * @param {*} data
   * @param {*} value 代码编辑器当前内容
   */
  @Bind()
  handleChangeValue(editor, data, value) {
    this.setState({
      rawValue: value,
    });
  }

  /**
   * 改变raw类型
   * @param {string} value 选中值
   */
  @Bind()
  changeRawType(value) {
    const mimeTypeObj =
      this.props.actionType === 'REQ'
        ? { reqMimeType: value, reqRawFlag: 1 }
        : { respMimeType: value, respRawFlag: 1 };
    this.props.onRawChange(mimeTypeObj);
  }

  /**
   * 保存参数信息
   */
  @Bind()
  handleOk() {
    const { onSave, dataSource, paramType, actionType, mimeType } = this.props;
    const { rawValue } = this.state;
    let totalValues = {};
    // 新建
    if (!dataSource) {
      // 校验通过，进行保存操作
      totalValues = {
        requiredFlag: 0,
        paramType,
        actionType,
        mimeType,
        defaultValueLongtext: rawValue,
      };
      onSave({ values: totalValues, flag: 'create' });
      // 编辑
    } else {
      const { objectVersionNumber, _token, paramId } = dataSource[0];
      totalValues = {
        requiredFlag: 0,
        paramId,
        paramType,
        actionType,
        mimeType,
        _token,
        objectVersionNumber,
        defaultValueLongtext: rawValue,
      };
      onSave({ values: totalValues, flag: 'edit' });
    }
  }

  /**
   * 展示rawType
   */
  @Bind()
  getRawType() {
    const { actionType, respRawType, resRawType } = this.props;
    let rawType = 'text/plain';
    if (actionType === 'REQ') {
      rawType = resRawType;
    }

    if (actionType === 'RESP') {
      rawType = respRawType;
    }
    return rawType;
  }

  render() {
    const { rawValue } = this.state;
    const { loading, confirmLoading, rawMimeTypes } = this.props;
    return (
      <Spin spinning={loading}>
        <div className="table-list-search" style={{ textAlign: 'right', marginTop: '10px' }}>
          <span>{intl.get('hitf.document.view.title.rawType').d('raw类型')}:</span>
          <Select
            style={{ margin: '0 8px', width: '150px' }}
            onChange={this.changeRawType}
            value={this.getRawType()}
          >
            {rawMimeTypes.length &&
              rawMimeTypes.map(({ value, meaning }) => (
                <Option key={value} value={value}>
                  {meaning}
                </Option>
              ))}
          </Select>
          <Button onClick={this.handleOk} type="primary" loading={confirmLoading}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
        <CodeMirror
          autoScroll
          className={style['hzero-codemirror']}
          value={rawValue}
          options={{
            mode: 'javascript',
            lineNumbers: true,
          }}
          onBeforeChange={this.handleChangeValue}
        />
      </Spin>
    );
  }
}
