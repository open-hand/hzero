/**
 * index
 * @date 2018/9/29
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { Spin } from 'hzero-ui';
import { cloneDeep, concat, forEach, isArray, slice } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';
import { DndProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { openTab } from 'utils/menuTab';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import PickBoxPanel from './PickBoxPanel/index';
import DesignerPanel from './DesignerPanel/index';
import PropPanel from './PropPanel/index';

import styles from './index.less';

import { DynamicConfigContext, emptyFieldType } from './config';

import {
  convertExtraTemplates,
  convertParseTemplates,
  convertInitExtraTemplate,
  convertInitExtraField,
} from './convertUtils';

@formatterCollections({ code: ['hpfm.ui'] })
export default class PageDesigner extends React.Component {
  // 存储 DesignerPanel 中 组件的 this
  renderRefs = {};

  constructor(props) {
    super(props);
    this.state = {
      config: {},
      // 给定左侧 可添加的组件类型
      activeComponentCode: '',
      // 属性编辑界面
      currentEditComponent: {},
      currentEditField: {},
      // 属性编辑组件的key(用来刷新右侧属性区域)
      propKey: uuid(),
      // 提供给 context 的值
      contextState: {},
    };
  }

  componentDidMount() {
    this.queryDetail();
  }

  render() {
    const { saving = false, fetching = false, bankPath, match } = this.props;
    const { contextState } = this.state;
    return (
      <DndProvider backend={HTML5Backend}>
        <DynamicConfigContext.Provider value={contextState}>
          <Header
            title={intl.get('hpfm.ui.model.page.configure').d('页面配置')}
            backPath={bankPath}
          >
            <ButtonPermission
              type="primary"
              permissionList={[
                {
                  code: `${match.path}.button.save`,
                  type: 'button',
                  meaning: '页面自定义页面配置-保存',
                },
              ]}
              onClick={this.handleSaveBtnClick}
              loading={saving}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
            <ButtonPermission
              permissionList={[
                {
                  code: `${match.path}.button.preview`,
                  type: 'button',
                  meaning: '页面自定义页面配置-预览',
                },
              ]}
              onClick={this.handleGotoPreview}
            >
              {intl.get('hzero.common.button.preview').d('预览')}
            </ButtonPermission>
          </Header>
          <Content style={{ padding: 0, display: 'flex', flexDirection: 'column' }}>
            <div className={styles['dynamic-config']}>
              <div className={styles['pick-box-panel']}>
                <Spin spinning={saving || fetching}>{this.renderPickBoxPanel()}</Spin>
              </div>
              <div className={styles['designer-panel']}>
                <Spin spinning={saving || fetching}>{this.renderDesignerPanel()}</Spin>
              </div>
              <div className={styles['prop-panel']}>
                <Spin spinning={saving || fetching}>{this.renderPropPanel()}</Spin>
              </div>
            </div>
          </Content>
        </DynamicConfigContext.Provider>
      </DndProvider>
    );
  }

  /**
   * 查询页面信息
   */
  @Bind()
  queryDetail() {
    const { getPageDetail } = this.props;
    getPageDetail().then(() => {
      const {
        uiPage: { detailIdp, config },
      } = this.props;
      const { contextState = {} } = this.state;
      const newConfig = cloneDeep(config);
      const newTemplates = convertExtraTemplates(newConfig.fields, { noCloneDeep: true });
      newConfig.fields = newTemplates;
      this.setState({
        contextState: {
          idp: detailIdp,
          ...contextState,
        },
        config: newConfig,
      });
    });
  }

  /**
   * 保存按钮点击
   */
  @Bind()
  handleSaveBtnClick() {
    const {
      uiPage: { config: prevConfig },
      onSave,
    } = this.props;
    const { config = {} } = this.state;
    const saveConfig = cloneDeep(config);
    const newTemplates = convertParseTemplates(saveConfig.fields, {
      noCloneDeep: true,
      refs: this.renderRefs,
    });
    saveConfig.fields = newTemplates;
    onSave(saveConfig, prevConfig).then(res => {
      if (res) {
        // 保存成功
        this.queryDetail();
      }
    });
  }

  /**
   * 渲染左侧组件区域
   */
  renderPickBoxPanel() {
    const { activeComponentCode } = this.state;
    return <PickBoxPanel activeComponentCode={activeComponentCode} />;
  }

  /**
   * 渲染中间编辑区域
   */
  renderDesignerPanel() {
    const { config = [], currentEditComponent, currentEditField } = this.state;
    return (
      <DesignerPanel
        config={config}
        onAddComponent={this.handleAddComponent}
        onAddField={this.handleAddField}
        onActiveComponent={this.handleActiveComponent}
        onActiveField={this.handleActiveField}
        onSwapField={this.handleSwapField}
        onRemoveField={this.handleRemoveField}
        currentEditComponent={currentEditComponent}
        currentEditField={currentEditField}
        refRender={this.handleRefRender}
      />
    );
  }

  /**
   * 渲染组件属性
   */
  renderPropPanel() {
    const { currentEditComponent, currentEditField, propKey, config } = this.state;
    return (
      <PropPanel
        component={currentEditComponent}
        field={currentEditField}
        propKey={propKey}
        templates={(config && config.fields) || []}
        config={config}
        onUpdateConfig={this.handleUpdateConfig}
        onRefresh={this.handleRefresh}
      />
    );
  }

  /**
   * 新增容器
   * @param {Object} component - new add component
   */
  @Bind()
  handleAddComponent(component = {}) {
    const { config } = this.state;
    const newComponent = cloneDeep(component);
    newComponent.fields = [];
    const newTemplate = convertInitExtraTemplate(newComponent, { noCloneDeep: true });
    if (newTemplate) {
      this.setState({
        config: {
          ...config,
          fields: [...config.fields, newTemplate],
        },
        activeComponentCode: newTemplate.templateType,
        currentEditComponent: newTemplate,
        currentEditField: undefined,
        propKey: uuid(),
      });
    }
  }

  /**
   * 新增字段
   * @param {Object} component - drop component
   * @param {Object} field - new add field
   * @param {Object} swapField - drop empty field
   */
  @Bind()
  handleAddField(component, field, swapField) {
    const newField = convertInitExtraField(component, field);
    if (isArray(component.fields)) {
      let insertIndex = component.fields.length;
      if (swapField) {
        forEach(component.fields, (cField, index) => {
          insertIndex = index;
          if (cField === swapField) {
            return false;
          }
        });
      }
      // eslint-disable-next-line
      component.fields = concat(
        [],
        slice(component.fields, 0, insertIndex),
        [newField],
        slice(component.fields, insertIndex)
      );
    } else {
      // eslint-disable-next-line no-param-reassign
      component.fields = [newField];
    }
    this.setState({
      activeComponentCode: component.templateType,
      currentEditComponent: component,
      currentEditField: newField,
      propKey: uuid(),
    });
  }

  /**
   * 交换字段
   * @param {Object} component - drop component
   * @param {Object} field - drag field
   * @param {Object} swapField - drop field
   */
  @Bind()
  handleSwapField(component, field, swapField) {
    const swapIndexs = [];
    forEach(component.fields, (cField, index) => {
      if (cField === field || cField === swapField) {
        // eslint-disable-next-line
        swapIndexs.push(index);
      }
    });
    if (swapIndexs.length === 2) {
      const tempField = component.fields[swapIndexs[0]];
      // eslint-disable-next-line
      component.fields[swapIndexs[0]] = component.fields[swapIndexs[1]];
      // eslint-disable-next-line
      component.fields[swapIndexs[1]] = tempField;
    }
  }

  /**
   * 删除字段
   * 只能删除 字段是 一维数组的字段
   */
  @Bind()
  handleRemoveField(component, removeField) {
    const { currentEditField = {} } = this.state;
    switch (component.templateType) {
      case 'DynamicTable':
        // eslint-disable-next-line no-param-reassign
        component.renderKey = uuid(); // warn dynamicTable fields update must reRender for react-sortable-pane
        break;
      default:
        break;
    }
    const newFields = [];
    forEach(component.fields, field => {
      if (field.fieldId !== removeField.fieldId) {
        newFields.push(field);
      }
    });
    // eslint-disable-next-line no-param-reassign
    component.fields = newFields;
    this.setState({
      currentEditComponent: component,
      currentEditField:
        currentEditField.fieldId === removeField.fieldId ? undefined : currentEditField,
    });
  }

  /**
   * 激活容器
   * @param {Object} component - active component
   */
  @Bind()
  handleActiveComponent(component = {}) {
    const { currentEditComponent, currentEditField } = this.state;
    if (component === currentEditComponent && !currentEditField) {
      // 当前编辑组件 已激活
      return;
    }
    this.setState({
      activeComponentCode: component.templateType,
      currentEditComponent: component,
      currentEditField: undefined,
      propKey: uuid(),
    });
  }

  /**
   * 激活字段
   * @param {Object} component - active filed's component
   * @param {Object} field - active field
   */
  @Bind()
  handleActiveField(component, field) {
    const { currentEditComponent, currentEditField } = this.state;
    if (
      field.componentType === emptyFieldType ||
      (currentEditComponent === component && currentEditField === field)
    ) {
      // 不能激活
      // 1. 占位的字段
      // 2. 当前字段已激活
      return;
    }
    this.setState({
      activeComponentCode: component.templateType,
      currentEditComponent: component,
      currentEditField: field,
      propKey: uuid(),
    });
  }

  @Bind()
  handleUpdateConfig(config = {}) {
    this.setState({
      config,
    });
  }

  /**
   * 获取当前编辑的页面编码
   */
  @Bind()
  getPageCode() {
    const {
      match: {
        params: { pageCode },
      },
    } = this.props;
    return pageCode;
  }

  /**
   * 打开页面预览
   */
  @Bind()
  handleGotoPreview() {
    const pageCode = this.getPageCode();
    const previewPath = `/hpfm/ui/page/preview/${pageCode}`;
    openTab({
      key: previewPath,
      path: previewPath,
      icon: 'search',
      title: 'hzero.common.title.uiPagePreview',
    });
  }

  /**
   * 获取中间编辑区域组件的 ref
   * @param renderKey
   * @param ref
   */
  @Bind()
  handleRefRender(renderKey, ref) {
    this.renderRefs = {
      ...this.renderRefs,
      [renderKey]: ref,
    };
  }

  /**
   * 刷新当前页面以更新 设计 或 属性区域内容
   */
  @Bind()
  handleRefresh() {
    this.forceUpdate();
  }
}
