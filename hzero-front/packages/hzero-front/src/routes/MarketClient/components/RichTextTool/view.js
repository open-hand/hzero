import React, { useState, useRef, useEffect } from 'react';
import classnames from 'classnames';
import Watermark from './utils/watermark';
import useDynamicImport from './hooks/use-dynamic-import';
import useRender from './hooks/use-render';
import { braftEditorViewLoaders } from './constants';
import ImageViewer from '../ImageViewer';

import styles from './index.less';

const RichtextView = React.forwardRef(
  (
    {
      content,
      footer,
      rightWidth,
      spacing,
      saveScrollDom,
      onMouseUp,
      className,
      waterMark = false, // 是否添加水印
      photoView = true, // 是否显示全屏查看图片
      userInfo = {},
    },
    // eslint-disable-next-line no-unused-vars
    ref
  ) => {
    const { realName, loginName } = userInfo;
    const [htmlStr, setHtmlStr] = useState(null);
    // 查看图片大图显示
    const [imgVisible, setImgVisible] = useState(false);
    const [imgSrc, setImgSrc] = useState();

    const scrollDomref = useRef();
    const viewRef = useRef();
    const imagesRef = useRef();

    const { renderInBody } = useRender();

    useDynamicImport({
      loaders: braftEditorViewLoaders,
      onLoad: (modules) => {
        const BraftEditor = modules[0].default;
        setHtmlStr(BraftEditor.createEditorState(content).toHTML());
      },
    });

    useEffect(() => {
      if (typeof saveScrollDom === 'function') {
        saveScrollDom(scrollDomref.current);
      }
    }, []);

    useEffect(() => {
      if (photoView && htmlStr) {
        // const ps = new PhotoSwipe();
        // ps.initBySelector(`.${styles['braft-view']} img`);
        imagesRef.current = document.querySelectorAll(`.${styles['braft-view']} img`);
        onImgView(imagesRef.current);
      }
    }, [htmlStr]);

    // 添加水印
    useEffect(() => {
      if (viewRef.current && waterMark) {
        if (loginName && realName) {
          const watermark = new Watermark({
            container: viewRef.current,
            content: `${loginName} ${realName}`,
            x: 100,
            y: 80,
          });
          watermark.output();
          return () => watermark.destroy();
        }
      }
    }, [viewRef.current]);

    // 点击图片查看大图
    function onImgView(nodes) {
      const items = [];
      Array.from(nodes).forEach((node) => {
        if (node.nodeType !== 1 || node.tagName.toLowerCase() !== 'img' || !node.src) {
          return;
        }
        items.push({
          src: node.src,
          title: node.alt,
          __node: node,
        });
      });

      items.forEach((item, index) => {
        Object.assign(item, {
          index,
          uid: index + 1,
        });
        item.__node.setAttribute('data-uid', index + 1);
        // eslint-disable-next-line no-param-reassign
        item.__node.onclick = (e) => {
          const { uid } = e.target.dataset;
          const _item = items.find((one) => `${one.uid}` === `${uid}`);
          if (!_item) return;
          setImgSrc(_item.src);
          setImgVisible(true);
        };
      });
    }

    return (
      <div
        className={classnames(styles['braft-view'], className)}
        onMouseUp={onMouseUp}
        ref={viewRef}
      >
        <div
          ref={scrollDomref}
          style={{ width: `calc(100% - ${rightWidth + spacing}px)`, marginRight: spacing }}
        >
          <div dangerouslySetInnerHTML={{ __html: htmlStr }} />
          {footer}
        </div>
        <div style={{ width: rightWidth }} />
        {/* 图片查看组件 */}
        {renderInBody(
          <ImageViewer
            previewVisible={imgVisible}
            srcList={[
              {
                src: imgSrc,
              },
            ]}
            className={styles['image-viewer']}
            handlePreviewClose={() => setImgVisible(false)}
          />
        )}
      </div>
    );
  }
);

export default RichtextView;
