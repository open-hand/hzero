import React, { PureComponent } from 'react';
import isAbsoluteUrl from 'is-absolute-url';
// import { isEmpty } from 'lodash';
import { Editor } from '@tinymce/tinymce-react';
// import loadScript from 'load-script';
import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import notification from 'utils/notification';
import { getAttachmentUrl, getCurrentLanguage } from 'utils/utils';
// Import TinyMCE
import 'tinymce/tinymce.min';
import 'tinymce/themes/modern/theme.min';
// Any plugins you want to use has to be imported
import 'tinymce/plugins/paste';
import 'tinymce/plugins/link';
import 'tinymce/plugins/table';
import 'tinymce/plugins/image';
import 'tinymce/plugins/imagetools';
import 'tinymce/plugins/preview';
import 'tinymce/plugins/code';
import 'tinymce/plugins/codesample';
import 'tinymce/plugins/textcolor';
import 'tinymce/plugins/fullscreen';
import 'tinymce/plugins/lists';
import 'tinymce/plugins/template';
// import 'tinymce/plugins/pagebreak';
import 'tinymce/plugins/searchreplace';
// import 'tinymce/plugins/toc';
// import 'tinymce/plugins/anchor';
import 'tinymce/plugins/print';
// import './skins/lightgray/skin.min.css';
import './content.min.css';
import RichTextInsertFile from './RichTextInsertFile';

// A theme is also required
// import 'tinymce/themes/modern/theme';

export default class TinymceEditor extends PureComponent {
  state = {
    uploadFileModalvisible: false,
  };

  config = getEnvConfig();

  componentDidMount() {
    // const language = getCurrentLanguage();
    // if (language !== 'en_US' && isEmpty(window.tinymce.i18n.data[language])) {
    //   import(`./locale/${language}.js`);
    // }
  }

  imagesUploadHandler(blobInfo, success, failure) {
    const { bucketName = this.config.BKT_PUBLIC, bucketDirectory = 'editor' } = this.props;
    const formData = new FormData();
    formData.append('file', blobInfo.blob(), 'blob.png');
    formData.append('bucketName', bucketName);
    formData.append('directory', bucketDirectory);
    formData.append('fileName', 'blob.png');
    request(`${this.config.HZERO_FILE}/v1/files/multipart`, {
      processData: false,
      method: 'POST',
      type: 'FORM',
      body: formData,
      responseType: 'text',
    }).then((res) => {
      if (isAbsoluteUrl(res)) {
        success(getAttachmentUrl(res, bucketName, undefined, bucketDirectory));
        notification.success();
      } else {
        failure(JSON.parse(res).message);
        notification.error({ message: JSON.parse(res).message });
      }
    });
  }

  handleOnChange(event) {
    const { onChange = (e) => e } = this.props;
    onChange(event.target.getContent());
  }

  editorSetup(editor) {
    editor.addButton('insertFile', {
      icon: 'upload',
      tooltip: 'Insert File',
      onclick: this.setUploadFileModalvisible.bind(this),
    });
  }

  setUploadFileModalvisible() {
    const { uploadFileModalvisible } = this.state;
    this.setState({
      uploadFileModalvisible: !uploadFileModalvisible,
    });
  }

  uploadFileModalOk(content) {
    this.tinymceEditor.editor.insertContent(content);
  }

  render() {
    const { content, bucketName = this.config.BKT_PUBLIC, bucketDirectory = 'editor' } = this.props;
    const { uploadFileModalvisible } = this.state;
    const language = getCurrentLanguage();
    const languageConfig =
      language !== 'en_US'
        ? {
            language,
            language_url: `${process.env.BASE_PATH || '/'}lib/tinymce/skins/langs/${language}.js`,
          }
        : {};
    const richTextInsertFileProps = {
      bucketName,
      bucketDirectory,
      visible: uploadFileModalvisible,
      onOk: this.uploadFileModalOk.bind(this),
      onCancel: this.setUploadFileModalvisible.bind(this),
    };
    return (
      <>
        <Editor
          ref={(ref) => {
            this.tinymceEditor = ref;
          }}
          value={content}
          init={{
            target: this.editor,
            plugins: [
              'paste',
              'link',
              'table',
              'image',
              'imagetools',
              'preview',
              'code',
              'codesample',
              'textcolor',
              'fullscreen',
              'lists',
              'template',
              // 'pagebreak',
              'searchreplace',
              // 'toc',
              // 'anchor',
              'print',
            ],
            toolbar:
              'undo redo | formatselect fontselect | alignleft aligncenter alignright alignjustify | bold italic strikethrough forecolor backcolor | link image insertFile | numlist bullist outdent indent  | removeformat fullscreen',
            images_upload_handler: this.imagesUploadHandler.bind(this),
            min_height: 500,
            setup: this.editorSetup.bind(this),
            skin_url: `${process.env.BASE_PATH || '/'}lib/tinymce/skins/lightgray`,
            ...languageConfig,
          }}
          onChange={this.handleOnChange.bind(this)}
        />
        <RichTextInsertFile {...richTextInsertFileProps} />
      </>
    );
  }
}
