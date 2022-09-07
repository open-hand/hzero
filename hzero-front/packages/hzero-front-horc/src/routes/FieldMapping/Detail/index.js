/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/7/8
 * @copyright HAND ® 2020
 */
import React from 'react';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { DataSet, Form, Modal, TextField, CodeArea, Select, Button, Spin } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
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
import { formDS, fieldDataDrawerDS } from '@/stores/FieldMapping/FieldMappingDS';
import FIELD_MAPPING_LANG from '@/langs/fieldMappingLang';
// import { TRANSFORM_STATUS } from '@/constants/constants';

@formatterCollections({ code: ['horc.fieldMapping'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const {
      match: { path },
      location = {},
    } = props;
    const isHistory = path.includes('history');
    const { editFlag } = location.state || {};
    this.state = {
      relation: [],
      sourceData: [],
      targetData: [],
      modalType: 'source',
      historyFlag: isHistory,
      readOnly: isHistory || !editFlag,
      tenantId: getCurrentOrganizationId(),
    };
    this.detailFormDS = new DataSet({
      ...formDS({ _required: true }),
    });
    this.fieldDataDrawerDS = new DataSet(fieldDataDrawerDS());
  }

  componentDidMount() {
    const {
      match: { params },
    } = this.props;
    const { tenantId } = this.state;
    const { id } = params;
    if (!isUndefined(id)) {
      this.fieldDataDrawerDS.create();
      this.handleFetchDetail(id);
    } else {
      this.detailFormDS.create({ tenantId });
    }
  }

  /**
   * 查询
   */
  async handleFetchDetail(id) {
    const { historyFlag } = this.state;
    const {
      match: {
        params: { version },
      },
    } = this.props;
    this.detailFormDS.setQueryParameter('transformId', id);
    this.detailFormDS.setQueryParameter('version', version);
    this.detailFormDS.setQueryParameter('_historyFlag', historyFlag);
    const res = await this.detailFormDS.query();
    const { sourceStructure, targetStructure, transformScript, versionDesc = '' } = res;
    const sourceData = jsonParse({ payload: JSON.parse(sourceStructure) });
    const targetData = jsonParse({ TARGET: JSON.parse(targetStructure) });
    // if (!historyFlag) {
    //   this.setState({ readOnly: statusCode === TRANSFORM_STATUS.PUBLISHED });
    // }
    this.setState(
      {
        sourceData,
        targetData,
        versionDesc,
        script: transformScript,
        sourceInputData: sourceStructure,
        targetInputData: targetStructure,
      },
      () => {
        const relation = transformLine(
          transformScript,
          sourceData[0].children,
          targetData[0].children
        );
        this.setState({ relation });
      }
    );
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const {
      match: { params },
    } = this.props;
    const { id } = params;
    const validate = await this.detailFormDS.validate();
    if (!validate) {
      return notification.error({
        message: FIELD_MAPPING_LANG.SAVE_VALIDATE,
      });
    }
    const result = await this.detailFormDS.submit();
    if (getResponse(result)) {
      if (isUndefined(id)) {
        this.handleGotoDetail(result.content[0].transformId);
      } else {
        this.handleFetchDetail(id);
      }
    }
  }

  /**
   * 跳转到明细页面
   * @param {*} id
   */
  @Bind()
  handleGotoDetail(id) {
    const { dispatch = () => {} } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/horc/field-mapping/detail/${id}`,
      })
    );
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
      this.fieldDataDrawerDS.create(data);
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
            height: document.querySelector('body').offsetHeight - 150,
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
    const inputData = this.fieldDataDrawerDS.current.get('inputData');
    const structJson = JSON.parse(inputData);
    if (res && inputData && structJson) {
      if (modalType === 'source') {
        this.detailFormDS.current.set('sourceStructure', inputData);
        this.setInputData({ payload: structJson });
      } else {
        this.detailFormDS.current.set('targetStructure', inputData);
        this.setInputData({ TARGET: structJson });
      }
      this.dataDrawer.close();
    }
    return false;
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
    this.detailFormDS.current.set('transformScript', value);
  }

  /**
   * 回退到指定版本
   */
  @Bind()
  async handleRevert() {
    const {
      match: {
        params: { id, version },
      },
    } = this.props;
    this.detailFormDS.current.set('transformId', id);
    this.detailFormDS.current.set('version', version);
    this.detailFormDS.current.set('_historyFlag', true);
    const result = await this.detailFormDS.submit();
    if (getResponse(result)) {
      this.handleGotoDetail(id);
    }
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
            this.detailFormDS.current.set('sourceStructure', reader.result);
            this.setInputData({ payload: structJson });
          } else {
            this.detailFormDS.current.set('targetStructure', reader.result);
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
    const { match } = this.props;
    const { path } = match;
    const {
      sourceData,
      targetData,
      script,
      relation,
      readOnly,
      historyFlag,
      versionDesc,
    } = this.state;
    const sourceCols = [
      {
        title: readOnly ? (
          FIELD_MAPPING_LANG.SOURCE_TITLE
        ) : (
          <a
            title={FIELD_MAPPING_LANG.SOURCE_TITLE}
            onClick={() => this.handleOpenDataDrawer('source')}
          >
            {FIELD_MAPPING_LANG.SOURCE_TITLE}
          </a>
        ),
        key: 'name',
        width: '100%',
      },
    ];
    const targetCols = [
      {
        title: readOnly ? (
          FIELD_MAPPING_LANG.TARGET_TITLE
        ) : (
          <a
            title={FIELD_MAPPING_LANG.TARGET_TITLE}
            onClick={() => this.handleOpenDataDrawer('target')}
          >
            {FIELD_MAPPING_LANG.TARGET_TITLE}
          </a>
        ),
        key: 'name',
        width: '100%',
      },
    ];
    const fieldMappingProps = {
      script,
      relation,
      source: {
        data: sourceData,
        columns: sourceCols,
      },
      target: {
        data: targetData,
        columns: targetCols,
      },
      edit: !readOnly,
      onGetValue: (value) => this.setRelationScript(value),
    };
    const isNew = isUndefined(match.params.id);
    return (
      <>
        <Header title={FIELD_MAPPING_LANG.DETAIL} backPath="/horc/field-mapping/list">
          {!readOnly && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.detail.save`,
                  type: 'button',
                  meaning: '字段映射-保存',
                },
              ]}
              icon="save"
              type="c7n-pro"
              color="primary"
              onClick={() => this.handleSave()}
            >
              {FIELD_MAPPING_LANG.SAVE}
            </ButtonPermission>
          )}
          {historyFlag && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.detail.revert`,
                  type: 'button',
                  meaning: '字段映射-版本回退',
                },
              ]}
              icon="settings_backup_restore"
              type="c7n-pro"
              color="primary"
              onClick={() => this.handleRevert()}
            >
              {`${FIELD_MAPPING_LANG.REVERT}: ${versionDesc}`}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Spin dataSet={this.detailFormDS}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{FIELD_MAPPING_LANG.BASIC_INFO}</h3>}
            >
              <Form
                labelLayout="horizontal"
                dataSet={this.detailFormDS}
                columns={3}
                disabled={readOnly}
              >
                <TextField name="transformCode" restrict="a-zA-Z0-9-_./" disabled={!isNew} />
                <TextField name="transformName" />
                <Select name="transformType" />
                <TextField name="versionDesc" disabled />
                {!historyFlag && <Select name="statusCode" disabled />}
              </Form>
            </Card>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{FIELD_MAPPING_LANG.DETAIL_INFO}</h3>}
            >
              {!readOnly && (
                <div style={{ textAlign: 'right', marginBottom: '5px' }}>
                  <Button onClick={this.handleOpenFileModal}>
                    {FIELD_MAPPING_LANG.IMPORT_JSON}
                  </Button>
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
          </Spin>
        </Content>
      </>
    );
  }
}
