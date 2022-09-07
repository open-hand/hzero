/**
 * FlowTopButtonBar
 * 任务流画布-顶部按钮
 * @description 创建主题
 * @author baitao.huang@hand-china.com
 * @date 2020/1/9
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { withPropsAPI } from 'gg-editor';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import styles from '../../index.less';
import QuestionPopover from '@/components/QuestionPopover';

class FlowTopButtonBar extends React.PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  render() {
    return (
      <div className={styles['horc-editor-hd-left']}>
        <QuestionPopover text={ORCHESTRATION_LANG.TOOL} message={ORCHESTRATION_LANG.TOOL_TIP} />
      </div>
    );
  }
}

export default withPropsAPI(FlowTopButtonBar);
