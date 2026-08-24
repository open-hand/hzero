const _ = require('lodash');
const path = require('path');
const fs = require('fs');
const request = require('request');
const wget = require('wget');

// icon 样式列表，暂时只支持iconfont库
const icons = {
  aliClassUrl: process.env.ICON_FONT_URL,
  dir: path.resolve(__dirname, '../src/assets/icons'), // 存放下载文件的目录
};

// 下载样式文件
const postUrl = (_url, fn) => {
  request(_url, (error, response, body) => {
    if (!error && response.statusCode === 200) {
      fn(body);
    } else {
      throw new Error('gen Icon error');
    }
  });
};

const downIcon = (iconUrl, dir) => {
  postUrl(`https:${iconUrl}`, chunk => {
    let form = 0;
    let to = form;
    let urlList = [];
    let count = 0;
    while (form !== -1 && to !== -1) {
      count++;
      if (count > 3000) throw new Error('gen icon failed');
      form = to + 1;
      form = chunk.indexOf('url(', form);
      to = chunk.indexOf(')', form + 1);
      if (form !== -1 && to !== -1) {
        urlList.push(chunk.substr(form + 5, to - form - 6));
      }
    }
    urlList = _.uniq(urlList.map(_url => _url.split('#')[0]));
    count = urlList.length;
    urlList.forEach(_url => {
      const __url = _url.split('?')[0];
      const { ext } = path.parse(__url);
      const fileName = `iconfont${ext}`;
      const filePath = path.join(dir, fileName);
      // eslint-disable-next-line
      fs.existsSync(filePath) && fs.unlinkSync(filePath);
      if (__url[0] !== '/') return;
      const download = wget.download(`https:${__url}`, filePath, {});
      chunk.split(_url).join('');
      download.on('error', err => {
        throw err;
      });
    });
    urlList.forEach(_url => {
      const strs = _url.split('?')[0].split('.');
      const type = strs[strs.length - 1];
      if (_url[0] !== '/') return;
      // eslint-disable-next-line
      chunk = chunk.replace(_url, `./iconfont.${type}`);
      // eslint-disable-next-line
      chunk = chunk.replace(_url, `./iconfont.${type}`);
    });
    fs.writeFileSync(path.join(dir, 'iconfont.css'), chunk);
  });
};

downIcon(icons.aliClassUrl, path.resolve(icons.dir));
