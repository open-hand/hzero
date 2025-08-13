import React from 'react';
import Viewer from 'react-viewer';
import 'react-viewer/dist/index.css';

/**
 * 图片预览组件接受 Props 说明
 * @param {boolean} previewVisible - 是否显示图拍预览组件
 * @param {array} srcList - 预览的图片列表
 * @param {number} current - 初始化的时候展示 srcList 中的第几张图片
 * @param {function} handlePreviewClose - 图拍预览组件关闭的回调函数
 */

export default function ImagesViewer({
  previewVisible,
  current = 0,
  srcList,
  handlePreviewClose,
  ...rest
}) {
  /**
   * 关闭按钮回调
   * @function handlePreviewCancel
   */
  const handlePreviewCancel = () => {
    if (typeof handlePreviewClose === 'function') {
      handlePreviewClose();
    }
  };

  return (
    <Viewer
      noImgDetails
      noNavbar
      scalable={false}
      zoomSpeed={0.1}
      activeIndex={current}
      visible={previewVisible}
      onClose={handlePreviewCancel}
      images={srcList}
      {...rest}
    />
  );
}
