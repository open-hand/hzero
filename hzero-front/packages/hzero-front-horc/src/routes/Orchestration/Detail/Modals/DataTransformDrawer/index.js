/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/7/15
 * @copyright HAND ® 2020
 */
import React from 'react';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import { DataSet, Form, Button, Select, Table, Modal } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { withPropsAPI } from 'gg-editor';
import { headerFormDS, castLineTableDS } from '@/stores/Orchestration/DataTransformDS';
import DATA_TRANSFORM_LANG from '@/langs/orchDataTransformLang';
import CastLineDrawer from './CastLineDrawer';
import MappingDrawer from './MappingDrawer';
import FormulaDrawer from './FormulaDrawer';

@formatterCollections({ code: ['horc.dataTransform'] })
class DataTransform extends React.Component {
  constructor(props) {
    super(props);
    this.headerFormDS = new DataSet({
      ...headerFormDS(),
    });
    this.castLineTableDS = new DataSet({
      ...castLineTableDS(),
    });
    props.onRef(this);
    this.state = {
      tenantId: getCurrentOrganizationId(),
    };
  }

  componentDidMount() {
    const { tenantId } = this.state;
    if (!isEmpty(this.nodeItem.model)) {
      this.init();
    } else {
      this.headerFormDS.create({ tenantId });
    }
  }

