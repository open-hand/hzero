/**
 * 字段映射
 * @author baitao.huang@hand-china.com
 * @date 2020/6/10
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { DataSet, Modal, CodeArea, Form, Select, Button } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { withPropsAPI } from 'gg-editor';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import ReactFileReader from 'react-file-reader';
import FieldMapping from '@/components/FieldMapping';
import {
  jsonParse,
  transformLine,
  getSameLineRel,
  getSameNameRel,
  getRelationScript,
  insertScriptHeader,
} from '@/components/FieldMapping/util';
import {
  fieldMappingFormDS,
  fieldDataModalDS,
  fieldDataDrawerDS,
  fieldMappingDrawerDS,
} from '@/stores/Orchestration/orchestrationDS';
import FIELD_MAPPING_LANG from '@/langs/fieldMappingLang';
import QuestionPopover from '@/components/QuestionPopover';

class FieldMappingDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      relation: [],
      sourceData: [],
      targetData: [],
      modalType: 'source',
    };
    this.fieldMappingFormDS = new DataSet(fieldMappingFormDS());
    this.fieldDataModalDS = new DataSet(fieldDataModalDS());
    this.fieldDataDrawerDS = new DataSet(fieldDataDrawerDS());
    this.fieldMappingDrawerDS = new DataSet(fieldMappingDrawerDS());
    props.onRef(this);
  }

  componentDidMount() {
    if (!isEmpty(this.nodeItem.model)) {
      this.init();
    } else {
      this.fieldMappingFormDS.create();
      this.fieldMappingDrawerDS.create();
    }
  }

  /**
   * 数据加载
   */
  init() {
    const { params } = this.nodeItem.model;
    const { type, ...others } = params;
    this.fieldMappingFormDS.create({ type });
    this.fieldMappingDrawerDS.create(others);
    const { sourceStructure, targetStructure, script } = params;
    this.setState(
      {
        script,
        sourceInputData: sourceStructure,
        targetInputData: targetStructure,
        sourceData: !isEmpty(sourceStructure)
          ? jsonParse({ payload: JSON.parse(sourceStructure) })
          : [],
        targetData: !isEmpty(targetStructure)
          ? jsonParse({ TARGET: JSON.parse(targetStructure) })
          : [],
      },
      () => {
        const { sourceData = [], targetData = [] } = this.state;
        const relation = transformLine(
          script,
          (sourceData[0] || {}).children,
          (targetData[0] || {}).children
        );
        this.setState({ relation });
      }
    );
  }

  /**
   * 获取当前节点
   */
  get nodeItem() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0] || {};
  }

  /**
   * 字段映射滑窗确认
   */
  @Bind()
  async handleOk() {
    const validate = await this.fieldMappingFormDS.validate();
    if (!validate) {
      return undefined;
    }
    const headerData = this.fieldMappingFormDS.current.toData();
    const data = this.fieldMappingDrawerDS.current.toData();
    const updateData = {
      params: {
        ...headerData,
        ...data,
      },
    };
    return updateData;
  }

  /**
   * 来源数据、目标数据弹窗
   */
  @Bind()
  handleOpenDataDrawer(modalType) {
    const inputData = modalType === 'source' ? 'sourceInputData' : 'targetInputData';
    const { sourceInputData, targetInputData } = this.state;
    const data = modalType === 'source' ? sourceInputData : targetInputData;
    if (isEmpty(this.fieldDataDrawerDS)) {
      this.fieldDataDrawerDS.create({ inputData: data });
    } else {
      this.fieldDataDrawerDS.current.init('inputData', data);
    }
    this.setState({ modalType });
    this.dataDrawer = Modal.open({
      title: FIELD_MAPPING_LANG.FIELD_DATA,
      drawer: true,
      closable: true,
      destroyOnClose: true,
      style: { width: 800 },
      children: (
        <CodeArea
          dataSet={this.fieldDataDrawerDS}
          name="inputData"
          style={{
            height: document.querySelector('.c7n-pro-modal-content').offsetHeight - 150,
          }}
          onChange={(value) => this.setState({ [inputData]: value })}
        />
      ),
      onOk: this.handleDataDrawerOk,
    });
  }

  /**
   * 确定操作
   */
  @Bind()
  async handleDataDrawerOk() {
    const { modalType } = this.state;
    const res = await this.fieldDataDrawerDS.validate();
    if (res) {
      if (modalType === 'source') {
        // Modal.open({
        //   movable: false,
        //   destroyOnClose: true,
        //   children: (
        //     <Form dataSet={this.fieldDataModalDS} labelWidth={120}>
        //       <TextField name="structureName" restrict="a-zA-Z0-9-_./" />
        //     </Form>
        //   ),
        //   onOk: this.handleParseData,
        // });
        const inputData = this.fieldDataDrawerDS.current.get('inputData');
        this.fieldMappingDrawerDS.current.set('sourceStructure', inputData);
        this.setInputData({ payload: JSON.parse(inputData) });
      } else {
        const inputData = this.fieldDataDrawerDS.current.get('inputData');
        this.fieldMappingDrawerDS.current.set('targetStructure', inputData);
        this.setInputData({ TARGET: JSON.parse(inputData) });
      }
      this.dataDrawer.close();
    }
    return false;
  }

  /**
   * 确定操作
   */
  @Bind()
  async handleParseData() {
    const res = await this.fieldDataModalDS.validate();
    if (res) {
      const name = this.fieldDataModalDS.current.get('structureName');
      const inputData = this.fieldDataDrawerDS.current.get('inputData');
      this.fieldMappingDrawerDS.current.set('sourceStructure', inputData);
      this.setInputData({ [name]: JSON.parse(inputData) });
      this.dataDrawer.close();
    } else {
      return false;
    }
  }

  /**
   * 设置sourceData/targetData
   */
  setInputData(inputData) {
    const { modalType } = this.state;
    const dataSource = modalType === 'source' ? 'sourceData' : 'targetData';
    this.setState({
      [dataSource]: jsonParse(inputData),
    });
  }

  /**
   * 保存脚本
   */
  @Bind()
  setRelationScript(value) {
    this.fieldMappingDrawerDS.current.set('script', value);
  }

  /**
   * 获取同行关系
   */
  @Bind()
  handleSameLineRel() {
    const { sourceData, targetData } = this.state;
    const relation = getSameLineRel(sourceData, targetData);
    const script = getRelationScript(relation);
    this.setRelationScript(script);
    this.setState({ relation, script });
  }

  /**
   * 获取同名关系
   */
  @Bind()
  handleSameNameRel() {
    const { sourceData, targetData } = this.state;
    const relation = getSameNameRel(sourceData, targetData);
    const script = getRelationScript(relation);
    this.setRelationScript(script);
    this.setState({ relation, script });
  }

  /**
   * 取消同名关系
   */
  handleCancelRel() {
    const script = insertScriptHeader('');
    this.setRelationScript(script);
    this.setState({ script, relation: [] });
  }

  /**
   * JSON文件上传处理
   * @param {ARRAY} files 文件
   */
  @Bind()
  handleFiles(files) {
    const { modalType } = this.state;
    const inputData = modalType === 'source' ? 'sourceInputData' : 'targetInputData';
    if (window.FileReader) {
      const reader = new FileReader();
      reader.readAsText(files[0]);
      reader.onload = () => {
        if (reader.result) {
          const structJson = JSON.parse(reader.result);
          if (modalType === 'source') {
            this.fieldMappingDrawerDS.current.set('sourceStructure', reader.result);
            this.setInputData({ payload: structJson });
          } else {
            this.fieldMappingDrawerDS.current.set('targetStructure', reader.result);
            this.setInputData({ TARGET: structJson });
          }
          this.setState({ [inputData]: reader.result });
          this.fileModal.close();
        }
      };
    }
  }

  @Bind()
  handleOpenFileModal() {
    const options = [
      {
        value: 'source',
        meaning: FIELD_MAPPING_LANG.SOURCE_TITLE,
      },
      {
        value: 'target',
        meaning: FIELD_MAPPING_LANG.TARGET_TITLE,
      },
    ];
    this.fileDataSet = new DataSet({
      autoQuery: false,
      autoCreate: true,
      selection: false,
      fields: [
        {
          name: 'fieldSource',
          label: FIELD_MAPPING_LANG.FILE_SOURCE,
          type: 'string',
          required: true,
        },
      ],
    });
    this.fileModal = Modal.open({
      title: FIELD_MAPPING_LANG.IMPORT_JSON,
      closable: true,
      destroyOnClose: true,
      style: { width: 450 },
      children: (
        <Form dataSet={this.fileDataSet}>
          <Select
            name="fieldSource"
            options={new DataSet({ data: options })}
            onChange={(val) => {
              this.setState({ modalType: val });
              this.fileModal.update({ footer: this.renderFileModalFooter(isEmpty(val)) });
            }}
          />
        </Form>
      ),
      afterClose: () => this.fileDataSet.reset(),
      footer: this.renderFileModalFooter(),
    });
  }

  /**
   * 更新文件弹窗按钮
   */
  @Bind()
  renderFileModalFooter(disable = true) {
    return (
      <ReactFileReader disabled={disable} fileTypes={['.json']} handleFiles={this.handleFiles}>
        <Button color="primary">{FIELD_MAPPING_LANG.IMPORT_JSON}</Button>
      </ReactFileReader>
    );
  }

  render() {
    const { disabledFlag } = this.props;
    const { sourceData, targetData, script, relation } = this.state;
    const sourceCols = [
      {
        title: disabledFlag ? (
          <QuestionPopover
            text={FIELD_MAPPING_LANG.SOURCE_TITLE}
            message={FIELD_MAPPING_LANG.SOURCE_TITLE_TIP}
          />
        ) : (
          <a
            title={FIELD_MAPPING_LANG.SOURCE_TITLE}
            onClick={() => this.handleOpenDataDrawer('source')}
          >
            <QuestionPopover
              text={FIELD_MAPPING_LANG.SOURCE_TITLE}
              message={FIELD_MAPPING_LANG.SOURCE_TITLE_TIP}
            />
          </a>
        ),
        key: 'name',
        width: '100%',
      },
    ];
    const targetCols = [
      {
        title: disabledFlag ? (
          <QuestionPopover
            text={FIELD_MAPPING_LANG.TARGET_TITLE}
            message={FIELD_MAPPING_LANG.TARGET_TITLE_TIP}
          />
        ) : (
          <a
            title={FIELD_MAPPING_LANG.TARGET_TITLE}
            onClick={() => this.handleOpenDataDrawer('target')}
          >
            <QuestionPopover
              text={FIELD_MAPPING_LANG.TARGET_TITLE}
              message={FIELD_MAPPING_LANG.TARGET_TITLE_TIP}
            />
          </a>
        ),
        key: 'name',
        width: '100%',
      },
    ];
    const fieldMappingProps = {
      script,
      relation,
      edit: !disabledFlag,
      source: {
        data: sourceData,
        columns: sourceCols,
      },
      target: {
        data: targetData,
        columns: targetCols,
      },
      // onDrawStart: (source, relation) => {
      //   console.log("onDrawStart: ", source, relation);
      // },
      // onDrawing: (source, relation) => {
      //   console.log("onDrawing: ", source, relation);
      // },
      // onDrawEnd: (source, target, relation) => {
      //   console.log("onDrawEnd: ", source, relation);
      // },
      onChange: () => {
        // console.log(11111, relation);
        // this.setState({
        //   relation
        // })
      },
      onGetValue: (value) => this.setRelationScript(value),
      // isSort: true,
    };
    return (
      <>
        <Card style={{ marginTop: '3px' }} title={<h3>{FIELD_MAPPING_LANG.BASIC_INFO}</h3>}>
          <Form
            labelLayout="horizontal"
            dataSet={this.fieldMappingFormDS}
            columns={3}
            disabled={disabledFlag}
          >
            <Select name="type" />
          </Form>
        </Card>
        <Card style={{ marginTop: '3px' }} title={<h3>{FIELD_MAPPING_LANG.DETAIL_INFO}</h3>}>
          {!disabledFlag && (
            <div style={{ textAlign: 'right', marginBottom: '5px' }}>
              <Button onClick={this.handleOpenFileModal}>{FIELD_MAPPING_LANG.IMPORT_JSON}</Button>
              <Button onClick={() => this.handleCancelRel()}>
                {FIELD_MAPPING_LANG.CANCEL_REL}
              </Button>
              <Button color="primary" onClick={() => this.handleSameNameRel()}>
                {FIELD_MAPPING_LANG.SAME_NAME_REL}
              </Button>
              <Button color="primary" onClick={() => this.handleSameLineRel()}>
                {FIELD_MAPPING_LANG.SAME_LINE_REL}
              </Button>
            </div>
          )}
          <FieldMapping {...fieldMappingProps} />
        </Card>
      </>
    );
  }
}
export default withPropsAPI(FieldMappingDrawer);
