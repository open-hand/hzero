import React, { PureComponent } from 'react';
import { Card } from 'choerodon-ui';
import { Form, Output, DataSet, Button, Table } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { getResponse } from 'utils/utils';
import { formulaLineTableDS, onlyReadFormDS } from '@/stores/DataMapping/DataMappingDS';
import getLang from '@/langs/dataMappingLang';

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
    const { castExpr } = this.props;
    this.onlyReadFormDS.loadData([{ castExpr }]);
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
                meaning: '公式-确定',
              },
            ]}
            type="c7n-pro"
            color="primary"
            disabled={detailLoading || readOnly}
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
   * 确定
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
        message: getLang('SAVE_VALIDATE'),
      });
    }
  }

  /**
   * 明细查询
   */
  @Bind()
  async handleFetchDetail() {
    const { castLineId } = this.props;
    this.formulaLineTableDS.setQueryParameter('castLineId', castLineId);
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
        header: getLang('OPERATOR'),
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
                  {getLang('DELETE')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: getLang('DELETE'),
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    const { readOnly } = this.props;
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('BASIC_INFO')}</h3>}
        >
          <Form dataSet={this.onlyReadFormDS} columns={2} labelWidth={60}>
            <Output name="castExpr" colSpan={2} />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('DETAIL_INFO')}</h3>}
        >
          {!readOnly && (
            <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
              <Button color="primary" onClick={this.handleCreate}>
                {getLang('CREATE')}
              </Button>
            </div>
          )}
          <Table dataSet={this.formulaLineTableDS} columns={this.castLineColumns} />
        </Card>
      </>
    );
  }
}
