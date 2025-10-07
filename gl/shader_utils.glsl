// simple texture vertex/fragment
attribute vec4 aPosition;
attribute vec2 aTexCoord;
varying vec2 vTexCoord;
void main() {
  gl_Position = aPosition;
  vTexCoord = aTexCoord;
}

// fragment
precision mediump float;
varying vec2 vTexCoord;
uniform sampler2D uTexture;
void main() {
  gl_FragColor = texture2D(uTexture, vTexCoord);
}
