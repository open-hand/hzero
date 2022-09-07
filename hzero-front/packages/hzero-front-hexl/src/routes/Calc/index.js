/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-08-30 16:29:22
 * @LastEditTime : 2019-12-23 19:44:34
 * @LastEditors  : shenghao.liu@hand-china.com
 */
import React from 'react';
import { DataSet, Table, TextField, Modal, Button } from 'choerodon-ui/pro';

import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';
import { getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { API_HOST, HZERO_HEXL } from 'utils/config';
import { Button as ButtonPermission } from 'components/Permission';

import { templateCodeDs, btDsConfig } from './stores';
import styles from './index.less';

import FieldAttributesModal from './FieldAttributesModal';

const { Column } = Table;

@formatterCollections({ code: ['hzero.calc', 'hzero.hexl'] })
export default class Calc extends React.Component {
  constructor(props) {
    super(props);

    // 实例化 模板ds
    this.templateCodeDs = new DataSet({
      ...templateCodeDs(),
    });
  }

  // 价目表table字段render
  @Bind()
  calcColumnRenderer({ record, name }) {
    if (record.status !== 'add' && name === 'columnConfig') {
      return (
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${this.props.match.path}.button.columnconfig`,
              type: 'button',
              meaning: 'WebExcel属性定义-字段属性',
            },
          ]}
          funcType="flat"
          icon="mode_edit"
          onClick={(e) => this.checkSheet(record, e)}
        >
          {intl.get('hzero.hexl.field.columnconfig').d('字段属性')}
        </ButtonPermission>
      );
    }
    if (record.status !== 'add' && name === 'templateConfig') {
      return (
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${this.props.match.path}.button.templateConfig`,
              type: 'button',
              meaning: 'WebExcel属性定义-模板配置',
            },
          ]}
          funcType="flat"
          icon="mode_edit"
          onClick={(e) => this.openEditModal(record, e)}
        >
          {intl.get('hzero.hexl.field.templateConfig').d('模板配置')}
        </ButtonPermission>
      );
    }
    if (record.status !== 'add' && name === 'btConfig') {
      return (
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${this.props.match.path}.button.btConfig`,
              type: 'button',
              meaning: 'WebExcel属性定义-按钮配置',
            },
          ]}
          funcType="flat"
          icon="mode_edit"
          onClick={(e) => this.openBtModal(record, e)}
        >
          {intl.get('hzero.hexl.field.btConfig').d('按钮配置')}
        </ButtonPermission>
      );
    }
  }

  // 按钮配置
  openBtModal(record) {
    const handleBtnDsChange = ({ dataSet, name, value }) => {
      const getData = dataSet.get(dataSet.currentIndex);
      if (name === 'buttonType' && value === 'GOAL_SEEK') {
        getData.set('buttonAction', null);
      }
      if (name === 'buttonType' && value === 'URL') {
        getData.set('targetExcelCode', null);
        getData.set('targetExcelValue', null);
        getData.set('variableExcelCode', null);
        getData.set('guessValue', null);
      }
    };

    const configBtDs = new DataSet({
      ...btDsConfig(),
      events: {
        update: handleBtnDsChange,
      },
    });

    configBtDs.setQueryParameter('templateId', record.get('templateId'));
    configBtDs.query();
    configBtDs.getField('templateId').set('defaultValue', record.get('templateId') || '');
    // 字段属性全部保存
    const saveColumnConfig = async () => {
      try {
        const validFlag = await Promise.all([configBtDs.validate()]);
        const allValidFlag = !(validFlag.filter((flag) => flag !== true).length > 0);
        if (allValidFlag) {
          const res = await Promise.all([configBtDs.submit()]);
          if (!isEmpty(res) && res.failed && res.message) {
            notification.error({ message: res.message });
          } else {
            await configBtDs.query();
          }
        } else {
          notification.error({
            message: intl.get('hzero.common.notification.invalid').d('校验不通过'),
          });
        }
      } catch (err) {
        // notification.error({ message: err });
        // notification.error({
        //   message: `${intl.get('hzero.common.notification.error').d('操作失败')}:${err.message}`,
        // });
      }
    };

    const closeModal = () => {
      modalRef.close();
    };

    const modalRef = Modal.open({
      key: Modal.key(),
      drawer: true,
      style: { width: '100%' },
      bodyStyle: { height: '100%' },
      title: intl.get('hzero.hexl.prompt.btConfig').d('按钮配置'),
      closable: true,
      destroyOnClose: true,
      children: (
        <div>
          <Table
            key="price-list-config-bt"
            highLightRow={false}
            buttons={['add', 'delete']}
            dataSet={configBtDs}
            pagination={{ position: 'bottom' }}
          >
            <Column name="buttonName" editor width={240} sortable />
            <Column name="buttonType" editor width={200} sortable />
            <Column
              name="targetExcelCode"
              editor={(e) => e.data.buttonType === 'GOAL_SEEK'}
              width={120}
              sortable
            />
            <Column
              name="targetExcelValue"
              editor={(e) => e.data.buttonType === 'GOAL_SEEK'}
              width={100}
              sortable
            />
            <Column
              name="variableExcelCode"
              editor={(e) => e.data.buttonType === 'GOAL_SEEK'}
              width={120}
              sortable
            />
            <Column
              name="guessValue"
              editor={(e) => e.data.buttonType === 'GOAL_SEEK'}
              width={100}
              sortable
            />
            <Column name="buttonAction" editor={(e) => e.data.buttonType === 'URL'} sortable />
          </Table>
        </div>
      ),
      footer: () => (
        <div>
          <Button color="primary" onClick={saveColumnConfig} className={styles.rightFloat}>
            {intl.get('hzero.hexl.button.saveAll').d('全部保存')}
          </Button>
          <Button onClick={closeModal} className={styles.rightFloat}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </div>
      ),
    });
  }

  // 模板配置窗口
  openEditModal(record) {
    Modal.open({
      key: Modal.key(),
      drawer: true,
      style: { width: '100%' },
      bodyStyle: { height: '100%' },
      title: intl.get('hzero.hexl.field.templateConfig').d('模板配置'),
      closable: true,
      className: styles.editModalWrap,
      children: (
        <div className={styles.ModalIframeWrap}>
          <iframe
            title={intl.get('hzero.hexl.field.templateConfig').d('模板配置')}
            src={`${API_HOST}${HZERO_HEXL}/index.html?templateCode=${
              record.pristineData.templateCode
            }&access_token=${getAccessToken()}&readOnly=N&organizationId=${getCurrentOrganizationId()}&templateVersion=${
              record.pristineData.templateVersion
            }&templateId=${record.pristineData.templateId}`}
            width="95%"
          />
        </div>
      ),
      footer: null,
      onClose: () => {
        this.templateCodeDs.query();
      },
    });
  }

  // 检查是否进行了模板配置
  @Bind()
  checkSheet(record) {
    const handleSheetLoad = ({ dataSet }) => {
      if (!dataSet.get(0)) {
        notification.warning({
          message: intl.get('hzero.hexl.validation.beforeTemplateConfig').d('请先进行模板配置'),
        });
      } else {
        this.openColumnModal(record, dataSet);
      }
    };

    // eslint-disable-next-line no-unused-vars
    const optionDs = new DataSet({
      selection: 'single',
      transport: {
        read: ({ config }) => ({
          ...config,
          url: `${API_HOST}${HZERO_HEXL}/v1/${getCurrentOrganizationId()}/excel-templates/${record.get(
            'templateId'
          )}/sheet-names`,
          method: 'get',
        }),
      },
      autoQuery: true,
      events: {
        load: handleSheetLoad,
      },
    });
  }

  // 字段属性配置窗口
  @Bind()
  openColumnModal(record, sheetDS) {
    const closeModal = () => {
      modalRef.close();
    };

    const saveColumnConfig = async () => {
      await this.FieldAttributesModal.saveColumnConfig();
    };

    const modalRef = Modal.open({
      key: Modal.key(),
      drawer: true,
      style: { width: '100%' },
      bodyStyle: { height: '100%' },
      title: intl.get('hzero.hexl.prompt.columnconfig').d('模板字段属性'),
      closable: true,
      destroyOnClose: true,
      children: (
        <FieldAttributesModal
          record={record}
          sheetDS={sheetDS}
          triggerRef={(ref) => {
            this.FieldAttributesModal = ref;
          }}
        />
      ),
      footer: () => (
        <div>
          <Button color="primary" onClick={saveColumnConfig} className={styles.rightFloat}>
            {intl.get('hzero.hexl.button.saveAll').d('全部保存')}
          </Button>
          <Button onClick={closeModal} className={styles.rightFloat}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </div>
      ),
      onClose: () => {
        this.templateCodeDs.query();
      },
    });
  }

  // 字段行table操作按钮
  configLnCommands() {
    return [['delete', { color: 'red' }]];
  }

  // 模板创建
  @Bind()
  calcCreate() {
    this.templateCodeDs.create({}, 0);
  }

  // 模板保存
  @Bind()
  async calcSave() {
    try {
      const validFlag = await this.templateCodeDs.validate();
      if (validFlag) {
        const res = await this.templateCodeDs.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          notification.error({ message: res.message });
        } else {
          this.templateCodeDs.query();
        }
      } else {
        notification.error({
          message: intl.get('hzero.common.notification.invalid').d('校验不通过'),
        });
      }
    } catch (err) {
      // notification.error({
      //   message: `${intl.get('hzero.common.notification.error').d('操作失败')}:${err.message}`,
      // });
      // this.templateCodeDs.query();
    }
  }

  // 模板删除
  @Bind()
  async calcDelete() {
    try {
      const res = await this.templateCodeDs.delete(this.templateCodeDs.selected);
      if (!isEmpty(res) && res.failed && res.message) {
        notification.error({ message: res.message });
      }
    } catch (err) {
      notification.error({
        message: `${intl.get('hzero.common.notification.error').d('操作失败')}:${err.message}`,
      });
    }
  }

  // 模板刷新
  @Bind()
  calcRefresh() {
    this.templateCodeDs.query();
  }

  // 模板table按钮
  get buttons() {
    return [
      <Button funcType="raised" icon="refresh" onClick={this.calcRefresh} key="refresh">
        {intl.get('hzero.common.button.refresh').d('刷新')}
      </Button>,
      <Button funcType="raised" icon="save" onClick={this.calcSave} key="save">
        {intl.get('hzero.common.button.save').d('保存')}
      </Button>,
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${this.props.match.path}.button.delete`,
            type: 'button',
            meaning: 'WebExcel属性定义-删除',
          },
        ]}
        funcType="raised"
        icon="delete"
        onClick={this.calcDelete}
        key="delete"
      >
        {intl.get('hzero.common.button.delete').d('删除')}
      </ButtonPermission>,
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${this.props.match.path}.button.create`,
            type: 'button',
            meaning: 'WebExcel属性定义-新增',
          },
        ]}
        funcType="raised"
        icon="add"
        // type="primary"
        color="primary"
        onClick={this.calcCreate}
        key="add"
      >
        {intl.get('hzero.common.button.add').d('新增')}
      </ButtonPermission>,
    ];
  }

  // 模板table列
  get columns() {
    return [
      {
        name: 'templateCode',
        editor: (record, name) =>
          name === 'templateCode' && record.status === 'add' ? (
            <TextField
              dataSet={this.templateCodeDs}
              name="templateCode"
              placeholder={intl
                .get('hzero.calc.button.price.config')
                .d('只支持输入大写字母/数字/符号_-')}
            />
          ) : null,
        sortable: true,
      },
      { name: 'description', editor: true, sortable: true },
      {
        name: 'templateVersion',
        editor: (record, name) =>
          name === 'templateVersion' && record.status === 'add' ? (
            <TextField
              placeholder={intl.get('hzero.hexl.validation.digital').d('只支持输入数字')}
              restrict="0-9"
            />
          ) : null,
        sortable: true,
      },
      { name: 'enabledFlag', editor: true, minWidth: 50 },
      {
        name: 'templateConfig',
        header: <span>{intl.get('hzero.hexl.field.templateConfig').d('模板配置')}</span>,
        align: 'center',
        renderer: this.calcColumnRenderer,
        lock: 'right',
      },
      {
        name: 'columnConfig',
        header: <span>{intl.get('hzero.hexl.field.columnconfig').d('字段属性')}</span>,
        align: 'center',
        renderer: this.calcColumnRenderer,
        lock: 'right',
      },
      {
        name: 'btConfig',
        header: <span>{intl.get('hzero.hexl.field.btConfig').d('按钮配置')}</span>,
        align: 'center',
        renderer: this.calcColumnRenderer,
        lock: 'right',
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={intl.get('hexl.calc.view.message.title.webExcelConfig').d('WebExcel管理')}>
          {this.buttons}
        </Header>
        <Content>
          <Table dataSet={this.templateCodeDs} border queryFieldsLimit={2} columns={this.columns} />
        </Content>
      </>
    );
  }
}
