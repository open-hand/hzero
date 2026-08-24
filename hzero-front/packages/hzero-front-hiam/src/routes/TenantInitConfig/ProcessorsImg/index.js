/**
 * ProcessorsImg - 处理器流程图
 * @date: 2019-6-26
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { groupBy } from 'lodash';
import { Spin, Popover } from 'hzero-ui';
import classnames from 'classnames';
import intl from 'utils/intl';
import styles from './index.less';

const modelPrompt = 'hiam.tenantConfig.model.tenantConfig';

/**
 * 处理器流程图
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - 图形数据源
 * @reactProps {String} type - 初始化类型
 * @return React.element
 */
export default class ProcessorsImg extends PureComponent {
  /**
   * 渲染流程图
   * @param {array} data - 执行器数据
   * @param {string} type - 初始化类型
   */
  @Bind()
  renderImg(data, type) {
    const preProcessors =
      data.PRE_PROCESSOR && data.PRE_PROCESSOR.length
        ? this.renderProcessors(data.PRE_PROCESSOR, 'pre')
        : null;
    const postProcessors =
      data.POST_PROCESSOR && data.POST_PROCESSOR.length
        ? this.renderProcessors(data.POST_PROCESSOR, 'post')
        : null;
    const content = this.squareWrapper(type);
    if (preProcessors || postProcessors) {
      return (
        <div className={classnames(styles['tenantInit-img-wrapper'])}>
          {preProcessors}
          {content}
          {postProcessors}
        </div>
      );
    } else {
      return (
        <div>
          {type === 'create'
            ? intl.get(`${modelPrompt}.noCreate`).d('暂无租户创建类型的图形')
            : intl.get(`${modelPrompt}.noUpdate`).d('暂无租户更新类型的图形')}
        </div>
      );
    }
  }

  /**
   * 渲染执行器
   * @param {array} data - 执行器数据
   * @param {string} type - 初始化类型
   */
  @Bind()
  renderProcessors(data, type) {
    if (data) {
      const levelData = groupBy(data, 'orderSeq');
      const processorsContent = [];
      Object.keys(levelData).forEach(key => {
        const processors = this.squareWrapper(levelData[key]);
        if (type === 'post') processorsContent.push(this.line());
        processorsContent.push(processors);
        if (type === 'pre') processorsContent.push(this.line());
      });
      return processorsContent;
    }
  }

  /**
   * 渲染方形容器框
   * @param {array/string} node - 节点
   */
  @Bind()
  squareWrapper(node) {
    let content;
    if (typeof node === 'string') {
      content = (
        <div className={classnames(styles['tenantInit-img-square'])}>
          <span>{node}</span>
        </div>
      );
    } else {
      content = (
        <>
          {node.map(
            ({
              processorName,
              processorCode,
              processStatus,
              processStatusMeaning,
              processMessage,
            }) => {
              const tipContent = (
                <div>
                  {processStatus && (
                    <div>
                      {intl.get(`${modelPrompt}.processStatus`).d('处理状态')}:{' '}
                      {processStatusMeaning}
                    </div>
                  )}
                  <div>
                    {intl.get(`${modelPrompt}.processorName`).d('处理器名称')}: {processorName}
                  </div>
                  <div>
                    {intl.get(`${modelPrompt}.processorCode`).d('处理器代码')}: {processorCode}
                  </div>
                  {processMessage && processStatus === 'E' && (
                    <div style={{ maxWidth: 472, maxHeight: 400, overflow: 'scroll' }}>
                      {intl.get(`${modelPrompt}.processMessage`).d('处理消息')}: {processMessage}
                    </div>
                  )}
                </div>
              );
              const status = processStatus === 'S' ? 'success' : 'failed';
              return (
                <Popover content={tipContent} placement="right">
                  <div
                    className={classnames(styles['tenantInit-img-square'], {
                      [styles[`tenantInit-img-square-${status}`]]: !!processStatus,
                    })}
                  >
                    <span>{processorName}</span>
                  </div>
                </Popover>
              );
            }
          )}
        </>
      );
      if (node.length > 1) {
        content = <div className={classnames(styles['tenantInit-img-squares'])}>{content}</div>;
      }
    }
    return content;
  }

  /**
   * 渲染箭头
   */
  @Bind()
  line() {
    return <div className={classnames(styles['tenantInit-img-line'])} />;
  }

  render() {
    const { loading, dataSource, type } = this.props;
    return <Spin spinning={loading}>{this.renderImg(dataSource, type)}</Spin>;
  }
}
