/**
 * 新增字段 Model
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/17 20:00
 * @LastEditTime: 2019/10/25 9:38
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import {
  DataSet,
  Form,
  TextField,
  Lov,
  Modal,
  Select,
  CheckBox,
  Table,
  Button,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { HZERO_HRES } from 'utils/config';
import { observer } from 'mobx-react';
import openFormulaModel from './FormulaModel';

const modalKey = Modal.key();

@observer
class CreateModal extends Component {
  ds;

  ruleDs;

  constructor(props) {
    super(props);
    this.ruleDs = new DataSet({
      selection: false,
      autoQuery: false,
      paging: false,
      autoCreate: false,
      fields: [
        {
          label: intl.get('hres.ruleComponent.model.ruleCmp.paramName').d('脚本参数名称'),
          name: 'paramName',
          type: 'string',
          required: true,
          noCache: true,
        },
        {
          label: intl.get('hres.ruleComponent.model.ruleCmp.formula').d('公式'),
          name: 'formula',
          type: 'string',
          required: true,
        },
      ],
    });
    this.ds = new DataSet({
      children: { ruleScriptParamList: this.ruleDs },
      transport: {
        submit: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component-field/submit`,
      },
      events: {
        submit: ({ data }) => {
          const { record, dataSet, ruleCode } = this.props;
          const current = data[0];
          if (record) {
            if (
              record.get('fieldType') === this.ds.get(0).get('fieldType') &&
              record.get('editType') === this.ds.get(0).get('editType') &&
              record.get('businessModel') === this.ds.get(0).get('businessModel') &&
              record.get('isRequired') === this.ds.get(0).get('isRequired') &&
              record.get('formula') === this.ds.get(0).get('formula')
            ) {
              notification.info({
                message: intl.get('hres.common.notification.unmodified').d('表单未做修改'),
              });
              return false;
            }
            current._status = 'update';
          } else {
            current.tenantId = getCurrentOrganizationId();
            current.ruleCode = ruleCode;
            current.ruleComponentName = dataSet.ruleComponentName;
          }
          if (
            (current.fieldType === 'DATE' &&
              (current.editType === 'SELECT' || current.editType === 'NUMBER')) ||
            (current.fieldType === 'STRING' &&
              (current.editType === 'DATE' || current.editType === 'NUMBER')) ||
            (current.fieldType === 'NUMBER' && current.editType === 'DATE')
          ) {
            notification.error({
              message: intl
                .get('hres.rule.view.message.validate.modelError')
                .d('字段类型和编辑类型冲突！'),
            });
            return false;
          }
          return true;
        },
      },
      autoCreate: true,
      fields: [
        {
          name: 'fieldName',
          type: 'string',
          label: intl.get('hres.ruleComponent.model.ruleCmp.fieldName').d('字段名称'),
          required: true,
        },
        {
          name: 'isRequired',
          type: 'boolean',
          trueValue: 'Y',
          falseValue: 'N',
          label: intl.get('hres.ruleComponent.model.ruleCmp.isRequired').d('是否必输'),
          defaultValue: 'N',
          required: true,
        },
        {
          name: 'fieldType',
          type: 'string',
          label: intl.get('hres.ruleComponent.model.ruleCmp.fieldType').d('字段类型'),
          lookupCode: 'HRES.FIELD_TYPE',
          required: true,
        },
        {
          name: 'editType',
          type: 'string',
          label: intl.get('hres.ruleComponent.model.ruleCmp.editType').d('编辑类型'),
          lookupCode: 'HRES.EDIT_TYPE',
          required: true,
        },
        {
          name: 'businessModelLov',
          type: 'object',
          label: intl.get('hres.ruleComponent.model.ruleCmp.businessModelLov').d('业务模型'),
          lovCode: 'HRES.BUSINESS_MODEL',
          dynamicProps: {
            lovPara: ({ record }) => ({
              editType: record.get('editType'),
            }),
          },
          ignore: 'always',
          noCache: true,
        },
        {
          name: 'businessModelName',
          type: 'string',
          bind: 'businessModelLov.businessModelName',
        },
        {
          name: 'businessModel',
          type: 'string',
          bind: 'businessModelLov.businessModel',
        },
        {
          name: 'valueField',
          type: 'string',
          bind: 'businessModelLov.valueField',
        },
        {
          name: 'formula',
          type: 'string',
          label: intl.get('hres.ruleComponent.model.ruleCmp.formula').d('公式'),
        },
        {
          name: 'ruleScriptCodeLov',
          type: 'object',
          label: intl.get('hres.ruleComponent.model.ruleCmp.ruleScriptCode').d('规则脚本'),
          lovCode: 'HPFM.RULE_SCRIPT',
          lovPara: { tenantId: getCurrentOrganizationId() },
          required: true,
          ignore: 'always',
          dynamicProps: ({ record }) => ({
            required: record.get('editType') === 'SCRIPT',
          }),
        },
        {
          name: 'ruleScriptCode',
          type: 'string',
          bind: 'ruleScriptCodeLov.scriptCode',
        },
      ],
    });
    this.props.callback(this.ds);
  }

  get columns() {
    const { type } = this.props;
    return [
      {
        name: 'paramName',
        editor: type !== 'edit',
      },
      {
        name: 'formula',
        editor:
          type === 'edit'
            ? false
            : () => (
                <TextField
                  onFocus={() => {
                    const { ruleCode } = this.props;
                    openFormulaModel(this.ruleDs.get(0), ruleCode);
                  }}
                />
              ),
      },
    ];
  }

  async componentDidMount() {
    const { record } = this.props;
    if (record) {
      this.ds.removeAll();
      this.ds.create(record.data);
    }
  }

  saveRule = (
    <Button
      icon="save"
      name="save"
      funcType="flat"
      clearButton={false}
      onClick={this.handleSaveRule}
      key="save"
    >
      {intl.get('hzero.common.button.save').d('保存')}
    </Button>
  );

  @Bind()
  handleSaveRule() {
    console.log(this.ruleDs.records);
  }

  /**
   * 判断businessModelLov字段是否必输
   * @param record
   * @returns {Promise<void>}
   */
  @Bind()
  isRequired(record) {
    this.ds.current.set('businessModelLov', undefined);
    if (record === 'LOV' || record === 'SELECT') {
      this.ds.getField('businessModelLov').set('required', true);
      return false;
    }
    this.ds.getField('businessModelLov').reset();
    this.ds.validate();
    return true;
  }

  /**
   * 选择字段类型后给编辑类型默认值
   * @param record
   * @returns {Promise<void>}
   */
  @Bind()
  setEditType(record) {
    if (record === 'STRING') {
      this.ds.current.set('editType', 'TEXT');
    }
    if (record === 'DATE') {
      this.ds.current.set('editType', 'DATE');
    }
    if (record === 'NUMBER') {
      this.ds.current.set('editType', 'NUMBER');
    }
  }

  render() {
    const { type, ruleCode } = this.props;
    const buttons = [['add', { disabled: type === 'edit' }], this.saveRule];

    return (
      <>
        <Form columns={1} dataSet={this.ds}>
          <TextField name="fieldName" disabled={type === 'edit'} restrict="\S" />
          <Select name="fieldType" onChange={this.setEditType} disabled={type === 'edit'} />
          <Select name="editType" onChange={this.isRequired} disabled={type === 'edit'} />
          {(this.ds.current.get('editType') === 'LOV' ||
            this.ds.current.get('editType') === 'SELECT') && (
            <Lov
              name="businessModelLov"
              disabled={type === 'edit' || !this.ds.current.get('editType')}
            />
          )}
          {this.ds.current.get('editType') !== 'SCRIPT' && (
            <TextField
              name="formula"
              onClick={() => {
                openFormulaModel(this.ds.get(0), ruleCode);
              }}
            />
          )}
          {this.ds.current.get('editType') === 'SCRIPT' && (
            <Lov name="ruleScriptCodeLov" disabled={type === 'edit'} />
          )}
          <CheckBox name="isRequired" disabled={type === 'edit'} />
        </Form>
        {this.ds.current.get('editType') === 'SCRIPT' && (
          <Table
            dataSet={this.ruleDs}
            columns={this.columns}
            buttons={type === 'edit' ? null : buttons}
          />
        )}
      </>
    );
  }
}

