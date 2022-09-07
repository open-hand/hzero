import { API_HOST, HZERO_ADM } from 'utils/config';

export const Config = {
  API: `${API_HOST}${HZERO_ADM}/v1/market/work-order/multipart`,
  // video: {
  //   bucketName: 'hsop',
  //   directory: 'hsop/video/',
  //   sizeNum: 1024 * 1024 * 500, // 文件最大限制
  //   sizeTxt: '500M',
  //   format: ['avi', 'dv', 'mp4', 'mpeg', 'mpg', 'wm', 'mkv'], // 'mov', 'flv'
  //   // video/quicktime(上传不了), video/x-flv,（格式识别不了）
  //   formatType: 'video/avi,video/dv,video/mp4,video/mpeg,video/mpg,video/x-ms-wm,video/x-matroska',
  // },
  image: {
    bucketName: 'image',
    directory: 'doc_classify',
    sizeNum: 1024 * 1024 * 10, // 文件最大限制
    sizeTxt: '10M',
    format: ['png', 'jpeg', 'jpg', 'jpe', 'gif'], // 'svg' 不支持svg格式
    formatType: 'image/png,image/jpeg,image/gif', // image/svg
  },
  file: {
    bucketName: 'file',
    directory: 'doc_classify',
    sizeNum: 1024 * 1024 * 50, // 文件最大限制
    sizeTxt: '50M',
    format: ['pdf', 'doc', 'docx', 'xls', 'xlsx'],
    formatType:
      'application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    API: `${API_HOST}${HZERO_ADM}/v1/market/work-order/secret-multipart`,
  },
};

export const Controls = [
  'undo',
  'redo',
  'separator',
  'font-size',
  'line-height',
  'letter-spacing',
  'separator',
  'text-color',
  'bold',
  'italic',
  'underline',
  'strike-through',
  'separator',
  'superscript',
  'subscript',
  'remove-styles',
  'emoji',
  'separator',
  'text-indent',
  'text-align',
  'separator',
  'headings',
  'list-ul',
  'list-ol',
  'blockquote',
  'code',
  'separator',
  'media',
  'link',
  'doc-quote',
  'table',
  'split',
  'hr',
  'separator',
  'clear',
  'separator',
  'fullscreen',
];
