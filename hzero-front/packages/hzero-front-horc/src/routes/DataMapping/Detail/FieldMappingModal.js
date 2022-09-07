import React, { PureComponent } from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Form, TextField } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
// import { operatorRender } from 'utils/renderer';
import { getResponse } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { fieldMappingTableDS, onlyReadFormDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';
import LogicOperation from '@/components/LogicOperation';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.fieldMappingTableDS = new DataSet({
      ...fieldMappingTableDS({
        // onAddFormItem: this.addFormItem,
        // onRemoveFormItem: this.removeFormItem,
        // onLoad: this.handleLoad,
      }),
    });
    this.onlyReadFormDS = new DataSet({
      ...onlyReadFormDS(),
    });
  }

  componentDidMount() {
    this.handleUpdateModalProp();
    this.init();
  }

  init() {
    const { fieldMappingData = {} } = this.props;
    this.onlyReadFormDS.loadData([fieldMappingData]);
    this.fieldMappingTableDS.loadData([fieldMappingData]);
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
            type="c7n-pro"
            color="primary"
            permissionList={[
              {
                code: `${path}.button.mapping.filed.save`,
                type: 'button',
                meaning: '字段映射配置-保存',
              },
            ]}
            disabled={readOnly}
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
    const validate = await this.fieldMappingTableDS.validate();
    if (validate) {
      const result = await this.fieldMappingTableDS.submit();
      if (getResponse(result)) {
        this.props.modal.close();
        this.props.onFetchLine();
      }
    } else {
      notification.error({
        message: DATA_MAPPING_LANG.SAVE_VALIDATE,
      });
    }
  }

  // /**
  //  * 新增
  //  */
  // @Bind()
  // async handleCrease() {
  //   const { mappingTargetId, tenantId } = this.props;
  //   await this.fieldMappingTableDS.create({
  //     mappingTargetId,
  //     tenantId,
  //   });
  // }

  // /**
  //  * 数据加载完后渲染表单
  //  */
  // @Bind()
  // handleLoad(dataSet) {
  //   this.renderForm(dataSet);
  // }

  // /**
  //  * 渲染表单
  //  */
  // @Bind()
  // renderForm(dataSet = []) {
  //   let formItems = [];
  //   dataSet.forEach((record) => {
  //     formItems = this.renderFormItem(formItems, record);
  //   });
  //   this.setState({ formItems });
  // }

  // /**
  //  * 添加子表单
  //  */
  // @Bind()
  // addFormItem(record = {}) {
  //   let { formItems } = this.state;
  //   formItems = this.renderFormItem(formItems, record);
  //   this.setState({ formItems });
  // }

  // /**
  //  * 添加单行formItem
  //  */
  // renderFormItem(items, record) {
  //   const { comparisonTypeList } = this.props;
  //   const formItems = items;
  //   formItems.push(
  //     <TextField
  //       required
  //       colSpan={9}
  //       label={DATA_MAPPING_LANG.CONDITION_FIELD}
  //       value={record.get('mappingField')}
  //       onChange={(value) => record.set('mappingField', value)}
  //     />
  //   );
  //   formItems.push(
  //     <Select
  //       required
  //       colSpan={5}
  //       label={DATA_MAPPING_LANG.CONDITION}
  //       value={record.get('comparisonType')}
  //       onChange={(value) => record.set('comparisonType', value)}
  //     >
  //       {comparisonTypeList.map((item) => (
  //         <Select.Option value={item.value} key={item.value}>
  //           {item.meaning}
  //         </Select.Option>
  //       ))}
  //     </Select>
  //   );
  //   formItems.push(
  //     <TextField
  //       required
  //       colSpan={9}
  //       label={DATA_MAPPING_LANG.VALUE}
  //       value={record.get('sourceValue')}
  //       onChange={(value) => record.set('sourceValue', value)}
  //     />
  //   );
  //   formItems.push(
  //     <Button
  //       funcType="flat"
  //       icon="delete_forever"
  //       onClick={() => this.fieldMappingTableDS.delete(record)}
  //     />
  //   );
  //   return formItems;
  // }

  // /**
  //  * 删除单行
  //  */
  // @Bind()
  // removeFormItem(dataSet) {
  //   this.renderForm(dataSet);
  // }

  // get fieldMappingColumns() {
  //   const { path } = this.props;
  //   return [
  //     {
  //       name: 'mappingField',
  //       editor: (record) => record.getState('editing'),
  //     },
  //     {
  //       name: 'sourceValue',
  //       editor: (record) => record.getState('editing'),
  //     },
  //     {
  //       header: DATA_MAPPING_LANG.OPERATOR,
  //       width: 120,
  //       align: 'center',
  //       renderer: ({ record }) => {
  //         const actions = [
  //           record.getState('editing') && {
  //             ele: (
  //               <ButtonPermission
  //                 type="text"
  //                 permissionList={[
  //                   {
  //                     code: `${path}.button.mapping.filed.save`,
  //                     type: 'button',
  //                     meaning: '字段映射配置-保存',
  //                   },
  //                 ]}
  //                 onClick={() => this.handleSave(record)}
  //               >
  //                 {DATA_MAPPING_LANG.SAVE}
  //               </ButtonPermission>
  //             ),
  //             key: 'save',
  //             len: 2,
  //             title: DATA_MAPPING_LANG.SAVE,
  //           },
  //           record.status === 'add' && {
  //             ele: (
  //               <ButtonPermission
  //                 type="text"
  //                 permissionList={[
  //                   {
  //                     code: `${path}.button.mapping.filed.cancel`,
  //                     type: 'button',
  //                     meaning: '字段映射配置-取消',
  //                   },
  //                 ]}
  //                 onClick={() => this.fieldMappingTableDS.remove(record)}
  //               >
  //                 {DATA_MAPPING_LANG.CANCEL}
  //               </ButtonPermission>
  //             ),
  //             key: 'cancel',
  //             len: 2,
  //             title: DATA_MAPPING_LANG.CANCEL,
  //           },
  //           !record.getState('editing') && {
  //             ele: (
  //               <ButtonPermission
  //                 type="text"
  //                 permissionList={[
  //                   {
  //                     code: `${path}.button.mapping.filed.edit`,
  //                     type: 'button',
  //                     meaning: '字段映射配置-编辑',
  //                   },
  //                 ]}
  //                 onClick={() => record.setState('editing', true)}
  //               >
  //                 {DATA_MAPPING_LANG.EDIT}
  //               </ButtonPermission>
  //             ),
  //             key: 'edit',
  //             len: 2,
  //             title: DATA_MAPPING_LANG.EDIT,
  //           },
  //           !record.getState('editing') && {
  //             ele: (
  //               <ButtonPermission
  //                 type="text"
  //                 permissionList={[
  //                   {
  //                     code: `${path}.button.mapping.filed.delete`,
  //                     type: 'button',
  //                     meaning: '字段映射配置-删除',
  //                   },
  //                 ]}
  //                 onClick={() => this.fieldMappingTableDS.delete(record)}
  //               >
  //                 {DATA_MAPPING_LANG.DELETE}
  //               </ButtonPermission>
  //             ),
  //             key: 'delete',
  //             len: 2,
  //             title: DATA_MAPPING_LANG.DELETE,
  //           },
  //         ];
  //         return operatorRender(actions, record);
  //       },
  //     },
  //   ];
  // }

  @Bind()
  getFormat(format) {
    const { jsonLogicFormat: conditionJson, stringFormat: evaluateExpression } = format;
    const { current } = this.fieldMappingTableDS;
    current.set('conditionJson', conditionJson);
    current.set('evaluateExpression', evaluateExpression);
  }

  render() {
    // const { formItems } = this.state;
    const { logicValue, readOnly } = this.props;
    const logicOperationProps = {
      readOnly,
      value: logicValue,
      onGetFormat: this.getFormat,
    };
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_MAPPING_LANG.BASIC_INFO}</h3>}
        >
          <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
            <TextField name="castField" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_MAPPING_LANG.CONDITION_MAINTAIN}</h3>}
        >
          {/* <div style={{ marginBottom: '5px' }}>
            <Button
              key="addCondition"
              funcType="flat"
              icon="add"
              color="primary"
              onClick={this.handleCrease}
            >
              {DATA_MAPPING_LANG.ADD_CONDITION}
            </Button>
          </div>
          <Form
            dataSet={this.fieldMappingTableDS}
            columns={24}
            labelLayout="float"
            useColon={false}
          >
            {formItems}
          </Form> */}
          <LogicOperation {...logicOperationProps} />
        </Card>
      </>
    );
  }
}
