import React, { PureComponent } from 'react';
import { Card, Tag } from 'choerodon-ui';
import { Form, Output, DataSet, Button, Table } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { getResponse } from 'utils/utils';
import QuestionPopover from '@/components/QuestionPopover';
import { formulaLineTableDS, onlyReadFormDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
      isNew: props.isNew,
    };
    this.formulaLineTableDS = new DataSet({
      ...formulaLineTableDS(),
    });
    this.onlyReadFormDS = new DataSet({
      ...onlyReadFormDS(),
    });
  }

  componentDidMount() {
    const { isNew } = this.state;
    if (!isNew) {
      this.handleFetchDetail();
    }
    this.handleUpdateModalProp();
    this.init();
  }

  init() {
    const { highlightedCastExpr } = this.props;
    this.onlyReadFormDS.loadData([{ highlightedCastExpr }]);
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
                code: `${path}.button.formula.save`,
                type: 'button',
                meaning: '公式-保存',
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
    const { modal, onFetchLine } = this.props;
    const validate = await this.formulaLineTableDS.validate();
    if (validate) {
      const result = await this.formulaLineTableDS.submit();
      if (getResponse(result)) {
        modal.close();
        onFetchLine();
      }
    } else {
      notification.error({
        message: DATA_MAPPING_LANG.SAVE_VALIDATE,
      });
    }
  }

  /**
   * 明细查询
   */
  @Bind()
  async handleFetchDetail() {
    const { castLineId, historyFlag, version } = this.props;
    this.formulaLineTableDS.setQueryParameter('castLineId', castLineId);
    if (historyFlag) {
      this.formulaLineTableDS.setQueryParameter('formerVersionFlag', historyFlag);
      this.formulaLineTableDS.setQueryParameter('version', version);
    }
    await this.formulaLineTableDS.query();
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
    this.props.onFetchLine();
  }

  get castLineColumns() {
    const { path, readOnly } = this.props;
    return [
      {
        name: 'orderSeq',
        editor: !readOnly,
        width: 80,
      },
      {
        name: 'exprFieldSourceType',
        editor: !readOnly,
      },
      {
        name: 'exprFieldSourceValue',
        editor: !readOnly,
      },
      {
        header: DATA_MAPPING_LANG.OPERATOR,
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
                  disabled={readOnly}
                  onClick={() => this.handleDelete(record)}
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

  @Bind()
  renderTipContent() {
    return (
      <>
        <p>{DATA_MAPPING_LANG.CAST_FORMULA_TIP_HEADER}</p>
        <p>
          {DATA_MAPPING_LANG.CAST_FORMULA_TIP_CONST}
          <Tag color="#ffbb00">#ffbb00</Tag>
        </p>
        <p>
          {DATA_MAPPING_LANG.CAST_FORMULA_TIP_FORMULA}
          <Tag color="#f0e68c">#f0e68c</Tag>
        </p>
        <p>
          {DATA_MAPPING_LANG.CAST_FORMULA_TIP_RESPONSE}
          <Tag color="#7fffd4">#7fffd4</Tag>
        </p>
      </>
    );
  }

  render() {
    const { readOnly } = this.props;
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_MAPPING_LANG.BASIC_INFO}</h3>}
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
                  text={DATA_MAPPING_LANG.CAST_FORMULA}
                  message={this.renderTipContent()}
                />
              }
            />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_MAPPING_LANG.DETAIL_INFO}</h3>}
        >
          {!readOnly && (
            <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
              <Button color="primary" onClick={this.handleCreate}>
                {DATA_MAPPING_LANG.CREATE}
              </Button>
            </div>
          )}
          <Table dataSet={this.formulaLineTableDS} columns={this.castLineColumns} />
        </Card>
      </>
    );
  }
}
