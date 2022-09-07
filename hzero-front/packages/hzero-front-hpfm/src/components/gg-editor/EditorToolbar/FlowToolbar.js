import React from 'react';
import { Toolbar } from 'gg-editor';
import ToolbarButton from './ToolbarButton';
import styles from './index.less';

const FlowToolbar = () => {
  return (
    <Toolbar className={styles.toolbar}>
      {/* <ToolbarButton command="undo" /> */}
      {/* <ToolbarButton command="redo" /> */}
      {/* <ToolbarButton command="copy" icon="content_copy" /> */}
      {/* <ToolbarButton command="paste" icon="content_paste" /> */}
      {/* <ToolbarButton command="delete" />
      <Divider type="vertical" /> */}
      <ToolbarButton command="zoomIn" icon="zoom_in" text="Zoom In" />
      <ToolbarButton command="zoomOut" icon="zoom_out" text="Zoom Out" />
      <ToolbarButton command="autoZoom" icon="fullscreen_exit" text="Fit Map" />
      <ToolbarButton command="resetZoom" icon="split_cell" text="Actual Size" />
      {/* <Divider type="vertical" /> */}
      {/* <ToolbarButton command="toBack" icon="flip_to_back" text="To Back" />
      <ToolbarButton command="toFront" icon="flip_to_front" text="To Front" /> */}
      {/* <Divider type="vertical" /> */}
      {/* <ToolbarButton command="multiSelect" icon="select_all" text="Multi Select" /> */}
    </Toolbar>
  );
};

export default FlowToolbar;
