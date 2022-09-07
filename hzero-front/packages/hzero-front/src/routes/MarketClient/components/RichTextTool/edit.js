import React, { useImperativeHandle, useEffect, useRef } from 'react';
import { Modal } from 'hzero-ui';

import notification from 'utils/notification';
import useDynamicImport from './hooks/use-dynamic-import';
import { isValidArray } from './utils/base';

import { Config, Controls } from './config';
import styles from './index.less';
import { braftEditorEditLoaders } from './constants';
import { getImageUrl } from '../../Feedback/services';

const ESC_CODE = 27;
const RichtextEdit = React.forwardRef(
  (
    {
      content,
      controls = Controls, // 工具栏工具列表
      disabledList = [], // 被排除的工具栏工具列表
      mediaAccepts = {}, // 媒体库支持的格式
      ...rest
    },
    ref
  ) => {
    const editorRef = useRef();
    const { modules, renderModule } = useDynamicImport({
      loaders: braftEditorEditLoaders,
    });

    useEffect(() => {
      const fn = (e) => {
        if (e.which === ESC_CODE) {
          if (editorRef.current?.state?.isFullscreen) {
            editorRef.current.toggleFullscreen();
          }
        }
      };
      window.addEventListener('keydown', fn);
      return () => {
        window.removeEventListener('keydown', fn);
      };
    }, []);

    // [FIX] 修复content变化时，内容不变化
    useEffect(() => {
      if (!editorRef.current?.setValue) return;
      if (!modules?.[0]?.default) return;
      const BraftEditor = modules[0].default;
      editorRef.current.setValue(BraftEditor.createEditorState(content));
    }, [content]);

    useImperativeHandle(ref, () => ({
      getContent,
    }));

    function getContent() {
      if (!editorRef.current) return '';
      return editorRef.current.getValue().toRAW();
    }

    // 过滤不展示的工具
    function getControls() {
      if (isValidArray(disabledList)) {
        return controls.filter((one) => !disabledList.includes(one));
      }
      return controls;
    }

    async function onUploadFn(param) {
      const formData = new FormData();
      const suffix = param.file.name.split('.');
      // 图片文件夹
      if (Config.image.format.indexOf(suffix[suffix.length - 1]) > -1) {
        formData.append('bucketName', Config.image.bucketName);
        formData.append('directory', Config.image.directory);
        formData.append('fileName', param.file.name);
      }

      const blob = new Blob([param.file]);
      const file = new window.File([blob], param.file.name);
      formData.append('file', file);
      const res = await getImageUrl(formData);

      if (!res || res.failed) {
        notification.error({ message: `上传失败，${res.message}` });
        param.error({
          msg: res.message || 'error',
        });
        return;
      }

      param.success({
        url: res.address,
        meta: {
          id: param.id,
          title: param.file.name,
          alt: param.file.name,
          loop: true, // 指定音视频是否循环播放
          autoPlay: true, // 指定音视频是否自动播放
          controls: true, // 指定音视频是否显示控制栏
          // poster: { AvatarImg }, // 指定视频播放器的封面
        },
      });
    }

    function onValidateFn(file) {
      if (file.name) {
        const suffix = file.name.split('.');
        // 设置图片大小限制
        if (Config.image.format.indexOf(suffix[suffix.length - 1]?.toLowerCase()) > -1) {
          if (file.size <= Config.image.sizeNum) {
            return true;
          } else {
            notification.error({ message: `只支持小于${Config.image.sizeTxt}的图片！` });
            return false;
          }
        }
      }
      Modal.info({
        centered: true,
        title: '提示',
        content: `图片格式只支持${Config.image.format.join('、')}`,
        okText: '确定',
        onCancel: '取消',
      });
      return false;
    }

    function onFullScreen(isFullScreen) {
      // 顶部导航栏 && 项目中心导航栏 && 左侧折叠icon
      const topMenuDom = document.getElementsByClassName('nav-header-wrapper')?.[0]?.children?.[1];
      const porjectHeaderDom = document.getElementsByClassName('hsop-project-center-header')?.[0];
      const leftSiderIcon = document.getElementsByClassName('hsop-editor-left-sider-icon')?.[0];
      if (isFullScreen) {
        if (topMenuDom) {
          topMenuDom.style.setProperty('position', 'static');
        }
        if (porjectHeaderDom) {
          porjectHeaderDom.style.setProperty('position', 'static');
        }
        if (leftSiderIcon) {
          leftSiderIcon.style.setProperty('position', 'static');
        }
      } else {
        if (topMenuDom) {
          topMenuDom.style.setProperty('position', 'fixed');
        }
        if (porjectHeaderDom) {
          porjectHeaderDom.style.setProperty('position', 'fixed');
        }
        if (leftSiderIcon) {
          leftSiderIcon.style.setProperty('position', 'fixed');
        }
      }
    }

    return renderModule(({ default: BraftEditor }) => {
      return (
        <div className={styles['braft-editor-container']}>
          <div className={styles.content}>
            <BraftEditor
              className={styles['braft-editor']}
              defaultValue={content ? BraftEditor.createEditorState(content) : null}
              ref={editorRef}
              media={{
                uploadFn: onUploadFn,
                validateFn: onValidateFn,
                accepts: {
                  image: Config.image.formatType,
                  ...mediaAccepts,
                },
              }}
              controls={getControls()}
              // extendControls={getExtendControls()}
              onFullscreen={onFullScreen}
              {...rest}
            />
          </div>
        </div>
      );
    });
  }
);

export default RichtextEdit;
