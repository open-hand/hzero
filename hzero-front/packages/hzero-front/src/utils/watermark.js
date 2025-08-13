// 默认设置
export const defaultSettings = {
  watermark_x: 20, // 水印起始位置x轴坐标
  watermark_y: 56, // 水印起始位置Y轴坐标
  watermark_rows: 20, // 水印行数
  watermark_cols: 20, // 水印列数
  watermark_x_space: 100, // 水印x轴间隔
  watermark_y_space: 50, // 水印y轴间隔
  watermark_color: '#aaa', // 水印字体颜色
  watermark_alpha: 0.2, // 水印透明度
  watermark_fontsize: '12px', // 水印字体大小
  watermark_font: '微软雅黑', // 水印字体
  watermark_width: 210, // 水印宽度
  watermark_height: 60, // 水印长度
  watermark_angle: 15, // 水印倾斜度数
};
/* eslint-disable radix */
/* eslint-disable camelcase */
export function watermark(
  text,
  { watermark_cols, watermark_rows, watermark_x_space, watermark_y_space },
  container
) {
  let x;
  let y;
  for (let i = 0; i < watermark_rows; i++) {
    y = defaultSettings.watermark_y + (watermark_y_space + defaultSettings.watermark_height) * i;
    for (let j = 0; j < watermark_cols; j++) {
      x = defaultSettings.watermark_x + (defaultSettings.watermark_width + watermark_x_space) * j;
      const mask_div = document.createElement('div');
      mask_div.appendChild(document.createTextNode(text));
      mask_div.className = 'mask_mark';
      // 设置水印div倾斜显示
      mask_div.style.webkitTransform = `rotate(-${defaultSettings.watermark_angle}deg)`;
      mask_div.style.MozTransform = `rotate(-${defaultSettings.watermark_angle}deg)`;
      mask_div.style.msTransform = `rotate(-${defaultSettings.watermark_angle}deg)`;
      mask_div.style.OTransform = `rotate(-${defaultSettings.watermark_angle}deg)`;
      mask_div.style.transform = `rotate(-${defaultSettings.watermark_angle}deg)`;
      mask_div.style.visibility = '';
      mask_div.style.position = 'absolute';
      mask_div.style.left = `${x}px`;
      mask_div.style.top = `${y}px`;
      mask_div.style.overflow = 'hidden';
      mask_div.style.zIndex = '1000';
      mask_div.style.pointerEvents = 'none'; // pointer-events:none 让水印不遮挡页面的点击事件
      mask_div.style.opacity = defaultSettings.watermark_alpha;
      mask_div.style.fontSize = defaultSettings.watermark_fontsize;
      mask_div.style.fontFamily = defaultSettings.watermark_font;
      mask_div.style.color = defaultSettings.watermark_color;
      mask_div.style.textAlign = 'center';
      mask_div.style.width = `${defaultSettings.watermark_width}px`;
      mask_div.style.height = `${defaultSettings.watermark_height}px`;
      mask_div.style.setProperty('display', 'block', 'important');
      mask_div.style.setProperty('visibility', 'visible', 'important');
      container.appendChild(mask_div);
    }
  }
}

export function cacWaterMark(container) {
  // 获取页面最大宽度
  const p_width = Math.max(container.scrollWidth, container.clientWidth);
  const cutWidth = p_width * 0.015;
  const page_width = p_width - cutWidth;
  // 获取页面最大高度
  const page_height = Math.max(container.scrollHeight, container.clientHeight);
  let { watermark_cols, watermark_rows, watermark_x_space, watermark_y_space } = defaultSettings;
  // 如果将水印列数设置为0，或水印列数设置过大，超过页面最大宽度，则重新计算水印列数和水印x轴间隔
  if (
    watermark_cols === 0 ||
    parseInt(
      defaultSettings.watermark_x +
        defaultSettings.watermark_width * watermark_cols +
        watermark_x_space * (watermark_cols - 1)
    ) > page_width
  ) {
    watermark_cols = parseInt(
      (page_width - defaultSettings.watermark_x + watermark_x_space) /
        (defaultSettings.watermark_width + watermark_x_space)
    );
    watermark_x_space = parseInt(
      (page_width -
        defaultSettings.watermark_x -
        defaultSettings.watermark_width * watermark_cols) /
        (watermark_cols - 1)
    );
  }
  // 如果将水印行数设置为0，或水印行数设置过大，超过页面最大长度，则重新计算水印行数和水印y轴间隔
  if (
    watermark_rows === 0 ||
    parseInt(
      defaultSettings.watermark_y +
        defaultSettings.watermark_height * watermark_rows +
        watermark_y_space * (watermark_rows - 1)
    ) > page_height
  ) {
    watermark_rows = parseInt(
      (watermark_y_space + page_height - defaultSettings.watermark_y) /
        (defaultSettings.watermark_height + watermark_y_space)
    );
    watermark_y_space = parseInt(
      (page_height -
        defaultSettings.watermark_y -
        defaultSettings.watermark_height * watermark_rows) /
        (watermark_rows - 1 || 1)
    );
  }
  if (watermark_cols === 0) watermark_cols = 1;
  if (watermark_rows === 0) watermark_rows = 1;
  return {
    watermark_rows,
    watermark_cols,
    watermark_x_space,
    watermark_y_space,
  };
}
