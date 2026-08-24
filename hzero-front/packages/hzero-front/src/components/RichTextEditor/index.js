import React from 'react';
import loadScript from 'load-script';
import { kebabCase, isEmpty, isFunction } from 'lodash';
import qs from 'querystring';
import { Modal, Upload, Button, Icon, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isAbsoluteUrl from 'is-absolute-url';
import intl from 'utils/intl';
import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import notification from 'utils/notification';
import {
  getCurrentLanguage,
  getAttachmentUrl,
  getAccessToken,
  isTenantRoleLevel,
  getCurrentOrganizationId,
} from 'utils/utils';
// import './style.less';

let promise;

function getEditorNamespace(editorURL) {
  if (typeof editorURL !== 'string' || editorURL.length < 1) {
    throw new TypeError('CKEditor URL must be a non-empty string.');
  }

  if ('CKEDITOR' in window) {
    if (window.CKEDITOR) {
      window.CKEDITOR.disableAutoInline = true;
    }
    return Promise.resolve(window.CKEDITOR);
  }
  if (!promise) {
    promise = new Promise((scriptResolve, scriptReject) => {
      loadScript(editorURL, (err) => {
        if (err) {
          scriptReject(err);
        } else {
          if (window.CKEDITOR) {
            window.CKEDITOR.disableAutoInline = true;
          }
          scriptResolve(window.CKEDITOR);
          promise = undefined;
        }
      });
    });
  }

  return promise;
}

function checkApiTenantPrefix() {
  const organizationId = getCurrentOrganizationId();
  return isTenantRoleLevel() ? `/${organizationId}` : '';
}

export default class CKEditor extends React.Component {
  constructor(props) {
    super(props);

    this.element = null;
    this.editor = null;
    this.state = {
      fileUploadModalVisible: false,
      file: null,
    };
    this.config = getEnvConfig();
  }

  componentDidMount() {
    this.initCKEditor();
  }

  componentWillUnmount() {
    this._destroyEditor();
  }

  componentDidUpdate(prevProps) {
    const { props, editor } = this;

    /* istanbul ignore next */
    if (!editor) {
      return;
    }

    if (prevProps.data !== props.data && editor.getData() !== props.data) {
      const newContent = props.data;
      editor.setData(newContent);
    }

    if (prevProps.readOnly !== props.readOnly) {
      editor.setReadOnly(props.readOnly);
    }

    if (prevProps.style !== props.style) {
      editor.container.setStyles(props.style);
    }

    this._attachEventHandlers(prevProps);

    window.addEventListener('popstate', () => {
      // to do
      if (editor && editor.commands.maximize.state === 1) {
        editor.destroy();
        window.location.reload();
      }
    });
  }

  @Bind()
  getContent() {
    const { privateBucket = false } = this.props;
    const { HZERO_FILE } = this.config;
    const content = this.editor.getData();
    let str = content;
    if (privateBucket) {
      const imgReg = new RegExp(/<img\b.*?(?:>|\/>)/g);
      str = content.replace(imgReg, (item) => {
        const replaceImg = item.replace(
          /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/,
          (temp) => {
            const index = temp.indexOf('&amp;t=1') === -1 ? 0 : temp.indexOf('&amp;t=1');
            const t = temp.slice(0, index);
            const { url } = qs.parse(t.substring(t.indexOf('?') + 1), '&amp;');
            return temp.startsWith(HZERO_FILE) ? url || t : t;
          }
        );
        return replaceImg;
      });

      const videoReg = new RegExp(/<video\b.*?(?:>|\/>)/g);
      str = str.replace(videoReg, (item) => {
        const replaceImg = item.replace(
          /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/,
          (temp) => {
            const { url } = qs.parse(temp.substring(temp.indexOf('?') + 1), '&amp;');
            return temp.startsWith(HZERO_FILE) ? url || temp : temp;
          }
        );
        return replaceImg;
      });
    }
    return str;
  }

  @Bind()
  initCKEditor() {
    const { onEditorChange = () => {}, content, readOnly, config = {}, style } = this.props;
    const { editorLoading } = this.state;
    if (!editorLoading && !window.CKEDITOR) {
      this.setState({
        editorLoading: true,
      });
    }
    getEditorNamespace(CKEditor.editorUrl)
      .then((CKEDITOR) => {
        const { HZERO_FILE, BKT_PUBLIC } = this.config;
        const { bucketName = BKT_PUBLIC, privateBucket = false } = this.props;
        const organizationId = getCurrentOrganizationId();
        // eslint-disable-next-line no-useless-catch
        try {
          const language = kebabCase(getCurrentLanguage() || 'zh_cn').toLocaleLowerCase();
          this.setState({
            editorLoading: false,
          });
          if (this.element) {
            this.editor = CKEDITOR.replace(this.element, {
              // extraPlugins:
              //   'image2',
              // codeSnippet_theme: 'solarized_dark',
              // customConfig: '/lib/ckeditor/config.js',
              removeButtons:
                'About,Flash,Save,Source,Form,Checkbox,Button,ShowBlocks,NewPage,Print,Language,Templates,CreateDiv,Radio,TextField,Textarea,Select,HiddenField',
              removePlugins: 'image,scayt,wsc,iframe',
              language,
              readOnly,
              height: 600,
              uploadUrl: `${HZERO_FILE}/v1${checkApiTenantPrefix()}/files/multipart`,
              filebrowserUploadUrl: `${HZERO_FILE}/v1${checkApiTenantPrefix()}/files/multipart`,
              disableNativeSpellChecker: false,
              font_names: `Arial/Arial, Helvetica, sans-serif; Comic Sans MS/Comic Sans MS;Courier New/Courier New;Georgia/Georgia;Lucida Sans Unicode/Lucida Sans Unicode;Times New Roman/Times New Roman, Times, serif;Verdana;宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;微软雅黑/微软雅黑;`,
              ...config,
            });
          }

          if (this.editor) {
            this.editor.filter = this.editor.filter || {};
            this._attachEventHandlers();
            if (content) {
              if (privateBucket) {
                const accessToken = getAccessToken();
                const imgReg = new RegExp(/<img\b.*?(?:>|\/>)/g);
                let newContent = content.replace(imgReg, (item) => {
                  const url = item.match(
                    /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/
                  )
                    ? item.match(
                        /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/
                      )[0]
                    : undefined;
                  const newUrl = `${HZERO_FILE}/v1/${organizationId}/files/redirect-url?bucketName=${bucketName}&access_token=${accessToken}&url=${encodeURIComponent(
                    url
                  )}`;
                  let replaceImg = item;
                  replaceImg = item.replace(
                    /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/,
                    newUrl
                  );
                  return replaceImg;
                });

                const videoReg = new RegExp(/<video\b.*?(?:>|\/>)/g);
                newContent = newContent.replace(videoReg, (item) => {
                  const url = item.match(
                    /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/
                  )
                    ? item.match(
                        /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/
                      )[0]
                    : undefined;
                  const newUrl = `${HZERO_FILE}/v1/${organizationId}/files/redirect-url?bucketName=${bucketName}&access_token=${accessToken}&url=${encodeURIComponent(
                    url
                  )}`;
                  let replaceImg = item;
                  replaceImg = item.replace(
                    /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/,
                    newUrl
                  );
                  return replaceImg;
                });
                this.editor.setData(newContent);
              } else {
                this.editor.setData(content);
              }
            }
            this._attachEventHandlers();
            if (isFunction(this.editor.on)) {
              this.editor.on('change', (evt) => {
                // getData() returns CKEditor's HTML content.
                onEditorChange(evt.editor.getData());
              });
              if (style) {
                this.editor.on('loaded', () => {
                  this.editor.container.setStyles(style);
                  this.editor.addCommand('insertFiles', {
                    exec: () => {
                      // const now = new Date();
                      // editor.insertHtml( `The current date and time is: <em>${ now.toString() }</em>` );
                      this.setState({
                        fileUploadModalVisible: true,
                      });
                    },
                  });
                  this.editor.ui.addButton('insertFilesBtn', {
                    label: intl.get('hzero.common.richTextEditor.insertFile').d('插入文件'),
                    command: 'insertFiles',
                    toolbar: 'insert',
                    icon: 'Templates',
                  });
                });
              }
              const accessToken = getAccessToken();
              this.editor.on(
                'fileUploadRequest',
                (evt) => {
                  const { bucketDirectory = 'editor' } = this.props;
                  const { fileLoader } = evt.data;
                  const { xhr, file } = fileLoader;
                  if (file.size > 30 * 1024 * 1024) {
                    // eslint-disable-next-line no-alert
                    alert(
                      intl
                        .get('hzero.common.upload.error.size', { fileSize: 30 })
                        .d(`上传文件大小不能超过: 30 MB`)
                    );
                    evt.stop();
                    return false;
                  } else {
                    xhr.open('POST', fileLoader.uploadUrl, true);
                    if (accessToken) {
                      xhr.setRequestHeader('Authorization', `bearer ${accessToken}`);
                    }
                    xhr.setRequestHeader('Cache-Control', 'no-cache');
                    xhr.setRequestHeader('X-File-Name', this.fileName);
                    xhr.setRequestHeader('X-File-Size', this.total);

                    const formData = new FormData();
                    formData.append('file', fileLoader.file, fileLoader.fileName);
                    formData.append('bucketName', bucketName);
                    formData.append('directory', bucketDirectory);
                    formData.append('fileName', fileLoader.fileName);
                    xhr.send(formData);
                    evt.stop();
                  }
                },
                null,
                null,
                4
              );

              this.editor.on('fileUploadResponse', (evt) => {
                // Prevent the default response handler.
                evt.stop();
                // FIXME: @LiJun
                const { resolveImageUrl = () => {} } = this.props;
                const { fileLoader } = evt.data;
                const { xhr } = fileLoader;
                const res = xhr.responseText;
                if (isAbsoluteUrl(res)) {
                  const imageUrl = resolveImageUrl(res, bucketName);
                  let newUrl = res;
                  if (privateBucket) {
                    newUrl = `${HZERO_FILE}/v1/${organizationId}/files/redirect-url?bucketName=${bucketName}&access_token=${accessToken}&url=${encodeURIComponent(
                      res
                    )}&t=1`;
                  }
                  // eslint-disable-next-line no-param-reassign
                  evt.data.url = imageUrl && isAbsoluteUrl(imageUrl) ? imageUrl : newUrl;
                } else {
                  // eslint-disable-next-line no-param-reassign
                  evt.data.message = JSON.parse(res).message;
                }

                setTimeout(() => {
                  onEditorChange(evt.editor.getData());
                }, 0);
                // Get XHR and response.
              });
            }
          }
        } catch (e) {
          throw e;
        }
      })
      .catch(window.console.error);
    // getEditorNamespace(CKEditor.editorUrl)
    //   .then(CKEDITOR => {
    //     // const constructor = this.props.type === 'inline' ? 'inline' : 'replace';

    //     this.setState({
    //       editorLoading: false,
    //     });

    //     const language = kebabCase(getCurrentLanguage() || 'zh_cn').toLocaleLowerCase();

    //     this.editor = CKEDITOR.replace(this.element, {
    //       editorName: 'richTextEditor',
    //       language,
    //       // customConfig: '',
    //       // Define the toolbar: https://ckeditor.com/docs/ckeditor4/latest/guide/dev_toolbar
    //       // The full preset from CDN which we used as a base provides more features than we need.
    //       // Also by default it comes with a 3-line toolbar. Here we put all buttons in two rows.
    //       // toolbar: [{
    //       //     name: 'clipboard',
    //       //     items: ['PasteFromWord', '-', 'Undo', 'Redo'],
    //       //   },
    //       //   {
    //       //     name: 'basicstyles',
    //       //     items: ['Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat', 'Subscript', 'Superscript'],
    //       //   },
    //       //   {
    //       //     name: 'links',
    //       //     items: ['Link', 'Unlink'],
    //       //   },
    //       //   {
    //       //     name: 'paragraph',
    //       //     items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote'],
    //       //   },
    //       //   {
    //       //     name: 'insert',
    //       //     items: ['Image', 'Table'],
    //       //   },
    //       //   {
    //       //     name: 'editing',
    //       //     items: ['Scayt'],
    //       //   },
    //       //   '/',

    //       //   {
    //       //     name: 'styles',
    //       //     items: ['Format', 'Font', 'FontSize'],
    //       //   },
    //       //   {
    //       //     name: 'colors',
    //       //     items: ['TextColor', 'BGColor', 'CopyFormatting'],
    //       //   },
    //       //   {
    //       //     name: 'align',
    //       //     items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
    //       //   },
    //       //   {
    //       //     name: 'document',
    //       //     items: ['Print', 'Source', 'NewPage'],
    //       //   },
    //       // ],

    //       // Since we define all configuration options here, let's instruct CKEditor to not load config.js which it does by default.
    //       // One HTTP request less will result in a faster startup time.
    //       // For more information check https://ckeditor.com/docs/ckeditor4/latest/api/CKEDITOR_config.html#cfg-customConfig

    //       // // Upload images to a CKFinder connector (note that the response type is set to JSON).
    //       // uploadUrl: '/apps/ckfinder/3.4.5/core/connector/php/connector.php?command=QuickUpload&type=Files&responseType=json',

    //       // Configure your file manager integration. This example uses CKFinder 3 for PHP.
    //       // filebrowserBrowseUrl: '/apps/ckfinder/3.4.5/ckfinder.html',
    //       // filebrowserImageBrowseUrl: '/apps/ckfinder/3.4.5/ckfinder.html?type=Images',
    //       // filebrowserUploadUrl: '/apps/ckfinder/3.4.5/core/connector/php/connector.php?command=QuickUpload&type=Files',
    //       // filebrowserImageUploadUrl: '/apps/ckfinder/3.4.5/core/connector/php/connector.php?command=QuickUpload&type=Images',

    //       // Sometimes applications that convert HTML to PDF prefer setting image width through attributes instead of CSS styles.
    //       // For more information check:
    //       //  - About Advanced Content Filter: https://ckeditor.com/docs/ckeditor4/latest/guide/dev_advanced_content_filter
    //       //  - About Disallowed Content: https://ckeditor.com/docs/ckeditor4/latest/guide/dev_disallowed_content
    //       //  - About Allowed Content: https://ckeditor.com/docs/ckeditor4/latest/guide/dev_allowed_content_rules
    //       disallowedContent: 'img{width,height,float}',
    //       extraAllowedContent: 'img[width,height,align];span{background}',

    //       // Enabling extra plugins, available in the full-all preset: https://ckeditor.com/cke4/presets
    //       extraPlugins:
    //         'colorbutton,font,justify,print,image2,codesnippet,placeholder,preview,bidi,emoji,find,selectall,copyformatting,newpage,uploadimage',
    //       codeSnippet_theme: 'solarized_dark',
    //       /** ********************* File management support ********************** */
    //       // In order to turn on support for file uploads, CKEditor has to be configured to use some server side
    //       // solution with file upload/management capabilities, like for example CKFinder.
    //       // For more information see https://ckeditor.com/docs/ckeditor4/latest/guide/dev_ckfinder_integration

    //       // Uncomment and correct these lines after you setup your local CKFinder instance.
    //       // filebrowserBrowseUrl: 'http://example.com/ckfinder/ckfinder.html',
    //       // filebrowserUploadUrl: 'http://example.com/ckfinder/core/connector/php/connector.php?command=QuickUpload&type=Files',
    //       /** ********************* File management support ********************** */

    //       // Make the editing area bigger than default.
    //       height: 600,
    //       readOnly,
    //       // An array of stylesheets to style the WYSIWYG area.
    //       // Note: it is recommended to keep your own styles in a separate file in order to make future updates painless.
    //       // contentsCss: [
    //       //   // 'http://cdn.ckeditor.com/4.11.4/full-all/contents.css',
    //       //   'assets/css/pastefromword.css',
    //       // ],

    //       // This is optional, but will let us define multiple different styles for multiple editors using the same CSS file.
    //       bodyClass: 'document-editor',

    //       // Reduce the list of block elements listed in the Format dropdown to the most commonly used.
    //       format_tags: 'p;h1;h2;h3;pre',

    //       // Simplify the Image and Link dialog windows. The "Advanced" tab is not needed in most cases.
    //       removeDialogTabs: 'image:advanced;link:advanced',
    //       removeButtons: 'About',
    //       removePlugins: 'image',
    //       uploadUrl: `${HZERO_FILE}/v1/files/multipart`,
    //       filebrowserUploadUrl: `${HZERO_FILE}/v1/files/multipart`,
    //       // Define the list of styles which should be available in the Styles dropdown list.
    //       // If the "class" attribute is used to style an element, make sure to define the style for the class in "mystyles.css"
    //       // (and on your website so that it rendered in the same way).
    //       // Note: by default CKEditor looks for styles.js file. Defining stylesSet inline (as below) stops CKEditor from loading
    //       // that file, which means one HTTP request less (and a faster startup).
    //       // For more information see https://ckeditor.com/docs/ckeditor4/latest/guide/dev_styles
    //       stylesSet: [
    //         /* Inline Styles */
    //         {
    //           name: 'Marker',
    //           element: 'span',
    //           attributes: {
    //             class: 'marker',
    //           },
    //         },
    //         {
    //           name: 'Cited Work',
    //           element: 'cite',
    //         },
    //         {
    //           name: 'Inline Quotation',
    //           element: 'q',
    //         },

    //         /* Object Styles */
    //         {
    //           name: 'Special Container',
    //           element: 'div',
    //           styles: {
    //             padding: '5px 10px',
    //             background: '#eee',
    //             border: '1px solid #ccc',
    //           },
    //         },
    //         {
    //           name: 'Compact table',
    //           element: 'table',
    //           attributes: {
    //             cellpadding: '5',
    //             cellspacing: '0',
    //             border: '1',
    //             bordercolor: '#ccc',
    //           },
    //           styles: {
    //             'border-collapse': 'collapse',
    //           },
    //         },
    //         {
    //           name: 'Borderless Table',
    //           element: 'table',
    //           styles: {
    //             'border-style': 'hidden',
    //             'background-color': '#E6E6FA',
    //           },
    //         },
    //         {
    //           name: 'Square Bulleted List',
    //           element: 'ul',
    //           styles: {
    //             'list-style-type': 'square',
    //           },
    //         },
    //       ],
    //       ...config,
    //     });
    //     // CKEDITOR.plugins.add( 'customUploadFiles', {
    //     //   icons: 'templates',
    //     //   init: _editor => {
    //     //     _editor.addCommand( 'insertFiles', {
    //     //       exec: () => {
    //     //         // const now = new Date();
    //     //         // editor.insertHtml( `The current date and time is: <em>${ now.toString() }</em>` );
    //     //         this.setState({
    //     //           fileUploadModalVisible: true,
    //     //         });
    //     //       },
    //     //     });
    //     //     _editor.ui.addButton( 'insertFilesBtn', {
    //     //         label: intl.get('hzero.common.richTextEditor.insertFile').d('插入文件'),
    //     //         command: 'insertFiles',
    //     //         toolbar: 'insert',
    //     //         icon: 'Templates',
    //     //     });
    //     //   },
    //     // });

    //     const { editor } = this;
    //     if (editor) {
    //       // editor.addCommand('insertFiles', {
    //       //   exec: () => {
    //       //     // const now = new Date();
    //       //     // editor.insertHtml( `The current date and time is: <em>${ now.toString() }</em>` );
    //       //     this.setState({
    //       //       fileUploadModalVisible: true,
    //       //     });
    //       //   },
    //       // });
    //       // editor.ui.addButton('insertFilesBtn', {
    //       //   label: intl.get('hzero.common.richTextEditor.insertFile').d('插入文件'),
    //       //   command: 'insertFiles',
    //       //   toolbar: 'insert',
    //       //   icon: 'Templates',
    //       // });

    //       editor.on(
    //         'fileUploadRequest',
    //         evt => {
    //           const { bucketName = 'static-text' } = this.props;
    //           const { fileLoader } = evt.data;
    //           const { xhr } = fileLoader;
    //           const accessToken = getAccessToken();
    //           xhr.open('POST', fileLoader.uploadUrl, true);
    //           if (accessToken) {
    //             xhr.setRequestHeader('Authorization', `bearer ${accessToken}`);
    //           }
    //           xhr.setRequestHeader('Cache-Control', 'no-cache');
    //           xhr.setRequestHeader('X-File-Name', this.fileName);
    //           xhr.setRequestHeader('X-File-Size', this.total);

    //           const formData = new FormData();
    //           formData.append('file', fileLoader.file, fileLoader.fileName);
    //           formData.append('bucketName', bucketName);
    //           formData.append('fileName', fileLoader.fileName);
    //           xhr.send(formData);
    //           evt.stop();
    //         },
    //         null,
    //         null,
    //         4
    //       );
    //       editor.on('fileUploadResponse', evt => {
    //         // Prevent the default response handler.
    //         evt.stop();
    //         const { bucketName = 'static-text' } = this.props;
    //         const { fileLoader } = evt.data;
    //         const { xhr } = fileLoader;
    //         const res = xhr.responseText;
    //         if (isAbsoluteUrl(res)) {
    //           // eslint-disable-next-line no-param-reassign
    //           evt.data.url = getAttachmentUrl(res, bucketName);
    //         } else {
    //           // eslint-disable-next-line no-param-reassign
    //           evt.data.message = JSON.parse(res).message;
    //         }

    //         // Get XHR and response.
    //       });

    //       this._attachEventHandlers();

    //       // if ( this.props.style && this.props.type !== 'inline' ) {
    //       // 	editor.on( 'loaded', () => {
    //       // 		editor.container.setStyles( this.props.style );
    //       // 	} );
    //       // }

    //       if (content) {
    //         editor.setData(content);
    //       }

    //       editor.on('change', evt => {
    //         // getData() returns CKEditor's HTML content.
    //         onChange(evt.editor.getData());
    //       });
    //     }

    //   })
    //   .catch(window.console.error);
  }

  @Bind()
  _attachEventHandlers(prevProps = {}) {
    const { props } = this;

    Object.keys(this.props).forEach((propName) => {
      if (!propName.startsWith('on') || prevProps[propName] === props[propName]) {
        return;
      }

      this._attachEventHandler(propName, prevProps[propName]);
    });
  }

  @Bind()
  _attachEventHandler(propName, prevHandler) {
    // eslint-disable-next-line no-useless-catch
    try {
      const evtName = `${propName[2].toLowerCase()}${propName.substr(3)}`;

      if (prevHandler) {
        this.editor.removeListener(evtName, prevHandler);
      }

      if (isFunction(this.editor.on)) {
        this.editor.on(evtName, this.props[propName]);
      }
    } catch (e) {
      throw e;
    }
  }

  @Bind()
  _destroyEditor() {
    // eslint-disable-next-line no-useless-catch
    try {
      if (this.editor) {
        this.editor.destroy(true);
      }
      this.editor = null;
      this.element = null;
    } catch (e) {
      throw e;
    }
  }

  @Bind()
  beforeUpload(file) {
    this.setState({
      file,
    });
    return false;
  }

  @Bind()
  onRemove() {
    this.setState({
      file: null,
    });
  }

  @Bind()
  upload() {
    const { HZERO_FILE, BKT_PUBLIC } = this.config;
    const { bucketName = BKT_PUBLIC, bucketDirectory = 'editor' } = this.props;
    const { file } = this.state;
    const formData = new FormData();
    formData.append('file', file, file.name);
    formData.append('bucketName', bucketName);
    formData.append('directory', bucketDirectory);
    formData.append('fileName', file.name);
    this.setState({
      uploading: true,
    });
    request(`${HZERO_FILE}/v1${checkApiTenantPrefix()}/files/multipart`, {
      processData: false,
      method: 'POST',
      type: 'FORM',
      body: formData,
      responseType: 'text',
    }).then((res) => {
      if (isAbsoluteUrl(res)) {
        // success(getAttachmentUrl(res, bucketName));
        this.setState({
          uploading: false,
          url: getAttachmentUrl(res, bucketName, undefined, bucketDirectory),
        });
        notification.success();
      } else {
        // failure(JSON.parse(res).message);
        notification.error({ message: JSON.parse(res).message });
        this.setState({
          uploading: false,
        });
      }
    });
  }

  @Bind()
  closeFileUploadModal() {
    this.setState({
      fileUploadModalVisible: false,
      uploading: false,
      file: null,
      url: null,
    });
  }

  @Bind()
  onFileUploadModalOk() {
    const { url, file } = this.state;
    // eslint-disable-next-line no-useless-catch
    try {
      if (!isEmpty(url)) {
        this.editor.insertHtml(`<a href="${url}" >${file.name}</a>`);
      }
    } catch (e) {
      throw e;
    }

    this.setState({
      fileUploadModalVisible: false,
      file: null,
      url: null,
    });
  }

  render() {
    const { style = {} } = this.props;
    const { uploading, file, editorLoading, fileUploadModalVisible } = this.state;
    const fileUploadProps = {
      beforeUpload: this.beforeUpload,
      fileList: file ? [file] : [],
      onRemove: this.onRemove,
    };
    return (
      <>
        <Spin spinning={editorLoading}>
          <div
            contentEditable="true"
            style={style}
            ref={(ref) => {
              this.element = ref;
            }}
            id="richTextEditor"
            className="rich-text-editor"
          />
        </Spin>
        <Modal
          title={intl.get('hzero.common.richTextEditor.insertImage').d('插入文件')}
          visible={fileUploadModalVisible}
          onOk={this.onFileUploadModalOk}
          onCancel={this.closeFileUploadModal}
        >
          <Upload {...fileUploadProps}>
            <Button>
              <Icon type="upload" />{' '}
              {intl.get('hzero.common.richTextEditor.selectFile').d('选择图片')}
            </Button>
          </Upload>
          <br />
          <Button
            className="upload-demo-start"
            type="primary"
            onClick={this.upload}
            disabled={isEmpty(file)}
            loading={uploading}
          >
            {uploading
              ? intl.get('hzero.common.richTextEditor.uploading').d('正在上传')
              : intl.get('hzero.common.richTextEditor.startUpload').d('开始上传')}
          </Button>
        </Modal>
      </>
    );
  }
}

CKEditor.defaultProps = {
  type: 'classic',
  data: '',
  config: {},
  readOnly: false,
};

const getEditorUrl = () => {
  let CKEDITOR_BASEPATH = window.CKEDITOR_BASEPATH;
  if (!window.CKEDITOR_BASEPATH) {
    let publicUrl = process.env.PUBLIC_URL || '/';
    if (!publicUrl.endsWith('/')) {
      publicUrl = publicUrl + '/';
    }
    CKEDITOR_BASEPATH = `${publicUrl}lib/ckeditor/`;
  }
  if (!CKEDITOR_BASEPATH.endsWith('/')) {
    CKEDITOR_BASEPATH = CKEDITOR_BASEPATH + '/';
  }
  return `${CKEDITOR_BASEPATH}ckeditor.js`;
};

CKEditor.editorUrl = getEditorUrl();
