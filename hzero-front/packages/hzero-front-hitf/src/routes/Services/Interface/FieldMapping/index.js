/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/7/8
 * @copyright HAND ® 2020
 */
import React from 'react';
import notification from 'utils/notification';
import { DataSet, Form, Modal, TextField, CodeArea, Select, Button, Spin } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { getCurrentOrganizationId, getResponse, isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
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
import getLang from '@/langs/fieldMappingLang';

@formatterCollections({ code: [getLang('PREFIX')] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      relation: [],
      sourceData: [],
      targetData: [],
      modalType: 'source',
      tenantId: isTenantRoleLevel() ? getCurrentOrganizationId() : props.tenantId,
    };
    this.detailFormDS = new DataSet({
      ...formDS({ _required: false }),
    });
    this.fieldDataDrawerDS = new DataSet(fieldDataDrawerDS());
  }

  componentDidMount() {
    const { transformId, namespace, serverCode, interfaceCode, transformLevel } = this.props;
    const { tenantId } = this.state;
    if (!isUndefined(transformId)) {
      this.fieldDataDrawerDS.create();
      this.handleFetchDetail(transformId);
    } else {
      this.detailFormDS.create({ tenantId, namespace, serverCode, interfaceCode, transformLevel });
    }
    this.handleUpdateModalProp();
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path, readOnly } = this.props;
    modal.update({
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.mapping.save`,
                type: 'button',
                meaning: '字段映射-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={readOnly}
            onClick={this.handleSave}
          >
            {getLang('SURE')}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 查询
   */
  async handleFetchDetail(id) {
    this.detailFormDS.setQueryParameter('transformId', id);
    const res = await this.detailFormDS.query();
    const { sourceStructure, targetStructure, transformScript } = res;
    const sourceData = jsonParse({ payload: JSON.parse(sourceStructure) });
    const targetData = jsonParse({ TARGET: JSON.parse(targetStructure) });
    this.setState(
      {
        sourceData,
        targetData,
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
    const { transformId, transformIdName, onWriteBack } = this.props;
    const validate = await this.detailFormDS.validate();
    if (!validate) {
      return notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
    }
    const result = await this.detailFormDS.submit();
    if (getResponse(result)) {
      const id = isUndefined(transformId) ? result.content[0].transformId : transformId;
      const name = result.content[0].transformName;
      onWriteBack(transformIdName, id, name);
      this.handleFetchDetail(id);
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
        pathname: `/hitf/field-mapping/detail/${id}`,
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
      title: getLang('FIELD_DATA'),
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

  render() {
    const { readOnly } = this.props;
    const { sourceData, targetData, script, relation } = this.state;
    const sourceCols = [
      {
        title: readOnly ? (
          getLang('SOURCE_TITLE')
        ) : (
          <a title={getLang('SOURCE_TITLE')} onClick={() => this.handleOpenDataDrawer('source')}>
            {getLang('SOURCE_TITLE')}
          </a>
        ),
        key: 'name',
        width: '100%',
      },
    ];
    const targetCols = [
      {
        title: readOnly ? (
          getLang('TARGET_TITLE')
        ) : (
          <a title={getLang('TARGET_TITLE')} onClick={() => this.handleOpenDataDrawer('target')}>
            {getLang('TARGET_TITLE')}
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
        mutiple: true,
      },
      target: {
        data: targetData,
        columns: targetCols,
      },
      edit: !readOnly,
      onGetValue: (value) => this.setRelationScript(value),
    };
    return (
      <Spin dataSet={this.detailFormDS}>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('BASIC_INFO')}</h3>}
        >
          <Form
            labelLayout="horizontal"
            dataSet={this.detailFormDS}
            columns={3}
            disabled={readOnly}
          >
            <Select name="transformType" />
            <TextField name="versionDesc" disabled />
            <Select name="statusCode" disabled />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('DETAIL_INFO')}</h3>}
        >
          {!readOnly && (
            <div style={{ textAlign: 'right', marginBottom: '5px' }}>
              <Button onClick={() => this.handleCancelRel()}>{getLang('CANCEL_REL')}</Button>
              <Button color="primary" onClick={() => this.handleSameNameRel()}>
                {getLang('SAME_NAME_REL')}
              </Button>
              <Button color="primary" onClick={() => this.handleSameLineRel()}>
                {getLang('SAME_LINE_REL')}
              </Button>
            </div>
          )}
          <FieldMapping {...fieldMappingProps} />
        </Card>
      </Spin>
    );
  }
}
