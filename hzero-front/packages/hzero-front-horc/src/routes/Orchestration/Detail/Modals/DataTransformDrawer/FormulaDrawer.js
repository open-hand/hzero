import React, { PureComponent } from 'react';
import { Card, Tag } from 'choerodon-ui';
import { Form, Output, DataSet, Button, Table } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import QuestionPopover from '@/components/QuestionPopover';
import { formulaLineTableDS, onlyReadFormDS } from '@/stores/Orchestration/DataTransformDS';
import DATA_TRANSFORM_LANG from '@/langs/orchDataTransformLang';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.formulaLineTableDS = new DataSet({
      ...formulaLineTableDS(),
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
    const { currentRecord } = this.props;
    const { exprRules = [] } = currentRecord.toData();
    this.onlyReadFormDS.loadData([{ highlightedCastExpr: this.renderCastExpr(exprRules) }]);
    if (currentRecord.status === 'add') {
      // 新建再编辑情况
      exprRules.forEach((data) => {
        this.formulaLineTableDS.create(data);
      });
    } else {
      // 保存过后再编辑情况
      this.formulaLineTableDS.loadData(exprRules);
    }
  }

  /**
   * 公式样式化
   */
  renderCastExpr(exprRules = []) {
    let formulaCastExpr = '';
    exprRules.forEach((item) => {
      const { exprFieldSourceType, exprFieldSourceValue } = item;
      if (exprFieldSourceType === 'CONSTANT') {
        // 常量
        formulaCastExpr = `${formulaCastExpr}<span style="padding:0 5px;background-color:#ffbb00;font-weight:bold;font-style:italic;">${exprFieldSourceValue}</span>`;
      } else if (exprFieldSourceType === 'EXPR') {
        // 公式
        formulaCastExpr = `${formulaCastExpr}<span style="padding:0 5px;background-color:#f0e68c;">${exprFieldSourceValue}</span>`;
      } else if (exprFieldSourceType === 'PACKET_FIELD') {
        // 报文字段
        formulaCastExpr = `${formulaCastExpr}<span style="padding:0 5px;background-color:#7fffd4;">${exprFieldSourceValue}</span>`;
      } else {
        formulaCastExpr = `${formulaCastExpr}<span style="padding:0 5px;">${exprFieldSourceValue}</span>`;
      }
    });
    return formulaCastExpr;
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal, path, disabledFlag } = this.props;
    modal.update({
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.formula.save`,
                type: 'button',
                meaning: '公式-保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={disabledFlag}
            onClick={this.handleSave}
          >
            {DATA_TRANSFORM_LANG.SAVE}
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
    const { modal, currentRecord } = this.props;
    const validate = await this.formulaLineTableDS.validate();
    if (validate) {
      const results = this.formulaLineTableDS.toData();
      currentRecord.set('exprRules', results);
      modal.close();
    } else {
      notification.error({
        message: DATA_TRANSFORM_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 创建行
   */
  @Bind()
  handleCreate() {
    const { tenantId, castLineId } = this.props;
    const record = this.formulaLineTableDS.create({ tenantId, castLineId });
    let order = record.index + 1;
    if (record.previousRecord) {
      order = record.previousRecord.get('orderSeq') + 1;
    }
    record.set('orderSeq', order);
  }

  /**
   * 行删除
   */
  @Bind()
  async handleDelete(record) {
    await this.formulaLineTableDS.delete(record);
  }

  get castLineColumns() {
    const { path, disabledFlag } = this.props;
    return [
      {
        name: 'orderSeq',
        editor: !disabledFlag,
        width: 80,
      },
      {
        name: 'exprFieldSourceType',
        editor: !disabledFlag,
      },
      {
        name: 'exprFieldSourceValue',
        editor: !disabledFlag,
      },
      {
        header: DATA_TRANSFORM_LANG.OPERATOR,
        width: 80,
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
                      code: `${path}.button.formula.delete`,
                      type: 'button',
                      meaning: '公式列表-删除',
                    },
                  ]}
                  disabled={disabledFlag}
                  onClick={() => this.handleDelete(record)}
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

  @Bind()
  renderTipContent() {
    return (
      <>
        <p>{DATA_TRANSFORM_LANG.CAST_FORMULA_TIP_HEADER}</p>
        <p>
          {DATA_TRANSFORM_LANG.CAST_FORMULA_TIP_CONST}
          <Tag color="#ffbb00">#ffbb00</Tag>
        </p>
        <p>
          {DATA_TRANSFORM_LANG.CAST_FORMULA_TIP_FORMULA}
          <Tag color="#f0e68c">#f0e68c</Tag>
        </p>
        <p>
          {DATA_TRANSFORM_LANG.CAST_FORMULA_TIP_RESPONSE}
          <Tag color="#7fffd4">#7fffd4</Tag>
        </p>
      </>
    );
  }

  render() {
    const { disabledFlag } = this.props;
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_TRANSFORM_LANG.BASIC_INFO}</h3>}
        >
          <Form dataSet={this.onlyReadFormDS} columns={2} labelWidth={80}>
            <Output
              name="highlightedCastExpr"
              colSpan={2}
              renderer={({ text }) => (
                <p style={{ whiteSpace: 'pre-line' }} dangerouslySetInnerHTML={{ __html: text }} />
              )}
              label={
                <QuestionPopover
                  text={DATA_TRANSFORM_LANG.CAST_FORMULA}
                  message={this.renderTipContent()}
                />
              }
            />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_TRANSFORM_LANG.DETAIL_INFO}</h3>}
        >
          {!disabledFlag && (
            <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
              <Button color="primary" onClick={this.handleCreate}>
                {DATA_TRANSFORM_LANG.CREATE}
              </Button>
            </div>
          )}
          <Table dataSet={this.formulaLineTableDS} columns={this.castLineColumns} />
        </Card>
      </>
    );
  }
}
