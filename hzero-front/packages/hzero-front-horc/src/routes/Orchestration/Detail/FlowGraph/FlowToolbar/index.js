/**
 * FlowToolbar
 * 任务流画布-工具栏
 * @author baitao.huang@hand-china.com
 * @date 2020/1/2
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Badge, Divider } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { Toolbar, withPropsAPI } from 'gg-editor';

import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { transformStatus } from '@/utils/utils';
import ToolbarButton from './ToolbarButton';
import {
  MiniMapCommand,
  SaveFlowCommand,
  EditCommand,
  WebSocketCommand,
  StartParamInfoCommand,
  VarParamInfoCommand,
} from './Commands';
import styles from './index.less';

class FlowToolbar extends React.Component {
  state = {
    // firstLoadSetting: true,
    miniMapVisible: false,
  };

  @Bind()
  toggleMiniMapVisible(miniMapVisible) {
    this.setState({ miniMapVisible });
  }

  render() {
    const {
      loading,
      handleOpenHeaderModal,
      graphStatus,
      orchType,
      disabledFlag,
      webSocketMessage,
      onOpenDrawerByShape,
    } = this.props;
    const { miniMapVisible } = this.state;

    return (
      <Toolbar className={styles['horc-flow-toolbar']}>
        <WebSocketCommand webSocketMessage={webSocketMessage} />
        <MiniMapCommand
          miniMapVisible={miniMapVisible}
          toggleMiniMapVisible={this.toggleMiniMapVisible}
        />
        <EditCommand onOpenDrawerByShape={onOpenDrawerByShape} />
        <SaveFlowCommand handleOpenHeaderModal={handleOpenHeaderModal} />
        <StartParamInfoCommand />
        <VarParamInfoCommand />
        <ToolbarButton
          command="undo"
          icon="swap-left"
          text={ORCHESTRATION_LANG.UNDO}
          disabledFlag={disabledFlag}
        />
        <ToolbarButton
          command="redo"
          icon="swap-right"
          text={ORCHESTRATION_LANG.REDO}
          disabledFlag={disabledFlag}
        />
        <Divider type="vertical" />
        <ToolbarButton
          command="copy"
          icon="copy"
          text={ORCHESTRATION_LANG.COPY}
          disabledFlag={disabledFlag}
        />
        <ToolbarButton
          command="paste"
          icon="book"
          text={ORCHESTRATION_LANG.PASTE}
          disabledFlag={disabledFlag}
        />
        <ToolbarButton
          command="delete"
          icon="delete"
          text={ORCHESTRATION_LANG.DELETE}
          disabledFlag={disabledFlag}
        />
        <Divider type="vertical" />
        <ToolbarButton command="zoomIn" icon="arrows-alt" text={ORCHESTRATION_LANG.ZOOM_IN} />
        <ToolbarButton command="zoomOut" icon="shrink" text={ORCHESTRATION_LANG.ZOOM_OUT} />
        <ToolbarButton command="autoZoom" icon="retweet" text={ORCHESTRATION_LANG.AUTO_ZOOM} />
        <ToolbarButton command="resetZoom" icon="swap" text={ORCHESTRATION_LANG.RESET_ZOOM} />
        <Divider type="vertical" />
        {/* <ToolbarButton
          command="exeFlow"
          icon="play-circle"
          loading={loading}
          text={ORCHESTRATION_LANG.EXE}
        />
        <ToolbarButton
          command="cronFlow"
          icon="dashboard"
          loading={loading}
          text={ORCHESTRATION_LANG.CRON}
        /> */}
        {orchType !== 'definition' && (
          <>
            <ToolbarButton
              command="varParamInfo"
              icon="laptop"
              text={ORCHESTRATION_LANG.VIEW_VAR}
            />
            <ToolbarButton
              command="startParamInfo"
              icon="right-circle"
              text={ORCHESTRATION_LANG.START_PARAM}
            />
            <Divider type="vertical" />
          </>
        )}
        <ToolbarButton
          command="saveFlow"
          icon="save"
          loading={loading}
          text={ORCHESTRATION_LANG.SAVE}
          disabledFlag={disabledFlag}
        />
        <Divider type="vertical" />
        <Badge status={transformStatus(graphStatus.statusCode)} text={graphStatus.statusMeaning} />
      </Toolbar>
    );
  }
}

export default withPropsAPI(FlowToolbar);
