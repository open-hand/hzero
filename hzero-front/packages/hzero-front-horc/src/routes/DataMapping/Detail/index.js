/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/7/8
 * @copyright HAND ® 2020
 */
import React from 'react';
import { connect } from 'dva';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { DataSet, Form, Button, TextField, Select, Table, Modal } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
import { headerFormDS, castLineTableDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';
import CastLineDrawer from './CastLineDrawer';
import MappingDrawer from './MappingDrawer';
import FormulaDrawer from './FormulaDrawer';

@connect(({ dataMapping }) => ({
  dataMapping,
}))
@formatterCollections({ code: ['horc.dataMapping'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const {
      match: { path },
      location = {},
    } = props;
    const isHistory = path.includes('history');
    const { editFlag } = location.state || {};
    this.state = {
      tenantId: getCurrentOrganizationId(),
      historyFlag: isHistory,
      readOnly: isHistory || !editFlag,
    };
    this.headerFormDS = new DataSet({
      ...headerFormDS(),
    });
    this.castLineTableDS = new DataSet({
      ...castLineTableDS(),
    });
  }

  componentDidMount() {
    const {
      dispatch,
      match: { params },
    } = this.props;
    const { tenantId } = this.state;
    const { id } = params;
    if (!isUndefined(id)) {
      this.handleFetchDetail(id);
    } else {
      this.headerFormDS.create({ tenantId });
    }
    dispatch({ type: 'dataMapping/init' });
  }

  /**
   * 查询
   */
  async handleFetchDetail(id) {
    const { historyFlag } = this.state;
    const {
      match: {
        params: { version },
      },
    } = this.props;
    this.headerFormDS.setQueryParameter('castHeaderId', id);
    this.headerFormDS.setQueryParameter('version', version);
    this.headerFormDS.setQueryParameter('_historyFlag', historyFlag);

    this.castLineTableDS.setQueryParameter('castHeaderId', id);
    if (historyFlag) {
      this.castLineTableDS.setQueryParameter('formerVersionFlag', historyFlag);
      this.castLineTableDS.setQueryParameter('version', version);
    }
    await Promise.all([this.headerFormDS.query(), this.castLineTableDS.query()]);
    this.setState({ versionDesc: this.headerFormDS.current.get('versionDesc') || '' });
  }

  /**
   * 保存
   */
  @Bind()
  async handleSave() {
    const { match: params } = this.props;
    const { id } = params;
    const validate = await this.headerFormDS.validate();
    if (!validate) {
      return notification.error({
        message: DATA_MAPPING_LANG.SAVE_VALIDATE,
      });
    }
    const result = await this.headerFormDS.submit();
    if (getResponse(result)) {
      if (isUndefined(id)) {
        this.handleGotoDetail(result.content[0].castHeaderId);
      } else {
        await this.headerFormDS.query();
      }
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
        pathname: `/horc/data-mapping/detail/${id}`,
      })
    );
  }

  /**
   * 行列表查询
   */
  @Bind()
  async handleFetchLine() {
    const {
      match: { params },
    } = this.props;
    const { id } = params;
    this.castLineTableDS.setQueryParameter('castHeaderId', id);
    await this.castLineTableDS.query();
  }

  /**
   * 打开castLine滑窗
   */
  @Bind()
  handleOpenCastLineDrawer(isNew, record) {
    const {
      match: { path, params },
    } = this.props;
    const { historyFlag, readOnly } = this.state;
    const { tenantId } = this.state;
    const castLineDrawerProps = {
      isNew,
      path,
      tenantId,
      historyFlag,
      readOnly,
      version: params.version,
      castLineId: isNew ? null : record.get('castLineId'),
      castHeaderId: params.id,
      onFetchLine: this.handleFetchLine,
    };
    Modal.open({
      title: isNew ? DATA_MAPPING_LANG.CREATE_LINE : DATA_MAPPING_LANG.EDIT_LINE,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 600 },
      children: <CastLineDrawer {...castLineDrawerProps} />,
      okText: DATA_MAPPING_LANG.SAVE,
    });
  }

  /**
   * 转化映射滑窗
   */
  @Bind()
  handleOpenMappingDrawer(record) {
    const {
      match: {
        path,
        params: { version },
      },
      dataMapping: { comparisonTypeList },
    } = this.props;
    const { historyFlag, readOnly } = this.state;
    const { tenantId } = this.state;
    const mappingDrawerProps = {
      path,
      tenantId,
      comparisonTypeList,
      version,
      historyFlag,
      readOnly,
      castLineData: record.toData(),
      onFetchLine: this.handleFetchLine,
    };
    Modal.open({
      title: DATA_MAPPING_LANG.CAST_VAL_MAINTAIN,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1000 },
      children: <MappingDrawer {...mappingDrawerProps} />,
      okText: DATA_MAPPING_LANG.SURE,
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
      match: {
        path,
        params: { version },
      },
    } = this.props;
    const { tenantId, readOnly, historyFlag } = this.state;
    const formulaDrawerProps = {
      path,
      tenantId,
      readOnly,
      historyFlag,
      version,
      castLineId: record.get('castLineId'),
      castExpr: record.get('castExpr'),
      highlightedCastExpr: record.get('highlightedCastExpr'),
      onFetchLine: this.handleFetchLine,
    };
    Modal.open({
      title: DATA_MAPPING_LANG.CAST_FORMULA,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1000 },
      children: <FormulaDrawer {...formulaDrawerProps} />,
      okText: DATA_MAPPING_LANG.SAVE,
    });
  }

  /**
   * 回退到指定版本
   */
  @Bind()
  async handleRevert() {
    const {
      match: {
        params: { id, version },
      },
    } = this.props;
    this.headerFormDS.current.set('transformId', id);
    this.headerFormDS.current.set('version', version);
    this.headerFormDS.current.set('_historyFlag', true);
    const result = await this.headerFormDS.submit();
    if (getResponse(result)) {
      this.handleGotoDetail(id);
    }
  }

  get castLineColumns() {
    const {
      match: { path },
    } = this.props;
    const { readOnly } = this.state;
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
              ? DATA_MAPPING_LANG.FORMULA_MAINTAIN
              : value}
          </a>
        ),
      },
      {
        name: 'castVal',
        width: 100,
        renderer: ({ record }) => (
          <a onClick={() => this.handleOpenMappingDrawer(record)}>
            {DATA_MAPPING_LANG[`CAST_${record.get('castType')}`] || ''}
          </a>
        ),
      },
      {
        header: DATA_MAPPING_LANG.OPERATOR,
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
                      code: `${path}.button.castLine.delete`,
                      type: 'button',
                      meaning: '数据转化列表-删除',
                    },
                  ]}
                  disabled={readOnly}
                  onClick={() => this.castLineTableDS.delete(record)}
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
    const { match } = this.props;
    const { historyFlag, readOnly, versionDesc } = this.state;
    const { path, params } = match;
    const isNew = isUndefined(params.id);
    return (
      <>
        <Header title={DATA_MAPPING_LANG.DETAIL} backPath="/horc/data-mapping/list">
          {!readOnly && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.detail.save`,
                  type: 'button',
                  meaning: '数据转化-保存',
                },
              ]}
              icon="save"
              type="c7n-pro"
              color="primary"
              onClick={() => this.handleSave()}
            >
              {DATA_MAPPING_LANG.SAVE}
            </ButtonPermission>
          )}
          {historyFlag && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.detail.revert`,
                  type: 'button',
                  meaning: '字段映射-版本回退',
                },
              ]}
              icon="settings_backup_restore"
              type="c7n-pro"
              color="primary"
              onClick={() => this.handleRevert()}
            >
              {`${DATA_MAPPING_LANG.REVERT}: ${versionDesc}`}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{DATA_MAPPING_LANG.BASIC_INFO}</h3>}
          >
            <Form
              labelLayout="horizontal"
              dataSet={this.headerFormDS}
              columns={3}
              disabled={readOnly}
            >
              <TextField name="castCode" restrict="a-zA-Z0-9-_./" disabled={!isNew} />
              <TextField name="castName" />
              <Select name="dataType" />
              <TextField name="versionDesc" disabled />
              {!historyFlag && <Select name="statusCode" disabled />}
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{DATA_MAPPING_LANG.DETAIL_INFO}</h3>}
          >
            {!readOnly && (
              <div style={{ width: '100%', textAlign: 'right', marginBottom: '5px' }}>
                <Button
                  color="primary"
                  disabled={isNew}
                  onClick={() => this.handleOpenCastLineDrawer(true)}
                >
                  {DATA_MAPPING_LANG.CREATE}
                </Button>
              </div>
            )}
            <Table dataSet={this.castLineTableDS} columns={this.castLineColumns} />
          </Card>
        </Content>
      </>
    );
  }
}
