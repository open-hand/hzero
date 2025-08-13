/**
 * 标签专用的 ckeditor
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/2
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { action, autorun, computed, observable } from 'mobx';
import { observer } from 'mobx-react';
import { Bind, Debounce } from 'lodash-decorators';
import { FormField } from 'choerodon-ui/pro/lib/field/FormField';
import { Spin } from 'choerodon-ui/pro';
import uuid from 'uuid/v4';

import RichTextEditor from 'components/RichTextEditor';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DEBOUNCE_TIME } from 'utils/constants';

import LabelTemplatePluginEntry from './plugins/label-template';
import { elementUnit } from './utils';

/**
 * 获取 内容的前缀
 * @param {object} config
 * @param {number} config.width - 宽度
 * @param {number} config.height - 高度
 */
function getLabelContainerPrefix(config) {
  const { width, height } = config;
  return `<div data-type="canvas" style="width: ${width}${elementUnit};height: ${height}${elementUnit}; border: 1px dashed #1e3255;">`;
}

/**
 * 获取 内容的后缀
 */
function getLabelContainerSuffix() {
  return '</div>';
}

// 表示标签的编码; 类型由 后端确认
const allowAttributes = [
  'contenteditable',
  // 存储的编辑使用到的数据
  'data-bordercolor',
  'data-borderwidth',
  'data-src',
  'data-backgroundcolor',
  'data-code',
  'data-datatype',
  'data-height',
  'data-shape',
  'data-type',
  'data-value',
  'data-width',
  // 存储 后端渲染使用的style
  'data-style',
];
// 前端渲染使用的style
const allowStyles = [
  'box-sizing',
  'border-width',
  'border-style',
  'border-color',
  'position',
  'display',
  'width',
  'height',
  'border-radius',
  'background-color',
  'overflow',
  'color',
];

@observer
export default class LabelCKEditor extends FormField {
  @observable inited = false;

