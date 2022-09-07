/**
 * Demo - 示例表单(成功示例、失败示例)
 * @date: 2019/5/21
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Col, Form, Input, Row, Select } from 'hzero-ui';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import style from './index.less';

const { Option } = Select;
const FormItem = Form.Item;
const lastFormLayout = {
  labelCol: {
    span: 3,
  },
  wrapperCol: {
    span: 16,
  },
};

/**
 * 示例
 * @extends {Component} - React.Component
 * @reactProps {string} demo - 示例数据
 * @reactProps {string} status - 状态码
 * @reactProps {string} mimeType - Content-Type
 * @reactProps {array} respContentTypes - content-type的值集
 * @reactProps {string} demoType - 示例类型(Success/Failed)
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Demo extends Component {
  editor;

  constructor() {
    super();
    this.getValues = this.getValues.bind(this);
    this.state = {
      demoValue: '',
    };
  }

  componentDidMount() {
    const { demo } = this.props;
    this.setState({
      demoValue: demo,
    });
  }

  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.demo !== this.props.demo) {
      this.setState({
        demoValue: nextProps.demo,
      });
    }
    return null;
  }

  /**
   * 初始化代码编辑器
   * @param {obejct} editor 富文本编辑器
   */
  @Bind()
  handleCodeMirrorRef(editor) {
    this.editor = editor;
    editor.setSize('540px', '300px');
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
      demoValue: value,
    });
  }

  /**
   * 获取示例表单内容
   */
  @Bind()
  getValues() {
    const { form, demoType } = this.props;
    const { demoValue } = this.state;
    let formValues = {};
    form.validateFields((err, values) => {
      if (!err) {
        formValues = { ...values };
        if (demoType === 'Success') {
          formValues.respSuccessDemo = demoValue;
        } else {
          formValues.respFailedDemo = demoValue;
        }
      }
    });
    return formValues;
  }

  render() {
    const {
      form: { getFieldDecorator },
      status,
      mimeType,
      respContentTypes,
      demoType,
    } = this.props;
    const { demoValue } = this.state;
    return (
      <Form>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.httpcode').d('Http响应码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator(`resp${demoType}Status`, {
                initialValue: status,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem label="Content-Type" {...MODAL_FORM_ITEM_LAYOUT}>
              {getFieldDecorator(`resp${demoType}MimeType`, {
                initialValue: mimeType,
              })(
                <Select allowClear>
                  {respContentTypes.length &&
                    respContentTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col>
            <FormItem
              label={intl.get('hitf.services.model.services.demo.content').d('示例内容')}
              {...lastFormLayout}
            >
              <CodeMirror
                autoScroll
                className={style['hzero-codemirror']}
                value={demoValue}
                options={{
                  mode: 'javascript',
                  lineNumbers: true,
                }}
                editorDidMount={this.handleCodeMirrorRef}
                onBeforeChange={this.handleChangeValue}
              />
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
