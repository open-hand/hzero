import React from 'react';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Form, Button, Select, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import 'codemirror/mode/clike/clike';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import CodeMirror from 'components/CodeMirror';

const { Option } = Select;
const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hsdr.jobInfo' })
@connect(({ loading, jobInfo }) => ({
  updating: loading.effects['jobInfo/updateJobGlue'],
  jobInfo,
}))
export default class Glue extends React.PureComponent {
  codeMirrorEditor;

  constructor(props) {
    super(props);
    this.state = {
      glueSource: '',
    };
  }

  componentDidMount() {
    this.queryJobInfo();
  }

  /**
   * @function queryJobInfo - 查询job详情
   */
  queryJobInfo() {
    const {
      dispatch,
      match: {
        params: { id },
      },
    } = this.props;
    dispatch({
      type: 'jobInfo/queryJobInfo',
      payload: { id },
    }).then(res => {
      if (res) {
        this.setState({ glueSource: res.glueSource });
        dispatch({
          type: 'jobInfo/queryJobGlueList',
          payload: { jobId: id },
        });
      }
    });
  }

  /**
   * @function queryJobGlueDetail - 查询glue详情
   */
  queryJobGlueDetail(params) {
    const {
      dispatch,
      match: {
        params: { id },
      },
      form,
    } = this.props;
    form.resetFields();
    dispatch({
      type: 'jobInfo/queryJobGlueDetail',
      payload: { id: params || id },
    }).then(res => {
      if (res) {
        this.setState({ glueSource: res.glueSource });
        dispatch({
          type: 'jobInfo/queryJobGlueList',
          payload: { jobId: id },
        });
      }
    });
  }

  /**
   * @function handleSave - 保存
   */
  @Bind()
  handleSave() {
    const {
      form,
      dispatch,
      jobInfo: { jobInfoDetail = {} },
      match: {
        params: { id },
      },
    } = this.props;
    const { glueRemark, glueType, glueTypeMeaning, jobId } = jobInfoDetail;
    let content = '';
    if (this.codeMirrorEditor) {
      content = this.codeMirrorEditor.getValue();
    }
    form.validateFields((err, fieldsValue) => {
      if (isEmpty(err)) {
        dispatch({
          type: 'jobInfo/updateJobGlue',
          payload: {
            glueRemark,
            glueSource: content,
            glueType,
            glueTypeMeaning,
            jobId: jobId || id,
            ...fieldsValue,
          },
        }).then(res => {
          if (res) {
            notification.success();
            this.queryJobInfo();
          }
        });
      }
    });
  }

  /**
   * @function setCodeMirror - 获取CodeMirror实例
   * @param {object} editor - CodeMirror实例
   */
  setCodeMirror(editor) {
    this.codeMirrorEditor = editor;
  }

  /**
   * 编辑代码后更新数据
   * @param {object} editor - 编辑器对象
   * @param {object} data - 数据对象
   * @param {string} value - 编辑后的代码
   */
  @Bind()
  codeChange(editor, data, value) {
    this.setState({ glueSource: value });
  }

  render() {
    const {
      form,
      jobInfo: { jobInfoDetail = {}, glueList = [] },
      updating,
    } = this.props;
    const { glueRemark, glueTypeMeaning, jobDesc } = jobInfoDetail;
    const { getFieldDecorator } = form;
    const { glueSource } = this.state;
    const codeMirrorProps = {
      value: glueSource,
      options: {
        mode: 'text/x-java',
      },
      onBeforeChange: this.codeChange,
    };
    return (
      <>
        <Header title="GLUE" backPath="/hsdr/job-info">
          <Select
            onChange={value => this.queryJobGlueDetail(value)}
            style={{ width: '200px' }}
            placeholder={intl.get('hsdr.jobInfo.model.jobInfo.version').d('版本回溯')}
          >
            {glueList.map(item => (
              <Option
                label={`${item.glueTypeMeaning}：${item.glueRemark}`}
                value={item.id}
                key={item.id}
              >
                {`${item.glueTypeMeaning}：${item.glueRemark}`}
              </Option>
            ))}
          </Select>
          <Button
            type="primary"
            icon="save"
            style={{ marginLeft: 10 }}
            loading={updating}
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content
          description={
            <>
              <span>
                {intl.get('hsdr.jobInfo.model.jobInfo.glueTypeMeaning').d('运行模式')}：
                {glueTypeMeaning}
              </span>
              <span style={{ marginLeft: '8px' }}>
                {intl.get('hsdr.jobInfo.model.jobInfo.jobDesc').d('任务描述')}：{jobDesc}
              </span>
            </>
          }
        >
          <Row>
            <Col span={12}>
              <p style={{ fontWeight: 'bold' }}>
                {intl.get('hsdr.jobInfo.model.jobInfo.operationMode').d('运行模式：')}
              </p>
              <CodeMirror
                codeMirrorProps={codeMirrorProps}
                fetchCodeMirror={editor => this.setCodeMirror(editor)}
              />
            </Col>
          </Row>
          <Form>
            <Row>
              <Col span={12}>
                <FormItem label={intl.get('hsdr.jobInfo.model.jobInfo.glueRemark').d('源码备注')}>
                  {getFieldDecorator('glueRemark', {
                    initialValue: glueRemark,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hsdr.jobInfo.model.jobInfo.glueRemark').d('源码备注'),
                        }),
                      },
                      {
                        max: 30,
                        message: intl
                          .get('hsdr.jobInfo.view.validation.glueRemark')
                          .d('源码备注长度在30个字符以内'),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Content>
      </>
    );
  }
}
