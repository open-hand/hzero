/**
 *节点详情
 * @author baitao.huang@hand-china.com
 * @date 2020/5/27
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Card } from 'choerodon-ui';
import {
  Form,
  TextField,
  CheckBox,
  DataSet,
  Icon,
  Select,
  UrlField,
  Switch,
  Table,
  Button,
  CodeArea,
  SelectBox,
  Modal,
  NumberField,
} from 'choerodon-ui/pro';
import { withPropsAPI } from 'gg-editor';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import { operatorRender } from 'utils/renderer';
import { Button as ButtonPermission } from 'components/Permission';
import Upload from 'components/Upload/UploadButton';
import LinkUpload from 'components/Upload';
import { BKT_HORC } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { BODY_METHOD, SUBJECT, CONTENT_TYPE, REQUEST_BODY_TYPE } from '@/constants/constants';
import QuestionPopover from '@/components/QuestionPopover';
import {
  requestSettingFormDS,
  requestHeaderDS,
  requestQueryDS,
  requestBodyDS,
  assertionDS,
} from '@/stores/Orchestration/orchestrationDS';

class HttpDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.requestSettingFormDS = new DataSet(requestSettingFormDS());
    this.requestHeaderDS = new DataSet(requestHeaderDS());
    this.requestBodyDS = new DataSet(requestBodyDS());
    this.requestQueryDS = new DataSet(requestQueryDS());
    this.assertionDS = new DataSet({
      ...assertionDS({
        onAddAssertionFormItem: this.addAssertionFormItem,
        onRemoveAssertionFormItem: this.removeAssertionFormItem,
        onFiledUpdate: this.handleFieldUpdate,
      }),
    });
    props.onRef(this);
    this.state = {
      requestBodyType: 'Form',
      httpMethod: 'GET',
      formItems: [],
      contentType: null,
      tenantId: getCurrentOrganizationId(),
    };
  }

  componentDidMount() {
    if (!isEmpty(this.nodeItem.model)) {
      this.init();
    } else {
      this.requestSettingFormDS.create();
    }
  }

  /**
   * 数据加载
   */
  init() {
    const { params } = this.nodeItem.model;
    const { httpMethod, fileBody, httpBody, ...otherParams } = params;
    let { httpParams, assertions } = params;
    // 此步骤为了把null置为【】
    httpParams = httpParams || [];
    assertions = assertions || [];
    const formData = {
      ...otherParams,
      httpMethod,
      fileBody,
      httpBody,
    };
    const headerData = httpParams.filter((param) => param.httpParameterType === 'HEADER');
    const bodyData = httpParams.filter((param) => param.httpParameterType.indexOf('BODY') > -1);
    const queryData = httpParams.filter((param) => param.httpParameterType === 'QUERY');
    this.requestSettingFormDS.create(formData);
    headerData.forEach((data) => this.requestHeaderDS.create(data));
    bodyData.forEach((data) => this.requestBodyDS.create(data));
    queryData.forEach((data) => this.requestQueryDS.create(data));
    assertions.forEach((assertion) => {
      const record = this.assertionDS.create(assertion);
      record.setState('subject', record.get('subject'));
    });
    const contentTypeRecord = headerData.find((item) => item.key === 'Content-Type');
    const contentType = isUndefined(contentTypeRecord) ? null : contentTypeRecord.value;
    this.setState({
      httpMethod,
      contentType,
      requestBodyType:
        // eslint-disable-next-line no-nested-ternary
        !isEmpty(fileBody)
          ? 'File'
          : !isEmpty(httpBody)
          ? 'Text'
          : (CONTENT_TYPE.find((item) => item.value === contentType) || {}).type || 'Form',
    });
  }

  /**
   * 获取当前节点
   */
  get nodeItem() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0] || {};
  }

  /**
   * 节点信息滑窗信息保存
   */
  @Bind()
  async handleOk() {
    const settingValidate = await this.requestSettingFormDS.validate();
    const headerValidate = await this.requestHeaderDS.validate();
    const queryValidate = await this.requestQueryDS.validate();
    const bodyValidate = await this.requestBodyDS.validate();
    const assertionValidate = await this.assertionDS.validate();

    if (
      !settingValidate ||
      !headerValidate ||
      !bodyValidate ||
      !assertionValidate ||
      !queryValidate
    ) {
      return undefined;
    }
    const data = this.requestSettingFormDS.current.toData();
    const headerData = this.requestHeaderDS.toData();
    const bodyData = this.requestBodyDS.toData();
    const queryData = this.requestQueryDS.toData();
    const assertionData = this.assertionDS.toData();
    const formatedData = this.formatNodeData(data, headerData, queryData, bodyData, assertionData);
    return formatedData;
  }

  /**
   * 格式化节点数据格式
   */
  formatNodeData(
    nodeData = {},
    headerData = [],
    queryData = [],
    bodyData = [],
    assertionData = []
  ) {
    const {
      url,
      httpMethod,
      httpBody,
      fileBody,
      httpRequestCharset,
      httpResponseCharset,
      readTimeout,
      connectionTimeout,
      enableResultPropagation,
      ...other
    } = nodeData;
    const params = {
      url,
      httpMethod,
      httpBody,
      fileBody,
      httpRequestCharset,
      httpResponseCharset,
      readTimeout,
      connectionTimeout,
      enableResultPropagation,
      httpParams: [...headerData, ...queryData, ...bodyData],
      assertions: assertionData,
    };
    return {
      ...other,
      params,
    };
  }

  /**
   * 设置请求体类型
   */
  @Bind()
  async handleSetRequestBodyType(value) {
    const modified = await this.clearDS();
    if (modified) {
      this.setState({ requestBodyType: value });
      this.handleRemoveContentType();
    }
  }

  /**
   * 设置请求头中的
   */
  @Bind()
  handleRemoveContentType() {
    const record = this.requestHeaderDS.records.find((item) => item.get('key') === 'Content-Type');
    if (!isUndefined(record)) {
      this.requestHeaderDS.remove(record);
      this.setState({ contentType: null });
    }
  }

  /**
   * 清空DS
   */
  async clearDS() {
    let modified = true;
    if (
      !isEmpty(this.requestBodyDS.data) ||
      (this.requestSettingFormDS.current &&
        (!isEmpty(this.requestSettingFormDS.current.get('httpBody')) ||
          !isEmpty(this.requestSettingFormDS.current.get('fileBody'))))
    ) {
      const res = await Modal.confirm({
        children: <p>{ORCHESTRATION_LANG.CLEAR_HTTP_BODY_CONFIRM}</p>,
      });
      if (res === 'ok') {
        this.requestBodyDS.reset();
        if (this.requestSettingFormDS.current) {
          this.requestSettingFormDS.current.init('httpBody', undefined);
          this.requestSettingFormDS.current.init('fileBody', undefined);
        }
      } else {
        modified = false;
      }
    }
    return modified;
  }

  /**
   * 设置请求头中的
   */
  @Bind()
  handleSetContentTypeForRequestHeader(value) {
    const record = this.requestHeaderDS.records.find((item) => item.get('key') === 'Content-Type');
    if (isUndefined(record)) {
      this.requestHeaderDS.create({
        key: 'Content-Type',
        value,
      });
    } else {
      record.set('value', value);
    }
  }

  /**
   * 渲染断言表单
   */
  @Bind()
  renderAssertionForm(dataSet = []) {
    let formItems = [];
    dataSet.forEach((record) => {
      formItems = this.renderFormItem(formItems, record);
    });
    this.setState({ formItems });
  }

  /**
   * 添加断言子表单
   */
  @Bind()
  addAssertionFormItem(record = {}) {
    let { formItems } = this.state;
    formItems = this.renderFormItem(formItems, record);
    this.setState({ formItems });
  }

  /**
   * 添加单行formItem
   */
  renderFormItem(items, record) {
    const { operatorList, assertionSubjects } = this.props;
    const formItems = items;
    formItems.push(
      <Select
        newLine
        required
        colSpan={5}
        label={ORCHESTRATION_LANG.SUBJECT}
        value={record.get('subject')}
        onChange={(value) => record.set('subject', value)}
      >
        {assertionSubjects.map((item) => (
          <Select.Option value={item.value} key={item.value}>
            {item.meaning}
          </Select.Option>
        ))}
      </Select>
    );
    if (SUBJECT.includes(record.getState('subject')) || SUBJECT.includes(record.get('subject'))) {
      formItems.push(
        <TextField
          colSpan={6}
          label={ORCHESTRATION_LANG.FIELD}
          value={record.get('field')}
          onChange={(value) => record.set('field', value)}
        />
      );
    }
    formItems.push(
      <Select
        required
        colSpan={6}
        label={ORCHESTRATION_LANG.CONDITION}
        value={record.get('condition')}
        onChange={(value) => record.set('condition', value)}
      >
        {operatorList.map((item) => (
          <Select.Option value={item.value} key={item.value}>
            {item.meaning}
          </Select.Option>
        ))}
      </Select>
    );
    formItems.push(
      <TextField
        required
        colSpan={5}
        label={ORCHESTRATION_LANG.EXPECTATION}
        value={record.get('expectation')}
        onChange={(value) => record.set('expectation', value)}
      />
    );
    formItems.push(
      <Button
        funcType="flat"
        icon="delete_forever"
        onClick={() => this.assertionDS.delete(record)}
      />
    );

    if (record.getState('subject') === 'JSON_BODY' || record.get('subject') === 'JSON_BODY') {
      formItems.push(
        <QuestionPopover
          message={
            <>
              {ORCHESTRATION_LANG.JSON_BODY_TIP}
              <a
                href="https://help.talend.com/access/sources/content/topic?pageid=tester_json_path&amp;EnrichVersion=Cloud&amp;afs:lang=en"
                // eslint-disable-next-line react/jsx-no-target-blank
                target="_blank"
              >
                JSON Path
              </a>
            </>
          }
        />
      );
    }
    if (record.getState('subject') === 'XML_BODY' || record.get('subject') === 'XML_BODY') {
      formItems.push(
        <QuestionPopover
          message={
            <>
              {ORCHESTRATION_LANG.XML_BODY_TIP}
              <a
                href="https://help.talend.com/access/sources/content/topic?pageid=tester_xpath&amp;EnrichVersion=Cloud&amp;afs:lang=en"
                // eslint-disable-next-line react/jsx-no-target-blank
                target="_blank"
              >
                XPath 1.0
              </a>
            </>
          }
        />
      );
    }
    return formItems;
  }

  /**
   * 断言主题变更时触发
   */
  @Bind()
  handleFieldUpdate(dataSet, record, name, value) {
    if (name === 'subject') {
      record.setState('subject', value);
      this.renderAssertionForm(dataSet);
    }
  }

  /**
   * 删除单行断言
   */
  @Bind()
  removeAssertionFormItem(dataSet) {
    this.renderAssertionForm(dataSet);
  }

  get requestHeaderButtons() {
    const { disabledFlag } = this.props;
    const buttons = disabledFlag
      ? []
      : [
          <Button
            key="addRequestHeader"
            funcType="flat"
            icon="add"
            color="primary"
            onClick={() => this.requestHeaderDS.create()}
          >
            {ORCHESTRATION_LANG.ADD_REQUEST_HEADER}
          </Button>,
          <Button
            key="clearRequestHeader"
            funcType="flat"
            icon="clear_all"
            color="default"
            onClick={() => this.requestHeaderDS.deleteAll()}
          >
            {ORCHESTRATION_LANG.CLEAR_REQUEST_HEADER}
          </Button>,
        ];
    return buttons;
  }

  get requestHeaderColumns() {
    const { path, disabledFlag } = this.props;
    const editable = !disabledFlag;
    const columns = [
      {
        name: 'key',
        width: 180,
        editor: editable && <TextField />,
      },
      {
        name: 'value',
        editor: editable && <TextField />,
      },
      {
        name: 'exprEnabled',
        align: 'center',
        width: 100,
        editor: editable && <CheckBox />,
      },
      {
        header: ORCHESTRATION_LANG.OPERATOR,
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.requestHeader.delete`,
                      type: 'button',
                      meaning: '请求头-删除',
                    },
                  ]}
                  disabled={disabledFlag}
                  onClick={() => this.requestHeaderDS.delete(record)}
                >
                  {ORCHESTRATION_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: ORCHESTRATION_LANG.DELETE,
            },
          ];
          return operatorRender(actions);
        },
      },
    ];
    return columns;
  }

  get requestQueryButtons() {
    const { disabledFlag } = this.props;
    const buttons = disabledFlag
      ? []
      : [
          <Button
            key="addRequestQuery"
            funcType="flat"
            icon="add"
            color="primary"
            onClick={() => this.requestQueryDS.create()}
          >
            {ORCHESTRATION_LANG.ADD_PARAM}
          </Button>,
          <Button
            key="clearRequestQuery"
            funcType="flat"
            icon="clear_all"
            color="default"
            onClick={() => this.requestQueryDS.deleteAll()}
          >
            {ORCHESTRATION_LANG.CLEAR_PARAM}
          </Button>,
        ];
    return buttons;
  }

  get requestQueryColumns() {
    const { path, disabledFlag } = this.props;
    const editable = !disabledFlag;
    const columns = [
      {
        name: 'key',
        width: 180,
        editor: editable && <TextField />,
      },
      {
        name: 'value',
        editor: editable && <TextField />,
      },
      {
        name: 'exprEnabled',
        align: 'center',
        width: 100,
        editor: editable && <CheckBox />,
      },
      {
        header: ORCHESTRATION_LANG.OPERATOR,
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.requestQuery.delete`,
                      type: 'button',
                      meaning: '请求参数-删除',
                    },
                  ]}
                  disabled={disabledFlag}
                  onClick={() => this.requestQueryDS.delete(record)}
                >
                  {ORCHESTRATION_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: ORCHESTRATION_LANG.DELETE,
            },
          ];
          return operatorRender(actions);
        },
      },
    ];
    return columns;
  }

  get requestBodyButtons() {
    const { disabledFlag } = this.props;
    const buttons = disabledFlag
      ? []
      : [
          <Button
            key="addRequestBody"
            funcType="flat"
            icon="add"
            color="primary"
            onClick={() => this.requestBodyDS.create()}
          >
            {ORCHESTRATION_LANG.ADD_PARAM}
          </Button>,
          <Button
            key="clearRequestBody"
            funcType="flat"
            icon="clear_all"
            color="default"
            onClick={() => this.requestBodyDS.deleteAll()}
          >
            {ORCHESTRATION_LANG.CLEAR_PARAM}
          </Button>,
        ];
    return buttons;
  }

  get requestBodyColumns() {
    const { path, disabledFlag } = this.props;
    const { tenantId } = this.state;
    const editable = !disabledFlag;
    const columns = [
      {
        name: 'key',
        width: 180,
        editor: editable && <TextField />,
      },
      {
        name: 'httpParameterType',
        editor: (record) =>
          editable && (
            <Select
              optionsFilter={(optionRecord) => optionRecord.get('tag') === 'BODY'}
              onChange={() => {
                record.init('exprEnabled', false);
                record.init('value', null);
              }}
            />
          ),
      },
      {
        name: 'value',
        editor: (record) =>
          editable && record.get('httpParameterType') !== 'BODY_FILE' && <TextField />,
        renderer: ({ record, value }) =>
          record.get('httpParameterType') === 'BODY_FILE' ? (
            <LinkUpload
              single
              bucketName={BKT_HORC}
              attachmentUUID={value}
              tenantId={tenantId}
              bucketDirectory="horc01"
              btnText={ORCHESTRATION_LANG.UPLOAD_FILE}
              removeCallback={() => this.onLinkFileRemove(record)}
              uploadSuccess={() => this.onLinkUploadSuccess(record)}
              afterOpenUploadModal={(attachmentUuid) => this.setState({ attachmentUuid })}
            />
          ) : (
            value
          ),
      },
      {
        name: 'exprEnabled',
        align: 'center',
        width: 100,
        editor: (record) =>
          record.get('httpParameterType') !== 'BODY_FILE' && editable && <CheckBox />,
      },
      {
        header: ORCHESTRATION_LANG.OPERATOR,
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.requestBody.delete`,
                      type: 'button',
                      meaning: '请求体-删除',
                    },
                  ]}
                  disabled={disabledFlag}
                  onClick={() => this.requestBodyDS.delete(record)}
                >
                  {ORCHESTRATION_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: ORCHESTRATION_LANG.DELETE,
            },
          ];
          return operatorRender(actions);
        },
      },
    ];
    return columns;
  }

  /**
   * 请求方法变更
   */
  @Bind()
  async handleHttpMethodChange(value, oldValue) {
    this.setState({ httpMethod: value });
    if (this.requestBodyDS.length > 0) {
      const confirm = await Modal.confirm({
        children: <p>{ORCHESTRATION_LANG.HTTP_METHOD_CHANGE_CONFIRM}</p>,
      });
      if (confirm === 'ok') {
        this.requestBodyDS.removeAll();
      } else {
        this.requestSettingFormDS.current.set('httpMethod', oldValue);
        this.setState({ httpMethod: oldValue });
      }
    }
  }

  // 上传图片成功
  @Bind()
  onLinkUploadSuccess(record) {
    const { attachmentUuid } = this.state;
    record.set('value', attachmentUuid);
  }

  @Bind()
  onLinkFileRemove(record) {
    record.init('value', null);
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(fileInfo) {
    const { response } = fileInfo;
    this.requestSettingFormDS.current.set('fileBody', response);
  }

  @Bind()
  onFileRemove() {
    this.requestSettingFormDS.current.init('fileBody', null);
  }

  render() {
    const { disabledFlag } = this.props;
    const { requestBodyType, httpMethod, formItems, contentType } = this.state;
    return (
      <>
        <Card
          style={{ marginTop: '3px' }}
          title={
            <h3>
              {
                <QuestionPopover
                  text={ORCHESTRATION_LANG.REQUEST_SETTING}
                  message={ORCHESTRATION_LANG.REQUEST_SETTING_TIP}
                />
              }
            </h3>
          }
        >
          <Form dataSet={this.requestSettingFormDS} columns={2} disabled={disabledFlag}>
            <Select
              newLine
              name="httpMethod"
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.REQUEST_METHOD}
                  message={ORCHESTRATION_LANG.REQUEST_METHOD_TIP}
                />
              }
              colSpan={2}
              onChange={this.handleHttpMethodChange}
            />
            <UrlField name="url" colSpan={2} />
            <Select
              name="httpRequestCharset"
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.REQUEST_CHARSET}
                  message={ORCHESTRATION_LANG.CHARSET_TIP}
                />
              }
            />
            <Select
              name="httpResponseCharset"
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.RESPONSE_CHARSET}
                  message={ORCHESTRATION_LANG.CHARSET_TIP}
                />
              }
            />
            <NumberField
              name="readTimeout"
              min={0}
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.READ_TIMEOUT}
                  message={ORCHESTRATION_LANG.READ_TIMEOUT_TIP}
                />
              }
              addonAfter={ORCHESTRATION_LANG.MILLI}
              addonBefore={<Icon type="av_timer" />}
            />
            <NumberField
              name="connectionTimeout"
              min={0}
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.CONNECTION_TIMEOUT}
                  message={ORCHESTRATION_LANG.CONNECTION_TIMEOUT_TIP}
                />
              }
              addonAfter={ORCHESTRATION_LANG.MILLI}
              addonBefore={<Icon type="av_timer" />}
            />
            <Switch
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.ENABLE_RESULT_PROPAGATION}
                  message={ORCHESTRATION_LANG.ENABLE_RESULT_PROPAGATION_TIP}
                />
              }
              name="enableResultPropagation"
            />
          </Form>
        </Card>
        <Card
          style={{ marginTop: '3px' }}
          title={
            <h3>
              {
                <QuestionPopover
                  text={ORCHESTRATION_LANG.REQUEST_QUERY}
                  message={ORCHESTRATION_LANG.REQUEST_QUERY_TIP}
                />
              }
            </h3>
          }
        >
          <Table
            key="requestQuery"
            buttons={this.requestQueryButtons}
            dataSet={this.requestQueryDS}
            columns={this.requestQueryColumns}
          />
        </Card>
        <Card
          style={{ marginTop: '3px' }}
          title={
            <h3>
              {
                <QuestionPopover
                  text={ORCHESTRATION_LANG.REQUEST_HEADER}
                  message={ORCHESTRATION_LANG.REQUEST_HEADER_TIP}
                />
              }
            </h3>
          }
        >
          <Table
            key="requestHeader"
            buttons={this.requestHeaderButtons}
            dataSet={this.requestHeaderDS}
            columns={this.requestHeaderColumns}
          />
        </Card>
        {BODY_METHOD.includes(httpMethod) && (
          <Card
            style={{ marginTop: '3px' }}
            title={
              <h3>
                {
                  <QuestionPopover
                    text={ORCHESTRATION_LANG.REQUEST_BODY}
                    message={ORCHESTRATION_LANG.REQUEST_BODY_TIP}
                  />
                }
              </h3>
            }
            extra={
              <Select
                defaultValue="Form"
                clearButton={false}
                value={requestBodyType}
                disabled={disabledFlag}
                onChange={this.handleSetRequestBodyType}
              >
                {REQUEST_BODY_TYPE.map((item) => (
                  <Select.Option value={item.value} key={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            }
          >
            {requestBodyType === 'Form' && (
              <>
                <Table
                  key="requestBody"
                  buttons={this.requestBodyButtons}
                  dataSet={this.requestBodyDS}
                  columns={this.requestBodyColumns}
                />
                <SelectBox
                  style={{ marginTop: '5px' }}
                  mode="button"
                  value={contentType}
                  disabled={disabledFlag}
                  onChange={this.handleSetContentTypeForRequestHeader}
                >
                  {CONTENT_TYPE.filter((item) => item.type === 'Form').map((item) => (
                    <SelectBox.Option value={item.value}>{item.meaning}</SelectBox.Option>
                  ))}
                </SelectBox>
              </>
            )}
            {requestBodyType === 'Text' && (
              <>
                <CodeArea
                  dataSet={this.requestSettingFormDS}
                  name="httpBody"
                  style={{ height: '300px' }}
                />
                <SelectBox
                  mode="button"
                  value={contentType}
                  disabled={disabledFlag}
                  onChange={this.handleSetContentTypeForRequestHeader}
                >
                  {CONTENT_TYPE.filter((item) => item.type === 'Text').map((item) => (
                    <SelectBox.Option value={item.value}>{item.meaning}</SelectBox.Option>
                  ))}
                </SelectBox>
              </>
            )}
            {requestBodyType === 'File' && (
              <Upload
                single
                // fileSize={30 * 1024 * 1024}
                bucketName={BKT_HORC}
                // accept="application/zip"
                bucketDirectory="horc01"
                fileList={[
                  {
                    uid: '-1',
                    name:
                      this.requestSettingFormDS.current &&
                      this.requestSettingFormDS.current.get('fileBody'),
                    status: 'done',
                    url:
                      this.requestSettingFormDS.current &&
                      this.requestSettingFormDS.current.get('fileBody'),
                  },
                ]}
                text={ORCHESTRATION_LANG.UPLOAD_FILE}
                onRemove={this.onFileRemove}
                onUploadSuccess={this.onUploadSuccess}
              />
            )}
          </Card>
        )}
        <Card
          style={{ marginTop: '3px' }}
          title={
            <h3>
              {
                <QuestionPopover
                  text={ORCHESTRATION_LANG.ASSERTION}
                  message={ORCHESTRATION_LANG.ASSERTION_TIP}
                />
              }
            </h3>
          }
        >
          {!disabledFlag && (
            <div style={{ marginBottom: '5px' }}>
              <Button
                key="addAssertion"
                funcType="flat"
                icon="add"
                color="primary"
                onClick={() => this.assertionDS.create()}
              >
                {ORCHESTRATION_LANG.ADD_ASSERTION}
              </Button>
              <Button
                key="clearAssertion"
                funcType="flat"
                icon="clear_all"
                color="default"
                onClick={() => this.assertionDS.deleteAll()}
              >
                {ORCHESTRATION_LANG.CLEAR_ASSERTION}
              </Button>
            </div>
          )}
          <Form
            dataSet={this.assertionDS}
            columns={24}
            labelLayout="placeholder"
            useColon={false}
            disabled={disabledFlag}
          >
            {formItems}
          </Form>
        </Card>
      </>
    );
  }
}
export default withPropsAPI(HttpDrawer);
