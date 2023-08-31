export default class NyxTextFormat {
  textSize: number;
  underline: boolean;
  textScaleX: double;
  textScaleY: double;
  letterSpacing: double;
  lineSpacing: double;
  topPadding: number;
  leftPadding: number;
  align: NyxAlign;
  style: NyxFontStyle;
  font: NyxFont;

  constructor() {
    this.textSize = 24;
    this.underline = false;
    this.textScaleX = 1.0;
    this.textScaleY = 1.0;
    this.letterSpacing = 0;
    this.lineSpacing = 0;
    this.topPadding = 0;
    this.leftPadding = 0;
    this.align = NyxAlign.left;
    this.style = NyxFontStyle.normal;
    this.font = NyxFont.defaultFont;
  }

  toObject(): Record<string, number | double> {
    return {
      textSize: this.textSize,
      underline: this.underline,
      textScaleX: this.textScaleX,
      textScaleY: this.textScaleY,
      letterSpacing: this.letterSpacing,
      lineSpacing: this.lineSpacing,
      topPadding: this.topPadding,
      leftPadding: this.leftPadding,
      align:
        this.align === NyxAlign.left
          ? 0
          : this.align === NyxAlign.center
          ? 1
          : 2,
      style:
        this.style === NyxFontStyle.normal
          ? 0
          : this.style === NyxFontStyle.bold
          ? 1
          : this.style === NyxFontStyle.italic
          ? 2
          : 3,
      font:
        this.font === NyxFont.defaultFont
          ? 0
          : this.font === NyxFont.defaultBold
          ? 1
          : this.font === NyxFont.sansSerif
          ? 2
          : this.font === NyxFont.serif
          ? 3
          : 4,
    };
  }
}

enum NyxFontStyle {
  normal,
  bold,
  italic,
  boldItalic,
}

enum NyxFont {
  defaultFont,
  defaultBold,
  sansSerif,
  serif,
  monospace,
}

enum NyxAlign {
  left,
  center,
  right,
}
