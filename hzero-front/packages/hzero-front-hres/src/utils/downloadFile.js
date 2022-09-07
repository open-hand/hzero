/**
 * 文件下载
 * @Author: daiwen <wen.dai@hand-china.com>
 * @Date: 2019-08-19 17:45:49
 * @LastEditTime: 2019-08-23 14:47:50
 * @Copyright: Copyright (c) 2018, Hand
 */

/**
 * @param {string} name 下载的文件名
 * @param {blob} blob 文件流
 */
export function commandDownload(blob, name) {
  try {
    // 创建隐藏的可下载链接
    const eleLink = document.createElement('a');
    eleLink.download = `${name}`;
    eleLink.style.display = 'none';
    eleLink.href = window.URL.createObjectURL(blob);
    // 触发点击
    document.body.appendChild(eleLink);
    eleLink.click();
    // 然后移除
    document.body.removeChild(eleLink);
  } catch (err) {
    console.log(err);
  }
}

let iframe;
/**
 * 在 iframe 中下载文件，不需要使用 window.open
 * @param {*} url 文件url
 */
export function doExport(url) {
  if (!iframe) {
    iframe = document.createElement('iframe');
    iframe.id = '_export_window2';
    iframe.name = '_export_window2';
    iframe.style.cssText =
      'position:absolute;left:-10000px;top:-10000px;width:1px;height:1px;display:none';
    document.body.appendChild(iframe);
  }
  const eleLink = document.createElement('a');
  eleLink.target = '_export_window2';
  eleLink.style.display = 'none';
  eleLink.href = url;
  document.body.appendChild(eleLink);
  eleLink.click();
  document.body.removeChild(eleLink);
}