/**
 * 获取model里的dateSet
 * @param ds
 */
let tempDs;

function callback(ds) {
  tempDs = ds;
}

/**
 * 打开model框函数
 * @param data
 */
export default function openCreateModel(data) {
  const { record, ruleCode, modelType, dataSet } = data;
  Modal.open({
    key: modalKey,
    title:
      modelType === 'new'
        ? intl.get('hres.ruleComponent.model.ruleCmp.ruleCmpNew').d('规则字段新增')
        : intl.get('hres.ruleComponent.model.ruleCmp.ruleCmpEdit').d('规则字段编辑'),
    drawer: true,
    children: (
      <CreateModal
        dataSet={dataSet}
        ruleCode={ruleCode}
        type={modelType}
        record={record}
        callback={callback}
      />
    ),
    destroyOnClose: true,
    onOk: () =>
      // eslint-disable-next-line
      new Promise(async (resolve) => {
        const result = await tempDs.validate();
        if (result === false) {
          notification.error({
            message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
          });
          return resolve(false);
        } else {
          tempDs.addEventListener('submitSuccess', async () => {
            await dataSet.query();
          });
          let res;
          try {
            res = await tempDs.submit();
          } finally {
            if (!isEmpty(res) && !res.failed) {
              resolve();
            } else {
              resolve(false);
            }
          }
        }
      }),
    afterClose: () => {
      tempDs.removeEventListener('submitSuccess', () => {
        dataSet.query();
      });
    },
  });
}