  @observable richTextEditorProps = {
    key: uuid(),
    config: {
      resize_enabled: false,
      extraPlugins: 'label-template',
      // extraAllowedContent: 'div[data-type]{width,height,border} span[contenteditable,data-backgroundcolor,data-code,data-datatype,data-height,data-shape,data-style,data-type,data-value,data-width]{position,display,width,height,border,border-radius,background-color} img[alt,contenteditable,data-backgroundcolor,data-code,data-datatype,data-height,data-shape,data-style,data-type,data-value,data-width]{position,display,width,height,border,border-radius,background-color}',
      extraAllowedContent: {
        // 标签容器, 由于使用的 iframe 所有所有样式必须内联
        div: {
          attributes: 'data-type', // data-type="canvas" 表示标签 容器/画板
          styles: ['width', 'height', 'border'], // 样式
        },
        span: {
          attributes: allowAttributes.join(','),
          styles: allowStyles.join(','), // 形状 由 后端控制, 但是在编辑时的形状由前端控制; width, height
        },
        // img: {
        //   // data-code,contenteditable,data-shape,data-width,data-height,data-value,data-type,data-datatype,data-style,data-backgroundcolor
        //   attributes:
        //     'alt,contenteditable,data-backgroundcolor,data-code,data-datatype,data-height,data-shape,data-style,data-type,data-value,data-width', // 表示标签的编码; 类型由 后端确认
        //   // position,display,width,height,border,border-radius,background-color
        //   styles: 'position,display,width,height,border,border-radius,background-color', // 形状 由 后端控制, 但是在编辑时的形状由前端控制; width, height
        // },
        // img: {
        //   attributes: '*',
        //   styles: '*',
        // },
        // img(eleConfig) {
        //   if (eleConfig) {
        //     // the img is label;
        //     return (
        //       ['circle', 'rectangle'].includes(eleConfig.attributes['data-shape']) &&
        //       eleConfig.attributes['data-code']
        //     );
        //   }
        // },
      },
      // 不管原先的按钮了, 直接使用标签需要的按钮
      toolbarGroups: [
        { name: 'document', groups: ['mode', 'document', 'doctools'] },
        { name: 'clipboard', groups: ['clipboard', 'undo'] },
        { name: 'editing', groups: ['find', 'selection', 'spellchecker'] },
        { name: 'forms' },
        '/',
        { name: 'basicstyles', groups: ['basicstyles', 'cleanup'] },
        { name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi'] },
        { name: 'links' },
        { name: 'insert' },
        '/',
        { name: 'styles' },
        { name: 'colors' },
        { name: 'tools' },
        { name: 'others' },
        { name: 'about' },
        '/',
        { name: 'label-template' },
      ],
    },
  };

  taskRunning = false;

  pendingTask = [];

  prevTemplateWidth;

  prevTemplateHigh;

  /**
   * 执行任务的存储
   */
  tasks = {};

  // 富文本ref
  richTextEditorRef = React.createRef();

  // 任务定义器, 会定期检查是否有任务&执行任务
  triggerTaskTimer;

  get templateBuildConfig() {
    return {
      width: this.templateWidth,
      height: this.templateHigh,
    };
  }

  @computed get templateHigh() {
    if (!this.props.templateHigh || this.props.templateHigh < 1) {
      return 100;
    }
    return this.props.templateHigh;
  }

  @computed get templateWidth() {
    if (!this.props.templateWidth || this.props.templateWidth < 1) {
      return 100;
    }
    return this.props.templateWidth;
  }

  /**
   * @return null | editor
   */
  getEditor() {
    if (this.richTextEditorRef.current) {
      return this.richTextEditorRef.current.editor || null;
    }
    return null;
  }

  componentDidMount() {
    /**
     * 监听 任务情况
     */
    this.triggerTaskTimer = setInterval(this.triggerTaskRun, DEBOUNCE_TIME);
    /**
     * 监听 mobx 更新
     * 在 templateWidth 与 templateHigh 更新后 更新编辑器
     */
    this.disposer = autorun(() => {
      this.autoUpdateEditor({
        templateWidth: this.templateWidth,
        templateHigh: this.templateHigh,
      });
    });
    this.runInitAll();
  }

  componentWillUnmount() {
    if (this.triggerTaskTimer !== undefined) {
      clearTimeout(this.triggerTaskTimer);
      this.triggerTaskTimer = undefined;
    }
    this.tasks = null;
    this.disposer();
    this.handleDataChange.cancel();
  }

  @Bind()
  runInitAll() {
    /**
     * @typr InitTask
     * @description 初始化 编辑器
     * @todo 移除一些插件, 加入自己的插件
     */
    this.runTask({
      name: 'init',
      test: () => {
        const editor = this.getEditor();
        return !!editor;
      },
      run: this.initAll,
      runUntilTestSuccess: true,
      remainBefore: false,
      // need
      // always: false,
    });
  }

  /**
   * 遍历 模板内容
   * @param {string} htmlStr - 模板内容
   * @param {Function} travel - 回调
   */
  travelTemplateContent(htmlStr, travel) {
    // eslint-disable-next-line no-undef,new-cap
    const htmlParser = new CKEDITOR.htmlParser();
    // level control the depth of html tree
    let level = 0;
    let didMeetContainer = false;
    let containerLevel = 0;
    let hadReplace = false;
    htmlParser.onCDATA = cdata => {
      if (didMeetContainer && !hadReplace) {
        travel('cdataIn', { cdata });
      } else {
        travel('cdataOut', { cdata });
      }
    };
    htmlParser.onComment = comment => {
      if (didMeetContainer && !hadReplace) {
        travel('commentIn', { comment });
      } else {
        travel('commentOut', { comment });
      }
    };
    htmlParser.onTagOpen = (tagName, attributes, selfClosing) => {
      // TODO: can't add data-xxx pro for ckeditor; so add this
      level += 1;
      const prevDidMeetContainer = didMeetContainer;
      let isLabel = false;
      let labelShape = '';
      Object.keys(attributes || {}).forEach(key => {
        if (key === 'data-type' && attributes[key] === 'canvas') {
          if (!didMeetContainer) {
            containerLevel = level;
            didMeetContainer = true;
          }
        }
        if (key === 'data-shape') {
          isLabel = true;
          labelShape = attributes[key];
        }
      });
      if (prevDidMeetContainer && level <= containerLevel) {
        hadReplace = true;
        travel('tagOpenOut', { tagName, attributes, selfClosing, isLabel, labelShape });
      } else if (didMeetContainer) {
        travel('tagOpenIn', { tagName, attributes, selfClosing, isLabel, labelShape });
      } else {
        // no root canvas
        travel('tagOpenOut', { tagName, attributes, selfClosing, isLabel, labelShape });
      }
      if (selfClosing) {
        level -= 1;
      }
    };
    htmlParser.onTagClose = tagName => {
      level -= 1;
      if (didMeetContainer && !hadReplace) {
        travel('tagCloseIn', { tagName });
      } else {
        travel('tagCloseOut', { tagName });
      }
    };
    htmlParser.onText = text => {
      if (didMeetContainer && !hadReplace) {
        travel('textIn', { text });
      } else {
        travel('textOut', { text });
      }
    };
    htmlParser.parse(htmlStr);
  }

  checkAndReplaceTemplateContent(data) {
    const retContent = [];
    const replaceContent = [];
    let didIn = false;
    let didOut = false;
    let inLabel = false;
    let labelType;
    this.travelTemplateContent(data, (type, options) => {
      switch (type) {
        case 'tagOpenIn':
          {
            didIn = true;
            const { selfClosing, tagName, attributes, isLabel, labelShape } = options;
            inLabel = isLabel;
            labelType = labelShape;
            const newAttributes = { ...attributes };
            if (isLabel) {
              // 设置 宽高 给 标签
              // 需要取出 background-color 到样式中
              newAttributes.style = `${newAttributes['data-style']} background-color: ${
                newAttributes['data-backgroundcolor']
              };`;
            }
            const attributeStr = Object.keys(newAttributes)
              .map(attrKey => `${attrKey}="${newAttributes[attrKey]}"`)
              .join(' ');
            if (selfClosing) {
              retContent.push(`<${tagName} ${attributeStr} />`);
            } else {
              retContent.push(`<${tagName} ${attributeStr}>`);
            }
          }
          break;
        case 'tagOpenOut':
          {
            didOut = true;
            const { selfClosing, tagName, attributes, isLabel, labelShape } = options;
            inLabel = isLabel;
            labelType = labelShape;
            const newAttributes = { ...attributes };
            if (isLabel) {
              // 设置 宽高 给 标签
              // 需要取出 background-color 到样式中
              newAttributes.style = `${newAttributes['data-style']} background-color: ${
                newAttributes['data-backgroundcolor']
              };`;
            }
            const attributeStr = Object.keys(newAttributes)
              .map(attrKey => `${attrKey}="${newAttributes[attrKey]}"`)
              .join(' ');
            if (selfClosing) {
              replaceContent.push(`<${tagName} ${attributeStr} />`);
            } else {
              replaceContent.push(`<${tagName} ${attributeStr}>`);
            }
          }
          break;
        case 'tagCloseIn':
          {
            const { tagName } = options;
            retContent.push(`</${tagName}>`);
          }
          break;
        case 'tagCloseOut':
          {
            const { tagName } = options;
            replaceContent.push(`</${tagName}>`);
          }
          break;
        case 'textIn':
          {
            didIn = true;
            const { text } = options;
            if (inLabel && ['circle', 'rectangle'].includes(labelType)) {
              // 前后需要加空格 以使之可编辑内容
              retContent.push(` ${text.trim()} `);
            } else {
              retContent.push(text);
            }
          }
          break;
        case 'textOut':
          {
            didOut = true;
            const { text } = options;
            replaceContent.push(text);
          }
          break;
        default:
          break;
      }
    });
    // todo: retContent has the whitespace in the last line;
    retContent.splice(retContent.length - 2, 0, ...replaceContent);
    if (!didIn) {
      retContent.unshift(getLabelContainerPrefix(this.templateBuildConfig));
      retContent.push(getLabelContainerSuffix());
    }
    return {
      templateContent: retContent.join(''),
      hadReplace: didOut,
    };
  }

  @Debounce(DEBOUNCE_TIME)
  @Bind()
  handleDataChange(data) {
    this.runTask({
      name: 'data-change',
      run: () => {
        if (data) {
          // 检查是否删除了容器, 或者是否在非指定区域编辑
          // FIXME: 如果找到了 可以锁定 元素 的方法, 可以去掉这部分逻辑
          const { hadReplace, templateContent } = this.checkAndReplaceTemplateContent(data);
          if (hadReplace) {
            // 提示用户
            notification.info({
              message: intl.get('hrpt.labelTemplate.view.message.rmOverflow').d('超出内容被移除'),
            });
            super.setValue(templateContent);
            this.getEditor().setData(templateContent, {
              noSnapshot: true, // don't add undo record, because of undo maybe fire0 event
            });
          } else {
            super.setValue(data);
          }
        } else {
          // 如果全部删除了, 将 container 放到数据中
          const initialTemplateContent = `${getLabelContainerPrefix(
            this.templateBuildConfig
          )}${getLabelContainerSuffix()}`;
          super.setValue(
            `${getLabelContainerPrefix(this.templateBuildConfig)}${getLabelContainerSuffix()}`
          );
          this.getEditor().setData(initialTemplateContent, {
            noSnapshot: true, // don't add undo record, because of undo maybe fire0 event
          });
        }
      },
      runUntilTestSuccess: false,
      remainBefore: false,
    });
  }

  /**
   * 监听 change 事件
   * 设置初始值
   * 监听 resize 事件, 设置宽高
   */
  @Bind()
  initAll() {
    const editor = this.getEditor();
    if (editor) {
      const { CKEDITOR } = window;
      LabelTemplatePluginEntry(CKEDITOR);
      this.runTask({
        name: 'realInit',
        test: () => {
          const editor1 = this.getEditor();
          return editor1 && editor1.document;
        },
        run: this.realInit,
        runUntilTestSuccess: true,
        remainBefore: false,
      });
    }
  }

  @Bind()
  realInit() {
    const editor = this.getEditor();
    this.inited = true;
    const templateContent = super.getValue() || '';
    if (templateContent) {
      // 已经编辑过了
      // 第一次插入 使用 dom 方式
      const elePath = [];
      const extraElePath = [];
      let topEle;
      const topExtraEles = [];
      let didIn = false;
      // eslint-disable-next-line no-unused-vars
      let didOut = false;
      let inLabel = false;
      let labelType;
      this.travelTemplateContent(templateContent, (type, options) => {
        switch (type) {
          case 'tagOpenIn':
            {
              didIn = true;
              const { tagName, attributes, isLabel, labelShape, selfClosing } = options;
              inLabel = isLabel;
              labelType = labelShape;
              const newAttributes = { ...attributes };
              if (isLabel) {
                // 设置 宽高 给 标签
                // 需要取出 background-color 到样式中
                newAttributes.style = `${newAttributes['data-style']} background-color: ${
                  newAttributes['data-backgroundcolor']
                };`;
              }
              // 创建元素
              const curEle = editor.document.createElement(tagName, {
                attributes: newAttributes,
              });
              if (!topEle) {
                topEle = curEle;
              }
              if (elePath.length > 0) {
                elePath[elePath.length - 1].append(curEle);
              }
              if (!selfClosing) {
                elePath.push(curEle);
              }
            }
            break;
          case 'tagOpenOut':
            {
              didOut = true;
              const { tagName, attributes, isLabel, labelShape, selfClosing } = options;
              inLabel = isLabel;
              labelType = labelShape;
              const newAttributes = { ...attributes };
              if (isLabel) {
                // 设置 宽高 给 标签
                // 需要取出 background-color 到样式中
                newAttributes.style = `${newAttributes['data-style']} background-color: ${
                  newAttributes['data-backgroundcolor']
                };`;
              }
              // 创建元素
              const curEle = editor.document.createElement(tagName, {
                attributes: newAttributes,
              });
              if (extraElePath.length > 0) {
                extraElePath[extraElePath.length - 1].append(curEle);
              } else {
                topExtraEles.push(curEle);
              }
              if (!selfClosing) {
                extraElePath.push(curEle);
              }
            }
            break;
          case 'tagCloseIn':
            if (elePath.length > 0) {
              elePath.pop();
            }
            break;
          case 'tagCloseOut':
            if (extraElePath.length > 0) {
              extraElePath.pop();
            }
            break;
          case 'textIn':
            didIn = true;
            if (elePath.length > 0) {
              elePath[elePath.length - 1].appendText(
                inLabel && ['circle', 'rectangle'].includes(labelType)
                  ? ` ${options.text.trim()} `
                  : options.text
              );
            }
            break;
          case 'textOut':
            didOut = true;
            if (extraElePath.length > 0) {
              extraElePath[extraElePath.length - 1].appendText(
                inLabel && ['circle', 'rectangle'].includes(labelType)
                  ? ` ${options.text.trim()} `
                  : options.text
              );
            }
            break;
          default:
            break;
        }
      });
      let html;
      if (topEle) {
        topExtraEles.forEach(ele => {
          topEle.append(ele);
        });
        html = topEle.getOuterHtml();
      } else {
        html = topExtraEles.map(ele => ele.getOuterHtml()).join('\n');
      }
      const insertHtml = didIn
        ? html
        : `${getLabelContainerPrefix(this.templateBuildConfig)}${html}${getLabelContainerSuffix()}`;
      // FIXME: there believe the backend api data;
      editor.setData(insertHtml.replace('&nbsp;', ' '));
      // editor.insertHtml(insertHtml.replace('&nbsp;', ' '), 'unfiltered_html');
    } else {
      // 没有编辑过
      const initialTemplateContent = `${getLabelContainerPrefix(
        this.templateBuildConfig
      )}init template${getLabelContainerSuffix()}`;
      super.setValue(initialTemplateContent);
      editor.setData(initialTemplateContent);
    }
    editor.on('change', evt => {
      this.handleDataChange(evt.editor.getData());
    });
  }

  @Bind()
  autoUpdateEditor({ templateWidth, templateHigh }) {
    if (templateWidth !== this.prevTemplateWidth || templateHigh !== this.prevTemplateHigh) {
      this.runTask({
        test: () =>
          this.inited &&
          (this.templateHigh !== this.prevTemplateHigh ||
            this.templateWidth !== this.prevTemplateWidth),
        name: 'updateEditorStyle',
        run: () => {
          // 更新标签容器的宽高
          const newText = (
            super.getValue() ||
            `${getLabelContainerPrefix(
              this.templateBuildConfig
            )}init template${getLabelContainerSuffix()}`
          ).replace(
            /<div data-type="canvas" style="[\w ;:#]+">/,
            `<div data-type="canvas" style="width: ${this.templateWidth}${elementUnit};height: ${this.templateHigh}${elementUnit}; border: 1px dashed #1e3255;">`
          );
          super.setValue(newText);
          this.getEditor().setData(newText);
          // TODO: because of I don't know why mobx don't update in this change, so change to simple pro and forceUpdate there
          this.prevTemplateHigh = templateHigh;
          this.prevTemplateWidth = templateWidth;
        },
        runUntilTestSuccess: true,
        remainBefore: false,
      });
    }
  }

  /**
   * 在初始化后执行任务
   * @param {object} options - 配置
   * @param {Function} options.name - 任务名(唯一)
   * @param {Function} [options.test=()=>this.inited] - 达标条件
   * @param {Function} options.run - 需要执行的任务
   * @param {boolean} [options.runUntilTestSuccess = true] - 如果没有完成加载, 是否在加载完后再执行
   * @param {boolean} [options.remainBefore=true] - 是否保留之前的
   */
  runTask(options = {}) {
    const {
      name,
      run,
      runUntilTestSuccess = true,
      remainBefore = true,
      test = () => this.inited,
    } = options;
    if (this.taskRunning) {
      this.pendingTask.push({
        name,
        run,
        runUntilTestSuccess,
        remainBefore,
        test,
      });
      return;
    }
    if (test()) {
      run();
    } else if (runUntilTestSuccess) {
      // 在加载后继续执行
      if (remainBefore) {
        // 保留之前的执行
        this.tasks[name] = this.tasks[name] || [];
        this.tasks[name].push({
          name,
          run,
          runUntilTestSuccess,
          remainBefore,
          test,
        });
      } else {
        // 清除之前的执行
        this.tasks[name] = [{ name, run, runUntilTestSuccess, remainBefore, test }];
      }
    }
  }

  /**
   * 循环执行 task
   */
  @Bind()
  triggerTaskRun() {
    this.taskRunning = true;
    const newTasks = {};
    let haveDebounceTask = false;
    Object.keys(this.tasks).forEach(name => {
      this.tasks[name].forEach(options => {
        if (options.test()) {
          options.run();
        } else {
          haveDebounceTask = true;
          newTasks[options.name] = newTasks[options.name] || [];
          newTasks[options.name].push(options);
        }
      });
    });
    if (haveDebounceTask || this.pendingTask.length !== 0) {
      this.pendingTask.forEach(taskOptions => {
        if (taskOptions.remainBefore) {
          newTasks[taskOptions.name] = newTasks[taskOptions.name] || [];
          newTasks[taskOptions.name].push(taskOptions);
        } else {
          newTasks[taskOptions.name] = [taskOptions];
        }
      });
      this.tasks = newTasks;
    }
    this.pendingTask = [];
    this.taskRunning = false;
  }

  @Bind()
  @action
  setValue(newData) {
    super.setValue(newData || '');
  }

  render() {
    return (
      <Spin spinning={!this.inited}>
        <RichTextEditor ref={this.richTextEditorRef} {...this.richTextEditorProps} />
      </Spin>
    );
  }
}
