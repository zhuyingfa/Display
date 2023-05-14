#extension GL_OES_EGL_image_external : require
// 摄像头数据比较特殊的一个地方
precision mediump float; // 数据精度
varying vec2 aCoord;

uniform samplerExternalOES  vTexture;  // samplerExternalOES: 图片， 采样器

void main(){
    //  texture2D: vTexture采样器，采样  aCoord 这个像素点的RGBA值
    vec4 rgba = texture2D(vTexture,aCoord);  //rgba
    //    gl_FragColor = vec4(1.-rgba.r,1.-rgba.g,1.-rgba.b,rgba.a);
    //    float grey = 0.3*rgba.r+0.59*rgba.g+0.11*rgba.b;
    //    gl_FragColor = vec4(grey,grey,grey,rgba.a);
    gl_FragColor = rgba;
}