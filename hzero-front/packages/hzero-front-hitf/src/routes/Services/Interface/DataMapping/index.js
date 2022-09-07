/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/7/8
 * @copyright HAND ® 2020
 */
import React from 'react';
import { Button as ButtonPermission } from 'components/Permission';
import { DataSet, Form, Button, Select, Table, Modal } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { headerFormDS, castLineTableDS } from '@/stores/DataMapping/DataMappingDS';
import getLang from '@/langs/dataMappingLang';
import CastLineDrawer from './CastLineDrawer';
import MappingDrawer from './MappingDrawer';
import FormulaDrawer from './FormulaDrawer';

@formatterCollections({ code: [getLang('PREFIX')] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tenantId: isTenantRoleLevel() ? getCurrentOrganizationId() : props.tenantId,
    };
    this.headerFormDS = new DataSet({
      ...headerFormDS({ _required: false }),
    });
    this.castLineTableDS = new DataSet({
      ...castLineTableDS(),
    });
  }

  componentDidMount() {
    const { castHeaderId, namespace, serverCode, interfaceCode, dataType, castLevel } = this.props;
    const { tenantId } = this.state;
    if (!isUndefined(castHeaderId)) {
      this.handleFetchDetail(castHeaderId);
    } else {
      this.headerFormDS.create({
        tenantId,
        namespace,
        serverCode,
        interfaceCode,
        dataType,
        castLevel,
      });
    }
    this.handleUpdateModalProp();
  }

  /**
   * 更新当前Modal的属性
   */
  @Bind()
  handleUpdateModalProp() {
    const { modal } = this.props;
    modal.update({
      footer: () => (
        <div style={{ textAlign: 'right' }}>
          <Button color="primary" onClick={() => modal.close()}>
            {getLang('SURE')}
          </Button>
        </div>
      ),
    });
  }

  /**
   * 查询
   */
  @Bind()
  async handleFetchDetail(id) {
    this.headerFormDS.setQueryParameter('castHeaderId', id);
    this.castLineTableDS.setQueryParameter('castHeaderId', id);
    await Promise.all([this.headerFormDS.query(), this.castLineTableDS.query()]);
  }

  /**
   * 行列表查询
   */
  @Bind()
  async handleFetchLine() {
    const { castHeaderId } = this.props;
    this.castLineTableDS.setQueryParameter('castHeaderId', castHeaderId);
    await this.castLineTableDS.query();
  }

  /**
   * 打开castLine滑窗
   */
  @Bind()
  handleOpenCastLineDrawer(isNew, record) {
    const {
      readOnly,
      castHeaderId,
      castHeaderIdName,
      onWriteBack,
      match: { path },
    } = this.props;
    const { tenantId } = this.state;
    const castLineDrawerProps = {
      readOnly,
      isNew,
      path,
      tenantId,
      castHeaderId,
      castHeaderIdName,
      onWriteBack,
      headerData: this.headerFormDS.current.toData(),
      castLineId: isNew ? null : record.get('castLineId'),
      onFetchLine: this.handleFetchLine,
      onFetchDetail: this.handleFetchDetail,
    };
    Modal.open({
      title: isNew ? getLang('CREATE_LINE') : getLang('EDIT_LINE'),
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 600 },
      children: <CastLineDrawer {...castLineDrawerProps} />,
      okText: getLang('SURE'),
    });
  }

  /**
   * 转化映射滑窗
   */
  @Bind()
  handleOpenMappingDrawer(record) {
    const {
      readOnly,
      match: { path },
    } = this.props;
    const { tenantId } = this.state;
    const mappingDrawerProps = {
      path,
      tenantId,
      readOnly,
      castLineData: record.toData(),
      onFetchLine: this.handleFetchLine,
    };
    Modal.open({
      title: getLang('CAST_VAL_MAINTAIN'),
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1000 },
      children: <MappingDrawer {...mappingDrawerProps} />,
      okText: getLang('SURE'),
      footer: (okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          {okBtn}
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 公式滑窗
   */
  @Bind()
  handleOpenFormulaDrawer(record) {
    const {
      readOnly,
      match: { path },
    } = this.props;
    const { tenantId } = this.state;
    const formulaDrawerProps = {
      path,
      tenantId,
      readOnly,
      castLineId: record.get('castLineId'),
      castExpr: record.get('castExpr'),
      onFetchLine: this.handleFetchLine,
    };
    Modal.open({
      title: getLang('CAST_FORMULA'),
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1000 },
      children: <FormulaDrawer {...formulaDrawerProps} />,
      okText: getLang('SURE'),
    });
  }

  get castLineColumns() {
    const {
      readOnly,
      match: { path },
    } = this.props;
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
              ? getLang('FORMULA_MAINTAIN')
              : value}
          </a>
        ),
      },
      {
        name: 'castVal',
        width: 100,
        renderer: ({ record }) => (
          <a onClick={() => this.handleOpenMappingDrawer(record)}>
            {getLang(`CAST_${record.get('castType')}`) || ''}
          </a>
        ),
      },
      {
        header: getLang('OPERATOR'),
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
                  {getLang('EDIT')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: getLang('EDIT'),
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
                  disabled={readOnly}
                  onClick={() => this.castLineTableDS.delete(record)}
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
          <Form labelLayout="horizontal" dataSet={this.headerFormDS} columns={3}>
            <Select name="dataType" disabled />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('DETAIL_INFO')}</h3>}
        >
          {!readOnly && (
            <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
              <Button color="primary" onClick={() => this.handleOpenCastLineDrawer(true)}>
                {getLang('CREATE')}
              </Button>
            </div>
          )}
          <Table dataSet={this.castLineTableDS} columns={this.castLineColumns} />
        </Card>
      </>
    );
  }
}
