/**
 * CreateForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-04
 * @copyright 2019-06-04 © HAND
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';
import uuid from 'uuid/v4';

import Lov from 'components/Lov';

import {
  EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
  ROW_HALF_WRITE_ONLY_CLASSNAME,
  ROW_LAST_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

import ProxyInput from '@/components/ProxyComponent/Input';
import ProxyButton from '@/components/ProxyComponent/Button';

import styles from './styles.less';

@Form.create({ fieldNameProp: null })
export default class CreateForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      context: [
        {
          id: uuid(),
          contextKey: '',
          contextType: '',
        },
      ],
    };
  }

  @Bind()
  handleChangeTenantId() {
    this.props.form.resetFields('templateCode');
  }

  @Bind()
  handleContextDel(id) {
    const { context } = this.state;
    this.setState({
      context: context.filter((record) => record.id !== id),
    });
  }

  @Bind()
  handleContextAdd() {
    const { context } = this.state;
    this.setState({
      context: [...context, { id: uuid(), contextKey: '', contextType: '' }],
    });
  }

  @Bind()
  handleContextTypeChange(id, e) {
    e.preventDefault();
    const { context } = this.state;
    this.setState({
      context: context.map((record) => {
        if (record.id === id) {
          return {
            ...record,
            contextType: e.target.value,
          };
        } else {
          return record;
        }
      }),
    });
  }

  @Bind()
  handleContextKeyChange(id, e) {
    e.preventDefault();
    const { context } = this.state;
    this.setState({
      context: context.map((record) => {
        if (record.id === id) {
          return {
            ...record,
            contextKey: e.target.value,
          };
        } else {
          return record;
        }
      }),
    });
  }

  @Bind()
  async getAsyncSubmitData() {
    const { form } = this.props;
    return new Promise((resolve, reject) => {
      form.validateFields((err, fieldsValue) => {
        if (!err) {
          const { context } = this.state;
          const filterContext = context.filter(
            ({ contextKey, contextType }) => contextKey && contextType
          );
          resolve({
            ...fieldsValue,
            context:
              filterContext.length === 0
                ? null
                : filterContext.map(({ contextKey, contextType }) => ({
                    contextKey,
                    contextType,
                  })),
          });
        } else {
          reject(err);
        }
      });
    });
  }

  render() {
    const { organizationId, languageMessage, form, isTenantRoleLevel } = this.props;
    const { context } = this.state;
    const { getFieldValue } = form;
    return (
      <Form className={EDIT_FORM_CLASSNAME}>
        <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_WRITE_ONLY_CLASSNAME}>
          {!isTenantRoleLevel && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT}
                label={languageMessage.model.textTraction.tenant}
              >
                {form.getFieldDecorator('tenantId', {
                  rules: [
                    {
                      required: true,
                      message: languageMessage.common.validation.notNull(
                        languageMessage.model.textTraction.tenant
                      ),
                    },
                  ],
                })(<Lov code="HPFM.TENANT" onChange={this.handleChangeTenantId} />)}
              </Form.Item>
            </Col>
          )}
          {isTenantRoleLevel && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT}
                label={languageMessage.model.textTraction.template}
              >
                {form.getFieldDecorator('templateCode', {
                  rules: [
                    {
                      required: true,
                      message: languageMessage.common.validation.notNull(
                        languageMessage.model.textTraction.template
                      ),
                    },
                  ],
                })(<Lov code="HNLP.TEMPLATE_CODE" queryParams={{ tenantId: organizationId }} />)}
              </Form.Item>
            </Col>
          )}
          {!isTenantRoleLevel && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT}
                label={languageMessage.model.textTraction.template}
              >
                {form.getFieldDecorator('templateCode', {
                  rules: [
                    {
                      required: true,
                      message: languageMessage.common.validation.notNull(
                        languageMessage.model.textTraction.template
                      ),
                    },
                  ],
                })(
                  <Lov
                    code="HNLP.TEMPLATE_CODE"
                    queryParams={{ tenantId: getFieldValue('tenantId') }}
                    disabled={isNil(getFieldValue('tenantId'))}
                  />
                )}
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_HALF_WRITE_ONLY_CLASSNAME}>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...EDIT_FORM_ITEM_LAYOUT_COL_2}
              label={languageMessage.model.textTraction.text}
            >
              {form.getFieldDecorator('text', {
                rules: [
                  {
                    required: true,
                    message: languageMessage.common.validation.notNull(
                      languageMessage.model.textTraction.text
                    ),
                  },
                ],
              })(<Input.TextArea />)}
            </Form.Item>
          </Col>
        </Row>
        {context.map(({ contextKey, contextType, id }, index) => (
          <Row
            {...EDIT_FORM_ROW_LAYOUT}
            className={`${ROW_WRITE_ONLY_CLASSNAME} ${ROW_LAST_CLASSNAME}`}
            key={id}
          >
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT}
                label={languageMessage.model.textTraction.contextType}
              >
                <ProxyInput
                  value={contextType}
                  onChange={this.handleContextTypeChange}
                  record={id}
                />
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...EDIT_FORM_ITEM_LAYOUT}
                label={languageMessage.model.textTraction.contextKey}
              >
                <ProxyInput value={contextKey} onChange={this.handleContextKeyChange} record={id} />
              </Form.Item>
            </Col>
            <Col
              {...FORM_COL_3_LAYOUT}
              className={`${styles['edit-form-btn-col']} ${SEARCH_COL_CLASSNAME}`}
            >
              <Form.Item>
                {!!index && (
                  <ProxyButton onClick={this.handleContextDel} record={id}>
                    {languageMessage.common.btn.del}
                  </ProxyButton>
                )}
                <Button onClick={this.handleContextAdd}>{languageMessage.common.btn.add}</Button>
              </Form.Item>
            </Col>
          </Row>
        ))}
      </Form>
    );
  }
}
