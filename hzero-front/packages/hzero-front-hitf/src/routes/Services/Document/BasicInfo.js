/**
 * BasicInfo - 基本接口信息
 * @date: 2019/5/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Col, Form, Input, Row, Button } from 'hzero-ui';
import { Tooltip } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

/**
 * 基本接口信息
 * @extends {Component} - React.Component
 * @reactProps {object} currentInterface - 当前接口数据
 * @reactProps {object} currentInterfaceType - 接口类型
 * @reactProps {string} documentName - 文档名称
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class BasicInfo extends Component {
  constructor() {
    super();
    this.getValues = this.getValues.bind(this);
  }

  /**
   * 获取表单值
   */
  @Bind()
  getValues() {
    const { form } = this.props;
    let name = false;
    form.validateFields((err, { documentName }) => {
      if (!err) {
        name = documentName;
      }
    });
    return name;
  }

  @Bind()
  handleRecognize() {
    const { onRecognize = (e) => e } = this.props;
    onRecognize();
  }

  render() {
    const {
      form: { getFieldDecorator },
      currentInterface,
      documentName,
      currentInterfaceType,
      recognizeParamLoading,
    } = this.props;
    const bthLayout = {
      labelCol: {
        span: 6,
      },
      wrapperCol: {
        span: 16,
        offset: 6,
      },
    };
    return (
      <Form>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.documentName').d('文档名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('documentName', {
                initialValue: documentName,
                rules: [
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.interfaceCode').d('接口编码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('interfaceCode', {
                initialValue: currentInterface.interfaceCode,
              })(<Input disabled />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.interfaceName').d('接口名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('interfaceName', {
                initialValue: currentInterface.interfaceName,
              })(<Input disabled />)}
            </FormItem>
          </Col>
          {currentInterfaceType !== 'COMPOSITE' && (
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem label="URI" {...MODAL_FORM_ITEM_LAYOUT}>
                {getFieldDecorator('interfaceUrl', {
                  initialValue: currentInterface.interfaceUrl,
                })(<Input disabled />)}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get('hitf.services.model.services.requestMethod').d('请求方式')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('requestMethod', {
                initialValue: currentInterface.requestMethod,
              })(<Input disabled />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem {...bthLayout} label="">
              <Tooltip
                title={intl.get('hitf.services.view.message.tip.recognize').d('拉取swagger的参数')}
                placement="top"
              >
                <Button
                  onClick={this.handleRecognize}
                  loading={recognizeParamLoading}
                  help={intl.get('hitf.services.view.message.tip.recognize').d('拉取swagger的参数')}
                  showHelp="tooltip"
                >
                  {intl.get('hitf.services.view.button.recognize').d('参数识别')}
                </Button>
              </Tooltip>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