  /**
   * 获取当前节点
   */
  get nodeItem() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0] || {};
  }

  /**
   * 数据加载
   */
  init() {
    const { params } = this.nodeItem.model;
    if (isEmpty(params)) {
      this.headerFormDS.create();
    } else {
      const { rules, ...others } = params;
      this.headerFormDS.loadData([others]);
      this.castLineTableDS.loadData(rules);
    }
  }

  /**
   * 数据转化滑窗
   */
  @Bind()
  async handleOk() {
    const validate = await this.headerFormDS.validate();
    if (!validate) {
      return undefined;
    }
    const headerData = this.headerFormDS.current.toData();
    const lineData = this.castLineTableDS.toData();
    const updateData = {
      params: {
        ...headerData,
        rules: lineData,
      },
    };
    return updateData;
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const validate = await this.headerFormDS.validate();
    if (!validate) {
      return notification.error({
        message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
      });
    }
    await this.headerFormDS.submit();
  }

  /**
   * 打开castLine滑窗
   */
  @Bind()
  handleOpenCastLineDrawer(isNew, record) {
    const { path, disabledFlag } = this.props;
    const { tenantId } = this.state;
    const castLineDrawerProps = {
      path,
      disabledFlag,
      tenantId,
      currentRecord: record,
      onRef: (ref) => {
        this.castLineFormDS = ref.castLineFormDS;
      },
    };
    this.castLineDrawer = Modal.open({
      title: isNew ? DATA_TRANSFORM_LANG.CREATE_LINE : DATA_TRANSFORM_LANG.EDIT_LINE,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 600 },
      children: <CastLineDrawer {...castLineDrawerProps} />,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.transform.line.sure`,
                type: 'button',
                meaning: '转化行明细-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={disabledFlag}
            onClick={() => this.handleSaveCastLine(record)}
          >
            {DATA_TRANSFORM_LANG.SURE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * castLine滑窗保存
   */
  @Bind()
  async handleSaveCastLine(record) {
    const validate = await this.castLineFormDS.validate();
    if (validate) {
      const result = await this.castLineFormDS.current.toData();
      if (isUndefined(record)) {
        this.castLineTableDS.create(result);
      } else {
        const updateRecord = this.castLineTableDS.findRecordById(record.id);
        const {
          castRoot,
          castField,
          castType,
          castExpr,
          castLov,
          castSql,
          castUrl,
          castMask,
        } = result;
        // 一个个回填，确保dataset的add、update状态能够正确变更
        updateRecord.set('castRoot', castRoot);
        updateRecord.set('castField', castField);
        updateRecord.set('castType', castType);
        updateRecord.set('castExpr', castExpr);
        updateRecord.set('castLov', castLov);
        updateRecord.set('castSql', castSql);
        updateRecord.set('castUrl', castUrl);
        updateRecord.set('castMask', castMask);
      }
      this.castLineDrawer.close();
    } else {
      notification.error({
        message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 转化映射滑窗
   */
  @Bind()
  handleOpenMappingDrawer(record) {
    const { path, disabledFlag } = this.props;
    const { tenantId } = this.state;
    const mappingDrawerProps = {
      path,
      disabledFlag,
      tenantId,
      currentRecord: record,
      onRef: (ref) => {
        this.mappingLineTableDS = ref.mappingLineTableDS;
        this.sqlFormDS = ref.sqlFormDS;
      },
    };
    this.mappingDrawer = Modal.open({
      title:
        record.status === 'add'
          ? DATA_TRANSFORM_LANG.CREATE_MAPPING_LINE
          : DATA_TRANSFORM_LANG.EDIT_MAPPING_LINE,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1000 },
      children: <MappingDrawer {...mappingDrawerProps} />,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.mapping.detail.sure`,
                type: 'button',
                meaning: '转化映射-行明细-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={disabledFlag}
            onClick={() => this.handleSaveMapping(record)}
          >
            {DATA_TRANSFORM_LANG.SURE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 转化映射滑窗保存
   */
  @Bind()
  async handleSaveMapping(record) {
    if (this.sqlFormDS.current) {
      const validate = await this.sqlFormDS.validate();
      if (validate) {
        const { castSql } = this.sqlFormDS.current.toData();
        const updateRecord = this.castLineTableDS.findRecordById(record.id);
        updateRecord.set('castSql', castSql);
        this.mappingDrawer.close();
      } else {
        notification.error({
          message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
        });
      }
      return;
    }
    const validate = await this.mappingLineTableDS.validate();
    if (validate) {
      const results = this.mappingLineTableDS.toData();
      const updateRecord = this.castLineTableDS.findRecordById(record.id);
      updateRecord.set('mappingTargets', results);
      this.mappingDrawer.close();
    } else {
      notification.error({
        message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 公式滑窗
   */
  @Bind()
  handleOpenFormulaDrawer(record) {
    const { path, disabledFlag } = this.props;
    const { tenantId } = this.state;
    const formulaDrawerProps = {
      path,
      disabledFlag,
      tenantId,
      castLineId: record.get('castLineId'),
      castExpr: record.get('castExpr'),
      currentRecord: record,
    };
    Modal.open({
      title: DATA_TRANSFORM_LANG.CAST_FORMULA,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1000 },
      children: <FormulaDrawer {...formulaDrawerProps} />,
      okText: DATA_TRANSFORM_LANG.SAVE,
    });
  }

  get castLineColumns() {
    const { path, disabledFlag } = this.props;
    return [
      {
        name: 'castRoot',
        width: 150,
      },
      {
        name: 'castField',
        width: 150,
      },
      {
        name: 'castType',
        width: 120,
      },
      {
        name: 'castExpr',
        // width: 250,
        renderer: ({ value, record }) => (
          <a onClick={() => this.handleOpenFormulaDrawer(record)}>
            {isUndefined(value) && record.get('castType') === 'EXPR'
              ? DATA_TRANSFORM_LANG.FORMULA_MAINTAIN
              : value}
          </a>
        ),
      },
      {
        name: 'castVal',
        width: 100,
        renderer: ({ record }) => (
          <a onClick={() => this.handleOpenMappingDrawer(record)}>
            {DATA_TRANSFORM_LANG[`CAST_${record.get('castType')}`] || ''}
          </a>
        ),
      },
      {
        header: DATA_TRANSFORM_LANG.OPERATOR,
        width: 110,
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
                      code: `${path}.button.castLine.edit`,
                      type: 'button',
                      meaning: '数据转化列表-编辑',
                    },
                  ]}
                  onClick={() => this.handleOpenCastLineDrawer(false, record)}
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
                      code: `${path}.button.castLine.delete`,
                      type: 'button',
                      meaning: '数据转化列表-删除',
                    },
                  ]}
                  disabled={disabledFlag}
                  onClick={() => this.castLineTableDS.delete(record)}
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
    const { disabledFlag } = this.props;
    return (
      <>
        <Card style={{ marginTop: '3px' }} title={<h3>{DATA_TRANSFORM_LANG.BASIC_INFO}</h3>}>
          <Form
            labelLayout="horizontal"
            dataSet={this.headerFormDS}
            columns={3}
            disabled={disabledFlag}
          >
            <Select name="castDataType" />
          </Form>
        </Card>
        <Card style={{ marginTop: '3px' }} title={<h3>{DATA_TRANSFORM_LANG.DETAIL_INFO}</h3>}>
          {!disabledFlag && (
            <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
              <Button color="primary" onClick={() => this.handleOpenCastLineDrawer(true)}>
                {DATA_TRANSFORM_LANG.CREATE}
              </Button>
            </div>
          )}
          <Table dataSet={this.castLineTableDS} columns={this.castLineColumns} />
        </Card>
      </>
    );
  }
}
export default withPropsAPI(DataTransform);
