import React, { PureComponent } from 'react';
import { Spin, Card } from 'choerodon-ui';
import { Form, TextField, DataSet, Button, Table, Modal, CodeArea, Select } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { getResponse } from 'utils/utils';
import notification from 'utils/notification';
import { isUndefined } from 'lodash';
import { mappingLineTableDS, onlyReadFormDS, sqlFormDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';
import MappingFormDrawer from './MappingFormDrawer';
import FieldMappingModal from './FieldMappingModal';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
    };
    this.mappingLineTableDS = new DataSet({
      ...mappingLineTableDS(),
    });
    this.onlyReadFormDS = new DataSet({
      ...onlyReadFormDS(),
    });
    this.sqlFormDS = new DataSet({
      ...sqlFormDS(),
    });
  }

  componentDidMount() {
    const { castLineData } = this.props;
    this.handleFetchDetail();
    this.init();
    if (castLineData.castType === 'SQL') {
      this.handleUpdateModalProp();
    }
  }

  init() {
    const { castLineData } = this.props;
    this.onlyReadFormDS.loadData([castLineData]);
  }

  /**
   * 明细查询
   */
  @Bind()
  async handleFetchDetail() {
    const { castLineData, historyFlag, version } = this.props;
    const { castLineId } = castLineData;
    this.setState({ detailLoading: true });
    if (castLineData.castType === 'VAL') {
      this.mappingLineTableDS.setQueryParameter('castLineId', castLineId);
      if (historyFlag) {
        this.mappingLineTableDS.setQueryParameter('formerVersionFlag', historyFlag);
        this.mappingLineTableDS.setQueryParameter('version', version);
      }
      await this.mappingLineTableDS.query();
    }
    if (castLineData.castType === 'SQL') {
      this.sqlFormDS.setQueryParameter('castLineId', castLineId);
      if (historyFlag) {
        this.sqlFormDS.setQueryParameter('formerVersionFlag', historyFlag);
        this.sqlFormDS.setQueryParameter('version', version);
      }
      await this.sqlFormDS.query();
    }
    this.setState({ detailLoading: false });
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path, readOnly } = this.props;
    const { detailLoading } = this.state;
    modal.update({
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.mapping.save`,
                type: 'button',
                meaning: '数据映射-保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={detailLoading || readOnly}
            onClick={this.handleSave}
          >
            {DATA_MAPPING_LANG.SAVE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const { modal } = this.props;
    const validate = await this.sqlFormDS.validate();
    if (validate) {
      const result = await this.sqlFormDS.submit();
      if (getResponse(result)) {
        modal.close();
      }
    } else {
      notification.error({
        message: DATA_MAPPING_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 打开转化字段滑窗
   */
  @Bind()
  handleOpenMappingLineDrawer(isNew, record) {
    const { path, tenantId, castLineData, readOnly, version, historyFlag } = this.props;
    const { castLineId } = castLineData;
    const mappingFormDrawerProps = {
      isNew,
      path,
      tenantId,
      castLineId,
      castLineData,
      readOnly,
      version,
      historyFlag,
      mappingTargetId: isNew ? null : record.get('mappingTargetId'),
      onFetchLine: this.handleFetchDetail,
    };
    this.modal = Modal.open({
      title: DATA_MAPPING_LANG.CONDITION_MAINTAIN,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 600 },
      children: <MappingFormDrawer {...mappingFormDrawerProps} />,
      okText: DATA_MAPPING_LANG.SAVE,
    });
  }

  /**
   * 打开字段映射弹窗
   */
  @Bind()
  handleOpenFieldMappingModal(record) {
    const { path, tenantId, comparisonTypeList, castLineData, readOnly } = this.props;
    const fieldMappingModalProps = {
      path,
      tenantId,
      readOnly,
      comparisonTypeList,
      mappingTargetId: record.get('mappingTargetId'),
      logicValue: isUndefined(record.get('conditionJson'))
        ? {}
        : JSON.parse(record.get('conditionJson')),
      fieldMappingData: { ...castLineData, ...record.toData() },
      onFetchLine: this.handleFetchDetail,
    };
    this.modal = Modal.open({
      title: DATA_MAPPING_LANG.CONDITION_MAINTAIN,
      closable: true,
      key: Modal.key(),
      style: { width: 900 },
      children: <FieldMappingModal {...fieldMappingModalProps} />,
    });
  }

  get castLineColumns() {
    const { path, readOnly } = this.props;
    return [
      {
        name: 'targetValue',
        width: 150,
      },
      {
        name: 'fieldType',
        width: 120,
      },
      {
        name: 'sourceMappingFields',
      },
      {
        header: DATA_MAPPING_LANG.OPERATOR,
        width: 180,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.mapping.edit`,
                      type: 'button',
                      meaning: '转化映射列表-编辑',
                    },
                  ]}
                  onClick={() => this.handleOpenMappingLineDrawer(false, record)}
                >
                  {DATA_MAPPING_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: DATA_MAPPING_LANG.EDIT,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.mapping.condition`,
                      type: 'button',
                      meaning: '转化映射列表-条件维护',
                    },
                  ]}
                  onClick={() => this.handleOpenFieldMappingModal(record)}
                >
                  {DATA_MAPPING_LANG.CONDITION_MAINTAIN}
                </ButtonPermission>
              ),
              key: 'fieldMappingSetting',
              len: 4,
              title: DATA_MAPPING_LANG.CONDITION_MAINTAIN,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.mapping.delete`,
                      type: 'button',
                      meaning: '转化映射列表-删除',
                    },
                  ]}
                  disabled={readOnly}
                  onClick={() => this.mappingLineTableDS.delete(record)}
                >
                  {DATA_MAPPING_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: DATA_MAPPING_LANG.DELETE,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    const { readOnly, castLineData = {} } = this.props;
    const { detailLoading } = this.state;
    return (
      <Spin spinning={detailLoading}>
        {castLineData.castType === 'VAL' && (
          <>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{DATA_MAPPING_LANG.BASIC_INFO}</h3>}
            >
              <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
                <TextField name="castField" />
                <Select name="castType" />
              </Form>
            </Card>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{DATA_MAPPING_LANG.DETAIL_INFO}</h3>}
            >
              {!readOnly && (
                <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
                  <Button color="primary" onClick={() => this.handleOpenMappingLineDrawer(true)}>
                    {DATA_MAPPING_LANG.CREATE}
                  </Button>
                </div>
              )}
              <Table dataSet={this.mappingLineTableDS} columns={this.castLineColumns} />
            </Card>
          </>
        )}
        {castLineData.castType === 'SQL' && (
          <>
            <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
              <TextField name="castField" />
              <Select name="castType" />
            </Form>
            <Form dataSet={this.sqlFormDS} disabled={readOnly}>
              <CodeArea name="castSql" style={{ height: 550 }} />
            </Form>
          </>
        )}
      </Spin>
    );
  }
}
