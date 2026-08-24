import React, { useState, useEffect } from 'react';
import { Col, Form, Input, Modal, Row, Select, Spin } from 'hzero-ui';
import 'codemirror/mode/clike/clike'; // java 样式
import { isNil } from 'lodash';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import CodeMirror from 'components/CodeMirror';

import intl from 'utils/intl';
import {
  MODAL_FORM_ITEM_LAYOUT,
  EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT_COL_3,
} from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';

function Editor(props) {
  const javaStr =
    'package org.hzero.demo.handler;\r\n\r\nimport io.choerodon.core.convertor.ApplicationContextHelper;\r\nimport org.hzero.starter.api.customize.commons.handler.ICustomizeHandler;\r\n\r\npublic class DemoHandler implements ICustomizeHandler {\r\n\r\n    @Override  \r\n    public Object execute(Object ... args) {    \r\n        System.out.println("--------------------------------- customize begin ---------------------------------");\r\n        if (args != null) {\r\n            for (Object arg : args) {\r\n                System.out.println("println arg = " + arg);\r\n            }\r\n        }\r\n        System.out.println("--------------------------------- customize end ---------------------------------");\r\n        return null;\r\n    }\r\n}';

  const [typeValues, setTypeValues] = useState(undefined);
  const [flag, setFlag] = useState(true);

  const setFlags = () => {
    if (!flag) {
      setTypeValues(undefined);
      setFlag(true);
    }
  };

  useEffect(() => {
    setFlags();
  });
  const {
    form,
    initData = {},
    title = '',
    isSiteFlag,
    visible = false,
    loading = false,
    initLoading = false,
    rulePositionList = [],
    typeCodeList = [],
    onCancel = (e) => e,
    onOk = (e) => e,
  } = props;
  const { getFieldDecorator } = form;
  const {
    ruleCode,
    ruleName,
    tenantId,
    tenantName,
    typeValue,
    description,
    rulePosition,
    typeCode,
    syncFlag = 1,
    enabledFlag = 1,
  } = initData;

  const handleOk = () => {
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({ ...initData, ...fieldsValue });
        setFlag(false);
      }
    });
  };

  const handleCancel = () => {
    onCancel();
    setTypeValues(undefined);
  };

  const handleChange = (value) => {
    if (!ruleCode && value === 'JAVA') {
      setTypeValues(javaStr);
      form.setFieldsValue({ typeValue: javaStr });
    } else if (!ruleCode) {
      setTypeValues('');
      form.setFieldsValue({ typeValue: '' });
    }
  };

  const handleCodeChange = (editor, data, value) => {
    if (flag) {
      setTypeValues(value);
      form.setFieldsValue({ typeValue: value });
    }
  };

  const codeMirrorProps = {
    value: isNil(typeValues) ? typeValue : typeValues,
    options: {
      mode: 'text/x-java',
    },
    onChange: handleCodeChange,
  };

  return (
    <Modal
      destroyOnClose
      wrapClassName="ant-modal-sidebar-right"
      transitionName="move-right"
      title={title}
      width="720px"
      visible={visible}
      confirmLoading={loading}
      onCancel={handleCancel}
      onOk={handleOk}
    >
      <Spin spinning={initLoading}>
        <Form className={EDIT_FORM_CLASSNAME}>
          <Row>
            {isSiteFlag && (
              <Col span={12}>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hzero.common.model.tenantName').d('租户')}
                  style={{ marginBottom: '12px' }}
                >
                  {getFieldDecorator('tenantId', {
                    initialValue: tenantId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hzero.common.model.tenantName').d('租户'),
                        }),
                      },
                    ],
                  })(<Lov code="HPFM.TENANT" textValue={tenantName} />)}
                </Form.Item>
              </Col>
            )}
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.customize.model.customize.rule.ruleCode').d('规则编码')}
              >
                {getFieldDecorator('ruleCode', {
                  initialValue: ruleCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.customize.model.customize.rule.ruleCode')
                          .d('规则编码'),
                      }),
                    },
                    {
                      max: 30,
                      message: intl.get('hzero.common.validation.max', {
                        max: 30,
                      }),
                    },
                    {
                      pattern: CODE_UPPER,
                      message: intl
                        .get('hzero.common.validation.codeUpper')
                        .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                  ],
                })(<Input trim typeCase="upper" inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.customize.model.customize.rule.ruleName').d('规则名称')}
              >
                {getFieldDecorator('ruleName', {
                  initialValue: ruleName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.customize.model.customize.rule.ruleName')
                          .d('规则名称'),
                      }),
                    },
                    {
                      max: 120,
                      message: intl.get('hzero.common.validation.max', {
                        max: 120,
                      }),
                    },
                  ],
                })(<Input trim />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hpfm.customize.model.customize.rule.rulePositionMeaning')
                  .d('位置')}
              >
                {getFieldDecorator('rulePosition', {
                  initialValue: rulePosition,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.customize.model.customize.rule.rulePositionMeaning')
                          .d('位置'),
                      }),
                    },
                  ],
                })(
                  <Select>
                    {rulePositionList.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.customize.model.customize.rule.typeCode').d('类别')}
              >
                {getFieldDecorator('typeCode', {
                  initialValue: typeCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.customize.model.customize.rule.typeCode').d('类别'),
                      }),
                    },
                  ],
                })(
                  <Select onChange={handleChange}>
                    {typeCodeList.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.customize.model.customize.rule.description').d('描述')}
              >
                {getFieldDecorator('description', {
                  initialValue: description,
                  rules: [
                    {
                      max: 480,
                      message: intl.get('hzero.common.validation.max', {
                        max: 480,
                      }),
                    },
                  ],
                })(<Input trim />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.customize.model.customize.rule.syncFlag').d('同步')}
              >
                {getFieldDecorator('syncFlag', {
                  initialValue: syncFlag,
                })(<Switch />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hzero.common.status.enable').d('启用')}
              >
                {getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag,
                })(<Switch />)}
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                label={intl.get('hpfm.customize.model.customize.point.typeValue').d('代码')}
              >
                {getFieldDecorator('typeValue', {
                  initialValue: typeValue,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.customize.model.customize.point.typeValue').d('代码'),
                      }),
                    },
                  ],
                })(<CodeMirror codeMirrorProps={codeMirrorProps} />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Spin>
    </Modal>
  );
}

export default Form.create({ fieldNameProp: null })(Editor);
