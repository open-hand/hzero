import React, { PureComponent } from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Button, Table, Modal, Form, TextField, Select, CodeArea } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { isUndefined } from 'lodash';
import {
  mappingLineTableDS,
  onlyReadFormDS,
  sqlFormDS,
} from '@/stores/Orchestration/DataTransformDS';
import DATA_TRANSFORM_LANG from '@/langs/orchDataTransformLang';
import FieldMappingModal from './FieldMappingModal';
import MappingFormDrawer from './MappingFormDrawer';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.mappingLineTableDS = new DataSet({
      ...mappingLineTableDS(),
    });
    this.onlyReadFormDS = new DataSet({
      ...onlyReadFormDS(),
    });
    this.sqlFormDS = new DataSet({
      ...sqlFormDS(),
    });
    props.onRef(this);
  }

  componentDidMount() {
    this.init();
  }

  /**
   * 数据加载
   */
  @Bind
  init() {
    const { currentRecord = {} } = this.props;
    const { castType, mappingTargets = [] } = currentRecord.toData();
    this.onlyReadFormDS.loadData([currentRecord]);
    if (currentRecord.status === 'add') {
      // 新建再编辑情况
      if (castType === 'SQL') {
        this.sqlFormDS.create(currentRecord.toData());
      } else {
        mappingTargets.forEach((data) => {
          this.mappingLineTableDS.create(data);
        });
      }
    } else if (castType === 'SQL') {
      // 保存过后再编辑情况
      this.sqlFormDS.loadData([{ ...currentRecord.toData() }]);
    } else {
      this.mappingLineTableDS.loadData(mappingTargets);
    }
  }

  /**
   * 打开字段映射弹窗
   */
  @Bind()
  handleOpenFieldMappingModal(record) {
    const { path, disabledFlag, tenantId } = this.props;
    const fieldMappingModalProps = {
      path,
      disabledFlag,
      tenantId,
      currentRecord: record,
      castField: this.onlyReadFormDS.current.get('castField'),
      logicValue: isUndefined(record.get('conditionJson'))
        ? {}
        : JSON.parse(record.get('conditionJson')),
      onRef: (ref) => {
        this.fieldMappingTableDS = ref.fieldMappingTableDS;
      },
    };
    this.fieldMappingModal = Modal.open({
      movable: false,
      title: DATA_TRANSFORM_LANG.CONDITION_MAINTAIN,
      closable: true,
      key: Modal.key(),
      style: { width: 750 },
      children: <FieldMappingModal {...fieldMappingModalProps} />,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.mapping.filed.sure`,
                type: 'button',
                meaning: '字段映射配置-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={disabledFlag}
            onClick={() => this.handleSaveFieldMapping(record)}
          >
            {DATA_TRANSFORM_LANG.SURE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 字段映射弹窗保存
   */
  @Bind()
  async handleSaveFieldMapping(record) {
    const validate = await this.fieldMappingTableDS.validate();
    if (validate) {
      const result = await this.fieldMappingTableDS.current.toData();
      const { conditionJson, evaluateExpression, sourceMappingFields } = result;
      record.set('conditionJson', conditionJson);
      record.set('evaluateExpression', evaluateExpression);
      record.set('sourceMappingFields', sourceMappingFields);
      this.fieldMappingModal.close();
    } else {
      notification.error({
        message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 打开转化字段滑窗
   */
  @Bind()
  handleOpenMappingLineDrawer(record) {
    const { path, disabledFlag, tenantId } = this.props;
    const mappingFormDrawerProps = {
      path,
      disabledFlag,
      tenantId,
      mappingLineTableDS: this.mappingLineTableDS,
      currentRecord: record,
      castField: this.onlyReadFormDS.current.get('castField'),
    };
    this.modal = Modal.open({
      title: DATA_TRANSFORM_LANG.CONDITION_MAINTAIN,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 600 },
      children: <MappingFormDrawer {...mappingFormDrawerProps} />,
      okText: DATA_TRANSFORM_LANG.SAVE,
    });
  }

  get castLineColumns() {
    const { path, disabledFlag } = this.props;
    return [
      {
        name: 'targetValue',
        width: 180,
      },
      {
        name: 'fieldType',
        width: 150,
      },
      {
        name: 'sourceMappingFields',
      },
      {
        header: DATA_TRANSFORM_LANG.OPERATOR,
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
                  onClick={() => this.handleOpenMappingLineDrawer(record)}
                >
                  {DATA_TRANSFORM_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: DATA_TRANSFORM_LANG.EDIT,
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
                  {DATA_TRANSFORM_LANG.CONDITION_MAINTAIN}
                </ButtonPermission>
              ),
              key: 'fieldMappingSetting',
              len: 4,
              title: DATA_TRANSFORM_LANG.CONDITION_MAINTAIN,
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
                  disabled={disabledFlag}
                  onClick={() => this.mappingLineTableDS.delete(record)}
                >
                  {DATA_TRANSFORM_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: DATA_TRANSFORM_LANG.DELETE,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    const { currentRecord, disabledFlag } = this.props;
    const { castType } = currentRecord.toData();
    return (
      <>
        {castType === 'VAL' && (
          <>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{DATA_TRANSFORM_LANG.BASIC_INFO}</h3>}
            >
              <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
                <TextField name="castField" />
                <Select name="castType" />
              </Form>
            </Card>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{DATA_TRANSFORM_LANG.DETAIL_INFO}</h3>}
            >
              {!disabledFlag && (
                <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
                  <Button color="primary" onClick={() => this.handleOpenMappingLineDrawer()}>
                    {DATA_TRANSFORM_LANG.CREATE}
                  </Button>
                </div>
              )}
              <Table dataSet={this.mappingLineTableDS} columns={this.castLineColumns} />
            </Card>
          </>
        )}
        {castType === 'SQL' && (
          <>
            <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
              <TextField name="castField" />
              <Select name="castType" />
            </Form>
            <Form dataSet={this.sqlFormDS} disabled={disabledFlag}>
              <CodeArea name="castSql" style={{ height: 550 }} />
            </Form>
          </>
        )}
      </>
    );
  }
}
